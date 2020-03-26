package com.pratham.prathamdigital.ui.admin_statistics;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.models.Modal_TotalDaysGroupsPlayed;
import com.pratham.prathamdigital.ui.fragment_admin_options.ContractOptions;

import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {

    private List<Modal_TotalDaysGroupsPlayed> modal_totalDaysGroupsPlayeds;
    private ContractOptions.optionAdapterClick contractOptions;

    public GroupAdapter(Context context, List<Modal_TotalDaysGroupsPlayed> modal_totalDaysGroupsPlayeds,
                        ContractOptions.optionAdapterClick contractOptions) {
        Context context1 = context;
        this.modal_totalDaysGroupsPlayeds = modal_totalDaysGroupsPlayeds;
        this.contractOptions = contractOptions;
    }

    @NonNull
    @Override
    public GroupAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_stat_group, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupAdapter.ViewHolder viewHolder, int i) {
        viewHolder.stat_grp_name.setText(modal_totalDaysGroupsPlayeds.get(viewHolder.getAdapterPosition()).getGroupName());
        viewHolder.stat_grp_date.setText(modal_totalDaysGroupsPlayeds.get(viewHolder.getAdapterPosition()).getDates());
        viewHolder.itemView.setOnClickListener(v -> {
            contractOptions.menuClicked(viewHolder.getAdapterPosition(), null, null);
        });
    }

    @Override
    public int getItemCount() {
        return modal_totalDaysGroupsPlayeds.size();
    }

    public void updateItems(List<Modal_TotalDaysGroupsPlayed> modal_totalDaysGroupsPlayeds) {
        this.modal_totalDaysGroupsPlayeds = modal_totalDaysGroupsPlayeds;
        notifyDataSetChanged();
    }

    public List<Modal_TotalDaysGroupsPlayed> getItems() {
        return modal_totalDaysGroupsPlayeds;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView stat_grp_name;
        TextView stat_grp_date;
        MaterialCardView item_grp_card;

        ViewHolder(View itemView) {
            super(itemView);
            stat_grp_name = itemView.findViewById(R.id.stat_grp_name);
            stat_grp_date = itemView.findViewById(R.id.stat_grp_date);
            item_grp_card = itemView.findViewById(R.id.item_grp_card);
        }
    }
}
