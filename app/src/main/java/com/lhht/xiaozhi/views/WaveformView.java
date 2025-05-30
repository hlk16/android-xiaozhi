package com.lhht.xiaozhi.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class WaveformView extends View {
    private float[] amplitudes;
    private Paint paint;
    private Path path;

    public WaveformView(Context context) {
        super(context);
        init();
    }

    public WaveformView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WaveformView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2f);
        paint.setAntiAlias(true);

        path = new Path();
    }

    public void setAmplitudes(float[] amplitudes) {
        this.amplitudes = amplitudes;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (amplitudes == null || amplitudes.length == 0) {
            return;
        }

        float width = getWidth();
        float height = getHeight();
        float centerY = height / 2;
        float maxAmplitude = 0.5f; // 最大振幅为视图高度的一半

        path.reset();
        float stepX = width / (amplitudes.length - 1);

        // 绘制波形
        path.moveTo(0, centerY);
        for (int i = 0; i < amplitudes.length; i++) {
            float x = i * stepX;
            float y = centerY + (amplitudes[i] * height * maxAmplitude);
            path.lineTo(x, y);
        }

        canvas.drawPath(path, paint);
    }
} 