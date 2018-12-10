package com.pratham.prathamdigital.ui.pdf_viewer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.pratham.prathamdigital.BaseActivity;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.models.Modal_Score;
import com.pratham.prathamdigital.services.BackgroundSoundService;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import java.util.ArrayList;

public class Activity_PdfViewer extends BaseActivity implements GestureDetector.OnGestureListener, PDFContract.pdf_View {
    private String StartTime;
    private String resId;
    private boolean backpressedFlag = false;
    PageFlipView flipView;
    GestureDetector detector;
    PDF_PresenterImpl pdf_presenter;
    public static MediaPlayer page_flip_mp;
    public static ArrayList<Bitmap> bitmaps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pdf_presenter = new PDF_PresenterImpl(this, this);
        detector = new GestureDetector(this);
        page_flip_mp = MediaPlayer.create(this, R.raw.page_flip);
        StartTime = PD_Utility.getCurrentDateTime();
        resId = getIntent().getStringExtra("resId");
        pdf_presenter.generateImageFromPdf(getIntent().getStringExtra("pdfPath"));
        FastSave.getInstance().saveInt(PD_Constant.PDF_DURATION, 1000);
        FastSave.getInstance().saveInt(PD_Constant.PDF_MESH_PIXELS, 2);
        FastSave.getInstance().saveBoolean(PD_Constant.PDF_PAGE_MODE, false);
        flipView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }


    @Override
    public void onBackPressed() {
        backpressedFlag = true;
        addScoreToDB();
        setResult(RESULT_CANCELED);
        finish();
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
        if (PD_Utility.isServiceRunning(BackgroundSoundService.class, this))
            stopService(new Intent(this, BackgroundSoundService.class));
        LoadBitmapTask.get(this).start();
        flipView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        flipView.onPause();
        LoadBitmapTask.get(this).stop();
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
        if (event.getAction() == MotionEvent.ACTION_UP) {
            flipView.onFingerUp(event.getX(), event.getY());
            return true;
        }
        return detector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        flipView.onFingerDown(e.getX(), e.getY());
        page_flip_mp.start();
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        flipView.onFingerMove(e2.getX(), e2.getY());
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public void recievedBitmaps(ArrayList<Bitmap> bitmaps) {
        this.bitmaps = bitmaps;
        flipView = new PageFlipView(this);
        setContentView(flipView);
    }
}
