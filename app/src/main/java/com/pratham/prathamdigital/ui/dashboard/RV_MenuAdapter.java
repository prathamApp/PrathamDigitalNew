package com.pratham.prathamdigital.ui.dashboard;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.models.Modal_NavigationMenu;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by HP on 01-08-2017.
 */

public class RV_MenuAdapter extends RecyclerView.Adapter<RV_MenuAdapter.NormalItemViewHolder> {

    private Context context;
    private ArrayList<Modal_NavigationMenu> menus;
    private ContractMenu contractMenu;

    public RV_MenuAdapter(Context context, ArrayList<Modal_NavigationMenu> menus, ContractMenu contractMenu) {
        this.context = context;
        this.menus = menus;
        this.contractMenu = contractMenu;
    }

    @NonNull
    @Override
    public NormalItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView;
        LayoutInflater normal = LayoutInflater.from(viewGroup.getContext());
        itemView = normal.inflate(R.layout.item_nav_menu, viewGroup, false);
        return new NormalItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NormalItemViewHolder holder, int i) {
        holder.txt_menu_name.setText(menus.get(holder.getAdapterPosition()).getMenu_name());
        if (menus.get(holder.getAdapterPosition()).isIsselected()) {
            holder.txt_menu_name.setBackground(context.getResources().getDrawable(R.drawable.navigation_menu_selected_round_bkgd));
        } else {
            holder.txt_menu_name.setBackground(context.getResources().getDrawable(R.drawable.navigation_menu_unselected_round_bkgd));
        }
    }

    @Override
    public int getItemCount() {
        return menus.size();
    }

    public class NormalItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.txt_nav_menu_name)
        TextView txt_menu_name;

        public NormalItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
