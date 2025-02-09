// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui.spectator;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiSpectator;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentTranslation;
import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.client.gui.spectator.categories.SpectatorDetails;
import java.util.List;

public class SpectatorMenu
{
    private static final ISpectatorMenuObject CLOSE_ITEM;
    private static final ISpectatorMenuObject SCROLL_LEFT;
    private static final ISpectatorMenuObject SCROLL_RIGHT_ENABLED;
    private static final ISpectatorMenuObject SCROLL_RIGHT_DISABLED;
    public static final ISpectatorMenuObject EMPTY_SLOT;
    private final ISpectatorMenuRecipient listener;
    private final List<SpectatorDetails> previousCategories;
    private ISpectatorMenuView category;
    private int selectedSlot;
    private int page;
    
    static {
        CLOSE_ITEM = new EndSpectatorObject(null);
        SCROLL_LEFT = new MoveMenuObject(-1, true);
        SCROLL_RIGHT_ENABLED = new MoveMenuObject(1, true);
        SCROLL_RIGHT_DISABLED = new MoveMenuObject(1, false);
        EMPTY_SLOT = new ISpectatorMenuObject() {
            @Override
            public void selectItem(final SpectatorMenu menu) {
            }
            
            @Override
            public ITextComponent getSpectatorName() {
                return new TextComponentString("");
            }
            
            @Override
            public void renderIcon(final float p_178663_1_, final int alpha) {
            }
            
            @Override
            public boolean isEnabled() {
                return false;
            }
        };
    }
    
    public SpectatorMenu(final ISpectatorMenuRecipient p_i45497_1_) {
        this.previousCategories = (List<SpectatorDetails>)Lists.newArrayList();
        this.category = new BaseSpectatorGroup();
        this.selectedSlot = -1;
        this.listener = p_i45497_1_;
    }
    
    public ISpectatorMenuObject getItem(final int p_178643_1_) {
        final int i = p_178643_1_ + this.page * 6;
        if (this.page > 0 && p_178643_1_ == 0) {
            return SpectatorMenu.SCROLL_LEFT;
        }
        if (p_178643_1_ == 7) {
            return (i < this.category.getItems().size()) ? SpectatorMenu.SCROLL_RIGHT_ENABLED : SpectatorMenu.SCROLL_RIGHT_DISABLED;
        }
        if (p_178643_1_ == 8) {
            return SpectatorMenu.CLOSE_ITEM;
        }
        return (i >= 0 && i < this.category.getItems().size()) ? MoreObjects.firstNonNull(this.category.getItems().get(i), SpectatorMenu.EMPTY_SLOT) : SpectatorMenu.EMPTY_SLOT;
    }
    
    public List<ISpectatorMenuObject> getItems() {
        final List<ISpectatorMenuObject> list = (List<ISpectatorMenuObject>)Lists.newArrayList();
        for (int i = 0; i <= 8; ++i) {
            list.add(this.getItem(i));
        }
        return list;
    }
    
    public ISpectatorMenuObject getSelectedItem() {
        return this.getItem(this.selectedSlot);
    }
    
    public ISpectatorMenuView getSelectedCategory() {
        return this.category;
    }
    
    public void selectSlot(final int slotIn) {
        final ISpectatorMenuObject ispectatormenuobject = this.getItem(slotIn);
        if (ispectatormenuobject != SpectatorMenu.EMPTY_SLOT) {
            if (this.selectedSlot == slotIn && ispectatormenuobject.isEnabled()) {
                ispectatormenuobject.selectItem(this);
            }
            else {
                this.selectedSlot = slotIn;
            }
        }
    }
    
    public void exit() {
        this.listener.onSpectatorMenuClosed(this);
    }
    
    public int getSelectedSlot() {
        return this.selectedSlot;
    }
    
    public void selectCategory(final ISpectatorMenuView menuView) {
        this.previousCategories.add(this.getCurrentPage());
        this.category = menuView;
        this.selectedSlot = -1;
        this.page = 0;
    }
    
    public SpectatorDetails getCurrentPage() {
        return new SpectatorDetails(this.category, this.getItems(), this.selectedSlot);
    }
    
    static /* synthetic */ void access$1(final SpectatorMenu spectatorMenu, final int page) {
        spectatorMenu.page = page;
    }
    
    static class EndSpectatorObject implements ISpectatorMenuObject
    {
        private EndSpectatorObject() {
        }
        
        @Override
        public void selectItem(final SpectatorMenu menu) {
            menu.exit();
        }
        
        @Override
        public ITextComponent getSpectatorName() {
            return new TextComponentTranslation("spectatorMenu.close", new Object[0]);
        }
        
        @Override
        public void renderIcon(final float p_178663_1_, final int alpha) {
            Minecraft.getMinecraft().getTextureManager().bindTexture(GuiSpectator.SPECTATOR_WIDGETS);
            Gui.drawModalRectWithCustomSizedTexture(0, 0, 128.0f, 0.0f, 16, 16, 256.0f, 256.0f);
        }
        
        @Override
        public boolean isEnabled() {
            return true;
        }
    }
    
    static class MoveMenuObject implements ISpectatorMenuObject
    {
        private final int direction;
        private final boolean enabled;
        
        public MoveMenuObject(final int p_i45495_1_, final boolean p_i45495_2_) {
            this.direction = p_i45495_1_;
            this.enabled = p_i45495_2_;
        }
        
        @Override
        public void selectItem(final SpectatorMenu menu) {
            SpectatorMenu.access$1(menu, menu.page + this.direction);
        }
        
        @Override
        public ITextComponent getSpectatorName() {
            return (this.direction < 0) ? new TextComponentTranslation("spectatorMenu.previous_page", new Object[0]) : new TextComponentTranslation("spectatorMenu.next_page", new Object[0]);
        }
        
        @Override
        public void renderIcon(final float p_178663_1_, final int alpha) {
            Minecraft.getMinecraft().getTextureManager().bindTexture(GuiSpectator.SPECTATOR_WIDGETS);
            if (this.direction < 0) {
                Gui.drawModalRectWithCustomSizedTexture(0, 0, 144.0f, 0.0f, 16, 16, 256.0f, 256.0f);
            }
            else {
                Gui.drawModalRectWithCustomSizedTexture(0, 0, 160.0f, 0.0f, 16, 16, 256.0f, 256.0f);
            }
        }
        
        @Override
        public boolean isEnabled() {
            return this.enabled;
        }
    }
}
