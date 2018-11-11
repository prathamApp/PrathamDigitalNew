package com.pratham.prathamdigital.socket.entity;

public class Message extends Entity {

    private String senderIMEI;
    private String sendTime;
    private String msgContent;
    private CONTENT_TYPE contentType;
    private int percent;

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    private int length;


    public Message(String paramSenderIMEI, String paramSendTime, String paramMsgContent,
                   CONTENT_TYPE paramContentType) {
        this.senderIMEI = paramSenderIMEI;
        this.sendTime = paramSendTime;
        this.msgContent = paramMsgContent;
        this.contentType = paramContentType;
    }

    public Message() {

    }

    public enum CONTENT_TYPE {
        TEXT, IMAGE, FILE, VOICE, VEDIO, MUSIC, APK, type;
    }

    public String getSenderIMEI() {
        return senderIMEI;
    }

    public void setSenderIMEI(String paramSenderIMEI) {
        this.senderIMEI = paramSenderIMEI;
    }

    public CONTENT_TYPE getContentType() {
        return contentType;
    }

    public void setContentType(CONTENT_TYPE paramContentType) {
        this.contentType = paramContentType;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String paramSendTime) {
        this.sendTime = paramSendTime;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String paramMsgContent) {
        this.msgContent = paramMsgContent;
    }

    public Message clone() {
        return new Message(senderIMEI, sendTime, msgContent, contentType);
    }

    // @JSONField(serialize = false)
    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

}
