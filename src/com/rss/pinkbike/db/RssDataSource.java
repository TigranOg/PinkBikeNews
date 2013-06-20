package com.rss.pinkbike.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.rss.pinkbike.entities.RssEntity;

import java.sql.SQLException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Tiga
 * Date: 5/4/13
 * Time: 6:28 PM
 */
public class RssDataSource {
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = {MySQLiteHelper.COLUMN_ID, MySQLiteHelper.COLUMN_PUB_DATE,
            MySQLiteHelper.COLUMN_TITLE, MySQLiteHelper.COLUMN_DESCRIPTION, MySQLiteHelper.COLUMN_LINK,
            MySQLiteHelper.IMG_NAME, MySQLiteHelper.COLUMN_STATE, MySQLiteHelper.COLUMN_POSITION};

    public RssDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void createRss(RssEntity rssEntity) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_PUB_DATE, rssEntity.getPubDate().getTime());
        values.put(MySQLiteHelper.COLUMN_TITLE, rssEntity.getTitle());
        values.put(MySQLiteHelper.COLUMN_DESCRIPTION, rssEntity.getDescription());
        values.put(MySQLiteHelper.COLUMN_LINK, rssEntity.getLink());
        values.put(MySQLiteHelper.IMG_NAME, rssEntity.getImgName());
        values.put(MySQLiteHelper.COLUMN_STATE, rssEntity.getState());
        values.put(MySQLiteHelper.COLUMN_POSITION, rssEntity.getPosition());
        database.insert(MySQLiteHelper.TABLE_NAME, null, values);
    }

    public Map<String, RssEntity> getAllRss() {
        Map<String, RssEntity> rssEntityMap = new HashMap<String, RssEntity>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_NAME,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            RssEntity rssEntity = cursorToUser(cursor);
            rssEntityMap.put(rssEntity.getLink(), rssEntity);
            cursor.moveToNext();
        }

        cursor.close();

        return rssEntityMap;
    }

    public void updateRssStateByLink(String link) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_STATE, 1);
        database.update(MySQLiteHelper.TABLE_NAME, values, MySQLiteHelper.COLUMN_LINK + " = " + "'" + link + "'", null);
    }

    public List<RssEntity> getTop20Rss() {
        List<RssEntity> rssEntityMap = new ArrayList<RssEntity>();

        Cursor cursor = database.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_NAME + " LIMIT 20;", null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            RssEntity rssEntity = cursorToUser(cursor);
            rssEntityMap.add(rssEntity);
            cursor.moveToNext();
        }

        cursor.close();

        return rssEntityMap;
    }

    private RssEntity cursorToUser(Cursor cursor) {
        RssEntity rssEntity = new RssEntity();

        rssEntity.setId(cursor.getInt(0));

        long pubDate = cursor.getLong(1);
        rssEntity.setPubDate(new Date(pubDate));

        rssEntity.setTitle(cursor.getString(2));
        rssEntity.setDescription(cursor.getString(3));
        rssEntity.setLink(cursor.getString(4));
        rssEntity.setImgName(cursor.getString(5));
        rssEntity.setState(cursor.getInt(6));
        rssEntity.setPosition(cursor.getInt(7));
        return rssEntity;
    }
}
