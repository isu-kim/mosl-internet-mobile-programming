package com.example.hardware;

import android.util.Log;

public class DipswClass {
    private native void openDipsw();
    private native void closeDipsw();
    private native int getDipsw();

    private int userID = -1;

    public DipswClass() {
        System.loadLibrary("tetris");
    }

    public void Open() {
        this.openDipsw();
    }

    public void Close() {
        this.closeDipsw();
    }

    public int GetValue() {

        int value = getDipsw();
        Log.i("info", String.valueOf(value));
        return value;
    }

    public void SetUserID(int userID) {
        this.userID = userID;
    }

    public int GetUserID() {
        return this.userID;
    }
}
