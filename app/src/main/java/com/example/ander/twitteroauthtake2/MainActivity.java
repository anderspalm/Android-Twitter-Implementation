package com.example.ander.twitteroauthtake2;

import android.app.backup.SharedPreferencesBackupHelper;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.text.LoginFilter;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String CLIENT_ID = "";
    private static final String CLIENT_SECRET = "";
    EditText mEditText;
    Button mButton;
    protected String mAccesstoken;
    public String mBearerToken;
    SharedPreferences mPreferences;
    ArrayList<Tweet> mArrayList;
    ListView mListView;
    TweetAdapter mAdapter;
    Context mContext;

    public String mResponse;


    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = MainActivity.this;

        mEditText = (EditText) findViewById(R.id.edit_text);
        mButton = (Button) findViewById(R.id.button);

        doBase64();

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = mEditText.getText().toString();
                getItemsfromJSON(name);

                mAdapter.updateListItems(mArrayList);
                mAdapter.notifyDataSetChanged();
                mEditText.clearFocus();
            }
        });

        mArrayList = new ArrayList<>();
        mListView = (ListView) findViewById(R.id.listView);
        mAdapter = new TweetAdapter(MainActivity.this, R.layout.list_view_items, mArrayList);
        mListView.setAdapter(mAdapter);

        TextView search = (TextView) findViewById(R.id.name_search);
        search.setText(mEditText.getText().toString());

//        convertImage("http:\\/\\/pbs.twimg.com\\/profile_images\\/746810270912249856\\/QdxQuiTL.jpg");
    }

    public void doBase64() {
        String mauthId = CLIENT_ID;
        String mauthSc = CLIENT_SECRET;
        String pre64String = mauthId + ":" + mauthSc;
        String base64 = Base64.encodeToString(pre64String.getBytes(), Base64.NO_WRAP);
        Log.i(TAG, "doBase64: this is the base64 code " + base64);

        OkHttpClient client = new OkHttpClient(); // allows us to talk to the network

        RequestBody body = new FormBody.Builder() // this is the body of the request
                .add("grant_type", "client_credentials")
                .build();

        Request apiRequest = new Request.Builder() // this is the request
                .addHeader("Authorization", "Basic " + base64)
                .addHeader("Content-type", "Content-Type: application/x-www-form-urlencoded;charset=UTF-8")
                .post(body) // what type of request
                .url("https://api.twitter.com/oauth2/token") // the URL
                .build();  // to build the request

        client.newCall(apiRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "onFailure: first request failed");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.i(TAG, "onResponse: first response failed");
                } else {
                    mAccesstoken = response.body().string();
                    Log.i(TAG, "onResponse: " + mAccesstoken);

                    Gson gson = new Gson();
                    TwitterOauthResponse oauthResponse = gson.fromJson(mAccesstoken, TwitterOauthResponse.class);
                    Log.i(TAG, "onResponse: Your token is " + oauthResponse.getAccess_token());
                    mBearerToken = oauthResponse.getAccess_token();
//                    mPreferences.edit().putString(mAccesstoken, oauthResponse.access_token).apply();
                }
            }
        });
    }

    public void getItemsfromJSON(final String name) {
        Request newRequest = new Request.Builder()
                .url("https://api.twitter.com/1.1/statuses/user_timeline.json?count=20&screen_name=" + name)
                .addHeader("Authorization", "Bearer " + mBearerToken)
                .get()
                .build();

        OkHttpClient newClient = new OkHttpClient();
        newClient.newCall(newRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "onFailure: this url is unknown " + "https://api.twitter.com/1.1/statuses/user_timeline.json?count=1&screen_name=" + name);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                mResponse = response.body().string();
                try {
                    Log.i(TAG, "onResponse: Here is the mResponse 2..." + mResponse);
                    parseAndAddJson();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void parseAndAddJson() throws JSONException {
        mArrayList.clear();
        ArrayList<Tweet> arrayList = new ArrayList<>();
        Log.i(TAG, "parseAndAddJson: objectAsString " + mResponse);
        JSONArray array = new JSONArray(mResponse);

        for (int i = 0; i < array.length(); i++) {
            JSONObject repo = array.getJSONObject(i);
            String text = repo.getString("text");
            String creationDate = repo.getString("created_at");
            JSONObject user = repo.getJSONObject("user");
            String name = user.getString("name");
            String location = user.getString("location");
            String profile_image = user.getString("profile_image_url_https");
            String backgroundImage = user.getString("profile_background_image_url");

            String fixedPImage = convertImage(profile_image);
            String fixedBImage = convertImage(backgroundImage);

            mArrayList.add(new Tweet(text, timeAgo(creationDate), name, location, fixedPImage, fixedBImage));
            Log.i(TAG, "parseAndAddJson:  " + profile_image + backgroundImage);
            Log.i(TAG, "parseAndAddJson: current time " + DateFormat.getDateInstance().format(new Date()));
        }
    }


    public String convertImage(String image) {
        for (int i = 0; i < image.length() - 1; i++) {
            if (image.charAt(i) == '\\') {
                Log.i(TAG, "convertImage: yes ");
                image = image.substring(0, i + 1) + image.substring(i + 2, image.length());
            }
        }
        Log.i(TAG, "convertImage: " + image);
        return image;
    }

    public String timeAgo(String tweetsDay){

        int tweetMonth = extractMonth(tweetsDay);
        int tweetDay = extractDay(tweetsDay);

        String todaysDate = DateFormat.getDateInstance().format(new Date());
        int todaysMonth = extractMonth(todaysDate);
        int todaysDay = extractDay(todaysDate);
        Log.i(TAG, "timeAgo day + month: " + todaysDay + "/" + todaysMonth + "/2016");

        int dayLapse = 0;
        double monthDiffInDays = Math.abs((tweetMonth - todaysMonth) * 30.43685);
        dayLapse += monthDiffInDays;
        dayLapse += Math.abs(tweetDay - todaysDay);
        Log.i(TAG, "timeAgo: dayLapse: " + dayLapse + " days ago");

        return dayLapse + " days ago";
    }

    public int extractMonth(String creationDate) {
        for (int i = 0; i < creationDate.length() - 1; i++) {
            if (i > 0) {
                String month = creationDate.charAt(i - 1) + creationDate.charAt(i) + creationDate.charAt(i + 1) + "";
                switch (month) {
                    case "Jan":
                        Log.i(TAG, "extractMonth: 1");
                        return 1;
                    case "Feb":
                        Log.i(TAG, "extractMonth: 2");
                        return 2;
                    case "Mar":
                        Log.i(TAG, "extractMonth: 3");
                        return 3;
                    case "Apr":
                        Log.i(TAG, "extractMonth: 4");
                        return 4;
                    case "May":
                        Log.i(TAG, "extractMonth: 5");
                        return 5;
                    case "Jun":
                        Log.i(TAG, "extractMonth: 6");
                        return 6;
                    case "Jul":
                        Log.i(TAG, "extractMonth: 7");
                        return 7;
                    case "Aug":
                        Log.i(TAG, "extractMonth: 8");
                        return 8;
                    case "Sep":
                        Log.i(TAG, "extractMonth: 9");
                        return 9;
                    case "Oct":
                        Log.i(TAG, "extractMonth: 10");
                        return 10;
                    case "Nov":
                        Log.i(TAG, "extractMonth: 11");
                        return 11;
                    case "Dec":
                        Log.i(TAG, "extractMonth: 12");
                        return 12;
                }
            }
        }
        return 0;
    }

    public int extractDay(String date) {
        for (int i = 0; i < date.length() - 1; i++) {
            if (i > 0) {
                String month = date.substring(i - 1, i + 2);
                if (month.equals("Jan") || month.equals("Feb") || month.equals("Mar") || month.equals("Apr") || month.equals("May") || month.equals("Jun")
                        || month.equals("Jul") || month.equals("Aug") || month.equals("Sep") || month.equals("Oct") || month.equals("Nov") || month.equals("Dec")) {
                    Log.i(TAG, "extractDate: day before parse " + date.substring(i + 2, i + 4));
                    int day = Integer.parseInt(date.substring(i + 3, i + 5));
                    Log.i(TAG, "extractDate: day " + day);
                    return day;
                }
            }
        }
        return 0;
    }

}
