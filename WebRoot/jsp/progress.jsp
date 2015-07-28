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
  <div class="toolbar"><span class="tag-1"></span><p>URL���ݷ���ƽ̨</p><em><a href="<%=request.getContextPath()%>/index.jsp">������ҳ</a></em></div>
  <div class="datagrid">
    <table width="100%" border="1" cellspacing="0" cellpadding="0">
      <tr>
        <td class="td_fill">���ڷ�����URL�б�</td>
        <td class="td_fill" align="left">
           <b><%=ResultCnt.LogName %></b>
        </td>
      </tr>
      <tr>
        <td class="td_fill">��������</td>
        <td class="td_fill" align="left">
                            ����ɷ�����/��������URL����:&nbsp;&nbsp;<b><%=ResultCnt.getProcessingTotalNum() %>/<font color="0000ff"><%=ResultCnt.OriginTotalNum %></font></b>
        </td>
      </tr>
      <tr>
        <td class="td_fill">����������ϸ</td>
        <td class="td_fill">
          <table border="0" cellspacing="0" cellpadding="0" align="left">
            <tr align="left">
                <td>�����ɹ���URL��Ŀ:</td>
                <td><b><%=ResultCnt.Success.toString() %></b></td>
            </tr>
            <tr align="left">
                <td>ȱ�ٹؼ������õ�URL��Ŀ:</td>
                <td><b><%=ResultCnt.LackOfKeywords.toString() %></b></td>
            </tr>
          </table>
        &nbsp;&nbsp;&nbsp;&nbsp;
          <table border="0" cellspacing="0" cellpadding="0" align="left">
            <tr align="left">
                <td>�������η�������������ʧ�ܵ�URL��Ŀ:</td>
                <td><b><%=ResultCnt.ConnetionErr.toString() %></b></td>
            </tr>
            <tr align="left">
                <td>����һ������ʧ�ܣ��������·�����URL��Ŀ��<%=ResultCnt.Recrawl %>��</td>
                <td><b>&nbsp;</b></td>
            </tr>
            <tr align="left">
                <td>��ȡ����htmlԴ���URL��Ŀ:</td>
                <td><b><%=ResultCnt.NoContent.toString() %></b></td>
            </tr>
            <tr align="left">
                <td>ҳ��charset�޷�������URL��Ŀ:</td>
                <td><b><%=ResultCnt.InvalidCharset.toString() %></b></td>
            </tr>
          </table>           
        </td>
      </tr>     
    </table>
 </div>
</div>

<div class="footer">Copyright @ 2013 <a href="mailto:rockyqi1001@gmail.com">rockyqi1001@gmail.com</a> ��ǰ�汾�ţ�<span class="ff0000"><%=AppConstants.VERSION %></span></div>
</body>
</html>