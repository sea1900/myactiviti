package com.ces.process.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import me.kafeitu.demo.activiti.util.WorkflowUtils;

import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.ProcessEngineImpl;
import org.activiti.engine.impl.bpmn.diagram.ProcessDiagramGenerator;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.io.FilenameUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ces.common.action.CommonAction;
import com.ces.common.utils.PageUtil;
import com.ces.common.webbean.Page;
import com.ces.framework.json.util.JsonConverter;
import com.ces.framework.util.PropUtils;
import com.ces.process.service.ProcessDefinitionService;
import com.ces.process.service.WorkflowTraceService;

public class WorkFlowAction extends CommonAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected Logger logger = LoggerFactory.getLogger(getClass());

	protected ProcessDefinitionService processDefinitionService;
	protected WorkflowTraceService traceService;

	protected static Map<String, ProcessDefinition> PROCESS_DEFINITION_CACHE = new HashMap<String, ProcessDefinition>();

	private List<Map<String, Object>> todoResult = new ArrayList<Map<String, Object>>();

	private File upload;
	private String uploadFileName;
	private String uploadContentType;

	/**
	 * 流程定义列表
	 * 
	 * @return
	 */
	public String processList() {
		/*
		 * 保存两个对象，一个是ProcessDefinition（流程定义），一个是Deployment（流程部署）
		 */
		List<Object[]> objects = new ArrayList<Object[]>();

		Page<Object[]> page = new Page<Object[]>(PageUtil.PAGE_SIZE);
		int[] pageParams = PageUtil.init(page, request);

		ProcessDefinitionQuery processDefinitionQuery = repositoryService
				.createProcessDefinitionQuery().orderByDeploymentId().desc();
		List<ProcessDefinition> processDefinitionList = processDefinitionQuery
				.listPage(pageParams[0], pageParams[1]);
		for (ProcessDefinition processDefinition : processDefinitionList) {
			String deploymentId = processDefinition.getDeploymentId();
			Deployment deployment = repositoryService.createDeploymentQuery()
					.deploymentId(deploymentId).singleResult();
			objects.add(new Object[] { processDefinition, deployment });
		}

		page.setTotalCount(processDefinitionQuery.count());
		page.setResult(objects);
		session.put("page", page);

		String message = request.getParameter("message");
		request.setAttribute("message", message);

		setForwardJsp("/views/workflow/process-list.jsp");
		return FORWARD;
	}

	/**
	 * 部署全部流程
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/redeploy/all")
	public String redeployAll(
			@Value("#{APP_PROPERTIES['export.diagram.path']}") String exportDir)
			throws Exception {
		processDefinitionService.deployAllFromClasspath(exportDir);
		return "redirect:/workflow/process-list";
	}

	/**
	 * 读取资源，通过部署ID
	 */
	public String loadByDeployment() {
		// 流程定义
		String processDefinitionId = request
				.getParameter("processDefinitionId");
		// 资源类型(xml|image)
		String resourceType = request.getParameter("resourceType");

		ProcessDefinition processDefinition = repositoryService
				.createProcessDefinitionQuery()
				.processDefinitionId(processDefinitionId).singleResult();
		String resourceName = "";
		if (resourceType.equals("image")) {
			resourceName = processDefinition.getDiagramResourceName();
		} else if (resourceType.equals("xml")) {
			resourceName = processDefinition.getResourceName();
		}
		InputStream resourceAsStream = repositoryService.getResourceAsStream(
				processDefinition.getDeploymentId(), resourceName);
		byte[] b = new byte[1024];
		int len = -1;
		try {
			while ((len = resourceAsStream.read(b, 0, 1024)) != -1) {
				response.getOutputStream().write(b, 0, len);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 读取资源，通过流程ID
	 * 
	 * @param resourceType
	 *            资源类型(xml|image)
	 * @param processInstanceId
	 *            流程实例ID
	 * @param response
	 * @throws Exception
	 */
	public String loadByProcessInstance() {
		String resourceType = request.getParameter("type");
		String processInstanceId = request.getParameter("pid");

		InputStream resourceAsStream = null;
		try {
			ProcessInstance processInstance = runtimeService
					.createProcessInstanceQuery()
					.processInstanceId(processInstanceId).singleResult();
			ProcessDefinition processDefinition = repositoryService
					.createProcessDefinitionQuery()
					.processDefinitionId(
							processInstance.getProcessDefinitionId())
					.singleResult();

			String resourceName = "";
			if (resourceType.equals("image")) {
				resourceName = processDefinition.getDiagramResourceName();
			} else if (resourceType.equals("xml")) {
				resourceName = processDefinition.getResourceName();
			}
			resourceAsStream = repositoryService.getResourceAsStream(
					processDefinition.getDeploymentId(), resourceName);
			byte[] b = new byte[1024];
			int len = -1;
			while ((len = resourceAsStream.read(b, 0, 1024)) != -1) {
				response.getOutputStream().write(b, 0, len);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 删除部署的流程，级联删除流程实例
	 * 
	 * @param deploymentId
	 *            流程部署ID
	 */
	public String delete() {
		String deploymentId = request.getParameter("deploymentId");
		repositoryService.deleteDeployment(deploymentId, true);

		String message = "已删除部署ID为" + deploymentId + "的流程";
		try {
			setRedirectJsp("workflow_processList.action?message="
					+ URLEncoder.encode(message, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return REDIRECT;
	}

	/**
	 * 输出跟踪流程信息
	 * 
	 */
	public String traceProcess() {
		String processInstanceId = request.getParameter("pid");
		List<Map<String, Object>> activityInfos = new ArrayList<Map<String, Object>>();
		try {
			activityInfos = traceService.traceProcess(processInstanceId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JsonConverter.listToJson(activityInfos, response);

		return SUCCESS;
	}

	/**
	 * 读取带跟踪的图片
	 */
	public String readResource() {
		String executionId = request.getParameter("executionId");

		try {
			ProcessInstance processInstance = runtimeService
					.createProcessInstanceQuery()
					.processInstanceId(executionId).singleResult();
			BpmnModel bpmnModel = repositoryService
					.getBpmnModel(processInstance.getProcessDefinitionId());
			List<String> activeActivityIds = runtimeService
					.getActiveActivityIds(executionId);
			// 不使用spring请使用下面的两行代码
			// ProcessEngineImpl defaultProcessEngine = (ProcessEngineImpl)
			// ProcessEngines.getDefaultProcessEngine();
			// Context.setProcessEngineConfiguration(defaultProcessEngine.getProcessEngineConfiguration());

			// 使用spring注入引擎请使用下面的这行代码
			Context.setProcessEngineConfiguration(((ProcessEngineImpl)processEngine)
					.getProcessEngineConfiguration());

			InputStream imageStream = ProcessDiagramGenerator.generateDiagram(
					bpmnModel, "png", activeActivityIds);

			// 输出资源内容到相应对象
			byte[] b = new byte[1024];
			int len;
			while ((len = imageStream.read(b, 0, 1024)) != -1) {
				response.getOutputStream().write(b, 0, len);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String deploy() {
		String exportDir = PropUtils.getStringValue("application.properties",
				"export.diagram.path");

		try {
			InputStream fileInputStream = new FileInputStream(upload);
			Deployment deployment = null;

			String extension = FilenameUtils.getExtension(uploadFileName);
			if (extension.equals("zip") || extension.equals("bar")) {
				ZipInputStream zip = new ZipInputStream(fileInputStream);
				deployment = repositoryService.createDeployment()
						.addZipInputStream(zip).deploy();
			} else {
				deployment = repositoryService.createDeployment()
						.addInputStream(uploadFileName, fileInputStream)
						.deploy();
			}

			List<ProcessDefinition> list = repositoryService
					.createProcessDefinitionQuery()
					.deploymentId(deployment.getId()).list();

			for (ProcessDefinition processDefinition : list) {
				WorkflowUtils.exportDiagramToFile(repositoryService,
						processDefinition, exportDir);
			}

			String message = "部署成功,流程部署ID为" + deployment.getId();
			setRedirectJsp("workflow_processList.action?message="
					+ URLEncoder.encode(message, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(
					"error on deploy process, because of file input stream", e);
		}

		return REDIRECT;
	}

	public String convertToModel() {
		String processDefinitionId = request
				.getParameter("processDefinitionId");

		ProcessDefinition processDefinition = repositoryService
				.createProcessDefinitionQuery()
				.processDefinitionId(processDefinitionId).singleResult();
		InputStream bpmnStream = repositoryService.getResourceAsStream(
				processDefinition.getDeploymentId(),
				processDefinition.getResourceName());
		XMLInputFactory xif = XMLInputFactory.newInstance();
		InputStreamReader in;
		try {
			in = new InputStreamReader(bpmnStream, "UTF-8");

			XMLStreamReader xtr = xif.createXMLStreamReader(in);
			BpmnModel bpmnModel = new BpmnXMLConverter()
					.convertToBpmnModel(xtr);

			BpmnJsonConverter converter = new BpmnJsonConverter();
			ObjectNode modelNode = converter.convertToJson(bpmnModel);
			Model modelData = repositoryService.newModel();
			modelData.setKey(processDefinition.getKey());
			modelData.setName(processDefinition.getResourceName());
			modelData.setCategory(processDefinition.getDeploymentId());

			ObjectNode modelObjectNode = new ObjectMapper().createObjectNode();
			modelObjectNode.put(ModelDataJsonConstants.MODEL_NAME,
					processDefinition.getName());
			modelObjectNode.put(ModelDataJsonConstants.MODEL_REVISION, 1);
			modelObjectNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION,
					processDefinition.getDescription());
			modelData.setMetaInfo(modelObjectNode.toString());

			repositoryService.saveModel(modelData);

			repositoryService.addModelEditorSource(modelData.getId(), modelNode
					.toString().getBytes("utf-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		setRedirectJsp("/model_modelList.action");
		return REDIRECT;
	}

	/**
	 * 待办任务--Portlet
	 * 
	 * @return
	 */
	public String todoList() {
		User user = (User) session.get("user");

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");

		// 已经签收的任务
		List<Task> todoList = taskService.createTaskQuery()
				.taskAssignee(user.getId()).active().list();
		for (Task task : todoList) {
			String processDefinitionId = task.getProcessDefinitionId();
			ProcessDefinition processDefinition = getProcessDefinition(processDefinitionId);

			Map<String, Object> singleTask = packageTaskInfo(sdf, task,
					processDefinition);
			singleTask.put("status", "todo");
			todoResult.add(singleTask);
		}

		// 等待签收的任务
		List<Task> toClaimList = taskService.createTaskQuery()
				.taskCandidateUser(user.getId()).active().list();
		for (Task task : toClaimList) {
			String processDefinitionId = task.getProcessDefinitionId();
			ProcessDefinition processDefinition = getProcessDefinition(processDefinitionId);

			Map<String, Object> singleTask = packageTaskInfo(sdf, task,
					processDefinition);
			singleTask.put("status", "claim");
			todoResult.add(singleTask);
		}

		return SUCCESS;
	}

	private Map<String, Object> packageTaskInfo(SimpleDateFormat sdf,
			Task task, ProcessDefinition processDefinition) {
		Map<String, Object> singleTask = new HashMap<String, Object>();
		singleTask.put("id", task.getId());
		singleTask.put("name", task.getName());
		singleTask.put("createTime", sdf.format(task.getCreateTime()));
		singleTask.put("pdname", processDefinition.getName());
		singleTask.put("pdversion", processDefinition.getVersion());
		singleTask.put("pid", task.getProcessInstanceId());
		return singleTask;
	}

	private ProcessDefinition getProcessDefinition(String processDefinitionId) {
		ProcessDefinition processDefinition = PROCESS_DEFINITION_CACHE
				.get(processDefinitionId);
		if (processDefinition == null) {
			processDefinition = repositoryService
					.createProcessDefinitionQuery()
					.processDefinitionId(processDefinitionId).singleResult();
			PROCESS_DEFINITION_CACHE
					.put(processDefinitionId, processDefinition);
		}
		return processDefinition;
	}

	/**
	 * 挂起、激活流程实例
	 */
	public String updateState() {
		String processDefinitionId = request
				.getParameter("processDefinitionId");
		String state = request.getParameter("state");
		String message = "";

		if (state.equals("active")) {
			message = "已激活ID为" + processDefinitionId + "的流程定义";
			repositoryService.activateProcessDefinitionById(
					processDefinitionId, true, null);
		} else if (state.equals("suspend")) {
			repositoryService.suspendProcessDefinitionById(processDefinitionId,
					true, null);
			message = "已挂起ID为" + processDefinitionId + "的流程定义";
		}

		try {
			setRedirectJsp("workflow_processList.action?message="
					+ URLEncoder.encode(message, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return REDIRECT;
	}

	public String processMsg() {
		setForwardJsp("/views/workflow/process-list.jsp");
		return FORWARD;
	}

	/**
	 * 导出图片文件到硬盘
	 * 
	 * @return
	 */
//	@RequestMapping(value = "export/diagrams")
//	public List<String> exportDiagrams(
//			@Value("#{APP_PROPERTIES['export.diagram.path']}") String exportDir)
//			throws IOException {
//		List<String> files = new ArrayList<String>();
//		List<ProcessDefinition> list = repositoryService
//				.createProcessDefinitionQuery().list();
//
//		for (ProcessDefinition processDefinition : list) {
//			files.add(WorkflowUtils.exportDiagramToFile(repositoryService,
//					processDefinition, exportDir));
//		}
//
//		return files;
//	}

	public void setProcessDefinitionService(
			ProcessDefinitionService processDefinitionService) {
		this.processDefinitionService = processDefinitionService;
	}

	public void setTraceService(WorkflowTraceService traceService) {
		this.traceService = traceService;
	}

	public List<Map<String, Object>> getTodoResult() {
		return todoResult;
	}

	public void setTodoResult(List<Map<String, Object>> todoResult) {
		this.todoResult = todoResult;
	}

	public File getUpload() {
		return upload;
	}

	public void setUpload(File upload) {
		this.upload = upload;
	}

	public String getUploadFileName() {
		return uploadFileName;
	}

	public void setUploadFileName(String uploadFileName) {
		this.uploadFileName = uploadFileName;
	}

	public String getUploadContentType() {
		return uploadContentType;
	}

	public void setUploadContentType(String uploadContentType) {
		this.uploadContentType = uploadContentType;
	}
}
