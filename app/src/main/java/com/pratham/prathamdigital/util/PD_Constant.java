package com.pratham.prathamdigital.util;

public class PD_Constant {

    public static final String BASE_URL = "http://prodigi.openiscool.org/api/pos/";
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
    public static final String VIEW_TYPE = "VIEW_TYPE";
    public static final String DOWNLOAD = "DOWNLOAD";
    public static final String SETTINGS = "SETTINGS";
    public static final String SHARE = "SHARE";
    public static final String SESSIONID = "sessionid";
    public static final String GROUPID = "groupid";
    public static final String SETTINGS_BACK = "settings_back";
    public static final String STUDENTID = "studentid";
    public static String RASP_IP = "http://192.168.4.1:8080/";
    public static final String PRATHAM_KOLIBRI_HOTSPOT = "prathamkolibri";
    public static String CONNECTION_TYPE = "";
    public static String pradigiObbPath = "";
    public static String FTP_USERNAME = "pratham";
    public static String FTP_PASSWORD = "pratham";
    public static String FTP_IP = "";
    public static String FTP_PORT = "";
    public static String BROWSE_RASPBERRY = "BROWSE_RASPBERRY";
    public static String RASPBERRY_HEADER = "RASPBERRY_HEADER";
    public static String BROWSE_RASPBERRY_URL = RASP_IP + "api/contentnode?parent=";
    public static String GET_RASPBERRY_HEADER = RASP_IP + "api/contentnode?content_id=f9da12749d995fa197f8b4c0192e7b2c";
    public static final int TCP_SERVER_RECEIVE_PORT = 4447;
    public static int READ_BUFFER_SIZE = 1024 * 4;
    int[] english_age_id = {1100, 1101, 1102, 1103};
    int[] hindi_age_id = {20, 21, 22, 23};
    int[] marathi_age_id = {25, 26, 27, 28};
    int[] kannada_age_id = {30, 31, 32, 33};
    int[] telugu_age_id = {35, 36, 37, 38};
    int[] bengali_age_id = {40, 41, 42, 43};
    int[] gujarati_age_id = {45, 46, 47, 48};
    int[] assamese_age_id = {50, 51, 52, 53};
    int[] punjabi_age_id = {55, 56, 57, 58};
    int[] odiya_age_id = {60, 61, 62, 63};
    int[] tamil_age_id = {65, 66, 67, 68};
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
    public static final String WIFI_AP_HEADER = "Pratham_";
    public static final String WIFI_AP_PASSWORD = "pratham123";
    //Notification Keys
    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";
    //    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";
    public static final String SEND_REG_TO_SERVER = "send_reg_to_server";
    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;
    public static final int WEBVIEW = 1;
    public static final String LANGUAGE = "language";
    public static final String GOOGLE_ID = "google_id";
    public static final String EMAIL = "email";
    public static final String PERSON_NAME = "person_name";
    public static final String LANG = "lang";
//    public static final String SHARED_PREF = "ah_firebase";

    public static enum URL {

        BROWSE_BY_ID(BASE_URL + "get?id="),
        SEARCH_BY_KEYWORD(BASE_URL + "GetSearchList?"),
        POST_GOOGLE_DATA(BASE_URL + "PostGoogleSignIn"),
        GET_TOP_LEVEL_NODE(BASE_URL + "GetTopLevelNode?lang="),
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

    public static final int REQUEST_SHOW_CREATE = 0x01;
    public static final int REQUEST_SHOW_CREATING = 0x02;
    public static final String EXTRA_BLUR_PATH = "blur_path";
    public static final String EXTRA_SENDER_IP = "sender_ip";
    public static final String EXTRA_CHAT_USER = "chat_user";
    public static final String EXTRA_NEW_MSG_CONTENT = "new_msg_content";
    public static final String EXTRA_NEW_MSG_TYPE = "new_msg_type";
    public static final int NEW_MSG_TYPE_TXT = 0x401;
    public static final int NEW_MSG_TYPE_IMAGE = 0x402;
    public static final int NEW_MSG_TYPE_VOICE = 0x403;
    public static final int NEW_MSG_TYPE_FILE = 0x404;
    public static final int NEW_MSG_TYPE_VEDIO = 0x405;
    public static final int NEW_MSG_TYPE_MUSIC = 0x406;
    public static final int NEW_MSG_TYPE_APK = 0x407;

    public static final int REQUEST_PICK_IMAGE = 0x000300;
    public static final int REQUEST_PICK_FILE = 0x000301;
    public static final int REQUEST_PICK_APK = 0x000302;
    public static final int REQUEST_PICK_MUSIC = 0x000303;
    public static final int REQUEST_PICK_VEDIO = 0x000304;
    public static final String ACTION_CLEAR_SEND_FILES = "action.CLEAR_SEND_FILES";
    public static final String ACTION_NEW_MSG = "action.NEW_MSG";
    public static final String ACTION_UPDATE_BOTTOM = "action.UPDATE_BOTTOM";
}
