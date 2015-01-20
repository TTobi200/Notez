package de.util.test;

import java.util.*;

import org.junit.*;

import de.util.param.*;

public class ParametersTest
{
	private List<String[]> keys;
	private List<String> arguments;
	private String[] args;
	private ParameterList<Parameter> p;

	@Before
	public void setUpBeforeClass()
	{
		keys = new ArrayList<>();
		keys.add("test 1 2 3".split(" "));
		keys.add("was geht hier ab".split(" "));
		keys.add("xxx".split(" "));
		keys.add("yyy".split(" "));
		keys.add("zzz".split(" "));
		keys.add("t t t t t".split(" "));

		arguments = new ArrayList<>();

		for(String[] s : keys)
		{
			s[0] = KeyParameter.KEY_PREFIX + s[0];

			arguments.addAll(Arrays.asList(s));
		}

		args = arguments.stream().toArray(String[]::new);

		p = Parameters.parse(args);
	}

	@Test
	public void testGeneralParsing()
	{
		Assert.assertEquals("the parsed list has not the right length", args.length, p.size());

		for(int i = 0; i < args.length; i++)
		{
			Assert.assertEquals("the " + (i + 1) + "th parameter is wrong", args[i], p.get(i)
					.getName());
		}
	}

	@Test
	public void testKeyParsing()
	{
		Assert.assertEquals("the wrong number of key is present", keys.size(), p.getKeyParameters()
				.size());

		for(String[] key : keys)
		{
			Assert.assertTrue("key isn't present", p.isKeyPresent(key[0]));
			Assert.assertFalse("key is present multiple times.", p.isKeyMultiplePresent(key[0]));
		}

		for(int i = 0; i < keys.size(); i++)
		{
			String[] key = keys.get(i);
			KeyParameter keyParam = p.getKeyParameters().get(i);

			Assert.assertEquals("the key " + key[0] + " has the wrong number of arguments",
				key.length - 1, keyParam.getParameter().size());

			for(int j = 1; j < key.length; j++)
			{
				Assert.assertEquals("wrong parameter at position " + (j - 1) + " of key " + key[0],
					key[j], keyParam.getParameter().get(j - 1).getName());
			}
		}
	}

	@Test
	public void testOverAllParsing()
	{
		// test correct parsing
		int i = 0;
		for(String[] key : keys)
		{
			KeyParameter kp = (KeyParameter)p.get(i);
			Assert.assertTrue("Parameter " + i + " is no keyparameter: " + kp.getName(),
				kp.isKeyParameter());

			Assert.assertEquals(args[i], kp.getName());
			for(int j = 1; j < key.length; j++)
			{
				Assert.assertFalse("Parameter " + (i + j) + " is a keyparameter: "
						+ p.get(i + j).getName(), p.get(i + j).isKeyParameter());
			}

			ParameterList<Parameter> pp = kp.getParameter();

			for(int j = 0; j < pp.size(); j++)
			{
				Assert.assertEquals("parameter not given correctly to keyvalue", key[1 + j], pp
						.get(j).getName());
			}

			Assert.assertTrue("key " + key[0] + " not present", p.isKeyPresent(key[0]));
			Assert.assertNotNull(p.getKeyParameter(key[0]));
			Assert.assertEquals(key[0], p.getKeyParameter(key[0]).getName());

			i += key.length;
		}
	}
}
