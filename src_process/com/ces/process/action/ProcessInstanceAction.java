package com.ces.process.action;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;

import com.ces.common.action.CommonAction;
import com.ces.common.utils.PageUtil;
import com.ces.common.webbean.Page;

public class ProcessInstanceAction extends CommonAction {
	private static final long serialVersionUID = 1L;

	public String running() {
		Page<ProcessInstance> page = new Page<ProcessInstance>(
				PageUtil.PAGE_SIZE);
		int[] pageParams = PageUtil.init(page, request);

		ProcessInstanceQuery processInstanceQuery = runtimeService
				.createProcessInstanceQuery();
		List<ProcessInstance> list = processInstanceQuery.listPage(
				pageParams[0], pageParams[1]);
		page.setResult(list);
		page.setTotalCount(processInstanceQuery.count());
		session.put("page", page);

		String message = request.getParameter("message");
		request.setAttribute("message", message);
		setForwardJsp("/views/workflow/running-manage.jsp");
		return FORWARD;
	}

	/**
	 * 挂起、激活流程实例
	 */
	public String updateState() {
		String processInstanceId = request.getParameter("processInstanceId");
		String state = request.getParameter("state");
		String message = "";
		if (state.equals("active")) {
			message = "已激活ID为[" + processInstanceId + "]的流程实例。";
			runtimeService.activateProcessInstanceById(processInstanceId);
		} else if (state.equals("suspend")) {
			runtimeService.suspendProcessInstanceById(processInstanceId);
			message = "已挂起ID为[" + processInstanceId + "]的流程实例。";
		}
		try {
			setRedirectJsp("processInstance_running.action?message="
					+ URLEncoder.encode(message, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return REDIRECT;
	}
}
