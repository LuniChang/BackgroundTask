package com.xu.Log.print;

import com.xu.Log.storage.StorageByFile2;

public class PrinterFactory {

	private static XuPrinter mXuPrinter=null;

	public static Printer getXuPrinter() {
		if (mXuPrinter == null) {
			mXuPrinter = new XuPrinter();
			mXuPrinter.setStorage(StorageByFile2.getInstance());
		}
		return mXuPrinter;
	}

}
