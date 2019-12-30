package com.pratham.prathamdigital.ui.content_player.assignments;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.facebook.drawee.view.SimpleDraweeView;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.ui.content_player.ContentPlayerContract;

import java.io.File;
import java.util.List;

public class AssignmentFilesAdapter extends RecyclerView.Adapter {

    private static final int ADD_NEW = 1;
    private static final int FILE_TYPE = 2;
    private List<String> assignment_files;
    private Context context;
    private int parentPosition;
    private ContentPlayerContract.assignment_submission assignment_submission;

    public AssignmentFilesAdapter(Context context, List<String> assignment_files,
                                  ContentPlayerContract.assignment_submission assignment_submission, int parentPosition) {
        this.context = context;
        this.assignment_files = assignment_files;
        this.assignment_submission = assignment_submission;
        this.parentPosition = parentPosition;
    }

    @Override
    public int getItemViewType(int position) {
        if (assignment_files.get(position) == null)
            return ADD_NEW;
        else return FILE_TYPE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        switch (viewType) {
            case ADD_NEW:
                LayoutInflater header = LayoutInflater.from(parent.getContext());
                v = header.inflate(R.layout.item_add_assignment_files, parent, false);
                return new AddFileViewHolder(v);
            case FILE_TYPE:
                LayoutInflater file = LayoutInflater.from(parent.getContext());
                v = file.inflate(R.layout.item_assignment_files, parent, false);
                return new AssignmentFileViewHolder(v);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {
        i = holder.getAdapterPosition();
        switch (holder.getItemViewType()) {
            case ADD_NEW:
                AddFileViewHolder addFileViewHolder = (AddFileViewHolder) holder;
                addFileViewHolder.setView(holder.getAdapterPosition(), parentPosition);
                break;
            case FILE_TYPE:
                AssignmentFileViewHolder assignmentFileViewHolder = (AssignmentFileViewHolder) holder;
                assignmentFileViewHolder.setView(holder.getAdapterPosition(), assignment_files.get(i));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return assignment_files.size();
    }

    private class AddFileViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout add_assignment_file;

        AddFileViewHolder(View v) {
            super(v);
            add_assignment_file = v.findViewById(R.id.add_assignment_file);
        }

        private void setView(int selfPosition, int parentPosition) {
            add_assignment_file.setOnClickListener(v -> assignment_submission.addFileClicked(parentPosition));
        }
    }

    private class AssignmentFileViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView iv_assignment_file;
        ImageView iv_delete_file;

        AssignmentFileViewHolder(View v) {
            super(v);
            iv_assignment_file = v.findViewById((R.id.iv_assignment_file));
            iv_delete_file = v.findViewById((R.id.iv_delete_file));
        }

        private void setView(int selfPosition, String file_path) {
            iv_assignment_file.setImageURI(Uri.fromFile(new File(file_path)));
            iv_assignment_file.setOnClickListener(v -> assignment_submission.onFilePreviewClicked(file_path));
            iv_delete_file.setOnClickListener(v -> assignment_submission.deleteFile(itemView, parentPosition, selfPosition));
        }
    }
}
