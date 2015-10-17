package org.ogasimli.footballscores.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Database helper class
 */
class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "football_scores.db";
    private static final int DATABASE_VERSION = 2;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //Fixtures
        final String CreateScoresTable = "CREATE TABLE " + ScoresContract.FIXTURES_TABLE + " ("
                + ScoresContract.FixturesTable._ID + " INTEGER PRIMARY KEY,"
                + ScoresContract.FixturesTable.DATE_COL + " TEXT NOT NULL,"
                + ScoresContract.FixturesTable.TIME_COL + " INTEGER NOT NULL,"
                + ScoresContract.FixturesTable.HOME_ID_COL + " TEXT NOT NULL,"
                + ScoresContract.FixturesTable.HOME_NAME_COL + " TEXT NOT NULL,"
                + ScoresContract.FixturesTable.AWAY_ID_COL + " TEXT NOT NULL,"
                + ScoresContract.FixturesTable.AWAY_NAME_COL + " TEXT NOT NULL,"
                + ScoresContract.FixturesTable.LEAGUE_COL + " INTEGER NOT NULL,"
                + ScoresContract.FixturesTable.HOME_GOALS_COL + " TEXT NOT NULL,"
                + ScoresContract.FixturesTable.AWAY_GOALS_COL + " TEXT NOT NULL,"
                + ScoresContract.FixturesTable.MATCH_ID + " INTEGER NOT NULL,"
                + ScoresContract.FixturesTable.MATCH_DAY + " INTEGER NOT NULL,"
                + " UNIQUE ("+ ScoresContract.FixturesTable.MATCH_ID+") ON CONFLICT REPLACE"
                + " );";

        //Teams
        final String createTeamsTable = "CREATE TABLE " + ScoresContract.TEAMS_TABLE + " ("
                + ScoresContract.TeamsTable._ID + " INTEGER PRIMARY KEY,"
                + ScoresContract.TeamsTable.TEAM_ID + " TEXT NOT NULL,"
                + ScoresContract.TeamsTable.TEAM_NAME + " TEXT NOT NULL,"
                + ScoresContract.TeamsTable.TEAM_CREST_URL + " TEXT NOT NULL,"
                + " UNIQUE ("+ ScoresContract.TeamsTable.TEAM_ID +") ON CONFLICT REPLACE"
                + " );";

        db.execSQL(CreateScoresTable);
        db.execSQL(createTeamsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Remove old values when upgrading.
        db.execSQL("DROP TABLE IF EXISTS " + ScoresContract.FIXTURES_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + ScoresContract.TEAMS_TABLE);
    }

    public static ContentValues buildTeamContentValues(String id, String name, String crestUrl) {
        ContentValues teamValues = new ContentValues();
        teamValues.put(ScoresContract.TeamsTable.TEAM_ID, id);
        teamValues.put(ScoresContract.TeamsTable.TEAM_NAME, name);
        teamValues.put(ScoresContract.TeamsTable.TEAM_CREST_URL, crestUrl);

        return  teamValues;
    }

    public static ContentValues buildFixtureContentValues(String id, String date, String time,
                                                          String homeTeamId, String homeTeamName,
                                                          String homeTeamGoals, String awayTeamId,
                                                          String awayTeamName, String awayTeamGoals,
                                                          String leagueId, String matchDay) {
        ContentValues fixtureValues = new ContentValues();
        fixtureValues.put(ScoresContract.FixturesTable.MATCH_ID, id);
        fixtureValues.put(ScoresContract.FixturesTable.DATE_COL, date);
        fixtureValues.put(ScoresContract.FixturesTable.TIME_COL, time);
        fixtureValues.put(ScoresContract.FixturesTable.HOME_ID_COL, homeTeamId);
        fixtureValues.put(ScoresContract.FixturesTable.HOME_NAME_COL, homeTeamName);
        fixtureValues.put(ScoresContract.FixturesTable.HOME_GOALS_COL, homeTeamGoals);
        fixtureValues.put(ScoresContract.FixturesTable.AWAY_ID_COL, awayTeamId);
        fixtureValues.put(ScoresContract.FixturesTable.AWAY_NAME_COL, awayTeamName);
        fixtureValues.put(ScoresContract.FixturesTable.AWAY_GOALS_COL, awayTeamGoals);
        fixtureValues.put(ScoresContract.FixturesTable.LEAGUE_COL, leagueId);
        fixtureValues.put(ScoresContract.FixturesTable.MATCH_DAY, matchDay);

        return fixtureValues;
    }
}