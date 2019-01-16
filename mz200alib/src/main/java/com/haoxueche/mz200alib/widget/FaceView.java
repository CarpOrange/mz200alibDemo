package com.haoxueche.mz200alib.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.haoxueche.mz200alib.R;
import com.haoxueche.mz200alib.camera.CameraManager;
import com.haoxueche.mz200alib.util.SharePreferUtil;


/**
 * Created by xiezhongming on 17/9/27.
 */

public class FaceView extends AppCompatImageView {

    private static final String TAG = "FaceView";
    private Paint mLinePaint;
    private Camera.Face[] mFaces;
    private Matrix mMatrix = new Matrix();
    private RectF mRect = new RectF();
    private Drawable mFaceIndicator = null;

    /**
     * 是否前置摄像头预览
     */
    private boolean isFrontCamera = false;

    private int cameraId;

    public FaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
        mFaceIndicator = getResources().getDrawable(R.mipmap.ic_face_find);
    }


    public void setFaces(final Camera.Face[] faces) {
        post(new Runnable() {
            @Override
            public void run() {
                FaceView.this.mFaces = faces;
                invalidate();
            }
        });
    }

    public void clearFaces() {
       post(new Runnable() {
           @Override
           public void run() {
               mFaces = null;
               invalidate();
           }
       });
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (mFaces == null || mFaces.length < 1) {
            return;
        }
        prepareMatrix(mMatrix, false, 0, getWidth(), getHeight());
        canvas.save();
        mMatrix.postRotate(0); //Matrix.postRotateĬ����˳ʱ��
        canvas.rotate(-0);   //Canvas.rotate()Ĭ������ʱ��
        for (int i = 0; i < mFaces.length; i++) {
            mRect.set(mFaces[i].rect);
            mMatrix.mapRect(mRect);
            if (cameraId == CameraManager.CAMERA_FACING_FRONT) {

                mFaceIndicator.setBounds(getWidth() - Math.round(mRect.right), Math.round(mRect.top),
                        getWidth() - Math.round(mRect.left), Math.round(mRect.bottom));
            } else {
                if (!SharePreferUtil.isInsideCameraRotate()) {
                    mFaceIndicator.setBounds(Math.round(mRect.left), Math.round(mRect.top),
                            Math.round(mRect.right), Math.round(mRect.bottom));
                } else {
                    mFaceIndicator.setBounds(getWidth() - Math.round(mRect.right), getHeight() - Math.round(mRect.bottom),
                            getWidth() - Math.round(mRect.left), getHeight() - Math.round(mRect.top));
                }
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
        matrix.postRotate(displayOrientation);
        // Camera driver coordinates range from (-1000, -1000) to (1000, 1000).
        // UI coordinates range from (0, 0) to (width, height).
        matrix.postScale(viewWidth / 2000f, viewHeight / 2000f);
        matrix.postTranslate(viewWidth / 2f, viewHeight / 2f);
    }

    public void setFrontCamera(boolean frontCamera) {
        isFrontCamera = frontCamera;
    }

    public void setCameraId(int cameraId) {
        this.cameraId = cameraId;
    }
}
