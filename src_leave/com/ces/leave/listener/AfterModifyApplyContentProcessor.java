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
 * 调整请假内容处理器
 * 
 * @author hc
 */
public class AfterModifyApplyContentProcessor implements TaskListener {

	private static final long serialVersionUID = 1L;

	private LeaveService leaveService;

	private RuntimeService runtimeService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.activiti.engine.delegate.TaskListener#notify(org.activiti.engine.
	 * delegate.DelegateTask)
	 */
	public void notify(DelegateTask delegateTask) {
		String processInstanceId = delegateTask.getProcessInstanceId();
		ProcessInstance processInstance = runtimeService
				.createProcessInstanceQuery()
				.processInstanceId(processInstanceId).singleResult();
		Leave leave = (Leave) leaveService.findEntityById(Leave.class,
				new Long(processInstance.getBusinessKey()));

		leave.setLeaveType((String) delegateTask.getVariable("leaveType"));
		leave.setStartTime(new Timestamp(((Date)delegateTask.getVariable("startTime")).getTime()));
		leave.setEndTime(new Timestamp(((Date)delegateTask.getVariable("endTime")).getTime()));
		leave.setReason((String) delegateTask.getVariable("reason"));

		leaveService.merge(leave);
	}

	public void setLeaveService(LeaveService leaveService) {
		this.leaveService = leaveService;
	}

	public void setRuntimeService(RuntimeService runtimeService) {
		this.runtimeService = runtimeService;
	}

}
