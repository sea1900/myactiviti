package com.ces.process.service;

import org.activiti.engine.repository.ProcessDefinition;

import com.ces.common.service.CommonService;

public interface ProcessDefinitionService extends CommonService {

	public void deployAllFromClasspath(String exportDir) throws Exception;

	public void redeploy(String exportDir, String... processKey)
			throws Exception;

	public void deployFromClasspath(String exportDir, String... processKey)
			throws Exception;

	public ProcessDefinition findProcessDefinition(String processDefinitionId);

	public ProcessDefinition findProcessDefinitionByPid(String processInstanceId);

}
