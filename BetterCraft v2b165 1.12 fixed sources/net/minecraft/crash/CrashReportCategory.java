// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.crash;

import net.minecraft.block.state.IBlockState;
import net.minecraft.block.Block;
import java.util.Iterator;
import net.minecraft.util.math.BlockPos;
import com.google.common.collect.Lists;
import java.util.List;

public class CrashReportCategory
{
    private final CrashReport crashReport;
    private final String name;
    private final List<Entry> children;
    private StackTraceElement[] stackTrace;
    
    public CrashReportCategory(final CrashReport report, final String name) {
        this.children = (List<Entry>)Lists.newArrayList();
        this.stackTrace = new StackTraceElement[0];
        this.crashReport = report;
        this.name = name;
    }
    
    public static String getCoordinateInfo(final double x, final double y, final double z) {
        return String.format("%.2f,%.2f,%.2f - %s", x, y, z, getCoordinateInfo(new BlockPos(x, y, z)));
    }
    
    public static String getCoordinateInfo(final BlockPos pos) {
        return getCoordinateInfo(pos.getX(), pos.getY(), pos.getZ());
    }
    
    public static String getCoordinateInfo(final int x, final int y, final int z) {
        final StringBuilder stringbuilder = new StringBuilder();
        try {
            stringbuilder.append(String.format("World: (%d,%d,%d)", x, y, z));
        }
        catch (final Throwable var16) {
            stringbuilder.append("(Error finding world loc)");
        }
        stringbuilder.append(", ");
        try {
            final int i = x >> 4;
            final int j = z >> 4;
            final int k = x & 0xF;
            final int l = y >> 4;
            final int i2 = z & 0xF;
            final int j2 = i << 4;
            final int k2 = j << 4;
            final int l2 = (i + 1 << 4) - 1;
            final int i3 = (j + 1 << 4) - 1;
            stringbuilder.append(String.format("Chunk: (at %d,%d,%d in %d,%d; contains blocks %d,0,%d to %d,255,%d)", k, l, i2, i, j, j2, k2, l2, i3));
        }
        catch (final Throwable var17) {
            stringbuilder.append("(Error finding chunk loc)");
        }
        stringbuilder.append(", ");
        try {
            final int k3 = x >> 9;
            final int l3 = z >> 9;
            final int i4 = k3 << 5;
            final int j3 = l3 << 5;
            final int k4 = (k3 + 1 << 5) - 1;
            final int l4 = (l3 + 1 << 5) - 1;
            final int i5 = k3 << 9;
            final int j4 = l3 << 9;
            final int k5 = (k3 + 1 << 9) - 1;
            final int j5 = (l3 + 1 << 9) - 1;
            stringbuilder.append(String.format("Region: (%d,%d; contains chunks %d,%d to %d,%d, blocks %d,0,%d to %d,255,%d)", k3, l3, i4, j3, k4, l4, i5, j4, k5, j5));
        }
        catch (final Throwable var18) {
            stringbuilder.append("(Error finding world loc)");
        }
        return stringbuilder.toString();
    }
    
    public void setDetail(final String nameIn, final ICrashReportDetail<String> detail) {
        try {
            this.addCrashSection(nameIn, detail.call());
        }
        catch (final Throwable throwable) {
            this.addCrashSectionThrowable(nameIn, throwable);
        }
    }
    
    public void addCrashSection(final String sectionName, final Object value) {
        this.children.add(new Entry(sectionName, value));
    }
    
    public void addCrashSectionThrowable(final String sectionName, final Throwable throwable) {
        this.addCrashSection(sectionName, throwable);
    }
    
    public int getPrunedStackTrace(final int size) {
        final StackTraceElement[] astacktraceelement = Thread.currentThread().getStackTrace();
        if (astacktraceelement.length <= 0) {
            return 0;
        }
        System.arraycopy(astacktraceelement, 3 + size, this.stackTrace = new StackTraceElement[astacktraceelement.length - 3 - size], 0, this.stackTrace.length);
        return this.stackTrace.length;
    }
    
    public boolean firstTwoElementsOfStackTraceMatch(final StackTraceElement s1, final StackTraceElement s2) {
        if (this.stackTrace.length == 0 || s1 == null) {
            return false;
        }
        final StackTraceElement stacktraceelement = this.stackTrace[0];
        if (stacktraceelement.isNativeMethod() != s1.isNativeMethod() || !stacktraceelement.getClassName().equals(s1.getClassName()) || !stacktraceelement.getFileName().equals(s1.getFileName()) || !stacktraceelement.getMethodName().equals(s1.getMethodName())) {
            return false;
        }
        if (s2 != null != this.stackTrace.length > 1) {
            return false;
        }
        if (s2 != null && !this.stackTrace[1].equals(s2)) {
            return false;
        }
        this.stackTrace[0] = s1;
        return true;
    }
    
    public void trimStackTraceEntriesFromBottom(final int amount) {
        final StackTraceElement[] astacktraceelement = new StackTraceElement[this.stackTrace.length - amount];
        System.arraycopy(this.stackTrace, 0, astacktraceelement, 0, astacktraceelement.length);
        this.stackTrace = astacktraceelement;
    }
    
    public void appendToStringBuilder(final StringBuilder builder) {
        builder.append("-- ").append(this.name).append(" --\n");
        builder.append("Details:");
        for (final Entry crashreportcategory$entry : this.children) {
            builder.append("\n\t");
            builder.append(crashreportcategory$entry.getKey());
            builder.append(": ");
            builder.append(crashreportcategory$entry.getValue());
        }
        if (this.stackTrace != null && this.stackTrace.length > 0) {
            builder.append("\nStacktrace:");
            StackTraceElement[] stackTrace;
            for (int length = (stackTrace = this.stackTrace).length, i = 0; i < length; ++i) {
                final StackTraceElement stacktraceelement = stackTrace[i];
                builder.append("\n\tat ");
                builder.append(stacktraceelement);
            }
        }
    }
    
    public StackTraceElement[] getStackTrace() {
        return this.stackTrace;
    }
    
    public static void addBlockInfo(final CrashReportCategory category, final BlockPos pos, final Block blockIn, final int blockData) {
        final int i = Block.getIdFromBlock(blockIn);
        category.setDetail("Block type", new ICrashReportDetail<String>() {
            @Override
            public String call() throws Exception {
                try {
                    return String.format("ID #%d (%s // %s)", i, blockIn.getUnlocalizedName(), blockIn.getClass().getCanonicalName());
                }
                catch (final Throwable var2) {
                    return "ID #" + i;
                }
            }
        });
        category.setDetail("Block data value", new ICrashReportDetail<String>() {
            @Override
            public String call() throws Exception {
                if (blockData < 0) {
                    return "Unknown? (Got " + blockData + ")";
                }
                final String s = String.format("%4s", Integer.toBinaryString(blockData)).replace(" ", "0");
                return String.format("%1$d / 0x%1$X / 0b%2$s", blockData, s);
            }
        });
        category.setDetail("Block location", new ICrashReportDetail<String>() {
            @Override
            public String call() throws Exception {
                return CrashReportCategory.getCoordinateInfo(pos);
            }
        });
    }
    
    public static void addBlockInfo(final CrashReportCategory category, final BlockPos pos, final IBlockState state) {
        category.setDetail("Block", new ICrashReportDetail<String>() {
            @Override
            public String call() throws Exception {
                return state.toString();
            }
        });
        category.setDetail("Block location", new ICrashReportDetail<String>() {
            @Override
            public String call() throws Exception {
                return CrashReportCategory.getCoordinateInfo(pos);
            }
        });
    }
    
    static class Entry
    {
        private final String key;
        private final String value;
        
        public Entry(final String key, final Object value) {
            this.key = key;
            if (value == null) {
                this.value = "~~NULL~~";
            }
            else if (value instanceof Throwable) {
                final Throwable throwable = (Throwable)value;
                this.value = "~~ERROR~~ " + throwable.getClass().getSimpleName() + ": " + throwable.getMessage();
            }
            else {
                this.value = value.toString();
            }
        }
        
        public String getKey() {
            return this.key;
        }
        
        public String getValue() {
            return this.value;
        }
    }
}
