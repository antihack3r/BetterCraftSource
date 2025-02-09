/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.scoreboard;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.WorldSavedData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ScoreboardSaveData
extends WorldSavedData {
    private static final Logger logger = LogManager.getLogger();
    private Scoreboard theScoreboard;
    private NBTTagCompound delayedInitNbt;

    public ScoreboardSaveData() {
        this("scoreboard");
    }

    public ScoreboardSaveData(String name) {
        super(name);
    }

    public void setScoreboard(Scoreboard scoreboardIn) {
        this.theScoreboard = scoreboardIn;
        if (this.delayedInitNbt != null) {
            this.readFromNBT(this.delayedInitNbt);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        if (this.theScoreboard == null) {
            this.delayedInitNbt = nbt;
        } else {
            this.readObjectives(nbt.getTagList("Objectives", 10));
            this.readScores(nbt.getTagList("PlayerScores", 10));
            if (nbt.hasKey("DisplaySlots", 10)) {
                this.readDisplayConfig(nbt.getCompoundTag("DisplaySlots"));
            }
            if (nbt.hasKey("Teams", 9)) {
                this.readTeams(nbt.getTagList("Teams", 10));
            }
        }
    }

    protected void readTeams(NBTTagList p_96498_1_) {
        int i2 = 0;
        while (i2 < p_96498_1_.tagCount()) {
            Team.EnumVisible team$enumvisible1;
            Team.EnumVisible team$enumvisible;
            NBTTagCompound nbttagcompound = p_96498_1_.getCompoundTagAt(i2);
            String s2 = nbttagcompound.getString("Name");
            if (s2.length() > 16) {
                s2 = s2.substring(0, 16);
            }
            ScorePlayerTeam scoreplayerteam = this.theScoreboard.createTeam(s2);
            String s1 = nbttagcompound.getString("DisplayName");
            if (s1.length() > 32) {
                s1 = s1.substring(0, 32);
            }
            scoreplayerteam.setTeamName(s1);
            if (nbttagcompound.hasKey("TeamColor", 8)) {
                scoreplayerteam.setChatFormat(EnumChatFormatting.getValueByName(nbttagcompound.getString("TeamColor")));
            }
            scoreplayerteam.setNamePrefix(nbttagcompound.getString("Prefix"));
            scoreplayerteam.setNameSuffix(nbttagcompound.getString("Suffix"));
            if (nbttagcompound.hasKey("AllowFriendlyFire", 99)) {
                scoreplayerteam.setAllowFriendlyFire(nbttagcompound.getBoolean("AllowFriendlyFire"));
            }
            if (nbttagcompound.hasKey("SeeFriendlyInvisibles", 99)) {
                scoreplayerteam.setSeeFriendlyInvisiblesEnabled(nbttagcompound.getBoolean("SeeFriendlyInvisibles"));
            }
            if (nbttagcompound.hasKey("NameTagVisibility", 8) && (team$enumvisible = Team.EnumVisible.func_178824_a(nbttagcompound.getString("NameTagVisibility"))) != null) {
                scoreplayerteam.setNameTagVisibility(team$enumvisible);
            }
            if (nbttagcompound.hasKey("DeathMessageVisibility", 8) && (team$enumvisible1 = Team.EnumVisible.func_178824_a(nbttagcompound.getString("DeathMessageVisibility"))) != null) {
                scoreplayerteam.setDeathMessageVisibility(team$enumvisible1);
            }
            this.func_96502_a(scoreplayerteam, nbttagcompound.getTagList("Players", 8));
            ++i2;
        }
    }

    protected void func_96502_a(ScorePlayerTeam p_96502_1_, NBTTagList p_96502_2_) {
        int i2 = 0;
        while (i2 < p_96502_2_.tagCount()) {
            this.theScoreboard.addPlayerToTeam(p_96502_2_.getStringTagAt(i2), p_96502_1_.getRegisteredName());
            ++i2;
        }
    }

    protected void readDisplayConfig(NBTTagCompound p_96504_1_) {
        int i2 = 0;
        while (i2 < 19) {
            if (p_96504_1_.hasKey("slot_" + i2, 8)) {
                String s2 = p_96504_1_.getString("slot_" + i2);
                ScoreObjective scoreobjective = this.theScoreboard.getObjective(s2);
                this.theScoreboard.setObjectiveInDisplaySlot(i2, scoreobjective);
            }
            ++i2;
        }
    }

    protected void readObjectives(NBTTagList nbt) {
        int i2 = 0;
        while (i2 < nbt.tagCount()) {
            NBTTagCompound nbttagcompound = nbt.getCompoundTagAt(i2);
            IScoreObjectiveCriteria iscoreobjectivecriteria = IScoreObjectiveCriteria.INSTANCES.get(nbttagcompound.getString("CriteriaName"));
            if (iscoreobjectivecriteria != null) {
                String s2 = nbttagcompound.getString("Name");
                if (s2.length() > 16) {
                    s2 = s2.substring(0, 16);
                }
                ScoreObjective scoreobjective = this.theScoreboard.addScoreObjective(s2, iscoreobjectivecriteria);
                scoreobjective.setDisplayName(nbttagcompound.getString("DisplayName"));
                scoreobjective.setRenderType(IScoreObjectiveCriteria.EnumRenderType.func_178795_a(nbttagcompound.getString("RenderType")));
            }
            ++i2;
        }
    }

    protected void readScores(NBTTagList nbt) {
        int i2 = 0;
        while (i2 < nbt.tagCount()) {
            NBTTagCompound nbttagcompound = nbt.getCompoundTagAt(i2);
            ScoreObjective scoreobjective = this.theScoreboard.getObjective(nbttagcompound.getString("Objective"));
            String s2 = nbttagcompound.getString("Name");
            if (s2.length() > 40) {
                s2 = s2.substring(0, 40);
            }
            Score score = this.theScoreboard.getValueFromObjective(s2, scoreobjective);
            score.setScorePoints(nbttagcompound.getInteger("Score"));
            if (nbttagcompound.hasKey("Locked")) {
                score.setLocked(nbttagcompound.getBoolean("Locked"));
            }
            ++i2;
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        if (this.theScoreboard == null) {
            logger.warn("Tried to save scoreboard without having a scoreboard...");
        } else {
            nbt.setTag("Objectives", this.objectivesToNbt());
            nbt.setTag("PlayerScores", this.scoresToNbt());
            nbt.setTag("Teams", this.func_96496_a());
            this.func_96497_d(nbt);
        }
    }

    protected NBTTagList func_96496_a() {
        NBTTagList nbttaglist = new NBTTagList();
        for (ScorePlayerTeam scoreplayerteam : this.theScoreboard.getTeams()) {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setString("Name", scoreplayerteam.getRegisteredName());
            nbttagcompound.setString("DisplayName", scoreplayerteam.getTeamName());
            if (scoreplayerteam.getChatFormat().getColorIndex() >= 0) {
                nbttagcompound.setString("TeamColor", scoreplayerteam.getChatFormat().getFriendlyName());
            }
            nbttagcompound.setString("Prefix", scoreplayerteam.getColorPrefix());
            nbttagcompound.setString("Suffix", scoreplayerteam.getColorSuffix());
            nbttagcompound.setBoolean("AllowFriendlyFire", scoreplayerteam.getAllowFriendlyFire());
            nbttagcompound.setBoolean("SeeFriendlyInvisibles", scoreplayerteam.getSeeFriendlyInvisiblesEnabled());
            nbttagcompound.setString("NameTagVisibility", scoreplayerteam.getNameTagVisibility().internalName);
            nbttagcompound.setString("DeathMessageVisibility", scoreplayerteam.getDeathMessageVisibility().internalName);
            NBTTagList nbttaglist1 = new NBTTagList();
            for (String s2 : scoreplayerteam.getMembershipCollection()) {
                nbttaglist1.appendTag(new NBTTagString(s2));
            }
            nbttagcompound.setTag("Players", nbttaglist1);
            nbttaglist.appendTag(nbttagcompound);
        }
        return nbttaglist;
    }

    protected void func_96497_d(NBTTagCompound p_96497_1_) {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        boolean flag = false;
        int i2 = 0;
        while (i2 < 19) {
            ScoreObjective scoreobjective = this.theScoreboard.getObjectiveInDisplaySlot(i2);
            if (scoreobjective != null) {
                nbttagcompound.setString("slot_" + i2, scoreobjective.getName());
                flag = true;
            }
            ++i2;
        }
        if (flag) {
            p_96497_1_.setTag("DisplaySlots", nbttagcompound);
        }
    }

    protected NBTTagList objectivesToNbt() {
        NBTTagList nbttaglist = new NBTTagList();
        for (ScoreObjective scoreobjective : this.theScoreboard.getScoreObjectives()) {
            if (scoreobjective.getCriteria() == null) continue;
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setString("Name", scoreobjective.getName());
            nbttagcompound.setString("CriteriaName", scoreobjective.getCriteria().getName());
            nbttagcompound.setString("DisplayName", scoreobjective.getDisplayName());
            nbttagcompound.setString("RenderType", scoreobjective.getRenderType().func_178796_a());
            nbttaglist.appendTag(nbttagcompound);
        }
        return nbttaglist;
    }

    protected NBTTagList scoresToNbt() {
        NBTTagList nbttaglist = new NBTTagList();
        for (Score score : this.theScoreboard.getScores()) {
            if (score.getObjective() == null) continue;
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setString("Name", score.getPlayerName());
            nbttagcompound.setString("Objective", score.getObjective().getName());
            nbttagcompound.setInteger("Score", score.getScorePoints());
            nbttagcompound.setBoolean("Locked", score.isLocked());
            nbttaglist.appendTag(nbttagcompound);
        }
        return nbttaglist;
    }
}

