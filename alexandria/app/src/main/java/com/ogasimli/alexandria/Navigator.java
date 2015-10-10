package com.ogasimli.alexandria;

import com.ogasimli.alexandria.activities.addbook.AddBookActivity;
import com.ogasimli.alexandria.activities.bookdetail.BookDetailActivity;

import android.content.Context;

/**
 * Created by ogasimli on 03.10.2015.
 * Custom class used for navigation
 */
public class Navigator {

    public static void goToIsbnRegistration(Context context) {
        context.startActivity(AddBookActivity.launchIntent(context));
    }

    public static void goToBookDetail(Context context, long bookId) {
        context.startActivity(BookDetailActivity.launchIntent(context, bookId));
    }
}
