package com.ces.leave.action;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import me.kafeitu.demo.activiti.util.UserUtil;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.identity.User;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ces.common.action.CommonAction;
import com.ces.leave.entity.Leave;
import com.ces.leave.service.LeaveService;

public class LeaveAction extends CommonAction {

	private LeaveService leaveService;

	/**
	 * 启动请假流程
	 * 
	 * @param leave
	 */
	@RequestMapping(value = "start", method = RequestMethod.POST)
	public String startWorkflow(Leave leave,
			RedirectAttributes redirectAttributes, HttpSession session) {
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
			redirectAttributes.addFlashAttribute("message", "流程已启动，流程ID："
					+ processInstance.getId());
		} catch (ActivitiException e) {
			if (e.getMessage().indexOf("no processes deployed with key") != -1) {
				logger.warn("没有部署流程!", e);
				redirectAttributes.addFlashAttribute("error",
						"没有部署流程，请在[工作流]->[流程管理]页面点击<重新部署流程>");
			} else {
				logger.error("启动请假流程失败：", e);
				redirectAttributes.addFlashAttribute("error", "系统内部错误！");
			}
		} catch (Exception e) {
			logger.error("启动请假流程失败：", e);
			redirectAttributes.addFlashAttribute("error", "系统内部错误！");
		}
		return "redirect:/oa/leave/apply";
	}

	public void setLeaveService(LeaveService leaveService) {
		this.leaveService = leaveService;
	}
}
