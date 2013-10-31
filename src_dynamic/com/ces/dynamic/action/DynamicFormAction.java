package com.ces.dynamic.action;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import me.kafeitu.demo.activiti.util.UserUtil;

import org.activiti.engine.form.FormProperty;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.form.StartFormDataImpl;
import org.activiti.engine.impl.form.TaskFormDataImpl;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;

import com.ces.common.action.CommonAction;
import com.ces.common.utils.PageUtil;
import com.ces.common.webbean.Page;
import com.ces.framework.json.util.JsonConverter;

/**
 * 动态表单 了解不同表单请访问：
 * http://www.kafeitu.me/activiti/2012/08/05/diff-activiti-workflow-forms.html
 * 
 * @author hc
 */
public class DynamicFormAction extends CommonAction {
	private static final long serialVersionUID = 1L;

	/**
	 * 动态form流程列表
	 * 
	 * @return
	 */
	public String processList() {
		Page<ProcessDefinition> page = new Page<ProcessDefinition>(
				PageUtil.PAGE_SIZE);
		int[] pageParams = PageUtil.init(page, request);
		/*
		 * 只读取动态表单：leave-dynamic-from
		 */
		ProcessDefinitionQuery query1 = repositoryService
				.createProcessDefinitionQuery()
				.processDefinitionKey("leave-dynamic-from").active()
				.orderByDeploymentId().desc();
		List<ProcessDefinition> list = query1.listPage(pageParams[0],
				pageParams[1]);
		ProcessDefinitionQuery query2 = repositoryService
				.createProcessDefinitionQuery()
				.processDefinitionKey("dispatch").active()
				.orderByDeploymentId().desc();
		List<ProcessDefinition> dispatchList = query2.listPage(pageParams[0],
				pageParams[1]);
		list.addAll(dispatchList);

		page.setResult(list);
		page.setTotalCount(query1.count() + query2.count());
		session.put("page", page);

		String message = request.getParameter("message");
		request.setAttribute("message", message);

		setForwardJsp("/views/form/dynamic/dynamic-form-process-list.jsp");
		return FORWARD;
	}

	/**
	 * 读取启动流程的表单字段
	 */
	@SuppressWarnings("unchecked")
	public String receiveStartForm() {
		String processDefinitionId = request
				.getParameter("processDefinitionId");
		Map<String, Object> result = new HashMap<String, Object>();
		StartFormDataImpl startFormData = (StartFormDataImpl) formService
				.getStartFormData(processDefinitionId);
		startFormData.setProcessDefinition(null);

		/*
		 * 读取enum类型数据，用于下拉框
		 */
		List<FormProperty> formProperties = startFormData.getFormProperties();
		for (FormProperty formProperty : formProperties) {
			Map<String, String> values = (Map<String, String>) formProperty
					.getType().getInformation("values");
			if (values != null) {
				for (Entry<String, String> enumEntry : values.entrySet()) {
					logger.debug("enum, key: {" + enumEntry.getKey()
							+ "}, value: {" + enumEntry.getValue() + "}");
				}
				result.put("enum_" + formProperty.getId(), values);
			}
		}

		result.put("form", startFormData);

		JsonConverter.beanToJson(result, response);
		return null;
	}

	/**
	 * 读取Task的表单
	 */
	@SuppressWarnings("unchecked")
	public String receiveTaskForm() {
		String taskId = request.getParameter("taskId");
		Map<String, Object> result = new HashMap<String, Object>();
		
		TaskFormDataImpl taskFormData = (TaskFormDataImpl) formService
				.getTaskFormData(taskId);

		// 设置task为null，否则输出json的时候会报错
		taskFormData.setTask(null);

		result.put("taskFormData", taskFormData);
		/*
		 * 读取enum类型数据，用于下拉框
		 */
		List<FormProperty> formProperties = taskFormData.getFormProperties();
		for (FormProperty formProperty : formProperties) {
			Map<String, String> values = (Map<String, String>) formProperty
					.getType().getInformation("values");
			if (values != null) {
				for (Entry<String, String> enumEntry : values.entrySet()) {
					logger.debug("enum, key: {" + enumEntry.getKey()
							+ "}, value: {" + enumEntry.getValue() + "}");
				}
				result.put(formProperty.getId(), values);
			}
		}

		JsonConverter.beanToJson(result, response);
		return null;
	}

	/**
	 * 提交task的并保存form
	 */
	public String fulfill() {
		String taskId = request.getParameter("taskId");
		Map<String, String> formProperties = new HashMap<String, String>();

		// 从request中读取参数然后转换
		Map<String, String[]> parameterMap = request.getParameterMap();
		Set<Entry<String, String[]>> entrySet = parameterMap.entrySet();
		for (Entry<String, String[]> entry : entrySet) {
			String key = entry.getKey();

			// fp_的意思是form paremeter
			if (StringUtils.defaultString(key).startsWith("fp_")) {
				formProperties.put(key.split("_")[1], entry.getValue()[0]);
			}
		}

		logger.debug("start form parameters: {" + formProperties + "}");

		User user = UserUtil.getUserFromSession(request.getSession());

		// 用户未登录不能操作，实际应用使用权限框架实现，例如Spring Security、Shiro等
		if (user == null || StringUtils.isBlank(user.getId())) {
			return "redirect:/login?timeout=true";
		}
		identityService.setAuthenticatedUserId(user.getId());

		formService.submitTaskFormData(taskId, formProperties);

		String message = "任务完成：taskId=" + taskId;
		try {
			setRedirectJsp("dynamicForm_taskList.action?message="
					+ URLEncoder.encode(message, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return REDIRECT;
	}

	/**
	 * 读取启动流程的表单字段
	 */
	public String startProcess() {
		String processDefinitionId = request
				.getParameter("processDefinitionId");
		Map<String, String> formProperties = new HashMap<String, String>();

		// 从request中读取参数然后转换
		Map<String, String[]> parameterMap = request.getParameterMap();
		Set<Entry<String, String[]>> entrySet = parameterMap.entrySet();
		for (Entry<String, String[]> entry : entrySet) {
			String key = entry.getKey();

			// fp_的意思是form paremeter
			if (StringUtils.defaultString(key).startsWith("fp_")) {
				formProperties.put(key.split("_")[1], entry.getValue()[0]);
			}
		}

		logger.debug("start form parameters: {" + formProperties + "}");

		User user = UserUtil.getUserFromSession(session);
		// 用户未登录不能操作，实际应用使用权限框架实现，例如Spring Security、Shiro等
		if (user == null || StringUtils.isBlank(user.getId())) {
			return "redirect:/login?timeout=true";
		}
		identityService.setAuthenticatedUserId(user.getId());

		ProcessInstance processInstance = formService.submitStartFormData(
				processDefinitionId, formProperties);
		logger.debug("start a processinstance: {" + processInstance + "}");

		String message = "";
		message = "启动成功，流程ID：" + processInstance.getId();

		try {
			setRedirectJsp("dynamicForm_processList.action?message="
					+ URLEncoder.encode(message, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return REDIRECT;
	}

	/**
	 * task列表
	 * 
	 * @return
	 */
	public String taskList() {
		User user = UserUtil.getUserFromSession(session);

		List<Task> tasks = new ArrayList<Task>();

		/**
		 * 这里为了演示区分开自定义表单的请假流程，值读取leave-dynamic-from
		 * 在FormKeyController中有使用native方式查询的例子
		 */

		// 分配到当前登录用户的任务
		List<Task> list = taskService.createTaskQuery()
				.processDefinitionKey("leave-dynamic-from")
				.taskAssignee(user.getId()).active().orderByTaskId().desc()
				.list();

		// 为签收的任务
		List<Task> list2 = taskService.createTaskQuery()
				.processDefinitionKey("leave-dynamic-from")
				.taskCandidateUser(user.getId()).active().orderByTaskId()
				.desc().list();

		tasks.addAll(list);
		tasks.addAll(list2);

		List<Task> list3 = taskService.createTaskQuery()
				.processDefinitionKey("dispatch").taskAssignee(user.getId())
				.active().orderByTaskId().desc().list();

		// 为签收的任务
		List<Task> list4 = taskService.createTaskQuery()
				.processDefinitionKey("dispatch")
				.taskCandidateUser(user.getId()).active().orderByTaskId()
				.desc().list();

		tasks.addAll(list3);
		tasks.addAll(list4);

		session.put("tasks", tasks);
		String message = request.getParameter("message");
		request.setAttribute("message", message);

		setForwardJsp("/views/form/dynamic/dynamic-form-task-list.jsp");
		return FORWARD;
	}

	/**
	 * 签收任务
	 */
	public String claim() {
		String taskId = request.getParameter("taskId");
		String userId = UserUtil.getUserFromSession(session).getId();
		taskService.claim(taskId, userId);
		String message = "任务已签收";

		try {
			setRedirectJsp("dynamicForm_taskList.action?message="
					+ URLEncoder.encode(message, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return REDIRECT;
	}

	/**
	 * 运行中的流程实例
	 * 
	 * @return
	 */
	public String runningList() {
		Page<ProcessInstance> page = new Page<ProcessInstance>(
				PageUtil.PAGE_SIZE);
		int[] pageParams = PageUtil.init(page, request);
		ProcessInstanceQuery leaveDynamicQuery = runtimeService
				.createProcessInstanceQuery()
				.processDefinitionKey("leave-dynamic-from")
				.orderByProcessInstanceId().desc().active();
		List<ProcessInstance> list = leaveDynamicQuery.listPage(pageParams[0],
				pageParams[1]);
		ProcessInstanceQuery dispatchQuery = runtimeService
				.createProcessInstanceQuery().processDefinitionKey("dispatch")
				.active().orderByProcessInstanceId().desc();
		List<ProcessInstance> list2 = dispatchQuery.listPage(pageParams[0],
				pageParams[1]);
		list.addAll(list2);

		page.setResult(list);
		page.setTotalCount(leaveDynamicQuery.count() + dispatchQuery.count());
		session.put("page", page);

		setForwardJsp("/views/form/running-list.jsp");
		return FORWARD;
	}

	/**
	 * 已结束的流程实例
	 * 
	 * @return
	 */
	public String finishedList() {
		Page<HistoricProcessInstance> page = new Page<HistoricProcessInstance>(
				PageUtil.PAGE_SIZE);
		int[] pageParams = PageUtil.init(page, request);
		HistoricProcessInstanceQuery leaveDynamicQuery = historyService
				.createHistoricProcessInstanceQuery()
				.processDefinitionKey("leave-dynamic-from").finished()
				.orderByProcessInstanceEndTime().desc();
		List<HistoricProcessInstance> list = leaveDynamicQuery.listPage(
				pageParams[0], pageParams[1]);
		HistoricProcessInstanceQuery dispatchQuery = historyService
				.createHistoricProcessInstanceQuery()
				.processDefinitionKey("dispatch").finished()
				.orderByProcessInstanceEndTime().desc();
		List<HistoricProcessInstance> list2 = dispatchQuery.listPage(
				pageParams[0], pageParams[1]);
		list.addAll(list2);

		page.setResult(list);
		page.setTotalCount(leaveDynamicQuery.count() + dispatchQuery.count());

		session.put("page", page);

		setForwardJsp("/views/form/finished-list.jsp");
		return FORWARD;
	}

}
