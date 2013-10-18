package com.ces.framework.service.impl;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import com.ces.framework.orm.BaseDao;
import com.ces.framework.service.BaseService;
import com.ces.framework.util.BeanUtils;
import com.ces.framework.webbean.page.Pager;

/**
 * 业务层基类
 * 
 * @author haic
 * 
 */
public class BaseServiceImpl implements BaseService {

	protected BaseDao baseDao;

	/***
	 * 删除对象方法
	 */
	public void delete(Object entity) {
		baseDao.remove(entity);
	}

	/**
	 * 分页查询函数，根据entityClass和查询条件参数,排序参数创建默认Criteria查询对象 <code>Criteria</code>.
	 * 
	 * @return 含结果记录数和当前页数据的Page对象.
	 */

	public <T> Pager<T> find(Class<T> entityClass, Object paramObject,
			String currentPage, String pageSize, String taxisfield, String taxis) {
		Criteria criteria = baseDao.createCriteria(entityClass, taxisfield,
				false,
				Example.create(paramObject).enableLike(MatchMode.ANYWHERE))
				.add(Restrictions.eq("isDeleted", false));
		return baseDao.pagedQuery(criteria, currentPage, pageSize);
	}

	/**
	 * 分页查询函数，根据entityClass和ID参数,获取对应对象数据
	 * 
	 * @return 查询对应
	 */
	public <T> Object getEntity(Class<T> entityClass, Serializable id) {
		return baseDao.get(entityClass, id);
	}

	/**
	 * 判定对象属性是否唯一
	 */
	public <T> boolean isUnique(Class<T> entityClass, Object entity,
			String uniquePropertyNames) {
		return baseDao.isUnique(entityClass, entity, uniquePropertyNames);
	}

	/***
	 * 保存方法
	 */
	public void save(Object entity) {
		baseDao.save(entity);

	}

	public Object saveReId(Object entity) {
		return baseDao.saveReId(entity);

	}

	/***
	 * 修改方法
	 */
	public void update(Object entity) {
		baseDao.update(entity);

	}

	public void setBaseDao(BaseDao baseDao) {
		this.baseDao = baseDao;
	}

	/**
	 * 通过HQL语句分页
	 */
	public <T> Pager<T> findByHql(String hql, String currentPage,
			String pageSize, Object... paramObject) {

		return baseDao.pagedQuery(hql, currentPage, pageSize, paramObject);
	}

	/**
	 * 通过类对象 获取对应类对象所有结果集 不适用于超大数据量查询 hibnate 无特殊配置情况下 最大查询结果集3万条（还与应用电脑性能有关）
	 * 数据量过大时,会内存溢出
	 */
	public <T> List<T> getAll(Class<T> entityClass) {

		return baseDao.getAll(entityClass);
	}

	/**
	 * 通过ID 获取对应对象 脱离事务
	 */
	public <T> Object findEntityById(Class<T> entityObject, Serializable id) {
		Object obj = null;
		try {
			obj = entityObject.newInstance();
			BeanUtils.copyProperties(obj, baseDao.get(entityObject, id));
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return obj;
	}

	/**
	 * HQL 语句执行方法
	 * 
	 * @param hql
	 * @return
	 */
	public int executeUpdate(String hql) {
		Query query = baseDao.createQuery(hql);
		return query.executeUpdate();
	}

	/**
	 * HQL 语句执行方法
	 * 
	 * @param hql
	 * @return
	 */
	public int executeUpdate(String hql, Object... obj) {
		Query query = baseDao.createQuery(hql, obj);
		return query.executeUpdate();
	}

	/***
	 * 
	 * 通过HQL语句查询对应记录集
	 */
	public <T> List<?> findListByHQL(String hql) {
		return baseDao.find(hql);
	}

	/**
	 * 保存或修改方法 方法详细查阅baseDao
	 * 
	 * @param entity
	 * @return
	 */
	public Object merge(Object entity) {
		return baseDao.merge(entity);
	}

	/**
	 * 保存或修改方法 方法详细查阅baseDao
	 * 
	 * @param entityName
	 * @param entity
	 * @return
	 */
	public Object merge(String entityName, Object entity) {
		return baseDao.merge(entityName, entity);
	}

	/**
	 * 对象锁方法
	 * 
	 * @param entityName
	 * @param entity
	 * @param lockMode
	 */
	public void lock(String entityName, Object entity, LockMode lockMode) {
		baseDao.lock(entityName, entity, lockMode);
	}

	/**
	 * 对象锁方法
	 * 
	 * @param entity
	 * @param lockMode
	 */
	public void lock(Object entity, LockMode lockMode) {
		baseDao.lock(entity, lockMode);
	}

	/**
	 * 保存方法 详细说明 见BaseDao
	 * 
	 * @param entity
	 */
	public void persist(Object entity) {
		baseDao.persist(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ces.framework.service.BaseService#saveOrUpdate(java.lang.Object)
	 */
	public void saveOrUpdate(Object entity) {
		// TODO Auto-generated method stub
		baseDao.saveOrUpdate(entity);
	}
}
