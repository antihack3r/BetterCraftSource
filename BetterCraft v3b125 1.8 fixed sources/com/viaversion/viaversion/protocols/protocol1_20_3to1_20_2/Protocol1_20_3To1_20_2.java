/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.protocols.protocol1_20_3to1_20_2;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.data.MappingData;
import com.viaversion.viaversion.api.data.MappingDataBase;
import com.viaversion.viaversion.api.minecraft.entities.EntityTypes1_20_3;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.UUIDIntArrayType;
import com.viaversion.viaversion.api.type.types.misc.ParticleType;
import com.viaversion.viaversion.api.type.types.version.Types1_20_3;
import com.viaversion.viaversion.data.entity.EntityTrackerBase;
import com.viaversion.viaversion.libs.gson.JsonArray;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.libs.gson.JsonPrimitive;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ByteArrayTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ByteTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.DoubleTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.FloatTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.IntArrayTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.IntTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.LongArrayTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.LongTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.NumberTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ShortTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import com.viaversion.viaversion.protocols.protocol1_20_2to1_20.packet.ClientboundConfigurationPackets1_20_2;
import com.viaversion.viaversion.protocols.protocol1_20_2to1_20.packet.ClientboundPackets1_20_2;
import com.viaversion.viaversion.protocols.protocol1_20_2to1_20.packet.ServerboundConfigurationPackets1_20_2;
import com.viaversion.viaversion.protocols.protocol1_20_2to1_20.packet.ServerboundPackets1_20_2;
import com.viaversion.viaversion.protocols.protocol1_20_3to1_20_2.packet.ClientboundPackets1_20_3;
import com.viaversion.viaversion.protocols.protocol1_20_3to1_20_2.packet.ServerboundPackets1_20_3;
import com.viaversion.viaversion.protocols.protocol1_20_3to1_20_2.rewriter.BlockItemPacketRewriter1_20_3;
import com.viaversion.viaversion.protocols.protocol1_20_3to1_20_2.rewriter.EntityPacketRewriter1_20_3;
import com.viaversion.viaversion.rewriter.SoundRewriter;
import com.viaversion.viaversion.rewriter.StatisticsRewriter;
import com.viaversion.viaversion.rewriter.TagRewriter;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class Protocol1_20_3To1_20_2
extends AbstractProtocol<ClientboundPackets1_20_2, ClientboundPackets1_20_3, ServerboundPackets1_20_2, ServerboundPackets1_20_3> {
    public static final MappingData MAPPINGS = new MappingDataBase("1.20.2", "1.20.3");
    private static final Set<String> BOOLEAN_TYPES = new HashSet<String>(Arrays.asList("interpret", "bold", "italic", "underlined", "strikethrough", "obfuscated"));
    private final BlockItemPacketRewriter1_20_3 itemRewriter = new BlockItemPacketRewriter1_20_3(this);
    private final EntityPacketRewriter1_20_3 entityRewriter = new EntityPacketRewriter1_20_3(this);

    public Protocol1_20_3To1_20_2() {
        super(ClientboundPackets1_20_2.class, ClientboundPackets1_20_3.class, ServerboundPackets1_20_2.class, ServerboundPackets1_20_3.class);
    }

    @Override
    protected void registerPackets() {
        super.registerPackets();
        this.cancelServerbound(ServerboundPackets1_20_3.CONTAINER_SLOT_STATE_CHANGED);
        TagRewriter<ClientboundPackets1_20_2> tagRewriter = new TagRewriter<ClientboundPackets1_20_2>(this);
        tagRewriter.registerGeneric(ClientboundPackets1_20_2.TAGS);
        SoundRewriter<ClientboundPackets1_20_2> soundRewriter = new SoundRewriter<ClientboundPackets1_20_2>(this);
        soundRewriter.register1_19_3Sound(ClientboundPackets1_20_2.SOUND);
        soundRewriter.registerSound(ClientboundPackets1_20_2.ENTITY_SOUND);
        new StatisticsRewriter<ClientboundPackets1_20_2>(this).register(ClientboundPackets1_20_2.STATISTICS);
        this.registerServerbound(ServerboundPackets1_20_3.UPDATE_JIGSAW_BLOCK, (PacketWrapper wrapper) -> {
            wrapper.passthrough(Type.POSITION1_14);
            wrapper.passthrough(Type.STRING);
            wrapper.passthrough(Type.STRING);
            wrapper.passthrough(Type.STRING);
            wrapper.passthrough(Type.STRING);
            wrapper.passthrough(Type.STRING);
            wrapper.read(Type.VAR_INT);
            wrapper.read(Type.VAR_INT);
        });
        this.registerClientbound(ClientboundPackets1_20_2.ADVANCEMENTS, (PacketWrapper wrapper) -> {
            wrapper.passthrough(Type.BOOLEAN);
            int size = wrapper.passthrough(Type.VAR_INT);
            for (int i2 = 0; i2 < size; ++i2) {
                wrapper.passthrough(Type.STRING);
                if (wrapper.passthrough(Type.BOOLEAN).booleanValue()) {
                    wrapper.passthrough(Type.STRING);
                }
                if (wrapper.passthrough(Type.BOOLEAN).booleanValue()) {
                    this.convertComponent(wrapper);
                    this.convertComponent(wrapper);
                    this.itemRewriter.handleItemToClient(wrapper.passthrough(Type.ITEM1_20_2));
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
        this.registerClientbound(ClientboundPackets1_20_2.TAB_COMPLETE, (PacketWrapper wrapper) -> {
            wrapper.passthrough(Type.VAR_INT);
            wrapper.passthrough(Type.VAR_INT);
            wrapper.passthrough(Type.VAR_INT);
            int suggestions = wrapper.passthrough(Type.VAR_INT);
            for (int i2 = 0; i2 < suggestions; ++i2) {
                wrapper.passthrough(Type.STRING);
                this.convertOptionalComponent(wrapper);
            }
        });
        this.registerClientbound(ClientboundPackets1_20_2.MAP_DATA, (PacketWrapper wrapper) -> {
            wrapper.passthrough(Type.VAR_INT);
            wrapper.passthrough(Type.BYTE);
            wrapper.passthrough(Type.BOOLEAN);
            if (wrapper.passthrough(Type.BOOLEAN).booleanValue()) {
                int icons = wrapper.passthrough(Type.VAR_INT);
                for (int i2 = 0; i2 < icons; ++i2) {
                    wrapper.passthrough(Type.BYTE);
                    wrapper.passthrough(Type.BYTE);
                    wrapper.passthrough(Type.BYTE);
                    wrapper.passthrough(Type.BYTE);
                    this.convertOptionalComponent(wrapper);
                }
            }
        });
        this.registerClientbound(ClientboundPackets1_20_2.BOSSBAR, (PacketWrapper wrapper) -> {
            wrapper.passthrough(Type.UUID);
            int action = wrapper.passthrough(Type.VAR_INT);
            if (action == 0 || action == 3) {
                this.convertComponent(wrapper);
            }
        });
        this.registerClientbound(ClientboundPackets1_20_2.PLAYER_CHAT, (PacketWrapper wrapper) -> {
            wrapper.passthrough(Type.UUID);
            wrapper.passthrough(Type.VAR_INT);
            wrapper.passthrough(Type.OPTIONAL_SIGNATURE_BYTES);
            wrapper.passthrough(Type.STRING);
            wrapper.passthrough(Type.LONG);
            wrapper.passthrough(Type.LONG);
            int lastSeen = wrapper.passthrough(Type.VAR_INT);
            for (int i2 = 0; i2 < lastSeen; ++i2) {
                int index = wrapper.passthrough(Type.VAR_INT);
                if (index != 0) continue;
                wrapper.passthrough(Type.SIGNATURE_BYTES);
            }
            this.convertOptionalComponent(wrapper);
            int filterMaskType = wrapper.passthrough(Type.VAR_INT);
            if (filterMaskType == 2) {
                wrapper.passthrough(Type.LONG_ARRAY_PRIMITIVE);
            }
            wrapper.passthrough(Type.VAR_INT);
            this.convertComponent(wrapper);
            this.convertOptionalComponent(wrapper);
        });
        this.registerClientbound(ClientboundPackets1_20_2.SCOREBOARD_OBJECTIVE, (PacketWrapper wrapper) -> {
            wrapper.passthrough(Type.STRING);
            byte action = wrapper.passthrough(Type.BYTE);
            if (action == 0 || action == 2) {
                this.convertComponent(wrapper);
            }
        });
        this.registerClientbound(ClientboundPackets1_20_2.TEAMS, (PacketWrapper wrapper) -> {
            wrapper.passthrough(Type.STRING);
            byte action = wrapper.passthrough(Type.BYTE);
            if (action == 0 || action == 2) {
                this.convertComponent(wrapper);
                wrapper.passthrough(Type.BYTE);
                wrapper.passthrough(Type.STRING);
                wrapper.passthrough(Type.STRING);
                wrapper.passthrough(Type.VAR_INT);
                this.convertComponent(wrapper);
                this.convertComponent(wrapper);
            }
        });
        this.registerClientbound(State.CONFIGURATION, ClientboundConfigurationPackets1_20_2.DISCONNECT.getId(), ClientboundConfigurationPackets1_20_2.DISCONNECT.getId(), this::convertComponent);
        this.registerClientbound(State.CONFIGURATION, ClientboundConfigurationPackets1_20_2.RESOURCE_PACK.getId(), ClientboundConfigurationPackets1_20_2.RESOURCE_PACK.getId(), this.resourcePackHandler());
        this.registerClientbound(ClientboundPackets1_20_2.DISCONNECT, this::convertComponent);
        this.registerClientbound(ClientboundPackets1_20_2.RESOURCE_PACK, this.resourcePackHandler());
        this.registerClientbound(ClientboundPackets1_20_2.SERVER_DATA, this::convertComponent);
        this.registerClientbound(ClientboundPackets1_20_2.ACTIONBAR, this::convertComponent);
        this.registerClientbound(ClientboundPackets1_20_2.TITLE_TEXT, this::convertComponent);
        this.registerClientbound(ClientboundPackets1_20_2.TITLE_SUBTITLE, this::convertComponent);
        this.registerClientbound(ClientboundPackets1_20_2.DISGUISED_CHAT, this::convertComponent);
        this.registerClientbound(ClientboundPackets1_20_2.SYSTEM_CHAT, this::convertComponent);
        this.registerClientbound(ClientboundPackets1_20_2.OPEN_WINDOW, (PacketWrapper wrapper) -> {
            wrapper.passthrough(Type.VAR_INT);
            int containerTypeId = wrapper.read(Type.VAR_INT);
            wrapper.write(Type.VAR_INT, MAPPINGS.getMenuMappings().getNewId(containerTypeId));
            this.convertComponent(wrapper);
        });
        this.registerClientbound(ClientboundPackets1_20_2.TAB_LIST, (PacketWrapper wrapper) -> {
            this.convertComponent(wrapper);
            this.convertComponent(wrapper);
        });
        this.registerClientbound(ClientboundPackets1_20_2.COMBAT_KILL, new PacketHandlers(){

            @Override
            public void register() {
                this.map(Type.VAR_INT);
                this.handler(wrapper -> Protocol1_20_3To1_20_2.this.convertComponent(wrapper));
            }
        });
        this.registerClientbound(ClientboundPackets1_20_2.PLAYER_INFO_UPDATE, (PacketWrapper wrapper) -> {
            BitSet actions = wrapper.passthrough(Type.PROFILE_ACTIONS_ENUM);
            int entries = wrapper.passthrough(Type.VAR_INT);
            for (int i2 = 0; i2 < entries; ++i2) {
                wrapper.passthrough(Type.UUID);
                if (actions.get(0)) {
                    wrapper.passthrough(Type.STRING);
                    int properties = wrapper.passthrough(Type.VAR_INT);
                    for (int j2 = 0; j2 < properties; ++j2) {
                        wrapper.passthrough(Type.STRING);
                        wrapper.passthrough(Type.STRING);
                        wrapper.passthrough(Type.OPTIONAL_STRING);
                    }
                }
                if (actions.get(1) && wrapper.passthrough(Type.BOOLEAN).booleanValue()) {
                    wrapper.passthrough(Type.UUID);
                    wrapper.passthrough(Type.PROFILE_KEY);
                }
                if (actions.get(2)) {
                    wrapper.passthrough(Type.VAR_INT);
                }
                if (actions.get(3)) {
                    wrapper.passthrough(Type.BOOLEAN);
                }
                if (actions.get(4)) {
                    wrapper.passthrough(Type.VAR_INT);
                }
                if (!actions.get(5)) continue;
                this.convertOptionalComponent(wrapper);
            }
        });
    }

    private PacketHandler resourcePackHandler() {
        return wrapper -> {
            wrapper.passthrough(Type.STRING);
            wrapper.passthrough(Type.STRING);
            wrapper.passthrough(Type.BOOLEAN);
            this.convertOptionalComponent(wrapper);
        };
    }

    private void convertComponent(PacketWrapper wrapper) throws Exception {
        wrapper.write(Type.TAG, Protocol1_20_3To1_20_2.jsonComponentToTag(wrapper.read(Type.COMPONENT)));
    }

    private void convertOptionalComponent(PacketWrapper wrapper) throws Exception {
        wrapper.write(Type.OPTIONAL_TAG, Protocol1_20_3To1_20_2.jsonComponentToTag(wrapper.read(Type.OPTIONAL_COMPONENT)));
    }

    public static @Nullable JsonElement tagComponentToJson(@Nullable Tag tag) {
        try {
            return Protocol1_20_3To1_20_2.convertToJson(null, tag);
        }
        catch (Exception e2) {
            Via.getPlatform().getLogger().severe("Error converting component: " + tag);
            e2.printStackTrace();
            return new JsonPrimitive("<error>");
        }
    }

    public static @Nullable Tag jsonComponentToTag(@Nullable JsonElement component) {
        try {
            return Protocol1_20_3To1_20_2.convertToTag(component);
        }
        catch (Exception e2) {
            Via.getPlatform().getLogger().severe("Error converting component: " + component);
            e2.printStackTrace();
            return new StringTag("<error>");
        }
    }

    private static @Nullable Tag convertToTag(@Nullable JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return null;
        }
        if (element.isJsonObject()) {
            CompoundTag tag = new CompoundTag();
            for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
                Protocol1_20_3To1_20_2.convertObjectEntry(entry.getKey(), entry.getValue(), tag);
            }
            return tag;
        }
        if (element.isJsonArray()) {
            return Protocol1_20_3To1_20_2.convertJsonArray(element);
        }
        if (element.isJsonPrimitive()) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            if (primitive.isString()) {
                return new StringTag(primitive.getAsString());
            }
            if (primitive.isBoolean()) {
                return new ByteTag((byte)(primitive.getAsBoolean() ? (char)'\u0001' : '\u0000'));
            }
            Number number = primitive.getAsNumber();
            if (number instanceof Integer) {
                return new IntTag(number.intValue());
            }
            if (number instanceof Byte) {
                return new ByteTag(number.byteValue());
            }
            if (number instanceof Short) {
                return new ShortTag(number.shortValue());
            }
            if (number instanceof Long) {
                return new LongTag(number.longValue());
            }
            if (number instanceof Double) {
                return new DoubleTag(number.doubleValue());
            }
            if (number instanceof Float) {
                return new FloatTag(number.floatValue());
            }
            return new StringTag(primitive.getAsString());
        }
        throw new IllegalArgumentException("Unhandled json type " + element.getClass().getSimpleName() + " with value " + element.getAsString());
    }

    private static ListTag convertJsonArray(JsonElement element) {
        ListTag listTag = new ListTag();
        boolean singleType = true;
        for (JsonElement entry : element.getAsJsonArray()) {
            Tag convertedEntryTag = Protocol1_20_3To1_20_2.convertToTag(entry);
            if (listTag.getElementType() != null && listTag.getElementType() != convertedEntryTag.getClass()) {
                singleType = false;
                break;
            }
            listTag.add(convertedEntryTag);
        }
        if (singleType) {
            return listTag;
        }
        ListTag processedListTag = new ListTag();
        for (JsonElement entry : element.getAsJsonArray()) {
            Tag convertedTag = Protocol1_20_3To1_20_2.convertToTag(entry);
            if (convertedTag instanceof CompoundTag) {
                processedListTag.add(listTag);
                continue;
            }
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.put("text", new StringTag());
            compoundTag.put("extra", convertedTag);
        }
        return processedListTag;
    }

    private static void convertObjectEntry(String key, JsonElement element, CompoundTag tag) {
        UUID uuid;
        JsonObject hoverEvent;
        JsonElement id2;
        if (key.equals("contents") && element.isJsonObject() && (id2 = (hoverEvent = element.getAsJsonObject()).get("id")) != null && id2.isJsonPrimitive() && (uuid = Protocol1_20_3To1_20_2.parseUUID(id2.getAsString())) != null) {
            hoverEvent.remove("id");
            CompoundTag convertedTag = (CompoundTag)Protocol1_20_3To1_20_2.convertToTag(element);
            convertedTag.put("id", new IntArrayTag(UUIDIntArrayType.uuidToIntArray(uuid)));
            tag.put(key, convertedTag);
            return;
        }
        tag.put(key, Protocol1_20_3To1_20_2.convertToTag(element));
    }

    private static @Nullable UUID parseUUID(String uuidString) {
        try {
            return UUID.fromString(uuidString);
        }
        catch (IllegalArgumentException e2) {
            return null;
        }
    }

    private static @Nullable JsonElement convertToJson(@Nullable String key, @Nullable Tag tag) {
        if (tag == null) {
            return null;
        }
        if (tag instanceof CompoundTag) {
            JsonObject object = new JsonObject();
            for (Map.Entry<String, Tag> entry : ((CompoundTag)tag).entrySet()) {
                Protocol1_20_3To1_20_2.convertCompoundTagEntry(entry.getKey(), entry.getValue(), object);
            }
            return object;
        }
        if (tag instanceof ListTag) {
            ListTag list = (ListTag)tag;
            JsonArray array = new JsonArray();
            for (Tag listEntry : list) {
                array.add(Protocol1_20_3To1_20_2.convertToJson(null, listEntry));
            }
            return array;
        }
        if (tag instanceof NumberTag) {
            NumberTag numberTag = (NumberTag)tag;
            if (key != null && BOOLEAN_TYPES.contains(key)) {
                return new JsonPrimitive(numberTag.asBoolean());
            }
            return new JsonPrimitive(numberTag.getValue());
        }
        if (tag instanceof StringTag) {
            return new JsonPrimitive(((StringTag)tag).getValue());
        }
        if (tag instanceof ByteArrayTag) {
            ByteArrayTag arrayTag = (ByteArrayTag)tag;
            JsonArray array = new JsonArray();
            for (byte num : arrayTag.getValue()) {
                array.add(num);
            }
            return array;
        }
        if (tag instanceof IntArrayTag) {
            IntArrayTag arrayTag = (IntArrayTag)tag;
            JsonArray array = new JsonArray();
            for (int num : arrayTag.getValue()) {
                array.add(num);
            }
            return array;
        }
        if (tag instanceof LongArrayTag) {
            LongArrayTag arrayTag = (LongArrayTag)tag;
            JsonArray array = new JsonArray();
            for (long num : arrayTag.getValue()) {
                array.add(num);
            }
            return array;
        }
        throw new IllegalArgumentException("Unhandled tag type " + tag.getClass().getSimpleName());
    }

    private static void convertCompoundTagEntry(String key, Tag tag, JsonObject object) {
        CompoundTag showEntity;
        Object idTag;
        if (key.equals("contents") && tag instanceof CompoundTag && (idTag = (showEntity = (CompoundTag)tag).get("id")) instanceof IntArrayTag) {
            showEntity.remove("id");
            JsonObject convertedElement = (JsonObject)Protocol1_20_3To1_20_2.convertToJson(key, tag);
            convertedElement.addProperty("id", Protocol1_20_3To1_20_2.uuidIntsToString(((IntArrayTag)idTag).getValue()));
            object.add(key, convertedElement);
            return;
        }
        object.add(key.isEmpty() ? "text" : key, Protocol1_20_3To1_20_2.convertToJson(key, tag));
    }

    private static String uuidIntsToString(int[] parts) {
        if (parts.length != 4) {
            return new UUID(0L, 0L).toString();
        }
        return UUIDIntArrayType.uuidFromIntArray(parts).toString();
    }

    @Override
    protected void onMappingDataLoaded() {
        super.onMappingDataLoaded();
        EntityTypes1_20_3.initialize(this);
        Types1_20_3.PARTICLE.filler(this).reader("block", ParticleType.Readers.BLOCK).reader("block_marker", ParticleType.Readers.BLOCK).reader("dust", ParticleType.Readers.DUST).reader("falling_dust", ParticleType.Readers.BLOCK).reader("dust_color_transition", ParticleType.Readers.DUST_TRANSITION).reader("item", ParticleType.Readers.ITEM1_20_2).reader("vibration", ParticleType.Readers.VIBRATION1_20_3).reader("sculk_charge", ParticleType.Readers.SCULK_CHARGE).reader("shriek", ParticleType.Readers.SHRIEK);
    }

    @Override
    public void init(UserConnection connection) {
        this.addEntityTracker(connection, new EntityTrackerBase(connection, EntityTypes1_20_3.PLAYER));
    }

    @Override
    public MappingData getMappingData() {
        return MAPPINGS;
    }

    public BlockItemPacketRewriter1_20_3 getItemRewriter() {
        return this.itemRewriter;
    }

    public EntityPacketRewriter1_20_3 getEntityRewriter() {
        return this.entityRewriter;
    }

    @Override
    protected ServerboundPacketType serverboundFinishConfigurationPacket() {
        return ServerboundConfigurationPackets1_20_2.FINISH_CONFIGURATION;
    }

    @Override
    protected ClientboundPacketType clientboundFinishConfigurationPacket() {
        return ClientboundConfigurationPackets1_20_2.FINISH_CONFIGURATION;
    }
}

