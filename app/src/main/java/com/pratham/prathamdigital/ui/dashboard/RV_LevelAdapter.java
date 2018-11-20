package com.pratham.prathamdigital.ui.dashboard;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.models.Modal_ContentDetail;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by HP on 01-08-2017.
 */

public class RV_LevelAdapter extends RecyclerView.Adapter {

    public static final int LAST_ITEM = 1;
    public static final int NORMAL_ITEM = 2;
    private Context context;
    private ArrayList<Modal_ContentDetail> levels;

    public RV_LevelAdapter(Context context, ArrayList<Modal_ContentDetail> levels) {
        this.context = context;
        this.levels = levels;
    }

    @Override
    public int getItemViewType(int position) {
        position += 1;
        if (position == levels.size())
            return LAST_ITEM;
        else return NORMAL_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView;
        switch (viewType) {
            case LAST_ITEM:
                LayoutInflater last = LayoutInflater.from(viewGroup.getContext());
                itemView = last.inflate(R.layout.item_level_last_state, viewGroup, false);
                return new LastItemViewHolder(itemView);
            case NORMAL_ITEM:
                LayoutInflater normal = LayoutInflater.from(viewGroup.getContext());
                itemView = normal.inflate(R.layout.item_level_normal_state, viewGroup, false);
                return new NormalItemViewHolder(itemView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        switch (viewHolder.getItemViewType()) {
            case NORMAL_ITEM:
                NormalItemViewHolder holder = (NormalItemViewHolder) viewHolder;
                holder.l_name.setText(levels.get(viewHolder.getAdapterPosition()).getNodetitle());
                break;
            case LAST_ITEM:
                LastItemViewHolder last = (LastItemViewHolder) viewHolder;
                last.last_level_name.setText(levels.get(viewHolder.getAdapterPosition()).getNodetitle());
                break;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            switch (holder.getItemViewType()) {
                case NORMAL_ITEM:
                    NormalItemViewHolder normalItemViewHolder = (NormalItemViewHolder) holder;
                    ((NormalItemViewHolder) holder).l_name.setText(levels.get(holder.getAdapterPosition()).getNodetitle());
                    break;
                case LAST_ITEM:
                    LastItemViewHolder last = (LastItemViewHolder) holder;
                    last.last_level_name.setText(levels.get(holder.getAdapterPosition()).getNodetitle());
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return levels.size();
    }

    public class NormalItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.level_name)
        TextView l_name;

        public NormalItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class LastItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.last_level_name)
        TextView last_level_name;

        public LastItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
