package net.rockyqi.web.crawler.url.analyzer;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Thread.State;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;

/**
 * @Description:URL批量分析servlet
 * @author rockyqi1001@gmail.com
 * @version 1.0
 */
public class UrlAnalyzeServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8565592730284683908L;

	protected static final Logger logger = Logger.getLogger(UrlAnalyzeServlet.class.getName());
	
	private static Thread urlAnalyzeThread = null;
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String url = "./jsp/urlAnalyze.jsp";
		if (null != urlAnalyzeThread && urlAnalyzeThread.getState() != State.TERMINATED) {
			response.setStatus(HttpStatus.SC_PROCESSING);
			PrintWriter out = response.getWriter();
			out.write("Analyzer processing, please try again later...");
			out.close();
			return;
		}
		
		boolean isHadoopReq = false;
		String logFileName;
		// get log file name from request param.
		String msg = request.getParameter("msg");
		String filePathFromPage = request.getParameter("filepath");
		
		if (null == msg || "".equals(msg)) {
			isHadoopReq = false;
			logger.info("file path=" + filePathFromPage);
			logFileName = filePathFromPage;
			if (null == logFileName || "".equals(logFileName) || !new File(logFileName).exists()){
				request.setAttribute("result", "输入的路径有误，没有指向正确的文件！");
				request.getRequestDispatcher(url).forward(request, response);
				return;
			}
		} else {
			isHadoopReq = true;
			logger.info("msg=" + msg);
			logFileName = msg;
		}
		if (null == logFileName || "".equals(logFileName)) {
			return;
		}
		// start main thread to do url analyze
		ResultCnt.init();
		ResultCnt.LogName = msg;
		UrlAnalyzeThread.recrawlUrlList.clear();
		UrlAnalyzeThread.lackKeywordsUrlList.clear();
		urlAnalyzeThread = new Thread(new UrlAnalyzeThread(logFileName));
		urlAnalyzeThread.start();
		
		if (isHadoopReq) {
			response.setStatus(HttpStatus.SC_OK);
			PrintWriter out = response.getWriter();
			out.write("Start analyzing all urls, please wait...");
			out.close();
		} else {
			request.setAttribute("result", "任务提交成功！后台分析处理中...");
			request.getRequestDispatcher(url).forward(request, response);
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
