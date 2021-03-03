package com.pratham.prathamdigital.view_holders;

import android.animation.Animator;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Handler;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.ui.fragment_content.ContentContract;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import java.io.File;
import java.util.Objects;

public class FileViewHolder extends RecyclerView.ViewHolder {
    @Nullable
    RelativeLayout rl_reveal;
    @Nullable
    SimpleDraweeView file_content_image;
    @Nullable
    ImageView img_download_content;
    @Nullable
    ImageView item_file_delete;
    @Nullable
    RelativeLayout rl_download;
    @Nullable
    TextView file_content_desc;
    @Nullable
    RelativeLayout content_card_file;
    @Nullable
    LinearLayout rl_delete_reveal;
    @Nullable
    Button file_del_yes;
    @Nullable
    Button file_del_no;
    private final ContentContract.contentClick contentClick;

    public FileViewHolder(View view, final ContentContract.contentClick contentClick) {
        super(view);
        rl_reveal = view.findViewById(R.id.rl_reveal);
        file_content_image = view.findViewById(R.id.file_content_image);
        img_download_content = view.findViewById(R.id.img_download_content);
        item_file_delete = view.findViewById(R.id.item_file_delete);
        rl_download = view.findViewById(R.id.rl_download);
        file_content_desc = view.findViewById(R.id.file_content_desc);
        content_card_file = view.findViewById(R.id.content_card_file);
        rl_delete_reveal = view.findViewById(R.id.rl_delete_reveal);
        file_del_yes = view.findViewById(R.id.file_del_yes);
        file_del_no = view.findViewById(R.id.file_del_no);
        this.contentClick = contentClick;
    }

    public void setContentItem(Modal_ContentDetail contentItem, int pos) {
        Objects.requireNonNull(file_content_desc).setText(contentItem.getNodetitle());
        if (contentItem.isDownloaded()) {
            if (!PrathamApplication.isTablet)
                Objects.requireNonNull(item_file_delete).setVisibility(View.VISIBLE);
            else Objects.requireNonNull(item_file_delete).setVisibility(View.GONE);
            Objects.requireNonNull(rl_reveal).setVisibility(View.GONE);
            if (contentItem.getNodeserverimage() != null && !contentItem.getNodeserverimage().isEmpty()) {
                Uri imgUri;
                if (contentItem.isOnSDCard()) {
                    imgUri = Uri.fromFile(new File(
                            PrathamApplication.externalContentPath + "/PrathamImages/" + contentItem.getNodeimage()));
                    Objects.requireNonNull(file_content_image).setImageURI(imgUri);
                } else {
                    imgUri = Uri.fromFile(new File(
                            PrathamApplication.pradigiPath + "/PrathamImages/" + contentItem.getNodeimage()));
                    Objects.requireNonNull(file_content_image).setImageURI(imgUri);
                }
            }
            if (contentItem.getResourcetype().toLowerCase().equalsIgnoreCase(PD_Constant.GAME))
                Objects.requireNonNull(img_download_content).setImageResource(R.drawable.ic_joystick);
            else if (contentItem.getResourcetype().toLowerCase().equalsIgnoreCase(PD_Constant.VIDEO))
                Objects.requireNonNull(img_download_content).setImageResource(R.drawable.ic_video);
            else if (contentItem.getResourcetype().toLowerCase().equalsIgnoreCase(PD_Constant.PDF))
                Objects.requireNonNull(img_download_content).setImageResource(R.drawable.ic_book);
            else if (contentItem.getResourcetype().toLowerCase().equalsIgnoreCase(PD_Constant.AUDIO))
                Objects.requireNonNull(img_download_content).setImageResource(R.drawable.ic_music_icon);
            Objects.requireNonNull(content_card_file).setOnClickListener(v -> contentClick.openContent(pos, contentItem));
            Objects.requireNonNull(rl_download).setOnClickListener(v -> contentClick.openContent(pos, contentItem));
            Objects.requireNonNull(item_file_delete).setOnClickListener(v -> {
                Objects.requireNonNull(rl_delete_reveal).setVisibility(View.INVISIBLE);
                new Handler().postDelayed(() -> reveal(rl_delete_reveal, item_file_delete), 200);
            });
            Objects.requireNonNull(file_del_yes).setOnClickListener(v -> contentClick.deleteContent(pos, contentItem));
            Objects.requireNonNull(file_del_no).setOnClickListener(v -> unreveal(rl_delete_reveal, item_file_delete));
        } else {
            Objects.requireNonNull(rl_delete_reveal).setVisibility(View.GONE);
            Objects.requireNonNull(item_file_delete).setVisibility(View.GONE);
            Objects.requireNonNull(img_download_content).setImageResource(R.drawable.content_download_icon);
            ImageRequest request = null;
            if (contentItem.getKolibriNodeImageUrl() != null && !contentItem.getKolibriNodeImageUrl().isEmpty()) {
                request = ImageRequestBuilder
                        .newBuilderWithSource(Uri.parse(contentItem.getKolibriNodeImageUrl()))
                        .setResizeOptions(new ResizeOptions(300, 200))
                        .setLocalThumbnailPreviewsEnabled(true).build();
            } else if (contentItem.getNodeserverimage() != null && !contentItem.getNodeserverimage().isEmpty()) {
                request = ImageRequestBuilder
                        .newBuilderWithSource(Uri.parse(contentItem.getNodeserverimage()))
                        .setResizeOptions(new ResizeOptions(300, 200))
                        .setLocalThumbnailPreviewsEnabled(true).build();
            }
            if (request != null) {
                DraweeController controller = Fresco.newDraweeControllerBuilder()
                        .setImageRequest(request)
                        .setOldController(Objects.requireNonNull(file_content_image).getController())
                        .build();
                file_content_image.setController(controller);
            }
            Drawable background = Objects.requireNonNull(rl_download).getBackground();
            if (background instanceof GradientDrawable) {
                int color = PD_Utility.getRandomColorGradient();
                ((GradientDrawable) background).setColor(color);
                Objects.requireNonNull(rl_reveal).setBackgroundColor(color);
            }
            rl_download.setOnClickListener(v -> {
                if (rl_reveal != null) rl_reveal.setVisibility(View.INVISIBLE);
                contentClick.onDownloadClicked(pos, contentItem, rl_reveal, rl_download);
            });
            Objects.requireNonNull(content_card_file).setOnClickListener(null);
        }
        if (Objects.requireNonNull(rl_reveal).getVisibility() == View.VISIBLE)
            unreveal(rl_reveal, rl_download);
        if (Objects.requireNonNull(rl_delete_reveal).getVisibility() == View.VISIBLE)
            unreveal(rl_delete_reveal, item_file_delete);
    }

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
}