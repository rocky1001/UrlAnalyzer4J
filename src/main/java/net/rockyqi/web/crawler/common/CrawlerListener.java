package net.rockyqi.web.crawler.common;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import net.rockyqi.web.file.FileHelper;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;


/**
 * @Description:Web监听器,在web应用启动时先运行本类.完成运行路径设置,数据库初始化等工作
 * @author rockyqi1001@gmail.com
 * @version 1.0
 */
public class CrawlerListener implements ServletContextListener {
	// logger
	protected static final Logger logger = Logger.getLogger(CrawlerListener.class.getName());

	@Override
	public void contextInitialized(ServletContextEvent event) {
		try {
			// set environment path
			setEnvPath(event);
			// initialize web cralwer here
			initWebCrawler();
			logger.info("WebCrawler started successfully, now version=" + AppConstants.VERSION);
		} catch (Exception e) {
			logger.info("WebCrawler init failed, now closing...");
			logger.error(e, e);
			contextDestroyed(event);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		// stop web crawler
		try {
			stopWebCrawler();
		} catch (IOException e) {
			logger.error(e, e);
		}
		logger.info("WebCrawler stopped successfully.");
	}

	/**
	 * start micro blog refresh timer.
	 * 
	 * @throws Exception
	 */
	private void initWebCrawler() throws IOException {
		initWordsMap();
		logger.debug("WebCrawler started.");
	}

	/**
	 * stop micro blog refresh timer.
	 */
	private void stopWebCrawler() throws IOException {
		// stop db pool
		logger.debug("WebCrawler stopped.");
	}

	/**
	 * @Description:设置运行环境路径参数
	 * @author rockyqi1001@gmail.com
	 * @param event
	 * @throws Exception
	 * @throws ConfigurationException
	 */
	private void setEnvPath(ServletContextEvent event) throws Exception, ConfigurationException {
		// get absolute path of WEB-INF
		String realPathOfRuntime = getRealPath(event, "WEB-INF") + File.separator;
		String propertyFileName = realPathOfRuntime + "conf" + File.separator + "web.properties";
		logger.info("Property file name:" + propertyFileName);
		// initialize properties manager
		PropertyMgr.setRealPathOfPropertiesFile(propertyFileName);
		// get os name
		String osName = System.getProperty("os.name");
		logger.info("Operating system name:" + osName);
		if (osName == null || osName.trim().equals("")) {
			logger.error("Wrong operating system name !");
		}
		// get web server name
		String webServerName = event.getServletContext().getServerInfo();
		logger.info("Web server name:" + webServerName);
		if (webServerName == null || webServerName.trim().equals("")) {
			logger.error("Wrong web server name !");
		}
		// save above properties to file
		PropertiesConfiguration propertyConfig = PropertyMgr.getPropertyConfig(propertyFileName);
		propertyConfig.setProperty("OS", osName.toLowerCase());
		propertyConfig.setProperty("WebServer", webServerName.toLowerCase());
		propertyConfig.setProperty(PropertyConst.REAL_PATH, realPathOfRuntime);
		logger.info(PropertyConst.REAL_PATH + ":" + propertyConfig.getString(PropertyConst.REAL_PATH));
		propertyConfig.save();

	}

	private String getRealPath(ServletContextEvent event, String fileName) throws Exception {
		String realPath = event.getServletContext().getRealPath(fileName);
		if (realPath == null || realPath.trim().equals("")) {
			realPath = event.getServletContext().getResource("/").getPath() + fileName;
		}
		return realPath;
	}
	
	private void initWordsMap() throws IOException{
		String realPath = PropertyMgr.getString(PropertyConst.REAL_PATH);
		String extDicPath = realPath + "classes/biz.dic";
		logger.info("extDicPath=" + extDicPath);
		CrawlerHelper.WORDS_LIST = FileHelper.readFileIntoLineVector(extDicPath);
		if (null == CrawlerHelper.WORDS_LIST || CrawlerHelper.WORDS_LIST.size() < 1) {
			logger.error("ERROR when reading ext.dic file, NOW ABORT...");
			stopWebCrawler();
		}
//		for (String word : CrawlerHelper.WORDS_LIST) {
//			System.out.println(word);
//		}
	}

}
