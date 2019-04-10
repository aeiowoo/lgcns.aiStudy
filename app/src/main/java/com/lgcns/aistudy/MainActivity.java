package com.lgcns.aistudy;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.YuvImage;
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

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private final int PERMISSION_CAMERA = 1;

    private CameraView cameraView;
    private ImageView captureImageView;

    private Bitmap imageBitmap;
    private Paint paint;
    private static final int COLOR_CHOICES[] = {
            Color.BLUE,
            Color.CYAN,
            Color.GREEN,
            Color.MAGENTA,
            Color.RED,
            Color.WHITE,
            Color.YELLOW
    };

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

        paint = new Paint();
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
    }

    private boolean checkIsCameraAvailable() {

        boolean isCameraAvailable = false;

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

        cameraView.getCamera().setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                Log.d("[AI STUDY]", "onPreviewFrame!");

                Log.d("[AI STUDY]", "onPreviewFrame-data.length : " + data.length);

                Camera.Parameters parameters = camera.getParameters();
                int width = parameters.getPreviewSize().width;
                int height = parameters.getPreviewSize().height;

                YuvImage yuv = new YuvImage(data, parameters.getPreviewFormat(), width, height, null);

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                yuv.compressToJpeg(new Rect(0, 0, width, height), 50, out);

                byte[] bytes = out.toByteArray();
                imageBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                detectFace();
            }
        });
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
            RectF rect = new RectF(x1,y1,x2,y2);
            tempCanvas.drawRoundRect(rect, 2, 2, myRectPaint);

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

            Random r = new Random();
            int selectedColor = COLOR_CHOICES[r.nextInt(7)];

            paint.setColor(selectedColor);
            paint.setTextSize(40);

            tempCanvas.drawText("id: " + thisFace.getId(),
                    rect.centerX(), rect.centerY(), paint);
        }

        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        tempBitmap = Bitmap.createBitmap(tempBitmap, 0, 0, tempBitmap.getWidth(),tempBitmap.getHeight(), matrix, true);

        captureImageView.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));
        faceDetector.release();
    }
}
