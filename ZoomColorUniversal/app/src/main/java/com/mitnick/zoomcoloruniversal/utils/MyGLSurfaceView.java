package com.mitnick.zoomcoloruniversal.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLException;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.mitnick.zoomcoloruniversal.opengles.DirectDriver;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Mitnick.Guo on 2017/6/7.
 */
public class MyGLSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {
private static final String TAG="~gyh";
    private int textureId;
    private SurfaceTexture surfaceTexture;
    private DirectDriver directDriver;
    Context context;
    //手势控制用
    private UniversalOnGestureListener universalOnGestureListener;
    private GestureDetector gestureDetector;

    public MyGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;

        /*
        * 手势控制初始化始
        * */
        universalOnGestureListener = new UniversalOnGestureListener();
        gestureDetector = new GestureDetector(context,universalOnGestureListener);
        gestureDetector.setIsLongpressEnabled(false);
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //实现快进快退松开时候的回调
                if (event.getAction() == MotionEvent.ACTION_UP){
                    if (hasFF_REW){
                        if (universalGestureListener != null) {
                            universalGestureListener.onEndFF_REW(event);
                        }
                        hasFF_REW = false;
                    }else  if (hasVolume){
                        if (universalGestureListener != null) {
                            universalGestureListener.onEndVolume(event);
                        }
                        hasVolume = false;
                    }
                }
                //监听触摸事件
                return gestureDetector.onTouchEvent(event);
            }
        });
        //手势控制初始化终
        setEGLContextClientVersion(2);
        setRenderer(this);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
    }
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
      /* String gl_renderer = gl.glGetString(GL10.GL_RENDERER);
        String  gl_vendor = gl.glGetString(GL10.GL_VENDOR);
        String gl_version = gl.glGetString(GL10.GL_VERSION);
        String  gl_extensions = gl.glGetString(GL10.GL_EXTENSIONS);
        Log.d("suhuazhi", "gl_renderer = " + gl_renderer);//渲染器
        Log.d("suhuazhi", "gl_vendor = " + gl_vendor);//供应商
        Log.d("suhuazhi", "gl_version = " + gl_version);//版本
        Log.d("suhuazhi", "gl_extensions = " + gl_extensions);//扩展名*/
        GLES20.glClearColor(0.5f,0.5f,0.5f,1.0f);
        textureId = createTextureId();
        surfaceTexture = new SurfaceTexture(textureId);
        surfaceTexture.setOnFrameAvailableListener(this);
        directDriver = new DirectDriver(textureId,getContext());
    }

    private int createTextureId() {
        int[] textureId=new int[1];
        GLES20.glGenTextures(1,textureId,0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,textureId[0]);

        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MAG_FILTER,  GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        return textureId[0];
    }
    //顶点原始矩阵
    private float[] mMVPMatrix=new float[]{
            1,0,0,0,
            0,1,0,0,
            0,0,1,0,
            0,0,0,1};
    //纹理原始矩阵
    private float[] mTextureMatrix=new float[]{
            1,0,0,0,
            0,1,0,0,
            0,0,1,0,
            0,0,0,1};
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.d(TAG, "onSurfaceChanged: ");

        GLES20.glViewport(0,0,width,height);
        if (!CameraInstance.getInstance().isPreviewing()) {
            Log.d(TAG, "onSurfaceChanged:!CameraInstance.getInstance().isPreviewing() ");
            CameraInstance.getInstance().doOpenCamera();
            CameraInstance.getInstance().doStartPreview(surfaceTexture);
            CameraInstance.getInstance().switch_binmode(2);
            CameraInstance.getInstance().switch_exp_mode(0);
        }
//        surfaceTexture.getTransformMatrix(mMVPMatrix);//拿到原始矩阵4*4

    }
    long lastBmp=0,bmp=0;
    boolean ifExit=true;
    int flag=0;
//    GL10 gl;

  /*  Runnable runnable=new Runnable() {
        @Override
        public void run() {
                    bmp = getBmp(gl);
        }
    };*/
    //Thread thread = new Thread("gyhThread");
    @Override
    @TargetApi(11)
    public void onDrawFrame(final GL10 gl) {
        Log.d(TAG, "onDrawFrame: ");
//        this.gl=gl;
        printFPS();
        GLES20.glClearColor(1f,1.f,1.f,1.f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        surfaceTexture.updateTexImage();//更新一帧
        /*float[] mtx=new float[16];
        surfaceTexture.getTransformMatrix(mtx);
        //surfaceTexture.getTransformMatrix(mMVPMatrix);
        for (int i = 0; i < mtx.length; i++) {
            if (i<=5) {
                Log.d("~gyh", "onDrawFrame: mMVPMatrix:"+mtx[i]);
            }
        }*/
      /*  for (int i = 0; i < mMVPMatrix.length; i++) {
                Log.d("~gyh", "onDrawFrame: mMVPMatrix:"+mMVPMatrix[i]);
        }
        Log.d(TAG, "onDrawFrame: mMVPMatrix=====================");*/
        if (isSelectedZoomUp) {
            Log.d(TAG, "onDrawFrame: isSelectedZoomUp111:"+isSelectedZoomUp);
            directDriver=new DirectDriver(textureId,getContext());
            isSelectedZoomUp=false;
        }
        directDriver.draw(selectedColorMode,mMVPMatrix,mTextureMatrix,maxLerp,minLerp);
//        directDriver.draw(selectedColorMode,mMVPMatrix,maxLerp,minLerp);
//        directDriver.draw(selectedColorMode);

        //Log.d("~gyh", "onDrawFrame: front thread");
       //Log.d("~gyh", "onDrawFrame: thread.isAlive():"+thread.isAlive());
         /*if (!thread.isAlive()) {
             thread = new Thread(new Runnable() {
                 @Override
                 public void run() {
                     bmp = getBmp(gl);
                 }
             }, "gyhThread");
            thread.start();
            if (lastBmp!=bmp) {
                Log.d("~Gyh", "onDrawFrame:  currTime:"+(bmp-lastBmp));
                lastBmp=bmp;//说明处理完一张图了
            }
        }*/
//                Bitmap bmp = createBitmapFromGLSurface(0, 0, 1056,784, gl);


    }
    /*
    * 50次刷新平均帧率，单位：毫秒ms，千分之一秒
    *
    * */
    long oldTime=0;
    List<Long> betweenTimes=null;
    int times=0;
    private void printFPS() {
        if (times==51) {
            long sum=0;
            for (long everyNum :
                    betweenTimes) {
                sum+=everyNum;
            }
            Log.d("~gyh", "printFPS: size:"+betweenTimes.size()+"  aver:"+(sum/betweenTimes.size()));
            times=0;
        }
        if (times==0) {
            betweenTimes=new ArrayList<Long>();
        }
        long  newTime= System.currentTimeMillis();
        if (oldTime==0) {
            oldTime=newTime;
        }
        long betweenTime = newTime - oldTime;
        oldTime=newTime;
        if (times!=0) {
            betweenTimes.add(betweenTime);
        }
        times++;
        Log.d("~gyh", "printFPS: betweenTime:"+betweenTime);
    }

    /*
    * surfaceTexture的onFrame……
    * */
    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        Log.d(TAG, "onFrameAvailable: ");
        this.requestRender();
    }
/*
* 设置lerp阈值
*
* */
float maxLerp=100.f;
float minLerp=50.f;
    public void setMaxLerp(float maxLerp){
        this.maxLerp=maxLerp;
    }
    public void setMinlerp(float minLerp){
        this.minLerp=minLerp;
    }

    /*
	* 设置颜色
	* */
    int selectedColorMode=0;
    public void setSelectedColorMode(int colorMode){
        this.selectedColorMode=colorMode;
    }
    /*
      * Opengles纹理矩阵缩放
      * */
    private float zoom_currMultipleTexture=1.f;
    public void setZoom_OpenglesTextureMatrix(boolean  isZoomIn){
        Matrix.scaleM(mTextureMatrix,0,  1/zoom_currMultipleTexture,  1/zoom_currMultipleTexture,1.f);//归一化
        if (isZoomIn) {
            zoom_currMultipleTexture/=2f;
        }else {
            zoom_currMultipleTexture*=2f;
            if (zoom_currMultipleTexture>=1) {
                zoom_currMultipleTexture=1.f;
            }
        }

        Log.d(TAG, "setZoom_OpenglesTextureMatrix: zoom_currMultipleTexture_shader:"+zoom_currMultipleTexture);

        Matrix.scaleM(mTextureMatrix,0,zoom_currMultipleTexture,zoom_currMultipleTexture,1.f);
    }
    /*
       * Opengles顶点矩阵缩放
       * */
    private float zoom_currMultiple=1.f;
    public void setZoom_OpenglesMatrix(boolean  isZoomIn){
        Matrix.scaleM(mMVPMatrix,0,  1/zoom_currMultiple,  1/zoom_currMultiple,1.f);//归一化
        if (isZoomIn) {
            zoom_currMultiple+=0.5f;
        }else {
            zoom_currMultiple-=0.5f;
            if (zoom_currMultiple<=1) {
                zoom_currMultiple=1.f;
            }
        }
        Matrix.scaleM(mMVPMatrix,0,zoom_currMultiple,zoom_currMultiple,1.f);
    }
    /*
    * 顶点缩放，效果与矩阵缩放差不多
    * */
    boolean isSelectedZoomUp=false;
    public void setZoomVertex(boolean isZoomIn){
        if (isZoomIn) {
            zoom_currMultiple+=0.5f;
        }else {
            zoom_currMultiple-=0.5f;
            if (zoom_currMultiple<=1) {
                zoom_currMultiple=1.f;
            }
        }
        directDriver.setVertexLevel(zoom_currMultiple);
        isSelectedZoomUp=true;
    }
    /*
    * 纹理缩放
    * */
    public void setZoomTexture(boolean isZoomIn){
        if (isZoomIn) {
            zoom_currMultiple+=0.5f;
        }else {
            zoom_currMultiple-=0.5f;
            if (zoom_currMultiple<=1) {
                zoom_currMultiple=1.f;
            }
        }


//        float level=zoomLevel*1f;
        directDriver.setTxtureCoords(zoom_currMultiple);
        isSelectedZoomUp=true;
    }

    public void viewFinish(){
        CameraInstance.getInstance().doStopPreview();
    }
    /*
    * 将其中一帧gl数据转为bitmap
    * */
    /*
	* 要注意的是，因为openGL ES framebuffer和图像通道的存储顺序不同，需要做一次ABGR到ARGB的转换。
一般来说，offscreen render的用处主要是做GPU加速，如果你的算法是计算密集型的，
可以通过一些方法将其转化成位图形式，作为纹理（texture）载入到GPU显存中，
再利用GPU的并行计算能力，对其进行处理，最后利用glReadPixels将计算结果读取到内存中。*/
    private Bitmap createBitmapFromGLSurface(int x, int y, int w, int h, GL10 gl) {

        //		Log.d("~gyh", "createBitmapFromGLSurface:w: "+w+"...h:"+h);
        int bitmapBuffer[] = new int[w * h];
        int bitmapSource[] = new int[w * h];
        IntBuffer intBuffer = IntBuffer.wrap(bitmapBuffer);
        intBuffer.position(0);
        try {
            gl.glReadPixels(x, y, w, h, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE,
                    intBuffer);
            int offset1, offset2;
            for (int i = 0; i < h; i++) {
                offset1 = i * w;
                offset2 = (h - i - 1) * w;
                for (int j = 0; j < w; j++) {
                    int texturePixel = bitmapBuffer[offset1 + j];
                    int blue = (texturePixel >> 16) & 0xff;
                    int red = (texturePixel << 16) & 0x00ff0000;
                    Log.d("~gyh", "createBitmapFromGLSurface: red::"+red);
                    int pixel = (texturePixel & 0xff00ff00) | red | blue;
                    bitmapSource[offset2 + j] = pixel;
                }
            }
        } catch (GLException e) {
            return null;
        }
        //		Log.d("~gyh", "createBitmapFromGLSurface: bitmapSource.length:"+bitmapSource.length);
        return Bitmap.createBitmap(bitmapSource, w, h, Bitmap.Config.ARGB_8888);
    }

/*
* //兰斯特设备不能运行
* */
   /* @Override
    public boolean onTouchEvent(MotionEvent event) {
        Toast.makeText(getContext(),"onTouchEvent",Toast.LENGTH_SHORT).show();
        Log.d("~gyh", "onTouchEvent: ");
        if (event.getAction()== MotionEvent.ACTION_DOWN) {
            CameraInstance.getInstance().touchFocus(event);
        }
        return  false;
    }*/
/*==================================================*/
    /*
    * 集成的手势控制
    * 内部类
    * 接口
    * 设置接口回调
    * */
    //横向偏移检测，让快进快退不那么敏感
    private int offsetX = 1;
    private boolean hasFF_REW = false,hasVolume = false;
    private static final int NONE = 0, VOLUME = 1, BRIGHTNESS = 2, FF_REW = 3;
    int mScrollMode = NONE;
    //内部类
    public class UniversalOnGestureListener extends GestureDetector.SimpleOnGestureListener {

   //private MyGLSurfaceView myGLSurfaceView;
    private static final String TAG="~gyh";
    public UniversalOnGestureListener() {
        //this.myGLSurfaceView = myGLSurfaceView;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        Log.d(TAG, "onDown: ");
        hasFF_REW = false;
        hasVolume = false;
        //每次按下都重置为NONE
        mScrollMode = NONE;
        if (universalGestureListener != null) {
            universalGestureListener.onDown(e);
        }
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        Log.d(TAG, "onScroll: e1:" + e1.getX() + "," + e1.getY());
        Log.d(TAG, "onScroll: e2:" + e2.getX() + "," + e2.getY());
        Log.d(TAG, "onScroll: X:" + distanceX + "  Y:" + distanceY);
        switch (mScrollMode) {
            case NONE:
                Log.d(TAG, "NONE: ");
                //offset是让快进快退不要那么敏感的值
                //如果满足条件则是横向滑动，不满足就是竖向滑动
                //在GPU实验中只使用VOLUME不用BR
                if (Math.abs(distanceX) - Math.abs(distanceY) > offsetX) {
                    mScrollMode = FF_REW;
                } else {
                   /* if (e1.getX() < getWidth() / 2) {
                        mScrollMode = BRIGHTNESS;
                    } else {
                        mScrollMode = VOLUME;
                    }*/
                    mScrollMode = VOLUME;
                }
                break;
            case VOLUME:
                if (universalGestureListener != null) {
                    universalGestureListener.onVolumeGesture(e1, e2, distanceX, distanceY);
                }
                hasVolume=true;
                Log.d(TAG, "VOLUME: ");
                break;
            case BRIGHTNESS:
                if (universalGestureListener != null) {
                   // universalGestureListener.onBrightnessGesture(e1, e2, distanceX, distanceY);
                }
                Log.d(TAG, "BRIGHTNESS: ");
                break;
            case FF_REW:
                if (universalGestureListener != null) {
                    universalGestureListener.onFF_REWGesture(e1, e2, distanceX, distanceY);
                }
                hasFF_REW = true;
                Log.d(TAG, "FF_REW: ");
                break;
        }
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        Log.d(TAG, "onDoubleTap: ");
        if (universalGestureListener != null) {
            universalGestureListener.onDoubleTapGesture(e);
        }
        return super.onDoubleTap(e);
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        Log.d(TAG, "onSingleTapConfirmed: ");
        if (universalGestureListener != null) {
            universalGestureListener.onSingleTapGesture(e);
        }
        return super.onSingleTapConfirmed(e);
    }

       /* @Override
        public boolean onContextClick(MotionEvent e) {
            Log.d(TAG, "onContextClick: ");
            return true;
        }*/

        @Override
        public void onLongPress(MotionEvent e) {
            Log.d(TAG, "onLongPress: ");
            super.onLongPress(e);
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            Log.d(TAG, "onDoubleTapEvent: ");
            return super.onDoubleTapEvent(e);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log.d(TAG, "onSingleTapUp: ");
            return super.onSingleTapUp(e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.d(TAG, "onFling: ");
            return super.onFling(e1, e2, velocityX, velocityY);
        }


        @Override
        public void onShowPress(MotionEvent e) {
            Log.d(TAG, "onShowPress: ");
            super.onShowPress(e);
        }
}
//接口回调
private UniversalGestureListener universalGestureListener=null;
    public void setVideoGestureListener(UniversalGestureListener universalGestureListener){
        this.universalGestureListener=universalGestureListener;
    }
    /**
     * 用于提供给外部实现的手势处理接口
     */
    public interface UniversalGestureListener {
        //亮度手势，手指在Layout左半部上下滑动时候调用
        // void onBrightnessGesture(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY);
        //音量手势，手指在Layout右半部上下滑动时候调用
         void onVolumeGesture(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY);
        //音量执行后的松开时候调用
        void onEndVolume(MotionEvent e);
        //快进快退手势，手指在Layout左右滑动的时候调用
         void onFF_REWGesture(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY);
        //快进快退执行后的松开时候调用
        void onEndFF_REW(MotionEvent e);
        //单击手势，确认是单击的时候调用
         void onSingleTapGesture(MotionEvent e);

        //双击手势，确认是双击的时候调用
         void onDoubleTapGesture(MotionEvent e);
        //按下手势，第一根手指按下时候调用
         void onDown(MotionEvent e);

    }
/*
* eyevoice找红点之类
* */
private ByteBuffer mCaptureBuffer;
    private synchronized long getBmp( GL10 gl){
       /* Bitmap bmp = createBitmapFromGLSurface(0, 0, 1056,784, gl);
        Log.d("~gyh", "run: "+bmp.getHeight());
                Point point=new Point();
                Mat mat = new Mat();
                Utils.bitmapToMat(bmp, mat);
        Log.d("~gyh", "onDrawFrame:mat rows:"+mat.rows());
        point = getCenterRedPoint(mat, point);
                Log.d("~gyh", "onDrawFrame: point:("+point.x+","+point.y+")");

        bmp.recycle();*/
        //        thread.stop();
        return System.currentTimeMillis();
    }
/*private Point getCenterRedPoint(Mat mat, Point center){

    int flag = 0;
    for (int i = 0; i < mat.height(); i = i + 4) {
        for (int j = 0; j < mat.width(); j = j + 4) {

            double b = mat.get(i, j)[2];
            double g = mat.get(i, j)[1];
            double r = mat.get(i, j)[0];
            //                if (r > 175 && r < 240 && g > 140 && b > 115 && b < 180 && g / r > 0.75 && g / b > 1.1) {
            //                if (r > 10 && r < 80 && g > 120 && b > 50 && b < 120 && g / r > 2 && g / b > 1.5) {
              *//*  if (r > 24 && r <44
                        && g > 80
                        && b > 57 && b < 97
                        && g / r > 2.6
                        && g / b > 1.24) {*//*
            Log.d("~gyh", "getCenterRedPoint: r::"+r);
            Log.d("~gyh", "getCenterRedPoint: g::"+g);
            Log.d("~gyh", "getCenterRedPoint: b::"+b);
            if(r==255&&g==0&&b==0){

                center.x = j;
                center.y = i;
                flag = 1;
                break;
            }
        }
        if (flag == 1) break;
    }
    return center;
}*/
}
