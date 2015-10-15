package org.ogasimli.footballscores.widget;

import com.ogasimli.footballscores.R;

import org.joda.time.LocalDate;
import org.ogasimli.footballscores.data.DbContract;
import org.ogasimli.footballscores.data.ScoresProvider;
import org.ogasimli.footballscores.object.FixtureAndTeam;
import org.ogasimli.footballscores.utilities.Utilities;

import android.appwidget.AppWidgetManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;

/**
 * Created by com.ogasimli on 11.10.2015.
 */
public class WidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetRemoteViewsFactory(getApplicationContext(), intent);
    }
}

class WidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private static final String LOG_TAG = WidgetRemoteViewsFactory.class.getSimpleName();

    private final Context mContext;
    private final ContentResolver mContentResolver;
    private final ArrayList<FixtureAndTeam> mFixtureAndTeams;

    public WidgetRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

        mContentResolver = mContext.getContentResolver();
        mFixtureAndTeams = new ArrayList<>();
    }

    @Override
    public void onCreate() {

        //Get matches for today
        long todayMillis = new LocalDate().toDateTimeAtStartOfDay().getMillis();
        Log.d(LOG_TAG, "Widget loading matches for today");
        Cursor cursor = mContentResolver.query(
                ScoresProvider.FIXTURES_AND_TEAMS_URI,
                DbContract.FixturesAndTeamsView.projection,
                DbContract.FixturesTable.DATE_COL + " = ?",
                new String[]{ Utilities.getDateMillisForQueryFormat(todayMillis) },
                null
        );

        //Check if matches for day
        if(cursor != null && cursor.getCount() > 0) {
            Log.d(LOG_TAG, "Fixtures found: " + cursor.getCount());

            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                mFixtureAndTeams.add(FixtureAndTeam.fromCursor(cursor));
                cursor.moveToNext();
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
        return mFixtureAndTeams.size();
    }

    @Override
    public RemoteViews getViewAt(int i) {
        FixtureAndTeam fixtureAndTeam = mFixtureAndTeams.get(i);

        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.widget_item);

        //Home team
        remoteViews.setTextViewText(R.id.home_team_name, fixtureAndTeam.homeTeamName);
        remoteViews.setTextViewText(R.id.home_team_score, fixtureAndTeam.getHomeTeamGoals() + "");

        //Away team
        remoteViews.setTextViewText(R.id.away_team_name, fixtureAndTeam.awayTeamName);
        remoteViews.setTextViewText(R.id.away_team_score, fixtureAndTeam.getAwayTeamGoals() + "");

        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
