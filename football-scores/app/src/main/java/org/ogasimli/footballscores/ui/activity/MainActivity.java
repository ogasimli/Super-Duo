package org.ogasimli.footballscores.ui.activity;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ogasimli.footballscores.R;

import org.ogasimli.footballscores.sync.AccountUtilities;
import org.ogasimli.footballscores.sync.ScoresSyncAdapter;
import org.ogasimli.footballscores.ui.adapter.DailyScoresFragmentPagerAdapter;

import android.accounts.Account;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final int TODAY_TAB_NUM = 2;

    //Controls
    @Bind(R.id.toolbar)
    Toolbar mToolbarView;
    @Bind(R.id.tabs)
    TabLayout mTabs;
    @Bind(R.id.pager)
    ViewPager mPager;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Account account = AccountUtilities.createSyncAccount(this);

        setSupportActionBar(mToolbarView);

        DailyScoresFragmentPagerAdapter adapter = new DailyScoresFragmentPagerAdapter(getSupportFragmentManager(), this);
        mPager.setAdapter(adapter);
        mTabs.setupWithViewPager(mPager);

        if(savedInstanceState == null) {
            //Get Today's fixtures
            mTabs.getTabAt(TODAY_TAB_NUM).select();

            //Request sync
            ScoresSyncAdapter.syncImmediately(this);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_about) {
            //Show About Dialog
            new MaterialDialog.Builder(this)
                    .title(R.string.action_about)
                    .content(R.string.about_text)
                    .positiveText(R.string.dialog_positive_button_text)
                    .show();
        }

        return super.onOptionsItemSelected(item);
    }
}
