package com.ogasimli.alexandria.object;

import com.ogasimli.alexandria.data.AlexandriaContract;

import android.database.Cursor;

/**
 * Created by ogasimli on 03.10.2015.
 */
public class Book {

    public long bookId;

    public String bookTitle;

    private String bookSubTitle;

    public String bookAuthorName;

    public String bookDescription;

    public String bookCategory;

    public String bookCoverUrl;

    private Book() {
        bookId = 0;
        bookTitle = "";
        bookSubTitle = "";
        bookAuthorName = "";
        bookDescription = "";
        bookCategory = "";
        bookCoverUrl = "";
    }

    public static Book fromCursor(Cursor cursor) {
        Book book = new Book();
        if (null == cursor) {
            return null;
        } else if (cursor.getCount() < 1) {
            return null;
        } else {
            book.bookId = cursor.getLong(cursor.getColumnIndex(AlexandriaContract.BookEntry._ID));
            book.bookTitle = cursor.getString(cursor.getColumnIndex(AlexandriaContract.BookEntry.TITLE));
            book.bookSubTitle = cursor.getString(cursor.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE));
            book.bookAuthorName = cursor.getString(cursor.getColumnIndex(AlexandriaContract.AuthorEntry.AUTHOR));
            book.bookDescription = cursor.getString(cursor.getColumnIndex(AlexandriaContract.BookEntry.DESC));
            book.bookCategory = cursor.getString(cursor.getColumnIndex(AlexandriaContract.CategoryEntry.CATEGORY));
            book.bookCoverUrl = cursor.getString(cursor.getColumnIndex(AlexandriaContract.BookEntry.IMAGE_URL));
            return book;
        }
    }
}
