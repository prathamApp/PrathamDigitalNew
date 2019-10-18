package com.pratham.prathamdigital.ui.content_player.course_detail;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.ui.content_player.ContentPlayerContract;
import com.pratham.prathamdigital.util.PD_Constant;

import java.io.File;
import java.util.List;
import java.util.Objects;

public class CourseDetailAdapter extends RecyclerView.Adapter<CourseDetailAdapter.ChildViewHolder> {

    private List<Modal_ContentDetail> childs;
    private ContentPlayerContract.courseDetailAdapterClick courseDetailAdapterClick;

    CourseDetailAdapter(Context context, List<Modal_ContentDetail> childs,
                        ContentPlayerContract.courseDetailAdapterClick courseDetailAdapterClick) {
        this.childs = childs;
        this.courseDetailAdapterClick = courseDetailAdapterClick;
    }

    @NonNull
    @Override
    public ChildViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater file = LayoutInflater.from(parent.getContext());
        View v = file.inflate(R.layout.item_course_detail_file, parent, false);
        return new ChildViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildViewHolder childViewHolder, int i) {
        childViewHolder.course_child_title.setText(childs.get(i).getNodetitle());
        Uri imgUri = Uri.fromFile(new File(
                PrathamApplication.pradigiPath + "/PrathamImages/" + childs.get(i).getNodeimage()));
        childViewHolder.course_child_image.setImageURI(imgUri);
        if (childs.get(i).getResourcetype().toLowerCase().equalsIgnoreCase(PD_Constant.GAME))
            Objects.requireNonNull(childViewHolder.img_content_type).setImageResource(R.drawable.ic_joystick);
        else if (childs.get(i).getResourcetype().toLowerCase().equalsIgnoreCase(PD_Constant.VIDEO))
            Objects.requireNonNull(childViewHolder.img_content_type).setImageResource(R.drawable.ic_video);
        else if (childs.get(i).getResourcetype().toLowerCase().equalsIgnoreCase(PD_Constant.PDF))
            Objects.requireNonNull(childViewHolder.img_content_type).setImageResource(R.drawable.ic_book);
        childViewHolder.itemView.setOnClickListener(v -> courseDetailAdapterClick.onChildItemClicked(childs.get(i)));
    }

    @Override
    public int getItemCount() {
        return childs.size();
    }

    class ChildViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView course_child_image;
        TextView course_child_title;
        ImageView img_content_type;

        ChildViewHolder(@NonNull View itemView) {
            super(itemView);
            course_child_image = itemView.findViewById(R.id.course_child_image);
            course_child_title = itemView.findViewById(R.id.course_child_title);
            img_content_type = itemView.findViewById(R.id.img_content_type);
        }
    }
}
