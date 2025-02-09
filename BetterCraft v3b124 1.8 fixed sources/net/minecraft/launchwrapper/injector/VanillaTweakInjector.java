/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.launchwrapper.injector;

import java.awt.Frame;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import javax.imageio.ImageIO;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;
import org.lwjgl.opengl.Display;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

public class VanillaTweakInjector
implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        if (!"net.minecraft.client.Minecraft".equals(name)) {
            return bytes;
        }
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 8);
        MethodNode mainMethod = null;
        for (MethodNode methodNode : classNode.methods) {
            if (!"main".equals(methodNode.name)) continue;
            mainMethod = methodNode;
            break;
        }
        if (mainMethod == null) {
            return bytes;
        }
        FieldNode workDirNode = null;
        for (FieldNode fieldNode : classNode.fields) {
            String fileTypeDescriptor = Type.getDescriptor(File.class);
            if (!fileTypeDescriptor.equals(fieldNode.desc) || (fieldNode.access & 8) != 8) continue;
            workDirNode = fieldNode;
            break;
        }
        MethodNode injectedMethod = new MethodNode();
        Label label = new Label();
        injectedMethod.visitLabel(label);
        injectedMethod.visitLineNumber(9001, label);
        injectedMethod.visitMethodInsn(184, "net/minecraft/launchwrapper/injector/VanillaTweakInjector", "inject", "()Ljava/io/File;");
        injectedMethod.visitFieldInsn(179, "net/minecraft/client/Minecraft", workDirNode.name, "Ljava/io/File;");
        mainMethod.instructions.insert(injectedMethod.instructions);
        ClassWriter writer = new ClassWriter(3);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    public static File inject() {
        System.out.println("Turning of ImageIO disk-caching");
        ImageIO.setUseCache(false);
        VanillaTweakInjector.loadIconsOnFrames();
        System.out.println("Setting gameDir to: " + Launch.minecraftHome);
        return Launch.minecraftHome;
    }

    public static void loadIconsOnFrames() {
        block5: {
            try {
                File smallIcon = new File(Launch.assetsDir, "icons/icon_16x16.png");
                File bigIcon = new File(Launch.assetsDir, "icons/icon_32x32.png");
                System.out.println("Loading current icons for window from: " + smallIcon + " and " + bigIcon);
                Display.setIcon(new ByteBuffer[]{VanillaTweakInjector.loadIcon(smallIcon), VanillaTweakInjector.loadIcon(bigIcon)});
                Frame[] frames = Frame.getFrames();
                if (frames == null) break block5;
                List<Image> icons = Arrays.asList(ImageIO.read(smallIcon), ImageIO.read(bigIcon));
                Frame[] frameArray = frames;
                int n2 = frames.length;
                int n3 = 0;
                while (n3 < n2) {
                    Frame frame = frameArray[n3];
                    try {
                        frame.setIconImages(icons);
                    }
                    catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                    ++n3;
                }
            }
            catch (IOException e2) {
                e2.printStackTrace();
            }
        }
    }

    private static ByteBuffer loadIcon(File iconFile) throws IOException {
        BufferedImage icon = ImageIO.read(iconFile);
        int[] rgb = icon.getRGB(0, 0, icon.getWidth(), icon.getHeight(), null, 0, icon.getWidth());
        ByteBuffer buffer = ByteBuffer.allocate(4 * rgb.length);
        int[] nArray = rgb;
        int n2 = rgb.length;
        int n3 = 0;
        while (n3 < n2) {
            int color = nArray[n3];
            buffer.putInt(color << 8 | color >> 24 & 0xFF);
            ++n3;
        }
        buffer.flip();
        return buffer;
    }
}

