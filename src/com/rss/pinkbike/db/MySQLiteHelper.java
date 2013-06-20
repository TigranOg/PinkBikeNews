package com.rss.pinkbike.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created with IntelliJ IDEA.
 * User: Tiga
 * Date: 5/4/13
 * Time: 6:17 PM

 */
public class MySQLiteHelper  extends SQLiteOpenHelper {
    public static final String TABLE_NAME = "rss";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PUB_DATE = "pubdate";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_LINK = "link";
    public static final String IMG_NAME = "img_name";
    public static final String COLUMN_STATE = "state";
    public static final String COLUMN_POSITION = "position";

    private static final String DATABASE_NAME = "pinkbikerss.db";
    private static final int DATABASE_VERSION = 15;

    private static final String DATABASE_CREATE = "create table "
            + TABLE_NAME + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_PUB_DATE + " integer not null, "
            + COLUMN_TITLE + " text not null, "
            + COLUMN_DESCRIPTION + " text not null, "
            + COLUMN_LINK + " text not null, "
            + IMG_NAME + " text not null, "
            + COLUMN_STATE + " integer not null, "
            + COLUMN_POSITION + " integer not null "
            +");";


    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 15) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            db.execSQL(DATABASE_CREATE);
        }
    }
}
