package com.pratham.prathamdigital.ui.download_list;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pratham.prathamdigital.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DownloadListFragment extends DialogFragment {
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
        if (adapter == null) {
            initializeAdapter();
        }
    }

    private void initializeAdapter() {
        adapter = new DownloadListAdapter();
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        rv_download.setLayoutManager(manager);
        rv_download.setAdapter(adapter);
    }
}
