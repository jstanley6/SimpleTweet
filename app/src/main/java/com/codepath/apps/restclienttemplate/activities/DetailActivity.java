package com.codepath.apps.restclienttemplate.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

public class DetailActivity extends AppCompatActivity {

   TextView tvScreenName;
   TextView tvBody;
   ImageView ivProfileImage;
   Tweet tweet;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        tvScreenName = findViewById(R.id.tvScreenName);
        tvBody = findViewById(R.id.tvBody);
        ivProfileImage = findViewById(R.id.ivProfileImage);
        tweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet"));
        tvScreenName.setText(tweet.user.screenName);
       Glide.with(this).load(tweet.user.profileImageUrl).into(ivProfileImage);
        tvBody.setText(tweet.body);

    }
}
