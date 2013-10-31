package com.ces.framework.json.util;

import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * 
 * json格式的转换类<br/>
 * <br/>
 * 可能出现的问题:原始转换时,注意有Date类型字段的对象转换问题 ,原因:
 * 这是因为bean里有Date字段，且从数据库里读出来的是{@link java.sql.Date}赋值给了{@link java.util.Date},
 * 转化成JSONArray时出错解决：可以在从数据库读出Date时直接写成：new
 * java.util.Date(rs.getDate("date").getTime)<br/>
 * <br/>
 * 2.0添加支持对java.sql.Timestamp对象的转换支持
 * 
 * @author hc
 * @version 2.0
 */
public class JsonConverter {
	private final static Logger log = Logger.getLogger(JsonConverter.class);

	/**
	 * 将对象转换为json格式(未处理时间格式)
	 * 
	 * @param obj
	 * @param response
	 */
	public static void beanToJsonOriginal(Object obj,
			HttpServletResponse response) {
		String json = "";
		try {
			json = JSONObject.fromObject(obj).toString();
			response.setContentType("text/javascript;charset=UTF-8");
			log.debug(json);
			PrintWriter out = response.getWriter();
			out.write(json);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将list或数组转换为json格式(未处理时间格式)
	 * 
	 * @param obj
	 * @param response
	 */
	public static void listToJsonOriginal(Object obj,
			HttpServletResponse response) {
		String json = "";
		try {
			json = JSONArray.fromObject(obj).toString();
			response.setContentType("text/javascript;charset=UTF-8");
			log.debug(json);
			PrintWriter out = response.getWriter();
			out.write(json);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将对象转换为json格式(处理时间,默认时间格式)
	 * 
	 * @param obj
	 * @param response
	 */
	public static void beanToJson(Object obj, HttpServletResponse response) {
		beanToJson(obj, response, null);
	}

	/**
	 * 将对象转换为json格式(处理时间,指定时间格式)
	 * 
	 * @param obj
	 * @param response
	 * @param datePattern
	 */
	public static void beanToJson(Object obj, HttpServletResponse response,
			String datePattern) {
		String json = "";
		try {
			json = JSONObject.fromObject(obj,
					JsonConverter.configJson(datePattern)).toString();
			response.setContentType("text/javascript;charset=UTF-8");
			log.debug(json);
			PrintWriter out = response.getWriter();
			out.write(json);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将list或数组转换为json格式(处理时间,默认时间格式)
	 * 
	 * @param obj
	 * @param response
	 */
	public static void listToJson(Object obj, HttpServletResponse response) {
		listToJson(obj, response, null);
	}

	/**
	 * 将数组或List转换为json格式(处理时间,指定时间格式)
	 * 
	 * @param obj
	 * @param response
	 * @param datePattern
	 */
	public static void listToJson(Object obj, HttpServletResponse response,
			String datePattern) {
		String json = "";
		try {
			json = JSONArray.fromObject(obj,
					JsonConverter.configJson(datePattern)).toString();
			response.setContentType("text/javascript;charset=UTF-8");
			log.debug(json);
			PrintWriter out = response.getWriter();
			out.write(json);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 配置json-lib需要的excludes和datePattern.
	 * 
	 * @param datePattern
	 *            日期转换模式
	 * @return JsonConfig 根据dataPattern生成的jsonConfig，用于write
	 */
	public static JsonConfig configJson(String datePattern) {
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setExcludes(new String[] { "" });
		jsonConfig.setIgnoreDefaultExcludes(false);
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

		if (StringUtils.isNotBlank(datePattern)) {
			jsonConfig.registerJsonValueProcessor(Date.class,
					new DateJsonValueProcessor(datePattern));
			jsonConfig.registerJsonValueProcessor(Timestamp.class,
					new DateJsonValueProcessor(datePattern));
		} else {
			jsonConfig.registerJsonValueProcessor(Date.class,
					new DateJsonValueProcessor());
			jsonConfig.registerJsonValueProcessor(Timestamp.class,
					new DateJsonValueProcessor());
		}
		return jsonConfig;
	}

	/**
	 * 从一个JSON 对象字符格式中得到一个java对象
	 * 
	 * @param jsonString
	 * @param pojoCalss
	 * @return
	 */
	public static Object getObjectFromJson(String json, Class<?> pojoCalss) {
		Object pojo;
		JSONObject jsonObject = JSONObject.fromObject(json);
		CustomDateMorpher.registerMorpher();
		pojo = JSONObject.toBean(jsonObject, pojoCalss);
		return pojo;
	}

	/**
	 * 从一个JSON 数组格式中得到一个对象集合
	 * 
	 * @param jsonString
	 * @param pojoCalss
	 * @return
	 */
	public static Collection<?> getCollectionFromJson(String json,
			Class<?> pojoCalss) {
		Collection<?> coll;
		JSONArray jsonArray = JSONArray.fromObject(json);
		CustomDateMorpher.registerMorpher();
		coll = JSONArray.toCollection(jsonArray, pojoCalss);
		return coll;
	}
}