package com.mitnick.zoomcoloruniversal.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileUtil {
	private static final String TAG = "~gyh_FileUtil";
//	private static final File parentPath = Environment.getExternalStorageDirectory();
//	private static   String storagePath = "";
//	private static final String DST_FOLDER_NAME = "PlayCamera";

	/**初始化保存路径
	 * @return
	 */
	/*private static String initPath(){
		if(storagePath.equals("")){
			storagePath = parentPath.getAbsolutePath()+"/" + DST_FOLDER_NAME;
			File f = new File(storagePath);
			if(!f.exists()){
				f.mkdir();
			}
		}
		return storagePath;
	}
*/
	/**保存Bitmap到sdcard
	 * @param b
	 */
	Bitmap mBitmap;
	Context context;
	String fileName;
	public String saveBitmap(Bitmap b, Context context){
		this.context=context;
		this.mBitmap=b;
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		 fileName="IMG_" + timeStamp + ".jpg";//返回给调用者
		Log.d(TAG, "saveBitmap: "+fileName);
		new AyncySavePictre().execute();
		return fileName;
		/*String path = initPath();
		long dataTake = System.currentTimeMillis();
		String jpegName = path + "/" + dataTake +".jpg";
		Log.i(TAG, "saveBitmap:jpegName = " + jpegName);
		try {

//			File mPictureFile = getOutputMediaFile();
//			FileOutputStream fout = new FileOutputStream(mPictureFile);
			FileOutputStream fout = new FileOutputStream(jpegName);



			BufferedOutputStream bos = new BufferedOutputStream(fout);
//			bos.write(bitmapTobyte(b));
			b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			bos.flush();
			bos.close();
			Log.i("~gyh", "saveBitmap成功");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.i("~gyh", "saveBitmap:失败");
			e.printStackTrace();
		}
*/
	}
	/**
	 * 异步保存图片工具内部类
	 */
	class AyncySavePictre extends AsyncTask<String ,String,String> {
		public AyncySavePictre() {

		}
		@Override
		protected String doInBackground(String... params) {
			File mPictureFile = null;

			try {
				mPictureFile = getOutputMediaFile();
				FileOutputStream mFileout = new FileOutputStream(mPictureFile);
				mFileout.write(bitmapTobyte());
				mFileout.flush();
				mFileout.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			if(mPictureFile != null) {
				Intent mediaScanIntent = new Intent(
						Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
				mediaScanIntent.setData(Uri.fromFile(mPictureFile));
				context.sendBroadcast(mediaScanIntent);
			}
			return null;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(String s) {
			Log.d("~gyh", "onPostExecute: Successfully");
//			MyToast.makeTextAndSizeColor(getApplicationContext() , "Save Successfully",50 , Color.YELLOW, Toast.LENGTH_LONG).show();
			super.onPostExecute(s);
		}
	}
	/**
	 *输出媒体文件
	 */
	public File getOutputMediaFile() {
		File mediaStorageDir = new File(
				Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"Camera");//创建Camera路径
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				return null;
			}
		}

		// Create a media file name
//		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//
//		String fileName="IMG_" + timeStamp + ".jpg";//返回给调用者
		File mediaFile;
		//File.separator文件路径分隔符，考虑到跨平台，这样使用，而不是直接使用/或者\
		mediaFile = new File(mediaStorageDir.getPath() + File.separator
				+ fileName);
		Log.d(TAG, "getOutputMediaFile: "+mediaFile.getAbsolutePath());
		return mediaFile;
	}

	/**
	 *图片转byte[]
	 */
	public byte[] bitmapTobyte() {
		ByteArrayOutputStream mByteArray = new ByteArrayOutputStream();
		mBitmap.compress(Bitmap.CompressFormat.JPEG , 100 , mByteArray);
		return mByteArray.toByteArray();
	}


}
