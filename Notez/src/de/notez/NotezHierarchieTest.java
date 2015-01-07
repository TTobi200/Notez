/*
 * Copyright © 2015 Unitechnik Systems GmbH. All Rights Reserved.
 */
package de.notez;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

import de.util.NotezPlatformUtil;

public class NotezHierarchieTest
{
	private int testNumber = 0;
	
	@Test
	public void test()
	{
		NotezPlatformUtil.initialize();
		
		NotezNote note1 = new NotezNote(new File("1"));
		NotezNote note2 = new NotezNote(new File("2"));
		NotezNote note3 = new NotezNote(new File("3"));
		
		testNumber++;
		
		testNote(note1, 1, null, null);
		testNote(note2, 2, null, null);
		testNote(note3, 3, null, null);
		
		testNumber++;
		note2.setNoteParent(note1);
		
		testNote(note1, 1, null, note2);
		testNote(note2, 2, note1, null);
		testNote(note3, 3, null, null);
		
		testNumber++;
		note3.setNoteParent(note2);
		
		testNote(note1, 1, null, note2);
		testNote(note2, 2, note1, note3);
		testNote(note3, 3, note2, null);
		
		testNumber++;
		note3.setNoteParent(note1);
		
		testNote(note1, 1, null, note3);
		testNote(note2, 2, null, null);
		testNote(note3, 3, note1, null);
	}
	
	@Test
	public void test2()
	{
		NotezNote[] note = new NotezNote[6];
		
		for(int i = 0; i < note.length; i++)
		{
			note[i] = new NotezNote(new File(String.valueOf(i)));
		}
		
		for(int i = 1; i < note.length; i++)
		{
			note[i].setNoteParent(note[i - 1]);
		}
		
		testNumber++;
		testNote(note[0], 1, null, note[1]);
		
		for(int i = 1; i < note.length - 1; i++)
		{
			testNote(note[i], i + 1, note[i - 1], note[i + 1]);
		}
		
		testNote(note[note.length - 1], note.length, note[note.length - 2], null);
		
		testNumber++;
		note[2].setNoteParent(null);
		note[4].setNoteParent(null);
		
		for(int i = 0; i < note.length; i += 2)
		{
			testNote(note[i], i + 1, null, note[i + 1]);
			testNote(note[i + 1], i + 2, note[i], null);
		}
		
		testNumber++;
		note[4].setNoteParent(note[0]);
		note[2].setNoteParent(note[4]);
		
		testNote(note[0], 0, null, note[4]);
		testNote(note[1], 1, null, null);
		testNote(note[2], 2, note[4], note[3]);
		testNote(note[3], 3, note[2], null);
		testNote(note[4], 4, note[0], note[2]);
		testNote(note[5], 5, null, null);
	}
	
	private void testNote(NotezNote note, int noteIndex, NotezNote expPar, NotezNote expChild)
	{
		assertEquals("test" + testNumber + ": note" + noteIndex + " wrong parent", expPar, note.noteParentProperty().get());
		assertEquals("test" + testNumber + ": note" + noteIndex + " wrong child", expChild, note.noteChildProperty().get());
	}
}
