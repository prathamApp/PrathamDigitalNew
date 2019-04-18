package com.pratham.prathamdigital.view_holders;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.Priority;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.ui.fragment_content.ContentContract;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FolderViewHolder extends RecyclerView.ViewHolder {
    @Nullable
    @BindView(R.id.folder_content_image)
    SimpleDraweeView folder_content_image;
    @Nullable
    @BindView(R.id.folder_title)
    TextView folder_title;
    @Nullable
    @BindView(R.id.folder_content_desc)
    TextView folder_content_desc;
    @Nullable
    @BindView(R.id.content_card)
    RelativeLayout content_card;

    Modal_ContentDetail contentItem;
    ContentContract.contentClick contentClick;
    ImageRequest request = null;

    public FolderViewHolder(View itemView, final ContentContract.contentClick contentClick) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.contentClick = contentClick;
    }

    public void setFolderItem(Modal_ContentDetail contentItem, int pos) {
        this.contentItem = contentItem;
        if (contentItem.getNodeserverimage() != null || !contentItem.getNodeserverimage().isEmpty()) {
            if (contentItem.isDownloaded()) {
                Uri imgUri;
                if (contentItem.isOnSDCard()) {
                    imgUri = Uri.fromFile(new File(
                            PrathamApplication.contentSDPath + "/PrathamImages/" + contentItem.getNodeimage()));
                    folder_content_image.setImageURI(imgUri);
                } else {
                    imgUri = Uri.fromFile(new File(
                            PrathamApplication.pradigiPath + "/PrathamImages/" + contentItem.getNodeimage()));
                    folder_content_image.setImageURI(imgUri);
                }
                request = ImageRequestBuilder
                        .newBuilderWithSource(imgUri)
                        .setRequestPriority(Priority.HIGH)
                        .setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH)
                        .build();
            } else {
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
                            .setOldController(folder_content_image.getController())
                            .build();
                    folder_content_image.setController(controller);
                }
            }
        }
        if (request != null)
            processImageWithPaletteApi(request);
//        else
//            content_card.setBackgroundColor(PD_Utility.getRandomColorGradient());
        folder_title.setText(contentItem.getNodetitle());
        if (contentItem.getNodedesc() == null || contentItem.getNodedesc().isEmpty())
            folder_content_desc.setText("No description");
        else
            folder_content_desc.setText(contentItem.getNodedesc());
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contentClick.onfolderClicked(pos, contentItem);
            }
        });
    }

    private void processImageWithPaletteApi(ImageRequest request) {
        DataSource<CloseableReference<CloseableImage>> dataSource =
                Fresco.getImagePipeline().fetchDecodedImage(request, folder_content_image.getContext());
        dataSource.subscribe(new BaseBitmapDataSubscriber() {
            @Override
            protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {

            }

            @Override
            protected void onNewResultImpl(@Nullable Bitmap bitmap) {
                Palette.from(bitmap).maximumColorCount(5).generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        Palette.Swatch textSwatch = palette.getVibrantSwatch();
                        if (textSwatch != null) {
                            content_card.setBackgroundColor(palette.getDarkVibrantSwatch().getRgb());
                            folder_title.setTextColor(textSwatch.getBodyTextColor());
                        }
                    }
                });
            }
        }, CallerThreadExecutor.getInstance());
//            folder_content_image.setController(controller);
    }
}
