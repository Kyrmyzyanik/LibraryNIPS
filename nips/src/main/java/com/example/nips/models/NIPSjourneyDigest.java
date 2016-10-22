package com.example.nips.models;

/**
 * Created by kyrmyzy on 21/10/2016.
 */

public class NIPSjourneyDigest {
    private String id;
    private NIPSCoordinate[] ends;
    private NIPSBounds  bounds;
    //optional
    private long score;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public NIPSCoordinate[] getEnds() {
        return ends;
    }

    public void setEnds(NIPSCoordinate[] ends) {
        this.ends = ends;
    }

    public NIPSBounds getBounds() {
        return bounds;
    }

    public void setBounds(NIPSBounds bounds) {
        this.bounds = bounds;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }
}
