package com.pratham.prathamdigital.ui.admin_statistics;

import android.annotation.SuppressLint;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.models.Modal_NavigationMenu;
import com.pratham.prathamdigital.models.Modal_ResourcePlayedByGroups;
import com.pratham.prathamdigital.models.Modal_TotalDaysGroupsPlayed;
import com.pratham.prathamdigital.ui.fragment_admin_options.ContractOptions;
import com.yarolegovich.discretescrollview.DSVOrientation;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@EFragment(R.layout.fragment_admin_statistics)
public class Fragment_AdminStatistics extends Fragment implements AdminStatContract.StatView, ContractOptions.optionAdapterClick {
    //    @ViewById(R.id.cir_stat_reveal)
//    CircularRevelLayout cir_stat_reveal;
    @ViewById(R.id.txt_active)
    TextView txt_active;
    @ViewById(R.id.rv_stat_group)
    DiscreteScrollView rv_stat_group;
    @ViewById(R.id.rv_daily_stat)
    RecyclerView rv_daily_stat;
    @ViewById(R.id.rl_no_data)
    View rl_no_data;

    @Bean(AdminStatPresenter.class)
    AdminStatContract.StatPresenter statPresenter;
    GroupAdapter groupAdapter;
    GroupResourcesAdapter groupResourcesAdapter;

    @AfterViews
    public void init() {
//        if (getArguments() != null) {
//            int revealX = getArguments().getInt(PD_Constant.REVEALX, 0);
//            int revealY = getArguments().getInt(PD_Constant.REVEALY, 0);
//            cir_stat_reveal.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//                @Override
//                public boolean onPreDraw() {
//                    cir_stat_reveal.getViewTreeObserver().removeOnPreDrawListener(this);
//                    cir_stat_reveal.revealFrom(revealX, revealY, 0);
//                    return true;
//                }
//            });
//        }
        statPresenter.setView(Fragment_AdminStatistics.this);
    }

    @SuppressLint("SetTextI18n")
    @UiThread
    @Override
    public void showDeviceDays(int days) {
        txt_active.setText("This device was active for " + days + " days");
    }

    @Override
    public void showTotalDaysPlayedByGroups(List<Modal_TotalDaysGroupsPlayed> modal_totalDaysGroupsPlayeds) {
        initializeGroupAdapter(modal_totalDaysGroupsPlayeds);
    }

    @UiThread
    public void initializeGroupAdapter(List<Modal_TotalDaysGroupsPlayed> modal_totalDaysGroupsPlayeds) {
        if (groupAdapter == null) {
            groupAdapter = new GroupAdapter(getActivity(), modal_totalDaysGroupsPlayeds, this);
            rv_stat_group.setOrientation(DSVOrientation.VERTICAL);
            rv_stat_group.setAdapter(groupAdapter);
            rv_stat_group.addOnItemChangedListener(new DiscreteScrollView.OnItemChangedListener<RecyclerView.ViewHolder>() {
                @Override
                public void onCurrentItemChanged(@Nullable RecyclerView.ViewHolder viewHolder, int adapterPosition) {
                    statPresenter.getRecourcesPlayedByGroups(groupAdapter.getItems().get(adapterPosition).getGroupID());
                }
            });
            rv_stat_group.setItemTransitionTimeMillis(150);
            rv_stat_group.setItemTransformer(new ScaleTransformer.Builder()
                    .setMinScale(0.9f)
                    .setMaxScale(1.05f)
                    .build());
        } else
            groupAdapter.updateItems(modal_totalDaysGroupsPlayeds);
        if (modal_totalDaysGroupsPlayeds.size() > 0) rv_daily_stat.smoothScrollToPosition(0);
        else rl_no_data.setVisibility(View.VISIBLE);
    }

    @Override
    public void showResourcesPlayedByGroups(HashMap<String, List<Modal_ResourcePlayedByGroups>> modal_resourcePlayedByGroups) {
        initializeResourcesAdapter(modal_resourcePlayedByGroups);
    }

    @UiThread
    public void initializeResourcesAdapter(HashMap<String, List<Modal_ResourcePlayedByGroups>> modal_resourcePlayedByGroups) {
        if (groupResourcesAdapter == null) {
            groupResourcesAdapter = new GroupResourcesAdapter(getActivity(), modal_resourcePlayedByGroups);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            rv_daily_stat.setHasFixedSize(true);
            rv_daily_stat.setLayoutManager(linearLayoutManager);
            rv_daily_stat.setAdapter(groupResourcesAdapter);
        } else
            groupResourcesAdapter.updateData(modal_resourcePlayedByGroups);
        if (modal_resourcePlayedByGroups.size() > 0) rv_daily_stat.smoothScrollToPosition(0);
    }

    @Click(R.id.img_stat_back)
    public void setStatBack() {
        Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStack();
    }

    @Override
    public void menuClicked(int position, Modal_NavigationMenu modal_navigationMenu, View view) {
        rv_stat_group.smoothScrollToPosition(position);
    }
}
