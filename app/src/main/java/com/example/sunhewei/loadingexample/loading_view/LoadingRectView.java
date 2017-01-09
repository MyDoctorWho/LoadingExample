package com.example.sunhewei.loadingexample.loading_view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by sunhewei on 2017/1/7.
 */

public class LoadingRectView extends SurfaceView{

    private int color = Color.parseColor("#ff9900");

    private Paint paint;

    private SurfaceHolder surfaceHolder;

    private Point pCenter;

    private int viewWidth,viewHeight;

    private float space;

    public LoadingRectView(Context context) {
        this(context,null);
    }

    public LoadingRectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void init(){
        surfaceHolder = getHolder();
        setZOrderOnTop(true);
        surfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
        initPain();
        pCenter = new Point();
    }

    public void initPain(){
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        viewWidth = getMeasuredWidth();
        viewHeight = getMeasuredHeight();
        if(viewHeight != 0 && viewWidth != 0){
            pCenter.x = viewWidth / 2;
            pCenter.y = viewHeight / 2;
            float spacereate = 1 / 100f;
            space = viewWidth<=viewHeight?viewWidth * spacereate:viewHeight * spacereate;

            paint.setPathEffect(new CornerPathEffect(0.1f));
            
        }
    }


    public class Point{
        public float x,y;

        public Point(){

        }

        public Point(float x,float y){
            this.x = x;
            this.y = y;
        }
    }
}
