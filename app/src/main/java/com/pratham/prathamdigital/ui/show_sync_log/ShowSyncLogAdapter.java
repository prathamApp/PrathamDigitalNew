package com.pratham.prathamdigital.ui.show_sync_log;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.models.Modal_Log;
import com.pratham.prathamdigital.models.Model_NewSyncLog;
import com.pratham.prathamdigital.models.SyncLogDataModel;
import com.pratham.prathamdigital.util.PD_Constant;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ShowSyncLogAdapter extends RecyclerView.Adapter<ShowSyncLogAdapter.MyViewHolder> {
    List<Model_NewSyncLog> showSyncLogList;
    Context context;
    SyncLogDataModel syncLogDataModel;
    CheckSyncDetailsClick checkSyncDetailsClick;
    //private final ReplaceTabItemClick replaceTabItemClick;

    public ShowSyncLogAdapter(Context context, List<Model_NewSyncLog> showSyncLogList, CheckSyncDetailsClick checkSyncDetailsClick){//}, ReplaceTabItemClick replaceTabItemClick) {
        this.showSyncLogList=showSyncLogList;
        this.context=context;
        this.checkSyncDetailsClick = checkSyncDetailsClick;
//        this.replaceTabItemClick = replaceTabItemClick;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sync_datalog_new, parent, false);
        return new MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tv_pushDate.setText(showSyncLogList.get(position).getPushDate());
        holder.tv_fileId.setText(showSyncLogList.get(position).getUuid());

        holder.tv_pushType.setText(showSyncLogList.get(position).getPushType());
        holder.tv_pushId.setText(String.valueOf(showSyncLogList.get(position).getPushId()));
        holder.tv_status.setText(showSyncLogList.get(position).getStatus());

        if(showSyncLogList.get(position).getPushType().equalsIgnoreCase(PD_Constant.DB_PUSH)){
            holder.tv_courseCount.setVisibility(View.GONE);
            holder.tv_pushId.setVisibility(View.GONE);
            holder.tv_pushType.setVisibility(View.GONE);
        } else {
            holder.tv_courseCount.setVisibility(View.VISIBLE);
            holder.tv_pushId.setVisibility(View.VISIBLE);
            holder.tv_pushType.setVisibility(View.VISIBLE);
        }

        if(showSyncLogList.get(position).getStatus().equalsIgnoreCase(PD_Constant.PENDING)){
            holder.img_status.setImageResource(R.drawable.ic_pending_40px);
            holder.tv_status.setTextColor(context.getResources().getColor(R.color.light_orange));
        } else if (showSyncLogList.get(position).getStatus().contains(PD_Constant.SUCCESS) ||
                showSyncLogList.get(position).getStatus().equalsIgnoreCase(PD_Constant.COMPLETED)) {
            holder.img_status.setImageResource(R.drawable.ic_ok);
            holder.tv_status.setTextColor(context.getResources().getColor(R.color.light_green));
        } else {
            holder.img_status.setImageResource(R.drawable.ic_cancel);
            holder.tv_status.setTextColor(context.getResources().getColor(R.color.red));
        }

        holder.tv_courseCount.setOnClickListener(view -> {
            checkSyncDetailsClick.checkSyncDetails(showSyncLogList.get(position).getPushId());
        });

/*        if(showSyncLogList.get(position).getPushType()!=null && showSyncLogList.get(position).getPushType().contains("success")) {
            holder.tv_pushType.setTextColor(context.getResources().getColor(R.color.colorBtnGreenDark));
            holder.ll_success.setVisibility(View.VISIBLE);
            holder.ll_fail.setVisibility(View.GONE);

            if(showSyncLogList.get(position).getExceptionMessage().equalsIgnoreCase("DB_Sync")){
                holder.tv_courseCount.setVisibility(View.GONE);
                holder.tv_fileId.setVisibility(View.GONE);
                holder.tv_pushId.setVisibility(View.GONE);
            }
            else holder.tv_courseCount.setVisibility(View.VISIBLE);

            try {
                Log.e("ddddd : ",showSyncLogList.get(position).getLogDetail());
                JSONObject jsonObj = new JSONObject(showSyncLogList.get(position).getLogDetail());
                Gson gson = new Gson();
                syncLogDataModel = gson.fromJson(jsonObj.toString(), SyncLogDataModel.class);
                if(syncLogDataModel!=null){
                    //holder.tv_courseCount.setText("Course Synced : "+syncLogDataModel.getCoursesCount());
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        else {
            holder.tv_pushType.setTextColor(context.getResources().getColor(R.color.red));
            holder.ll_fail.setVisibility(View.VISIBLE);
            holder.ll_success.setVisibility(View.GONE);
            holder.tv_courseCount.setVisibility(View.GONE);
            if(showSyncLogList.get(position).getExceptionMessage().equalsIgnoreCase("DB_Sync")){
                holder.tv_courseCount.setVisibility(View.GONE);
                holder.tv_fileId.setVisibility(View.GONE);
                holder.tv_pushId.setVisibility(View.GONE);
            }
        }

        holder.tv_courseCount.setOnClickListener(view -> {
            checkSyncDetailsClick.checkSyncDetails();
        });

/*        if(deviseList.get(position).getStatus()!=null && deviseList.get(position).getStatus().contains("Pending"))
            holder.serialID.setTextColor(context.getResources().getColor(R.color.red));
        else holder.serialID.setTextColor(context.getResources().getColor(R.color.grey_800));

        holder.cv_tablet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(deviseList.get(position).getStatus()!=null && deviseList.get(position).getStatus().contains("Pending"))
                    Toast.makeText(context, "Request Already Sent.", Toast.LENGTH_SHORT).show();
                else replaceTabItemClick.onTabItemClicked(position, deviseList.get(position));
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return showSyncLogList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_pushDate;
        TextView tv_pushType;
        TextView tv_courseCount;
        TextView tv_fileId;
        TextView tv_pushId;
        LinearLayout ll_success;
        ImageView img_status;
        TextView tv_status;
        //LinearLayout ll_fail;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_pushDate = itemView.findViewById(R.id.tv_pushDate);
            tv_pushType = itemView.findViewById(R.id.tv_pushType);
            tv_courseCount = itemView.findViewById(R.id.tv_courseCount);
            tv_fileId = itemView.findViewById(R.id.tv_fileId);
            tv_pushId = itemView.findViewById(R.id.tv_pushId);
            ll_success = itemView.findViewById(R.id.ll_success);
            img_status = itemView.findViewById(R.id.iv_status);
            tv_status = itemView.findViewById(R.id.tv_status);
            //ll_fail = itemView.findViewById(R.id.ll_fail);
        }
    }

    public void filterList(ArrayList<Model_NewSyncLog> filterdNames) {
        this.showSyncLogList=filterdNames;
        notifyDataSetChanged();
    }
}
