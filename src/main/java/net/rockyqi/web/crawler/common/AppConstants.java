package net.rockyqi.web.crawler.common;

import java.text.SimpleDateFormat;
import java.util.regex.Pattern;
/**
 * 
 * @Description:Application相关常量类
 * @author rockyqi1001@gmail.com
 * @version 1.0
 */
public class AppConstants {
	//software version
	public final static String VERSION = "V1.0";
	//regex used in parse keyword from html string
	public final static Pattern KEYWORDS_REGEX = Pattern.compile("<\\s*meta\\s*name=\"keywords\"\\s*content=\"([^\"]*)\"\\s*/?\\s*>",
			Pattern.CASE_INSENSITIVE);
	//time formatter
	public final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_hh:mm:ss");
	//time formatter
	public final static SimpleDateFormat sdfFileName = new SimpleDateFormat("yyyyMMdd_HHmmss");
	//key splitter
	public final static String VALUE_SPLITER = ":";
	//label splitter
	public final static String WORD_SPLITER = ";";
	//word label splitter
	public final static String WORD_LABEL_SPLITER = ",";
	//label analyze result prefix
	public final static String LABEL_PREFIX = "|~|";
	
	public final static String TMP_FOLDER_NAME = "tmp";
	
	public final static String RESULT_FOLDER_NAME = "result";
	
	public final static String DATE_LINK = "---";
	
	public final static String LOG_FILE_SUFFIX = "requestuniUrl.txt";
	
}
