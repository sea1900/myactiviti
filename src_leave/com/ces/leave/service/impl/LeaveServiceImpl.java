package com.ces.leave.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;

import com.ces.common.service.impl.CommonServiceImpl;
import com.ces.common.webbean.Page;
import com.ces.leave.entity.Leave;
import com.ces.leave.service.LeaveService;

public class LeaveServiceImpl extends CommonServiceImpl implements LeaveService {

	/**
	 * 启动流程
	 * 
	 * @param entity
	 */
	public ProcessInstance startWorkflow(Leave entity,
			Map<String, Object> variables) {
		Calendar c = Calendar.getInstance();
		entity.setApplyTime(new Timestamp(c.getTimeInMillis()));
		entity.setRealityStartTime(new Timestamp(c.getTimeInMillis()));
		entity.setRealityEndTime(new Timestamp(c.getTimeInMillis()));
		save(entity);
		logger.debug("save entity: {}" + entity);
		String businessKey = entity.getId().toString();

		// 用来设置启动流程的人员ID，引擎会自动把用户ID保存到activiti:initiator中
		identityService.setAuthenticatedUserId(entity.getUserId());

		ProcessInstance processInstance = runtimeService
				.startProcessInstanceByKey("leave", businessKey, variables);
		String processInstanceId = processInstance.getId();
		entity.setProcessInstanceId(processInstanceId);
		logger.debug("start process of {key={" + "leave" + "}, bkey={"
				+ businessKey + "}, pid={" + processInstanceId
				+ "}, variables={" + variables + "}}");
		return processInstance;
	}

	/**
	 * 查询待办任务
	 * 
	 * @param userId
	 *            用户ID
	 * @return
	 */
	public List<Leave> findTodoTasks(String userId, Page<Leave> page,
			int[] pageParams) {
		List<Leave> results = new ArrayList<Leave>();
		List<Task> tasks = new ArrayList<Task>();

		// 根据当前人的ID查询
		TaskQuery todoQuery = taskService.createTaskQuery()
				.processDefinitionKey("leave").taskAssignee(userId).active()
				.orderByTaskId().desc().orderByTaskCreateTime().desc();
		List<Task> todoList = todoQuery.listPage(pageParams[0], pageParams[1]);

		// 根据当前人未签收的任务
		TaskQuery claimQuery = taskService.createTaskQuery()
				.processDefinitionKey("leave").taskCandidateUser(userId)
				.active().orderByTaskId().desc().orderByTaskCreateTime().desc();
		List<Task> unsignedTasks = claimQuery.listPage(pageParams[0],
				pageParams[1]);

		// 合并
		tasks.addAll(todoList);
		tasks.addAll(unsignedTasks);

		// 根据流程的业务ID查询实体并关联
		for (Task task : tasks) {
			String processInstanceId = task.getProcessInstanceId();
			ProcessInstance processInstance = runtimeService
					.createProcessInstanceQuery()
					.processInstanceId(processInstanceId).active()
					.singleResult();
			String businessKey = processInstance.getBusinessKey();
			Leave leave = (Leave) findEntityById(Leave.class, new Long(
					businessKey));
			leave.setTask(task);
			leave.setProcessInstance(processInstance);
			leave.setProcessDefinition(getProcessDefinition(processInstance
					.getProcessDefinitionId()));
			results.add(leave);
		}

		page.setTotalCount(todoQuery.count() + claimQuery.count());
		page.setResult(results);
		return results;
	}

	/**
	 * 读取运行中的流程
	 * 
	 * @return
	 */
	public List<Leave> findRunningProcessInstaces(Page<Leave> page,
			int[] pageParams) {
		List<Leave> results = new ArrayList<Leave>();
		ProcessInstanceQuery query = runtimeService
				.createProcessInstanceQuery().processDefinitionKey("leave")
				.active().orderByProcessInstanceId().desc();
		List<ProcessInstance> list = query.listPage(pageParams[0],
				pageParams[1]);

		// 关联业务实体
		for (ProcessInstance processInstance : list) {
			String businessKey = processInstance.getBusinessKey();
			Leave leave = (Leave) findEntityById(Leave.class, new Long(
					businessKey));
			leave.setProcessInstance(processInstance);
			leave.setProcessDefinition(getProcessDefinition(processInstance
					.getProcessDefinitionId()));
			results.add(leave);

			// 设置当前任务信息
			List<Task> tasks = taskService.createTaskQuery()
					.processInstanceId(processInstance.getId()).active()
					.orderByTaskCreateTime().desc().listPage(0, 1);
			leave.setTask(tasks.get(0));
		}

		page.setTotalCount(query.count());
		page.setResult(results);
		return results;
	}

	/**
	 * 读取已结束中的流程
	 * 
	 * @return
	 */
	public List<Leave> findFinishedProcessInstaces(Page<Leave> page,
			int[] pageParams) {
		List<Leave> results = new ArrayList<Leave>();
		HistoricProcessInstanceQuery query = historyService
				.createHistoricProcessInstanceQuery()
				.processDefinitionKey("leave").finished()
				.orderByProcessInstanceEndTime().desc();
		List<HistoricProcessInstance> list = query.listPage(pageParams[0],
				pageParams[1]);

		// 关联业务实体
		for (HistoricProcessInstance historicProcessInstance : list) {
			String businessKey = historicProcessInstance.getBusinessKey();
			Leave leave = (Leave) findEntityById(Leave.class, new Long(
					businessKey));
			leave.setProcessDefinition(getProcessDefinition(historicProcessInstance
					.getProcessDefinitionId()));
			leave.setHistoricProcessInstance(historicProcessInstance);
			results.add(leave);
		}
		page.setTotalCount(query.count());
		page.setResult(results);
		return results;
	}

	/**
	 * 查询流程定义对象
	 * 
	 * @param processDefinitionId
	 *            流程定义ID
	 * @return
	 */
	protected ProcessDefinition getProcessDefinition(String processDefinitionId) {
		ProcessDefinition processDefinition = repositoryService
				.createProcessDefinitionQuery()
				.processDefinitionId(processDefinitionId).singleResult();
		return processDefinition;
	}

}
