package com.pratham.prathamdigital.socket.udp;


import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.socket.entity.Message;
import com.pratham.prathamdigital.socket.entity.Users;
import com.pratham.prathamdigital.socket.tcp.TcpClient;
import com.pratham.prathamdigital.socket.tcp.TcpService;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UDPMessageListener implements Runnable {

    private static final String TAG = "Pratham_UDPMessageListener";

    private static final int POOL_SIZE = 5;
    private static final int BUFFERLENGTH = 1024;

    private static byte[] sendBuffer = new byte[BUFFERLENGTH];
    private static byte[] receiveBuffer = new byte[BUFFERLENGTH];

    private HashMap<String, String> mLastMsgCache;
    private ArrayList<Users> mUnReadPeopleList;
    private HashMap<String, Users> mOnlineUsers;

    private String BROADCASTIP;
    private Thread receiveUDPThread;
    private boolean isThreadRunning;
    private List<OnNewMsgListener> mListenerList;

    private Users mLocalUser;
//    private SqlDBOperate mDBOperate;

    private static ExecutorService executor;
    private static DatagramSocket UDPSocket;
    private static DatagramPacket sendDatagramPacket;
    private DatagramPacket receiveDatagramPacket;

    private static Context mContext;
    private static UDPMessageListener instance;
    private Handler mHanlder;


    private UDPMessageListener() {
        BROADCASTIP = "255.255.255.255";
        // BROADCASTIP = WifiUtils.getBroadcastAddress();

//        mDBOperate = new SqlDBOperate(mContext);
        mListenerList = new ArrayList<OnNewMsgListener>();
        mOnlineUsers = new LinkedHashMap<String, Users>();
        mLastMsgCache = new HashMap<String, String>();
        mUnReadPeopleList = new ArrayList<Users>();

        int cpuNums = Runtime.getRuntime().availableProcessors();
        executor = Executors.newFixedThreadPool(cpuNums * POOL_SIZE);

    }

    /**
     * <p/>
     * UDPSocketThread
     * <p/>
     *
     * @return instance
     */
    public static UDPMessageListener getInstance(Context context) {
        if (instance == null) {
            mContext = context;
            instance = new UDPMessageListener();
        }
        return instance;
    }

    @Override
    public void run() {
        while (isThreadRunning) {

            try {
                UDPSocket.receive(receiveDatagramPacket);
            } catch (IOException e) {
                isThreadRunning = false;
                receiveDatagramPacket = null;
                if (UDPSocket != null) {
                    UDPSocket.close();
                    UDPSocket = null;
                }
                receiveUDPThread = null;
                e.printStackTrace();
                break;
            }

            if (receiveDatagramPacket.getLength() == 0) {
                continue;
            }

            String resStr = "";
            try {
                resStr = new String(receiveBuffer, 0, receiveDatagramPacket.getLength(), "gbk");
            } catch (UnsupportedEncodingException e) {
            }

            String senderIp = receiveDatagramPacket.getAddress().getHostAddress();
            IPMSGProtocol ipmsgRes = PD_Utility.jsonToBean(resStr, IPMSGProtocol.class);
            processMessage(ipmsgRes, senderIp);

            if (receiveDatagramPacket != null) {
                receiveDatagramPacket.setLength(BUFFERLENGTH);
            }

        }//while

        receiveDatagramPacket = null;
        if (UDPSocket != null) {
            UDPSocket.close();
            UDPSocket = null;
        }
        receiveUDPThread = null;

    }

    public void processMessage(IPMSGProtocol ipmsgRes, String senderIp) {

        int commandNo = ipmsgRes.commandNo;

        TcpService tcpService = TcpService.getInstance(mContext);
        tcpService.setHandler(mHanlder);

        TcpClient tcpClient = TcpClient.getInstance(mContext);
        tcpClient.setHandler(mHanlder);

        switch (commandNo) {

            case IPMSGConst.NO_CONNECT_SUCCESS: {
                sendUDPdata(getConfirmCommand(IPMSGConst.AN_CONNECT_SUCCESS, ipmsgRes.targetIP, senderIp));
            }
            break;

            case IPMSGConst.NO_SEND_TXT: {
                Message textMsg = ipmsgRes.addObject;
                Intent intent = new Intent(PD_Constant.ACTION_NEW_MSG);
                intent.putExtra(PD_Constant.EXTRA_NEW_MSG_TYPE, PD_Constant.NEW_MSG_TYPE_TXT);
                intent.putExtra(PD_Constant.EXTRA_NEW_MSG_CONTENT, textMsg.getMsgContent());
                mContext.sendBroadcast(intent);

                sendUDPdata(getConfirmCommand(IPMSGConst.AN_SEND_TXT, ipmsgRes.targetIP, senderIp));
            }
            break;

            case IPMSGConst.NO_SEND_IMAGE: {
                tcpService.setSavePath(PrathamApplication.IMAG_PATH);
                tcpService.startReceive();

                IPMSGProtocol command = getConfirmCommand(IPMSGConst.AN_SEND_IMAGE, ipmsgRes.targetIP, senderIp);
                command.addObject = ipmsgRes.addObject;
                sendUDPdata(command);
            }
            break;

            case IPMSGConst.NO_SEND_VOICE: {
                tcpService.setSavePath(PrathamApplication.VOICE_PATH);
                tcpService.startReceive();

                IPMSGProtocol command = getConfirmCommand(IPMSGConst.AN_SEND_VOICE, ipmsgRes.targetIP, senderIp);
                command.addObject = ipmsgRes.addObject;
                sendUDPdata(command);
            }
            break;

            case IPMSGConst.NO_SEND_VEDIO: {
                tcpService.setSavePath(PrathamApplication.VEDIO_PATH);
                tcpService.startReceive();

                IPMSGProtocol command = getConfirmCommand(IPMSGConst.AN_SEND_VEDIO, ipmsgRes.targetIP, senderIp);
                command.addObject = ipmsgRes.addObject;
                sendUDPdata(command);

                Intent intent = new Intent(PD_Constant.ACTION_NEW_MSG);
                intent.putExtra(PD_Constant.EXTRA_NEW_MSG_TYPE, PD_Constant.NEW_MSG_TYPE_VEDIO);
                intent.putExtra(PD_Constant.EXTRA_NEW_MSG_CONTENT, ipmsgRes.addObject.getMsgContent());
                mContext.sendBroadcast(intent);
            }
            break;
            case IPMSGConst.NO_SEND_MUSIC: {
                tcpService.setSavePath(PrathamApplication.MUSIC_PATH);
                tcpService.startReceive();

                IPMSGProtocol command = getConfirmCommand(IPMSGConst.AN_SEND_MUSIC, ipmsgRes.targetIP, senderIp);
                command.addObject = ipmsgRes.addObject;
                sendUDPdata(command);

                Intent intent = new Intent(PD_Constant.ACTION_NEW_MSG);
                intent.putExtra(PD_Constant.EXTRA_NEW_MSG_TYPE, PD_Constant.NEW_MSG_TYPE_MUSIC);
                intent.putExtra(PD_Constant.EXTRA_NEW_MSG_CONTENT, ipmsgRes.addObject.getMsgContent());
                mContext.sendBroadcast(intent);
            }
            break;
            case IPMSGConst.NO_SEND_FILE: {
                tcpService.setSavePath(PrathamApplication.FILE_PATH);
                tcpService.startReceive();

                IPMSGProtocol command = getConfirmCommand(IPMSGConst.AN_SEND_FILE, ipmsgRes.targetIP, senderIp);
                command.addObject = ipmsgRes.addObject;
                sendUDPdata(command);

                Intent intent = new Intent(PD_Constant.ACTION_NEW_MSG);
                intent.putExtra(PD_Constant.EXTRA_NEW_MSG_TYPE, PD_Constant.NEW_MSG_TYPE_FILE);
                intent.putExtra(PD_Constant.EXTRA_NEW_MSG_CONTENT, ipmsgRes.addObject.getMsgContent());
                mContext.sendBroadcast(intent);
            }
            break;

            case IPMSGConst.AN_CONNECT_SUCCESS: {
            }
            break;

            case IPMSGConst.AN_SEND_TXT: {
            }
            break;

            case IPMSGConst.AN_SEND_IMAGE: {
                Message textMsg = ipmsgRes.addObject;
                tcpClient.startSend();
                tcpClient.sendFile(textMsg.getMsgContent(), senderIp, Message.CONTENT_TYPE.IMAGE);
            }
            break;

            case IPMSGConst.AN_SEND_VOICE: { //服务器确认成功接收图片
                Message textMsg = ipmsgRes.addObject;
                tcpClient.startSend();
                tcpClient.sendFile(textMsg.getMsgContent(), senderIp, Message.CONTENT_TYPE.VOICE);
            }
            break;

            case IPMSGConst.AN_SEND_VEDIO: {
                Message textMsg = ipmsgRes.addObject;
                tcpClient.startSend();
                tcpClient.sendFile(textMsg.getMsgContent(), senderIp, Message.CONTENT_TYPE.VEDIO);
            }
            break;
            case IPMSGConst.AN_SEND_MUSIC: {
                Message textMsg = ipmsgRes.addObject;
                tcpClient.startSend();
                tcpClient.sendFile(textMsg.getMsgContent(), senderIp, Message.CONTENT_TYPE.MUSIC);
            }
            break;
            case IPMSGConst.AN_SEND_FILE: {
                Message textMsg = ipmsgRes.addObject;
                tcpClient.startSend();
                tcpClient.sendFile(textMsg.getMsgContent(), senderIp, Message.CONTENT_TYPE.FILE);
            }
            break;

        } // End of switch
        callBack(ipmsgRes);
    }

    /**
     * @param commandNo
     * @param senderIp
     * @param targetIP
     */
    private IPMSGProtocol getConfirmCommand(int commandNo, String senderIp, String targetIP) {
        IPMSGProtocol command = new IPMSGProtocol();
        command.commandNo = commandNo;
        command.senderIP = senderIp;
        command.targetIP = targetIP;
        command.packetNo = new Date().getTime() + "";
        return command;
    }

    /**
     * @param ipmsgRes
     */
    private void callBack(IPMSGProtocol ipmsgRes) {
        //  showToast("listenerSize=" + mListenerList.size());
        for (int i = 0; i < mListenerList.size(); i++) {
            mListenerList.get(i).processMessage(ipmsgRes);
        }
    }

    /**
     */
    public void connectUDPSocket() {
        try {
            if (UDPSocket == null)
                UDPSocket = new DatagramSocket(IPMSGConst.PORT);

            if (receiveDatagramPacket == null)
                receiveDatagramPacket = new DatagramPacket(receiveBuffer, BUFFERLENGTH);

            startUDPSocketThread();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    /**
     */
    public void startUDPSocketThread() {
        if (receiveUDPThread == null) {
            receiveUDPThread = new Thread(this);
            receiveUDPThread.start();
        }
        isThreadRunning = true;
    }

    /**
     */
    public void stopUDPSocketThread() {
        isThreadRunning = false;
        if (receiveUDPThread != null)
            receiveUDPThread.interrupt();
        receiveUDPThread = null;
        instance = null;
    }

    public void addMsgListener(OnNewMsgListener listener) {
        this.mListenerList.add(listener);
    }

    public void removeMsgListener(OnNewMsgListener listener) {
        this.mListenerList.remove(listener);
    }


    /**
     *
     */
    public static void sendUDPdata(final IPMSGProtocol ipmsgProtocol) {

        final String targetIP = ipmsgProtocol.targetIP;

        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    InetAddress targetAddr = InetAddress.getByName(targetIP);
                    sendBuffer = PD_Utility.beanToJson(ipmsgProtocol).getBytes("gbk");
                    sendDatagramPacket = new DatagramPacket(sendBuffer, sendBuffer.length, targetAddr, IPMSGConst.PORT);
                    UDPSocket.send(sendDatagramPacket);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }


    public Users getOnlineUser(String paramIMEI) {
        return mOnlineUsers.get(paramIMEI);
    }


    public HashMap<String, Users> getOnlineUserMap() {
        return mOnlineUsers;
    }

    /**
     */
    public void addLastMsgCache(String paramIMEI, Message msg) {
        StringBuffer content = new StringBuffer();
        switch (msg.getContentType()) {
            case FILE:
                content.append("<FILE>: ").append(msg.getMsgContent());
                break;
            case IMAGE:
                content.append("<IMAGE>: ").append(msg.getMsgContent());
                break;
            case VOICE:
                content.append("<VOICE>: ").append(msg.getMsgContent());
                break;
            default:
                content.append(msg.getMsgContent());
                break;
        }
        if (msg.getMsgContent().isEmpty()) {
            content.append(" ");
        }
        mLastMsgCache.put(paramIMEI, content.toString());
    }

    /**
     */
    public String getLastMsgCache(String paramIMEI) {
        return mLastMsgCache.get(paramIMEI);
    }

    /**
     */
    public void removeLastMsgCache(String paramIMEI) {
        mLastMsgCache.remove(paramIMEI);
    }

    public void clearMsgCache() {
        mLastMsgCache.clear();
    }

    public void clearUnReadMessages() {
        mUnReadPeopleList.clear();
    }

    /**
     * ng
     *
     * @param people
     */
    public void addUnReadPeople(Users people) {
        if (!mUnReadPeopleList.contains(people))
            mUnReadPeopleList.add(people);
    }

    /**
     * @return
     */
    public ArrayList<Users> getUnReadPeopleList() {
        return mUnReadPeopleList;
    }

    /**
     * @return
     */
    public int getUnReadPeopleSize() {
        return mUnReadPeopleList.size();
    }

    /**
     * @param people
     */
    public void removeUnReadPeople(Users people) {
        if (mUnReadPeopleList.contains(people))
            mUnReadPeopleList.remove(people);
    }

    public void setHandler(Handler mHandler) {
        this.mHanlder = mHandler;
    }

    /**
     */
    public interface OnNewMsgListener {
        void processMessage(IPMSGProtocol pMsg);
    }

    public void showToast(final String s) {
      /*  ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                T.show(mContext, s);
            }
        });*/
    }
}