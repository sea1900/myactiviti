package com.ces.framework.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

/**
 * 用于读写Property属性配制文件的Singleton类
 * 
 * @author hc
 * @version 1.0 2012-06-13
 * 
 */
public class PropUtils {
	private static Properties prop = new Properties();

	private PropUtils() {
	};

	/**
	 * 通过属性配制文件的Key值获得String型的Value值
	 * 
	 * @param filePath
	 *            属性配制文件的完整路径(相对于classes路径)
	 * @param key
	 *            属性配制文件的Key值
	 * 
	 * @return Key值所对应的int Value值
	 */
	public static String getStringValue(String filePath, String key) {
		String value = null;
		InputStream ins = null;
		try {
			// getResourceAsStream读取的不是最新值
			String path = PropUtils.class.getClassLoader()
					.getResource(filePath).getPath();
			File file = new File(path);
			ins = new BufferedInputStream(new FileInputStream(file));
			prop.load(ins);
			value = prop.getProperty(key);
			return StringUtils.isEmpty(value) ? null : value.trim();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (ins != null)
					ins.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	/**
	 * 通过属性配制文件的Key值获得int型的Value值
	 * 
	 * @param filePath
	 *            属性配制文件的完整路径(相对于classes路径)
	 * @param key
	 *            属性配制文件的Key值
	 * 
	 * @return Key值所对应的int Value值
	 */
	public static int getIntValue(String filePath, String key) {
		String value = getStringValue(filePath, key);

		return StringUtils.isBlank(value) ? 0 : Integer.valueOf(value);
	}

	/**
	 * 通过属性配制文件的Key值获得Double型的Value值
	 * 
	 * @param filePath
	 *            属性配制文件的完整路径(相对于classes路径)
	 * @param key
	 *            属性配制文件的Key值
	 * 
	 * @return Key值所对应的Double Value值
	 */
	public static Double getDoubleValue(String filePath, String key) {
		String value = getStringValue(filePath, key);

		return StringUtils.isBlank(value) ? 0 : Double.valueOf(value);
	}

	/**
	 * 通过属性配制文件的Key值获得Boolean型的Value值
	 * 
	 * @param filePath
	 *            属性配制文件的完整路径(相对于classes路径)
	 * @param key
	 *            属性配制文件的Key值
	 * 
	 * @return Key值所对应的boolean Value值
	 */
	public static boolean getBooleanValue(String filePath, String key) {
		String value = getStringValue(filePath, key);

		return StringUtils.isBlank(value) ? false : Boolean.valueOf(value);
	}

	/**
	 * 通过Key值将String型的Value值写入指定的配置文件
	 * 
	 * @param filePath
	 *            属性配制文件的完整路径(相对于classes路径)
	 * @param key
	 *            属性配制文件的Key值
	 * @param Key值所对应的String
	 *            Value值
	 * 
	 * @return
	 */
	public static void writeProp(String filePath, String key, String value) {
		Properties prop = new Properties();
		InputStream ins = null;
		OutputStream ous = null;
		try {
			ins = PropUtils.class.getClassLoader()
					.getResourceAsStream(filePath);
			prop.load(ins);
			ins.close();

			URL url = PropUtils.class.getClassLoader().getResource(filePath);
			File file = new File(url.toURI());
			if (!file.exists())
				throw new IOException("file is not exist!");

			ous = new FileOutputStream(file);
			prop.setProperty(key, value);
			prop.store(ous, "Update '" + key + "' value");

			ous.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} finally {
			try {
				if (ins != null)
					ins.close();
				if (ous != null)
					ous.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 通过Key值将int型的Value值写入指定的配置文件
	 * 
	 * @param filePath
	 *            属性配制文件的完整路径(相对于classes路径)
	 * @param key
	 *            属性配制文件的Key值
	 * @param Key值所对应的int
	 *            Value值
	 * 
	 * @return
	 */
	public static void writeProp(String filePath, String key, int value) {
		writeProp(filePath, key, String.valueOf(value));
	}
	
	/**
	 * 通过Key值将Double型的Value值写入指定的配置文件
	 * 
	 * @param filePath
	 *            属性配制文件的完整路径(相对于classes路径)
	 * @param key
	 *            属性配制文件的Key值
	 * @param Key值所对应的Double
	 *            Value值
	 * 
	 * @return
	 */
	public static void writeProp(String filePath, String key, Double value) {
		writeProp(filePath, key, String.valueOf(value));
	}
	
	/**
	 * 通过Key值将boolean型的Value值写入指定的配置文件
	 * 
	 * @param filePath
	 *            属性配制文件的完整路径(相对于classes路径)
	 * @param key
	 *            属性配制文件的Key值
	 * @param Key值所对应的boolean
	 *            Value值
	 * 
	 * @return
	 */
	public static void writeProp(String filePath, String key, boolean value) {
		writeProp(filePath, key, String.valueOf(value));
	}

	public static void main(String[] args) {
		System.out.println(StringUtils.isBlank(null));
	}
}
