package com.example.selectfile.pdf;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

public class SignView extends RelativeLayout {

    Paint mPaint;
    //控件的宽
    int mWidth;
    //控件的高
    int mHeight;
    //保存当前显示的页码
    int mCurrentPage = 0;
    //背景图的Bitmap
    Bitmap mBgBitmap;
    //背景图片的缩放矩阵
    Matrix mBgMatrix;

    public static final float DEFAULT_MAX_SCALE = 3.0f;
    public static final float DEFAULT_MID_SCALE = 1.75f;
    public static final float DEFAULT_MIN_SCALE = 1.0f;

    private float minZoom = DEFAULT_MIN_SCALE;
    private float midZoom = DEFAULT_MID_SCALE;
    private float maxZoom = DEFAULT_MAX_SCALE;

    /**
     * The zoom level, always >= 1
     */
    private float zoom = 1f;

    /**
     * Animation manager manage all offset and zoom animation
     */
    private AnimationManager animationManager;

    /**
     * Drag manager manage all touch events
     */
    private DragPinchManager dragPinchManager;
    /**
     * If you picture all the pages side by side in their optimal width,
     * and taking into account the zoom level, the current offset is the
     * position of the left border of the screen in this big picture
     */
    private float currentXOffset = 0;

    /**
     * If you picture all the pages side by side in their optimal width,
     * and taking into account the zoom level, the current offset is the
     * position of the left border of the screen in this big picture
     */
    private float currentYOffset = 0;

    public SignView(Context context) {
        this(context, null);
    }

    public SignView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SignView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBgMatrix = new Matrix();
        animationManager = new AnimationManager(this);
        dragPinchManager = new DragPinchManager(this, animationManager);
        setWillNotDraw(false);

    }


    /**
     * 说明： onFinishInflate()
     * onFinishInflate是view加载完xml之后执行的方法，相当于只是完成了布局的映射，在这个方法里面是得不到控件的高宽的，
     * 控件的高宽是必须在调用了onMeasure方法之后才能得到，而onFinishInflate方法是在setContentView之后、onMeasure之前。
     * <p>
     * 控件大小改变后调用，一般调用顺序是：onMeasure() -> onSizeChange() -> onLayout() -> onDraw()
     *
     * @param w    新的宽
     * @param h    新的高
     * @param oldw 旧的宽
     * @param oldh 旧的高
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        initBackground();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mBgBitmap, mBgMatrix, mPaint);
    }


    public void setBgMatrix(Matrix mBgMatrix) {
        this.mBgMatrix = mBgMatrix;
        redraw();
    }

    public void setBgBitmap(Bitmap bgBitmap) {
        this.mBgBitmap = bgBitmap;
    }

    /**
     * 初始化背景
     */
    public void initBackground() {
        Glide.with(this).asBitmap().load(mBgBitmap).into(new CustomTarget<Bitmap>(mWidth, mHeight) {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                //因为外部传进来的Bitmap的宽高和控件的宽高不一致，所以进行缩放。让图片全屏显示
                backgroundFullScreen();
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }

        });
    }

    /**
     * 背景全屏
     */

    private void backgroundFullScreen() {
        if (mBgBitmap == null || mWidth == 0 || mHeight == 0) {
            return;
        }
        float scale = calculateInSampleSize(mBgBitmap, mWidth, mHeight);
        mBgMatrix.postScale(scale, scale);
        //刷新画布
        invalidate();
    }


    /**
     * 计算bitmap缩放比
     *
     * @param bitmap  传入的bitmap
     * @param width   控件的宽
     * @param height  控件的搞
     * @return 缩放值
     */
    public float calculateInSampleSize(Bitmap bitmap, int width, int height) {
        //传入的图片的高
        int outHeight = bitmap.getHeight();
        //传入的图片宽
        int outWidth = bitmap.getWidth();
        float scale;
        float scaleWidth = 1f * width / outWidth;
        float scaleHeight = 1f * height / outHeight;
        //图片是否小于View
        boolean isInside = false;
        if (outHeight < mHeight && outWidth < mWidth) {
            isInside = true;
        }
        if (!isInside) {
            //图片大于View 缩小 缩放比取最大值
            scale = getRoundValue(Math.max(scaleWidth, scaleHeight));
        } else {
            //图片在View内部或者等于View 放大或不变， 放大 缩放比取最小值
            scale = getRoundValue(Math.min(scaleWidth, scaleHeight));
        }
        return scale;
    }

    /**
     * 调整计算后的缩小比，除不尽 -0.01f 使缩放倍数变小
     *
     * @param num
     * @return
     */
    private float getRoundValue(float num) {
        float resetValue = num % 0.01f;
        if (resetValue > 0) {
            num = num - 0.01f;
        }
        return num;
    }


    public float getMinZoom() {
        return minZoom;
    }

    public float getMidZoom() {
        return midZoom;
    }

    public float getMaxZoom() {
        return maxZoom;
    }

    public float getZoom() {
        return zoom;
    }

    public void zoomWithAnimation(float centerX, float centerY, float scale) {
        animationManager.startZoomAnimation(centerX, centerY, zoom, scale);
    }

    public void zoomWithAnimation(float scale) {
        animationManager.startZoomAnimation(getWidth() / 2, getHeight() / 2, zoom, scale);
    }

    public void resetZoomWithAnimation() {
        zoomWithAnimation(minZoom);
    }

    /**
     * Change the zoom level, relatively to a pivot point.
     * It will call moveTo() to make sure the given point stays
     * in the middle of the screen.
     *
     * @param zoom  The zoom level.
     * @param pivot The point on the screen that should stays.
     */
    public void zoomCenteredTo(float zoom, PointF pivot) {
        float dzoom = zoom / this.zoom;
        zoomTo(zoom);
        float baseX = currentXOffset * dzoom;
        float baseY = currentYOffset * dzoom;
        baseX += (pivot.x - pivot.x * dzoom);
        baseY += (pivot.y - pivot.y * dzoom);
        moveTo(baseX, baseY);
    }

    /**
     * Change the zoom level
     */
    public void zoomTo(float zoom) {
        this.zoom = zoom;
    }

    public void moveTo(float offsetX, float offsetY) {
        moveTo(offsetX, offsetY, true);
    }

    /**
     * Move to the given X and Y offsets, but check them ahead of time
     * to be sure not to go outside the the big strip.
     *
     * @param offsetX    The big strip X offset to use as the left border of the screen.
     * @param offsetY    The big strip Y offset to use as the right border of the screen.
     * @param moveHandle whether to move scroll handle or not
     */
    public void moveTo(float offsetX, float offsetY, boolean moveHandle) {
//        if (swipeVertical) {
//            // Check X offset
//            float scaledPageWidth = toCurrentScale(pdfFile.getMaxPageWidth());
//            if (scaledPageWidth < getWidth()) {
//                offsetX = getWidth() / 2 - scaledPageWidth / 2;
//            } else {
//                if (offsetX > 0) {
//                    offsetX = 0;
//                } else if (offsetX + scaledPageWidth < getWidth()) {
//                    offsetX = getWidth() - scaledPageWidth;
//                }
//            }
//
//            // Check Y offset
//            float contentHeight = pdfFile.getDocLen(zoom);
//            if (contentHeight < getHeight()) { // whole document height visible on screen
//                offsetY = (getHeight() - contentHeight) / 2;
//            } else {
//                if (offsetY > 0) { // top visible
//                    offsetY = 0;
//                } else if (offsetY + contentHeight < getHeight()) { // bottom visible
//                    offsetY = -contentHeight + getHeight();
//                }
//            }
//
//            if (offsetY < currentYOffset) {
//                scrollDir = ScrollDir.END;
//            } else if (offsetY > currentYOffset) {
//                scrollDir = ScrollDir.START;
//            } else {
//                scrollDir = ScrollDir.NONE;
//            }
//        } else {
//            // Check Y offset
//            float scaledPageHeight = toCurrentScale(pdfFile.getMaxPageHeight());
//            if (scaledPageHeight < getHeight()) {
//                offsetY = getHeight() / 2 - scaledPageHeight / 2;
//            } else {
//                if (offsetY > 0) {
//                    offsetY = 0;
//                } else if (offsetY + scaledPageHeight < getHeight()) {
//                    offsetY = getHeight() - scaledPageHeight;
//                }
//            }
//
//            // Check X offset
//            float contentWidth = pdfFile.getDocLen(zoom);
//            if (contentWidth < getWidth()) { // whole document width visible on screen
//                offsetX = (getWidth() - contentWidth) / 2;
//            } else {
//                if (offsetX > 0) { // left visible
//                    offsetX = 0;
//                } else if (offsetX + contentWidth < getWidth()) { // right visible
//                    offsetX = -contentWidth + getWidth();
//                }
//            }
//
//            if (offsetX < currentXOffset) {
//                scrollDir = ScrollDir.END;
//            } else if (offsetX > currentXOffset) {
//                scrollDir = ScrollDir.START;
//            } else {
//                scrollDir = ScrollDir.NONE;
//            }
//        }
//
//        currentXOffset = offsetX;
//        currentYOffset = offsetY;
//        float positionOffset = getPositionOffset();
//
//        if (moveHandle && scrollHandle != null && !documentFitsView()) {
//            scrollHandle.setScroll(positionOffset);
//        }
//
//        callbacks.callOnPageScroll(getCurrentPage(), positionOffset);

        redraw();
    }


    void redraw() {
        invalidate();
    }

}
