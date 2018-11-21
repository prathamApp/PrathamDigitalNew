package com.pratham.prathamdigital.ui.pdf_viewer;

import android.graphics.Bitmap;

import java.util.ArrayList;

public interface PDFContract {
    interface pdf_View {
        void recievedBitmaps(ArrayList<Bitmap> bitmaps);
    }

    interface pdfPresenter {
    }
}
