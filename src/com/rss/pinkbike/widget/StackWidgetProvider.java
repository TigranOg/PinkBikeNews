package com.rss.pinkbike.widget;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;
import com.rss.pinkbike.R;
import com.rss.pinkbike.db.RssDataSource;
import com.rss.pinkbike.util.ApplicationManager;

import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: Tiga
 * Date: 5/19/13
 * Time: 2:51 PM
 */
public class StackWidgetProvider extends AppWidgetProvider {
    public static final String TOUCH_ACTION = "com.rss.pinkbike.TOUCH_ACTION";
    public static final String EXTRA_ITEM_LINK = "com.rss.pinkbike.EXTRA_ITEM_LINK";
    public static final String EXTRA_ITEM_POSITION = "com.rss.pinkbike.EXTRA_ITEM_POSITION";
    public static final String EXTRA_ITEM_STATE = "com.rss.pinkbike.EXTRA_ITEM_STATE";


    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @SuppressLint("NewApi")
    @Override
    public void onReceive(Context context, Intent intent) {
        AppWidgetManager mgr = AppWidgetManager.getInstance(context);
        if (intent.getAction().equals(TOUCH_ACTION)) {
            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);

            String link = intent.getStringExtra(EXTRA_ITEM_LINK);
            int position = intent.getIntExtra(EXTRA_ITEM_POSITION, 0);
            boolean state = intent.getBooleanExtra(EXTRA_ITEM_STATE, false);


            if (!state) {
                ApplicationManager.getInstance().getMapToShow().get(Integer.valueOf(position)).setState(1);
                RssDataSource dataSource =  new RssDataSource(context);

                try {
                    dataSource.open();
                    dataSource.updateRssStateByLink(link);

                } catch (SQLException e) {
                    Log.e("pinkbike", "list.setOnItemClickListener() SQLException ERROR");
                }

                dataSource.close();
            }

            Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(myIntent);

            if (mgr != null) {
                mgr.notifyAppWidgetViewDataChanged(appWidgetId, R.id.stack_view);
            }
        }
        super.onReceive(context, intent);
    }

    @SuppressLint("NewApi")
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // update each of the widgets with the remote adapter
        for (int i = 0; i < appWidgetIds.length; ++i) {

            // Here we setup the intent which points to the StackViewService which will
            // provide the views for this collection.
            Intent intent = new Intent(context, StackWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            // When intents are compared, the extras are ignored, so we need to embed the extras
            // into the data so that the extras will not be ignored.
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            rv.setRemoteAdapter(appWidgetIds[i], R.id.stack_view, intent);

            // The empty view is displayed when the collection has no items. It should be a sibling
            // of the collection view.
            rv.setEmptyView(R.id.stack_view, R.id.empty_view);

            // Here we setup the a pending intent template. Individuals items of a collection
            // cannot setup their own pending intents, instead, the collection as a whole can
            // setup a pending intent template, and the individual items can set a fillInIntent
            // to create unique before on an item to item basis.
            Intent touchIntent = new Intent(context, StackWidgetProvider.class);
            touchIntent.setAction(StackWidgetProvider.TOUCH_ACTION);
            touchIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            PendingIntent toastPendingIntent = PendingIntent.getBroadcast(context, 0, touchIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setPendingIntentTemplate(R.id.stack_view, toastPendingIntent);

            appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
}
