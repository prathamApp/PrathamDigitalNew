package com.pratham.prathamdigital.ui.fragment_share_recieve;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.AsyncListDiffer;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.progress_layout.ProgressLayout;
import com.pratham.prathamdigital.models.File_Model;
import com.pratham.prathamdigital.util.PD_Constant;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.FileViewHolder> {
    Context context;
    ContractShare.shareView shareView;
    private AsyncListDiffer<File_Model> mDiffer;
    private DiffUtil.ItemCallback<File_Model> diffcallback = new DiffUtil.ItemCallback<File_Model>() {
        @Override
        public boolean areItemsTheSame(@NonNull File_Model detail, @NonNull File_Model t1) {
            return Objects.equals(detail.getDetail().getNodeid(), t1.getDetail().getNodeid());
        }

        @Override
        public boolean areContentsTheSame(@NonNull File_Model detail, @NonNull File_Model t1) {
            int result = detail.compareTo(t1);
            if (result == 0) return true;
            return false;
        }
    };

    public void submitList(List<File_Model> data) {
        mDiffer.submitList(data);
    }

    public FileListAdapter(Context context, ContractShare.shareView shareView) {
        mDiffer = new AsyncListDiffer<File_Model>(this, diffcallback);
        this.context = context;
        this.shareView = shareView;
    }

    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater header = LayoutInflater.from(parent.getContext());
        View v = header.inflate(R.layout.share_file_item, parent, false);
        return new FileViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, int i) {
        File_Model file_model = mDiffer.getCurrentList().get(holder.getAdapterPosition());
        holder.setFile_model(file_model, holder.getAdapterPosition());
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            File_Model file_model = (File_Model) payloads.get(0);
            holder.updateFile_model(file_model, holder.getAdapterPosition());
        }
    }

    @Override
    public int getItemCount() {
        return mDiffer.getCurrentList().size();
    }

//    public void updateList(final ArrayList<File_Model> newList) {
//        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new FileDiffUtilCallback(newList, files));
//        files.clear();
//        this.files.addAll(newList);
//        diffResult.dispatchUpdatesTo(this);
//    }

    class FileViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.share_content_image)
        ImageView share_content_image;
        @BindView(R.id.share_title)
        TextView share_title;
        @BindView(R.id.progressLayout)
        ProgressLayout progressLayout;

        File_Model file_model;

        public FileViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setFile_model(File_Model file_model, int pos) {
            this.file_model = file_model;
            if (file_model.getDetail().isOnSDCard())
                Picasso.get().load(new File(PrathamApplication.contentSDPath + "/PrathamImages/" + file_model.getDetail().getNodeimage()))
//                    .resize(130, 130)
                        .placeholder(R.drawable.ic_app_logo_)
                        .into(share_content_image);
            else
                Picasso.get().load(new File(PrathamApplication.pradigiPath + "/PrathamImages/" + file_model.getDetail().getNodeimage()))
//                    .resize(130, 130)
                        .placeholder(R.drawable.ic_app_logo_)
                        .into(share_content_image);
            if (file_model.getDetail().getContentType().equalsIgnoreCase(PD_Constant.FOLDER)) {
                progressLayout.setVisibility(View.GONE);
                progressLayout.setCurProgress(0);
                share_title.setText(file_model.getDetail().getNodetitle());
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shareView.fileItemClicked(file_model.getDetail(), pos);
                    }
                });
            } else {
                progressLayout.setVisibility(View.VISIBLE);
                progressLayout.setCurProgress(file_model.getProgress());
                share_title.setText("Share");
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shareView.sendItemChecked(file_model, pos);
                    }
                });
            }
        }

        public void updateFile_model(File_Model file_model, int pos) {
            this.file_model = file_model;
            if (file_model.getDetail().isOnSDCard())
                Picasso.get().load(new File(PrathamApplication.contentSDPath + "/PrathamImages/" + file_model.getDetail().getNodeimage()))
//                        .resize(130, 130)
                        .placeholder(R.drawable.ic_app_logo_)
                        .into(share_content_image);
            else
                Picasso.get().load(new File(PrathamApplication.pradigiPath + "/PrathamImages/" + file_model.getDetail().getNodeimage()))
//                        .resize(130, 130)
                        .placeholder(R.drawable.ic_app_logo_)
                        .into(share_content_image);
            if (file_model.getDetail().getContentType().equalsIgnoreCase(PD_Constant.FOLDER)) {
                progressLayout.setVisibility(View.GONE);
                share_title.setText(file_model.getDetail().getNodetitle());
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shareView.fileItemClicked(file_model.getDetail(), pos);
                    }
                });
            } else {
                progressLayout.setVisibility(View.VISIBLE);
                progressLayout.setCurProgress(file_model.getProgress());
                if (progressLayout.getProgress() > 98)
                    share_title.setText("Sent");
                else if (progressLayout.getProgress() > 2)
                    share_title.setText("Sending...");
                else
                    share_title.setText("Send");
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shareView.sendItemChecked(file_model, getAdapterPosition());
                    }
                });
            }
        }
    }
}
