package com.xu.backgroundtask;

public interface BackgroundTaskViewListener {

	public void whenCacthError(String msg);
	public String getBackgroundTaskId();
	public void dismiss();
	public void updateTitle(String msg);
}