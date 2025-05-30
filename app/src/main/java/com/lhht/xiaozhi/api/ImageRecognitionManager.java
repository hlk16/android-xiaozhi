package com.lhht.xiaozhi.api;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.util.Log;

import com.iflytek.sparkchain.core.LLM;
import com.iflytek.sparkchain.core.LLMCallbacks;
import com.iflytek.sparkchain.core.LLMConfig;
import com.iflytek.sparkchain.core.LLMError;
import com.iflytek.sparkchain.core.LLMEvent;
import com.iflytek.sparkchain.core.LLMFactory;
import com.iflytek.sparkchain.core.LLMResult;
import com.iflytek.sparkchain.core.Memory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageRecognitionManager {
    private static final String TAG = "ImageRecognitionManager";
    private final Context context;
    private LLM llm;
    private int token = 0;
    private ImageRecognitionCallback callback;

    public interface ImageRecognitionCallback {
        void onRecognitionResult(String content);
        void onRecognitionError(String errorMessage);
    }

    public ImageRecognitionManager(Context context, ImageRecognitionCallback callback) {
        this.context = context;
        this.callback = callback;
        initLLM();
    }

    private void initLLM() {
        LLMConfig llmConfig = LLMConfig.builder()
                .maxToken(2048);
        Memory window_memory = Memory.windowMemory(5);
        llm = LLMFactory.imageUnderstanding(llmConfig, window_memory);
        llm.registerLLMCallbacks(createLLMCallbacks());
    }

    private LLMCallbacks createLLMCallbacks() {
        return new LLMCallbacks() {
            @Override
            public void onLLMResult(LLMResult llmResult, Object usrContext) {
                if (token == (int) usrContext) {
                    String content = llmResult.getContent();
                    if (content != null && callback != null) {
                        callback.onRecognitionResult(content);
                    }
                    if (llmResult.getStatus() == 2) {
                        Log.d(TAG, String.format("Recognition completed - Tokens: completion=%d, prompt=%d, total=%d",
                                llmResult.getCompletionTokens(),
                                llmResult.getPromptTokens(),
                                llmResult.getTotalTokens()));
                    }
                }
            }

            @Override
            public void onLLMEvent(LLMEvent event, Object o) {
                // 预留事件处理
            }

            @Override
            public void onLLMError(LLMError error, Object o) {
                if (callback != null) {
                    callback.onRecognitionError(String.format("错误: %d - %s",
                            error.getErrCode(),
                            error.getErrMsg()));
                }
            }
        };
    }

    public void processPreviewFrame(byte[] data, Camera camera) {
        try {
            byte[] processedImageData = convertPreviewFrameToJpeg(data, camera);
            if (processedImageData != null) {
                recognizeImage(processedImageData);
            }
        } catch (IOException e) {
            Log.e(TAG, "处理预览帧失败", e);
            if (callback != null) {
                callback.onRecognitionError("图像处理失败: " + e.getMessage());
            }
        }
    }

    private byte[] convertPreviewFrameToJpeg(byte[] data, Camera camera) throws IOException {
        Camera.Parameters parameters = camera.getParameters();
        int width = parameters.getPreviewSize().width;
        int height = parameters.getPreviewSize().height;

        YuvImage yuv = new YuvImage(data, parameters.getPreviewFormat(), width, height, null);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // 初始压缩，使用较高质量
        yuv.compressToJpeg(new Rect(0, 0, width, height), 85, out);

        byte[] imageBytes = out.toByteArray();
        if (imageBytes.length > 2 * 1024 * 1024) { // 2MB限制
            // 进一步压缩
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            out.reset();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, out);
            imageBytes = out.toByteArray();

            // 如果仍然太大，最终压缩
            if (imageBytes.length > 2 * 1024 * 1024) {
                out.reset();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
                imageBytes = out.toByteArray();
                bitmap.recycle();
            }
        }

        return imageBytes;
    }

    private void recognizeImage(byte[] imageData) {
        if (llm == null) {
            Log.e(TAG, "LLM未初始化");
            return;
        }
        token++;
        llm.clearHistory();
        int ret = llm.arun("这是什么", imageData, token);
        if (ret != 0) {
            Log.e(TAG, "识别请求失败: " + ret);
            if (callback != null) {
                callback.onRecognitionError("识别请求失败，错误码: " + ret);
            }
        }
    }

    public void release() {
        llm = null;
        callback = null;
    }
}