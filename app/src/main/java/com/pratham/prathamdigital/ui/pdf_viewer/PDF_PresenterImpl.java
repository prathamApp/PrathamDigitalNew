package com.pratham.prathamdigital.ui.pdf_viewer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.ParcelFileDescriptor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class PDF_PresenterImpl {
    private Context context;
    private PDFContract.pdf_View pdf_view;

    public PDF_PresenterImpl(Context context, PDFContract.pdf_View pdf_view) {
        this.context = context;
        this.pdf_view = pdf_view;
    }

    public void generateImageFromPdf(String resPath) {
        ArrayList<Bitmap> pdf = new ArrayList<>();
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
                pdf.add(mBitmap);
                // close the page
                page.close();
            }
            pdf_view.recievedBitmaps(pdf);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ParcelFileDescriptor getSeekableFileDescriptor(String resPath) {
        ParcelFileDescriptor fd = null;
        try {
            fd = ParcelFileDescriptor.open(new File(resPath),
                    ParcelFileDescriptor.MODE_READ_ONLY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fd;
    }
}
