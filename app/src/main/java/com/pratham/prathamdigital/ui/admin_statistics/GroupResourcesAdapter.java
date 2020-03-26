package com.pratham.prathamdigital.ui.admin_statistics;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.flexbox.FlexDirection;
import com.pratham.prathamdigital.custom.flexbox.FlexboxLayoutManager;
import com.pratham.prathamdigital.custom.flexbox.JustifyContent;
import com.pratham.prathamdigital.models.Modal_ResourcePlayedByGroups;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class GroupResourcesAdapter extends RecyclerView.Adapter<GroupResourcesAdapter.ViewHolder> {

    private HashMap<String, List<Modal_ResourcePlayedByGroups>> modal_resourcePlayedByGroups;
    private Context context1;

    public GroupResourcesAdapter(Context context, HashMap<String, List<Modal_ResourcePlayedByGroups>> modal_resourcePlayedByGroups) {
        context1 = context;
        this.modal_resourcePlayedByGroups = modal_resourcePlayedByGroups;
    }

    @NonNull
    @Override
    public GroupResourcesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_stat_grp_resource, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupResourcesAdapter.ViewHolder viewHolder, int i) {
        Set<String> keyset = modal_resourcePlayedByGroups.keySet();
        String[] keys = keyset.toArray(new String[keyset.size()]);
        viewHolder.stat_date.setText(keys[viewHolder.getAdapterPosition()]);
        ResourcesAdapter resourcesAdapter = new ResourcesAdapter(modal_resourcePlayedByGroups.get(keys[viewHolder.getAdapterPosition()]));
        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(context1, FlexDirection.ROW);
        flexboxLayoutManager.setJustifyContent(JustifyContent.CENTER);
        viewHolder.rv_stat_resource.setLayoutManager(flexboxLayoutManager);
        viewHolder.rv_stat_resource.setAdapter(resourcesAdapter);
    }

    @Override
    public int getItemCount() {
        return modal_resourcePlayedByGroups.size();
    }

    public void updateData(HashMap<String, List<Modal_ResourcePlayedByGroups>> modal_resourcePlayedByGroups) {
        this.modal_resourcePlayedByGroups = modal_resourcePlayedByGroups;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView stat_date;
        RecyclerView rv_stat_resource;

        ViewHolder(View itemView) {
            super(itemView);
            stat_date = itemView.findViewById(R.id.stat_date);
            rv_stat_resource = itemView.findViewById(R.id.rv_stat_resource);
        }
    }
}
