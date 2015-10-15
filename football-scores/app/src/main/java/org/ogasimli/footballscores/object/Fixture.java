package org.ogasimli.footballscores.object;

import org.ogasimli.footballscores.data.DbContract;
import org.ogasimli.footballscores.data.ScoresProvider;

import android.content.ContentResolver;
import android.content.ContentValues;

/**
 * Created by com.ogasimli on 11.10.2015.
 */
public class Fixture {

    public static void save(
            ContentResolver contentResolver, String id, String date, String time,
            String homeTeamId, String homeTeamName, String homeTeamGoals,
            String awayTeamId, String awayTeamName, String awayTeamGoals, String leagueId, String matchDay) {

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

        contentResolver.insert(ScoresProvider.FIXTURES_URI, fixtureValues);

    }
}
