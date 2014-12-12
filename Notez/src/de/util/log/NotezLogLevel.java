/*
 * $Header$
 * 
 * $Log$
 * Copyright © 2014 T.Ohm . All Rights Reserved.
 */
package de.util.log;

/**
 * Several levels for logging messaged.
 *
 * @author ddd
 */
public enum NotezLogLevel
{
	/** the smallest level, just for developers */
	DEBUG("DEBUG"),
	/** simple information, that could be interesting for the user */
	INFO("INFO"),
	/** something does not running the correct way, but the application keeps running */
	WARN("WARN"),
	/** An error occured and the application has to be shut down */
	ERROR("ERROR"),
	/**
	 * An unthought or uncaught exception occurred and ther is no possibility for the application to
	 * keep running
	 */
	FATAL("FATAL");

	private String name;

	private NotezLogLevel(String name)
	{
		this.name = name;
	}

	/**
	 * @return the name of this level.
	 */
	public String getName()
	{
		return name;
	}
}
