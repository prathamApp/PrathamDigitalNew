package com.pratham.prathamdigital.ui.fragment_share_recieve;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.pratham.prathamdigital.R;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.FileViewHolder> {
    Context context;
    List<File> files;
    ContractShare.shareView shareView;

    public FileListAdapter(Context context, List<File> files, ContractShare.shareView shareView) {
        this.context = context;
        this.files = files;
        this.shareView = shareView;
    }

    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater header = LayoutInflater.from(parent.getContext());
        View v = header.inflate(R.layout.row_file_item, parent, false);
        return new FileViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, int i) {
        File f = files.get(holder.getAdapterPosition());
        holder.file_title.setText(f.getName());
        if (f.isDirectory())
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shareView.fileItemClicked(f, holder.getAdapterPosition());
                }
            });
        else
            holder.itemView.setOnClickListener(null);
        holder.btn_send_files.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareView.sendItemClicked(f, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            File f = (File) payloads.get(0);
            holder.file_title.setText(f.getName());
            if (f.isDirectory())
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shareView.fileItemClicked(f, holder.getAdapterPosition());
                    }
                });
            else
                holder.itemView.setOnClickListener(null);
            holder.btn_send_files.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shareView.sendItemClicked(f, holder.getAdapterPosition());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public void updateList(final List<File> newList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new FileDiffUtilCallback(newList, files));
        files.clear();
        this.files.addAll(newList);
        diffResult.dispatchUpdatesTo(this);
    }

    class FileViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.file_icon)
        LottieAnimationView file_icon;
        @BindView(R.id.file_title)
        TextView file_title;
        @BindView(R.id.btn_send_files)
        Button btn_send_files;

        public FileViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
