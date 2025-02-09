/*
 * Decompiled with CFR 0.152.
 */
package javazoom.jl.player.advanced;

import javazoom.jl.player.advanced.AdvancedPlayer;

public class PlaybackEvent {
    public static int STOPPED = 1;
    public static int STARTED = 2;
    private AdvancedPlayer source;
    private int frame;
    private int id;

    public PlaybackEvent(AdvancedPlayer source, int id2, int frame) {
        this.id = id2;
        this.source = source;
        this.frame = frame;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id2) {
        this.id = id2;
    }

    public int getFrame() {
        return this.frame;
    }

    public void setFrame(int frame) {
        this.frame = frame;
    }

    public AdvancedPlayer getSource() {
        return this.source;
    }

    public void setSource(AdvancedPlayer source) {
        this.source = source;
    }
}

