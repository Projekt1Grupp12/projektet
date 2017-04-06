/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.networkconnect;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;

/**
 * Sample Activity demonstrating how to connect to the network and fetch raw
 * HTML. It uses a Fragment that encapsulates the network operations on an AsyncTask.
 *
 * This sample uses a TextView to display output.
 */
public class MainActivity extends FragmentActivity implements DownloadCallback {

    // Reference to the TextView showing fetched data, so we can clear it with a button
    // as necessary.
    private TextView mDataText;
    private TextView mTextIP;

    // Keep a reference to the NetworkFragment which owns the AsyncTask object
    // that is used to execute network ops.
    private NetworkFragment mNetworkFragment;

    // Boolean telling us whether a download is in progress, so we don't trigger overlapping
    // downloads with consecutive button clicks.
    private boolean mDownloading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_main);
        mDataText = (TextView) findViewById(R.id.data_text);
        mTextIP = (EditText) findViewById(R.id.pingIP_Address);
        mNetworkFragment = NetworkFragment.getInstance(getSupportFragmentManager(), "https://www.google.com");
        Log.d("CREATE", "onCreate() is being executed");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("BUTTON", "onOptionsItemSelected() is being executed");
        GetMethodDemo gmd = new GetMethodDemo();
        switch (item.getItemId()) {
            // When the user clicks FETCH, fetch the first 500 characters of
            // raw HTML from www.google.com.
            case R.id.fetch_action:
                Log.d("FETCH", "trying to fetch data");
                startDownload();
                return true;
            // Clear the text and cancel download.
            case R.id.clear_action:
                finishDownloading();
                gmd.cancel(true);

                Log.d("gmd.getStatus()", String.valueOf(gmd.getStatus()));
                mDataText.setText("");
                Log.d("CLEAR", "text cleared");
            return true;
            case R.id.ping_action:
                Log.d("PING", "trying to ping");

                String host = String.valueOf(mTextIP.getText());
                Log.d("mTextIP.getText()", host);

                gmd.execute("http://"+host);
                //startPing(host);
                return true;
        }
        return false;
    }


    private void startDownload() {
        if (!mDownloading && mNetworkFragment != null) {
            // Execute the async download.
            mNetworkFragment.startDownload();
            mDownloading = true;
        }
    }

    private void startPing(String Url){
        if(!mDownloading && mNetworkFragment != null){
            mNetworkFragment.startPing(Url);
            mDownloading = true;
        }
    }

    @Override
    public void updateFromDownload(String result) {
        if (result != null) {
            mDataText.setText(result);
        } else {
            mDataText.setText(getString(R.string.connection_error));
        }
    }

    @Override
    public NetworkInfo getActiveNetworkInfo() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo;
    }

    @Override
    public void finishDownloading() {
        mDownloading = false;
        if (mNetworkFragment != null) {
            mNetworkFragment.cancelDownload();
        }
    }

    @Override
    public void onProgressUpdate(int progressCode, int percentComplete) {
        switch(progressCode) {
            // You can add UI behavior for progress updates here.
            case Progress.ERROR:
                break;
            case Progress.CONNECT_SUCCESS:
                break;
            case Progress.GET_INPUT_STREAM_SUCCESS:
                break;
            case Progress.PROCESS_INPUT_STREAM_IN_PROGRESS:
                mDataText.setText("" + percentComplete + "%");
                break;
            case Progress.PROCESS_INPUT_STREAM_SUCCESS:
                break;
        }
    }
}
