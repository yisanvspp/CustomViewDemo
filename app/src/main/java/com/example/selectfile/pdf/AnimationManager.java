package com.example.selectfile.pdf;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.PointF;
import android.view.animation.DecelerateInterpolator;

public class AnimationManager {

    private SignView mSignView;
    private ValueAnimator animation;

    public AnimationManager(SignView signView) {
        this.mSignView = signView;
    }

    public void startZoomAnimation(float centerX, float centerY, float zoomFrom, float zoomTo) {
        animation = ValueAnimator.ofFloat(zoomFrom, zoomTo);
        animation.setInterpolator(new DecelerateInterpolator());
        ZoomAnimation zoomAnim = new ZoomAnimation(centerX, centerY);
        animation.addUpdateListener(zoomAnim);
        animation.addListener(zoomAnim);
        animation.setDuration(400);
        animation.start();
    }

    class ZoomAnimation implements ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener {

        private final float centerX;
        private final float centerY;

        public ZoomAnimation(float centerX, float centerY) {
            this.centerX = centerX;
            this.centerY = centerY;
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            float zoom = (Float) animation.getAnimatedValue();
            mSignView.zoomCenteredTo(zoom, new PointF(centerX, centerY));
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {
        }

        @Override
        public void onAnimationStart(Animator animation) {
        }

    }

}
