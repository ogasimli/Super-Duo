package org.ogasimli.footballscores.widget;

import com.ogasimli.footballscores.R;

import org.ogasimli.footballscores.sync.ScoresSyncAdapter;
import org.ogasimli.footballscores.ui.activity.MainActivity;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class WidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

            // Create an Intent to launch MainActivity
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            remoteViews.setOnClickPendingIntent(R.id.widget, pendingIntent);

            // Set up the collection
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                setRemoteAdapter(context, remoteViews);
            } else {
                setRemoteAdapterV11(context, remoteViews);
            }

            Intent clickIntent = new Intent(context, MainActivity.class);
            PendingIntent clickPendingIntent = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(clickIntent)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setPendingIntentTemplate(R.id.fixtures_list, clickPendingIntent);
            remoteViews.setEmptyView(R.id.fixtures_list, R.id.error_view);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
    }

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        super.onReceive(context, intent);
        if (ScoresSyncAdapter.ACTION_DATA_UPDATED.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, getClass()));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.fixtures_list);
        }
    }

    /**
     * Sets the remote adapter used to fill in the list items
     *
     * @param remoteViews RemoteViews to set the RemoteAdapter
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private static void setRemoteAdapter(Context context, @NonNull RemoteViews remoteViews) {
        remoteViews.setRemoteAdapter(R.id.fixtures_list,
                new Intent(context, WidgetRemoteViewsService.class));
    }

    /**
     * Sets the remote adapter used to fill in the list items
     *
     * @param remoteViews RemoteViews to set the RemoteAdapter
     */
    @SuppressWarnings("deprecation")
    private void setRemoteAdapterV11(Context context, @NonNull final RemoteViews remoteViews) {
        remoteViews.setRemoteAdapter(0, R.id.fixtures_list,
                new Intent(context, WidgetRemoteViewsService.class));
    }
}
