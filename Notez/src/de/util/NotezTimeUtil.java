package de.util;

import java.time.format.DateTimeFormatter;

public class NotezTimeUtil
{
	public static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
//	public static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("YYYY.MMMM.dd HH:mm:ss:SS");
	public static final DateTimeFormatter FILE_NAME_TIME_FORMATTER = DateTimeFormatter.ofPattern("YYYY_MM_dd_HHmmss");
}
