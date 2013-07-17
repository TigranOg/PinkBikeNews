package com.rss.pinkbike.util;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Tiga
 * Date: 5/14/13
 * Time: 1:37 AM
 */
public class BitmapManager {
    public static final String PATH = Environment.getExternalStorageDirectory() + "/" + "appData/Pinkbike/thumbnails/";
    private static final String TAG = "BitmapManager";

    public String getFileName(String imgUrl) {
        int index = imgUrl.length();
        return  imgUrl.substring(index - 14, index);
    }

    public Bitmap getBitmapForListView(String imgUrl) {
            Bitmap bm = null;
            String urlStr = imgUrl;
            String imgName = getFileName(urlStr);

            if (isFilePresent(imgName)) {
                try {
                    bm = loadImage(PATH + imgName);
                    if (bm != null && !bm.isRecycled()) {
                        return bm;
                    }
                    //return bm;
                } catch (Exception ex) {
                }
            }

            URL url;
            try {
                String uri = urlStr;
                if (uri.startsWith("http")) {
                    url = new URL(uri);
                    bm = readBitmapFromNetwork(url);
                    //Log.e(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@load from web: " + uri);
                } else
                    bm = loadImage(uri);
                //return bm;
            } catch (Exception ex) {
                Log.i(TAG, "Error load Bitmap from network:  " + ex);
            }
            return bm;
    }

    private synchronized Bitmap loadImage(final String uri) {
        if (uri.equals(""))
            return null;
        try {
            BitmapFactory.Options op = new BitmapFactory.Options();
            op.inPreferredConfig = Bitmap.Config.RGB_565;
            Bitmap bmp = BitmapFactory.decodeFile(uri, op);
            if (bmp != null && !bmp.isRecycled())
                return bmp;
        } catch (Exception ex) {
            Log.i(TAG, "loadImage from SD-CARD exception  " + ex);
        } catch (OutOfMemoryError e) {
            return loadImage(uri);
        }
        return null;
    }

    public Bitmap readBitmapFromNetwork(URL url) {
        InputStream is = null;
        BufferedInputStream bis = null;
        Bitmap bmp = null;
        try {
            is = fetch(url.toString());
            bis = new BufferedInputStream(is);
            BitmapFactory.Options op = new BitmapFactory.Options();
            op.inPreferredConfig = Bitmap.Config.RGB_565;
            bmp = BitmapFactory.decodeStream(bis, null, op);

            String filename = getFileName(url.toString());
            File dest = new File(PATH);
            if (!dest.exists()) {
                File path = new File(Environment.getExternalStorageDirectory() + "/appData");
                if (!path.exists())
                    path.mkdir();
                path = new File(Environment.getExternalStorageDirectory() + "/" + "appData/Pinkbike/");
                if (!path.exists())
                    path.mkdir();
                path = new File(Environment.getExternalStorageDirectory() + "/" + "appData/Pinkbike/thumbnails");
                if (!path.exists())
                    path.mkdir();
            }
            if(! new File(Environment.getExternalStorageDirectory() + "/" + "appData/Pinkbike/thumbnails/.thumbnails").createNewFile()) {
                Log.i(TAG, "ERROR create file appData/Pinkbike/thumbnails/.thumbnails");
            }
            dest = new File(PATH, filename);
            FileOutputStream out = new FileOutputStream(dest);
            bmp.compress(Bitmap.CompressFormat.JPEG, 92, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            Log.i(TAG, "Error load im  " + e);
        } catch (OutOfMemoryError e) {
//            clearCache();
            return readBitmapFromNetwork(url);
        } finally {
            try {
                if (is != null)
                    is.close();
                if (bis != null)
                    bis.close();
            } catch (IOException e) {
            }
        }
        return bmp;
    }

    private InputStream fetch(String address) throws IOException {
        Log.e(TAG, "load from web: " + address);
        HttpGet httpRequest = new HttpGet(URI.create(address));
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response = (HttpResponse) httpclient.execute(httpRequest);
        HttpEntity entity = response.getEntity();
        InputStream instream = new BufferedInputStream(entity.getContent());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int pos = 0;
        byte[] buffer = new byte[1024];
        while ((pos = instream.read(buffer)) != -1) {
            baos.write(buffer, 0, pos);
        }

        return new ByteArrayInputStream(baos.toByteArray());
    }

    private boolean isFilePresent(String name) {
        File dest = new File(PATH + name);
        return dest.exists();
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return resizedBitmap;
    }
}
