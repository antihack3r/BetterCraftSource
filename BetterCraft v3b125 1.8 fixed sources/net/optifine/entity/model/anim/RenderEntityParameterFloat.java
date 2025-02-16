/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.entity.model.anim;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;
import net.optifine.expr.ExpressionType;
import net.optifine.expr.IExpressionFloat;

public enum RenderEntityParameterFloat implements IExpressionFloat
{
    LIMB_SWING("limb_swing"),
    LIMB_SWING_SPEED("limb_speed"),
    AGE("age"),
    HEAD_YAW("head_yaw"),
    HEAD_PITCH("head_pitch"),
    SCALE("scale"),
    HEALTH("health"),
    HURT_TIME("hurt_time"),
    IDLE_TIME("idle_time"),
    MAX_HEALTH("max_health"),
    MOVE_FORWARD("move_forward"),
    MOVE_STRAFING("move_strafing"),
    PARTIAL_TICKS("partial_ticks"),
    POS_X("pos_x"),
    POS_Y("pos_y"),
    POS_Z("pos_z"),
    REVENGE_TIME("revenge_time"),
    SWING_PROGRESS("swing_progress");

    private String name;
    private RenderManager renderManager;
    private static final RenderEntityParameterFloat[] VALUES;

    static {
        VALUES = RenderEntityParameterFloat.values();
    }

    private RenderEntityParameterFloat(String name) {
        this.name = name;
        this.renderManager = Minecraft.getMinecraft().getRenderManager();
    }

    public String getName() {
        return this.name;
    }

    @Override
    public ExpressionType getExpressionType() {
        return ExpressionType.FLOAT;
    }

    @Override
    public float eval() {
        Render render = this.renderManager.renderRender;
        if (render == null) {
            return 0.0f;
        }
        if (render instanceof RendererLivingEntity) {
            RendererLivingEntity rendererlivingentity = (RendererLivingEntity)render;
            switch (this) {
                case LIMB_SWING: {
                    return rendererlivingentity.renderLimbSwing;
                }
                case LIMB_SWING_SPEED: {
                    return rendererlivingentity.renderLimbSwingAmount;
                }
                case AGE: {
                    return rendererlivingentity.renderAgeInTicks;
                }
                case HEAD_YAW: {
                    return rendererlivingentity.renderHeadYaw;
                }
                case HEAD_PITCH: {
                    return rendererlivingentity.renderHeadPitch;
                }
                case SCALE: {
                    return rendererlivingentity.renderScaleFactor;
                }
            }
            EntityLivingBase entitylivingbase = rendererlivingentity.renderEntity;
            if (entitylivingbase == null) {
                return 0.0f;
            }
            switch (this) {
                case HEALTH: {
                    return entitylivingbase.getHealth();
                }
                case HURT_TIME: {
                    return entitylivingbase.hurtTime;
                }
                case IDLE_TIME: {
                    return entitylivingbase.getAge();
                }
                case MAX_HEALTH: {
                    return entitylivingbase.getMaxHealth();
                }
                case MOVE_FORWARD: {
                    return entitylivingbase.moveForward;
                }
                case MOVE_STRAFING: {
                    return entitylivingbase.moveStrafing;
                }
                case POS_X: {
                    return (float)entitylivingbase.posX;
                }
                case POS_Y: {
                    return (float)entitylivingbase.posY;
                }
                case POS_Z: {
                    return (float)entitylivingbase.posZ;
                }
                case REVENGE_TIME: {
                    return entitylivingbase.getRevengeTimer();
                }
                case SWING_PROGRESS: {
                    return entitylivingbase.getSwingProgress(rendererlivingentity.renderPartialTicks);
                }
            }
        }
        return 0.0f;
    }

    public static RenderEntityParameterFloat parse(String str) {
        if (str == null) {
            return null;
        }
        int i2 = 0;
        while (i2 < VALUES.length) {
            RenderEntityParameterFloat renderentityparameterfloat = VALUES[i2];
            if (renderentityparameterfloat.getName().equals(str)) {
                return renderentityparameterfloat;
            }
            ++i2;
        }
        return null;
    }
}

