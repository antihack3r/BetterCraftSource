// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.play.server;

import net.minecraft.network.INetHandler;
import java.io.IOException;
import net.minecraft.network.PacketBuffer;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.IScoreCriteria;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.Packet;

public class SPacketScoreboardObjective implements Packet<INetHandlerPlayClient>
{
    private String objectiveName;
    private String objectiveValue;
    private IScoreCriteria.EnumRenderType type;
    private int action;
    
    public SPacketScoreboardObjective() {
    }
    
    public SPacketScoreboardObjective(final ScoreObjective objective, final int actionIn) {
        this.objectiveName = objective.getName();
        this.objectiveValue = objective.getDisplayName();
        this.type = objective.getCriteria().getRenderType();
        this.action = actionIn;
    }
    
    @Override
    public void readPacketData(final PacketBuffer buf) throws IOException {
        this.objectiveName = buf.readStringFromBuffer(16);
        this.action = buf.readByte();
        if (this.action == 0 || this.action == 2) {
            this.objectiveValue = buf.readStringFromBuffer(32);
            this.type = IScoreCriteria.EnumRenderType.getByName(buf.readStringFromBuffer(16));
        }
    }
    
    @Override
    public void writePacketData(final PacketBuffer buf) throws IOException {
        buf.writeString(this.objectiveName);
        buf.writeByte(this.action);
        if (this.action == 0 || this.action == 2) {
            buf.writeString(this.objectiveValue);
            buf.writeString(this.type.getRenderType());
        }
    }
    
    @Override
    public void processPacket(final INetHandlerPlayClient handler) {
        handler.handleScoreboardObjective(this);
    }
    
    public String getObjectiveName() {
        return this.objectiveName;
    }
    
    public String getObjectiveValue() {
        return this.objectiveValue;
    }
    
    public int getAction() {
        return this.action;
    }
    
    public IScoreCriteria.EnumRenderType getRenderType() {
        return this.type;
    }
}
