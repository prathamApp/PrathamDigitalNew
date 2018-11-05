package com.pratham.prathamdigital.ui.download_list;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.models.Modal_FileDownloading;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DownloadListFragment extends Fragment {
    @BindView(R.id.rv_download)
    RecyclerView rv_download;

    DownloadListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.download_list_fragment, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initializeAdapter(List<Modal_FileDownloading> downloadings) {
        adapter = new DownloadListAdapter(getActivity(), downloadings);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rv_download.setLayoutManager(manager);
        rv_download.setAdapter(adapter);
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
    public void onEvent(final List<Modal_FileDownloading> downloadings) {
        if (adapter != null) {
            adapter.updateList(downloadings);
        } else {
            initializeAdapter(downloadings);
        }
    }
}
