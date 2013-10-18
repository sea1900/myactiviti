package com.ces.framework.action;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts2.dispatcher.SessionMap;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.SessionAware;

import com.ces.framework.webbean.page.Pager;
import com.opensymphony.xwork2.ActionSupport;

/**
 * Action基类,加入分页功能;
 * 
 * @author hc
 * 
 */
@SuppressWarnings("unchecked")
public class BaseAction extends ActionSupport implements ServletRequestAware,
		ServletResponseAware, SessionAware {

	private static final long serialVersionUID = 1L;
	protected Logger logger = Logger.getLogger(getClass());

	// 分页对象 默认从显示第一页显示,每页显示10条
	protected Pager pager = new Pager();
	protected int count;
	protected int start = 0;
	protected int limit = 10;
	protected String sort;

	// 页面对象
	protected HttpServletRequest request;
	protected HttpServletResponse response;
	protected SessionMap<String, Object> session;

	// 跳转对象
	// 跳到指定页面，需要配置
	protected static final String FORWARD = "forwardJsp";
	protected static final String REDIRECT = "redirectJsp";
	
	protected String forwardJsp;
	protected String redirectJsp;
	
	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}

	/**
	 * @param count
	 *            the count to set
	 */
	public void setCount(int count) {
		this.count = count;
	}

	/**
	 * @return the start
	 */
	public int getStart() {
		return start;
	}

	/**
	 * @param start
	 *            the start to set
	 */
	public void setStart(int start) {
		this.start = start;
	}

	/**
	 * @return the limit
	 */
	public int getLimit() {
		return limit;
	}

	/**
	 * @param limit
	 *            the limit to set
	 */
	public void setLimit(int limit) {
		this.limit = limit;
	}

	/**
	 * @return the sort
	 */
	public String getSort() {
		return sort;
	}

	/**
	 * @param sort
	 *            the sort to set
	 */
	public void setSort(String sort) {
		this.sort = sort;
	}

	public Pager getPager() {
		return pager;
	}

	public void setPager(Pager pager) {
		this.pager = pager;
	}

	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	public void setSession(Map<String, Object> session) {
		this.session = (SessionMap) session;
	}

	public String getForwardJsp() {
		return forwardJsp;
	}

	public void setForwardJsp(String forwardJsp) {
		this.forwardJsp = forwardJsp;
	}

	public String getRedirectJsp() {
		return redirectJsp;
	}

	public void setRedirectJsp(String redirectJsp) {
		this.redirectJsp = redirectJsp;
	}

}