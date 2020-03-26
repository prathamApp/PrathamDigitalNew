package com.pratham.prathamdigital.view_holders;

import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.ui.content_player.ContentPlayerContract;
import com.pratham.prathamdigital.util.PD_Constant;

import java.io.File;
import java.util.Objects;

public class CourseChildViewHolder extends RecyclerView.ViewHolder {
    SimpleDraweeView course_child_image;
    TextView course_child_title;
    ImageView img_content_type;

    public CourseChildViewHolder(@NonNull View itemView) {
        super(itemView);
        course_child_image = itemView.findViewById(R.id.course_child_image);
        course_child_title = itemView.findViewById(R.id.course_child_title);
        img_content_type = itemView.findViewById(R.id.img_content_type);
    }

    public void setChildItems(Modal_ContentDetail contentDetail,
                              ContentPlayerContract.courseDetailAdapterClick courseDetailAdapterClick) {
        course_child_title.setText(contentDetail.getNodetitle());
        Uri imgUri;
        if (contentDetail.isOnSDCard())
            imgUri = Uri.fromFile(new File(
                    PrathamApplication.externalContentPath + "/PrathamImages/" + contentDetail.getNodeimage()));
        else
            imgUri = Uri.fromFile(new File(
                    PrathamApplication.pradigiPath + "/PrathamImages/" + contentDetail.getNodeimage()));
        course_child_image.setImageURI(imgUri);
        if (contentDetail.getResourcetype().toLowerCase().equalsIgnoreCase(PD_Constant.GAME))
            Objects.requireNonNull(img_content_type).setImageResource(R.drawable.ic_joystick);
        else if (contentDetail.getResourcetype().toLowerCase().equalsIgnoreCase(PD_Constant.VIDEO))
            Objects.requireNonNull(img_content_type).setImageResource(R.drawable.ic_video);
        else if (contentDetail.getResourcetype().toLowerCase().equalsIgnoreCase(PD_Constant.PDF))
            Objects.requireNonNull(img_content_type).setImageResource(R.drawable.ic_book);
        itemView.setOnClickListener(v -> courseDetailAdapterClick.onChildItemClicked(contentDetail));
    }
}
