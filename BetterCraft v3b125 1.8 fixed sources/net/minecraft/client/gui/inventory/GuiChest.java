/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.gui.inventory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;

public class GuiChest
extends GuiContainer {
    private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
    private static final ScheduledExecutorService CHEST_STEALER_SERVICE = Executors.newSingleThreadScheduledExecutor();
    private int speed;
    private IInventory upperChestInventory;
    private IInventory lowerChestInventory;
    private int inventoryRows;

    public GuiChest(IInventory upperInv, IInventory lowerInv) {
        super(new ContainerChest(upperInv, lowerInv, Minecraft.getMinecraft().thePlayer));
        this.upperChestInventory = upperInv;
        this.lowerChestInventory = lowerInv;
        this.allowUserInput = false;
        int i2 = 222;
        int j2 = i2 - 108;
        this.inventoryRows = lowerInv.getSizeInventory() / 9;
        this.ySize = j2 + this.inventoryRows * 18;
        this.speed = 10;
    }

    @Override
    public void initGui() {
        int x2 = (width - this.xSize) / 2;
        int y2 = (height - this.ySize) / 2;
        int x22 = (width - this.xSize) / 2 + this.xSize;
        int y22 = (height - this.ySize) / 2 + this.ySize;
        this.buttonList.add(new GuiButton(0, x22 - 52 - 25, y2 + 5, 25, 10, "Steal"));
        this.buttonList.add(new GuiButton(1, x22 - 25 - 25, y2 + 5, 25, 10, "Store"));
        this.buttonList.add(new GuiButton(2, x22 - 20, y2 + 5, 10, 10, String.valueOf(this.speed)));
        super.initGui();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        GuiButton button;
        if ((mouseButton == 0 || mouseButton == 1) && (button = (GuiButton)this.buttonList.get(2)).mousePressed(this.mc, mouseX, mouseY)) {
            button.playPressSound(this.mc.getSoundHandler());
            if (button.id == 2) {
                if (mouseButton == 0) {
                    if (this.speed < 20) {
                        ++this.speed;
                    }
                } else if (mouseButton == 1 && this.speed > 0) {
                    --this.speed;
                }
                button.setDisplayString(String.valueOf(this.speed));
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        GuiButton button = (GuiButton)this.buttonList.get(2);
        if (button.isMouseOver()) {
            this.drawHoveringText(Arrays.asList("Configurate pickup speed because anticheat", "", "The higher the number, the slower the speed"), mouseX, mouseY);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0 || button.id == 1) {
            BiConsumer<IInventory, Integer> function = (inventory, index) -> {
                Slot slot = this.inventorySlots.getSlotFromInventory((IInventory)inventory, (int)index);
                if (Objects.isNull(slot) || !slot.getHasStack()) {
                    return;
                }
                CHEST_STEALER_SERVICE.schedule(() -> this.mc.playerController.windowClick(this.inventorySlots.windowId, slot.slotNumber, 0, 1, this.mc.thePlayer), (long)(this.inventorySlots.inventorySlots.stream().filter(slotObj -> Objects.nonNull(slotObj) && slotObj.getHasStack()).collect(Collectors.toList()).indexOf(slot) * (this.speed * 10)), TimeUnit.MILLISECONDS);
            };
            IInventory inventory2 = button.id == 0 ? this.lowerChestInventory : (button.id == 1 ? this.upperChestInventory : null);
            IntStream.range(0, inventory2.getSizeInventory()).forEach(index -> function.accept(inventory2, index));
        }
        super.actionPerformed(button);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRendererObj.drawString(this.lowerChestInventory.getDisplayName().getUnformattedText(), 8, 6, 0x404040);
        this.fontRendererObj.drawString(this.upperChestInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.getTextureManager().bindTexture(CHEST_GUI_TEXTURE);
        int i2 = (width - this.xSize) / 2;
        int j2 = (height - this.ySize) / 2;
        this.drawTexturedModalRect(i2, j2, 0, 0, this.xSize, this.inventoryRows * 18 + 17);
        this.drawTexturedModalRect(i2, j2 + this.inventoryRows * 18 + 17, 0, 126, this.xSize, 96);
    }
}

