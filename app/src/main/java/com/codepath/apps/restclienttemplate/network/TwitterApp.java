package com.codepath.apps.restclienttemplate.network;

import android.app.Application;
import android.content.Context;

import androidx.room.Room;

import com.codepath.apps.restclienttemplate.models.MyDatabase;
import com.codepath.apps.restclienttemplate.models.TwitterDatabase;
import com.facebook.stetho.Stetho;

/*
 * This is the Android application itself and is used to configure various settings
 * including the image cache in memory and on disk. This also adds a singleton
 * for accessing the relevant rest client.
 *
 *     TwitterClient client = TwitterApp.getRestClient(Context context);
 *     // use client to send requests to API
 *
 */
public class TwitterApp extends Application {

    TwitterDatabase twitterDatabase;
    MyDatabase myDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        // when upgrading versions, kill the original tables by using
		// fallbackToDestructiveMigration()
        twitterDatabase = Room.databaseBuilder(this, TwitterDatabase.class,
                TwitterDatabase.NAME).fallbackToDestructiveMigration().build();

        // use chrome://inspect to inspect your SQL database
        Stetho.initializeWithDefaults(this);
    }

    public static TwitterClient getRestClient(Context context) {
        return (TwitterClient) TwitterClient.getInstance(TwitterClient.class, context);
    }

    public TwitterDatabase getTwitterDatabase() {
        return twitterDatabase;
    }

    public MyDatabase getMyDatabase() {
        return myDatabase;
    }

    public void setMyDatabase(MyDatabase myDatabase) {
        this.myDatabase = myDatabase;
    }

}