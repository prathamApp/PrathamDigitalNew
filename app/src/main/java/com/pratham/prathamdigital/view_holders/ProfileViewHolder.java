package com.pratham.prathamdigital.view_holders;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.models.Modal_ProfileDetails;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProfileViewHolder extends RecyclerView.ViewHolder {

    public TextView date, videocount, gamecount, pdfcount;
    public View upperView, lowerView;
    public ImageView img_vidCnt, img_gamCnt, img_pdfCnt;

    public ProfileViewHolder(@NonNull View itemView) {
        super(itemView);
        date = itemView.findViewById(R.id.date);
        videocount = itemView.findViewById(R.id.vidCnt);
        gamecount = itemView.findViewById(R.id.gameCnt);
        pdfcount = itemView.findViewById(R.id.pdfCnt);
        upperView = itemView.findViewById(R.id.iv_upper_view);
        lowerView = itemView.findViewById(R.id.iv_lower_view);
        img_vidCnt = itemView.findViewById(R.id.icon1);
        img_gamCnt = itemView.findViewById(R.id.icon2);
        img_pdfCnt = itemView.findViewById(R.id.icon3);
    }

    public void setProfileView(Context context, Modal_ProfileDetails details, int position, int listSize){

        date.setText(details.getDate());
        videocount.setText(details.getVideoCnt());
        gamecount.setText(details.getGameCnt());
        pdfcount.setText(details.getPdfCnt());

        if (position == 0) {
            upperView.setVisibility(View.INVISIBLE);
        } else if (position == listSize - 1) {
            lowerView.setVisibility(View.INVISIBLE);
        }

    }
}
