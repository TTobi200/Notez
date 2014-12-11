package de.util.log;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

import de.util.NotezLoggerUtil;

public class NotezStreamLogger implements NotezLogger
{
	private OutputStream out;
	protected boolean addLineSeparator;

	public NotezStreamLogger(OutputStream stream)
	{
		this(stream, false);
	}

	public NotezStreamLogger(OutputStream stream, boolean addLineSeparator)
	{
		out = Objects.requireNonNull(stream);
		this.addLineSeparator = addLineSeparator;
	}

	@Override
	public void log(NotezLogLevel level, String message, Throwable cause)
	{
		try
		{
			message = NotezLoggerUtil.creMessageString(level, message);
			out.write(message.getBytes());

			if(addLineSeparator && !message.endsWith(System.lineSeparator()))
			{
				out.write(System.lineSeparator().getBytes());
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	public OutputStream getOutputStream()
	{
		return out;
	}
}
