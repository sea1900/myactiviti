package com.ces.leave.listener;

import java.sql.Timestamp;
import java.util.Date;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.runtime.ProcessInstance;

import com.ces.leave.entity.Leave;
import com.ces.leave.service.LeaveService;

/**
 * 销假后处理器
 * <p>
 * 设置销假时间
 * </p>
 * <p>
 * 使用Spring代理，可以注入Bean，管理事物
 * </p>
 * 
 * @author hc
 */
public class ReportBackEndProcessor implements TaskListener {
	private static final long serialVersionUID = 1L;

	private LeaveService leaveService;
	private RuntimeService runtimeService;

	public void notify(DelegateTask delegateTask) {
		String processInstanceId = delegateTask.getProcessInstanceId();
		ProcessInstance processInstance = runtimeService
				.createProcessInstanceQuery()
				.processInstanceId(processInstanceId).singleResult();
		Leave leave = (Leave) leaveService.getEntity(Leave.class,new Long(processInstance
				.getBusinessKey()));

		Object realityStartTime = delegateTask.getVariable("realityStartTime");
		leave.setRealityStartTime(new Timestamp(((Date)realityStartTime).getTime()));
		Object realityEndTime = delegateTask.getVariable("realityEndTime");
		leave.setRealityEndTime(new Timestamp(((Date)realityEndTime).getTime()));

		leaveService.saveOrUpdate(leave);
	}

	public void setRuntimeService(RuntimeService runtimeService) {
		this.runtimeService = runtimeService;
	}

	public void setLeaveService(LeaveService leaveService) {
		this.leaveService = leaveService;
	}

}
