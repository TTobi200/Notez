/*
 * Copyright © 2015 Unitechnik Systems GmbH. All Rights Reserved.
 */
package de.notez.network;

import java.io.Serializable;

// TODO could be a huge security issue
/**
 * An action to be done on the remote side.<br>
 * Be aware that this class has not even a single reference to any object of this vm and only uses static actions.
 * 
 * @author ddd
 */
public interface NotezRemoteAction extends NotezRemoteObject, Serializable
{
	/**
	 * Execute the task of this object.
	 */
	public void exec();
}
