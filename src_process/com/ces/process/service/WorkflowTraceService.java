package com.ces.process.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.ces.common.service.CommonService;

public interface WorkflowTraceService extends CommonService {

	/**
	 * 流程跟踪图
	 * 
	 * @param processInstanceId
	 *            流程实例ID
	 * @param request 
	 * @return 封装了各种节点信息
	 */
	public List<Map<String, Object>> traceProcess(String processInstanceId)
			throws Exception;
}
