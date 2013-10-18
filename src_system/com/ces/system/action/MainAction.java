package com.ces.system.action;

import com.ces.framework.action.BaseAction;

public class MainAction extends BaseAction {
	private static final long serialVersionUID = 1L;

	public String main() throws Exception {
		String menuId = request.getParameter("menuId");
		session.put("menu", menuId);
		
		return SUCCESS;
	}
}
