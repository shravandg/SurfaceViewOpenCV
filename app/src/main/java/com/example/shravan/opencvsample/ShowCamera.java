package com.example.shravan.opencvsample;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Created by Shravan on 2/15/2017.
 */
public class ShowCamera extends SurfaceView implements SurfaceHolder.Callback {

    private Camera myCam;
    private SurfaceHolder holdMe;

    public ShowCamera(Context c, Camera myCam){
        super(c);
        this.myCam = myCam;
        holdMe = getHolder();
        holdMe.addCallback(this);
    }
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
            myCam.setPreviewDisplay(surfaceHolder);
            myCam.startPreview();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        myCam.stopPreview();
        myCam.release();
    }
}
