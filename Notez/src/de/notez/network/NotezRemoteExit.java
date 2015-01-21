/*
 * Copyright © 2015 Unitechnik Systems GmbH. All Rights Reserved.
 */
package de.notez.network;

import java.io.IOException;

import com.sun.javafx.application.PlatformImpl;

import de.gui.NotezDialog;
import de.notez.NotezSystem;

/**
 * A {@link NotezRemoteAction} that askes the user, and uses {@link NotezSystem#exit()} if confirmed
 * 
 * @author ddd
 */
public class NotezRemoteExit implements NotezRemoteAction
{
	private static final long serialVersionUID = -2048216341581704036L;

	@Override
	public void exec() throws SecurityException
	{
		PlatformImpl.runLater(() ->
		{
			try
			{
				switch(NotezDialog.showQuestionDialog(null, "Exit Notez", "Do you really want to close notez?"))
				{
					default:
						return;
					case YES:
				}
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}

			NotezSystem.exit();
		});
	}

}
