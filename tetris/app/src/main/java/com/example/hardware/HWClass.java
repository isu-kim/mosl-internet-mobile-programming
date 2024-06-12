package com.example.hardware;

public class HWClass {
    public PiezoClass pc;
    public SegmentClass sc;
    public DipswClass dc;

    public HWClass() {
        this.pc = new PiezoClass();
        this.sc = new SegmentClass();
        this.dc = new DipswClass();
    }

    public void Init() {
        this.pc.PlayTetrisTheme();
        this.sc.StartScore();
    }
}
