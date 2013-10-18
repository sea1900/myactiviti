package com.ces.test;

import java.util.List;

import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

public class FirstApp {

	private ProcessEngine processEngine;

	private RepositoryService repositoryService;
	private RuntimeService runtimeService;
	private TaskService taskService;
	
	private HistoryService historyService;

	public FirstApp() {
		// Create Activiti process engine
		// ProcessEngine processEngine = ProcessEngineConfiguration
		// .createStandaloneProcessEngineConfiguration()
		// .buildProcessEngine();
		processEngine = ProcessEngines.getDefaultProcessEngine();

		repositoryService = processEngine.getRepositoryService();
		runtimeService = processEngine.getRuntimeService();
		taskService = processEngine.getTaskService();
		
		historyService = processEngine.getHistoryService(); 
	}

	public String deploy() {
		// 部署
		Deployment deploy = repositoryService.createDeployment()
				.addClasspathResource("FinancialReportProcess.bpmn20.xml")
				.deploy();

		String deployId = deploy.getId();
		System.out.println("部署成功，标识：" + deployId + "\r\t部署Name："
				+ deploy.getName() + "\r\t部署Category：" + deploy.getCategory());

		return deployId;
	}

	public String beginDeploy() {
		ProcessInstance processInstance = runtimeService
				.startProcessInstanceByKey("financialReport");
		String processDefId = processInstance.getId();

		System.out.println("启动成功,ID:" + processDefId + "\r\t"
				+ processInstance.getActivityId());

		return processDefId;
	}

	public void userTask() {
		List<Task> tasks = taskService.createTaskQuery()
				.taskCandidateUser("john").list();
		for (Task task : tasks) {
			System.out.println("john以下任务可领取: " + task.getName());

			// claim it
			taskService.claim(task.getId(), "john");
		}

		// Verify Fozzie can now retrieve the task
		tasks = taskService.createTaskQuery().taskAssignee("john").list();
		for (Task task : tasks) {
			System.out.println("john以下任务可完成: " + task.getName());

			// Complete the task
			taskService.complete(task.getId());
		}
	}

	public void adminTask() {
		List<Task> list = taskService.createTaskQuery().taskCandidateGroup("management")
				.list();

		for (Task task : list) {
			System.out.println("admin以下任务可领取: " + task.getName());

			// 领取任务
			taskService.claim(task.getId(), "admin");
		}

		List<Task> list2 = taskService.createTaskQuery().taskAssignee("admin")
				.list();

		for (Task task2 : list2) {
			System.out.println("admin以下任务可完成: " + task2.getName());

			// 完成任务
			taskService.complete(task2.getId());
		}

	}

	public void findHistory(String procId) {
	    HistoricProcessInstance historicProcessInstance = 
	      historyService.createHistoricProcessInstanceQuery().processInstanceId(procId).singleResult(); 
	    System.out.println("Process instance end time: " + historicProcessInstance.getEndTime()); 
	}
	
	public static void main(String[] args) {
		FirstApp app = new FirstApp();

		app.deploy();
		String procId = app.beginDeploy();
		System.out.println(procId);
		app.userTask();
		app.adminTask();
		
		app.findHistory(procId);
	}
}
