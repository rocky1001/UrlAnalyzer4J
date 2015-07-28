package net.rockyqi.web.crawler.url.analyzer;

import java.util.concurrent.Callable;

import net.rockyqi.web.crawler.common.CommonMethods;
import net.rockyqi.web.crawler.common.CrawlerHelper;


public class UrlAnalyzeWorker implements Callable<String> {
	
	private String url;
	
	private boolean doRecrawl;
	
	public UrlAnalyzeWorker(String aUrl, boolean aDoRecrawl){
		url = aUrl;
		doRecrawl = aDoRecrawl;
	}

	@Override
	public String call() throws Exception {
		String htmlString = CommonMethods.getSinglePageHtmlString(url, false);
		if (null == htmlString || "".equals(htmlString)){
			// ����Ƿ�����Ҫ����ץȡ��URL�б�
			if (doRecrawl) {
				UrlAnalyzeThread.recrawlUrlList.add(url);
				return "";
			} else {
				ResultCnt.ConnetionErr.incrementAndGet();
				return "";
			}
		}
		String wordsCount = CrawlerHelper.processHtmlString(url, htmlString);
		if (null == wordsCount) {
			//���������URL����������������÷���
			return "";
		} else if ("".equals(wordsCount)) {
			//ȱ�ٹؼ��ʵ��·���ʧ�ܵ�URL����������������÷���
			ResultCnt.LackOfKeywords.incrementAndGet();
			UrlAnalyzeThread.lackKeywordsUrlList.add(url);
			return "";
		} else {
			//�Է����ɹ���URL�������ͷ����������
			ResultCnt.Success.incrementAndGet();
			return url + "||" + wordsCount;
		}
	}
}
