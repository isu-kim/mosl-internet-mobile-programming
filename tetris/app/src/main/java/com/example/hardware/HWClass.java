package com.example.hardware;

public class HWClass {
    public PiezoClass pc;
    public SegmentClass sc;

    public HWClass() {
        this.pc = new PiezoClass();
        this.sc = new SegmentClass();
    }

    public void Init() {
        this.pc.PlayTetrisTheme();
        this.sc.StartScore();
    }
}
