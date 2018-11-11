package com.pratham.prathamdigital.ui.download_list;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.number_progressbar.NumberProgressBar;
import com.pratham.prathamdigital.models.Modal_FileDownloading;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DownloadListAdapter extends RecyclerView.Adapter<DownloadListAdapter.DownloadViewHolder> {
    Context context;
    List<Modal_FileDownloading> downloadings;

    public DownloadListAdapter(Context context, List<Modal_FileDownloading> downloadings) {
        this.context = context;
        this.downloadings = downloadings;
    }

    @Override
    public DownloadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater header = LayoutInflater.from(parent.getContext());
        View v = header.inflate(R.layout.download_list_item, parent, false);
        return new DownloadViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DownloadViewHolder holder, int i) {
        holder.content_title.setText(downloadings.get(holder.getAdapterPosition()).getFilename());
        holder.number_progress.setProgress(downloadings.get(holder.getAdapterPosition()).getProgress());
    }

    @Override
    public void onBindViewHolder(@NonNull DownloadViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            Modal_FileDownloading content = (Modal_FileDownloading) payloads.get(0);
            holder.content_title.setText(content.getFilename());
            holder.number_progress.setProgress(content.getProgress());
        }
    }

    @Override
    public int getItemCount() {
        return downloadings.size();
    }

    public void updateList(final List<Modal_FileDownloading> newList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DownloadDiffUtilCallback(newList, downloadings));
        downloadings.clear();
        this.downloadings.addAll(newList);
        diffResult.dispatchUpdatesTo(this);
    }

    class DownloadViewHolder extends RecyclerView.ViewHolder {

        @Nullable
        @BindView(R.id.number_progress)
        NumberProgressBar number_progress;
        @Nullable
        @BindView(R.id.download_content_title)
        TextView content_title;

        public DownloadViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}