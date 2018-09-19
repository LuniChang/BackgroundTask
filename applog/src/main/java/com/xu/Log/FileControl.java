package com.xu.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

/**
 * Created by xq on 2015/1/6.
 */
public class FileControl {

	public static boolean copyFile(String srcpath, String outpath) {
		try {
			boolean isSameFile=srcpath.equals(outpath);
				
			
			
			FileInputStream fin = new FileInputStream(srcpath);
			FileOutputStream fout = null;
			String tmpPath=outpath+".tmp";
			if(isSameFile){
				fout=new FileOutputStream(tmpPath);
			}else{
				fout=new FileOutputStream(outpath);
			}
				
			
			byte[] data=new byte[1024];
			while(fin.read(data)!=-1){
				fout.write(data);
			}
			
			fout.flush();
			fout.close();
			fin.close();
			
			
			if(isSameFile){
				File tmpFile=new File(tmpPath);
				File outFile=new File(outpath);
				tmpFile.renameTo(outFile);
			}
			
			return true;
		} catch (IOException e) {
			Log.e("filecontrol", e.toString());
			return false;
		}
	}

	public static boolean saveStringToFile(String string, String path) {
		try {
			File f = new File(path);
			if (!f.getParentFile().exists()) {
				f.mkdirs();
			}
			string = new String(string.getBytes(), "UTF-8");
			FileOutputStream fout = new FileOutputStream(f);
			fout.write(string.getBytes());
			fout.flush();
			fout.close();
			return true;
		} catch (IOException e) {
			Log.e("filecontrol","IOException" + e.toString());
			return false;
		}
	}

	public static boolean saveByteToFile(byte[] bt, String path) {
		try {
			File f = new File(path);
			if (!f.getParentFile().exists()) {
				f.mkdirs();
			}
			FileOutputStream fout = new FileOutputStream(f);
			fout.write(bt);
			fout.flush();
			fout.close();
			return true;
		} catch (IOException e) {
			Log.e("filecontrol","IOException:saveByteToFile" + e.toString());
			return false;
		}
	}

	
	public static void makeLostPath(String path){
		File f = new File(path);
		if (!f.getParentFile().exists()) {
			f.getParentFile().mkdirs();
		}
	}
	
	
	public static String loadFileString(String path) {
		try {
			File f = new File(path);
			if (!f.getParentFile().exists()) {
				f.getParentFile().mkdirs();
			}
			FileInputStream fin = new FileInputStream(f);

			InputStreamReader read = new InputStreamReader(fin, "UTF-8");
			BufferedReader bufferedReader = new BufferedReader(read);
			String lineTxt = null;

			String str = "";
			while ((lineTxt = bufferedReader.readLine()) != null) {
				str = str + lineTxt;
			}
			read.close();

			fin.close();
			return str;
		} catch (IOException e) {
			Log.e("XU", "IOException" + e.toString());
			return null;
		}

	}

	/**
	 * 判断文件类型
	 * 
	 * @param filename
	 * @param type
	 * @return
	 */
	public static boolean isCheckFileType(String filename, String type) {

		int index = filename.lastIndexOf(".");

		if (index >= 0) {
			String pathType = filename.substring(index + 1).toLowerCase();
			if (pathType.equals(type))
				return true;
		}

		return false;

	}

	/**
	 * 没有/
	 * 
	 * @return
	 */
	public static String getSDcardPath() {
		Environment.getExternalStorageState();
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();
		} else {
			return "/mnt/sdcard";
		}

		return sdDir.toString();
	}

	public static String getMd5ByFile(String filePath) {
		File file = new File(filePath);
		if (!file.exists()) {
			return null;
		}
		String value = null;
		FileInputStream in = null;
		try {
			in = new FileInputStream(file);
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			byte[] buffer = new byte[4 * 1024];
			int length;
			while ((length = in.read(buffer)) != -1) {
				md5.update(buffer, 0, length);
			}

			value = new String(md5.digest());
		} catch (Exception e) {

		} finally {
			if (null != in) {
				try {
					in.close();
				} catch (IOException e) {

				}
			}
		}
		return value;
	}

	public static void savaStringToFileEnd(String fileName, String content) {

		File writeFile = new File(fileName);
		if (!writeFile.getParentFile().exists()) {
			writeFile.getParentFile().mkdirs();
		}

		RandomAccessFile randomFile = null;
		try {
			// 打开一个随机访问文件流，按读写方式
			randomFile = new RandomAccessFile(fileName, "rw");
			// 文件长度，字节数
			long fileLength = randomFile.length();
			// 将写文件指针移到文件尾。
			randomFile.seek(fileLength);
			randomFile.write(content.getBytes("UTF-8"));
		} catch (IOException e) {

		} finally {
			if (randomFile != null) {
				try {
					randomFile.close();
				} catch (IOException e) {

				}
			}
		}
	}

	public static SimpleDateFormat timeformat = new SimpleDateFormat(
			"yyyy-MM-dd H:m:s:SSS");

	public static void logToFile(String msg) {
		// 使用pattern
		FileControl.savaStringToFileEnd(FileControl.getAPPRootPath()
				+ "down/log.txt",
				timeformat.format(Calendar.getInstance().getTime()) + ": "
						+ msg + "\r\n");
	}

	/**
	 * 比较大小 -1 小 0相同 1大于
	 * 
	 * @param path
	 *            源文件路径
	 * @param size
	 *            比较大小
	 * @return
	 */
	public static int compareFileSize(String path, long size) {

		File file = new File(path);
		if (file.length() > size)
			return 1;
		else if (file.length() < size)
			return -1;

		return 0;
	}

	/**
	 * 拷贝项目assets下的文件
	 * 
	 * @param context
	 * @param assetFilename
	 *            assets的文件名
	 * @param filePath
	 *            保存到手机的文件路径
	 * @return
	 */
	public static boolean copyAssetFile(Context context, String assetsFilename,
			String filePath) {
		try {

			InputStream inputStream = context.getResources().getAssets()
					.open(assetsFilename);
			FileOutputStream outputStream = new FileOutputStream(filePath);

			while (true) {
				byte data[] = new byte[1024 * 4];
				if (inputStream.read(data) != -1) {
					outputStream.write(data);
				} else {
					break;
				}
			}
			outputStream.flush();
			outputStream.close();
			inputStream.close();

			return true;
		} catch (IOException e) {
			Log.e("filecontrol",e.toString());
		}
		return false;

	}

	public static boolean checkFileExist(String filePath) {

		boolean result = false;

		File file = new File(filePath);

		if (file.exists()) {
			result = true;
		} else {
			result = false;
		}

		return result;
	}

	public static boolean deleteFileAndSub(File dir) {
		for (File file : dir.listFiles()) {
			if(file.isDirectory()){
				deleteFileAndSub(file);
			}
			file.delete();
		}
		return dir.delete();
	}

	@SuppressLint("SdCardPath")
	public static String getAPPRootPath() {
		Environment.getExternalStorageState();
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();
		} else {
			return "/mnt/sdcard/xu/";
		}

		return sdDir.toString() + "/xu/";
	}

	private static String setting_params[] = null;

	public static String getAppSettingParam(int index) {
		if (setting_params == null) {
			String settingStr = FileControl.loadFileString(getAPPRootPath()
					+ "setting.txt");
			if (settingStr != null && !"".equals(settingStr)) {
				setting_params = settingStr.split(",");
			}
		}
		if (setting_params != null && setting_params.length > index
				&& index > -1) {
			return setting_params[index];
		}

		return null;
	}

	public static void  initAppSettingParam(Context context){
			if(setting_params==null){
				String settingStr=FileControl.loadFileString(getAPPRootPath()+"setting.txt");
				if(settingStr==null){
					try{
						InputStream inputStream = context.getResources().getAssets().open("setting.txt");
						InputStreamReader inputStreamReader=new InputStreamReader(inputStream, "UTF-8");
					
					BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
					String readLineStr=null;

					String str="";
					while ((readLineStr = bufferedReader.readLine()) != null) {
						str = str + readLineStr;
					}
					
					settingStr=str;
					bufferedReader.close();
					inputStream.close();
					
					} catch (IOException e) {
				        	Log.e("XU","initAppSettingParam:" + e.toString());
				     }
					
				}
				
				if(settingStr!=null&&!"".equals(settingStr)){
					setting_params= settingStr.split(",");
				}
			}
			
		}

	/* Checks if external storage is available for read and write */
	public static boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}

	/* Checks if external storage is available to at least read */
	public static boolean isExternalStorageReadable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)
				|| Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			return true;
		}
		return false;
	}

}
