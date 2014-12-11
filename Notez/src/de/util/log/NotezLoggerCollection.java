package de.util.log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class NotezLoggerCollection implements NotezLogger
{
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

	public void addLogger(NotezLogger... logger)
	{
		this.logger.addAll(Arrays.asList(logger));
	}

	public void removeLogger(NotezLogger... logger)
	{
		this.logger.removeAll(Arrays.asList(logger));
	}

	public void removeAllLogger()
	{
		logger.clear();
	}
}
