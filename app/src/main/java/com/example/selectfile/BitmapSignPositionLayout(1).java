//package com.example.selectfile;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Canvas;
//import android.graphics.Matrix;
//import android.graphics.Paint;
//import android.graphics.drawable.Drawable;
//import android.util.AttributeSet;
//import android.view.MotionEvent;
//import android.widget.RelativeLayout;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//
//import com.blankj.utilcode.util.LogUtils;
//import com.bumptech.glide.Glide;
//import com.bumptech.glide.request.target.CustomTarget;
//import com.bumptech.glide.request.transition.Transition;
//import com.shoufacm.www.R;
//
//import java.io.Serializable;
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//import java.util.UUID;
//
///**
// * @auther lvzhao
// * Created on 2020/3/5.
// */
//public class BitmapSignPositionLayout extends RelativeLayout {
//
//    /**
//     * 个人印章
//     */
//    public static final String SEAL_TYPE_PERSONAL = "3";
//    /**
//     * 企业印章
//     */
//    public static final String SEAL_TYPE_COMPANY = "0";
//    /**
//     * 个人时间
//     */
//    public static final String SEAL_TYPE_PERSONAL_TIME = "1";
//    /**
//     * 企业时间
//     */
//    public static final String SEAL_TYPE_COMPANY_TIME = "1";
//    /**
//     * 经办人
//     */
//    public static final String SEAL_TYPE_AGENT = "2";
//
//    /**
//     * 删除图标
//     */
//    public static final String SEAL_TYPE_DELETE = "-1";
//
//
//    private Paint mPaint;
//    private int mWidth;
//    private int mHeight;
//    private BackgroundPosition backgroundPosition;
//
//
//    /**
//     * 是否2指
//     */
//    private boolean isDoublePointer = false;
//    /**
//     * 旧2指距离
//     */
//    private float oldLength;
//    /**
//     * 最新2指距离
//     */
//    private float lastLength;
//    /**
//     * 点击目标Position
//     */
//    private Position clickPosition;
//    /**
//     * 点击目标Position相关联的删除图标的点
//     */
//    private Position clickPositionRelatedDeletePosition;
//
//
//    /**
//     * 点数组
//     */
//    private List<Position> positions;
//
//    /**
//     * 背景图的Bitmap
//     */
//    private Bitmap mBgBitmap;
//
//    /**
//     * 保存当前显示的页码
//     */
//    private int mCurrentPage = 0;
//
//    private float downX;
//    private float downY;
//
//    /**
//     * 背景图的宽
//     */
//    private int bgBitmapWidth;
//    /**
//     * 背景图的高
//     */
//    private int bgBitmapHeight;
//    /**
//     * 删除的图标
//     */
//    private Bitmap deleteBitmap;
//
//    public BitmapSignPositionLayout(Context context) {
//        this(context, null);
//    }
//
//    public BitmapSignPositionLayout(Context context, AttributeSet attrs) {
//        this(context, attrs, 0);
//    }
//
//    public BitmapSignPositionLayout(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        positions = new ArrayList<>();
//        deleteBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_sign_delete)
//                .copy(Bitmap.Config.ARGB_8888, true);
//        setWillNotDraw(false);
//    }
//
//
//    @Override
//    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        super.onSizeChanged(w, h, oldw, oldh);
//        mWidth = w;
//        mHeight = h;
//        initBackground(null, 0);
//    }
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//        if (backgroundPosition.drawBitmap != null) {
//            bgBitmapWidth = backgroundPosition.drawBitmap.getWidth();
//            bgBitmapHeight = backgroundPosition.drawBitmap.getHeight();
//            canvas.drawBitmap(backgroundPosition.drawBitmap, backgroundPosition.getDrawX(),
//                    backgroundPosition.getDrawY(), mPaint);
//            // LogUtils.d("canvas ", bgBitmapWidth, bgBitmapHeight);
//        }
//
//    }
//
//    //禁止拖动
//    boolean isForbidDrag = false;
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        // LogUtils.d("onTouchEvent", event.getAction());
//        switch (event.getActionMasked()) {
//            case MotionEvent.ACTION_POINTER_DOWN:
//                isDoublePointer = true;
//                if (isDoublePointer && event.getPointerCount() >= 2) {
//                    oldLength = calculation(event);
//                }
//                break;
//            case MotionEvent.ACTION_DOWN:
//                isDoublePointer = false;
//                downX = event.getX();
//                downY = event.getY();
//                //背景画布上的x y坐标
//                float backgroundDownX = downX - backgroundPosition.getDrawX();
//                float backgroundDownY = downY - backgroundPosition.getDrawY();
//
//                boolean isClickPositions = false;
//                for (int i = 0; i < positions.size(); i++) {
//                    Position position = positions.get(i);
//                    if (position.isClickDown(backgroundDownX, backgroundDownY)) {
//                        //当前点击的印章图标点
//                        clickPosition = position;
//                        //删除的图标Position
//                        Position delPosition = getDeletePosition(position.uuid);
//                        if (delPosition != null) {
//                            clickPositionRelatedDeletePosition = delPosition;
//                        }
//
//                        isClickPositions = true;
//                        if (mListener != null) {
//                            isForbidDrag = true;
//                            mListener.onPositionClick(position);
//                            break;
//                        }
//                        if (mOnDeletePositionClickListener != null && position.type.equals(SEAL_TYPE_DELETE)) {
//                            isForbidDrag = true;
//                            mOnDeletePositionClickListener.onDeletePositionClick(position);
//                            break;
//                        }
//                    }
//                }
//                if (!isClickPositions) {
//                    clickPosition = backgroundPosition;
//                }
//
//
//                break;
//            case MotionEvent.ACTION_MOVE:
//                // LogUtils.d("onTouchEvent - Move", clickPosition, isDoublePointer, event.getPointerCount());
//                if (clickPosition != null && !isForbidDrag) {
//                    if (isDoublePointer && event.getPointerCount() >= 2) {//2指缩放
//                        lastLength = calculation(event);
//                        float scale = lastLength / oldLength;
//                        oldLength = lastLength;
////                        LogUtils.d(scale);
//
//                        if (clickPosition instanceof BackgroundPosition) {
//                            clickPosition.setScale(scale, downX, downY);
//                        } else {
//                            clickPosition.setScale(scale);
//                        }
//
//                        drawBackgroundCanvas();
//                        invalidate();
//
//                    } else if (!isDoublePointer) {//单指移动
//                        float moveX = event.getX();
//                        float moveY = event.getY();
//                        clickPosition.setOffsetPosition(moveX - downX, moveY - downY);
//                        if (clickPositionRelatedDeletePosition != null) {
//                            clickPositionRelatedDeletePosition.setOffsetDeletePosition(moveX - downX, moveY - downY,
//                                    clickPosition.drawBitmap);
//                        }
//                        downX = moveX;
//                        downY = moveY;
//                        //背景拖动不重绘
//                        if (!(clickPosition instanceof BackgroundPosition)) {
//                            drawBackgroundCanvas();
//                        }
//                    }
//                    invalidate();
//                }
//                break;
//
//            case MotionEvent.ACTION_UP:
//                //LogUtils.d("action up");
//                if (isDoublePointer) {
//                    isDoublePointer = false;
//                }
//                clickPosition = null;
//                isForbidDrag = false;
//                break;
//
//            case MotionEvent.ACTION_POINTER_UP:
////                LogUtils.d("action pointer up");
//                isForbidDrag = false;
//                break;
//
//        }
//        return true;
//    }
//
//
//    /**
//     *
//     * 初始化背景
//     */
//    public void initBackground(OnResourceLoadComplete listener, int currentPage) {
//        //保存当前显示的界面截图
//        this.mCurrentPage = currentPage;
//        backgroundPosition = new BackgroundPosition();
////        LogUtils.d(mWidth, mHeight);
//        Glide.with(this).asBitmap().load(mBgBitmap).into(new CustomTarget<Bitmap>(mWidth, mHeight) {
//            @Override
//            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                backgroundPosition.initBitmap(resource);
//                backgroundPosition.setPosition(mWidth / 2, mHeight / 2);
//                //初始化标记点
//                //initPosition();
//                if (listener != null) {
//                    listener.onResourceLoadComplete();
//                }
//                backgroundFullScreen();
//                drawBackgroundCanvas();
//                invalidate();
//            }
//
//            @Override
//            public void onLoadCleared(@Nullable Drawable placeholder) {
//
//            }
//
//        });
//    }
//
//    /**
//     * 初始化背景
//     */
//    public void initBackground(OnResourceLoadComplete listener) {
//        backgroundPosition = new BackgroundPosition();
////        LogUtils.d(mWidth, mHeight);
//        Glide.with(this).asBitmap().load(mBgBitmap).into(new CustomTarget<Bitmap>(mWidth, mHeight) {
//            @Override
//            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                backgroundPosition.initBitmap(resource);
//                backgroundPosition.setPosition(mWidth / 2, mHeight / 2);
//                //初始化标记点
//                //initPosition();
//                if (listener != null) {
//                    listener.onResourceLoadComplete();
//                }
//                backgroundFullScreen();
//                drawBackgroundCanvas();
//                invalidate();
//            }
//
//            @Override
//            public void onLoadCleared(@Nullable Drawable placeholder) {
//
//            }
//
//        });
//    }
//
//
//    /**
//     * 添加显示的图片
//     *
//     * @param bitmap      Bitmap
//     * @param sealType    图片章的类型  0 印章 1 时间印章
//     * @param currentPage 当前页
//     */
//
//    public void addPosition(Bitmap bitmap, String sealType, int currentPage) {
//        //印章和删除图标关系的id
//        String uuid = UUID.randomUUID().toString();
//        //设置当前bitmap的显示的坐标位置
//        int defaultPositionX = 0;
//        int defaultPositionY = 0;
//        if (backgroundPosition != null) {
//            defaultPositionX = (backgroundPosition.drawBitmap.getWidth() / 6) * 5;
//            defaultPositionY = (backgroundPosition.drawBitmap.getHeight() / 6) * 4;
//        }
//        Position position = new Position();
//        position.uuid = uuid;
//        position.type = sealType;
//        position.currentPage = currentPage;
//        position.initBitmap(bitmap);
//        position.setPosition(defaultPositionX, defaultPositionY);
//        positions.add(position);
//
//        //添加删除图标
//        Position delPosition = new Position();
//        delPosition.uuid = uuid;
//        delPosition.type = SEAL_TYPE_DELETE;
//        delPosition.currentPage = currentPage;
//        delPosition.initBitmap(deleteBitmap);
//        delPosition.setPosition(defaultPositionX + position.getHalfDrawWidth() + 15,
//                defaultPositionY - position.getHalfDrawHeight());
//        positions.add(delPosition);
//
//        drawPosition();
//        invalidate();
//    }
//
//    /**
//     * 添加显示的图片
//     *
//     * @param bitmap      Bitmap
//     * @param sealType    图片章的类型  0 印章 1 时间印章
//     * @param currentPage 当前页
//     */
//
//    public void addPosition(Bitmap bitmap, String sealType, int currentPage, float x, float y) {
//        //印章和删除图标关系的id
//        String uuid = UUID.randomUUID().toString();
//        Position position = new Position();
//        position.uuid = uuid;
//        position.type = sealType;
//        position.currentPage = currentPage;
//        position.initBitmap(bitmap);
//        if (backgroundPosition != null) {
//            position.setPosition(x, y);
//        }
//        positions.add(position);
//        drawPosition();
//        invalidate();
//    }
//
//    /**
//     * 删除印章
//     *
//     * @param posUuid 根据印章的uuid
//     */
//    public void deletePosition(String posUuid) {
//        if (positions != null && positions.size() > 0) {
//            Iterator<Position> iterator = positions.iterator();
//            while (iterator.hasNext()) {
//                Position next = iterator.next();
//                if (next.uuid.equals(posUuid)) {
//                    iterator.remove();
//                }
//            }
//        }
//        drawPosition();
//        invalidate();
//    }
//
//    /**
//     * 撤销编辑
//     */
//    public void clearPosition() {
//        if (positions != null) {
//            positions.clear();
//        }
//        invalidate();
//    }
//
//    /**
//     * 获取删除图标的点Position
//     *
//     * @param uuid 点的Position的唯一标识
//     * @return Position
//     */
//    public Position getDeletePosition(String uuid) {
//        if (positions != null && positions.size() > 0) {
//            for (Position p : positions) {
//                if (p.uuid.equals(uuid) && p.type.equals(SEAL_TYPE_DELETE)) {
//                    return p;
//                }
//            }
//        }
//        return null;
//    }
//
//    public int getBgBitmapWidth() {
//        return bgBitmapWidth;
//    }
//
//    public int getBgBitmapHeight() {
//        return bgBitmapHeight;
//    }
//
//
//    /**
//     * 画图标
//     */
//    private void drawPosition() {
//        backgroundPosition.newCanvas();
//        for (int i = 0; i < positions.size(); i++) {
//            Position position = positions.get(i);
//            if (mCurrentPage == position.currentPage) {
//                backgroundPosition.getCanvas().drawBitmap(position.drawBitmap, position.getDrawX(), position.getDrawY(), mPaint);
//            }
//        }
//    }
//
//
//    /**
//     * 背景全屏
//     */
//    private void backgroundFullScreen() {
//        if (backgroundPosition.sourceBitmap == null || mWidth == 0 || mHeight == 0) {
//            return;
//        }
//        float scale = calculateInSampleSize(backgroundPosition.sourceBitmap, mWidth, mHeight);
//        backgroundPosition.setScale(scale);
//        backgroundPosition.firstScale = scale;
//    }
//
//    /**
//     * 计算bitmap缩放比
//     *
//     * @param bitmap
//     * @param width
//     * @param height
//     * @return
//     */
//    public float calculateInSampleSize(Bitmap bitmap, int width, int height) {
//        int outHeight = bitmap.getHeight();
//        int outWidth = bitmap.getWidth();
//        float scale;
//        float scaleWidth = 1f * width / outWidth;
//        float scaleHeight = 1f * height / outHeight;
//        //图片是否小于View
//        boolean isInside = false;
//        if (outHeight <= mHeight && outWidth <= mWidth) {
//            isInside = true;
//        }
//        //图片大于View 缩小 缩放比取最大值
//        if (!isInside) {
//            scale = getRoundValue(Math.max(scaleWidth, scaleHeight));
//        } else { //图片在View内部或者等于View 放大或不变， 放大 缩放比取最小值
//            scale = getRoundValue(Math.min(scaleWidth, scaleHeight));
//        }
//        return scale;
//    }
//
//    /**
//     * 调整计算后的缩小比，除不尽 -0.01f 使缩放倍数变小
//     *
//     * @param num
//     * @return
//     */
//    private float getRoundValue(float num) {
//        float resetValue = num % 0.01f;
//        if (resetValue > 0) {
//            num = num - 0.01f;
//        }
//        return num;
//    }
//
//    /**
//     * 计算2指间距离
//     *
//     * @param event
//     * @return
//     */
//    private float calculation(MotionEvent event) {
//        if (event.getPointerCount() < 2) {
//            return oldLength;
//        }
//        float x1 = event.getX();
//        float x2 = event.getX(1);
//        float y1 = event.getY();
//        float y2 = event.getY(1);
//        return (float) Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2));
//    }
//
//    /**
//     * 绘制背景画布
//     */
//    private void drawBackgroundCanvas() {
//        backgroundPosition.newCanvas();
//        for (int i = 0; i < positions.size(); i++) {
//            Position position = positions.get(i);
//            if (mCurrentPage == position.currentPage) {
//
//                float drawX = 0f, drawY = 0f;
//                Matrix mMatrix = position.mMatrix;
//                float[] f = new float[9];
//                mMatrix.getValues(f);
//                float scaleX = f[Matrix.MSCALE_X];
//                float scaleY = f[Matrix.MSCALE_Y];
//                if (scaleX != 1f) {
//                    drawX = position.getDrawX() / scaleX;
//                } else {
//                    drawX = position.getDrawX();
//                }
//                if (scaleY != 1f) {
//                    drawY = position.getDrawY() / scaleY;
//                } else {
//                    drawY = position.getDrawY();
//                }
//                backgroundPosition.getCanvas().drawBitmap(position.drawBitmap, drawX, drawY, mPaint);
//            }
//        }
//    }
//
//    public Bitmap getBgBitmap() {
//        return mBgBitmap;
//    }
//
//    public void setBgBitmap(Bitmap mBgBitmap) {
//        this.mBgBitmap = mBgBitmap;
//    }
//
//
//    /**
//     * 背景图片实体
//     */
//    class BackgroundPosition extends Position {
//
//        public Canvas mCanvas;
//        private float totalScale;
//        public float firstScale = 1f;
//
//        @Override
//        public void setScale(float scale) {
//            float[] floats = new float[9];
//            mMatrix.getValues(floats);
//            totalScale = floats[Matrix.MSCALE_X];
//            float newPx = sourceBitmap.getWidth() * totalScale * scale * sourceBitmap.getHeight() * totalScale * scale;
//            if (newPx <= getScaleMaxSize() && newPx >= getScaleMinSize()) {
//                mMatrix.postScale(scale, scale);
//
//                for (int i = 0; i < positions.size(); i++) {
//                    if (i == (positions.size() - 1)) {
//                        //最后一个进行缩放，因为其他。添加的时候已经缩放过 了
//                        Position position = positions.get(i);
//                        position.setScale(scale);
//                        position.setPosition(position.pX * scale, position.pY * scale);
//                    }
//                }
//            }
//
//            if (totalScale == firstScale) {
//                mMatrix.getValues(floats);
//                totalScale = floats[Matrix.MSCALE_X];
//            }
//
//            //缩放比小于等于初始化值，位置居中
//            if (totalScale <= firstScale) {
//                this.pX = mWidth / 2;
//                this.pY = mHeight / 2;
//            }
//        }
//
//
//        /**
//         * 设置缩放 以及 缩放目标点
//         *
//         * @param scale
//         * @param x
//         * @param y
//         */
//        @Override
//        public void setScale(float scale, float x, float y) {
//            float[] floats = new float[9];
//            mMatrix.getValues(floats);
//            totalScale = floats[Matrix.MSCALE_X];
//
//            float newPx = sourceBitmap.getWidth() * totalScale * scale * sourceBitmap.getHeight() * totalScale * scale;
//            if (newPx <= getScaleMaxSize() && newPx >= getScaleMinSize()) {
//                mMatrix.postScale(scale, scale);
//
//                //目标点与中心点的偏移
//                float offsetX =
////                        x
////                        (x - getDrawX())
////                        (getDrawX() - x)
//                        (x - pX)
//                                * (1 - scale);
//                float offsetY =
////                        y
////                        (y - getDrawY())
////                        (getDrawY() - y)
//                        (y - pY)
//                                * (1 - scale);
////                LogUtils.d("setScale ", scale, x, y, offsetX, offsetY);
//                setOffsetPosition(offsetX, offsetY);
//
//                for (int i = 0; i < positions.size(); i++) {
//                    Position position = positions.get(i);
//                    position.setScale(scale);
//                    position.setPosition(position.pX * scale, position.pY * scale);
//                }
//            }
//            if (totalScale == firstScale) {
//                mMatrix.getValues(floats);
//                totalScale = floats[Matrix.MSCALE_X];
//            }
//            //缩放比小于等于初始化值，位置居中
//            if (totalScale <= firstScale) {
//                this.pX = mWidth / 2;
//                this.pY = mHeight / 2;
//            }
//        }
//
//
//        @Override
//        public void setOffsetPosition(float offsetX, float offsetY) {
//            //LogUtils.d("setDragPosition", pX, pY, offsetX, offsetY, totalScale, firstScale);
//            if (totalScale <= firstScale) {
//                this.pX = mWidth / 2;
//                this.pY = mHeight / 2;
//                return;
//            }
//            //位移pX pY坐标
//            this.pX += offsetX;
//            this.pY += offsetY;
//
//            //获取边界值
//            float boundaryValue = getBoundaryValue();
//
//            float drawBitmapWidth = drawBitmap.getWidth();
//            float halfViewWidth = mWidth / 2;
//            float halfOffsetWidth = (drawBitmapWidth - mWidth) / 2;
//            float drawBitmapHeight = drawBitmap.getHeight();
//            float halfViewHeight = mHeight / 2;
//            float halfOffsetHeight = (drawBitmapHeight - mHeight) / 2;
//            //边界值
//            if (drawBitmapWidth > mWidth) {
//                if (this.pX < halfViewWidth - halfOffsetWidth - boundaryValue) {
//                    this.pX = halfViewWidth - halfOffsetWidth - boundaryValue;
//                }
//                if (this.pX > halfViewWidth + halfOffsetWidth + boundaryValue) {
//                    this.pX = halfViewWidth + halfOffsetWidth + boundaryValue;
//                }
//            }
//            if (drawBitmapHeight > mHeight) {
//                if (this.pY < halfViewHeight - halfOffsetHeight - boundaryValue) {
//                    this.pY = halfViewHeight - halfOffsetHeight - boundaryValue;
//                }
//                if (this.pY > halfViewHeight + halfOffsetHeight + boundaryValue) {
//                    this.pY = halfViewHeight + halfOffsetHeight + boundaryValue;
//                }
//            }
//
//        }
//
//        @Override
//        public float getScaleMaxSize() {
//            return 2560 * 1440;
//        }
//
//        @Override
//        public float getScaleMinSize() {
//            return 1280 * 720;
//        }
//
//        public float getBoundaryValue() {
//            return 50;
//        }
//
//
//        /**
//         * 生成新画布
//         */
//        public void newCanvas() {
//            mCanvas = new Canvas(newDrawBitmap());
//        }
//
//        /**
//         * 获取画布
//         *
//         * @return
//         */
//        public Canvas getCanvas() {
//            if (mCanvas == null) {
//                mCanvas = new Canvas(drawBitmap);
//            }
//            return mCanvas;
//        }
//
//        public float getTotalScale() {
//            return totalScale;
//        }
//    }
//
//    /**
//     * 小标记点实体
//     */
//    public class Position implements Serializable {
//        /**
//         * 与删除图标关联
//         */
//        public String uuid;
//        public float pX;
//        public float pY;
//        /**
//         * 源Bitmap
//         */
//        public Bitmap sourceBitmap;
//        /**
//         * onDraw 调用的Bitmap
//         */
//        public Bitmap drawBitmap;
//        /**
//         * 缩放Martix
//         */
//        public Matrix mMatrix;
//        /**
//         * SEAL_TYPE_PERSONAL = "3"; 个人印章
//         * SEAL_TYPE_COMPANY = "0";企业印章
//         * SEAL_TYPE_PERSONAL_TIME = "1";个人时间
//         * SEAL_TYPE_COMPANY_TIME = "1";企业时间
//         * SEAL_TYPE_AGENT = "2";经办人
//         */
//        public String type;
//        /**
//         * 当前页
//         */
//        public int currentPage;
//        /**
//         * drawBitmap 尺寸
//         */
//        float halfDrawWidth;
//        float halfDrawHeight;
//        float drawWidth;
//        float drawHeight;
//
//        public Position() {
//            this.mMatrix = new Matrix();
//        }
//
//        /**
//         * 初始化Bitmap
//         *
//         * @param bitmap
//         */
//        public void initBitmap(Bitmap bitmap) {
//            sourceBitmap = bitmap;
//            drawBitmap = bitmap;
//            drawWidth = drawBitmap.getWidth();
//            drawHeight = drawBitmap.getHeight();
//            halfDrawWidth = drawWidth / 2;
//            halfDrawHeight = drawHeight / 2;
//        }
//
//        /**
//         * 设置中心点位置
//         *
//         * @param x
//         * @param y
//         */
//        public void setPosition(float x, float y) {
//            // LogUtils.d("setPosition", x, y);
//            this.pX = x;
//            this.pY = y;
//        }
//
//        /**
//         * 设置缩放
//         *
//         * @param scale
//         */
//        public void setScale(float scale) {
//            float[] floats = new float[9];
//            mMatrix.getValues(floats);
//            float totalScale = floats[Matrix.MSCALE_X];
//
//            float newPx = sourceBitmap.getWidth() * totalScale * scale * sourceBitmap.getHeight() * totalScale * scale;
//            if (newPx <= getScaleMaxSize() && newPx >= getScaleMinSize()) {
//                mMatrix.postScale(scale, scale);
//                newDrawBitmap();
//            }
//        }
//
//        /**
//         * 设置缩放 以及 缩放目标点
//         *
//         * @param scale
//         * @param x
//         * @param y
//         */
//        public void setScale(float scale, float x, float y) {
//            float[] floats = new float[9];
//            mMatrix.getValues(floats);
//            float totalScale = floats[Matrix.MSCALE_X];
//
//            float newPx = sourceBitmap.getWidth() * totalScale * scale * sourceBitmap.getHeight() * totalScale * scale;
//            if (newPx <= getScaleMaxSize() && newPx >= getScaleMinSize()) {
//                mMatrix.postScale(scale, scale, x, y);
//
//                float offsetX = x - scale * x;
//                float offsetY = y - scale * y;
//                LogUtils.d("setScale ", scale, x, y, offsetX, offsetY);
//                setOffsetPosition(offsetX, offsetY);
//
//                newDrawBitmap();
//            }
//        }
//
//
//        /**
//         * 生成新绘制Bitmap
//         *
//         * @return
//         */
//        public Bitmap newDrawBitmap() {
//            //缩放bitmap
//            drawBitmap = Bitmap.createBitmap(sourceBitmap, 0, 0,
//                    sourceBitmap.getWidth(), sourceBitmap.getHeight(), mMatrix, false);
//
//            drawWidth = drawBitmap.getWidth();
//            drawHeight = drawBitmap.getHeight();
//            halfDrawWidth = drawWidth / 2;
//            halfDrawHeight = drawHeight / 2;
//            return drawBitmap;
//        }
//
//        /**
//         * 设置中心点位置 (拖动)
//         *
//         * @param x
//         * @param y
//         */
//        public void setDragPosition(float x, float y) {
//            LogUtils.d("setDragPosition", pX, pY, x, y);
//            if (Math.abs(pX - x) < 300 && Math.abs(pY - y) < 300) {
//                this.pX = x;
//                this.pY = y;
//            }
//        }
//
//        /**
//         * 设置中心点便宜位置 (拖动)
//         *
//         * @param offsetX
//         * @param offsetY
//         */
//
//        public void setOffsetPosition(float offsetX, float offsetY) {
//            // LogUtils.d("setDragPosition", pX, pY, offsetX, offsetY);
//            this.pX += offsetX;
//            this.pY += offsetY;
//            float offsetMinX = drawBitmap.getWidth() / 2;
//            float offsetMaxX = backgroundPosition.drawBitmap.getWidth() - drawBitmap.getWidth() / 2;
//            float offsetMinY = drawBitmap.getHeight() / 2;
//            float offsetMaxY = backgroundPosition.drawBitmap.getHeight() - drawBitmap.getHeight() / 2;
//            if (this.pX < offsetMinX) {
//                this.pX = offsetMinX;
//            }
//            if (this.pX > offsetMaxX) {
//                this.pX = offsetMaxX;
//            }
//            if (this.pY < offsetMinY) {
//                this.pY = offsetMinY;
//            }
//            if (this.pY > offsetMaxY) {
//                this.pY = offsetMaxY;
//            }
//        }
//
//        /**
//         * 设置删除图标的偏移量
//         *
//         * @param offsetX
//         * @param offsetY
//         */
//        public void setOffsetDeletePosition(float offsetX, float offsetY, Bitmap bitmap) {
//            // LogUtils.d("setDragPosition", pX, pY, offsetX, offsetY);
//            Bitmap sealBitmap = null; //删除图标 左侧软章的bitmap
//            this.pX += offsetX;
//            this.pY += offsetY;
//            if (bitmap != null) {
//                sealBitmap = bitmap;
//            }
//            //删除按钮的bitmap偏移量限制
//            float offsetMinX = sealBitmap.getWidth();
//            float offsetMaxX = backgroundPosition.drawBitmap.getWidth() - 15;
//            float offsetMinY = 15;
//            float offsetMaxY = backgroundPosition.drawBitmap.getHeight() - sealBitmap.getHeight();
//
//            if (this.pX < offsetMinX) {
//                this.pX = offsetMinX;
//            }
//            if (this.pX > offsetMaxX) {
//                this.pX = offsetMaxX;
//            }
//            if (this.pY < offsetMinY) {
//                this.pY = offsetMinY;
//            }
//            if (this.pY > offsetMaxY) {
//                this.pY = offsetMaxY;
//            }
//        }
//
//
//        public float getpX() {
//            return pX;
//        }
//
//        public float getpY() {
//            return pY;
//        }
//
//        public float getHalfDrawWidth() {
//            return halfDrawWidth;
//        }
//
//        public float getHalfDrawHeight() {
//            return halfDrawHeight;
//        }
//
//        public float getDrawWidth() {
//            return drawWidth;
//        }
//
//        public float getDrawHeight() {
//            return drawHeight;
//        }
//
//        /**
//         * 获取draw时的X点
//         *
//         * @returnf
//         */
//        public float getDrawX() {
//            // LogUtils.d("drag", pX - halfDrawWidth);
//            return pX - halfDrawWidth;
//        }
//
//        /**
//         * 获取draw时的Y点
//         *
//         * @return
//         */
//        public float getDrawY() {
//            // LogUtils.d("drag", pY - halfDrawHeight);
//            return pY - halfDrawHeight;
//        }
//
//        public String getType() {
//            return type;
//        }
//
//        /**
//         * 获取缩放最小尺寸
//         *
//         * @return
//         */
//        public float getScaleMaxSize() {
//            return 500 * 500;
//        }
//
//        /**
//         * 获取缩放最大尺寸
//         *
//         * @return
//         */
//        public float getScaleMinSize() {
//            return 0;
//        }
//
//        /**
//         * 点击确认
//         *
//         * @param x
//         * @param y
//         * @return
//         */
//        public boolean isClickDown(float x, float y) {
//            float minX = pX - halfDrawWidth;
//            float maxX = pX + halfDrawWidth;
//            float minY = pY - halfDrawHeight;
//            float maxY = pY + halfDrawHeight;
//            return x > minX && x < maxX && y > minY && y < maxY;
//        }
//
//
//        public int getCurrentPage() {
//            return currentPage;
//        }
//    }
//
//
//    public Bitmap save() {
//        return this.backgroundPosition.drawBitmap;
//    }
//
//
//    public List<Position> getPositions() {
//        List<Position> list = new ArrayList<>();
//        if (positions.size() > 0) {
//            for (Position p : positions) {
//                if (!p.type.equals(SEAL_TYPE_DELETE)) {
//                    list.add(p);
//                }
//            }
//        }
//        return list;
//    }
//
//    public BackgroundPosition getBackgroundPosition() {
//        return backgroundPosition;
//    }
//
//    public float getTotalScale() {
//        return backgroundPosition.getTotalScale();
//    }
//
//    //------------------------接口
//
//
//    private OnPositionClickListener mListener;
//    private OnDeletePositionClickListener mOnDeletePositionClickListener;
//
//    public void setOnPositionClickListener(OnPositionClickListener listener) {
//        this.mListener = listener;
//    }
//
//    public void setOnDeletePositionClickListener(OnDeletePositionClickListener listener) {
//        this.mOnDeletePositionClickListener = listener;
//    }
//
//
//    /**
//     * 印章的点击事件
//     */
//    public interface OnPositionClickListener {
//        void onPositionClick(Position position);
//    }
//
//
//    /**
//     * 删除图标的点击回调
//     */
//    public interface OnDeletePositionClickListener {
//        void onDeletePositionClick(Position position);
//    }
//
//
//    /**
//     * 加载
//     */
//    public interface OnResourceLoadComplete {
//        void onResourceLoadComplete();
//    }
//
//}
