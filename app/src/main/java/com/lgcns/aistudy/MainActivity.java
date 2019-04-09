package com.lgcns.aistudy;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final int PERMISSION_CAMERA = 1;

    private final int CAMERA_FACING_FRONT = 1;
    private final int CAMERA_FACING_BACK = 0;

    private CameraView cameraView;
    private Button cameraChangeBtn;
    private Button faceDetectBtn;
    private Button imgFaceDetectBtn;
    private ImageView captureImageView;

    private Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        setCustomContentViews();

        if(checkIsCameraAvailable()) {
            setCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == PERMISSION_CAMERA) {

            boolean isGrantPermission = true;

            for(int result : grantResults) {
                if(result != PackageManager.PERMISSION_GRANTED) {
                    isGrantPermission = false;
                    break;
                }
            }

            if(isGrantPermission) {
                setCamera();
            } else {
                Toast.makeText(this, "권한 획득이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setCustomContentViews() {
        captureImageView = findViewById(R.id.captureImageView);

        cameraChangeBtn = findViewById(R.id.cameraChangeBtn);
        cameraChangeBtn.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View view) {
                changeCamera();
            }
        });

        faceDetectBtn = findViewById(R.id.faceDetectBtn);
        faceDetectBtn.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View view) {
                captureCameraImage();
            }
        });

        imgFaceDetectBtn = findViewById(R.id.imgFaceDetectBtn);
        imgFaceDetectBtn.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View view) {

                //findViewById(R.id.testImg).setVisibility(View.VISIBLE);

                Drawable drawable = getResources().getDrawable(R.drawable.test);
                imageBitmap = ((BitmapDrawable)drawable).getBitmap();

                detectFace();
            }
        });
    }

    private boolean checkIsCameraAvailable() {

        boolean isCameraAvailable = false;

        // MOS 이상 권한 체크
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

            if (cameraPermission == PackageManager.PERMISSION_GRANTED) {
                isCameraAvailable = true;
            } else {
                String[] permissions = { Manifest.permission.CAMERA };
                ActivityCompat.requestPermissions(this, permissions, PERMISSION_CAMERA);
            }
        } else {
            isCameraAvailable = true;
        }
        return isCameraAvailable;
    }

    private void setCamera() {
        cameraView = findViewById(R.id.cameraPreview);
    }

    private void changeCamera() {

//        cameraView.getCamera().stopPreview();
//        cameraView.getCamera().release();
//
//        cameraView.setCameraFacing(cameraView.getCameraFacing() == CAMERA_FACING_FRONT ? CAMERA_FACING_BACK : CAMERA_FACING_FRONT);
//        cameraView = findViewById(R.id.cameraPreview);
//
//        cameraView.startCameraPreview();
    }

    private void captureCameraImage() {

        Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
            public void onShutter() {
            }
        };

        Camera.PictureCallback rawCallback = new Camera.PictureCallback() {
            public void onPictureTaken(byte[] data, Camera camera) {
            }
        };

        Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
            public void onPictureTaken(byte[] data, Camera camera) {
                //이미지의 너비와 높이 결정
                int w = camera.getParameters().getPictureSize().width;
                int h = camera.getParameters().getPictureSize().height;
                //int orientation = calculatePreviewOrientation(mCameraInfo, mDisplayOrientation);

                Log.d("[AI STUDY]", "onPictureTaken w : " + w);
                Log.d("[AI STUDY]", "onPictureTaken h : " + h);

                //byte array를 bitmap으로 변환
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                imageBitmap = BitmapFactory.decodeByteArray( data, 0, data.length, options);

                //이미지를 디바이스 방향으로 회전
//                Matrix matrix = new Matrix();
//                matrix.postRotate(270);
//                imageBitmap = Bitmap.createBitmap(imageBitmap, 0, 0, w, h, matrix, true);

                //captureImageView.setImageBitmap(imageBitmap);
                detectFace();
                captureImageView.setRotationX(90);
            }
        };

        cameraView.getCamera().takePicture(shutterCallback, rawCallback, jpegCallback);
    }

    private void detectFace() {

        //rect color paint
        Paint myRectPaint = new Paint();
        myRectPaint.setStrokeWidth(5);
        myRectPaint.setColor(Color.RED);
        myRectPaint.setStyle(Paint.Style.STROKE);
        //circle color paint
        Paint Pnt = new Paint();
        Pnt.setColor(Color.GREEN);
        Pnt.setStrokeWidth(5);
        Pnt.setStyle(Paint.Style.STROKE);

        Log.d("[AI STUDY]", "processDetectedFace imageBitmap.getWidth() : " + imageBitmap.getWidth());
        Log.d("[AI STUDY]", "processDetectedFace imageBitmap.getHeight() : " + imageBitmap.getHeight());

        Bitmap tempBitmap = Bitmap.createBitmap(imageBitmap.getWidth(), imageBitmap.getHeight(), Bitmap.Config.RGB_565);
        Canvas tempCanvas = new Canvas(tempBitmap);
        tempCanvas.drawBitmap(imageBitmap, 0, 0, null);

        FaceDetector faceDetector = new FaceDetector.Builder(getApplicationContext())
                .setTrackingEnabled(true)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .build();
        if(!faceDetector.isOperational()){
            //new AlertDialog.Builder(view.getContext()).setMessage("couldn't set up the face detector!");
            return;
        }

        Frame frame = new Frame.Builder()
                .setBitmap(imageBitmap)
                .build();
        SparseArray<Face> faces = faceDetector.detect(frame);

        Log.d("[AI STUDY]", "faces.size() : " + faces.size());

        for(int i=0; i<faces.size(); i++){
            Face thisFace = faces.valueAt(i);
            float x1 = thisFace.getPosition().x;
            float y1 = thisFace.getPosition().y;
            float x2 = x1 + thisFace.getWidth();
            float y2 = y1 + thisFace.getHeight();
            tempCanvas.drawRoundRect(new RectF(x1,y1,x2,y2), 2, 2, myRectPaint);

            //draw landmarks
            List<Landmark> landmarks = thisFace.getLandmarks();

            Log.d("[AI STUDY]", "landmarks.size() : " + landmarks.size());

            //for make landmark circle
            for(int j=0; j<landmarks.size(); j++){
                Landmark thisLand = landmarks.get(j);
                float landx = thisLand.getPosition().x;
                float landy = thisLand.getPosition().y;
                tempCanvas.drawCircle(landx, landy, 10, Pnt);
            }
        }
        captureImageView.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));
        faceDetector.release();
    }
}
