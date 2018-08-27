package com.disruption.newsapp.data;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class QueryUtils {

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /*Read Timeout*/
    private static final int READ_TIMEOUT = 10000;

    /*Connect Timeout*/
    private static final int CONNECT_TIMEOUT = 10000;

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     */
    private QueryUtils() {
    }

    /**
     * @param queryUrl is the url to query the Guardian server
     * @return the list of articles
     */
    public static List<NewsArticle> getArticleData(String queryUrl) {
        //Create the url
        URL url = createUrl(queryUrl);

        //Perform a HTTP request to the url and receive a JSON response
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request", e);
        }

        //Get the list of articles
        return getNewsArticles(jsonResponse);
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        //Initialize a string for the JSON response from the Guardian server
        String jsonResponseFromGuardian = "";

        //Make sure the url is not null.
        if (url == null) {
            return jsonResponseFromGuardian;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        //Try making a connection
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(READ_TIMEOUT);
            urlConnection.setConnectTimeout(CONNECT_TIMEOUT);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            //Check if the request was successful. If it is, read the input stream
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponseFromGuardian = readFromInputStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error retrieving JSON data from the Guardian server: ", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }

        return jsonResponseFromGuardian;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromInputStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();

        //Make sure the input stream is not null before proceeding
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            while (line != null) {
                output.append(line);
                line = bufferedReader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link NewsArticle} objects after parsing the JSON response from the Guardian server
     */
    private static List<NewsArticle> getNewsArticles(String newsArticleJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsArticleJSON)) {
            return null;
        }

        //An ArrayList for holding the articles
        List<NewsArticle> articles = new ArrayList<>();

        // Try to parse the JSON response string.
        try {
            // Parse the response given by the JSON response string and
            // build up a list of NewsItem objects with the corresponding data.
            JSONObject baseJsonResponse = new JSONObject(newsArticleJSON);

            //Get JSON Object
            JSONObject response = baseJsonResponse.getJSONObject("response");

            //Get the array
            JSONArray results = response.getJSONArray("results");

            //Loop through the array and get the attributes
            for (int i = 0; i < results.length(); i++) {

                //Get the JSON object at index i
                JSONObject currentArticle = results.getJSONObject(i);

                //Get the fields object
                JSONObject fields = currentArticle.getJSONObject("fields");

                //Get the title of the article
                String newsTitle = fields.optString("headline");

                //Get the section of the article
                String newsSection = currentArticle.optString("sectionName");

                //Get the date of publication
                String newsDate = currentArticle.optString("webPublicationDate");

                //Get th url of the article
                String newsUrl = currentArticle.optString("webUrl");

                //Get the tags array
                JSONArray tags = currentArticle.getJSONArray("tags");

                //Get the first object in the tags array
                JSONObject currentArticleTags = tags.getJSONObject(0);

                //Get the first name
                String newsAuthorFirstName = currentArticleTags.optString("firstName");

                //Get the last name
                String newsAuthorLastName = currentArticleTags.optString("lastName");

                //Get the name of the author
                String newsAuthor = newsAuthorFirstName + " " + newsAuthorLastName;

                //Create a new NewsArticle object
                NewsArticle newsArticle = new NewsArticle(newsTitle, newsSection, newsDate, newsUrl, newsAuthor);

                //Add this article to the list of articles
                articles.add(newsArticle);
            }

        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        return articles;
    }
}
