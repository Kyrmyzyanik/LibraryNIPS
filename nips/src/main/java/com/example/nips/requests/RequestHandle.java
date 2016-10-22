package com.example.nips.requests;

/**
 * Created by kyrmyzy on 21/10/2016.
 */

import android.os.Looper;

import java.lang.ref.WeakReference;

/**
 * A Handle to an AsyncRequest which can be used to cancel a running request.
 */
public class RequestHandle {
    private final WeakReference<AsyncHttpRequest> request;

    public RequestHandle(AsyncHttpRequest request) {
        this.request = new WeakReference<AsyncHttpRequest>(request);
        request.run();
    }



}
