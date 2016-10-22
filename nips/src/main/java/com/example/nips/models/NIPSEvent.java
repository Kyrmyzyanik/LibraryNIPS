package com.example.nips.models;

import java.util.Date;

/**
 * Created by kyrmyzy on 21/10/2016.
 */

public class NIPSEvent {
    private Date time;
    private String type;

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
