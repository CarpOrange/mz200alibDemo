package com.haoxueche.mz200alib.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.haoxueche.mz200alib.R;


public class FaceView1 extends AppCompatImageView {

    private static final String TAG = "FaceView";
    private Paint mLinePaint;
    private Rect[] mFaces;
    private Matrix mMatrix = new Matrix();
    private RectF mRect = new RectF();
    private Drawable mFaceIndicator = null;
    private int activeWidth;
    private int activeHeight;

    /**
     * 是否前置摄像头预览
     */
    private boolean isFrontCamera = false;
    private boolean rotate;
    private int displayOrientation;

    public FaceView1(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
        mFaceIndicator = getResources().getDrawable(R.mipmap.ic_face_find);
    }

    /**
     * 会导致异常，所以加了synchronized
     * java.lang.ArrayIndexOutOfBoundsException length=0; index=0
     *
     * @param faces
     */
    public synchronized void setFaces(Rect[] faces) {
        this.mFaces = faces;
        postInvalidate();
    }

    public synchronized void clearFaces() {
        mFaces = null;
        invalidate();
    }


    @Override
    protected synchronized void onDraw(Canvas canvas) {
        //会导致异常，所以加了synchronized  (java.lang.ArrayIndexOutOfBoundsException length=0; index=0)
        if (mFaces == null || mFaces.length < 1) {
            super.onDraw(canvas);
            return;
        }
        prepareMatrix(mMatrix, false, displayOrientation, canvas.getWidth(), canvas.getHeight());
        canvas.save();
        for (int i = 0; i < mFaces.length; i++) {
            mRect.set(mFaces[i]);
            mMatrix.mapRect(mRect);
            if (isFrontCamera) {
                mFaceIndicator.setBounds(getWidth() - Math.round(mRect.right), Math.round(mRect
                                .top),
                        getWidth() - Math.round(mRect.left), Math.round(mRect.bottom));
            } else {
                mFaceIndicator.setBounds(Math.round(mRect.left), Math.round(mRect.top),
                        Math.round(mRect.right), Math.round(mRect.bottom));
            }
            mFaceIndicator.draw(canvas);
        }
        canvas.restore();
        super.onDraw(canvas);
    }

    private void initPaint() {
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        int color = Color.rgb(98, 212, 68);
        mLinePaint.setColor(color);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(5f);
        mLinePaint.setAlpha(180);
    }

    void prepareMatrix(Matrix matrix, boolean mirror, int displayOrientation,
                       int viewWidth, int viewHeight) {
        // Need mirror for front camera.
        matrix.setScale(mirror ? -1 : 1, 1);
        // This is the value for android.hardware.Camera.setDisplayOrientation.
        matrix.postRotate(displayOrientation, activeWidth / 2, activeHeight / 2);
        float scaleWdith = viewWidth * 1.0f / activeWidth;
        float scaleHeight = viewHeight * 1.0f / activeHeight;
        matrix.postScale(scaleWdith, scaleHeight);
//        matrix.postTranslate(viewWidth / 2f, viewHeight / 2f);
    }

    public void setFrontCamera(boolean frontCamera) {
        isFrontCamera = frontCamera;
    }

    public int getActiveWidth() {
        return activeWidth;
    }

    public void setActiveWidth(int activeWidth) {
        this.activeWidth = activeWidth;
    }

    public int getActiveHeight() {
        return activeHeight;
    }

    public void setActiveHeight(int activeHeight) {
        this.activeHeight = activeHeight;
    }

    public boolean isRotate() {
        return rotate;
    }

    public void setRotate(boolean rotate) {
        this.rotate = rotate;
        if (rotate) {
            displayOrientation = 180;
        } else {
            displayOrientation = 0;
        }
    }
}