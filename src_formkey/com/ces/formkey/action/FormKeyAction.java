package com.ces.formkey.action;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import me.kafeitu.demo.activiti.util.UserUtil;
import net.sf.json.JSONObject;

import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.persistence.entity.SuspensionState;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.NativeTaskQuery;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;

import com.ces.common.action.CommonAction;
import com.ces.common.utils.PageUtil;
import com.ces.common.webbean.Page;
import com.ces.framework.json.util.JsonConverter;

/**
 * 外置表单Controller 了解不同表单请访问：
 * http://www.kafeitu.me/activiti/2012/08/05/diff-activiti-workflow-forms.html
 * 
 * @author hc
 */
public class FormKeyAction extends CommonAction {
	private static final long serialVersionUID = 1L;

	/**
	 * 动态form流程列表
	 * 
	 * @param model
	 * @return
	 */
	public String processList() {
		Page<ProcessDefinition> page = new Page<ProcessDefinition>(
				PageUtil.PAGE_SIZE);
		int[] pageParams = PageUtil.init(page, request);
		/*
		 * 只读取form key表单：leave-formkey
		 */
		ProcessDefinitionQuery query = repositoryService
				.createProcessDefinitionQuery()
				.processDefinitionKey("leave-formkey").active()
				.orderByDeploymentId().desc();
		List<ProcessDefinition> list = query.listPage(pageParams[0],
				pageParams[1]);

		page.setResult(list);
		page.setTotalCount(query.count());
		session.put("page", page);

		String message = request.getParameter("message");
		request.setAttribute("message", message);

		setForwardJsp("/views/form/formkey/formkey-process-list.jsp");
		return FORWARD;
	}

	/**
	 * 读取启动流程的表单内容
	 */
	public Object receiveStartForm() {
		String processDefinitionId = request
				.getParameter("processDefinitionId");
		// 根据流程定义ID读取外置表单
		Object startForm = formService
				.getRenderedStartForm(processDefinitionId);

		PrintWriter out = null;
		try {
			out = response.getWriter();
			
			out.print("'"+startForm.toString()+"'");
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 读取Task的表单
	 */
	public Object receiveTaskForm() {
		String taskId = request.getParameter("taskId");
		Object renderedTaskForm = formService.getRenderedTaskForm(taskId);

		PrintWriter out = null;
		try {
			out = response.getWriter();
			
			out.print("'"+renderedTaskForm.toString()+"'");
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
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

			/*
			 * 参数结构：fq_reason，用_分割 fp的意思是form paremeter 最后一个是属性名称
			 */
			if (StringUtils.defaultString(key).startsWith("fp_")) {
				String[] paramSplit = key.split("_");
				formProperties.put(paramSplit[1], entry.getValue()[0]);
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
			setRedirectJsp("formKey_taskList.action?message="
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

		User user = UserUtil.getUserFromSession(request.getSession());
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
			setRedirectJsp("formKey_processList.action?message="
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
	 * @param model
	 * @return
	 */
	public String taskList() {
		User user = UserUtil.getUserFromSession(request.getSession());
		Page<Task> page = new Page<Task>(PageUtil.PAGE_SIZE);
		int[] pageParams = PageUtil.init(page, request);

		/**
		 * 这里为了演示区分开自定义表单的请假流程，值读取leave-formkey
		 */

		// 已经签收的或者直接分配到当前人的任务
		String asigneeSql = "select distinct RES.* from ACT_RU_TASK RES inner join ACT_RE_PROCDEF D on RES.PROC_DEF_ID_ = D.ID_ WHERE RES.ASSIGNEE_ = #{userId}"
				+ " and D.KEY_ = #{processDefinitionKey} and RES.SUSPENSION_STATE_ = #{suspensionState}";

		// 当前人在候选人或者候选组范围之内
		String needClaimSql = "select distinct RES1.* from ACT_RU_TASK RES1 inner join ACT_RU_IDENTITYLINK I on I.TASK_ID_ = RES1.ID_ inner join ACT_RE_PROCDEF D1 on RES1.PROC_DEF_ID_ = D1.ID_ WHERE"
				+ " D1.KEY_ = #{processDefinitionKey} and RES1.ASSIGNEE_ is null and I.TYPE_ = 'candidate'"
				+ " and ( I.USER_ID_ = #{userId} or I.GROUP_ID_ IN (select g.GROUP_ID_ from ACT_ID_MEMBERSHIP g where g.USER_ID_ = #{userId} ) )"
				+ " and RES1.SUSPENSION_STATE_ = #{suspensionState}";
		String sql = asigneeSql + " union all " + needClaimSql;
		NativeTaskQuery query = taskService
				.createNativeTaskQuery()
				.sql(sql)
				.parameter("processDefinitionKey", "leave-formkey")
				.parameter("suspensionState",
						SuspensionState.ACTIVE.getStateCode())
				.parameter("userId", user.getId());
		List<Task> tasks = query.listPage(pageParams[0], pageParams[1]);

		page.setResult(tasks);
		page.setTotalCount(query
				.sql("select count(*) from (" + sql + ") as CT").count());

		session.put("page", page);
		String message = request.getParameter("message");
		request.setAttribute("message", message);

		setForwardJsp("/views/form/formkey/formkey-task-list.jsp");
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
			setRedirectJsp("formKey_taskList.action?message="
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
	 * @param model
	 * @return
	 */
	public String runningList() {
		Page<ProcessInstance> page = new Page<ProcessInstance>(
				PageUtil.PAGE_SIZE);
		int[] pageParams = PageUtil.init(page, request);
		ProcessInstanceQuery query = runtimeService
				.createProcessInstanceQuery()
				.processDefinitionKey("leave-formkey").active()
				.orderByProcessInstanceId().desc();
		List<ProcessInstance> list = query.listPage(pageParams[0],
				pageParams[1]);
		page.setResult(list);
		page.setTotalCount(query.count());
		session.put("page", page);

		setForwardJsp("/views/form/running-list.jsp");
		return FORWARD;
	}

	/**
	 * 已结束的流程实例
	 * 
	 * @param model
	 * @return
	 */
	public String finishedList() {
		Page<HistoricProcessInstance> page = new Page<HistoricProcessInstance>(
				PageUtil.PAGE_SIZE);
		int[] pageParams = PageUtil.init(page, request);
		HistoricProcessInstanceQuery query = historyService
				.createHistoricProcessInstanceQuery()
				.processDefinitionKey("leave-formkey")
				.orderByProcessInstanceEndTime().desc().finished();
		List<HistoricProcessInstance> list = query.listPage(pageParams[0],
				pageParams[1]);

		page.setResult(list);
		page.setTotalCount(query.count());
		session.put("page", page);

		setForwardJsp("/views/form/finished-list.jsp");
		return FORWARD;
	}

}
