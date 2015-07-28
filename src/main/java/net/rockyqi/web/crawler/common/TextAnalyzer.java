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
			// ����IK�ִ�����ʹ��smart�ִ�ģʽ
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
		// ��ȡLucene��TokenStream����
		TokenStream ts = null;
		try {
			ts = analyzer.tokenStream("testField", stringReader);
			// ��ȡ��Ԫ�ı�����
			CharTermAttribute term = ts.addAttribute(CharTermAttribute.class);
			// ����TokenStream������StringReader��
			ts.reset();
			// ������ȡ�ִʽ��
			while (ts.incrementToken()) {
				wordsList.add(term.toString());
				logger.debug(term.toString());
			}
		} catch (IOException e) {
			logger.error(e, e);
		} finally {
			stringReader.close();
			// �ͷ�TokenStream��������Դ
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
		List<String> resultList = analyzer.analyzeToWordsList("��ž��ܲ�ϰ��ƽ�ҾͿ����Ҳ�˵�����ǲ��Զ������������ۺͱ������Կ�");
		System.out.println(resultList.size());
		for (String result : resultList) {
			System.out.println(result);
		}
	}
}
