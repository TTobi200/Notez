/*
 * Copyright © 2015 Unitechnik Systems GmbH. All Rights Reserved.
 */
package de.notez.network;

import java.security.Permission;
import java.util.Objects;

/**
 * A special manager for notez<br>
 * all action done by a {@link NotezRemoteAction} that will be requested are forbidden
 * 
 * @author ddd
 */
public class NotezSecurityManager extends SecurityManager
{
	/** The single-instance notez security manager */
	private static NotezSecurityManager manager;
	
	/**
	 * Install this manager as securitymanager - does nothing if already installed
	 */
	protected static void installSecurityManager()
	{
		if(System.getSecurityManager() != manager)
		{
			manager = new NotezSecurityManager();
		}
	}
	
	private SecurityManager sec;
	
	private NotezSecurityManager()
	{
		sec = System.getSecurityManager();
		System.setSecurityManager(this);
	}
	
	@Override
	public void checkPermission(Permission perm, Object context)
	{
		checkRemoteThread();
		
		if(Objects.nonNull(sec))
		{
			sec.checkPermission(perm, context);
		}
	}

	@Override
	public void checkPermission(Permission perm)
	{
		checkRemoteThread();
		
		if(Objects.nonNull(sec))
		{
			sec.checkPermission(perm);
		}
	}
	
	protected void checkRemoteThread() throws SecurityException
	{
		if(Thread.currentThread() == NotezServer.getRemoteActionThread())
		{
			throw new SecurityException();
		}
	}
}
