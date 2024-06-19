package com.example.hardware;

public class FullColorLEDClass {
    public FullColorLEDClass() {
        System.loadLibrary("tetris");
    }

    private native void Write(int num, int r, int g, int b);
    public void LEDOn(int r, int g, int b) {
        this.Write(5, r, g, b);
    }

    public void Blink(int r, int g, int b) {
        this.LEDOn(r, g, b);
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        this.LEDOn(0, 0, 0);
    }
}
