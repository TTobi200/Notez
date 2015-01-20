package de.util;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Objects;

import de.notez.NotezSystem;
import de.util.log.NotezLog;

public class NotezUncaughtExceptionHandler implements UncaughtExceptionHandler
{
	public static synchronized void initialize()
	{
		if(Objects.isNull(n))
		{
			n = new NotezUncaughtExceptionHandler();
		}
	}
	
	private static NotezUncaughtExceptionHandler n;
	private NotezUncaughtExceptionHandler()
	{
		Thread.setDefaultUncaughtExceptionHandler(this);
	}
	
	@Override
	public void uncaughtException(Thread t, Throwable e)
	{
		if(NotezLoggerUtil.isLoggingInitialized())
		{
			NotezLog.fatal("Uncaught throwable", e);
		}
		
		NotezSystem.exit(NotezSystem.FATAL);
	}
}
