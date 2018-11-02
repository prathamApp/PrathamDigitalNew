package com.pratham.prathamdigital.ui.pdf_viewer;

import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.barteksc.pdfviewer.PDFView;
import com.pratham.prathamdigital.BaseActivity;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.models.Modal_Score;
import com.pratham.prathamdigital.util.PD_Utility;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Activity_PdfViewer extends BaseActivity {

    @BindView(R.id.pdf_viewer)
    PDFView pdfView;
    @BindView(R.id.rl_title)
    ViewGroup rl_title;
    @BindView(R.id.pdf_title)
    TextView pdf_title;

    private String myPdf;
    private String StartTime;
    private String resId;
    private boolean backpressedFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);
        ButterKnife.bind(this);
        myPdf = getIntent().getStringExtra("pdfPath");
        pdf_title.setText(getIntent().getStringExtra("pdfTitle"));
        StartTime = PD_Utility.GetCurrentDateTime();
        resId = getIntent().getStringExtra("resId");
        pdfView.fromUri(Uri.parse(myPdf))
                .enableSwipe(true)
                .swipeHorizontal(true)
                .enableDoubletap(true)
                .defaultPage(0)
                .enableAnnotationRendering(true)
                .password(null)
                .scrollHandle(null)
                .enableAntialiasing(true)
                .load();
    }

    @Override
    public void onBackPressed() {
        backpressedFlag = true;
        addScoreToDB();
        setResult(RESULT_CANCELED);
        finishAfterTransition();
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
        modalScore.setStartDateTime(PD_Utility.GetCurrentDateTime());
        modalScore.setEndDateTime(PD_Utility.GetCurrentDateTime());
        BaseActivity.scoreDao.insert(modalScore);
    }
}
