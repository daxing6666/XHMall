package com.xinheng.frame.ui.view.refresh;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;

public class MaterialWaveView extends View implements MaterialHeadListener {

    private int waveHeight;
    private int headHeight;
    public static int DefaulWaveHeight;
    public static int DefaulHeadHeight;
    private Path path;
    private Paint paint;
    private int color;

    public MaterialWaveView(Context context) {
        this(context, null, 0);
        init();
    }

    public MaterialWaveView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init();
    }

    public MaterialWaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        setWillNotDraw(false);
        path = new Path();
        paint = new Paint();
        paint.setAntiAlias(true);//抗锯齿
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        path.reset();// 重置路径
        paint.setColor(color);
        //默认从坐标(0,0)开始绘制
        path.lineTo(0, headHeight);
        //绘制贝塞尔曲线
        path.quadTo(getMeasuredWidth() / 2, headHeight + waveHeight, getMeasuredWidth(), headHeight);
        path.lineTo(getMeasuredWidth(), 0);
        canvas.drawPath(path, paint);
    }


    @Override
    public void onComlete(MaterialRefreshLayout br) {
        waveHeight = 0;
        ValueAnimator animator =ValueAnimator.ofInt(headHeight,0);
        animator.setDuration(200);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                headHeight = value;
                invalidate();
            }
        });
    }

    @Override
    public void onBegin(MaterialRefreshLayout br) {

    }

    @Override
    public void onPull(MaterialRefreshLayout br, float fraction) {
        setHeadHeight((int) (dip2px(getContext(), DefaulHeadHeight) * limitValue(1, fraction)));
        setWaveHeight((int) (dip2px(getContext(), DefaulWaveHeight) * Math.max(0, fraction - 1)));
        invalidate();
    }

    @Override
    public void onRelease(MaterialRefreshLayout br, float fraction) {

    }

    @Override
    public void onRefreshing(MaterialRefreshLayout br) {
        setHeadHeight((int) (dip2px(getContext(), DefaulHeadHeight)));
        ValueAnimator animator = ValueAnimator.ofInt(getWaveHeight(),0);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setWaveHeight((int) animation.getAnimatedValue());
                invalidate();
            }
        });
        animator.setInterpolator(new BounceInterpolator());
        animator.setDuration(200);
        animator.start();
    }

    public int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public float limitValue(float a, float b) {
        float valve = 0;
        final float min = Math.min(a, b);
        final float max = Math.max(a, b);
        valve = valve > min ? valve : min;
        valve = valve < max ? valve : max;
        return valve;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
        invalidate();
    }

    public int getHeadHeight() {
        return headHeight;
    }

    public void setHeadHeight(int headHeight) {
        this.headHeight = headHeight;
    }

    public int getWaveHeight() {
        return waveHeight;
    }

    public void setWaveHeight(int waveHeight) {
        this.waveHeight = waveHeight;
    }

    public int getDefaulWaveHeight() {
        return DefaulWaveHeight;
    }

    public void setDefaulWaveHeight(int defaulWaveHeight) {
        DefaulWaveHeight = defaulWaveHeight;
    }

    public int getDefaulHeadHeight() {
        return DefaulHeadHeight;
    }

    public void setDefaulHeadHeight(int defaulHeadHeight) {
        DefaulHeadHeight = defaulHeadHeight;
    }
}
