package com.mitnick.zoomcoloruniversal.utils;

import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.Log;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CamParaUtil {
	private static final String TAG = "~gyh";
	private CameraSizeComparator sizeComparator = new CameraSizeComparator();
	private static CamParaUtil myCamPara = null;
	private CamParaUtil(){

	}
	public static CamParaUtil getInstance(){
		if(myCamPara == null){
			myCamPara = new CamParaUtil();
			return myCamPara;
		}
		else{
			return myCamPara;
		}
	}

	public Size getPropPreviewSize(List<Size> list, float th, int minWidth){
		Collections.sort(list, sizeComparator);
		int i = 0;
		for(Size s:list){
			if((s.width >= minWidth) && equalRate(s, th)){
				Log.i("~gyh", "PreviewSize:w = " + s.width + "h = " + s.height);
				break;
			}
			i++;
		}
		if(i == list.size()){
			i = 0;//如果没找到，就选最小的size
		}
		return list.get(i);
	}
	public Size getPropPictureSize(List<Size> list, float th, int minWidth){
		Collections.sort(list, sizeComparator);

		int i = 0;
		for(Size s:list){
			if((s.width >= minWidth) && equalRate(s, th)){
				Log.i("~gyh", "PictureSize : w = " + s.width + "h = " + s.height);
				break;
			}
			i++;
		}
		if(i == list.size()){
			i = 0;//如果没找到，就选最小的size
		}
		return list.get(i);
	}

	/*
	* 近似相等？
	* */
	public boolean equalRate(Size s, float rate){
//		Log.d("~gyh", "equalRate: ");
		float r = (float)(s.width)/(float)(s.height);
		if(Math.abs(r - rate) <= 0.03)
		{
			return true;
		}
		else{
			return false;
		}
	}

	public  class CameraSizeComparator implements Comparator<Size> {
		public int compare(Size lhs, Size rhs) {
			// TODO Auto-generated method stub
			if(lhs.width == rhs.width){
				return 0;
			}
			else if(lhs.width > rhs.width){
				return 1;
			}
			else{
				return -1;
			}
		}

	}

	/**打印支持的previewSizes
	 * @param params
	 *7#: previewSizes:width = 4224 height = 3136
	05-14 08:36:08.600 23628-23655/org.yanzi.playcamera_v3 I/yanzi: previewSizes:width = 3840 height = 2160
	05-14 08:36:08.600 23628-23655/org.yanzi.playcamera_v3 I/yanzi: previewSizes:width = 2112 height = 1568
	05-14 08:36:08.610 23628-23655/org.yanzi.playcamera_v3 I/yanzi: previewSizes:width = 1296 height = 720
	 */
	public  void printSupportPreviewSize(Camera.Parameters params){
		List<Size> previewSizes = params.getSupportedPreviewSizes();
		for(int i=0; i< previewSizes.size(); i++){
			Size size = previewSizes.get(i);
			Log.i("~gyh", "previewSizes:width = "+size.width+" height = "+size.height);
		}
	
	}

	/**打印支持的pictureSizes
	 * @param params
	 * 7#:pictureSizes:width = 4224 height = 3136
	05-14 08:36:08.600 23628-23655/org.yanzi.playcamera_v3 I/yanzi: pictureSizes:width = 3840 height = 2160
	05-14 08:36:08.600 23628-23655/org.yanzi.playcamera_v3 I/yanzi: pictureSizes:width = 1920 height = 1080
	 */
	public  void printSupportPictureSize(Camera.Parameters params){
		List<Size> pictureSizes = params.getSupportedPictureSizes();
		for(int i=0; i< pictureSizes.size(); i++){
			Size size = pictureSizes.get(i);
			Log.i(TAG, "pictureSizes:width = "+ size.width
					+" height = " + size.height);
		}
	}
	/**打印支持的聚焦模式
	 * @param params
	 * 7#:focusModes--auto
	05-14 08:36:08.610 23628-23655/org.yanzi.playcamera_v3 I/yanzi: focusModes--infinity
	05-14 08:36:08.610 23628-23655/org.yanzi.playcamera_v3 I/yanzi: focusModes--macro
	05-14 08:36:08.610 23628-23655/org.yanzi.playcamera_v3 I/yanzi: focusModes--fixed
	 */
	public void printSupportFocusMode(Camera.Parameters params){
		List<String> focusModes = params.getSupportedFocusModes();
		for(String mode : focusModes){
			Log.i(TAG, "focusModes--" + mode);
		}
	}
}
