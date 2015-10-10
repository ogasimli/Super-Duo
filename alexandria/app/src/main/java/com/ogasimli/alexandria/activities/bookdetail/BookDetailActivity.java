package com.ogasimli.alexandria.activities.bookdetail;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ogasimli.alexandria.R;
import com.ogasimli.alexandria.data.AlexandriaContract;
import com.ogasimli.alexandria.object.Book;
import com.ogasimli.alexandria.services.BookService;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ogasimli on 03.10.2015.
 */
public class BookDetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = BookDetailActivity.class.getSimpleName();

    //Constants
    private static final String ARGS_BOOK_ID = "book_id";
    private static final int LOADER_ID = 999;

    //Variables
    private long mBookId;
    private Book mBook;
    private ShareActionProvider mShareActionProvider;

    //Controls
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.book_cover)
    ImageView mBookCoverView;

    @Bind(R.id.book_title)
    TextView mBookTitleView;

    @Bind(R.id.book_author)
    TextView mBookAuthorView;

    @Bind(R.id.book_category)
    TextView mBookCategoryView;

    @Bind(R.id.book_description)
    TextView mBookDescriptionView;


    /**
     * Factory
     */
    public static Intent launchIntent(Context context, long bookId) {
        Intent intent = new Intent(context, BookDetailActivity.class);
        intent.putExtra(ARGS_BOOK_ID, bookId);

        return intent;
    }

    /**
     * Lifecycle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
        ButterKnife.bind(this);

        //Get Book Id
        mBookId = getIntent().getLongExtra(ARGS_BOOK_ID, -1);
        Log.d(LOG_TAG, "Details for Book: " + mBookId);

        //Prepare Loader
        getSupportLoaderManager().initLoader(LOADER_ID, null, this);

        //Setup action bar
        initToolbar();
    }

    /**
     * Menu methods
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_book_detail, menu);

        MenuItem menuItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_delete) {
            BookService.deleteBook(this, Long.toString(mBookId));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Custom methods
     */
    private void displayBookInfo() {
        //Progress Bar
        if (mBook != null){
            //Cover
                Glide.with(this)
                        .load(mBook.bookCoverUrl)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.ic_placeholder)
                        .error(R.drawable.ic_placeholder)
                        .into(mBookCoverView);

            //Data
            mBookTitleView.setText(mBook.bookTitle);
            mToolbar.setTitle(R.string.title_activity_book_details);
            mBookAuthorView.setText(mBook.bookAuthorName);
            mBookDescriptionView.setText(mBook.bookDescription);
            if (null == mBook.bookCategory || mBook.bookCategory.length()==0){
                mBookCategoryView.setText(R.string.unknown_book_category);
            } else {
                mBookCategoryView.setText(mBook.bookCategory);
            }
        }

        //Share intent
        setShareIntent();
    }

    private void setShareIntent() {
        if(mShareActionProvider == null || mBook == null)
            return;

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text) + mBook.bookTitle);
        mShareActionProvider.setShareIntent(shareIntent);
    }

    /*Initialize Toolbar*/
    @SuppressWarnings("ConstantConditions")
    private void initToolbar() {
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Loader Callbacks
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                this,
                AlexandriaContract.BookEntry.buildFullBookUri(mBookId),
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null) {
            finish();
            return;
        }

        data.moveToFirst();
        mBook = Book.fromCursor(data);

        displayBookInfo();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
