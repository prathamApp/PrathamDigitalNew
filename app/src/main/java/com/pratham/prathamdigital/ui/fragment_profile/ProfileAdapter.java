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
import com.pratham.prathamdigital.view_holders.ProfileViewHolder;

import java.util.List;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import lombok.NonNull;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileViewHolder>{
    private List<Modal_ProfileDetails> detailsList;
    private Context context;

    public ProfileAdapter(Context context, List<Modal_ProfileDetails> detailsList) {
        this.context=context;
        this.detailsList = detailsList;
    }

    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_profile_details, parent, false);

        return new ProfileViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder holder, int position) {
        Modal_ProfileDetails details = detailsList.get(position);
        int detailListSize = detailsList.size();
        holder.setProfileView(context,details,position,detailListSize);
    }
    @Override
    public int getItemCount() {
        return detailsList.size();
    }
}
