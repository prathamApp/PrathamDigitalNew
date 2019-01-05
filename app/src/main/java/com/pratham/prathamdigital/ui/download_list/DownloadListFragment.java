package com.pratham.prathamdigital.ui.download_list;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.bottomsheet.SuperBottomSheetFragment;
import com.pratham.prathamdigital.custom.wrappedLayoutManagers.WrapContentLinearLayoutManager;
import com.pratham.prathamdigital.models.Modal_FileDownloading;
import com.stolets.rxdiffutil.RxDiffUtil;
import com.stolets.rxdiffutil.diffrequest.DiffRequestManager;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

@EFragment(R.layout.download_list_fragment)
public class DownloadListFragment extends SuperBottomSheetFragment {
    private static final String TAG = DownloadListFragment.class.getSimpleName();
    @ViewById(R.id.rv_download)
    RecyclerView rv_download;

    DownloadListAdapter adapter;
    private DiffRequestManager<Modal_FileDownloading, DownloadListAdapter> mDiffRequestManager;

    @UiThread
    public void initializeAdapter(List<Modal_FileDownloading> downloadings) {
        adapter = new DownloadListAdapter(getActivity(), downloadings);
        LinearLayoutManager manager = new WrapContentLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rv_download.setHasFixedSize(true);
        rv_download.setLayoutManager(manager);
        rv_download.setAdapter(adapter);
        mDiffRequestManager = RxDiffUtil
                .bindTo(getActivity())
                .with(adapter);
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
        if (downloadings != null) {
            if (adapter != null) {
                mDiffRequestManager
                        .newDiffRequestWith(new DownloadDiffUtilCallback(adapter.getModelList(), downloadings))
                        .updateAdapterWithNewData(downloadings)
                        .detectMoves(true)
                        .calculate();
            } else {
                initializeAdapter(downloadings);
            }
        }
    }


}
