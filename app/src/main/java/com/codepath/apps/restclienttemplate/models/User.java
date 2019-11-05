package com.codepath.apps.restclienttemplate.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Parcel
@Entity
public class User {

    @ColumnInfo
    @PrimaryKey
    public long id;

    @ColumnInfo
    public String name;

    @ColumnInfo
    public String screenName;

    @ColumnInfo
    public String profileImageUrl;

    public User(){}

    public static User fromJson(JSONObject jsonObject) throws JSONException {

        User user = new User();

        user.name = jsonObject.getString("name");

        // we should read the value of id from JSON now
        user.id = jsonObject.getLong("id");
        user.screenName = jsonObject.getString("screen_name");
        user.profileImageUrl = jsonObject.getString("profile_image_url_https");

        return user;
    }

    public static List<User> fromJsonTweetArray(JSONArray jsonArray) throws JSONException {

        List<User> users = new ArrayList<>();

        for(int i = 0; i < jsonArray.length(); i++) {
            User user = User.fromJson(jsonArray.getJSONObject(i).getJSONObject("user"));
            users.add(user);
        }

        return users;
    }
}
