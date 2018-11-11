package com.pratham.prathamdigital.ui.fragment_age_group;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.pratham.prathamdigital.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FragmentSelectAgeGroup extends Fragment {

    @BindView(R.id.iv_age_3_to_6)
    ImageView iv_age_3_to_6;
    @BindView(R.id.iv_age_8_to_14)
    ImageView iv_age_8_to_14;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_age_group, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }
}
