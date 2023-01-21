package com.pratham.prathamdigital.ui.fragment_select_group;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.BlurPopupDialog.BlurPopupWindow;
import com.pratham.prathamdigital.custom.CircularRevelLayout;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.models.Modal_Groups;
import com.pratham.prathamdigital.models.Modal_Student;
import com.pratham.prathamdigital.services.AppKillService;
import com.pratham.prathamdigital.ui.avatar.Fragment_SelectAvatar;
import com.pratham.prathamdigital.ui.avatar.Fragment_SelectAvatar_;
import com.pratham.prathamdigital.ui.dashboard.ActivityMain_;
import com.pratham.prathamdigital.ui.fragment_child_attendance.FragmentChildAttendance;
import com.pratham.prathamdigital.ui.fragment_child_attendance.FragmentChildAttendance_;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.pratham.prathamdigital.PrathamApplication.groupDao;
import static com.pratham.prathamdigital.PrathamApplication.statusDao;
import static com.pratham.prathamdigital.PrathamApplication.studentDao;

@EFragment(R.layout.fragment_select_group)
public class FragmentSelectGroup extends Fragment implements ContractGroup, CircularRevelLayout.CallBacks {

    @ViewById(R.id.rv_group)
    RecyclerView rv_group;
    @ViewById(R.id.circular_group_reveal)
    CircularRevelLayout circular_group_reveal;
    @ViewById(R.id.btn_group_next)
    Button btn_group_next;
    @ViewById(R.id.img_att_back)
    ImageView img_Att_back;

    private GroupAdapter groupAdapter;
    private int revealX;
    private int revealY;
    private BlurPopupWindow noGrpDialog;


    @AfterViews
    public void initialize() {
        circular_group_reveal.setListener(this);
        if (getArguments() != null) {
            revealX = getArguments().getInt(PD_Constant.REVEALX, 0);
            revealY = getArguments().getInt(PD_Constant.REVEALY, 0);
            circular_group_reveal.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    circular_group_reveal.getViewTreeObserver().removeOnPreDrawListener(this);
                    circular_group_reveal.revealFrom(revealX, revealY, 0);
                    return true;
                }
            });
            if (getArguments() != null && getArguments().getBoolean(PD_Constant.SHOW_BACK))
                img_Att_back.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initiaiteParametersInGroup();
    }

    @Background
    public void initiaiteParametersInGroup() {
        ArrayList<String> present_groups = new ArrayList<>();
        String groupId1 = statusDao.getValue(PD_Constant.GROUPID1);
        if (groupId1 != null && !groupId1.equalsIgnoreCase("0")) present_groups.add(groupId1);
        String groupId2 = statusDao.getValue(PD_Constant.GROUPID2);
        if (groupId2 != null && !groupId2.equalsIgnoreCase("0")) present_groups.add(groupId2);
        String groupId3 = statusDao.getValue(PD_Constant.GROUPID3);
        if (groupId3 != null && !groupId3.equalsIgnoreCase("0")) present_groups.add(groupId3);
        String groupId4 = statusDao.getValue(PD_Constant.GROUPID4);
        if (groupId4 != null && !groupId4.equalsIgnoreCase("0")) present_groups.add(groupId4);
        String groupId5 = statusDao.getValue(PD_Constant.GROUPID5);
        if (groupId5 != null && !groupId5.equalsIgnoreCase("0")) present_groups.add(groupId5);
        List<Modal_Groups> groups;
        if (Objects.requireNonNull(getArguments()).getBoolean(PD_Constant.GROUP_AGE_BELOW_7)) {
            groups = get3to6Groups(present_groups);
        } else {
            groups = get8to14Groups(present_groups);
        }
        if (groups != null && groups.size() > 0) {
            setGroups(groups);
        } else {
            if ((Objects.requireNonNull(getArguments()).getBoolean(PD_Constant.GROUP_ENROLLED))) {
                showNoGroupsDialog();
            }
        }
    }

    @UiThread
    public void showNoGroupsDialog() {
        noGrpDialog = new BlurPopupWindow.Builder(getActivity())
                .setContentView(R.layout.no_grp_dialog)
                .bindClickListener(v -> {
                    noGrpDialog.dismiss();
                    Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStack();
                }, R.id.dialog_no_grp_btn_exit)
                .setGravity(Gravity.CENTER)
                .setDismissOnTouchBackground(false)
                .setDismissOnClickBack(false)
                .setScaleRatio(0.2f)
                .setBlurRadius(10)
                .setTintColor(0x30000000)
                .build();
        noGrpDialog.show();
    }

    @UiThread
    public void showNoGroupsExitDialog() {
        noGrpDialog = new BlurPopupWindow.Builder(getActivity())
                .setContentView(R.layout.no_grp_dialog)
                .bindClickListener(v -> {
                    noGrpDialog.dismiss();
                    Objects.requireNonNull(getActivity()).finish();
                }, R.id.dialog_no_grp_btn_exit)
                .setGravity(Gravity.CENTER)
                .setDismissOnTouchBackground(false)
                .setDismissOnClickBack(false)
                .setScaleRatio(0.2f)
                .setBlurRadius(10)
                .setTintColor(0x30000000)
                .build();
        noGrpDialog.show();
    }

    private List<Modal_Groups> get3to6Groups(ArrayList<String> allGroups) {
        List<Modal_Groups> groups = new ArrayList<>();
        Modal_Groups group;
        boolean grpFound;
        for (String grID : allGroups) {
            grpFound = false;
            ArrayList<Modal_Student> students = (ArrayList<Modal_Student>) studentDao.getGroupwiseStudents(grID);
            for (Modal_Student stu : students) {
                if (Integer.parseInt(stu.getAge()) < 7) {
                    grpFound = true;
                }
            }
            if (grpFound) {
                group = groupDao.getGroupByGrpID(grID);
                groups.add(group);
            }
        }
        return groups;
    }

    private List<Modal_Groups> get8to14Groups(ArrayList<String> allGroups) {
        List<Modal_Groups> groups = new ArrayList<>();
        Modal_Groups group;
        boolean grpFound;
        for (String grID : allGroups) {
            grpFound = false;
            ArrayList<Modal_Student> students = (ArrayList<Modal_Student>) studentDao.getGroupwiseStudents(grID);
            for (Modal_Student stu : students) {
                if (Integer.parseInt(stu.getAge()) >= 7) {
                    grpFound = true;
                }
            }
            if (grpFound) {
                group = groupDao.getGroupByGrpID(grID);
                groups.add(group);
            }
        }

        //todo
        String GID = "%SmartPhone%";
        List<Modal_Student> studentsSmartPhone = studentDao.getGroupwiseStudentsLike(GID);
         if(studentsSmartPhone.size()!=0) {
              for (Modal_Student stu : studentsSmartPhone) {
                    Modal_Groups groups1 = new Modal_Groups();
                    groups1.setGroupId(stu.getStudentId()+"_SmartPhone");
                    groups1.setGroupName(stu.getFullName());
                    groups1.setVillageId(stu.getStudentId());
                    groups1.setProgramId(0);
                    groups1.setGroupCode("");
                    groups1.setSchoolName("");
                    groups1.setDeviceId(PD_Utility.getDeviceID());
                    Log.e("URL : ", String.valueOf(groups1));
                    groups.add(groups1);
                }
            }
        return groups;
    }

    @UiThread
    public void setGroups(List<Modal_Groups> groups) {
        groupAdapter = new GroupAdapter(getActivity()/*, groups*/, FragmentSelectGroup.this);
        rv_group.setHasFixedSize(true);
        rv_group.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rv_group.setAdapter(groupAdapter);
        groupAdapter.submitList(groups);
    }

    @Click(R.id.btn_group_next)
    public void setSelectedGroup() {
        Modal_Groups tempGrp = new Modal_Groups();
        for (Modal_Groups grp : groupAdapter.getList()) {
            if (grp.isSelected()) {
                tempGrp = grp;
                break;
            } else tempGrp = null;
        }
        if (tempGrp != null)
            setNext(btn_group_next, tempGrp);
        else Toast.makeText(getActivity(), R.string.select_grp_or_stud, Toast.LENGTH_SHORT).show();
    }

    @Click(R.id.img_att_back)
    public void setAttBack() {
        try {
            //Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStack();
            Objects.requireNonNull(getActivity()).onBackPressed();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Click(R.id.iv_addProfile)
    public void addNewProfile() {
        Bundle bundle = new Bundle();
        bundle.putInt(PD_Constant.REVEALX, 0);
        bundle.putInt(PD_Constant.REVEALY, 0);
        bundle.putBoolean(PD_Constant.SHOW_BACK, true);
        PD_Utility.showFragment(getActivity(), new Fragment_SelectAvatar_(), R.id.frame_attendance,
                bundle, Fragment_SelectAvatar.class.getSimpleName());
    }

    @UiThread
    public void setNext(View v, Modal_Groups modal_groups) {
        if(modal_groups.getGroupId().contains("SmartPhone")){
            PrathamApplication.bubble_mp.start();
            String individualId = modal_groups.GroupId.split("_SmartPhone")[0];
            Log.e("tempGrp : ", individualId);
            ArrayList<Modal_Student> students = new ArrayList<>(studentDao.getAllStudent(individualId));
            Log.e("tempStud : ", students.toString());
            int[] outLocation = new int[2];
            v.getLocationOnScreen(outLocation);
            outLocation[0] += v.getWidth() / 2;
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(PD_Constant.STUDENT_LIST, students);
            bundle.putString(PD_Constant.GROUP_NAME, modal_groups.getGroupName());
            bundle.putString(PD_Constant.GROUPID, modal_groups.getGroupId());
            bundle.putInt(PD_Constant.REVEALX, outLocation[0]);
            bundle.putInt(PD_Constant.REVEALY, outLocation[1]);
            bundle.putBoolean("ISTABMODE", false);
            FastSave.getInstance().saveString(PD_Constant.ENROL_TYPE,"INDIVIDUAL");
            if ((Objects.requireNonNull(getArguments()).getBoolean(PD_Constant.GROUP_ENROLLED))) {
                bundle.putBoolean(PD_Constant.GROUP_ENROLLED, true);
                PD_Utility.addFragment(getActivity(), new FragmentChildAttendance_(), R.id.splash_frame,
                        bundle, FragmentChildAttendance.class.getSimpleName());
            } else {
                PD_Utility.addFragment(getActivity(), new FragmentChildAttendance_(), R.id.frame_attendance,
                        bundle, FragmentChildAttendance.class.getSimpleName());
            }
            //presentActivity();
        } else {
            PrathamApplication.bubble_mp.start();
            ArrayList<Modal_Student> students = new ArrayList<>(studentDao.getGroupwiseStudents(modal_groups.getGroupId()));
            int[] outLocation = new int[2];
            v.getLocationOnScreen(outLocation);
            outLocation[0] += v.getWidth() / 2;
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(PD_Constant.STUDENT_LIST, students);
            bundle.putString(PD_Constant.GROUP_NAME, modal_groups.getGroupName());
            bundle.putString(PD_Constant.GROUPID, modal_groups.getGroupId());
            bundle.putInt(PD_Constant.REVEALX, outLocation[0]);
            bundle.putInt(PD_Constant.REVEALY, outLocation[1]);
            FastSave.getInstance().saveString(PD_Constant.ENROL_TYPE,"GROUP");
     /*   PD_Utility.addFragment(getActivity(), new FragmentChildAttendance_(), R.id.frame_attendance,
                bundle, FragmentChildAttendance.class.getSimpleName());*/
            if ((Objects.requireNonNull(getArguments()).getBoolean(PD_Constant.GROUP_ENROLLED))) {
                bundle.putBoolean(PD_Constant.GROUP_ENROLLED, true);
                bundle.putBoolean("ISTABMODE", true);
                PD_Utility.addFragment(getActivity(), new FragmentChildAttendance_(), R.id.splash_frame,
                        bundle, FragmentChildAttendance.class.getSimpleName());
            } else {
                bundle.putBoolean("ISTABMODE", true);
                PD_Utility.addFragment(getActivity(), new FragmentChildAttendance_(), R.id.frame_attendance,
                        bundle, FragmentChildAttendance.class.getSimpleName());
            }
        }
    }

    @Override
    public void groupItemClicked(View v, Modal_Groups modalGroup, int position) {
        PrathamApplication.bubble_mp.start();
        for (Modal_Groups grp : groupAdapter.getList()) {
            if (grp.getGroupId().equalsIgnoreCase(modalGroup.getGroupId()))
                grp.setSelected(true);
            else
                grp.setSelected(false);
        }
//        temp.set(position, modalGroup);
        groupAdapter.notifyDataSetChanged();
    }

    @UiThread
    public void presentActivity() {
        Objects.requireNonNull(getActivity()).startService(new Intent(getActivity(), AppKillService.class));
        FastSave.getInstance().saveBoolean(PD_Constant.STORAGE_ASKED, false);
        Intent mActivityIntent = new Intent(getActivity(), ActivityMain_.class);
        if (Objects.requireNonNull(getArguments()).getBoolean(PD_Constant.DEEP_LINK, false)) {
            mActivityIntent.putExtra(PD_Constant.DEEP_LINK, true);
            mActivityIntent.putExtra(PD_Constant.DEEP_LINK_CONTENT, getArguments().getString(PD_Constant.DEEP_LINK_CONTENT));
        }
        startActivity(mActivityIntent);
        getActivity().overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
        getActivity().finishAfterTransition();
    }

    @Override
    public void onRevealed() {

    }

    @Override
    public void onUnRevealed() {
    }
}
