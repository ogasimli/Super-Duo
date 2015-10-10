package com.ogasimli.alexandria.activities.booklist;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import com.github.clans.fab.FloatingActionButton;
import com.ogasimli.alexandria.Navigator;
import com.ogasimli.alexandria.R;
import com.ogasimli.alexandria.data.AlexandriaContract;
import com.ogasimli.alexandria.object.Book;
import com.ogasimli.alexandria.services.BookService;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;


public class BookListFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemClickListener {

    private static final int LOADER_ID = 1000;

    private static final String LOG_TAG = BookListFragment.class.getSimpleName();

    private BookListAdapter mAdapter;

    private BookSearchBroadcastReceiver mBroadcastReceiver;

    private ProgressDialog mProgressDialog;

    private boolean mSearchRunning;

    private boolean mIsConnected;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.booksGrid)
    GridView mBooksGridView;

    @Bind(R.id.noBooksFoundMsg)
    TextView mNoBooksFoundMsg;

    @Bind(R.id.fab_add)
    FloatingActionButton mFabAdd;

    @Bind(R.id.fab_scan)
    FloatingActionButton mFabScan;

    @SuppressWarnings("unused")
    public static BookListFragment getInstance(String searchQuery) {
        BookListFragment fragment = new BookListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        registerBroadcastForBookSearch();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_list, container, false);
        ButterKnife.bind(this, view);

        //Setup toolbar
        ((BookListActivity) getActivity()).setSupportActionBar(mToolbar);

        //Setup add fab
        mFabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigator.goToIsbnRegistration(getActivity());
            }
        });

        //Setup scan fab
        mFabScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> formats = new ArrayList<>();
                formats.add("EAN_13");
                IntentIntegrator.forSupportFragment(BookListFragment.this).initiateScan(formats);
            }
        });

        //Setup list adapter
        mAdapter = new BookListAdapter(getActivity(), null, 0);
        mBooksGridView.setNumColumns(calculateSpanCount());
        mBooksGridView.setAdapter(mAdapter);

        //Setup click listener for list
        mBooksGridView.setOnItemClickListener(this);


        //Start cursor
        getLoaderManager().initLoader(LOADER_ID, null, this);

        //Check if search running
        if (mSearchRunning) {
            showProgressDialog(true);
        }

        return view;
    }

    /*Method to calculate grid span count*/
    private int calculateSpanCount() {
        int orientation = getResources().getConfiguration().orientation;
        int sw = getResources().getConfiguration().smallestScreenWidthDp;
        boolean landscape = (orientation == Configuration.ORIENTATION_LANDSCAPE);
        if (sw < 600) {
            return (landscape) ? 3 : 2;
        } else {
            return (landscape) ? 4 : 3;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);

        unregisterBroadcastForBookSearch();
    }

    /**
     * Loader callbacks
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(
                getActivity(),
                AlexandriaContract.BookEntry.FULL_CONTENT_URI,
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
        if (data == null)
            return;

        if (data.getCount() > 0) {
            mNoBooksFoundMsg.setVisibility(View.GONE);
        } else {
            mNoBooksFoundMsg.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    /**
     * Item click callbacks
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Cursor cursor = mAdapter.getCursor();
        cursor.moveToPosition(i);
        Book book = Book.fromCursor(cursor);
        Navigator.goToBookDetail(getActivity(), book != null ? book.bookId : 0);
    }

    /**
     * Get results of scanning and pass to BookService class
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                isOnline();
                if (mIsConnected) {
                    //Get and pass ISBN number to BookService
                    String isbnNumber = result.getContents();
                    BookService.fetchBook(getContext(), isbnNumber, LOG_TAG);
                    //Load ProgressDialog
                    mSearchRunning = true;
                    showProgressDialog(true);
                }else {
                    Toast.makeText(getActivity(), R.string.error_no_connection,
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * BroadcastReceiver class which notifies on results of search
     */
    public class BookSearchBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            long bookId = intent.getLongExtra(BookService.SEARCH_EVENT_BOOK_ID, 0);
            String message = intent.getStringExtra(BookService.SEARCH_EVENT_MESSAGE);
            String status = intent.getStringExtra(BookService.SEARCH_EVENT_STATUS);

            Log.d(LOG_TAG, status + " : " + message + " : " + bookId);

            mSearchRunning = false;
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                showProgressDialog(false);
            }

            //Book not found, Book already added
            if (status.equals(BookService.BOOK_NOT_FOUND + LOG_TAG))
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

            //Book already added
            if (status.equals(BookService.BOOK_ALREADY_ADDED + LOG_TAG)) {
                Navigator.goToBookDetail(getActivity(), bookId);
            }

            //Book found
            if (status.equals(BookService.BOOK_FOUND + LOG_TAG)) {
                Navigator.goToBookDetail(getActivity(), bookId);
            }
        }
    }

    private void registerBroadcastForBookSearch() {
        mBroadcastReceiver = new BookSearchBroadcastReceiver();
        IntentFilter filter = new IntentFilter(BookService.SEARCH_EVENT);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mBroadcastReceiver,filter);
    }

    private void unregisterBroadcastForBookSearch() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mBroadcastReceiver);
    }

    /*Helper method to show and hide ProgressDialog*/
    private void showProgressDialog(boolean show) {
        if (show) {
            mProgressDialog = ProgressDialog.show(getActivity(),
                    getString(R.string.progress_dialog_title),
                    getString(R.string.progress_dialog_content),true, true);
        }else if (mProgressDialog != null && mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
        }
    }

    /*Helper method to check if device has a network connection*/
    private void isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        mIsConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }
}