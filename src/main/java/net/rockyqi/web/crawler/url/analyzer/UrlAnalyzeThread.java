package net.rockyqi.web.crawler.url.analyzer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.rockyqi.web.crawler.common.AppConstants;
import net.rockyqi.web.crawler.common.CommonMethods;
import net.rockyqi.web.crawler.common.PropertyConst;
import net.rockyqi.web.crawler.common.PropertyMgr;
import net.rockyqi.web.file.FileHelper;
import net.rockyqi.web.file.GZipHelper;
import net.rockyqi.web.ftp.FtpHelper;

import org.apache.log4j.Logger;


/**
 * URL���ݷ������࣬���URL���ݷ����Ĺ���
 * @author rockyqi1001@gmail.com
 * @version 1.0
 */
public class UrlAnalyzeThread implements Runnable {
	//logger
	private static final Logger logger = Logger.getLogger(UrlAnalyzeThread.class.getName());
	// recrawl url list
	public static List<String> recrawlUrlList = new CopyOnWriteArrayList<String>();
	// lack of keywords url list
	public static List<String> lackKeywordsUrlList = new CopyOnWriteArrayList<String>();
	//�����log����
	private String logFileName;

	public UrlAnalyzeThread(String aLogFileName) {
		logFileName = aLogFileName;
	}

	/**
	 * do url analyze
	 */
	@Override
	public void run() {
		String localLogFilePath;
		// 1.check if logFileName is a local file
		if (new File(logFileName).exists()) {
			// if so, open the local file.
			localLogFilePath = logFileName;
			logFileName = localLogFilePath.substring(localLogFilePath.lastIndexOf(File.separator) + 1, localLogFilePath.length());
			logger.info("Log file at local path=" + localLogFilePath);
		} else {
			// if not, get log file from ftp.
			localLogFilePath = FtpHelper.getLogFromFTP(logFileName);
			logger.info("Finished get log file from ftp at local path=" + localLogFilePath);
		}
		if (null == localLogFilePath || "".equals(localLogFilePath)) {
			ResultCnt.init();
			return;
		}
		// do un-tar
		if (localLogFilePath.indexOf(".tar.gz") != -1) {
			GZipHelper.unTargzFile(localLogFilePath, localLogFilePath.substring(0, localLogFilePath.lastIndexOf(File.separator)));
			logFileName = logFileName.substring(0, logFileName.lastIndexOf(".tar.gz"));
			String unzippedFilePath = localLogFilePath.substring(0, localLogFilePath.lastIndexOf(File.separator)) + File.separator
					+ logFileName;
			localLogFilePath = unzippedFilePath;
			logger.info("Finished unzip log file at local path=" + localLogFilePath);
		}

		logger.info("Start analyze ulrs, file name=" + logFileName);
		// 2.��URL��־�е�ÿ��URL��¼,��������߳�(�߳���Ŀ������),�ֱ�ץȡ�ͷ�����ЩURL
		List<String> resultList = analyzeUrlList(localLogFilePath);
		logger.info("Finished all url analyze.");
		logger.info("Analyze success num=" + ResultCnt.Success);
		logger.info("Lack of keywords num=" + ResultCnt.LackOfKeywords);
		logger.info("Connection error num=" + ResultCnt.ConnetionErr);
		logger.info("No content error num=" + ResultCnt.NoContent);
		logger.info("Invalid charset num=" + ResultCnt.InvalidCharset);
		
		// 3.���������д�뱾���ļ����л���
		String resultFolderPath = PropertyMgr.getString(PropertyConst.REAL_PATH) + AppConstants.RESULT_FOLDER_NAME;
		//TODO:check below!!!
		String date = logFileName.substring(0, logFileName.indexOf("UniLog"));
		String resultFilePath = resultFolderPath + File.separator + logFileName;
		logFileName = date + "/" + logFileName;
		
		if (FileHelper.writeDataIntoLocalFile(resultList, resultFilePath)) {
			logger.info("Finished writing all url analyze result to file:" + resultFilePath);
			// 4.����������ļ��ش���hadoopƽ̨����֪ͨhadoopƽ̨�������
			if (FtpHelper.sendLogToFTP(resultFilePath, logFileName)) {
				logger.info("Finished sending all result file to hadoop ftp.");
				// ����hadoopƽ̨�ӿ�url��֪ͨhadoopƽ̨�������
				notifyHadoop(date);
				logger.info("Finished notifying hadoop.");
			} else {
				logger.error("Failed sending all result file to hadoop.");
			}
		} else {
			logger.error("Failed writing local result file.");
		}
		//write all lack of keywords file to disk
		if (FileHelper.writeDataIntoLocalFile(lackKeywordsUrlList, resultFilePath + ".lk")) {
			logger.info("Finished writing all <lack of keywords> url analyze result to file:" + resultFilePath + ".lk");
		} else {
			logger.error("Failed writing <lack of keywords> local result file.");
		}
		
		// clear recrawl list
		recrawlUrlList.clear();
		lackKeywordsUrlList.clear();
	}

	/**
	 * @Description:
	 * @author rockyqi1001@gmail.com
	 * @return
	 */
	private List<String> analyzeUrlList(String localLogFilePath) {
		logger.debug("Start url list analyze.");
		List<String> urlList = FileHelper.readFileIntoLineList(localLogFilePath);
		int urlsNum = urlList.size();
		logger.info("Total url number=" + urlsNum);
		if (urlsNum < 1) {
			return null;
		} else {
			ResultCnt.OriginTotalNum = urlsNum;
			List<String> resultList = new ArrayList<String>(urlsNum);
			int threadsNum = PropertyMgr.getInt(PropertyConst.ANALYZE_THREAD_NUMBER, 128);
			ExecutorService threadPool = Executors.newFixedThreadPool(threadsNum);
			CompletionService<String> pool = new ExecutorCompletionService<String>(threadPool);
			try {
				// 1. do all url crawl and analyze
				doCrawlGetResult(urlList, resultList, pool, true);
				logger.info("Recrawl url number=" + recrawlUrlList.size());
				ResultCnt.Recrawl = recrawlUrlList.size();
				// 2. if there is(are) any connection failed urls, do recrawl
				if (recrawlUrlList.size() > 0) {
					doCrawlGetResult(recrawlUrlList, resultList, pool, false);
				}
				
			} catch (Exception e) {
				logger.error(e, e);
			} finally {
				threadPool.shutdown();
			}
			return resultList;
		}
		
	}

	/**
	 * @Description:
	 * @author rockyqi1001@gmail.com
	 * @param urlList
	 * @param urlsNum
	 * @param resultList
	 * @param pool
	 * @throws InterruptedException
	 * @throws ExecutionException 
	 */
	private void doCrawlGetResult(List<String> urlList, List<String> resultList, CompletionService<String> pool, boolean doRecrawl)
			throws InterruptedException, ExecutionException {
		int listSize = urlList.size();
		for (int i = 0; i < listSize; i++) {
			pool.submit(new UrlAnalyzeWorker(urlList.get(i), doRecrawl));
		}
		if(doRecrawl){
			logger.info("Submitted " + listSize + " urls(origin) to thread pool.");
		} else {
			logger.info("Submitted " + listSize + " urls(recrawl) to thread pool.");
		}
		
		for (int i = 0; i < listSize; i++) {
			String result = pool.take().get();
			if (null == result || "".equals(result)) {
				continue;
			}
			// д���
			resultList.add(result);
		}
		logger.debug("Finished url analyzing, total success num=" + resultList.size());
	}

	private static void notifyHadoop(String date) {
		String interfaceUrlOfHadoop = PropertyMgr.getString(PropertyConst.HADOOP_INTERFACE_URL);
		interfaceUrlOfHadoop = interfaceUrlOfHadoop + "&date=" + date;
		logger.info("interfaceUrlOfHadoop=" + interfaceUrlOfHadoop);
		CommonMethods.getSinglePageHtmlString(interfaceUrlOfHadoop, true);
	}

//	public static void main(String[] args) {
//		notifyHadoop("2013-11-30");
//	}
}
