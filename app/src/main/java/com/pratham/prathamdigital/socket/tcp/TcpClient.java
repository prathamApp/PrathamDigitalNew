package com.pratham.prathamdigital.socket.tcp;


import android.content.Context;
import android.os.Handler;

import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.socket.entity.FileState;
import com.pratham.prathamdigital.socket.entity.Message;
import com.pratham.prathamdigital.socket.udp.IPMSGConst;
import com.pratham.prathamdigital.util.PD_Constant;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class TcpClient implements Runnable {
    private static final String TAG = "Pratham_TcpClient";

    private Thread mThread;
    private boolean IS_THREAD_STOP = false;
    private boolean SEND_FLAG = false;
    private static Context mContext = null;
    private static TcpClient instance;
    // private ArrayList<FileStyle> fileStyles;
    // private ArrayList<FileState> fileStates;
    private ArrayList<SendFileThread> sendFileThreads;
    private SendFileThread sendFileThread;
    private static Handler mHandler = null;

    private TcpClient() {
        sendFileThreads = new ArrayList<SendFileThread>();
        mThread = new Thread(this);
    }

    public static void setHandler(Handler paramHandler) {
        mHandler = paramHandler;
    }

    public Thread getThread() {
        return mThread;
    }

    /**
     * <p>
     * TcpService
     * <p>
     */
    public static TcpClient getInstance(Context context) {
        mContext = context;
        if (instance == null) {
            instance = new TcpClient();
        }
        return instance;
    }

   /* public void sendFile(ArrayList<FileStyle> fileStyles, ArrayList<FileState> fileStates,
            String target_IP) {
        while (SEND_FLAG == true)
            ;

        for (FileStyle fileStyle : fileStyles) {
            SendFileThread sendFileThread = new SendFileThread(target_IP, fileStyle.fullPath);
            sendFileThreads.add(sendFileThread);
        }
        SEND_FLAG = true;
    }*/

    private TcpClient(Context context) {
        this();
    }

    public void startSend() {
        IS_THREAD_STOP = false;
        if (!mThread.isAlive())
            mThread.start();
    }

    public void sendFile(String filePath, String target_IP) {
        SendFileThread sendFileThread = new SendFileThread(target_IP, filePath);
        while (SEND_FLAG == true)
            ;
        sendFileThreads.add(sendFileThread);
        SEND_FLAG = true;
    }

    public void sendFile(String filePath, String target_IP, Message.CONTENT_TYPE type) {
        SendFileThread sendFileThread = new SendFileThread(target_IP, filePath, type);
        while (SEND_FLAG == true)
            ;
        sendFileThreads.add(sendFileThread);
        FileState sendFileState = new FileState(filePath);
        PrathamApplication.sendFileStates.put(filePath, sendFileState);
        SEND_FLAG = true;
    }

    @Override
    public void run() {
        while (!IS_THREAD_STOP) {
            if (SEND_FLAG) {
                for (SendFileThread sendFileThread : sendFileThreads) {
                    sendFileThread.start();
                }
                sendFileThreads.clear();
                SEND_FLAG = false;
            }

        }
    }

    public void release() {
        while (SEND_FLAG == true)
            ;
        while (sendFileThread.isAlive())
            ;
        IS_THREAD_STOP = false;
    }

    public class SendFileThread extends Thread {
        private static final String TAG = "Pratham_SendFileThread";
        private boolean SEND_FLAG = true;
        private byte[] mBuffer = new byte[PD_Constant.READ_BUFFER_SIZE];
        private OutputStream output = null;
        private DataOutputStream dataOutput;
        private FileInputStream fileInputStream;
        private Socket socket = null;
        private String target_IP;
        private String filePath;
        private Message.CONTENT_TYPE type;

        public SendFileThread(String target_IP, String filePath) {
            this.target_IP = target_IP;
            this.filePath = filePath;
        }

        public SendFileThread(String target_IP, String filePath, Message.CONTENT_TYPE type) {
            this(target_IP, filePath);
            this.type = type;
        }

        public void sendFile() {
            int readSize = 0;
            try {
                socket = new Socket(target_IP, PD_Constant.TCP_SERVER_RECEIVE_PORT);
                fileInputStream = new FileInputStream(new File(filePath));
                output = socket.getOutputStream();
                dataOutput = new DataOutputStream(output);
                int fileSize = fileInputStream.available();
                dataOutput.writeUTF(filePath.substring(filePath.lastIndexOf(File.separator) + 1)
                        + "!" + fileSize + "!IMEI!" + type);
                int count = 0;
                long length = 0;

                FileState fs = PrathamApplication.sendFileStates.get(filePath);
                fs.fileSize = fileSize;
                fs.type = type;
                while (-1 != (readSize = fileInputStream.read(mBuffer))) {
                    length += readSize;
                    dataOutput.write(mBuffer, 0, readSize);
                    count++;
                    fs.percent = (int) (length * 100 / fileSize);

                    switch (type) {
                        case IMAGE:
                            break;
                        case VOICE:
                            break;
                        case VEDIO:
                        case MUSIC:
                        case APK:
                        case FILE:
                            android.os.Message msg = mHandler.obtainMessage();
                            msg.what = IPMSGConst.WHAT_FILE_SENDING;
                            msg.obj = fs;
                            msg.sendToTarget();
                            break;

                        default:
                            break;
                    }
                    dataOutput.flush();
                }

                output.close();
                dataOutput.close();
                socket.close();

                switch (type) {
                   /* case IMAGE:
                        Message imageMsg = new Message("",
                                DateUtils.getNowtime(), fs.fileName, type);
                        imageMsg.setMsgContent(FileUtils.getNameByPath(imageMsg.getMsgContent()));
                        UDPMessageListener.sendUDPdata(IPMSGConst.IPMSG_SENDMSG, target_IP, imageMsg);
                        break;

                    case VOICE:
                        Message voiceMsg = new Message(SessionUtils.getIMEI(),
                                DateUtils.getNowtime(), fs.fileName, type);
                        voiceMsg.setMsgContent(FileUtils.getNameByPath(voiceMsg.getMsgContent()));
                        UDPMessageListener.sendUDPdata(IPMSGConst.IPMSG_SENDMSG, target_IP, voiceMsg);
                        break;*/
                    case VEDIO:
                    case MUSIC:
                    case APK:
                    case FILE:
                        android.os.Message msg = mHandler.obtainMessage();
                        msg.what = IPMSGConst.WHAT_FILE_SENDING;
                        fs.percent = 100;
                        msg.obj = fs;
                        msg.sendToTarget();
                        break;

                    default:
                        break;
                }

                PrathamApplication.sendFileStates.remove(fs.filePath);
            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                SEND_FLAG = false;
                e.printStackTrace();
            } catch (IOException e) {
                SEND_FLAG = false;
                e.printStackTrace();
            } finally {
                // IS_THREAD_STOP=true;
            }
        }

        @Override
        public void run() {
            if (SEND_FLAG) {
                sendFile();
            }
        }
    }
}
