package com.pratham.prathamdigital.ui.pdf_viewer;

import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.pdf.BookFlipPageTransformer;
import com.pratham.prathamdigital.models.EventMessage;
import com.pratham.prathamdigital.ui.content_player.Activity_ContentPlayer;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Objects;

@EFragment(R.layout.activity_pdf_viewer)
public class Fragment_PdfViewer extends Fragment implements PDFContract.pdf_View {

    private static MediaPlayer page_flip_mp;
    private static ArrayList<Bitmap> bitmaps;

    @Bean(PDF_PresenterImpl.class)
    PDFContract.pdfPresenter pdf_presenter;
    @ViewById(R.id.pdf_curl_view)
    ViewPager pdf_curl_view;
    private String startTime;
    private String resId;
    private int pageSelected = 1;

    @AfterViews
    public void initialize() {
        pdf_presenter.setView(Fragment_PdfViewer.this);
        page_flip_mp = MediaPlayer.create(getActivity(), R.raw.page_flip);
        startTime = PD_Utility.getCurrentDateTime();
        resId = Objects.requireNonNull(getArguments()).getString("resId");
        pdf_presenter.generateImageFromPdf(getArguments().getString("pdfPath"));
    }

    @UiThread
    @Override
    public void recievedBitmaps(ArrayList<Bitmap> bits) {
        bitmaps = bits;
        PDFPagerAdapter pagerAdapter = new PDFPagerAdapter(getActivity(), bitmaps);
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
                pageSelected = i + 1;
                if (pageSelected == bitmaps.size())
                    ((Activity_ContentPlayer) Objects.requireNonNull(getActivity())).showNextButton(resId);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
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
                pdf_presenter.addScoreToDB(resId, startTime, pageSelected);
                ((Activity_ContentPlayer) Objects.requireNonNull(getActivity())).closeContentPlayer();
            }
        }
    }
}
