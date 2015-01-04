/*
 * $Header$
 *
 * $Log$
 * Copyright © 2014 T.Ohm . All Rights Reserved.
 */
package de.notez.parser;

import java.io.IOException;

/**
 * Exception indicating the given version of a notezparser requested is not supported.
 *
 * @author ddd
 */
public class UnsupportedVersionException extends IOException
{
    private static final long serialVersionUID = 1L;

    protected String version;

    protected UnsupportedVersionException(String version, Throwable cause)
    {
        super(creUnsupportedVersionMessage(version), cause);
        this.version = version;
    }

    protected UnsupportedVersionException(String version)
    {
        super(creUnsupportedVersionMessage(version));
        this.version = version;
    }

    protected UnsupportedVersionException(Throwable cause)
    {
        super("Null-Version not supported", cause);
    }

    private static String creUnsupportedVersionMessage(String version)
    {
        return "Version " + version + " of NotezParser not supported";
    }

    /**
     * @return the version-string that is not supported
     */
    public String getVersion()
	{
		return version;
	}
}
