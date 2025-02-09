// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.play.server;

import net.minecraft.network.INetHandler;
import java.util.Iterator;
import java.io.IOException;
import net.minecraft.network.PacketBuffer;
import net.minecraft.scoreboard.ScorePlayerTeam;
import com.google.common.collect.Lists;
import net.minecraft.scoreboard.Team;
import java.util.Collection;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.Packet;

public class SPacketTeams implements Packet<INetHandlerPlayClient>
{
    public String name;
    public String displayName;
    public String prefix;
    public String suffix;
    public String nameTagVisibility;
    public String collisionRule;
    public int color;
    public final Collection<String> players;
    public int action;
    public int friendlyFlags;
    
    public SPacketTeams() {
        this.name = "";
        this.displayName = "";
        this.prefix = "";
        this.suffix = "";
        this.nameTagVisibility = Team.EnumVisible.ALWAYS.internalName;
        this.collisionRule = Team.CollisionRule.ALWAYS.name;
        this.color = -1;
        this.players = (Collection<String>)Lists.newArrayList();
    }
    
    public SPacketTeams(final ScorePlayerTeam teamIn, final int actionIn) {
        this.name = "";
        this.displayName = "";
        this.prefix = "";
        this.suffix = "";
        this.nameTagVisibility = Team.EnumVisible.ALWAYS.internalName;
        this.collisionRule = Team.CollisionRule.ALWAYS.name;
        this.color = -1;
        this.players = (Collection<String>)Lists.newArrayList();
        this.name = teamIn.getRegisteredName();
        this.action = actionIn;
        if (actionIn == 0 || actionIn == 2) {
            this.displayName = teamIn.getTeamName();
            this.prefix = teamIn.getColorPrefix();
            this.suffix = teamIn.getColorSuffix();
            this.friendlyFlags = teamIn.getFriendlyFlags();
            this.nameTagVisibility = teamIn.getNameTagVisibility().internalName;
            this.collisionRule = teamIn.getCollisionRule().name;
            this.color = teamIn.getChatFormat().getColorIndex();
        }
        if (actionIn == 0) {
            this.players.addAll(teamIn.getMembershipCollection());
        }
    }
    
    public SPacketTeams(final ScorePlayerTeam teamIn, final Collection<String> playersIn, final int actionIn) {
        this.name = "";
        this.displayName = "";
        this.prefix = "";
        this.suffix = "";
        this.nameTagVisibility = Team.EnumVisible.ALWAYS.internalName;
        this.collisionRule = Team.CollisionRule.ALWAYS.name;
        this.color = -1;
        this.players = (Collection<String>)Lists.newArrayList();
        if (actionIn != 3 && actionIn != 4) {
            throw new IllegalArgumentException("Method must be join or leave for player constructor");
        }
        if (playersIn != null && !playersIn.isEmpty()) {
            this.action = actionIn;
            this.name = teamIn.getRegisteredName();
            this.players.addAll(playersIn);
            return;
        }
        throw new IllegalArgumentException("Players cannot be null/empty");
    }
    
    @Override
    public void readPacketData(final PacketBuffer buf) throws IOException {
        this.name = buf.readStringFromBuffer(16);
        this.action = buf.readByte();
        if (this.action == 0 || this.action == 2) {
            this.displayName = buf.readStringFromBuffer(32);
            this.prefix = buf.readStringFromBuffer(16);
            this.suffix = buf.readStringFromBuffer(16);
            this.friendlyFlags = buf.readByte();
            this.nameTagVisibility = buf.readStringFromBuffer(32);
            this.collisionRule = buf.readStringFromBuffer(32);
            this.color = buf.readByte();
        }
        if (this.action == 0 || this.action == 3 || this.action == 4) {
            for (int i = buf.readVarIntFromBuffer(), j = 0; j < i; ++j) {
                this.players.add(buf.readStringFromBuffer(40));
            }
        }
    }
    
    @Override
    public void writePacketData(final PacketBuffer buf) throws IOException {
        buf.writeString(this.name);
        buf.writeByte(this.action);
        if (this.action == 0 || this.action == 2) {
            buf.writeString(this.displayName);
            buf.writeString(this.prefix);
            buf.writeString(this.suffix);
            buf.writeByte(this.friendlyFlags);
            buf.writeString(this.nameTagVisibility);
            buf.writeString(this.collisionRule);
            buf.writeByte(this.color);
        }
        if (this.action == 0 || this.action == 3 || this.action == 4) {
            buf.writeVarIntToBuffer(this.players.size());
            for (final String s : this.players) {
                buf.writeString(s);
            }
        }
    }
    
    @Override
    public void processPacket(final INetHandlerPlayClient handler) {
        handler.handleTeams(this);
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getDisplayName() {
        return this.displayName;
    }
    
    public String getPrefix() {
        return this.prefix;
    }
    
    public String getSuffix() {
        return this.suffix;
    }
    
    public Collection<String> getPlayers() {
        return this.players;
    }
    
    public int getAction() {
        return this.action;
    }
    
    public int getFriendlyFlags() {
        return this.friendlyFlags;
    }
    
    public int getColor() {
        return this.color;
    }
    
    public String getNameTagVisibility() {
        return this.nameTagVisibility;
    }
    
    public String getCollisionRule() {
        return this.collisionRule;
    }
}
