package org.ogasimli.footballscores.sync;

import com.ogasimli.footballscores.BuildConfig;
import com.ogasimli.footballscores.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ogasimli.footballscores.api.FootballDataService;
import org.ogasimli.footballscores.api.GetTeamInformationResponse;
import org.ogasimli.footballscores.data.ScoresContract;
import org.ogasimli.footballscores.data.ScoresProvider;
import org.ogasimli.footballscores.object.Fixture;
import org.ogasimli.footballscores.object.Team;
import org.ogasimli.footballscores.utilities.Utilities;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Sync adapter class for fetching data
 */
public class ScoresSyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String LOG_TAG = ScoresSyncAdapter.class.getSimpleName();
    private static final boolean DEBUG = true;

    public static final String ACTION_DATA_UPDATED =
            "com.ogasimli.footballscores.ACTION_DATA_UPDATED";

    private static final String API_KEY = BuildConfig.API_KEY;

    public ScoresSyncAdapter(Context context) {
        super(context, true);
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String authority,
                              ContentProviderClient contentProviderClient, SyncResult syncResult) {

        //Check api key
        if (DEBUG) {
            Log.d(LOG_TAG, "Api Key: " + API_KEY);
        }

        //Get next 3 days data including today
        getData("n4");

        //Get previous day data
        getData("p3");

        //Get team logos
        getTeamsCrestsUrls();
    }

    private void getData (String timeFrame) {
        //Creating fetch URL
        final String BASE_URL = "http://api.football-data.org/alpha/fixtures"; //Base URL
        final String QUERY_TIME_FRAME = "timeFrame"; //Time Frame parameter to determine days
        //final String QUERY_MATCH_DAY = "matchday";

        Uri fetchBuild = Uri.parse(BASE_URL).buildUpon().
                appendQueryParameter(QUERY_TIME_FRAME, timeFrame).build();
        if(DEBUG)
            Log.d(LOG_TAG, "The url we are looking at is: " + fetchBuild.toString()); //log spam

        HttpURLConnection connection = null;
        BufferedReader reader = null;
        String jsonData = null;
        //Opening Connection
        try {
            URL fetch = new URL(fetchBuild.toString());
            connection = (HttpURLConnection) fetch.openConnection();
            connection.setRequestMethod("GET");

            //Api key for service in string resource
            connection.addRequestProperty("X-Auth-Token", API_KEY);

            //Connect to api
            connection.connect();

            // Read the input stream into a String
            InputStream inputStream = connection.getInputStream();
            StringBuilder buffer = new StringBuilder();
            if (inputStream == null) {
                // Nothing to do.
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line).append("\n");
            }
            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return;
            }
            jsonData = buffer.toString();
            if(DEBUG)
                Log.d(LOG_TAG, jsonData);
        }
        catch (Exception e)
        {
            Log.e(LOG_TAG,"Exception here: " + e.getMessage());
            e.printStackTrace();
        }
        finally {
            if(connection != null)
            {
                connection.disconnect();
            }
            if (reader != null)
            {
                try {
                    reader.close();
                }
                catch (IOException e)
                {
                    Log.e(LOG_TAG,"Error Closing Stream");
                }
            }
        }
        try {
            if (jsonData != null) {
                //This bit is to check if the data contains any matches. If not, we call processJson on the dummy data
                JSONArray matches = new JSONObject(jsonData).getJSONArray("fixtures");
                if (matches.length() == 0) {
                    //if there is no data, call the function on dummy data
                    //this is expected behavior during the off season.
                    processJSONdata(getContext().getString(R.string.dummy_data), false);
                    return;
                }

                processJSONdata(jsonData, true);
            } else {
                //Could not Connect
                Log.d(LOG_TAG, "Could not connect to server.");
            }
        }
        catch(Exception e)
        {
            Log.e(LOG_TAG, "Json parsing error: " + e.getMessage());
        }
    }
    private void processJSONdata (String JSONdata, boolean isReal)
    {
        //JSON data
        //Response Tokens
        //Response links
        final String SEASON_LINK = "http://api.football-data.org/alpha/soccerseasons/";
        final String MATCH_LINK = "http://api.football-data.org/alpha/fixtures/";
        final String HOME_TEAM_LINK = "http://api.football-data.org/alpha/teams/";
        final String AWAY_TEAM_LINK = "http://api.football-data.org/alpha/teams/";
        //Link objects
        final String FIXTURES_OBJECT = "fixtures";
        final String LINKS_OBJECT = "_links";
        final String SOCCER_SEASON_OBJECT = "soccerseason";
        final String SELF_OBJECT = "self";
        final String HOME_TEAM_OBJECT = "homeTeam";
        final String AWAY_TEAM_OBJECT = "awayTeam";
        //JSON data tokens
        final String MATCH_DATE = "date";
        final String HOME_TEAM_NAME = "homeTeamName";
        final String AWAY_TEAM_NAME = "awayTeamName";
        final String RESULT = "result";
        final String HOME_TEAM_GOALS = "goalsHomeTeam";
        final String AWAY_TEAM_GOALS = "goalsAwayTeam";
        final String MATCH_DAY = "matchday";

        //Match data
        String matchId;
        String matchDay;
        String league;
        String matchDate;
        String matchTime;
        //Home team data
        String homeId;
        String homeName;
        String homeGoals;
        //Away tema data
        String awayId;
        String awayName;
        String awayGoals;

        try {
            JSONArray matches = new JSONObject(JSONdata).getJSONArray(FIXTURES_OBJECT);

            //ContentValues to be inserted
            for(int i = 0;i < matches.length();i++)
            {

                JSONObject matchData = matches.getJSONObject(i);
                league = matchData.getJSONObject(LINKS_OBJECT).getJSONObject(SOCCER_SEASON_OBJECT).
                        getString("href");
                league = league.replace(SEASON_LINK,"");
                //This if statement controls which leagues we're interested in the data from.
                //add leagues here in order to have them be added to the DB.
                // If you are finding no data in the app, check that this contains all the leagues.
                // If it doesn't, that can cause an empty DB, bypassing the dummy data routine.
                int leagueId = Integer.parseInt(league);
                if(     leagueId == Utilities.PREMIER_LEAGUE    ||
                        leagueId == Utilities.SERIE_A           ||
                        leagueId == Utilities.BUNDESLIGA1       ||
                        leagueId == Utilities.BUNDESLIGA2       ||
                        leagueId == Utilities.PRIMERA_DIVISION    )
                {
                    matchId = matchData.getJSONObject(LINKS_OBJECT).getJSONObject(SELF_OBJECT).
                            getString("href");
                    matchId = matchId.replace(MATCH_LINK, "");
                    if(!isReal){
                        //This if statement changes the match ID of the dummy data so that it all goes into the database
                        matchId=matchId+Integer.toString(i);
                    }

                    matchDate = matchData.getString(MATCH_DATE);
                    matchTime = matchDate.substring(matchDate.indexOf("T") + 1, matchDate.indexOf("Z"));
                    matchDate = matchDate.substring(0,matchDate.indexOf("T"));
                    DateFormat match_date = DateFormat.getDateTimeInstance();
                    match_date.setTimeZone(TimeZone.getTimeZone("UTC"));
                    try {
                        Date parseddate = match_date.parse(matchDate+matchTime);
                        DateFormat new_date = DateFormat.getDateTimeInstance();
                        new_date.setTimeZone(TimeZone.getDefault());
                        matchDate = new_date.format(parseddate);
                        matchTime = matchDate.substring(matchDate.indexOf(":") + 1);
                        matchDate = matchDate.substring(0,matchDate.indexOf(":"));

                        if(!isReal){
                            //This if statement changes the dummy data's date to match our current date range.
                            Date fragmentdate = new Date(System.currentTimeMillis()+((i-2)*86400000));
                            DateFormat mformat = DateFormat.getDateInstance();
                            matchDate=mformat.format(fragmentdate);
                        }
                    }
                    catch (Exception e)
                    {
                        Log.d(LOG_TAG, "error here!");
                        Log.e(LOG_TAG,e.getMessage());
                    }

                    //Extract match data
                    //Home team
                    homeId = matchData.getJSONObject(LINKS_OBJECT).getJSONObject(HOME_TEAM_OBJECT).getString("href").replace(HOME_TEAM_LINK, "");
                    homeName = matchData.getString(HOME_TEAM_NAME);
                    homeGoals = matchData.getJSONObject(RESULT).getString(HOME_TEAM_GOALS);
                    //Away team
                    awayId = matchData.getJSONObject(LINKS_OBJECT).getJSONObject(AWAY_TEAM_OBJECT).getString("href").replace(AWAY_TEAM_LINK, "");
                    awayName = matchData.getString(AWAY_TEAM_NAME);
                    awayGoals = matchData.getJSONObject(RESULT).getString(AWAY_TEAM_GOALS);
                    //Match
                    matchDay = matchData.getString(MATCH_DAY);
                    if(DEBUG) {
                        Log.v(LOG_TAG, "******************************");
                        Log.v(LOG_TAG, "Match: " + matchId);
                        Log.v(LOG_TAG, "Date: " + matchDate);
                        Log.v(LOG_TAG, "Time: " + matchTime);
                        Log.v(LOG_TAG, "Home: " + homeId + " " + homeName);
                        Log.v(LOG_TAG, "Away: " + awayId + " " + awayName);
                        Log.v(LOG_TAG, "Goals: " + homeGoals + " - " + awayGoals);
                    }

                    //Save teams if required
                    Team team;
                    ContentResolver contentResolver = getContext().getContentResolver();
                    //Home team
                    team = Team.withId(contentResolver, homeId);
                    if(team == null)
                        Team.save(contentResolver, homeId, homeName, "");

                    //Away team
                    team = Team.withId(contentResolver, awayId);
                    if(team == null)
                        Team.save(contentResolver, awayId, awayName, "");

                    //Save fixture
                    Fixture.save(
                            contentResolver, matchId, matchDate, matchTime,
                            homeId, homeName, homeGoals, awayId,
                            awayName, awayGoals, league, matchDay
                    );

                    updateWidgets();
                }
            }
        }
        catch (JSONException e)
        {
            Log.e(LOG_TAG,e.getMessage());
        }
    }

    private void getTeamsCrestsUrls() {
        Cursor cursor = getContext().getContentResolver().query(
                ScoresProvider.TEAMS_URI,
                null,
                ScoresContract.TeamsTable.TEAM_CREST_URL + " = '' ",
                null,
                null
        );

        if(cursor == null)
            return;

        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            Team team = Team.fromCursor(cursor);

            try {
                GetTeamInformationResponse response =
                        new FootballDataService().getTeamInformation(API_KEY, team.id);
                if(response != null && response.crestUrl != null && response.crestUrl.length() > 0) {
                    Team.save(getContext().getContentResolver(), team.id, team.name, response.crestUrl);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            cursor.moveToNext();
        }
        cursor.close();
    }

    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(AccountUtilities.createSyncAccount(context),
                AccountUtilities.AUTHORITY, bundle);
    }

    private void updateWidgets() {
        Context context = getContext();
        // Setting the package ensures that only components in our app will receive the broadcast
        Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED)
                .setPackage(context.getPackageName());
        context.sendBroadcast(dataUpdatedIntent);
    }
}

