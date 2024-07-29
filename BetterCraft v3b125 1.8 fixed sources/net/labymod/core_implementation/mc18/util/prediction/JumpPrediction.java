/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.core_implementation.mc18.util.prediction;

import java.util.List;
import net.labymod.core.LabyModCore;
import net.labymod.core_implementation.mc18.util.prediction.JumpVec2f;
import net.labymod.core_implementation.mc18.util.prediction.JumpVec3d;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

public class JumpPrediction {
    public static boolean isJumpPredicted(EntityPlayerSP player) {
        float moveX = (float)(player.posX - player.prevPosX);
        float moveZ = (float)(player.posZ - player.prevPosZ);
        if (player.onGround && !player.isSneaking() && !player.isRiding()) {
            WorldClient world = LabyModCore.getMinecraft().getWorld();
            JumpVec2f vec2f = new JumpVec2f(player.movementInput.moveStrafe, player.movementInput.moveForward);
            if (vec2f.x != 0.0f || vec2f.y != 0.0f) {
                IBlockState iblockstate2;
                BlockPos blockpos;
                IBlockState iblockstate;
                JumpVec3d vec3d = new JumpVec3d(player.posX, player.getEntityBoundingBox().minY, player.posZ);
                double d0 = player.posX + (double)moveX;
                double d2 = player.posZ + (double)moveZ;
                JumpVec3d vec3d2 = new JumpVec3d(d0, player.getEntityBoundingBox().minY, d2);
                JumpVec3d vec3d3 = new JumpVec3d(moveX, 0.0, moveZ);
                float f2 = player.getAIMoveSpeed();
                float f22 = (float)vec3d3.lengthSquared();
                if (f22 <= 0.001f) {
                    float f3 = f2 * vec2f.x;
                    float f4 = f2 * vec2f.y;
                    float f5 = MathHelper.sin(player.rotationYaw * ((float)Math.PI / 180));
                    float f6 = MathHelper.cos(player.rotationYaw * ((float)Math.PI / 180));
                    vec3d3 = new JumpVec3d(f3 * f6 - f4 * f5, vec3d3.y, f4 * f6 + f3 * f5);
                    f22 = (float)vec3d3.lengthSquared();
                    if (f22 <= 0.001f) {
                        return false;
                    }
                }
                float f7 = (float)JumpPrediction.fastInvSqrt(f22);
                JumpVec3d vec3d4 = JumpPrediction.scale(vec3d3, f7);
                JumpVec3d vec3d5 = JumpPrediction.getForward(player);
                float f8 = (float)(vec3d5.x * vec3d4.x + vec3d5.z * vec3d4.z);
                if (f8 >= -0.15f && (iblockstate = world.getBlockState(blockpos = new BlockPos(player.posX, player.getEntityBoundingBox().maxY, player.posZ))).getBlock().getCollisionBoundingBox(world, blockpos, iblockstate) == null && (iblockstate2 = world.getBlockState(blockpos = blockpos.up())).getBlock().getCollisionBoundingBox(world, blockpos, iblockstate2) == null) {
                    float f14;
                    float f9 = 1.2f;
                    if (player.isPotionActive(Potion.jump)) {
                        f9 += (float)(player.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.75f;
                    }
                    float f10 = Math.max(f2 * 7.0f, 1.0f / f7);
                    JumpVec3d vec3d6 = JumpPrediction.add(vec3d2, JumpPrediction.scale(vec3d4, f10));
                    float f11 = player.width;
                    float f12 = player.height;
                    AxisAlignedBB axisalignedbb = JumpPrediction.axisAlignedBB(vec3d, JumpPrediction.addVector(vec3d6, 0.0, f12, 0.0)).expand(f11, 0.0, f11);
                    JumpVec3d lvt_19_1_ = JumpPrediction.addVector(vec3d, 0.0, 0.51f, 0.0);
                    vec3d6 = JumpPrediction.addVector(vec3d6, 0.0, 0.51f, 0.0);
                    JumpVec3d vec3d7 = JumpPrediction.crossProduct(vec3d4, new JumpVec3d(0.0, 1.0, 0.0));
                    JumpVec3d vec3d8 = JumpPrediction.scale(vec3d7, f11 * 0.5f);
                    JumpVec3d vec3d9 = JumpPrediction.subtract(lvt_19_1_, vec3d8);
                    JumpVec3d vec3d10 = JumpPrediction.subtract(vec3d6, vec3d8);
                    JumpVec3d vec3d11 = JumpPrediction.add(lvt_19_1_, vec3d8);
                    JumpVec3d vec3d12 = JumpPrediction.add(vec3d6, vec3d8);
                    List<AxisAlignedBB> list = world.getCollidingBoundingBoxes(player, axisalignedbb);
                    list.isEmpty();
                    float f13 = Float.MIN_VALUE;
                    for (AxisAlignedBB axisalignedbb2 : list) {
                        if (!JumpPrediction.intersects(axisalignedbb2, vec3d9, vec3d10) && !JumpPrediction.intersects(axisalignedbb2, vec3d11, vec3d12)) continue;
                        f13 = (float)axisalignedbb2.maxY;
                        JumpVec3d vec3d13 = JumpPrediction.getCenter(axisalignedbb2);
                        BlockPos blockpos2 = new BlockPos(vec3d13.x, vec3d13.y, vec3d13.z);
                        int i2 = 1;
                        while ((float)i2 < f9) {
                            IBlockState iblockstate4;
                            BlockPos blockpos3 = blockpos2.up(i2);
                            IBlockState iblockstate3 = world.getBlockState(blockpos3);
                            AxisAlignedBB axisalignedbb3 = iblockstate3.getBlock().getCollisionBoundingBox(world, blockpos3, iblockstate3);
                            if (axisalignedbb3 != null && (double)(f13 = (float)axisalignedbb3.maxY + (float)blockpos3.getY()) - player.getEntityBoundingBox().minY > (double)f9) {
                                return false;
                            }
                            if (i2 > 1 && (iblockstate4 = world.getBlockState(blockpos = blockpos.up())).getBlock().getCollisionBoundingBox(world, blockpos, iblockstate4) != null) {
                                return false;
                            }
                            ++i2;
                        }
                        break block0;
                    }
                    if (f13 != Float.MIN_VALUE && (f14 = (float)((double)f13 - player.getEntityBoundingBox().minY)) > 0.5f && f14 <= f9) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static JumpVec3d getCenter(AxisAlignedBB ax2) {
        return new JumpVec3d(ax2.minX + (ax2.maxX - ax2.minX) * 0.5, ax2.minY + (ax2.maxY - ax2.minY) * 0.5, ax2.minZ + (ax2.maxZ - ax2.minZ) * 0.5);
    }

    public static boolean intersects(AxisAlignedBB ax2, double x1, double y1, double z1, double x2, double y2, double z2) {
        return ax2.minX < x2 && ax2.maxX > x1 && ax2.minY < y2 && ax2.maxY > y1 && ax2.minZ < z2 && ax2.maxZ > z1;
    }

    public static boolean intersects(AxisAlignedBB ax2, JumpVec3d min, JumpVec3d max) {
        return JumpPrediction.intersects(ax2, Math.min(min.x, max.x), Math.min(min.y, max.y), Math.min(min.z, max.z), Math.max(min.x, max.x), Math.max(min.y, max.y), Math.max(min.z, max.z));
    }

    public static JumpVec3d subtract(JumpVec3d vec, JumpVec3d vec2) {
        return JumpPrediction.subtract(vec2, vec.x, vec.y, vec.z);
    }

    public static JumpVec3d subtract(JumpVec3d vec, double x2, double y2, double z2) {
        return JumpPrediction.addVector(vec, -x2, -y2, -z2);
    }

    public static JumpVec3d crossProduct(JumpVec3d vec, JumpVec3d vec2) {
        return new JumpVec3d(vec2.y * vec.z - vec2.z * vec.y, vec2.z * vec.x - vec2.x * vec.z, vec2.x * vec.y - vec2.y * vec.x);
    }

    private static AxisAlignedBB axisAlignedBB(JumpVec3d a2, JumpVec3d b2) {
        return new AxisAlignedBB(a2.x, a2.y, a2.z, b2.x, b2.y, b2.z);
    }

    public static JumpVec3d add(JumpVec3d vec1, JumpVec3d vec) {
        return JumpPrediction.addVector(vec1, vec.x, vec.y, vec.z);
    }

    public static JumpVec3d addVector(JumpVec3d vec, double x2, double y2, double z2) {
        return new JumpVec3d(vec.x + x2, vec.y + y2, vec.z + z2);
    }

    public static JumpVec2f getPitchYaw(EntityPlayerSP player) {
        return new JumpVec2f(player.rotationPitch, player.rotationYaw);
    }

    public static JumpVec3d getForward(EntityPlayerSP player) {
        return JumpPrediction.fromPitchYawVector(JumpPrediction.getPitchYaw(player));
    }

    public static JumpVec3d fromPitchYawVector(JumpVec2f p_189984_0_) {
        return JumpPrediction.fromPitchYaw(p_189984_0_.x, p_189984_0_.y);
    }

    public static JumpVec3d fromPitchYaw(float p_189986_0_, float p_189986_1_) {
        float f2 = MathHelper.cos(-p_189986_1_ * ((float)Math.PI / 180) - (float)Math.PI);
        float f22 = MathHelper.sin(-p_189986_1_ * ((float)Math.PI / 180) - (float)Math.PI);
        float f3 = -MathHelper.cos(-p_189986_0_ * ((float)Math.PI / 180));
        float f4 = MathHelper.sin(-p_189986_0_ * ((float)Math.PI / 180));
        return new JumpVec3d(f22 * f3, f4, f2 * f3);
    }

    public static JumpVec3d scale(JumpVec3d vec, double factor) {
        return new JumpVec3d(vec.x * factor, vec.y * factor, vec.z * factor);
    }

    public static double fastInvSqrt(double p_181161_0_) {
        double d0 = 0.5 * p_181161_0_;
        long i2 = Double.doubleToRawLongBits(p_181161_0_);
        i2 = 6910469410427058090L - (i2 >> 1);
        p_181161_0_ = Double.longBitsToDouble(i2);
        p_181161_0_ *= 1.5 - d0 * p_181161_0_ * p_181161_0_;
        return p_181161_0_;
    }
}

