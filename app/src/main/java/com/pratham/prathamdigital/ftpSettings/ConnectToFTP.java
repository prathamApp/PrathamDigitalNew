package com.pratham.prathamdigital.ftpSettings;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.pratham.prathamdigital.interfaces.FTPConnected;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

public class ConnectToFTP extends AsyncTask<Void, Void, Boolean> {
    //    private ProgressDialog pd;
    private Context context;
    FTPClient client1;
    FTPConnected ftpConnected;

    public ConnectToFTP(Context context, FTPConnected ftpConnected) {
        this.context = context;
        this.ftpConnected = ftpConnected;
//        this.ftpConnectInterface = ftpConnectInterface;
//        this.ipaddress = ipaddress.replace("ftp://", "");
//        this.port = port;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
//        pd = new ProgressDialog(context);
//        pd.setMessage("Connecting ... Please wait !!!");
//        pd.setCanceledOnTouchOutside(false);
//        pd.setCancelable(false);
//        pd.show();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        client1 = new FTPClient();
        try {
//            ipaddress = ipaddress.replace("ftp://", "");
//            ipaddress = ipaddress.replace(":8080", "");
            client1.setControlEncoding("UTF-8");
            client1.setAutodetectUTF8(true);
            /*The Apache Commons Net library believes that the 220 response from the server does not conform to RFC 959 (probably rightfully).
            If you want to allow the library to talk to the server, call FTP.setStrictReplyParsing*/
            client1.setStrictReplyParsing(false);
            client1.connect("192.168.43.1", 8080);
            client1.login("ftp", "ftp");
//            client1.changeWorkingDirectory("/storage/sdcard1");
            client1.enterLocalPassiveMode();
            client1.setFileType(FTP.BINARY_FILE_TYPE);
            FTPFile[] files = client1.listFiles();
            String wor = client1.printWorkingDirectory();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean connected) {
        super.onPostExecute(connected);
//        if (pd != null)
//            pd.dismiss();
//        MyApplication.ftpClient = client1;
//        ftpConnectInterface.onConnectionEshtablished(client1.isConnected());
        if (connected) {
//            Toast.makeText(context, "hotspot connected", Toast.LENGTH_SHORT).show();
            ftpConnected.onFTPConnected(connected, client1);
        } else {
            Toast.makeText(context, "not connected", Toast.LENGTH_SHORT).show();
            ftpConnected.onFTPConnected(connected, null);
        }
    }
}