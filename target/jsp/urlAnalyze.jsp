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
            alert('������Ҫ�����ı�����־·��!');
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
  <div class="toolbar"><span class="tag-1"></span><p>URL���ݷ���ƽ̨</p><em><a href="<%=request.getContextPath()%>/index.jsp">������ҳ</a></em></div>
  <div class="datagrid">

  <table width="100%" border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td class="td_fill">��ȡ�ͷ�������</td>
        <td><ul>
          <li>1.������ʽ</li>
          <li>&nbsp;&nbsp;&nbsp;&nbsp;a.�ڵ�ַ��ֱ�ӷ���/UrlAnalyzeServlet?msg='��־����'</li>
          <li>&nbsp;&nbsp;&nbsp;&nbsp;b.�����������������뱾����־�ļ�·�������������ʼ������</li>
          <li>2.URL���ݷ������߶�URL�б��ļ��е�ȫ��URL����ץȡ�ͷ���,��д�����ļ�</li>
          <li>3.������ļ�ͨ��FTP��ʽ���͸�����ƽ̨</li>
        </ul></td>
      </tr>
      <form name="urlAnalyzeForm" action="<%=request.getContextPath()%>/UrlAnalyzeServlet" method="post">
      <tr>
        <td class="td_fill">���뱾����־�ļ�·��</td>
        <td class="td_fill">
            <INPUT class="ipt-text" style="WIDTH: 70%" id=filepath type="text" name=filepath><br />
            <A href="javascript:submit();">��ʼ����</A>
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
    <td class="span1">ִ�н��</td>
    <td class="span3"><%=result %> </td>
  </tr>
</table>
<%
  }
%>
  </div>
</div>

<div class="footer">Copyright @ 2013 <a href="mailto:rockyqi1001@gmail.com">rockyqi1001@gmail.com</a> ��ǰ�汾�ţ�<span class="ff0000"><%=AppConstants.VERSION %></span></div>
</body>
</html>
