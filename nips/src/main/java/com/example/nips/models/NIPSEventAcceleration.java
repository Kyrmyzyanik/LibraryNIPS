package com.example.nips.models;

/**
 * Created by kyrmyzy on 21/10/2016.
 */

public class NIPSEventAcceleration {

    private long dt;

    //Difference in speed [m/s]
    private long dv;

    public long getDt() {
        return dt;
    }

    public void setDt(long dt) {
        this.dt = dt;
    }

    public long getDv() {
        return dv;
    }

    public void setDv(long dv) {
        this.dv = dv;
    }
}
