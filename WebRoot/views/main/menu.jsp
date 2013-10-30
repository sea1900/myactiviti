<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<ul id="css3menu">
	<li class="topfirst"><a rel="<%=path %>/views/main/welcome.jsp">首页</a></li>
	<li>
		<a rel="#">请假（普通表单）</a>
		<ul>
			<li><a rel="leave_apply.action">请假申请(普通)</a></li>
			<li><a rel="leave_taskList.action">请假办理(普通)</a></li>
			<li><a rel="leave_runningList.action">运行中流程(普通)</a></li>
			<li><a rel="leave_finishedList.action">已结束流程(普通)</a></li>
		</ul>
	</li>
	<li>
		<a rel="#">动态表单</a>
		<ul>
			<li><a rel="form/dynamic/process-list">流程列表(动态)</a></li>
			<li><a rel="form/dynamic/task/list">任务列表(动态)</a></li>
			<li><a rel="form/dynamic/process-instance/running/list">运行中流程表(动态)</a></li>
			<li><a rel="form/dynamic/process-instance/finished/list">已结束流程(动态)</a></li>
		</ul>
	</li>
	<li>
		<a rel="#">外置表单</a>
		<ul>
			<li><a rel="form/formkey/process-list">流程列表(外置)</a></li>
			<li><a rel="form/formkey/task/list">任务列表(外置)</a></li>
			<li><a rel="form/formkey/process-instance/running/list">运行中流程表(外置)</a></li>
			<li><a rel="form/formkey/process-instance/finished/list">已结束流程(外置)</a></li>
		</ul>
	</li>
	<li>
		<a rel='#'>流程管理</a>
		<ul>
			<li><a rel='workflow_processList.action'>流程定义及部署管理</a></li>
			<li><a rel='processInstance_running.action'>运行中流程</a></li>
			<li><a rel='model_modelList.action'>模型工作区</a></li>
		</ul>
	</li>
</ul>