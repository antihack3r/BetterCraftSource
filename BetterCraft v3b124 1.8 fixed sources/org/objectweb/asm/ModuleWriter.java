/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm;

import org.objectweb.asm.ByteVector;
import org.objectweb.asm.ModuleVisitor;
import org.objectweb.asm.SymbolTable;

final class ModuleWriter
extends ModuleVisitor {
    private final SymbolTable symbolTable;
    private final int moduleNameIndex;
    private final int moduleFlags;
    private final int moduleVersionIndex;
    private final ByteVector requires;
    private final ByteVector exports;
    private final ByteVector opens;
    private final ByteVector usesIndex;
    private final ByteVector provides;
    private final ByteVector packageIndex;
    private int requiresCount;
    private int exportsCount;
    private int opensCount;
    private int usesCount;
    private int providesCount;
    private int packageCount;
    private int mainClassIndex;

    ModuleWriter(SymbolTable symbolTable, int name, int access, int version) {
        super(458752);
        this.symbolTable = symbolTable;
        this.moduleNameIndex = name;
        this.moduleFlags = access;
        this.moduleVersionIndex = version;
        this.requires = new ByteVector();
        this.exports = new ByteVector();
        this.opens = new ByteVector();
        this.usesIndex = new ByteVector();
        this.provides = new ByteVector();
        this.packageIndex = new ByteVector();
    }

    @Override
    public void visitMainClass(String mainClass) {
        this.mainClassIndex = this.symbolTable.addConstantClass((String)mainClass).index;
    }

    @Override
    public void visitPackage(String packaze) {
        this.packageIndex.putShort(this.symbolTable.addConstantPackage((String)packaze).index);
        ++this.packageCount;
    }

    @Override
    public void visitRequire(String module, int access, String version) {
        this.requires.putShort(this.symbolTable.addConstantModule((String)module).index).putShort(access).putShort(version == null ? 0 : this.symbolTable.addConstantUtf8(version));
        ++this.requiresCount;
    }

    @Override
    public void visitExport(String packaze, int access, String ... modules) {
        this.exports.putShort(this.symbolTable.addConstantPackage((String)packaze).index).putShort(access);
        if (modules == null) {
            this.exports.putShort(0);
        } else {
            this.exports.putShort(modules.length);
            String[] var4 = modules;
            int var5 = modules.length;
            int var6 = 0;
            while (var6 < var5) {
                String module = var4[var6];
                this.exports.putShort(this.symbolTable.addConstantModule((String)module).index);
                ++var6;
            }
        }
        ++this.exportsCount;
    }

    @Override
    public void visitOpen(String packaze, int access, String ... modules) {
        this.opens.putShort(this.symbolTable.addConstantPackage((String)packaze).index).putShort(access);
        if (modules == null) {
            this.opens.putShort(0);
        } else {
            this.opens.putShort(modules.length);
            String[] var4 = modules;
            int var5 = modules.length;
            int var6 = 0;
            while (var6 < var5) {
                String module = var4[var6];
                this.opens.putShort(this.symbolTable.addConstantModule((String)module).index);
                ++var6;
            }
        }
        ++this.opensCount;
    }

    @Override
    public void visitUse(String service) {
        this.usesIndex.putShort(this.symbolTable.addConstantClass((String)service).index);
        ++this.usesCount;
    }

    @Override
    public void visitProvide(String service, String ... providers) {
        this.provides.putShort(this.symbolTable.addConstantClass((String)service).index);
        this.provides.putShort(providers.length);
        String[] var3 = providers;
        int var4 = providers.length;
        int var5 = 0;
        while (var5 < var4) {
            String provider = var3[var5];
            this.provides.putShort(this.symbolTable.addConstantClass((String)provider).index);
            ++var5;
        }
        ++this.providesCount;
    }

    @Override
    public void visitEnd() {
    }

    int getAttributeCount() {
        return 1 + (this.packageCount > 0 ? 1 : 0) + (this.mainClassIndex > 0 ? 1 : 0);
    }

    int computeAttributesSize() {
        this.symbolTable.addConstantUtf8("Module");
        int size = 22 + this.requires.length + this.exports.length + this.opens.length + this.usesIndex.length + this.provides.length;
        if (this.packageCount > 0) {
            this.symbolTable.addConstantUtf8("ModulePackages");
            size += 8 + this.packageIndex.length;
        }
        if (this.mainClassIndex > 0) {
            this.symbolTable.addConstantUtf8("ModuleMainClass");
            size += 8;
        }
        return size;
    }

    void putAttributes(ByteVector output) {
        int moduleAttributeLength = 16 + this.requires.length + this.exports.length + this.opens.length + this.usesIndex.length + this.provides.length;
        output.putShort(this.symbolTable.addConstantUtf8("Module")).putInt(moduleAttributeLength).putShort(this.moduleNameIndex).putShort(this.moduleFlags).putShort(this.moduleVersionIndex).putShort(this.requiresCount).putByteArray(this.requires.data, 0, this.requires.length).putShort(this.exportsCount).putByteArray(this.exports.data, 0, this.exports.length).putShort(this.opensCount).putByteArray(this.opens.data, 0, this.opens.length).putShort(this.usesCount).putByteArray(this.usesIndex.data, 0, this.usesIndex.length).putShort(this.providesCount).putByteArray(this.provides.data, 0, this.provides.length);
        if (this.packageCount > 0) {
            output.putShort(this.symbolTable.addConstantUtf8("ModulePackages")).putInt(2 + this.packageIndex.length).putShort(this.packageCount).putByteArray(this.packageIndex.data, 0, this.packageIndex.length);
        }
        if (this.mainClassIndex > 0) {
            output.putShort(this.symbolTable.addConstantUtf8("ModuleMainClass")).putInt(2).putShort(this.mainClassIndex);
        }
    }
}

