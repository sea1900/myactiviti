package com.ces.framework.orm;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.hql.ast.QueryTranslatorImpl;
import org.hibernate.impl.CriteriaImpl;
import org.hibernate.metadata.ClassMetadata;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import com.ces.framework.util.BeanUtils;
import com.ces.framework.webbean.page.Pager;

/**
 * Hibernate Dao的泛型基类
 * <p/>
 * 继承于Spring<code>HibernateDaoSupport</code>,提供分页函数和若干便捷查询方法，并对返回值作了泛型类型转.
 * 
 * @author haic
 * 
 * @see HibernateDaoSupport
 * @see HibernateEntityDao
 * 
 */
@SuppressWarnings("unchecked")
public class BaseDao extends HibernateDaoSupport {

	/**
	 * 根据ID获取对象. 实际调用Hibernate的session.load()方法返回实体或其proxy对象. 
	 * <br/>如果对象不存在，抛出异常.
	 */
	public <T> T get(Class<T> entityClass, Serializable id) {
		return (T) getHibernateTemplate().load(entityClass, id);
	}

	/**
	 * 对象保存OR修改 方法 继承自HibernateTemplate  应用于持久化对象冲突时
	 * @param entity
	 * @return
	 */
	public Object merge(Object entity) {
		return getHibernateTemplate().merge(entity);
	}

	/**
	 * 对象保存OR修改 方法 继承自HibernateTemplate  应用于持久化对象冲突时
	 * @param entityName
	 * @param entity
	 * @return
	 */
	public Object merge(String entityName, Object entity) {
		return getHibernateTemplate().merge(entityName, entity);
	}

	/**
	 * 清楚单个数据缓存方法
	 * @param entity
	 */
	public void evict(Object entity) {
		getHibernateTemplate().evict(entity);
	}

	/**
	 * 数据库对象锁方法 继承自hibnateTemplate
	 * 
	 * @param entityName
	 * @param entity
	 * @param lockMode
	 */
	public void lock(String entityName, Object entity, LockMode lockMode) {
		getHibernateTemplate().lock(entityName, entity, lockMode);
	}

	/**
	 * 数据库对象锁方法 继承自hibnateTemplate
	 * 
	 * @param entity
	 * @param lockMode
	 */
	public void lock(Object entity, LockMode lockMode) {
		getHibernateTemplate().lock(entity, lockMode);
	}

	/**
	 * 获取全部对象.
	 */
	public <T> List<T> getAll(Class<T> entityClass) {
		return getHibernateTemplate().loadAll(entityClass);
	}

	/**
	 * 获取全部对象,带排序字段与升降序参数.
	 */
	public <T> List<T> getAll(Class<T> entityClass, String orderBy,
			boolean isAsc) {
		Assert.hasText(orderBy);
		if (isAsc)
			return getHibernateTemplate().findByCriteria(
					DetachedCriteria.forClass(entityClass).addOrder(
							Order.asc(orderBy)));
		else
			return getHibernateTemplate().findByCriteria(
					DetachedCriteria.forClass(entityClass).addOrder(
							Order.desc(orderBy)));
	}

	/**
	 * 通过HQL语句查询LIST 对象
	 * @param queryString
	 * @return
	 */
	public <T> List<T> find(String queryString) {
		Assert.hasText(queryString);
		return getHibernateTemplate().find(queryString);
	}

	/**
	 * 保存或更新对象.
	 */
	public void saveOrUpdate(Object o) {
		getHibernateTemplate().saveOrUpdate(o);
	}

	/**
	 * 保存对象
	 * 
	 * @param o
	 */
	public void save(Object o) {
		getHibernateTemplate().save(o);
	}
	
	/**
	 * 保存并返回主键
	 */
	public Object saveReId(Object o) {
		return getHibernateTemplate().save(o);
	}
	
	/**
	 * 更新对象
	 * 
	 * @param o
	 */
	public void update(Object o) {
		getHibernateTemplate().update(o);
	}

	/**
	 * 删除对象.
	 */
	public void remove(Object o) {
		getHibernateTemplate().delete(o);
	}

	/**
	 * 根据ID删除对象.
	 */
	public <T> void removeById(Class<T> entityClass, Serializable id) {
		remove(get(entityClass, id));
	}

	/***
	 * 强行清除session缓存
	 */
	public void flush() {
		getHibernateTemplate().flush();
	}

	/***
	 * 清除缓存
	 */
	public void clear() {
		getHibernateTemplate().clear();
	}

	
	/**
	 * 创建Query对象.
	 * 对于需要first,max,fetchsize,cache,cacheRegion等诸多设置的函数,可以在返回Query后自行设置.
	 * 留意可以连续设置,如下示例
	 * 
	 * <pre>
	 * dao.getQuery(hql).setMaxResult(100).setCacheable(true).list();
	 * </pre>
	 * 
	 * 调用方式如下：
	 * 
	 * <pre>
	 *        dao.createQuery(hql)
	 *        dao.createQuery(hql,arg0);
	 *        dao.createQuery(hql,arg0,arg1);
	 *        dao.createQuery(hql,new Object[arg0,arg1,arg2])
	 * </pre>
	 * 
	 * @param values
	 *            可变参数.
	 */
	public Query createQuery(String hql, Object... values) {
		Assert.hasText(hql);
		Query query = getSession().createQuery(hql);
		for (int i = 0; i < values.length; i++) {
			query.setParameter(i, values[i]);
		}
		return query;
	}

	/**
	 * 创建Criteria对象.
	 * 
	 * @param criterions
	 *            可变的Restrictions条件列表,见{@link #createQuery(String,Object...)}
	 */
	public <T> Criteria createCriteria(Class<T> entityClass,
			Criterion... criterions) {
		Criteria criteria = getSession().createCriteria(entityClass);
		for (Criterion c : criterions) {
			criteria.add(c);
		}
		return criteria;
	}

	/**
	 * 创建Criteria对象，带排序字段与升降序字段.
	 * 
	 * @see #createCriteria(Class,Criterion[])
	 */
	public <T> Criteria createCriteria(Class<T> entityClass, String orderBy,
			boolean isAsc, Criterion... criterions) {
		// Assert.hasText(orderBy);
		Criteria criteria = createCriteria(entityClass, criterions);
		if (orderBy != null && !"".equals(orderBy)) {
			if (isAsc)
				criteria.addOrder(Order.asc(orderBy));
			else
				criteria.addOrder(Order.desc(orderBy));
		}
		return criteria;
	}

	/**
	 * 根据hql查询,直接使用HibernateTemplate的find函数.
	 * 
	 * @param values
	 *            可变参数,见{@link #createQuery(String,Object...)}
	 */
	public <T> List<T> find(String hql, Object... values) {
		Assert.hasText(hql);
		return getHibernateTemplate().find(hql, values);
	}

	/**
	 * 根据属性名和属性值查询对象.
	 * 
	 * @return 符合条件的对象列表
	 */
	public <T> List<T> findBy(Class<T> entityClass, String propertyName,
			Object value) {
		Assert.hasText(propertyName);
		return createCriteria(entityClass, Restrictions.eq(propertyName, value))
				.list();
	}

	/**
	 * 根据属性名和属性值查询对象,带排序参数.
	 */
	public <T> List<T> findBy(Class<T> entityClass, String propertyName,
			Object value, String orderBy, boolean isAsc) {
		Assert.hasText(propertyName);
		Assert.hasText(orderBy);
		return createCriteria(entityClass, orderBy, isAsc,
				Restrictions.eq(propertyName, value)).list();
	}

	/**
	 * 根据属性名和属性值查询唯一对象.
	 * 
	 * @return 符合条件的唯一对象 or null if not found.
	 */
	public <T> T findUniqueBy(Class<T> entityClass, String propertyName,
			Object value) {
		Assert.hasText(propertyName);
		return (T) createCriteria(entityClass,
				Restrictions.eq(propertyName, value)).uniqueResult();
	}

	/**
	 * 分页查询函数，根据HQL查询
	 * 
	 * @param pageNo 页号.
	 * @param pageSize 每页条目数
	 * @param hql HQL语句 
	 * @return 含结果记录数和当前页数据的Page对象.
	 */
	public <T> Pager<T> pagedQuery(String hql, int startRecord, int pageSize,
			Object... values) {
		Assert.hasText(hql);
		//总记录数
		long totalCount = getDataTotal(hql, values);
		if (totalCount < 1)
			return new Pager(totalCount, null);
		//当前页号
		int pageNo = Pager.validPageNo(startRecord,pageSize, totalCount);
		// 实际查询返回分页对象
		Query query = createQuery(hql, values);
		List list = query.setFirstResult(startRecord).setMaxResults(pageSize)
				.list();

		return new Pager(startRecord,pageNo, pageSize, totalCount, list);
	}

	/**
	 * 分页查询函数，根据criteria查询
	 * 
	 * @param pageNo 页号.
	 * @param pageSize 每页条目数
	 * @param criteria 查询对象分页 
	 * @return 含结果记录数和当前页数据的Page对象.
	 */
	public <T> Pager<T> pagedQuery(Criteria criteria, int startRecord, int pageSize) {
		Assert.notNull(criteria);
		// Assert.isTrue(pageNo >= 1, "pageNo should start from 1");
		CriteriaImpl impl = (CriteriaImpl) criteria;

		// 先把Projection和OrderBy条件取出�?,清空两�?�来执行Count操作
		Projection projection = impl.getProjection();
		List<CriteriaImpl.OrderEntry> orderEntries;
		try {
			orderEntries = (List) BeanUtils.forceGetProperty(impl,
					"orderEntries");
			BeanUtils.forceSetProperty(impl, "orderEntries", new ArrayList());
		} catch (Exception e) {
			throw new InternalError(" Runtime Exception impossibility throw ");
		}

		// 执行查询
		int totalCount = (Integer) criteria.setProjection(
				Projections.rowCount()).uniqueResult();

		// 将之前的Projection和OrderBy条件重新设回�?
		criteria.setProjection(projection);
		if (projection == null) {
			criteria.setResultTransformer(CriteriaSpecification.ROOT_ENTITY);
		}

		try {
			BeanUtils.forceSetProperty(impl, "orderEntries", orderEntries);
		} catch (Exception e) {
			throw new InternalError(" Runtime Exception impossibility throw ");
		}

		// 返回分页对象
		if (totalCount < 1)
			return new Pager(totalCount, null);
		//当前页号
		int pageNo = Pager.validPageNo(startRecord,pageSize, totalCount);

		List list = criteria.setFirstResult(startRecord).setMaxResults(pageSize)
				.list();
		return new Pager(startRecord,pageNo, pageSize, totalCount, list);
	}

	/**
	 * 分页查询函数，根据criteria查询
	 * 
	 * @param pageNo 页号.
	 * @param pageSize 每页条目数
	 * @param criteria 查询对象分页 
	 * @return 含结果记录数和当前页数据的Page对象.
	 */
	public <T> Pager<T> pagedQuery(Criteria criteria, String startRecord, String pageSize) {
		int intStartRecord = 0;
		int intPageSize = 10;
		if (startRecord != null && !"".equals(startRecord))
			intStartRecord = Integer.parseInt(startRecord);
		if (pageSize != null && !"".equals(pageSize))
			intPageSize = Integer.parseInt(pageSize);

		Pager pager = pagedQuery(criteria, intStartRecord, intPageSize);

		return pager;

	}

	/**
	 * 通过HQL语句方式查询分页  
	 * 调用方法  1.   pagedQuery( hql,  pageNo,  pageSize)
	 * 			 2.   pagedQuery( hql,  pageNo,  pageSize, values)
	 * @param pageNo 页号.
	 * @param pageSize 每页条目数
	 * @param hql  HQL语句方式查询对象 多应用于较复杂查询
	 * @param values 参数对象 可选参数
	 * @return 含结果记录数和当前页数据的Page对象.
	 */
	public <T> Pager<T> pagedQuery(String hql, String startRecord, String pageSize,
			Object... values) {
		int intStartRecord = 0;
		int intPageSize = 10;
		if (startRecord != null && !"".equals(startRecord))
			intStartRecord = Integer.parseInt(startRecord);
		if (pageSize != null && !"".equals(pageSize))
			intPageSize = Integer.parseInt(pageSize);
		Pager pager = pagedQuery(hql, intStartRecord, intPageSize, values);
		return pager;

	}

	/**
	 * 分页查询函数，根据entityClass和查询条件参数,排序参数创建默认Criteria查询对象
	 * <code>Criteria</code>.
	 * 
	 * @param pageNo 页号.
	 * @param pageSize 每页条目数
	 * @param entityClass  查询的类对象
	 * @param criterions 对象 可选参数
	 * @return 含结果记录数和当前页数据的Page对象.
	 */
	public <T> Pager<T> pagedQuery(Class entityClass, int startRecord, int pageSize,
			Criterion... criterions) {
		Criteria criteria = createCriteria(entityClass, criterions);
		return pagedQuery(criteria, startRecord, pageSize);
	}

	/**
	 * 分页查询函数，根据entityClass和查询条件参数,排序参数创建默认Criteria查询对象
	 * <code>Criteria</code>.
	 * 
	 * @param pageNo 页号.
	 * @param pageSize 每页条目数
	 * @param entityClass  查询的类对象
	 * @param orderBy 排序字段
	 * @param isAsc 升降序
	 * @param criterions 对象 可选参数
	 * @return 含结果记录数和当前页数据的Page对象.
	 */
	public <T> Pager<T> pagedQuery(Class entityClass, int startRecord, int pageSize,
			String orderBy, boolean isAsc, Criterion... criterions) {
		Criteria criteria = createCriteria(entityClass, orderBy, isAsc,
				criterions);
		return pagedQuery(criteria, startRecord, pageSize);
	}

	/**
	 * 判断对象某些属性的值在数据库中是否唯一.
	 * 
	 * @param uniquePropertyNames
	 *            在POJO里不能重复的属性列值,以,号分割如"name,loginid,password"
	 */
	public <T> boolean isUnique(Class<T> entityClass, Object entity,
			String uniquePropertyNames) {
		Assert.hasText(uniquePropertyNames);
		Criteria criteria = createCriteria(entityClass).setProjection(
				Projections.rowCount());
		criteria.add(Restrictions.eq("isDeleted", false));
		String[] nameList = uniquePropertyNames.split(",");
		try {
			// 循环加入唯一name
			for (String name : nameList) {
				criteria.add(Restrictions.eq(name, PropertyUtils.getProperty(
						entity, name)));
			}

			// 以下代码为了如果是update的情况,排除entity自身.

			String idName = getIdName(entityClass);

			// 取得entity的主键id
			Serializable id = getId(entityClass, entity);

			// 如果id!=null,说明对象已存在,该操作为update,加入排除自身的判断
			if (id != null)
				criteria.add(Restrictions.not(Restrictions.eq(idName, id)));
		} catch (Exception e) {
			ReflectionUtils.handleReflectionException(e);
		}

		return (Integer) criteria.uniqueResult() == 0;
	}

	/**
	 * 取得对象的主键编号,辅助函数.
	 */
	public Serializable getId(Class entityClass, Object entity)
			throws NoSuchMethodException, IllegalAccessException,
			InvocationTargetException {
		Assert.notNull(entity);
		Assert.notNull(entityClass);
		return (Serializable) PropertyUtils.getProperty(entity,
				getIdName(entityClass));
	}

	/**
	 * 取得对象的主键名,辅助函数.
	 */
	public String getIdName(Class clazz) {
		Assert.notNull(clazz);
		ClassMetadata meta = getSessionFactory().getClassMetadata(clazz);
		Assert.notNull(meta, "Class " + clazz
				+ " not define in hibernate session factory.");
		String idName = meta.getIdentifierPropertyName();
		Assert.hasText(idName, clazz.getSimpleName()
				+ " has no identifier property define.");
		return idName;
	}

	// /**
	// * 去除hql的select 子句，未考虑union的情�?,用于pagedQuery.
	// *
	// * @see #pagedQuery(String,int,int,Object[])
	// */
	// private static String removeSelect(String hql) {
	// Assert.hasText(hql);
	// int beginPos = hql.toLowerCase().indexOf("from");
	// Assert.isTrue(beginPos != -1, " hql : " + hql
	// + " must has a keyword 'from'");
	// return hql.substring(beginPos);
	// }

	/**
	 * 去除hql的orderby 子句，用于pagedQuery.
	 * 
	 * @see #pagedQuery(String,int,int,Object[])
	 */
	// private static String removeOrders(String hql) {
	// Assert.hasText(hql);
	// Pattern p = Pattern.compile("order\\s*by[\\w|\\W|\\s|\\S]*",
	// Pattern.CASE_INSENSITIVE);
	// Matcher m = p.matcher(hql);
	// StringBuffer sb = new StringBuffer();
	// while (m.find()) {
	// m.appendReplacement(sb, "");
	// }
	// m.appendTail(sb);
	// return sb.toString();
	// }
	/**
	 * persist把一个瞬态的实例持久化，但是并"不保证"标识符被立刻填入到持久化实例中，标识符的填入可能被推迟 到flush的时间。
	 * 
	 * persist"保证"，当它在一个transaction外部被调用的时候并不触发一个Sql Insert，这个功能是很有用的，
	 * 当我们通过继承Session/persistence context来封装一个长会话流程的时候，一个persist这样的函数是需要的。
	 * 
	 * 继承自 HibernateTemplate  
	 * 
	 */
	public void persist(Object entity) {
		getHibernateTemplate().persist(entity);
	}

	/**
	 * 通过SQL语句查询记录集
	 * 
	 * @param sql
	 * @param entitys
	 * @return
	 */
	public List findBySQL(String sql) {
		return findBySQL(sql, null, null);
	}

	/**
	 * 通过SQL语句查询记录集
	 * 
	 * @param sql
	 * @param entitys
	 * @return
	 */
	public List findBySQL(String sql, List entitys) {
		return findBySQL(sql, entitys, null);
	}

	/**
	 * 通过SQL语句查询记录集 应用回调方法操作SESSION
	 * 
	 * @param sql
	 * @param entitys
	 * @param joins
	 * @return
	 * @throws DataAccessException
	 */

	public List findBySQL(final String sql, final List entitys, final List joins)
			throws DataAccessException {

		return (List) getHibernateTemplate().executeWithNativeSession(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						SQLQuery sqlQuery = session.createSQLQuery(sql);
						if (entitys != null && !entitys.isEmpty()) {
							for (int i = 0; i < entitys.size(); i++) {
								if (entitys.get(i) instanceof Map) {
									Map entity = (Map) entitys.get(i);
									sqlQuery.addEntity((String) entity
											.get("alias"), (Class) entity
											.get("entityClass"));
								}
							}
						}
						if (joins != null && !joins.isEmpty()) {
							for (int i = 0; i < joins.size(); i++) {
								if (entitys.get(i) instanceof Map) {
									Map join = (Map) joins.get(i);
									sqlQuery.addJoin(
											(String) join.get("alias"),
											(String) join.get("path"));
								}
							}
						}
						return sqlQuery.list();
					}
				});
	}

	/**
	 * 获取符合HQL语句查询的记录集条目数
	 * 
	 * @param hql
	 * @param values
	 * @return 记录条目数
	 */

	// 新增获取HQL语句总条目数查询方法
	public Long getDataTotal(String hql, Object[]... values) {
		return getDataTotal(hql, values);
	}

	/**
	 * 获取符合HQL语句查询的记录集条目数 （模版模式 方法回调）
	 * 
	 * @param hql
	 * @param values
	 * @return 记录条目数
	 */
	// HQL语句查询方法 模版模式 方法回调
	public Long getDataTotal(final String hql, final Object[] values)
			throws DataAccessException {
		return (Long) getHibernateTemplate().executeWithNativeSession(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						// 将HQL语句 转换为SQL
						QueryTranslatorImpl queryTranslator = new QueryTranslatorImpl(
								hql, hql, Collections.EMPTY_MAP,
								(SessionFactoryImplementor) session
										.getSessionFactory());
						queryTranslator.compile(Collections.EMPTY_MAP, false);
						String tempSQL = queryTranslator.getSQLString();
						System.out.println(tempSQL);
						// 将对应转换完成的SQL语句 套入查询模版
						String countSQL = "select count(*) from (" + tempSQL
								+ ") tmp_count_t";
						// 创建Query 对象
						System.out.println(countSQL);
						Query query = session.createSQLQuery(countSQL);

						// 添加参数
						for (int i = 0; i < values.length; i++) {
							query.setParameter(i, values[i]);
						}

						// 执行查询
						List list = query.list();
						Long count = 0l;
						// 获取结果集
						if (list != null) {
							count = list.size() > 0 ? new Long(list.get(0)
									.toString()) : 0l;
						}
						return count;
					}
				});
	}
	
	/////////////////////////////////SQL分页
	public Long getDataTotalByTableName(String queryString) throws DataAccessException {
		
		String countSQL="select count(*) from ("+queryString+")";
		Query query = getSession().createSQLQuery(countSQL);
		List list = query.list();
		Long count = 0l;
		// 获取结果集
		if (list != null) {
			count = list.size() > 0 ? new Long(list.get(0).toString()) : 0l;
		}
		return count;
	}
	public <T> Pager<T> pagedQueryBySQL(String sql, int startRecord, int pageSize) {
		Assert.hasText(sql);
		//总记录数
		long totalCount = getDataTotalByTableName(sql);
		if (totalCount < 1)
			return new Pager(totalCount, null);
		//当前页号
		int pageNo = Pager.validPageNo(startRecord,pageSize, totalCount);
		// 实际查询返回分页对象
		Query query = getSession().createSQLQuery(sql);
		List list = query.setFirstResult(startRecord).setMaxResults(pageSize).list();
		return new Pager(startRecord,pageNo, pageSize, totalCount, list);
	}
}