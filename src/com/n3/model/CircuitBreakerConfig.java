/*
 * 版权声明 .
 * 此文档的版权归通联支付网络服务有限公司所有
 * Powered By [AIPSEE-framework]
 */

package com.n3.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

/**
 * TcircuitBreakerConfig 的modle 用于封装与应用程序的业务逻辑相关的数据.
 * @author 
 * @version 1.0
 * @since 1.0
 */

@Entity
@Table(name="T_CIRCUIT_BREAKER_CONFIG")
@IdClass(CircuitBreakerConfigPK.class)
public class CircuitBreakerConfig {
	
	//columns START
	/** 变量 name . */
	private java.lang.String name;
	/** 变量 webServerName . */
	private java.lang.String webServerName;
	/** 变量 urlPattern . */
	private java.lang.String urlPattern;
	/** 变量 currentState . */
	private java.lang.String currentState;
	/** 变量 lastUpdTs . */
	private String lastUpdTs;
	
	private int concurrency;
	private int bufferSize;
	//columns END

	/**
	* TcircuitBreakerConfig 的构造函数
	*/
	public CircuitBreakerConfig() {
	}
	/**
	* TcircuitBreakerConfig 的构造函数
	*/
	public CircuitBreakerConfig(
		java.lang.String name,
		java.lang.String webServerName
	) {
		this.name = name;
		this.webServerName = webServerName;
	}

	/**
	 * Name 置值.
	 * @param  java.lang.String
	 */	
	public void setName(java.lang.String value) {
		this.name = value;
	}
	/**
	 * Name 取值.
	 * @return java.lang.String
	 */
	@Id
	public java.lang.String getName() {
		return this.name;
	}
	/**
	 * WebServerName 置值.
	 * @param  java.lang.String
	 */	
	public void setWebServerName(java.lang.String value) {
		this.webServerName = value;
	}
	/**
	 * WebServerName 取值.
	 * @return java.lang.String
	 */
	@Id
	@Column(name="WEB_SERVER_NAME")
	public java.lang.String getWebServerName() {
		return this.webServerName;
	}
	/**
	 * UrlPattern 置值.
	 * @param  java.lang.String
	 */	
	public void setUrlPattern(java.lang.String value) {
		this.urlPattern = value;
	}
	/**
	 * UrlPattern 取值.
	 * @return java.lang.String
	 */
	@Column(name="URL_PATTERN")
	public java.lang.String getUrlPattern() {
		return this.urlPattern;
	}
	/**
	 * CurrentState 置值.
	 * @param  java.lang.String
	 */	
	public void setCurrentState(java.lang.String value) {
		this.currentState = value;
	}
	/**
	 * CurrentState 取值.
	 * @return java.lang.String
	 */
	@Column(name="CURRENT_STATE")
	public java.lang.String getCurrentState() {
		return this.currentState;
	}
	
	/**
	 * LastUpdTs 置值.
	 * @param  java.sql.Date
	 */	
	public void setLastUpdTs(String value) {
		this.lastUpdTs = value;
	}
	/**
	 * LastUpdTs 取值.
	 * @return java.sql.Date
	 */
	@Column(name="LAST_UPD_TS")
	public String getLastUpdTs() {
		return this.lastUpdTs;
	}
	public int getConcurrency() {
		return concurrency;
	}
	public void setConcurrency(int concurrency) {
		this.concurrency = concurrency;
	}
	
	@Column(name="BUFFER_SIZE")
	public int getBufferSize() {
		return bufferSize;
	}
	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}
}

