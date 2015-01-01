/*
 * Copyright © 2014 Unitechnik Systems GmbH. All Rights Reserved.
 */
package de.gui.comp;

import de.gui.NotezGui;
import de.gui.NotezNote;

public interface NotezComponent
{
	public void setGui(NotezGui gui);
	public NotezGui getGui();
	public void setListener();

	public default NotezNote getNote()
	{
		return getGui().getNote();
	}
}
