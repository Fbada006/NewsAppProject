package com.disruption.newsapp.data;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.support.annotation.Nullable;

import java.util.List;

public class NewsArticleLoader extends AsyncTaskLoader<List<NewsArticle>> {

    //The url to query the Guardian server
    private String url;

    public NewsArticleLoader(Context context, String url) {
        super(context);
        this.url = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Nullable
    @Override
    public List<NewsArticle> loadInBackground() {
        //First make sure the url is not null
        if (url == null) {
            return null;
        }

        //Perform the network request and get the list of news articles
        return QueryUtils.getArticleData(url);
    }
}
