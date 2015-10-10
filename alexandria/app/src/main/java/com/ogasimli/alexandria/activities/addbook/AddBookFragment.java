package com.ogasimli.alexandria.activities.addbook;

import com.ogasimli.alexandria.Navigator;
import com.ogasimli.alexandria.R;
import com.ogasimli.alexandria.services.BookService;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ogasimli on 03.10.2015.
 */
public class AddBookFragment extends Fragment {

    //Variables
    private static final String LOG_TAG = AddBookActivity.class.getSimpleName();

    private BookSearchBroadcastReceiver mBroadcastReceiver;

    private ProgressDialog mProgressDialog;

    private boolean mSearchRunning;

    private boolean mIsConnected;

    //Controls
    @Bind(R.id.isbn_number)
    EditText mIsbnNumberView;

    @Bind(R.id.isbn_input_layout)
    TextInputLayout mIsbnInputLayout;

    @Bind(R.id.confirm_button)
    Button mConfirmButton;

    public static AddBookFragment newInstance() {
        AddBookFragment fragment = new AddBookFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    public AddBookFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        registerBroadcastForBookSearch();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_book, container, false);
        ButterKnife.bind(this, view);

        mIsbnNumberView.addTextChangedListener(new MyTextWatcher(mIsbnNumberView));

        //Register confirm button action
        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSearchRunning || !validateIsbn()) return;

                String isbnNumber = mIsbnNumberView.getText().toString();

                //Add ean 13 digits
                if (isbnNumber.length() == 10 && !isbnNumber.startsWith("978")) {
                    isbnNumber = "978" + isbnNumber;
                }

                isOnline();
                if (mIsConnected) {
                    BookService.fetchBook(getContext(), isbnNumber, LOG_TAG);
                    mSearchRunning = true;
                    showProgressDialog(true);
                } else {
                    Toast.makeText(getActivity(), R.string.error_no_connection,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        mIsbnNumberView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (mIsbnNumberView.getRight() -
                            mIsbnNumberView.getCompoundDrawables()
                                    [DRAWABLE_RIGHT].getBounds().width())) {
                        mIsbnNumberView.setText("");
                    }
                }
                return false;
            }
        });

        //Check if search running
        if (mSearchRunning) {
            showProgressDialog(true);
        }

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterBroadcastForBookSearch();
    }

    /**
    * Helper method to validate ISBN
    */
    private boolean validateIsbn() {
        String isbnNumber = mIsbnNumberView.getText().toString();
        if (isbnNumber.length() != 10 && isbnNumber.length() != 13) {
            mIsbnInputLayout.setError(getString(R.string.error_invalid_isbn_number));
            return false;
        } else {
            mIsbnInputLayout.setErrorEnabled(false);
            return true;
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
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

            //Book already added
            if (status.equals(BookService.BOOK_ALREADY_ADDED + LOG_TAG)) {
                Navigator.goToBookDetail(getContext(), bookId);
            }

            //Book found
            if (status.equals(BookService.BOOK_FOUND + LOG_TAG)) {
                Navigator.goToBookDetail(getContext(), bookId);
            }
        }

    }
    private void registerBroadcastForBookSearch() {
        mBroadcastReceiver = new BookSearchBroadcastReceiver();
        IntentFilter filter = new IntentFilter(BookService.SEARCH_EVENT);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mBroadcastReceiver,filter);
    }

    private void unregisterBroadcastForBookSearch() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mBroadcastReceiver);
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

    /**
     * TextWatcher class to watch EditText
     */
    private class MyTextWatcher implements TextWatcher {

        private final View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            if (view.getId() == R.id.isbn_number)
                validateIsbn();
        }
    }
}
