/*
 * Copyright © 2014 Unitechnik Systems GmbH. All Rights Reserved.
 */
package de.notez.network;

import java.util.EventListener;

/**
 * Listenerinterface for {@link NotezRemoteObjectEvent}s
 */
public interface NotezRemoteObjectListener extends EventListener
{
	/**
	 * Any notezclient received a remoteobject
	 * 
	 * @param e the event occured
	 */
	public void remoteObjectReceived(NotezRemoteObjectEvent e);
}
