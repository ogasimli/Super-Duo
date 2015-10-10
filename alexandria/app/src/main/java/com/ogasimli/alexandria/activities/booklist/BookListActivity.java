package com.ogasimli.alexandria.activities.booklist;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ogasimli.alexandria.R;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import butterknife.ButterKnife;

/**
 * Created by ogasimli on 03.10.2015.
 */
public class BookListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);
        ButterKnife.bind(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_book_list, menu);
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