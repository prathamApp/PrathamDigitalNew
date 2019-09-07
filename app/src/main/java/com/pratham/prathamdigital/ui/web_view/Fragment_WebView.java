package com.pratham.prathamdigital.ui.web_view;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.models.EventMessage;
import com.pratham.prathamdigital.ui.content_player.Activity_ContentPlayer;
import com.pratham.prathamdigital.util.PD_Constant;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.Objects;

@EFragment(R.layout.activity_web_view)
public class Fragment_WebView extends Fragment implements VideoListener {

    @ViewById(R.id.loadGame)
    WebView webView;
    @ViewById(R.id.videoView)
    com.pratham.prathamdigital.custom.FullScreenVideoView videoView;

    private String resId;

    @AfterViews
    public void initialize() {
        String index_path = Objects.requireNonNull(getArguments()).getString("index_path");
        String path = new File(index_path).getParent() + "/";
        resId = getArguments().getString("resId");
        boolean isOnSdCard = getArguments().getBoolean("isOnSdCard", false);
        createWebView(index_path, path, resId, isOnSdCard);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void createWebView(String GamePath, String parse, String resId, boolean isOnSdCard) {
        try {
            webView.loadUrl("file:///" + GamePath);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
            webView.addJavascriptInterface(new JSInterface(getActivity(), webView,
                    "file://" + parse, resId, isOnSdCard, this, getActivity()), "Android");
            WebView.setWebContentsDebuggingEnabled(true);
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
    public void showVideo(String videoPath) {
        webView.setVisibility(View.GONE);
        videoView.setVisibility(View.VISIBLE);
        Uri video = Uri.parse(videoPath);
        videoView.setVideoURI(video);
        videoView.setOnPreparedListener(mp -> {
            videoView.start();
        });
        videoView.setOnCompletionListener(mp -> {
            webView.setVisibility(View.VISIBLE);
            videoView.setVisibility(View.GONE);
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void messageReceived(EventMessage message) {
        if (message != null) {
            if (message.getMessage().equalsIgnoreCase(PD_Constant.CLOSE_CONTENT_PLAYER)) {
                webView.post(() -> webView.loadUrl("about:blank"));
                webView.clearCache(true);
                Runtime rs = Runtime.getRuntime();
                rs.freeMemory();
                rs.gc();
                rs.freeMemory();
                ((Activity_ContentPlayer) Objects.requireNonNull(getActivity())).closeContentPlayer();
            }
        }
    }

    @Override
    public void gameCompleted() {
        if (Objects.requireNonNull(getArguments()).getBoolean("isCourse")) {
            EventMessage message = new EventMessage();
            message.setMessage(PD_Constant.SHOW_NEXT_CONTENT);
            message.setDownloadId(resId);
            EventBus.getDefault().post(message);
        } else
            Objects.requireNonNull(getActivity()).onBackPressed();
    }
}

