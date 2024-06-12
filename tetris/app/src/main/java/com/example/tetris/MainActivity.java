package com.example.tetris;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Toast;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.example.hardware.HWClass;

public class MainActivity extends Activity {
    TetrisCtrl mTetrisCtrl;
    Point mScreenSize = new Point(0, 0);
    Point mMousePos = new Point(-1, -1);
    int mCellSize = 0;
    boolean mIsTouchMove = false;

    // JNI Methods
    HWClass hwc = new HWClass();

    static {
        System.loadLibrary("tetris");
    }

    public native String stringFromJNI(); // @todo: remove this, just for debugging.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("[INFO]", "onCreate");
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        // @todo: remove this, just for debugging
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(this /* MyActivity */, stringFromJNI(), duration);
        toast.show();
        // @todo: till here

        DisplayMetrics dm = this.getApplicationContext().getResources().getDisplayMetrics();
        mScreenSize.x = dm.widthPixels;
        mScreenSize.y = dm.heightPixels;
        mCellSize = (int)(mScreenSize.x / 8);

        initTetrisCtrl();
    }

    void initTetrisCtrl() {
        mTetrisCtrl = new TetrisCtrl(this);
        mTetrisCtrl.SetHWControl(hwc);
        for(int i=0; i <= 7; i++) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cell0 + i);
            mTetrisCtrl.addCellImage(i, bitmap);
        }

        RelativeLayout layoutCanvas = findViewById(R.id.layoutCanvas);
        layoutCanvas.addView(mTetrisCtrl);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_4:
                mTetrisCtrl.block2Left();
                return true;
            case KeyEvent.KEYCODE_5:
                mTetrisCtrl.block2Rotate();
                return true;
            case KeyEvent.KEYCODE_6:
                mTetrisCtrl.block2Right();
                return true;
            case KeyEvent.KEYCODE_2:
                mTetrisCtrl.userRestart();
                return true;
            case KeyEvent.KEYCODE_8:
                mTetrisCtrl.block2Bottom();
                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }


    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        switch( event.getAction() ) {
            case MotionEvent.ACTION_DOWN :
                mIsTouchMove = false;
                if( event.getY() < (int)(mScreenSize.y * 0.75)) {
                    mMousePos.x = (int) event.getX();
                    mMousePos.y = (int) event.getY();
                }
                break;
            case MotionEvent.ACTION_MOVE :
                if( mMousePos.x < 0 )
                    break;
                if( (event.getX() - mMousePos.x) > mCellSize ) {
                    mTetrisCtrl.block2Right();
                    mMousePos.x = (int) event.getX();
                    mMousePos.y = (int) event.getY();
                    mIsTouchMove = true;
                } else if( (mMousePos.x - event.getX()) > mCellSize ) {
                    mTetrisCtrl.block2Left();
                    mMousePos.x = (int) event.getX();
                    mMousePos.y = (int) event.getY();
                    mIsTouchMove = true;
                }
                break;
            case MotionEvent.ACTION_UP :
                if( mIsTouchMove == false && mMousePos.x > 0 )
                    mTetrisCtrl.block2Rotate();
                mMousePos.set(-1, -1);
                break;
        }
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mTetrisCtrl.pauseGame();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mTetrisCtrl.restartGame();
    }
}
