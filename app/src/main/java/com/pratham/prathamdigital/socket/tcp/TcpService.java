package com.pratham.prathamdigital.socket.tcp;


import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.socket.entity.FileState;
import com.pratham.prathamdigital.socket.entity.Message;
import com.pratham.prathamdigital.socket.udp.IPMSGConst;
import com.pratham.prathamdigital.util.PD_Constant;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class TcpService implements Runnable {
    private static final String TAG = "Pratham_TcpService";

    private ServerSocket serviceSocket;
    private boolean SCAN_FLAG = false;
    private Thread mThread;
    ArrayList<FileState> receivedFileNames;
    ArrayList<SaveFileToDisk> saveFileToDisks;
    private static Handler mHandler;
    private String filePath = null;

    private static Context mContext;
    private static TcpService instance;

    private boolean IS_THREAD_STOP = false;

    private TcpService() {
        try {
            serviceSocket = new ServerSocket(PD_Constant.TCP_SERVER_RECEIVE_PORT);
            saveFileToDisks = new ArrayList<SaveFileToDisk>();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mThread = new Thread(this);
    }

    /**
     * <p>
     * TcpService
     * <p>
     */
    public static TcpService getInstance(Context context) {
        mContext = context;
        if (instance == null) {
            instance = new TcpService();
        }
        return instance;
    }

    public static void setHandler(Handler paramHandler) {
        mHandler = paramHandler;
    }

    public void setSavePath(String fileSavePath) {
        this.filePath = fileSavePath;
        // REV_FLAG=true;
    }

    public TcpService(Context context) {
        this();
        mContext = context;
    }

    private void scan_recv() {
        try {
            Socket socket = serviceSocket.accept(); // UDP
            // socket.setSoTimeout(5000);

            SaveFileToDisk fileToDisk = new SaveFileToDisk(socket, filePath);
            fileToDisk.start();

        } catch (IOException e) {
            e.printStackTrace();
            SCAN_FLAG = false;
        }
    }

    @Override
    public void run() {
        while (!IS_THREAD_STOP) {
            if (SCAN_FLAG) {
                scan_recv();
            }
        }
    }

    public void release() {
        if (null != serviceSocket && !serviceSocket.isClosed())
            try {
                serviceSocket.close();
                serviceSocket = null;
            } catch (IOException e) {
                // Auto-generated catch block
                e.printStackTrace();
            }
        while (SCAN_FLAG == true)
            ;
        SCAN_FLAG = false;
        IS_THREAD_STOP = true;
    }

    public void startReceive() {
        SCAN_FLAG = true;
        if (!mThread.isAlive())
            mThread.start();
    }

    public void startReceive(ArrayList<FileState> receivedFileNames) {
        SCAN_FLAG = true;
        if (!mThread.isAlive())
            mThread.start();
        this.receivedFileNames = receivedFileNames;
    }

    public void stopReceive() {
        while (SCAN_FLAG == true)
            ;
        SCAN_FLAG = false;
    }

    public class SaveFileToDisk extends Thread {
        private boolean SCAN_RECIEVE = true;
        private InputStream input = null;
        private DataInputStream dataInput;
        private byte[] mBuffer = new byte[PD_Constant.READ_BUFFER_SIZE];
        private String savePath;
        private String type[] = {"TEXT", "IMAGE", "FILE", "VOICE", "VEDIO", "MUSIC", "APK"};

        public SaveFileToDisk(Socket socket) {
            try {
                input = socket.getInputStream();
                dataInput = new DataInputStream(input);
            } catch (IOException e) {
                // Auto-generated catch block
                SCAN_RECIEVE = false;
                e.printStackTrace();
            }
        }

        public SaveFileToDisk(Socket socket, String savePath) {
            this(socket);
            this.savePath = savePath;
        }

        public void recieveFile() {
            int readSize = 0;
            FileOutputStream fileOutputStream = null;
            BufferedOutputStream bufferOutput = null;
            String strFiledata;
            String[] strData = null;
            String fileSavePath;

            try {
                strFiledata = dataInput.readUTF().toString();
                strData = strFiledata.split("!");
                long length = Long.parseLong(strData[1]);

//                fileSavePath = savePath + File.separator + strData[2] + File.separator + strData[0];
                fileSavePath = savePath + File.separator + strData[0];
                fileOutputStream = new FileOutputStream(new File(fileSavePath));
                FileState fileState = new FileState(length, 0, fileSavePath, getType(strData[3]));
                PrathamApplication.recieveFileStates.put(fileSavePath, fileState);
                FileState fs = PrathamApplication.recieveFileStates.get(fileSavePath);
                bufferOutput = new BufferedOutputStream(fileOutputStream);
                long lastLength = 0;
                long currentLength = 0;
                long lastTime = System.currentTimeMillis();
                long currentTime = 0;
                int count = 0;
                long startTime = System.currentTimeMillis();
                while (-1 != (readSize = dataInput.read(mBuffer))) {
                    bufferOutput.write(mBuffer, 0, readSize);
                    currentLength += readSize;
                    count++;
                    if (count % 10 == 0) {
                        currentTime = System.currentTimeMillis();
                        long time = currentTime - lastTime;
                        lastTime = currentTime;
                        long Length = currentLength - lastLength;
                        lastLength = currentLength;
                        fs.currentSize = currentLength;
                        fs.percent = (int) ((float) currentLength / (float) length * 100);

                        switch (fs.type) {
                            case IMAGE:
                                break;

                            case VOICE:
                                break;

                            case VEDIO:
                            case MUSIC:
                            case APK:
                            case FILE:
                                if (mHandler != null) {
                                    android.os.Message msg = mHandler.obtainMessage();
                                    msg.what = IPMSGConst.WHAT_FILE_RECEIVING;
                                    msg.obj = fs;
                                    msg.sendToTarget();
                                }
                                break;

                            default:
                                break;
                        }
                    }
                }

                bufferOutput.flush();

                input.close();
                dataInput.close();
                bufferOutput.close();
                fileOutputStream.close();

                int type = PD_Constant.NEW_MSG_TYPE_FILE;
                switch (fs.type) {
                    case IMAGE:
                        type = PD_Constant.NEW_MSG_TYPE_IMAGE;
                        break;
                    case VOICE:
                        type = PD_Constant.NEW_MSG_TYPE_VOICE;
                        break;
                    case MUSIC:
                    case VEDIO:
                    case APK:
                    case FILE:
                        type = PD_Constant.NEW_MSG_TYPE_FILE;
                        android.os.Message msg = mHandler.obtainMessage();
                        msg.what = IPMSGConst.WHAT_FILE_RECEIVING;
                        fs.percent = 100;
                        msg.obj = fs;
                        msg.sendToTarget();
                        break;

                    default:
                        break;
                }
                PrathamApplication.recieveFileStates.remove(fs.filePath);

                Intent intent = new Intent(PD_Constant.ACTION_NEW_MSG);
                intent.putExtra(PD_Constant.EXTRA_NEW_MSG_TYPE, type);
                intent.putExtra(PD_Constant.EXTRA_NEW_MSG_CONTENT, fileSavePath);
                if (type != PD_Constant.NEW_MSG_TYPE_FILE
                        && type != PD_Constant.NEW_MSG_TYPE_VEDIO
                        && type != PD_Constant.NEW_MSG_TYPE_MUSIC
                        && type != PD_Constant.NEW_MSG_TYPE_APK) {
                    mContext.sendBroadcast(intent);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private Message.CONTENT_TYPE getType(String string) {
            if (string.equals(type[0]))
                return Message.CONTENT_TYPE.TEXT;
            else if (string.equals(type[1]))
                return Message.CONTENT_TYPE.IMAGE;
            else if (string.equals(type[2]))
                return Message.CONTENT_TYPE.FILE;
            else if (string.equals(type[3]))
                return Message.CONTENT_TYPE.VOICE;
            else if (string.equals(type[4]))
                return Message.CONTENT_TYPE.VEDIO;
            else if (string.equals(type[5]))
                return Message.CONTENT_TYPE.MUSIC;
            else if (string.equals(type[6]))
                return Message.CONTENT_TYPE.APK;
            return null;

        }

        @Override
        public void run() {
            super.run();
            if (SCAN_RECIEVE)
                recieveFile();
        }
    }
}
