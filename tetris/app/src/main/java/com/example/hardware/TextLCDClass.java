package com.example.hardware;

public class TextLCDClass {
    public TextLCDClass() {
        System.loadLibrary("tetris");
    }

    public native void on();
    public native void off();
    public native void initialize();
    public native void clear();

    public native void printLine1(String str);
    public native void printLine2(String str);

    public void Init() {
        this.initialize();
        this.on();
    }

    public void UnInit() {
        this.clear();
        this.off();
    }
}
