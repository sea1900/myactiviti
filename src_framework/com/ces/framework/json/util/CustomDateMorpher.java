package com.ces.framework.json.util;

import java.util.Date;

import net.sf.ezmorph.MorpherRegistry;
import net.sf.ezmorph.object.DateMorpher;
import net.sf.json.util.JSONUtils;

/**  
 * 自定义转为json需注册的Morpher
 * 处理日期转换  
 *   
 * @author hhcc  
 * @version 1.0,2010-09-09  
 */ 
public class CustomDateMorpher {
	public static final String[] DATE_FORMAT = {
		"yyyy-MM-dd HH:mm:ss",
		"yyyy-MM-dd HH:mm",
		"yyyy-MM-dd",
		"yyyy-MM"
	};   
	  
    public static void registerMorpher() {
    	Date date = new Date();
        MorpherRegistry morpherRegistry = JSONUtils.getMorpherRegistry();   
        morpherRegistry.registerMorpher(new DateMorpher(DATE_FORMAT,date));   
    }   
}
