/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.BlockFace;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.libs.fastutil.ints.Int2IntMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2IntOpenHashMap;
import com.viaversion.viaversion.libs.fastutil.ints.IntOpenHashSet;
import com.viaversion.viaversion.libs.fastutil.ints.IntSet;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.BlockData;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.ConnectionData;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.ConnectionHandler;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.WrappedBlockData;

public class RedstoneConnectionHandler
extends ConnectionHandler {
    private static final IntSet REDSTONE = new IntOpenHashSet();
    private static final Int2IntMap CONNECTED_BLOCK_STATES = new Int2IntOpenHashMap(1296);
    private static final Int2IntMap POWER_MAPPINGS = new Int2IntOpenHashMap(1296);
    private static final int BLOCK_CONNECTION_TYPE_ID = BlockData.connectionTypeId("redstone");

    static ConnectionData.ConnectorInitAction init() {
        RedstoneConnectionHandler connectionHandler = new RedstoneConnectionHandler();
        String redstoneKey = "minecraft:redstone_wire";
        return blockData -> {
            if (!"minecraft:redstone_wire".equals(blockData.getMinecraftKey())) {
                return;
            }
            REDSTONE.add(blockData.getSavedBlockStateId());
            ConnectionData.connectionHandlerMap.put(blockData.getSavedBlockStateId(), (ConnectionHandler)connectionHandler);
            CONNECTED_BLOCK_STATES.put(RedstoneConnectionHandler.getStates(blockData), blockData.getSavedBlockStateId());
            POWER_MAPPINGS.put(blockData.getSavedBlockStateId(), Integer.parseInt(blockData.getValue("power")));
        };
    }

    private static short getStates(WrappedBlockData data) {
        short b2 = 0;
        b2 = (short)(b2 | RedstoneConnectionHandler.getState(data.getValue("east")));
        b2 = (short)(b2 | RedstoneConnectionHandler.getState(data.getValue("north")) << 2);
        b2 = (short)(b2 | RedstoneConnectionHandler.getState(data.getValue("south")) << 4);
        b2 = (short)(b2 | RedstoneConnectionHandler.getState(data.getValue("west")) << 6);
        b2 = (short)(b2 | Integer.parseInt(data.getValue("power")) << 8);
        return b2;
    }

    private static int getState(String value) {
        switch (value) {
            case "none": {
                return 0;
            }
            case "side": {
                return 1;
            }
            case "up": {
                return 2;
            }
        }
        return 0;
    }

    @Override
    public int connect(UserConnection user, Position position, int blockState) {
        int b2 = 0;
        b2 = (short)(b2 | this.connects(user, position, BlockFace.EAST));
        b2 = (short)(b2 | this.connects(user, position, BlockFace.NORTH) << 2);
        b2 = (short)(b2 | this.connects(user, position, BlockFace.SOUTH) << 4);
        b2 = (short)(b2 | this.connects(user, position, BlockFace.WEST) << 6);
        b2 = (short)(b2 | POWER_MAPPINGS.get(blockState) << 8);
        return CONNECTED_BLOCK_STATES.getOrDefault(b2, blockState);
    }

    private int connects(UserConnection user, Position position, BlockFace side) {
        Position relative = position.getRelative(side);
        int blockState = this.getBlockData(user, relative);
        if (this.connects(side, blockState)) {
            return 1;
        }
        int up2 = this.getBlockData(user, relative.getRelative(BlockFace.TOP));
        if (REDSTONE.contains(up2) && !ConnectionData.OCCLUDING_STATES.contains(this.getBlockData(user, position.getRelative(BlockFace.TOP)))) {
            return 2;
        }
        int down = this.getBlockData(user, relative.getRelative(BlockFace.BOTTOM));
        if (REDSTONE.contains(down) && !ConnectionData.OCCLUDING_STATES.contains(this.getBlockData(user, relative))) {
            return 1;
        }
        return 0;
    }

    private boolean connects(BlockFace side, int blockState) {
        BlockData blockData = (BlockData)ConnectionData.blockConnectionData.get(blockState);
        return blockData != null && blockData.connectsTo(BLOCK_CONNECTION_TYPE_ID, side.opposite(), false);
    }
}

