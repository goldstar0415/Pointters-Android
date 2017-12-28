package com.pointters.model;

/**
 * Created by prashantkumar on 9/10/17.
 */

public class TagServiceSellerModel {

    private String _id;
    private String _type;
    private String _score;
    private TagSourceModel _source;


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

    public TagSourceModel getSource() {
        return _source;
    }

    public void setSource(TagSourceModel source) {
        this._source = source;
    }
}
