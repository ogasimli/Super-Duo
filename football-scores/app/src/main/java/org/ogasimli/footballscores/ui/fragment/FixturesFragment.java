package org.ogasimli.footballscores.ui.fragment;

import com.ogasimli.footballscores.R;

import org.ogasimli.footballscores.data.ScoresContract;
import org.ogasimli.footballscores.data.ScoresProvider;
import org.ogasimli.footballscores.object.FixtureAndTeam;
import org.ogasimli.footballscores.ui.adapter.FixturesCursorAdapter;
import org.ogasimli.footballscores.utilities.Utilities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Fixtures fragment class
 */
public class FixturesFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {

    //Constants
    private static final String ARGS_DATE_MILLIS = "date_millis";

    //Variables
    private long mDateMillis;
    private FixturesCursorAdapter mAdapter;
    private static final int LOADER_ID = 2000;

    //Controls
    @Bind(R.id.progress_bar)
    ProgressBar mProgressBarView;
    @Bind(R.id.list)
    ListView mListView;
    @Bind(R.id.error_view)
    TextView mErrorView;

    /**
     * Constructors and factories
     */
    public static FixturesFragment newInstance(long dateMillis) {
        FixturesFragment fragment = new FixturesFragment();
        Bundle args = new Bundle();
        args.putLong(ARGS_DATE_MILLIS, dateMillis);
        fragment.setArguments(args);

        return fragment;
    }

    public FixturesFragment() {
    }

    /**
     * Lifecycle methods
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mDateMillis = getArguments().getLong(ARGS_DATE_MILLIS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_fixtures, container, false);
        ButterKnife.bind(this, rootView);

        mAdapter = new FixturesCursorAdapter(getActivity());
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(this);

        getLoaderManager().initLoader(LOADER_ID, null, this);

        return rootView;
    }

    /**
     * Cursor callbacks
     */
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        mErrorView.setVisibility(View.GONE);
        mProgressBarView.setVisibility(View.VISIBLE);

        return new CursorLoader(
                getActivity(),
                ScoresProvider.FIXTURES_AND_TEAMS_URI,
                ScoresContract.FixturesAndTeamsView.projection,
                ScoresContract.FixturesTable.DATE_COL + " = ?",
                new String[]{Utilities.getDateMillisForQueryFormat(mDateMillis)},
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mProgressBarView.setVisibility(View.GONE);

        mAdapter.swapCursor(cursor);

        //Cursor is available
        if (cursor != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), ScoresProvider.FIXTURES_URI);
            cursor.setNotificationUri(getContext().getContentResolver(), ScoresProvider.TEAMS_URI);

            //No data found
            if (cursor.getCount() > 0) {
                mErrorView.setVisibility(View.GONE);
            } else {
                mErrorView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mAdapter.swapCursor(null);
    }

    /**
     * Item click callbacks
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        FixtureAndTeam fixtureAndTeam = mAdapter.getItem(position);

        if (fixtureAndTeam == null)
            return;

        String FOOTBALL_SCORES_HASHTAG = "#Football_Scores";

        String shareText =
                fixtureAndTeam.homeTeamName + " " +
                        "(" + fixtureAndTeam.getHomeTeamGoals() + " - "
                        + fixtureAndTeam.getAwayTeamGoals() + ") " +
                        fixtureAndTeam.awayTeamName + " " +
                        FOOTBALL_SCORES_HASHTAG;

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText + FOOTBALL_SCORES_HASHTAG);
        getContext().startActivity(Intent.createChooser(shareIntent,
                getString(R.string.share_intent_title)));
    }
}