package com.disruption.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.disruption.newsapp.adapter.NewsArticleAdapter;
import com.disruption.newsapp.data.NewsArticle;
import com.disruption.newsapp.data.NewsArticleLoader;
import com.disruption.newsapp.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<NewsArticle>>
        , NewsArticleAdapter.RecyclerViewClickListener {

    //Constant integer value for the loader ID
    private static final int EARTHQUAKE_LOADER_ID = 1;

    /*The ArrayList for storing the list of articles*/
    private List<NewsArticle> newsArticles;

    /*DataBinding instance*/
    ActivityMainBinding activityMainBinding;

    /*Connectivity manager*/
    private ConnectivityManager connectivityManager;

    /*Network info variable*/
    NetworkInfo activeNetwork;

    /*The adapter*/
    private NewsArticleAdapter newsArticleAdapter;

    /*Base Url for fetching data from the Guardian server*/
    private static final String GUARDIAN_REQUEST_URL =
            "https://content.guardianapis.com/search?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        //Check for the network status first before starting the loader
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetwork = null;
        if (connectivityManager != null) {
            activeNetwork = connectivityManager.getActiveNetworkInfo();
        }
        if (activeNetwork != null && activeNetwork.isConnected()) {

            //Get a reference to the loader manager so that interaction with loaders can be made possible
            LoaderManager loaderManager = getLoaderManager();

            //Initialize the loader passing in the ID constant, null for the bundle and "this" for the
            //last parameter since this activity implements the LoaderCallback interface
            loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, MainActivity.this);
        } else {
            activityMainBinding.loadingArticlesProgressBar.setVisibility(View.GONE);

            //Set the text to inform about a faulty connection
            activityMainBinding.emptyView.setText(R.string.no_internet_connection);
        }
        //The ArrayList of news articles
        newsArticles = new ArrayList<>();

        newsArticleAdapter = new NewsArticleAdapter(newsArticles, this);
        activityMainBinding.recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        activityMainBinding.recyclerView.setLayoutManager(layoutManager);
        activityMainBinding.recyclerView.setItemAnimator(new DefaultItemAnimator());

        //Set the adapter to the recycler view
        activityMainBinding.recyclerView.setAdapter(newsArticleAdapter);

        //Refresh when swiping down
        activityMainBinding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Check for the network status first before starting the loader
                connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                activeNetwork = null;
                if (connectivityManager != null) {
                    activeNetwork = connectivityManager.getActiveNetworkInfo();
                }
                if (activeNetwork != null && activeNetwork.isConnected()) {

                    //Get a reference to the loader manager so that interaction with loaders can be made possible
                    LoaderManager loaderManager = getLoaderManager();

                    //Initialize the loader passing in the ID constant, null for the bundle and "this" for the
                    //last parameter since this activity implements the LoaderCallback interface
                    loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, MainActivity.this);
                } else {
                    activityMainBinding.loadingArticlesProgressBar.setVisibility(View.GONE);

                    //Set the text to inform about a faulty connection
                    activityMainBinding.emptyView.setText(R.string.no_internet_connection);
                }

                //Show that there was a successful load so the indicator should be hidden
                activityMainBinding.swipeRefresh.setRefreshing(false);

                //The ArrayList of news articles
                newsArticles = new ArrayList<>();

                newsArticleAdapter = new NewsArticleAdapter(newsArticles, MainActivity.this);
                activityMainBinding.recyclerView.setHasFixedSize(true);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                activityMainBinding.recyclerView.setLayoutManager(layoutManager);
                activityMainBinding.recyclerView.setItemAnimator(new DefaultItemAnimator());

                //Set the adapter to the recycler view
                activityMainBinding.recyclerView.setAdapter(newsArticleAdapter);
            }
        });

        //Set the refreshing color
        activityMainBinding.swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.colorAccent));

    }

    @Override
    public Loader<List<NewsArticle>> onCreateLoader(int id, Bundle args) {
        //Get the shared preferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        //Retrieve the string value from the preferences
        String orderBy = sharedPreferences.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));

        //Retrieve the query value
        String query = sharedPreferences.getString(
                getString(R.string.settings_query_key),
                getString(R.string.settings_query_default));


        //Parse the base URL
        Uri baseUri = Uri.parse(GUARDIAN_REQUEST_URL);
        Uri.Builder builder = baseUri.buildUpon();

        //Append the necessary parameters from the preferences
        if (!query.isEmpty()) {
            builder.appendQueryParameter("q", query);
        } else {
            builder.appendQueryParameter("q", "football");
        }

        //Get the API Key
        String apiKey = BuildConfig.GuardianSecretKey;

        builder.appendQueryParameter("show-tags", "contributor");
        builder.appendQueryParameter("show-fields", "headline");
        builder.appendQueryParameter("page-size", "50");
        builder.appendQueryParameter("order-by", orderBy);
        builder.appendQueryParameter("api-key", apiKey);

        //Create and return a new loader using the Uri above
        return new NewsArticleLoader(this, builder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<NewsArticle>> loader, List<NewsArticle> articles) {
        //Hide the loading indicator
        activityMainBinding.loadingArticlesProgressBar.setVisibility(View.GONE);

        //Clear any existing data
        newsArticleAdapter.clear();

        //Make sure that there is a valid list of articles to add to the arrayList
        if (articles != null && !articles.isEmpty()) {
            newsArticleAdapter.addAll(articles);
        } else {
            //Set the appropriate text that there were no articles found
            activityMainBinding.emptyView.setText(R.string.no_articles_found);
        }

        //Stop the loader to allow for swipe to refresh
        getLoaderManager().destroyLoader(loader.getId());
    }

    @Override
    public void onLoaderReset(Loader<List<NewsArticle>> loader) {
        //Clear the list
        newsArticleAdapter.clear();

    }

    @Override
    public void onRecyclerViewItemClicked(View view, int position) {
        //Get the current feature
        NewsArticle currentArticle = newsArticles.get(position);

        //Parse the url
        Uri newsArticleUri = Uri.parse(currentArticle.getNewsUrl());

        //Create a new implicit intent for every item clicked
        Intent openWebPageIntent = new Intent(Intent.ACTION_VIEW, newsArticleUri);

        //Start the Intent
        startActivity(openWebPageIntent);
    }

    /**
     * This method initialize the contents of the Activity's options menu.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Specify what happens after the menu item is pressed
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
