package com.pratham.prathamdigital.ui.fragment_share_recieve;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiConfiguration;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.isupatches.wisefy.callbacks.AddNetworkCallbacks;
import com.isupatches.wisefy.callbacks.ConnectToNetworkCallbacks;
import com.isupatches.wisefy.callbacks.GetSavedNetworkCallbacks;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.async.CopyExistingJSONS;
import com.pratham.prathamdigital.async.FTPFileJsonUploadTask;
import com.pratham.prathamdigital.async.FTPFileUploadTask;
import com.pratham.prathamdigital.async.FTPImageUploadTask;
import com.pratham.prathamdigital.async.GetDownloadedContent;
import com.pratham.prathamdigital.ftpSettings.ConnectToFTP;
import com.pratham.prathamdigital.interfaces.DownloadedContents;
import com.pratham.prathamdigital.interfaces.FTPConnected;
import com.pratham.prathamdigital.models.File_Model;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.services.MessageReveiver;
import com.pratham.prathamdigital.socket.udp.IPMSGConst;
import com.pratham.prathamdigital.socket.udp.IPMSGProtocol;
import com.pratham.prathamdigital.socket.udp.UDPMessageListener;
import com.pratham.prathamdigital.util.PD_Constant;

import org.apache.commons.net.ftp.FTPClient;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SharePresenter implements DownloadedContents, ContractShare.sharePresenter, FTPConnected {
    private static final String TAG = SharePresenter.class.getSimpleName();
    Context context;
    ContractShare.shareView shareView;
    FTPClient ftpClient;
    ArrayList<Modal_ContentDetail> levels = new ArrayList<>();

    public SharePresenter(Context context, ContractShare.shareView shareView) {
        this.context = context;
        this.shareView = shareView;
    }

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
//            startConnectionDisconnectlistener(client);
        }
    }

//    private void startConnectionDisconnectlistener(FTPClient client) {
//        new Timer().schedule(new TimerTask() {
//            @SuppressLint("MissingPermission")
//            @Override
//            public void run() {
//                String name = PrathamApplication.wiseF.getCurrentNetwork().getSSID();
//                try {
//                    if (!name.contains("Pratham_")) {
//                        cancel();
//                        ftpClient.disconnect();
//                        shareView.closeFTPJoin();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new Date(System.currentTimeMillis() + 2500), 2500);
//    }

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
                    FTPFileUploadTask task = new FTPFileUploadTask(context, ftpClient, dirPath,
                            detail.getResourcetype(), detail.getNodeid());
                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
//                    //for content Image Transfer
                    imgDirPath += "/PrathamImages/" + detail.getNodeimage();
                    FTPImageUploadTask imgtask = new FTPImageUploadTask(context, ftpClient, imgDirPath);
                    imgtask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
//                    //for file Json Transfer
                    FTPFileJsonUploadTask jsontask = new FTPFileJsonUploadTask(context, ftpClient, contentJson.getAbsolutePath());
                    jsontask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
                }
            } else {
                Toast.makeText(context, "File not found.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private File createJsonFileForContent(Modal_ContentDetail detail) {
        try {
            JSONArray array = new JSONArray();
            ArrayList<Modal_ContentDetail> temp = levels;
            temp.add(detail);
            Gson gson = new Gson();
            for (Modal_ContentDetail d : temp) {
                String toJson = gson.toJson(d);
                array.put(toJson);
            }
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
    public void readRecievedFiles() {
        //read recieved jsons and insert to DB
        new CopyExistingJSONS(context, null).execute();
    }
}
