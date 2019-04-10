package com.lgcns.aistudy;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

import java.io.IOException;

public class CameraView extends SurfaceView implements SurfaceHolder.Callback {

    private int cameraFacing = 0;

    private SurfaceHolder holder;
    private Camera camera;
    private Camera.CameraInfo cameraInfo;

    public Camera getCamera() {
        return camera;
    }

    public CameraView(Context context, AttributeSet attr) {
        super(context, attr);

        Log.d("[AI STUDY]", "CameraView Constructor!");

        if(camera == null) {
            camera = Camera.open(cameraFacing);
        }

        cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraFacing, cameraInfo);

        holder = getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void startCameraPreview() {
        try {

            camera.setDisplayOrientation(90);
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        startCameraPreview();
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
