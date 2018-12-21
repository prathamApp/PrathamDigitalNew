package com.pratham.prathamdigital.ui.fragment_share_recieve;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.progress_layout.ProgressLayout;
import com.pratham.prathamdigital.models.File_Model;
import com.pratham.prathamdigital.util.PD_Constant;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.FileViewHolder> {
    Context context;
    ArrayList<File_Model> files;
    ContractShare.shareView shareView;

    public FileListAdapter(Context context, ArrayList<File_Model> files, ContractShare.shareView shareView) {
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
        int pos = holder.getAdapterPosition();
        holder.file_title.setText(files.get(pos).getDetail().getNodetitle());
        holder.progressLayout.setCurProgress(files.get(pos).getProgress());
        if (files.get(pos).getDetail().isOnSDCard())
            Picasso.get().load(new File(PrathamApplication.contentSDPath + "/PrathamImages/" + files.get(pos).getDetail().getNodeimage()))
                    .resize(50, 50)
                    .placeholder(R.drawable.ic_app_logo_)
                    .into(holder.file_icon);
        else
            Picasso.get().load(new File(PrathamApplication.pradigiPath + "/PrathamImages/" + files.get(pos).getDetail().getNodeimage()))
                    .resize(50, 50)
                    .placeholder(R.drawable.ic_app_logo_)
                    .into(holder.file_icon);
        if (files.get(pos).getDetail().getContentType().equalsIgnoreCase(PD_Constant.FOLDER)) {
            holder.btn_send.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shareView.fileItemClicked(files.get(pos).getDetail(), pos);
                }
            });
        } else {
            holder.btn_send.setVisibility(View.VISIBLE);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shareView.sendItemChecked(files.get(pos), pos);
                }
            });
        }
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            File_Model contentDetail = (File_Model) payloads.get(0);
            holder.file_title.setText(contentDetail.getDetail().getNodetitle());
            holder.progressLayout.setCurProgress(contentDetail.getProgress());
            if (contentDetail.getDetail().isOnSDCard())
                Picasso.get().load(new File(PrathamApplication.contentSDPath + "/PrathamImages/" + contentDetail.getDetail().getNodeimage()))
                        .resize(50, 50)
                        .placeholder(R.drawable.ic_app_logo_)
                        .into(holder.file_icon);
            else
                Picasso.get().load(new File(PrathamApplication.pradigiPath + "/PrathamImages/" + contentDetail.getDetail().getNodeimage()))
                        .resize(50, 50)
                        .placeholder(R.drawable.ic_app_logo_)
                        .into(holder.file_icon);
            if (contentDetail.getDetail().getContentType().equalsIgnoreCase(PD_Constant.FOLDER)) {
                holder.btn_send.setVisibility(View.GONE);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shareView.fileItemClicked(contentDetail.getDetail(), holder.getAdapterPosition());
                    }
                });
            } else {
                holder.btn_send.setVisibility(View.VISIBLE);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shareView.sendItemChecked(contentDetail, holder.getAdapterPosition());
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public void updateList(final ArrayList<File_Model> newList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new FileDiffUtilCallback(newList, files));
        files.clear();
        this.files.addAll(newList);
        diffResult.dispatchUpdatesTo(this);
    }

    class FileViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.file_icon)
        ImageView file_icon;
        @BindView(R.id.file_title)
        TextView file_title;
        @BindView(R.id.btn_send)
        Button btn_send;
        @BindView(R.id.progressLayout)
        ProgressLayout progressLayout;

        public FileViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
