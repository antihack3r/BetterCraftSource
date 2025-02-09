/*
 * Decompiled with CFR 0.152.
 */
package org.spongepowered.asm.mixin.struct;

import java.util.LinkedHashMap;
import java.util.ListIterator;
import java.util.Map;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.util.Bytecode;

public class SourceMap {
    private static final String DEFAULT_STRATUM = "Mixin";
    private static final String NEWLINE = "\n";
    private final String sourceFile;
    private final Map<String, Stratum> strata = new LinkedHashMap<String, Stratum>();
    private int nextLineOffset = 1;
    private String defaultStratum = "Mixin";

    public SourceMap(String sourceFile) {
        this.sourceFile = sourceFile;
    }

    public String getSourceFile() {
        return this.sourceFile;
    }

    public String getPseudoGeneratedSourceFile() {
        return this.sourceFile.replace(".java", "$mixin.java");
    }

    public File addFile(ClassNode classNode) {
        return this.addFile(this.defaultStratum, classNode);
    }

    public File addFile(String stratumName, ClassNode classNode) {
        return this.addFile(stratumName, classNode.sourceFile, classNode.name + ".java", Bytecode.getMaxLineNumber(classNode, 500, 50));
    }

    public File addFile(String sourceFileName, String sourceFilePath, int size) {
        return this.addFile(this.defaultStratum, sourceFileName, sourceFilePath, size);
    }

    public File addFile(String stratumName, String sourceFileName, String sourceFilePath, int size) {
        Stratum stratum = this.strata.get(stratumName);
        if (stratum == null) {
            stratum = new Stratum(stratumName);
            this.strata.put(stratumName, stratum);
        }
        File file = stratum.addFile(this.nextLineOffset, size, sourceFileName, sourceFilePath);
        this.nextLineOffset += size;
        return file;
    }

    public String toString() {
        StringBuilder sb2 = new StringBuilder();
        this.appendTo(sb2);
        return sb2.toString();
    }

    private void appendTo(StringBuilder sb2) {
        sb2.append("SMAP").append(NEWLINE);
        sb2.append(this.getSourceFile()).append(NEWLINE);
        sb2.append(this.defaultStratum).append(NEWLINE);
        for (Stratum stratum : this.strata.values()) {
            stratum.appendTo(sb2);
        }
        sb2.append("*E").append(NEWLINE);
    }

    static class Stratum {
        private static final String STRATUM_MARK = "*S";
        private static final String FILE_MARK = "*F";
        private static final String LINES_MARK = "*L";
        public final String name;
        private final Map<String, File> files = new LinkedHashMap<String, File>();

        public Stratum(String name) {
            this.name = name;
        }

        public File addFile(int lineOffset, int size, String sourceFileName, String sourceFilePath) {
            File file = this.files.get(sourceFilePath);
            if (file == null) {
                file = new File(this.files.size() + 1, lineOffset, size, sourceFileName, sourceFilePath);
                this.files.put(sourceFilePath, file);
            }
            return file;
        }

        void appendTo(StringBuilder sb2) {
            sb2.append(STRATUM_MARK).append(" ").append(this.name).append(SourceMap.NEWLINE);
            sb2.append(FILE_MARK).append(SourceMap.NEWLINE);
            for (File file : this.files.values()) {
                file.appendFile(sb2);
            }
            sb2.append(LINES_MARK).append(SourceMap.NEWLINE);
            for (File file : this.files.values()) {
                file.appendLines(sb2);
            }
        }
    }

    public static class File {
        public final int id;
        public final int lineOffset;
        public final int size;
        public final String sourceFileName;
        public final String sourceFilePath;

        public File(int id2, int lineOffset, int size, String sourceFileName) {
            this(id2, lineOffset, size, sourceFileName, null);
        }

        public File(int id2, int lineOffset, int size, String sourceFileName, String sourceFilePath) {
            this.id = id2;
            this.lineOffset = lineOffset;
            this.size = size;
            this.sourceFileName = sourceFileName;
            this.sourceFilePath = sourceFilePath;
        }

        public void applyOffset(ClassNode classNode) {
            for (MethodNode method : classNode.methods) {
                this.applyOffset(method);
            }
        }

        public void applyOffset(MethodNode method) {
            ListIterator<AbstractInsnNode> iter = method.instructions.iterator();
            while (iter.hasNext()) {
                AbstractInsnNode node = (AbstractInsnNode)iter.next();
                if (!(node instanceof LineNumberNode)) continue;
                ((LineNumberNode)node).line += this.lineOffset - 1;
            }
        }

        void appendFile(StringBuilder sb2) {
            if (this.sourceFilePath != null) {
                sb2.append("+ ").append(this.id).append(" ").append(this.sourceFileName).append(SourceMap.NEWLINE);
                sb2.append(this.sourceFilePath).append(SourceMap.NEWLINE);
            } else {
                sb2.append(this.id).append(" ").append(this.sourceFileName).append(SourceMap.NEWLINE);
            }
        }

        public void appendLines(StringBuilder sb2) {
            sb2.append("1#").append(this.id).append(",").append(this.size).append(":").append(this.lineOffset).append(SourceMap.NEWLINE);
        }
    }
}

