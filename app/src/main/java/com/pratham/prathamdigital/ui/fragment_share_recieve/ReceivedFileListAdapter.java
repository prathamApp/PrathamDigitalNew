package com.pratham.prathamdigital.ui.fragment_share_recieve;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.AsyncListDiffer;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.models.Modal_ReceivingFilesThroughFTP;
import com.pratham.prathamdigital.util.PD_Constant;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReceivedFileListAdapter extends RecyclerView.Adapter<ReceivedFileListAdapter.FileViewHolder> {
    Context context;
    private AsyncListDiffer<Modal_ReceivingFilesThroughFTP> mDiffer;
    private DiffUtil.ItemCallback<Modal_ReceivingFilesThroughFTP> diffcallback = new DiffUtil.ItemCallback<Modal_ReceivingFilesThroughFTP>() {
        @Override
        public boolean areItemsTheSame(@NonNull Modal_ReceivingFilesThroughFTP detail, @NonNull Modal_ReceivingFilesThroughFTP t1) {
            return Objects.equals(detail.getGameName(), t1.getGameName());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Modal_ReceivingFilesThroughFTP detail, @NonNull Modal_ReceivingFilesThroughFTP t1) {
            int result = detail.compareTo(t1);
            if (result == 0) return true;
            return false;
        }
    };

    public void submitList(List<Modal_ReceivingFilesThroughFTP> data) {
        mDiffer.submitList(data);
    }

    public ReceivedFileListAdapter(Context context) {
        mDiffer = new AsyncListDiffer<Modal_ReceivingFilesThroughFTP>(this, diffcallback);
        this.context = context;
    }

    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater header = LayoutInflater.from(parent.getContext());
        View v = header.inflate(R.layout.receiving_row_file_item, parent, false);
        return new FileViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, int i) {
        Modal_ReceivingFilesThroughFTP files = mDiffer.getCurrentList().get(holder.getAdapterPosition());
        holder.setFiles(files);
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            Modal_ReceivingFilesThroughFTP file = (Modal_ReceivingFilesThroughFTP) payloads.get(0);
            holder.updateFileItem(file);
        }
    }

    @Override
    public int getItemCount() {
        return mDiffer.getCurrentList().size();
    }

    class FileViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.receive_content_parts)
        TextView receive_content_parts;
        @BindView(R.id.receive_content_title)
        TextView receive_content_title;
        @BindView(R.id.receive_lottie_view)
        LottieAnimationView receive_lottie_view;
        Modal_ReceivingFilesThroughFTP files;

        public FileViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setFiles(Modal_ReceivingFilesThroughFTP files) {
            this.files = files;
            switch (files.getGameType()) {
                case PD_Constant.GAME:
                    receive_lottie_view.setAnimation("gaming_pad.json");
                    break;
                case PD_Constant.VIDEO:
                    receive_lottie_view.setAnimation("play_button.json");
                    break;
                case PD_Constant.PDF:
                    receive_lottie_view.setAnimation("book.json");
                    break;
                case PD_Constant.APK:
                    receive_lottie_view.setAnimation("gaming_pad.json");
                    break;
            }
            receive_content_title.setText(files.getGameName());
            receive_content_parts.setText(files.getGamePart());
        }

        public void updateFileItem(Modal_ReceivingFilesThroughFTP files) {
            receive_content_parts.setText(files.getGamePart());
        }
    }
}
