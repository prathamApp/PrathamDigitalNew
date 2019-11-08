package com.pratham.prathamdigital.ui.fragment_select_group;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.card.MaterialCardView;
import android.support.v7.recyclerview.extensions.AsyncListDiffer;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.models.Modal_Groups;

import java.util.List;
import java.util.Objects;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {
    private final Context context;
    private final ContractGroup contractGroup;
    private final AsyncListDiffer<Modal_Groups> mDiffer;

    public GroupAdapter(Context context, ContractGroup contractGroup) {
        DiffUtil.ItemCallback<Modal_Groups> diffcallback = new DiffUtil.ItemCallback<Modal_Groups>() {
            @Override
            public boolean areItemsTheSame(@NonNull Modal_Groups detail, @NonNull Modal_Groups t1) {
                return Objects.equals(detail.getGroupId(), t1.getGroupId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull Modal_Groups detail, @NonNull Modal_Groups t1) {
                int result = detail.compareTo(t1);
                return result == 0;
            }
        };
        mDiffer = new AsyncListDiffer<>(this, diffcallback);
        this.context = context;
        this.contractGroup = contractGroup;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_group, parent, false);
        return new GroupAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int pos) {
        pos = viewHolder.getAdapterPosition();
        viewHolder.group_name.setText(mDiffer.getCurrentList().get(pos).getGroupName());
        if (mDiffer.getCurrentList().get(pos).isSelected()) {
            viewHolder.group_card.setCardBackgroundColor(context.getResources().getColor(R.color.att_selected));
            viewHolder.img_grp_selected.setVisibility(View.VISIBLE);
        } else {
            viewHolder.group_card.setCardBackgroundColor(context.getResources().getColor(R.color.att_unselected));
            viewHolder.img_grp_selected.setVisibility(View.GONE);
        }
        viewHolder.itemView.setOnClickListener(v -> contractGroup.groupItemClicked(viewHolder.itemView,
                mDiffer.getCurrentList().get(viewHolder.getAdapterPosition()), viewHolder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return mDiffer.getCurrentList().size();
    }

    public void submitList(List<Modal_Groups> new_groups) {
        mDiffer.submitList(new_groups);
    }

    public List<Modal_Groups> getList() {
        return mDiffer.getCurrentList();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView group_card;
        TextView group_name;
        ImageView img_grp_selected;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            group_card = itemView.findViewById(R.id.group_card);
            group_name = itemView.findViewById(R.id.group_name);
            img_grp_selected = itemView.findViewById(R.id.img_grp_selected);
        }
    }
}
