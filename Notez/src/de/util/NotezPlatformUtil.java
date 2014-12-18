/*
 * Copyright © 2014 Unitechnik Systems GmbH. All Rights Reserved.
 */
package de.util;

import javafx.application.Platform;

import com.sun.javafx.application.PlatformImpl;

public class NotezPlatformUtil
{
	public static void initialize()
	{
		PlatformImpl.startup(() -> {});
		Platform.setImplicitExit(false);
	}
	
	public static void shutDown()
	{
		Platform.exit();
	}
}
