package com.pratham.prathamdigital.ui.fragment_select_group;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.card.MaterialCardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.models.Modal_Groups;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {
    private ArrayList<Modal_Groups> datalist;
    private Context context;
    private ContractGroup contractGroup;

    public GroupAdapter(Context context, ArrayList<Modal_Groups> datalist, ContractGroup contractGroup) {
        this.context = context;
        this.datalist = datalist;
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
        viewHolder.group_name.setText(datalist.get(pos).getGroupName());
//        if (datalist.get(pos).isSelected()) {
//            viewHolder.group_card.setCardBackgroundColor(context.getResources().getColor(R.color.green));
//            viewHolder.group_name.setTextColor(context.getResources().getColor(R.color.white));
//        } else {
//            viewHolder.group_card.setCardBackgroundColor(context.getResources().getColor(R.color.white));
//            viewHolder.group_name.setTextColor(context.getResources().getColor(R.color.black_20));
//        }
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contractGroup.groupItemClicked(viewHolder.itemView, datalist.get(viewHolder.getAdapterPosition()), viewHolder.getAdapterPosition());
            }
        });
    }

    public void updateGroupItems(final ArrayList<Modal_Groups> newGroups) {
        datalist = newGroups;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.group_card)
        MaterialCardView group_card;
        @BindView(R.id.group_name)
        TextView group_name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
