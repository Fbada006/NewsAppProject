package com.disruption.newsapp.data;

public class NewsArticle {

    /*The title of the article*/
    private String newsTitle;

    /*The section of the article*/
    private String newsSection;

    /*The date of publication of the article*/
    private String newsDate;

    /*The url of the news article*/
    private String newsUrl;

    /*The author of the news article*/
    private String newsAuthor;


    /**
     * The Constructor for {@link NewsArticle} objects
     *
     * @param newsTitle   is the title of the article
     * @param newsSection is the section of the article such as Business
     * @param newsDate    is the date the article was written
     * @param newsUrl     is the url of the article
     * @param newsAuthor  is the news author
     */
    NewsArticle(String newsTitle, String newsSection, String newsDate, String newsUrl, String newsAuthor) {
        this.newsTitle = newsTitle;
        this.newsSection = newsSection;
        this.newsDate = newsDate;
        this.newsUrl = newsUrl;
        this.newsAuthor = newsAuthor;
    }

    /*Getters for all the variables above*/
    public String getNewsTitle() {
        return newsTitle;
    }

    public String getNewsSection() {
        return newsSection;
    }

    public String getNewsDate() {
        return newsDate;
    }

    public String getNewsUrl() {
        return newsUrl;
    }

    public String getNewsAuthor() {
        return newsAuthor;
    }
}
