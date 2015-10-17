package org.ogasimli.footballscores.object;

import com.google.gson.Gson;

import org.ogasimli.footballscores.data.ScoresContract;
import org.ogasimli.footballscores.data.ScoresProvider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;

/**
 * Object class for holding team information
 */
public class Team {

    public String id;
    public String name;
    private String crestUrl;

    private Team() {
        id = name = crestUrl = "";
    }

    public boolean hasCrestUrl() {
        return crestUrl.length() > 0;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

    public static Team withId(ContentResolver contentResolver, String id) {
        String[] projection = null;
        String selection = ScoresContract.TeamsTable.TEAM_ID + " = ? ";
        String[] selectionArgs = new String[]{ id };
        String sortOrder = null;

        Cursor cursor = contentResolver.query(
                ScoresProvider.TEAMS_URI,
                null,
                selection,
                selectionArgs,
                null
        );

        if(cursor == null || cursor.getCount() == 0)
            return null;

        cursor.moveToFirst();
        return Team.fromCursor(cursor);
    }

    public static Team fromCursor(Cursor cursor) {
        Team team = new Team();
        team.id = cursor.getString(cursor.getColumnIndex(ScoresContract.TeamsTable.TEAM_ID));
        team.name = cursor.getString(cursor.getColumnIndex(ScoresContract.TeamsTable.TEAM_NAME));
        team.crestUrl = cursor.getString(cursor.getColumnIndex(ScoresContract.TeamsTable.TEAM_CREST_URL));

        return team;
    }

    public static void save(ContentResolver contentResolver, String id, String name, String crestUrl) {
        ContentValues teamValues = new ContentValues();
        teamValues.put(ScoresContract.TeamsTable.TEAM_ID, id);
        teamValues.put(ScoresContract.TeamsTable.TEAM_NAME, name);
        teamValues.put(ScoresContract.TeamsTable.TEAM_CREST_URL, crestUrl);

        contentResolver.insert(ScoresProvider.TEAMS_URI, teamValues);
    }
}
