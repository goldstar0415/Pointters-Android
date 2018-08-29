package com.pointters.model;

/**
 * Created by prashantkumar on 27/9/17.
 */

public class Media {
    private String _id;
    private String fileName;
    private String mediaType;


    public Media(){

    }

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

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}
