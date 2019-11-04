package com.codepath.apps.restclienttemplate;

import com.codepath.apps.restclienttemplate.TweetWithUser;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface TweetDao {
    @Query("SELECT Tweet.body AS tweet_body, Tweet.created_at as tweet_createdAt, " +
            "User.* FROM Tweet INNER JOIN User ON Tweet.userId = User.id " +
            "ORDER BY Tweet.id DESC LIMIT 5")
    List<TweetWithUser> recentItems();

    // retrieving tweets is omitted

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertModel(Tweet... tweet);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertModel(User... toArray);
}
