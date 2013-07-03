package com.rss.pinkbike.activities;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.Toast;
import com.rss.pinkbike.R;
import com.rss.pinkbike.entities.RssEntity;
import com.rss.pinkbike.db.RssDataSource;
import com.rss.pinkbike.util.ApplicationManager;
import com.rss.pinkbike.util.RssManager;
import com.rss.pinkbike.util.PullToRefreshListView;
import com.rss.pinkbike.views.CustomListAdapter;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends ListActivity {
    private Map<String, RssEntity> rssEntities;
    private RssDataSource dataSource;
    private ArrayList<HashMap<String, String>> adaptorList;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
    }

    private void initListView() {
        PullToRefreshListView list = (PullToRefreshListView) getListView();
        list.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ApplicationManager.getInstance().setNeedToUpdate(true);
                GetDataTask task = new GetDataTask();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    task.execute();
                }
                task = null;
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                String tag = (String) view.getTag();

                String link = tag.split("#666#")[0];
                String pos = tag.split("#666#")[1];

                RssEntity rss = ApplicationManager.getInstance().getMapToShow().get(Integer.valueOf(pos));

                if (!rss.getStateAsBoolean()) {
                    ApplicationManager.getInstance().getMapToShow().get(Integer.valueOf(pos)).setState(1);
                    try {
                        dataSource.open();
                        dataSource.updateRssStateByLink(link);

                    } catch (SQLException e) {
                        Log.e("pinkbike", "list.setOnItemClickListener() SQLException ERROR");
                    }

                    dataSource.close();

                    //TODO update widgets
                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                    int appWidgetId = getIntent().getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                                AppWidgetManager.INVALID_APPWIDGET_ID);
                    RemoteViews views = new RemoteViews(context.getPackageName(),
                            R.id.stack_view);
                    if (appWidgetManager != null) {
                        appWidgetManager.updateAppWidget(appWidgetId, views);
                    }
                }

                Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                startActivity(myIntent);
            }
        });

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                return false;
            }
        });

        adaptorList = new ArrayList<HashMap<String, String>>();

        CustomListAdapter adapter = new CustomListAdapter(this, adaptorList);

        list.setAdapter(adapter);
    }

    private class GetDataTask extends AsyncTask<Void, Void, ArrayList<HashMap<String, String>>> {
        private boolean isOnline = false;
        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            isOnline = netInfo != null && netInfo.isConnectedOrConnecting();
            dialog = ProgressDialog.show(MainActivity.this, null, "Loading...");
        }


        @Override
        protected ArrayList<HashMap<String, String>> doInBackground(Void... params) {
            ArrayList<HashMap<String, String>> dateList = new ArrayList<HashMap<String, String>>();

            long currTime = new Date().getTime();

            if(ApplicationManager.getInstance().isNeedToUpdate() || (currTime - ApplicationManager.getInstance().getStartTime() >= 30*60*1000)) {

                try {
                    dataSource.open();
                    rssEntities = dataSource.getAllRss();

                    Map<String, RssEntity> rssEntityMap = new HashMap<String, RssEntity>();

                    if (isOnline) {
                        rssEntityMap = RssManager.getRssMap(rssEntities.size());
                    }

                    HashMap<Integer, RssEntity> mapToShow = new HashMap<Integer, RssEntity>();

                    for (RssEntity rss : rssEntityMap.values()) {
                        if (!rssEntities.containsKey(rss.getLink())) {
                            dataSource.createRss(rss);
                            rssEntities.put(rss.getLink(), rss);
                        }
                    }

                    for (RssEntity rss : rssEntities.values()) {
                        mapToShow.put(rss.getPosition(), rss);
                    }

                    ApplicationManager.getInstance().setMapToShow(mapToShow);

                } catch (SQLException e) {
                    Log.e("pinkbike", "void GetDataTask.doInBackground() SQLException ERROR");
                }

                dataSource.close();

            }

            fillDateList(dateList);
            return dateList;
        }

        @Override
        protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
            if (!isOnline) {
                Toast toast = Toast.makeText(MainActivity.this, "No connection to internet.", Toast.LENGTH_LONG);
                toast.show();
            }

            adaptorList.clear();
            adaptorList.addAll(result);

            if (dialog != null) {
                dialog.dismiss();
            }

            ApplicationManager.getInstance().setStartTime(new Date().getTime());
            ApplicationManager.getInstance().setNeedToUpdate(false);

            ((PullToRefreshListView) getListView()).onRefreshComplete();
            super.onPostExecute(result);
        }

        private void fillDateList(ArrayList<HashMap<String, String>> dateList) {
            SimpleDateFormat df = new SimpleDateFormat("d/MM/yyyy", Locale.ENGLISH);
            HashMap<Integer, RssEntity> mapToShow = ApplicationManager.getInstance().getMapToShow();
            Object[] sortedKeys = mapToShow.keySet().toArray();

            Arrays.sort(sortedKeys, Collections.reverseOrder());

            for (Object position : sortedKeys) {
                RssEntity rss = mapToShow.get(position);


                HashMap<String, String> mapTable = new HashMap<String, String>();
                mapTable.put("list_img", rss.getImgName());
                mapTable.put("link", rss.getLink());
                mapTable.put("pub_date", df.format(rss.getPubDate()));
                mapTable.put("title", rss.getTitle());
                mapTable.put("state", rss.getStateAsBoolean() ? "old" : "new");
                mapTable.put("position", String.valueOf(rss.getPosition()));
                dateList.add(mapTable);
            }
        }
    }

    @Override
    protected void onResume() {
        dataSource = new RssDataSource(this);
        rssEntities = new HashMap<String, RssEntity>();
        initListView();
        context = MainActivity.this;

        GetDataTask task = new GetDataTask();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            task.execute();
        }
        task = null;

        super.onResume();
    }
}
