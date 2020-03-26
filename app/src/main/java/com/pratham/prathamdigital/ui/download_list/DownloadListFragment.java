package com.pratham.prathamdigital.ui.download_list;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.bottomsheet.SuperBottomSheetFragment;
import com.pratham.prathamdigital.custom.wrappedLayoutManagers.WrapContentLinearLayoutManager;
import com.pratham.prathamdigital.models.EventMessage;
import com.pratham.prathamdigital.models.Modal_FileDownloading;
import com.pratham.prathamdigital.util.PD_Constant;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

@EFragment(R.layout.download_list_fragment)
public class DownloadListFragment extends SuperBottomSheetFragment implements DowloadContract {
    private static final String TAG = DownloadListFragment.class.getSimpleName();
    @ViewById(R.id.rv_download)
    RecyclerView rv_download;

    private DownloadListAdapter adapter;

    @UiThread
    public void initializeAdapter(List<Modal_FileDownloading> downloadings) {
        adapter = new DownloadListAdapter(getActivity(), this);
        LinearLayoutManager manager = new WrapContentLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rv_download.setHasFixedSize(true);
        rv_download.setLayoutManager(manager);
        rv_download.setAdapter(adapter);
        adapter.submitList(downloadings);
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
                adapter.submitList(downloadings);
            } else {
                initializeAdapter(downloadings);
            }
        }
    }

    @Override
    public void deleteDownload(int pos, String downloadId) {
        EventMessage message = new EventMessage();
        message.setMessage(PD_Constant.CANCEL_DOWNLOAD);
        message.setDownloadId(downloadId);
        EventBus.getDefault().post(message);
//        AndroidNetworking.cancel(downloadId);
    }
}
