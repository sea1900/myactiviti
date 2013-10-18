package com.ces.leave.service;

import java.util.List;
import java.util.Map;

import me.kafeitu.demo.activiti.util.Page;

import org.activiti.engine.runtime.ProcessInstance;

import com.ces.common.service.CommonService;
import com.ces.leave.entity.Leave;

public interface LeaveService extends CommonService {
	/**
	 * 启动流程
	 * 
	 * @param entity
	 */
	public ProcessInstance startWorkflow(Leave entity,
			Map<String, Object> variables);

	/**
	 * 查询待办任务
	 * 
	 * @param userId
	 *            用户ID
	 * @return
	 */
	public List<Leave> findTodoTasks(String userId, Page<Leave> page,
			int[] pageParams);

	/**
	 * 读取运行中的流程
	 * 
	 * @return
	 */
	public List<Leave> findRunningProcessInstaces(Page<Leave> page,
			int[] pageParams);

	/**
	 * 读取已结束中的流程
	 * 
	 * @return
	 */
	public List<Leave> findFinishedProcessInstaces(Page<Leave> page,
			int[] pageParams);
}
