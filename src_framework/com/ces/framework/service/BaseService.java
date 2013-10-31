package com.ces.framework.service;

import java.io.Serializable;
import java.util.List;

import com.ces.framework.webbean.page.Pager;

/**
 * 业务层顶级接口
 * 
 * @author haichen
 * 
 */
public interface BaseService {

	/**
	 * 保存实体
	 * 
	 * @param entity
	 */
	public void save(Object entity);

	/**
	 * 
	 * @param entity
	 */
	public Object saveReId(Object entity);

	/**
	 * 获得所有该对象的数据
	 * 
	 * @param <T>
	 * @param entityClass
	 * @return
	 */
	public <T> List<T> getAll(Class<T> entityClass);

	/**
	 * 删除实体
	 * 
	 * @param entity
	 */
	public void delete(Object entity);

	/**
	 * 修改实体
	 * 
	 * @param entity
	 */
	public void update(Object entity);

	/**
	 * 修改实体
	 * 
	 * @param entity
	 */
	public void saveOrUpdate(Object entity);

	/**
	 * 翻页查询
	 * 
	 * @param paramObject
	 * @param currentPage
	 * @param pageSize
	 * @param taxisfield
	 * @param taxis
	 * @return
	 */
	public <T> Pager<T> find(Class<T> entityClass, Object paramObject,
			String currentPage, String pageSize, String taxisfield, String taxis);

	/**
	 * 翻页查询BYHQL
	 * 
	 * @param paramObject
	 * @param currentPage
	 * @param pageSize
	 * @param taxisfield
	 * @param taxis
	 * @return
	 */
	public <T> Pager<T> findByHql(String hql, String currentPage,
			String pageSize, Object... paramObject);

	/**
	 * 唯一性验证
	 * 
	 * @param <T>
	 * @param entityClass
	 * @param entity
	 * @param uniquePropertyNames
	 * @return 返回true是没有重复，false是有重复
	 */
	public <T> boolean isUnique(Class<T> entityClass, Object entity,
			String uniquePropertyNames);

	/**
	 * 获取实体信息
	 * 
	 * @param id
	 * @return
	 */
	public <T> Object getEntity(Class<T> entityClass, Serializable id);

	/**
	 * 获取单实体对象
	 * 
	 */
	public <T> Object findEntityById(Class<T> entityObject, Serializable id);

	/**
	 * 通过语句获得对象集合
	 */
	public <T> List<?> findListByHQL(String hql);

	/**
	 * 保存或修改方法 方法
	 * 
	 * @param entity
	 * @return
	 */
	public Object merge(Object entity);

	/**
	 * 保存或修改方法 方法
	 * 
	 * @param entityName
	 * @param entity
	 * @return
	 */
	public Object merge(String entityName, Object entity);

}
