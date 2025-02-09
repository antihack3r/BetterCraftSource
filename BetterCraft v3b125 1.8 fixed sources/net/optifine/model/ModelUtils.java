/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.model;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.src.Config;
import net.minecraft.util.EnumFacing;

public class ModelUtils {
    public static void dbgModel(IBakedModel model) {
        if (model != null) {
            Config.dbg("Model: " + model + ", ao: " + model.isAmbientOcclusion() + ", gui3d: " + model.isGui3d() + ", builtIn: " + model.isBuiltInRenderer() + ", particle: " + model.getParticleTexture());
            EnumFacing[] aenumfacing = EnumFacing.VALUES;
            int i2 = 0;
            while (i2 < aenumfacing.length) {
                EnumFacing enumfacing = aenumfacing[i2];
                List<BakedQuad> list = model.getFaceQuads(enumfacing);
                ModelUtils.dbgQuads(enumfacing.getName(), list, "  ");
                ++i2;
            }
            List<BakedQuad> list1 = model.getGeneralQuads();
            ModelUtils.dbgQuads("General", list1, "  ");
        }
    }

    private static void dbgQuads(String name, List quads, String prefix) {
        for (Object o2 : quads) {
            BakedQuad bakedquad = (BakedQuad)o2;
            ModelUtils.dbgQuad(name, bakedquad, prefix);
        }
    }

    public static void dbgQuad(String name, BakedQuad quad, String prefix) {
        Config.dbg(String.valueOf(prefix) + "Quad: " + quad.getClass().getName() + ", type: " + name + ", face: " + quad.getFace() + ", tint: " + quad.getTintIndex() + ", sprite: " + quad.getSprite());
        ModelUtils.dbgVertexData(quad.getVertexData(), "  " + prefix);
    }

    public static void dbgVertexData(int[] vd2, String prefix) {
        int i2 = vd2.length / 4;
        Config.dbg(String.valueOf(prefix) + "Length: " + vd2.length + ", step: " + i2);
        int j2 = 0;
        while (j2 < 4) {
            int k2 = j2 * i2;
            float f2 = Float.intBitsToFloat(vd2[k2 + 0]);
            float f1 = Float.intBitsToFloat(vd2[k2 + 1]);
            float f22 = Float.intBitsToFloat(vd2[k2 + 2]);
            int l2 = vd2[k2 + 3];
            float f3 = Float.intBitsToFloat(vd2[k2 + 4]);
            float f4 = Float.intBitsToFloat(vd2[k2 + 5]);
            Config.dbg(String.valueOf(prefix) + j2 + " xyz: " + f2 + "," + f1 + "," + f22 + " col: " + l2 + " u,v: " + f3 + "," + f4);
            ++j2;
        }
    }

    public static IBakedModel duplicateModel(IBakedModel model) {
        List list = ModelUtils.duplicateQuadList(model.getGeneralQuads());
        EnumFacing[] aenumfacing = EnumFacing.VALUES;
        ArrayList<List<BakedQuad>> list1 = new ArrayList<List<BakedQuad>>();
        int i2 = 0;
        while (i2 < aenumfacing.length) {
            EnumFacing enumfacing = aenumfacing[i2];
            List<BakedQuad> list2 = model.getFaceQuads(enumfacing);
            List list3 = ModelUtils.duplicateQuadList(list2);
            list1.add(list3);
            ++i2;
        }
        SimpleBakedModel simplebakedmodel = new SimpleBakedModel(list, list1, model.isAmbientOcclusion(), model.isGui3d(), model.getParticleTexture(), model.getItemCameraTransforms());
        return simplebakedmodel;
    }

    public static List duplicateQuadList(List lists) {
        ArrayList<BakedQuad> list = new ArrayList<BakedQuad>();
        for (Object o2 : lists) {
            BakedQuad bakedquad = (BakedQuad)o2;
            BakedQuad bakedquad1 = ModelUtils.duplicateQuad(bakedquad);
            list.add(bakedquad1);
        }
        return list;
    }

    public static BakedQuad duplicateQuad(BakedQuad quad) {
        BakedQuad bakedquad = new BakedQuad((int[])quad.getVertexData().clone(), quad.getTintIndex(), quad.getFace(), quad.getSprite());
        return bakedquad;
    }
}

