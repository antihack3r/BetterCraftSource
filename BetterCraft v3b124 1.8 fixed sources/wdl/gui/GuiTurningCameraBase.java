/*
 * Decompiled with CFR 0.152.
 */
package wdl.gui;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import wdl.WDL;

public abstract class GuiTurningCameraBase
extends GuiScreen {
    private float yaw;
    private float yawNextTick;
    private int oldCameraMode;
    private boolean oldHideHud;
    private boolean oldShowDebug;
    private EntityPlayer.EnumChatVisibility oldChatVisibility;
    private EntityPlayerSP cam;
    private Entity oldRenderViewEntity;
    private boolean initializedCamera = false;
    private static final float ROTATION_SPEED = 1.0f;
    private static final float ROTATION_VARIANCE = 0.7f;

    @Override
    public void initGui() {
        if (!this.initializedCamera) {
            this.cam = new EntityPlayerSP(WDL.minecraft, WDL.worldClient, WDL.thePlayer.sendQueue, WDL.thePlayer.getStatFileWriter());
            this.cam.setLocationAndAngles(WDL.thePlayer.posX, WDL.thePlayer.posY - WDL.thePlayer.getYOffset(), WDL.thePlayer.posZ, WDL.thePlayer.rotationYaw, 0.0f);
            this.yaw = this.yawNextTick = WDL.thePlayer.rotationYaw;
            this.oldShowDebug = WDL.minecraft.gameSettings.showDebugProfilerChart;
            this.oldChatVisibility = WDL.minecraft.gameSettings.chatVisibility;
            WDL.minecraft.gameSettings.showDebugProfilerChart = false;
            WDL.minecraft.gameSettings.chatVisibility = EntityPlayer.EnumChatVisibility.HIDDEN;
            this.oldRenderViewEntity = WDL.minecraft.getRenderViewEntity();
            this.initializedCamera = true;
        }
        WDL.minecraft.setRenderViewEntity(this.cam);
    }

    @Override
    public void updateScreen() {
        this.yaw = this.yawNextTick;
        this.yawNextTick = this.yaw + 1.0f * (float)(1.0 + (double)0.7f * Math.cos((double)(this.yaw + 45.0f) / 45.0 * Math.PI));
    }

    private double truncateDistanceIfBlockInWay(double camX, double camZ, double currentDistance) {
        Vec3 playerPos = WDL.thePlayer.getPositionVector().addVector(0.0, WDL.thePlayer.getEyeHeight(), 0.0);
        Vec3 offsetPos = new Vec3(WDL.thePlayer.posX - currentDistance * camX, WDL.thePlayer.posY + (double)WDL.thePlayer.getEyeHeight(), WDL.thePlayer.posZ + camZ);
        int i2 = 0;
        while (i2 < 9) {
            double distance;
            Vec3 to2;
            Vec3 from;
            MovingObjectPosition pos;
            float offsetZ;
            float offsetX = (i2 & 1) != 0 ? -0.1f : 0.1f;
            float offsetY = (i2 & 2) != 0 ? -0.1f : 0.1f;
            float f2 = offsetZ = (i2 & 4) != 0 ? -0.1f : 0.1f;
            if (i2 == 8) {
                offsetX = 0.0f;
                offsetY = 0.0f;
                offsetZ = 0.0f;
            }
            if ((pos = this.mc.theWorld.rayTraceBlocks(from = playerPos.addVector(offsetX, offsetY, offsetZ), to2 = offsetPos.addVector(offsetX, offsetY, offsetZ))) != null && (distance = pos.hitVec.distanceTo(playerPos)) < currentDistance && distance > 0.0) {
                currentDistance = distance;
            }
            ++i2;
        }
        return currentDistance - 0.25;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (this.cam != null) {
            float yaw = this.yaw + (this.yawNextTick - this.yaw) * partialTicks;
            this.cam.rotationPitch = 0.0f;
            this.cam.prevRotationPitch = 0.0f;
            this.cam.prevRotationYaw = this.cam.rotationYaw = yaw;
            double x2 = Math.cos((double)yaw / 180.0 * Math.PI);
            double z2 = Math.sin((double)(yaw - 90.0f) / 180.0 * Math.PI);
            double distance = this.truncateDistanceIfBlockInWay(x2, z2, 0.5);
            this.cam.prevPosY = this.cam.posY = WDL.thePlayer.posY;
            this.cam.lastTickPosY = this.cam.posY;
            this.cam.prevPosX = this.cam.posX = WDL.thePlayer.posX - distance * x2;
            this.cam.lastTickPosX = this.cam.posX;
            this.cam.prevPosZ = this.cam.posZ = WDL.thePlayer.posZ + distance * z2;
            this.cam.lastTickPosZ = this.cam.posZ;
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        WDL.minecraft.gameSettings.showDebugProfilerChart = this.oldShowDebug;
        WDL.minecraft.gameSettings.chatVisibility = this.oldChatVisibility;
        WDL.minecraft.setRenderViewEntity(this.oldRenderViewEntity);
    }
}

