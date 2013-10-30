package me.kafeitu.demo.activiti.util;

import javax.servlet.http.HttpSession;

import org.activiti.engine.identity.User;
import org.apache.struts2.dispatcher.SessionMap;

/**
 * 用户工具类
 * 
 * @author hc
 */
public class UserUtil {

	public static final String USER = "user";

	/**
	 * 设置用户到session
	 * 
	 * @param session
	 * @param user
	 */
	public static void saveUserToSession(SessionMap<String, Object> session,
			User user) {
		session.put(USER, user);
	}

	/**
	 * 从Session获取当前用户信息
	 * 
	 * @param session
	 * @return
	 */
	public static User getUserFromSession(SessionMap<String, Object> session) {
		Object attribute = session.get(USER);
		return attribute == null ? null : (User) attribute;
	}

	/**
	 * 设置用户到session
	 * @param session
	 * @param user
	 */
	public static void saveUserToSession(HttpSession session, User user) {
		session.setAttribute(USER, user);
	}

	/**
	 * 从Session获取当前用户信息
	 * @param session
	 * @return
	 */
	public static User getUserFromSession(HttpSession session) {
		Object attribute = session.getAttribute(USER);
		return attribute == null ? null : (User) attribute;
	}
}
