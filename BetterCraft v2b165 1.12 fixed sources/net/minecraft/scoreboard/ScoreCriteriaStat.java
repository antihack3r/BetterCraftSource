// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.scoreboard;

import net.minecraft.stats.StatBase;

public class ScoreCriteriaStat extends ScoreCriteria
{
    private final StatBase stat;
    
    public ScoreCriteriaStat(final StatBase statIn) {
        super(statIn.statId);
        this.stat = statIn;
    }
}
