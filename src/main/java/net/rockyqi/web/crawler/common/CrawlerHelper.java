package net.rockyqi.web.crawler.common;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import net.rockyqi.web.crawler.url.analyzer.ResultCnt;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.l3s.boilerpipe.BoilerpipeExtractor;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.CommonExtractors;
import de.l3s.boilerpipe.extractors.ExtractorBase;
import de.l3s.boilerpipe.sax.HTMLHighlighter;

/**
 * 完成对HTML源码字符串的截取，分词，统计
 * 
 * @Description:
 * @author rockyqi1001@gmail.com
 * @version 1.0
 */
public class CrawlerHelper {
	/**
	 * logger
	 */
	protected static final Logger logger = Logger.getLogger(CrawlerHelper.class.getName());

	public static Vector<String> WORDS_LIST;

	/**
	 * @Description 从URL对应的HTML字符串中获取中文文本内容（标题，keyword和正文）；
	 *              随后进行当前内容的分词和词频统计，并将相应的结果组成字符串返回。
	 * @author rockyqi1001@gmail.com
	 * @param htmlString
	 */
	public static String processHtmlString(String htmlString) {
		return processHtmlString(null, htmlString);
	}

	/**
	 * @Description 从URL对应的HTML字符串中获取中文文本内容（标题，keyword和正文）；
	 *              随后进行当前内容的分词和词频统计，并将相应的结果组成字符串返回。
	 * @author rockyqi1001@gmail.com
	 * @param htmlString
	 */
	public static String processHtmlString(String url, String htmlString) {
		logger.debug("origin html=" + htmlString);
		if (null == htmlString || "".equals(htmlString)) {
			logger.debug("Empty content, url=" + url);
			ResultCnt.NoContent.incrementAndGet();
			return null;
		}
		// 对htmlString重新进行编码，统一编码为页面指定的编码格式
		String encodeString = encodeHtmlString(url, htmlString);
		if (null == encodeString || "".equals(encodeString)) {
			ResultCnt.InvalidCharset.incrementAndGet();
			return null;
		}

		logger.debug("start words analyze.");
		TextAnalyzer ta = new TextAnalyzer();
		// 对title进行分词
		String title = getTitle(encodeString);
		logger.debug("title=" + title);
		List<String> analyzedTitles = ta.analyzeToWordsList(title);
		logger.debug("words from title:");
		printStringList(analyzedTitles);

		// 对keywords进行分词
		String keywords = getKeywords(encodeString);
		logger.debug("keywords=" + keywords);
		List<String> analyzedKeywords = ta.analyzeToWordsList(keywords);
		logger.debug("words from keywords:");
		printStringList(analyzedKeywords);

		// 对正文内容进行分词
		// TODO:对getP标签后的结果，再进行按密度进行正文内容提取的操作？
		String mainText = getMainTextByTagP(encodeString);
		List<String> analyzedText = ta.analyzeToWordsList(mainText);
		int mapSize = 2048;
		if (null != analyzedText && analyzedText.size() > mapSize) {
			mapSize = analyzedText.size();
		}

		HashMap<String, Integer> mainTextWordsCountMap = new HashMap<String, Integer>(mapSize);
		// 对正文分词结果(analyzedText)统计每个词汇出现的次数,写入mainTextWordsCountMap
		countOrScoreWords(analyzedText, 1, mainTextWordsCountMap);
		countOrScoreWords(analyzedTitles, 1, mainTextWordsCountMap);
		countOrScoreWords(analyzedKeywords, 1, mainTextWordsCountMap);

		List<String> topXWordsList = sortMapByValueGetTopXKeys(mainTextWordsCountMap, 1000);
		logger.debug("words from text:");
		printStringList(topXWordsList);
		logger.debug("finished words analyze.");
		// 返回标签字符串
		return makeAnalyzeResultString(topXWordsList, mainTextWordsCountMap);
	}

	/**
	 * @Description:根据打分后的分词，从关系库获取相应标签，再次对标签进行打分，并获取比重较大的标签
	 * @author rockyqi1001@gmail.com
	 * @param topXWordsList
	 * @return
	 */
	private static String makeAnalyzeResultString(List<String> topXWordsList, HashMap<String, Integer> wordsCountMap) {
		// 获取出现次数最多的前X个词汇(目前默认获取100个)
		int counter = 0;
		int topX = PropertyMgr.getInt(PropertyConst.TOP_X, 100);
		StringBuffer sb = new StringBuffer("");
		// 从数据库获取词汇的标签(多个)
		logger.debug("makeAnalyzeResultString begin.");
		for (String word : topXWordsList) {
			if (isWordValid(word)) {
				counter++;
				sb.append(word);
				sb.append(AppConstants.VALUE_SPLITER);
				sb.append(wordsCountMap.get(word));
				sb.append(AppConstants.WORD_SPLITER);
				if (counter == topX) {
					break;
				}
			} else {
				continue;
			}
		}
		logger.debug("score the labels end.");
		return sb.toString();
	}

	private static boolean isWordValid(String word) {
		if (1 == word.length()) {
			return false;
		} else if (WORDS_LIST.contains(word)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @Description:网页爬虫使用8859-1编码抓取网页,因此在后续使用时需要对抓取到的html字符串按照其内部charset定义进行编码.
	 * @author rockyqi1001@gmail.com
	 * @param htmlString
	 * @return
	 */
	private static String encodeHtmlString(String url, String htmlString) {
		String charset = getCharset(htmlString);
		charset = charset.replaceAll(";", "");
		String encodeString = null;
		if (!"8859_1".equalsIgnoreCase(charset)) {
			try {
				encodeString = new String(htmlString.getBytes("8859_1"), charset);
			} catch (Exception e) {
				// logger.error("Wrong charset=" + charset);
				// logger.error("Related url=" + url);
				try {
					encodeString = new String(htmlString.getBytes("8859_1"), "gb2312");
				} catch (UnsupportedEncodingException e1) {
					logger.error(e, e);
				}
			}
		} else {
			encodeString = htmlString;
		}
		return encodeString;
	}

	/**
	 * @Description:获取html源码中meta-charset字段定义的页面编码格式
	 * @author rockyqi1001@gmail.com
	 * @param htmlString
	 * @return
	 */
	private static String getCharset(String htmlString) {
		if (null == htmlString || "".equals(htmlString)) {
			return null;
		}
		// set default charset to gb2312
		String charset = "gb2312";
		Document doc = Jsoup.parse(htmlString);
		Elements metas = doc.select("meta");
		for (Element meta : metas) {
			if (meta.hasAttr("charset")) {
				charset = meta.attr("charset");
				break;
			} else if (meta.hasAttr("http-equiv") && "Content-Type".equalsIgnoreCase(meta.attr("http-equiv"))) {
				if (meta.hasAttr("content") || meta.hasAttr("Content")) {
					if (meta.attr("content").indexOf("charset=") != -1) {
						charset = meta.attr("content").substring((meta.attr("content").indexOf("charset=") + 8));
						break;
					}
					if (meta.attr("content").indexOf("Charset=") != -1) {
						charset = meta.attr("content").substring((meta.attr("content").indexOf("Charset=") + 8));
						break;
					}
				}
			} else if (meta.hasAttr("content") && meta.attr("content").indexOf("charset=") != -1) {
				charset = meta.attr("content").substring((meta.attr("content").indexOf("charset=") + 8));
				break;
			} else if (meta.hasAttr("content") && meta.attr("content").indexOf("Charset=") != -1) {
				charset = meta.attr("content").substring((meta.attr("content").indexOf("Charset=") + 8));
				break;
			}
		}

		if (charset.indexOf(";no-cache") != -1) {
			charset = charset.substring(0, charset.indexOf(";no-cache"));
		}

		return charset;
	}

	/**
	 * @Description:从HTML中解析出title字段
	 * @author rockyqi1001@gmail.com
	 * @param htmlString
	 * @return
	 */
	private static String getTitle(String htmlString) {
		Document doc = Jsoup.parse(htmlString);
		if (null == doc || null == doc.title()) {
			return null;
		} else {
			return doc.title();
		}
	}

	/**
	 * @Description:从HTML中解析出keywords字段
	 * @author rockyqi1001@gmail.com
	 * @param htmlString
	 * @return
	 */
	private static String getKeywords(String htmlString) {
		String keywords = null;
		Document doc = Jsoup.parse(htmlString);
		if (null == doc) {
			return null;
		}
		Elements metas = doc.select("meta");
		for (Element meta : metas) {
			if ("keywords".equalsIgnoreCase(meta.attr("name"))) {
				keywords = meta.attr("content");
				break;
			}
		}
		return keywords;
	}

	/**
	 * @Description:从HTML中解析出全部正文
	 * @author rockyqi1001@gmail.com
	 * @param htmlString
	 * @return
	 */
	private static String getMainTextByTagP(String htmlString) {
		StringBuffer sb = new StringBuffer();
		Document doc = Jsoup.parse(htmlString);
		if (null == doc) {
			return null;
		}
		Elements elements = doc.select("p");
		if (null == elements) {
			return null;
		}
		for (Element e : elements) {
			sb.append(e.text());
		}
		return sb.toString();
	}

	/**
	 * @Description:从HTML中解析出全部正文
	 * @author rockyqi1001@gmail.com
	 * @param htmlString
	 * @return
	 * @throws BoilerpipeProcessingException
	 * @throws SAXException
	 * @throws IOException
	 */
	private static String getMainTextByBoilerpipe(URL url) throws BoilerpipeProcessingException, IOException {
		// choose from a set of useful BoilerpipeExtractors...
		 final ExtractorBase extractor = CommonExtractors.ARTICLE_EXTRACTOR;
		// final ExtractorBase extractor = CommonExtractors.DEFAULT_EXTRACTOR;
		// final ExtractorBase extractor = CommonExtractors.CANOLA_EXTRACTOR;
		// final ExtractorBase extractor = CommonExtractors.LARGEST_CONTENT_EXTRACTOR;
		
//		InputSource is = new InputSource();
//		is.setEncoding("GBK");
//		is.setByteStream(url.openStream());
//		return CommonExtractors.ARTICLE_EXTRACTOR.getText(is);
		
		return extractor.getText(url);
	}

	/**
	 * @Description:对map的value进行排序（从高到低），并且返回排名最高的X个key列表
	 * @author rockyqi1001@gmail.com
	 * @param wordsMap
	 * @return
	 */
	private static <T> List<T> sortMapByValueGetTopXKeys(HashMap<T, Integer> wordsMap, int topX) {
		List<T> topXKeysList = new ArrayList<T>();
		List<T> keys = sortMapByValue(wordsMap);
		// get top X words
		for (int i = 0; (i < topX && i < keys.size()); i++) {
			topXKeysList.add((keys.get(i)));
		}
		return topXKeysList;
	}

	/**
	 * @Description:统计key出现次数(score=1),或者对key进行打分(score是每个key出现时获取的分数)
	 * @author rockyqi1001@gmail.com
	 * @param keyList
	 * @param score
	 * @param resultMap
	 */
	private static <T> void countOrScoreWords(List<T> keyList, int score, HashMap<T, Integer> resultMap) {
		if (null == keyList || keyList.size() < 1) {
			return;
		}
		Iterator<T> it = keyList.iterator();
		while (it.hasNext()) {
			T key = it.next();
			// System.out.println(key);
			if (null == resultMap.get(key)) {
				resultMap.put(key, new Integer(score));
			} else {
				Integer sum = resultMap.get(key);
				resultMap.put(key, (sum + score));
			}
		}
	}

	private static <T> List<T> sortMapByValue(HashMap<T, Integer> wordsMap) {
		ByValueComparator<T> vc = new ByValueComparator<T>(wordsMap);
		List<T> keys = new ArrayList<T>(wordsMap.keySet());
		Collections.sort(keys, vc);
		// return sorted keys
		return keys;
	}

	private static class ByValueComparator<T> implements Comparator<T> {
		HashMap<T, Integer> base_map;

		public ByValueComparator(HashMap<T, Integer> base_map) {
			this.base_map = base_map;
		}

		public int compare(T arg0, T arg1) {
			if (!base_map.containsKey(arg0) || !base_map.containsKey(arg1)) {
				return 0;
			}
			if (base_map.get(arg0).intValue() < base_map.get(arg1).intValue()) {
				return 1;
			} else if (base_map.get(arg0).intValue() == base_map.get(arg1).intValue()) {
				return 0;
			} else {
				return -1;
			}
		}
	}

	private static void printStringList(List<String> stringList) {
		if (null == stringList) {
			return;
		}
		for (String tmp : stringList) {
			// System.out.println(tmp);
			logger.debug(tmp);
		}
	}

	public static void main(String[] args) throws BoilerpipeProcessingException, IOException, SAXException {
//		String urlString = "http://auto.163.com/14/1110/23/AANO1BGL00084TUP.html";
		String urlString = "http://news.163.com/14/1110/10/AAMC8KDJ00014AED.html";
//		String urlString = "http://server.chinabyte.com/399/12587899.shtml";
		URL url = new URL(urlString);
		// String url = "http://auto.ifeng.com/guonei/20130227/845114.shtml";
		// String url = "http://www.oschina.net/question/12_88019";
		// 1. get html string
		// String htmlString = CommonMethods.getSinglePageHtmlString(url,
		// false);
		// 2. parse all cn words in the html string
		// String result = processHtmlString(url, htmlString);
		// 3.get charset
		String mainText = getMainTextByBoilerpipe(url);
		System.out.println(mainText);
	}

}
