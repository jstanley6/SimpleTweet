package com.codepath.apps.restclienttemplate.data;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.TweetDao;
import com.codepath.apps.restclienttemplate.models.User;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities={Tweet.class, User.class}, version=1)
public abstract class TwitterDatabase extends RoomDatabase {

    public abstract TweetDao tweetDao();
    // Database name to be used
    public static final String NAME = "TwitterDataBase";
}
