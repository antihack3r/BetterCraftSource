/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.EntityNotFoundException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class CommandResultStats {
    private static final int NUM_RESULT_TYPES = Type.values().length;
    private static final String[] STRING_RESULT_TYPES = new String[NUM_RESULT_TYPES];
    private String[] entitiesID = STRING_RESULT_TYPES;
    private String[] objectives = STRING_RESULT_TYPES;

    public void setCommandStatScore(final ICommandSender sender, Type resultTypeIn, int scorePoint) {
        String s2 = this.entitiesID[resultTypeIn.getTypeID()];
        if (s2 != null) {
            Scoreboard scoreboard;
            ScoreObjective scoreobjective;
            String s1;
            ICommandSender icommandsender = new ICommandSender(){

                @Override
                public String getName() {
                    return sender.getName();
                }

                @Override
                public IChatComponent getDisplayName() {
                    return sender.getDisplayName();
                }

                @Override
                public void addChatMessage(IChatComponent component) {
                    sender.addChatMessage(component);
                }

                @Override
                public boolean canCommandSenderUseCommand(int permLevel, String commandName) {
                    return true;
                }

                @Override
                public BlockPos getPosition() {
                    return sender.getPosition();
                }

                @Override
                public Vec3 getPositionVector() {
                    return sender.getPositionVector();
                }

                @Override
                public World getEntityWorld() {
                    return sender.getEntityWorld();
                }

                @Override
                public Entity getCommandSenderEntity() {
                    return sender.getCommandSenderEntity();
                }

                @Override
                public boolean sendCommandFeedback() {
                    return sender.sendCommandFeedback();
                }

                @Override
                public void setCommandStat(Type type, int amount) {
                    sender.setCommandStat(type, amount);
                }
            };
            try {
                s1 = CommandBase.getEntityName(icommandsender, s2);
            }
            catch (EntityNotFoundException var11) {
                return;
            }
            String s22 = this.objectives[resultTypeIn.getTypeID()];
            if (s22 != null && (scoreobjective = (scoreboard = sender.getEntityWorld().getScoreboard()).getObjective(s22)) != null && scoreboard.entityHasObjective(s1, scoreobjective)) {
                Score score = scoreboard.getValueFromObjective(s1, scoreobjective);
                score.setScorePoints(scorePoint);
            }
        }
    }

    public void readStatsFromNBT(NBTTagCompound tagcompound) {
        if (tagcompound.hasKey("CommandStats", 10)) {
            NBTTagCompound nbttagcompound = tagcompound.getCompoundTag("CommandStats");
            Type[] typeArray = Type.values();
            int n2 = typeArray.length;
            int n3 = 0;
            while (n3 < n2) {
                Type commandresultstats$type = typeArray[n3];
                String s2 = String.valueOf(commandresultstats$type.getTypeName()) + "Name";
                String s1 = String.valueOf(commandresultstats$type.getTypeName()) + "Objective";
                if (nbttagcompound.hasKey(s2, 8) && nbttagcompound.hasKey(s1, 8)) {
                    String s22 = nbttagcompound.getString(s2);
                    String s3 = nbttagcompound.getString(s1);
                    CommandResultStats.setScoreBoardStat(this, commandresultstats$type, s22, s3);
                }
                ++n3;
            }
        }
    }

    public void writeStatsToNBT(NBTTagCompound tagcompound) {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        Type[] typeArray = Type.values();
        int n2 = typeArray.length;
        int n3 = 0;
        while (n3 < n2) {
            Type commandresultstats$type = typeArray[n3];
            String s2 = this.entitiesID[commandresultstats$type.getTypeID()];
            String s1 = this.objectives[commandresultstats$type.getTypeID()];
            if (s2 != null && s1 != null) {
                nbttagcompound.setString(String.valueOf(commandresultstats$type.getTypeName()) + "Name", s2);
                nbttagcompound.setString(String.valueOf(commandresultstats$type.getTypeName()) + "Objective", s1);
            }
            ++n3;
        }
        if (!nbttagcompound.hasNoTags()) {
            tagcompound.setTag("CommandStats", nbttagcompound);
        }
    }

    public static void setScoreBoardStat(CommandResultStats stats, Type resultType, String entityID, String objectiveName) {
        if (entityID != null && entityID.length() != 0 && objectiveName != null && objectiveName.length() != 0) {
            if (stats.entitiesID == STRING_RESULT_TYPES || stats.objectives == STRING_RESULT_TYPES) {
                stats.entitiesID = new String[NUM_RESULT_TYPES];
                stats.objectives = new String[NUM_RESULT_TYPES];
            }
            stats.entitiesID[resultType.getTypeID()] = entityID;
            stats.objectives[resultType.getTypeID()] = objectiveName;
        } else {
            CommandResultStats.removeScoreBoardStat(stats, resultType);
        }
    }

    private static void removeScoreBoardStat(CommandResultStats resultStatsIn, Type resultTypeIn) {
        if (resultStatsIn.entitiesID != STRING_RESULT_TYPES && resultStatsIn.objectives != STRING_RESULT_TYPES) {
            resultStatsIn.entitiesID[resultTypeIn.getTypeID()] = null;
            resultStatsIn.objectives[resultTypeIn.getTypeID()] = null;
            boolean flag = true;
            Type[] typeArray = Type.values();
            int n2 = typeArray.length;
            int n3 = 0;
            while (n3 < n2) {
                Type commandresultstats$type = typeArray[n3];
                if (resultStatsIn.entitiesID[commandresultstats$type.getTypeID()] != null && resultStatsIn.objectives[commandresultstats$type.getTypeID()] != null) {
                    flag = false;
                    break;
                }
                ++n3;
            }
            if (flag) {
                resultStatsIn.entitiesID = STRING_RESULT_TYPES;
                resultStatsIn.objectives = STRING_RESULT_TYPES;
            }
        }
    }

    public void addAllStats(CommandResultStats resultStatsIn) {
        Type[] typeArray = Type.values();
        int n2 = typeArray.length;
        int n3 = 0;
        while (n3 < n2) {
            Type commandresultstats$type = typeArray[n3];
            CommandResultStats.setScoreBoardStat(this, commandresultstats$type, resultStatsIn.entitiesID[commandresultstats$type.getTypeID()], resultStatsIn.objectives[commandresultstats$type.getTypeID()]);
            ++n3;
        }
    }

    public static enum Type {
        SUCCESS_COUNT(0, "SuccessCount"),
        AFFECTED_BLOCKS(1, "AffectedBlocks"),
        AFFECTED_ENTITIES(2, "AffectedEntities"),
        AFFECTED_ITEMS(3, "AffectedItems"),
        QUERY_RESULT(4, "QueryResult");

        final int typeID;
        final String typeName;

        private Type(int id2, String name) {
            this.typeID = id2;
            this.typeName = name;
        }

        public int getTypeID() {
            return this.typeID;
        }

        public String getTypeName() {
            return this.typeName;
        }

        public static String[] getTypeNames() {
            String[] astring = new String[Type.values().length];
            int i2 = 0;
            Type[] typeArray = Type.values();
            int n2 = typeArray.length;
            int n3 = 0;
            while (n3 < n2) {
                Type commandresultstats$type = typeArray[n3];
                astring[i2++] = commandresultstats$type.getTypeName();
                ++n3;
            }
            return astring;
        }

        public static Type getTypeByName(String name) {
            Type[] typeArray = Type.values();
            int n2 = typeArray.length;
            int n3 = 0;
            while (n3 < n2) {
                Type commandresultstats$type = typeArray[n3];
                if (commandresultstats$type.getTypeName().equals(name)) {
                    return commandresultstats$type;
                }
                ++n3;
            }
            return null;
        }
    }
}

