package com.pratham.prathamdigital.ui.fragment_select_group;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.models.Modal_Groups;
import com.pratham.prathamdigital.ui.fragment_child_attendance.ChildAdapter;
import com.pratham.prathamdigital.util.PD_Constant;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FragmentSelectGroup extends Fragment {

    @BindView(R.id.rv_group)
    RecyclerView rv_group;

    ChildAdapter childAdapter;
    ArrayList<Modal_Groups> students;
    private int revealX;
    private int revealY;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_select_group, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void setChilds(ArrayList<Modal_Groups> groups) {
//        if (childAdapter == null) {
//            childAdapter = new ChildAdapter(getActivity(), childs, avatars, FragmentSelectGroup.this);
//            rv_child.setHasFixedSize(true);
//            rv_child.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
//            rv_child.setAdapter(childAdapter);
//        } else {
//            childAdapter.updateChildItems(childs);
//        }
    }


//    @OnTouch(R.id.btn_attendance_next)
//    public boolean setNextAvatar(View view, MotionEvent event) {
//        revealX = (int) event.getRawX();
//        revealY = (int) event.getY();
//        return getActivity().onTouchEvent(event);
//    }

    @OnClick(R.id.btn_group_next)
    public void setNext(View v) {
        PrathamApplication.bubble_mp.start();
        FastSave.getInstance().saveString(PD_Constant.AVATAR, "avatars/dino_dance.json");
    }

//    public void presentActivity(View view) {
//        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) getActivity(), view, "transition");
//        Intent intent = new Intent(getActivity(), ActivityMain.class);
//        intent.putExtra(ActivityMain.EXTRA_CIRCULAR_REVEAL_X, revealX);
//        intent.putExtra(ActivityMain.EXTRA_CIRCULAR_REVEAL_Y, revealY);
//        ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
//    }

}
