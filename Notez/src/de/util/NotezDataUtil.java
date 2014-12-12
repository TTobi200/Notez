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
		to.setPages(from.getPages().stream().map(textData -> equalize(textData, new BaseNotezTextData())).collect(Collectors.toList()));
		to.setCurPageIndex(from.getCurPageIndex());
		to.setText(from.getText());

		return to;
	}

	public static NotezTextData equalize(NotezTextData from, NotezTextData to)
	{
		to.setText(from.getText());

		return to;
	}
}
