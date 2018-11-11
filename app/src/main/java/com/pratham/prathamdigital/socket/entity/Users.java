package com.pratham.prathamdigital.socket.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class Users extends Entity implements Parcelable {

    private String mIMEI;
    private String mDevice;
    private String mConstellation;
    private String mIpaddress;
    private String mLogintime;


    public Users() {
    }

    public Users(String IMEI,
                 String device, String constellation, String ip, String logintime) {
        this.mIMEI = IMEI;
        this.mDevice = device;
        this.mConstellation = constellation;
        this.mIpaddress = ip;
        this.mLogintime = logintime;

    }

    //@JSONField (name = Users.IMEI)
    public String getIMEI() {
        return this.mIMEI;
    }

    //@JSONField (name = Users.DEVICE)
    public String getDevice() {
        return this.mDevice;
    }


    //@JSONField (name = Users.CONSTELLATION)
    public String getConstellation() {
        return this.mConstellation;
    }

    //@JSONField (name = Users.IPADDRESS)
    public String getIpaddress() {
        return this.mIpaddress;
    }

    //@JSONField (name = Users.LOGINTIME)
    public String getLogintime() {
        return this.mLogintime;
    }


    public void setIMEI(String paramIMEI) {
        this.mIMEI = paramIMEI;
    }

    public void setDevice(String paramDevice) {
        this.mDevice = paramDevice;
    }


    public void setConstellation(String paramConstellation) {
        this.mConstellation = paramConstellation;
    }

    public void setIpaddress(String paramIpaddress) {
        this.mIpaddress = paramIpaddress;
    }

    public void setLogintime(String paramLogintime) {
        this.mLogintime = paramLogintime;
    }

    public static Creator<Users> getCreator() {
        return CREATOR;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mIMEI);
        dest.writeString(mDevice);
        dest.writeString(mConstellation);
        dest.writeString(mIpaddress);
        dest.writeString(mLogintime);
    }

    public static final Creator<Users> CREATOR = new Creator<Users>() {
        @Override
        public Users createFromParcel(Parcel source) {
            Users user = new Users();
            user.setIMEI(source.readString());
            user.setDevice(source.readString());
            user.setConstellation(source.readString());
            user.setIpaddress(source.readString());
            user.setLogintime(source.readString());
            return user;
        }

        @Override
        public Users[] newArray(int size) {
            return new Users[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

}
