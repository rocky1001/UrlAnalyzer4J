<%@ page language="java" contentType="text/html;charset=gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="net.rockyqi.web.crawler.common.AppConstants"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312" />
<title>url analyzer</title>
<link href="<%=request.getContextPath()%>/styles/global.css" rel="stylesheet" type="text/css" /></script>
<script type="text/javascript">
    function submit() {
        var filepath = document.urlAnalyzeForm.filepath.value;
        if ("" == filepath) {
            alert('请输入要分析的本地日志路径!');
            return;
        }
        document.urlAnalyzeForm.submit();
    }
</script>
</head>

<body>
<div class="header">
    <div class="pg_logo"></div>    
</div>
<div class="container">
  <div class="toolbar"><span class="tag-1"></span><p>URL内容分析平台</p><em><a href="<%=request.getContextPath()%>/index.jsp">返回首页</a></em></div>
  <div class="datagrid">

  <table width="100%" border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td class="td_fill">获取和分析流程</td>
        <td><ul>
          <li>1.触发方式</li>
          <li>&nbsp;&nbsp;&nbsp;&nbsp;a.在地址栏直接访问/UrlAnalyzeServlet?msg='日志名称'</li>
          <li>&nbsp;&nbsp;&nbsp;&nbsp;b.在下面的输入框中输入本地日志文件路径，并点击“开始分析”</li>
          <li>2.URL内容分析工具对URL列表文件中的全部URL进行抓取和分析,并写入结果文件</li>
          <li>3.将结果文件通过FTP方式发送给分析平台</li>
        </ul></td>
      </tr>
      <form name="urlAnalyzeForm" action="<%=request.getContextPath()%>/UrlAnalyzeServlet" method="post">
      <tr>
        <td class="td_fill">输入本地日志文件路径</td>
        <td class="td_fill">
            <INPUT class="ipt-text" style="WIDTH: 70%" id=filepath type="text" name=filepath><br />
            <A href="javascript:submit();">开始分析</A>
        </td>
      </tr>
      </form>
  </table>

<%
  String result = (String) request.getAttribute("result");
  if (null != result && !"".equals(result)) {
%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td class="span1">执行结果</td>
    <td class="span3"><%=result %> </td>
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
