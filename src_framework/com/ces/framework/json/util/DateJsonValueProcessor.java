/**
 * 
 */
package com.ces.framework.json.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;

/**
 * @author ch
 * @since 2010-04-09
 */
public class DateJsonValueProcessor implements JsonValueProcessor {
	public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
	private DateFormat dateFormat;

	public DateJsonValueProcessor() {
		dateFormat = new SimpleDateFormat(DEFAULT_DATE_PATTERN);
	}

	/**
	 * 构造方法.
	 * 
	 * @param datePattern
	 *            日期格式
	 */
	public DateJsonValueProcessor(String datePattern) {
		dateFormat = new SimpleDateFormat(datePattern);
	}

	public Object processObjectValue(String key, Object value, JsonConfig config) {
		if (value instanceof Date) {
			String str = dateFormat.format((Date) value);
			return str;
		}
		if (value instanceof Timestamp) {
			String str = dateFormat.format((Timestamp) value);
			return str;
		}
		return value == null ? null : value.toString();
	}

	public Object processArrayValue(Object value, JsonConfig config) {
		String[] obj = {};
		if (value instanceof Date[]) {
			Date[] dates = (Date[]) value;
			obj = new String[dates.length];
			for (int i = 0; i < dates.length; i++) {
				obj[i] = dateFormat.format(dates[i]);
			}
		}
		if (value instanceof Timestamp[]) {
			Timestamp[] timestamps = (Timestamp[]) value;
			obj = new String[timestamps.length];
			for (int i = 0; i < timestamps.length; i++) {
				obj[i] = dateFormat.format(timestamps[i]);
			}
		}

		return obj;
	}

}