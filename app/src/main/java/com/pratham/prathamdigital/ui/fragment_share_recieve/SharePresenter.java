package com.pratham.prathamdigital.ui.fragment_share_recieve;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiConfiguration;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

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
import com.pratham.prathamdigital.async.GetDownloadedContent;
import com.pratham.prathamdigital.ftpSettings.ConnectToFTP;
import com.pratham.prathamdigital.interfaces.DownloadedContents;
import com.pratham.prathamdigital.interfaces.FTPConnected;
import com.pratham.prathamdigital.models.File_Model;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.models.Modal_ReceivingFilesThroughFTP;
import com.pratham.prathamdigital.services.MessageReveiver;
import com.pratham.prathamdigital.socket.udp.IPMSGConst;
import com.pratham.prathamdigital.socket.udp.IPMSGProtocol;
import com.pratham.prathamdigital.socket.udp.UDPMessageListener;
import com.pratham.prathamdigital.util.PD_Constant;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.apache.commons.net.ftp.FTPClient;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

@EBean
public class SharePresenter implements DownloadedContents, ContractShare.sharePresenter, FTPConnected {
    private static final String TAG = SharePresenter.class.getSimpleName();
    Context context;
    ContractShare.shareView shareView;
    FTPClient ftpClient;
    ArrayList<Modal_ContentDetail> levels = new ArrayList<>();
    HashMap<String, Modal_ReceivingFilesThroughFTP> filesRecieving = new HashMap<>();

    public SharePresenter(Context context) {
        this.context = context;
    }

    @Override
    public void setView(FragmentShareRecieve fragmentShareRecieve) {
        this.shareView = (ContractShare.shareView) fragmentShareRecieve;
    }

    @Background
    @Override
    public void connectToWify(String ssid) {
        PrathamApplication.wiseF.getSavedNetwork(ssid, new GetSavedNetworkCallbacks() {
            @Override
            public void savedNetworkNotFound() {
                addNetwork(ssid);
            }

            @Override
            public void retrievedSavedNetwork(@NotNull WifiConfiguration wifiConfiguration) {
                connectToNetwork(ssid);
            }

            @Override
            public void wisefyFailure(int i) {
                Log.d(TAG, "wisefyFailure:");
            }
        });
    }

    private void addNetwork(String ssid) {
        PrathamApplication.wiseF.addWPA2Network(ssid, PD_Constant.WIFI_AP_PASSWORD, new AddNetworkCallbacks() {
            @Override
            public void failureAddingNetwork(int i) {
                Log.d(TAG, "failureAddingNetwork: ");
            }

            @Override
            public void networkAdded(int i, @NotNull WifiConfiguration wifiConfiguration) {
                connectToNetwork(ssid);
            }

            @Override
            public void wisefyFailure(int i) {
                Log.d(TAG, "wisefyFailure: ");
            }
        });
    }

    private void connectToNetwork(String ssid) {
        PrathamApplication.wiseF.connectToNetwork(ssid, 10000, new ConnectToNetworkCallbacks() {
            @Override
            public void connectedToNetwork() {
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
    public void showFolders(Modal_ContentDetail detail) {
        if (detail == null) {
            new GetDownloadedContent(SharePresenter.this, null).execute();
        } else {
            levels.add(detail);
            new GetDownloadedContent(SharePresenter.this, detail.getNodeid()).execute();
        }
    }

    @Override
    public void traverseFolderBackward() {
        if (levels.isEmpty()) {
            shareView.disconnectFTP();
        } else {
            new GetDownloadedContent(SharePresenter.this, levels.get(levels.size() - 1).getNodeid()).execute();
            levels.remove(levels.size() - 1);
        }
    }

    public void registerListener(FragmentShareRecieve fragmentShareRecieve) {
        MessageReveiver messageReveiver = new MessageReveiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(PD_Constant.ACTION_NEW_MSG);
        context.registerReceiver(messageReveiver, filter);

        UDPMessageListener udpMessageListener = UDPMessageListener.getInstance(context);
        udpMessageListener.addMsgListener(fragmentShareRecieve);
    }

    public void sendMessage(String content, com.pratham.prathamdigital.socket.entity.Message.CONTENT_TYPE type, String localIPaddress, String serverIPaddres) {
        String nowtime = new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date());
        IPMSGProtocol command = new IPMSGProtocol();
        command.targetIP = serverIPaddres;
        command.senderIP = localIPaddress;
        command.packetNo = new Date().getTime() + "";
        command.addObject = new com.pratham.prathamdigital.socket.entity.Message("", nowtime, content, type);
        command.commandNo = IPMSGConst.NO_SEND_TXT;
        UDPMessageListener.sendUDPdata(command);
    }

    @Background
    @Override
    public void downloadedContents(Object o, String parentId) {
        ArrayList<File_Model> downloads = new ArrayList<>();
        for (Modal_ContentDetail detail : (ArrayList<Modal_ContentDetail>) o) {
            File_Model model = new File_Model();
            model.setProgress(0);
            model.setDetail(detail);
            downloads.add(model);
        }
        shareView.showFilesList(downloads, parentId);
    }

    @Override
    public void connectFTP() {
        new ConnectToFTP(context, SharePresenter.this).execute();
    }

    @Override
    public void onFTPConnected(boolean connected, FTPClient client) {
        if (connected) {
            this.ftpClient = client;
            startConnectionDisconnectlistener(client);
        }
    }

    @Background
    public void startConnectionDisconnectlistener(FTPClient client) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    client.getStatus();
                    Log.d(TAG, "run::" + client.getStatus());
                } catch (Exception e) {
                    e.printStackTrace();
                    shareView.closeFTPJoin();
                    timer.cancel();
                }
            }
        }, new Date(System.currentTimeMillis() + 2000), 2000);

    }

    @Background
    @Override
    public void sendFiles(Modal_ContentDetail detail) {
        if (ftpClient != null) {
            String dirPath = "";
            String imgDirPath = "";
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
                    FTPContentUploadTask contentTask = new FTPContentUploadTask(context, ftpClient, dirPath,
                            detail.getResourcetype(), detail.getNodeid());
                    contentTask.execute();
////                    for content Image Transfer
                    imgDirPath += "/PrathamImages/" + detail.getNodeimage();
                    FTPSingleFileUploadTask imgtask = new FTPSingleFileUploadTask(context, ftpClient, imgDirPath, true);
                    imgtask.execute();
////                    for file Json Transfer
                    FTPSingleFileUploadTask jsontask = new FTPSingleFileUploadTask(context, ftpClient, contentJson.getAbsolutePath(), false);
                    jsontask.execute();
                }
            } else {
                Toast.makeText(context, "File not found.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private File createJsonFileForContent(Modal_ContentDetail detail) {
        try {
            ArrayList<Modal_ContentDetail> temp = new ArrayList<>();
            temp.addAll(levels);
            temp.add(detail);
            Gson gson = new GsonBuilder().create();
            JsonArray array = gson.toJsonTree(temp).getAsJsonArray();
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + detail.getNodetitle() + ".json");
            FileWriter writer = new FileWriter(file.getAbsolutePath());
            writer.write(array.toString());
            writer.flush();
            writer.close();
            Log.d(TAG, "createJsonFileForContent:" + array.toString());
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void readRecievedFiles(File filePath) {
        //read recieved jsons and insert to DB
        new CopyExistingJSONS(context, filePath).execute();
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
            filesRecieving.put(game_name, modal);
            shareView.showRecieving(new ArrayList<Modal_ReceivingFilesThroughFTP>(filesRecieving.values()));
        } else if (filePath.getAbsolutePath().contains("PrathamVideo")) {
            String[] sub = filePath.getAbsolutePath().split("PrathamVideo/");
            String vid_name = filePath.getName();
            String vid_parts = sub[1];
            modal.setGameName(vid_name);
            modal.setGamePart(vid_parts);
            modal.setGameType(PD_Constant.VIDEO);
            filesRecieving.put(vid_name, modal);
            shareView.showRecieving(new ArrayList<Modal_ReceivingFilesThroughFTP>(filesRecieving.values()));
        } else if (filePath.getAbsolutePath().contains("PrathamPDF")) {
            String[] sub = filePath.getAbsolutePath().split("PrathamPDF/");
            String pdf_name = filePath.getName();
            String pdf_parts = sub[1];
            modal.setGameName(pdf_name);
            modal.setGamePart(pdf_parts);
            modal.setGameType(PD_Constant.PDF);
            filesRecieving.put(pdf_name, modal);
            shareView.showRecieving(new ArrayList<Modal_ReceivingFilesThroughFTP>(filesRecieving.values()));
        } else if (filePath.getAbsolutePath().endsWith(".json")) {
            readRecievedFiles(filePath);
        }
    }
}
