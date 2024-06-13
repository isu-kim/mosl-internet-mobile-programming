package com.example.hardware;

import android.util.Log;

public class SegmentClass {

    private native void openSegment();
    private native void writeSegment(int num);
    private native void closeSegment();

    private volatile boolean isPlaying = true;
    private volatile boolean isPaused = false;

    private int score = 0;

    public SegmentClass() {
        System.loadLibrary("tetris");
    }

    /**
     * Check if the music is playing
     * @return bool value whether the music shall be playing
     */
    public synchronized boolean isPlaying() {
        return isPlaying;
    }

    /**
     * Set if the music shall be playing
     * @param isPlaying the value to set
     */
    public synchronized void setPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    /**
     * Check if music is paused
     * @return isPaused
     */
    public synchronized boolean isPaused() {
        return isPaused;
    }

    /**
     * Set music playing pause.
     * @param isPaused The value to set.
     */
    public synchronized void setIsPaused(boolean isPaused) {
        this.isPaused = isPaused;
    }

    public void StartScore() {
        Thread scoreThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    openSegment();
                    loopScoring();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        scoreThread.start();
    }

    public void loopScoring() {
        while (this.isPlaying()) {
            Log.i("info", String.valueOf(this.GetScore()));
            this.writeSegment(this.GetScore());
        }
    }

    public synchronized void SetScore(int score) {
        this.score = score;
    }

    public synchronized int GetScore(){
        return this.score;
    }

    public synchronized void ResumeScoring() {
        this.isPlaying = true;
    }

    public synchronized void PauseScoring() {
        this.isPlaying = false;
    }

    public synchronized void StopScore() {
        this.isPlaying = false;
        this.writeSegment(0);
        this.closeSegment();
    }

    public void Close() {
        this.closeSegment();
    }
}
