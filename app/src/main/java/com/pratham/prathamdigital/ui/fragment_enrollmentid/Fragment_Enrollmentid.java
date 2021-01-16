package com.pratham.prathamdigital.ui.fragment_enrollmentid;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.async.PD_ApiRequest;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.dbclasses.BackupDatabase;
import com.pratham.prathamdigital.dbclasses.PrathamDatabase;
import com.pratham.prathamdigital.interfaces.ApiResult;
import com.pratham.prathamdigital.models.Attendance;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.models.Modal_Enrollment;
import com.pratham.prathamdigital.models.Modal_Groups;
import com.pratham.prathamdigital.models.Modal_Session;
import com.pratham.prathamdigital.models.Modal_Student;
import com.pratham.prathamdigital.models.Model_CourseEnrollment;
import com.pratham.prathamdigital.services.AppKillService;
import com.pratham.prathamdigital.ui.attendance_activity.AttendanceActivity_;
import com.pratham.prathamdigital.ui.dashboard.ActivityMain_;
import com.pratham.prathamdigital.ui.fragment_select_group.FragmentSelectGroup;
import com.pratham.prathamdigital.ui.fragment_select_group.FragmentSelectGroup_;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

import static com.pratham.prathamdigital.PrathamApplication.attendanceDao;
import static com.pratham.prathamdigital.PrathamApplication.sessionDao;
import static com.pratham.prathamdigital.PrathamApplication.statusDao;
import static com.pratham.prathamdigital.PrathamApplication.studentDao;
import static com.pratham.prathamdigital.util.PD_Constant.SERVER_VILLAGE;

@EFragment(R.layout.fragment_enrollmentid)
public class Fragment_Enrollmentid extends Fragment {

    @ViewById(R.id.et_enrollment_no)
    EditText et_enrollment_id;
    @ViewById(R.id.tv_checkEnrollID)
    TextView tv_checkEnrollID;


    @ViewById(R.id.form_root)
    RelativeLayout from_root;
    @ViewById(R.id.rl_enroll_no_details)
    RelativeLayout rl_enroll_no_details;
    @ViewById(R.id.rl_enroll_no_not_found)
    RelativeLayout rl_enroll_no_not_found;
    @ViewById(R.id.rl_enroll_grp_details)
    RelativeLayout rl_enroll_grp_details;

    @ViewById(R.id.tv_enrolled_student_name)
    TextView tv_enrolled_student_name;
    @ViewById(R.id.tv_enrolled_student_age)
    TextView tv_enrolled_student_age;
    @ViewById(R.id.tv_enrolled_student_class)
    TextView tv_enrolled_student_class;
    @ViewById(R.id.tv_enrolled_student_gender)
    TextView tv_enrolled_student_gender;
    @ViewById(R.id.tv_enrolled_student_grp_id)
    TextView tv_enrolled_student_grp_id;
    @ViewById(R.id.tv_enrolled_student_grp_name)
    TextView tv_enrolled_student_grp_name;
    @ViewById(R.id.tv_enrolled_student_village_name)
    TextView tv_enrolled_student_village_name;

    @ViewById(R.id.tv_enrolled_group_groupName)
    TextView tv_enrolled_group_groupName;
    @ViewById(R.id.lv_enrolled_groupList)
    ListView lv_enrolled_groupList;

    boolean isGroup=false;
    String grpID;


    @Bean(PD_ApiRequest.class)
    PD_ApiRequest pd_apiRequest;
    private final List<Modal_Student> studentList = new ArrayList();
    Modal_Student newEnrolledStudent;


    public Fragment_Enrollmentid() {
        // Required empty public constructor
    }

    @AfterViews
    public void initialize(){
        if(et_enrollment_id.getText().toString().isEmpty()){
            rl_enroll_no_details.setVisibility(View.GONE);
            rl_enroll_no_not_found.setVisibility(View.GONE);
            rl_enroll_grp_details.setVisibility(View.GONE);
        }
//        pd_apiRequest.setApiResult(Fragment_Enrollmentid.this);
    }

    @Click(R.id.tv_checkEnrollID)
    public void checkEnrollID() {
        hideKeyboard(from_root);
        if (!et_enrollment_id.getText().toString().trim().equalsIgnoreCase("")) {
            if (PrathamApplication.wiseF.isDeviceConnectedToMobileOrWifiNetwork()) {
                getStudentByEnrollmentNo(et_enrollment_id.getText().toString().trim());
            } else {
                newEnrolledStudent = PrathamDatabase.getDatabaseInstance(getActivity()).getStudentDao().getStudent(et_enrollment_id.getText().toString().trim());
                setResponse(newEnrolledStudent);
            }
        } else {
            Toast.makeText(getActivity(), "Enter enrollment number", Toast.LENGTH_SHORT).show();
        }
    }

    private void getStudentByEnrollmentNo(String enrollmentID){
        newEnrolledStudent = new Modal_Student();
        String url = PD_Constant.STUDENT_ENROLLMENT_URL + et_enrollment_id.getText().toString() + "&appid=SCAPP";
        Log.e("URL ** : ",url);
        try {
            Gson gson = new Gson();
            ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading..");
            progressDialog.setCancelable(false);
            progressDialog.show();
            AndroidNetworking.get(url)
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            Log.e("URL resp ** : ", String.valueOf(response));
                            Type listType = new TypeToken<Modal_Enrollment>() {
                            }.getType();
                            Modal_Enrollment enrollmentModel = gson.fromJson(response, listType);
                            if (enrollmentModel != null){
                                try {
                                    addStudentData(enrollmentModel);
                                    //setResponse(newEnrolledStudent);
                                    progressDialog.dismiss();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    progressDialog.dismiss();

                                }
                            } else
                                setResponse(newEnrolledStudent);
                            progressDialog.dismiss();
                        }

                        @Override
                        public void onError(ANError anError) {

                        }

                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
        }

    private void addStudentData(Modal_Enrollment enrollmentModel) {
        try {
//            dismissLoadingDialog();
            if (enrollmentModel.getLstCourseEnroll() != null) {
                List<Model_CourseEnrollment> courseEnrollmentList = new ArrayList<>();
                for (int v = 0; v < enrollmentModel.getLstCourseEnroll().size(); v++) {
                    Model_CourseEnrollment model_courseEnrollment = new Model_CourseEnrollment();
                    model_courseEnrollment.setCourseId(enrollmentModel.getLstCourseEnroll().get(v).getCourseId());
                    model_courseEnrollment.setGroupId(enrollmentModel.getLstCourseEnroll().get(v).getGroupId());
                    model_courseEnrollment.setPlanFromDate(enrollmentModel.getLstCourseEnroll().get(v).getPlanFromDate());
                    model_courseEnrollment.setPlanToDate(enrollmentModel.getLstCourseEnroll().get(v).getPlanToDate());
                    model_courseEnrollment.setLanguage(enrollmentModel.getLstCourseEnroll().get(v).getLanguage());
                    courseEnrollmentList.add(model_courseEnrollment);
                }
                PrathamDatabase.getDatabaseInstance(getActivity()).getCourseDao().insertListCourse(courseEnrollmentList);
            }
            if (enrollmentModel.getEnrollmentType().equalsIgnoreCase("Student")) {
                newEnrolledStudent = new Modal_Student();
                newEnrolledStudent.setStudentId(enrollmentModel.getLstStudent().get(0).getStudentId());
                newEnrolledStudent.setFullName(enrollmentModel.getLstStudent().get(0).getFullName());
                newEnrolledStudent.setMiddleName("");
                newEnrolledStudent.setLastName(enrollmentModel.getLstStudent().get(0).getStudentEnrollment());
                newEnrolledStudent.setStud_Class(enrollmentModel.getLstStudent().get(0).getClasss());
                newEnrolledStudent.setAge(enrollmentModel.getLstStudent().get(0).getAge());
                newEnrolledStudent.setGender(enrollmentModel.getLstStudent().get(0).getGender());
                newEnrolledStudent.setGroupId(enrollmentModel.getLstStudent().get(0).getGroupId());
                newEnrolledStudent.setGroupName(enrollmentModel.getLstStudent().get(0).getGroupName());
                rl_enroll_grp_details.setVisibility(View.GONE);
                setResponse(newEnrolledStudent);

                //PrathamDatabase.getDatabaseInstance(getActivity()).getStudentDao().insertStudent(newEnrolledStudent);
            } else {
                isGroup=true;
                Modal_Groups groups = new Modal_Groups();
                groups.setGroupId(enrollmentModel.getGroupId());
                groups.setGroupName(enrollmentModel.getGroupName());
                groups.setVillageId(enrollmentModel.getVillageId());
                groups.setProgramId(enrollmentModel.getProgramId());
                groups.setGroupCode(enrollmentModel.getGroupCode());
                groups.setSchoolName(enrollmentModel.getSchoolName());
                groups.setVIllageName(enrollmentModel.getVIllageName());
                groups.setDeviceId(PD_Utility.getDeviceID());
                grpID=enrollmentModel.getGroupId();
                PrathamDatabase.getDatabaseInstance(getActivity()).getGroupDao().insertGroup(groups);
                List<Modal_Student> studentList = new ArrayList<>();
                List<String> studentListTemp = new ArrayList<>();
                Modal_Student student = null;
                for (int i = 0; i < enrollmentModel.getLstStudent().size(); i++) {
                    student = new Modal_Student();
                    student.setStudentId(enrollmentModel.getLstStudent().get(i).getStudentId());
                    student.setFullName(enrollmentModel.getLstStudent().get(i).getFullName());
                    student.setStud_Class(enrollmentModel.getLstStudent().get(i).getClasss());
                    student.setAge(enrollmentModel.getLstStudent().get(i).getAge());
                    student.setGender(enrollmentModel.getLstStudent().get(i).getGender());
                    student.setGroupId(enrollmentModel.getLstStudent().get(i).getGroupId());
                    student.setGroupName(enrollmentModel.getLstStudent().get(i).getGroupName());
                    student.setAvatarName("avatars/dino_dance.json");
                    //student.setDeviceId(deviceID);
                    studentList.add(student);
                    studentListTemp.add(enrollmentModel.getLstStudent().get(i).getFullName());
                }
                tv_enrolled_group_groupName.setText(enrollmentModel.getGroupName());
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Objects.requireNonNull(getActivity()),android.R.layout.simple_list_item_1, studentListTemp);
                lv_enrolled_groupList.setAdapter(arrayAdapter);
                rl_enroll_no_details.setVisibility(View.GONE);
                rl_enroll_no_not_found.setVisibility(View.GONE);
                rl_enroll_grp_details.setVisibility(View.VISIBLE);
                PrathamDatabase.getDatabaseInstance(getActivity()).getStudentDao().insertAllStudents(studentList);
            }
//            dismissLoadingDialog();
            BackupDatabase.backup(getActivity());
            Toast.makeText(getActivity(), "Profile created Successfully..", Toast.LENGTH_SHORT).show();
//        splashInterface.onChildAdded();
        } catch (Exception e) {
//            dismissLoadingDialog();
  /*          new Handler().postDelayed(() ->
                    btn_back.performClick(), 1000);*/
            e.printStackTrace();
        }
    }
    private void setResponse(Modal_Student student) {
        try {
            if (student != null) {
                if (student.getFullName() != null) {
                    tv_enrolled_student_name.setText(student.getFullName());
                    tv_enrolled_student_age.setText(student.getAge() + "");
                    tv_enrolled_student_class.setText(student.getStud_Class());
                    tv_enrolled_student_gender.setText(student.getGender());
                    tv_enrolled_student_grp_id.setText(student.getGroupId());
                    tv_enrolled_student_grp_name.setText(student.getGroupName());
                    //            tv_enrolled_student_village_id.setText(student.get);

                    rl_enroll_no_details.setVisibility(View.VISIBLE);
                    rl_enroll_no_not_found.setVisibility(View.GONE);
                } else {
                    rl_enroll_no_details.setVisibility(View.GONE);
                    rl_enroll_no_not_found.setVisibility(View.VISIBLE);
                }
            } else {
                if (!PrathamApplication.wiseF.isDeviceConnectedToMobileOrWifiNetwork()) {
//                    Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                    Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
                }
                rl_enroll_no_details.setVisibility(View.GONE);
                rl_enroll_no_not_found.setVisibility(View.VISIBLE);
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    private void hideKeyboard(View view) {
        if (view != null)
            if (getActivity() != null) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
    }


    @Click(R.id.tv_enroll_back)
    public void onBackPress(){
        assert getFragmentManager() != null;
        getFragmentManager().popBackStackImmediate();
    }

    @Click(R.id.btn_saveProfile)
    public void saveStudent(){
        if(isGroup) {

            String groupId1 = statusDao.getValue(PD_Constant.GROUPID1);
            String groupId2 = statusDao.getValue(PD_Constant.GROUPID2);
            String groupId3 = statusDao.getValue(PD_Constant.GROUPID3);
            String groupId4 = statusDao.getValue(PD_Constant.GROUPID4);
            String groupId5 = statusDao.getValue(PD_Constant.GROUPID5);
            if (groupId1 != null && !groupId1.equalsIgnoreCase("0") && !groupId1.equalsIgnoreCase(grpID))
                statusDao.updateValue(PD_Constant.GROUPID1,grpID);
            else if (groupId2 != null && !groupId2.equalsIgnoreCase("0") && !groupId2.equalsIgnoreCase(grpID))
                statusDao.updateValue(PD_Constant.GROUPID2,grpID);
            else if (groupId3 != null && !groupId3.equalsIgnoreCase("0") && !groupId3.equalsIgnoreCase(grpID))
                statusDao.updateValue(PD_Constant.GROUPID3,grpID);
            else if (groupId4 != null && !groupId4.equalsIgnoreCase("0") && !groupId4.equalsIgnoreCase(grpID))
                statusDao.updateValue(PD_Constant.GROUPID4,grpID);
            else if (groupId5 != null && !groupId5.equalsIgnoreCase("0") && !groupId5.equalsIgnoreCase(grpID))
                statusDao.updateValue(PD_Constant.GROUPID5,grpID);
            else {
                statusDao.updateValue(PD_Constant.GROUPID5,grpID);
            }
/*            Intent mActivityIntent = new Intent(getActivity(), AttendanceActivity_.class);
            mActivityIntent.putExtra(PD_Constant.STUDENT_ADDED, true);
            startActivity(mActivityIntent);*/
            PrathamApplication.bubble_mp.start();
            Bundle bundle = new Bundle();
            bundle.putBoolean(PD_Constant.GROUP_ENROLLED, true);
            bundle.putBoolean(PD_Constant.GROUP_AGE_BELOW_7, false);
            bundle.putString(PD_Constant.GROUPID, grpID);
            PD_Utility.addFragment(getActivity(), new FragmentSelectGroup_(), R.id.splash_frame,
                    bundle, FragmentSelectGroup.class.getSimpleName());
        }
//            presentActivity();
        else
            insertStudentAndMarkAttendance();
    }

    private void insertStudentAndMarkAttendance() {
        FastSave.getInstance().saveString(PD_Constant.AVATAR, "avatars/dino_dance.json");
        FastSave.getInstance().saveString(PD_Constant.PROFILE_NAME, tv_enrolled_student_name.getText().toString());
        newEnrolledStudent.setFirstName("");
        newEnrolledStudent.setMiddleName("");
        newEnrolledStudent.setLastName("");
        newEnrolledStudent.setSentFlag(0);
        newEnrolledStudent.setAvatarName("avatars/dino_dance.json");
        Modal_Student student = PrathamDatabase.getDatabaseInstance(getActivity()).getStudentDao().getStudent(newEnrolledStudent.getStudentId());
        if (student != null) {
            Toast.makeText(getActivity(), "Profile is already saved..", Toast.LENGTH_SHORT).show();
        } else {
            studentDao.insertStudent(newEnrolledStudent);
            BackupDatabase.backup(getActivity());
            Toast.makeText(getActivity(), "Profile created Successfully..", Toast.LENGTH_SHORT).show();
        }
        FastSave.getInstance().saveString(PD_Constant.GROUPID, newEnrolledStudent.getStudentId());
        FastSave.getInstance().saveString(PD_Constant.SESSIONID, PD_Utility.getUUID().toString());
        markAttendance(newEnrolledStudent);
        presentActivity();
    }

    @UiThread
    public void presentActivity() {
        Objects.requireNonNull(getActivity()).startService(new Intent(getActivity(), AppKillService.class));
        FastSave.getInstance().saveBoolean(PD_Constant.STORAGE_ASKED, false);
        Intent mActivityIntent = new Intent(getActivity(), ActivityMain_.class);
        if (getArguments() != null && getArguments().getBoolean(PD_Constant.DEEP_LINK, false)) {
            mActivityIntent.putExtra(PD_Constant.DEEP_LINK, true);
            mActivityIntent.putExtra(PD_Constant.DEEP_LINK_CONTENT, getArguments().getString(PD_Constant.DEEP_LINK_CONTENT));
        }
/*        if (doesArgumentContainsNotificationData()) {
            mActivityIntent.putExtra(PD_Constant.PUSH_NOTI_KEY, noti_key);
            mActivityIntent.putExtra(PD_Constant.PUSH_NOTI_VALUE, noti_value);
        }*/
        startActivity(mActivityIntent);
        getActivity().overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
        getActivity().finishAfterTransition();
    }

    private void markAttendance(Modal_Student stud) {
        //<editor-fold desc="below code is for saving the student attendance so as to pass it to "meri dukan" game, nothing else">
        ArrayList<Modal_Student> stuList = new ArrayList<>();
        stuList.add(stud);
        String stu_json = new Gson().toJson(stuList);
        FastSave.getInstance().saveString(PD_Constant.PRESENT_STUDENTS, stu_json);
        //</editor-fold>
        ArrayList<Attendance> attendances = new ArrayList<>();
        Attendance attendance = new Attendance();
        attendance.SessionID = FastSave.getInstance().getString(PD_Constant.SESSIONID, "");
        attendance.StudentID = stud.getStudentId();
        attendance.Date = PD_Utility.getCurrentDateTime();
        attendance.GroupID = "SmartPhone";
        attendance.sentFlag = 0;
        attendances.add(attendance);
        attendanceDao.insertAttendance(attendances);
        Modal_Session s = new Modal_Session();
        s.setSessionID(FastSave.getInstance().getString(PD_Constant.SESSIONID, ""));
        s.setFromDate(PD_Utility.getCurrentDateTime());
        s.setToDate("NA");
        sessionDao.insert(s);
    }

}
