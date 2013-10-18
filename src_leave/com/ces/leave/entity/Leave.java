package com.ces.leave.entity;

import java.sql.Timestamp;
import java.util.Map;

import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

/**
 * OaLeave entity. @author MyEclipse Persistence Tools
 */

public class Leave implements java.io.Serializable {

	// Fields

	private Long id;
	private String processInstanceId;
	private String userId;
	private Timestamp startTime;
	private Timestamp endTime;
	private String leaveType;
	private String reason;
	private Timestamp applyTime;
	private Timestamp realityStartTime;
	private Timestamp realityEndTime;

	// -- 临时属性 --//
	// 流程任务
	private Task task;
	private Map<String, Object> variables;
	// 运行中的流程实例
	private ProcessInstance processInstance;
	// 历史的流程实例
	private HistoricProcessInstance historicProcessInstance;
	// 流程定义
	private ProcessDefinition processDefinition;

	// Constructors

	/** default constructor */
	public Leave() {
	}

	/** full constructor */
	public Leave(String processInstanceId, String userId, Timestamp startTime,
			Timestamp endTime, String leaveType, String reason,
			Timestamp applyTime, Timestamp realityStartTime,
			Timestamp realityEndTime) {
		this.processInstanceId = processInstanceId;
		this.userId = userId;
		this.startTime = startTime;
		this.endTime = endTime;
		this.leaveType = leaveType;
		this.reason = reason;
		this.applyTime = applyTime;
		this.realityStartTime = realityStartTime;
		this.realityEndTime = realityEndTime;
	}

	// Property accessors

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getProcessInstanceId() {
		return this.processInstanceId;
	}

	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Timestamp getStartTime() {
		return this.startTime;
	}

	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}

	public Timestamp getEndTime() {
		return this.endTime;
	}

	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}

	public String getLeaveType() {
		return this.leaveType;
	}

	public void setLeaveType(String leaveType) {
		this.leaveType = leaveType;
	}

	public String getReason() {
		return this.reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public Timestamp getApplyTime() {
		return this.applyTime;
	}

	public void setApplyTime(Timestamp applyTime) {
		this.applyTime = applyTime;
	}

	public Timestamp getRealityStartTime() {
		return this.realityStartTime;
	}

	public void setRealityStartTime(Timestamp realityStartTime) {
		this.realityStartTime = realityStartTime;
	}

	public Timestamp getRealityEndTime() {
		return this.realityEndTime;
	}

	public void setRealityEndTime(Timestamp realityEndTime) {
		this.realityEndTime = realityEndTime;
	}

	
	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public ProcessInstance getProcessInstance() {
		return processInstance;
	}

	public void setProcessInstance(ProcessInstance processInstance) {
		this.processInstance = processInstance;
	}

	public HistoricProcessInstance getHistoricProcessInstance() {
		return historicProcessInstance;
	}

	public void setHistoricProcessInstance(HistoricProcessInstance historicProcessInstance) {
		this.historicProcessInstance = historicProcessInstance;
	}

	public ProcessDefinition getProcessDefinition() {
		return processDefinition;
	}

	public void setProcessDefinition(ProcessDefinition processDefinition) {
		this.processDefinition = processDefinition;
	}

}