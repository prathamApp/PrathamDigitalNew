package com.pratham.prathamdigital.ui.pdf_viewer;

import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.pratham.prathamdigital.BaseActivity;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.pdf.BookFlipPageTransformer;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.models.Modal_Score;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Activity_PdfViewer extends BaseActivity implements PDFContract.pdf_View {
    private String startTime;
    private String resId;
    private boolean backpressedFlag = false;
    PDF_PresenterImpl pdf_presenter;
    public static MediaPlayer page_flip_mp;
    public static ArrayList<Bitmap> bitmaps;
    PDFPagerAdapter pagerAdapter;

    @BindView(R.id.pdf_curl_view)
    ViewPager pdf_curl_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);
        ButterKnife.bind(this);
        pdf_presenter = new PDF_PresenterImpl(this, this);
        page_flip_mp = MediaPlayer.create(this, R.raw.page_flip);
        startTime = PD_Utility.getCurrentDateTime();
        resId = getIntent().getStringExtra("resId");
        pdf_presenter.generateImageFromPdf(getIntent().getStringExtra("pdfPath"));
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
        modalScore.setSessionID(FastSave.getInstance().getString(PD_Constant.SESSIONID, ""));
        if (PrathamApplication.isTablet)
            modalScore.setGroupID(FastSave.getInstance().getString(PD_Constant.GROUPID, "no_group"));
        else
            modalScore.setStudentID(FastSave.getInstance().getString(PD_Constant.STUDENTID, "no_student"));
        modalScore.setDeviceID(PD_Utility.getDeviceID());
        modalScore.setResourceID(resId);
        modalScore.setQuestionId(0);
        modalScore.setScoredMarks(0);
        modalScore.setTotalMarks((bitmaps.size() > 0) ? bitmaps.size() : 0);
        modalScore.setStartDateTime(startTime);
        modalScore.setEndDateTime(PD_Utility.getCurrentDateTime());
        modalScore.setLevel(0);
        modalScore.setLabel("_");
        modalScore.setSentFlag(0);
        BaseActivity.scoreDao.insert(modalScore);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        PrathamApplication.getInstance().toggleBackgroundMusic(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    //    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        if (hasFocus) {
//            getWindow().getDecorView().setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//        }
//    }

    @Override
    public void recievedBitmaps(ArrayList<Bitmap> bits) {
        this.bitmaps = bits;
        pagerAdapter = new PDFPagerAdapter(Activity_PdfViewer.this, bitmaps);
        pdf_curl_view.setAdapter(pagerAdapter);
        pdf_curl_view.setClipToPadding(false);
        BookFlipPageTransformer transformer = new BookFlipPageTransformer();
        transformer.setEnableScale(true);
        pdf_curl_view.setPageTransformer(true, transformer);
        pdf_curl_view.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                page_flip_mp.start();
            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }
}
