<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.ces.common.utils.PropertyFileUtil"%>
<%@page import="org.springframework.beans.factory.config.PropertiesFactoryBean"%>
<!DOCTYPE html>
<%
PropertyFileUtil.init();
%>
<html lang="en">
<head>
	<%@ include file="/common/global.jsp"%>
	<%@ include file="/common/meta.jsp" %>
    <title>Activiti-演示系统</title>
    <%@ include file="/common/include-base-styles.jsp" %>
	<%@ include file="/common/include-jquery-ui-theme.jsp" %>
    <link rel="stylesheet" type="text/css" href="<%=path%>/css/menu.css" />
    <%@ include file="/common/include-custom-styles.jsp" %>
	<link href="<%=path%>/css/main.css" type="text/css" rel="stylesheet"/>
	<style type="text/css">
	.ui-tabs-panel {height: 100%; width: 100%;}
	.ui-tabs .ui-tabs-nav li a {padding-right: .5em;}
	#tabs li .ui-icon-close { float: left; margin: 0.5em 0.2em 0 0; cursor: pointer; }
	#add_tab { cursor: pointer; }
	</style>

    <script src="<%=path%>/jquery/jquery-1.8.3.js" type="text/javascript"></script>
    <script src="<%=path%>/jquery/plugins/jui/jquery-ui-${themeVersion }.min.js" type="text/javascript"></script>
    <script src="<%=path%>/jquery/plugins/jui/extends/themeswitcher/jquery.ui.switcher.js" type="text/javascript"></script>
    <script src="<%=path%>/jquery/plugins/tools/jquery.cookie.js" type="text/javascript"></script>
	<script src="<%=path%>/jquery/plugins/jui/extends/layout/jquery.layout.min.js?v=1.3" type="text/javascript"></script>
	<script src='<%=path%>/jquery/common.js' type="text/javascript"></script>
    <script src='<%=path%>/js/main/main-frame.js' type="text/javascript"></script>
</head>
<body>
<!-- #TopPane -->
<div id="topPane" class="ui-layout-north ui-widget ui-widget-header ui-widget-content">
	<div style="padding-left:5px; font-size: 16px; margin-top: 1px;">
       	<table id="topTable" style="padding: 0px;margin: 0px;margin-top: -5px" width="100%">
       		<tr>
       			<td width="40px">
       				<img src="<%=path%>/images/logo.png" height="48" align="top"  style="margin-top:5px" />
       			</td>
       			<td>
       				<span style="font-size: 17px;color:#FFFF33">Activiti<br/>演示系统</span><br/>
       			</td>
       			<td>
       				<div style="float:right; color: #fff;font-size: 12px;margin-top: 2px">
		        		<div>
		        			<label for="username">欢迎：</label>
		        			<span title="角色：${groupNames }">${user.firstName } ${user.lastName }/${user.id }</span>
		        		</div>
		        		<div style="text-align: right;">
		        			<a id="chang-theme" href="#">切换风格</a>
		       				<a href="#" id="loginOut">安全退出</a>
		        		</div>
		        	</div>
       			</td>
       		</tr>
       	</table>
       </div>
</div>

<!-- RightPane -->
<div id="centerPane" class="ui-layout-center ui-helper-reset ui-widget-content">
	<div id="tabs">
		<ul><li><a class="tabs-title" href="#tab-index">首页</a><span class='ui-icon ui-icon-close' title='关闭标签页'></span></li></ul>
		<div id="tab-index">
			<iframe id="mainIframe" name="mainIframe" src="<%=path%>/views/main/welcome.jsp" class="module-iframe" scrolling="auto" frameborder="0" style="width:100%;height:100%;"></iframe>
		</div>
	</div>
</div>

<!-- #BottomPane -->
<div id="bottomPane" class="ui-layout-south ui-widget ui-widget-content">
	<div class="footer ui-state-default">
		<span class="copyright">©2008-2013</span>
		<span class="version">Version：</span>
	</div>
</div>
<%@ include file="menu.jsp" %>
<div id="themeswitcherDialog"><div id="themeSwitcher"></div></div>
</body>
</html>
