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
			// 检查是否是需要重新抓取的URL列表
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
			//解析错误的URL，仅计数，结果不用返回
			return "";
		} else if ("".equals(wordsCount)) {
			//缺少关键词导致分析失败的URL，仅计数，结果不用返回
			ResultCnt.LackOfKeywords.incrementAndGet();
			UrlAnalyzeThread.lackKeywordsUrlList.add(url);
			return "";
		} else {
			//对分析成功的URL做计数和分析结果返回
			ResultCnt.Success.incrementAndGet();
			return url + "||" + wordsCount;
		}
	}
}
