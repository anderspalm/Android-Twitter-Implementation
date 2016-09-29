package com.example.ander.twitteroauthtake2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ander on 8/10/2016.
 */
public class TweetAdapter extends ArrayAdapter<Tweet> {
    private int mLayoutResource;
    Context mContext;
    ArrayList<Tweet> mTweeList;

    public TweetAdapter(Context context, int resource, ArrayList<Tweet> objects) {
        super(context, resource, objects);
        mLayoutResource = resource;
        mContext = context;
        mTweeList = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        view = layoutInflater.inflate(mLayoutResource, null);

        if(position <=mTweeList.size()) {
            Tweet tweet = mTweeList.get(position);

            TextView name = (TextView) view.findViewById(R.id.name);
            TextView text = (TextView) view.findViewById(R.id.longText);
            TextView location = (TextView) view.findViewById(R.id.location);
            TextView created_at = (TextView) view.findViewById(R.id.time);
            ImageView back_img = (ImageView) view.findViewById(R.id.background_image);
            ImageView pro_img = (ImageView) view.findViewById(R.id.profile_image);

            name.setText(tweet.getmName());
            text.setText(tweet.getText());
            created_at.setText(tweet.getCreated_at());
            location.setText(tweet.getmLocation());
//        Picasso.with(mContext).load(tweet.getBackgroundImage()).into(back_img);
            Picasso.with(mContext).load(tweet.getmProfileImage()).into(pro_img);
        }
        return view;
    }

    public void updateListItems(ArrayList<Tweet> newList) {
        mTweeList = newList;
        notifyDataSetChanged();
    }
}
