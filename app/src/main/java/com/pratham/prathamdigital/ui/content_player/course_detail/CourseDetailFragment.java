package com.pratham.prathamdigital.ui.content_player.course_detail;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.archit.calendardaterangepicker.customviews.DateRangeCalendarView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.liulishuo.okdownload.DownloadTask;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.async.PD_ApiRequest;
import com.pratham.prathamdigital.async.ZipDownloader;
import com.pratham.prathamdigital.custom.BlurPopupDialog.BlurPopupWindow;
import com.pratham.prathamdigital.custom.flexbox.FlexDirection;
import com.pratham.prathamdigital.custom.flexbox.FlexboxLayoutManager;
import com.pratham.prathamdigital.custom.flexbox.JustifyContent;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.custom.view_animators.Animate;
import com.pratham.prathamdigital.custom.view_animators.Techniques;
import com.pratham.prathamdigital.dbclasses.BackupDatabase;
import com.pratham.prathamdigital.interfaces.ApiResult;
import com.pratham.prathamdigital.models.Attendance;
import com.pratham.prathamdigital.models.EventMessage;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.models.Modal_DownloadContent;
import com.pratham.prathamdigital.models.Modal_FileDownloading;
import com.pratham.prathamdigital.models.Modal_Log;
import com.pratham.prathamdigital.models.Modal_Student;
import com.pratham.prathamdigital.models.Model_CourseEnrollment;
import com.pratham.prathamdigital.models.Model_CourseExperience;
import com.pratham.prathamdigital.ui.content_player.ContentPlayerContract;
import com.pratham.prathamdigital.ui.dashboard.ActivityMain;
import com.pratham.prathamdigital.ui.dashboard.ActivityMain_;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;
import com.pratham.prathamdigital.view_holders.CourseChildViewHolder;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.pratham.prathamdigital.PrathamApplication.attendanceDao;
import static com.pratham.prathamdigital.PrathamApplication.logDao;
import static com.pratham.prathamdigital.PrathamApplication.modalContentDao;
import static com.pratham.prathamdigital.PrathamApplication.sessionDao;
import static com.pratham.prathamdigital.PrathamApplication.studentDao;
import static com.pratham.prathamdigital.PrathamApplication.villageDao;
import static com.pratham.prathamdigital.custom.file_picker.FilePickerActivity.TAG;

@SuppressLint("NonConstantResourceId")
@EFragment(R.layout.fragment_course_detail)
public class CourseDetailFragment extends Fragment implements ContentPlayerContract.courseDetailAdapterClick, ApiResult {

    private static final int REQUEST_CODE_ASSESSMENT_BACK = 001;
    @ViewById(R.id.rv_course_childs)
    RecyclerView rv_course_childs;
    @ViewById(R.id.course_name)
    TextView course_name;
    @ViewById(R.id.course_detail)
    TextView course_detail;
    @ViewById(R.id.btn_submit_assignment)
    Button btn_submit_assignment;
    @ViewById(R.id.play_all_content_serially)
    Button btn_playAll;
    @ViewById(R.id.download_all_content_serially)
    Button btn_downloadAll;
    @ViewById(R.id.tv_enroll_course)
    TextView tv_enroll_course;
    @ViewById(R.id.tv_goto_course)
    TextView tv_goto_course;
    @ViewById(R.id.rg_assessment)
    RadioGroup rg_assessment;

    CourseDetailAdapter adapter;
    CourseChildViewHolder courseChildViewHolder;
    private Model_CourseEnrollment enrollment;
    private List<Modal_ContentDetail> childs;
    private List<Modal_ContentDetail> childs1;
    private List<Modal_ContentDetail> dispchilds = new ArrayList<>();
    private List<Modal_ContentDetail> assessmentNode = new ArrayList<>();

    private final Map<String, Integer> filesDownloading = new HashMap<>();
    private final Map<String, Modal_FileDownloading> filesContentDownloading = new HashMap<>();
    private final Map<String, DownloadTask> currentDownloadTasks = new HashMap<>();
    private int revealX;
    private int revealY;
    private BlurPopupWindow download_builder;
    ArrayList<Modal_ContentDetail> levelContents = new ArrayList<>();

    @Bean(PD_ApiRequest.class)
    PD_ApiRequest pd_apiRequest;

    @Bean(ZipDownloader.class)
    ZipDownloader zipDownloader;

    boolean assessmentToastFlag = false;

    /**Below fields added to enroll course directly from seekho section*/
    Modal_ContentDetail mcd;
    private HashMap<String, List<Model_CourseEnrollment>> coursesPerWeek = new HashMap<>();
    boolean isCourseAlreadyEnrolled = false;

    //calender fields
    private Calendar startDate;
    private Calendar endDate;
    @ViewById(R.id.rl_calendar_view)
    RelativeLayout rl_calendar_view;
    @ViewById(R.id.course_date_picker)
    DateRangeCalendarView course_date_picker;

    private static final int SHOW_DATE_PICKER = 1;
    private static final int HIDE_DATE_PICKER = 2;

    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_DATE_PICKER:
                    Animate.with(Techniques.BounceInUp)
                            .duration(700)
                            .onStart(animator -> rl_calendar_view.setVisibility(View.VISIBLE))
                            .playOn(rl_calendar_view);
                    initializeCalendar();
                    break;
                case HIDE_DATE_PICKER:
                    Animate.with(Techniques.SlideOutDown)
                            .duration(700)
                            .onEnd(animator -> rl_calendar_view.setVisibility(View.GONE))
                            .playOn(rl_calendar_view);
                    break;
            }
        }
    };

    /**till here*/


    @SuppressLint("SetTextI18n")
    @AfterViews
    public void init() {
        //if screen open via node click
        if (Objects.requireNonNull(Objects.requireNonNull(getArguments()).getString("NODE_CALL")).equalsIgnoreCase("CALL_FROM_NODE")) {
            mcd = Objects.requireNonNull(getArguments().getParcelable(PD_Constant.CONTENT_PARENT));
            Log.e("MYC : ", mcd.getNodetitle()+" | "+mcd.getNodedesc()+" | "+mcd.getNodetype());
            course_name.setText(Objects.requireNonNull(getArguments()).getString("NODE_TITLE"));
            course_detail.setText(getArguments().getString("NODE_DESC"));
            levelContents = Objects.requireNonNull(getArguments()).getParcelableArrayList(PD_Constant.CONTENT_LEVEL);
            //btn_downloadAll.setVisibility(View.VISIBLE);

            /**First check whether course is already enrolled or not*/
            checkCourseAlreadyEnrolled(mcd,"WEEK_1");
            if(isCourseAlreadyEnrolled){
                tv_enroll_course.setVisibility(View.GONE);
                tv_goto_course.setVisibility(View.VISIBLE);
            } else {
                tv_enroll_course.setVisibility(View.VISIBLE);
                tv_goto_course.setVisibility(View.GONE);
            };
        }
        //else screen open via Course navigation item click
        else {
            enrollment = Objects.requireNonNull(getArguments()).getParcelable(PD_Constant.COURSE_PARENT);
            assert enrollment != null;
            course_name.setText(enrollment.getCourseDetail().getNodetitle());
            course_detail.setText(enrollment.getCourseDetail().getNodedesc());
            btn_playAll.setVisibility(View.VISIBLE);
            tv_enroll_course.setVisibility(View.GONE);
            btn_downloadAll.setVisibility(View.GONE);
            assessmentToastFlag = true;
        }

        getCourseContent();

        pd_apiRequest.setApiResult(CourseDetailFragment.this);

        /**Assessment toggle switch*/
        rg_assessment.setOnCheckedChangeListener((group, checkedId) ->
                {
                    switch (checkedId){
                        case R.id.rb_content :
                            getCourseContent();
                            break;

                        case R.id.rb_assessment :
                            getfinalAssessment(childs);
                            break;
                    }
                }
                );
    }

    @Override
    public void onResume() {
        super.onResume();
        checkCourseCompletetionStatus();
    }

    private void checkCourseCompletetionStatus() {
        EventMessage message = new EventMessage();
        message.setMessage(PD_Constant.CHECK_COURSE_COMPLETION);
        EventBus.getDefault().post(message);
    }

    private String parseDate(String date) {
        String[] date_split = date.split(" ");
        return date_split[1] + " " + date_split[2] + " " + date_split[3] + "," + date_split[6];
    }


    /**This function gets the whole content of respective course*/
    private void getCourseContent(){
        //check if update available for content
        if (childs == null || childs.isEmpty()) {
            childs = Objects.requireNonNull(getArguments()).getParcelableArrayList(PD_Constant.CONTENT);
            childs1 = Objects.requireNonNull(getArguments()).getParcelableArrayList("course_update");
            if (childs1 != null && !Objects.requireNonNull(childs1).isEmpty()) {
                for (Modal_ContentDetail detail1 : childs) {
                    for (Modal_ContentDetail detail : childs1) {
                        if (detail.getNodeid().equalsIgnoreCase(detail1.getNodeid())) {
                            detail1.setNodeUpdate(true);
                            Log.e("version if: ", detail.getNodeid());
                        } else {
                            //detail1.setNodeUpdate(false);
                            //Log.e("version else: ",detail1.getNodeid());
                        }
                    }
                    dispchilds.add(detail1);
                }
            } else {
                dispchilds = childs;
            }
            //to sort list sequence wise like portal
            try {
                Collections.sort(dispchilds, (o1, o2) -> {
                    if (o1.seq_no == null) {
                        return (o1.getNodeid().compareToIgnoreCase(o2.getNodeid()));
                    } else {
                        int s1 = Integer.parseInt(o1.getSeq_no());
                        int s2 = Integer.parseInt(o2.getSeq_no());
                        return (Integer.compare(s1, s2));
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        initializeAdapter(dispchilds);
    }

    /**This function gets only assessments of the respective course*/
    private void getfinalAssessment(List<Modal_ContentDetail> childs) {
        assessmentNode.clear();
        for(Modal_ContentDetail contentDetail: childs){
            if(contentDetail.getNodetype().equalsIgnoreCase("Assessment")){
                assessmentNode.add(contentDetail);
            }
        }
        initializeAdapter(assessmentNode);
    }


    private void initializeAdapter(List<Modal_ContentDetail> dispchilds) {
        courseChildViewHolder = new CourseChildViewHolder(Objects.requireNonNull(getView()));
        adapter = new CourseDetailAdapter(getActivity(), this);
        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(getActivity(), FlexDirection.ROW);
        flexboxLayoutManager.setJustifyContent(JustifyContent.FLEX_START);
        rv_course_childs.setLayoutManager(flexboxLayoutManager);
        rv_course_childs.setAdapter(adapter);
        adapter.submitList(dispchilds);
    }

    private void initializeCalendar() {
        Calendar startSelectionDate = Calendar.getInstance();
        startSelectionDate.add(Calendar.MONTH, -1);
        Calendar endSelectionDate = (Calendar) startSelectionDate.clone();
        endSelectionDate.add(Calendar.DATE, 4);
        course_date_picker.setSelectedDateRange(startSelectionDate, endSelectionDate);
        course_date_picker.setCalendarListener(new DateRangeCalendarView.CalendarListener() {
            @Override
            public void onFirstDateSelected(Calendar startDate) {

            }

            @Override
            public void onDateRangeSelected(Calendar s_Date, Calendar e_Date) {
                startDate = s_Date;
                endDate = e_Date;
            }
        });
    }


    @Click(R.id.tv_enroll_course)
    public void enrollCourse(){
        /**Check whether the content related to the course is available in database.*/
        Modal_ContentDetail modal_contentDetail = PrathamApplication.modalContentDao.getContent(mcd.getNodeid(),
                FastSave.getInstance().getString(PD_Constant.LANGUAGE, PD_Constant.HINDI));
        if(modal_contentDetail==null){
            Toast.makeText(getActivity(), "You must download atleast one Resource first!", Toast.LENGTH_LONG).show();
        } else{
            mHandler.sendEmptyMessage(SHOW_DATE_PICKER);
        }
    }

    /**Moving from current fragment to course fragment*/
    @Click(R.id.tv_goto_course)
    public void openCourseSection(){
        Intent courseintent = new Intent(getActivity(), ActivityMain_.class);
        courseintent.putExtra(PD_Constant.OPEN_COURSES, PD_Constant.OPEN_COURSES);
        startActivity(courseintent);
    }

    @Click(R.id.iv_close_calendar)
    public void setCloseCalendar() {
        mHandler.sendEmptyMessage(HIDE_DATE_PICKER);
    }

    @Click(R.id.btn_course_time_select)
    public void onCourseTimeSelected() {
        if (endDate == null || startDate == null) {
            Toast.makeText(getActivity(), R.string.select_correct_timeline, Toast.LENGTH_SHORT).show();
            return;
        }
        mHandler.sendEmptyMessage(HIDE_DATE_PICKER);
        addCourseToDb("WEEK_1", mcd, startDate, endDate);
        tv_goto_course.setVisibility(View.VISIBLE);
        tv_enroll_course.setVisibility(View.GONE);
    }


    @Click(R.id.play_all_content_serially)
    public void playCourse() {
        EventMessage message = new EventMessage();
        message.setMessage(PD_Constant.PLAY_COURSE);
        message.setDownloadId(null);
        EventBus.getDefault().post(message);
    }

    /**
     * This function is used to download all course item on single click.*/
    @Click(R.id.download_all_content_serially)
    public void downloadAllCourse() {
        if (FastSave.getInstance().getBoolean(PD_Constant.STORAGE_ASKED, false)) {
            for (Modal_ContentDetail dispContent : dispchilds) {
                if (dispContent.getResourcetype().equalsIgnoreCase("Video") ||
                        dispContent.getResourcetype().equalsIgnoreCase("Audio") ||
                        dispContent.getResourcetype().equalsIgnoreCase("PDF")) {

                    if (dispContent.isDownloaded() || dispContent.isOnSDCard()) {
                        //Log.e("Course : ", dispContent.getNodetitle());
                    } else {
                        Log.e("Course : ", dispContent.getNodetitle());
                        int itemPosition = Integer.parseInt(dispContent.getSeq_no());
                        courseChildViewHolder.downloadAll(dispContent, this, itemPosition);
                        //break;
                    }
                }
            }
        } else {
            download_builder = new BlurPopupWindow.Builder(getActivity())
                    .setContentView(R.layout.download_alert_dialog)
                    .bindClickListener(v -> {
                        FastSave.getInstance().saveBoolean(PD_Constant.STORAGE_ASKED, true);
                        downloadAllCourse();
                        download_builder.dismiss();
                    }, R.id.btn_okay)
                    .bindClickListener(v -> download_builder.dismiss(), R.id.txt_download_cancel)
                    .setGravity(Gravity.CENTER)
                    .setDismissOnClickBack(true)
                    .setDismissOnTouchBackground(true)
                    .setScaleRatio(0.2f)
                    .setBlurRadius(8)
                    .setTintColor(0x30000000)
                    .build();
            TextView tv = download_builder.findViewById(R.id.txt_download_alert);
            tv.append(" " + PD_Constant.STORING_IN);
            download_builder.show();
        }
    }

    @Override
    public void onChildItemClicked(Modal_ContentDetail modal_contentDetail) {
        EventMessage message = new EventMessage();
        message.setMessage(PD_Constant.PLAY_SPECIFIC_COURSE_CONTENT);
        message.setDownloadId(modal_contentDetail.getNodeid());
        EventBus.getDefault().post(message);
    }

    @Override
    public void onAssessmentItemClicked(Modal_ContentDetail contentDetail) {
        if (PrathamApplication.wiseF.isDeviceConnectedToMobileOrWifiNetwork()) {
            if (!assessmentToastFlag)
                saveAssessment(contentDetail);
        }

        new MaterialAlertDialogBuilder(Objects.requireNonNull(getActivity()))
                .setTitle(R.string.confirm)
                .setMessage(R.string.asmnt_redirect)
                .setPositiveButton("YES", (dialog, which) -> {
                    try {
                        //startActivityForResult(); //use this when actual testing
                        startAssessment(contentDetail);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), R.string.asmntapp_not_found, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("CANCEL", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    public void startAssessment(Modal_ContentDetail contentDetail) {
//        if (!PrathamApplication.isTablet) {

        Intent intent = new Intent("com.pratham.assessment.ui.choose_assessment.science.ScienceAssessmentActivity_");
        Bundle mBundle = new Bundle();

        ArrayList<String> studname = new ArrayList<>();
        ArrayList<String> studID = new ArrayList<>();
        ArrayList<String> studGrpID = new ArrayList<>();
        List<Attendance> newAttendance = attendanceDao.getNewAttendances(FastSave.getInstance().getString(PD_Constant.SESSIONID, "no session"));
        for (Attendance att : newAttendance) {
            List<Modal_Student> newStudent = studentDao.getAllStudent(att.getStudentID());
            for (Modal_Student stud : newStudent) {
                studname.add(stud.getFullName());
                studID.add(stud.getStudentId());
                studGrpID.add(stud.getGroupId());
            }
        }

        if (studname.size() == 1) {
            String groupId = studGrpID.get(0).replace("_SmartPhone", "");
            mBundle.putString("studentId", studID.get(0));
            mBundle.putString("appName", getResources().getString(R.string.app_name));
            mBundle.putString("studentName", studname.get(0));
            mBundle.putString("studentGroupId", groupId);
            mBundle.putString("subjectName", contentDetail.getSubject());
            mBundle.putString("subjectLanguage", contentDetail.getContent_language());
//                mBundle.putString("subjectLevel", "1");
            mBundle.putString("examId", contentDetail.getNodekeywords());
            Log.e("exam id : ", contentDetail.getNodekeywords());
//                mBundle.putString("subjectId", "89");
            mBundle.putString("currentSessionId", FastSave.getInstance().getString(PD_Constant.SESSIONID, "no_session"));
            intent.putExtras(mBundle);
            Log.e("assessment fields : ", mBundle.toString());
            startActivityForResult(intent, REQUEST_CODE_ASSESSMENT_BACK);
            sessionDao.UpdateToDate(FastSave.getInstance().getString(PD_Constant.SESSIONID, ""), PD_Utility.getCurrentDateTime());
            Log.d("url :", FastSave.getInstance().getString(PD_Constant.SESSIONID, ""));
            BackupDatabase.backup(getActivity());
        } else {
            final CharSequence[] charSequenceItems = studname.toArray(new CharSequence[studname.size()]);
            new MaterialAlertDialogBuilder(Objects.requireNonNull(getActivity()))
                    .setTitle("Select Student for Assessment")
                    .setItems(charSequenceItems, (dialog, which) -> {
                        // extras.putString(key, value);
                        mBundle.putString("studentId", studID.get(which));
                        mBundle.putString("appName", getResources().getString(R.string.app_name));
                        mBundle.putString("studentName", studname.get(which));
                        mBundle.putString("studentGroupId", studGrpID.get(which));
                        mBundle.putString("subjectName", contentDetail.getSubject());
                        mBundle.putString("subjectLanguage", contentDetail.getContent_language());
                        //mBundle.putString("subjectLevel", "1");
                        mBundle.putString("examId", contentDetail.getNodekeywords());
                        //mBundle.putString("subjectId", "89");
                        intent.putExtras(mBundle);
                        mBundle.putString("currentSessionId", FastSave.getInstance().getString(PD_Constant.SESSIONID, "no_session"));
                        startActivityForResult(intent, REQUEST_CODE_ASSESSMENT_BACK);
                        sessionDao.UpdateToDate(FastSave.getInstance().getString(PD_Constant.SESSIONID, ""), PD_Utility.getCurrentDateTime());
                        Log.d("url :", FastSave.getInstance().getString(PD_Constant.SESSIONID, ""));
                        BackupDatabase.backup(getActivity());
                    })
                    .show();
        }
    }

    //saving the assessment nodetype in db
    public void saveAssessment(Modal_ContentDetail contentDetail) {
        ArrayList<Modal_ContentDetail> temp = new ArrayList<>(levelContents);
        Modal_ContentDetail content = contentDetail;//Objects.requireNonNull(filesContentDownloading.get(downloadId)).getContentDetail();
        content.setContentType("file");
        content.setContent_language(FastSave.getInstance().getString(PD_Constant.LANGUAGE, PD_Constant.HINDI));
        temp.add(content);
        for (Modal_ContentDetail d : temp) {
            if (d.getNodeimage() != null) {
                String img_name = d.getNodeimage().substring(d.getNodeimage().lastIndexOf('/') + 1);
                d.setNodeimage(img_name);
            }
            d.setContent_language(FastSave.getInstance().getString(PD_Constant.LANGUAGE, PD_Constant.HINDI));
            d.setDownloaded(true);
            d.setOnSDCard(false);
        }
        modalContentDao.addContentList(temp);
        Toast.makeText(getActivity(), R.string.asmnt_saved, Toast.LENGTH_SHORT).show();
    }

    //function is called when download icon is clicked
    @Override
    public void onDownloadClicked(int position, Modal_ContentDetail contentDetail, View reveal_view, View start_reveal_item) {
        if (FastSave.getInstance().getBoolean(PD_Constant.STORAGE_ASKED, false)) {
            adapter.reveal(reveal_view, start_reveal_item);
            PrathamApplication.bubble_mp.start();
            filesDownloading.put(contentDetail.getNodeid(), position);
            downloadContent(contentDetail, Objects.requireNonNull(getArguments()).getParcelableArrayList(PD_Constant.CONTENT_LEVEL));
        } else {
            download_builder = new BlurPopupWindow.Builder(getActivity())
                    .setContentView(R.layout.download_alert_dialog)
                    .bindClickListener(v -> {
                        FastSave.getInstance().saveBoolean(PD_Constant.STORAGE_ASKED, true);
                        onDownloadClicked(position, contentDetail, reveal_view, start_reveal_item);
                        download_builder.dismiss();
                    }, R.id.btn_okay)
                    .bindClickListener(v -> download_builder.dismiss(), R.id.txt_download_cancel)
                    .setGravity(Gravity.CENTER)
                    .setDismissOnClickBack(true)
                    .setDismissOnTouchBackground(true)
                    .setScaleRatio(0.2f)
                    .setBlurRadius(8)
                    .setTintColor(0x30000000)
                    .build();
            TextView tv = download_builder.findViewById(R.id.txt_download_alert);
            tv.append(" " + PD_Constant.STORING_IN);
            download_builder.show();
        }
    }

    @Background
    public void downloadContent(Modal_ContentDetail contentDetail, ArrayList<Modal_ContentDetail> levelContents) {
        if (PrathamApplication.wiseF.isDeviceConnectedToMobileNetwork()) {
            pd_apiRequest.getContentFromInternet(PD_Constant.INTERNET_DOWNLOAD,
                    PD_Constant.URL.DOWNLOAD_RESOURCE.toString() + contentDetail.getNodeid() + "&deviceid=" + PD_Utility.getDeviceID(), null);
        } else if (PrathamApplication.wiseF.isDeviceConnectedToWifiNetwork()) {
            if (PrathamApplication.wiseF.isDeviceConnectedToSSID(PD_Constant.PRATHAM_KOLIBRI_HOTSPOT)) {
                try {
//                    String url = contentDetail.getResourcezip();
                    String url = "";
                    String filename = URLDecoder.decode(contentDetail.getResourcezip(), "UTF-8")
                            .substring(URLDecoder.decode(contentDetail.getResourcezip(), "UTF-8").lastIndexOf('/') + 1);
                    String foldername = contentDetail.getResourcetype();
                    String fileFormat = contentDetail.getResourcepath().substring(contentDetail.getResourcepath().lastIndexOf("."));
                    if (foldername.equalsIgnoreCase("pdf")) {
                        url = PD_Constant.RASP_IP + PD_Constant.RASP_LOCAL_URL + "/docs/" + filename;
                    } else if (foldername.equalsIgnoreCase("game")) {
                        url = PD_Constant.RASP_IP + PD_Constant.RASP_LOCAL_URL + "/zips/" + filename;
                    } else if (foldername.equalsIgnoreCase("video")) {
                        if (fileFormat.equalsIgnoreCase(".mp4"))
                            url = PD_Constant.RASP_IP + PD_Constant.RASP_LOCAL_URL + "/videos/mp4/" + filename;
                        else
                            url = PD_Constant.RASP_IP + PD_Constant.RASP_LOCAL_URL + "/videos/m4v/" + filename;

                    } else if (foldername.equalsIgnoreCase("audio")) {
                        if (fileFormat.equalsIgnoreCase(".mp3"))
                            url = PD_Constant.RASP_IP + PD_Constant.RASP_LOCAL_URL + "/audios/mp3/" + filename;
                        else
                            url = PD_Constant.RASP_IP + PD_Constant.RASP_LOCAL_URL + "/audios/wav/" + filename;
                    }
                    Log.e("**URL:", url);
                    zipDownloader.initializeforCourse(CourseDetailFragment.this
                            , url, foldername, filename, contentDetail, levelContents, PD_Constant.RASPPI);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else {
                pd_apiRequest.getContentFromInternet(PD_Constant.INTERNET_DOWNLOAD,
                        PD_Constant.URL.DOWNLOAD_RESOURCE.toString() + contentDetail.getNodeid() + "&deviceid=" + PD_Utility.getDeviceID(), null);
                //String url = PD_Constant.URL.DOWNLOAD_RESOURCE.toString() + contentDetail.getNodeid() + "&deviceid=" + PD_Utility.getDeviceID();
                //Log.e("URL",url);
            }
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void messageReceived(EventMessage message) {
        if (message != null) {
            if (message.getMessage().equalsIgnoreCase(PD_Constant.CLOSE_CONTENT_PLAYER)) {
                EventMessage eventMessage1 = new EventMessage();
                eventMessage1.setMessage(PD_Constant.CLOSE_CONTENT_ACTIVITY);
                EventBus.getDefault().post(eventMessage1);
            } else if (message.getMessage().equalsIgnoreCase(PD_Constant.COURSE_COMPLETED))
                btn_submit_assignment.setVisibility(View.GONE);//made gone to skip assignment step
        }
    }

    //assignment feature skiped
    @Click(R.id.btn_submit_assignment)
    public void openAssignments() {
/*        Bundle bundle = new Bundle();
        bundle.putParcelable(PD_Constant.ENROLLED_COURSE, enrollment);
        bundle.putString(PD_Constant.OPEN_ASSIGNMENTS, enrollment.getCourseDetail().getAssignment());
        EventMessage message = new EventMessage();
        message.setMessage(PD_Constant.OPEN_ASSIGNMENTS);
        message.setBundle(bundle);
        EventBus.getDefault().post(message);*/
    }

    @Override
    public void recievedContent(String header, String response, ArrayList<Modal_ContentDetail> contentList) {
        ArrayList<Modal_ContentDetail> displayedContents = new ArrayList<>();
        ArrayList<Modal_ContentDetail> totalContents = new ArrayList<>();
        try {
            Log.e("url response:::", response);
            Log.e("url response:::", "requestType:: " + header);
            Gson gson = new Gson();
            if (header.equalsIgnoreCase(PD_Constant.INTERNET_DOWNLOAD)) {
                JSONObject jsonObject = new JSONObject(response);
                Modal_DownloadContent download_content = gson.fromJson(jsonObject.toString(), Modal_DownloadContent.class);
                Modal_ContentDetail contentDetail = download_content.getNodelist().get(download_content.getNodelist().size() - 1);
                String fileName = download_content.getDownloadurl()
                        .substring(download_content.getDownloadurl().lastIndexOf('/') + 1);
                zipDownloader.initializeforCourse(CourseDetailFragment.this, download_content.getDownloadurl(),
                        download_content.getFoldername(), fileName, contentDetail, levelContents, "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void recievedError(String header, ArrayList<Modal_ContentDetail> contentList) {
        Toast.makeText(getActivity(), header, Toast.LENGTH_SHORT).show();
    }


    /**Add course to db while enrolling from seekho section*/
    //todo: move to presenter in future
    public void addCourseToDb(String week, Modal_ContentDetail selectedCourse, Calendar startDate, Calendar endDate) {
        String groupId = FastSave.getInstance().getString(PD_Constant.GROUPID, "no_group");
        checkCourseAlreadyEnrolled(selectedCourse, week);
        if (!isCourseAlreadyEnrolled) {
            Model_CourseEnrollment courseEnrollment = new Model_CourseEnrollment();
            courseEnrollment.setCoachVerificationDate("");
            courseEnrollment.setCoachVerified(false);
            //add experience as json object string in db
            Model_CourseExperience model_courseExperience = new Model_CourseExperience();
            model_courseExperience.setAssignments(null);
            model_courseExperience.setWords_learnt("");
            model_courseExperience.setAssignments_completed("");
            model_courseExperience.setAssignments_description("");
            model_courseExperience.setCoach_comments("");
            model_courseExperience.setCoach_verification_date("");
            model_courseExperience.setCoach_image("");
            model_courseExperience.setAssignment_submission_date(PD_Utility.getCurrentDateTime());
            model_courseExperience.setStatus(PD_Constant.COURSE_NOT_VERIFIED);

            courseEnrollment.setCourseExperience(new Gson().toJson(model_courseExperience));
            courseEnrollment.setCourseDetail(selectedCourse);
            courseEnrollment.setCourseId(selectedCourse.getNodeid());
            courseEnrollment.setGroupId(groupId);
            courseEnrollment.setPlanFromDate(week + " " + startDate.getTime().toString());
            courseEnrollment.setPlanToDate(week + " " + endDate.getTime().toString());
            courseEnrollment.setSentFlag(0);
            courseEnrollment.setLanguage(FastSave.getInstance().getString(PD_Constant.LANGUAGE, PD_Constant.HINDI));
            //add @courseEnrollment in hashmap and db
            List<Model_CourseEnrollment> enrollments;
            if (coursesPerWeek.containsKey(week)) {
                enrollments = new ArrayList<>(Objects.requireNonNull(coursesPerWeek.get(week)));
                enrollments.add(courseEnrollment);
            } else {
                enrollments = new ArrayList<>();
                enrollments.add(courseEnrollment);
            }
            coursesPerWeek.put(week, enrollments);
            PrathamApplication.courseDao.insertCourse(courseEnrollment);
            Toast.makeText(getActivity(),"Course Added!", Toast.LENGTH_SHORT).show();
        } else {
            //course is already added in that particular week
            Toast.makeText(getActivity(), R.string.course_already_enrolled, Toast.LENGTH_SHORT).show();
        }
    }

    /**Getting already enrolled courses from db*/
    private List<Model_CourseEnrollment> enrolledCoursesFromDb(String week, String groupId) {
        coursesPerWeek.remove(week);
        List<Model_CourseEnrollment> courseEnrollments = PrathamApplication.courseDao.
                fetchEnrolledCourses(groupId, week, FastSave.getInstance().getString(PD_Constant.LANGUAGE, PD_Constant.HINDI));
        if (courseEnrollments == null) return null;
        List<Model_CourseEnrollment> temp = new ArrayList<>();
        for (Model_CourseEnrollment ce : courseEnrollments) {
            Model_CourseExperience courseExperience = new Gson().fromJson(ce.getCourseExperience(), Model_CourseExperience.class);
            if (!courseExperience.getStatus().equalsIgnoreCase(PD_Constant.FEEDBACK_GIVEN)) {
                ce.setCourseDetail(PrathamApplication.modalContentDao.getContent(ce.getCourseId(),
                        FastSave.getInstance().getString(PD_Constant.LANGUAGE, PD_Constant.HINDI)));
                ce.setProgressCompleted(isCourseProgressCompleted(ce, week));
                temp.add(ce);
            }
        }
        if (temp.size() > 0)
            coursesPerWeek.put(week, temp);
        return temp;
    }

    /**Check whether the course is completed or not*/
    private boolean isCourseProgressCompleted(Model_CourseEnrollment ce, String week) {
        String progress = PrathamApplication.contentProgressDao.getCourseProgress(ce.getGroupId(), ce.getCourseId(), week);
        //considering the case if the progress is not complete 100%
        if (progress != null) {
            return Integer.parseInt(progress) > 95;
        } else return false;
    }

    public void checkCourseAlreadyEnrolled(Modal_ContentDetail selectedCourse, String week){
        String groupId = FastSave.getInstance().getString(PD_Constant.GROUPID, "no_group");
        List<Model_CourseEnrollment> courseEnrollments = enrolledCoursesFromDb(week, groupId);
        for (Model_CourseEnrollment cen : Objects.requireNonNull(courseEnrollments)) {
            if (selectedCourse.getNodeid().equalsIgnoreCase(cen.getCourseDetail().getNodeid())) {
                isCourseAlreadyEnrolled = true;
                break;
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMainBackPressed(EventMessage message) {
        if (message != null) {
            if (message.getMessage().equalsIgnoreCase(PD_Constant.CONTENT_BACK)) {
                //setContent_back();
            } else if (message.getMessage().equalsIgnoreCase(PD_Constant.FAST_DOWNLOAD_STARTED)) {
                fileDownloadStarted(message.getDownloadId(), message.getModal_fileDownloading());
            } else if (message.getMessage().equalsIgnoreCase(PD_Constant.FAST_DOWNLOAD_UPDATE)) {
                updateFileProgress(message.getDownloadId(), message.getModal_fileDownloading());
            } else if (message.getMessage().equalsIgnoreCase(PD_Constant.FAST_DOWNLOAD_COMPLETE)) {
                onDownloadCompleted(message.getDownloadId(), message.getContentDetail());
            } else if (message.getMessage().equalsIgnoreCase(PD_Constant.FAST_DOWNLOAD_ERROR)) {
                ondownloadError(message.getDownloadId());
            } else if (message.getMessage().equalsIgnoreCase(PD_Constant.FILE_DOWNLOAD_COMPLETE)) {
                onDownloadComplete(message);
/*            } else if (message.getMessage().equalsIgnoreCase(PD_Constant.CONNECTION_STATUS)) {
                updateConnectionStatus(message);*/
            } else if (message.getMessage().equalsIgnoreCase(PD_Constant.FILE_DOWNLOAD_ERROR)) {
                onDownloadError(message);
            } else if (message.getMessage().equalsIgnoreCase(PD_Constant.BROADCAST_DOWNLOADINGS)) {
                broadcast_downloadings();
            } else if (message.getMessage().equalsIgnoreCase(PD_Constant.CANCEL_DOWNLOAD)) {
                cancelDownload(message.getDownloadId());
            }
        }
    }

    @UiThread
    public void onDownloadComplete(EventMessage message) {
        if (message != null) {
            if (filesDownloading.containsKey(message.getContentDetail().getNodeid())) {
                List<Modal_ContentDetail> data = new ArrayList<>(adapter.getData());
                for (int i = 0; i < data.size(); i++) {
                    if (data.get(i) != null && data.get(i).getNodeid() != null &&
                            data.get(i).getNodeid().equalsIgnoreCase(message.getContentDetail().getNodeid())) {
                        adapter.notifyItemChanged(i, message.getContentDetail());
                        break;
                    }
                }
                adapter.submitList(data);
            }
        }
    }

    @UiThread
    public void onDownloadError(EventMessage message) {
        if (message != null) {
            if (filesDownloading.containsKey(message.getContentDetail().getNodeid())) {
                List<Modal_ContentDetail> data = new ArrayList<>(adapter.getData());
                for (int i = 0; i < data.size(); i++) {
                    if (data.get(i) != null && data.get(i).getNodeid() != null &&
                            data.get(i).getNodeid().equalsIgnoreCase(message.getContentDetail().getNodeid())) {
                        adapter.notifyItemChanged(i, data.get(i));
                        break;
                    }
                }
            }
        }
    }

    public void fileDownloadStarted(String downloadID, Modal_FileDownloading
            modal_fileDownloading) {
        filesContentDownloading.put(downloadID, modal_fileDownloading);
        postDownloadStartMessage();
    }

    public void updateFileProgress(String downloadID, Modal_FileDownloading mfd) {
        filesContentDownloading.put(downloadID, mfd);
        postProgressMessage();
    }

    public void onDownloadCompleted(String downloadID, Modal_ContentDetail content) {
        filesContentDownloading.remove(downloadID);
        postAllDownloadsCompletedMessage();
        postSingleFileDownloadCompleteMessage(content);
        currentDownloadTasks.remove(downloadID);
        for (int i = 0; i < childs.size(); i++) {
            if (childs.get(i) != null && childs.get(i).getNodeid() != null &&
                    childs.get(i).getNodeid().equalsIgnoreCase(content.getNodeid())) {
                childs.set(i, content);
                break;
            }
        }

        //update data download channel
        Modal_Log modal_log = new Modal_Log();
        modal_log.setErrorType("DOWNLOAD");
        modal_log.setExceptionMessage(content.getNodetitle());
        modal_log.setMethodName(content.getNodeid());
        modal_log.setCurrentDateTime(PD_Utility.getCurrentDateTime());
        modal_log.setSessionId(FastSave.getInstance().getString(PD_Constant.SESSIONID, ""));
        modal_log.setExceptionStackTrace("APK BUILD DATE : " + PD_Constant.apkDate);
        modal_log.setDeviceId("" + PD_Utility.getDeviceID());
        modal_log.setLogDetail(content.getResourcezip());
        if (PrathamApplication.wiseF.isDeviceConnectedToSSID(PD_Constant.PRATHAM_KOLIBRI_HOTSPOT))
            modal_log.setLogDetail("PI#" + content.getResourcezip());
        else
            modal_log.setLogDetail("INTERNET#" + content.getResourcezip());

        logDao.insertLog(modal_log);
        BackupDatabase.backup(getActivity());
    }

    public void ondownloadError(String downloadId) {
        Modal_ContentDetail content = Objects.requireNonNull(filesContentDownloading.get(downloadId)).getContentDetail();
        filesContentDownloading.remove(downloadId);
        postSingleFileDownloadErrorMessage(content);
        EventBus.getDefault().post(new ArrayList<>(filesContentDownloading.values()));
        cancelDownload(downloadId);
    }

    public void broadcast_downloadings() {
        postProgressMessage();
    }

    @Background
    public void eventFileDownloadStarted_(EventMessage message) {
        Modal_FileDownloading modal_fileDownloading = new Modal_FileDownloading();
        modal_fileDownloading.setDownloadId(message.getDownloadId());
        modal_fileDownloading.setFilename(message.getFile_name());
        modal_fileDownloading.setProgress(0);
        modal_fileDownloading.setContentDetail(message.getContentDetail());
        filesContentDownloading.put(message.getDownloadId(), modal_fileDownloading);
        postDownloadStartMessage();
        for (Modal_ContentDetail detail : levelContents) {
            if (detail.getNodeserverimage() != null) {
                String f_name = detail.getNodeserverimage()
                        .substring(detail.getNodeserverimage().lastIndexOf('/') + 1);
                PD_ApiRequest.downloadImage(detail.getNodeserverimage(), f_name);
            }
        }
        String f_name = message.getContentDetail().getNodeserverimage()
                .substring(message.getContentDetail().getNodeserverimage().lastIndexOf('/') + 1);
        PD_ApiRequest.downloadImage(message.getContentDetail().getNodeserverimage(), f_name);
    }

    @UiThread
    public void postDownloadStartMessage() {
        EventMessage msg = new EventMessage();
        msg.setMessage(PD_Constant.FILE_DOWNLOAD_STARTED);
        msg.setDownlaodContentSize(filesContentDownloading.size());
        EventBus.getDefault().post(msg);
    }

    public void eventUpdateFileProgress_(EventMessage message) {
        String downloadId = message.getDownloadId();
        String filename = message.getFile_name();
        int progress = (int) message.getProgress();
        Log.d(TAG, "updateFileProgress: " + downloadId + ":::" + filename + ":::" + progress);
        if (filesContentDownloading.get(downloadId) != null /*&& filesDownloading.get(downloadId).getProgress() != progress*/) {
            Modal_FileDownloading modal_fileDownloading = new Modal_FileDownloading();
            modal_fileDownloading.setDownloadId(String.valueOf(downloadId));
            modal_fileDownloading.setFilename(filename);
            modal_fileDownloading.setProgress(progress);
            modal_fileDownloading.setContentDetail(Objects.requireNonNull(filesContentDownloading.get(downloadId)).getContentDetail());
            filesContentDownloading.put(String.valueOf(downloadId), modal_fileDownloading);
            postProgressMessage();
        }
    }

    @UiThread
    public void postProgressMessage() {
        try {
            EventBus.getDefault().post(new ArrayList<>(filesContentDownloading.values()));
        } catch (ConcurrentModificationException e) {
            e.printStackTrace();
        }
    }

    public void eventOnDownloadCompleted(EventMessage message) {
        if (message != null) {
            if (message.getMessage().equalsIgnoreCase(PD_Constant.DOWNLOAD_COMPLETE)) {
                eventOnDownloadCompleted_(message);
            }
        }
    }

    @Background
    public void eventOnDownloadCompleted_(EventMessage message) {
        String downloadId = message.getDownloadId();
        Log.d(TAG, "updateFileProgress: " + downloadId);
        ArrayList<Modal_ContentDetail> temp = new ArrayList<>(levelContents);
        Modal_ContentDetail content = Objects.requireNonNull(filesContentDownloading.get(downloadId)).getContentDetail();
        content.setContentType("file");
        content.setContent_language(FastSave.getInstance().getString(PD_Constant.LANGUAGE, PD_Constant.HINDI));
        temp.add(content);
        for (Modal_ContentDetail d : temp) {
            if (d.getNodeimage() != null) {
                String img_name = d.getNodeimage().substring(d.getNodeimage().lastIndexOf('/') + 1);
                d.setNodeimage(img_name);
            }
            d.setContent_language(FastSave.getInstance().getString(PD_Constant.LANGUAGE, PD_Constant.HINDI));
            d.setDownloaded(true);
            d.setOnSDCard(false);
        }
        modalContentDao.addContentList(temp);
        filesContentDownloading.remove(downloadId);
        postAllDownloadsCompletedMessage();
        postSingleFileDownloadCompleteMessage(content);
    }

    @UiThread
    public void postSingleFileDownloadCompleteMessage(Modal_ContentDetail content) {
        EventMessage msg = new EventMessage();
        msg.setMessage(PD_Constant.FILE_DOWNLOAD_COMPLETE);
        msg.setDownlaodContentSize(filesContentDownloading.size());
        msg.setContentDetail(content);
        EventBus.getDefault().post(msg);
    }

    @UiThread
    public void postAllDownloadsCompletedMessage() {
//        if (filesDownloading.size() == 0) {
        EventBus.getDefault().post(new ArrayList<>(filesContentDownloading.values()));
//        }
    }

    @Background
    public void eventOnDownloadFailed(EventMessage message) {
        if (message != null) {
            if (message.getMessage().equalsIgnoreCase(PD_Constant.DOWNLOAD_FAILED)) {
                Modal_ContentDetail content = Objects.requireNonNull(filesContentDownloading.get(message.getDownloadId())).getContentDetail();
                postSingleFileDownloadErrorMessage(content);
                filesContentDownloading.remove(message.getDownloadId());
                EventBus.getDefault().post(new ArrayList<>(filesContentDownloading.values()));
            }
        }
    }

    @UiThread
    public void postSingleFileDownloadErrorMessage(Modal_ContentDetail content) {
        EventMessage msg = new EventMessage();
        msg.setMessage(PD_Constant.FILE_DOWNLOAD_ERROR);
        msg.setDownlaodContentSize(filesContentDownloading.size());
        msg.setContentDetail(content);
        EventBus.getDefault().post(msg);
    }

    public void currentDownloadRunning(String downloadId, DownloadTask task) {
        if (!currentDownloadTasks.containsKey(downloadId)) {
            currentDownloadTasks.put(downloadId, task);
        }
    }

    public void cancelDownload(String downloadId) {
        if (downloadId != null && !downloadId.isEmpty()) {
            if (currentDownloadTasks.containsKey(downloadId))
                Objects.requireNonNull(currentDownloadTasks.get(downloadId)).cancel();
            postProgressMessage();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ASSESSMENT_BACK) {
            sessionDao.UpdateToDate(FastSave.getInstance().getString(PD_Constant.SESSIONID, ""), PD_Utility.getCurrentDateTime());
            Log.d("url :", FastSave.getInstance().getString(PD_Constant.SESSIONID, ""));
            BackupDatabase.backup(getActivity());
        }
    }

}
