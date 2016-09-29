package com.example.ander.twitteroauthtake2;

/**
 * Created by ander on 8/10/2016.
 */
public class Tweet {

    String text;
    String created_at, mName, mLocation, mProfileImage, BackgroundImage;

    public Tweet(String text, String created_at, String mName, String mLocation, String mProfileImage, String backgroundImage) {
        this.text = text;
        this.created_at = created_at;
        this.mName = mName;
        this.mLocation = mLocation;
        this.mProfileImage = mProfileImage;
        BackgroundImage = backgroundImage;
    }

    public String getText() {
        return text;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getmName() {
        return mName;
    }

    public String getmLocation() {
        return mLocation;
    }

    public String getmProfileImage() {
        return mProfileImage;
    }

    public String getBackgroundImage() {
        return BackgroundImage;
    }
}
