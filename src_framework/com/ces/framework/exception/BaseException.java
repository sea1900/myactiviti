package com.ces.framework.exception;

/**
 * 异常基类
 * 
 * @version 1.0
 * @author haichen
 */

public class BaseException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * <p>
	 * The message key for this message.
	 * </p>
	 */
	protected String key = null;

	/**
	 * <p>
	 * The replacement values for this mesasge.
	 * </p>
	 */
	protected Object[] values = null;

	public BaseException() {
	}

	public BaseException(String message) {
		super(message);
		key = message;
	}

	public BaseException(String message, Throwable cause) {
		super(message, cause);
	}

	public BaseException(Throwable cause) {
		super(cause);
	}

	public BaseException(String key, Object... value) {
		this.key = key;
		this.values = value;
	}

}
