package com.codepath.apps.restclienttemplate.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import okhttp3.Headers;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.apps.restclienttemplate.utils.EndlessRecyclerViewScrollListener;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.network.TweetWithUser;
import com.codepath.apps.restclienttemplate.adapters.TweetsAdapter;
import com.codepath.apps.restclienttemplate.network.TwitterApp;
import com.codepath.apps.restclienttemplate.network.TwitterClient;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.TweetDao;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class TimelineActivity extends AppCompatActivity {
    public static final int RESULT_CODE = 20;
    private SwipeRefreshLayout swipeContainer;
    TweetDao tweetDao;
    TwitterClient client;
    RecyclerView rvTweets;
    List<Tweet> tweets;
    TweetsAdapter tweetsAdapter;
    public static final String TAG = "TimelineActivity";
    EndlessRecyclerViewScrollListener scrollListener;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.compose) {
            //Compose Icon has been selected
            //Navigate to compose activity
            Intent intent = new Intent(this, ComposeActivity.class);
            startActivityForResult(intent, RESULT_CODE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == RESULT_CODE && resultCode == RESULT_OK) {

            //Get data from the intent (tweet)
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));
            //Update the RV with the tweet
            //modify data source
            tweets.add(0, tweet);
            //update the adapter
            tweetsAdapter.notifyItemInserted(0);
            rvTweets.smoothScrollToPosition(0);


        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);


        // Lookup the swipe container view
        swipeContainer = findViewById(R.id.swipeContainer);

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                populateHomeTimeline();
            }
        });


       client = TwitterApp.getRestClient(this);

       //Find the recylcerview
        rvTweets = findViewById(R.id.rvTweets);

        //initialize the list of tweets and adapter
        tweets = new ArrayList<>();
        tweetsAdapter = new TweetsAdapter(this, tweets);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //recylver view setup: layout manager and the adapter
        rvTweets.setLayoutManager(layoutManager);
        rvTweets.setAdapter(tweetsAdapter);
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.i(TAG, "onLoadMore " + page);
                loadMoreData();
            }
        };
        // Adds the scroll listener to RecyclerView
        rvTweets.addOnScrollListener(scrollListener);


        tweetDao = ((TwitterApp) getApplicationContext()).getTwitterDatabase().tweetDao();

        // Remember to always move DB queries off of the Main thread
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "Showing data from database");
                // Request list of Tweets with Users using DAO
                List<TweetWithUser> tweetsFromDatabase = tweetDao.recentItems();
                tweetsAdapter.clear();


                // TweetWithUser has to be converted Tweet objects with nested User objects (see next snippet)
                List<Tweet> tweetList = TweetWithUser.getTweetList(tweetsFromDatabase);

                if(!tweetList.isEmpty()) {
                    tweetsAdapter.addAll(tweetList);
                } else {
                    populateHomeTimeline();
                }
            }
        });



    }

    private void loadMoreData() {
        // Send an API request to retrieve appropriate paginated data
        client.getNextPageOfTweets(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {

                Log.i(TAG, "onSuccess for loadMoreData! " + json.toString());
                //  --> Deserialize and construct new model objects from the API response
                JSONArray jsonArray = json.jsonArray;
                try {
                    //  --> Append the new data objects to the existing set of items inside the array of items
                    //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`
                   List<Tweet> tweets = Tweet.fromJsonArray(jsonArray);
                   tweetsAdapter.addAll(tweets);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                Log.e(TAG, "onFailure for loadMoreData! " + response, throwable);
            }
        } ,tweets.get(tweets.size() - 1).id);

    }


    private void populateHomeTimeline() {

        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "onSuccess! " + json.toString());
                JSONArray jsonArray = json.jsonArray;
                try {
                    tweetsAdapter.clear();
                    final List<Tweet> freshTweets = Tweet.fromJsonArray(jsonArray);
                    final List<User> freshUsers = User.fromJsonTweetArray(jsonArray);
                    tweetsAdapter.addAll(freshTweets);

                    // Interaction with Database can't happen on the Main thread
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            // runInTransaction() allows to perform multiple db actions in a batch thus preserving consistency
                            ((TwitterApp) getApplicationContext()).getTwitterDatabase().runInTransaction(new Runnable() {
                                @Override
                                public void run() {
                                    // Inserting both Tweets and Users to their respective tables
                                    tweetDao.insertModel(freshUsers.toArray(new User[0]));
                                    tweetDao.insertModel(freshTweets.toArray(new Tweet[0]));
                                }
                            });
                        }
                    });
                    // Now we call setRefreshing(false) to signal refresh has finished
                    swipeContainer.setRefreshing(false);
                } catch (JSONException e) {
                    Log.e(TAG, "json exception", e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                Log.e(TAG, "onFailure! " + response, throwable);
            }
        });
    }
}
