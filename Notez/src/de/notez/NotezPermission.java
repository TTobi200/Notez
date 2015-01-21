/*
 * Copyright © 2015 Unitechnik Systems GmbH. All Rights Reserved.
 */
package de.notez;

import java.security.BasicPermission;

public class NotezPermission extends BasicPermission
{
	private static final long serialVersionUID = 1L;
	
	public static final NotezPermission PERMISSION_NOTEZ_DELETE = new NotezPermission("notezDelete");

	public NotezPermission(String name, String actions)
	{
		super(name, actions);
	}

	public NotezPermission(String name)
	{
		super(name);
	}
}
