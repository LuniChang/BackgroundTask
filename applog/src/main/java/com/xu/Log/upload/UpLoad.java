package com.xu.Log.upload;

import com.xu.Log.storage.Storage;

public abstract class UpLoad {
 
	protected Storage storage;
	 
	 
	public abstract void doUpLoad();
	 
	public void setStorage(Storage storage) {
	  this.storage=storage;
	}
	 
}
 
