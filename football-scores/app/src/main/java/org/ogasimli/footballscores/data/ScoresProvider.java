package org.ogasimli.footballscores.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Arrays;

/**
 * Database content provider class
 */
public class ScoresProvider extends ContentProvider {

    private static final String LOG_TAG = ScoresProvider.class.getSimpleName();

    private static DbHelper mDbHelper;

    //Uris
    public static final Uri TEAMS_URI =
            ScoresContract.BASE_CONTENT_URI.buildUpon().appendPath("teams").build();
    public static final Uri FIXTURES_URI =
            ScoresContract.BASE_CONTENT_URI.buildUpon().appendPath("fixtures").build();
    public static final Uri FIXTURES_AND_TEAMS_URI =
            ScoresContract.BASE_CONTENT_URI.buildUpon().appendPath("fixtures_teams").build();

    //Uri codes
    private static final int TEAMS_URI_CODE = 100;
    private static final int FIXTURES_URI_CODE = 101;
    private static final int FIXTURES_AND_TEAMS_URI_CODE = 102;

    @Override
    public boolean onCreate() {
        mDbHelper = new DbHelper(getContext());
        return true;
    }

    //URI matcher
    private final UriMatcher mUriMatcher = buildUriMatcher();
    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ScoresContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, "teams" , TEAMS_URI_CODE);
        matcher.addURI(authority, "fixtures" , FIXTURES_URI_CODE);
        matcher.addURI(authority, "fixtures_teams" , FIXTURES_AND_TEAMS_URI_CODE);
        return matcher;
    }

    @SuppressWarnings("ConstantConditions")
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Log.v(LOG_TAG, "query(uri=" + uri + ", proj=" + Arrays.toString(projection) + ", " +
                "selection=" + selection + ", selectionArgs=" + Arrays.toString(selectionArgs) +")");

        final SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor;

        final int match = mUriMatcher.match(uri);
        switch (match) {
            case TEAMS_URI_CODE:
                Log.d(LOG_TAG, ScoresContract.TEAMS_TABLE);
                cursor = db.query(ScoresContract.TEAMS_TABLE, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;

            case FIXTURES_URI_CODE:
                Log.d(LOG_TAG, ScoresContract.FIXTURES_TABLE);
                cursor = db.query(ScoresContract.FIXTURES_TABLE, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;

            case FIXTURES_AND_TEAMS_URI_CODE:
                Log.d(LOG_TAG, ScoresContract.FIXTURES_TEAMS_VIEW);
                cursor = db.query(ScoresContract.FIXTURES_TEAMS_VIEW, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;

            default:
                Log.e(LOG_TAG, "No implementation for " + uri);
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @SuppressWarnings("ConstantConditions")
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        Log.v(LOG_TAG, "insert(uri=" + uri + ", values=" + contentValues.toString() + ")");

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        long rowId;
        final int match = mUriMatcher.match(uri);

        switch (match) {
            case TEAMS_URI_CODE:
                rowId = db.insertWithOnConflict(ScoresContract.TEAMS_TABLE, null, contentValues,
                        SQLiteDatabase.CONFLICT_REPLACE);
                break;
            case FIXTURES_URI_CODE:
                rowId = db.insertWithOnConflict(ScoresContract.FIXTURES_TABLE, null, contentValues,
                        SQLiteDatabase.CONFLICT_REPLACE);
                break;
            default:
                Log.e(LOG_TAG, "No implementation for " + uri);
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        if(rowId != -1){
            getContext().getContentResolver().notifyChange(uri, null);
            return ContentUris.withAppendedId(uri, rowId);
        }else{
            return null;
        }
    }

    @Override
    public int delete(@NonNull Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
