package com.pratham.prathamdigital.ui.pdf_viewer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.models.Modal_Score;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static com.pratham.prathamdigital.PrathamApplication.scoreDao;

@EBean
public class PDF_PresenterImpl implements PDFContract.pdfPresenter {
    private final Context context;
    private PDFContract.pdf_View pdf_view;
    private ArrayList<Bitmap> bitmaps;

    public PDF_PresenterImpl(Context context) {
        this.context = context;
    }

    @Override
    public void setView(Fragment_PdfViewer activity_pdfViewer) {
        pdf_view = activity_pdfViewer;
    }

    @Background
    @Override
    public void generateImageFromPdf(String resPath) {
        if (bitmaps == null)
            bitmaps = new ArrayList<>();
        bitmaps.clear();
        try {
            PdfRenderer renderer = new PdfRenderer(getSeekableFileDescriptor(resPath));
            // let us just render all pages
            final int pageCount = renderer.getPageCount();
            for (int i = 0; i < pageCount; i++) {
                PdfRenderer.Page page = renderer.openPage(i);
                // say we render for showing on the screen
                Bitmap mBitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
                page.render(mBitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                // do stuff with the bitmap
                bitmaps.add(mBitmap);
                // close the page
                page.close();
            }
            renderer.close();
            pdf_view.recievedBitmaps(bitmaps);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ParcelFileDescriptor getSeekableFileDescriptor(String resPath) {
        ParcelFileDescriptor fd = null;
        try {
            fd = context.getContentResolver().openFileDescriptor(Uri.fromFile(new File(resPath)), "r");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fd;
    }

    @Background
    @Override
    public void addScoreToDB(String resId, String startTime, int pageSelected) {
        Modal_Score modalScore = new Modal_Score();
        modalScore.setSessionID(FastSave.getInstance().getString(PD_Constant.SESSIONID, ""));
        if (PrathamApplication.isTablet) {
            modalScore.setGroupID(FastSave.getInstance().getString(PD_Constant.GROUPID, "no_group"));
            modalScore.setStudentID("");
        } else {
            modalScore.setGroupID("");
            modalScore.setStudentID(FastSave.getInstance().getString(PD_Constant.GROUPID, "no_student"));
        }
        modalScore.setDeviceID(PD_Utility.getDeviceID());
        modalScore.setResourceID(resId);
        modalScore.setQuestionId(0);
        modalScore.setScoredMarks(pageSelected);
        modalScore.setTotalMarks((bitmaps.size() > 0) ? bitmaps.size() : 0);
        modalScore.setStartDateTime(startTime);
        modalScore.setEndDateTime(PD_Utility.getCurrentDateTime());
        modalScore.setLevel(0);
        modalScore.setLabel("_");
        modalScore.setSentFlag(0);
        scoreDao.insert(modalScore);
    }
}
