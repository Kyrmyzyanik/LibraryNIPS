package com.example.nips.requests;

import android.util.Log;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicBoolean;

public class AsyncHttpRequest implements Runnable {
    int i = 0;

    public AsyncHttpRequest() {
    }

    public AsyncHttpRequest(int i) {
        this.i = i;
    }

    @Override
    public void run() {

            makeRequestWithRetries(i);

    }

    private void makeRequestA() {
        int j = 0;
        for(j = 0; j < 10000; j++) {
                j += j;
        }

        Log.e(" j A = "," = " + j);
    }

    private void makeRequestB() {
        int j = 0;
        for(j = 0; j < 10000; j++) {
            j = j * 2 + j;
        }

        Log.e(" j B = "," = " + j);
    }

    private void makeRequestC() {
        int j = 0;
        for(j = 0; j < 10000; j++) {
            j = j * j;
        }

        Log.e(" j C = "," = " + j);
    }

    private void makeRequestWithRetries(int k) {
        if(k == 0) {
            makeRequestA();
        }

        if(k == 1) {
            makeRequestB();
        }

        if(k == 2) {
            makeRequestC();
        }
    }

}
