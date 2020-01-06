package com.pratham.prathamdigital.ui.fragment_admin_options;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.models.Modal_NavigationMenu;

import java.util.ArrayList;

public class RV_OptionsAdapter extends RecyclerView.Adapter<RV_OptionsAdapter.NormalItemViewHolder> {

    private final ArrayList<Modal_NavigationMenu> menus;
    private final ContractOptions.optionAdapterClick contractMenu;

    public RV_OptionsAdapter(Context context, ArrayList<Modal_NavigationMenu> menus, ContractOptions.optionAdapterClick contractMenu) {
        Context context1 = context;
        this.menus = menus;
        this.contractMenu = contractMenu;
    }

    @NonNull
    @Override
    public NormalItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView;
        LayoutInflater normal = LayoutInflater.from(viewGroup.getContext());
        itemView = normal.inflate(R.layout.item_admin_options, viewGroup, false);
        return new NormalItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NormalItemViewHolder holder, int i) {
        holder.txt_option_name.setText(menus.get(holder.getAdapterPosition()).getMenu_name());
        holder.img_option.setImageResource(menus.get(holder.getAdapterPosition()).getMenuImage());
        holder.itemView.setOnClickListener(v -> contractMenu.menuClicked(holder.getAdapterPosition(), menus.get(holder.getAdapterPosition()), holder.itemView));
    }

    @Override
    public int getItemCount() {
        return menus.size();
    }

    public ArrayList<Modal_NavigationMenu> getMenus() {
        return menus;
    }

    public class NormalItemViewHolder extends RecyclerView.ViewHolder {
        TextView txt_option_name;
        ImageView img_option;

        NormalItemViewHolder(View itemView) {
            super(itemView);
            txt_option_name = itemView.findViewById(R.id.txt_option_name);
            img_option = itemView.findViewById(R.id.img_option);
        }
    }
}