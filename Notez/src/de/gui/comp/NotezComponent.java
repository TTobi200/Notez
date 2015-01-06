package de.gui.comp;

import de.gui.NotezGui;
import de.notez.NotezNote;

/**
 * basic interface a compoment of a notezgui should implement
 * 
 * @author ddd
 */
public interface NotezComponent
{
	/**
	 * @param gui the gui this component is placed in
	 */
	public void setGui(NotezGui gui);
	/**
	 * @return the gui this component is placed in
	 */
	public NotezGui getGui();
	public void setListener();

	public default NotezNote getNote()
	{
		return getGui().getNote();
	}
}
