<%@ page language="java" contentType="text/html;charset=gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<%@ page import="net.rockyqi.web.crawler.common.AppConstants"%>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312" />
<title>url analyzer</title>
<link href="styles/global.css" rel="stylesheet" type="text/css" />
</head>
<body style="background:#f9f9f9">
<div class="header">
    <div class="pg_logo"></div>    
</div>
<div class="main-container">
    
    <h1 style="margin-top:40px;"><span class="num">1</span>URL内容分析平台</h1>
      <div class="btn"><a class="btn-large" href="<%=request.getContextPath()%>/jsp/urlAnalyze.jsp"></a></div>
      <div class="text"><a href="<%=request.getContextPath()%>/jsp/progress.jsp">查看正在运行的分析工作</a></div>
    <h1 style="margin-top:40px;"><span class="num">2</span>URL内容分析测试平台</h1>
      <div class="btn"><a class="btn-large" href="<%=request.getContextPath()%>/jsp/contentAnalyze.jsp"></a></div>
   
</div>
<div class="footer">Copyright @ 2013 <a href="mailto:rockyqi1001@gmail.com">rockyqi1001@gmail.com</a> 当前版本号：<span class="ff0000"><%=AppConstants.VERSION %></span></div>
</body>
</html>