package com.pratham.prathamdigital.util;

public class PD_Constant {

    public static final String BASE_URL = "http://prodigi.openiscool.org/api/pos/";
    //    public static final String BASE_URL = "http://devprodigi.openiscool.org/api/pos/";
    public static final String AVATAR = "avatar";
    public static final String INTERNET_HEADER = "INTERNET_HEADER";
    public static final String BROWSE_INTERNET = "BROWSE_INTERNET";
    public static final String INTERNET_DOWNLOAD = "INTERNET_DOWNLOAD";
    public static final String IS_GOOGLE_SIGNED_IN = "IS_GOOGLE_SIGNED_IN";
    public static final String GOOGLE_TOKEN = "GOOGLE_TOKEN";
    public static final String CONTENT = "CONTENT";
    public static final int LOCATION_GRANTED = 1;
    public static final String GAME = "game";
    public static final String VIDEO = "video";
    public static final String PDF = "pdf";
    public static final String SESSIONID = "sessionid";
    public static final String GROUPID = "groupid";
    public static final String STUDENTID = "studentid";
    public static final String CONTENT_BACK = "content_back";
    public static final String STUDENT_ADDED = "STUDENT_ADDED";
    public static final String STUDENT_LIST = "STUDENT_LIST";
    public static final String GROUP_AGE_BELOW_7 = "GROUP_AGE_BELOW_7";
    public static final String GROUPID1 = "group1";
    public static final String GROUPID2 = "group2";
    public static final String GROUPID3 = "group3";
    public static final String GROUPID4 = "group4";
    public static final String GROUPID5 = "group5";
    public static final String FACILITY_ID = "facility_id";
    public static final String WRITE_PERMISSION = "write_permission";
    public static final String DOWNLOAD_COMPLETE = "download_complete";
    public static final String REVEALX = "REVEALX";
    public static final String REVEALY = "REVEALY";
    public static final String LANGUAGE_BACK = "language_back";
    public static final String NO_CONNECTION = "no_connection";
    public static final String FTP_HOTSPOT_SSID = "ftp_hotspot_ssid";
    public static final String FTP_HOTSPOT_PASS = "ftp_hotspot_pass";
    public static final String GROUP_NAME = "group_name";
    public static final String PROFILE_NAME = "profile_name";
    public static final String SHOW_BACK = "show_back";
    public static final String PRADIGI_ICON = "pradigi_icon";
    public static final String CANCEL_DOWNLOAD = "cancel_download";
    public static final String DEEP_LINK = "deep_link";
    public static final String DEEP_LINK_CONTENT = "deep_link_content";
    public static final String AKS_FILE_PATH = "aks_file_path";
    public static int FTP_HOTSPOT_KEYMGMT = -11;
    public static final String CONNECTION_STATUS = "CONNECTION_STATUS";
    public static final String STORAGE_ASKED = "storage_asked";
    public static final String FROMDATE = "fromDate";
    public static final String TODATE = "toDate";
    public static final String SCORE = "scores";
    public static final String ATTENDANCE = "attendances";
    public static final String SESSION = "session";
    public static final String LOGS = "logs";
    public static final String STUDENTS = "students";
    public static final String METADATA = "metadata";
    public static final String USAGEDATA = "USAGEDATA";
    public static final String DOWNLOAD_STARTED = "download_started";
    public static final String DOWNLOAD_UPDATE = "download_update";
    public static final String PRADIGI_FOLDER = "Pradigi";
    public static final String FILE_DOWNLOAD_COMPLETE = "file_download_complete";
    public static final String SCORE_COUNT = "ScoreCount";
    public static final String FOLDER = "folder";
    public static final String FILE = "file";
    public static final String FILE_SHARE_PROGRESS = "file_share_progress";
    public static final String SHARE_BACK = "share_back";
    public static final String CLOSE_FTP_SERVER = "close_ftp_server";
    public static final String FTP_CLIENT_CONNECTED = "ftp_client_connected";
    public static final String APK = "apk";
    public static final String FILE_DOWNLOAD_STARTED = "file_download_started";
    public static final String KOLIBRI_CRL = "kolibri_crl";
    public static final String SERVER_CRL = "server_crl";
    public static final String KOLIBRI_GRP = "kolibri_grp";
    public static final String SERVER_GRP = "server_grp";
    public static final String KOLIBRI_STU = "kolibri_stu";
    public static final String SERVER_STU = "server_stu";
    public static final String KOLIBRI_BLOCK = "kolibri_block";
    public static final String SERVER_BLOCK = "server_block";
    public static final String DOWNLOAD_FAILED = "download_failed";
    public static final String FILE_DOWNLOAD_ERROR = "FILE_DOWNLOAD_ERROR";
    public static final String BROADCAST_DOWNLOADINGS = "broadcast_downloadings";
    public static final String SDCARD_URI = "sdcard_uri";
    public static final String SDCARD_PATH = "sdcard_path";
    public static final String APP_VERSION = "app_version";
    public static final String FILE_SHARE_COMPLETE = "file_share_complete";
    public static final String SHARE_PROFILE = "id_profile_";
    public static final String SHARE_USAGE = "id_usage_";
    public static final String PRATHAM_TEMP_FILES = "pratham_temp_files";
    public static final String FILE_RECEIVE_COMPLETE = "file_receive_complete";
    public static final String FTP_KEYMGMT = "ftp_keymgmt";
    public static String HOTSPOT_SSID = "pratham";
    public static String HOTSPOT_PASSWORD = "";
    public static String STORING_IN = "";
    public static final String SUCCESSFULLYPUSHED = "successfully_pushed";
    public static final String PUSHFAILED = "push_failed";
    public static String RASP_IP = "http://192.168.4.1:8080";
    public static final String PRATHAM_KOLIBRI_HOTSPOT = "prathamkolibri";
    public static String BROWSE_RASPBERRY = "BROWSE_RASPBERRY";
    public static String RASPBERRY_HEADER = "RASPBERRY_HEADER";
    //languages
    public static String HINDI = "Hindi";
    public static String ENGLISH = "English";
    public static String MARATHI = "Marathi";
    public static String KANNADA = "Kannada";
    public static String TELUGU = "Telugu";
    public static String BENGALI = "Bengali";
    public static String GUJARATI = "Gujarati";
    public static String PUNJABI = "Punjabi";
    public static String TAMIL = "Tamil";
    public static String ODIYA = "Odiya";
    public static String MALAYALAM = "Malayalam";
    public static String ASSAMESE = "Assamese";

    //wifi constants
    public static final int ApScanResult = 201;
    public static final int WiFiConnectSuccess = 202;
    public static final int ApCreateApSuccess = 203;
    public static final String WIFI_AP_PASSWORD = "pratham123";

    public static final String LANGUAGE = "language";
    public static final String GOOGLE_ID = "google_id";

    public static enum URL {

        BROWSE_BY_ID(BASE_URL + "get?id="),
        SEARCH_BY_KEYWORD(BASE_URL + "GetSearchList?"),
        POST_GOOGLE_DATA(BASE_URL + "PostGoogleSignIn"),
        GET_TOP_LEVEL_NODE(BASE_URL + "GetTopLevelNode?lang="),
        DATASTORE_RASPBERY_URL(RASP_IP + "/pratham/datastore/"),
        BROWSE_RASPBERRY_URL(RASP_IP + "/api/contentnode?parent="),
        //        GET_RASPBERRY_HEADER(RASP_IP + "/api/contentnode?content_id=f9da12749d995fa197f8b4c0192e7b2c"),
        GET_RASPBERRY_HEADER(RASP_IP + "/api/channel/"),
        POST_SMART_INTERNET_URL("http://www.rpi.prathamskills.org/api/pushdatasmartphone/post/"),
        POST_TAB_INTERNET_URL("http://www.rpi.prathamskills.org/api/pushdatapradigi/post/"),
        DOWNLOAD_RESOURCE(BASE_URL + "DownloadResource?resid=");

        private final String name;

        private URL(String s) {
            name = s;
        }

        public boolean equalsName(String otherName) {
            return (otherName == null) ? false : name.equals(otherName);
        }

        public String toString() {
            return name;
        }

    }

    public static final String TempDLContent = "{\n" +
            "  \"nodelist\": [\n" +
            "    {\n" +
            "      \"nodeid\": 1299,\n" +
            "      \"nodetype\": \"KksMainTopic\",\n" +
            "      \"nodetitle\": \"KKS Listing\",\n" +
            "      \"nodekeywords\": \"KKS\",\n" +
            "      \"nodeeage\": \" 08-14\",\n" +
            "      \"nodedesc\": \"\",\n" +
            "      \"nodeimage\": \"\",\n" +
            "      \"nodeserverimage\": \"\",\n" +
            "      \"resourceid\": \"\",\n" +
            "      \"resourcetype\": \"Topic\",\n" +
            "      \"resourcepath\": \"\",\n" +
            "      \"nodeserverpath\": \"\",\n" +
            "      \"level\": 4,\n" +
            "      \"parentid\": null\n" +
            "    },\n" +
            "    {\n" +
            "      \"nodeid\": 1302,\n" +
            "      \"nodetype\": \"Topic\",\n" +
            "      \"nodetitle\": \"Test\",\n" +
            "      \"nodekeywords\": \"KKS Test\",\n" +
            "      \"nodeeage\": \" 08-14\",\n" +
            "      \"nodedesc\": \"\",\n" +
            "      \"nodeimage\": \"\",\n" +
            "      \"nodeserverimage\": \"\",\n" +
            "      \"resourceid\": \"\",\n" +
            "      \"resourcetype\": \"Topic\",\n" +
            "      \"resourcepath\": \"\",\n" +
            "      \"nodeserverpath\": \"\",\n" +
            "      \"level\": 3,\n" +
            "      \"parentid\": 1299\n" +
            "    },\n" +
            "    {\n" +
            "      \"nodeid\": 1312,\n" +
            "      \"nodetype\": \"Resource\",\n" +
            "      \"nodetitle\": \"Expert\",\n" +
            "      \"nodekeywords\": \"Test Level 2\",\n" +
            "      \"nodeeage\": \" 08-14\",\n" +
            "      \"nodedesc\": \"\",\n" +
            "      \"nodeimage\": \"assessL3.png\",\n" +
            "      \"nodeserverimage\": \"http://www.prodigi.openiscool.org/repository/KKSImages/assessL3.png\",\n" +
            "      \"resourceid\": \"\",\n" +
            "      \"resourcetype\": \"Game\",\n" +
            "      \"resourcepath\": \"TestLevel2/index.html\",\n" +
            "      \"nodeserverpath\": \"\",\n" +
            "      \"level\": 2,\n" +
            "      \"parentid\": 1302\n" +
            "    }\n" +
            "  ],\n" +
            "  \"downloadurl\": \"http://www.prodigi.openiscool.org/repository/KKSGames/A_flipItAssmnt.zip\",\n" +
            "  \"foldername\": \"Game\"\n" +
            "}";
}
