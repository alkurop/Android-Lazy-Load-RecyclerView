package com.github.alkurop.updatinglist.mock_classes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by alkurop on 01.07.16.
 */
public class DataObject implements Parcelable {
    int i = 0;

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {dest.writeInt(this.i);}

    public DataObject() {}

    protected DataObject(Parcel in) {this.i = in.readInt();}

    public static final Parcelable.Creator<DataObject> CREATOR = new Parcelable.Creator<DataObject>() {
        @Override public DataObject createFromParcel(Parcel source) {return new DataObject(source);}

        @Override public DataObject[] newArray(int size) {return new DataObject[size];}
    };
}
