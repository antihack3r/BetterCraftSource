// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.scoreboard;

import net.minecraft.entity.player.EntityPlayer;
import java.util.List;

public class ScoreDummyCriteria implements IScoreObjectiveCriteria
{
    private final String dummyName;
    
    public ScoreDummyCriteria(final String name) {
        this.dummyName = name;
        IScoreObjectiveCriteria.INSTANCES.put(name, this);
    }
    
    @Override
    public String getName() {
        return this.dummyName;
    }
    
    @Override
    public int setScore(final List<EntityPlayer> p_96635_1_) {
        return 0;
    }
    
    @Override
    public boolean isReadOnly() {
        return false;
    }
    
    @Override
    public EnumRenderType getRenderType() {
        return EnumRenderType.INTEGER;
    }
}
