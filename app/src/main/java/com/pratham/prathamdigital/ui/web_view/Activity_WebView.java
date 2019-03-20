package com.pratham.prathamdigital.ui.web_view;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.VideoView;

import com.pratham.prathamdigital.BaseActivity;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.util.PD_Utility;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;


public class Activity_WebView extends BaseActivity implements VideoListener {

    @BindView(R.id.loadGame)
    WebView webView;
    @BindView(R.id.videoView)
    com.pratham.prathamdigital.custom.FullScreenVideoView videoView;

    String startTime = "";
    private boolean backpressedFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        ButterKnife.bind(this);
        startTime = PD_Utility.getCurrentDateTime();
        String index_path = getIntent().getStringExtra("index_path");
        String path = new File(index_path).getParent() + "/";
        String resId = getIntent().getStringExtra("resId");
        boolean isOnSdCard = getIntent().getBooleanExtra("isOnSdCard", false);
        createWebView(index_path, path, resId, isOnSdCard);
    }

    public void createWebView(String GamePath, String parse, String resId, boolean isOnSdCard) {
        try {
            webView.loadUrl("file:///" + GamePath);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
            webView.addJavascriptInterface(new JSInterface(Activity_WebView.this, webView,
                    "file://" + parse, resId, isOnSdCard, this, this), "Android");
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
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        webView.post(new Runnable() {
            public void run() {
                webView.loadUrl("about:blank");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        backpressedFlag = true;
        webView.post(new Runnable() {
            public void run() {
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

    @Override
    public void showVideo(String videoPath) {
        webView.setVisibility(View.GONE);
        videoView.setVisibility(View.VISIBLE);
        Uri video = Uri.parse(videoPath);
        videoView.setVideoURI(video);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
//                mp.setLooping(true);
                videoView.start();
            }
        });
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                webView.setVisibility(View.VISIBLE);
                videoView.setVisibility(View.GONE);
            }
        });

    }
}

