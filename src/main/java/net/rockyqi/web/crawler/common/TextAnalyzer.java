package net.rockyqi.web.crawler.common;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class TextAnalyzer {
	
	protected static final Logger logger = Logger.getLogger(TextAnalyzer.class.getName());
	
	private StringReader stringReader = null;
	
	private static Analyzer analyzer = null;

	public TextAnalyzer() {
		if (null == analyzer) {
			// 构建IK分词器，使用smart分词模式
			analyzer = new IKAnalyzer(false);
		}
	}

	public List<String> analyzeToWordsList(String content) {
		if (null == content || "".equals(content)) {
			return null;
		}
		logger.debug("analyzeToWordsList begin.");
		stringReader = new StringReader(content);
		List<String> wordsList = new ArrayList<String>();
		// 获取Lucene的TokenStream对象
		TokenStream ts = null;
		try {
			ts = analyzer.tokenStream("testField", stringReader);
			// 获取词元文本属性
			CharTermAttribute term = ts.addAttribute(CharTermAttribute.class);
			// 重置TokenStream（重置StringReader）
			ts.reset();
			// 迭代获取分词结果
			while (ts.incrementToken()) {
				wordsList.add(term.toString());
				logger.debug(term.toString());
			}
		} catch (IOException e) {
			logger.error(e, e);
		} finally {
			stringReader.close();
			// 释放TokenStream的所有资源
			if (ts != null) {
				try {
					ts.end();
					ts.close();
				} catch (IOException e) {
					logger.error(e, e);
				}
			}
		}
		logger.debug("analyzeToWordsList end.");
		return wordsList;
	}

	public static void main(String[] args) {
		TextAnalyzer analyzer = new TextAnalyzer();
		List<String> resultList = analyzer.analyzeToWordsList("解放军总参习近平我就看看我不说话这是测试对吗再来个奔驰和宝马试试看");
		System.out.println(resultList.size());
		for (String result : resultList) {
			System.out.println(result);
		}
	}
}
