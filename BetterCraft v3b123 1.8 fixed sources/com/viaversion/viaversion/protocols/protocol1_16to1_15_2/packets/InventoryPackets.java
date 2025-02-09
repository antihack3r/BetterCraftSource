// 
// Decompiled by Procyon v0.6.0
// 

package com.viaversion.viaversion.protocols.protocol1_16to1_15_2.packets;

import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.util.Key;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.LongTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.NumberTag;
import java.util.Iterator;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.IntArrayTag;
import com.viaversion.viaversion.api.type.types.UUIDIntArrayType;
import java.util.UUID;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.rewriter.RecipeRewriter;
import com.viaversion.viaversion.api.type.types.ShortType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.storage.InventoryTracker1_16;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.protocol.packet.PacketType;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.ClientboundPackets1_16;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.Protocol1_16To1_15_2;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.ServerboundPackets1_16;
import com.viaversion.viaversion.protocols.protocol1_15to1_14_4.ClientboundPackets1_15;
import com.viaversion.viaversion.rewriter.ItemRewriter;

public class InventoryPackets extends ItemRewriter<ClientboundPackets1_15, ServerboundPackets1_16, Protocol1_16To1_15_2>
{
    public InventoryPackets(final Protocol1_16To1_15_2 protocol) {
        super(protocol);
    }
    
    public void registerPackets() {
        final PacketHandler cursorRemapper = wrapper -> {
            final PacketWrapper clearPacket = wrapper.create(ClientboundPackets1_16.SET_SLOT);
            clearPacket.write(Type.UNSIGNED_BYTE, (Short)(-1));
            clearPacket.write(Type.SHORT, (Short)(-1));
            clearPacket.write(Type.FLAT_VAR_INT_ITEM, null);
            clearPacket.send(Protocol1_16To1_15_2.class);
            return;
        };
        ((AbstractProtocol<ClientboundPackets1_15, CM, SM, SU>)this.protocol).registerClientbound(ClientboundPackets1_15.OPEN_WINDOW, new PacketHandlers() {
            public void register() {
                this.map((Type<Object>)Type.VAR_INT);
                this.map((Type<Object>)Type.VAR_INT);
                this.map(Type.COMPONENT);
                this.handler(cursorRemapper);
                this.handler(wrapper -> {
                    final InventoryTracker1_16 inventoryTracker = wrapper.user().get(InventoryTracker1_16.class);
                    int windowType = wrapper.get((Type<Integer>)Type.VAR_INT, 1);
                    if (windowType >= 20) {
                        wrapper.set(Type.VAR_INT, 1, ++windowType);
                    }
                    inventoryTracker.setInventoryOpen(true);
                });
            }
        });
        ((AbstractProtocol<ClientboundPackets1_15, CM, SM, SU>)this.protocol).registerClientbound(ClientboundPackets1_15.CLOSE_WINDOW, new PacketHandlers() {
            public void register() {
                this.handler(cursorRemapper);
                this.handler(wrapper -> {
                    final InventoryTracker1_16 inventoryTracker = wrapper.user().get(InventoryTracker1_16.class);
                    inventoryTracker.setInventoryOpen(false);
                });
            }
        });
        ((AbstractProtocol<ClientboundPackets1_15, CM, SM, SU>)this.protocol).registerClientbound(ClientboundPackets1_15.WINDOW_PROPERTY, new PacketHandlers() {
            public void register() {
                this.map((Type<Object>)Type.UNSIGNED_BYTE);
                this.map((Type<Object>)Type.SHORT);
                this.map((Type<Object>)Type.SHORT);
                this.handler(wrapper -> {
                    final short property = wrapper.get((Type<Short>)Type.SHORT, 0);
                    if (property >= 4 && property <= 6) {
                        final short enchantmentId = wrapper.get((Type<Short>)Type.SHORT, 1);
                        if (enchantmentId >= 11) {
                            final ShortType short1 = Type.SHORT;
                            final short enchantmentId2 = (short)(enchantmentId + 1);
                            final int n;
                            wrapper.set(short1, n, enchantmentId2);
                        }
                    }
                });
            }
        });
        ((ItemRewriter<ClientboundPackets1_15, S, T>)this).registerSetCooldown(ClientboundPackets1_15.COOLDOWN);
        ((ItemRewriter<ClientboundPackets1_15, S, T>)this).registerWindowItems(ClientboundPackets1_15.WINDOW_ITEMS, Type.FLAT_VAR_INT_ITEM_ARRAY);
        ((ItemRewriter<ClientboundPackets1_15, S, T>)this).registerTradeList(ClientboundPackets1_15.TRADE_LIST);
        ((ItemRewriter<ClientboundPackets1_15, S, T>)this).registerSetSlot(ClientboundPackets1_15.SET_SLOT, Type.FLAT_VAR_INT_ITEM);
        ((ItemRewriter<ClientboundPackets1_15, S, T>)this).registerAdvancements(ClientboundPackets1_15.ADVANCEMENTS, Type.FLAT_VAR_INT_ITEM);
        ((AbstractProtocol<ClientboundPackets1_15, CM, SM, SU>)this.protocol).registerClientbound(ClientboundPackets1_15.ENTITY_EQUIPMENT, new PacketHandlers() {
            public void register() {
                this.map((Type<Object>)Type.VAR_INT);
                this.handler(wrapper -> {
                    final int slot = wrapper.read((Type<Integer>)Type.VAR_INT);
                    wrapper.write(Type.BYTE, (byte)slot);
                    InventoryPackets.this.handleItemToClient(wrapper.passthrough(Type.FLAT_VAR_INT_ITEM));
                });
            }
        });
        new RecipeRewriter<ClientboundPackets1_15>(this.protocol).register(ClientboundPackets1_15.DECLARE_RECIPES);
        ((ItemRewriter<C, ServerboundPackets1_16, T>)this).registerClickWindow(ServerboundPackets1_16.CLICK_WINDOW, Type.FLAT_VAR_INT_ITEM);
        ((ItemRewriter<C, ServerboundPackets1_16, T>)this).registerCreativeInvAction(ServerboundPackets1_16.CREATIVE_INVENTORY_ACTION, Type.FLAT_VAR_INT_ITEM);
        ((AbstractProtocol<CU, CM, SM, ServerboundPackets1_16>)this.protocol).registerServerbound(ServerboundPackets1_16.CLOSE_WINDOW, wrapper -> {
            final InventoryTracker1_16 inventoryTracker = wrapper.user().get(InventoryTracker1_16.class);
            inventoryTracker.setInventoryOpen(false);
            return;
        });
        ((AbstractProtocol<CU, CM, SM, ServerboundPackets1_16>)this.protocol).registerServerbound(ServerboundPackets1_16.EDIT_BOOK, wrapper -> this.handleItemToServer(wrapper.passthrough(Type.FLAT_VAR_INT_ITEM)));
        ((ItemRewriter<ClientboundPackets1_15, S, T>)this).registerSpawnParticle(ClientboundPackets1_15.SPAWN_PARTICLE, Type.FLAT_VAR_INT_ITEM, Type.DOUBLE);
    }
    
    @Override
    public Item handleItemToClient(final Item item) {
        if (item == null) {
            return null;
        }
        final CompoundTag tag = item.tag();
        if (item.identifier() == 771 && tag != null) {
            final Tag ownerTag = tag.get("SkullOwner");
            if (ownerTag instanceof CompoundTag) {
                final CompoundTag ownerCompundTag = (CompoundTag)ownerTag;
                final Tag idTag = ownerCompundTag.get("Id");
                if (idTag instanceof StringTag) {
                    final UUID id = UUID.fromString((String)idTag.getValue());
                    ownerCompundTag.put("Id", new IntArrayTag(UUIDIntArrayType.uuidToIntArray(id)));
                }
            }
        }
        else if (item.identifier() == 759 && tag != null) {
            final Tag pages = tag.get("pages");
            if (pages instanceof ListTag) {
                for (final Tag pageTag : (ListTag)pages) {
                    if (!(pageTag instanceof StringTag)) {
                        continue;
                    }
                    final StringTag page = (StringTag)pageTag;
                    page.setValue(((Protocol1_16To1_15_2)this.protocol).getComponentRewriter().processText(page.getValue()).toString());
                }
            }
        }
        oldToNewAttributes(item);
        item.setIdentifier(Protocol1_16To1_15_2.MAPPINGS.getNewItemId(item.identifier()));
        return item;
    }
    
    @Override
    public Item handleItemToServer(final Item item) {
        if (item == null) {
            return null;
        }
        item.setIdentifier(Protocol1_16To1_15_2.MAPPINGS.getOldItemId(item.identifier()));
        if (item.identifier() == 771 && item.tag() != null) {
            final CompoundTag tag = item.tag();
            final Tag ownerTag = tag.get("SkullOwner");
            if (ownerTag instanceof CompoundTag) {
                final CompoundTag ownerCompundTag = (CompoundTag)ownerTag;
                final Tag idTag = ownerCompundTag.get("Id");
                if (idTag instanceof IntArrayTag) {
                    final UUID id = UUIDIntArrayType.uuidFromIntArray((int[])idTag.getValue());
                    ownerCompundTag.put("Id", new StringTag(id.toString()));
                }
            }
        }
        newToOldAttributes(item);
        return item;
    }
    
    public static void oldToNewAttributes(final Item item) {
        if (item.tag() == null) {
            return;
        }
        final ListTag attributes = item.tag().get("AttributeModifiers");
        if (attributes == null) {
            return;
        }
        for (final Tag tag : attributes) {
            final CompoundTag attribute = (CompoundTag)tag;
            rewriteAttributeName(attribute, "AttributeName", false);
            rewriteAttributeName(attribute, "Name", false);
            final Tag leastTag = attribute.get("UUIDLeast");
            if (leastTag != null) {
                final Tag mostTag = attribute.get("UUIDMost");
                final int[] uuidIntArray = UUIDIntArrayType.bitsToIntArray(((NumberTag)leastTag).asLong(), ((NumberTag)mostTag).asLong());
                attribute.put("UUID", new IntArrayTag(uuidIntArray));
            }
        }
    }
    
    public static void newToOldAttributes(final Item item) {
        if (item.tag() == null) {
            return;
        }
        final ListTag attributes = item.tag().get("AttributeModifiers");
        if (attributes == null) {
            return;
        }
        for (final Tag tag : attributes) {
            final CompoundTag attribute = (CompoundTag)tag;
            rewriteAttributeName(attribute, "AttributeName", true);
            rewriteAttributeName(attribute, "Name", true);
            final IntArrayTag uuidTag = attribute.get("UUID");
            if (uuidTag != null && uuidTag.getValue().length == 4) {
                final UUID uuid = UUIDIntArrayType.uuidFromIntArray(uuidTag.getValue());
                attribute.put("UUIDLeast", new LongTag(uuid.getLeastSignificantBits()));
                attribute.put("UUIDMost", new LongTag(uuid.getMostSignificantBits()));
            }
        }
    }
    
    public static void rewriteAttributeName(final CompoundTag compoundTag, final String entryName, final boolean inverse) {
        final StringTag attributeNameTag = compoundTag.get(entryName);
        if (attributeNameTag == null) {
            return;
        }
        String attributeName = attributeNameTag.getValue();
        if (inverse) {
            attributeName = Key.namespaced(attributeName);
        }
        final String mappedAttribute = (inverse ? Protocol1_16To1_15_2.MAPPINGS.getAttributeMappings().inverse() : Protocol1_16To1_15_2.MAPPINGS.getAttributeMappings()).get(attributeName);
        if (mappedAttribute == null) {
            return;
        }
        attributeNameTag.setValue(mappedAttribute);
    }
}
