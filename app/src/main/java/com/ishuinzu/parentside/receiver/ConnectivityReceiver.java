package com.ishuinzu.parentside.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.ishuinzu.parentside.app.CheckInternet;

import java.io.IOException;

public class ConnectivityReceiver extends BroadcastReceiver {
    public static ConnectivityReceiverListener connectivityReceiverListener;

    public ConnectivityReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (connectivityReceiverListener != null) {
            connectivityReceiverListener.onNetworkConnectionChange(isConencted());
        }
    }

    public static boolean isConencted() {
        boolean icConnected = false;
        boolean isWorking = false;

        ConnectivityManager connectivityManager = (ConnectivityManager) CheckInternet.getInstance().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            icConnected = true;

            try {
                String command = "ping -c 1 google.com";
                if (Runtime.getRuntime().exec(command).waitFor() == 0) {
                    isWorking = true;
                }
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }

        return icConnected && isWorking;
    }

    public interface ConnectivityReceiverListener {
        void onNetworkConnectionChange(boolean isConnected);
    }
}
