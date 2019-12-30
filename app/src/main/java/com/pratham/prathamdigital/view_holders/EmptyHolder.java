package com.pratham.prathamdigital.view_holders;

import android.support.annotation.NonNull;
import android.support.design.card.MaterialCardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.models.Modal_ContentDetail;

public class EmptyHolder extends RecyclerView.ViewHolder {

    private MaterialCardView header_card;
    private SimpleDraweeView img_header;
    private Button header_startCourse;
    private TextView txt_header;

    public EmptyHolder(@NonNull View itemView) {
        super(itemView);
        this.header_card = (MaterialCardView) itemView.findViewById(R.id.header_card);
        this.img_header = (SimpleDraweeView) itemView.findViewById(R.id.img_header);
        this.header_startCourse = (Button) itemView.findViewById(R.id.header_startCourse);
        this.txt_header = (TextView) itemView.findViewById(R.id.txt_header);
    }

    public void setView(Modal_ContentDetail contentDetail) {
        /*if (contentDetail != null && contentDetail.getNodetype().equalsIgnoreCase(PD_Constant.COURSE)) {
            header_card.setVisibility(View.VISIBLE);
            txt_header.setText(contentDetail.getNodetitle());
            ImageRequest request = null;
            if (contentDetail.isDownloaded()) {
                Uri imgUri;
                if (contentDetail.isOnSDCard())
                    imgUri = Uri.fromFile(new File(
                            PrathamApplication.externalContentPath + "/PrathamImages/" + contentDetail.getNodeimage()));
                else
                    imgUri = Uri.fromFile(new File(
                            PrathamApplication.pradigiPath + "/PrathamImages/" + contentDetail.getNodeimage()));
                Objects.requireNonNull(img_header).setImageURI(imgUri);
            } else {
                if (contentDetail.getKolibriNodeImageUrl() != null && !contentDetail.getKolibriNodeImageUrl().isEmpty()) {
                    request = ImageRequestBuilder
                            .newBuilderWithSource(Uri.parse(contentDetail.getKolibriNodeImageUrl()))
                            .setResizeOptions(new ResizeOptions(300, 200))
                            .setLocalThumbnailPreviewsEnabled(true).build();
                } else {
                    if (contentDetail.getNodeserverimage() != null)
                        request = ImageRequestBuilder
                                .newBuilderWithSource(Uri.parse(contentDetail.getNodeserverimage()))
                                .setResizeOptions(new ResizeOptions(300, 200))
                                .setLocalThumbnailPreviewsEnabled(false).build();
                }
                if (request != null) {
                    Log.d("uri::", "" + request.getSourceUri().toString());
                    DraweeController controller = Fresco.newDraweeControllerBuilder()
                            .setOldController(Objects.requireNonNull(img_header).getController())
                            .setImageRequest(request)
                            .build();
                    img_header.setController(controller);
                }
            }
        } else {*/
        if (header_card.getVisibility() == View.VISIBLE)
            header_card.setVisibility(View.GONE);
        header_startCourse.setOnClickListener(null);
//        }
    }
}