<%@ page language="java" contentType="text/html;charset=gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="net.rockyqi.web.crawler.url.analyzer.ResultCnt"%>
<%@ page import="net.rockyqi.web.crawler.common.AppConstants"%>
<html>
<head>
<title>url analyzer</title>
<link href="<%=request.getContextPath()%>/styles/global.css" rel="stylesheet" type="text/css" /></script>
</head>
<body>
<div class="header">
    <div class="pg_logo"></div>    
</div>
<div class="container">
  <div class="toolbar"><span class="tag-1"></span><p>URL内容分析平台</p><em><a href="<%=request.getContextPath()%>/index.jsp">返回首页</a></em></div>
  <div class="datagrid">
    <table width="100%" border="1" cellspacing="0" cellpadding="0">
      <tr>
        <td class="td_fill">正在分析的URL列表</td>
        <td class="td_fill" align="left">
           <b><%=ResultCnt.LogName %></b>
        </td>
      </tr>
      <tr>
        <td class="td_fill">分析进度</td>
        <td class="td_fill" align="left">
                            已完成分析数/待分析的URL总数:&nbsp;&nbsp;<b><%=ResultCnt.getProcessingTotalNum() %>/<font color="0000ff"><%=ResultCnt.OriginTotalNum %></font></b>
        </td>
      </tr>
      <tr>
        <td class="td_fill">分析进度明细</td>
        <td class="td_fill">
          <table border="0" cellspacing="0" cellpadding="0" align="left">
            <tr align="left">
                <td>分析成功的URL数目:</td>
                <td><b><%=ResultCnt.Success.toString() %></b></td>
            </tr>
            <tr align="left">
                <td>缺少关键字配置的URL数目:</td>
                <td><b><%=ResultCnt.LackOfKeywords.toString() %></b></td>
            </tr>
          </table>
        &nbsp;&nbsp;&nbsp;&nbsp;
          <table border="0" cellspacing="0" cellpadding="0" align="left">
            <tr align="left">
                <td>经过两次分析，最终连接失败的URL数目:</td>
                <td><b><%=ResultCnt.ConnetionErr.toString() %></b></td>
            </tr>
            <tr align="left">
                <td>（第一次连接失败，进行重新分析的URL数目：<%=ResultCnt.Recrawl %>）</td>
                <td><b>&nbsp;</b></td>
            </tr>
            <tr align="left">
                <td>获取不到html源码的URL数目:</td>
                <td><b><%=ResultCnt.NoContent.toString() %></b></td>
            </tr>
            <tr align="left">
                <td>页面charset无法解析的URL数目:</td>
                <td><b><%=ResultCnt.InvalidCharset.toString() %></b></td>
            </tr>
          </table>           
        </td>
      </tr>     
    </table>
 </div>
</div>

<div class="footer">Copyright @ 2013 <a href="mailto:rockyqi1001@gmail.com">rockyqi1001@gmail.com</a> 当前版本号：<span class="ff0000"><%=AppConstants.VERSION %></span></div>
</body>
</html>