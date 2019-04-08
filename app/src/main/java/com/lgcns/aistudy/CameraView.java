package com.lgcns.aistudy;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

public class CameraView extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder holder;

    private Camera camera;

    public CameraView(Context context) {
        super(context);

        camera = new Camera();
        holder = getHolder();
        holder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        Canvas canvas = holder.lockCanvas();
        camera.applyToCanvas(canvas);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
