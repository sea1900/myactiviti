package com.ces.system.service.impl;

import java.math.BigInteger;

import org.activiti.engine.identity.User;
import org.apache.log4j.Logger;

import com.ces.system.dao.ConfigDao;
import com.ces.system.dao.UsersDao;
import com.ces.system.service.LoginService;

/**
 * 对外提供登录的服务实现类
 * 
 * 
 */
public class LoginServiceImpl implements LoginService {

	private static Logger logger = Logger.getLogger(LoginServiceImpl.class);
	private UsersDao usersDao;

	public void setUsersDao(UsersDao usersDao) {
		this.usersDao = usersDao;
	}

	public UsersDao getUsersDao() {
		return usersDao;
	}

}