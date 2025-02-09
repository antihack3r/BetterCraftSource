/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.resources;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiScreenResourcePacks;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;

public abstract class ResourcePackListEntry
implements GuiListExtended.IGuiListEntry {
    private static final ResourceLocation RESOURCE_PACKS_TEXTURE = new ResourceLocation("textures/gui/resource_packs.png");
    private static final IChatComponent field_183020_d = new ChatComponentTranslation("resourcePack.incompatible", new Object[0]);
    private static final IChatComponent field_183021_e = new ChatComponentTranslation("resourcePack.incompatible.old", new Object[0]);
    private static final IChatComponent field_183022_f = new ChatComponentTranslation("resourcePack.incompatible.new", new Object[0]);
    protected final Minecraft mc;
    protected final GuiScreenResourcePacks resourcePacksGUI;

    public ResourcePackListEntry(GuiScreenResourcePacks resourcePacksGUIIn) {
        this.resourcePacksGUI = resourcePacksGUIIn;
        this.mc = Minecraft.getMinecraft();
    }

    @Override
    public void drawEntry(int slotIndex, int x2, int y2, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
        int i1;
        int i2 = this.func_183019_a();
        if (i2 != 1) {
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            Gui.drawRect(x2 - 1, y2 - 1, x2 + listWidth - 9, y2 + slotHeight + 1, -8978432);
        }
        this.func_148313_c();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        Gui.drawModalRectWithCustomSizedTexture(x2, y2, 0.0f, 0.0f, 32, 32, 32.0f, 32.0f);
        String s2 = this.func_148312_b();
        String s1 = this.func_148311_a();
        if ((this.mc.gameSettings.touchscreen || isSelected) && this.func_148310_d()) {
            this.mc.getTextureManager().bindTexture(RESOURCE_PACKS_TEXTURE);
            Gui.drawRect(x2, y2, x2 + 32, y2 + 32, -1601138544);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            int j2 = mouseX - x2;
            int k2 = mouseY - y2;
            if (i2 < 1) {
                s2 = field_183020_d.getFormattedText();
                s1 = field_183021_e.getFormattedText();
            } else if (i2 > 1) {
                s2 = field_183020_d.getFormattedText();
                s1 = field_183022_f.getFormattedText();
            }
            if (this.func_148309_e()) {
                if (j2 < 32) {
                    Gui.drawModalRectWithCustomSizedTexture(x2, y2, 0.0f, 32.0f, 32, 32, 256.0f, 256.0f);
                } else {
                    Gui.drawModalRectWithCustomSizedTexture(x2, y2, 0.0f, 0.0f, 32, 32, 256.0f, 256.0f);
                }
            } else {
                if (this.func_148308_f()) {
                    if (j2 < 16) {
                        Gui.drawModalRectWithCustomSizedTexture(x2, y2, 32.0f, 32.0f, 32, 32, 256.0f, 256.0f);
                    } else {
                        Gui.drawModalRectWithCustomSizedTexture(x2, y2, 32.0f, 0.0f, 32, 32, 256.0f, 256.0f);
                    }
                }
                if (this.func_148314_g()) {
                    if (j2 < 32 && j2 > 16 && k2 < 16) {
                        Gui.drawModalRectWithCustomSizedTexture(x2, y2, 96.0f, 32.0f, 32, 32, 256.0f, 256.0f);
                    } else {
                        Gui.drawModalRectWithCustomSizedTexture(x2, y2, 96.0f, 0.0f, 32, 32, 256.0f, 256.0f);
                    }
                }
                if (this.func_148307_h()) {
                    if (j2 < 32 && j2 > 16 && k2 > 16) {
                        Gui.drawModalRectWithCustomSizedTexture(x2, y2, 64.0f, 32.0f, 32, 32, 256.0f, 256.0f);
                    } else {
                        Gui.drawModalRectWithCustomSizedTexture(x2, y2, 64.0f, 0.0f, 32, 32, 256.0f, 256.0f);
                    }
                }
            }
        }
        if ((i1 = this.mc.fontRendererObj.getStringWidth(s2)) > 157) {
            s2 = String.valueOf(this.mc.fontRendererObj.trimStringToWidth(s2, 157 - this.mc.fontRendererObj.getStringWidth("..."))) + "...";
        }
        this.mc.fontRendererObj.drawStringWithShadow(s2, x2 + 32 + 2, y2 + 1, 0xFFFFFF);
        List<String> list = this.mc.fontRendererObj.listFormattedStringToWidth(s1, 157);
        int l2 = 0;
        while (l2 < 2 && l2 < list.size()) {
            this.mc.fontRendererObj.drawStringWithShadow(list.get(l2), x2 + 32 + 2, y2 + 12 + 10 * l2, 0x808080);
            ++l2;
        }
    }

    protected abstract int func_183019_a();

    protected abstract String func_148311_a();

    protected abstract String func_148312_b();

    protected abstract void func_148313_c();

    protected boolean func_148310_d() {
        return true;
    }

    protected boolean func_148309_e() {
        return !this.resourcePacksGUI.hasResourcePackEntry(this);
    }

    protected boolean func_148308_f() {
        return this.resourcePacksGUI.hasResourcePackEntry(this);
    }

    protected boolean func_148314_g() {
        List<ResourcePackListEntry> list = this.resourcePacksGUI.getListContaining(this);
        int i2 = list.indexOf(this);
        return i2 > 0 && list.get(i2 - 1).func_148310_d();
    }

    protected boolean func_148307_h() {
        List<ResourcePackListEntry> list = this.resourcePacksGUI.getListContaining(this);
        int i2 = list.indexOf(this);
        return i2 >= 0 && i2 < list.size() - 1 && list.get(i2 + 1).func_148310_d();
    }

    @Override
    public boolean mousePressed(int slotIndex, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_, int p_148278_6_) {
        if (this.func_148310_d() && p_148278_5_ <= 32) {
            if (this.func_148309_e()) {
                this.resourcePacksGUI.markChanged();
                int j2 = this.func_183019_a();
                if (j2 != 1) {
                    String s1 = I18n.format("resourcePack.incompatible.confirm.title", new Object[0]);
                    String s2 = I18n.format("resourcePack.incompatible.confirm." + (j2 > 1 ? "new" : "old"), new Object[0]);
                    this.mc.displayGuiScreen(new GuiYesNo(new GuiYesNoCallback(){

                        @Override
                        public void confirmClicked(boolean result, int id2) {
                            List<ResourcePackListEntry> list2 = ResourcePackListEntry.this.resourcePacksGUI.getListContaining(ResourcePackListEntry.this);
                            ResourcePackListEntry.this.mc.displayGuiScreen(ResourcePackListEntry.this.resourcePacksGUI);
                            if (result) {
                                list2.remove(ResourcePackListEntry.this);
                                ResourcePackListEntry.this.resourcePacksGUI.getSelectedResourcePacks().add(0, ResourcePackListEntry.this);
                            }
                        }
                    }, s1, s2, 0));
                } else {
                    this.resourcePacksGUI.getListContaining(this).remove(this);
                    this.resourcePacksGUI.getSelectedResourcePacks().add(0, this);
                }
                return true;
            }
            if (p_148278_5_ < 16 && this.func_148308_f()) {
                this.resourcePacksGUI.getListContaining(this).remove(this);
                this.resourcePacksGUI.getAvailableResourcePacks().add(0, this);
                this.resourcePacksGUI.markChanged();
                return true;
            }
            if (p_148278_5_ > 16 && p_148278_6_ < 16 && this.func_148314_g()) {
                List<ResourcePackListEntry> list1 = this.resourcePacksGUI.getListContaining(this);
                int k2 = list1.indexOf(this);
                list1.remove(this);
                list1.add(k2 - 1, this);
                this.resourcePacksGUI.markChanged();
                return true;
            }
            if (p_148278_5_ > 16 && p_148278_6_ > 16 && this.func_148307_h()) {
                List<ResourcePackListEntry> list = this.resourcePacksGUI.getListContaining(this);
                int i2 = list.indexOf(this);
                list.remove(this);
                list.add(i2 + 1, this);
                this.resourcePacksGUI.markChanged();
                return true;
            }
        }
        return false;
    }

    @Override
    public void setSelected(int p_178011_1_, int p_178011_2_, int p_178011_3_) {
    }

    @Override
    public void mouseReleased(int slotIndex, int x2, int y2, int mouseEvent, int relativeX, int relativeY) {
    }
}

