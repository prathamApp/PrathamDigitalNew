package com.pratham.prathamdigital.ui.download_list;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.number_progressbar.NumberProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DownloadListAdapter extends RecyclerView.Adapter {

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater header = LayoutInflater.from(parent.getContext());
        View v = header.inflate(R.layout.item_content_header, parent, false);
        return new DownloadViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class DownloadViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.number_progress)
        NumberProgressBar number_progress;
        @BindView(R.id.content_title)
        TextView content_title;

        public DownloadViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(itemView);
        }
    }
}
