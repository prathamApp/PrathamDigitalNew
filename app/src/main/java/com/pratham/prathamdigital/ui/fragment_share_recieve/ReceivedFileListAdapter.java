package com.pratham.prathamdigital.ui.fragment_share_recieve;

import android.content.Context;
import android.support.annotation.NonNull;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReceivedFileListAdapter extends RecyclerView.Adapter<ReceivedFileListAdapter.FileViewHolder> {
    Context context;
    ArrayList<Modal_ReceivingFilesThroughFTP> files;

    public ReceivedFileListAdapter(Context context, ArrayList<Modal_ReceivingFilesThroughFTP> files) {
        this.context = context;
        this.files = files;
    }

    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater header = LayoutInflater.from(parent.getContext());
        View v = header.inflate(R.layout.receiving_row_file_item, parent, false);
        return new FileViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, int i) {
        int pos = holder.getAdapterPosition();
        switch (files.get(pos).getGameType()) {
            case PD_Constant.GAME:
                holder.receive_lottie_view.setAnimation("gaming_pad.json");
                break;
            case PD_Constant.VIDEO:
                holder.receive_lottie_view.setAnimation("play_button.json");
                break;
            case PD_Constant.PDF:
                holder.receive_lottie_view.setAnimation("book.json");
                break;
            case PD_Constant.APK:
                holder.receive_lottie_view.setAnimation("gaming_pad.json");
                break;
        }
        holder.receive_content_title.setText(files.get(pos).getGameName());
        holder.receive_content_parts.setText(files.get(pos).getGamePart());
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            Modal_ReceivingFilesThroughFTP file = (Modal_ReceivingFilesThroughFTP) payloads.get(0);
            holder.receive_content_parts.setText(file.getGamePart());
        }
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public void updateList(final ArrayList<Modal_ReceivingFilesThroughFTP> newList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new ReceivedFileDiffUtilCallback(newList, files));
        files.clear();
        this.files.addAll(newList);
        diffResult.dispatchUpdatesTo(this);
    }

    class FileViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.receive_content_parts)
        TextView receive_content_parts;
        @BindView(R.id.receive_content_title)
        TextView receive_content_title;
        @BindView(R.id.receive_lottie_view)
        LottieAnimationView receive_lottie_view;

        public FileViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
