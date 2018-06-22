package com.mitnick.zoomcoloruniversal.textureview;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.mitnick.zoomcoloruniversal.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main3ActivityForTextureView extends Activity implements TextureView.SurfaceTextureListener, View.OnClickListener {

//    private TextureView myTexture;
    private Camera mCamera;
    private Button zoomUpBt,zoomDownBt;
    private TextureView textureView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main3_activity_for_texture_view);
//        myTexture = new TextureView(this);
//        myTexture.setSurfaceTextureListener(this);
       // setContentView(myTexture);
        initView();
    }

    private void initView() {
        textureView = ((TextureView) findViewById(R.id.main3_tvId));
        textureView.setSurfaceTextureListener(this);
        zoomUpBt = ((Button) findViewById(R.id.main3_zoomUpBtId));
        zoomDownBt = ((Button) findViewById(R.id.main3_zoomDownBtId));
        zoomUpBt.setOnClickListener(this);
        zoomDownBt.setOnClickListener(this);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
        mCamera = Camera.open();
        Camera.Size previewSize = mCamera.getParameters().getPreviewSize();
    /*    textureView.setLayoutParams(new RelativeLayout.LayoutParams(
                previewSize.width, previewSize.height));*/
        try {
            mCamera.setPreviewTexture(surfaceTexture);
        } catch (IOException t) {
        }
        mCamera.startPreview();
        textureView.setAlpha(1.0f);
//        myTexture.setRotation(90.0f);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        mCamera.stopPreview();
        mCamera.release();
        return true;
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
//        printFPS();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main3_zoomUpBtId:
                setZoomFromCam(true);
                break;

            case R.id.main3_zoomDownBtId:
                setZoomFromCam(false);
                break;
        }

    }

    /*
* 相机自带放大缩小
* */
    public void setZoomFromCam(boolean isZoomIn) {
//        boolean isZoomIn=true;
        Log.d("~gyh", "setZoomFromCam:aaa isZoomIn:"+isZoomIn);
        Camera.Parameters parameters=mCamera.getParameters();
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
            mCamera.setParameters(parameters);

            Log.d("~gyh", "setZoomFromCam:    parameters.getZoom()"+   parameters.getZoom());
        } else {
            Log.i("~gyh", "zoom not supported");
        }
    }
}
