package com.pratham.prathamdigital.ui.fragment_child_attendance;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.elastic_view.ElasticView;
import com.pratham.prathamdigital.models.Modal_Student;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChildAdapter extends RecyclerView.Adapter {
    private static final int ADD_CHILD = 1;
    private static final int CHILD = 2;
    private ArrayList<Modal_Student> datalist;
    private ArrayList<String> child_avatar;
    private Context context;
    private ContractChildAttendance.attendanceView attendanceView;

    public ChildAdapter(Context context, ArrayList<Modal_Student> datalist,
                        ArrayList<String> child_avatar, ContractChildAttendance.attendanceView attendanceView) {
        this.context = context;
        this.datalist = datalist;
        this.child_avatar = child_avatar;
        this.attendanceView = attendanceView;
    }

    @Override
    public int getItemViewType(int position) {
        if (datalist.get(position).getStudentId().equalsIgnoreCase("Add Child"))
            return ADD_CHILD;
        else
            return CHILD;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = null;
        switch (viewType) {
            case ADD_CHILD:
                LayoutInflater header = LayoutInflater.from(parent.getContext());
                v = header.inflate(R.layout.item_add_child, parent, false);
                return new AddChildHolder(v);
            case CHILD:
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                v = inflater.inflate(R.layout.item_child_attendance, parent, false);
                return new ChildHolder(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        position = viewHolder.getAdapterPosition();
        switch (viewHolder.getItemViewType()) {
            case ADD_CHILD:
                AddChildHolder addChildHolder = (AddChildHolder) viewHolder;
                addChildHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        attendanceView.addChild(addChildHolder.itemView);
                    }
                });
                break;
            case CHILD:
                ChildHolder childHolder = (ChildHolder) viewHolder;
                childHolder.child_name.setText(datalist.get(position).getFullName());
                childHolder.child_avatar.setAnimation(child_avatar.get(position));
                if (datalist.get(position).isChecked()) {
                    childHolder.child_name.setTextColor(context.getResources().getColor(R.color.green));
                } else {
                    childHolder.child_name.setTextColor(context.getResources().getColor(R.color.white));
                }
                int finalPos = position;
                childHolder.child_view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (PrathamApplication.isTablet)
                            attendanceView.childItemClicked(datalist.get(finalPos), finalPos);
                        else
                            attendanceView.moveToDashboardOnChildClick(datalist.get(finalPos), finalPos, childHolder.itemView);
                    }
                });
                break;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            switch (holder.getItemViewType()) {
                case ADD_CHILD:
                    AddChildHolder addChildHolder = (AddChildHolder) holder;
                    addChildHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            attendanceView.addChild(addChildHolder.itemView);
                        }
                    });
                    break;
                case CHILD:
                    ChildHolder childHolder = (ChildHolder) holder;
                    Modal_Student student = (Modal_Student) payloads.get(0);
                    childHolder.child_name.setText(student.getFullName());
                    childHolder.child_avatar.setAnimation(child_avatar.get(holder.getAdapterPosition()));
                    if (student.isChecked()) {
                        childHolder.child_name.setTextColor(context.getResources().getColor(R.color.green));
                    } else {
                        childHolder.child_name.setTextColor(context.getResources().getColor(R.color.white));
                    }
                    childHolder.child_view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (PrathamApplication.isTablet)
                                attendanceView.childItemClicked(student, holder.getAdapterPosition());
                            else
                                attendanceView.moveToDashboardOnChildClick(student, holder.getAdapterPosition(), childHolder.itemView);
                        }
                    });
            }
        }
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    public class ChildHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.child_view)
        ElasticView child_view;
        @BindView(R.id.child_name)
        TextView child_name;
        @BindView(R.id.child_avatar)
        LottieAnimationView child_avatar;

        public ChildHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class AddChildHolder extends RecyclerView.ViewHolder {
        public AddChildHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
