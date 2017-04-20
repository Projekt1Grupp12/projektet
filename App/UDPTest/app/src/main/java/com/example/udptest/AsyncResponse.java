package com.example.udptest;

/**
 * Created by George on 2017-04-19.
 */

/**
 * Interface used as a listner to AsyncTask so that MainActivity receives
 * the resulting output from AsyncTask from the server.
 */
public interface AsyncResponse {
    void postResult(String asyncresult);
}
