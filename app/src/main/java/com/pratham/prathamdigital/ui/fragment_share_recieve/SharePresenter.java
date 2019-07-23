package com.pratham.prathamdigital.ui.fragment_share_recieve;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.isupatches.wisefy.callbacks.AddNetworkCallbacks;
import com.isupatches.wisefy.callbacks.ConnectToNetworkCallbacks;
import com.isupatches.wisefy.callbacks.GetSavedNetworkCallbacks;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.async.CopyExistingJSONS;
import com.pratham.prathamdigital.async.FTPContentUploadTask;
import com.pratham.prathamdigital.async.FTPSingleFileUploadTask;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.ftpSettings.ConnectToFTP;
import com.pratham.prathamdigital.interfaces.DownloadedContents;
import com.pratham.prathamdigital.interfaces.FTPConnected;
import com.pratham.prathamdigital.models.Attendance;
import com.pratham.prathamdigital.models.File_Model;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.models.Modal_Crl;
import com.pratham.prathamdigital.models.Modal_Groups;
import com.pratham.prathamdigital.models.Modal_Log;
import com.pratham.prathamdigital.models.Modal_ReceivingFilesThroughFTP;
import com.pratham.prathamdigital.models.Modal_Score;
import com.pratham.prathamdigital.models.Modal_Session;
import com.pratham.prathamdigital.models.Modal_Status;
import com.pratham.prathamdigital.models.Modal_Student;
import com.pratham.prathamdigital.models.Modal_Village;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.apache.commons.net.ftp.FTPClient;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.pratham.prathamdigital.PrathamApplication.attendanceDao;
import static com.pratham.prathamdigital.PrathamApplication.crLdao;
import static com.pratham.prathamdigital.PrathamApplication.groupDao;
import static com.pratham.prathamdigital.PrathamApplication.logDao;
import static com.pratham.prathamdigital.PrathamApplication.modalContentDao;
import static com.pratham.prathamdigital.PrathamApplication.scoreDao;
import static com.pratham.prathamdigital.PrathamApplication.sessionDao;
import static com.pratham.prathamdigital.PrathamApplication.statusDao;
import static com.pratham.prathamdigital.PrathamApplication.studentDao;
import static com.pratham.prathamdigital.PrathamApplication.villageDao;

@EBean
public class SharePresenter implements DownloadedContents, ContractShare.sharePresenter, FTPConnected {
    private static final String TAG = SharePresenter.class.getSimpleName();
    private final Context context;
    private final ArrayList<Modal_ContentDetail> levels = new ArrayList<>();
    private final HashMap<String, Modal_ReceivingFilesThroughFTP> filesRecieving = new HashMap<>();
    private ContractShare.shareView shareView;
    private FTPClient ftpClient;
    private Timer ftpTimer = new Timer();
    private String scanned_qr_ip;
    @Bean(FTPContentUploadTask.class)
    FTPContentUploadTask ftpContentUploadTask;
    @Bean(FTPSingleFileUploadTask.class)
    FTPSingleFileUploadTask ftpSingleFileUploadTask;
    @Bean(ConnectToFTP.class)
    ConnectToFTP connectToFTP;
    @Bean(CopyExistingJSONS.class)
    CopyExistingJSONS copyExistingJSONS;

    private final AddNetworkCallbacks addNetworkCallbacks = new AddNetworkCallbacks() {
        @Override
        public void failureAddingNetwork(int i) {
            Log.d(TAG, "failureAddingNetwork: ");
        }

        @Override
        public void networkAdded(int i, @NotNull WifiConfiguration wifiConfiguration) {
            connectToNetwork(wifiConfiguration.SSID);
        }

        @Override
        public void wisefyFailure(int i) {
            Log.d(TAG, "wisefyFailure: ");
        }
    };

    public SharePresenter(Context context) {
        this.context = context;
    }

    @Override
    public void setView(Fragment fragmentShareRecieve) {
        this.shareView = (ContractShare.shareView) fragmentShareRecieve;
    }

    private int ftpConnectionTry = 0;

    @Override
    public void viewDestroyed() {
        shareView = null;
    }

    private void addNetwork(String ssid, String wifipass) {
        if (wifipass.isEmpty())
            PrathamApplication.wiseF.addOpenNetwork(ssid, addNetworkCallbacks);
        else
            PrathamApplication.wiseF.addWPA2Network(ssid, wifipass, addNetworkCallbacks);
    }

    @Background
    @Override
    public void connectToWify(String ssid, String wifipass) {
        PrathamApplication.wiseF.getSavedNetwork(ssid, new GetSavedNetworkCallbacks() {
            @Override
            public void savedNetworkNotFound() {
                addNetwork(ssid, wifipass);
            }

            @Override
            public void retrievedSavedNetwork(@NotNull WifiConfiguration wifiConfiguration) {
                connectToNetwork(wifiConfiguration.SSID);
            }

            @Override
            public void wisefyFailure(int i) {
                Log.d(TAG, "wisefyFailure:");
            }
        });
    }

    private void connectToNetwork(String ssid) {
        PrathamApplication.wiseF.connectToNetwork(ssid, 10000, new ConnectToNetworkCallbacks() {
            @Override
            public void connectedToNetwork() {
                if (shareView != null)
                    shareView.onWifiConnected(ssid);
            }

            @Override
            public void failureConnectingToNetwork() {
                Log.d(TAG, "failureConnectingToNetwork: ");
            }

            @Override
            public void networkNotFoundToConnectTo() {
                Log.d(TAG, "networkNotFoundToConnectTo: ");
            }

            @Override
            public void wisefyFailure(int i) {
                Log.d(TAG, "wisefyFailure: ");
            }
        });
    }

    @Override
    public void connectToAddedSSID(String ssid) {
        if (shareView != null)
            shareView.onWifiConnected(ssid);
    }

    @Override
    public void connectionFailed() {
        if (shareView != null) shareView.ftpConnectionFailed();
    }

    @Override
    public void showFolders(Modal_ContentDetail detail) {
        if (detail == null) {
            getDownloadedContents(null, null);
        } else {
            levels.add(detail);
            if (levels.size() == 1) shareView.animateHamburger();
            getDownloadedContents(detail.getNodeid(), detail.getAltnodeid());
        }
    }

    @Override
    public void traverseFolderBackward() {
        if (levels.isEmpty()) {
            if (shareView != null) {
                shareView.animateHamburger();
                shareView.disconnectFTP();
            }
        } else {
            Modal_ContentDetail detail = levels.get(levels.size() - 1);
            getDownloadedContents(detail.getNodeid(), ""); //altnodeId is sent blank coz it points to its same node,
            // that's why their will be no child nodes
            levels.remove(levels.size() - 1);
        }
    }

    @Override
    public void downloadedContents(Object o, String parentId) {
        ArrayList<File_Model> downloads = new ArrayList<>();
        for (Modal_ContentDetail detail : (ArrayList<Modal_ContentDetail>) o) {
            File_Model model = new File_Model();
            model.setProgress(0);
            model.setDetail(detail);
            downloads.add(model);
        }
        if (PrathamApplication.isTablet)
            downloads.addAll(addProfileAndUsage());
        if (shareView != null)
            shareView.showFilesList(downloads, parentId);
    }

    private ArrayList<File_Model> addProfileAndUsage() {
        ArrayList<File_Model> downloads = new ArrayList<>();
        //Add Profile Item
        Modal_ContentDetail profile_detail = new Modal_ContentDetail();
        profile_detail.setNodeid(PD_Constant.SHARE_PROFILE);
        profile_detail.setContent_language(FastSave.getInstance().getString(PD_Constant.LANGUAGE, PD_Constant.HINDI));
        profile_detail.setOnSDCard(false);
        profile_detail.setNodeimage("");
        profile_detail.setContentType(PD_Constant.FOLDER);
        profile_detail.setNodetitle("Share Profiles");
        File_Model model = new File_Model();
        model.setProgress(0);
        model.setDetail(profile_detail);
        downloads.add(model);

        //Add Usage Item
        Modal_ContentDetail usage_detail = new Modal_ContentDetail();
        usage_detail.setNodeid(PD_Constant.SHARE_USAGE);
        usage_detail.setContent_language(FastSave.getInstance().getString(PD_Constant.LANGUAGE, PD_Constant.HINDI));
        usage_detail.setOnSDCard(false);
        usage_detail.setNodeimage("");
        usage_detail.setContentType(PD_Constant.FOLDER);
        usage_detail.setNodetitle("Share Tab Usage");
        File_Model model2 = new File_Model();
        model2.setProgress(0);
        model2.setDetail(usage_detail);
        downloads.add(model2);
        return downloads;
    }

    @Override
    public void connectFTP(String ip) {
        scanned_qr_ip = ip;
        connectToFTP.doInBackground(SharePresenter.this, scanned_qr_ip);
    }

    @Override
    public void onFTPConnected(boolean connected, FTPClient client) {
        if (connected) {
            this.ftpClient = client;
            startConnectionDisconnectlistener(client);
            if (shareView != null)
                shareView.ftpConnected_showFolders();
        } else {
            Log.d(TAG, "onFTPConnected: trying to connect again");
            new Handler().postDelayed(() -> {
                if (ftpConnectionTry < 5) {     //not more than 5 tries, otherwise infinite loop until success
                    ftpConnectionTry += 1;
                    if (scanned_qr_ip != null && !scanned_qr_ip.isEmpty())
                        connectFTP(scanned_qr_ip);
                } else {
                    if (shareView != null) shareView.ftpConnectionFailed();
                }
            }, 1000);
        }
    }

    @Override
    public void startTimer() {
        if (ftpClient != null) {
            ftpTimer = new Timer();
            startConnectionDisconnectlistener(ftpClient);
        } else {
            if (shareView != null)
                shareView.closeFTPJoin();
        }
    }

    @Background
    public void startConnectionDisconnectlistener(FTPClient client) {
        ftpTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    client.getStatus();
                    Log.d(TAG, "run::" + client.getStatus());
                } catch (Exception e) {
                    e.printStackTrace();
                    //connection lost with server
                    if (shareView != null)
                        shareView.closeFTPJoin();
                    if (ftpTimer != null)
                        ftpTimer.cancel();
                }
            }
        }, new Date(System.currentTimeMillis() + 2000), 2000);
    }

    @Background
    @Override
    public void sendFiles(Modal_ContentDetail detail) {
        if (ftpClient != null) {
            if (ftpTimer != null)
                ftpTimer.cancel();
            String dirPath;
            String imgDirPath;
            if (detail.isOnSDCard()) {
                dirPath = PrathamApplication.contentSDPath;
                imgDirPath = PrathamApplication.contentSDPath;
            } else {
                dirPath = PrathamApplication.pradigiPath;
                imgDirPath = PrathamApplication.pradigiPath;
            }
            //for content transfer
            dirPath += "/Pratham" + detail.getResourcetype() + "/";
            if (detail.getResourcetype().equalsIgnoreCase(PD_Constant.GAME)) {
                dirPath += detail.getResourcepath().split("/")[0];
            } else if (detail.getResourcetype().equalsIgnoreCase(PD_Constant.VIDEO)) {
                dirPath += detail.getResourcepath();
            } else if (detail.getResourcetype().equalsIgnoreCase(PD_Constant.PDF)) {
                dirPath += detail.getResourcepath();
            }
            File file = new File(dirPath);
            if (file.exists()) {
                File contentJson = createJsonFileForContent(detail);
                if (contentJson != null) {
//                    for Content Transfer
                    ftpContentUploadTask.doInBackground(ftpClient, dirPath, detail.getResourcetype(), detail.getNodeid());
//                    for content thumbnail Image Transfer
//                    imgDirPath += "/PrathamImages/" + detail.getNodeimage();
                    ArrayList<Modal_ContentDetail> temp = new ArrayList<>(levels);
                    temp.add(detail);
                    ftpSingleFileUploadTask.doInBackground(ftpClient, imgDirPath, true, temp);
//                    for file Json Transfer
                    ftpSingleFileUploadTask.doInBackground(ftpClient, contentJson.getAbsolutePath(), false, null);
                }
            } else {
                shareView.showFileNotFoundToast();
            }
        }
    }

    private File createJsonFileForContent(Modal_ContentDetail detail) {
        try {
            ArrayList<Modal_ContentDetail> temp = new ArrayList<>(levels);
            temp.add(detail);
            Gson gson = new GsonBuilder().create();
            JsonArray array = gson.toJsonTree(temp).getAsJsonArray();
            File path = context.getDir(PD_Constant.PRATHAM_TEMP_FILES, Context.MODE_PRIVATE);
            if (!path.exists()) path.mkdir();
            return createJsonFileOnInternal(array,
                    path.getAbsolutePath() + "/" + detail.getNodeid());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private File createJsonFileOnInternal(JsonArray array, String fileName) {
        try {
            File file = new File(fileName + ".json");
            FileWriter writer = new FileWriter(file.getAbsolutePath());
            writer.write(array.toString());
            writer.flush();
            writer.close();
            Log.d(TAG, "createJsonFileForContent:" + array.toString());
            return file;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void readRecievedFiles(File filePath) {
        //read recieved jsons and insert to DB
        copyExistingJSONS.doInBackground(filePath);
    }

    @Background
    @Override
    public void showFilesRecieving(File filePath) {
        Modal_ReceivingFilesThroughFTP modal = new Modal_ReceivingFilesThroughFTP();
        if (filePath.getAbsolutePath().contains("PrathamGame")) {
            String[] sub = filePath.getAbsolutePath().split("PrathamGame/");
            String game_name = sub[1].split("/")[0];
            String game_parts = sub[1];
            modal.setGameName(game_name);
            modal.setGamePart(game_parts);
            modal.setGameType(PD_Constant.GAME);
            modal.setReceived(false);
            filesRecieving.put(game_name, modal);
            if (shareView != null)
                shareView.showRecieving(new ArrayList<>(filesRecieving.values()));
        } else if (filePath.getAbsolutePath().contains("PrathamVideo")) {
            String[] sub = filePath.getAbsolutePath().split("PrathamVideo/");
            String vid_name = filePath.getName();
            String vid_parts = sub[1];
            modal.setGameName(vid_name);
            modal.setGamePart(vid_parts);
            modal.setGameType(PD_Constant.VIDEO);
            modal.setReceived(false);
            filesRecieving.put(vid_name, modal);
            if (shareView != null)
                shareView.showRecieving(new ArrayList<>(filesRecieving.values()));
        } else if (filePath.getAbsolutePath().contains("PrathamPDF")) {
            String[] sub = filePath.getAbsolutePath().split("PrathamPDF/");
            String pdf_name = filePath.getName();
            String pdf_parts = sub[1];
            modal.setGameName(pdf_name);
            modal.setGamePart(pdf_parts);
            modal.setGameType(PD_Constant.PDF);
            modal.setReceived(false);
            filesRecieving.put(pdf_name, modal);
            if (shareView != null)
                shareView.showRecieving(new ArrayList<>(filesRecieving.values()));
        } else if (filePath.getAbsolutePath().endsWith(".json")) {
            if (PD_Utility.isProfile(filePath.getName())) {
                modal.setGameName("Receiving Profiles");
                modal.setGamePart(filePath.getName());
                modal.setGameType(PD_Constant.PDF);
                modal.setReceived(false);
                filesRecieving.put("Receiving Profiles", modal);
                if (shareView != null)
                    shareView.showRecieving(new ArrayList<>(filesRecieving.values()));
            } else if (PD_Utility.isUsages(filePath.getName())) {
                modal.setGameName("Receiving Usages");
                modal.setGamePart(filePath.getName());
                modal.setGameType(PD_Constant.PDF);
                modal.setReceived(false);
                filesRecieving.put("Receiving Usages", modal);
                if (shareView != null)
                    shareView.showRecieving(new ArrayList<>(filesRecieving.values()));
            }
            readRecievedFiles(filePath);
        }
    }

    @Background
    @Override
    public void sendProfiles() {
        try {
            Gson gson = new GsonBuilder().create();
            File path = context.getDir(PD_Constant.PRATHAM_TEMP_FILES, Context.MODE_PRIVATE);
            if (!path.exists()) path.mkdir();
            //create village JSON file
            List<Modal_Village> villages = villageDao.getAllVillages();
            JsonArray vill_array = gson.toJsonTree(villages).getAsJsonArray();
            createJsonFileOnInternal(vill_array, path.getAbsolutePath() + "/villages");
            //create crl JSON file
            List<Modal_Crl> crLs = crLdao.getAllCRLs();
            JsonArray crl_array = gson.toJsonTree(crLs).getAsJsonArray();
            createJsonFileOnInternal(crl_array, path.getAbsolutePath() + "/crls");
            //create groups JSON file
            List<Modal_Groups> groups = groupDao.getAllGroups();
            JsonArray grp_array = gson.toJsonTree(groups).getAsJsonArray();
            createJsonFileOnInternal(grp_array, path.getAbsolutePath() + "/groups");
            //create students JSON file
            List<Modal_Student> students = studentDao.getAllStudents();
            JsonArray stu_array = gson.toJsonTree(students).getAsJsonArray();
            createJsonFileOnInternal(stu_array, path.getAbsolutePath() + "/students");
//            for file Json Transfer
            ftpSingleFileUploadTask.doInBackground(ftpClient, path.getAbsolutePath(), false, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Background
    @Override
    public void sendUsages() {
        try {
            Gson gson = new GsonBuilder().create();
            File path = context.getDir(PD_Constant.PRATHAM_TEMP_FILES, Context.MODE_PRIVATE);
            if (!path.exists()) path.mkdir();
            //create session JSON file
            List<Modal_Session> newSessions = sessionDao.getAllNewSessions();
            JsonArray session_array = gson.toJsonTree(newSessions).getAsJsonArray();
            createJsonFileOnInternal(session_array, path.getAbsolutePath() + "/sessions");
            //create logs JSON file
            List<Modal_Log> allLogs = logDao.getAllLogs();
            JsonArray log_array = gson.toJsonTree(allLogs).getAsJsonArray();
            createJsonFileOnInternal(log_array, path.getAbsolutePath() + "/logs");
            //create attendance JSON file
            List<Attendance> attendance = attendanceDao.getNewAttendances();
            JsonArray att_array = gson.toJsonTree(attendance).getAsJsonArray();
            createJsonFileOnInternal(att_array, path.getAbsolutePath() + "/attendance");
            //create score JSON file
            List<Modal_Score> newScores = scoreDao.getAllNewScores();
            JsonArray score_array = gson.toJsonTree(newScores).getAsJsonArray();
            createJsonFileOnInternal(score_array, path.getAbsolutePath() + "/scores");
            //create status JSON file
            List<Modal_Status> metadata = statusDao.getAllStatuses();
            JsonArray meta_array = gson.toJsonTree(metadata).getAsJsonArray();
            createJsonFileOnInternal(meta_array, path.getAbsolutePath() + "/status");
            // for file Json Transfer
            ftpSingleFileUploadTask.doInBackground(ftpClient, path.getAbsolutePath(), false, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Background
    public void getDownloadedContents(String parentId, String altnodeId) {
        String lang = FastSave.getInstance().getString(PD_Constant.LANGUAGE, PD_Constant.HINDI);
        List<Modal_ContentDetail> childsOfParent;
        if (parentId != null && !parentId.equalsIgnoreCase("0") && !parentId.isEmpty()) {
            childsOfParent = modalContentDao.getChildsOfParent(parentId, altnodeId, lang);
        } else {
            childsOfParent = modalContentDao.getParentsHeaders(lang);
        }
        downloadedContents(childsOfParent, parentId);
    }

    @Override
    public void scanFtp() {
    }
}
