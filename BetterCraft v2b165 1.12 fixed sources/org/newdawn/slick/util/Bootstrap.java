// 
// Decompiled by Procyon v0.6.0
// 

package org.newdawn.slick.util;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.Game;

public class Bootstrap
{
    public static void runAsApplication(final Game game, final int width, final int height, final boolean fullscreen) {
        try {
            final AppGameContainer container = new AppGameContainer(game, width, height, fullscreen);
            container.start();
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
}
