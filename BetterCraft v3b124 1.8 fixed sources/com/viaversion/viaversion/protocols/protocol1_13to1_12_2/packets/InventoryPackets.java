/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.protocols.protocol1_13to1_12_2.packets;

import com.google.common.base.Joiner;
import com.google.common.primitives.Ints;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.opennbt.conversion.ConverterRegistry;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.IntTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.NumberTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ShortTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import com.viaversion.viaversion.protocols.protocol1_12_1to1_12.ClientboundPackets1_12_1;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ChatRewriter;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ClientboundPackets1_13;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.Protocol1_13To1_12_2;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ServerboundPackets1_13;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.data.BlockIdData;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.data.MappingData;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.data.SoundSource;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.data.SpawnEggRewriter;
import com.viaversion.viaversion.rewriter.ItemRewriter;
import com.viaversion.viaversion.util.Key;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Optional;

public class InventoryPackets
extends ItemRewriter<ClientboundPackets1_12_1, ServerboundPackets1_13, Protocol1_13To1_12_2> {
    private static final String NBT_TAG_NAME = "ViaVersion|" + Protocol1_13To1_12_2.class.getSimpleName();

    public InventoryPackets(Protocol1_13To1_12_2 protocol) {
        super(protocol, null, null);
    }

    @Override
    public void registerPackets() {
        ((Protocol1_13To1_12_2)this.protocol).registerClientbound(ClientboundPackets1_12_1.SET_SLOT, new PacketHandlers(){

            @Override
            public void register() {
                this.map(Type.UNSIGNED_BYTE);
                this.map(Type.SHORT);
                this.map(Type.ITEM1_8, Type.ITEM1_13);
                this.handler(InventoryPackets.this.itemToClientHandler(Type.ITEM1_13));
            }
        });
        ((Protocol1_13To1_12_2)this.protocol).registerClientbound(ClientboundPackets1_12_1.WINDOW_ITEMS, new PacketHandlers(){

            @Override
            public void register() {
                this.map(Type.UNSIGNED_BYTE);
                this.map(Type.ITEM1_8_SHORT_ARRAY, Type.ITEM1_13_SHORT_ARRAY);
                this.handler(InventoryPackets.this.itemArrayToClientHandler(Type.ITEM1_13_SHORT_ARRAY));
            }
        });
        ((Protocol1_13To1_12_2)this.protocol).registerClientbound(ClientboundPackets1_12_1.WINDOW_PROPERTY, new PacketHandlers(){

            @Override
            public void register() {
                this.map(Type.UNSIGNED_BYTE);
                this.map(Type.SHORT);
                this.map(Type.SHORT);
                this.handler(wrapper -> {
                    short property = wrapper.get(Type.SHORT, 0);
                    if (property >= 4 && property <= 6) {
                        wrapper.set(Type.SHORT, 1, (short)((Protocol1_13To1_12_2)InventoryPackets.this.protocol).getMappingData().getEnchantmentMappings().getNewId(wrapper.get(Type.SHORT, 1).shortValue()));
                    }
                });
            }
        });
        ((Protocol1_13To1_12_2)this.protocol).registerClientbound(ClientboundPackets1_12_1.PLUGIN_MESSAGE, new PacketHandlers(){

            @Override
            public void register() {
                this.map(Type.STRING);
                this.handler(wrapper -> {
                    String channel = wrapper.get(Type.STRING, 0);
                    if (channel.equalsIgnoreCase("MC|StopSound")) {
                        String originalSource = wrapper.read(Type.STRING);
                        String originalSound = wrapper.read(Type.STRING);
                        wrapper.clearPacket();
                        wrapper.setPacketType(ClientboundPackets1_13.STOP_SOUND);
                        byte flags = 0;
                        wrapper.write(Type.BYTE, flags);
                        if (!originalSource.isEmpty()) {
                            flags = (byte)(flags | 1);
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
                            flags = (byte)(flags | 2);
                            wrapper.write(Type.STRING, originalSound);
                        }
                        wrapper.set(Type.BYTE, 0, flags);
                        return;
                    }
                    if (channel.equalsIgnoreCase("MC|TrList")) {
                        channel = "minecraft:trader_list";
                        wrapper.passthrough(Type.INT);
                        int size = wrapper.passthrough(Type.UNSIGNED_BYTE).shortValue();
                        for (int i2 = 0; i2 < size; ++i2) {
                            Item input = wrapper.read(Type.ITEM1_8);
                            InventoryPackets.this.handleItemToClient(input);
                            wrapper.write(Type.ITEM1_13, input);
                            Item output = wrapper.read(Type.ITEM1_8);
                            InventoryPackets.this.handleItemToClient(output);
                            wrapper.write(Type.ITEM1_13, output);
                            boolean secondItem = wrapper.passthrough(Type.BOOLEAN);
                            if (secondItem) {
                                Item second = wrapper.read(Type.ITEM1_8);
                                InventoryPackets.this.handleItemToClient(second);
                                wrapper.write(Type.ITEM1_13, second);
                            }
                            wrapper.passthrough(Type.BOOLEAN);
                            wrapper.passthrough(Type.INT);
                            wrapper.passthrough(Type.INT);
                        }
                    } else {
                        String old = channel;
                        if ((channel = InventoryPackets.getNewPluginChannelId(channel)) == null) {
                            if (!Via.getConfig().isSuppressConversionWarnings() || Via.getManager().isDebug()) {
                                Via.getPlatform().getLogger().warning("Ignoring outgoing plugin message with channel: " + old);
                            }
                            wrapper.cancel();
                            return;
                        }
                        if (channel.equals("minecraft:register") || channel.equals("minecraft:unregister")) {
                            String[] channels = new String(wrapper.read(Type.REMAINING_BYTES), StandardCharsets.UTF_8).split("\u0000");
                            ArrayList<String> rewrittenChannels = new ArrayList<String>();
                            for (String s2 : channels) {
                                String rewritten = InventoryPackets.getNewPluginChannelId(s2);
                                if (rewritten != null) {
                                    rewrittenChannels.add(rewritten);
                                    continue;
                                }
                                if (Via.getConfig().isSuppressConversionWarnings() && !Via.getManager().isDebug()) continue;
                                Via.getPlatform().getLogger().warning("Ignoring plugin channel in outgoing REGISTER: " + s2);
                            }
                            if (!rewrittenChannels.isEmpty()) {
                                wrapper.write(Type.REMAINING_BYTES, Joiner.on('\u0000').join(rewrittenChannels).getBytes(StandardCharsets.UTF_8));
                            } else {
                                wrapper.cancel();
                                return;
                            }
                        }
                    }
                    wrapper.set(Type.STRING, 0, channel);
                });
            }
        });
        ((Protocol1_13To1_12_2)this.protocol).registerClientbound(ClientboundPackets1_12_1.ENTITY_EQUIPMENT, new PacketHandlers(){

            @Override
            public void register() {
                this.map(Type.VAR_INT);
                this.map(Type.VAR_INT);
                this.map(Type.ITEM1_8, Type.ITEM1_13);
                this.handler(InventoryPackets.this.itemToClientHandler(Type.ITEM1_13));
            }
        });
        ((Protocol1_13To1_12_2)this.protocol).registerServerbound(ServerboundPackets1_13.CLICK_WINDOW, new PacketHandlers(){

            @Override
            public void register() {
                this.map(Type.UNSIGNED_BYTE);
                this.map(Type.SHORT);
                this.map(Type.BYTE);
                this.map(Type.SHORT);
                this.map(Type.VAR_INT);
                this.map(Type.ITEM1_13, Type.ITEM1_8);
                this.handler(InventoryPackets.this.itemToServerHandler(Type.ITEM1_8));
            }
        });
        ((Protocol1_13To1_12_2)this.protocol).registerServerbound(ServerboundPackets1_13.PLUGIN_MESSAGE, new PacketHandlers(){

            @Override
            public void register() {
                this.map(Type.STRING);
                this.handler(wrapper -> {
                    String channel;
                    String old = channel = wrapper.get(Type.STRING, 0);
                    if ((channel = InventoryPackets.getOldPluginChannelId(channel)) == null) {
                        if (!Via.getConfig().isSuppressConversionWarnings() || Via.getManager().isDebug()) {
                            Via.getPlatform().getLogger().warning("Ignoring incoming plugin message with channel: " + old);
                        }
                        wrapper.cancel();
                        return;
                    }
                    if (channel.equals("REGISTER") || channel.equals("UNREGISTER")) {
                        String[] channels = new String(wrapper.read(Type.REMAINING_BYTES), StandardCharsets.UTF_8).split("\u0000");
                        ArrayList<String> rewrittenChannels = new ArrayList<String>();
                        for (String s2 : channels) {
                            String rewritten = InventoryPackets.getOldPluginChannelId(s2);
                            if (rewritten != null) {
                                rewrittenChannels.add(rewritten);
                                continue;
                            }
                            if (Via.getConfig().isSuppressConversionWarnings() && !Via.getManager().isDebug()) continue;
                            Via.getPlatform().getLogger().warning("Ignoring plugin channel in incoming REGISTER: " + s2);
                        }
                        wrapper.write(Type.REMAINING_BYTES, Joiner.on('\u0000').join(rewrittenChannels).getBytes(StandardCharsets.UTF_8));
                    }
                    wrapper.set(Type.STRING, 0, channel);
                });
            }
        });
        ((Protocol1_13To1_12_2)this.protocol).registerServerbound(ServerboundPackets1_13.CREATIVE_INVENTORY_ACTION, new PacketHandlers(){

            @Override
            public void register() {
                this.map(Type.SHORT);
                this.map(Type.ITEM1_13, Type.ITEM1_8);
                this.handler(InventoryPackets.this.itemToServerHandler(Type.ITEM1_8));
            }
        });
    }

    @Override
    public Item handleItemToClient(Item item) {
        if (item == null) {
            return null;
        }
        CompoundTag tag = item.tag();
        int originalId = item.identifier() << 16 | item.data() & 0xFFFF;
        int rawId = item.identifier() << 4 | item.data() & 0xF;
        if (InventoryPackets.isDamageable(item.identifier())) {
            if (tag == null) {
                tag = new CompoundTag();
                item.setTag(tag);
            }
            tag.put("Damage", new IntTag(item.data()));
        }
        if (item.identifier() == 358) {
            if (tag == null) {
                tag = new CompoundTag();
                item.setTag(tag);
            }
            tag.put("map", new IntTag(item.data()));
        }
        if (tag != null) {
            String[] newValues;
            Object value;
            ListTag old;
            CompoundTag display;
            boolean banner;
            boolean bl2 = banner = item.identifier() == 425;
            if ((banner || item.identifier() == 442) && tag.get("BlockEntityTag") instanceof CompoundTag) {
                CompoundTag blockEntityTag = (CompoundTag)tag.get("BlockEntityTag");
                if (blockEntityTag.get("Base") instanceof IntTag) {
                    IntTag base = (IntTag)blockEntityTag.get("Base");
                    if (banner) {
                        rawId = 6800 + base.asInt();
                    }
                    base.setValue(15 - base.asInt());
                }
                if (blockEntityTag.get("Patterns") instanceof ListTag) {
                    for (Tag pattern : (ListTag)blockEntityTag.get("Patterns")) {
                        Object c2;
                        if (!(pattern instanceof CompoundTag) || !((c2 = ((CompoundTag)pattern).get("Color")) instanceof NumberTag)) continue;
                        ((CompoundTag)pattern).put("Color", new IntTag(15 - ((NumberTag)c2).asInt()));
                    }
                }
            }
            if (tag.get("display") instanceof CompoundTag && (display = (CompoundTag)tag.get("display")).get("Name") instanceof StringTag) {
                StringTag name = (StringTag)display.get("Name");
                display.put(NBT_TAG_NAME + "|Name", new StringTag(name.getValue()));
                name.setValue(ChatRewriter.legacyTextToJsonString(name.getValue(), true));
            }
            if (tag.get("ench") instanceof ListTag) {
                ListTag ench = (ListTag)tag.get("ench");
                ListTag enchantments = new ListTag(CompoundTag.class);
                for (Tag enchEntry : ench) {
                    NumberTag idTag;
                    if (!(enchEntry instanceof CompoundTag) || (idTag = (NumberTag)((CompoundTag)enchEntry).get("id")) == null) continue;
                    CompoundTag enchantmentEntry = new CompoundTag();
                    short oldId = idTag.asShort();
                    String newId = (String)Protocol1_13To1_12_2.MAPPINGS.getOldEnchantmentsIds().get(oldId);
                    if (newId == null) {
                        newId = "viaversion:legacy/" + oldId;
                    }
                    enchantmentEntry.put("id", new StringTag(newId));
                    enchantmentEntry.put("lvl", new ShortTag(((NumberTag)((CompoundTag)enchEntry).get("lvl")).asShort()));
                    enchantments.add(enchantmentEntry);
                }
                tag.remove("ench");
                tag.put("Enchantments", enchantments);
            }
            if (tag.get("StoredEnchantments") instanceof ListTag) {
                ListTag storedEnch = (ListTag)tag.get("StoredEnchantments");
                ListTag newStoredEnch = new ListTag(CompoundTag.class);
                for (Tag enchEntry : storedEnch) {
                    if (!(enchEntry instanceof CompoundTag)) continue;
                    CompoundTag enchantmentEntry = new CompoundTag();
                    short oldId = ((NumberTag)((CompoundTag)enchEntry).get("id")).asShort();
                    String newId = (String)Protocol1_13To1_12_2.MAPPINGS.getOldEnchantmentsIds().get(oldId);
                    if (newId == null) {
                        newId = "viaversion:legacy/" + oldId;
                    }
                    enchantmentEntry.put("id", new StringTag(newId));
                    enchantmentEntry.put("lvl", new ShortTag(((NumberTag)((CompoundTag)enchEntry).get("lvl")).asShort()));
                    newStoredEnch.add(enchantmentEntry);
                }
                tag.remove("StoredEnchantments");
                tag.put("StoredEnchantments", newStoredEnch);
            }
            if (tag.get("CanPlaceOn") instanceof ListTag) {
                old = (ListTag)tag.get("CanPlaceOn");
                ListTag newCanPlaceOn = new ListTag(StringTag.class);
                tag.put(NBT_TAG_NAME + "|CanPlaceOn", ConverterRegistry.convertToTag(ConverterRegistry.convertToValue(old)));
                for (Tag oldTag : old) {
                    value = oldTag.getValue();
                    String oldId = Key.stripMinecraftNamespace(value.toString());
                    String numberConverted = BlockIdData.numberIdToString.get(Ints.tryParse(oldId));
                    if (numberConverted != null) {
                        oldId = numberConverted;
                    }
                    if ((newValues = BlockIdData.blockIdMapping.get(oldId.toLowerCase(Locale.ROOT))) != null) {
                        for (String newValue : newValues) {
                            newCanPlaceOn.add(new StringTag(newValue));
                        }
                        continue;
                    }
                    newCanPlaceOn.add(new StringTag(oldId.toLowerCase(Locale.ROOT)));
                }
                tag.put("CanPlaceOn", newCanPlaceOn);
            }
            if (tag.get("CanDestroy") instanceof ListTag) {
                old = (ListTag)tag.get("CanDestroy");
                ListTag newCanDestroy = new ListTag(StringTag.class);
                tag.put(NBT_TAG_NAME + "|CanDestroy", ConverterRegistry.convertToTag(ConverterRegistry.convertToValue(old)));
                for (Tag oldTag : old) {
                    value = oldTag.getValue();
                    String oldId = Key.stripMinecraftNamespace(value.toString());
                    String numberConverted = BlockIdData.numberIdToString.get(Ints.tryParse(oldId));
                    if (numberConverted != null) {
                        oldId = numberConverted;
                    }
                    if ((newValues = BlockIdData.blockIdMapping.get(oldId.toLowerCase(Locale.ROOT))) != null) {
                        for (String newValue : newValues) {
                            newCanDestroy.add(new StringTag(newValue));
                        }
                        continue;
                    }
                    newCanDestroy.add(new StringTag(oldId.toLowerCase(Locale.ROOT)));
                }
                tag.put("CanDestroy", newCanDestroy);
            }
            if (item.identifier() == 383) {
                if (tag.get("EntityTag") instanceof CompoundTag) {
                    CompoundTag entityTag = (CompoundTag)tag.get("EntityTag");
                    if (entityTag.get("id") instanceof StringTag) {
                        StringTag identifier = (StringTag)entityTag.get("id");
                        rawId = SpawnEggRewriter.getSpawnEggId(identifier.getValue());
                        if (rawId == -1) {
                            rawId = 25100288;
                        } else {
                            entityTag.remove("id");
                            if (entityTag.isEmpty()) {
                                tag.remove("EntityTag");
                            }
                        }
                    } else {
                        rawId = 25100288;
                    }
                } else {
                    rawId = 25100288;
                }
            }
            if (tag.isEmpty()) {
                tag = null;
                item.setTag(null);
            }
        }
        if (Protocol1_13To1_12_2.MAPPINGS.getItemMappings().getNewId(rawId) == -1) {
            if (!InventoryPackets.isDamageable(item.identifier()) && item.identifier() != 358) {
                if (tag == null) {
                    tag = new CompoundTag();
                    item.setTag(tag);
                }
                tag.put(NBT_TAG_NAME, new IntTag(originalId));
            }
            if (item.identifier() == 31 && item.data() == 0) {
                rawId = 512;
            } else if (Protocol1_13To1_12_2.MAPPINGS.getItemMappings().getNewId(rawId & 0xFFFFFFF0) != -1) {
                rawId &= 0xFFFFFFF0;
            } else {
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

    public static String getNewPluginChannelId(String old) {
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
        }
        String mappedChannel = (String)Protocol1_13To1_12_2.MAPPINGS.getChannelMappings().get(old);
        if (mappedChannel != null) {
            return mappedChannel;
        }
        return MappingData.validateNewChannel(old);
    }

    @Override
    public Item handleItemToServer(Item item) {
        int oldId;
        if (item == null) {
            return null;
        }
        Integer rawId = null;
        boolean gotRawIdFromTag = false;
        CompoundTag tag = item.tag();
        if (tag != null && tag.get(NBT_TAG_NAME) instanceof IntTag) {
            rawId = ((NumberTag)tag.get(NBT_TAG_NAME)).asInt();
            tag.remove(NBT_TAG_NAME);
            gotRawIdFromTag = true;
        }
        if (rawId == null && (oldId = Protocol1_13To1_12_2.MAPPINGS.getItemMappings().inverse().getNewId(item.identifier())) != -1) {
            Optional<String> eggEntityId = SpawnEggRewriter.getEntityId(oldId);
            if (eggEntityId.isPresent()) {
                rawId = 25100288;
                if (tag == null) {
                    tag = new CompoundTag();
                    item.setTag(tag);
                }
                if (!tag.contains("EntityTag")) {
                    CompoundTag entityTag = new CompoundTag();
                    entityTag.put("id", new StringTag(eggEntityId.get()));
                    tag.put("EntityTag", entityTag);
                }
            } else {
                rawId = oldId >> 4 << 16 | oldId & 0xF;
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
            String[] newValues;
            Object value;
            Short oldId2;
            String newId;
            CompoundTag enchEntry;
            CompoundTag display;
            if (InventoryPackets.isDamageable(item.identifier()) && tag.get("Damage") instanceof IntTag) {
                if (!gotRawIdFromTag) {
                    item.setData((short)((Integer)((Tag)tag.get("Damage")).getValue()).intValue());
                }
                tag.remove("Damage");
            }
            if (item.identifier() == 358 && tag.get("map") instanceof IntTag) {
                if (!gotRawIdFromTag) {
                    item.setData((short)((Integer)((Tag)tag.get("map")).getValue()).intValue());
                }
                tag.remove("map");
            }
            if ((item.identifier() == 442 || item.identifier() == 425) && tag.get("BlockEntityTag") instanceof CompoundTag) {
                CompoundTag blockEntityTag = (CompoundTag)tag.get("BlockEntityTag");
                if (blockEntityTag.get("Base") instanceof IntTag) {
                    IntTag base = (IntTag)blockEntityTag.get("Base");
                    base.setValue(15 - base.asInt());
                }
                if (blockEntityTag.get("Patterns") instanceof ListTag) {
                    for (Tag pattern : (ListTag)blockEntityTag.get("Patterns")) {
                        if (!(pattern instanceof CompoundTag)) continue;
                        IntTag c2 = (IntTag)((CompoundTag)pattern).get("Color");
                        c2.setValue(15 - c2.asInt());
                    }
                }
            }
            if (tag.get("display") instanceof CompoundTag && (display = (CompoundTag)tag.get("display")).get("Name") instanceof StringTag) {
                StringTag name = (StringTag)display.get("Name");
                StringTag via = (StringTag)display.remove(NBT_TAG_NAME + "|Name");
                name.setValue(via != null ? via.getValue() : ChatRewriter.jsonToLegacyText(name.getValue()));
            }
            if (tag.get("Enchantments") instanceof ListTag) {
                ListTag enchantments = (ListTag)tag.get("Enchantments");
                ListTag ench = new ListTag(CompoundTag.class);
                for (Tag enchantmentEntry : enchantments) {
                    if (!(enchantmentEntry instanceof CompoundTag)) continue;
                    enchEntry = new CompoundTag();
                    newId = (String)((Tag)((CompoundTag)enchantmentEntry).get("id")).getValue();
                    oldId2 = (Short)Protocol1_13To1_12_2.MAPPINGS.getOldEnchantmentsIds().inverse().get(newId);
                    if (oldId2 == null && newId.startsWith("viaversion:legacy/")) {
                        oldId2 = Short.valueOf(newId.substring(18));
                    }
                    if (oldId2 == null) continue;
                    enchEntry.put("id", new ShortTag(oldId2));
                    enchEntry.put("lvl", new ShortTag(((NumberTag)((CompoundTag)enchantmentEntry).get("lvl")).asShort()));
                    ench.add(enchEntry);
                }
                tag.remove("Enchantments");
                tag.put("ench", ench);
            }
            if (tag.get("StoredEnchantments") instanceof ListTag) {
                ListTag storedEnch = (ListTag)tag.get("StoredEnchantments");
                ListTag newStoredEnch = new ListTag(CompoundTag.class);
                for (Tag enchantmentEntry : storedEnch) {
                    if (!(enchantmentEntry instanceof CompoundTag)) continue;
                    enchEntry = new CompoundTag();
                    newId = (String)((Tag)((CompoundTag)enchantmentEntry).get("id")).getValue();
                    oldId2 = (Short)Protocol1_13To1_12_2.MAPPINGS.getOldEnchantmentsIds().inverse().get(newId);
                    if (oldId2 == null && newId.startsWith("viaversion:legacy/")) {
                        oldId2 = Short.valueOf(newId.substring(18));
                    }
                    if (oldId2 == null) continue;
                    enchEntry.put("id", new ShortTag(oldId2));
                    enchEntry.put("lvl", new ShortTag(((NumberTag)((CompoundTag)enchantmentEntry).get("lvl")).asShort()));
                    newStoredEnch.add(enchEntry);
                }
                tag.remove("StoredEnchantments");
                tag.put("StoredEnchantments", newStoredEnch);
            }
            if (tag.get(NBT_TAG_NAME + "|CanPlaceOn") instanceof ListTag) {
                tag.put("CanPlaceOn", ConverterRegistry.convertToTag(ConverterRegistry.convertToValue(tag.get(NBT_TAG_NAME + "|CanPlaceOn"))));
                tag.remove(NBT_TAG_NAME + "|CanPlaceOn");
            } else if (tag.get("CanPlaceOn") instanceof ListTag) {
                ListTag old = (ListTag)tag.get("CanPlaceOn");
                ListTag newCanPlaceOn = new ListTag(StringTag.class);
                for (Tag oldTag : old) {
                    value = oldTag.getValue();
                    newValues = BlockIdData.fallbackReverseMapping.get(value instanceof String ? Key.stripMinecraftNamespace((String)value) : null);
                    if (newValues != null) {
                        for (String newValue : newValues) {
                            newCanPlaceOn.add(new StringTag(newValue));
                        }
                        continue;
                    }
                    newCanPlaceOn.add(oldTag);
                }
                tag.put("CanPlaceOn", newCanPlaceOn);
            }
            if (tag.get(NBT_TAG_NAME + "|CanDestroy") instanceof ListTag) {
                tag.put("CanDestroy", ConverterRegistry.convertToTag(ConverterRegistry.convertToValue(tag.get(NBT_TAG_NAME + "|CanDestroy"))));
                tag.remove(NBT_TAG_NAME + "|CanDestroy");
            } else if (tag.get("CanDestroy") instanceof ListTag) {
                ListTag old = (ListTag)tag.get("CanDestroy");
                ListTag newCanDestroy = new ListTag(StringTag.class);
                for (Tag oldTag : old) {
                    value = oldTag.getValue();
                    newValues = BlockIdData.fallbackReverseMapping.get(value instanceof String ? Key.stripMinecraftNamespace((String)value) : null);
                    if (newValues != null) {
                        for (String newValue : newValues) {
                            newCanDestroy.add(new StringTag(newValue));
                        }
                        continue;
                    }
                    newCanDestroy.add(oldTag);
                }
                tag.put("CanDestroy", newCanDestroy);
            }
        }
        return item;
    }

    public static String getOldPluginChannelId(String newId) {
        if ((newId = MappingData.validateNewChannel(newId)) == null) {
            return null;
        }
        switch (newId) {
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
        }
        String mappedChannel = (String)Protocol1_13To1_12_2.MAPPINGS.getChannelMappings().inverse().get(newId);
        if (mappedChannel != null) {
            return mappedChannel;
        }
        return newId.length() > 20 ? newId.substring(0, 20) : newId;
    }

    public static boolean isDamageable(int id2) {
        return id2 >= 256 && id2 <= 259 || id2 == 261 || id2 >= 267 && id2 <= 279 || id2 >= 283 && id2 <= 286 || id2 >= 290 && id2 <= 294 || id2 >= 298 && id2 <= 317 || id2 == 346 || id2 == 359 || id2 == 398 || id2 == 442 || id2 == 443;
    }
}

