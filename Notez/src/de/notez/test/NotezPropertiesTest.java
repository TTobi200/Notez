package de.notez.test;

import static org.junit.Assert.assertEquals;
import javafx.beans.property.SimpleStringProperty;

import org.junit.*;

import de.notez.prop.*;

public class NotezPropertiesTest
{

	@Test
	public void testProperties()
	{
		NotezProperties p = new NotezProperties();

		p.putBoolean("bool", true);
		p.putLong("long", Long.MAX_VALUE);
		p.putInt("int", 0);
		p.putFloat("float", .5f);
		p.putDouble("double", Double.MAX_VALUE);
		p.putString("string", "test");
		p.putObject("object", p);

		assertEquals(NotezProperties.NotezPropertyType.BOOL, p.getType("bool"));
		assertEquals(NotezProperties.NotezPropertyType.LONG, p.getType("long"));
		assertEquals(NotezProperties.NotezPropertyType.INT, p.getType("int"));
		assertEquals(NotezProperties.NotezPropertyType.FLOAT, p.getType("float"));
		assertEquals(NotezProperties.NotezPropertyType.DOUBLE, p.getType("double"));
		assertEquals(NotezProperties.NotezPropertyType.STRING, p.getType("string"));
		assertEquals(NotezProperties.NotezPropertyType.OBJECT, p.getType("object"));
		assertEquals(NotezProperties.NotezPropertyType.NULL, p.getType("null"));

		p.putBoolean("long", true);
		p.putLong("int", Long.MAX_VALUE);
		p.putInt("float", 0);
		p.putFloat("double", .5f);
		p.putDouble("string", Double.MAX_VALUE);
		p.putString("object", "test");
		p.putObject("null", p);

		assertEquals(NotezProperties.NotezPropertyType.BOOL, p.getType("bool"));
		assertEquals(NotezProperties.NotezPropertyType.BOOL, p.getType("long"));
		assertEquals(NotezProperties.NotezPropertyType.LONG, p.getType("int"));
		assertEquals(NotezProperties.NotezPropertyType.INT, p.getType("float"));
		assertEquals(NotezProperties.NotezPropertyType.FLOAT, p.getType("double"));
		assertEquals(NotezProperties.NotezPropertyType.DOUBLE, p.getType("string"));
		assertEquals(NotezProperties.NotezPropertyType.STRING, p.getType("object"));
		assertEquals(NotezProperties.NotezPropertyType.OBJECT, p.getType("null"));
	}

	@Test
	public void testFinalProperties()
	{
		NotezProperties p = new NotezFinalProperties();

		p.putBoolean("bool", true);
		p.putLong("long", Long.MAX_VALUE);
		p.putInt("int", 0);
		p.putFloat("float", .5f);
		p.putDouble("double", Double.MAX_VALUE);
		p.putString("string", "test");
		p.putObject("object", new Object());

		assertEquals(NotezProperties.NotezPropertyType.BOOL, p.getType("bool"));
		assertEquals(NotezProperties.NotezPropertyType.INT, p.getType("int"));
		assertEquals(NotezProperties.NotezPropertyType.LONG, p.getType("long"));
		assertEquals(NotezProperties.NotezPropertyType.FLOAT, p.getType("float"));
		assertEquals(NotezProperties.NotezPropertyType.DOUBLE, p.getType("double"));
		assertEquals(NotezProperties.NotezPropertyType.STRING, p.getType("string"));
		assertEquals(NotezProperties.NotezPropertyType.OBJECT, p.getType("object"));
		assertEquals(NotezProperties.NotezPropertyType.NULL, p.getType("null"));

		try
		{
			p.putBoolean("long", true);
			Assert.fail("putBoolean should have thrown an exception.");
		}
		catch(IllegalStateException e)
		{

		}
		try
		{
			p.putLong("bool", Long.MAX_VALUE);
			Assert.fail("putLong should have thrown an exception.");
		}
		catch(IllegalStateException e)
		{

		}
		try
		{
			p.putInt("bool", 0);
			Assert.fail("putInt should have thrown an exception.");
		}
		catch(IllegalStateException e)
		{

		}
		try
		{
			p.putFloat("bool", .5f);
			Assert.fail("putFloat should have thrown an exception.");
		}
		catch(IllegalStateException e)
		{

		}
		try
		{
			p.putDouble("bool", Double.MAX_VALUE);
			Assert.fail("putDouble should have thrown an exception.");
		}
		catch(IllegalStateException e)
		{

		}
		try
		{
			p.putString("bool", "test");
			Assert.fail("putString should have thrown an exception.");
		}
		catch(IllegalStateException e)
		{

		}
		try
		{
			p.putObject("bool", new Object());
			Assert.fail("putObject should have thrown an exception.");
		}
		catch(IllegalStateException e)
		{

		}
		p.put("null", new SimpleStringProperty());
	}
}
