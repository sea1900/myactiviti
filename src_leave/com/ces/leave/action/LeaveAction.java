package com.ces.leave.action;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.identity.User;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.lang3.StringUtils;

import com.ces.common.action.CommonAction;
import com.ces.common.utils.PageUtil;
import com.ces.common.webbean.Page;
import com.ces.common.webbean.Variable;
import com.ces.framework.json.util.JsonConverter;
import com.ces.leave.entity.Leave;
import com.ces.leave.service.LeaveService;

public class LeaveAction extends CommonAction {
	private static final long serialVersionUID = 1L;

	private LeaveService leaveService;

	private Leave leave;
	private Variable var;

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
			User user = (User)session.get("user");
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

		String userId = ((User)session.get("user")).getId();
		leaveService.findTodoTasks(userId, page, pageParams);

		session.put("page", page);

		String message = request.getParameter("message");
		request.setAttribute("message", message);
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
	public String claim() {
		String taskId = request.getParameter("taskId");

		String userId = ((User)session.get("user")).getId();
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
	public String detail() {
		String id = request.getParameter("id");
		Leave leave = (Leave) leaveService.findEntityById(Leave.class,
				Long.valueOf(id));

		JsonConverter.beanToJson(leave, response);
		return null;
	}

	/**
	 * 读取带变量的请假信息
	 * 
	 */
	public String detailWithVars() {
		String leaveId = request.getParameter("leaveId");
		String taskId = request.getParameter("taskId");
		Leave leave = (Leave) leaveService.findEntityById(Leave.class,
				Long.valueOf(leaveId));
		Map<String, Object> variables = taskService.getVariables(taskId);
		leave.setVariables(variables);

		JsonConverter.beanToJson(leave, response);
		return null;
	}

	/**
	 * 完成任务
	 * 
	 */
	public String fulfill() {
		String taskId = request.getParameter("taskId");
		PrintWriter out = null;
		try {
			out = response.getWriter();

			Map<String, Object> variables = var.getVariableMap();
			taskService.complete(taskId, variables);
			out.print("success");
		} catch (Exception e) {
			logger.error("error on complete task {}, variables={}", e);
			out.print("error");
		}

		return null;
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

	public Variable getVar() {
		return var;
	}

	public void setVar(Variable var) {
		this.var = var;
	}
}
