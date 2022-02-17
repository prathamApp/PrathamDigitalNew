package com.pratham.prathamdigital.view_holders;

import android.animation.Animator;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.label.LabelView;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.ui.content_player.ContentPlayerContract;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import java.io.File;
import java.util.Objects;

import static com.pratham.prathamdigital.PrathamApplication.contentProgressDao;

public class CourseChildViewHolder extends RecyclerView.ViewHolder {
    SimpleDraweeView course_child_image;
    TextView course_child_title;
    ImageView img_content_type;
    //Below added all fileviewholder items
    LabelView content_card;
    RelativeLayout rl_courseReveal;
    ImageView item_courseFile_delete;
    RelativeLayout rl_courseDownload;
    RelativeLayout content_courseCard_file;
    LinearLayout rl_courseDelete_reveal;
    Button course_del_yes;
    Button course_del_no;
    @Nullable
    TextView course_video_watched_percent;


    public CourseChildViewHolder(@NonNull View itemView) {
        super(itemView);
        course_child_image = itemView.findViewById(R.id.course_child_image);
        course_child_title = itemView.findViewById(R.id.course_child_title);
        img_content_type = itemView.findViewById(R.id.img_content_type);
        content_card = itemView.findViewById(R.id.content_card);
        rl_courseReveal = itemView.findViewById(R.id.rl_courseReveal);
        item_courseFile_delete = itemView.findViewById(R.id.item_courseFile_delete);
        rl_courseDownload = itemView.findViewById(R.id.rl_courseDownload);
        content_courseCard_file = itemView.findViewById(R.id.content_courseCard_file);
        rl_courseDelete_reveal = itemView.findViewById(R.id.rl_courseDelete_reveal);
        course_del_yes = itemView.findViewById(R.id.course_del_yes);
        course_del_no = itemView.findViewById(R.id.course_del_no);
        course_video_watched_percent = itemView.findViewById(R.id.item_watchedPercent);
    }

    public void setChildItems(Modal_ContentDetail contentDetail,
                              ContentPlayerContract.courseDetailAdapterClick courseDetailAdapterClick, int pos) {
        course_child_title.setText(contentDetail.getNodetitle());
        Log.e("url", contentDetail.getNodeid() + " | " + String.valueOf(contentDetail.getIsViewed()));
         if (contentDetail.isDownloaded()) {
/*            if (!PrathamApplication.isTablet)
                Objects.requireNonNull(item_courseFile_delete).setVisibility(View.GONE);
            else Objects.requireNonNull(item_courseFile_delete).setVisibility(View.GONE);*/
            Objects.requireNonNull(item_courseFile_delete).setVisibility(View.GONE);
            Objects.requireNonNull(rl_courseReveal).setVisibility(View.GONE);
            if (contentDetail.getNodeserverimage() != null && !contentDetail.getNodeserverimage().isEmpty()) {
                Uri imgUri;
                if (contentDetail.isOnSDCard())
                    imgUri = Uri.fromFile(new File(
                            PrathamApplication.externalContentPath + "/PrathamImages/" + contentDetail.getNodeimage()));
                else
                    imgUri = Uri.fromFile(new File(
                            PrathamApplication.pradigiPath + "/PrathamImages/" + contentDetail.getNodeimage()));

                course_child_image.setImageURI(imgUri);
            }
            //TODO : Check this..
            // Works on first view, but not when app is restarted
            try {
                if (contentDetail.getIsViewed()) {
                    content_card.setLabelVisual(true);
                } else
                    content_card.setLabelVisual(false);
            } catch (Exception e) {
                Log.e("Exception : ", e.getMessage());
            }

            Objects.requireNonNull(content_card).setBackgroundColor(PD_Utility.getRandomColorGradient());
            if (contentDetail.getResourcetype().toLowerCase().equalsIgnoreCase(PD_Constant.GAME)) {
                Objects.requireNonNull(img_content_type).setImageResource(R.drawable.ic_joystick);
                Objects.requireNonNull(course_video_watched_percent).setVisibility(View.GONE);
//                Objects.requireNonNull(img_content_type).setVisibility(View.VISIBLE);
            } else if (contentDetail.getResourcetype().toLowerCase().equalsIgnoreCase(PD_Constant.VIDEO)) {
                Objects.requireNonNull(img_content_type).setImageResource(R.drawable.ic_video);
//                Objects.requireNonNull(img_content_type).setVisibility(View.VISIBLE);
                String percent = contentProgressDao.progressPercent(FastSave.getInstance().getString(PD_Constant.GROUPID, ""), contentDetail.getResourceid());
                if (percent != null) {
                    Objects.requireNonNull(course_video_watched_percent).setVisibility(View.VISIBLE);
                    course_video_watched_percent.setText(percent + "%");
                } else {
                    Objects.requireNonNull(course_video_watched_percent).setVisibility(View.GONE);
                }
            } else if (contentDetail.getResourcetype().toLowerCase().equalsIgnoreCase(PD_Constant.PDF)) {
                Objects.requireNonNull(img_content_type).setImageResource(R.drawable.ic_book);
                Objects.requireNonNull(course_video_watched_percent).setVisibility(View.GONE);
//                Objects.requireNonNull(img_content_type).setVisibility(View.VISIBLE);
            } else if (contentDetail.getResourcetype().toLowerCase().equalsIgnoreCase(PD_Constant.AUDIO)) {
                Objects.requireNonNull(img_content_type).setImageResource(R.drawable.ic_music_icon);
                Objects.requireNonNull(course_video_watched_percent).setVisibility(View.GONE);
//                Objects.requireNonNull(img_content_type).setVisibility(View.VISIBLE);
            }

            if (contentDetail.getNodetype().equalsIgnoreCase(PD_Constant.ASSESSMENT)) {
                Objects.requireNonNull(img_content_type).setVisibility(View.GONE);
                Objects.requireNonNull(course_video_watched_percent).setVisibility(View.GONE);
                course_child_title.setText(contentDetail.getNodetitle());
 //               course_child_image.setImageResource(contentDetail.getNodeserverimage());
            } else {
                Objects.requireNonNull(img_content_type).setVisibility(View.VISIBLE);
            }

            /** this condition used for content versioning*/
            if (contentDetail.isNodeUpdate())
                Objects.requireNonNull(img_content_type).setImageResource(R.drawable.ic_update);

            rl_courseDownload.setOnClickListener(v -> {//
                Log.e("rl click : ", "in");
                if (contentDetail.isNodeUpdate()) {
                    if (rl_courseReveal != null) rl_courseReveal.setVisibility(View.INVISIBLE);
                    Log.e("rl click : ", "in if");
                    courseDetailAdapterClick.onDownloadClicked(pos, contentDetail, rl_courseReveal, rl_courseDownload);
                } else {
                    if (contentDetail.getNodetype().equalsIgnoreCase(PD_Constant.ASSESSMENT))
                        courseDetailAdapterClick.onAssessmentItemClicked(contentDetail);
                    else {
                        Log.e("rl click : ", "in if else");
                        courseDetailAdapterClick.onChildItemClicked(contentDetail);
                    }
                }
            });


            itemView.setOnClickListener(v -> {
                if (contentDetail.getNodetype().equalsIgnoreCase(PD_Constant.ASSESSMENT))
                    courseDetailAdapterClick.onAssessmentItemClicked(contentDetail);
                else
                    courseDetailAdapterClick.onChildItemClicked(contentDetail);
            });
        } else {
            //if content is not downloaded
            Objects.requireNonNull(rl_courseDelete_reveal).setVisibility(View.GONE);
            Objects.requireNonNull(item_courseFile_delete).setVisibility(View.GONE);
            Objects.requireNonNull(course_video_watched_percent).setVisibility(View.GONE);
            Objects.requireNonNull(img_content_type).setImageResource(R.drawable.content_download_icon);

            //show img
            ImageRequest request = null;
            if (contentDetail.getKolibriNodeImageUrl() != null && !contentDetail.getKolibriNodeImageUrl().isEmpty()) {
                request = ImageRequestBuilder
                        .newBuilderWithSource(Uri.parse(contentDetail.getKolibriNodeImageUrl()))
                        .setResizeOptions(new ResizeOptions(300, 200))
                        .setLocalThumbnailPreviewsEnabled(true).build();
            } else if (contentDetail.getNodeserverimage() != null && !contentDetail.getNodeserverimage().isEmpty()) {
                request = ImageRequestBuilder
                        .newBuilderWithSource(Uri.parse(contentDetail.getNodeserverimage()))
                        .setResizeOptions(new ResizeOptions(300, 200))
                        .setLocalThumbnailPreviewsEnabled(true).build();
            }

            if (request != null) {
                DraweeController controller = Fresco.newDraweeControllerBuilder()
                        .setImageRequest(request)
                        .setOldController(Objects.requireNonNull(course_child_image).getController())
                        .build();
                course_child_image.setController(controller);
            } //show img
            Drawable background = Objects.requireNonNull(rl_courseDownload).getBackground();//
            if (background instanceof GradientDrawable) {
                int color = PD_Utility.getRandomColorGradient();
                ((GradientDrawable) background).setColor(color);
                Objects.requireNonNull(rl_courseReveal).setBackgroundColor(color);
            }
            // TODO : Further Functionality when download clicked
            rl_courseDownload.setOnClickListener(v -> {//
                Log.e("rl click dwnld: ", "in");
                if (contentDetail.getNodetype().equalsIgnoreCase(PD_Constant.ASSESSMENT))
                    courseDetailAdapterClick.onAssessmentItemClicked(contentDetail);
                else {
                    if (rl_courseReveal != null) rl_courseReveal.setVisibility(View.INVISIBLE);
                    courseDetailAdapterClick.onDownloadClicked(pos, contentDetail, rl_courseReveal, rl_courseDownload);
                }
            });
            Objects.requireNonNull(content_courseCard_file).setOnClickListener(null);

            if (contentDetail.getNodetype().equalsIgnoreCase(PD_Constant.ASSESSMENT)) {
                Objects.requireNonNull(img_content_type).setVisibility(View.GONE);
                course_child_title.setText(contentDetail.getNodetitle());
//                course_child_image.setImageResource(R.drawable.assessment_logo);
                //rl_courseDownload.setEnabled(false);
            } else {
                Objects.requireNonNull(img_content_type).setVisibility(View.VISIBLE);
            }

            itemView.setOnClickListener(v -> {
                if (contentDetail.getNodetype().equalsIgnoreCase(PD_Constant.ASSESSMENT))
                    courseDetailAdapterClick.onAssessmentItemClicked(contentDetail);
            });

        }
        if (Objects.requireNonNull(rl_courseReveal).getVisibility() == View.VISIBLE)
            unreveal(rl_courseReveal, rl_courseDownload);
        if (Objects.requireNonNull(rl_courseDelete_reveal).getVisibility() == View.VISIBLE)
            unreveal(rl_courseDelete_reveal, item_courseFile_delete);
    }


/*        if (contentDetail.getNodetype().equalsIgnoreCase(PD_Constant.ASSESSMENT)) {
            Objects.requireNonNull(img_content_type).setVisibility(View.GONE);
            course_child_title.setText("Give Assessment");
            course_child_image.setImageResource(R.drawable.assessment_logo);
        }

        itemView.setOnClickListener(v -> {
            if (contentDetail.getNodetype().equalsIgnoreCase(PD_Constant.ASSESSMENT))
                courseDetailAdapterClick.onAssessmentItemClicked();
            else
                courseDetailAdapterClick.onChildItemClicked(contentDetail);
        });*/

    private void unreveal(View view, View end) {
        // previously visible view
        try {
            int centerX = view.getWidth();
            int centerY = view.getHeight();
            int startRadius = 0;
            int endRadius = Math.max(centerX, centerY);
            Animator anim = ViewAnimationUtils.createCircularReveal(view, (int) end.getX(), (int) end.getY(), endRadius, startRadius);
            anim.setInterpolator(new AccelerateDecelerateInterpolator());
            anim.setDuration(300);
            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    view.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
            anim.start();
        } catch (Exception e) {
            e.printStackTrace();
            view.setVisibility(View.GONE);
        }
    }

    public void reveal(View view, View startView) {
        // previously invisible view
        try {
            int centerX = view.getWidth();
            int centerY = view.getHeight();
            int startRadius = 0;
            int endRadius = (int) Math.hypot(centerX, centerY);
            Animator anim = ViewAnimationUtils.createCircularReveal(view, (int) startView.getX(), (int) startView.getY(), startRadius, endRadius);
            anim.setInterpolator(new AccelerateDecelerateInterpolator());
            anim.setDuration(300);
            view.setVisibility(View.VISIBLE);
            anim.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Method to call download method from adapter
    public void downloadAll(Modal_ContentDetail contentDetail, ContentPlayerContract.courseDetailAdapterClick courseDetailAdapterClick, int position){
        courseDetailAdapterClick.onDownloadClicked(position, contentDetail, rl_courseReveal, rl_courseDownload);
    }
}
