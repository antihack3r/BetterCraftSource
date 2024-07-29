/*
 * Decompiled with CFR 0.152.
 */
package lumien.chunkanimator.handler;

import java.util.WeakHashMap;
import lumien.chunkanimator.ChunkAnimator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3i;

public class AnimationHandler {
    WeakHashMap<RenderChunk, AnimationData> timeStamps = new WeakHashMap();

    public void preRender(RenderChunk renderChunk) {
        if (this.timeStamps.containsKey(renderChunk)) {
            int animationDuration;
            long timeDif;
            AnimationData animationData = this.timeStamps.get(renderChunk);
            long time = animationData.timeStamp;
            int mode = ChunkAnimator.getInstance().mode;
            if (time == -1L) {
                animationData.timeStamp = time = System.currentTimeMillis();
                if (mode == 4) {
                    int difZ;
                    EnumFacing chunkFacing;
                    BlockPos zeroedCenteredChunkPos;
                    BlockPos zeroedPlayerPosition = Minecraft.getMinecraft().thePlayer.getPosition();
                    BlockPos dif = (zeroedPlayerPosition = zeroedPlayerPosition.add(0, -zeroedPlayerPosition.getY(), 0)).subtract(zeroedCenteredChunkPos = renderChunk.getPosition().add(8, -renderChunk.getPosition().getY(), 8));
                    int difX = Math.abs(dif.getX());
                    animationData.chunkFacing = chunkFacing = difX > (difZ = Math.abs(dif.getZ())) ? (dif.getX() > 0 ? EnumFacing.EAST : EnumFacing.WEST) : (dif.getZ() > 0 ? EnumFacing.SOUTH : EnumFacing.NORTH);
                }
            }
            if ((timeDif = System.currentTimeMillis() - time) < (long)(animationDuration = ChunkAnimator.getInstance().animationDuration)) {
                double chunkY = renderChunk.getPosition().getY();
                if (mode == 2) {
                    int n2 = mode = chunkY < Minecraft.getMinecraft().theWorld.getHorizon() ? 0 : 1;
                }
                if (mode == 4) {
                    mode = 3;
                }
                switch (mode) {
                    case 0: {
                        double modY = chunkY / (double)animationDuration * (double)timeDif;
                        GlStateManager.translate(0.0, -chunkY + modY, 0.0);
                        break;
                    }
                    case 1: {
                        double modY = (256.0 - chunkY) / (double)animationDuration * (double)timeDif;
                        GlStateManager.translate(0.0, 256.0 - chunkY - modY, 0.0);
                        break;
                    }
                    case 3: {
                        EnumFacing chunkFacing = animationData.chunkFacing;
                        if (chunkFacing == null) break;
                        Vec3i vec = chunkFacing.getDirectionVec();
                        double mod = -(200.0 - 200.0 / (double)animationDuration * (double)timeDif);
                        GlStateManager.translate((double)vec.getX() * mod, 0.0, (double)vec.getZ() * mod);
                    }
                }
            } else {
                this.timeStamps.remove(renderChunk);
            }
        }
    }

    public void setPosition(RenderChunk renderChunk, BlockPos position) {
        if (Minecraft.getMinecraft().thePlayer != null) {
            boolean flag = true;
            BlockPos zeroedPlayerPosition = Minecraft.getMinecraft().thePlayer.getPosition();
            zeroedPlayerPosition = zeroedPlayerPosition.add(0, -zeroedPlayerPosition.getY(), 0);
            BlockPos zeroedCenteredChunkPos = position.add(8, -position.getY(), 8);
            if (ChunkAnimator.getInstance().disableAroundPlayer) {
                flag = zeroedPlayerPosition.distanceSq(zeroedCenteredChunkPos) > 4096.0;
                boolean bl2 = flag;
            }
            if (flag) {
                EnumFacing chunkFacing = null;
                if (ChunkAnimator.getInstance().mode == 3) {
                    int difZ;
                    BlockPos dif = zeroedPlayerPosition.subtract(zeroedCenteredChunkPos);
                    int difX = Math.abs(dif.getX());
                    chunkFacing = difX > (difZ = Math.abs(dif.getZ())) ? (dif.getX() > 0 ? EnumFacing.EAST : EnumFacing.WEST) : (dif.getZ() > 0 ? EnumFacing.SOUTH : EnumFacing.NORTH);
                }
                AnimationData animationData = new AnimationData(-1L, chunkFacing);
                this.timeStamps.put(renderChunk, animationData);
            }
        }
    }

    private class AnimationData {
        public long timeStamp;
        public EnumFacing chunkFacing;

        public AnimationData(long timeStamp, EnumFacing chunkFacing) {
            this.timeStamp = timeStamp;
            this.chunkFacing = chunkFacing;
        }
    }
}

