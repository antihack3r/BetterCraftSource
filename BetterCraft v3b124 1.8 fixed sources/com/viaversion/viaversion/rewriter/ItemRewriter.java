/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.rewriter;

import com.viaversion.viaversion.api.data.Mappings;
import com.viaversion.viaversion.api.data.ParticleMappings;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.rewriter.RewriterBase;
import com.viaversion.viaversion.api.type.Type;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ItemRewriter<C extends ClientboundPacketType, S extends ServerboundPacketType, T extends Protocol<C, ?, ?, S>>
extends RewriterBase<T>
implements com.viaversion.viaversion.api.rewriter.ItemRewriter<T> {
    private final Type<Item> itemType;
    private final Type<Item[]> itemArrayType;

    @Deprecated
    protected ItemRewriter(T protocol) {
        this(protocol, Type.ITEM1_13_2, Type.ITEM1_13_2_ARRAY);
    }

    public ItemRewriter(T protocol, Type<Item> itemType, Type<Item[]> itemArrayType) {
        super(protocol);
        this.itemType = itemType;
        this.itemArrayType = itemArrayType;
    }

    @Override
    public @Nullable Item handleItemToClient(@Nullable Item item) {
        if (item == null) {
            return null;
        }
        if (this.protocol.getMappingData() != null && this.protocol.getMappingData().getItemMappings() != null) {
            item.setIdentifier(this.protocol.getMappingData().getNewItemId(item.identifier()));
        }
        return item;
    }

    @Override
    public @Nullable Item handleItemToServer(@Nullable Item item) {
        if (item == null) {
            return null;
        }
        if (this.protocol.getMappingData() != null && this.protocol.getMappingData().getItemMappings() != null) {
            item.setIdentifier(this.protocol.getMappingData().getOldItemId(item.identifier()));
        }
        return item;
    }

    public void registerWindowItems(C packetType, final Type<Item[]> type) {
        this.protocol.registerClientbound(packetType, new PacketHandlers(){

            @Override
            public void register() {
                this.map(Type.UNSIGNED_BYTE);
                this.map(type);
                this.handler(ItemRewriter.this.itemArrayToClientHandler(type));
            }
        });
    }

    public void registerWindowItems1_17_1(C packetType) {
        this.protocol.registerClientbound(packetType, new PacketHandlers(){

            @Override
            public void register() {
                this.map(Type.UNSIGNED_BYTE);
                this.map(Type.VAR_INT);
                this.handler(wrapper -> {
                    Item[] items;
                    for (Item item : items = (Item[])wrapper.passthrough(ItemRewriter.this.itemArrayType)) {
                        ItemRewriter.this.handleItemToClient(item);
                    }
                    ItemRewriter.this.handleItemToClient((Item)wrapper.passthrough(ItemRewriter.this.itemType));
                });
            }
        });
    }

    public void registerOpenWindow(C packetType) {
        this.protocol.registerClientbound(packetType, new PacketHandlers(){

            @Override
            public void register() {
                this.map(Type.VAR_INT);
                this.handler(wrapper -> {
                    int windowType = wrapper.read(Type.VAR_INT);
                    int mappedId = ItemRewriter.this.protocol.getMappingData().getMenuMappings().getNewId(windowType);
                    if (mappedId == -1) {
                        wrapper.cancel();
                        return;
                    }
                    wrapper.write(Type.VAR_INT, mappedId);
                });
            }
        });
    }

    public void registerSetSlot(C packetType, final Type<Item> type) {
        this.protocol.registerClientbound(packetType, new PacketHandlers(){

            @Override
            public void register() {
                this.map(Type.UNSIGNED_BYTE);
                this.map(Type.SHORT);
                this.map(type);
                this.handler(ItemRewriter.this.itemToClientHandler(type));
            }
        });
    }

    public void registerSetSlot1_17_1(C packetType) {
        this.protocol.registerClientbound(packetType, new PacketHandlers(){

            @Override
            public void register() {
                this.map(Type.UNSIGNED_BYTE);
                this.map(Type.VAR_INT);
                this.map(Type.SHORT);
                this.map(ItemRewriter.this.itemType);
                this.handler(ItemRewriter.this.itemToClientHandler(ItemRewriter.this.itemType));
            }
        });
    }

    public void registerEntityEquipment(C packetType, final Type<Item> type) {
        this.protocol.registerClientbound(packetType, new PacketHandlers(){

            @Override
            public void register() {
                this.map(Type.VAR_INT);
                this.map(Type.VAR_INT);
                this.map(type);
                this.handler(ItemRewriter.this.itemToClientHandler(type));
            }
        });
    }

    public void registerEntityEquipmentArray(C packetType) {
        this.protocol.registerClientbound(packetType, new PacketHandlers(){

            @Override
            public void register() {
                this.map(Type.VAR_INT);
                this.handler(wrapper -> {
                    byte slot;
                    do {
                        slot = wrapper.passthrough(Type.BYTE);
                        ItemRewriter.this.handleItemToClient((Item)wrapper.passthrough(ItemRewriter.this.itemType));
                    } while ((slot & 0xFFFFFF80) != 0);
                });
            }
        });
    }

    public void registerCreativeInvAction(S packetType) {
        this.registerCreativeInvAction(packetType, this.itemType);
    }

    public void registerCreativeInvAction(S packetType, final Type<Item> type) {
        this.protocol.registerServerbound(packetType, new PacketHandlers(){

            @Override
            public void register() {
                this.map(Type.SHORT);
                this.map(type);
                this.handler(ItemRewriter.this.itemToServerHandler(type));
            }
        });
    }

    public void registerClickWindow(S packetType, final Type<Item> type) {
        this.protocol.registerServerbound(packetType, new PacketHandlers(){

            @Override
            public void register() {
                this.map(Type.UNSIGNED_BYTE);
                this.map(Type.SHORT);
                this.map(Type.BYTE);
                this.map(Type.SHORT);
                this.map(Type.VAR_INT);
                this.map(type);
                this.handler(ItemRewriter.this.itemToServerHandler(type));
            }
        });
    }

    public void registerClickWindow1_17_1(S packetType) {
        this.protocol.registerServerbound(packetType, new PacketHandlers(){

            @Override
            public void register() {
                this.map(Type.UNSIGNED_BYTE);
                this.map(Type.VAR_INT);
                this.map(Type.SHORT);
                this.map(Type.BYTE);
                this.map(Type.VAR_INT);
                this.handler(wrapper -> {
                    int length = wrapper.passthrough(Type.VAR_INT);
                    for (int i2 = 0; i2 < length; ++i2) {
                        wrapper.passthrough(Type.SHORT);
                        ItemRewriter.this.handleItemToServer((Item)wrapper.passthrough(ItemRewriter.this.itemType));
                    }
                    ItemRewriter.this.handleItemToServer((Item)wrapper.passthrough(ItemRewriter.this.itemType));
                });
            }
        });
    }

    public void registerSetCooldown(C packetType) {
        this.protocol.registerClientbound(packetType, wrapper -> {
            int itemId = wrapper.read(Type.VAR_INT);
            wrapper.write(Type.VAR_INT, this.protocol.getMappingData().getNewItemId(itemId));
        });
    }

    public void registerTradeList(C packetType) {
        this.protocol.registerClientbound(packetType, wrapper -> {
            wrapper.passthrough(Type.VAR_INT);
            int size = wrapper.passthrough(Type.UNSIGNED_BYTE).shortValue();
            for (int i2 = 0; i2 < size; ++i2) {
                this.handleItemToClient(wrapper.passthrough(this.itemType));
                this.handleItemToClient(wrapper.passthrough(this.itemType));
                if (wrapper.passthrough(Type.BOOLEAN).booleanValue()) {
                    this.handleItemToClient(wrapper.passthrough(this.itemType));
                }
                wrapper.passthrough(Type.BOOLEAN);
                wrapper.passthrough(Type.INT);
                wrapper.passthrough(Type.INT);
                wrapper.passthrough(Type.INT);
                wrapper.passthrough(Type.INT);
                wrapper.passthrough(Type.FLOAT);
                wrapper.passthrough(Type.INT);
            }
        });
    }

    public void registerTradeList1_19(C packetType) {
        this.protocol.registerClientbound(packetType, wrapper -> {
            wrapper.passthrough(Type.VAR_INT);
            int size = wrapper.passthrough(Type.VAR_INT);
            for (int i2 = 0; i2 < size; ++i2) {
                this.handleItemToClient(wrapper.passthrough(this.itemType));
                this.handleItemToClient(wrapper.passthrough(this.itemType));
                this.handleItemToClient(wrapper.passthrough(this.itemType));
                wrapper.passthrough(Type.BOOLEAN);
                wrapper.passthrough(Type.INT);
                wrapper.passthrough(Type.INT);
                wrapper.passthrough(Type.INT);
                wrapper.passthrough(Type.INT);
                wrapper.passthrough(Type.FLOAT);
                wrapper.passthrough(Type.INT);
            }
        });
    }

    public void registerAdvancements(C packetType, Type<Item> type) {
        this.protocol.registerClientbound(packetType, wrapper -> {
            wrapper.passthrough(Type.BOOLEAN);
            int size = wrapper.passthrough(Type.VAR_INT);
            for (int i2 = 0; i2 < size; ++i2) {
                wrapper.passthrough(Type.STRING);
                if (wrapper.passthrough(Type.BOOLEAN).booleanValue()) {
                    wrapper.passthrough(Type.STRING);
                }
                if (wrapper.passthrough(Type.BOOLEAN).booleanValue()) {
                    wrapper.passthrough(Type.COMPONENT);
                    wrapper.passthrough(Type.COMPONENT);
                    this.handleItemToClient((Item)wrapper.passthrough(type));
                    wrapper.passthrough(Type.VAR_INT);
                    int flags = wrapper.passthrough(Type.INT);
                    if ((flags & 1) != 0) {
                        wrapper.passthrough(Type.STRING);
                    }
                    wrapper.passthrough(Type.FLOAT);
                    wrapper.passthrough(Type.FLOAT);
                }
                wrapper.passthrough(Type.STRING_ARRAY);
                int arrayLength = wrapper.passthrough(Type.VAR_INT);
                for (int array = 0; array < arrayLength; ++array) {
                    wrapper.passthrough(Type.STRING_ARRAY);
                }
            }
        });
    }

    public void registerAdvancements1_20_2(C packetType) {
        this.registerAdvancements1_20_2(packetType, Type.COMPONENT);
    }

    public void registerAdvancements1_20_3(C packetType) {
        this.registerAdvancements1_20_2(packetType, Type.TAG);
    }

    private void registerAdvancements1_20_2(C packetType, Type<?> componentType) {
        this.protocol.registerClientbound(packetType, wrapper -> {
            wrapper.passthrough(Type.BOOLEAN);
            int size = wrapper.passthrough(Type.VAR_INT);
            for (int i2 = 0; i2 < size; ++i2) {
                wrapper.passthrough(Type.STRING);
                if (wrapper.passthrough(Type.BOOLEAN).booleanValue()) {
                    wrapper.passthrough(Type.STRING);
                }
                if (wrapper.passthrough(Type.BOOLEAN).booleanValue()) {
                    wrapper.passthrough(componentType);
                    wrapper.passthrough(componentType);
                    this.handleItemToClient(wrapper.passthrough(this.itemType));
                    wrapper.passthrough(Type.VAR_INT);
                    int flags = wrapper.passthrough(Type.INT);
                    if ((flags & 1) != 0) {
                        wrapper.passthrough(Type.STRING);
                    }
                    wrapper.passthrough(Type.FLOAT);
                    wrapper.passthrough(Type.FLOAT);
                }
                int requirements = wrapper.passthrough(Type.VAR_INT);
                for (int array = 0; array < requirements; ++array) {
                    wrapper.passthrough(Type.STRING_ARRAY);
                }
                wrapper.passthrough(Type.BOOLEAN);
            }
        });
    }

    public void registerWindowPropertyEnchantmentHandler(C packetType) {
        this.protocol.registerClientbound(packetType, new PacketHandlers(){

            @Override
            public void register() {
                this.map(Type.UNSIGNED_BYTE);
                this.handler(wrapper -> {
                    Mappings mappings = ItemRewriter.this.protocol.getMappingData().getEnchantmentMappings();
                    if (mappings == null) {
                        return;
                    }
                    short property = wrapper.passthrough(Type.SHORT);
                    if (property >= 4 && property <= 6) {
                        short enchantmentId = (short)mappings.getNewId(wrapper.read(Type.SHORT).shortValue());
                        wrapper.write(Type.SHORT, enchantmentId);
                    }
                });
            }
        });
    }

    public void registerSpawnParticle(C packetType, Type<Item> itemType, final Type<?> coordType) {
        this.protocol.registerClientbound(packetType, new PacketHandlers(){

            @Override
            public void register() {
                this.map(Type.INT);
                this.map(Type.BOOLEAN);
                this.map(coordType);
                this.map(coordType);
                this.map(coordType);
                this.map(Type.FLOAT);
                this.map(Type.FLOAT);
                this.map(Type.FLOAT);
                this.map(Type.FLOAT);
                this.map(Type.INT);
                this.handler(ItemRewriter.this.getSpawnParticleHandler());
            }
        });
    }

    public void registerSpawnParticle1_19(C packetType) {
        this.protocol.registerClientbound(packetType, new PacketHandlers(){

            @Override
            public void register() {
                this.map(Type.VAR_INT);
                this.map(Type.BOOLEAN);
                this.map(Type.DOUBLE);
                this.map(Type.DOUBLE);
                this.map(Type.DOUBLE);
                this.map(Type.FLOAT);
                this.map(Type.FLOAT);
                this.map(Type.FLOAT);
                this.map(Type.FLOAT);
                this.map(Type.INT);
                this.handler(ItemRewriter.this.getSpawnParticleHandler(Type.VAR_INT));
            }
        });
    }

    public PacketHandler getSpawnParticleHandler() {
        return this.getSpawnParticleHandler(Type.INT);
    }

    public PacketHandler getSpawnParticleHandler(Type<Integer> idType) {
        return wrapper -> {
            int id2 = (Integer)wrapper.get(idType, 0);
            if (id2 == -1) {
                return;
            }
            ParticleMappings mappings = this.protocol.getMappingData().getParticleMappings();
            if (mappings.isBlockParticle(id2)) {
                int data = wrapper.read(Type.VAR_INT);
                wrapper.write(Type.VAR_INT, this.protocol.getMappingData().getNewBlockStateId(data));
            } else if (mappings.isItemParticle(id2)) {
                this.handleItemToClient(wrapper.passthrough(this.itemType));
            }
            int mappedId = this.protocol.getMappingData().getNewParticleId(id2);
            if (mappedId != id2) {
                wrapper.set(idType, 0, mappedId);
            }
        };
    }

    public PacketHandler itemArrayToClientHandler(Type<Item[]> type) {
        return wrapper -> {
            Item[] items;
            for (Item item : items = (Item[])wrapper.get(type, 0)) {
                this.handleItemToClient(item);
            }
        };
    }

    public PacketHandler itemToClientHandler(Type<Item> type) {
        return wrapper -> this.handleItemToClient((Item)wrapper.get(type, 0));
    }

    public PacketHandler itemToServerHandler(Type<Item> type) {
        return wrapper -> this.handleItemToServer((Item)wrapper.get(type, 0));
    }
}

