/*
 * $Header$
 * 
 * $Log$
 * Copyright © 2014 T.Ohm . All Rights Reserved.
 */
package de.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NotezRegexUtil
{
	public static final String EMAIL_PATTERN =
					"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
									+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	public static final String IP_PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
											+
											"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
											+
											"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
											+
											"([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";;

	private static boolean matches(String pattern, Object obj)
	{
		Matcher matcher = Pattern.compile(
			pattern).matcher(String.valueOf(obj));
		return matcher.matches();
	}

	public static boolean isIp(Object obj)
	{
		return matches(IP_PATTERN, obj);
	}

	public static boolean isMailAdress(Object obj)
	{
		return matches(EMAIL_PATTERN, obj);
	}
}