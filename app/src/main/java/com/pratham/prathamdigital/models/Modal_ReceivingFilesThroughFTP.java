package com.pratham.prathamdigital.models;

import android.support.annotation.NonNull;

public class Modal_ReceivingFilesThroughFTP implements Comparable {
    String gameName;
    String gamePart;
    String gameType;
    boolean isReceived = false;

    public boolean isReceived() {
        return isReceived;
    }

    public void setReceived(boolean received) {
        isReceived = received;
    }

    public String getGameType() {
        return gameType;
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getGamePart() {
        return gamePart;
    }

    public void setGamePart(String gamePart) {
        this.gamePart = gamePart;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        Modal_ReceivingFilesThroughFTP compare = (Modal_ReceivingFilesThroughFTP) o;
        if (compare.getGameName() != null) {
            if (compare.getGameName() == (this.gameName))
                return 0;
            else return 1;
        } else
            return 0;
    }
}
