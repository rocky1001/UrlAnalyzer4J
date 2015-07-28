package net.rockyqi.web.crawler.content.analyzer;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.rockyqi.web.crawler.common.CommonMethods;
import net.rockyqi.web.crawler.common.CrawlerHelper;

import org.apache.log4j.Logger;


/**
 * @Description:对单个以及列表形式提交的URL的html中的中文内容，进行分词和统计的servlet
 * @author rockyqi1001@gmail.com
 * @version 1.0
 */
public class ContentAnalyzeServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2050927247249785524L;

	protected static final Logger logger = Logger.getLogger(ContentAnalyzeServlet.class.getName());

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String url = "./jsp/contentAnalyze.jsp";
		String analyzeUrl = request.getParameter("url");
		logger.info("Analyze url=" + analyzeUrl);

		String analyzeResult = CrawlerHelper.processHtmlString(analyzeUrl, CommonMethods.getSinglePageHtmlString(analyzeUrl, true));

		request.setAttribute("singleLabel", analyzeUrl);
		request.setAttribute("marks", analyzeResult);
		// return result to jsp
		request.setAttribute("result", "success");
		request.getRequestDispatcher(url).forward(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
