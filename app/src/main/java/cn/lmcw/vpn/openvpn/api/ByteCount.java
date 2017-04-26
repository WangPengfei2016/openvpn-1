package cn.lmcw.vpn.openvpn.api;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by dingyong on 2017/4/25.
 */
public class ByteCount implements Parcelable {
    public long in;
    public long out;
    public long diffIn;
    public long diffOut;

    public ByteCount(long in, long out, long diffIn, long diffOut) {
        this.in = in;
        this.out = out;
        this.diffIn = diffIn;
        this.diffOut = diffOut;
    }

    public ByteCount(){}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.in);
        dest.writeLong(this.out);
        dest.writeLong(this.diffIn);
        dest.writeLong(this.diffOut);
    }

    protected ByteCount(Parcel in) {
        this.in = in.readLong();
        this.out = in.readLong();
        this.diffIn = in.readLong();
        this.diffOut = in.readLong();
    }

    public static final Parcelable.Creator<ByteCount> CREATOR = new Parcelable.Creator<ByteCount>() {
        @Override
        public ByteCount createFromParcel(Parcel source) {
            return new ByteCount(source);
        }

        @Override
        public ByteCount[] newArray(int size) {
            return new ByteCount[size];
        }
    };
}
