package com.pratham.prathamdigital.view_holders;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.models.Model_Assignment;
import com.pratham.prathamdigital.ui.content_player.ContentPlayerContract;
import com.pratham.prathamdigital.ui.content_player.assignments.AssignmentFilesAdapter;

public class AssignmentViewHolder extends RecyclerView.ViewHolder {

    private TextView item_asmt_index;
    private TextView tv_asmt_title;
    private RecyclerView rv_asmt_files;

    public AssignmentViewHolder(@NonNull View itemView) {
        super(itemView);
        item_asmt_index = itemView.findViewById(R.id.item_asmt_index);
        tv_asmt_title = itemView.findViewById(R.id.tv_asmt_title);
        rv_asmt_files = itemView.findViewById(R.id.rv_asmt_files);
    }

    public void setView(Context context, Model_Assignment assignment, int pos,
                        ContentPlayerContract.assignment_submission assignment_submission) {
        item_asmt_index.setText(String.valueOf(pos + 1));
        tv_asmt_title.setText(assignment.getAssignment_desc());
        AssignmentFilesAdapter adapter = new AssignmentFilesAdapter(context, assignment.getAssignment_files(), assignment_submission, pos);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        rv_asmt_files.setLayoutManager(linearLayoutManager);
        rv_asmt_files.setAdapter(adapter);
    }
}
