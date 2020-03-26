package com.pratham.prathamdigital.ui.content_player.assignments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.models.Model_Assignment;
import com.pratham.prathamdigital.ui.content_player.ContentPlayerContract;
import com.pratham.prathamdigital.view_holders.AssignmentViewHolder;

import java.util.ArrayList;

public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentViewHolder> {

    private ArrayList<Model_Assignment> assignments;
    private Context context;
    private ContentPlayerContract.assignment_submission assignment_submission;

    AssignmentAdapter(Context context, ArrayList<Model_Assignment> assignments,
                      ContentPlayerContract.assignment_submission assignment_submission) {
        this.context = context;
        this.assignments = assignments;
        this.assignment_submission = assignment_submission;
    }

    @NonNull
    @Override
    public AssignmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater file = LayoutInflater.from(parent.getContext());
        View v = file.inflate(R.layout.item_assignments, parent, false);
        return new AssignmentViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AssignmentViewHolder childViewHolder, int i) {
        i = childViewHolder.getAdapterPosition();
        childViewHolder.setView(context, assignments.get(i), i, assignment_submission);
    }

    @Override
    public int getItemCount() {
        return assignments.size();
    }

    public ArrayList<Model_Assignment> getItems() {
        return assignments;
    }
}
