package com.mitnick.zoomcoloruniversal.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;

import com.mitnick.zoomcoloruniversal.ui.MainActivity;

import java.io.IOException;

/**
 * Created by Mitnick.Guo on 2017/6/7.
 */
public class CameraInstance {
    private static CameraInstance cameraInstance;

    private Camera camera;
    private Camera.Parameters parameters;
    private boolean isPreviewing=false;
    private CameraInstance() {
    }
    public static synchronized CameraInstance getInstance(){
        if (cameraInstance==null) {
            cameraInstance=new CameraInstance();
        }
        return cameraInstance;
    }
    /*
    * 10-24 10:43:23.315 6432-6461/com.mitnick.nedglass I/~gyh: previewSizes:width = 176 height = 144
10-24 10:43:23.315 6432-6461/com.mitnick.nedglass I/~gyh: previewSizes:width = 320 height = 240
10-24 10:43:23.315 6432-6461/com.mitnick.nedglass I/~gyh: previewSizes:width = 352 height = 288
10-24 10:43:23.315 6432-6461/com.mitnick.nedglass I/~gyh: previewSizes:width = 640 height = 480
10-24 10:43:23.315 6432-6461/com.mitnick.nedglass I/~gyh: previewSizes:width = 720 height = 480
10-24 10:43:23.315 6432-6461/com.mitnick.nedglass I/~gyh: previewSizes:width = 800 height = 600
10-24 10:43:23.316 6432-6461/com.mitnick.nedglass I/~gyh: previewSizes:width = 1280 height = 720
10-24 10:43:23.316 6432-6461/com.mitnick.nedglass I/~gyh: previewSizes:width = 1920 height = 1080
10-24 10:43:23.316 6432-6461/com.mitnick.nedglass I/~gyh: previewSizes:width = 3264 height = 2448
10-24 10:43:23.316 6432-6461/com.mitnick.nedglass I/~gyh: previewSizes:width = 1920 height = 1080

10-24 10:43:23.316 6432-6461/com.mitnick.nedglass I/~gyh: pictureSizes:width = 3264 height = 2448
10-24 10:43:23.316 6432-6461/com.mitnick.nedglass I/~gyh: pictureSizes:width = 2592 height = 1944
10-24 10:43:23.316 6432-6461/com.mitnick.nedglass I/~gyh: pictureSizes:width = 2048 height = 1536
10-24 10:43:23.316 6432-6461/com.mitnick.nedglass I/~gyh: pictureSizes:width = 1600 height = 1200
10-24 10:43:23.316 6432-6461/com.mitnick.nedglass I/~gyh: pictureSizes:width = 640 height = 480
10-24 10:43:23.316 6432-6461/com.mitnick.nedglass I/~gyh: pictureSizes:width = 352 height = 288
10-24 10:43:23.316 6432-6461/com.mitnick.nedglass I/~gyh: pictureSizes:width = 320 height = 240
10-24 10:43:23.316 6432-6461/com.mitnick.nedglass I/~gyh: pictureSizes:width = 176 height = 144
10-24 10:43:23.316 6432-6461/com.mitnick.nedglass I/~gyh: focusModes--fixed
10-24 10:43:23.316 6432-6461/com.mitnick.nedglass I/~gyh: focusModes--auto
10-24 10:43:23.316 6432-6461/com.mitnick.nedglass I/~gyh: focusModes--continuous-picture
10-24 10:43:23.318 6432-6461/com.mitnick.nedglass D/~gyh: initCamera: width:1920,height:1080
10-24 10:43:23.318 6432-6461/com.mitnick.nedglass D/~gyh: initCamera: width:3264,height:2448
    * */

    public void initCamera(){
        if (camera!=null) {
                parameters=camera.getParameters();
                parameters.setPictureFormat(PixelFormat.JPEG);
                Log.d("~gyh", "initCamera: ");
//                CamParaUtil.getInstance().printSupportPreviewSize(parameters);
//                CamParaUtil.getInstance().printSupportPictureSize(parameters);
//                CamParaUtil.getInstance().printSupportFocusMode(parameters);
            parameters.setPreviewSize(1920,1080);
           parameters.setPictureSize(1920,1080);
//                parameters.setPictureSize(1600 ,1200);
//                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
//                            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                           // parameters.setPreviewFpsRange(30000,60000);
//                camera.setDisplayOrientation(90);
            //parameters.setZoom(70);
            Log.d("~Gyh", "initCamera:getZoom "+  parameters.getZoom());
            Log.d("~Gyh", "initCamera: getMaxZoom"+  parameters.getMaxZoom());
                camera.setParameters(parameters);


            //Log.d("~gyh", "initCamera: width:"+parameters.getPreviewSize().width+",height:"+parameters.getPreviewSize().height);
            //Log.d("~gyh", "initCamera: width:"+parameters.getPictureSize().width+",height:"+parameters.getPictureSize().height);
        }
    }


    public void doOpenCamera(){
        try {
            Log.d("~gyh", "getNumberOfCameras: "+ Camera.getNumberOfCameras());
            if (camera==null) {
                try {
//                    camera= Camera.open(1);
//                    camera= Camera.open();
                    camera=  Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);



                }catch (Exception e){
                    Log.d("~gyh", "doOpenCamera: open ERROR");
                }
            }
        }
        catch (Exception e){
            Log.d("~gyh", "doOpenCamera: camera!=null");
            doStopPreview();
        }

    }
    /*
    * 普通surfaceView显示
    * */
    public void doStartPreview(SurfaceHolder holder){
        if (isPreviewing) {
            camera.stopPreview();
            return;
        }
        if (camera!=null) {
            try {
                camera.setPreviewDisplay(holder);
            } catch (IOException e) {
                e.printStackTrace();
            }
//            initCamera();
            camera.startPreview();
            camera.cancelAutoFocus();//加上这句实现连续自动对焦
            isPreviewing=true;
        }
    }
    /*
    * OpenGLES
    * */
    public void doStartPreview(SurfaceTexture surfaceTexture){
        if (isPreviewing) {
            camera.stopPreview();
            return;
        }
        if (camera!=null) {
            try {
                camera.setPreviewTexture(surfaceTexture);
            } catch (IOException e) {
                e.printStackTrace();
            }
//            initCamera();
            parameters=camera.getParameters();
            camera.startPreview();
            camera.cancelAutoFocus();//加上这句实现连续自动对焦
            isPreviewing=true;
        }
    }
    public void doStopPreview(){
        if(null!=camera)
        {
            camera.stopPreview();
            isPreviewing=false;
            camera.release();
            camera=null;
        }
    }
    public boolean isPreviewing(){
        return isPreviewing;
    }
    //兰斯特设备不能运行
    /*public void touchFocus(MotionEvent event){
        camera.cancelAutoFocus();
        parameters.setFocusMode(Camera.Parameters.FLASH_MODE_AUTO);
        camera.setParameters(parameters);
        camera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                if (success) {
                    Log.d("~gyh", "onAutoFocus: success");
                }else {
                    Log.d("~gyh", "onAutoFocus: fail");
                }
            }
        });
    }*/
/*
* 相机自带放大缩小
* */
    public void setZoomFromCam(boolean isZoomIn) {
        //        boolean isZoomIn=true;
        Log.d("~gyh", "setZoomFromCam:aaa isZoomIn:"+isZoomIn);
        Camera.Parameters parameters=camera.getParameters();
        Log.d("~gyh", "setZoomFromCam:  int maxZoom :"+ parameters.getMaxZoom());
        if (parameters.isZoomSupported()) {
            int maxZoom = parameters.getMaxZoom();
            int zoom = parameters.getZoom();
            Log.d("~gyh", "setZoomFromCam: maxZoom:"+ maxZoom);
            if (isZoomIn) {
                Log.d("~gyh", "setZoomFromCam:  isZoomIn:"+ isZoomIn);
                if (zoom!=maxZoom) {
                    zoom+=2;
                    Log.d("~gyh", "setZoomFromCam:  zoomaaa:"+ zoom);
                }
            }else {
                if (zoom!=0) {
                    zoom-=2;
                }
            }
            Log.d("~gyh", "setZoomFromCam:zoom:"+zoom);
            parameters.setZoom(zoom);
            camera.setParameters(parameters);
            Log.d("~gyh", "setZoomFromCam:    parameters.getZoom()"+   parameters.getZoom());
//            CameraInstance.getInstance().doStopPreview();
//            CameraInstance.getInstance().doOpenCamera();
//
//            CameraInstance.getInstance().doStartPreview(surfaceTexture);
        } else {
            Log.i("~gyh", "zoom not supported");
        }
    }
/*
* 相机自带放大缩小
* */
    public void setZoomFromCam1(boolean isZoomIn) {
        if (parameters.isZoomSupported()) {
            int maxZoom = parameters.getMaxZoom();
            int zoom = parameters.getZoom();
            if (isZoomIn) {
                if (zoom!=maxZoom) {
                    zoom+=2;
                }
            }else {
                if (zoom!=0) {
                    zoom-=2;
                }
            }
            Log.d("~gyh", "setZoomFromCam:zoom:"+zoom);
            parameters.setZoom(zoom);
            camera.setParameters(parameters);

            Log.d("~gyh", "setZoomFromCam:    parameters.getZoom()"+   parameters.getZoom());
        } else {
            Log.i("~gyh", "zoom not supported");
        }
    }

    public void setZoomFromCam(int zoomInt) {
        if (parameters.isZoomSupported()) {
            parameters.setZoom(zoomInt);
            camera.setParameters(parameters);
        } else {
            Log.i("~gyh", "zoom not supported");
        }
    }

    public Camera.Parameters getParameters() {
        return parameters;
    }
public void setPreviewCallback(Camera.PreviewCallback previewCallback){
    camera.setPreviewCallback(previewCallback);
}
    public void doTakePicture(final Context context, final MainActivity.GetTakePictureBitmap getTakePictureBitmap){
       if (camera!=null&&isPreviewing) {
           camera.takePicture(null, null, new Camera.PictureCallback() {
               @Override
               public void onPictureTaken(byte[] data, Camera camera) {
                   Bitmap bitmap=null;
                   if (isPreviewing()) {
                       doStopPreview();
                   }
                   if (null!=data) {
                        bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                   }
                   if(null!=bitmap){
                       //FileUtil fileUtil=new FileUtil();
                       //String fileName=fileUtil.saveBitmap(bitmap,context);//fileName
                      // Log.d("~gyh", "CamIn: "+fileName);
                       getTakePictureBitmap.responseBitmap(bitmap,null);
                   }
               }
           });
       }
   }
/*
* 7寸相机参数
* */
    //0小分辨率 2大分辨率
public void switch_binmode(int mode) {
    if (camera != null) {
      camera.switch_binmod(mode);
    }
}

    //0自动曝光  1固定曝光
    public void switch_exp_mode(int mode) {
        if (camera != null) {

            camera.switch_exp_mode(mode);
        }
    }
}
