// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class GuiButtonImage extends GuiButton
{
    private final ResourceLocation field_191750_o;
    private final int field_191747_p;
    private final int field_191748_q;
    private final int field_191749_r;
    
    public GuiButtonImage(final int p_i47392_1_, final int p_i47392_2_, final int p_i47392_3_, final int p_i47392_4_, final int p_i47392_5_, final int p_i47392_6_, final int p_i47392_7_, final int p_i47392_8_, final ResourceLocation p_i47392_9_) {
        super(p_i47392_1_, p_i47392_2_, p_i47392_3_, p_i47392_4_, p_i47392_5_, "");
        this.field_191747_p = p_i47392_6_;
        this.field_191748_q = p_i47392_7_;
        this.field_191749_r = p_i47392_8_;
        this.field_191750_o = p_i47392_9_;
    }
    
    public void func_191746_c(final int p_191746_1_, final int p_191746_2_) {
        this.xPosition = p_191746_1_;
        this.yPosition = p_191746_2_;
    }
    
    @Override
    public void drawButton(final Minecraft p_191745_1_, final int p_191745_2_, final int p_191745_3_, final float p_191745_4_) {
        if (this.visible) {
            this.hovered = (p_191745_2_ >= this.xPosition && p_191745_3_ >= this.yPosition && p_191745_2_ < this.xPosition + this.width && p_191745_3_ < this.yPosition + this.height);
            p_191745_1_.getTextureManager().bindTexture(this.field_191750_o);
            GlStateManager.disableDepth();
            final int i = this.field_191747_p;
            int j = this.field_191748_q;
            if (this.hovered) {
                j += this.field_191749_r;
            }
            this.drawTexturedModalRect(this.xPosition, this.yPosition, i, j, this.width, this.height);
            GlStateManager.enableDepth();
        }
    }
}
