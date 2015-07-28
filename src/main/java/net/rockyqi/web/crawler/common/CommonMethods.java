package net.rockyqi.web.crawler.common;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import net.rockyqi.web.file.GZipUtils;

import org.apache.log4j.Logger;


/**
 * @Description:Common methods used in project
 * @author rockyqi1001@gmail.com
 * @version 1.0
 */
public class CommonMethods {

	protected static final Logger logger = Logger.getLogger(CommonMethods.class.getName());

//	/**
//	 * @Description:将字符串编码转换为utf-8编码
//	 * @author rockyqi1001@gmail.com
//	 * @param str
//	 * @return
//	 */
//	public final static String encodeToUnicode(String str) {
//		String tmp;
//		StringBuffer sb = new StringBuffer();
//		char c;
//		int i, j;
//		for (i = 0; i < str.length(); i++) {
//			c = str.charAt(i);
//			if (c > 255) {
//				sb.append("\\u");
//				j = (c >>> 8);
//				tmp = Integer.toHexString(j);
//				if (tmp.length() == 1)
//					sb.append("0");
//				sb.append(tmp);
//				j = (c & 0xFF);
//				tmp = Integer.toHexString(j);
//				if (tmp.length() == 1)
//					sb.append("0");
//				sb.append(tmp);
//			} else {
//				sb.append(c);
//			}
//
//		}
//		return sb.toString();
//	}
//
//	/**
//	 * @Description:判断用户输入的url是否是指向单个页面
//	 * @author rockyqi1001@gmail.com
//	 * @param url
//	 * @return boolean
//	 */
//	public final static boolean isSinglePage(String url) {
//		if (null == url || "".equals(url)) {
//			return false;
//		} else if ((url.indexOf(".html")) != -1 || (url.indexOf(".htm")) != -1 || (url.indexOf(".shtml")) != -1
//				|| (url.indexOf(".php")) != -1 || (url.indexOf(".jsp")) != -1 || (url.indexOf(".aspx")) != -1
//				|| (url.indexOf(".asp")) != -1) {
//			return true;
//		} else {
//			return false;
//		}
//	}

	/**
	 * @Description:抓取单个web页面的html数据
	 * @author rockyqi1001@gmail.com
	 * @param urlString
	 * @return html string
	 */
	public final static String getSinglePageHtmlString(String urlString, boolean doLog) {
		String s = null; // 依次循环，至到读的值为空
		StringBuilder sb = new StringBuilder();
		HttpURLConnection urlConn = null;
		BufferedReader reader = null;
		try {
			URL url = new URL(urlString); // 将urlstr字符串网址实例化为URL定位地址s
			urlConn = (HttpURLConnection) url.openConnection(); // 打开url链接
			setRequestParams(urlConn);
			setRequestHeader(urlConn);
			// connect to the url(do get)
			urlConn.connect();
			if (urlConn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				if (doLog) {
					logger.error("response failed:<" + urlString + "> and respCode=" + urlConn.getResponseCode());
				}
				return null;
			}
			String htmlString;
			String contentEncoding = urlConn.getContentEncoding();
			if (null != contentEncoding && "gzip".equalsIgnoreCase(contentEncoding)) {
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				GZipUtils.decompress(urlConn.getInputStream(), os);
				htmlString = new String(os.toString("ISO-8859-1"));
			} else {
				reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), "ISO-8859-1")); // 实例化输入流，并获取网页源码
				while ((s = reader.readLine()) != null) {
					sb.append(s);
				}
				htmlString = sb.toString();
			}
			return htmlString;
		} catch (IOException e) {
			if (doLog) {
				logger.error("connect or read failed:<" + urlString + ">.");
			}
			return null;
		} catch (Exception e) {
			if (doLog) {
				logger.error("Exception:<" + urlString + ">.");
			}
			return null;
		} finally {
			if (null != urlConn) {
				urlConn.disconnect();
			}
			if (null != reader) {
				try {
					reader.close();
				} catch (IOException e) {
					logger.error(e, e);
				}
			}
		}
	}

	private final static void setRequestParams(HttpURLConnection urlConn) {
		// 设置连接超时时长
		urlConn.setConnectTimeout(PropertyMgr.getInt(PropertyConst.HTTP_CONN_TIMEOUT, 3000));
		// 设置读取超时时长
		urlConn.setReadTimeout(PropertyMgr.getInt(PropertyConst.HTTP_READ_TIMEOUT, 3000));
		urlConn.setAllowUserInteraction(false);
		urlConn.setDoInput(true);
		urlConn.setDoOutput(true);
	}

	private final static void setRequestHeader(HttpURLConnection urlConn) {
		urlConn.addRequestProperty("Connection", "keep-alive");
		urlConn.addRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		// TODO: Should use different UA
		urlConn.addRequestProperty("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.116 Safari/537.36");
		urlConn.addRequestProperty("DNT", "1");
		//ask for not compressed response
		urlConn.addRequestProperty("Accept-Encoding", "gzip");
		urlConn.addRequestProperty("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6,zh-TW;q=0.4,en-US;q=0.2");
		urlConn.addRequestProperty("Cache-Control", "max-age=0");
	}

	/**
	 * @Description 抓取单个web页面的html数据
	 * @author rockyqi1001@gmail.com
	 * @param urlString
	 * @return html string
	 */
//	public final static String getSinglePageHtmlString(String urlString, CloseableHttpClient httpClient) {
//		String url = URLCanonicalizer.getCanonicalURL(urlString);
//		if (null == url || "".equals(url)) {
//			// logger.info("Invalid urlString:<" + urlString + ">.");
//			return "";
//		}
//		CloseableHttpResponse response = null;
//		try {
//			URL urlObj = new URL(url);
//			URI uri = new URI(urlObj.getProtocol(), urlObj.getHost(), urlObj.getPath(), urlObj.getQuery(), null);
//			HttpGet httpget = new HttpGet(uri.toASCIIString());
//			response = httpClient.execute(httpget);
//			HttpEntity entity = response.getEntity();
//			if (null != entity) {
//				return EntityUtils.toString(entity);
//			}
//		} catch (Exception e) {
//			// Log all errors
//			logger.info("Connect or read failed:<" + urlString + ">.");
//			// logger.error(e, e);
//		} finally {
//			try {
//				if (null != response) {
//					response.close();
//				}
//			} catch (IOException e) {
//				logger.error("Error when closing response.");
//				logger.error(e, e);
//			}
//		}
//		return "";
//	}
}
