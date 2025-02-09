// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.realms;

import net.minecraft.client.renderer.Tessellator;

public class Tezzelator
{
    public static Tessellator t;
    public static final Tezzelator instance;
    
    static {
        Tezzelator.t = Tessellator.getInstance();
        instance = new Tezzelator();
    }
    
    public void end() {
        Tezzelator.t.draw();
    }
    
    public Tezzelator vertex(final double p_vertex_1_, final double p_vertex_3_, final double p_vertex_5_) {
        Tezzelator.t.getBuffer().pos(p_vertex_1_, p_vertex_3_, p_vertex_5_);
        return this;
    }
    
    public void color(final float p_color_1_, final float p_color_2_, final float p_color_3_, final float p_color_4_) {
        Tezzelator.t.getBuffer().color(p_color_1_, p_color_2_, p_color_3_, p_color_4_);
    }
    
    public void tex2(final short p_tex2_1_, final short p_tex2_2_) {
        Tezzelator.t.getBuffer().lightmap(p_tex2_1_, p_tex2_2_);
    }
    
    public void normal(final float p_normal_1_, final float p_normal_2_, final float p_normal_3_) {
        Tezzelator.t.getBuffer().normal(p_normal_1_, p_normal_2_, p_normal_3_);
    }
    
    public void begin(final int p_begin_1_, final RealmsVertexFormat p_begin_2_) {
        Tezzelator.t.getBuffer().begin(p_begin_1_, p_begin_2_.getVertexFormat());
    }
    
    public void endVertex() {
        Tezzelator.t.getBuffer().endVertex();
    }
    
    public void offset(final double p_offset_1_, final double p_offset_3_, final double p_offset_5_) {
        Tezzelator.t.getBuffer().setTranslation(p_offset_1_, p_offset_3_, p_offset_5_);
    }
    
    public RealmsBufferBuilder color(final int p_color_1_, final int p_color_2_, final int p_color_3_, final int p_color_4_) {
        return new RealmsBufferBuilder(Tezzelator.t.getBuffer().color(p_color_1_, p_color_2_, p_color_3_, p_color_4_));
    }
    
    public Tezzelator tex(final double p_tex_1_, final double p_tex_3_) {
        Tezzelator.t.getBuffer().tex(p_tex_1_, p_tex_3_);
        return this;
    }
}
