package com.example.nips.requests;

/**
 * Created by kyrmyzy on 21/10/2016.
 */

public class AsyncHttpClient {

    AsyncHttpRequest r = new AsyncHttpRequest();

    public RequestHandle get(int j, ResponseHandlerInterface kk) {
//        r = new AsyncHttpRequest(j);
//        r.run();
        RequestHandle tt = new RequestHandle(new AsyncHttpRequest(j));
        return tt;
    }

    public void getEventWithID() {
//        r.
    }

    public void getEventsSince() {

    }

    public void getTrailWithTrailID() {

    }

    public void getTrailsSince() {

    }

    public void getJourneyWithID() {

    }


    public void getJourneysSince() {

    }
}
