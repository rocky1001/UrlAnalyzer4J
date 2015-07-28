package net.rockyqi.web.crawler.url.analyzer;

import java.util.concurrent.atomic.AtomicInteger;

public class ResultCnt {
	public static String LogName = "none";
	
	public static int OriginTotalNum = 0;

	private static int processingTotalNum = 0;
	
	public static int Recrawl = 0;

	public static AtomicInteger Success = new AtomicInteger(0);
	
	public static AtomicInteger ConnetionErr = new AtomicInteger(0);

	public static AtomicInteger NoContent = new AtomicInteger(0);

	public static AtomicInteger LackOfKeywords = new AtomicInteger(0);
	
	public static AtomicInteger InvalidCharset = new AtomicInteger(0);

	public static void init() {
		LogName = "none";
		OriginTotalNum = 0;
		processingTotalNum = 0;
		Recrawl = 0;
		Success = new AtomicInteger(0);
		ConnetionErr = new AtomicInteger(0);
		NoContent = new AtomicInteger(0);
		LackOfKeywords = new AtomicInteger(0);
		InvalidCharset =  new AtomicInteger(0);
	}

	public static int getProcessingTotalNum() {
		processingTotalNum = Success.intValue() + ConnetionErr.intValue() + NoContent.intValue()
				+ LackOfKeywords.intValue() + InvalidCharset.intValue();
		return processingTotalNum;
	}

}
