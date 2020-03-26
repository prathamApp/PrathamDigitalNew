package com.pratham.prathamdigital.ui.content_player.course_detail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.ui.content_player.ContentPlayerContract;
import com.pratham.prathamdigital.view_holders.CourseChildViewHolder;

import java.util.List;
import java.util.Objects;

public class CourseDetailAdapter extends RecyclerView.Adapter<CourseChildViewHolder> {

    //    private List<Modal_ContentDetail> childs;
    private ContentPlayerContract.courseDetailAdapterClick courseDetailAdapterClick;
    private final AsyncListDiffer<Modal_ContentDetail> mDiffer;

    CourseDetailAdapter(Context context, ContentPlayerContract.courseDetailAdapterClick courseDetailAdapterClick) {
        DiffUtil.ItemCallback<Modal_ContentDetail> diffcallback = new DiffUtil.ItemCallback<Modal_ContentDetail>() {
            @Override
            public boolean areItemsTheSame(@NonNull Modal_ContentDetail detail, @NonNull Modal_ContentDetail t1) {
                return Objects.equals(detail.getNodeid(), t1.getNodeid());
            }

            @Override
            public boolean areContentsTheSame(@NonNull Modal_ContentDetail detail, @NonNull Modal_ContentDetail t1) {
                int result = detail.compareTo(t1);
                return result == 0;
            }
        };
        mDiffer = new AsyncListDiffer<>(this, diffcallback);
        this.courseDetailAdapterClick = courseDetailAdapterClick;
    }

    @NonNull
    @Override
    public CourseChildViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater file = LayoutInflater.from(parent.getContext());
        View v = file.inflate(R.layout.item_course_detail_file, parent, false);
        return new CourseChildViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseChildViewHolder childViewHolder, int i) {
        i = childViewHolder.getAdapterPosition();
        childViewHolder.setChildItems(mDiffer.getCurrentList().get(i), courseDetailAdapterClick);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseChildViewHolder childViewHolder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(childViewHolder, position, payloads);
        } else {
            Modal_ContentDetail contentDetail = (Modal_ContentDetail) payloads.get(0);
            childViewHolder.setChildItems(contentDetail, courseDetailAdapterClick);
        }
    }

    @Override
    public int getItemCount() {
        return mDiffer.getCurrentList().size();
    }

    public void submitList(List<Modal_ContentDetail> data) {
        mDiffer.submitList(data);
    }
}
