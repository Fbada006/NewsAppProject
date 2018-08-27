package com.disruption.newsapp.adapter;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.disruption.newsapp.R;
import com.disruption.newsapp.data.NewsArticle;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NewsArticleAdapter extends RecyclerView.Adapter<NewsArticleAdapter.MyViewHolder> {

    /*The List that contains the list of articles*/
    private List<NewsArticle> articles;

    /*Variable for the click listener*/
    final private RecyclerViewClickListener recyclerViewClickListener;

    public NewsArticleAdapter(List<NewsArticle> articles, RecyclerViewClickListener recyclerViewClickListener) {
        this.articles = articles;
        this.recyclerViewClickListener = recyclerViewClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflate the item and return a new ViewHolder
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.news_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        //Get the current article
        NewsArticle currentArticle = articles.get(position);
        //Set appropriate text on the views
        holder.newsTitle.setText(currentArticle.getNewsTitle());
        holder.newsSection.setText(currentArticle.getNewsSection());
        // holder.newsAuthor.setText(org.apache.commons.text.WordUtils.capitalize(currentArticle.getNewsAuthor()));

        //Present the date properly

        holder.newsDate.setText(formattedDate(currentArticle.getNewsDate()));
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    /**
     * The ViewHolder class containing an item
     */
    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //TextViews in the item
        TextView newsTitle;
        TextView newsSection;
        TextView newsAuthor;
        TextView newsDate;

        MyViewHolder(View itemView) {
            super(itemView);
            newsTitle = itemView.findViewById(R.id.news_title);
            newsSection = itemView.findViewById(R.id.news_section);
            newsAuthor = itemView.findViewById(R.id.news_author);
            newsDate = itemView.findViewById(R.id.news_date);
            //Call setOnClick Listener on the itemView above
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            recyclerViewClickListener.onRecyclerViewItemClicked(v, this.getAdapterPosition());
        }
    }

    /**
     * This interface will be responsible for listening to clicks
     */
    public interface RecyclerViewClickListener {
        /**
         * @param view     is the view clicked on
         * @param position is the index of the view
         */
        void onRecyclerViewItemClicked(View view, int position);
    }

    /**
     * Helper method to clear the ArrayList
     */
    public void clear() {
        //Clear the list
        articles.clear();
        //Let the adapter know about the change
        notifyDataSetChanged();
    }

    /**
     * Helper method to add all to the ArrayList
     *
     * @param newsArticles is the list of news articles to add
     */
    public void addAll(List<NewsArticle> newsArticles) {
        //Add all news articles to the list
        articles.addAll(newsArticles);
        //Let the adapter know about the change
        notifyDataSetChanged();
    }

    //Helper method for formatting the date
    private String formattedDate(String utcTime) {
        Date date = null;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        try {
            date = format.parse(utcTime.replaceAll("Z$", "+0000"));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        assert date != null;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return dateFormat.format(date) + ", " + timeFormat.format(date);
    }
}
