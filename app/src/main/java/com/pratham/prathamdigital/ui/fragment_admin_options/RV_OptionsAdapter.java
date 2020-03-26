package com.pratham.prathamdigital.ui.fragment_admin_options;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.animated_switch.RevealSwitch;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.models.Modal_NavigationMenu;
import com.pratham.prathamdigital.util.PD_Constant;

import java.util.ArrayList;

public class RV_OptionsAdapter extends RecyclerView.Adapter {

    private final ArrayList<Modal_NavigationMenu> menus;
    private final ContractOptions.optionAdapterClick contractMenu;
    private static final int NORMAL_OPTION = 1;
    private static final int SWITCH_OPTION = 2;

    public RV_OptionsAdapter(Context context, ArrayList<Modal_NavigationMenu> menus, ContractOptions.optionAdapterClick contractMenu) {
        Context context1 = context;
        this.menus = menus;
        this.contractMenu = contractMenu;
    }

    @Override
    public int getItemViewType(int position) {
        if ("Change Content Folder".equals(menus.get(position).getMenu_name()))
            return SWITCH_OPTION;
        return NORMAL_OPTION;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater normal = LayoutInflater.from(viewGroup.getContext());
        switch (viewType) {
            case NORMAL_OPTION:
                View itemView = normal.inflate(R.layout.item_admin_options, viewGroup, false);
                return new NormalItemViewHolder(itemView);
            case SWITCH_OPTION:
                View itemView1 = normal.inflate(R.layout.item_admin_switch_options, viewGroup, false);
                return new SwitchItemViewHolder(itemView1);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {
        switch (holder.getItemViewType()) {
            case NORMAL_OPTION:
                NormalItemViewHolder normalItemViewHolder = (NormalItemViewHolder) holder;
                normalItemViewHolder.txt_option_name.setText(menus.get(holder.getAdapterPosition()).getMenu_name());
                normalItemViewHolder.img_option.setImageResource(menus.get(holder.getAdapterPosition()).getMenuImage());
                normalItemViewHolder.itemView.setOnClickListener(v -> contractMenu.menuClicked(holder.getAdapterPosition(), menus.get(holder.getAdapterPosition()), holder.itemView));
                break;
            case SWITCH_OPTION:
                SwitchItemViewHolder switchItemViewHolder = (SwitchItemViewHolder) holder;
                switchItemViewHolder.txt_sw_option_name.setText(menus.get(holder.getAdapterPosition()).getMenu_name());
                switchItemViewHolder.switch_option.setEnable(FastSave.getInstance().getBoolean(PD_Constant.READ_DATA_FROM_DB, false));
                switchItemViewHolder.switch_option.setToggleListener(isEnable -> {
                    Log.d("onBindViewHolder::", isEnable + "z");
                    FastSave.getInstance().saveBoolean(PD_Constant.READ_DATA_FROM_DB, isEnable);
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        return menus.size();
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

    public class SwitchItemViewHolder extends RecyclerView.ViewHolder {
        TextView txt_sw_option_name;
        RevealSwitch switch_option;

        SwitchItemViewHolder(View itemView) {
            super(itemView);
            txt_sw_option_name = itemView.findViewById(R.id.txt_sw_option_name);
            switch_option = itemView.findViewById(R.id.switch_option);
        }
    }
}
