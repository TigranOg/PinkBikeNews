package com.rss.pinkbike.widget;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.rss.pinkbike.R;
import com.rss.pinkbike.db.RssDataSource;
import com.rss.pinkbike.entities.RssEntity;
import com.rss.pinkbike.util.ApplicationManager;
import com.rss.pinkbike.util.BitmapManager;
import com.rss.pinkbike.util.RssManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Tiga
 * Date: 5/19/13
 * Time: 2:48 PM
 */
@SuppressLint("NewApi")
public class StackWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StackRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

@SuppressLint("NewApi")
class StackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private RssDataSource dataSource;
    private List<RssEntity> rssEntities = new ArrayList<RssEntity>();
    private Context mContext;
    private int mAppWidgetId;

    public StackRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    public void onCreate() {
        // In onCreate() you setup any connections / cursors to your data source. Heavy lifting,
        // for example downloading or creating content etc, should be deferred to onDataSetChanged()
        // or getViewAt(). Taking more than 20 seconds in this call will result in an ANR.
        dataSource = new RssDataSource(mContext);


        // We sleep for 3 seconds here to show how the empty view appears in the interim.
        // The empty view is set in the StackWidgetProvider and should be a sibling of the
        // collection view.
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void onDestroy() {
        // In onDestroy() you should tear down anything that was setup for your data source,
        // eg. cursors, connections, etc.
        rssEntities.clear();
    }

    public int getCount() {
        return rssEntities != null ? rssEntities.size() : 0;
    }

    public RemoteViews getViewAt(int position) {
        // position will always range from 0 to getCount() - 1.

        // We construct a remote views item based on our widget item xml file, and set the
        // text based on the position.
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_item);
        rv.setTextViewText(R.id.text_item, rssEntities.get(position).getTitle());
        rv.setImageViewBitmap(R.id.img_item, BitmapFactory.decodeFile(BitmapManager.PATH + rssEntities.get(position).getImgName()));
        if (rssEntities.get(position).getStateAsBoolean()) {
            rv.setViewVisibility(R.id.label_new, View.GONE);
        }

        // Next, we set a fill-intent which will be used to fill-in the pending intent template
        // which is set on the collection view in StackWidgetProvider.
        Bundle extras = new Bundle();
        extras.putString(StackWidgetProvider.EXTRA_ITEM_LINK, rssEntities.get(position).getLink());
        extras.putInt(StackWidgetProvider.EXTRA_ITEM_POSITION, rssEntities.get(position).getPosition());
        extras.putBoolean(StackWidgetProvider.EXTRA_ITEM_STATE, rssEntities.get(position).getStateAsBoolean());

        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        rv.setOnClickFillInIntent(R.id.text_item, fillInIntent);

        // You can do heaving lifting in here, synchronously. For example, if you need to
        // process an image, fetch something from the network, etc., it is ok to do it here,
        // synchronously. A loading view will show up in lieu of the actual contents in the
        // interim.
        try {
            System.out.println("Loading view " + position);
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Return the remote views object.
        return rv;
    }

    public RemoteViews getLoadingView() {
        // You can create a custom loading view (for instance when getViewAt() is slow.) If you
        // return null here, you will get the default loading view.
        return null;
    }

    public int getViewTypeCount() {
        return 1;
    }

    public long getItemId(int position) {
        return position;
    }

    public boolean hasStableIds() {
        return true;
    }

    public void onDataSetChanged() {
        // This is triggered when you call AppWidgetManager notifyAppWidgetViewDataChanged
        // on the collection view corresponding to this factory. You can do heaving lifting in
        // here, synchronously. For example, if you need to process an image, fetch something
        // from the network, etc., it is ok to do it here, synchronously. The widget will remain
        // in its current state while work is being done here, so you don't need to worry about
        // locking up the widget.

        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        boolean isOnline = netInfo != null && netInfo.isConnectedOrConnecting();

        try {
            dataSource.open();
            Map<String, RssEntity> rssDbMap = dataSource.getAllRss();
            int pos = dataSource.getMaxPosition();

            Map<String, RssEntity> rssWebMap = new HashMap<String, RssEntity>();

            if (isOnline) {
                rssWebMap = RssManager.getRssMap(pos);
            }

            HashMap<Integer, RssEntity> mapToShow = new HashMap<Integer, RssEntity>();

            for (RssEntity rss : rssWebMap.values()) {
                if (!rssDbMap.containsKey(rss.getLink())) {
                    dataSource.createRss(rss);
                    rssDbMap.put(rss.getLink(), rss);
                }
            }

            for (RssEntity rss : rssDbMap.values()) {
                mapToShow.put(rss.getPosition(), rss);
            }

            ApplicationManager.getInstance().setMapToShow(mapToShow);

            rssEntities = dataSource.getTop20Rss();
        } catch (SQLException e) {
            Log.e("pinkbike", "void onDataSetChanged SQLException ERROR");
        }

        dataSource.close();

        ApplicationManager.getInstance().setStartTime(new Date().getTime());
        ApplicationManager.getInstance().setNeedToUpdate(false);
    }
}