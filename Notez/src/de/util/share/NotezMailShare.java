/*
 * $Header$
 * 
 * $Log$
 * Copyright � 2014 T.Ohm . All Rights Reserved.
 */
package de.util.share;

import java.io.File;
import java.io.IOException;

import de.gui.controller.NotezController;

public class NotezMailShare extends NotezShareBase
{
	protected String mailAdress;

	public NotezMailShare(String mailAdress)
	{
		this.mailAdress = mailAdress;
	}

	@Override
	public NotezShareResult shareNotez(NotezController ctrl, File notez)
		throws IOException
	{
		return null;
	}
}