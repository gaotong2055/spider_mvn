<%@ page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.util.JdbcUtil"%>
<%@page import="java.sql.PreparedStatement"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'addcnt.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
  </head>
  <body>
  		<%
		String ip = request.getRemoteHost();
		//System.out.println(ip);
  if(session.getAttribute("login") == null && !ip.equals("127.0.0.1") && !ip.equals("202.108.77.25")){
  	response.sendRedirect("index.jsp");
  }
   %>
   
   <%
   String m = request.getParameter("m");
   int mi = Integer.parseInt(m);
   System.out.println(mi);
   Connection conn = JdbcUtil.getConnection();
   String sql[] = new String[]{
   	"update wp_postmeta set meta_value= FLOOR( 20+RAND() *50) where meta_key='post_views_count' and meta_value < 50;",
   	"update wp_postmeta set meta_value= FLOOR( 50+RAND() *100) where meta_key='post_views_count' and meta_value >= 50 and meta_value < 200;",
   	"update wp_postmeta set meta_value= FLOOR( 200+RAND() *300) where meta_key='post_views_count' and meta_value >= 200 and meta_value < 600;"
   };
   if(mi >= 0 && mi <= 2){
   		PreparedStatement ps = conn.prepareStatement(sql[mi]);
   		int res = ps.executeUpdate();
   		out.println("update <br>" + res + "  <br>lines");
   		System.out.println(sql[mi]);
   }
   conn.close();
    %>
  </body>
</html>
