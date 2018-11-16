package com.pratham.prathamdigital.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.pratham.prathamdigital.socket.entity.ChatEntity;
import com.pratham.prathamdigital.socket.entity.Message;
import com.pratham.prathamdigital.util.PD_Constant;

public class MessageReveiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int type = intent.getIntExtra(PD_Constant.EXTRA_NEW_MSG_TYPE, PD_Constant.NEW_MSG_TYPE_TXT);//默认是TXT
        String content = intent.getExtras().getString(PD_Constant.EXTRA_NEW_MSG_CONTENT);

        ChatEntity chatMsg = new ChatEntity();
        chatMsg.setIsSend(false);
        chatMsg.setContent(content);
        chatMsg.setTime(System.currentTimeMillis());
        switch (type) {
            case PD_Constant.NEW_MSG_TYPE_TXT:
                chatMsg.setType(Message.CONTENT_TYPE.TEXT);
                break;
            case PD_Constant.NEW_MSG_TYPE_IMAGE:
                chatMsg.setType(Message.CONTENT_TYPE.IMAGE);
                break;
            case PD_Constant.NEW_MSG_TYPE_VOICE:
                chatMsg.setType(Message.CONTENT_TYPE.VOICE);
                break;
            case PD_Constant.NEW_MSG_TYPE_FILE:
                chatMsg.setType(Message.CONTENT_TYPE.FILE);
                break;
            case PD_Constant.NEW_MSG_TYPE_VEDIO:
                chatMsg.setType(Message.CONTENT_TYPE.VEDIO);
                break;
            case PD_Constant.NEW_MSG_TYPE_MUSIC:
                chatMsg.setType(Message.CONTENT_TYPE.MUSIC);
                break;
            case PD_Constant.NEW_MSG_TYPE_APK:
                chatMsg.setType(Message.CONTENT_TYPE.APK);
                break;
        }
    }
}
