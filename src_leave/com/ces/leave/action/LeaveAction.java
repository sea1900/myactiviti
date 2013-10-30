package com.ces.leave.action;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import me.kafeitu.demo.activiti.util.UserUtil;
import me.kafeitu.demo.activiti.util.Variable;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.identity.User;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ces.common.action.CommonAction;
import com.ces.common.utils.PageUtil;
import com.ces.common.webbean.Page;
import com.ces.leave.entity.Leave;
import com.ces.leave.service.LeaveService;

public class LeaveAction extends CommonAction {

	private LeaveService leaveService;

	private Leave leave;

	public String apply() {
		String message = request.getParameter("message");
		request.setAttribute("message", message);

		setForwardJsp("views/oa/leave/leaveApply.jsp");

		return FORWARD;
	}

	/**
	 * 启动请假流程
	 * 
	 * @param leave
	 */
	public String start() {
		String message = "";
		try {
			User user = UserUtil.getUserFromSession(session);
			// 用户未登录不能操作，实际应用使用权限框架实现，例如Spring Security、Shiro等
			if (user == null || StringUtils.isBlank(user.getId())) {
				return "redirect:/login?timeout=true";
			}
			leave.setUserId(user.getId());
			Map<String, Object> variables = new HashMap<String, Object>();
			ProcessInstance processInstance = leaveService.startWorkflow(leave,
					variables);
			message = "流程已启动，流程ID：" + processInstance.getId();
		} catch (ActivitiException e) {
			if (e.getMessage().indexOf("no processes deployed with key") != -1) {
				logger.warn("没有部署流程!", e);
				message = "没有部署流程，请在[工作流]->[流程管理]页面点击<重新部署流程>";
			} else {
				logger.error("启动请假流程失败：", e);
				message = "系统内部错误！";
			}
		} catch (Exception e) {
			logger.error("启动请假流程失败：", e);
			message = "系统内部错误！";
		}

		try {
			setRedirectJsp("leave_apply.action?message="
					+ URLEncoder.encode(message, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return REDIRECT;
	}

	/**
	 * 任务列表
	 * 
	 */
	public String taskList() {
		Page<Leave> page = new Page<Leave>(PageUtil.PAGE_SIZE);
		int[] pageParams = PageUtil.init(page, request);

		String userId = UserUtil.getUserFromSession(session).getId();
		leaveService.findTodoTasks(userId, page, pageParams);

		session.put("page", page);

		setForwardJsp("/views/oa/leave/taskList.jsp");
		return FORWARD;
	}

	/**
	 * 读取运行中的请假流程实例
	 * 
	 * @return
	 */
	public String runningList() {
		Page<Leave> page = new Page<Leave>(PageUtil.PAGE_SIZE);
		int[] pageParams = PageUtil.init(page, request);
		leaveService.findRunningProcessInstaces(page, pageParams);
		session.put("page", page);

		setForwardJsp("/views/oa/leave/running.jsp");
		return FORWARD;
	}

	/**
	 * 读取运行中的流程实例
	 * 
	 * @return
	 */
	public String finishedList() {
		Page<Leave> page = new Page<Leave>(PageUtil.PAGE_SIZE);
		int[] pageParams = PageUtil.init(page, request);
		leaveService.findFinishedProcessInstaces(page, pageParams);
		session.put("page", page);

		setForwardJsp("/views/oa/leave/finished.jsp");
		return FORWARD;
	}

	/**
	 * 签收任务
	 */
	@RequestMapping(value = "task/claim/{id}")
	public String claim() {
		String taskId = request.getParameter("taskId");

		String userId = UserUtil.getUserFromSession(session).getId();
		taskService.claim(taskId, userId);
		try {
			setRedirectJsp("leave_taskList.action?message="
					+ URLEncoder.encode("任务已签收", "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return REDIRECT;
	}

	/**
	 * 读取详细数据
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "detail/{id}")
	public String getLeaves() {
		String id = request.getParameter("id");
		// Leave leave = leaveManager.getLeave(id);
		return null;
	}

	/**
	 * 读取详细数据
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "detail-with-vars/{id}/{taskId}")
	@ResponseBody
	public Leave getLeaveWithVars(@PathVariable("id") Long id,
			@PathVariable("taskId") String taskId) {
		// Leave leave = leaveManager.getLeave(id);
		// Map<String, Object> variables = taskService.getVariables(taskId);
		// leave.setVariables(variables);
		return leave;
	}

	/**
	 * 完成任务
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "complete/{id}", method = { RequestMethod.POST,
			RequestMethod.GET })
	@ResponseBody
	public String complete(@PathVariable("id") String taskId, Variable var) {
		try {
			Map<String, Object> variables = var.getVariableMap();
			taskService.complete(taskId, variables);
			return "success";
		} catch (Exception e) {
			logger.error("error on complete task {}, variables={}", e);
			return "error";
		}
	}

	public void setLeaveService(LeaveService leaveService) {
		this.leaveService = leaveService;
	}

	public Leave getLeave() {
		return leave;
	}

	public void setLeave(Leave leave) {
		this.leave = leave;
	}
}
