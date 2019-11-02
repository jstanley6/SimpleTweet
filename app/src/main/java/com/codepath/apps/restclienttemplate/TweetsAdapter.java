package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;

import org.parceler.Parcel;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {

    Context context;
    List<Tweet> tweets;
    //pass in the context and list of tweets


    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    //for each row, inflate the layout
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View tweetView = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(tweetView);
    }

    //bind values based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        //Get the tweet at the passed in position
        Tweet tweet = tweets.get(position);

        //bind the tweet data into the ViewHolder
        holder.bind(tweet);

    }


    @Override
    public int getItemCount() {
        return tweets.size();
    }








    //define a view holder
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView ivProfileImage;
        TextView tvScreenName;
        TextView tvBody;
        RelativeLayout container;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvBody = itemView.findViewById(R.id.tvBody);
            container = itemView.findViewById(R.id.container);

            itemView.setOnClickListener(this);


        }

        public void bind(final Tweet tweet) {

            tvBody.setText(tweet.body);
            tvScreenName.setText(tweet.user.screenName);

            Glide.with(context).load(tweet.user.profileImageUrl).into(ivProfileImage);

//            container.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent intent = new Intent(context, DetailActivity.class);
//                    intent.putExtra("tweet", Parcels.wrap(tweet));
//                    context.startActivity(intent);
//                }
//            });

        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition(); // gets item position
            if (position != RecyclerView.NO_POSITION) { // Check if an item was deleted, but the user clicked it before the UI removed it
                Tweet tweet = tweets.get(position);

                Intent intent = new Intent(context, DetailActivity.class);
                    intent.putExtra("tweet", Parcels.wrap(tweet));
                    context.startActivity(intent);
            }
        }
    }
    // Clean all elements of the recycler
    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of tweets
    public void addAll(List<Tweet> tweetList) {
        tweets.addAll(tweetList);
        notifyDataSetChanged();
    }
}
