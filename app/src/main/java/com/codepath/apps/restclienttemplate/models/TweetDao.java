package com.codepath.apps.restclienttemplate.models;

import com.codepath.apps.restclienttemplate.network.TweetWithUser;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface TweetDao {
    @Query("SELECT Tweet.body AS tweet_body, Tweet.created_at as tweet_created_at, " +
            "User.* FROM Tweet INNER JOIN User ON Tweet.userId = User.id " +
            "ORDER BY Tweet.id DESC LIMIT 5")
    List<TweetWithUser> recentItems();

    @Query("SELECT * FROM Tweet ORDER BY created_at DESC")
    List<Tweet> getTweets();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertModel(Tweet... tweet);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertModel(User... user);
}
