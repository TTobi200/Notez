package de.util.log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * A logger using a collection of loggers.
 *
 * @author ddd
 */
public class NotezLoggerCollection implements NotezLogger
{
	/** the loggers to log to */
	protected Collection<NotezLogger> logger;

	public NotezLoggerCollection(NotezLogger... logger)
	{
		this.logger = new ArrayList<>(Arrays.asList(logger));
	}

	@Override
	public void log(NotezLogLevel level, String message, Throwable cause)
	{
		logger.forEach(logger -> logger.log(level, message, cause));
	}

	/**
	 * @param logger additional loggers for this collection
	 */
	public void addLogger(NotezLogger... logger)
	{
		this.logger.addAll(Arrays.asList(logger));
	}

	/**
	 * @param logger loggers to be removed from this collection
	 */
	public void removeLogger(NotezLogger... logger)
	{
		this.logger.removeAll(Arrays.asList(logger));
	}

	/**
	 * Remove all loggers from this collection
	 */
	public void removeAllLogger()
	{
		logger.clear();
	}
}
