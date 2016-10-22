package com.example.nips.models;

/**
 * Created by kyrmyzy on 21/10/2016.
 */

public class NIPSTrail {
    private String id;
    private String journeyId;
    private NIPSCoordinate[] ends;
    private NIPSBounds  bounds;
    private NIPSEvent[] events;
    private NIPSCoordinate[] coords;
    //optional
    private long score;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJourneyId() {
        return journeyId;
    }

    public void setJourneyId(String journeyId) {
        this.journeyId = journeyId;
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

    public NIPSEvent[] getEvents() {
        return events;
    }

    public void setEvents(NIPSEvent[] events) {
        this.events = events;
    }

    public NIPSCoordinate[] getCoords() {
        return coords;
    }

    public void setCoords(NIPSCoordinate[] coords) {
        this.coords = coords;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }
}
