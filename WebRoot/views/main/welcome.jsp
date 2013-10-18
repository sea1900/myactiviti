<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<!doctype html>
<html lang="en">
<head>
	<%@ include file="/common/global.jsp"%>
	<%@ include file="/common/meta.jsp"%>

	<%@ include file="/common/include-base-styles.jsp" %>
    <%@ include file="/common/include-jquery-ui-theme.jsp" %>
    <link href="<%=path%>/jquery/plugins/jui/extends/portlet/jquery.portlet.min.css?v=1.1.2" type="text/css" rel="stylesheet" />
    <link href="<%=path%>/jquery/plugins/qtip/jquery.qtip.css?v=1.1.2" type="text/css" rel="stylesheet" />
    <%@ include file="/common/include-custom-styles.jsp" %>
    <style type="text/css">
    	.template {display:none;}
    	.version {margin-left: 0.5em; margin-right: 0.5em;}
    	.trace {margin-right: 0.5em;}
        .center {
            width: 1200px;
            margin-left:auto;
            margin-right:auto;
        }
    </style>

    <script src="<%=path%>/jquery/jquery-1.8.3.js" type="text/javascript"></script>
    <script src="<%=path%>/jquery/plugins/jui/jquery-ui-${themeVersion }.min.js" type="text/javascript"></script>
    <script src="<%=path%>/jquery/plugins/jui/extends/portlet/jquery.portlet.pack.js?v=1.1.2" type="text/javascript"></script>
    <script src="<%=path%>/jquery/plugins/qtip/jquery.qtip.pack.js" type="text/javascript"></script>
	<script src="<%=path%>/jquery/plugins/html/jquery.outerhtml.js" type="text/javascript"></script>
	<script src="<%=path%>/js/activiti/workflow.js" type="text/javascript"></script>
    <script src="<%=path%>/js/main/welcome-portlet.js" type="text/javascript"></script>
</head>
<body style="margin-top: 1em;">
	<div class="center">
        <div style="text-align: center;">
            <h3>BPMN2.0规范的轻量级工作流引擎Activiti</h3>
        </div>
        <div id='portlet-container'></div>
    </div>
    <!-- 隐藏 -->
    <div class="forms template">
        <ul>
            <li>
                <b>普通表单</b>：每个节点的表单内容都写死在JSP或者HTML中。
            </li>
            <li>
                <b>动态表单</b>：表单内容存放在流程定义文件中（包含在启动事件以及每个用户任务中）。
            </li>
            <li>
                <b>外置表单</b>：每个用户任务对应一个单独的<b>.form</b>文件，和流程定义文件同时部署（打包为zip/bar文件）。
            </li>
        </ul>
    </div>
    <div class="arch template">
        <ul>
            <li>
                Activiti版本：公共版本（${prop['activiti.version']}）
                <c:if test="${prop['activiti.version'] != prop['activiti.engine.version']}">&nbsp;引擎<strong>特定</strong>版本（${prop['activiti.engine.version']}）</c:if>
            </li>
            <li>Struts版本：2.3</li>
            <li>Spring版本：3.3</li>
            <li>Hibernate：3</li>
        </ul>
    </div>

    <div class="demos template">
        <ul>
            <li>部署流程</li>
            <li>启动流程</li>
            <li>任务签收</li>
            <li>任务办理</li>
            <li>驳回请求</li>
            <li>查询运行中流程</li>
            <li>查询历史流程</li>
            <li>任务监听</li>
            <li>自定义表单</li>
            <li>动态表单</li>
            <li>外置表单</li>
            <li>个人待办任务汇总</li>
            <li>分页查询(<font color='red'>New</font>)</li>
            <li>流程定义缓存(<font color='red'>New</font>)</li>
            <li>集成Activiti Modeler(<font color='red'>New</font>)</li>
        </ul>
    </div>

	<div class="project-info template">
        <ul>
            <li><a target="_blank" href='http://www.activiti.org/'>官方网站</a></li>
            <li>下载<a target="_blank" href='http://www.activiti.org/download.html'>http://www.activiti.org/download.html</a></li>
        </ul>
    </div>
    
    <div class="links template">
    	abbbbbbbbbbbbbbb
    </div>
</body>
</html>
