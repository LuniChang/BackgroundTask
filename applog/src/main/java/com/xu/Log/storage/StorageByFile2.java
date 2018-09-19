package com.xu.Log.storage;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.xu.Log.FileControl;
import com.xu.Log.LogInfo;
import com.xu.Log.TimeTools;

import android.util.Log;

public class StorageByFile2 implements Storage {

	protected  BlockingQueue<LogInfo> logQueue;

	protected  Thread logThread;

	private static Storage instance;

	protected  boolean runFlag = true;

	protected final static int QUEUE_SIZE = 3000;
	protected static final String logDirRootPath = FileControl.getAPPRootPath()
			+ "log/";
	protected static final String TAG = "XU";

	/**
	 * log保存线程
	 * 
	 * @author Xuchang
	 *
	 */
	protected class LogStorageThread extends Thread {

		@Override
		public void run() {
			try {

				while (runFlag) {
					String logDirname = TimeTools.longToDateOnDay(System
							.currentTimeMillis());
					String logDirPath = logDirRootPath + logDirname;
					File logFileDir = new File(logDirPath);
					if (!logFileDir.exists()) {
						logFileDir.mkdirs();
						deleteLog(null, new Date(System.currentTimeMillis()
								- 1000 * 60 * 60 * 24 * 14));
					}

					if (logQueue != null && logQueue.size() > 0) {
						LogInfo saveInfo = null;
						try {
							saveInfo = logQueue.take();
							if(saveInfo==null){
								continue;
							}
//							RandomAccessFile output = null;
//							String saveFilePath = logDirPath + "/"
//									+ saveInfo.logType + "."
//									+ saveInfo.logLevel;
//							output = new RandomAccessFile(saveFilePath, "rws");
//							output.seek(output.length());
//							output.write(saveInfo.logMsg.getBytes("UTF-8"));
//							output.close();

							RandomAccessFile outputAll = null;
							String saveAllFilePath = logDirPath + "/all.txt";
							outputAll = new RandomAccessFile(saveAllFilePath,
									"rws");
							outputAll.seek(outputAll.length());
							outputAll.write(saveInfo.logMsg.getBytes("UTF-8"));
							outputAll.close();
						} catch (Exception e) {
							 

							Log.e(TAG, LogStorageThread.class.getName()
									+ "====>>" + e.toString());

						}
					} else {
						try {
							sleep(2000);
						} catch (InterruptedException e) {
							 
							Log.e(TAG, LogStorageThread.class.getName()
									+ "====>>logQueue:" + e.toString());

						}
					}
				}
			} catch (Exception e) {
				 
				Log.e(TAG,
						LogStorageThread.class.getName() + "====>>while:"
								+ e.toString());

			}
		}

	}

	private StorageByFile2() {
		onCreate();
	}

	private void onCreate() {
		if (logQueue == null)
			logQueue = new LinkedBlockingQueue<LogInfo>(QUEUE_SIZE);
		if (logThread == null) {
			logThread = new LogStorageThread();
			logThread.start();
		}

	}

	public static Storage getInstance() {
		if (instance == null)
			instance = new StorageByFile2();
		return instance;
	}

	/**
	 * @see com.xu.Log.storage.Storage#saveLog(com.xu.Log.LogInfo)
	 * 
	 * 
	 */
	@Override
	public boolean saveLog(LogInfo msg) {
		try {
			if (logThread == null) {
				logThread = new LogStorageThread();
				logThread.start();
			}
			logQueue.put(msg);
			return true;
		} catch (Exception e) {
			Log.e(TAG,
					StorageByFile2.class.getName() + "====>>saveLog:" + e.toString());

			return false;
		}

	}

	/**
	 * @see com.xu.Log.storage.Storage#deleteLog(java.util.Date,
	 *      java.util.Date)
	 * 
	 * 
	 */
	@Override
	public boolean deleteLog(Date startTime, Date endTime) {
		try {
			long timeNow = System.currentTimeMillis();
			long start = 0;
			long end = 0;

			if (startTime != null) {
				start = startTime.getTime();
			}
			if (endTime == null || endTime.getTime() > timeNow) {
				end = timeNow - 1000 * 60 * 60 * 24;
			} else {
				end = endTime.getTime();
			}

			File logRootDir = new File(logDirRootPath);
			File logFiles[]=logRootDir.listFiles();
			for (File logDir : logFiles) {

				try{
					long date = TimeTools.dateOnDayToLong(logDir.getName());
					if ((startTime == null && date <= end)
							|| (date >= start && date <= end)) {
						FileControl.deleteFileAndSub(logDir);
					}
				}
				catch (Exception e) {
					FileControl.deleteFileAndSub(logDir);
				}

			}

			return true;
		} catch (Exception e) {
			Log.e(TAG,
					StorageByFile2.class.getName() + "====>>deleteLog:" + e.toString());

			return false;
		}
	}

	/**
	 * return if sucess by List File ,else null
	 * 
	 * @see com.xu.Log.storage.Storage#getTargetLog(java.util.Date,
	 *      java.util.Date)
	 */
	@Override
	public Object getTargetLog(Date startTime, Date endTime) {
		try {
			List<File> listFile = new ArrayList<File>();
			long timeNow = System.currentTimeMillis();
			long start = 0;
			long end = 0;
			if (startTime != null) {
				start = startTime.getTime();
			}

			if (endTime == null || endTime.getTime() > timeNow) {
				end = timeNow - 1000 * 60 * 60 * 24;
			} else {
				end = endTime.getTime();
			}

			File logRootDir = new File(logDirRootPath);
			for (File logDir : logRootDir.listFiles()) {

				long date = TimeTools.dateOnDayToLong(logDir.getName());
				// if ((startTime == null && date <= end)
				// ||(date >= start&& date <= end)) {
				if (date >= start && date <= end) {
					listFile.add(logDir);
				}

			}
			return listFile;
		} catch (Exception e) {
			Log.e(TAG,
					StorageByFile2.class.getName() + "====>>getTargetLog:" + e.toString());

			return null;
		}
	}

	@Override
	public void quit() {
		this.runFlag=false;
		this.logThread=null;
	}

}
