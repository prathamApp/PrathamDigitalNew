package com.pratham.prathamdigital.view_holders;

import android.animation.Animator;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.elastic_view.ElasticView;
import com.pratham.prathamdigital.custom.swipe_reveal_layout.SwipeRevealLayout;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.ui.fragment_content.ContentContract;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import java.io.File;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FileViewHolder extends RecyclerView.ViewHolder {
    @Nullable
    @BindView(R.id.rl_reveal)
    public RelativeLayout rl_reveal;
    @Nullable
    @BindView(R.id.file_swipe_layout)
    public SwipeRevealLayout file_swipe_layout;
    @Nullable
    @BindView(R.id.file_content_image)
    SimpleDraweeView file_content_image;
    @Nullable
    @BindView(R.id.file_item_lottieview)
    LottieAnimationView file_item_lottieview;
    @Nullable
    @BindView(R.id.rl_download)
    RelativeLayout rl_download;
    @Nullable
    @BindView(R.id.rl_play_content)
    RelativeLayout rl_play_content;
    @Nullable
    @BindView(R.id.txt_resource_title)
    TextView txt_resource_title;
    @Nullable
    @BindView(R.id.file_content_desc)
    TextView file_content_desc;
    @Nullable
    @BindView(R.id.delete_layout)
    View delete_layout;
    @Nullable
    @BindView(R.id.content_card_file)
    ElasticView content_card_file;
    private final ContentContract.contentClick contentClick;

    public FileViewHolder(View view, final ContentContract.contentClick contentClick) {
        super(view);
        ButterKnife.bind(this, view);
        this.contentClick = contentClick;
    }

    public void setContentItem(Modal_ContentDetail contentItem, int pos) {
        if (contentItem.getNodeserverimage() != null && !contentItem.getNodeserverimage().isEmpty()) {
            ImageRequest request;
            if (contentItem.isDownloaded()) {
                Uri imgUri;
                if (contentItem.isOnSDCard()) {
                    imgUri = Uri.fromFile(new File(
                            PrathamApplication.contentSDPath + "/PrathamImages/" + contentItem.getNodeimage()));
                    Objects.requireNonNull(file_swipe_layout).setLockDrag(true);
                    Objects.requireNonNull(file_content_image).setImageURI(imgUri);
                } else {
                    imgUri = Uri.fromFile(new File(
                            PrathamApplication.pradigiPath + "/PrathamImages/" + contentItem.getNodeimage()));
                    Objects.requireNonNull(file_swipe_layout).setLockDrag(false);
                    Objects.requireNonNull(file_content_image).setImageURI(imgUri);
                }
//                request = ImageRequestBuilder
//                        .newBuilderWithSource(imgUri)
//                        .setRequestPriority(Priority.HIGH)
//                        .setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH)
//                        .build();
            } else {
                Objects.requireNonNull(file_swipe_layout).setLockDrag(true);
                if (contentItem.getKolibriNodeImageUrl() != null && !contentItem.getKolibriNodeImageUrl().isEmpty()) {
                    request = ImageRequestBuilder
                            .newBuilderWithSource(Uri.parse(contentItem.getKolibriNodeImageUrl()))
                            .setResizeOptions(new ResizeOptions(300, 200))
                            .setLocalThumbnailPreviewsEnabled(true).build();
                } else {
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
            }
        }
        if (Objects.requireNonNull(rl_reveal).getVisibility() == View.VISIBLE)
            unreveal(rl_reveal);
        if (contentItem.isDownloaded()) {
            Objects.requireNonNull(rl_download).setVisibility(View.GONE);
            rl_download.setOnClickListener(null);
            Objects.requireNonNull(rl_play_content).setVisibility(View.VISIBLE);
            Objects.requireNonNull(file_content_desc).setText(contentItem.getNodetitle());
            if (contentItem.getResourcetype().toLowerCase().equalsIgnoreCase(PD_Constant.GAME))
                Objects.requireNonNull(file_item_lottieview).setAnimation("gaming_pad.json");
            else if (contentItem.getResourcetype().toLowerCase().equalsIgnoreCase(PD_Constant.VIDEO))
                Objects.requireNonNull(file_item_lottieview).setAnimation("play_button.json");
            else if (contentItem.getResourcetype().toLowerCase().equalsIgnoreCase(PD_Constant.PDF))
                Objects.requireNonNull(file_item_lottieview).setAnimation("book.json");
            Objects.requireNonNull(content_card_file).setOnClickListener(v -> contentClick.openContent(pos, contentItem));
            Objects.requireNonNull(delete_layout).setOnClickListener(v -> {
                Objects.requireNonNull(file_swipe_layout).close(true);
                contentClick.deleteContent(pos, contentItem);
            });
        } else {
            Objects.requireNonNull(rl_play_content).setVisibility(View.GONE);
            rl_reveal.setVisibility(View.INVISIBLE);
            Objects.requireNonNull(rl_download).setVisibility(View.VISIBLE);
            Objects.requireNonNull(txt_resource_title).setText(contentItem.getNodetitle());
//            if (request != null)
//                processImageWithPaletteApi(request);
            Drawable background = rl_download.getBackground();
            if (background instanceof GradientDrawable) {
                int color = PD_Utility.getRandomColorGradient();
                ((GradientDrawable) background).setColor(color);
                rl_reveal.setBackgroundColor(color);
            }
            rl_download.setOnClickListener(v -> contentClick.onDownloadClicked(pos, contentItem, rl_reveal));
            Objects.requireNonNull(content_card_file).setOnClickListener(null);
        }
    }

    private void unreveal(View view) {
        // previously visible view
        try {
            int centerX = view.getWidth();
            int centerY = view.getHeight();
            int startRadius = 0;
            int endRadius = Math.max(view.getWidth(), view.getHeight());
            Animator anim = ViewAnimationUtils.createCircularReveal(view, centerX, centerY, endRadius, startRadius);
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

    private void processImageWithPaletteApi(ImageRequest request) {
        DataSource<CloseableReference<CloseableImage>> dataSource =
                Fresco.getImagePipeline().fetchDecodedImage(request, Objects.requireNonNull(file_content_image).getContext());
        dataSource.subscribe(new BaseBitmapDataSubscriber() {
            @Override
            protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {

            }

            @Override
            protected void onNewResultImpl(@Nullable Bitmap bitmap) {
                Palette.from(Objects.requireNonNull(bitmap)).maximumColorCount(5).generate(palette -> {
                    Palette.Swatch textSwatch = Objects.requireNonNull(palette).getVibrantSwatch();
                    if (textSwatch != null && rl_reveal != null)
                        rl_reveal.setBackgroundColor(textSwatch.getRgb());
                });
            }
        }, CallerThreadExecutor.getInstance());
//            folder_content_image.setController(controller);
    }
}