package com.example.hardware;

public class HWClass {
    public PiezoClass pc;
    public SegmentClass sc;
    public DipswClass dc;

    public TextLCDClass tc;
    public FullColorLEDClass flc;

    public HWClass() {
        this.pc = new PiezoClass();
        this.sc = new SegmentClass();
        this.dc = new DipswClass();
        this.tc = new TextLCDClass();
        this.flc = new FullColorLEDClass();
    }

    public void Init() {
        this.pc.PlayTetrisTheme();
        this.sc.StartScore();
    }
}
