package com.pointters.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by prashantkumar on 27/9/17.
 */

public class Media implements Serializable {
    private String fileName;
    private String mediaType;

    public Media(String fileName, String mediaType) {
        this.fileName = fileName;
        this.mediaType = mediaType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }


}
