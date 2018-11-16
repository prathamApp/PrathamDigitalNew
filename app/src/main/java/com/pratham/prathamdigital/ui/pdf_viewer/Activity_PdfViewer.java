package com.pratham.prathamdigital.ui.pdf_viewer;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.pdf.PdfRenderer;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

import com.pratham.prathamdigital.BaseActivity;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.pdf.PageCurlAdapter;
import com.pratham.prathamdigital.custom.pdf.PageSurfaceView;
import com.pratham.prathamdigital.models.Modal_Score;
import com.pratham.prathamdigital.util.PD_Utility;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Activity_PdfViewer extends BaseActivity {

//    @BindView(R.id.pdf_viewer)
//    PDFView pdfView;
//    @BindView(R.id.rl_title)
//    ViewGroup rl_title;
//    @BindView(R.id.pdf_title)
//    TextView pdf_title;

    private String myPdf;
    private String StartTime;
    private String resId;
    private boolean backpressedFlag = false;
    private ArrayList<Bitmap> pdf;
    PageSurfaceView pageSurfaceView;
    public static MediaPlayer page_flip_mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ButterKnife.bind(this);
        pageSurfaceView = new PageSurfaceView(this);
        setContentView(pageSurfaceView);
        page_flip_mp = MediaPlayer.create(this, R.raw.page_flip);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screen_width = size.x;
        int screen_height = size.y;
        myPdf = getIntent().getStringExtra("pdfPath");
//        pdf_title.setText(getIntent().getStringExtra("pdfTitle"));
        StartTime = PD_Utility.getCurrentDateTime();
        resId = getIntent().getStringExtra("resId");
        generateImageFromPdf();
        PageCurlAdapter pageCurlAdapter = new PageCurlAdapter(pdf);
        pageSurfaceView.setPageCurlAdapter(pageCurlAdapter);
//        pdfView.fromUri(Uri.parse(myPdf))
//                .enableSwipe(true)
//                .swipeHorizontal(true)
//                .enableDoubletap(true)
//                .defaultPage(0)
//                .enableAnnotationRendering(true)
//                .password(null)
//                .scrollHandle(null)
//                .enableAntialiasing(true)
//                .load();
    }

    private void generateImageFromPdf() {
        // create a new renderer
        pdf = new ArrayList<>();
        try {
            PdfRenderer renderer = new PdfRenderer(getSeekableFileDescriptor());
            // let us just render all pages
            final int pageCount = renderer.getPageCount();
            for (int i = 0; i < pageCount; i++) {
                PdfRenderer.Page page = renderer.openPage(i);
                // say we render for showing on the screen
                Bitmap mBitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
                page.render(mBitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                // do stuff with the bitmap
                pdf.add(mBitmap);
                // close the page
                page.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ParcelFileDescriptor getSeekableFileDescriptor() {
        ParcelFileDescriptor fd = null;
        try {
            fd = ParcelFileDescriptor.open(new File(myPdf),
                    ParcelFileDescriptor.MODE_READ_ONLY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fd;
    }

    @Override
    public void onBackPressed() {
        backpressedFlag = true;
        addScoreToDB();
        setResult(RESULT_CANCELED);
    }

    @Override
    protected void onDestroy() {
        if (!backpressedFlag) {
            addScoreToDB();
        }
        Log.d("pdf_activity", "Destroyed");
        super.onDestroy();
    }

    public void addScoreToDB() {
        Modal_Score modalScore = new Modal_Score();
        modalScore.setSessionID("");
        modalScore.setStudentID("");
        modalScore.setResourceID("");
        modalScore.setQuestionId(0);
        modalScore.setScoredMarks(0);
        modalScore.setTotalMarks(0);
        // Unique Device ID
        String deviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        modalScore.setDeviceID(deviceId.equals(null) ? "0000" : deviceId);
        modalScore.setStartDateTime(PD_Utility.getCurrentDateTime());
        modalScore.setEndDateTime(PD_Utility.getCurrentDateTime());
        BaseActivity.scoreDao.insert(modalScore);
    }

    @Override
    protected void onResume() {
        super.onResume();
        pageSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        pageSurfaceView.onPause();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return pageSurfaceView.onPageTouchEvent(event);
    }
}
