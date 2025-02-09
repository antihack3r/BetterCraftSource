/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.misc.background;

import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import me.nzxtercode.bettercraft.client.Config;
import me.nzxtercode.bettercraft.client.misc.background.GuiColorPicker;
import me.nzxtercode.bettercraft.client.misc.background.ShaderBackgroundLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class GuiBackground
extends GuiScreen {
    private GuiScreen parent;
    public SlotList list;

    public GuiBackground(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, width - 107, height - 27, 100, 20, "Back"));
        this.buttonList.add(new GuiButton(1, 7, height - 27, 100, 20, String.format("%s", ShaderBackgroundLoader.getLoader().isEnabledShader() ? String.valueOf(EnumChatFormatting.GREEN.toString()) + "Shader" : String.valueOf(EnumChatFormatting.RED.toString()) + "Shader")));
        this.buttonList.add(new GuiButton(2, 5, 5, 75, 20, "Change BG"));
        this.buttonList.add(new GuiButton(3, width - 80, 5, 75, 20, "Custom BG"));
        this.buttonList.add(new GuiButton(4, 110, height - 27, 100, 20, String.format("%s", ShaderBackgroundLoader.getLoader().isPEnabledBackground() ? String.valueOf(EnumChatFormatting.GREEN.toString()) + "Pictures" : String.valueOf(EnumChatFormatting.RED.toString()) + "Pictures")));
        this.buttonList.add(new GuiButton(5, width / 2 - 38, 5, 75, 20, "Colorpicker"));
        this.list = new SlotList(this.mc, width, height, 32, height - 32, 10);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        this.list.actionPerformed(button);
        switch (button.id) {
            case 0: {
                this.mc.displayGuiScreen(this.parent);
                break;
            }
            case 1: {
                ShaderBackgroundLoader.getLoader().setEnabledShader(!ShaderBackgroundLoader.getLoader().isEnabledShader());
                button.displayString = String.format("%s", ShaderBackgroundLoader.getLoader().isEnabledShader() ? String.valueOf(EnumChatFormatting.GREEN.toString()) + "Shader" : String.valueOf(EnumChatFormatting.RED.toString()) + "Shader");
                break;
            }
            case 2: {
                ShaderBackgroundLoader.getLoader().setCEnabledBackground(false);
                Config.getInstance().editBackground("Background", json -> {
                    int index = Config.getInstance().getBackground("Background").get("id").getAsInt();
                    json.addProperty("id", index + 1 >= ShaderBackgroundLoader.getBackgrounds().size() ? 0 : index + 1);
                });
                break;
            }
            case 3: {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setAcceptAllFileFilterUsed(false);
                fileChooser.setFileFilter(new FileNameExtensionFilter("Images", "png", "jpg"));
                fileChooser.showOpenDialog(null);
                ShaderBackgroundLoader.getLoader().setCEnabledBackground(!ShaderBackgroundLoader.getLoader().isCEnabledBackground());
                Config.getInstance().editBackground("Background", json -> json.addProperty("custom", fileChooser.getSelectedFile().getPath()));
                ShaderBackgroundLoader.getLoader().getImageCache().clear();
                break;
            }
            case 4: {
                ShaderBackgroundLoader.getLoader().setPEnabledBackground(!ShaderBackgroundLoader.getLoader().isPEnabledBackground());
                button.displayString = String.format("%s", ShaderBackgroundLoader.getLoader().isPEnabledBackground() ? String.valueOf(EnumChatFormatting.GREEN.toString()) + "Pictures" : String.valueOf(EnumChatFormatting.RED.toString()) + "Pictures");
                break;
            }
            case 5: {
                this.mc.displayGuiScreen(new GuiColorPicker(this, null));
            }
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        this.list.handleMouseInput();
        super.handleMouseInput();
    }

    @Override
    public void drawScreen(int p_drawScreen_1_, int p_drawScreen_2_, float p_drawScreen_3_) {
        this.list.drawScreen(p_drawScreen_1_, p_drawScreen_2_, p_drawScreen_3_);
        GL11.glPushMatrix();
        GL11.glScalef(2.0f, 2.0f, 2.0f);
        GL11.glPopMatrix();
        super.drawScreen(p_drawScreen_1_, p_drawScreen_2_, p_drawScreen_3_);
    }

    class SlotList
    extends GuiSlot {
        public SlotList(Minecraft p_i1052_1_, int p_i1052_2_, int p_i1052_3_, int p_i1052_4_, int p_i1052_5_, int p_i1052_6_) {
            super(p_i1052_1_, p_i1052_2_, p_i1052_3_, p_i1052_4_, p_i1052_5_, p_i1052_6_);
        }

        @Override
        protected int getSize() {
            return ShaderBackgroundLoader.getLoader().getShaderList().size();
        }

        @Override
        protected void elementClicked(int i2, boolean b2, int i1, int i22) {
            if (ShaderBackgroundLoader.getLoader().isEnabledShader()) {
                ShaderBackgroundLoader.getLoader().setCurrentShaderId(i2);
            }
        }

        @Override
        protected boolean isSelected(int i2) {
            return false;
        }

        @Override
        protected void drawBackground() {
            GuiBackground.this.drawDefaultBackground();
        }

        @Override
        protected void drawSlot(int i2, int i1, int i22, int i3, int i4, int i5) {
            GuiBackground.drawCenteredString(this.mc.fontRendererObj, String.valueOf(!ShaderBackgroundLoader.getLoader().isEnabledShader() ? EnumChatFormatting.DARK_GRAY.toString() : (ShaderBackgroundLoader.getLoader().getCurrentShaderId() == i2 ? EnumChatFormatting.GREEN.toString() : EnumChatFormatting.RESET.toString())) + ShaderBackgroundLoader.getLoader().getShaderList().get(i2).getKey(), this.width / 2, i22 + 2, -1);
        }
    }
}

