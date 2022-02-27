package com.ishuinzu.parentside.app;

import android.app.Application;

import com.ishuinzu.parentside.receiver.ConnectivityReceiver;

public class CheckInternet extends Application {
    public static CheckInternet CHECK_INTERNET;

    @Override
    public void onCreate() {
        super.onCreate();
        CHECK_INTERNET = this;
    }

    public static synchronized CheckInternet getInstance() {
        return CHECK_INTERNET;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }
}
