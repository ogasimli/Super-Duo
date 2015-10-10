package com.ogasimli.alexandria.activities.addbook;

import com.ogasimli.alexandria.R;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ogasimli on 03.10.2015.
 */
public class AddBookActivity extends AppCompatActivity {

    //Variables
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    /**
     * Factory
     */
    public static Intent launchIntent(Context context) {
        return new Intent(context, AddBookActivity.class);
    }

    /**
     * Lifecycle methods
     */
    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
        ButterKnife.bind(this);

        //Setup action bar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Setup fragment
        if(savedInstanceState != null) {
            AddBookFragment fragment = (AddBookFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.fragment_container);
        } else {
            AddBookFragment mFragment = AddBookFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, mFragment).commit();
        }
    }
}
