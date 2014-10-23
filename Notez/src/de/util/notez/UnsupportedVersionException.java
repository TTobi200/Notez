/*
 * Copyright © 2014 Unitechnik Systems GmbH. All Rights Reserved.
 */
package de.util.notez;

import java.io.IOException;

public class UnsupportedVersionException extends IOException
{
	private static final long serialVersionUID = 1L;

	public UnsupportedVersionException(String version, Throwable cause)
	{
		super(creUnsupportedVersionMessage(version), cause);
	}

	public UnsupportedVersionException(String version)
	{
		super(creUnsupportedVersionMessage(version));
	}

	public UnsupportedVersionException(Throwable cause)
	{
		super(cause);
	}
	
	private static String creUnsupportedVersionMessage(String version)
	{
		return "Version " + version + " of NotezParser not supported";
	}

}
