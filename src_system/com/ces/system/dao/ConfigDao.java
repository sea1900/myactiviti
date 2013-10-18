package com.ces.system.dao;

/**
 * ConfigDao接口类，主要用于数据库操作config表，是操作数据库的原子接口类
 * 
 * @author Daijun
 *
 */
public interface ConfigDao {

	/**
	 * 登录验证查询，如果=1，是ldap验证，=0，是普通密码验证
	 * @return 0:普通密码验证,1:ldap验证
	 * @throws Exception
	 */
	public int checkAuthenticationType() throws Exception;
}
