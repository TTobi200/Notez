package de.util.log;

public enum NotezLogLevel
{
	DEBUG("DEBUG"),
	INFO("INFO"),
	WARN("WARN"),
	ERROR("ERROR"),
	FATAL("FATAL");

	private String name;

	private NotezLogLevel(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}
}
