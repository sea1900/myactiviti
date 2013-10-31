package com.ces.system.action;

import java.util.List;

import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;

import com.ces.common.action.CommonAction;
import com.ces.system.utils.CookieUtils;

public class LoginAction extends CommonAction {
	private static Logger logger = Logger.getLogger(LoginAction.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// 用于存放登录用户名
	private String userName;
	// 用于存放登录密码
	private String password;

	// 用于存放登录是否成功的标志位
	private String strlogintip;

	// 是否自动登录的标志位
	private Boolean autoLogin;

	// 是否记住用户名的标志位
	private Boolean remUser;
	
	private CookieUtils cookieUtils = new CookieUtils();

	public Boolean getAutoLogin() {
		return autoLogin;
	}

	public void setAutoLogin(Boolean autoLogin) {
		this.autoLogin = autoLogin;
	}

	public Boolean getRemUser() {
		return remUser;
	}

	public void setRemUser(Boolean remUser) {
		this.remUser = remUser;
	}

	private User user;

	public String index() {
		return "login";
	}
	
	// 在登录时调用
	public String login() {
		try {
			boolean checkPassword = identityService.checkPassword(userName, password);
			if (checkPassword) {
				// read user from database
				User user = identityService.createUserQuery().userId(userName).singleResult();
				session.put("user", user);

				List<Group> groupList = identityService.createGroupQuery().groupMember(userName).list();
				session.put("groups", groupList);

				String[] groupNames = new String[groupList.size()];
				for (int i = 0; i < groupNames.length; i++) {
					System.out.println(groupList.get(i).getName());
					groupNames[i] = groupList.get(i).getName();
				}

				session.put("groupNames", ArrayUtils.toString(groupNames));

				return "welcome";
			} else {
				session.put("error", true);
				return "login";
			}
		} catch (Exception e) {
			logger.error(e);
			return "login";
		}
	}

	// 登出操作
	public String logout() throws Exception {
		try {
			session.put("user", null);
			session.put("userName", null);
			session.put("loginName", null);
			session.put("authenticationType", null);

			cookieUtils.delCookie(request, response, "user");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
		
		setForwardJsp("/views/login.jsp");
		return FORWARD;
	}

	public String getStrlogintip() {
		return strlogintip;
	}

	public void setStrlogintip(String strlogintip) {
		this.strlogintip = strlogintip;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}