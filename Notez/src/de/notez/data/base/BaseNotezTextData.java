package de.notez.data.base;

import de.notez.data.NotezTextData;
import de.util.NotezDataUtil;

/**
 * a basic implementation of the {@link NotezTextData}-interface
 *
 * @author ddd
 */
public class BaseNotezTextData implements NotezTextData
{
	/** The text of this data */
	protected String text;

	public BaseNotezTextData()
	{
		this(null);
	}

	public BaseNotezTextData(String text)
	{
		setText(text);
	}

	@Override
	public String getText()
	{
		return text;
	}

	@Override
	public void setText(String text)
	{
		this.text = text == null ? "" : text;
	}

	@Override
	public String toString()
	{
		return NotezDataUtil.toString(this);
	}
}
