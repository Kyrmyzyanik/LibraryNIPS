package com.example.nips.models;

/**
 * Created by kyrmyzy on 21/10/2016.
 */

public class NIPSBounds {
    private long minLongitude;
    private long minLatitude;

    private long maxLongitude;
    private long maxLatitude;

    public long getMinLongitude() {
        return minLongitude;
    }

    public void setMinLongitude(long minLongitude) {
        this.minLongitude = minLongitude;
    }

    public long getMinLatitude() {
        return minLatitude;
    }

    public void setMinLatitude(long minLatitude) {
        this.minLatitude = minLatitude;
    }

    public long getMaxLongitude() {
        return maxLongitude;
    }

    public void setMaxLongitude(long maxLongitude) {
        this.maxLongitude = maxLongitude;
    }

    public long getMaxLatitude() {
        return maxLatitude;
    }

    public void setMaxLatitude(long maxLatitude) {
        this.maxLatitude = maxLatitude;
    }



}
