package com.ogasimli.alexandria.activities.booklist;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ogasimli.alexandria.R;
import com.ogasimli.alexandria.object.Book;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ogasimli on 03.10.2015.
 */
class BookListAdapter extends CursorAdapter {

    //ViewHolder
    public static class ViewHolder {

        @Bind(R.id.fullBookCover)
        ImageView bookCover;

        @Bind(R.id.listBookTitle)
        TextView bookTitle;

        @Bind(R.id.listBookAuthor)
        TextView bookAuthor;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    /**
     * Constructor
     */
    @SuppressWarnings("unused")
    public BookListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    /**
     * Adapter methods
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        Book book = Book.fromCursor(cursor);

        if (book != null) {
            Glide.with(context)
                    .load(book.bookCoverUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_placeholder)
                    .into(viewHolder.bookCover);

            viewHolder.bookCover.setContentDescription(book.bookTitle);
            viewHolder.bookTitle.setText(book.bookTitle);
            viewHolder.bookAuthor.setText(book.bookAuthorName);
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.book_list_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

}
