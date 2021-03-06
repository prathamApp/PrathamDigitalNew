package com.pratham.prathamdigital.ui.admin_statistics;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.models.Modal_ResourcePlayedByGroups;

import java.util.List;

public class ResourcesAdapter extends RecyclerView.Adapter<ResourcesAdapter.ViewHolder> {

    private List<Modal_ResourcePlayedByGroups> datalist;

    public ResourcesAdapter(List<Modal_ResourcePlayedByGroups> datalist) {
        this.datalist = datalist;
    }

    @NonNull
    @Override
    public ResourcesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_stat_resource, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ResourcesAdapter.ViewHolder viewHolder, int i) {
        viewHolder.stat_res_name.setText(datalist.get(viewHolder.getAdapterPosition()).getNodetitle());
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView stat_res_name;

        ViewHolder(View itemView) {
            super(itemView);
            stat_res_name = itemView.findViewById(R.id.stat_res_name);
        }
    }
}
