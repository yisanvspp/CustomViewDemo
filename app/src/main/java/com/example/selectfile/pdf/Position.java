//package com.example.selectfile.pdf;
//
//import android.graphics.Bitmap;
//import android.graphics.Matrix;
//
//import java.io.Serializable;
//
///**
// * 点
// */
//public class Position implements Serializable {
//    /**
//     * 与删除图标关联
//     */
//    public String uuid;
//    public float pX;
//    public float pY;
//    /**
//     * 源Bitmap
//     */
//    public Bitmap sourceBitmap;
//    /**
//     * onDraw 调用的Bitmap
//     */
//    public Bitmap drawBitmap;
//    /**
//     * 缩放Martix
//     */
//    public Matrix mMatrix;
//    /**
//     * SEAL_TYPE_PERSONAL = "3"; 个人印章
//     * SEAL_TYPE_COMPANY = "0";企业印章
//     * SEAL_TYPE_PERSONAL_TIME = "1";个人时间
//     * SEAL_TYPE_COMPANY_TIME = "1";企业时间
//     * SEAL_TYPE_AGENT = "2";经办人
//     */
//    public String type;
//    /**
//     * 当前页
//     */
//    public int currentPage;
//    /**
//     * drawBitmap 尺寸
//     */
//    float halfDrawWidth;
//    float halfDrawHeight;
//    float drawWidth;
//    float drawHeight;
//
//    public Position() {
//        this.mMatrix = new Matrix();
//    }
//
//    /**
//     * 初始化Bitmap
//     *
//     * @param bitmap
//     */
//    public void initBitmap(Bitmap bitmap) {
//        sourceBitmap = bitmap;
//        drawBitmap = bitmap;
//        drawWidth = drawBitmap.getWidth();
//        drawHeight = drawBitmap.getHeight();
//        halfDrawWidth = drawWidth / 2;
//        halfDrawHeight = drawHeight / 2;
//    }
//
//    /**
//     * 设置中心点位置
//     *
//     * @param x
//     * @param y
//     */
//    public void setPosition(float x, float y) {
//        // LogUtils.d("setPosition", x, y);
//        this.pX = x;
//        this.pY = y;
//    }
//
//    /**
//     * 设置缩放
//     *
//     * @param scale
//     */
//    public void setScale(float scale) {
//        float[] floats = new float[9];
//        mMatrix.getValues(floats);
//        float totalScale = floats[Matrix.MSCALE_X];
//
//        float newPx = sourceBitmap.getWidth() * totalScale * scale * sourceBitmap.getHeight() * totalScale * scale;
//        if (newPx <= getScaleMaxSize() && newPx >= getScaleMinSize()) {
//            mMatrix.postScale(scale, scale);
//            newDrawBitmap();
//        }
//    }
//
//    /**
//     * 设置缩放 以及 缩放目标点
//     *
//     * @param scale
//     * @param x
//     * @param y
//     */
//    public void setScale(float scale, float x, float y) {
//        float[] floats = new float[9];
//        mMatrix.getValues(floats);
//        float totalScale = floats[Matrix.MSCALE_X];
//
//        float newPx = sourceBitmap.getWidth() * totalScale * scale * sourceBitmap.getHeight() * totalScale * scale;
//        if (newPx <= getScaleMaxSize() && newPx >= getScaleMinSize()) {
//            mMatrix.postScale(scale, scale, x, y);
//
//            float offsetX = x - scale * x;
//            float offsetY = y - scale * y;
//            LogUtils.d("setScale ", scale, x, y, offsetX, offsetY);
//            setOffsetPosition(offsetX, offsetY);
//
//            newDrawBitmap();
//        }
//    }
//
//
//    /**
//     * 生成新绘制Bitmap
//     *
//     * @return
//     */
//    public Bitmap newDrawBitmap() {
//        //缩放bitmap
//        drawBitmap = Bitmap.createBitmap(sourceBitmap, 0, 0,
//                sourceBitmap.getWidth(), sourceBitmap.getHeight(), mMatrix, false);
//
//        drawWidth = drawBitmap.getWidth();
//        drawHeight = drawBitmap.getHeight();
//        halfDrawWidth = drawWidth / 2;
//        halfDrawHeight = drawHeight / 2;
//        return drawBitmap;
//    }
//
//    /**
//     * 设置中心点位置 (拖动)
//     *
//     * @param x
//     * @param y
//     */
//    public void setDragPosition(float x, float y) {
//        LogUtils.d("setDragPosition", pX, pY, x, y);
//        if (Math.abs(pX - x) < 300 && Math.abs(pY - y) < 300) {
//            this.pX = x;
//            this.pY = y;
//        }
//    }
//
//    /**
//     * 设置中心点便宜位置 (拖动)
//     *
//     * @param offsetX
//     * @param offsetY
//     */
//
//    public void setOffsetPosition(float offsetX, float offsetY) {
//        // LogUtils.d("setDragPosition", pX, pY, offsetX, offsetY);
//        this.pX += offsetX;
//        this.pY += offsetY;
//        float offsetMinX = drawBitmap.getWidth() / 2;
//        float offsetMaxX = backgroundPosition.drawBitmap.getWidth() - drawBitmap.getWidth() / 2;
//        float offsetMinY = drawBitmap.getHeight() / 2;
//        float offsetMaxY = backgroundPosition.drawBitmap.getHeight() - drawBitmap.getHeight() / 2;
//        if (this.pX < offsetMinX) {
//            this.pX = offsetMinX;
//        }
//        if (this.pX > offsetMaxX) {
//            this.pX = offsetMaxX;
//        }
//        if (this.pY < offsetMinY) {
//            this.pY = offsetMinY;
//        }
//        if (this.pY > offsetMaxY) {
//            this.pY = offsetMaxY;
//        }
//    }
//
//    /**
//     * 设置删除图标的偏移量
//     *
//     * @param offsetX
//     * @param offsetY
//     */
//    public void setOffsetDeletePosition(float offsetX, float offsetY, Bitmap bitmap) {
//        // LogUtils.d("setDragPosition", pX, pY, offsetX, offsetY);
//        Bitmap sealBitmap = null; //删除图标 左侧软章的bitmap
//        this.pX += offsetX;
//        this.pY += offsetY;
//        if (bitmap != null) {
//            sealBitmap = bitmap;
//        }
//        //删除按钮的bitmap偏移量限制
//        float offsetMinX = sealBitmap.getWidth();
//        float offsetMaxX = backgroundPosition.drawBitmap.getWidth() - 15;
//        float offsetMinY = 15;
//        float offsetMaxY = backgroundPosition.drawBitmap.getHeight() - sealBitmap.getHeight();
//
//        if (this.pX < offsetMinX) {
//            this.pX = offsetMinX;
//        }
//        if (this.pX > offsetMaxX) {
//            this.pX = offsetMaxX;
//        }
//        if (this.pY < offsetMinY) {
//            this.pY = offsetMinY;
//        }
//        if (this.pY > offsetMaxY) {
//            this.pY = offsetMaxY;
//        }
//    }
//
//
//    public float getpX() {
//        return pX;
//    }
//
//    public float getpY() {
//        return pY;
//    }
//
//    public float getHalfDrawWidth() {
//        return halfDrawWidth;
//    }
//
//    public float getHalfDrawHeight() {
//        return halfDrawHeight;
//    }
//
//    public float getDrawWidth() {
//        return drawWidth;
//    }
//
//    public float getDrawHeight() {
//        return drawHeight;
//    }
//
//    /**
//     * 获取draw时的X点
//     *
//     * @returnf
//     */
//    public float getDrawX() {
//        // LogUtils.d("drag", pX - halfDrawWidth);
//        return pX - halfDrawWidth;
//    }
//
//    /**
//     * 获取draw时的Y点
//     *
//     * @return
//     */
//    public float getDrawY() {
//        // LogUtils.d("drag", pY - halfDrawHeight);
//        return pY - halfDrawHeight;
//    }
//
//    public String getType() {
//        return type;
//    }
//
//    /**
//     * 获取缩放最小尺寸
//     *
//     * @return
//     */
//    public float getScaleMaxSize() {
//        return 500 * 500;
//    }
//
//    /**
//     * 获取缩放最大尺寸
//     *
//     * @return
//     */
//    public float getScaleMinSize() {
//        return 0;
//    }
//
//    /**
//     * 点击确认
//     *
//     * @param x
//     * @param y
//     * @return
//     */
//    public boolean isClickDown(float x, float y) {
//        float minX = pX - halfDrawWidth;
//        float maxX = pX + halfDrawWidth;
//        float minY = pY - halfDrawHeight;
//        float maxY = pY + halfDrawHeight;
//        return x > minX && x < maxX && y > minY && y < maxY;
//    }
//
//
//    public int getCurrentPage() {
//        return currentPage;
//    }
//}
