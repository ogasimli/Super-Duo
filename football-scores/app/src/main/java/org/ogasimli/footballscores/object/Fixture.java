package org.ogasimli.footballscores.object;

import org.ogasimli.footballscores.data.ScoresContract;
import org.ogasimli.footballscores.data.ScoresProvider;

import android.content.ContentResolver;
import android.content.ContentValues;

/**
 * Object class for holding fixture information
 */
public class Fixture {

    public static void save(
            ContentResolver contentResolver, String id, String date, String time,
            String homeTeamId, String homeTeamName, String homeTeamGoals,
            String awayTeamId, String awayTeamName, String awayTeamGoals, String leagueId, String matchDay) {

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

        contentResolver.insert(ScoresProvider.FIXTURES_URI, fixtureValues);

    }
}
