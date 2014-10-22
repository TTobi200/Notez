/*
 * Copyright © 2014 Unitechnik Systems GmbH. All Rights Reserved.
 */
package de.util.notez;

import javafx.geometry.Point2D;

public class NotezData
{
	private String title;
	private String text;
	private Point2D position;
	
	public NotezData(String title, String text, Point2D position)
	{
		super();
		this.title = title;
		this.text = text;
		this.position = position;
	}

	public Point2D getPosition()
	{
		return position;
	}
	
	public String getText()
	{
		return text;
	}
	
	public String getTitle()
	{
		return title;
	}
}
