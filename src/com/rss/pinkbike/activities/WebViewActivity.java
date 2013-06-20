package com.rss.pinkbike.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.rss.pinkbike.R;

/**
 * Created with IntelliJ IDEA.
 * User: Tiga
 * Date: 5/4/13
 * Time: 6:00 PM

 */
public class WebViewActivity extends Activity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.web_view);
    }

    @Override
    protected void onResume() {
        WebView webView = (WebView) findViewById(R.id.webView);
        webView.loadUrl(getIntent().getStringExtra("link"));
        super.onResume();
    }
}