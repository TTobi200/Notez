/*
 * Copyright © 2014 Unitechnik Systems GmbH. All Rights Reserved.
 */
package de.util.share;

import java.io.File;
import java.io.IOException;

import de.gui.NotezController;

public interface NotezShare
{
    public void shareNotez(NotezController ctrl, File notez)
        throws IOException;
}
