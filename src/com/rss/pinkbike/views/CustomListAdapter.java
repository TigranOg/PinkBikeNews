package com.rss.pinkbike.views;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.rss.pinkbike.R;
import com.rss.pinkbike.util.BitmapManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: Tiga
 * Date: 5/17/13
 * Time: 12:48 AM
 */
public class CustomListAdapter extends BaseAdapter {
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater = null;

    public CustomListAdapter(Activity activity, ArrayList<HashMap<String, String>> data) {
        this.data = data;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (convertView == null)
            vi = inflater.inflate(R.layout.list_row, null);

        TextView title = (TextView) vi.findViewById(R.id.title);
        TextView pub_date = (TextView) vi.findViewById(R.id.pub_date);
        ImageView thumb_image = (ImageView) vi.findViewById(R.id.list_image);
        ImageView state_image = (ImageView) vi.findViewById(R.id.state_image);

        HashMap<String, String> post = new HashMap<String, String>();
        post = data.get(position);
        title.setText(post.get("title"));
//        Sat, 18 May 2013 00:00:00 PDT
        String sDate = post.get("pub_date");
        SimpleDateFormat df = new SimpleDateFormat("d/M/yyyy", Locale.ENGLISH);
        try {
           Date date = df.parse(post.get("pub_date"));
            Date today = new Date();
            if (date.getMonth() == today.getMonth() && date.getYear() == today.getYear() && date.getDate() == today.getDate()) {
                sDate = "Today";
            }
        } catch (ParseException e) {
            Log.e("pinkbike:ERROR", "SimpleDateFormat ParseException");
        }

        pub_date.setText(sDate);

        thumb_image.setImageBitmap(BitmapFactory.decodeFile(BitmapManager.PATH + post.get("list_img")));

        if (post.get("state").equals("old")) {
            state_image.setVisibility(View.GONE);
        }


        vi.setTag(post.get("link")+"#666#"+post.get("position"));
        return vi;
    }
}