package com.xu.Log;

import java.io.PrintWriter;
import java.io.StringWriter;

public class AppExcpLog implements Log<Exception> {

	private AppLog appLog;

	public AppExcpLog(int logType, String className) {
		super();
		appLog=new AppLog(logType, className);
	}


	@Override
	public void d(Exception param) {
		  StringWriter out = new StringWriter();
		  param.printStackTrace(new PrintWriter(out));
		  appLog.d(param.toString());
	}

	@Override
	public void i(Exception param) {
		 StringWriter out = new StringWriter();
		  param.printStackTrace(new PrintWriter(out));
		  appLog.i(param.toString());
	}

	@Override
	public void w(Exception param) {
		
	}

	@Override
	public void e(Exception param) {
		
	}

	@Override
	public void s(Exception param) {
		
	}

	
}
