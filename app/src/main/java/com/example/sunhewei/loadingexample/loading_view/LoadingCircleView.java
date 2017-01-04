package com.example.sunhewei.loadingexample.loading_view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
//import java.util.concurrent.TimeUnit;
//
//import rx.Observable;
//import rx.Subscription;
//import rx.functions.Action1;

/**
 * Created by sunhewei on 2017/1/3.
 */

public class LoadingCircleView extends View {

    private final int DEFAULT_DURATION = 15;
    private final int DEFAULT_EXTERNAL_RADIUS = dp2px(60);
    private final int DEFAULT_INTERNAL_RADIUS = dp2px(8);
    private final int DEFAULT_TEXTSIZE = dp2px(16);

    private final int DEFAULT_RADIAN = 45;

    private int mWidth;
    private int mHeight;
//    private Subscription mTimer;
    private Paint mPaint;
    private Path mPath = new Path();
    //动画播放标志位
    private boolean runAnim = false;

    private String loadings[] = new String[]{"loading","loading.","loading..","loading..."};
    //内圆色
    int mColors[];

    //外圆角度
    int mAngle = 0;

    //圈数
    int mCyclic = 0;

    //变大动画圆半径
    private float mGetBiggerCircleRadius;

    //移动圆半径
    private float mGetSmallerCircleRadius;

    //外圆
    private List<PointF> mPoints;

    //属性动画集
    private List<ValueAnimator> mAnimators;

    //外圆圆点
    private float x0, y0;

    //点间的弧度
    private int mRadian = DEFAULT_RADIAN;

    //时间间隔
    private int mDuration;

    //内圆半径
    private float mInternalR;

    //外圆半径
    private float mExternalR;

    public LoadingCircleView(Context context) {
        this(context,null);
    }

    public LoadingCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void init(){
        mAnimators = new ArrayList<>();
        mPoints = new ArrayList<>();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);

        mColors = new int[]{Color.GRAY, Color.LTGRAY};

        mDuration = DEFAULT_DURATION;
        mInternalR = DEFAULT_INTERNAL_RADIUS;
        mExternalR = DEFAULT_EXTERNAL_RADIUS;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mWidth = w;
        mHeight = h;

        setShader();
        resetPoint();
    }



    public void start() {
        if(!runAnim){
            runAnim = true;
            new Thread(animRunnable).start();
            this.setVisibility(VISIBLE);
        }
    }

    public void stop() {
        runAnim = false;
        this.setVisibility(GONE);
    }


    private Runnable animRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                while (runAnim){
                    Thread.sleep(mDuration);
                    dealTimerBusiness();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    private void dealTimerBusiness() {

        setOffset((mAngle % mRadian) / (float) mRadian);

        mAngle++;
        if (mAngle == 360) {
            mAngle = 0;
            mCyclic++;
        }
    }

    public void setOffset(float offSet) {
        createAnimator();
        seekAnimator(offSet);
        postInvalidate();
    }

    private void createAnimator() {

        if (mPoints.isEmpty()) {
            return;
        }
        mAnimators.clear();

        ValueAnimator circleGetSmallerAnimator = ValueAnimator.ofFloat(mGetBiggerCircleRadius, mGetSmallerCircleRadius);
        circleGetSmallerAnimator.setDuration(5000L);
        circleGetSmallerAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mGetSmallerCircleRadius = (float) animation.getAnimatedValue();
            }
        });
        mAnimators.add(circleGetSmallerAnimator);

        ValueAnimator circleGetBiggerAnimator = ValueAnimator.ofFloat(mGetSmallerCircleRadius, mGetBiggerCircleRadius);
        circleGetBiggerAnimator.setDuration(5000L);
        circleGetBiggerAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mGetBiggerCircleRadius = (float) animation.getAnimatedValue();
            }
        });
        mAnimators.add(circleGetBiggerAnimator);

    }


    private void seekAnimator(float offset) {
        for (ValueAnimator animator : mAnimators) {
            animator.setCurrentPlayTime((long) (5000.0F * offset));
        }
    }


    public void setShader(){
        Shader mLinearGradient = new LinearGradient(mWidth / 2 - mExternalR, mHeight / 2 - mExternalR, mWidth / 2 + mExternalR, mHeight / 2 + mExternalR, mColors, null,
                Shader.TileMode.CLAMP);
        mPaint.setShader(mLinearGradient);
    }

    public void resetPoint(){
        x0 = mWidth / 2;
        y0 = mHeight / 2;

        createPoints();

        if (!mPoints.isEmpty()) {
            mGetBiggerCircleRadius = mInternalR / 10f * 14f;
            mGetSmallerCircleRadius = mInternalR / 10f ;
            postInvalidate();
        }
    }

    private void createPoints() {
        mPoints.clear();
        for (int i = 0; i <= 360; i++) {
            if (i % mRadian == 0) {
                float x1 = getCircleX(i);
                float y1 = getCircleY(i);
                mPoints.add(new PointF(x1, y1));
            }
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawCircle(canvas);
        drawBezier(canvas);
        drawText(canvas);
    }


    private void drawText(Canvas canvas){
        Rect targetRect = new Rect((int)(mWidth / 2 - mExternalR), (int)(mHeight / 2 - mExternalR), (int)(mWidth / 2 + mExternalR), (int)(mHeight / 2 + mExternalR));
        Paint.FontMetricsInt fontMetrics = mPaint.getFontMetricsInt();

        int baseline = (targetRect.bottom + targetRect.top - fontMetrics.bottom - fontMetrics.top) / 2;
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(DEFAULT_TEXTSIZE);
        int index = mAngle/mRadian%4;
        canvas.drawText(loadings[index],targetRect.centerX(), baseline, mPaint);
    }


    private void drawCircle(Canvas canvas) {
        for (int i = 0; i < mPoints.size(); i++) {
            int index = mAngle / mRadian;
                if (i == index) {
                    if (mAngle % mRadian == 0) {
                        canvas.drawCircle(getCircleX(mAngle), getCircleY(mAngle), mGetBiggerCircleRadius, mPaint);
                    } else if (mAngle % mRadian > 0) {
                        canvas.drawCircle(getCircleX(mAngle), getCircleY(mAngle), mGetSmallerCircleRadius < mInternalR ? mInternalR : mGetSmallerCircleRadius, mPaint);
                    }
                } else if (i == index + 1) {
                    if (mAngle % mRadian == 0) {
                        canvas.drawCircle(mPoints.get(i).x, mPoints.get(i).y, mInternalR, mPaint);
                    } else {
                        canvas.drawCircle(mPoints.get(i).x, mPoints.get(i).y, mGetBiggerCircleRadius < mInternalR ? mInternalR : mGetBiggerCircleRadius, mPaint);
                    }
                }
            if(i != index + 1){
                canvas.drawCircle(mPoints.get(i).x, mPoints.get(i).y, mInternalR, mPaint);
            }
        }
    }

    private void drawBezier(Canvas canvas) {

        mPath.reset();

        int circleIndex = mAngle / mRadian;

        float rightX = getCircleX(mAngle);
        float rightY = getCircleY(mAngle);

        float leftX, leftY;

        int index;
        index = circleIndex + 1;
        leftX = mPoints.get(index >= mPoints.size() ? mPoints.size() - 1 : index).x;
        leftY = mPoints.get(index >= mPoints.size() ? mPoints.size() - 1 : index).y;


        double theta = getTheta(new PointF(leftX, leftY), new PointF(rightX, rightY));
        float sinTheta = (float) Math.sin(theta);
        float cosTheta = (float) Math.cos(theta);

        PointF pointF1 = new PointF(leftX - mInternalR * sinTheta, leftY + mInternalR * cosTheta);
        PointF pointF2 = new PointF(rightX - mInternalR * sinTheta, rightY + mInternalR * cosTheta);
        PointF pointF3 = new PointF(rightX + mInternalR * sinTheta, rightY - mInternalR * cosTheta);
        PointF pointF4 = new PointF(leftX + mInternalR * sinTheta, leftY - mInternalR * cosTheta);

        if (mAngle % mRadian < mRadian / 2) {

            mPath.moveTo(pointF3.x, pointF3.y);
            mPath.quadTo(rightX + (leftX - rightX) / (mRadian / 2) * (mAngle % mRadian > (mRadian / 2) ? (mRadian / 2) : mAngle % mRadian), rightY + (leftY - rightY) / (mRadian / 2) * (mAngle % mRadian > (mRadian / 2) ? (mRadian / 2) : mAngle % mRadian), pointF2.x, pointF2.y);
            mPath.lineTo(pointF3.x, pointF3.y);

            mPath.moveTo(pointF4.x, pointF4.y);
            mPath.quadTo(leftX + (rightX - leftX) / (mRadian / 2) * (mAngle % mRadian > (mRadian / 2) ? (mRadian / 2) : mAngle % mRadian), leftY + (rightY - leftY) / (mRadian / 2) * (mAngle % mRadian > (mRadian / 2) ? (mRadian / 2) : mAngle % mRadian), pointF1.x, pointF1.y);
            mPath.lineTo(pointF4.x, pointF4.y);

            mPath.close();
            canvas.drawPath(mPath, mPaint);
            return;
        }

        mPath.moveTo(pointF1.x, pointF1.y);
        mPath.quadTo((leftX + rightX) / 2, (leftY + rightY) / 2, pointF2.x, pointF2.y);
        mPath.lineTo(pointF3.x, pointF3.y);
        mPath.quadTo((leftX + rightX) / 2, (leftY + rightY) / 2, pointF4.x, pointF4.y);
        mPath.lineTo(pointF1.x, pointF1.y);

        mPath.close();

        canvas.drawPath(mPath, mPaint);
    }

    private double getTheta(PointF pointCenterLeft, PointF pointCenterRight) {
        double theta = Math.atan((pointCenterRight.y - pointCenterLeft.y) / (pointCenterRight.x - pointCenterLeft.x));
        return theta;
    }

    private float getCircleY(int angle) {
        return y0 + mExternalR * (float) Math.sin(angle * 3.14 / 180);
    }

    private float getCircleX(int angle) {
        return x0 + mExternalR * (float) Math.cos(angle * 3.14 / 180);
    }

    private int dp2px(float dp) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
