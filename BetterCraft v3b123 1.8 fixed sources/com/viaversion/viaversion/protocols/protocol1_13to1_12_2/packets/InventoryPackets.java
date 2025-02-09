// 
// Decompiled by Procyon v0.6.0
// 

package com.viaversion.viaversion.protocols.protocol1_13to1_12_2.packets;

import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.data.MappingData;
import java.util.Iterator;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.data.SpawnEggRewriter;
import java.util.Locale;
import com.google.common.primitives.Ints;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.data.BlockIdData;
import com.viaversion.viaversion.libs.opennbt.conversion.ConverterRegistry;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ShortTag;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ChatRewriter;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.NumberTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.IntTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import java.util.List;
import com.google.common.base.Joiner;
import java.util.ArrayList;
import java.nio.charset.StandardCharsets;
import com.viaversion.viaversion.api.minecraft.item.Item;
import java.util.Optional;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.data.SoundSource;
import com.viaversion.viaversion.api.protocol.packet.PacketType;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ClientboundPackets1_13;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.Protocol1_13To1_12_2;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ServerboundPackets1_13;
import com.viaversion.viaversion.protocols.protocol1_12_1to1_12.ClientboundPackets1_12_1;
import com.viaversion.viaversion.rewriter.ItemRewriter;

public class InventoryPackets extends ItemRewriter<ClientboundPackets1_12_1, ServerboundPackets1_13, Protocol1_13To1_12_2>
{
    private static final String NBT_TAG_NAME;
    
    public InventoryPackets(final Protocol1_13To1_12_2 protocol) {
        super(protocol);
    }
    
    public void registerPackets() {
        ((AbstractProtocol<ClientboundPackets1_12_1, CM, SM, SU>)this.protocol).registerClientbound(ClientboundPackets1_12_1.SET_SLOT, new PacketHandlers() {
            public void register() {
                this.map((Type<Object>)Type.UNSIGNED_BYTE);
                this.map((Type<Object>)Type.SHORT);
                this.map(Type.ITEM, Type.FLAT_ITEM);
                this.handler(InventoryPackets.this.itemToClientHandler(Type.FLAT_ITEM));
            }
        });
        ((AbstractProtocol<ClientboundPackets1_12_1, CM, SM, SU>)this.protocol).registerClientbound(ClientboundPackets1_12_1.WINDOW_ITEMS, new PacketHandlers() {
            public void register() {
                this.map((Type<Object>)Type.UNSIGNED_BYTE);
                this.map(Type.ITEM_ARRAY, Type.FLAT_ITEM_ARRAY);
                this.handler(InventoryPackets.this.itemArrayHandler(Type.FLAT_ITEM_ARRAY));
            }
        });
        ((AbstractProtocol<ClientboundPackets1_12_1, CM, SM, SU>)this.protocol).registerClientbound(ClientboundPackets1_12_1.WINDOW_PROPERTY, new PacketHandlers() {
            public void register() {
                this.map((Type<Object>)Type.UNSIGNED_BYTE);
                this.map((Type<Object>)Type.SHORT);
                this.map((Type<Object>)Type.SHORT);
                this.handler(wrapper -> {
                    final short property = wrapper.get((Type<Short>)Type.SHORT, 0);
                    if (property >= 4 && property <= 6) {
                        wrapper.set(Type.SHORT, 1, (short)((Protocol1_13To1_12_2)InventoryPackets.this.protocol).getMappingData().getEnchantmentMappings().getNewId(wrapper.get((Type<Short>)Type.SHORT, 1)));
                    }
                });
            }
        });
        ((AbstractProtocol<ClientboundPackets1_12_1, CM, SM, SU>)this.protocol).registerClientbound(ClientboundPackets1_12_1.PLUGIN_MESSAGE, new PacketHandlers() {
            public void register() {
                this.map(Type.STRING);
                this.handler(wrapper -> {
                    final String channel = wrapper.get(Type.STRING, 0);
                    if (channel.equalsIgnoreCase("MC|StopSound")) {
                        final String originalSource = wrapper.read(Type.STRING);
                        final String originalSound = wrapper.read(Type.STRING);
                        wrapper.clearPacket();
                        wrapper.setPacketType(ClientboundPackets1_13.STOP_SOUND);
                        byte flags = 0;
                        wrapper.write(Type.BYTE, flags);
                        if (!originalSource.isEmpty()) {
                            flags |= 0x1;
                            Optional<SoundSource> finalSource = SoundSource.findBySource(originalSource);
                            if (!finalSource.isPresent()) {
                                if (!Via.getConfig().isSuppressConversionWarnings() || Via.getManager().isDebug()) {
                                    Via.getPlatform().getLogger().info("Could not handle unknown sound source " + originalSource + " falling back to default: master");
                                }
                                finalSource = Optional.of(SoundSource.MASTER);
                            }
                            wrapper.write(Type.VAR_INT, finalSource.get().getId());
                        }
                        if (!originalSound.isEmpty()) {
                            flags |= 0x2;
                            wrapper.write(Type.STRING, originalSound);
                        }
                        wrapper.set(Type.BYTE, 0, flags);
                    }
                    else {
                        String channel2;
                        if (channel.equalsIgnoreCase("MC|TrList")) {
                            channel2 = "minecraft:trader_list";
                            wrapper.passthrough((Type<Object>)Type.INT);
                            for (int size = wrapper.passthrough((Type<Short>)Type.UNSIGNED_BYTE), i = 0; i < size; ++i) {
                                final Item input = wrapper.read(Type.ITEM);
                                InventoryPackets.this.handleItemToClient(input);
                                wrapper.write(Type.FLAT_ITEM, input);
                                final Item output = wrapper.read(Type.ITEM);
                                InventoryPackets.this.handleItemToClient(output);
                                wrapper.write(Type.FLAT_ITEM, output);
                                final boolean secondItem = wrapper.passthrough((Type<Boolean>)Type.BOOLEAN);
                                if (secondItem) {
                                    final Item second = wrapper.read(Type.ITEM);
                                    InventoryPackets.this.handleItemToClient(second);
                                    wrapper.write(Type.FLAT_ITEM, second);
                                }
                                wrapper.passthrough((Type<Object>)Type.BOOLEAN);
                                wrapper.passthrough((Type<Object>)Type.INT);
                                wrapper.passthrough((Type<Object>)Type.INT);
                            }
                        }
                        else {
                            final String old = channel;
                            channel2 = InventoryPackets.getNewPluginChannelId(channel);
                            if (channel2 == null) {
                                if (!Via.getConfig().isSuppressConversionWarnings() || Via.getManager().isDebug()) {
                                    Via.getPlatform().getLogger().warning("Ignoring outgoing plugin message with channel: " + old);
                                }
                                wrapper.cancel();
                                return;
                            }
                            else if (channel2.equals("minecraft:register") || channel2.equals("minecraft:unregister")) {
                                final String[] channels = new String(wrapper.read(Type.REMAINING_BYTES), StandardCharsets.UTF_8).split("\u0000");
                                final ArrayList rewrittenChannels = new ArrayList<String>();
                                final String[] array;
                                int j = 0;
                                for (int length = array.length; j < length; ++j) {
                                    final String s = array[j];
                                    final String rewritten = InventoryPackets.getNewPluginChannelId(s);
                                    if (rewritten != null) {
                                        rewrittenChannels.add(rewritten);
                                    }
                                    else if (!Via.getConfig().isSuppressConversionWarnings() || Via.getManager().isDebug()) {
                                        Via.getPlatform().getLogger().warning("Ignoring plugin channel in outgoing REGISTER: " + s);
                                    }
                                }
                                if (!rewrittenChannels.isEmpty()) {
                                    wrapper.write(Type.REMAINING_BYTES, Joiner.on('\0').join(rewrittenChannels).getBytes(StandardCharsets.UTF_8));
                                }
                                else {
                                    wrapper.cancel();
                                    return;
                                }
                            }
                        }
                        wrapper.set(Type.STRING, 0, channel2);
                    }
                });
            }
        });
        ((AbstractProtocol<ClientboundPackets1_12_1, CM, SM, SU>)this.protocol).registerClientbound(ClientboundPackets1_12_1.ENTITY_EQUIPMENT, new PacketHandlers() {
            public void register() {
                this.map((Type<Object>)Type.VAR_INT);
                this.map((Type<Object>)Type.VAR_INT);
                this.map(Type.ITEM, Type.FLAT_ITEM);
                this.handler(InventoryPackets.this.itemToClientHandler(Type.FLAT_ITEM));
            }
        });
        ((AbstractProtocol<CU, CM, SM, ServerboundPackets1_13>)this.protocol).registerServerbound(ServerboundPackets1_13.CLICK_WINDOW, new PacketHandlers() {
            public void register() {
                this.map((Type<Object>)Type.UNSIGNED_BYTE);
                this.map((Type<Object>)Type.SHORT);
                this.map((Type<Object>)Type.BYTE);
                this.map((Type<Object>)Type.SHORT);
                this.map((Type<Object>)Type.VAR_INT);
                this.map(Type.FLAT_ITEM, Type.ITEM);
                this.handler(InventoryPackets.this.itemToServerHandler(Type.ITEM));
            }
        });
        ((AbstractProtocol<CU, CM, SM, ServerboundPackets1_13>)this.protocol).registerServerbound(ServerboundPackets1_13.PLUGIN_MESSAGE, new PacketHandlers() {
            public void register() {
                this.map(Type.STRING);
                this.handler(wrapper -> {
                    final String old;
                    final String channel = old = wrapper.get(Type.STRING, 0);
                    final String channel2 = InventoryPackets.getOldPluginChannelId(channel);
                    if (channel2 == null) {
                        if (!Via.getConfig().isSuppressConversionWarnings() || Via.getManager().isDebug()) {
                            Via.getPlatform().getLogger().warning("Ignoring incoming plugin message with channel: " + old);
                        }
                        wrapper.cancel();
                    }
                    else {
                        if (channel2.equals("REGISTER") || channel2.equals("UNREGISTER")) {
                            final String[] channels = new String(wrapper.read(Type.REMAINING_BYTES), StandardCharsets.UTF_8).split("\u0000");
                            final ArrayList<String> rewrittenChannels = new ArrayList<String>();
                            final String[] array;
                            int i = 0;
                            for (int length = array.length; i < length; ++i) {
                                final String s = array[i];
                                final String rewritten = InventoryPackets.getOldPluginChannelId(s);
                                if (rewritten != null) {
                                    rewrittenChannels.add(rewritten);
                                }
                                else if (!Via.getConfig().isSuppressConversionWarnings() || Via.getManager().isDebug()) {
                                    Via.getPlatform().getLogger().warning("Ignoring plugin channel in incoming REGISTER: " + s);
                                }
                            }
                            wrapper.write(Type.REMAINING_BYTES, Joiner.on('\0').join(rewrittenChannels).getBytes(StandardCharsets.UTF_8));
                        }
                        wrapper.set(Type.STRING, 0, channel2);
                    }
                });
            }
        });
        ((AbstractProtocol<CU, CM, SM, ServerboundPackets1_13>)this.protocol).registerServerbound(ServerboundPackets1_13.CREATIVE_INVENTORY_ACTION, new PacketHandlers() {
            public void register() {
                this.map((Type<Object>)Type.SHORT);
                this.map(Type.FLAT_ITEM, Type.ITEM);
                this.handler(InventoryPackets.this.itemToServerHandler(Type.ITEM));
            }
        });
    }
    
    @Override
    public Item handleItemToClient(final Item item) {
        if (item == null) {
            return null;
        }
        CompoundTag tag = item.tag();
        final int originalId = item.identifier() << 16 | (item.data() & 0xFFFF);
        int rawId = item.identifier() << 4 | (item.data() & 0xF);
        if (isDamageable(item.identifier())) {
            if (tag == null) {
                item.setTag(tag = new CompoundTag());
            }
            tag.put("Damage", new IntTag(item.data()));
        }
        if (item.identifier() == 358) {
            if (tag == null) {
                item.setTag(tag = new CompoundTag());
            }
            tag.put("map", new IntTag(item.data()));
        }
        if (tag != null) {
            final boolean banner = item.identifier() == 425;
            if ((banner || item.identifier() == 442) && tag.get("BlockEntityTag") instanceof CompoundTag) {
                final CompoundTag blockEntityTag = tag.get("BlockEntityTag");
                if (blockEntityTag.get("Base") instanceof IntTag) {
                    final IntTag base = blockEntityTag.get("Base");
                    if (banner) {
                        rawId = 6800 + base.asInt();
                    }
                    base.setValue(15 - base.asInt());
                }
                if (blockEntityTag.get("Patterns") instanceof ListTag) {
                    for (final Tag pattern : blockEntityTag.get("Patterns")) {
                        if (pattern instanceof CompoundTag) {
                            final Tag c = ((CompoundTag)pattern).get("Color");
                            if (!(c instanceof NumberTag)) {
                                continue;
                            }
                            ((CompoundTag)pattern).put("Color", new IntTag(15 - ((NumberTag)c).asInt()));
                        }
                    }
                }
            }
            if (tag.get("display") instanceof CompoundTag) {
                final CompoundTag display = tag.get("display");
                if (display.get("Name") instanceof StringTag) {
                    final StringTag name = display.get("Name");
                    display.put(InventoryPackets.NBT_TAG_NAME + "|Name", new StringTag(name.getValue()));
                    name.setValue(ChatRewriter.legacyTextToJsonString(name.getValue(), true));
                }
            }
            if (tag.get("ench") instanceof ListTag) {
                final ListTag ench = tag.get("ench");
                final ListTag enchantments = new ListTag(CompoundTag.class);
                for (final Tag enchEntry : ench) {
                    final NumberTag idTag;
                    if (enchEntry instanceof CompoundTag && (idTag = ((CompoundTag)enchEntry).get("id")) != null) {
                        final CompoundTag enchantmentEntry = new CompoundTag();
                        final short oldId = idTag.asShort();
                        String newId = Protocol1_13To1_12_2.MAPPINGS.getOldEnchantmentsIds().get(oldId);
                        if (newId == null) {
                            newId = "viaversion:legacy/" + oldId;
                        }
                        enchantmentEntry.put("id", new StringTag(newId));
                        enchantmentEntry.put("lvl", new ShortTag(((CompoundTag)enchEntry).get("lvl").asShort()));
                        enchantments.add(enchantmentEntry);
                    }
                }
                tag.remove("ench");
                tag.put("Enchantments", enchantments);
            }
            if (tag.get("StoredEnchantments") instanceof ListTag) {
                final ListTag storedEnch = tag.get("StoredEnchantments");
                final ListTag newStoredEnch = new ListTag(CompoundTag.class);
                for (final Tag enchEntry : storedEnch) {
                    if (enchEntry instanceof CompoundTag) {
                        final CompoundTag enchantmentEntry2 = new CompoundTag();
                        final short oldId2 = ((CompoundTag)enchEntry).get("id").asShort();
                        String newId2 = Protocol1_13To1_12_2.MAPPINGS.getOldEnchantmentsIds().get(oldId2);
                        if (newId2 == null) {
                            newId2 = "viaversion:legacy/" + oldId2;
                        }
                        enchantmentEntry2.put("id", new StringTag(newId2));
                        enchantmentEntry2.put("lvl", new ShortTag(((CompoundTag)enchEntry).get("lvl").asShort()));
                        newStoredEnch.add(enchantmentEntry2);
                    }
                }
                tag.remove("StoredEnchantments");
                tag.put("StoredEnchantments", newStoredEnch);
            }
            if (tag.get("CanPlaceOn") instanceof ListTag) {
                final ListTag old = tag.get("CanPlaceOn");
                final ListTag newCanPlaceOn = new ListTag(StringTag.class);
                tag.put(InventoryPackets.NBT_TAG_NAME + "|CanPlaceOn", ConverterRegistry.convertToTag(ConverterRegistry.convertToValue(old)));
                for (final Tag oldTag : old) {
                    final Object value = oldTag.getValue();
                    String oldId3 = value.toString().replace("minecraft:", "");
                    final String numberConverted = BlockIdData.numberIdToString.get(Ints.tryParse(oldId3));
                    if (numberConverted != null) {
                        oldId3 = numberConverted;
                    }
                    final String[] newValues = BlockIdData.blockIdMapping.get(oldId3.toLowerCase(Locale.ROOT));
                    if (newValues != null) {
                        for (final String newValue : newValues) {
                            newCanPlaceOn.add(new StringTag(newValue));
                        }
                    }
                    else {
                        newCanPlaceOn.add(new StringTag(oldId3.toLowerCase(Locale.ROOT)));
                    }
                }
                tag.put("CanPlaceOn", newCanPlaceOn);
            }
            if (tag.get("CanDestroy") instanceof ListTag) {
                final ListTag old = tag.get("CanDestroy");
                final ListTag newCanDestroy = new ListTag(StringTag.class);
                tag.put(InventoryPackets.NBT_TAG_NAME + "|CanDestroy", ConverterRegistry.convertToTag(ConverterRegistry.convertToValue(old)));
                for (final Tag oldTag : old) {
                    final Object value = oldTag.getValue();
                    String oldId3 = value.toString().replace("minecraft:", "");
                    final String numberConverted = BlockIdData.numberIdToString.get(Ints.tryParse(oldId3));
                    if (numberConverted != null) {
                        oldId3 = numberConverted;
                    }
                    final String[] newValues = BlockIdData.blockIdMapping.get(oldId3.toLowerCase(Locale.ROOT));
                    if (newValues != null) {
                        for (final String newValue : newValues) {
                            newCanDestroy.add(new StringTag(newValue));
                        }
                    }
                    else {
                        newCanDestroy.add(new StringTag(oldId3.toLowerCase(Locale.ROOT)));
                    }
                }
                tag.put("CanDestroy", newCanDestroy);
            }
            if (item.identifier() == 383) {
                if (tag.get("EntityTag") instanceof CompoundTag) {
                    final CompoundTag entityTag = tag.get("EntityTag");
                    if (entityTag.get("id") instanceof StringTag) {
                        final StringTag identifier = entityTag.get("id");
                        rawId = SpawnEggRewriter.getSpawnEggId(identifier.getValue());
                        if (rawId == -1) {
                            rawId = 25100288;
                        }
                        else {
                            entityTag.remove("id");
                            if (entityTag.isEmpty()) {
                                tag.remove("EntityTag");
                            }
                        }
                    }
                    else {
                        rawId = 25100288;
                    }
                }
                else {
                    rawId = 25100288;
                }
            }
            if (tag.isEmpty()) {
                item.setTag(tag = null);
            }
        }
        if (Protocol1_13To1_12_2.MAPPINGS.getItemMappings().getNewId(rawId) == -1) {
            if (!isDamageable(item.identifier()) && item.identifier() != 358) {
                if (tag == null) {
                    item.setTag(tag = new CompoundTag());
                }
                tag.put(InventoryPackets.NBT_TAG_NAME, new IntTag(originalId));
            }
            if (item.identifier() == 31 && item.data() == 0) {
                rawId = 512;
            }
            else if (Protocol1_13To1_12_2.MAPPINGS.getItemMappings().getNewId(rawId & 0xFFFFFFF0) != -1) {
                rawId &= 0xFFFFFFF0;
            }
            else {
                if (!Via.getConfig().isSuppressConversionWarnings() || Via.getManager().isDebug()) {
                    Via.getPlatform().getLogger().warning("Failed to get 1.13 item for " + item.identifier());
                }
                rawId = 16;
            }
        }
        item.setIdentifier(Protocol1_13To1_12_2.MAPPINGS.getItemMappings().getNewId(rawId));
        item.setData((short)0);
        return item;
    }
    
    public static String getNewPluginChannelId(final String old) {
        switch (old) {
            case "MC|TrList": {
                return "minecraft:trader_list";
            }
            case "MC|Brand": {
                return "minecraft:brand";
            }
            case "MC|BOpen": {
                return "minecraft:book_open";
            }
            case "MC|DebugPath": {
                return "minecraft:debug/paths";
            }
            case "MC|DebugNeighborsUpdate": {
                return "minecraft:debug/neighbors_update";
            }
            case "REGISTER": {
                return "minecraft:register";
            }
            case "UNREGISTER": {
                return "minecraft:unregister";
            }
            case "BungeeCord": {
                return "bungeecord:main";
            }
            case "bungeecord:main": {
                return null;
            }
            default: {
                final String mappedChannel = Protocol1_13To1_12_2.MAPPINGS.getChannelMappings().get(old);
                if (mappedChannel != null) {
                    return mappedChannel;
                }
                return MappingData.validateNewChannel(old);
            }
        }
    }
    
    @Override
    public Item handleItemToServer(final Item item) {
        if (item == null) {
            return null;
        }
        Integer rawId = null;
        boolean gotRawIdFromTag = false;
        CompoundTag tag = item.tag();
        if (tag != null && tag.get(InventoryPackets.NBT_TAG_NAME) instanceof IntTag) {
            rawId = tag.get(InventoryPackets.NBT_TAG_NAME).asInt();
            tag.remove(InventoryPackets.NBT_TAG_NAME);
            gotRawIdFromTag = true;
        }
        if (rawId == null) {
            final int oldId = Protocol1_13To1_12_2.MAPPINGS.getItemMappings().inverse().getNewId(item.identifier());
            if (oldId != -1) {
                final Optional<String> eggEntityId = SpawnEggRewriter.getEntityId(oldId);
                if (eggEntityId.isPresent()) {
                    rawId = 25100288;
                    if (tag == null) {
                        item.setTag(tag = new CompoundTag());
                    }
                    if (!tag.contains("EntityTag")) {
                        final CompoundTag entityTag = new CompoundTag();
                        entityTag.put("id", new StringTag(eggEntityId.get()));
                        tag.put("EntityTag", entityTag);
                    }
                }
                else {
                    rawId = (oldId >> 4 << 16 | (oldId & 0xF));
                }
            }
        }
        if (rawId == null) {
            if (!Via.getConfig().isSuppressConversionWarnings() || Via.getManager().isDebug()) {
                Via.getPlatform().getLogger().warning("Failed to get 1.12 item for " + item.identifier());
            }
            rawId = 65536;
        }
        item.setIdentifier((short)(rawId >> 16));
        item.setData((short)(rawId & 0xFFFF));
        if (tag != null) {
            if (isDamageable(item.identifier()) && tag.get("Damage") instanceof IntTag) {
                if (!gotRawIdFromTag) {
                    item.setData((short)(int)tag.get("Damage").getValue());
                }
                tag.remove("Damage");
            }
            if (item.identifier() == 358 && tag.get("map") instanceof IntTag) {
                if (!gotRawIdFromTag) {
                    item.setData((short)(int)tag.get("map").getValue());
                }
                tag.remove("map");
            }
            if ((item.identifier() == 442 || item.identifier() == 425) && tag.get("BlockEntityTag") instanceof CompoundTag) {
                final CompoundTag blockEntityTag = tag.get("BlockEntityTag");
                if (blockEntityTag.get("Base") instanceof IntTag) {
                    final IntTag base = blockEntityTag.get("Base");
                    base.setValue(15 - base.asInt());
                }
                if (blockEntityTag.get("Patterns") instanceof ListTag) {
                    for (final Tag pattern : blockEntityTag.get("Patterns")) {
                        if (pattern instanceof CompoundTag) {
                            final IntTag c = ((CompoundTag)pattern).get("Color");
                            c.setValue(15 - c.asInt());
                        }
                    }
                }
            }
            if (tag.get("display") instanceof CompoundTag) {
                final CompoundTag display = tag.get("display");
                if (display.get("Name") instanceof StringTag) {
                    final StringTag name = display.get("Name");
                    final StringTag via = display.remove(InventoryPackets.NBT_TAG_NAME + "|Name");
                    name.setValue((via != null) ? via.getValue() : ChatRewriter.jsonToLegacyText(name.getValue()));
                }
            }
            if (tag.get("Enchantments") instanceof ListTag) {
                final ListTag enchantments = tag.get("Enchantments");
                final ListTag ench = new ListTag(CompoundTag.class);
                for (final Tag enchantmentEntry : enchantments) {
                    if (enchantmentEntry instanceof CompoundTag) {
                        final CompoundTag enchEntry = new CompoundTag();
                        final String newId = (String)((CompoundTag)enchantmentEntry).get("id").getValue();
                        Short oldId2 = Protocol1_13To1_12_2.MAPPINGS.getOldEnchantmentsIds().inverse().get(newId);
                        if (oldId2 == null && newId.startsWith("viaversion:legacy/")) {
                            oldId2 = Short.valueOf(newId.substring(18));
                        }
                        if (oldId2 == null) {
                            continue;
                        }
                        enchEntry.put("id", new ShortTag(oldId2));
                        enchEntry.put("lvl", new ShortTag(((CompoundTag)enchantmentEntry).get("lvl").asShort()));
                        ench.add(enchEntry);
                    }
                }
                tag.remove("Enchantments");
                tag.put("ench", ench);
            }
            if (tag.get("StoredEnchantments") instanceof ListTag) {
                final ListTag storedEnch = tag.get("StoredEnchantments");
                final ListTag newStoredEnch = new ListTag(CompoundTag.class);
                for (final Tag enchantmentEntry : storedEnch) {
                    if (enchantmentEntry instanceof CompoundTag) {
                        final CompoundTag enchEntry = new CompoundTag();
                        final String newId = (String)((CompoundTag)enchantmentEntry).get("id").getValue();
                        Short oldId2 = Protocol1_13To1_12_2.MAPPINGS.getOldEnchantmentsIds().inverse().get(newId);
                        if (oldId2 == null && newId.startsWith("viaversion:legacy/")) {
                            oldId2 = Short.valueOf(newId.substring(18));
                        }
                        if (oldId2 == null) {
                            continue;
                        }
                        enchEntry.put("id", new ShortTag(oldId2));
                        enchEntry.put("lvl", new ShortTag(((CompoundTag)enchantmentEntry).get("lvl").asShort()));
                        newStoredEnch.add(enchEntry);
                    }
                }
                tag.remove("StoredEnchantments");
                tag.put("StoredEnchantments", newStoredEnch);
            }
            if (tag.get(InventoryPackets.NBT_TAG_NAME + "|CanPlaceOn") instanceof ListTag) {
                tag.put("CanPlaceOn", ConverterRegistry.convertToTag(ConverterRegistry.convertToValue(tag.get(InventoryPackets.NBT_TAG_NAME + "|CanPlaceOn"))));
                tag.remove(InventoryPackets.NBT_TAG_NAME + "|CanPlaceOn");
            }
            else if (tag.get("CanPlaceOn") instanceof ListTag) {
                final ListTag old = tag.get("CanPlaceOn");
                final ListTag newCanPlaceOn = new ListTag(StringTag.class);
                for (final Tag oldTag : old) {
                    final Object value = oldTag.getValue();
                    final String[] newValues = BlockIdData.fallbackReverseMapping.get((value instanceof String) ? ((String)value).replace("minecraft:", "") : null);
                    if (newValues != null) {
                        for (final String newValue : newValues) {
                            newCanPlaceOn.add(new StringTag(newValue));
                        }
                    }
                    else {
                        newCanPlaceOn.add(oldTag);
                    }
                }
                tag.put("CanPlaceOn", newCanPlaceOn);
            }
            if (tag.get(InventoryPackets.NBT_TAG_NAME + "|CanDestroy") instanceof ListTag) {
                tag.put("CanDestroy", ConverterRegistry.convertToTag(ConverterRegistry.convertToValue(tag.get(InventoryPackets.NBT_TAG_NAME + "|CanDestroy"))));
                tag.remove(InventoryPackets.NBT_TAG_NAME + "|CanDestroy");
            }
            else if (tag.get("CanDestroy") instanceof ListTag) {
                final ListTag old = tag.get("CanDestroy");
                final ListTag newCanDestroy = new ListTag(StringTag.class);
                for (final Tag oldTag : old) {
                    final Object value = oldTag.getValue();
                    final String[] newValues = BlockIdData.fallbackReverseMapping.get((value instanceof String) ? ((String)value).replace("minecraft:", "") : null);
                    if (newValues != null) {
                        for (final String newValue : newValues) {
                            newCanDestroy.add(new StringTag(newValue));
                        }
                    }
                    else {
                        newCanDestroy.add(oldTag);
                    }
                }
                tag.put("CanDestroy", newCanDestroy);
            }
        }
        return item;
    }
    
    public static String getOldPluginChannelId(String newId) {
        newId = MappingData.validateNewChannel(newId);
        if (newId == null) {
            return null;
        }
        final String s = newId;
        switch (s) {
            case "minecraft:trader_list": {
                return "MC|TrList";
            }
            case "minecraft:book_open": {
                return "MC|BOpen";
            }
            case "minecraft:debug/paths": {
                return "MC|DebugPath";
            }
            case "minecraft:debug/neighbors_update": {
                return "MC|DebugNeighborsUpdate";
            }
            case "minecraft:register": {
                return "REGISTER";
            }
            case "minecraft:unregister": {
                return "UNREGISTER";
            }
            case "minecraft:brand": {
                return "MC|Brand";
            }
            case "bungeecord:main": {
                return "BungeeCord";
            }
            default: {
                final String mappedChannel = Protocol1_13To1_12_2.MAPPINGS.getChannelMappings().inverse().get(newId);
                if (mappedChannel != null) {
                    return mappedChannel;
                }
                return (newId.length() > 20) ? newId.substring(0, 20) : newId;
            }
        }
    }
    
    public static boolean isDamageable(final int id) {
        return (id >= 256 && id <= 259) || id == 261 || (id >= 267 && id <= 279) || (id >= 283 && id <= 286) || (id >= 290 && id <= 294) || (id >= 298 && id <= 317) || id == 346 || id == 359 || id == 398 || id == 442 || id == 443;
    }
    
    static {
        NBT_TAG_NAME = "ViaVersion|" + Protocol1_13To1_12_2.class.getSimpleName();
    }
}
