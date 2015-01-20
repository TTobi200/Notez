/*
 * $Header$
 * 
 * $Log$
 * Copyright © 2014 T.Ohm . All Rights Reserved.
 */
package de.util;

import static de.util.NotezTimeUtil.DEFAULT_FORMATTER;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.attribute.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import de.util.log.*;

/**
 * utility class for logging-related actions
 *
 * @author ddd
 */
public class NotezLoggerUtil
{
	/** extions of files containing error logs */
	public static final String ERR_FILE_EXT = ".error";
	/** extions of files containing debug logs */
	public static final String DEBUG_FILE_EXT = ".debug";
	
	private static boolean logInitialized = false;

	/**
	 * create the logging-message for the given message in the given level using
	 * {@link NotezTimeUtil#DEFAULT_FORMATTER}
	 *
	 * @param level
	 *            the level, the message should be logged in
	 * @param message
	 *            the message to log
	 * @return The message for logging containing information on the original message, the level and
	 *         the time
	 */
	public static String creMessageString(NotezLogLevel level, String message)
	{
		return creMessageString(level, message, DEFAULT_FORMATTER);
	}

	/**
	 * create the logging-message for the given message in the given level
	 *
	 * @param level
	 *            the level, the message should be logged in
	 * @param message
	 *            the message to log
	 * @param formatter
	 *            formatter for formatting the current time
	 * @return The message for logging containing information on the original message, the level and
	 *         the time
	 */
	public static String creMessageString(NotezLogLevel level, String message,
		DateTimeFormatter formatter)
	{
		LocalDateTime now = LocalDateTime.now();

		return new StringBuilder(level.getName())
				.append(" ")
				.append(
					Objects.isNull(formatter) ? DEFAULT_FORMATTER.format(now) : formatter
							.format(now)).append(": ").append(message).toString();
	}

	public static void initLogging(String folder, int daysToLog, boolean reorg) throws IOException
	{
		if(isLoggingInitialized())
		{
			return;
		}
		File logFolder = new File(folder);
		logFolder.mkdir();

		// DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HHmmss");
		// Date date = new Date();
		LocalDateTime now = LocalDateTime.now();

		// System.setErr(new PrintStream(logFolder.getAbsolutePath()
		// + File.separator
		// + dateFormat.format(date) + ERR_FILE_EXT));
		// System.setOut(new PrintStream(logFolder.getAbsolutePath()
		// + File.separator
		// + dateFormat.format(date)
		// + DEBUG_FILE_EXT));

		NotezStreamLogger errLogger = new NotezStreamLogger(new PrintStream(
				logFolder.getAbsolutePath() + File.separator
						+ NotezTimeUtil.FILE_NAME_TIME_FORMATTER.format(now)
						// + dateFormat.format(date)
						+ ERR_FILE_EXT));
		NotezStreamLogger outLogger = new NotezStreamLogger(new PrintStream(
				logFolder.getAbsolutePath() + File.separator
						+ NotezTimeUtil.FILE_NAME_TIME_FORMATTER.format(now)
						// + dateFormat.format(date)
						+ DEBUG_FILE_EXT));

		NotezLogger err = new NotezLoggerCollection(errLogger, new NotezStreamLogger(System.err,
				true));
		NotezLogger oout = new NotezLoggerCollection(outLogger, new NotezStreamLogger(System.out,
				true));

		System.setErr(new PrintStream(errLogger.getOutputStream())
		{
			@Override
			public void print(String s)
			{
				err.error(s);
			}
		});

		System.setOut(new PrintStream(outLogger.getOutputStream())
		{
			@Override
			public void print(String s)
			{
				oout.debug(s);
			}
		});

		File[] logfiles = logFolder.listFiles();
		if (reorg)
		{
			if (logfiles.length > (daysToLog * 2))
			{
				FileTime oldest = FileTime.fromMillis(System.currentTimeMillis());
				String oldestFileName = "";
				for(File log : logfiles)
				{
					BasicFileAttributes attr = Files.readAttributes(log.toPath(),
						BasicFileAttributes.class);

					if (oldest.compareTo(attr.creationTime()) > 0)
					{
						oldest = attr.creationTime();
						oldestFileName = log.getName().replace(ERR_FILE_EXT, "")
								.replace(DEBUG_FILE_EXT, "");
					}
				}

				new File(logFolder.getAbsolutePath() + File.separator + oldestFileName
						+ ERR_FILE_EXT).delete();
				new File(logFolder.getAbsolutePath() + File.separator + oldestFileName
						+ DEBUG_FILE_EXT).delete();
			}
		}
		
		logInitialized = true;
	}
	
	/**
	 * @return whether logging has been initialized
	 */
	public static boolean isLoggingInitialized()
	{
		return logInitialized;
	}
}
