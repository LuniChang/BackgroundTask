package com.xu.Log.print;

import com.xu.Log.LogInfo;
import com.xu.Log.storage.Storage;

public abstract class Printer {
 
	protected Storage storage;
	 
	 
	 
	public abstract void printOut(LogInfo msg);
	public void setStorage(Storage storage) {
		this.storage=storage;
	}
	 
}
 
