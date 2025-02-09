/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.BlockFace;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectOpenHashMap;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.ConnectionData;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.ConnectionHandler;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;

public class DoorConnectionHandler
extends ConnectionHandler {
    private static final Int2ObjectMap<DoorData> DOOR_DATA_MAP = new Int2ObjectOpenHashMap<DoorData>();
    private static final Map<Short, Integer> CONNECTED_STATES = new HashMap<Short, Integer>();

    static ConnectionData.ConnectorInitAction init() {
        LinkedList<String> baseDoors = new LinkedList<String>();
        baseDoors.add("minecraft:oak_door");
        baseDoors.add("minecraft:birch_door");
        baseDoors.add("minecraft:jungle_door");
        baseDoors.add("minecraft:dark_oak_door");
        baseDoors.add("minecraft:acacia_door");
        baseDoors.add("minecraft:spruce_door");
        baseDoors.add("minecraft:iron_door");
        DoorConnectionHandler connectionHandler = new DoorConnectionHandler();
        return blockData -> {
            int type = baseDoors.indexOf(blockData.getMinecraftKey());
            if (type == -1) {
                return;
            }
            int id2 = blockData.getSavedBlockStateId();
            DoorData doorData = new DoorData(blockData.getValue("half").equals("lower"), blockData.getValue("hinge").equals("right"), blockData.getValue("powered").equals("true"), blockData.getValue("open").equals("true"), BlockFace.valueOf(blockData.getValue("facing").toUpperCase(Locale.ROOT)), type);
            DOOR_DATA_MAP.put(id2, doorData);
            CONNECTED_STATES.put(DoorConnectionHandler.getStates(doorData), id2);
            ConnectionData.connectionHandlerMap.put(id2, (ConnectionHandler)connectionHandler);
        };
    }

    private static short getStates(DoorData doorData) {
        short s2 = 0;
        if (doorData.isLower()) {
            s2 = (short)(s2 | '\u0001');
        }
        if (doorData.isOpen()) {
            s2 = (short)(s2 | 2);
        }
        if (doorData.isPowered()) {
            s2 = (short)(s2 | 4);
        }
        if (doorData.isRightHinge()) {
            s2 = (short)(s2 | 8);
        }
        s2 = (short)(s2 | doorData.getFacing().ordinal() << 4);
        s2 = (short)(s2 | (doorData.getType() & 7) << 6);
        return s2;
    }

    @Override
    public int connect(UserConnection user, Position position, int blockState) {
        DoorData doorData = (DoorData)DOOR_DATA_MAP.get(blockState);
        if (doorData == null) {
            return blockState;
        }
        short s2 = 0;
        s2 = (short)(s2 | (doorData.getType() & 7) << 6);
        if (doorData.isLower()) {
            DoorData upperHalf = (DoorData)DOOR_DATA_MAP.get(this.getBlockData(user, position.getRelative(BlockFace.TOP)));
            if (upperHalf == null) {
                return blockState;
            }
            s2 = (short)(s2 | 1);
            if (doorData.isOpen()) {
                s2 = (short)(s2 | 2);
            }
            if (upperHalf.isPowered()) {
                s2 = (short)(s2 | 4);
            }
            if (upperHalf.isRightHinge()) {
                s2 = (short)(s2 | 8);
            }
            s2 = (short)(s2 | doorData.getFacing().ordinal() << 4);
        } else {
            DoorData lowerHalf = (DoorData)DOOR_DATA_MAP.get(this.getBlockData(user, position.getRelative(BlockFace.BOTTOM)));
            if (lowerHalf == null) {
                return blockState;
            }
            if (lowerHalf.isOpen()) {
                s2 = (short)(s2 | 2);
            }
            if (doorData.isPowered()) {
                s2 = (short)(s2 | 4);
            }
            if (doorData.isRightHinge()) {
                s2 = (short)(s2 | 8);
            }
            s2 = (short)(s2 | lowerHalf.getFacing().ordinal() << 4);
        }
        Integer newBlockState = CONNECTED_STATES.get(s2);
        return newBlockState == null ? blockState : newBlockState;
    }

    private static final class DoorData {
        private final boolean lower;
        private final boolean rightHinge;
        private final boolean powered;
        private final boolean open;
        private final BlockFace facing;
        private final int type;

        private DoorData(boolean lower, boolean rightHinge, boolean powered, boolean open, BlockFace facing, int type) {
            this.lower = lower;
            this.rightHinge = rightHinge;
            this.powered = powered;
            this.open = open;
            this.facing = facing;
            this.type = type;
        }

        public boolean isLower() {
            return this.lower;
        }

        public boolean isRightHinge() {
            return this.rightHinge;
        }

        public boolean isPowered() {
            return this.powered;
        }

        public boolean isOpen() {
            return this.open;
        }

        public BlockFace getFacing() {
            return this.facing;
        }

        public int getType() {
            return this.type;
        }
    }
}

