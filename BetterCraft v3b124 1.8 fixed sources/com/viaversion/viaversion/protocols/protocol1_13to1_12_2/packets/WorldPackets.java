/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.protocols.protocol1_13to1_12_2.packets;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.BlockChangeRecord;
import com.viaversion.viaversion.api.minecraft.ClientWorld;
import com.viaversion.viaversion.api.minecraft.Particle;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import com.viaversion.viaversion.api.minecraft.chunks.DataPalette;
import com.viaversion.viaversion.api.minecraft.chunks.PaletteType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.chunk.ChunkType1_13;
import com.viaversion.viaversion.api.type.types.chunk.ChunkType1_9_3;
import com.viaversion.viaversion.libs.fastutil.ints.IntOpenHashSet;
import com.viaversion.viaversion.libs.fastutil.ints.IntSet;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.NumberTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.protocols.protocol1_12_1to1_12.ClientboundPackets1_12_1;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ClientboundPackets1_13;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.Protocol1_13To1_12_2;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ServerboundPackets1_13;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.ConnectionData;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.ConnectionHandler;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.data.NamedSoundRewriter;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.data.ParticleRewriter;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.providers.BlockEntityProvider;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.providers.PaintingProvider;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.storage.BlockStorage;
import com.viaversion.viaversion.util.Key;
import java.util.Iterator;
import java.util.Optional;

public class WorldPackets {
    private static final IntSet VALID_BIOMES;

    public static void register(Protocol1_13To1_12_2 protocol) {
        protocol.registerClientbound(ClientboundPackets1_12_1.SPAWN_PAINTING, new PacketHandlers(){

            @Override
            public void register() {
                this.map(Type.VAR_INT);
                this.map(Type.UUID);
                this.handler(wrapper -> {
                    String motive;
                    PaintingProvider provider = Via.getManager().getProviders().get(PaintingProvider.class);
                    Optional<Integer> id2 = provider.getIntByIdentifier(motive = wrapper.read(Type.STRING));
                    if (!(id2.isPresent() || Via.getConfig().isSuppressConversionWarnings() && !Via.getManager().isDebug())) {
                        Via.getPlatform().getLogger().warning("Could not find painting motive: " + motive + " falling back to default (0)");
                    }
                    wrapper.write(Type.VAR_INT, id2.orElse(0));
                });
            }
        });
        protocol.registerClientbound(ClientboundPackets1_12_1.BLOCK_ENTITY_DATA, new PacketHandlers(){

            @Override
            public void register() {
                this.map(Type.POSITION1_8);
                this.map(Type.UNSIGNED_BYTE);
                this.map(Type.NAMED_COMPOUND_TAG);
                this.handler(wrapper -> {
                    BlockStorage storage;
                    BlockStorage.ReplacementData replacementData;
                    Position position = wrapper.get(Type.POSITION1_8, 0);
                    short action = wrapper.get(Type.UNSIGNED_BYTE, 0);
                    CompoundTag tag = wrapper.get(Type.NAMED_COMPOUND_TAG, 0);
                    BlockEntityProvider provider = Via.getManager().getProviders().get(BlockEntityProvider.class);
                    int newId = provider.transform(wrapper.user(), position, tag, true);
                    if (newId != -1 && (replacementData = (storage = wrapper.user().get(BlockStorage.class)).get(position)) != null) {
                        replacementData.setReplacement(newId);
                    }
                    if (action == 5) {
                        wrapper.cancel();
                    }
                });
            }
        });
        protocol.registerClientbound(ClientboundPackets1_12_1.BLOCK_ACTION, new PacketHandlers(){

            @Override
            public void register() {
                this.map(Type.POSITION1_8);
                this.map(Type.UNSIGNED_BYTE);
                this.map(Type.UNSIGNED_BYTE);
                this.map(Type.VAR_INT);
                this.handler(wrapper -> {
                    Position pos = wrapper.get(Type.POSITION1_8, 0);
                    short action = wrapper.get(Type.UNSIGNED_BYTE, 0);
                    short param = wrapper.get(Type.UNSIGNED_BYTE, 1);
                    int blockId = wrapper.get(Type.VAR_INT, 0);
                    if (blockId == 25) {
                        blockId = 73;
                    } else if (blockId == 33) {
                        blockId = 99;
                    } else if (blockId == 29) {
                        blockId = 92;
                    } else if (blockId == 54) {
                        blockId = 142;
                    } else if (blockId == 146) {
                        blockId = 305;
                    } else if (blockId == 130) {
                        blockId = 249;
                    } else if (blockId == 138) {
                        blockId = 257;
                    } else if (blockId == 52) {
                        blockId = 140;
                    } else if (blockId == 209) {
                        blockId = 472;
                    } else if (blockId >= 219 && blockId <= 234) {
                        blockId = blockId - 219 + 483;
                    }
                    if (blockId == 73) {
                        PacketWrapper blockChange = wrapper.create(ClientboundPackets1_13.BLOCK_CHANGE);
                        blockChange.write(Type.POSITION1_8, pos);
                        blockChange.write(Type.VAR_INT, 249 + action * 24 * 2 + param * 2);
                        blockChange.send(Protocol1_13To1_12_2.class);
                    }
                    wrapper.set(Type.VAR_INT, 0, blockId);
                });
            }
        });
        protocol.registerClientbound(ClientboundPackets1_12_1.BLOCK_CHANGE, new PacketHandlers(){

            @Override
            public void register() {
                this.map(Type.POSITION1_8);
                this.map(Type.VAR_INT);
                this.handler(wrapper -> {
                    Position position = wrapper.get(Type.POSITION1_8, 0);
                    int newId = WorldPackets.toNewId(wrapper.get(Type.VAR_INT, 0));
                    UserConnection userConnection = wrapper.user();
                    if (Via.getConfig().isServersideBlockConnections()) {
                        newId = ConnectionData.connect(userConnection, position, newId);
                        ConnectionData.updateBlockStorage(userConnection, position.x(), position.y(), position.z(), newId);
                    }
                    wrapper.set(Type.VAR_INT, 0, WorldPackets.checkStorage(wrapper.user(), position, newId));
                    if (Via.getConfig().isServersideBlockConnections()) {
                        wrapper.send(Protocol1_13To1_12_2.class);
                        wrapper.cancel();
                        ConnectionData.update(userConnection, position);
                    }
                });
            }
        });
        protocol.registerClientbound(ClientboundPackets1_12_1.MULTI_BLOCK_CHANGE, new PacketHandlers(){

            @Override
            public void register() {
                this.map(Type.INT);
                this.map(Type.INT);
                this.map(Type.BLOCK_CHANGE_RECORD_ARRAY);
                this.handler(wrapper -> {
                    Position position;
                    BlockChangeRecord[] records;
                    int chunkX = wrapper.get(Type.INT, 0);
                    int chunkZ = wrapper.get(Type.INT, 1);
                    UserConnection userConnection = wrapper.user();
                    for (BlockChangeRecord record : records = wrapper.get(Type.BLOCK_CHANGE_RECORD_ARRAY, 0)) {
                        int newBlock = WorldPackets.toNewId(record.getBlockId());
                        position = new Position(record.getSectionX() + (chunkX << 4), record.getY(), record.getSectionZ() + (chunkZ << 4));
                        record.setBlockId(WorldPackets.checkStorage(wrapper.user(), position, newBlock));
                        if (!Via.getConfig().isServersideBlockConnections()) continue;
                        ConnectionData.updateBlockStorage(userConnection, position.x(), position.y(), position.z(), newBlock);
                    }
                    if (Via.getConfig().isServersideBlockConnections()) {
                        for (BlockChangeRecord record : records) {
                            int blockState = record.getBlockId();
                            position = new Position(record.getSectionX() + chunkX * 16, record.getY(), record.getSectionZ() + chunkZ * 16);
                            ConnectionHandler handler = ConnectionData.getConnectionHandler(blockState);
                            if (handler == null) continue;
                            blockState = handler.connect(userConnection, position, blockState);
                            record.setBlockId(blockState);
                            ConnectionData.updateBlockStorage(userConnection, position.x(), position.y(), position.z(), blockState);
                        }
                        wrapper.send(Protocol1_13To1_12_2.class);
                        wrapper.cancel();
                        for (BlockChangeRecord record : records) {
                            Position position2 = new Position(record.getSectionX() + chunkX * 16, record.getY(), record.getSectionZ() + chunkZ * 16);
                            ConnectionData.update(userConnection, position2);
                        }
                    }
                });
            }
        });
        protocol.registerClientbound(ClientboundPackets1_12_1.EXPLOSION, new PacketHandlers(){

            @Override
            public void register() {
                if (!Via.getConfig().isServersideBlockConnections()) {
                    return;
                }
                this.map(Type.FLOAT);
                this.map(Type.FLOAT);
                this.map(Type.FLOAT);
                this.map(Type.FLOAT);
                this.map(Type.INT);
                this.handler(wrapper -> {
                    int i2;
                    UserConnection userConnection = wrapper.user();
                    int x2 = (int)Math.floor(wrapper.get(Type.FLOAT, 0).floatValue());
                    int y2 = (int)Math.floor(wrapper.get(Type.FLOAT, 1).floatValue());
                    int z2 = (int)Math.floor(wrapper.get(Type.FLOAT, 2).floatValue());
                    int recordCount = wrapper.get(Type.INT, 0);
                    Position[] records = new Position[recordCount];
                    for (i2 = 0; i2 < recordCount; ++i2) {
                        Position position;
                        records[i2] = position = new Position(x2 + wrapper.passthrough(Type.BYTE), (short)(y2 + wrapper.passthrough(Type.BYTE)), z2 + wrapper.passthrough(Type.BYTE));
                        ConnectionData.updateBlockStorage(userConnection, position.x(), position.y(), position.z(), 0);
                    }
                    wrapper.send(Protocol1_13To1_12_2.class);
                    wrapper.cancel();
                    for (i2 = 0; i2 < recordCount; ++i2) {
                        ConnectionData.update(userConnection, records[i2]);
                    }
                });
            }
        });
        protocol.registerClientbound(ClientboundPackets1_12_1.UNLOAD_CHUNK, new PacketHandlers(){

            @Override
            public void register() {
                if (Via.getConfig().isServersideBlockConnections()) {
                    this.handler(wrapper -> {
                        int x2 = wrapper.passthrough(Type.INT);
                        int z2 = wrapper.passthrough(Type.INT);
                        ConnectionData.blockConnectionProvider.unloadChunk(wrapper.user(), x2, z2);
                    });
                }
            }
        });
        protocol.registerClientbound(ClientboundPackets1_12_1.NAMED_SOUND, new PacketHandlers(){

            @Override
            public void register() {
                this.map(Type.STRING);
                this.handler(wrapper -> {
                    String sound = Key.stripMinecraftNamespace(wrapper.get(Type.STRING, 0));
                    String newSoundId = NamedSoundRewriter.getNewId(sound);
                    wrapper.set(Type.STRING, 0, newSoundId);
                });
            }
        });
        protocol.registerClientbound(ClientboundPackets1_12_1.CHUNK_DATA, wrapper -> {
            ClientWorld clientWorld = wrapper.user().get(ClientWorld.class);
            BlockStorage storage = wrapper.user().get(BlockStorage.class);
            ChunkType1_9_3 type = ChunkType1_9_3.forEnvironment(clientWorld.getEnvironment());
            ChunkType1_13 type1_13 = ChunkType1_13.forEnvironment(clientWorld.getEnvironment());
            Chunk chunk = wrapper.read(type);
            wrapper.write(type1_13, chunk);
            for (int s2 = 0; s2 < chunk.getSections().length; ++s2) {
                int p2;
                boolean willSave;
                DataPalette blocks;
                block21: {
                    block20: {
                        ChunkSection section = chunk.getSections()[s2];
                        if (section == null) continue;
                        blocks = section.palette(PaletteType.BLOCKS);
                        for (int p3 = 0; p3 < blocks.size(); ++p3) {
                            int old = blocks.idByIndex(p3);
                            int newId = WorldPackets.toNewId(old);
                            blocks.setIdByIndex(p3, newId);
                        }
                        if (!chunk.isFullChunk()) break block20;
                        willSave = false;
                        for (p2 = 0; p2 < blocks.size(); ++p2) {
                            if (!storage.isWelcome(blocks.idByIndex(p2))) continue;
                            willSave = true;
                            break;
                        }
                        if (!willSave) break block21;
                    }
                    for (int idx = 0; idx < 4096; ++idx) {
                        int id2 = blocks.idAt(idx);
                        Position position = new Position(ChunkSection.xFromIndex(idx) + (chunk.getX() << 4), ChunkSection.yFromIndex(idx) + (s2 << 4), ChunkSection.zFromIndex(idx) + (chunk.getZ() << 4));
                        if (storage.isWelcome(id2)) {
                            storage.store(position, id2);
                            continue;
                        }
                        if (chunk.isFullChunk()) continue;
                        storage.remove(position);
                    }
                }
                if (!Via.getConfig().isServersideBlockConnections() || !ConnectionData.needStoreBlocks()) continue;
                if (!chunk.isFullChunk()) {
                    ConnectionData.blockConnectionProvider.unloadChunkSection(wrapper.user(), chunk.getX(), s2, chunk.getZ());
                }
                willSave = false;
                for (p2 = 0; p2 < blocks.size(); ++p2) {
                    if (!ConnectionData.isWelcome(blocks.idByIndex(p2))) continue;
                    willSave = true;
                    break;
                }
                if (!willSave) continue;
                for (int idx = 0; idx < 4096; ++idx) {
                    int id3 = blocks.idAt(idx);
                    if (!ConnectionData.isWelcome(id3)) continue;
                    int globalX = ChunkSection.xFromIndex(idx) + (chunk.getX() << 4);
                    int globalY = ChunkSection.yFromIndex(idx) + (s2 << 4);
                    int globalZ = ChunkSection.zFromIndex(idx) + (chunk.getZ() << 4);
                    ConnectionData.blockConnectionProvider.storeBlock(wrapper.user(), globalX, globalY, globalZ, id3);
                }
            }
            if (chunk.isBiomeData()) {
                int latestBiomeWarn = Integer.MIN_VALUE;
                for (int i2 = 0; i2 < 256; ++i2) {
                    int biome = chunk.getBiomeData()[i2];
                    if (VALID_BIOMES.contains(biome)) continue;
                    if (biome != 255 && latestBiomeWarn != biome) {
                        if (!Via.getConfig().isSuppressConversionWarnings() || Via.getManager().isDebug()) {
                            Via.getPlatform().getLogger().warning("Received invalid biome id " + biome);
                        }
                        latestBiomeWarn = biome;
                    }
                    chunk.getBiomeData()[i2] = 1;
                }
            }
            BlockEntityProvider provider = Via.getManager().getProviders().get(BlockEntityProvider.class);
            Iterator<CompoundTag> iterator = chunk.getBlockEntities().iterator();
            while (iterator.hasNext()) {
                String id4;
                Object idTag;
                CompoundTag tag = iterator.next();
                int newId = provider.transform(wrapper.user(), null, tag, false);
                if (newId != -1) {
                    int z2;
                    int y2;
                    int x2 = ((NumberTag)tag.get("x")).asInt();
                    Position position = new Position(x2, (short)(y2 = ((NumberTag)tag.get("y")).asInt()), z2 = ((NumberTag)tag.get("z")).asInt());
                    BlockStorage.ReplacementData replacementData = storage.get(position);
                    if (replacementData != null) {
                        replacementData.setReplacement(newId);
                    }
                    chunk.getSections()[y2 >> 4].palette(PaletteType.BLOCKS).setIdAt(x2 & 0xF, y2 & 0xF, z2 & 0xF, newId);
                }
                if (!((idTag = tag.get("id")) instanceof StringTag) || !(id4 = Key.namespaced(((StringTag)idTag).getValue())).equals("minecraft:noteblock") && !id4.equals("minecraft:flower_pot")) continue;
                iterator.remove();
            }
            if (Via.getConfig().isServersideBlockConnections()) {
                ConnectionData.connectBlocks(wrapper.user(), chunk);
                wrapper.send(Protocol1_13To1_12_2.class);
                wrapper.cancel();
                ConnectionData.NeighbourUpdater updater = new ConnectionData.NeighbourUpdater(wrapper.user());
                for (int i3 = 0; i3 < chunk.getSections().length; ++i3) {
                    ChunkSection section = chunk.getSections()[i3];
                    if (section == null) continue;
                    updater.updateChunkSectionNeighbours(chunk.getX(), chunk.getZ(), i3);
                }
            }
        });
        protocol.registerClientbound(ClientboundPackets1_12_1.SPAWN_PARTICLE, new PacketHandlers(){

            @Override
            public void register() {
                this.map(Type.INT);
                this.map(Type.BOOLEAN);
                this.map(Type.FLOAT);
                this.map(Type.FLOAT);
                this.map(Type.FLOAT);
                this.map(Type.FLOAT);
                this.map(Type.FLOAT);
                this.map(Type.FLOAT);
                this.map(Type.FLOAT);
                this.map(Type.INT);
                this.handler(wrapper -> {
                    int particleId = wrapper.get(Type.INT, 0);
                    int dataCount = 0;
                    if (particleId == 37 || particleId == 38 || particleId == 46) {
                        dataCount = 1;
                    } else if (particleId == 36) {
                        dataCount = 2;
                    }
                    Integer[] data = new Integer[dataCount];
                    for (int i2 = 0; i2 < data.length; ++i2) {
                        data[i2] = wrapper.read(Type.VAR_INT);
                    }
                    Particle particle = ParticleRewriter.rewriteParticle(particleId, data);
                    if (particle == null || particle.getId() == -1) {
                        wrapper.cancel();
                        return;
                    }
                    if (particle.getId() == 11) {
                        int count = wrapper.get(Type.INT, 1);
                        float speed = wrapper.get(Type.FLOAT, 6).floatValue();
                        if (count == 0) {
                            wrapper.set(Type.INT, 1, 1);
                            wrapper.set(Type.FLOAT, 6, Float.valueOf(0.0f));
                            for (int i3 = 0; i3 < 3; ++i3) {
                                float colorValue = wrapper.get(Type.FLOAT, i3 + 3).floatValue() * speed;
                                if (colorValue == 0.0f && i3 == 0) {
                                    colorValue = 1.0f;
                                }
                                particle.getArgument(i3).setValue(Float.valueOf(colorValue));
                                wrapper.set(Type.FLOAT, i3 + 3, Float.valueOf(0.0f));
                            }
                        }
                    }
                    wrapper.set(Type.INT, 0, particle.getId());
                    for (Particle.ParticleData<?> particleData : particle.getArguments()) {
                        particleData.write(wrapper);
                    }
                });
            }
        });
        protocol.registerServerbound(ServerboundPackets1_13.PLAYER_BLOCK_PLACEMENT, wrapper -> {
            Position pos = wrapper.passthrough(Type.POSITION1_8);
            wrapper.passthrough(Type.VAR_INT);
            wrapper.passthrough(Type.VAR_INT);
            wrapper.passthrough(Type.FLOAT);
            wrapper.passthrough(Type.FLOAT);
            wrapper.passthrough(Type.FLOAT);
            if (Via.getConfig().isServersideBlockConnections() && ConnectionData.needStoreBlocks()) {
                ConnectionData.markModified(wrapper.user(), pos);
            }
        });
        protocol.registerServerbound(ServerboundPackets1_13.PLAYER_DIGGING, wrapper -> {
            int status = wrapper.passthrough(Type.VAR_INT);
            Position pos = wrapper.passthrough(Type.POSITION1_8);
            wrapper.passthrough(Type.UNSIGNED_BYTE);
            if (status == 0 && Via.getConfig().isServersideBlockConnections() && ConnectionData.needStoreBlocks()) {
                ConnectionData.markModified(wrapper.user(), pos);
            }
        });
    }

    public static int toNewId(int oldId) {
        int newId;
        if (oldId < 0) {
            oldId = 0;
        }
        if ((newId = Protocol1_13To1_12_2.MAPPINGS.getBlockMappings().getNewId(oldId)) != -1) {
            return newId;
        }
        newId = Protocol1_13To1_12_2.MAPPINGS.getBlockMappings().getNewId(oldId & 0xFFFFFFF0);
        if (newId != -1) {
            if (!Via.getConfig().isSuppressConversionWarnings() || Via.getManager().isDebug()) {
                Via.getPlatform().getLogger().warning("Missing block " + oldId);
            }
            return newId;
        }
        if (!Via.getConfig().isSuppressConversionWarnings() || Via.getManager().isDebug()) {
            Via.getPlatform().getLogger().warning("Missing block completely " + oldId);
        }
        return 1;
    }

    private static int checkStorage(UserConnection user, Position position, int newId) {
        BlockStorage storage = user.get(BlockStorage.class);
        if (storage.contains(position)) {
            BlockStorage.ReplacementData data = storage.get(position);
            if (data.getOriginal() == newId) {
                if (data.getReplacement() != -1) {
                    return data.getReplacement();
                }
            } else {
                storage.remove(position);
                if (storage.isWelcome(newId)) {
                    storage.store(position, newId);
                }
            }
        } else if (storage.isWelcome(newId)) {
            storage.store(position, newId);
        }
        return newId;
    }

    static {
        int i2;
        VALID_BIOMES = new IntOpenHashSet(70, 0.99f);
        for (i2 = 0; i2 < 50; ++i2) {
            VALID_BIOMES.add(i2);
        }
        VALID_BIOMES.add(127);
        for (i2 = 129; i2 <= 134; ++i2) {
            VALID_BIOMES.add(i2);
        }
        VALID_BIOMES.add(140);
        VALID_BIOMES.add(149);
        VALID_BIOMES.add(151);
        for (i2 = 155; i2 <= 158; ++i2) {
            VALID_BIOMES.add(i2);
        }
        for (i2 = 160; i2 <= 167; ++i2) {
            VALID_BIOMES.add(i2);
        }
    }
}

