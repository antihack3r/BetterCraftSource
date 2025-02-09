// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.texture;

import java.awt.Graphics;
import net.minecraft.client.resources.IResource;
import java.io.IOException;
import java.io.Closeable;
import org.apache.commons.io.IOUtils;
import net.minecraft.util.math.MathHelper;
import java.awt.image.ImageObserver;
import java.awt.Image;
import java.awt.image.BufferedImage;
import net.minecraft.client.resources.IResourceManager;
import org.apache.logging.log4j.LogManager;
import net.minecraft.item.EnumDyeColor;
import java.util.List;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Logger;

public class LayeredColorMaskTexture extends AbstractTexture
{
    private static final Logger LOG;
    private final ResourceLocation textureLocation;
    private final List<String> listTextures;
    private final List<EnumDyeColor> listDyeColors;
    
    static {
        LOG = LogManager.getLogger();
    }
    
    public LayeredColorMaskTexture(final ResourceLocation textureLocationIn, final List<String> p_i46101_2_, final List<EnumDyeColor> p_i46101_3_) {
        this.textureLocation = textureLocationIn;
        this.listTextures = p_i46101_2_;
        this.listDyeColors = p_i46101_3_;
    }
    
    @Override
    public void loadTexture(final IResourceManager resourceManager) throws IOException {
        this.deleteGlTexture();
        IResource iresource = null;
        BufferedImage bufferedimage2 = null;
        Label_0407: {
            try {
                iresource = resourceManager.getResource(this.textureLocation);
                final BufferedImage bufferedimage1 = TextureUtil.readBufferedImage(iresource.getInputStream());
                int i = bufferedimage1.getType();
                if (i == 0) {
                    i = 6;
                }
                bufferedimage2 = new BufferedImage(bufferedimage1.getWidth(), bufferedimage1.getHeight(), i);
                final Graphics graphics = bufferedimage2.getGraphics();
                graphics.drawImage(bufferedimage1, 0, 0, null);
                for (int j = 0; j < 17 && j < this.listTextures.size() && j < this.listDyeColors.size(); ++j) {
                    IResource iresource2 = null;
                    try {
                        final String s = this.listTextures.get(j);
                        final int k = this.listDyeColors.get(j).func_193350_e();
                        if (s != null) {
                            iresource2 = resourceManager.getResource(new ResourceLocation(s));
                            final BufferedImage bufferedimage3 = TextureUtil.readBufferedImage(iresource2.getInputStream());
                            if (bufferedimage3.getWidth() == bufferedimage2.getWidth() && bufferedimage3.getHeight() == bufferedimage2.getHeight() && bufferedimage3.getType() == 6) {
                                for (int l = 0; l < bufferedimage3.getHeight(); ++l) {
                                    for (int i2 = 0; i2 < bufferedimage3.getWidth(); ++i2) {
                                        final int j2 = bufferedimage3.getRGB(i2, l);
                                        if ((j2 & 0xFF000000) != 0x0) {
                                            final int k2 = (j2 & 0xFF0000) << 8 & 0xFF000000;
                                            final int l2 = bufferedimage1.getRGB(i2, l);
                                            final int i3 = MathHelper.multiplyColor(l2, k) & 0xFFFFFF;
                                            bufferedimage3.setRGB(i2, l, k2 | i3);
                                        }
                                    }
                                }
                                bufferedimage2.getGraphics().drawImage(bufferedimage3, 0, 0, null);
                            }
                        }
                    }
                    finally {
                        IOUtils.closeQuietly(iresource2);
                    }
                    IOUtils.closeQuietly(iresource2);
                }
                break Label_0407;
            }
            catch (final IOException ioexception) {
                LayeredColorMaskTexture.LOG.error("Couldn't load layered image", ioexception);
            }
            finally {
                IOUtils.closeQuietly(iresource);
            }
            return;
        }
        TextureUtil.uploadTextureImage(this.getGlTextureId(), bufferedimage2);
    }
}
