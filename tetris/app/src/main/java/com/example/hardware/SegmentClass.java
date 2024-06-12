package com.example.hardware;

public class SegmentClass {

    private native void openSegment();
    private native void writeSegment(int num);
    private native void closeSegment();

    public SegmentClass() {
        System.loadLibrary("tetris");
    }

    public void StartScore() {
        this.openSegment();
    }

    public void SetScore(int score) {
        this.writeSegment(score);
    }

    public void CleanUpScore() {
        this.writeSegment(0);
    }

    public void Close() {
        this.closeSegment();
    }
}
