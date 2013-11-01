package com.ces.general.action;

import com.ces.framework.action.BaseAction;

public class TaskAction extends BaseAction {

//	/**
//	 * 
//	 * 待办任务--Portlet
//	 */
//	public String todoList() {
//		User user = (User) session.get("user");
//		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
//
//		// 已经签收的任务
//		List<Task> todoList = taskService.createTaskQuery()
//				.taskAssignee(user.getId()).active().list();
//		for (Task task : todoList) {
//			String processDefinitionId = task.getProcessDefinitionId();
//			ProcessDefinition processDefinition = getProcessDefinition(processDefinitionId);
//
//			Map<String, Object> singleTask = packageTaskInfo(sdf, task,
//					processDefinition);
//			singleTask.put("status", "todo");
//			result.add(singleTask);
//		}
//
//		// 等待签收的任务
//		List<Task> toClaimList = taskService.createTaskQuery()
//				.taskCandidateUser(user.getId()).active().list();
//		for (Task task : toClaimList) {
//			String processDefinitionId = task.getProcessDefinitionId();
//			ProcessDefinition processDefinition = getProcessDefinition(processDefinitionId);
//
//			Map<String, Object> singleTask = packageTaskInfo(sdf, task,
//					processDefinition);
//			singleTask.put("status", "claim");
//			result.add(singleTask);
//		}
//
//		return result;
//	}
}
