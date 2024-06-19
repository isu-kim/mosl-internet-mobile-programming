package com.example.hardware;

import android.util.Log;

public class SegmentClass {

    private native void openSegment();
    private native void writeSegment(int num);
    private native void closeSegment();

    private boolean isPlaying = true;
    private boolean isPaused = false;

    private int score = 0;

    public SegmentClass() {
        System.loadLibrary("tetris");
    }

    /**
     * Check if the music is playing
     * @return bool value whether the music shall be playing
     */
    public boolean isPlaying() {
        return isPlaying;
    }

    /**
     * Set if the music shall be playing
     * @param isPlaying the value to set
     */
    public void setPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    /**
     * Check if music is paused
     * @return isPaused
     */
    public boolean isPaused() {
        return isPaused;
    }

    /**
     * Set music playing pause.
     * @param isPaused The value to set.
     */
    public void setIsPaused(boolean isPaused) {
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

    private void loopScoring() {
        while (true) {
                if (!isPlaying) {
                    break;
                }
                if (!isPaused) {
                    writeSegment(GetScore());
                }
            }
            try {
                Thread.sleep(1); // Adjust sleep time as needed
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

    }

    public void SetScore(int score) {
        Log.i("SCORE", "set score to " + score);
        this.score = score;
    }

    public int GetScore() {
        return this.score;
    }

    public void ResumeScoring() {
        this.isPaused = false;
    }

    public void PauseScoring() {
        this.isPaused = true;
    }

    public void StopScore() {
        this.isPlaying = false;
        this.writeSegment(0);
        this.closeSegment();
    }

    public void Close() {
        this.closeSegment();
    }
}