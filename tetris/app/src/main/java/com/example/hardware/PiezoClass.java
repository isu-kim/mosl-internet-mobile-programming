package com.example.hardware;

import static com.example.hardware.PiezoClass.Notes.*;

public class PiezoClass {
    /**
     * Note definition for HB FPGA Piezo
     */
    public static class Notes {
        public static final int NOTE_A5 = 0x16;
        public static final int NOTE_G5 = 0x15;
        public static final int NOTE_F5 = 0x14;
        public static final int NOTE_E5 = 0x13;
        public static final int NOTE_D5 = 0x12;
        public static final int NOTE_C5 = 0x11;
        public static final int NOTE_B4 = 0x07;
        public static final int NOTE_A4 = 0x06;
        public static final int REST = 0;
    }

    /**
     * Tetris theme from <a href="https://www.flutetunes.com/tunes.php?id=192">...</a>
     */
    private static final int[] melody = new int[]{
            NOTE_E5, 4, NOTE_B4, 8, NOTE_C5, 8, NOTE_D5, 4, NOTE_C5, 8, NOTE_B4, 8,
            NOTE_A4, 4, NOTE_A4, 8, NOTE_C5, 8, NOTE_E5, 4, NOTE_D5, 8, NOTE_C5, 8,
            NOTE_B4, -4, NOTE_C5, 8, NOTE_D5, 4, NOTE_E5, 4,
            NOTE_C5, 4, NOTE_A4, 4, NOTE_A4, 8, NOTE_A4, 4, NOTE_B4, 8, NOTE_C5, 8,

            NOTE_D5, -4, NOTE_F5, 8, NOTE_A5, 4, NOTE_G5, 8, NOTE_F5, 8,
            NOTE_E5, -4, NOTE_C5, 8, NOTE_E5, 4, NOTE_D5, 8, NOTE_C5, 8,
            NOTE_B4, 4, NOTE_B4, 8, NOTE_C5, 8, NOTE_D5, 4, NOTE_E5, 4,
            NOTE_C5, 4, NOTE_A4, 4, NOTE_A4, 4, REST, 4,

            NOTE_E5, 4, NOTE_B4, 8, NOTE_C5, 8, NOTE_D5, 4, NOTE_C5, 8, NOTE_B4, 8,
            NOTE_A4, 4, NOTE_A4, 8, NOTE_C5, 8, NOTE_E5, 4, NOTE_D5, 8, NOTE_C5, 8,
            NOTE_B4, -4, NOTE_C5, 8, NOTE_D5, 4, NOTE_E5, 4,
            NOTE_C5, 4, NOTE_A4, 4, NOTE_A4, 8, NOTE_A4, 4, NOTE_B4, 8, NOTE_C5, 8,

            NOTE_D5, -4, NOTE_F5, 8, NOTE_A5, 4, NOTE_G5, 8, NOTE_F5, 8,
            NOTE_E5, -4, NOTE_C5, 8, NOTE_E5, 4, NOTE_D5, 8, NOTE_C5, 8,
            NOTE_B4, 4, NOTE_B4, 8, NOTE_C5, 8, NOTE_D5, 4, NOTE_E5, 4,
            NOTE_C5, 4, NOTE_A4, 4, NOTE_A4, 4, REST, 4,

            NOTE_E5, 2, NOTE_C5, 2,
            NOTE_D5, 2, NOTE_B4, 2,
            NOTE_C5, 2, NOTE_A4, 2
    };

    private volatile boolean isPlaying = true;
    private int tempo = 144;


    private native void openPizeo();
    private native void writePizeo(char data);
    private native void closePizeo();


    public PiezoClass() {
        System.loadLibrary("hb_test");
        openPizeo();
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
     * Get music tempo
     * @return int value of the tempo
     */
    public synchronized int getTempo() {
        return tempo;
    }

    /**
     * Set music tempo
     * @param tempo the tempo to set
     */
    public synchronized void setTempo(int tempo) {
        this.tempo = tempo;
    }

    /**
     * Infinitely plays tetris theme, this will adjust to the tempo which is set by the
     * game itself
     */
    private void loopTetrisTheme() {
        while (isPlaying()) {
            int notes = melody.length / 2;
            int wholeNote = 60000 * 4 / getTempo();
            int divider = 0;
            int noteDuration = 0;

            for (int thisNote = 0; thisNote < notes * 2; thisNote = thisNote + 2) {
                divider = melody[thisNote + 1];
                if (divider > 0) {
                    noteDuration = wholeNote / divider;
                } else if (divider < 0) {
                    noteDuration = wholeNote / 4;
                    noteDuration = (int) (noteDuration * 1.5);
                }

                this.writePizeo((char) melody[thisNote]);

                try {
                    Thread.sleep(noteDuration);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                this.writePizeo((char) 0x00);
            }
        }
    }

    /**
     * Starts playing tetris theme in another thread
     */
    public void PlayTetrisTheme() {
        Thread tetrisThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    openPizeo();
                    loopTetrisTheme();
                    closePizeo();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        tetrisThread.start();
    }

    /**
     * Stops Tetris theme
     */
    public void StopTetrisTheme() {
        this.setPlaying(false);
        this.closePizeo();
    }
}
