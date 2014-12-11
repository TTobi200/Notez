package de.util.log;

public interface NotezLogger
{
	public default void log(NotezLogLevel level, String message)
	{
		log(level, message, null);
	}

	public void log(NotezLogLevel level, String message, Throwable cause);

	public default void info(String message)
	{
		log(NotezLogLevel.INFO, message);
	}
	public default void info(String message, Throwable cause)
	{
		log(NotezLogLevel.INFO, message, cause);
	}

	public default void debug(String message)
	{
		log(NotezLogLevel.DEBUG, message);
	}
	public default void debug(String message, Throwable cause)
	{
		log(NotezLogLevel.DEBUG, message, cause);
	}

	public default void warn(String message)
	{
		log(NotezLogLevel.WARN, message);
	}
	public default void warn(String message, Throwable cause)
	{
		log(NotezLogLevel.WARN, message, cause);
	}

	public default void error(String message)
	{
		log(NotezLogLevel.ERROR, message);
	}
	public default void error(String message, Throwable cause)
	{
		log(NotezLogLevel.ERROR, message, cause);
	}

	public default void fatal(String message)
	{
		log(NotezLogLevel.FATAL, message);
	}
	public default void fatal(String message, Throwable cause)
	{
		log(NotezLogLevel.FATAL, message, cause);
	}
}
