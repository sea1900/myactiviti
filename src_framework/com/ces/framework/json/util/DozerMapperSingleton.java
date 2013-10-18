package com.ces.framework.json.util;

import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;

/**
 * 单例模式
 * 
 * @author hhcc
 * 
 */
public class DozerMapperSingleton {
	private static Mapper mapper = new DozerBeanMapper();

	public DozerMapperSingleton() {
	}

	public static Mapper getInstance() {
		return mapper;
	}
}
