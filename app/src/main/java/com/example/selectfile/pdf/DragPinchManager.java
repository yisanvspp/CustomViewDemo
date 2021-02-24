package com.example.selectfile.pdf;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.WindowManager;

public class DragPinchManager implements GestureDetector.OnDoubleTapListener, GestureDetector.OnGestureListener, View.OnTouchListener, ScaleGestureDetector.OnScaleGestureListener {
    private static final String TAG = "DragPinchManager";
    private SignView mSignView;
    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleGestureDetector;
    private AnimationManager animationManager;


    private Matrix matrix;
    private float preScale = 1.0f;//之前的伸缩值
    private float curScale;//当前的伸缩值
    private Bitmap mNeedDrawBitmap; //需要的画的bitmap
    private int[] screenSize;//屏幕尺寸信息
    private float translateX;//平移到屏幕中心的X轴距离
    private float translateY;//平移到屏幕中心的Y轴距离
    private boolean isChangeScaleType;//是否转换为Matrix模式


    public DragPinchManager(SignView signView, AnimationManager animationManager) {
        this.mSignView = signView;
        this.animationManager = animationManager;
        gestureDetector = new GestureDetector(signView.getContext(), this);
        scaleGestureDetector = new ScaleGestureDetector(signView.getContext(), this);
        curScale = 1.0f;
        matrix = new Matrix();
        signView.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return scaleGestureDetector.onTouchEvent(event) | gestureDetector.onTouchEvent(event);
    }



    /*-------------------------单击双击相关------------------------------------*/

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        Log.e(TAG, "onSingleTapConfirmed: ");
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        Log.e(TAG, "onDoubleTap: ");
        if (mSignView.getZoom() < mSignView.getMidZoom()) {
            mSignView.zoomWithAnimation(e.getX(), e.getY(), mSignView.getMidZoom());
        } else if (mSignView.getZoom() < mSignView.getMaxZoom()) {
            mSignView.zoomWithAnimation(e.getX(), e.getY(), mSignView.getMaxZoom());
        } else {
            mSignView.resetZoomWithAnimation();
        }
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        Log.e(TAG, "onDoubleTapEvent: ");
        return false;
    }


    /*-------------------------手势相关------------------------------------*/

    @Override
    public boolean onDown(MotionEvent e) {
        Log.e(TAG, "onDown: ");
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        Log.e(TAG, "onScroll: ");
        matrix.preTranslate(-distanceX, -distanceY);
        mSignView.setBgMatrix(matrix);
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.e(TAG, "onFling: ");
        return false;
    }

    /*-------------------------缩放相关------------------------------------*/


    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        Log.e(TAG, "onScale: " + detector.getScaleFactor());
        curScale = detector.getScaleFactor() * preScale;//当前的伸缩值*之前的伸缩值 保持连续性
        //curScale = detector.getScaleFactor() ;//当前的伸缩值*之前的伸缩值 保持连续性
        if (curScale > 3 || curScale < 0.1) {//当放大倍数大于5或者缩小倍数小于0.1倍 就不伸缩图片 返回true取消处理伸缩手势事件
            preScale = curScale;
            return true;
        }
        matrix.setScale(curScale, curScale,detector.getFocusX(),detector.getFocusY());//在屏幕中心伸缩
        // matrix.preTranslate(mNeedDrawBitmap.getWidth() >> 1, mNeedDrawBitmap.getHeight()>>1);//使图片平移到屏幕中心显示
        mSignView.setBgMatrix(matrix);
//        img.setImageMatrix(matrix);//改变矩阵值显示图片
        preScale = curScale;//保存上一次的伸缩值
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        Log.e(TAG, "onScaleBegin: " + detector.getScaleFactor());
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        Log.e(TAG, "onScaleEnd: " + detector.getScaleFactor());
    }


    //获取屏幕尺寸
    private int[] getScreenSize(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int[] screenSize = new int[2];
        screenSize[0] = displayMetrics.widthPixels;
        screenSize[1] = displayMetrics.heightPixels;
        return screenSize;
    }
}
