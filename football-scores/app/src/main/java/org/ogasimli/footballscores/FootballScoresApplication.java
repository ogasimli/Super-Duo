package org.ogasimli.footballscores;

import net.danlew.android.joda.JodaTimeAndroid;

import android.app.Application;

/**
 * Created by com.ogasimli on 10.10.2015.
 */
public class FootballScoresApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        JodaTimeAndroid.init(this);
    }
}
