package com.ces.framework.util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 提供值类型间按照一定的格式转换功能,例如：Double、Integer、String之间的转换
 * 
 * @see DecimalFormat
 * @author haic
 * 
 */
public class ConversionUtils {

	/**
	 * Double值转换成Integer
	 * 
	 * @param number
	 * @return
	 */
	public static Integer decimalFormatInteger(Double number) {
		DecimalFormat formula = new DecimalFormat("#");
		return new Integer(formula.format(number));
	}

	/**
	 * Double值转换成String，保留指定的小数点
	 * 
	 * @param number
	 * @param format
	 *            转换模式,参见 {@link DecimalFormat}
	 * @return
	 */
	public static String decimalFormat(Double number, String format) {
		DecimalFormat formula = new DecimalFormat(format);
		return formula.format(number);
	}

	/**
	 * 日期转换成String
	 * 
	 * @param format
	 *            转换模式,参见 {@link SimpleDateFormat}
	 * @param date
	 * @return
	 */
	public static String formatDate(String format, Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}
}
