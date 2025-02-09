// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.scoreboard;

import net.minecraft.util.text.TextFormatting;
import com.google.common.collect.Maps;
import java.util.Map;

public interface IScoreCriteria
{
    public static final Map<String, IScoreCriteria> INSTANCES = Maps.newHashMap();
    public static final IScoreCriteria DUMMY = new ScoreCriteria("dummy");
    public static final IScoreCriteria TRIGGER = new ScoreCriteria("trigger");
    public static final IScoreCriteria DEATH_COUNT = new ScoreCriteria("deathCount");
    public static final IScoreCriteria PLAYER_KILL_COUNT = new ScoreCriteria("playerKillCount");
    public static final IScoreCriteria TOTAL_KILL_COUNT = new ScoreCriteria("totalKillCount");
    public static final IScoreCriteria HEALTH = new ScoreCriteriaHealth("health");
    public static final IScoreCriteria FOOD = new ScoreCriteriaReadOnly("food");
    public static final IScoreCriteria AIR = new ScoreCriteriaReadOnly("air");
    public static final IScoreCriteria ARMOR = new ScoreCriteriaReadOnly("armor");
    public static final IScoreCriteria XP = new ScoreCriteriaReadOnly("xp");
    public static final IScoreCriteria LEVEL = new ScoreCriteriaReadOnly("level");
    public static final IScoreCriteria[] TEAM_KILL = { new ScoreCriteriaColored("teamkill.", TextFormatting.BLACK), new ScoreCriteriaColored("teamkill.", TextFormatting.DARK_BLUE), new ScoreCriteriaColored("teamkill.", TextFormatting.DARK_GREEN), new ScoreCriteriaColored("teamkill.", TextFormatting.DARK_AQUA), new ScoreCriteriaColored("teamkill.", TextFormatting.DARK_RED), new ScoreCriteriaColored("teamkill.", TextFormatting.DARK_PURPLE), new ScoreCriteriaColored("teamkill.", TextFormatting.GOLD), new ScoreCriteriaColored("teamkill.", TextFormatting.GRAY), new ScoreCriteriaColored("teamkill.", TextFormatting.DARK_GRAY), new ScoreCriteriaColored("teamkill.", TextFormatting.BLUE), new ScoreCriteriaColored("teamkill.", TextFormatting.GREEN), new ScoreCriteriaColored("teamkill.", TextFormatting.AQUA), new ScoreCriteriaColored("teamkill.", TextFormatting.RED), new ScoreCriteriaColored("teamkill.", TextFormatting.LIGHT_PURPLE), new ScoreCriteriaColored("teamkill.", TextFormatting.YELLOW), new ScoreCriteriaColored("teamkill.", TextFormatting.WHITE) };
    public static final IScoreCriteria[] KILLED_BY_TEAM = { new ScoreCriteriaColored("killedByTeam.", TextFormatting.BLACK), new ScoreCriteriaColored("killedByTeam.", TextFormatting.DARK_BLUE), new ScoreCriteriaColored("killedByTeam.", TextFormatting.DARK_GREEN), new ScoreCriteriaColored("killedByTeam.", TextFormatting.DARK_AQUA), new ScoreCriteriaColored("killedByTeam.", TextFormatting.DARK_RED), new ScoreCriteriaColored("killedByTeam.", TextFormatting.DARK_PURPLE), new ScoreCriteriaColored("killedByTeam.", TextFormatting.GOLD), new ScoreCriteriaColored("killedByTeam.", TextFormatting.GRAY), new ScoreCriteriaColored("killedByTeam.", TextFormatting.DARK_GRAY), new ScoreCriteriaColored("killedByTeam.", TextFormatting.BLUE), new ScoreCriteriaColored("killedByTeam.", TextFormatting.GREEN), new ScoreCriteriaColored("killedByTeam.", TextFormatting.AQUA), new ScoreCriteriaColored("killedByTeam.", TextFormatting.RED), new ScoreCriteriaColored("killedByTeam.", TextFormatting.LIGHT_PURPLE), new ScoreCriteriaColored("killedByTeam.", TextFormatting.YELLOW), new ScoreCriteriaColored("killedByTeam.", TextFormatting.WHITE) };
    
    String getName();
    
    boolean isReadOnly();
    
    EnumRenderType getRenderType();
    
    public enum EnumRenderType
    {
        INTEGER("INTEGER", 0, "integer"), 
        HEARTS("HEARTS", 1, "hearts");
        
        private static final Map<String, EnumRenderType> BY_NAME;
        private final String renderType;
        
        static {
            BY_NAME = Maps.newHashMap();
            EnumRenderType[] values;
            for (int length = (values = values()).length, i = 0; i < length; ++i) {
                final EnumRenderType iscorecriteria$enumrendertype = values[i];
                EnumRenderType.BY_NAME.put(iscorecriteria$enumrendertype.getRenderType(), iscorecriteria$enumrendertype);
            }
        }
        
        private EnumRenderType(final String s, final int n, final String renderTypeIn) {
            this.renderType = renderTypeIn;
        }
        
        public String getRenderType() {
            return this.renderType;
        }
        
        public static EnumRenderType getByName(final String name) {
            final EnumRenderType iscorecriteria$enumrendertype = EnumRenderType.BY_NAME.get(name);
            return (iscorecriteria$enumrendertype == null) ? EnumRenderType.INTEGER : iscorecriteria$enumrendertype;
        }
    }
}
