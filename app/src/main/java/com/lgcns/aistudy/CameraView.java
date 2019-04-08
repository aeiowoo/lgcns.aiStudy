package com.lgcns.aistudy;

import android.content.Context;
import android.graphics.Canvas;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

import java.io.IOException;

public class CameraView extends SurfaceView implements SurfaceHolder.Callback {

    private final int BACK_CAMERA = 0;
    private final int FRONT_CAMERA = 1;

    private SurfaceHolder holder;

//    private Camera camera;
    private Camera camera;

    public CameraView(Context context, AttributeSet attr) {
        super(context, attr);

//        camera = new Camera();

        if(camera == null){
            camera = Camera.open(FRONT_CAMERA);
        }

        holder = getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

//        Canvas canvas = holder.lockCanvas();
//        camera.applyToCanvas(canvas);

        try {
            camera.setDisplayOrientation(90);
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        if(camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }
}
