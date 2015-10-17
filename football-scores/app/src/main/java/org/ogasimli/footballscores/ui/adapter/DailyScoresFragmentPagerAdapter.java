package org.ogasimli.footballscores.ui.adapter;

import com.ogasimli.footballscores.R;

import org.joda.time.LocalDate;
import org.ogasimli.footballscores.ui.fragment.FixturesFragment;
import org.ogasimli.footballscores.utilities.Utilities;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.Locale;

/**
 * Pager adapter class managing tabs
 */
public class DailyScoresFragmentPagerAdapter extends FragmentStatePagerAdapter {

    private static final int NUM_PAGES = 6;

    private final Context mContext;

    public DailyScoresFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        long millis = Utilities.getLocalDateForItem(position).toDateTimeAtStartOfDay().getMillis();
        return FixturesFragment.newInstance(millis);
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch(position) {
            case 1:
                return mContext.getString(R.string.yesterday);
            case 2:
                return mContext.getString(R.string.today);
            case 3:
                return mContext.getString(R.string.tomorrow);
            default:
                LocalDate localDate = Utilities.getLocalDateForItem(position);
                //DateTimeFormatter fmt = DateTimeFormat.forPattern("EEEE").withLocale(Locale.UK);
                return localDate.dayOfWeek().getAsText(Locale.UK);
        }
    }
}
