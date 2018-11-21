package com.pratham.prathamdigital.ui.web_view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.pratham.prathamdigital.BaseActivity;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.models.Modal_Score;
import com.pratham.prathamdigital.services.TTSService;
import com.pratham.prathamdigital.ui.video_player.Activity_VPlayer;
import com.pratham.prathamdigital.util.Audio;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import java.io.File;
import java.util.Locale;

public class JSInterface {
    Context mContext;
    String path;
    Audio recordAudio;
    //    MediaRecorder myAudioRecorder;
    public static MediaPlayer mp;
    WebView w;
    //    RadioGroup radioGroup;
    public static int flag = 0;
    public static Boolean MediaFlag = false;
    //    static Boolean pdfFlag = false;
    //    static Boolean audioFlag = false;
//    static Boolean trailerFlag = false;
//    static Boolean completeFlag = false;
    public String gamePath;
    TTSService ttspeech;
    //    static TextToSp textToSp;
    //    private TextToSpeech textToSp;
    private String resId;
    private String audio_directory_path = "";


    JSInterface(Context c, WebView w, String gamePath, String resId) {
        mContext = c;
        ttspeech = BaseActivity.ttsService;
//        this.textToSp = Activity_Main.ttspeech;
        this.resId = resId;
        this.gamePath = gamePath;
        createRecordingFolder();
        mp = new MediaPlayer();
        this.w = w;
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
            recordAudio = new Audio(recName);
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
        w.post(new Runnable() {
            public void run() {
                String jsString = "javascript:Utils.setPath('" + path + "')";
                w.loadUrl(jsString);
            }
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
            String path = "";
            flag = 0;
            String mp3File;
            try {
                if (storyName != null) {
                    File file = new File(audio_directory_path + "/" + storyName);
                    if (!file.exists()) file.mkdirs();
                    mp3File = file.getAbsolutePath() + "/" + filename;
                } else
                    mp3File = audio_directory_path + "/" + filename;

                if (filename.charAt(0) == '/') {
                    path = audio_directory_path + filename;//check for recording game and then change
                    mp.setDataSource(path);
                } else
                    mp.setDataSource(path);

                if (mp.isPlaying())
                    mp.stop();

                mp.prepare();
                mp.start();

                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        //audioFlag = false;
                        try {
                            w.post(new Runnable() {
                                @Override
                                public void run() {
                                    w.loadUrl("javascript:temp()");
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Ketan's Code
    @JavascriptInterface
    public void audioPlayer(String filename) {
        try {
            String path = null;
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

                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer mp) {
                        mp.stop();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void audioPause() {
        if (MediaFlag == true) {
            mp.pause();
            MediaFlag = false;
        }
    }

    @JavascriptInterface
    public void audioResume() {
        if (MediaFlag == false) {
            mp.start();
            MediaFlag = true;
        }
        try {
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    mp.stop();
                    mp.reset();
                    MediaFlag = false;
                }
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
            Intent intent = new Intent(mContext, Activity_VPlayer.class);
            intent.putExtra("videoPath", vidPath);
            intent.putExtra("resId", resId);
            MediaFlag = true;
            ((Activity) mContext).startActivityForResult(intent, 1);
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
    public static boolean informCompletion() {
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
            if (PrathamApplication.isTablet)
                addScoreForTablet(resId, "_", questionId, scorefromGame, totalMarks, level, startTime, "_");
            else
                addScoreForSmartPhone(resId, "_", questionId, scorefromGame, totalMarks, level, startTime, "_");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //this addScore method is called from the Games Made By UBS
    @JavascriptInterface
    public void addScore(String piStudId, int questionId, int scorefromGame, int totalMarks, int level, String startTime, String label) {
        try {
            if (PrathamApplication.isTablet)
                addScoreForTablet(resId, piStudId, questionId, scorefromGame, totalMarks, level, startTime, label);
            else
                addScoreForSmartPhone(resId, piStudId, questionId, scorefromGame, totalMarks, level, startTime, label);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addScoreForSmartPhone(String tempResId, String piStudId, int questionId, int scorefromGame, int totalMarks, int level, String startTime, String label) {
        Modal_Score modal_score = new Modal_Score();
        modal_score.setSessionID(FastSave.getInstance().getString(PD_Constant.SESSIONID, "no_session"));
        modal_score.setStudentID(FastSave.getInstance().getString(PD_Constant.STUDENTID, "no_student"));
        modal_score.setDeviceID(PD_Utility.getDeviceID());
        modal_score.setResourceID(tempResId);
        modal_score.setQuestionId(questionId);
        modal_score.setScoredMarks(scorefromGame);
        modal_score.setTotalMarks(totalMarks);
        modal_score.setStartDateTime(startTime);
        modal_score.setEndDateTime(PD_Utility.getCurrentDateTime());
        modal_score.setLevel(level);
        modal_score.setLabel(piStudId + "," + label);
        modal_score.setSentFlag(0);
        BaseActivity.scoreDao.insert(modal_score);
    }

    private void addScoreForTablet(String tempResId, String piStudId, int questionId, int scorefromGame, int totalMarks, int level, String startTime, String label) {
        Modal_Score modal_score = new Modal_Score();
        modal_score.setSessionID(FastSave.getInstance().getString(PD_Constant.SESSIONID, "no_session"));
        modal_score.setGroupID(FastSave.getInstance().getString(PD_Constant.GROUPID, "no_group"));
        modal_score.setDeviceID(PD_Utility.getDeviceID());
        modal_score.setResourceID(tempResId);
        modal_score.setQuestionId(questionId);
        modal_score.setScoredMarks(scorefromGame);
        modal_score.setTotalMarks(totalMarks);
        modal_score.setStartDateTime(startTime);
        modal_score.setEndDateTime(PD_Utility.getCurrentDateTime());
        modal_score.setLevel(level);
        modal_score.setLabel(piStudId + "," + label);
        modal_score.setSentFlag(0);
        BaseActivity.scoreDao.insert(modal_score);
    }
}