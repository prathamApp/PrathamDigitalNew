package com.pratham.prathamdigital.ui.fragment_profile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.models.Modal_ProfileDetails;

import java.util.List;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.MyViewHolder>{
    private List<Modal_ProfileDetails> detailsList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView date, videocount, gamecount, pdfcount;
        public View upperView, lowerView;
        public ImageView img_vidCnt, img_gamCnt, img_pdfCnt;


        public MyViewHolder(View view) {
            super(view);
            date = view.findViewById(R.id.date);
            videocount = view.findViewById(R.id.vidCnt);
            gamecount = view.findViewById(R.id.gameCnt);
            pdfcount = view.findViewById(R.id.pdfCnt);
            upperView = view.findViewById(R.id.iv_upper_view);
            lowerView = view.findViewById(R.id.iv_lower_view);
            img_vidCnt = view.findViewById(R.id.icon1);
            img_gamCnt = view.findViewById(R.id.icon2);
            img_pdfCnt = view.findViewById(R.id.icon3);
        }
    }


    public ProfileAdapter(Context context, List<Modal_ProfileDetails> detailsList) {
        this.context=context;
        this.detailsList = detailsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_profile_details, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Modal_ProfileDetails details = detailsList.get(position);
        holder.date.setText(details.getDate());
        holder.videocount.setText(details.getVideoCnt());
        holder.gamecount.setText(details.getGameCnt());
        holder.pdfcount.setText(details.getPdfCnt());

        if (position == 0) {
            holder.upperView.setVisibility(View.INVISIBLE);
        } else if (position == detailsList.size() - 1) {
            holder.lowerView.setVisibility(View.INVISIBLE);
        }
    }
    @Override
    public int getItemCount() {
        return detailsList.size();
    }
}
