package com.pointters.model;

/**
 * Created by mac on 12/22/17.
 */

public class SearchSendServicesModel {

    private String _id;
    private String _type;
    private String _score;
    private Service _source;


    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = id;
    }

    public String getType() {
        return _type;
    }

    public void setType(String type) {
        this._type = type;
    }

    public String getScore() {
        return _score;
    }

    public void setScore(String score) {
        this._score = score;
    }

    public Service getSource() {
        return _source;
    }

    public void setSource(Service source) {
        this._source = source;
    }
}
