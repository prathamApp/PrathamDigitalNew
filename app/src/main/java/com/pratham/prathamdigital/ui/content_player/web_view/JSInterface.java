package com.pratham.prathamdigital.ui.content_player.web_view;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pratham.prathamdigital.BaseActivity;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.models.Modal_Score;
import com.pratham.prathamdigital.models.Modal_Student;
import com.pratham.prathamdigital.services.TTSService;
import com.pratham.prathamdigital.util.Audio;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.pratham.prathamdigital.PrathamApplication.scoreDao;

public class JSInterface {
    private static final int GAME_COMPLETED = 1;
    //    MediaRecorder myAudioRecorder;
    private static MediaPlayer mp;
    //    RadioGroup radioGroup;
    private static Boolean MediaFlag = false;
    private final WebView w;
    //    static Boolean pdfFlag = false;
    //    static Boolean audioFlag = false;
//    static Boolean trailerFlag = false;
//    static Boolean completeFlag = false;
    private final String gamePath;
    private final TTSService ttspeech;
    //    static TextToSp textToSp;
    //    private TextToSpeech textToSp;
    private final String resId;
    private final VideoListener videoListener;
    private final Activity activity;
    private String audio_directory_path = "";
    private String path;
    private Audio recordAudio;

    String str_dateTime;


    JSInterface(Context c, WebView w, String gamePath, String resId, boolean isOnSdCard, VideoListener videoListener, Activity activity) {
        ttspeech = BaseActivity.ttsService;
        this.resId = resId;
        this.gamePath = gamePath;
        createRecordingFolder();
        mp = new MediaPlayer();
        this.w = w;
        this.videoListener = videoListener;
        this.activity = activity;
    }

    private void createRecordingFolder() {
        File file = new File(PrathamApplication.pradigiPath + "/PrathamRecordings");
        if (!file.exists()) {
            file.mkdirs();
        }
        audio_directory_path = file.getAbsolutePath();
    }

    @JavascriptInterface
    public void toggleVolume(String volume) {
        if (volume.equals("false"))
            mp.setVolume(0, 0);
        else {
            mp.setVolume(1, 1);
        }
    }

    @JavascriptInterface
    public String getLevel() {
        return "";
    }

    @JavascriptInterface
    public String getMediaPath(String gameFolder) {
        return gamePath;
    }

    @JavascriptInterface
    public void startRecording(String recName) {
        try {
            File file = new File(audio_directory_path, recName);
            if (!file.exists()) file.createNewFile();
            recordAudio = new Audio(file.getAbsolutePath());
            recordAudio.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void stopRecording() {
        try {
            if (recordAudio != null) {
                recordAudio.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void getPath(String gameFolder) {
        path = gamePath + "/";
        w.post(() -> {
            String jsString = "javascript:Utils.setPath('" + path + "')";
            w.loadUrl(jsString);
        });
    }

    /*@JavascriptInterface
    public void sendBackTojavascript() {
        w.post(new Runnable() {
            public void run() {
                String str1 = MainActivity.jsonstrOfNewVideos;
                String jsString = "javascript:loadNewJson('" + str1 + "')";
                w.loadUrl(jsString);
            }
        });
    }*/
    @JavascriptInterface
    public void showPdf(String filename, String resId) {
    }

    @JavascriptInterface
    public void audioPlayerForStory(String filename, String storyName) {
        try {
            mp = new MediaPlayer();
            if (mp.isPlaying())
                mp.stop();
            mp.reset();
            if (ttspeech.isSpeaking()) {
                ttspeech.stop();
            }
            File file = new File(audio_directory_path, filename);
            mp.setDataSource(file.getAbsolutePath());
            mp.prepare();
            mp.start();
            mp.setOnCompletionListener(mediaPlayer -> {
                //audioFlag = false;
                try {
                    w.post(() -> w.loadUrl("javascript:temp()"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void audioPlayer(String filename) {
        try {
            String path;
            if (filename.charAt(0) == '/') {
                path = filename;//check for recording game and then change
            } else {
                //path="/storage/sdcard1/.prathamMarathi/"+filename;
//                path = ContentScreen.fpath+"Media" + filename;
                path = gamePath;
            }
            mp = new MediaPlayer();

            try {
                mp.setDataSource(path);
                mp.prepare();
                mp.start();

                mp.setOnCompletionListener(MediaPlayer::stop);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void audioPause() {
        if (MediaFlag) {
            mp.pause();
            MediaFlag = false;
        }
    }

    @JavascriptInterface
    public void audioResume() {
        if (!MediaFlag) {
            mp.start();
            MediaFlag = true;
        }
        try {
            mp.setOnCompletionListener(mp -> {
                mp.stop();
                mp.reset();
                MediaFlag = false;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void stopAudioPlayer() {
        try {
            if (mp != null) {
                mp.stop();
                mp.reset();
                //mp = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void showVideo(String filename, String resId) {
        try {
            String vidPath = gamePath + filename;
            MediaFlag = true;
            activity.runOnUiThread(() -> videoListener.showVideo(vidPath));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void playTts(String theWordWasAndYouSaid, String ttsLanguage) {
        mp.stop();
        mp.reset();
        if (ttspeech.isSpeaking()) {
            ttspeech.stop();
        }
        if (ttsLanguage == null)
            ttspeech.setLanguage(new Locale("en", "IN"));
        else if (ttsLanguage.equals("eng"))
            ttspeech.setLanguage(new Locale("en", "IN"));
        else if (ttsLanguage.equals("hin"))
            ttspeech.setLanguage(new Locale("hi", "IN"));

        ttspeech.play(theWordWasAndYouSaid);

    }

    @JavascriptInterface
    public void stopTts() {
        ttspeech.stop();
    }

    @JavascriptInterface
    public void playTts(final String toSpeak) {
        ttspeech.setLanguage(new Locale("en", "IN"));
        ttspeech.play(toSpeak);
    }

    @JavascriptInterface
    public boolean informCompletion() {
        Log.d("gamecompleted:::", "informCompletion:");
        activity.runOnUiThread(() -> videoListener.gameCompleted());
        return true;
    }

   /* public String formatCustomDate(String[] splitedDate, String delimiter){
        for (int k=0;k<splitedDate.length;k++) {
            if (Integer.parseInt(splitedDate[k]) < 10) {
                splitedDate[k]= "0"+splitedDate[k];
            }
        }
        return TextUtils.join(delimiter,splitedDate);
    }*/

    //this addScore method is called from the Games Made By Pratham
    @JavascriptInterface
    public void addScore(String tempResId, int questionId, int scorefromGame, int totalMarks, int level, String startTime) {
        try {
            addScore(resId, "_", questionId, scorefromGame, totalMarks, level, startTime, "_");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //this addScore method is called from the Games Made By UBS
    @JavascriptInterface
    public void addScore(String piStudId, int questionId, int scorefromGame, int totalMarks, int level, String startTime, String label) {
        try {
            addScore(resId, piStudId, questionId, scorefromGame, totalMarks, level, startTime, label);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addScore(String tempResId, String piStudId, int questionId, int scorefromGame, int totalMarks, int level, String startTime, String label) {
        changeStartDateTimeFormat(startTime);
        Modal_Score modal_score = new Modal_Score();
        modal_score.setSessionID(FastSave.getInstance().getString(PD_Constant.SESSIONID, "no_session"));
        if (PrathamApplication.isTablet) {
            modal_score.setGroupID(FastSave.getInstance().getString(PD_Constant.GROUPID, "no_group"));
            modal_score.setStudentID("");
        } else {
            modal_score.setGroupID("");
            modal_score.setStudentID(FastSave.getInstance().getString(PD_Constant.GROUPID, "no_student"));
        }
        modal_score.setDeviceID(PD_Utility.getDeviceID());
        modal_score.setResourceID(tempResId);
        modal_score.setQuestionId(questionId);
        modal_score.setScoredMarks(scorefromGame);
        modal_score.setTotalMarks(totalMarks);
        modal_score.setStartDateTime(str_dateTime);
        modal_score.setEndDateTime(PD_Utility.getCurrentDateTime());
        modal_score.setLevel(level);
        modal_score.setLabel(piStudId + "," + label);
        modal_score.setSentFlag(0);
        scoreDao.insert(modal_score);
    }

    public void changeStartDateTimeFormat(String startDateTime){
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        try {
            Date startDate = format.parse(startDateTime);
            str_dateTime = format.format(startDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public String getStudentList() {
        try {
            String stud = FastSave.getInstance().getString(PD_Constant.PRESENT_STUDENTS, "[]");
            Type listType = new TypeToken<ArrayList<Modal_Student>>() {
            }.getType();
            List<Modal_Student> students = new Gson().fromJson(stud, listType);
            JSONArray array = new JSONArray();
            for (Modal_Student st : students) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("StudentId", st.getStudentId());
                jsonObject.put("StudentName", st.getFullName());
                array.put(jsonObject);
            }
            return array.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "[]";
        }
    }
}