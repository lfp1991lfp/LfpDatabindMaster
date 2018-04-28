package com.lfp.lfp_databind_master.xml.bean;

import android.databinding.BaseObservable;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lfp on 2018/3/27.
 * 内容bean
 */

public class ContentBean implements Parcelable {

    public static final Creator<ContentBean> CREATOR = new Creator<ContentBean>() {
        @Override
        public ContentBean createFromParcel(Parcel source) {
            return new ContentBean(source);
        }

        @Override
        public ContentBean[] newArray(int size) {
            return new ContentBean[size];
        }
    };
    //文本信息
    private String text;
    //0表示组，1表示组的内容
    private String type;

    public ContentBean(String text, String type) {
        this.text = text;
        this.type = type;
    }

    public ContentBean() {
    }

    protected ContentBean(Parcel in) {
        this.text = in.readString();
        this.type = in.readString();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.text);
        dest.writeString(this.type);
    }
}
