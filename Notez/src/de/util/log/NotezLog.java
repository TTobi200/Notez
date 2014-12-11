package de.util.log;

import de.util.NotezLoggerUtil;

public class NotezLog
{
	public static final NotezLogger systemLogger = (level, message, cause) -> {
		switch(level)
		{
			case INFO:
			case DEBUG:
			{
				System.out.println(NotezLoggerUtil.creMessageString(level, message));
				cause.printStackTrace(System.out);
				break;
			}
			case ERROR:
			case FATAL:
			case WARN:
			{
				System.err.println(NotezLoggerUtil.creMessageString(level, message));
				cause.printStackTrace(System.err);
				break;
			}
		}
	};
	private static NotezLogger defaultLogger;

	public static NotezLogger getDefaultLogger()
	{
		return defaultLogger;
	}

	public static void setDefaultLogger(NotezLogger defaultLogger)
	{
		NotezLog.defaultLogger = defaultLogger;
	}

	public static void log(NotezLogLevel level, String message)
	{
		log(level, message, null);
	}
	public static void log(NotezLogLevel level, String message, Throwable cause)
	{
		getDefaultLogger().log(level, message, cause);
	}

	public static void info(String message)
	{
		log(NotezLogLevel.INFO, message);
	}
	public static void info(String message, Throwable cause)
	{
		log(NotezLogLevel.INFO, message, cause);
	}

	public static void debug(String message)
	{
		log(NotezLogLevel.DEBUG, message);
	}
	public static void debug(String message, Throwable cause)
	{
		log(NotezLogLevel.DEBUG, message, cause);
	}

	public static void warn(String message)
	{
		log(NotezLogLevel.WARN, message);
	}
	public static void warn(String message, Throwable cause)
	{
		log(NotezLogLevel.WARN, message, cause);
	}

	public static void error(String message)
	{
		log(NotezLogLevel.ERROR, message);
	}
	public static void error(String message, Throwable cause)
	{
		log(NotezLogLevel.ERROR, message, cause);
	}

	public static void fatal(String message)
	{
		log(NotezLogLevel.FATAL, message);
	}
	public static void fatal(String message, Throwable cause)
	{
		log(NotezLogLevel.FATAL, message, cause);
	}
}
