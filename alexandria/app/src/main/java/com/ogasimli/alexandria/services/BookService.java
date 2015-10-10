package com.ogasimli.alexandria.services;

import com.ogasimli.alexandria.R;
import com.ogasimli.alexandria.data.AlexandriaContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 */
public class BookService extends IntentService {

    private final String LOG_TAG = BookService.class.getSimpleName();

    private static final String FETCH_BOOK = "com.ogasimli.alexandria.services.action.FETCH_BOOK";
    private static final String DELETE_BOOK = "com.ogasimli.alexxaandria.services.action.DELETE_BOOK";
    private static final String EAN = "com.ogasimli.alexandria.services.extra.EAN";
    private static final String FRAGMENT_NAME = "com.ogasimli.alexandria.services.extra.FRAGMENT_NAME";

    //Message Constants
    public static final String SEARCH_EVENT = "book_search_event";
    public static final String SEARCH_EVENT_MESSAGE = "book_search.message_extra";
    public static final String SEARCH_EVENT_BOOK_ID = "book_search.book_id_extra";
    public static final String SEARCH_EVENT_STATUS = "book_search.status_extra";

    //Available Status Constants
    public static final String BOOK_ALREADY_ADDED = "book_already_added";
    public static final String BOOK_FOUND = "book_found";
    public static final String BOOK_NOT_FOUND = "book_not_found";

    /**
     * Constructor
     */
    public BookService() {
        super("BookService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (FETCH_BOOK.equals(action)) {
                final String ean = intent.getStringExtra(EAN);
                final String fragmentName = intent.getStringExtra(FRAGMENT_NAME);
                fetchBook(ean, fragmentName);
            } else if (DELETE_BOOK.equals(action)) {
                final String ean = intent.getStringExtra(EAN);
                deleteBook(ean);
            }
        }
    }

    public static void fetchBook(Context context, String ean, String fragmentName) {
        Intent intent = new Intent(context, BookService.class);
        intent.setAction(FETCH_BOOK);
        intent.putExtra(EAN, ean);
        intent.putExtra(FRAGMENT_NAME, fragmentName);
        context.startService(intent);
    }

    public static void deleteBook(Context context, String ean) {
        Intent intent = new Intent(context, BookService.class);
        intent.setAction(DELETE_BOOK);
        intent.putExtra(EAN, ean);
        context.startService(intent);
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void deleteBook(String ean) {
        if(ean!=null) {
            getContentResolver().delete(AlexandriaContract.BookEntry.
                    buildBookUri(Long.parseLong(ean)), null, null);
        }
    }

    /**
     * Handle action fetchBook in the provided background thread with the provided
     * parameters.
     */
    private void fetchBook(String ean, String fragmentName ) {

        if(ean.length()!=13){
            return;
        }

        Cursor bookEntry = getContentResolver().query(
                AlexandriaContract.BookEntry.buildBookUri(Long.parseLong(ean)),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        if (bookEntry != null) {
            if(bookEntry.getCount()>0){
                bookEntry.close();
                broadcastBookAlreadyAdded(ean, fragmentName);
                return;
            }
        }

        if (bookEntry != null) {
            bookEntry.close();
        }

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String bookJsonString = null;

        try {
            final String FORECAST_BASE_URL = "https://www.googleapis.com/books/v1/volumes?";
            final String QUERY_PARAM = "q";
            final String ISBN_PARAM = "isbn:" + ean;

            Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, ISBN_PARAM)
                    .build();

            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder builder = new StringBuilder();
            if (inputStream == null) {
                return;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append("\n");
            }

            if (builder.length() == 0) {
                return;
            }
            bookJsonString = builder.toString();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error ", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }

        }

        final String ITEMS = "items";
        final String VOLUME_INFO = "volumeInfo";
        final String TITLE = "title";
        final String SUBTITLE = "subtitle";
        final String AUTHORS = "authors";
        final String DESC = "description";
        final String CATEGORIES = "categories";
        final String IMG_URL_PATH = "imageLinks";
        final String IMG_URL = "thumbnail";

        try {
            JSONObject bookJson = new JSONObject(bookJsonString);
            JSONArray bookArray;
            if(bookJson.has(ITEMS)){
                bookArray = bookJson.getJSONArray(ITEMS);
            }else{
                broadcastNoBookFound(fragmentName);
                return;
            }

            JSONObject bookInfo = ((JSONObject) bookArray.get(0)).getJSONObject(VOLUME_INFO);

            String title = bookInfo.getString(TITLE);

            String subtitle = "";
            if(bookInfo.has(SUBTITLE)) {
                subtitle = bookInfo.getString(SUBTITLE);
            }

            String desc="";
            if(bookInfo.has(DESC)){
                desc = bookInfo.getString(DESC);
            }

            String imgUrl = "";
            if(bookInfo.has(IMG_URL_PATH) && bookInfo.getJSONObject(IMG_URL_PATH).has(IMG_URL)) {
                imgUrl = bookInfo.getJSONObject(IMG_URL_PATH).getString(IMG_URL);
            }

            writeBackBook(ean, title, subtitle, desc, imgUrl);

            if(bookInfo.has(AUTHORS)) {
                writeBackAuthors(ean, bookInfo.getJSONArray(AUTHORS));
            }
            if(bookInfo.has(CATEGORIES)){
                writeBackCategories(ean,bookInfo.getJSONArray(CATEGORIES) );
            }

            broadcastBookFound(ean, fragmentName);

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error ", e);
        }
    }

    private void writeBackBook(String ean, String title, String subtitle, String desc, String imgUrl) {
        ContentValues values= new ContentValues();
        values.put(AlexandriaContract.BookEntry._ID, ean);
        values.put(AlexandriaContract.BookEntry.TITLE, title);
        values.put(AlexandriaContract.BookEntry.IMAGE_URL, imgUrl);
        values.put(AlexandriaContract.BookEntry.SUBTITLE, subtitle);
        values.put(AlexandriaContract.BookEntry.DESC, desc);
        getContentResolver().insert(AlexandriaContract.BookEntry.CONTENT_URI,values);
    }

    private void writeBackAuthors(String ean, JSONArray jsonArray) throws JSONException {
        ContentValues values= new ContentValues();
        for (int i = 0; i < jsonArray.length(); i++) {
            values.put(AlexandriaContract.AuthorEntry._ID, ean);
            values.put(AlexandriaContract.AuthorEntry.AUTHOR, jsonArray.getString(i));
            getContentResolver().insert(AlexandriaContract.AuthorEntry.CONTENT_URI, values);
            values= new ContentValues();
        }
    }

    private void writeBackCategories(String ean, JSONArray jsonArray) throws JSONException {
        ContentValues values= new ContentValues();
        for (int i = 0; i < jsonArray.length(); i++) {
            values.put(AlexandriaContract.CategoryEntry._ID, ean);
            values.put(AlexandriaContract.CategoryEntry.CATEGORY, jsonArray.getString(i));
            getContentResolver().insert(AlexandriaContract.CategoryEntry.CONTENT_URI, values);
            values= new ContentValues();
        }
    }

    /**
     * Information broadcasts for receivers
     */
    private void broadcastBookAlreadyAdded(String bookId, String fragmentName) {
        String message = getResources().getString(R.string.book_already_in_library);

        Intent messageIntent = new Intent(SEARCH_EVENT);
        messageIntent.putExtra(SEARCH_EVENT_MESSAGE, message);
        messageIntent.putExtra(SEARCH_EVENT_BOOK_ID, Long.parseLong(bookId));
        messageIntent.putExtra(SEARCH_EVENT_STATUS, BOOK_ALREADY_ADDED + fragmentName);

        LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);
    }

    private void broadcastBookFound(String bookId, String fragmentName) {
        Intent messageIntent = new Intent(SEARCH_EVENT);
        messageIntent.putExtra(SEARCH_EVENT_MESSAGE, "");
        messageIntent.putExtra(SEARCH_EVENT_BOOK_ID, Long.parseLong(bookId));
        messageIntent.putExtra(SEARCH_EVENT_STATUS, BOOK_FOUND + fragmentName);

        LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);
    }

    private void broadcastNoBookFound(String fragmentName) {
        String message = getResources().getString(R.string.book_not_found);

        Intent messageIntent = new Intent(SEARCH_EVENT);
        messageIntent.putExtra(SEARCH_EVENT_MESSAGE, message);
        messageIntent.putExtra(SEARCH_EVENT_STATUS, BOOK_NOT_FOUND + fragmentName);

        LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);
    }
}