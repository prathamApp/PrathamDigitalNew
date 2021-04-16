package com.pratham.prathamdigital.ui.content_player.course_detail;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

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
import com.pratham.prathamdigital.interfaces.ApiResult;
import com.pratham.prathamdigital.models.Attendance;
import com.pratham.prathamdigital.models.EventMessage;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.models.Modal_DownloadContent;
import com.pratham.prathamdigital.models.Modal_FileDownloading;
import com.pratham.prathamdigital.models.Modal_Student;
import com.pratham.prathamdigital.models.Model_CourseEnrollment;
import com.pratham.prathamdigital.ui.content_player.ContentPlayerContract;
import com.pratham.prathamdigital.ui.fragment_content.ContentPresenterImpl;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.pratham.prathamdigital.PrathamApplication.attendanceDao;
import static com.pratham.prathamdigital.PrathamApplication.modalContentDao;
import static com.pratham.prathamdigital.PrathamApplication.studentDao;
import static com.pratham.prathamdigital.custom.file_picker.FilePickerActivity.TAG;

@EFragment(R.layout.fragment_course_detail)
public class CourseDetailFragment extends Fragment implements ContentPlayerContract.courseDetailAdapterClick, ApiResult {

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

    CourseDetailAdapter adapter;
    private Model_CourseEnrollment enrollment;
    private List<Modal_ContentDetail> childs;

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

    boolean assessmentToastFlag=false;

    @SuppressLint("SetTextI18n")
    @AfterViews
    public void init() {
        //if screen open via node click
        if (Objects.requireNonNull(Objects.requireNonNull(getArguments()).getString("NODE_CALL")).equalsIgnoreCase("CALL_FROM_NODE")) {
            course_name.setText(Objects.requireNonNull(getArguments()).getString("NODE_TITLE"));
            course_detail.setText(getArguments().getString("NODE_DESC"));
            levelContents = Objects.requireNonNull(getArguments()).getParcelableArrayList(PD_Constant.CONTENT_LEVEL);
        }
        //else screen open via Course navigation item click
        else {
            enrollment = Objects.requireNonNull(getArguments()).getParcelable(PD_Constant.COURSE_PARENT);
            assert enrollment != null;
            course_name.setText(enrollment.getCourseDetail().getNodetitle());
            course_detail.setText(enrollment.getCourseDetail().getNodedesc());
            btn_playAll.setVisibility(View.VISIBLE);
            assessmentToastFlag=true;
        }
        initializeAdapter();
        pd_apiRequest.setApiResult(CourseDetailFragment.this);
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

    private void initializeAdapter() {
        if (childs == null || childs.isEmpty()) {
            childs = Objects.requireNonNull(getArguments()).getParcelableArrayList(PD_Constant.CONTENT);
            //to sort list sequence wise like portal
            Collections.sort(childs, (o1, o2) -> {
                if(o1.seq_no==null) {
                    return (o1.getNodeid().compareToIgnoreCase(o2.getNodeid()));
                }
                else {
                    int s1 = Integer.parseInt(o1.getSeq_no());
                    int s2 = Integer.parseInt(o2.getSeq_no());
                    return (Integer.compare(s1, s2));
                }
            });
        }

        adapter = new CourseDetailAdapter(getActivity(), this);
        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(getActivity(), FlexDirection.ROW);
        flexboxLayoutManager.setJustifyContent(JustifyContent.FLEX_START);
        rv_course_childs.setLayoutManager(flexboxLayoutManager);
        rv_course_childs.setAdapter(adapter);
        adapter.submitList(childs);
    }

    @Click(R.id.play_all_content_serially)
    public void playCourse() {
        EventMessage message = new EventMessage();
        message.setMessage(PD_Constant.PLAY_COURSE);
        message.setDownloadId(null);
        EventBus.getDefault().post(message);
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
            if(!assessmentToastFlag)
            saveAssessment(contentDetail);
        }

        new MaterialAlertDialogBuilder(Objects.requireNonNull(getActivity()))
                .setTitle("Confirm")
                .setMessage("You will be redirected to Assessment App!")
                .setPositiveButton("YES", (dialog, which) -> {
                    try {
                        //startActivityForResult(); //use this when actual testing
                        startAssessment(contentDetail);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "App not found", Toast.LENGTH_SHORT).show();
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
        List<Attendance> newAttendance = attendanceDao.getNewAttendances(FastSave.getInstance().getString(PD_Constant.SESSIONID, "no session"));
        for (Attendance att : newAttendance) {
            List<Modal_Student> newStudent = studentDao.getAllStudent(att.getStudentID());
            for (Modal_Student stud : newStudent) {
                studname.add(stud.getFullName());
                studID.add(stud.getStudentId());
            }
        }

        if (studname.size() == 1) {
            mBundle.putString("studentId", FastSave.getInstance().getString(PD_Constant.SESSIONID, "no session"));
            mBundle.putString("appName", getResources().getString(R.string.app_name));
            mBundle.putString("studentName", FastSave.getInstance().getString(PD_Constant.PROFILE_NAME, ""));
            mBundle.putString("subjectName", contentDetail.getSubject());
            mBundle.putString("subjectLanguage", contentDetail.getContent_language());
//                mBundle.putString("subjectLevel", "1");
            mBundle.putString("examId", contentDetail.getNodekeywords());
//                mBundle.putString("subjectId", "89");
            intent.putExtras(mBundle);
            startActivity(intent);
        } else {
            final CharSequence[] charSequenceItems = studname.toArray(new CharSequence[studname.size()]);
            new MaterialAlertDialogBuilder(Objects.requireNonNull(getActivity()))
                    .setTitle("Select Student for Assessment")
                    .setItems(charSequenceItems, (dialog, which) -> {
                        // extras.putString(key, value);
                        mBundle.putString("studentId", studID.get(which));
                        mBundle.putString("appName", getResources().getString(R.string.app_name));
                        mBundle.putString("studentName", studname.get(which));
                        mBundle.putString("subjectName", contentDetail.getSubject());
                        mBundle.putString("subjectLanguage", contentDetail.getContent_language());
                        //mBundle.putString("subjectLevel", "1");
                        mBundle.putString("examId", contentDetail.getNodekeywords());
                        //mBundle.putString("subjectId", "89");
                        intent.putExtras(mBundle);
                        startActivity(intent);
                    })
                    .show();
        }
    }

    //saving the assessment nodetype in db
    public void saveAssessment(Modal_ContentDetail contentDetail){
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
        Toast.makeText(getActivity(), "Assessment Saved.", Toast.LENGTH_SHORT).show();
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
                    String url="";
                    String filename = URLDecoder.decode(contentDetail.getResourcezip(), "UTF-8")
                            .substring(URLDecoder.decode(contentDetail.getResourcezip(), "UTF-8").lastIndexOf('/') + 1);
                    String foldername = contentDetail.getResourcetype();
                    if(foldername.equalsIgnoreCase("pdf")){
                        url = PD_Constant.RASP_IP + PD_Constant.RASP_LOCAL_URL+ "/docs/" + filename;
                    }else if(foldername.equalsIgnoreCase("game")){
                        url = PD_Constant.RASP_IP + PD_Constant.RASP_LOCAL_URL+ "/zips/" + filename;
                    }else if(foldername.equalsIgnoreCase("video")){
                        url = PD_Constant.RASP_IP + PD_Constant.RASP_LOCAL_URL+ "/videos/mp4/" + filename;
                    }else if(foldername.equalsIgnoreCase("audio")) {
                        url = PD_Constant.RASP_IP + PD_Constant.RASP_LOCAL_URL + "/audios/mp3/" + filename;
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
                btn_submit_assignment.setVisibility(View.VISIBLE);
        }
    }

    @Click(R.id.btn_submit_assignment)
    public void openAssignments() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(PD_Constant.ENROLLED_COURSE, enrollment);
        bundle.putString(PD_Constant.OPEN_ASSIGNMENTS, enrollment.getCourseDetail().getAssignment());
        EventMessage message = new EventMessage();
        message.setMessage(PD_Constant.OPEN_ASSIGNMENTS);
        message.setBundle(bundle);
        EventBus.getDefault().post(message);
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
                        download_content.getFoldername(), fileName, contentDetail, levelContents,"");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void recievedError(String header, ArrayList<Modal_ContentDetail> contentList) {
        Toast.makeText(getActivity(), header, Toast.LENGTH_SHORT).show();
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
        EventBus.getDefault().post(new ArrayList<>(filesContentDownloading.values()));
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
}
