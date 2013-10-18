package com.ces.system.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieUtils {

	// 添加一个cookie
	public Cookie addCookie(HttpServletResponse response, String key,
			String value, int maxAge) {
		Cookie cookie = new Cookie(key, value);
		cookie.setPath("/");
		cookie.setMaxAge(maxAge);// cookie保存时效
		
		response.addCookie(cookie);
		return cookie;
	}

	// 得到cookie
	public String getCookie(HttpServletRequest request, String name) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (name.equals(cookie.getName())) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}

	// 删除cookie
	public void delCookie(HttpServletRequest request,HttpServletResponse response, String name) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (name.equals(cookie.getName())) {
					cookie.setValue(null);
					cookie.setPath("/");
					cookie.setMaxAge(0);
					
					response.addCookie(cookie);
				}
			}
		}
	}
}
