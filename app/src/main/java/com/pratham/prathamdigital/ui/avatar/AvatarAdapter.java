package com.pratham.prathamdigital.ui.avatar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.pratham.prathamdigital.R;

import java.util.ArrayList;

public class AvatarAdapter extends RecyclerView.Adapter<AvatarAdapter.ViewHolder> {

    private final ArrayList<String> datalist;

    public AvatarAdapter(Context context, ArrayList<String> datalist) {
        Context context1 = context;
        this.datalist = datalist;
    }

    @NonNull
    @Override
    public AvatarAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_avatar, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AvatarAdapter.ViewHolder viewHolder, int i) {
        viewHolder.lottie_avatar.setAnimation(datalist.get(i));
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        LottieAnimationView lottie_avatar;

        ViewHolder(View itemView) {
            super(itemView);
            lottie_avatar = itemView.findViewById(R.id.lottie_avatar);
        }
    }
}
