// 
// Decompiled by Procyon v0.6.0
// 

package com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections;

import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.connection.UserConnection;
import java.util.Locale;
import java.util.EnumMap;
import com.viaversion.viaversion.libs.fastutil.ints.IntOpenHashSet;
import java.util.Map;
import com.viaversion.viaversion.libs.fastutil.ints.IntSet;
import com.viaversion.viaversion.api.minecraft.BlockFace;

public abstract class AbstractStempConnectionHandler extends ConnectionHandler
{
    private static final BlockFace[] BLOCK_FACES;
    private final IntSet blockId;
    private final int baseStateId;
    private final Map<BlockFace, Integer> stemps;
    
    protected AbstractStempConnectionHandler(final String baseStateId) {
        this.blockId = new IntOpenHashSet();
        this.stemps = new EnumMap<BlockFace, Integer>(BlockFace.class);
        this.baseStateId = ConnectionData.getId(baseStateId);
    }
    
    public ConnectionData.ConnectorInitAction getInitAction(final String blockId, final String toKey) {
        final AbstractStempConnectionHandler handler = this;
        return blockData -> {
            if (blockData.getSavedBlockStateId() == this.baseStateId || blockId.equals(blockData.getMinecraftKey())) {
                if (blockData.getSavedBlockStateId() != this.baseStateId) {
                    handler.blockId.add(blockData.getSavedBlockStateId());
                }
                ConnectionData.connectionHandlerMap.put(blockData.getSavedBlockStateId(), handler);
            }
            if (blockData.getMinecraftKey().equals(toKey)) {
                final String facing = blockData.getValue("facing").toUpperCase(Locale.ROOT);
                this.stemps.put(BlockFace.valueOf(facing), blockData.getSavedBlockStateId());
            }
        };
    }
    
    @Override
    public int connect(final UserConnection user, final Position position, final int blockState) {
        if (blockState != this.baseStateId) {
            return blockState;
        }
        for (final BlockFace blockFace : AbstractStempConnectionHandler.BLOCK_FACES) {
            if (this.blockId.contains(this.getBlockData(user, position.getRelative(blockFace)))) {
                return this.stemps.get(blockFace);
            }
        }
        return this.baseStateId;
    }
    
    static {
        BLOCK_FACES = new BlockFace[] { BlockFace.EAST, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST };
    }
}
