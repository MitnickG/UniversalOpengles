package com.mitnick.zoomcoloruniversal.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.Scroller;

/**
 * Created by Administrator on 2017/4/6.
 * 图片缩放、
 * 拖动仅支持水平与竖直拖动
    惯性只支持水平与竖直
 */

public class ImageViewSelf extends ImageView implements ScaleGestureDetector.OnScaleGestureListener, View.OnTouchListener, ViewTreeObserver.OnGlobalLayoutListener {
    private static final String TAG = ImageViewSelf.class.getSimpleName();
    public static final float SCALE_MAX = 20.0f;
    float x = 0, y = 0;
    float mLastX = x;
    float mLastY = y;
    float dx = 0;
    float dy = 0;
    private float initScale = 1.0f;
    private boolean isUp = true;

    private final float[] matrixValues = new float[9];
    private boolean once = true;

    private ScaleGestureDetector mScaleGestureDetector = null;
    private final Matrix mScaleMatrix = new Matrix();//应该是用于存入该ImageView的，缩放后的Matrix


    public ImageViewSelf(Context context) {
        this(context, null);
    }

    public ImageViewSelf(Context context, AttributeSet attrs) {
        super(context, attrs);
        super.setScaleType(ScaleType.MATRIX);
        mScaleGestureDetector = new ScaleGestureDetector(context, this);
        this.setOnTouchListener(this);
    }

    private float scaleOnScale;
    private  float scaleFactor;
    private  boolean isActivityZoom=false;
    private  float detectorGetX,detectorGetY;
    /*
    * 缩放过程
    * */
    @SuppressLint("NewApi")
    @Override
    public boolean onScale(ScaleGestureDetector detector) {
            scaleOnScale = getScale();//0.25+，每次scale时，先获得当前缩放比例
//                    Log.d("~gyh", "onScale: scale:.."+scaleOnScale);
/*// detector.getScaleFactor()缩放因子，>1表示正在放大，<1表示正在缩小*/
            scaleFactor = detector.getScaleFactor();//缩放比例变化值
//        Log.d("~gyh", "onScale: scaleFactor:.."+scaleFactor);
        if (getDrawable() == null)
            return true;
/*
* SCALE_MAX定义了放大最值
*
* *//*
*
* 如果当前的缩放值小于最大缩放值，而且缩放变量大于1.0（手势在放大）
* 或者
* 如果当前的缩放值大于最小缩放值，而且缩放变量小于1.0（手势在缩小）
* */
        if ((scaleOnScale < SCALE_MAX && scaleFactor > 1.0f)
                || (scaleOnScale > initScale && scaleFactor < 1.0f)) {

            if (scaleFactor * scaleOnScale < initScale) {
                scaleFactor = initScale / scaleOnScale;
            }
            if (scaleFactor * scaleOnScale > SCALE_MAX) {
                scaleFactor = SCALE_MAX / scaleOnScale;
            }
/*
* 1、setScale(sx,sy)，首先会将该Matrix设置为对角矩阵，即相当于调用reset()方法，然后在设置该Matrix的MSCALE_X和MSCALE_Y直接设置为sx,sy的值
2、preScale(sx,sy)，不会重置Matrix，而是直接与Matrix之前的MSCALE_X和MSCALE_Y值结合起来（相乘），M' = M * S(sx, sy)。
3、postScale(sx,sy)，不会重置Matrix，而是直接与Matrix之前的MSCALE_X和MSCALE_Y值结合起来（相乘），M' = S(sx, sy) * M。
* */
            /*
            * 第一次运行到这里的时候mScaleMatrix是空的Matrix
            * 说明这个方法只是对矩阵进行变换
            * */
            detectorGetX=detector.getFocusX();
            detectorGetY=detector.getFocusY();
            mScaleMatrix.postScale(scaleFactor, scaleFactor,
                    detector.getFocusX(), detector.getFocusY());//X\Y应该是手势多点触控的中间的X、Y
            checkBorderAndCenterWhenScale();
            setImageMatrix(mScaleMatrix);//应该就是将缩放后的matrix放入该image，还是将matrix矩阵状态映射到image上
        }
        return true;
    }
    public boolean setScaleFromActivity(float zoomLevel) {

        scaleOnScale = getScale();//0.25+，每次scale时，先获得当前缩放比例
        scaleFactor=zoomLevel;//缩放比例变化值
//        Log.d("~gyh", "onScale: scaleFactor:.." + scaleFactor);
        if (getDrawable() == null)
            return true;
/*
* SCALE_MAX定义了放大最值
*
* *//*
*
* 如果当前的缩放值小于最大缩放值，而且缩放变量大于1.0（手势在放大）
* 或者
* 如果当前的缩放值大于最小缩放值，而且缩放变量小于1.0（手势在缩小）
* */
        if ((scaleOnScale < SCALE_MAX && scaleFactor > 1.0f)
                || (scaleOnScale > initScale && scaleFactor < 1.0f)) {

            if (scaleFactor * scaleOnScale < initScale) {
                scaleFactor = initScale / scaleOnScale;
            }
            if (scaleFactor * scaleOnScale > SCALE_MAX) {
                scaleFactor = SCALE_MAX / scaleOnScale;
            }
/*
* 1、setScale(sx,sy)，首先会将该Matrix设置为对角矩阵，即相当于调用reset()方法，然后在设置该Matrix的MSCALE_X和MSCALE_Y直接设置为sx,sy的值
2、preScale(sx,sy)，不会重置Matrix，而是直接与Matrix之前的MSCALE_X和MSCALE_Y值结合起来（相乘），M' = M * S(sx, sy)。
3、postScale(sx,sy)，不会重置Matrix，而是直接与Matrix之前的MSCALE_X和MSCALE_Y值结合起来（相乘），M' = S(sx, sy) * M。
* */
            /*
            * 第一次运行到这里的时候mScaleMatrix是空的Matrix
            * 说明这个方法只是对矩阵进行变换
            * */
//            detectorGetX = detector.getFocusX();
//            detectorGetY = detector.getFocusY();
            mScaleMatrix.postScale(scaleFactor, scaleFactor,
                    detectorGetX, detectorGetY);//X\Y应该是手势多点触控的中间的X、Y
            checkBorderAndCenterWhenScale();
            setImageMatrix(mScaleMatrix);//应该就是将缩放后的matrix放入该image，还是将matrix矩阵状态映射到image上
        }
        return true;
    }
/*
* 一般都会有这个方法
* 字面意思：当缩放的时候，计算边界与中心
* */
    private void checkBorderAndCenterWhenScale() {
        RectF rect = getMatrixRectF();//那这里应该就已经有了那张图片矩阵
        float deltaX = 0;
        float deltaY = 0;
        /*
        * 7#
        * 1280*800*/
        int width = getWidth();
        int height = getHeight();

        /*
        * 如果矩阵宽度大于等于iv的宽度
        * */
        if (rect.width() >= width) {
            /*
            *如果矩阵宽度大于等于iv的宽度，同时矩阵左边界大于0
            * 如果iv全屏，此时相当于图片出屏幕右边
            * 得到X的变化值为出界值rect.left
            * 加-号代表方向
            * */
            if (rect.left > 0) {
                deltaX = -rect.left;
            }
            /*
            *如果矩阵宽度大于等于iv的宽度，同时矩阵右边界坐标小于iv宽度
            * 如果iv全屏，此时相当于图片出屏幕左边
            * 得到X的变化值为出界值width - rect.right
            * */
            if (rect.right < width) {
                deltaX = width - rect.right;
            }
        }
        if (rect.height() >= height) {
            if (rect.top > 0) {
                deltaY = -rect.top;//超出上边界定为负方向
            }
            if (rect.bottom < height) {
                deltaY = height - rect.bottom;
            }
        }

        /*
        * 下面两种为矩阵宽高没有超过iv宽高情况
        * 得到的是矩阵中心线距离iv中心线的距离
        * 当矩阵中心线在iv中心线右边时，deltaX为负方向
        * 当矩阵中心线在iv中心线下边时，deltaY为负方向
        * */
        if (rect.width() < width) {
            deltaX = width * 0.5f - rect.right + 0.5f * rect.width();
        }
        if (rect.height() < height) {
            deltaY = height * 0.5f - rect.bottom + 0.5f * rect.height();
        }
        Log.e("~gyh", "deltaX = " + deltaX + " , deltaY = " + deltaY);
        mScaleMatrix.postTranslate(deltaX, deltaY);//设置以哪个中心店变换
    }
/*
* RectF坐标系中一块矩形区域 F表示float类型
* 空参表示开辟一块空矩形区域
* getDrawable得到该ImageView中的drawable
* drawable是view被画出来的那个对象
* */
    private RectF getMatrixRectF() {
        Matrix matrix = mScaleMatrix;
        RectF rect = new RectF();
        Drawable d = getDrawable();
        if (null != d) {
            /*
            * getIntrinsicWidth和Height以dp为单位
            * */
            rect.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());//定义一块这样大小的矩阵
            matrix.mapRect(rect);//应该是将matrix数据写入到rect矩阵中，将Matrix 的值映射到RecF中
        }
        return rect;
    }

    /*
    * 缩放起始
    * */
    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    /*
    * 缩放结束
    * */
    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
    }

   VelocityTracker mVelocityTracker;
    private FlingRunnable mFlingRunnable;
    @Override
    public boolean onTouch(View v, MotionEvent event) {

        mScaleGestureDetector.onTouchEvent(event);//缩放手势拦截
        switch (event.getAction()) {
            /*
            * isUp应该是在MOVE开始和过程中的某些界定，只有当UP动作的时候会被设置为true
            * */
            case MotionEvent.ACTION_MOVE:
                /*
                * 当按住不动的时候  isUp：应该是获取当前按住的点的相关数据
                * 当按住移动的时候  isNotUp：根据移动数据，进行重新计算与绘制
                * */
                if(isUp){
//                    Log.d("~gyh", "onTouch:isUp event.getX():"+event.getX());
                mLastX = event.getX();
                mLastY = event.getY();
                    dx = mLastX - x;//dx可以理解为deltaX
                    dy = mLastY - y;
                    if (mVelocityTracker != null){
                        //将当前事件添加到检测器中
                        mVelocityTracker.addMovement(event);
                    }

                    if (Math.abs(dx) > Math.abs(dy)) {
                    dy = 0;
                    isUp = false;
                } else if (Math.abs(dx) <  Math.abs(dy)) {
                    dx = 0;
                    isUp = false;
                }
                mScaleMatrix.postTranslate(dx, dy);
                setImageMatrix(mScaleMatrix);//ImageView里的方法
                x = mLastX;
                y = mLastY;
                }
                else {
                    /*
                    * 移动过程中，会不断调用MOVE事件，而isUp为false，所以一直调如下过程
                    * */
                    /*
                    * 当上下MOVE
                    * */
                    if(dx == 0){
                        mLastY = event.getY();
                        dy = mLastY - y;
                        mScaleMatrix.postTranslate(dx, dy);
                        setImageMatrix(mScaleMatrix);
                        y =  mLastY;
                    }
                    /*
                    * 当左右MOVE
                    * */
                    else {
                        mLastX = event.getX();
                        float dx = mLastX - x;
                        mScaleMatrix.postTranslate(dx, dy);
                        setImageMatrix(mScaleMatrix);
                        x = mLastX;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:

                if (!isUp){//MOVE之后isUp一定是false，这里应该是怕出错这样写
                    if (mVelocityTracker != null){
                        //将当前事件添加到检测器中
                        mVelocityTracker.addMovement(event);
                        //计算当前的速度
                        mVelocityTracker.computeCurrentVelocity(1000);
                        //得到当前x方向速度
                        float vX=0;
                        //得到当前y方向的速度
                        float vY=0;
                        /*
                        * 如果是上下移动
                        * */
                        if (dx==0) {
                            //得到当前x方向速度
                            vX = 0;
                            //得到当前y方向的速度
                            vY = mVelocityTracker.getYVelocity();
                        }
                        /*
                        * 如果是左右移动
                        * */
                        if (dy==0) {
                            //得到当前x方向速度
                            vX = mVelocityTracker.getXVelocity();
                            //得到当前y方向的速度
                            vY = 0;
                        }

                        mFlingRunnable = new FlingRunnable(getContext());
                        //调用fling方法，传入控件宽高和当前x和y轴方向的速度
                        //这里得到的vX和vY和scroller需要的velocityX和velocityY的负号正好相反
                        //所以传入一个负值
                        /*if (dx==0) {

                        }*/
                        mFlingRunnable.fling(getWidth(),getHeight(),(int)-vX,(int)-vY);
                        //执行run方法
                        post(mFlingRunnable);
                    }}
                isUp = true;
                break;
            case MotionEvent.ACTION_DOWN:
                x = event.getX();
                y = event.getY();
                //初始化速度检测器
                mVelocityTracker = VelocityTracker.obtain();
                if (mVelocityTracker != null){
                    //将当前的事件添加到检测器中
                    mVelocityTracker.addMovement(event);
                }
                //当手指再次点击到图片时，停止图片的惯性滑动
                if (mFlingRunnable != null){
                    mFlingRunnable.cancelFling();
                    mFlingRunnable = null;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }

/*
* Translate           平移变换

Rotate                旋转变换

Scale                  缩放变换

Skew                  错切变换
* *//*
* |scaleX, skewX, translateX|
|skewY, scaleY, translateY|
|0 ,0 , scale |
* */
    public final float getScale() {
        /*
        * matrixValues一个float的3*3矩阵数组，此处调用方法为void，应该是把mScaleMatrix的某个值赋给matrixValues
        * 调用了native接口
        * 应该是将matrix的9个参数放到3*3的数组中，此方法返回数组中MSCALE_X这个位置的数值
        * 按照返回值的调用来看，应该是返回的当前缩放程度
        * */
        /** Copy 9 values from the matrix into the array.
         */
        mScaleMatrix.getValues(matrixValues);
        return matrixValues[Matrix.MSCALE_X];//如果没猜错此处返回MASCALE_Y没区别
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeGlobalOnLayoutListener(this);
    }

    @Override
    public void onGlobalLayout() {
        if (once) {
            Drawable d = getDrawable();
            if (d == null)
                return;
            int width = getWidth();
            int height = getHeight();

            int dw = d.getIntrinsicWidth();
            int dh = d.getIntrinsicHeight();
            float scale = 1.0f;


            if (dw > width && dh > height) {
                scale = Math.min(width * 1.0f / dw, height * 1.0f / dh);
            }
            initScale = scale;

            Log.e(TAG, "initScale = " + initScale);
            mScaleMatrix.postTranslate((width - dw) / 2, (height - dh) / 2);
            mScaleMatrix.postScale(scale, scale, getWidth() / 2,
                    getHeight() / 2);
            // 图片移动至屏幕中心
            setImageMatrix(mScaleMatrix);
            once = false;
        }
    }

    /**
     * 惯性滑动
     */
    private class FlingRunnable implements Runnable {
        private Scroller mScroller;
        private int mCurrentX , mCurrentY;

        public FlingRunnable(Context context){
            mScroller = new Scroller(context);
        }

        public void cancelFling(){
            mScroller.forceFinished(true);
        }

        /**
         * 这个方法主要是从onTouch中或得到当前滑动的水平和竖直方向的速度
         * 调用scroller.fling方法，这个方法内部能够自动计算惯性滑动
         * 的x和y的变化率，根据这个变化率我们就可以对图片进行平移了
         */
        public void fling(int viewWidth , int viewHeight , int velocityX ,
                          int velocityY){
            RectF rectF = getMatrixRectF();
            if (rectF == null){
                return;
            }
            //startX为当前图片左边界的x坐标
            final int startX = Math.round(-rectF.left);
            final int minX , maxX , minY , maxY;
            //如果图片宽度大于控件宽度
            if (rectF.width() > viewWidth){
                //这是一个滑动范围[minX,maxX]，详情见下图
                minX = 0;
                maxX = Math.round(rectF.width() - viewWidth);
            }else{
                //如果图片宽度小于控件宽度，则不允许滑动
                minX = maxX = startX;
            }
            //如果图片高度大于控件高度，同理
            final int startY = Math.round(-rectF.top);
            if (rectF.height() > viewHeight){
                minY = 0;
                maxY = Math.round(rectF.height() - viewHeight);
            }else{
                minY = maxY = startY;
            }
            mCurrentX = startX;
            mCurrentY = startY;

            if (startX != maxX || startY != maxY){
                //调用fling方法，然后我们可以通过调用getCurX和getCurY来获得当前的x和y坐标
                //这个坐标的计算是模拟一个惯性滑动来计算出来的，我们根据这个x和y的变化可以模拟
                //出图片的惯性滑动
                mScroller.fling(startX,startY,velocityX,velocityY,minX,maxX,minY,maxY);
            }

        }

        /**
         * 每隔16ms调用这个方法，实现惯性滑动的动画效果
         */
        @Override
        public void run() {
            if (mScroller.isFinished()){
                return;
            }
            //如果返回true，说明当前的动画还没有结束，我们可以获得当前的x和y的值
            if (mScroller.computeScrollOffset()){
                //获得当前的x坐标
                final int newX = mScroller.getCurrX();
                //获得当前的y坐标
                final int newY = mScroller.getCurrY();
                //进行平移操作
                mScaleMatrix.postTranslate(mCurrentX-newX , mCurrentY-newY);
                checkBorderAndCenterWhenScale();
                setImageMatrix(mScaleMatrix);

                mCurrentX = newX;
                mCurrentY = newY;
                //每16ms调用一次
                postDelayed(this,16);
            }
        }
    }

    //        private boolean setInitScale;
    public void setInitScale(){
//        initScale=1.0f;
//        mScaleMatrix.setScale(0.25510204f,0.25510204f);
        mScaleMatrix.setTranslate(100f,100f);
        checkBorderAndCenterWhenScale();
        setImageMatrix(mScaleMatrix);
    }

}
