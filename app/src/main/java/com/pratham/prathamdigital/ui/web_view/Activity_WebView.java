package com.pratham.prathamdigital.ui.web_view;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.pratham.prathamdigital.BaseActivity;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.util.PD_Utility;

import butterknife.BindView;
import butterknife.ButterKnife;


public class Activity_WebView extends BaseActivity {

    @BindView(R.id.loadGame)
    WebView webView;

    String startTime = "";
    private boolean backpressedFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        ButterKnife.bind(this);
        startTime = PD_Utility.getCurrentDateTime();

        String index_path = getIntent().getStringExtra("index_path");
        String path = getIntent().getStringExtra("path");
        String resId = getIntent().getStringExtra("resId");
        createWebView(index_path, path, resId);
    }

    public void createWebView(String GamePath, String parse, String resId) {
        try {
            webView.loadUrl("file:///" + GamePath);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
            webView.addJavascriptInterface(new JSInterface(Activity_WebView.this, webView,
                    "file://" + parse, resId), "Android");
            webView.setWebContentsDebuggingEnabled(true);
            webView.setWebViewClient(new WebViewClient());
            webView.setWebChromeClient(new WebChromeClient());
            webView.clearCache(true);

            webView.getSettings().setLoadsImagesAutomatically(true);
            webView.getSettings().setDomStorageEnabled(true);
            webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        if (!backpressedFlag)
//            addScoreToDB();
            Log.d("Destroyed Score Entry", "Destroyed Score Entry");
//        if (tts != null) {
//            tts.shutdown();
//            Log.d("tts_destroyed", "TTS Destroyed");
//        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        backpressedFlag = true;
        webView.post(new Runnable() {
            public void run() {
                //String jsString = "javascript:Utils.closeAllAudios()";
                //webView.loadUrl(jsString);
                //JSInterface.stopTtsBackground();
                webView.loadUrl("about:blank");
            }
        });
        super.onBackPressed();
        webView.clearCache(true);
        Runtime rs = Runtime.getRuntime();
        rs.freeMemory();
        rs.gc();
        rs.freeMemory();
        finish();
    }

    /*public static class WebViewService extends Service {
        Activity_WebView activity_webView;

        @Override
        public void onCreate() {
            super.onCreate();
            Log.v("Ketan:", "WebView Service Started");
            activity_webView = new Activity_WebView();
        }

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public void onTaskRemoved(Intent rootIntent) {
            Log.v("Ketan:", "WebView Service task removed");
//            super.onTaskRemoved(rootIntent);
            stopSelf();
        }
    }*/
}

