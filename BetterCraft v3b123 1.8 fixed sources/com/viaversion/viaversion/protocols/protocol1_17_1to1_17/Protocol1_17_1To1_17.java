// 
// Decompiled by Procyon v0.6.0
// 

package com.viaversion.viaversion.protocols.protocol1_17_1to1_17;

import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.api.minecraft.item.DataItem;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.StringType;
import com.viaversion.viaversion.protocols.protocol1_17to1_16_4.ServerboundPackets1_17;
import com.viaversion.viaversion.protocols.protocol1_17to1_16_4.ClientboundPackets1_17;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;

public final class Protocol1_17_1To1_17 extends AbstractProtocol<ClientboundPackets1_17, ClientboundPackets1_17_1, ServerboundPackets1_17, ServerboundPackets1_17>
{
    private static final StringType PAGE_STRING_TYPE;
    private static final StringType TITLE_STRING_TYPE;
    
    public Protocol1_17_1To1_17() {
        super(ClientboundPackets1_17.class, ClientboundPackets1_17_1.class, ServerboundPackets1_17.class, ServerboundPackets1_17.class);
    }
    
    @Override
    protected void registerPackets() {
        ((Protocol<ClientboundPackets1_17, ClientboundPackets1_17_1, SM, SU>)this).registerClientbound(ClientboundPackets1_17.REMOVE_ENTITY, ClientboundPackets1_17_1.REMOVE_ENTITIES, wrapper -> {
            final int entityId = wrapper.read((Type<Integer>)Type.VAR_INT);
            wrapper.write(Type.VAR_INT_ARRAY_PRIMITIVE, new int[] { entityId });
            return;
        });
        ((AbstractProtocol<ClientboundPackets1_17, CM, SM, SU>)this).registerClientbound(ClientboundPackets1_17.SET_SLOT, new PacketHandlers() {
            public void register() {
                this.map((Type<Object>)Type.UNSIGNED_BYTE);
                this.create(Type.VAR_INT, 0);
            }
        });
        ((AbstractProtocol<ClientboundPackets1_17, CM, SM, SU>)this).registerClientbound(ClientboundPackets1_17.WINDOW_ITEMS, new PacketHandlers() {
            public void register() {
                this.map((Type<Object>)Type.UNSIGNED_BYTE);
                this.create(Type.VAR_INT, 0);
                this.handler(wrapper -> {
                    wrapper.write(Type.FLAT_VAR_INT_ITEM_ARRAY_VAR_INT, (Item[])(Object)wrapper.read((Type<T>)Type.FLAT_VAR_INT_ITEM_ARRAY));
                    wrapper.write(Type.FLAT_VAR_INT_ITEM, null);
                });
            }
        });
        ((AbstractProtocol<CU, CM, SM, ServerboundPackets1_17>)this).registerServerbound(ServerboundPackets1_17.CLICK_WINDOW, new PacketHandlers() {
            public void register() {
                this.map((Type<Object>)Type.UNSIGNED_BYTE);
                this.read(Type.VAR_INT);
            }
        });
        ((AbstractProtocol<CU, CM, SM, ServerboundPackets1_17>)this).registerServerbound(ServerboundPackets1_17.EDIT_BOOK, wrapper -> {
            final CompoundTag tag = new CompoundTag();
            final Item item = new DataItem(942, (byte)1, (short)0, tag);
            wrapper.write(Type.FLAT_VAR_INT_ITEM, item);
            final int slot = wrapper.read((Type<Integer>)Type.VAR_INT);
            final int pages = wrapper.read((Type<Integer>)Type.VAR_INT);
            final ListTag pagesTag = new ListTag(StringTag.class);
            for (int i = 0; i < pages; ++i) {
                final String page = wrapper.read((Type<String>)Protocol1_17_1To1_17.PAGE_STRING_TYPE);
                pagesTag.add(new StringTag(page));
            }
            if (pagesTag.size() == 0) {
                pagesTag.add(new StringTag(""));
            }
            tag.put("pages", pagesTag);
            if (wrapper.read((Type<Boolean>)Type.BOOLEAN)) {
                final String title = wrapper.read((Type<String>)Protocol1_17_1To1_17.TITLE_STRING_TYPE);
                tag.put("title", new StringTag(title));
                tag.put("author", new StringTag(wrapper.user().getProtocolInfo().getUsername()));
                wrapper.write(Type.BOOLEAN, true);
            }
            else {
                wrapper.write(Type.BOOLEAN, false);
            }
            wrapper.write(Type.VAR_INT, slot);
        });
    }
    
    static {
        PAGE_STRING_TYPE = new StringType(8192);
        TITLE_STRING_TYPE = new StringType(128);
    }
}
