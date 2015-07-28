package net.rockyqi.web.crawler.common;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
/**
 * @Description:Properties文件读写工具类
 * @author rockyqi1001@gmail.com
 * @version 1.0
 */
public class PropertyMgr {

	protected static final Logger logger = Logger.getLogger(PropertyMgr.class.getName());

	private static String realPathOfPropertiesFile = null;
	
	private static PropertiesConfiguration propertyConfig = null;
	
	public static int getInt(String key, int defaultValue) {
		try {
			if (null == propertyConfig) {
				propertyConfig = new PropertiesConfiguration(realPathOfPropertiesFile);
			}
			return propertyConfig.getInt(key, defaultValue);
		} catch (Exception e) {
			logger.error(e, e);
			return defaultValue;
		}
	}

	public static String getString(String key) {
		try {
			if (null == propertyConfig) {
				propertyConfig = new PropertiesConfiguration(realPathOfPropertiesFile);
			}
			return propertyConfig.getString(key, "");
		} catch (Exception e) {
			logger.error(e, e);
			return "";
		}
	}
	
	public static void setString(String key, String value) {
		try {
			if (null == propertyConfig) {
				propertyConfig = new PropertiesConfiguration(realPathOfPropertiesFile);
			}
			propertyConfig.setProperty(key, value);
			propertyConfig.save();
		} catch (Exception e) {
			logger.error(e, e);
		}
	}

	/**
	 * @return the realPathOfPropertiesFile
	 */
	public static String getRealPathOfPropertiesFile() {
		return realPathOfPropertiesFile;
	}

	/**
	 * @param realPathOfPropertiesFile the realPathOfPropertiesFile to set
	 */
	public static void setRealPathOfPropertiesFile(String realPathOfPropertiesFile) {
		PropertyMgr.realPathOfPropertiesFile = realPathOfPropertiesFile;
	}

	/**
	 * @return the propertyConfig
	 */
	public static PropertiesConfiguration getPropertyConfig(String realPathOfPropertiesFile) {
		if (null == propertyConfig) {
			try {
				propertyConfig = new PropertiesConfiguration(realPathOfPropertiesFile);
			} catch (ConfigurationException e) {
				logger.error(e, e);
				return null;
			}
		}
		return propertyConfig;
	}

}