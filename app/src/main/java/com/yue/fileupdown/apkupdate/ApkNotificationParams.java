package com.yue.fileupdown.apkupdate;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;

import com.yue.fileupdown.R;

/**
 * @author shimy
 * @create 2019/11/21 13:43
 * @desc 应用更新通知参数
 */
public class ApkNotificationParams implements Parcelable {


    private Context context;
    private String authority;//provider 自动跳转到安装界面使用
    private String path;//安装文件路径
    private String channelId;//通道id 8.0
    private String channelName;//通道名称
    private String channelDescription;//通道详情
    private int smallIcon;//小图标
    private Bitmap largeIcon;//大图标 一般是应用图标
    private String contentTitle;//通知栏标题
    private int notifactionId;//通知id

    //    boolean isRetry;//重试按钮
    private ApkNotificationParams(Builder builder) {
        this.context = builder.context;
        this.authority = builder.authority;
        this.path = builder.path;
        this.channelId = builder.channelId;
        this.channelName = builder.channelName;
        this.channelDescription = builder.channelDescription;
        this.smallIcon = builder.smallIcon;
        if (builder.largeIcon != null)
            this.largeIcon = builder.largeIcon;
        else
            largeIcon = BitmapFactory.decodeResource(
                    context.getResources(),
                    R.mipmap.ic_launcher_round);
        this.contentTitle = builder.contentTitle;
        this.notifactionId = builder.notifactionId;
    }


    protected ApkNotificationParams(Parcel in) {
        authority = in.readString();
        path = in.readString();
        channelId = in.readString();
        channelName = in.readString();
        channelDescription = in.readString();
        smallIcon = in.readInt();
        largeIcon = in.readParcelable(Bitmap.class.getClassLoader());
        contentTitle = in.readString();
        notifactionId = in.readInt();
    }

    public static final Creator<ApkNotificationParams> CREATOR = new Creator<ApkNotificationParams>() {
        @Override
        public ApkNotificationParams createFromParcel(Parcel in) {
            return new ApkNotificationParams(in);
        }

        @Override
        public ApkNotificationParams[] newArray(int size) {
            return new ApkNotificationParams[size];
        }
    };

    public Context getContext() {
        return context;
    }

    public String getAuthority() {
        return authority;
    }

    public String getPath() {
        return path;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getChannelDescription() {
        return channelDescription;
    }

    public int getSmallIcon() {
        return smallIcon;
    }

    public Bitmap getLargeIcon() {
        return largeIcon;
    }

    public String getContentTitle() {
        return contentTitle;
    }

    public int getNotifactionId() {
        return notifactionId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(authority);
        dest.writeString(path);
        dest.writeString(channelId);
        dest.writeString(channelName);
        dest.writeString(channelDescription);
        dest.writeInt(smallIcon);
        dest.writeParcelable(largeIcon, flags);
        dest.writeString(contentTitle);
        dest.writeInt(notifactionId);
    }

    public static class Builder {
        private Context context;
        private String authority;//provider 自动跳转到安装界面使用
        private String path;//安装文件路径
        private String channelId = "APK_UPDATE";//通道id 8.0
        private String channelName = "update_channel_name";//通道名称
        private String channelDescription = "app更新";//通道详情
        private int smallIcon = R.mipmap.ic_launcher_round;//小图标
        private Bitmap largeIcon;//大图标 一般是应用图标
        private String contentTitle;//通知栏标题
        private int notifactionId = 1001;//通知id

        public Builder(Context context, String path) {
            this.context = context;
            this.path = path;
        }

        public ApkNotificationParams.Builder setAuthority(String authority) {
            this.authority = authority;
            return this;
        }

        public ApkNotificationParams.Builder setPath(String path) {
            this.path = path;
            return this;
        }

        public ApkNotificationParams.Builder setChannelId(String channelId) {
            this.channelId = channelId;
            return this;
        }

        public ApkNotificationParams.Builder setChannelName(String channelName) {
            this.channelName = channelName;
            return this;
        }

        public ApkNotificationParams.Builder setChannelDescription(String channelDescription) {
            this.channelDescription = channelDescription;
            return this;
        }

        public ApkNotificationParams.Builder setSmallIcon(int smallIcon) {
            this.smallIcon = smallIcon;
            return this;
        }

        public ApkNotificationParams.Builder setLargeIcon(Bitmap largeIcon) {
            this.largeIcon = largeIcon;
            return this;
        }

        public ApkNotificationParams.Builder setContentTitle(String contentTitle) {
            this.contentTitle = contentTitle;
            return this;
        }


        public ApkNotificationParams.Builder setNotifactionId(int notifactionId) {
            this.notifactionId = notifactionId;
            return this;
        }


        public ApkNotificationParams create() {
            ApkNotificationParams params = new ApkNotificationParams(this);
            return params;
        }
    }


}
