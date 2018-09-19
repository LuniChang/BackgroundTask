package com.xu.Log.print;

import com.xu.Log.LogInfo;
import com.xu.Log.TimeTools;
/**
 * log打印器
 * @author Xuchang
 *
 */
public class XuPrinter extends Printer {

	private static final String TAG="XU";
	@Override
	public void printOut(LogInfo msg) {
		 
		if(msg==null)
			return;
		String timeNow=TimeTools.longToDate(System.currentTimeMillis());
		String log=timeNow +"  "+ "["+ msg.logLevel +"]    " + msg.logMsg+"\r\n";
		msg.logMsg=log;
		if(!"s".equals(msg.logLevel)&&!"d".equals(msg.logLevel)){
			storage.saveLog(msg);
		}
		
		if("s".equals(msg.logLevel)){
			android.util.Log.d(TAG, msg.logMsg);
		}else if("d".equals(msg.logLevel)){
			android.util.Log.d(TAG, msg.logMsg);
		}else if("e".equals(msg.logLevel)){
			android.util.Log.e(TAG, msg.logMsg);
		}else if("w".equals(msg.logLevel)){
			android.util.Log.w(TAG, msg.logMsg);
		}else if("i".equals(msg.logLevel)){
			android.util.Log.i(TAG, msg.logMsg);
		}
	}
 
}
 
