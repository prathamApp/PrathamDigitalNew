package com.pratham.prathamdigital.ui.course_detail_page;

import android.support.v7.widget.RecyclerView;

import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.bottomsheet.SuperBottomSheetFragment;
import com.pratham.prathamdigital.models.Modal_FileDownloading;
import com.pratham.prathamdigital.ui.download_list.DownloadListAdapter;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

@EFragment(R.layout.download_list_fragment)
public class CourseDetailFragment extends SuperBottomSheetFragment{
    private static final String TAG = CourseDetailFragment.class.getSimpleName();
    @ViewById(R.id.rv_download)
    RecyclerView rv_download;

    private DownloadListAdapter adapter;

    @UiThread
    public void initializeAdapter() {

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

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEvent(final List<Modal_FileDownloading> downloadings) {

    }
}
