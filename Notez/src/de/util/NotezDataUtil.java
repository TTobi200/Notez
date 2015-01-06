/*
 * $Header$
 * 
 * $Log$
 * Copyright © 2014 T.Ohm . All Rights Reserved.
 */
package de.util;

import java.util.stream.Collectors;

import de.notez.data.NotezData;
import de.notez.data.NotezPagedData;
import de.notez.data.NotezStageData;
import de.notez.data.NotezTextData;
import de.notez.data.base.BaseNotezTextData;

public class NotezDataUtil
{
	public static NotezData equalize(NotezData from, NotezData to)
	{
		to.setTitle(from.getTitle());
		equalize(from.getPageData(), to.getPageData());
		equalize(from.getStageData(), to.getStageData());

		return to;
	}

	public static NotezStageData equalize(NotezStageData from, NotezStageData to)
	{
		to.setStageX(from.getStageX());
		to.setStageY(from.getStageY());
		to.setStageWidth(from.getStageWidth());
		to.setStageHeight(from.getStageHeight());

		return to;
	}

	public static NotezPagedData equalize(NotezPagedData from, NotezPagedData to)
	{
		to.setPages(from.getPages()
			.stream()
			.map(textData -> equalize(textData, new BaseNotezTextData()))
			.collect(Collectors.toList()));
		to.setCurPageIndex(from.getCurPageIndex());
		to.setText(from.getText());

		return to;
	}

	public static NotezTextData equalize(NotezTextData from, NotezTextData to)
	{
		to.setText(from.getText());

		return to;
	}

	public static String toString(NotezTextData textData)
	{
		return "textData: " + textData.getText();
	}

	public static String toString(NotezPagedData pagedData)
	{
		StringBuilder sb = new StringBuilder("pageData: ").append("index: ")
			.append(pagedData.getCurPageIndex())
			.append(" pages: ")
			.append("[");

		sb.append(toString(pagedData.getPages().get(0)));

		for(int i = 1; i < pagedData.getPages().size(); i++)
		{
			sb.append(", ").append(toString(pagedData.getPages().get(i)));
		}

		sb.append("]");

		return sb.toString();
	}

	public static String toString(NotezStageData stageData)
	{
		return new StringBuilder("stageData: ").append("x: ")
			.append(stageData.getStageX())
			.append(" y: ")
			.append(stageData.getStageY())
			.append(" width: ")
			.append(stageData.getStageWidth())
			.append(" height: ")
			.append(stageData.getStageHeight())
			.toString();
	}

	public static String toString(NotezData data)
	{
		return new StringBuilder("notezData: ").append("title: ")
			.append(data.getTitle())
			.append(toString(data.getStageData()))
			.append(toString(data.getPageData()))
			.toString();
	}
}
