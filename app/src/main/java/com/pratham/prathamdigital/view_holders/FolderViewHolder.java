package com.pratham.prathamdigital.view_holders;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.label.LabelView;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.ui.fragment_content.ContentContract;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import java.io.File;
import java.util.Objects;

public class FolderViewHolder extends RecyclerView.ViewHolder {
    @Nullable
    SimpleDraweeView folder_content_image;
    @Nullable
    TextView folder_title;
    @Nullable
    TextView folder_content_desc;
    @Nullable
    LabelView content_card;

    private final ContentContract.contentClick contentClick;

    public FolderViewHolder(View itemView, final ContentContract.contentClick contentClick) {
        super(itemView);
        folder_content_image = itemView.findViewById(R.id.folder_content_image);
        folder_title = itemView.findViewById(R.id.folder_title);
        folder_content_desc = itemView.findViewById(R.id.folder_content_desc);
        content_card = itemView.findViewById(R.id.content_card);
        this.contentClick = contentClick;
    }

    @SuppressLint("SetTextI18n")
    public void setFolderItem(Modal_ContentDetail contentItem, int pos) {
//        if (contentItem.getNodeserverimage() == null)
//            Objects.requireNonNull(contentItem.getNodeserverimage());
        if (contentItem.getNodetype().equalsIgnoreCase(PD_Constant.COURSE))
            Objects.requireNonNull(content_card).setLabelVisual(true);
        else Objects.requireNonNull(content_card).setLabelVisual(false);
        ImageRequest request = null;
        if (contentItem.isDownloaded()) {
            Uri imgUri;
            if (contentItem.isOnSDCard()) {
                imgUri = Uri.fromFile(new File(
                        PrathamApplication.externalContentPath + "/PrathamImages/" + contentItem.getNodeimage()));
                Objects.requireNonNull(folder_content_image).setImageURI(imgUri);
            } else {
                imgUri = Uri.fromFile(new File(
                        PrathamApplication.pradigiPath + "/PrathamImages/" + contentItem.getNodeimage()));
                Objects.requireNonNull(folder_content_image).setImageURI(imgUri);
            }
        } else {
            if (contentItem.getKolibriNodeImageUrl() != null && !contentItem.getKolibriNodeImageUrl().isEmpty()) {
                request = ImageRequestBuilder
                        .newBuilderWithSource(Uri.parse(contentItem.getKolibriNodeImageUrl()))
                        .setResizeOptions(new ResizeOptions(300, 200))
                        .setLocalThumbnailPreviewsEnabled(true).build();
            } else {
                if (contentItem.getNodeserverimage() != null)
                    request = ImageRequestBuilder
                            .newBuilderWithSource(Uri.parse(contentItem.getNodeserverimage()))
                            .setResizeOptions(new ResizeOptions(300, 200))
                            .setLocalThumbnailPreviewsEnabled(false).build();
            }
            if (request != null) {
                Log.d("uri::", "" + request.getSourceUri().toString());
                DraweeController controller = Fresco.newDraweeControllerBuilder()
                        .setOldController(Objects.requireNonNull(folder_content_image).getController())
                        .setImageRequest(request)
                        .build();
                folder_content_image.setController(controller);
            }
        }
        Objects.requireNonNull(content_card).setBackgroundColor(PD_Utility.getRandomColorGradient());
        Objects.requireNonNull(folder_title).setText(contentItem.getNodetitle());
        Objects.requireNonNull(folder_title).setSelected(true);
//        if (contentItem.getNodedesc() == null || contentItem.getNodedesc().isEmpty())
//            Objects.requireNonNull(folder_content_desc).setText("No description");
//        else
//            Objects.requireNonNull(folder_content_desc).setText(contentItem.getNodedesc());
        content_card.setOnClickListener(v -> contentClick.onfolderClicked(pos, contentItem));
    }
}
