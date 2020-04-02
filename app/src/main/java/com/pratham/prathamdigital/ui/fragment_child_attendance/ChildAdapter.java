package com.pratham.prathamdigital.ui.fragment_child_attendance;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.android.material.card.MaterialCardView;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.imageviews.FaceCenterCrop;
import com.pratham.prathamdigital.models.Modal_Student;
import com.pratham.prathamdigital.util.PD_Utility;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ChildAdapter extends RecyclerView.Adapter {
    private static final int ADD_CHILD = 1;
    private static final int TAB_CHILD = 2;
    private static final int SMART_CHILD = 3;
    private final ArrayList<Modal_Student> datalist;
    private final Context context;
    private final ContractChildAttendance.attendanceView attendanceView;

    public ChildAdapter(Context context, ArrayList<Modal_Student> datalist,
                        ContractChildAttendance.attendanceView attendanceView) {
        this.context = context;
        this.datalist = datalist;
        this.attendanceView = attendanceView;
    }

    @Override
    public int getItemViewType(int position) {
        if (datalist.get(position).getStudentId().equalsIgnoreCase("Add Child"))
            return ADD_CHILD;
        else if (PrathamApplication.isTablet)
            return TAB_CHILD;
        else
            return SMART_CHILD;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        switch (viewType) {
            case ADD_CHILD:
                LayoutInflater header = LayoutInflater.from(parent.getContext());
                v = header.inflate(R.layout.item_add_child, parent, false);
                return new AddChildHolder(v);
            case TAB_CHILD:
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                v = inflater.inflate(R.layout.item_child_attendance, parent, false);
                return new ChildHolder(v);
            case SMART_CHILD:
                LayoutInflater inflater2 = LayoutInflater.from(parent.getContext());
                v = inflater2.inflate(R.layout.item_smart_child_attendance, parent, false);
                return new SmartChildHolder(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        position = viewHolder.getAdapterPosition();
        switch (viewHolder.getItemViewType()) {
            case ADD_CHILD:
                AddChildHolder addChildHolder = (AddChildHolder) viewHolder;
                addChildHolder.itemView.setOnClickListener(v -> attendanceView.addChild(addChildHolder.itemView));
                break;
            case TAB_CHILD:
                ChildHolder childHolder = (ChildHolder) viewHolder;
                childHolder.setView(datalist.get(position), position);
                break;
            case SMART_CHILD:
                SmartChildHolder smartChildHolder = (SmartChildHolder) viewHolder;
                smartChildHolder.smart_child_name.setText(datalist.get(position).getFullName());
                smartChildHolder.smart_child_avatar.setAnimation(datalist.get(position).getAvatarName());
                int finalPos2 = position;
                smartChildHolder.smart_child_avatar.setOnClickListener(v ->
                        attendanceView.moveToDashboardOnChildClick(datalist.get(finalPos2), finalPos2, smartChildHolder.itemView));
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
                    addChildHolder.itemView.setOnClickListener(v -> attendanceView.addChild(addChildHolder.itemView));
                    break;
                case TAB_CHILD:
                    ChildHolder childHolder = (ChildHolder) holder;
                    Modal_Student student = (Modal_Student) payloads.get(0);
                    childHolder.setView(student, position);
            }
        }
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    public void updateItem(String stud_id, Uri capturedImageUri) {
        int pos = 0;
        for (int i = 0; i < datalist.size(); i++) {
            pos = i;
            if (datalist.get(pos).getStudentId().equalsIgnoreCase(stud_id)) {
                datalist.get(pos).setAvatarName(capturedImageUri.getPath());
                break;
            }
        }
        notifyItemChanged(pos);
    }

    class ChildHolder extends RecyclerView.ViewHolder {
        MaterialCardView child_card_name;
        TextView child_name;
        SimpleDraweeView child_avatar;
        RelativeLayout btn_click_photo;

        ChildHolder(@NonNull View itemView) {
            super(itemView);
            child_card_name = itemView.findViewById(R.id.child_card_name);
            child_name = itemView.findViewById(R.id.child_name);
            child_avatar = itemView.findViewById(R.id.child_avatar);
            btn_click_photo = itemView.findViewById(R.id.btn_click_photo);
        }

        @SuppressLint("CheckResult")
        public void setView(Modal_Student modal_student, int position) {
            child_name.setText(modal_student.getFullName());
            int finalPos = position;
            if (modal_student.getAvatarName() == null || modal_student.getAvatarName().isEmpty()) {
                int drawable = PD_Utility.getRandomAvatar(modal_student.getGender());
                child_avatar.setActualImageResource(drawable);
            } else {
                Fresco.getImagePipeline().clearMemoryCaches();
                ImageRequest request = ImageRequestBuilder
                        .newBuilderWithSource(Uri.fromFile(new File(modal_student.getAvatarName())))
                        .setPostprocessor(new FaceCenterCrop(900, 900))
                        .setLocalThumbnailPreviewsEnabled(false)
                        .build();
                PipelineDraweeController controller = (PipelineDraweeController)
                        Fresco.newDraweeControllerBuilder()
                                .setOldController(child_avatar.getController())
                                .setImageRequest(request)
                                .build();
                child_avatar.setController(controller);
            }
            if (modal_student.isChecked()) {
                child_avatar.setSelected(true);
                child_card_name.setCardBackgroundColor(context.getResources().getColor(R.color.mustord_yellow));
            } else {
                child_avatar.setSelected(false);
                child_card_name.setCardBackgroundColor(context.getResources().getColor(android.R.color.transparent));
            }
            btn_click_photo.setOnClickListener(v ->
                    attendanceView.openCamera(modal_student.getStudentId()));
            itemView.setOnClickListener(v ->
                    attendanceView.childItemClicked(modal_student, finalPos));
        }
    }

    class SmartChildHolder extends RecyclerView.ViewHolder {
        LottieAnimationView smart_child_avatar;
        TextView smart_child_name;

        SmartChildHolder(@NonNull View itemView) {
            super(itemView);
            smart_child_avatar = itemView.findViewById(R.id.smart_lottie_child_avatar);
            smart_child_name = itemView.findViewById(R.id.smart_child_name);
        }
    }

    class AddChildHolder extends RecyclerView.ViewHolder {
        AddChildHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
