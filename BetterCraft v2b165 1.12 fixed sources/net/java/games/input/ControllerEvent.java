// 
// Decompiled by Procyon v0.6.0
// 

package net.java.games.input;

public class ControllerEvent
{
    private Controller controller;
    
    public ControllerEvent(final Controller c) {
        this.controller = c;
    }
    
    public Controller getController() {
        return this.controller;
    }
}
