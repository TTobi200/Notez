/*
 * $Header$
 * 
 * $Log$
 * Copyright © 2014 T.Ohm . All Rights Reserved.
 */
package de;

import java.io.FileNotFoundException;
import java.io.PrintStream;

import javafx.application.Application;
import de.gui.NotezFrame;

public class Startup
{
	public static void main(String[] args) throws FileNotFoundException
	{
		// FORTEST when running executable jar
		System.setErr(new PrintStream("./err.txt"));
		Application.launch(NotezFrame.class, args);
	}
}