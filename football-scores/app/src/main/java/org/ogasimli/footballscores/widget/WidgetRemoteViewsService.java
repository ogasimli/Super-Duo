package org.ogasimli.footballscores.widget;

import com.ogasimli.footballscores.R;

import org.joda.time.LocalDate;
import org.ogasimli.footballscores.data.ScoresContract;
import org.ogasimli.footballscores.data.ScoresProvider;
import org.ogasimli.footballscores.object.FixtureAndTeam;
import org.ogasimli.footballscores.utilities.Utilities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;

/**
 * RemoteViewsService controlling the data being shown in the scrollable FIXTURES detail widget
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class WidgetRemoteViewsService extends RemoteViewsService {

    private static final String LOG_TAG = WidgetRemoteViewsService.class.getSimpleName();

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {

            private Cursor data = null;
            private ArrayList<FixtureAndTeam> mFixtureAndTeams;

            @Override
            public void onCreate() {
                //Get matches for today
                mFixtureAndTeams = new ArrayList<>();
                long todayMillis = new LocalDate().toDateTimeAtStartOfDay().getMillis();
                Log.d(LOG_TAG, "Widget loading matches for today");
                data = getContentResolver().query(
                        ScoresProvider.FIXTURES_AND_TEAMS_URI,
                        ScoresContract.FixturesAndTeamsView.projection,
                        ScoresContract.FixturesTable.DATE_COL + " = ?",
                        new String[]{Utilities.getDateMillisForQueryFormat(todayMillis)},
                        null
                );

                //Check if matches for day
                if(data != null && data.getCount() > 0) {
                    Log.d(LOG_TAG, "Fixtures found: " + data.getCount());

                    data.moveToFirst();
                    while(!data.isAfterLast()) {
                        mFixtureAndTeams.add(FixtureAndTeam.fromCursor(data));
                        data.moveToNext();
                    }
                }
            }

            @Override
            public void onDataSetChanged() {

            }

            @Override
            public void onDestroy() {
                mFixtureAndTeams.clear();
            }

            @Override
            public int getCount() {
                return data == null ? 0 : mFixtureAndTeams.size();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                FixtureAndTeam fixtureAndTeam = mFixtureAndTeams.get(position);
                RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout
                        .widget_item);

                String homeTeamName = fixtureAndTeam.homeTeamName;
                int homeTeamGoals = fixtureAndTeam.getHomeTeamGoals();
                String awayTeamName = fixtureAndTeam.awayTeamName;
                int awayTeamGoals = fixtureAndTeam.getAwayTeamGoals();

                remoteViews.setTextViewText(R.id.home_team_name, homeTeamName);
                remoteViews.setTextViewText(R.id.away_team_name, awayTeamName);
                remoteViews.setTextViewText(R.id.match_score, Utilities.getScores(getBaseContext(),
                        homeTeamGoals, awayTeamGoals));

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    setTeamCrestRemoteContentDescription(remoteViews, R.id.home_team_crest,
                            homeTeamName);
                    setTeamCrestRemoteContentDescription(remoteViews, R.id.away_team_crest,
                            awayTeamName);
                }

                return remoteViews;
            }

            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
            private void setTeamCrestRemoteContentDescription(RemoteViews views, int resourceId,
                                                              String description) {
                views.setContentDescription(resourceId, description);
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}