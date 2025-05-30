package com.lhht.xiaozhi.camera;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Environment;
import android.util.Log;
import android.util.Size;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CameraManager2 {
    private static final String TAG = "CameraManager";
    private final Context context;
    private final android.hardware.camera2.CameraManager cameraManager;
    private String cameraId;
    private PhotoCallback callback;

    public interface PhotoCallback {
        void onPhotoTaken(File photoFile);
    }

    public CameraManager2(Context context) {
        this.context = context.getApplicationContext();
        this.cameraManager = (android.hardware.camera2.CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
    }

    public void takeFrontCameraPhoto(@NonNull PhotoCallback callback) {
        this.callback = callback;
        try {
            String[] cameraIds = cameraManager.getCameraIdList();
            for (String id : cameraIds) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(id);
                Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
                    cameraId = id;
                    captureStillImage();
                    break;
                }
            }
        } catch (CameraAccessException e) {
            Log.e(TAG, "Camera access error", e);
        }
    }

    private void captureStillImage() {
        try {
            File photoFile = createTempImageFile();
            // 这里需要实现具体的拍照逻辑，示例代码省略了Camera2的完整实现
            // 实际开发中需要创建CameraCaptureSession等操作
            // 假设拍照完成回调
            callback.onPhotoTaken(photoFile);
        } catch (IOException e) {
            Log.e(TAG, "File creation failed", e);
        }
    }

    private File createTempImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "XIAOZHI_" + timeStamp + ".jpg";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(fileName, ".jpg", storageDir);
    }
}