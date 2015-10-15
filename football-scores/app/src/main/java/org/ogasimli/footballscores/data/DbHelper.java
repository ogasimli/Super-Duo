package org.ogasimli.footballscores.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by yehya khaled on 2/25/2015.
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
        final String CreateScoresTable = "CREATE TABLE " + DbContract.FIXTURES_TABLE + " ("
                + DbContract.FixturesTable._ID + " INTEGER PRIMARY KEY,"
                + DbContract.FixturesTable.DATE_COL + " TEXT NOT NULL,"
                + DbContract.FixturesTable.TIME_COL + " INTEGER NOT NULL,"
                + DbContract.FixturesTable.HOME_ID_COL + " TEXT NOT NULL,"
                + DbContract.FixturesTable.HOME_NAME_COL + " TEXT NOT NULL,"
                + DbContract.FixturesTable.AWAY_ID_COL + " TEXT NOT NULL,"
                + DbContract.FixturesTable.AWAY_NAME_COL + " TEXT NOT NULL,"
                + DbContract.FixturesTable.LEAGUE_COL + " INTEGER NOT NULL,"
                + DbContract.FixturesTable.HOME_GOALS_COL + " TEXT NOT NULL,"
                + DbContract.FixturesTable.AWAY_GOALS_COL + " TEXT NOT NULL,"
                + DbContract.FixturesTable.MATCH_ID + " INTEGER NOT NULL,"
                + DbContract.FixturesTable.MATCH_DAY + " INTEGER NOT NULL,"
                + " UNIQUE ("+ DbContract.FixturesTable.MATCH_ID+") ON CONFLICT REPLACE"
                + " );";

        //Teams
        final String createTeamsTable = "CREATE TABLE " + DbContract.TEAMS_TABLE + " ("
                + DbContract.TeamsTable._ID + " INTEGER PRIMARY KEY,"
                + DbContract.TeamsTable.TEAM_ID + " TEXT NOT NULL,"
                + DbContract.TeamsTable.TEAM_NAME + " TEXT NOT NULL,"
                + DbContract.TeamsTable.TEAM_CREST_URL + " TEXT NOT NULL,"
                + " UNIQUE ("+ DbContract.TeamsTable.TEAM_ID +") ON CONFLICT REPLACE"
                + " );";

        db.execSQL(CreateScoresTable);
        db.execSQL(createTeamsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Remove old values when upgrading.
        db.execSQL("DROP TABLE IF EXISTS " + DbContract.FIXTURES_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DbContract.TEAMS_TABLE);
    }

    public static ContentValues buildTeamContentValues(String id, String name, String crestUrl) {
        ContentValues teamValues = new ContentValues();
        teamValues.put(DbContract.TeamsTable.TEAM_ID, id);
        teamValues.put(DbContract.TeamsTable.TEAM_NAME, name);
        teamValues.put(DbContract.TeamsTable.TEAM_CREST_URL, crestUrl);

        return  teamValues;
    }

    public static ContentValues buildFixtureContentValues(String id, String date, String time,
                                                          String homeTeamId, String homeTeamName,
                                                          String homeTeamGoals, String awayTeamId,
                                                          String awayTeamName, String awayTeamGoals,
                                                          String leagueId, String matchDay) {
        ContentValues fixtureValues = new ContentValues();
        fixtureValues.put(DbContract.FixturesTable.MATCH_ID, id);
        fixtureValues.put(DbContract.FixturesTable.DATE_COL, date);
        fixtureValues.put(DbContract.FixturesTable.TIME_COL, time);
        fixtureValues.put(DbContract.FixturesTable.HOME_ID_COL, homeTeamId);
        fixtureValues.put(DbContract.FixturesTable.HOME_NAME_COL, homeTeamName);
        fixtureValues.put(DbContract.FixturesTable.HOME_GOALS_COL, homeTeamGoals);
        fixtureValues.put(DbContract.FixturesTable.AWAY_ID_COL, awayTeamId);
        fixtureValues.put(DbContract.FixturesTable.AWAY_NAME_COL, awayTeamName);
        fixtureValues.put(DbContract.FixturesTable.AWAY_GOALS_COL, awayTeamGoals);
        fixtureValues.put(DbContract.FixturesTable.LEAGUE_COL, leagueId);
        fixtureValues.put(DbContract.FixturesTable.MATCH_DAY, matchDay);

        return fixtureValues;
    }
}