/*
 * $Header$
 * 
 * $Log$
 * Copyright © 2014 T.Ohm . All Rights Reserved.
 */
package de.util.log;

/**
 * A basic logger for Notez.<br>
 * The interface is mainly designed for using {@link #log(NotezLogLevel, String)} nad
 * {@link #log(NotezLogLevel, String, Throwable)} but also provides methods for every
 * {@link NotezLogLevel} for easier usage.
 *
 * @author ddd
 */
public interface NotezLogger
{
	/**
	 * Log the given message in the given level.
	 *
	 * @param level The level to log at
	 * @param message the message to log
	 */
	public default void log(NotezLogLevel level, String message)
	{
		log(level, message, null);
	}

	/**
	 * Log the given message in the given level.
	 *
	 * @param level The level to log at
	 * @param message the message to log
	 * @param cause a throwable to log, will be ignored if null.
	 */
	public void log(NotezLogLevel level, String message, Throwable cause);

	/**
	 * Log the given message in the {@link NotezLogLevel#INFO}-level.
	 *
	 * @param message the message to log
	 */
	public default void info(String message)
	{
		log(NotezLogLevel.INFO, message);
	}

	/**
	 * Log the given message in the {@link NotezLogLevel#INFO}-level.
	 *
	 * @param message the message to log
	 * @param cause a throwable to log, will be ignored if null.
	 */
	public default void info(String message, Throwable cause)
	{
		log(NotezLogLevel.INFO, message, cause);
	}

	/**
	 * Log the given message in the {@link NotezLogLevel#DEBUG}-level.
	 *
	 * @param message the message to log
	 */
	public default void debug(String message)
	{
		log(NotezLogLevel.DEBUG, message);
	}

	/**
	 * Log the given message in the {@link NotezLogLevel#DEBUG}-level.
	 *
	 * @param message the message to log
	 * @param cause a throwable to log, will be ignored if null.
	 */
	public default void debug(String message, Throwable cause)
	{
		log(NotezLogLevel.DEBUG, message, cause);
	}

	/**
	 * Log the given message in the {@link NotezLogLevel#WARN}-level.
	 *
	 * @param message the message to log
	 */
	public default void warn(String message)
	{
		log(NotezLogLevel.WARN, message);
	}

	/**
	 * Log the given message in the {@link NotezLogLevel#WARN}-level.
	 *
	 * @param message the message to log
	 * @param cause a throwable to log, will be ignored if null.
	 */
	public default void warn(String message, Throwable cause)
	{
		log(NotezLogLevel.WARN, message, cause);
	}

	/**
	 * Log the given message in the {@link NotezLogLevel#ERROR}-level.
	 *
	 * @param message the message to log
	 */
	public default void error(String message)
	{
		log(NotezLogLevel.ERROR, message);
	}

	/**
	 * Log the given message in the {@link NotezLogLevel#ERROR}-level.
	 *
	 * @param message the message to log
	 * @param cause a throwable to log, will be ignored if null.
	 */
	public default void error(String message, Throwable cause)
	{
		log(NotezLogLevel.ERROR, message, cause);
	}

	/**
	 * Log the given message in the {@link NotezLogLevel#FATAL}-level.
	 *
	 * @param message the message to log
	 */
	public default void fatal(String message)
	{
		log(NotezLogLevel.FATAL, message);
	}

	/**
	 * Log the given message in the {@link NotezLogLevel#FATAL}-level.
	 *
	 * @param message the message to log
	 * @param cause a throwable to log, will be ignored if null.
	 */
	public default void fatal(String message, Throwable cause)
	{
		log(NotezLogLevel.FATAL, message, cause);
	}
}
