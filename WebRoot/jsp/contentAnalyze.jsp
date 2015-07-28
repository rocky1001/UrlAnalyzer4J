<%@ page language="java" contentType="text/html;charset=gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="net.rockyqi.web.crawler.common.AppConstants"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312" />
<title>webcrawler</title>
<link href="<%=request.getContextPath()%>/styles/global.css" rel="stylesheet" type="text/css" />
<script type="text/javascript">
    function submit() {
        var url = document.contentAnalyzeForm.url.value;
        if ("" == url) {
            alert('请输入要分析的URL!');
            return;
        }
        document.contentAnalyzeForm.submit();
    }
</script>
</head>

<body>
<div class="header">
    <div class="pg_logo"></div>    
</div>
<div class="container">
  <div class="toolbar"><span class="tag-1"></span><p>网页内容抓取和分析</p><em><a href="<%=request.getContextPath()%>/index.jsp">返回首页</a></em></div>
  <div class="datagrid">
<form name="contentAnalyzeForm" action="<%=request.getContextPath()%>/ContentAnalyzeServlet" method="post">
  <table width="100%" border="0" cellspacing="0" cellpadding="0">
      
      <tr>
        <td class="td_fill">请输入URL</td>
        <td>
          <INPUT class="ipt-text" style="WIDTH: 70%" id=url type="text" name=url><br />
          <A href="javascript:submit();">开始抓取并分析</A>
        </td>
      </tr>
      
      <tr>
        <td colspan=2><div id="urlDiv"></div></td>
      </tr>
  </table>
</form>
<%
  String singleLabel = (String) request.getAttribute("singleLabel");
  if (null != singleLabel && !"".equals(singleLabel)) {
%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td class="span1">URL</td>
    <td class="span3"><%=singleLabel %> </td>
  </tr>
  <%
    String marks = (String) request.getAttribute("marks");
  %>
         
  <tr>
    <td class="span1">词汇出现次数</td>
    <td class="span3">
      <table>
          <tr>
            <td class="span3"><%=marks %></td>
          </tr>
      </table>
     </td>
  </tr>
</table>
<%
  }
%>
  </div>
</div>

<div class="footer">Copyright @ 2013 <a href="mailto:rockyqi1001@gmail.com">rockyqi1001@gmail.com</a> 当前版本号：<span class="ff0000"><%=AppConstants.VERSION %></span></div>
</body>
</html>
