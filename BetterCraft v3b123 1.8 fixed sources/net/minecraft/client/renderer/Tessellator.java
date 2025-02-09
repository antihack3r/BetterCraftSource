// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer;

import net.optifine.SmartAnimations;

public class Tessellator
{
    private WorldRenderer worldRenderer;
    private WorldVertexBufferUploader vboUploader;
    private static final Tessellator instance;
    
    static {
        instance = new Tessellator(2097152);
    }
    
    public static Tessellator getInstance() {
        return Tessellator.instance;
    }
    
    public Tessellator(final int bufferSize) {
        this.vboUploader = new WorldVertexBufferUploader();
        this.worldRenderer = new WorldRenderer(bufferSize);
    }
    
    public void draw() {
        if (this.worldRenderer.animatedSprites != null) {
            SmartAnimations.spritesRendered(this.worldRenderer.animatedSprites);
        }
        this.worldRenderer.finishDrawing();
        this.vboUploader.draw(this.worldRenderer);
    }
    
    public WorldRenderer getWorldRenderer() {
        return this.worldRenderer;
    }
}
