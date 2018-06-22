package com.mitnick.zoomcoloruniversal.ui;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.mitnick.zoomcoloruniversal.R;
import com.mitnick.zoomcoloruniversal.utils.CameraInstance;
import com.mitnick.zoomcoloruniversal.utils.MyGLSurfaceView;


/**
 * Created by Mitnick.Guo on 2017/10/24.
 */
public class MainActivity extends Activity implements View.OnClickListener, MyGLSurfaceView.UniversalGestureListener, SeekBar.OnSeekBarChangeListener {
    private MyGLSurfaceView glsView;
    private String TAG="~gyh";
    private ImageView myPhoto;
    private Button takePicBt,camZoomUpBt,camZoomDownBt,alterColorBt;
//    new String[]{Manifest.permission.CAMERA}
/*static {
    if (!OpenCVLoader.initDebug()) {
        System.out.println("opencv 初始化失败！");
    } else {
        System.loadLibrary("opencv_java3");
    }
}*/
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.CAMERA,
        "android.permission.CAMERA",
            "android.permission.MOUNT_UNMOUNT_FILESYSTEMS" ,
            "android.permission.MOUNT_UNMOUNT_FILESYSTEMS",

            "android.permission.WRITE_EXTERNAL_STORAGE" ,

            "android.permission.READ_EXTERNAL_STORAGE" ,

            "android.permission.ACCESS_COARSE_LOCATION" ,
            "android.permission.ACCESS_FINE_LOCATION" ,
            "android.permission.INTERNET" ,
            "android.permission.ACCESS_NETWORK_STATE",
    };
    private SeekBar maxSeekBar;
    private SeekBar minSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        //运行时权限
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,PERMISSIONS_STORAGE,1);
        }else {
//            startActivityForResult(new Intent(MainActivity.this, CaptureActivity.class),0);
        }

//
        init();
    }
    private void init() {
        glsView = ((MyGLSurfaceView)findViewById(R.id.activity_main_mGlsvId));
        glsView.setVideoGestureListener(this);

        myPhoto = ((ImageView) findViewById(R.id.activity_main_ivId));
        myPhoto.setVisibility(View.GONE);
        myPhoto.setOnClickListener(this);
        takePicBt = ((Button) findViewById(R.id.activity_main_takePicBtId));
        camZoomUpBt = ((Button) findViewById(R.id.activity_main_camZoomUpBtId));
        camZoomDownBt = ((Button) findViewById(R.id.activity_main_camZoomDownBtId));
        alterColorBt = ((Button) findViewById(R.id.activity_main_alterColorBtId));

        maxSeekBar = ((SeekBar) findViewById(R.id.activity_main_maxLerpSeekBarId));
        minSeekBar = ((SeekBar) findViewById(R.id.activity_main_minLerpSeekBarId));
        maxSeekBar.setOnSeekBarChangeListener(this);
        minSeekBar.setOnSeekBarChangeListener(this);
        takePicBt.setOnClickListener(this);
        camZoomUpBt.setOnClickListener(this);
        camZoomDownBt.setOnClickListener(this);
        alterColorBt.setOnClickListener(this);
        //用手势控制，不用按钮了
        takePicBt.setVisibility(View.GONE);
        camZoomUpBt.setVisibility(View.GONE);
        camZoomDownBt.setVisibility(View.GONE);
        alterColorBt.setVisibility(View.GONE);
    }
    private int colorMode=0;//预览模式变色
    boolean isPreviewing=true;
    float ivScale=1.0f;
    public static String curFileName=null;


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_main_takePicBtId:
                takePicOrReturn();
                break;
            case R.id.activity_main_camZoomUpBtId:
                if (isPreviewing) {
                    CameraInstance.getInstance().setZoomFromCam(true);
                }
                break;
            case R.id.activity_main_camZoomDownBtId:
                if (isPreviewing) {
                    CameraInstance.getInstance().setZoomFromCam(false);
                }
                break;
            case R.id.activity_main_alterColorBtId:
                if (isPreviewing) {
                    colorMode+=1;
                    if (colorMode==12) {//12
                        colorMode=0;
                    }
                    glsView.setSelectedColorMode(colorMode);

                }
                break;
            case R.id.activity_main_ivId:
                takePicOrReturn();
                break;
        }
    }
/*
* glsf的手势变换
* */
@Override
public void onDown(MotionEvent e) {
    //每次按下的时候更新当前亮度和音量，还有进度
  /*  oldProgress = newProgress;
    oldVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    brightness = mLayoutParams.screenBrightness;
    if (brightness == -1){
        //一开始是默认亮度的时候，获取系统亮度，计算比例值
        brightness = mBrightnessHelper.getBrightness() / 255f;
    }*/
}
    /*
    * 音量调动手势抬起
    * */
    int a=1;
    @Override
    public void onEndVolume(MotionEvent e) {
        //makeToast("设置进度为" + newProgress);
        Log.d(TAG, "onEndVolume: onEndVolume");
        //ifEndFF_REW=true;
        if (offset_Volume > 0) {
            Log.d(TAG, "onEndVolume: -->"+offset_Volume);
            if (isPreviewing) {
//                CameraInstance.getInstance().setZoomFromCam(true);//相机原生放大
//                glsView.setZoom_OpenglesMatrix(true);//矩阵放大
//                glsView.setZoomVertex(true);//顶点拉伸和矩阵效果一样
//                glsView.setZoomTexture(true);//纹理拉伸
                glsView.setZoom_OpenglesTextureMatrix(true);
            }
        }else if(offset_Volume<0) {
            Log.d(TAG, "onEndVolume: <--"+offset_Volume);
            if (isPreviewing) {
//                CameraInstance.getInstance().setZoomFromCam(false);
//                glsView.setZoom_OpenglesMatrix(false);
//                glsView.setZoomVertex(false);//顶点拉伸和矩阵效果一样
//                glsView.setZoomTexture(false);//纹理拉伸
                glsView.setZoom_OpenglesTextureMatrix(false);
            }
        }
    }
    /*
    * 快进快退手势抬起
    * */
    @Override
    public void onEndFF_REW(MotionEvent e) {
        //makeToast("设置进度为" + newProgress);
        Log.d(TAG, "onFF_REWGesture: onEndFF_REW");
        //ifEndFF_REW=true;
        if (offset_FF_REW > 0) {
            Log.d(TAG, "onFF_REWGesture: -->"+offset_FF_REW);
            if (isPreviewing) {
                colorMode+=1;
                if (colorMode==12) {//12
                    colorMode=0;
                }
                glsView.setSelectedColorMode(colorMode);
            }
        }else if(offset_FF_REW<0) {
            Log.d(TAG, "onFF_REWGesture: <--"+offset_FF_REW);
            if (isPreviewing) {
                colorMode-=1;
                if (colorMode==-1) {//12
                    colorMode=11;
                }
                glsView.setSelectedColorMode(colorMode);
            }
        }
    }
    private  float offset_Volume=0.f;
    @Override
    public void onVolumeGesture(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

        offset_Volume = e1.getY() - e2.getY();
         // Log.d(TAG, "onEndVolume: offset_Volume " + offset_Volume);
       // Log.d(TAG, "onVolumeGesture: oldVolume " + oldVolume);
        //int value = ly_VG.getHeight()/maxVolume ;
       // int newVolume = (int) ((e1.getY() - e2.getY())/value + oldVolume);

       // mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,newVolume,AudioManager.FLAG_PLAY_SOUND);


        //        int newVolume = oldVolume;

        //Log.d(TAG, "onVolumeGesture: value" + value);

        //另外一种调音量的方法，感觉体验不好，就没采用
        //        if (distanceY > value){
        //            newVolume = 1 + oldVolume;
        //            mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
        //        }else if (distanceY < -value){
        //            newVolume = oldVolume - 1;
        //            mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND);
        //        }
       // Log.d(TAG, "onVolumeGesture: newVolume "+ newVolume);

        //要强行转Float类型才能算出小数点，不然结果一直为0
        //int volumeProgress = (int) (newVolume/Float.valueOf(maxVolume) *100);
       // if (volumeProgress >= 50){
        //    scl.setImageResource(R.drawable.volume_higher_w);
       // }else if (volumeProgress > 0){
       //     scl.setImageResource(R.drawable.volume_lower_w);
       // }else {
        //    scl.setImageResource(R.drawable.volume_off_w);
       // }
       // scl.setProgress(volumeProgress);
       // scl.show();
    }



    /* @Override
     public void onBrightnessGesture(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
         //这是直接设置系统亮度的方法
         //        if (Math.abs(distanceY) > ly_VG.getHeight()/255){
         //            if (distanceY > 0){
         //                setBrightness(4);
         //            }else {
         //                setBrightness(-4);
         //            }
         //        }

         //下面这是设置当前APP亮度的方法
        *//* Log.d(TAG, "onBrightnessGesture: old" + brightness);
        float newBrightness = (e1.getY() - e2.getY()) / ly_VG.getHeight() ;
        newBrightness += brightness;

        Log.d(TAG, "onBrightnessGesture: new" + newBrightness);
        if (newBrightness < 0){
            newBrightness = 0;
        }else if (newBrightness > 1){
            newBrightness = 1;
        }
        mLayoutParams.screenBrightness = newBrightness;
        mWindow.setAttributes(mLayoutParams);
        scl.setProgress((int) (newBrightness * 100));
        scl.setImageResource(R.drawable.brightness_w);
        scl.show();*//*
    }
*/
    //这是直接设置系统亮度的方法
    private void setBrightness(int brightness) {
        //要是有自动调节亮度，把它关掉
        /*mBrightnessHelper.offAutoBrightness();

        int oldBrightness = mBrightnessHelper.getBrightness();
        Log.d(TAG, "onBrightnessGesture: oldBrightness: " + oldBrightness);
        int newBrightness = oldBrightness + brightness;
        Log.d(TAG, "onBrightnessGesture: newBrightness: " + newBrightness);
        //设置亮度
        mBrightnessHelper.setSystemBrightness(newBrightness);
        //设置显示
        scl.setProgress((int) (Float.valueOf(newBrightness)/mBrightnessHelper.getMaxBrightness() * 100));
        scl.setImageResource(R.drawable.brightness_w);
        scl.show();*/

    }

private  float offset_FF_REW=0.f;
    @Override
    public void onFF_REWGesture(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        offset_FF_REW = e2.getX() - e1.getX();
        Log.d(TAG, "onFF_REWGesture: offset " + offset_FF_REW);
    }

    /*
    * 单击，拍照
    * */
    @Override
    public void onSingleTapGesture(MotionEvent e) {
        Log.d(TAG, "onSingleTapGesture: ");
        takePicOrReturn();
       // makeToast("SingleTap");
    }

    @Override
    public void onDoubleTapGesture(MotionEvent e) {
        Log.d(TAG, "onDoubleTapGesture: ");
        //makeToast("DoubleTap");
    }

    /*
    * LerpSeekBar
    * */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.activity_main_maxLerpSeekBarId:
                glsView.setMaxLerp(progress);

                Log.d(TAG, "onProgressChanged: max progress:"+progress);
                break;
            case R.id.activity_main_minLerpSeekBarId:
                glsView.setMinlerp(progress);
                Log.d(TAG, "onProgressChanged:min  progress:"+progress);
                break;
        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    /*==================*/
    public interface GetTakePictureBitmap{
        void responseBitmap(Bitmap bitmap, String fileName);
    }
    /**
     * 选择变换
     *
     * @param origin 原图
     * @param alpha  旋转角度，可正可负
     * @return 旋转后的图片
     */
    private Bitmap rotateBitmap(Bitmap origin, float alpha) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.setRotate(alpha);

        // 围绕原地进行旋转
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
        origin.recycle();
        return newBM;
    }
    /*=============Opencv初始化*/
    @Override
    public void onResume() {
        super.onResume();

        /*if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            //            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, mLoader);
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }*/

        //        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0,this, mLoaderCallback);
        // you may be tempted, to do something here, but it's *async*, and may take some time,
        // so any opencv call here will lead to unresolved native errors.
    }

/*
* 拍照与返回
* */
    private void takePicOrReturn(){
        if (!isPreviewing) {
            Log.d(TAG, "takePicOrReturn:!isPreviewing ");
            myPhoto.setVisibility(View.GONE);
            //                    alignLeftBt.setVisibility(View.GONE);
            //                    blackIv.setVisibility(View.GONE);
            glsView.setVisibility(View.VISIBLE);
            glsView.bringToFront();
            takePicBt.bringToFront();
            camZoomUpBt.bringToFront();
            camZoomDownBt.bringToFront();
            alterColorBt.bringToFront();
            maxSeekBar.bringToFront();
            minSeekBar.bringToFront();
            //                    openCamera();
            isPreviewing = true;
            //                    scaleTo(iv_self, 1.0f);
            //                    auxiliaryLineIndex = 7;
            //                    changeAuxiliaryLine();
            //                    LedLensUtils.setBackLight(MyApplication.getBglight());
            //                    changeKeyPress();
                  /*  glsView.viewFinish();
                    finish();*/
            return;
        }
        Log.d(TAG, "takePicOrReturn:isPreviewing ");
        isPreviewing=false;
        glsView.setVisibility(View.GONE);
        CameraInstance.getInstance().doTakePicture(MainActivity.this,new GetTakePictureBitmap() {
            @Override
            public void responseBitmap(Bitmap bitmap, String fileName) {

                       /* if (iv!=null) {
                            frameLayout.removeView(iv);
                            iv=null;
                            iv=new ImageViewSelf(MainButtonActivity.this);
                            FrameLayout.LayoutParams tparams=new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT);//定义显示组件参数
                            frameLayout.addView(iv,tparams);
                            iv.setVisibility(View.VISIBLE);
                        }*/
                //                        UnityPlayer.UnitySendMessage("LeftEye", "GetTakeImagePath", fileName);//这里注意更换
                curFileName=fileName;
                //                        getBitmap=bitmap;
                //得到bitmap是异步的，只能在回调中对myPhoto贴图
                myPhoto.setVisibility(View.VISIBLE);
//                Bitmap bitmap1 = rotateBitmap(bitmap, 90);
                myPhoto.setImageBitmap(bitmap);
                //                        myPhoto.setImageBitmap(getBitmap);
            }
        });
    }
    /*
    * 按键
    * */
   /* @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyDown: keyCode:"+keyCode);
        Log.d(TAG, "onKeyDown: event:"+event.toString());

        switch (keyCode)
        {
            //前后
            case 22:
                if (isPreviewing) {
                    CameraInstance.getInstance().setZoomFromCam(true);
                }
                break;
            case 21:
                if (isPreviewing) {
                    CameraInstance.getInstance().setZoomFromCam(false);
                }
                break;
            //OK
            case 66:
                if (!isPreviewing) {
                    glsView.viewFinish();
                    finish();
                    break;
                }
                isPreviewing=false;
                glsView.setVisibility(View.GONE);
                CameraInstance.getInstance().doTakePicture(MainActivity.this,new GetTakePictureBitmap() {
                    @Override
                    public void responseBitmap(Bitmap bitmap, String fileName) {

                       *//* if (iv!=null) {
                            frameLayout.removeView(iv);
                            iv=null;
                            iv=new ImageViewSelf(MainButtonActivity.this);
                            FrameLayout.LayoutParams tparams=new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT);//定义显示组件参数
                            frameLayout.addView(iv,tparams);
                            iv.setVisibility(View.VISIBLE);
                        }*//*
                        //                        UnityPlayer.UnitySendMessage("LeftEye", "GetTakeImagePath", fileName);//这里注意更换
                        curFileName=fileName;
                        //                        getBitmap=bitmap;
                        //得到bitmap是异步的，只能在回调中对myPhoto贴图
                        myPhoto.setVisibility(View.VISIBLE);
                        myPhoto.setImageBitmap(bitmap);
                        //                        myPhoto.setImageBitmap(getBitmap);
                    }
                });
                break;
            //ok键长按
            case 4:
                if (isPreviewing) {
                    colorMode+=1;
                    if (colorMode==12) {//12
                        colorMode=0;
                    }
                    glsView.setSelectedColorMode(colorMode);
                }
                break;
        }
       *//* switch (keyCode) {
            //ok
            case 66:
                if (!isPreviewing) {
                    glsView.viewFinish();
                    finish();
                    break;
                }
                isPreviewing=false;
                glsView.setVisibility(View.GONE);
                CameraInstance.getInstance().doTakePicture(MainActivity.this,new GetTakePictureBitmap() {
                    @Override
                    public void responseBitmap(Bitmap bitmap, String fileName) {

                       *//**//* if (iv!=null) {
                            frameLayout.removeView(iv);
                            iv=null;
                            iv=new ImageViewSelf(MainButtonActivity.this);
                            FrameLayout.LayoutParams tparams=new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT);//定义显示组件参数
                            frameLayout.addView(iv,tparams);
                            iv.setVisibility(View.VISIBLE);
                        }*//**//*
//                        UnityPlayer.UnitySendMessage("LeftEye", "GetTakeImagePath", fileName);//这里注意更换
                        curFileName=fileName;
                        //                        getBitmap=bitmap;
                        //得到bitmap是异步的，只能在回调中对myPhoto贴图
                        myPhoto.setVisibility(View.VISIBLE);
                        myPhoto.setImageBitmap(bitmap);
                        //                        myPhoto.setImageBitmap(getBitmap);
                    }
                });
                break;
           *//**//* //上下左右
            case 19:
                if (isPreviewing) {
                    CameraInstance.getInstance().setZoomFromCam(true);
                }
                break;
            case 20:
                if (isPreviewing) {
                    CameraInstance.getInstance().setZoomFromCam(false);
                }
                break;
            case 21:
                if (isPreviewing) {
                    colorMode-=1;
                    if (colorMode==-1) {//12
                        colorMode=11;
                    }
                    glsView.setSelectedColorMode(colorMode);
                }
                break;
            case 22:
                if (isPreviewing) {
                    colorMode+=1;
                    if (colorMode==12) {//12
                        colorMode=0;
                    }
                    glsView.setSelectedColorMode(colorMode);
                }
                break;
            //菜单menu
            case 82:
                break;
            //返回return
            case 4:
                if (!isPreviewing) {
                    isPreviewing=true;
                    ivScale=1.0f;
                    glsView.setVisibility(View.VISIBLE);
                    myPhoto.setImageBitmap(null);
                    myPhoto.setVisibility(View.GONE);
                }else if(isPreviewing){
                    //如果归位，退出应用
                    if(colorMode==0&& CameraInstance.getInstance().getParameters().getZoom()==0){
//                        UnityPlayer.UnitySendMessage("LeftEye", "CloseProgramFlag", "CLOSE");
                        glsView.viewFinish();
                        finish();
                    }
                    else {
                        //颜色归位
                        colorMode=0;
                        glsView.setSelectedColorMode(colorMode);
                        //比例归位
                        CameraInstance.getInstance().setZoomFromCam(0);
                    }
                }
                break;
            //左边小键
            case 57:
                break;
            //右边小键
            case 130:
                break;
            //右边系统音量控制长键上下
            case 24:
                break;
            case 25:
                break;*//**//*
        }*//*
        return  true;
    }*/
}
