package com.ces.framework.json.util;

import org.dozer.Mapper;
import org.hibernate.MappingException;

/**
 * Vo和Po转换器,能够完成VO和PO的相互转换
 * 
 * @author haichen
 */
public class VoPoConverter {
	/**
	 * 将源对象中的属性值复制到目标对象的同名属性中<br>
	 * 属性名相同,类型必须相同<br>
	 * 注意:src和desc都必须存在
	 * 
	 * @param src
	 *            源对象
	 * @param desc
	 *            目标对象
	 */
	public static void copyProperties(Object desc, Object src) {
		Mapper mapper = DozerMapperSingleton.getInstance();
		try {
			mapper.map(src, desc);
		} catch (MappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 将源对象中的属性值复制到目标对象的同名属性中<br>
	 * 属性名相同,类型必须相同<br>
	 * 注意:src必须存在,desc由dozer帮我们创建
	 * 
	 * @param src
	 *            源对象
	 * @param desc
	 *            目标对象的类型
	 * @param clazz
	 * 			  目标对象的class对象
	 */
	public static <T> T copyProperties(Class<T> clazz, Object src) {
		Mapper mapper =  DozerMapperSingleton.getInstance();
		try {
			return (T) mapper.map(src, clazz);
		} catch (MappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
