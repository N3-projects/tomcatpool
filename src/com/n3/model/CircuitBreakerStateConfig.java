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
 * TcircuitBreakerState 的modle 用于封装与应用程序的业务逻辑相关的数据.
 * @author 
 * @version 1.0
 * @since 1.0
 */

@Entity
@Table(name="T_CIRCUIT_BREAKER_STATE")
@IdClass(CircuitBreakerStateConfigPK.class)
public class CircuitBreakerStateConfig {
	
	//columns START
	/** 变量 name . */
	private java.lang.String name;
	/** 变量 circuitBreakerName . */
	private java.lang.String circuitBreakerName;
	/** 变量 webServerName . */
	private java.lang.String webServerName;
	private java.lang.Long targetTimes;
	/** 变量 thresholdSuccessTimes . */
	private java.lang.Long thresholdSuccessTimes;
	/** 变量 thresholdFailureTimes . */
	private java.lang.Long thresholdFailureTimes;
	/** 变量 thresholdSuccessRate . */
	private java.math.BigDecimal thresholdSuccessRate;
	/** 变量 thresholdFailureRate . */
	private java.math.BigDecimal thresholdFailureRate;
	/** 变量 delaySeconds . */
	private java.lang.Long delaySeconds;
	/** 变量 periodSeconds . */
	private java.lang.Long periodSeconds;
	//columns END

	/**
	* TcircuitBreakerState 的构造函数
	*/
	public CircuitBreakerStateConfig() {
	}
	/**
	* TcircuitBreakerState 的构造函数
	*/
	public CircuitBreakerStateConfig(
		java.lang.String name,
		java.lang.String circuitBreakerName,
		java.lang.String webServerName
	) {
		this.name = name;
		this.circuitBreakerName = circuitBreakerName;
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
	 * CircuitBreakerName 置值.
	 * @param  java.lang.String
	 */	
	public void setCircuitBreakerName(java.lang.String value) {
		this.circuitBreakerName = value;
	}
	/**
	 * CircuitBreakerName 取值.
	 * @return java.lang.String
	 */
	@Id
	@Column(name="CIRCUIT_BREAKER_NAME")
	public java.lang.String getCircuitBreakerName() {
		return this.circuitBreakerName;
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
	 * ThresholdSuccessTimes 置值.
	 * @param  java.lang.Long
	 */	
	public void setThresholdSuccessTimes(java.lang.Long value) {
		this.thresholdSuccessTimes = value;
	}
	/**
	 * ThresholdSuccessTimes 取值.
	 * @return java.lang.Long
	 */
	@Column(name="THRESHOLD_SUCCESS_TIMES")
	public java.lang.Long getThresholdSuccessTimes() {
		return this.thresholdSuccessTimes;
	}
	/**
	 * ThresholdFailureTimes 置值.
	 * @param  java.lang.Long
	 */	
	public void setThresholdFailureTimes(java.lang.Long value) {
		this.thresholdFailureTimes = value;
	}
	/**
	 * ThresholdFailureTimes 取值.
	 * @return java.lang.Long
	 */
	@Column(name="THRESHOLD_FAILURE_TIMES")
	public java.lang.Long getThresholdFailureTimes() {
		return this.thresholdFailureTimes;
	}
	/**
	 * ThresholdSuccessRate 置值.
	 * @param  java.math.BigDecimal
	 */	
	public void setThresholdSuccessRate(java.math.BigDecimal value) {
		this.thresholdSuccessRate = value;
	}
	/**
	 * ThresholdSuccessRate 取值.
	 * @return java.math.BigDecimal
	 */
	@Column(name="THRESHOLD_SUCCESS_RATE")
	public java.math.BigDecimal getThresholdSuccessRate() {
		return this.thresholdSuccessRate;
	}
	/**
	 * ThresholdFailureRate 置值.
	 * @param  java.math.BigDecimal
	 */	
	public void setThresholdFailureRate(java.math.BigDecimal value) {
		this.thresholdFailureRate = value;
	}
	/**
	 * ThresholdFailureRate 取值.
	 * @return java.math.BigDecimal
	 */
	@Column(name="THRESHOLD_FAILURE_RATE")
	public java.math.BigDecimal getThresholdFailureRate() {
		return this.thresholdFailureRate;
	}
	/**
	 * DelaySeconds 置值.
	 * @param  java.lang.Integer
	 */	
	public void setDelaySeconds(java.lang.Long value) {
		this.delaySeconds = value;
	}
	/**
	 * DelaySeconds 取值.
	 * @return java.lang.Integer
	 */
	@Column(name="DELAY_SECONDS")
	public java.lang.Long getDelaySeconds() {
		return this.delaySeconds;
	}
	/**
	 * PeriodSeconds 置值.
	 * @param  java.lang.Integer
	 */	
	public void setPeriodSeconds(java.lang.Long value) {
		this.periodSeconds = value;
	}
	/**
	 * PeriodSeconds 取值.
	 * @return java.lang.Integer
	 */
	@Column(name="PERIOD_SECONDS")
	public java.lang.Long getPeriodSeconds() {
		return this.periodSeconds;
	}
	@Column(name="TARGET_TIMES")
	public java.lang.Long getTargetTimes() {
		return targetTimes;
	}
	public void setTargetTimes(java.lang.Long targetTimes) {
		this.targetTimes = targetTimes;
	}
	
}

