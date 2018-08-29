package com.pointters.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

/**
 * Created by vikas on 8/17/2017.
 */

public class FileAndBitmapModel implements Parcelable{

    private File file;
    private Bitmap bitmap;
    private String fileType;

    public FileAndBitmapModel(File file, Bitmap bitmap, String fileType) {
        this.file = file;
        this.bitmap = bitmap;
        this.fileType = fileType;
    }

    protected FileAndBitmapModel(Parcel in) {
        bitmap = in.readParcelable(Bitmap.class.getClassLoader());
        fileType = in.readString();
    }

    public static final Creator<FileAndBitmapModel> CREATOR = new Creator<FileAndBitmapModel>() {
        @Override
        public FileAndBitmapModel createFromParcel(Parcel in) {
            return new FileAndBitmapModel(in);
        }

        @Override
        public FileAndBitmapModel[] newArray(int size) {
            return new FileAndBitmapModel[size];
        }
    };

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(bitmap, i);
        parcel.writeString(fileType);
    }
}
