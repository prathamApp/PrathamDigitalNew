package com.pratham.prathamdigital.ftpSettings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

import com.pratham.prathamdigital.interfaces.FTPConnected;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

@EBean
public class ConnectToFTP {
    @SuppressLint("StaticFieldLeak")
    private Context context;
    private FTPClient client1;
    private FTPConnected ftpConnected;

    public ConnectToFTP(Context context) {
        this.context = context;
    }

    @Background
    public void doInBackground(FTPConnected _ftpConnected, String _ip) {
        this.ftpConnected = _ftpConnected;
        client1 = new FTPClient();
        try {
            Thread.sleep(2500);
            client1.setControlEncoding("UTF-8");
            client1.setAutodetectUTF8(true);
            /*The Apache Commons Net library believes that the 220 response from the server does not conform to RFC 959 (probably rightfully).
            If you want to allow the library to talk to the server, call FTP.setStrictReplyParsing*/
            client1.setStrictReplyParsing(false);
            client1.connect(_ip, 8080);
            client1.login("ftp", "ftp");
//            client1.changeWorkingDirectory("/storage/sdcard1");
            client1.enterLocalPassiveMode();
            client1.setFileType(FTP.BINARY_FILE_TYPE);
//            FTPFile[] files = client1.listFiles();
//            String wor = client1.printWorkingDirectory();
            onPostExecute(true);
        } catch (Exception e) {
            e.printStackTrace();
            onPostExecute(false);
        }
    }

    @UiThread
    protected void onPostExecute(Boolean connected) {
        if (connected) {
            ftpConnected.onFTPConnected(true, client1);
        } else {
            Toast.makeText(context, "Connecting... Please wait!", Toast.LENGTH_SHORT).show();
            ftpConnected.onFTPConnected(connected, null);
        }
    }
}
