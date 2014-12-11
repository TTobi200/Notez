package de.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class NotezTimeUtil
{
	/** the default formatter for {@link LocalDateTime}s in Notez */
	public static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
	// public static final DateTimeFormatter DEFAULT_FORMATTER =
	// DateTimeFormatter.ofPattern("YYYY.MMMM.dd HH:mm:ss:SS");
	/** The formatter for times, that should be used in file-names */
	public static final DateTimeFormatter FILE_NAME_TIME_FORMATTER = DateTimeFormatter
			.ofPattern("YYYY_MM_dd_HHmmss");
}
