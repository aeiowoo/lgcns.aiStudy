package com.lgcns.aistudy;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.vision.face.FaceDetector;

public class MainActivity extends AppCompatActivity {

    private final int PERMISSION_CAMERA = 1;

    private CameraView cameraView;
    private Button cameraChangeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        // MOS 이상 권한 체크
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

            if (cameraPermission == PackageManager.PERMISSION_GRANTED) {
                setCamera();
            } else {
                String[] permissions = { Manifest.permission.CAMERA };
                ActivityCompat.requestPermissions(this, permissions, PERMISSION_CAMERA);
            }
        } else {
            setCamera();
        }

        cameraChangeBtn = findViewById(R.id.button);
        cameraChangeBtn.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View view) {
                // TODO : click event
            }
        });
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

    private void setCamera() {
        cameraView = findViewById(R.id.cameraPreview);
    }
}
