import java.util.Arrays;
import net.minecraft.client.main.Main;

// 
// Decompiled by Procyon v0.6.0
// 

public class Start
{
    public static void main(final String[] args) {
        Main.main(concat(new String[] { "--version", "mcp", "--accessToken", "0", "--assetsDir", "assets", "--assetIndex", "1.12", "--userProperties", "{}" }, args));
    }
    
    public static <T> T[] concat(final T[] first, final T[] second) {
        final Object[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return (T[])result;
    }
}
