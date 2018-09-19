package com.xu.Log.storage;

import java.util.Date;

import com.xu.Log.LogInfo;

public interface Storage {
 
	public abstract void quit();
	public abstract boolean saveLog(LogInfo msg);
	public abstract boolean deleteLog(Date startTime, Date endTime);
	public abstract Object getTargetLog(Date startTime, Date endTime);
}
 
