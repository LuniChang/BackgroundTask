package com.xu.Log;

import com.xu.Log.print.Printer;
import com.xu.Log.print.PrinterFactory;

public class AppLog implements Log<String> {

	private int logType;

	private String className;

	private Printer errorPrinter = PrinterFactory.getXuPrinter();

	private Printer warnPrinter = PrinterFactory.getXuPrinter();

	private Printer infoPrinter = PrinterFactory.getXuPrinter();

	private Printer debugPrinter = PrinterFactory.getXuPrinter();

	private Printer secretPrinter = PrinterFactory.getXuPrinter();

	public AppLog(int logType, String className) {
		super();
		this.logType = logType;
		this.className = className;
	}

	@Override
	public void d(String param) {
		 
		LogInfo logInfo = new LogInfo();
		logInfo.logType = this.logType;
		logInfo.logLevel = "d";
		logInfo.logMsg = className + "====>>" + param;
		debugPrinter.printOut(logInfo);
	}

	@Override
	public void i(String param) {
		 
		LogInfo logInfo = new LogInfo();
		logInfo.logType = this.logType;
		logInfo.logLevel = "i";
		logInfo.logMsg = className + "====>>" + param;
		infoPrinter.printOut(logInfo);
	}

	@Override
	public void w(String param) {
		 
		LogInfo logInfo = new LogInfo();
		logInfo.logType = this.logType;
		logInfo.logLevel = "w";
		logInfo.logMsg = className + "====>>" + param;
		warnPrinter.printOut(logInfo);
	}

	@Override
	public void e(String param) {
		 
		LogInfo logInfo = new LogInfo();
		logInfo.logType = this.logType;
		logInfo.logLevel = "e";
		logInfo.logMsg = className + "====>>" + param;
		errorPrinter.printOut(logInfo);
	}

	@Override
	public void s(String param) {
		 
		LogInfo logInfo = new LogInfo();
		logInfo.logType = this.logType;
		logInfo.logLevel = "s";
		logInfo.logMsg = className + "====>>" + param;
		secretPrinter.printOut(logInfo);
	}

}
