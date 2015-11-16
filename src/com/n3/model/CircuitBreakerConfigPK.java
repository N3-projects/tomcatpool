/*
 * 版权声明 .
 * 此文档的版权归通联支付网络服务有限公司所有
 * Powered By [AIPSEE-framework]
 */

package com.n3.model;

import java.io.Serializable;

/**
 * TcircuitBreakerConfigPk ,用于联合主键.
 * @author 
 * @version 1.0
 * @since 1.0
 */

public class CircuitBreakerConfigPK implements Serializable{
	
	//key columns START
	/** 变量  name . */
	private java.lang.String name;
	/** 变量  webServerName . */
	private java.lang.String webServerName;
	//key columns END
	/**
	* TcircuitBreakerConfigPk  的构造函数 .
	*/
	public CircuitBreakerConfigPK() {
	}
	/**
	* TcircuitBreakerConfigPk  的构造函数 .
	*/
	public CircuitBreakerConfigPK(
		java.lang.String name ,
		java.lang.String webServerName 
	) {
		this.name = name;
		this.webServerName = webServerName;
	}

	
	/**
	* Name 置值 .
	* @param  java.lang.String
	*/	
	public void setName(java.lang.String value) {
	this.name = value;
	}
	/**
	* Name 取值 .
	* @return java.lang.String
	*/
	public java.lang.String getName() {
	return this.name;
	}
	/**
	* WebServerName 置值 .
	* @param  java.lang.String
	*/	
	public void setWebServerName(java.lang.String value) {
	this.webServerName = value;
	}
	/**
	* WebServerName 取值 .
	* @return java.lang.String
	*/
	public java.lang.String getWebServerName() {
	return this.webServerName;
	}


}



