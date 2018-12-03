package com.pratham.prathamdigital.ui.import_db;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.card.MaterialCardView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.CircularRevelLayout;
import com.pratham.prathamdigital.util.PD_Constant;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Fragment_ImportData extends Fragment implements CircularRevelLayout.CallBacks {
    @BindView(R.id.circular_import_reveal)
    CircularRevelLayout circular_import_reveal;
    @BindView(R.id.internal_card)
    MaterialCardView internal_card;
    @BindView(R.id.external_card)
    MaterialCardView external_card;
    private int revealX;
    private int revealY;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_device_specification, container, false);
        ButterKnife.bind(this, rootView);
        circular_import_reveal.setListener(this);
        if (getArguments() != null) {
            revealX = getArguments().getInt(PD_Constant.REVEALX, 0);
            revealY = getArguments().getInt(PD_Constant.REVEALY, 0);
            circular_import_reveal.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    circular_import_reveal.getViewTreeObserver().removeOnPreDrawListener(this);
                    circular_import_reveal.revealFrom(revealX, revealY, 0);
                    return true;
                }
            });
        }
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onRevealed() {

    }

    @Override
    public void onUnRevealed() {

    }
}
