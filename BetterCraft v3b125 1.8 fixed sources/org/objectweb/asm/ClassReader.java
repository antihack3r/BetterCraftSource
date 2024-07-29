/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ConstantDynamic;
import org.objectweb.asm.Context;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.MethodWriter;
import org.objectweb.asm.ModuleVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.TypePath;

public class ClassReader {
    public static final int SKIP_CODE = 1;
    public static final int SKIP_DEBUG = 2;
    public static final int SKIP_FRAMES = 4;
    public static final int EXPAND_FRAMES = 8;
    static final int EXPAND_ASM_INSNS = 256;
    private static final int INPUT_STREAM_DATA_CHUNK_SIZE = 4096;
    public final byte[] b;
    public final int header;
    private final int[] cpInfoOffsets;
    private final String[] constantUtf8Values;
    private final ConstantDynamic[] constantDynamicValues;
    private final int[] bootstrapMethodOffsets;
    private final int maxStringLength;

    public ClassReader(byte[] classFile) {
        this(classFile, 0, classFile.length);
    }

    public ClassReader(byte[] classFileBuffer, int classFileOffset, int classFileLength) {
        this(classFileBuffer, classFileOffset, true);
    }

    ClassReader(byte[] classFileBuffer, int classFileOffset, boolean checkClassVersion) {
        this.b = classFileBuffer;
        if (checkClassVersion && this.readShort(classFileOffset + 6) > 56) {
            throw new IllegalArgumentException("Unsupported class file major version " + this.readShort(classFileOffset + 6));
        }
        int constantPoolCount = this.readUnsignedShort(classFileOffset + 8);
        this.cpInfoOffsets = new int[constantPoolCount];
        this.constantUtf8Values = new String[constantPoolCount];
        int currentCpInfoIndex = 1;
        int currentCpInfoOffset = classFileOffset + 10;
        int currentMaxStringLength = 0;
        boolean hasConstantDynamic = false;
        boolean hasConstantInvokeDynamic = false;
        while (currentCpInfoIndex < constantPoolCount) {
            int cpInfoSize;
            this.cpInfoOffsets[currentCpInfoIndex++] = currentCpInfoOffset + 1;
            switch (classFileBuffer[currentCpInfoOffset]) {
                case 1: {
                    cpInfoSize = 3 + this.readUnsignedShort(currentCpInfoOffset + 1);
                    if (cpInfoSize <= currentMaxStringLength) break;
                    currentMaxStringLength = cpInfoSize;
                    break;
                }
                default: {
                    throw new IllegalArgumentException();
                }
                case 3: 
                case 4: 
                case 9: 
                case 10: 
                case 11: 
                case 12: {
                    cpInfoSize = 5;
                    break;
                }
                case 5: 
                case 6: {
                    cpInfoSize = 9;
                    ++currentCpInfoIndex;
                    break;
                }
                case 7: 
                case 8: 
                case 16: 
                case 19: 
                case 20: {
                    cpInfoSize = 3;
                    break;
                }
                case 15: {
                    cpInfoSize = 4;
                    break;
                }
                case 17: {
                    cpInfoSize = 5;
                    hasConstantDynamic = true;
                    break;
                }
                case 18: {
                    cpInfoSize = 5;
                    hasConstantInvokeDynamic = true;
                }
            }
            currentCpInfoOffset += cpInfoSize;
        }
        this.maxStringLength = currentMaxStringLength;
        this.header = currentCpInfoOffset;
        this.constantDynamicValues = hasConstantDynamic ? new ConstantDynamic[constantPoolCount] : null;
        this.bootstrapMethodOffsets = hasConstantDynamic | hasConstantInvokeDynamic ? this.readBootstrapMethodsAttribute(currentMaxStringLength) : null;
    }

    public ClassReader(InputStream inputStream) throws IOException {
        this(ClassReader.readStream(inputStream, false));
    }

    public ClassReader(String className) throws IOException {
        this(ClassReader.readStream(ClassLoader.getSystemResourceAsStream(String.valueOf(className.replace('.', '/')) + ".class"), true));
    }

    private static byte[] readStream(InputStream inputStream, boolean close) throws IOException {
        if (inputStream == null) {
            throw new IOException("Class not found");
        }
        try {
            byte[] var5;
            int bytesRead;
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] data = new byte[4096];
            while ((bytesRead = inputStream.read(data, 0, data.length)) != -1) {
                outputStream.write(data, 0, bytesRead);
            }
            outputStream.flush();
            byte[] byArray = var5 = outputStream.toByteArray();
            return byArray;
        }
        finally {
            if (close) {
                inputStream.close();
            }
        }
    }

    public int getAccess() {
        return this.readUnsignedShort(this.header);
    }

    public String getClassName() {
        return this.readClass(this.header + 2, new char[this.maxStringLength]);
    }

    public String getSuperName() {
        return this.readClass(this.header + 4, new char[this.maxStringLength]);
    }

    public String[] getInterfaces() {
        int currentOffset = this.header + 6;
        int interfacesCount = this.readUnsignedShort(currentOffset);
        String[] interfaces = new String[interfacesCount];
        if (interfacesCount > 0) {
            char[] charBuffer = new char[this.maxStringLength];
            int i2 = 0;
            while (i2 < interfacesCount) {
                interfaces[i2] = this.readClass(currentOffset += 2, charBuffer);
                ++i2;
            }
        }
        return interfaces;
    }

    public void accept(ClassVisitor classVisitor, int parsingOptions) {
        this.accept(classVisitor, new Attribute[0], parsingOptions);
    }

    public void accept(ClassVisitor classVisitor, Attribute[] attributePrototypes, int parsingOptions) {
        Context context = new Context();
        context.attributePrototypes = attributePrototypes;
        context.parsingOptions = parsingOptions;
        char[] charBuffer = context.charBuffer = new char[this.maxStringLength];
        int currentOffset = this.header;
        int accessFlags = this.readUnsignedShort(currentOffset);
        String thisClass = this.readClass(currentOffset + 2, charBuffer);
        String superClass = this.readClass(currentOffset + 4, charBuffer);
        String[] interfaces = new String[this.readUnsignedShort(currentOffset + 6)];
        currentOffset += 8;
        int innerClassesOffset = 0;
        while (innerClassesOffset < interfaces.length) {
            interfaces[innerClassesOffset] = this.readClass(currentOffset, charBuffer);
            currentOffset += 2;
            ++innerClassesOffset;
        }
        innerClassesOffset = 0;
        int enclosingMethodOffset = 0;
        String signature = null;
        String sourceFile = null;
        String sourceDebugExtension = null;
        int runtimeVisibleAnnotationsOffset = 0;
        int runtimeInvisibleAnnotationsOffset = 0;
        int runtimeVisibleTypeAnnotationsOffset = 0;
        int runtimeInvisibleTypeAnnotationsOffset = 0;
        int moduleOffset = 0;
        int modulePackagesOffset = 0;
        String moduleMainClass = null;
        String nestHostClass = null;
        int nestMembersOffset = 0;
        Attribute attributes = null;
        int currentAttributeOffset = this.getFirstAttributeOffset();
        int fieldsCount = this.readUnsignedShort(currentAttributeOffset - 2);
        while (fieldsCount > 0) {
            String methodsCount = this.readUTF8(currentAttributeOffset, charBuffer);
            int annotationDescriptor = this.readInt(currentAttributeOffset + 2);
            currentAttributeOffset += 6;
            if ("SourceFile".equals(methodsCount)) {
                sourceFile = this.readUTF8(currentAttributeOffset, charBuffer);
            } else if ("InnerClasses".equals(methodsCount)) {
                innerClassesOffset = currentAttributeOffset;
            } else if ("EnclosingMethod".equals(methodsCount)) {
                enclosingMethodOffset = currentAttributeOffset;
            } else if ("NestHost".equals(methodsCount)) {
                nestHostClass = this.readClass(currentAttributeOffset, charBuffer);
            } else if ("NestMembers".equals(methodsCount)) {
                nestMembersOffset = currentAttributeOffset;
            } else if ("Signature".equals(methodsCount)) {
                signature = this.readUTF8(currentAttributeOffset, charBuffer);
            } else if ("RuntimeVisibleAnnotations".equals(methodsCount)) {
                runtimeVisibleAnnotationsOffset = currentAttributeOffset;
            } else if ("RuntimeVisibleTypeAnnotations".equals(methodsCount)) {
                runtimeVisibleTypeAnnotationsOffset = currentAttributeOffset;
            } else if ("Deprecated".equals(methodsCount)) {
                accessFlags |= 0x20000;
            } else if ("Synthetic".equals(methodsCount)) {
                accessFlags |= 0x1000;
            } else if ("SourceDebugExtension".equals(methodsCount)) {
                sourceDebugExtension = this.readUtf(currentAttributeOffset, annotationDescriptor, new char[annotationDescriptor]);
            } else if ("RuntimeInvisibleAnnotations".equals(methodsCount)) {
                runtimeInvisibleAnnotationsOffset = currentAttributeOffset;
            } else if ("RuntimeInvisibleTypeAnnotations".equals(methodsCount)) {
                runtimeInvisibleTypeAnnotationsOffset = currentAttributeOffset;
            } else if ("Module".equals(methodsCount)) {
                moduleOffset = currentAttributeOffset;
            } else if ("ModuleMainClass".equals(methodsCount)) {
                moduleMainClass = this.readClass(currentAttributeOffset, charBuffer);
            } else if ("ModulePackages".equals(methodsCount)) {
                modulePackagesOffset = currentAttributeOffset;
            } else if (!"BootstrapMethods".equals(methodsCount)) {
                Attribute type = this.readAttribute(attributePrototypes, methodsCount, currentAttributeOffset, annotationDescriptor, charBuffer, -1, null);
                type.nextAttribute = attributes;
                attributes = type;
            }
            currentAttributeOffset += annotationDescriptor;
            --fieldsCount;
        }
        classVisitor.visit(this.readInt(this.cpInfoOffsets[1] - 7), accessFlags, thisClass, signature, superClass, interfaces);
        if ((parsingOptions & 2) == 0 && (sourceFile != null || sourceDebugExtension != null)) {
            classVisitor.visitSource(sourceFile, sourceDebugExtension);
        }
        if (moduleOffset != 0) {
            this.readModuleAttributes(classVisitor, context, moduleOffset, modulePackagesOffset, moduleMainClass);
        }
        if (nestHostClass != null) {
            classVisitor.visitNestHost(nestHostClass);
        }
        if (enclosingMethodOffset != 0) {
            String var31 = this.readClass(enclosingMethodOffset, charBuffer);
            int var32 = this.readUnsignedShort(enclosingMethodOffset + 2);
            String var33 = var32 == 0 ? null : this.readUTF8(this.cpInfoOffsets[var32], charBuffer);
            String var34 = var32 == 0 ? null : this.readUTF8(this.cpInfoOffsets[var32] + 2, charBuffer);
            classVisitor.visitOuterClass(var31, var33, var34);
        }
        if (runtimeVisibleAnnotationsOffset != 0) {
            fieldsCount = this.readUnsignedShort(runtimeVisibleAnnotationsOffset);
            int var32 = runtimeVisibleAnnotationsOffset + 2;
            while (fieldsCount-- > 0) {
                String var33 = this.readUTF8(var32, charBuffer);
                var32 += 2;
                var32 = this.readElementValues(classVisitor.visitAnnotation(var33, true), var32, true, charBuffer);
            }
        }
        if (runtimeInvisibleAnnotationsOffset != 0) {
            fieldsCount = this.readUnsignedShort(runtimeInvisibleAnnotationsOffset);
            int var32 = runtimeInvisibleAnnotationsOffset + 2;
            while (fieldsCount-- > 0) {
                String var33 = this.readUTF8(var32, charBuffer);
                var32 += 2;
                var32 = this.readElementValues(classVisitor.visitAnnotation(var33, false), var32, true, charBuffer);
            }
        }
        if (runtimeVisibleTypeAnnotationsOffset != 0) {
            fieldsCount = this.readUnsignedShort(runtimeVisibleTypeAnnotationsOffset);
            int var32 = runtimeVisibleTypeAnnotationsOffset + 2;
            while (fieldsCount-- > 0) {
                var32 = this.readTypeAnnotationTarget(context, var32);
                String var33 = this.readUTF8(var32, charBuffer);
                var32 += 2;
                var32 = this.readElementValues(classVisitor.visitTypeAnnotation(context.currentTypeAnnotationTarget, context.currentTypeAnnotationTargetPath, var33, true), var32, true, charBuffer);
            }
        }
        if (runtimeInvisibleTypeAnnotationsOffset != 0) {
            fieldsCount = this.readUnsignedShort(runtimeInvisibleTypeAnnotationsOffset);
            int var32 = runtimeInvisibleTypeAnnotationsOffset + 2;
            while (fieldsCount-- > 0) {
                var32 = this.readTypeAnnotationTarget(context, var32);
                String var33 = this.readUTF8(var32, charBuffer);
                var32 += 2;
                var32 = this.readElementValues(classVisitor.visitTypeAnnotation(context.currentTypeAnnotationTarget, context.currentTypeAnnotationTargetPath, var33, false), var32, true, charBuffer);
            }
        }
        while (attributes != null) {
            Attribute var35 = attributes.nextAttribute;
            attributes.nextAttribute = null;
            classVisitor.visitAttribute(attributes);
            attributes = var35;
        }
        if (nestMembersOffset != 0) {
            fieldsCount = this.readUnsignedShort(nestMembersOffset);
            int var32 = nestMembersOffset + 2;
            while (fieldsCount-- > 0) {
                classVisitor.visitNestMember(this.readClass(var32, charBuffer));
                var32 += 2;
            }
        }
        if (innerClassesOffset != 0) {
            fieldsCount = this.readUnsignedShort(innerClassesOffset);
            int var32 = innerClassesOffset + 2;
            while (fieldsCount-- > 0) {
                classVisitor.visitInnerClass(this.readClass(var32, charBuffer), this.readClass(var32 + 2, charBuffer), this.readUTF8(var32 + 4, charBuffer), this.readUnsignedShort(var32 + 6));
                var32 += 8;
            }
        }
        fieldsCount = this.readUnsignedShort(currentOffset);
        currentOffset += 2;
        while (fieldsCount-- > 0) {
            currentOffset = this.readField(classVisitor, context, currentOffset);
        }
        int var32 = this.readUnsignedShort(currentOffset);
        currentOffset += 2;
        while (var32-- > 0) {
            currentOffset = this.readMethod(classVisitor, context, currentOffset);
        }
        classVisitor.visitEnd();
    }

    private void readModuleAttributes(ClassVisitor classVisitor, Context context, int moduleOffset, int modulePackagesOffset, String moduleMainClass) {
        char[] buffer = context.charBuffer;
        String moduleName = this.readModule(moduleOffset, buffer);
        int moduleFlags = this.readUnsignedShort(moduleOffset + 2);
        String moduleVersion = this.readUTF8(moduleOffset + 4, buffer);
        int currentOffset = moduleOffset + 6;
        ModuleVisitor moduleVisitor = classVisitor.visitModule(moduleName, moduleFlags, moduleVersion);
        if (moduleVisitor != null) {
            int providesWithCount;
            int var23;
            String usesCount;
            int opensCount;
            int exportsCount;
            int requiresCount;
            if (moduleMainClass != null) {
                moduleVisitor.visitMainClass(moduleMainClass);
            }
            if (modulePackagesOffset != 0) {
                requiresCount = this.readUnsignedShort(modulePackagesOffset);
                exportsCount = modulePackagesOffset + 2;
                while (requiresCount-- > 0) {
                    moduleVisitor.visitPackage(this.readPackage(exportsCount, buffer));
                    exportsCount += 2;
                }
            }
            requiresCount = this.readUnsignedShort(currentOffset);
            currentOffset += 2;
            while (requiresCount-- > 0) {
                String var21 = this.readModule(currentOffset, buffer);
                opensCount = this.readUnsignedShort(currentOffset + 2);
                usesCount = this.readUTF8(currentOffset + 4, buffer);
                currentOffset += 6;
                moduleVisitor.visitRequire(var21, opensCount, usesCount);
            }
            exportsCount = this.readUnsignedShort(currentOffset);
            currentOffset += 2;
            while (exportsCount-- > 0) {
                String var22 = this.readPackage(currentOffset, buffer);
                var23 = this.readUnsignedShort(currentOffset + 2);
                int providesCount = this.readUnsignedShort(currentOffset + 4);
                currentOffset += 6;
                String[] provides = null;
                if (providesCount != 0) {
                    provides = new String[providesCount];
                    providesWithCount = 0;
                    while (providesWithCount < providesCount) {
                        provides[providesWithCount] = this.readModule(currentOffset, buffer);
                        currentOffset += 2;
                        ++providesWithCount;
                    }
                }
                moduleVisitor.visitExport(var22, var23, provides);
            }
            opensCount = this.readUnsignedShort(currentOffset);
            currentOffset += 2;
            while (opensCount-- > 0) {
                usesCount = this.readPackage(currentOffset, buffer);
                int providesCount = this.readUnsignedShort(currentOffset + 2);
                int var24 = this.readUnsignedShort(currentOffset + 4);
                currentOffset += 6;
                String[] var26 = null;
                if (var24 != 0) {
                    var26 = new String[var24];
                    int providesWith = 0;
                    while (providesWith < var24) {
                        var26[providesWith] = this.readModule(currentOffset, buffer);
                        currentOffset += 2;
                        ++providesWith;
                    }
                }
                moduleVisitor.visitOpen(usesCount, providesCount, var26);
            }
            var23 = this.readUnsignedShort(currentOffset);
            currentOffset += 2;
            while (var23-- > 0) {
                moduleVisitor.visitUse(this.readClass(currentOffset, buffer));
                currentOffset += 2;
            }
            int providesCount = this.readUnsignedShort(currentOffset);
            currentOffset += 2;
            while (providesCount-- > 0) {
                String var25 = this.readClass(currentOffset, buffer);
                providesWithCount = this.readUnsignedShort(currentOffset + 2);
                currentOffset += 4;
                String[] var27 = new String[providesWithCount];
                int i2 = 0;
                while (i2 < providesWithCount) {
                    var27[i2] = this.readClass(currentOffset, buffer);
                    currentOffset += 2;
                    ++i2;
                }
                moduleVisitor.visitProvide(var25, var27);
            }
            moduleVisitor.visitEnd();
        }
    }

    private int readField(ClassVisitor classVisitor, Context context, int fieldInfoOffset) {
        String annotationDescriptor;
        int currentAnnotationOffset;
        int nextAttribute;
        char[] charBuffer = context.charBuffer;
        int accessFlags = this.readUnsignedShort(fieldInfoOffset);
        String name = this.readUTF8(fieldInfoOffset + 2, charBuffer);
        String descriptor = this.readUTF8(fieldInfoOffset + 4, charBuffer);
        int currentOffset = fieldInfoOffset + 6;
        Object constantValue = null;
        String signature = null;
        int runtimeVisibleAnnotationsOffset = 0;
        int runtimeInvisibleAnnotationsOffset = 0;
        int runtimeVisibleTypeAnnotationsOffset = 0;
        int runtimeInvisibleTypeAnnotationsOffset = 0;
        Attribute attributes = null;
        int attributesCount = this.readUnsignedShort(currentOffset);
        currentOffset += 2;
        while (attributesCount-- > 0) {
            String fieldVisitor = this.readUTF8(currentOffset, charBuffer);
            nextAttribute = this.readInt(currentOffset + 2);
            currentOffset += 6;
            if ("ConstantValue".equals(fieldVisitor)) {
                currentAnnotationOffset = this.readUnsignedShort(currentOffset);
                constantValue = currentAnnotationOffset == 0 ? null : this.readConst(currentAnnotationOffset, charBuffer);
            } else if ("Signature".equals(fieldVisitor)) {
                signature = this.readUTF8(currentOffset, charBuffer);
            } else if ("Deprecated".equals(fieldVisitor)) {
                accessFlags |= 0x20000;
            } else if ("Synthetic".equals(fieldVisitor)) {
                accessFlags |= 0x1000;
            } else if ("RuntimeVisibleAnnotations".equals(fieldVisitor)) {
                runtimeVisibleAnnotationsOffset = currentOffset;
            } else if ("RuntimeVisibleTypeAnnotations".equals(fieldVisitor)) {
                runtimeVisibleTypeAnnotationsOffset = currentOffset;
            } else if ("RuntimeInvisibleAnnotations".equals(fieldVisitor)) {
                runtimeInvisibleAnnotationsOffset = currentOffset;
            } else if ("RuntimeInvisibleTypeAnnotations".equals(fieldVisitor)) {
                runtimeInvisibleTypeAnnotationsOffset = currentOffset;
            } else {
                Attribute var22 = this.readAttribute(context.attributePrototypes, fieldVisitor, currentOffset, nextAttribute, charBuffer, -1, null);
                var22.nextAttribute = attributes;
                attributes = var22;
            }
            currentOffset += nextAttribute;
        }
        FieldVisitor var21 = classVisitor.visitField(accessFlags, name, descriptor, signature, constantValue);
        if (var21 == null) {
            return currentOffset;
        }
        if (runtimeVisibleAnnotationsOffset != 0) {
            nextAttribute = this.readUnsignedShort(runtimeVisibleAnnotationsOffset);
            currentAnnotationOffset = runtimeVisibleAnnotationsOffset + 2;
            while (nextAttribute-- > 0) {
                annotationDescriptor = this.readUTF8(currentAnnotationOffset, charBuffer);
                currentAnnotationOffset += 2;
                currentAnnotationOffset = this.readElementValues(var21.visitAnnotation(annotationDescriptor, true), currentAnnotationOffset, true, charBuffer);
            }
        }
        if (runtimeInvisibleAnnotationsOffset != 0) {
            nextAttribute = this.readUnsignedShort(runtimeInvisibleAnnotationsOffset);
            currentAnnotationOffset = runtimeInvisibleAnnotationsOffset + 2;
            while (nextAttribute-- > 0) {
                annotationDescriptor = this.readUTF8(currentAnnotationOffset, charBuffer);
                currentAnnotationOffset += 2;
                currentAnnotationOffset = this.readElementValues(var21.visitAnnotation(annotationDescriptor, false), currentAnnotationOffset, true, charBuffer);
            }
        }
        if (runtimeVisibleTypeAnnotationsOffset != 0) {
            nextAttribute = this.readUnsignedShort(runtimeVisibleTypeAnnotationsOffset);
            currentAnnotationOffset = runtimeVisibleTypeAnnotationsOffset + 2;
            while (nextAttribute-- > 0) {
                currentAnnotationOffset = this.readTypeAnnotationTarget(context, currentAnnotationOffset);
                annotationDescriptor = this.readUTF8(currentAnnotationOffset, charBuffer);
                currentAnnotationOffset += 2;
                currentAnnotationOffset = this.readElementValues(var21.visitTypeAnnotation(context.currentTypeAnnotationTarget, context.currentTypeAnnotationTargetPath, annotationDescriptor, true), currentAnnotationOffset, true, charBuffer);
            }
        }
        if (runtimeInvisibleTypeAnnotationsOffset != 0) {
            nextAttribute = this.readUnsignedShort(runtimeInvisibleTypeAnnotationsOffset);
            currentAnnotationOffset = runtimeInvisibleTypeAnnotationsOffset + 2;
            while (nextAttribute-- > 0) {
                currentAnnotationOffset = this.readTypeAnnotationTarget(context, currentAnnotationOffset);
                annotationDescriptor = this.readUTF8(currentAnnotationOffset, charBuffer);
                currentAnnotationOffset += 2;
                currentAnnotationOffset = this.readElementValues(var21.visitTypeAnnotation(context.currentTypeAnnotationTarget, context.currentTypeAnnotationTargetPath, annotationDescriptor, false), currentAnnotationOffset, true, charBuffer);
            }
        }
        while (attributes != null) {
            Attribute var23 = attributes.nextAttribute;
            attributes.nextAttribute = null;
            var21.visitAttribute(attributes);
            attributes = var23;
        }
        var21.visitEnd();
        return currentOffset;
    }

    private int readMethod(ClassVisitor classVisitor, Context context, int methodInfoOffset) {
        MethodWriter var26;
        int var27;
        int nextAttribute;
        char[] charBuffer = context.charBuffer;
        context.currentMethodAccessFlags = this.readUnsignedShort(methodInfoOffset);
        context.currentMethodName = this.readUTF8(methodInfoOffset + 2, charBuffer);
        context.currentMethodDescriptor = this.readUTF8(methodInfoOffset + 4, charBuffer);
        int currentOffset = methodInfoOffset + 6;
        int codeOffset = 0;
        int exceptionsOffset = 0;
        String[] exceptions = null;
        boolean synthetic = false;
        int signatureIndex = 0;
        int runtimeVisibleAnnotationsOffset = 0;
        int runtimeInvisibleAnnotationsOffset = 0;
        int runtimeVisibleParameterAnnotationsOffset = 0;
        int runtimeInvisibleParameterAnnotationsOffset = 0;
        int runtimeVisibleTypeAnnotationsOffset = 0;
        int runtimeInvisibleTypeAnnotationsOffset = 0;
        int annotationDefaultOffset = 0;
        int methodParametersOffset = 0;
        Attribute attributes = null;
        int attributesCount = this.readUnsignedShort(currentOffset);
        currentOffset += 2;
        while (attributesCount-- > 0) {
            String methodVisitor = this.readUTF8(currentOffset, charBuffer);
            nextAttribute = this.readInt(currentOffset + 2);
            currentOffset += 6;
            if ("Code".equals(methodVisitor)) {
                if ((context.parsingOptions & 1) == 0) {
                    codeOffset = currentOffset;
                }
            } else if ("Exceptions".equals(methodVisitor)) {
                exceptionsOffset = currentOffset;
                exceptions = new String[this.readUnsignedShort(currentOffset)];
                var27 = currentOffset + 2;
                int annotationDescriptor = 0;
                while (annotationDescriptor < exceptions.length) {
                    exceptions[annotationDescriptor] = this.readClass(var27, charBuffer);
                    var27 += 2;
                    ++annotationDescriptor;
                }
            } else if ("Signature".equals(methodVisitor)) {
                signatureIndex = this.readUnsignedShort(currentOffset);
            } else if ("Deprecated".equals(methodVisitor)) {
                context.currentMethodAccessFlags |= 0x20000;
            } else if ("RuntimeVisibleAnnotations".equals(methodVisitor)) {
                runtimeVisibleAnnotationsOffset = currentOffset;
            } else if ("RuntimeVisibleTypeAnnotations".equals(methodVisitor)) {
                runtimeVisibleTypeAnnotationsOffset = currentOffset;
            } else if ("AnnotationDefault".equals(methodVisitor)) {
                annotationDefaultOffset = currentOffset;
            } else if ("Synthetic".equals(methodVisitor)) {
                synthetic = true;
                context.currentMethodAccessFlags |= 0x1000;
            } else if ("RuntimeInvisibleAnnotations".equals(methodVisitor)) {
                runtimeInvisibleAnnotationsOffset = currentOffset;
            } else if ("RuntimeInvisibleTypeAnnotations".equals(methodVisitor)) {
                runtimeInvisibleTypeAnnotationsOffset = currentOffset;
            } else if ("RuntimeVisibleParameterAnnotations".equals(methodVisitor)) {
                runtimeVisibleParameterAnnotationsOffset = currentOffset;
            } else if ("RuntimeInvisibleParameterAnnotations".equals(methodVisitor)) {
                runtimeInvisibleParameterAnnotationsOffset = currentOffset;
            } else if ("MethodParameters".equals(methodVisitor)) {
                methodParametersOffset = currentOffset;
            } else {
                Attribute currentAnnotationOffset = this.readAttribute(context.attributePrototypes, methodVisitor, currentOffset, nextAttribute, charBuffer, -1, null);
                currentAnnotationOffset.nextAttribute = attributes;
                attributes = currentAnnotationOffset;
            }
            currentOffset += nextAttribute;
        }
        MethodVisitor var25 = classVisitor.visitMethod(context.currentMethodAccessFlags, context.currentMethodName, context.currentMethodDescriptor, signatureIndex == 0 ? null : this.readUtf(signatureIndex, charBuffer), exceptions);
        if (var25 == null) {
            return currentOffset;
        }
        if (var25 instanceof MethodWriter && (var26 = (MethodWriter)var25).canCopyMethodAttributes(this, methodInfoOffset, currentOffset - methodInfoOffset, synthetic, (context.currentMethodAccessFlags & 0x20000) != 0, this.readUnsignedShort(methodInfoOffset + 4), signatureIndex, exceptionsOffset)) {
            return currentOffset;
        }
        if (methodParametersOffset != 0) {
            nextAttribute = this.readByte(methodParametersOffset);
            var27 = methodParametersOffset + 1;
            while (nextAttribute-- > 0) {
                var25.visitParameter(this.readUTF8(var27, charBuffer), this.readUnsignedShort(var27 + 2));
                var27 += 4;
            }
        }
        if (annotationDefaultOffset != 0) {
            AnnotationVisitor var28 = var25.visitAnnotationDefault();
            this.readElementValue(var28, annotationDefaultOffset, null, charBuffer);
            if (var28 != null) {
                var28.visitEnd();
            }
        }
        if (runtimeVisibleAnnotationsOffset != 0) {
            nextAttribute = this.readUnsignedShort(runtimeVisibleAnnotationsOffset);
            var27 = runtimeVisibleAnnotationsOffset + 2;
            while (nextAttribute-- > 0) {
                String var29 = this.readUTF8(var27, charBuffer);
                var27 += 2;
                var27 = this.readElementValues(var25.visitAnnotation(var29, true), var27, true, charBuffer);
            }
        }
        if (runtimeInvisibleAnnotationsOffset != 0) {
            nextAttribute = this.readUnsignedShort(runtimeInvisibleAnnotationsOffset);
            var27 = runtimeInvisibleAnnotationsOffset + 2;
            while (nextAttribute-- > 0) {
                String var29 = this.readUTF8(var27, charBuffer);
                var27 += 2;
                var27 = this.readElementValues(var25.visitAnnotation(var29, false), var27, true, charBuffer);
            }
        }
        if (runtimeVisibleTypeAnnotationsOffset != 0) {
            nextAttribute = this.readUnsignedShort(runtimeVisibleTypeAnnotationsOffset);
            var27 = runtimeVisibleTypeAnnotationsOffset + 2;
            while (nextAttribute-- > 0) {
                var27 = this.readTypeAnnotationTarget(context, var27);
                String var29 = this.readUTF8(var27, charBuffer);
                var27 += 2;
                var27 = this.readElementValues(var25.visitTypeAnnotation(context.currentTypeAnnotationTarget, context.currentTypeAnnotationTargetPath, var29, true), var27, true, charBuffer);
            }
        }
        if (runtimeInvisibleTypeAnnotationsOffset != 0) {
            nextAttribute = this.readUnsignedShort(runtimeInvisibleTypeAnnotationsOffset);
            var27 = runtimeInvisibleTypeAnnotationsOffset + 2;
            while (nextAttribute-- > 0) {
                var27 = this.readTypeAnnotationTarget(context, var27);
                String var29 = this.readUTF8(var27, charBuffer);
                var27 += 2;
                var27 = this.readElementValues(var25.visitTypeAnnotation(context.currentTypeAnnotationTarget, context.currentTypeAnnotationTargetPath, var29, false), var27, true, charBuffer);
            }
        }
        if (runtimeVisibleParameterAnnotationsOffset != 0) {
            this.readParameterAnnotations(var25, context, runtimeVisibleParameterAnnotationsOffset, true);
        }
        if (runtimeInvisibleParameterAnnotationsOffset != 0) {
            this.readParameterAnnotations(var25, context, runtimeInvisibleParameterAnnotationsOffset, false);
        }
        while (attributes != null) {
            Attribute var30 = attributes.nextAttribute;
            attributes.nextAttribute = null;
            var25.visitAttribute(attributes);
            attributes = var30;
        }
        if (codeOffset != 0) {
            var25.visitCode();
            this.readCode(var25, context, codeOffset);
        }
        var25.visitEnd();
        return currentOffset;
    }

    /*
     * Unable to fully structure code
     */
    private void readCode(MethodVisitor methodVisitor, Context context, int codeOffset) {
        classFileBuffer = this.b;
        charBuffer = context.charBuffer;
        maxStack = this.readUnsignedShort(codeOffset);
        maxLocals = this.readUnsignedShort(codeOffset + 2);
        codeLength = this.readInt(codeOffset + 4);
        bytecodeStartOffset = currentOffset = codeOffset + 8;
        bytecodeEndOffset = currentOffset + codeLength;
        context.currentMethodLabels = new Label[codeLength + 1];
        labels = context.currentMethodLabels;
        block39: while (currentOffset < bytecodeEndOffset) {
            exceptionTableLength = currentOffset - bytecodeStartOffset;
            stackMapFrameOffset = classFileBuffer[currentOffset] & 255;
            block0 : switch (stackMapFrameOffset) {
                case 0: 
                case 1: 
                case 2: 
                case 3: 
                case 4: 
                case 5: 
                case 6: 
                case 7: 
                case 8: 
                case 9: 
                case 10: 
                case 11: 
                case 12: 
                case 13: 
                case 14: 
                case 15: 
                case 26: 
                case 27: 
                case 28: 
                case 29: 
                case 30: 
                case 31: 
                case 32: 
                case 33: 
                case 34: 
                case 35: 
                case 36: 
                case 37: 
                case 38: 
                case 39: 
                case 40: 
                case 41: 
                case 42: 
                case 43: 
                case 44: 
                case 45: 
                case 46: 
                case 47: 
                case 48: 
                case 49: 
                case 50: 
                case 51: 
                case 52: 
                case 53: 
                case 59: 
                case 60: 
                case 61: 
                case 62: 
                case 63: 
                case 64: 
                case 65: 
                case 66: 
                case 67: 
                case 68: 
                case 69: 
                case 70: 
                case 71: 
                case 72: 
                case 73: 
                case 74: 
                case 75: 
                case 76: 
                case 77: 
                case 78: 
                case 79: 
                case 80: 
                case 81: 
                case 82: 
                case 83: 
                case 84: 
                case 85: 
                case 86: 
                case 87: 
                case 88: 
                case 89: 
                case 90: 
                case 91: 
                case 92: 
                case 93: 
                case 94: 
                case 95: 
                case 96: 
                case 97: 
                case 98: 
                case 99: 
                case 100: 
                case 101: 
                case 102: 
                case 103: 
                case 104: 
                case 105: 
                case 106: 
                case 107: 
                case 108: 
                case 109: 
                case 110: 
                case 111: 
                case 112: 
                case 113: 
                case 114: 
                case 115: 
                case 116: 
                case 117: 
                case 118: 
                case 119: 
                case 120: 
                case 121: 
                case 122: 
                case 123: 
                case 124: 
                case 125: 
                case 126: 
                case 127: 
                case 128: 
                case 129: 
                case 130: 
                case 131: 
                case 133: 
                case 134: 
                case 135: 
                case 136: 
                case 137: 
                case 138: 
                case 139: 
                case 140: 
                case 141: 
                case 142: 
                case 143: 
                case 144: 
                case 145: 
                case 146: 
                case 147: 
                case 148: 
                case 149: 
                case 150: 
                case 151: 
                case 152: 
                case 172: 
                case 173: 
                case 174: 
                case 175: 
                case 176: 
                case 177: 
                case 190: 
                case 191: 
                case 194: 
                case 195: {
                    ++currentOffset;
                    break;
                }
                case 16: 
                case 18: 
                case 21: 
                case 22: 
                case 23: 
                case 24: 
                case 25: 
                case 54: 
                case 55: 
                case 56: 
                case 57: 
                case 58: 
                case 169: 
                case 188: {
                    currentOffset += 2;
                    break;
                }
                case 17: 
                case 19: 
                case 20: 
                case 132: 
                case 178: 
                case 179: 
                case 180: 
                case 181: 
                case 182: 
                case 183: 
                case 184: 
                case 187: 
                case 189: 
                case 192: 
                case 193: {
                    currentOffset += 3;
                    break;
                }
                case 153: 
                case 154: 
                case 155: 
                case 156: 
                case 157: 
                case 158: 
                case 159: 
                case 160: 
                case 161: 
                case 162: 
                case 163: 
                case 164: 
                case 165: 
                case 166: 
                case 167: 
                case 168: 
                case 198: 
                case 199: {
                    this.createLabel(exceptionTableLength + this.readShort(currentOffset + 1), labels);
                    currentOffset += 3;
                    break;
                }
                case 170: {
                    this.createLabel(exceptionTableLength + this.readInt(currentOffset += 4 - (exceptionTableLength & 3)), labels);
                    stackMapTableEndOffset = this.readInt(currentOffset + 8) - this.readInt(currentOffset + 4) + 1;
                    currentOffset += 12;
                    while (stackMapTableEndOffset-- > 0) {
                        this.createLabel(exceptionTableLength + this.readInt(currentOffset), labels);
                        currentOffset += 4;
                    }
                    continue block39;
                }
                case 171: {
                    this.createLabel(exceptionTableLength + this.readInt(currentOffset += 4 - (exceptionTableLength & 3)), labels);
                    compressedFrames = this.readInt(currentOffset + 4);
                    currentOffset += 8;
                    while (compressedFrames-- > 0) {
                        this.createLabel(exceptionTableLength + this.readInt(currentOffset + 4), labels);
                        currentOffset += 8;
                    }
                    continue block39;
                }
                case 185: 
                case 186: {
                    currentOffset += 5;
                    break;
                }
                case 196: {
                    switch (classFileBuffer[currentOffset + 1] & 255) {
                        case 21: 
                        case 22: 
                        case 23: 
                        case 24: 
                        case 25: 
                        case 54: 
                        case 55: 
                        case 56: 
                        case 57: 
                        case 58: 
                        case 169: {
                            currentOffset += 4;
                            break block0;
                        }
                        case 132: {
                            currentOffset += 6;
                            break block0;
                        }
                    }
                    throw new IllegalArgumentException();
                }
                case 197: {
                    currentOffset += 4;
                    break;
                }
                case 200: 
                case 201: 
                case 220: {
                    this.createLabel(exceptionTableLength + this.readInt(currentOffset + 1), labels);
                    currentOffset += 5;
                    break;
                }
                case 202: 
                case 203: 
                case 204: 
                case 205: 
                case 206: 
                case 207: 
                case 208: 
                case 209: 
                case 210: 
                case 211: 
                case 212: 
                case 213: 
                case 214: 
                case 215: 
                case 216: 
                case 217: 
                case 218: 
                case 219: {
                    this.createLabel(exceptionTableLength + this.readUnsignedShort(currentOffset + 1), labels);
                    currentOffset += 3;
                    break;
                }
                default: {
                    throw new IllegalArgumentException();
                }
            }
        }
        exceptionTableLength = this.readUnsignedShort(currentOffset);
        currentOffset += 2;
        while (exceptionTableLength-- > 0) {
            var41 = this.createLabel(this.readUnsignedShort(currentOffset), labels);
            var42 = this.createLabel(this.readUnsignedShort(currentOffset + 2), labels);
            var43 = this.createLabel(this.readUnsignedShort(currentOffset + 4), labels);
            localVariableTableOffset = this.readUTF8(this.cpInfoOffsets[this.readUnsignedShort(currentOffset + 6)], charBuffer);
            currentOffset += 8;
            methodVisitor.visitTryCatchBlock(var41, var42, var43, localVariableTableOffset);
        }
        stackMapFrameOffset = 0;
        stackMapTableEndOffset = 0;
        var44 = true;
        var45 = 0;
        localVariableTypeTableOffset = 0;
        visibleTypeAnnotationOffsets = null;
        invisibleTypeAnnotationOffsets = null;
        attributes = null;
        attributesCount = this.readUnsignedShort(currentOffset);
        currentOffset += 2;
        while (attributesCount-- > 0) {
            expandFrames = this.readUTF8(currentOffset, charBuffer);
            currentVisibleTypeAnnotationIndex = this.readInt(currentOffset + 2);
            currentOffset += 6;
            if ("LocalVariableTable".equals(expandFrames)) {
                if ((context.parsingOptions & 2) == 0) {
                    var45 = currentOffset;
                    currentInvisibleTypeAnnotationIndex = this.readUnsignedShort(currentOffset);
                    var47 = currentOffset + 2;
                    while (currentInvisibleTypeAnnotationIndex-- > 0) {
                        currentInvisibleTypeAnnotationBytecodeOffset = this.readUnsignedShort(var47);
                        this.createDebugLabel(currentInvisibleTypeAnnotationBytecodeOffset, labels);
                        insertFrame = this.readUnsignedShort(var47 + 2);
                        this.createDebugLabel(currentInvisibleTypeAnnotationBytecodeOffset + insertFrame, labels);
                        var47 += 10;
                    }
                }
            } else if ("LocalVariableTypeTable".equals(expandFrames)) {
                localVariableTypeTableOffset = currentOffset;
            } else if ("LineNumberTable".equals(expandFrames)) {
                if ((context.parsingOptions & 2) == 0) {
                    currentInvisibleTypeAnnotationIndex = this.readUnsignedShort(currentOffset);
                    var47 = currentOffset + 2;
                    while (currentInvisibleTypeAnnotationIndex-- > 0) {
                        currentInvisibleTypeAnnotationBytecodeOffset = this.readUnsignedShort(var47);
                        insertFrame = this.readUnsignedShort(var47 + 2);
                        var47 += 4;
                        this.createDebugLabel(currentInvisibleTypeAnnotationBytecodeOffset, labels);
                        labels[currentInvisibleTypeAnnotationBytecodeOffset].addLineNumber(insertFrame);
                    }
                }
            } else if ("RuntimeVisibleTypeAnnotations".equals(expandFrames)) {
                visibleTypeAnnotationOffsets = this.readTypeAnnotations(methodVisitor, context, currentOffset, true);
            } else if ("RuntimeInvisibleTypeAnnotations".equals(expandFrames)) {
                invisibleTypeAnnotationOffsets = this.readTypeAnnotations(methodVisitor, context, currentOffset, false);
            } else if ("StackMapTable".equals(expandFrames)) {
                if ((context.parsingOptions & 4) == 0) {
                    stackMapFrameOffset = currentOffset + 2;
                    stackMapTableEndOffset = currentOffset + currentVisibleTypeAnnotationIndex;
                }
            } else if ("StackMap".equals(expandFrames)) {
                if ((context.parsingOptions & 4) == 0) {
                    stackMapFrameOffset = currentOffset + 2;
                    stackMapTableEndOffset = currentOffset + currentVisibleTypeAnnotationIndex;
                    var44 = false;
                }
            } else {
                currentVisibleTypeAnnotationBytecodeOffset = this.readAttribute(context.attributePrototypes, expandFrames, currentOffset, currentVisibleTypeAnnotationIndex, charBuffer, codeOffset, labels);
                currentVisibleTypeAnnotationBytecodeOffset.nextAttribute = attributes;
                attributes = currentVisibleTypeAnnotationBytecodeOffset;
            }
            currentOffset += currentVisibleTypeAnnotationIndex;
        }
        v0 = var46 = (context.parsingOptions & 8) != 0;
        if (stackMapFrameOffset != 0) {
            context.currentFrameOffset = -1;
            context.currentFrameType = 0;
            context.currentFrameLocalCount = 0;
            context.currentFrameLocalCountDelta = 0;
            context.currentFrameLocalTypes = new Object[maxLocals];
            context.currentFrameStackCount = 0;
            context.currentFrameStackTypes = new Object[maxStack];
            if (var46) {
                this.computeImplicitFrame(context);
            }
            currentVisibleTypeAnnotationIndex = stackMapFrameOffset;
            while (currentVisibleTypeAnnotationIndex < stackMapTableEndOffset - 2) {
                if (classFileBuffer[currentVisibleTypeAnnotationIndex] == 8 && (var47 = this.readUnsignedShort(currentVisibleTypeAnnotationIndex + 1)) >= 0 && var47 < codeLength && (classFileBuffer[bytecodeStartOffset + var47] & 255) == 187) {
                    this.createLabel(var47, labels);
                }
                ++currentVisibleTypeAnnotationIndex;
            }
        }
        if (var46 && (context.parsingOptions & 256) != 0) {
            methodVisitor.visitFrame(-1, maxLocals, null, 0, null);
        }
        currentVisibleTypeAnnotationIndex = 0;
        var47 = this.getTypeAnnotationBytecodeOffset(visibleTypeAnnotationOffsets, 0);
        currentInvisibleTypeAnnotationIndex = 0;
        currentInvisibleTypeAnnotationBytecodeOffset = this.getTypeAnnotationBytecodeOffset(invisibleTypeAnnotationOffsets, 0);
        var48 = false;
        wideJumpOpcodeDelta = (context.parsingOptions & 256) == 0 ? 33 : 0;
        currentOffset = bytecodeStartOffset;
        while (currentOffset < bytecodeEndOffset) {
            nextAttribute = currentOffset - bytecodeStartOffset;
            localVariableTableLength = labels[nextAttribute];
            if (localVariableTableLength != null) {
                localVariableTableLength.accept(methodVisitor, (context.parsingOptions & 2) == 0);
            }
            while (stackMapFrameOffset != 0 && (context.currentFrameOffset == nextAttribute || context.currentFrameOffset == -1)) {
                if (context.currentFrameOffset != -1) {
                    if (var44 && !var46) {
                        methodVisitor.visitFrame(context.currentFrameType, context.currentFrameLocalCountDelta, context.currentFrameLocalTypes, context.currentFrameStackCount, context.currentFrameStackTypes);
                    } else {
                        methodVisitor.visitFrame(-1, context.currentFrameLocalCount, context.currentFrameLocalTypes, context.currentFrameStackCount, context.currentFrameStackTypes);
                    }
                    var48 = false;
                }
                stackMapFrameOffset = stackMapFrameOffset < stackMapTableEndOffset ? this.readStackMapFrame(stackMapFrameOffset, var44, var46, context) : 0;
            }
            if (var48) {
                if ((context.parsingOptions & 8) != 0) {
                    methodVisitor.visitFrame(256, 0, null, 0, null);
                }
                var48 = false;
            }
            startPc = classFileBuffer[currentOffset] & 255;
            switch (startPc) {
                case 0: 
                case 1: 
                case 2: 
                case 3: 
                case 4: 
                case 5: 
                case 6: 
                case 7: 
                case 8: 
                case 9: 
                case 10: 
                case 11: 
                case 12: 
                case 13: 
                case 14: 
                case 15: 
                case 46: 
                case 47: 
                case 48: 
                case 49: 
                case 50: 
                case 51: 
                case 52: 
                case 53: 
                case 79: 
                case 80: 
                case 81: 
                case 82: 
                case 83: 
                case 84: 
                case 85: 
                case 86: 
                case 87: 
                case 88: 
                case 89: 
                case 90: 
                case 91: 
                case 92: 
                case 93: 
                case 94: 
                case 95: 
                case 96: 
                case 97: 
                case 98: 
                case 99: 
                case 100: 
                case 101: 
                case 102: 
                case 103: 
                case 104: 
                case 105: 
                case 106: 
                case 107: 
                case 108: 
                case 109: 
                case 110: 
                case 111: 
                case 112: 
                case 113: 
                case 114: 
                case 115: 
                case 116: 
                case 117: 
                case 118: 
                case 119: 
                case 120: 
                case 121: 
                case 122: 
                case 123: 
                case 124: 
                case 125: 
                case 126: 
                case 127: 
                case 128: 
                case 129: 
                case 130: 
                case 131: 
                case 133: 
                case 134: 
                case 135: 
                case 136: 
                case 137: 
                case 138: 
                case 139: 
                case 140: 
                case 141: 
                case 142: 
                case 143: 
                case 144: 
                case 145: 
                case 146: 
                case 147: 
                case 148: 
                case 149: 
                case 150: 
                case 151: 
                case 152: 
                case 172: 
                case 173: 
                case 174: 
                case 175: 
                case 176: 
                case 177: 
                case 190: 
                case 191: 
                case 194: 
                case 195: {
                    methodVisitor.visitInsn(startPc);
                    ++currentOffset;
                    ** GOTO lbl347
                }
                case 16: 
                case 188: {
                    methodVisitor.visitIntInsn(startPc, classFileBuffer[currentOffset + 1]);
                    currentOffset += 2;
                    ** GOTO lbl347
                }
                case 17: {
                    methodVisitor.visitIntInsn(startPc, this.readShort(currentOffset + 1));
                    currentOffset += 3;
                    ** GOTO lbl347
                }
                case 18: {
                    methodVisitor.visitLdcInsn(this.readConst(classFileBuffer[currentOffset + 1] & 255, charBuffer));
                    currentOffset += 2;
                    ** GOTO lbl347
                }
                case 19: 
                case 20: {
                    methodVisitor.visitLdcInsn(this.readConst(this.readUnsignedShort(currentOffset + 1), charBuffer));
                    currentOffset += 3;
                    ** GOTO lbl347
                }
                case 21: 
                case 22: 
                case 23: 
                case 24: 
                case 25: 
                case 54: 
                case 55: 
                case 56: 
                case 57: 
                case 58: 
                case 169: {
                    methodVisitor.visitVarInsn(startPc, classFileBuffer[currentOffset + 1] & 255);
                    currentOffset += 2;
                    ** GOTO lbl347
                }
                case 26: 
                case 27: 
                case 28: 
                case 29: 
                case 30: 
                case 31: 
                case 32: 
                case 33: 
                case 34: 
                case 35: 
                case 36: 
                case 37: 
                case 38: 
                case 39: 
                case 40: 
                case 41: 
                case 42: 
                case 43: 
                case 44: 
                case 45: {
                    methodVisitor.visitVarInsn(21 + ((startPc -= 26) >> 2), startPc & 3);
                    ++currentOffset;
                    ** GOTO lbl347
                }
                case 59: 
                case 60: 
                case 61: 
                case 62: 
                case 63: 
                case 64: 
                case 65: 
                case 66: 
                case 67: 
                case 68: 
                case 69: 
                case 70: 
                case 71: 
                case 72: 
                case 73: 
                case 74: 
                case 75: 
                case 76: 
                case 77: 
                case 78: {
                    methodVisitor.visitVarInsn(54 + ((startPc -= 59) >> 2), startPc & 3);
                    ++currentOffset;
                    ** GOTO lbl347
                }
                case 132: {
                    methodVisitor.visitIincInsn(classFileBuffer[currentOffset + 1] & 255, classFileBuffer[currentOffset + 2]);
                    currentOffset += 3;
                    ** GOTO lbl347
                }
                case 153: 
                case 154: 
                case 155: 
                case 156: 
                case 157: 
                case 158: 
                case 159: 
                case 160: 
                case 161: 
                case 162: 
                case 163: 
                case 164: 
                case 165: 
                case 166: 
                case 167: 
                case 168: 
                case 198: 
                case 199: {
                    methodVisitor.visitJumpInsn(startPc, labels[nextAttribute + this.readShort(currentOffset + 1)]);
                    currentOffset += 3;
                    ** GOTO lbl347
                }
                case 170: {
                    typeAnnotationOffset = labels[nextAttribute + this.readInt(currentOffset += 4 - (nextAttribute & 3))];
                    var53 = this.readInt(currentOffset + 4);
                    var56 = this.readInt(currentOffset + 8);
                    currentOffset += 12;
                    var57 = new Label[var56 - var53 + 1];
                    signature = 0;
                    while (signature < var57.length) {
                        var57[signature] = labels[nextAttribute + this.readInt(currentOffset)];
                        currentOffset += 4;
                        ++signature;
                    }
                    methodVisitor.visitTableSwitchInsn(var53, var56, typeAnnotationOffset, var57);
                    ** GOTO lbl347
                }
                case 171: {
                    typeAnnotationOffset = labels[nextAttribute + this.readInt(currentOffset += 4 - (nextAttribute & 3))];
                    var53 = this.readInt(currentOffset + 4);
                    currentOffset += 8;
                    var54 = new int[var53];
                    var57 = new Label[var53];
                    signature = 0;
                    while (signature < var53) {
                        var54[signature] = this.readInt(currentOffset);
                        var57[signature] = labels[nextAttribute + this.readInt(currentOffset + 4)];
                        currentOffset += 8;
                        ++signature;
                    }
                    methodVisitor.visitLookupSwitchInsn(typeAnnotationOffset, var54, var57);
                    ** GOTO lbl347
                }
                case 178: 
                case 179: 
                case 180: 
                case 181: 
                case 182: 
                case 183: 
                case 184: 
                case 185: {
                    var52 = this.cpInfoOffsets[this.readUnsignedShort(currentOffset + 1)];
                    var53 = this.cpInfoOffsets[this.readUnsignedShort(var52 + 2)];
                    annotationDescriptor = this.readClass(var52, charBuffer);
                    index = this.readUTF8(var53, charBuffer);
                    var59 = this.readUTF8(var53 + 2, charBuffer);
                    if (startPc < 182) {
                        methodVisitor.visitFieldInsn(startPc, annotationDescriptor, index, var59);
                    } else {
                        var60 = classFileBuffer[var52 - 1] == 11;
                        methodVisitor.visitMethodInsn(startPc, annotationDescriptor, index, var59, var60);
                    }
                    currentOffset = startPc == 185 ? (currentOffset += 5) : (currentOffset += 3);
                    ** GOTO lbl347
                }
                case 186: {
                    var52 = this.cpInfoOffsets[this.readUnsignedShort(currentOffset + 1)];
                    var53 = this.cpInfoOffsets[this.readUnsignedShort(var52 + 2)];
                    annotationDescriptor = this.readUTF8(var53, charBuffer);
                    index = this.readUTF8(var53 + 2, charBuffer);
                    signature = this.bootstrapMethodOffsets[this.readUnsignedShort(var52)];
                    i = (Handle)this.readConst(this.readUnsignedShort(signature), charBuffer);
                    bootstrapMethodArguments = new Object[this.readUnsignedShort(signature + 2)];
                    signature += 4;
                    i1 = 0;
                    while (i1 < bootstrapMethodArguments.length) {
                        bootstrapMethodArguments[i1] = this.readConst(this.readUnsignedShort(signature), charBuffer);
                        signature += 2;
                        ++i1;
                    }
                    methodVisitor.visitInvokeDynamicInsn(annotationDescriptor, index, i, bootstrapMethodArguments);
                    currentOffset += 5;
                    ** GOTO lbl347
                }
                case 187: 
                case 189: 
                case 192: 
                case 193: {
                    methodVisitor.visitTypeInsn(startPc, this.readClass(currentOffset + 1, charBuffer));
                    currentOffset += 3;
                    ** GOTO lbl347
                }
                case 196: {
                    startPc = classFileBuffer[currentOffset + 1] & 255;
                    if (startPc == 132) {
                        methodVisitor.visitIincInsn(this.readUnsignedShort(currentOffset + 2), this.readShort(currentOffset + 4));
                        currentOffset += 6;
                    } else {
                        methodVisitor.visitVarInsn(startPc, this.readUnsignedShort(currentOffset + 2));
                        currentOffset += 4;
                    }
                    ** GOTO lbl347
                }
                case 197: {
                    methodVisitor.visitMultiANewArrayInsn(this.readClass(currentOffset + 1, charBuffer), classFileBuffer[currentOffset + 3] & 255);
                    currentOffset += 4;
                    ** GOTO lbl347
                }
                case 200: 
                case 201: {
                    methodVisitor.visitJumpInsn(startPc - wideJumpOpcodeDelta, labels[nextAttribute + this.readInt(currentOffset + 1)]);
                    currentOffset += 5;
                    ** GOTO lbl347
                }
                case 202: 
                case 203: 
                case 204: 
                case 205: 
                case 206: 
                case 207: 
                case 208: 
                case 209: 
                case 210: 
                case 211: 
                case 212: 
                case 213: 
                case 214: 
                case 215: 
                case 216: 
                case 217: 
                case 218: 
                case 219: {
                    startPc = startPc < 218 ? startPc - 49 : startPc - 20;
                    typeAnnotationOffset = labels[nextAttribute + this.readUnsignedShort(currentOffset + 1)];
                    if (startPc != 167 && startPc != 168) {
                        startPc = startPc < 167 ? (startPc + 1 ^ 1) - 1 : startPc ^ 1;
                        targetType = this.createLabel(nextAttribute + 3, labels);
                        methodVisitor.visitJumpInsn(startPc, targetType);
                        methodVisitor.visitJumpInsn(200, typeAnnotationOffset);
                        var48 = true;
                    } else {
                        methodVisitor.visitJumpInsn(startPc + 33, typeAnnotationOffset);
                    }
                    currentOffset += 3;
                    ** GOTO lbl347
                }
                case 220: {
                    methodVisitor.visitJumpInsn(200, labels[nextAttribute + this.readInt(currentOffset + 1)]);
                    var48 = true;
                    currentOffset += 5;
                    if (true) ** GOTO lbl347
                }
                default: {
                    throw new AssertionError();
                }
            }
            do {
                if (var47 == nextAttribute) {
                    var52 = this.readTypeAnnotationTarget(context, visibleTypeAnnotationOffsets[currentVisibleTypeAnnotationIndex]);
                    var55 = this.readUTF8(var52, charBuffer);
                    this.readElementValues(methodVisitor.visitInsnAnnotation(context.currentTypeAnnotationTarget, context.currentTypeAnnotationTargetPath, var55, true), var52 += 2, true, charBuffer);
                }
                var47 = this.getTypeAnnotationBytecodeOffset(visibleTypeAnnotationOffsets, ++currentVisibleTypeAnnotationIndex);
lbl347:
                // 22 sources

            } while (visibleTypeAnnotationOffsets != null && currentVisibleTypeAnnotationIndex < visibleTypeAnnotationOffsets.length && var47 <= nextAttribute);
            while (invisibleTypeAnnotationOffsets != null && currentInvisibleTypeAnnotationIndex < invisibleTypeAnnotationOffsets.length && currentInvisibleTypeAnnotationBytecodeOffset <= nextAttribute) {
                if (currentInvisibleTypeAnnotationBytecodeOffset == nextAttribute) {
                    var52 = this.readTypeAnnotationTarget(context, invisibleTypeAnnotationOffsets[currentInvisibleTypeAnnotationIndex]);
                    var55 = this.readUTF8(var52, charBuffer);
                    this.readElementValues(methodVisitor.visitInsnAnnotation(context.currentTypeAnnotationTarget, context.currentTypeAnnotationTargetPath, var55, false), var52 += 2, true, charBuffer);
                }
                currentInvisibleTypeAnnotationBytecodeOffset = this.getTypeAnnotationBytecodeOffset(invisibleTypeAnnotationOffsets, ++currentInvisibleTypeAnnotationIndex);
            }
        }
        if (labels[codeLength] != null) {
            methodVisitor.visitLabel(labels[codeLength]);
        }
        if (var45 != 0 && (context.parsingOptions & 2) == 0) {
            var49 = null;
            if (localVariableTypeTableOffset != 0) {
                var49 = new int[this.readUnsignedShort(localVariableTypeTableOffset) * 3];
                currentOffset = localVariableTypeTableOffset + 2;
                var50 = var49.length;
                while (var50 > 0) {
                    var49[--var50] = currentOffset + 6;
                    var49[--var50] = this.readUnsignedShort(currentOffset + 8);
                    var49[--var50] = this.readUnsignedShort(currentOffset);
                    currentOffset += 10;
                }
            }
            var50 = this.readUnsignedShort(var45);
            currentOffset = var45 + 2;
            while (var50-- > 0) {
                startPc = this.readUnsignedShort(currentOffset);
                var52 = this.readUnsignedShort(currentOffset + 2);
                var55 = this.readUTF8(currentOffset + 4, charBuffer);
                annotationDescriptor = this.readUTF8(currentOffset + 6, charBuffer);
                var58 = this.readUnsignedShort(currentOffset + 8);
                currentOffset += 10;
                var59 = null;
                if (var49 != null) {
                    var61 = 0;
                    while (var61 < var49.length) {
                        if (var49[var61] == startPc && var49[var61 + 1] == var58) {
                            var59 = this.readUTF8(var49[var61 + 2], charBuffer);
                            break;
                        }
                        var61 += 3;
                    }
                }
                methodVisitor.visitLocalVariable(var55, annotationDescriptor, var59, labels[startPc], labels[startPc + var52], var58);
            }
        }
        if (visibleTypeAnnotationOffsets != null) {
            var49 = visibleTypeAnnotationOffsets;
            var50 = visibleTypeAnnotationOffsets.length;
            startPc = 0;
            while (startPc < var50) {
                var52 = var49[startPc];
                var53 = this.readByte(var52);
                if (var53 == 64 || var53 == 65) {
                    currentOffset = this.readTypeAnnotationTarget(context, var52);
                    annotationDescriptor = this.readUTF8(currentOffset, charBuffer);
                    this.readElementValues(methodVisitor.visitLocalVariableAnnotation(context.currentTypeAnnotationTarget, context.currentTypeAnnotationTargetPath, context.currentLocalVariableAnnotationRangeStarts, context.currentLocalVariableAnnotationRangeEnds, context.currentLocalVariableAnnotationRangeIndices, annotationDescriptor, true), currentOffset += 2, true, charBuffer);
                }
                ++startPc;
            }
        }
        if (invisibleTypeAnnotationOffsets != null) {
            var49 = invisibleTypeAnnotationOffsets;
            var50 = invisibleTypeAnnotationOffsets.length;
            startPc = 0;
            while (startPc < var50) {
                var52 = var49[startPc];
                var53 = this.readByte(var52);
                if (var53 == 64 || var53 == 65) {
                    currentOffset = this.readTypeAnnotationTarget(context, var52);
                    annotationDescriptor = this.readUTF8(currentOffset, charBuffer);
                    this.readElementValues(methodVisitor.visitLocalVariableAnnotation(context.currentTypeAnnotationTarget, context.currentTypeAnnotationTargetPath, context.currentLocalVariableAnnotationRangeStarts, context.currentLocalVariableAnnotationRangeEnds, context.currentLocalVariableAnnotationRangeIndices, annotationDescriptor, false), currentOffset += 2, true, charBuffer);
                }
                ++startPc;
            }
        }
        while (attributes != null) {
            var51 = attributes.nextAttribute;
            attributes.nextAttribute = null;
            methodVisitor.visitAttribute(attributes);
            attributes = var51;
        }
        methodVisitor.visitMaxs(maxStack, maxLocals);
    }

    protected Label readLabel(int bytecodeOffset, Label[] labels) {
        if (labels[bytecodeOffset] == null) {
            labels[bytecodeOffset] = new Label();
        }
        return labels[bytecodeOffset];
    }

    private Label createLabel(int bytecodeOffset, Label[] labels) {
        Label label = this.readLabel(bytecodeOffset, labels);
        label.flags = (short)(label.flags & 0xFFFFFFFE);
        return label;
    }

    private void createDebugLabel(int bytecodeOffset, Label[] labels) {
        if (labels[bytecodeOffset] == null) {
            Label var10000 = this.readLabel(bytecodeOffset, labels);
            var10000.flags = (short)(var10000.flags | 1);
        }
    }

    private int[] readTypeAnnotations(MethodVisitor methodVisitor, Context context, int runtimeTypeAnnotationsOffset, boolean visible) {
        char[] charBuffer = context.charBuffer;
        int[] typeAnnotationsOffsets = new int[this.readUnsignedShort(runtimeTypeAnnotationsOffset)];
        int currentOffset = runtimeTypeAnnotationsOffset + 2;
        int i2 = 0;
        while (i2 < typeAnnotationsOffsets.length) {
            int pathLength;
            typeAnnotationsOffsets[i2] = currentOffset;
            int targetType = this.readInt(currentOffset);
            switch (targetType >>> 24) {
                default: {
                    throw new IllegalArgumentException();
                }
                case 16: 
                case 17: 
                case 18: 
                case 23: 
                case 66: 
                case 67: 
                case 68: 
                case 69: 
                case 70: {
                    currentOffset += 3;
                    break;
                }
                case 64: 
                case 65: {
                    pathLength = this.readUnsignedShort(currentOffset + 1);
                    currentOffset += 3;
                    while (pathLength-- > 0) {
                        int path = this.readUnsignedShort(currentOffset);
                        int annotationDescriptor = this.readUnsignedShort(currentOffset + 2);
                        currentOffset += 6;
                        this.createLabel(path, context.currentMethodLabels);
                        this.createLabel(path + annotationDescriptor, context.currentMethodLabels);
                    }
                    break;
                }
                case 71: 
                case 72: 
                case 73: 
                case 74: 
                case 75: {
                    currentOffset += 4;
                }
            }
            pathLength = this.readByte(currentOffset);
            if (targetType >>> 24 == 66) {
                TypePath var13 = pathLength == 0 ? null : new TypePath(this.b, currentOffset);
                String var14 = this.readUTF8(currentOffset += 1 + 2 * pathLength, charBuffer);
                currentOffset += 2;
                currentOffset = this.readElementValues(methodVisitor.visitTryCatchAnnotation(targetType & 0xFFFFFF00, var13, var14, visible), currentOffset, true, charBuffer);
            } else {
                currentOffset += 3 + 2 * pathLength;
                currentOffset = this.readElementValues(null, currentOffset, true, charBuffer);
            }
            ++i2;
        }
        return typeAnnotationsOffsets;
    }

    private int getTypeAnnotationBytecodeOffset(int[] typeAnnotationOffsets, int typeAnnotationIndex) {
        return typeAnnotationOffsets != null && typeAnnotationIndex < typeAnnotationOffsets.length && this.readByte(typeAnnotationOffsets[typeAnnotationIndex]) >= 67 ? this.readUnsignedShort(typeAnnotationOffsets[typeAnnotationIndex] + 1) : -1;
    }

    private int readTypeAnnotationTarget(Context context, int typeAnnotationOffset) {
        int pathLength;
        int currentOffset;
        int targetType = this.readInt(typeAnnotationOffset);
        switch (targetType >>> 24) {
            case 0: 
            case 1: 
            case 22: {
                targetType &= 0xFFFF0000;
                currentOffset = typeAnnotationOffset + 2;
                break;
            }
            default: {
                throw new IllegalArgumentException();
            }
            case 16: 
            case 17: 
            case 18: 
            case 23: 
            case 66: {
                targetType &= 0xFFFFFF00;
                currentOffset = typeAnnotationOffset + 3;
                break;
            }
            case 19: 
            case 20: 
            case 21: {
                targetType &= 0xFF000000;
                currentOffset = typeAnnotationOffset + 1;
                break;
            }
            case 64: 
            case 65: {
                targetType &= 0xFF000000;
                pathLength = this.readUnsignedShort(typeAnnotationOffset + 1);
                currentOffset = typeAnnotationOffset + 3;
                context.currentLocalVariableAnnotationRangeStarts = new Label[pathLength];
                context.currentLocalVariableAnnotationRangeEnds = new Label[pathLength];
                context.currentLocalVariableAnnotationRangeIndices = new int[pathLength];
                for (int i2 = 0; i2 < pathLength; ++i2) {
                    int startPc = this.readUnsignedShort(currentOffset);
                    int length = this.readUnsignedShort(currentOffset + 2);
                    int index = this.readUnsignedShort(currentOffset + 4);
                    currentOffset += 6;
                    context.currentLocalVariableAnnotationRangeStarts[i2] = this.createLabel(startPc, context.currentMethodLabels);
                    context.currentLocalVariableAnnotationRangeEnds[i2] = this.createLabel(startPc + length, context.currentMethodLabels);
                    context.currentLocalVariableAnnotationRangeIndices[i2] = index;
                }
                break;
            }
            case 67: 
            case 68: 
            case 69: 
            case 70: {
                targetType &= 0xFF000000;
                currentOffset = typeAnnotationOffset + 3;
                break;
            }
            case 71: 
            case 72: 
            case 73: 
            case 74: 
            case 75: {
                targetType &= 0xFF0000FF;
                currentOffset = typeAnnotationOffset + 4;
            }
        }
        context.currentTypeAnnotationTarget = targetType;
        pathLength = this.readByte(currentOffset);
        context.currentTypeAnnotationTargetPath = pathLength == 0 ? null : new TypePath(this.b, currentOffset);
        return currentOffset + 1 + 2 * pathLength;
    }

    private void readParameterAnnotations(MethodVisitor methodVisitor, Context context, int runtimeParameterAnnotationsOffset, boolean visible) {
        int currentOffset = runtimeParameterAnnotationsOffset + 1;
        int numParameters = this.b[runtimeParameterAnnotationsOffset] & 0xFF;
        methodVisitor.visitAnnotableParameterCount(numParameters, visible);
        char[] charBuffer = context.charBuffer;
        int i2 = 0;
        while (i2 < numParameters) {
            int numAnnotations = this.readUnsignedShort(currentOffset);
            currentOffset += 2;
            while (numAnnotations-- > 0) {
                String annotationDescriptor = this.readUTF8(currentOffset, charBuffer);
                currentOffset += 2;
                currentOffset = this.readElementValues(methodVisitor.visitParameterAnnotation(i2, annotationDescriptor, visible), currentOffset, true, charBuffer);
            }
            ++i2;
        }
    }

    /*
     * Unable to fully structure code
     */
    private int readElementValues(AnnotationVisitor annotationVisitor, int annotationOffset, boolean named, char[] charBuffer) {
        block3: {
            numElementValuePairs = this.readUnsignedShort(annotationOffset);
            currentOffset = annotationOffset + 2;
            if (!named) ** GOTO lbl10
            while (numElementValuePairs-- > 0) {
                elementName = this.readUTF8(currentOffset, charBuffer);
                currentOffset = this.readElementValue(annotationVisitor, currentOffset + 2, elementName, charBuffer);
            }
            break block3;
lbl-1000:
            // 1 sources

            {
                currentOffset = this.readElementValue(annotationVisitor, currentOffset, null, charBuffer);
lbl10:
                // 2 sources

                ** while (numElementValuePairs-- > 0)
            }
        }
        if (annotationVisitor != null) {
            annotationVisitor.visitEnd();
        }
        return currentOffset;
    }

    private int readElementValue(AnnotationVisitor annotationVisitor, int elementValueOffset, String elementName, char[] charBuffer) {
        if (annotationVisitor == null) {
            switch (this.b[elementValueOffset] & 0xFF) {
                case 64: {
                    return this.readElementValues(null, elementValueOffset + 3, true, charBuffer);
                }
                case 91: {
                    return this.readElementValues(null, elementValueOffset + 1, false, charBuffer);
                }
                case 101: {
                    return elementValueOffset + 5;
                }
            }
            return elementValueOffset + 3;
        }
        int currentOffset = elementValueOffset + 1;
        switch (this.b[elementValueOffset] & 0xFF) {
            case 64: {
                currentOffset = this.readElementValues(annotationVisitor.visitAnnotation(elementName, this.readUTF8(currentOffset, charBuffer)), currentOffset + 2, true, charBuffer);
                break;
            }
            default: {
                throw new IllegalArgumentException();
            }
            case 66: {
                annotationVisitor.visit(elementName, (byte)this.readInt(this.cpInfoOffsets[this.readUnsignedShort(currentOffset)]));
                currentOffset += 2;
                break;
            }
            case 67: {
                annotationVisitor.visit(elementName, Character.valueOf((char)this.readInt(this.cpInfoOffsets[this.readUnsignedShort(currentOffset)])));
                currentOffset += 2;
                break;
            }
            case 68: 
            case 70: 
            case 73: 
            case 74: {
                annotationVisitor.visit(elementName, this.readConst(this.readUnsignedShort(currentOffset), charBuffer));
                currentOffset += 2;
                break;
            }
            case 83: {
                annotationVisitor.visit(elementName, (short)this.readInt(this.cpInfoOffsets[this.readUnsignedShort(currentOffset)]));
                currentOffset += 2;
                break;
            }
            case 90: {
                annotationVisitor.visit(elementName, this.readInt(this.cpInfoOffsets[this.readUnsignedShort(currentOffset)]) == 0 ? Boolean.FALSE : Boolean.TRUE);
                currentOffset += 2;
                break;
            }
            case 91: {
                int numValues = this.readUnsignedShort(currentOffset);
                currentOffset += 2;
                if (numValues == 0) {
                    return this.readElementValues(annotationVisitor.visitArray(elementName), currentOffset - 2, false, charBuffer);
                }
                switch (this.b[currentOffset] & 0xFF) {
                    case 66: {
                        byte[] byteValues = new byte[numValues];
                        int var16 = 0;
                        while (var16 < numValues) {
                            byteValues[var16] = (byte)this.readInt(this.cpInfoOffsets[this.readUnsignedShort(currentOffset + 1)]);
                            currentOffset += 3;
                            ++var16;
                        }
                        annotationVisitor.visit(elementName, byteValues);
                        return currentOffset;
                    }
                    case 67: {
                        char[] var18 = new char[numValues];
                        int var19 = 0;
                        while (var19 < numValues) {
                            var18[var19] = (char)this.readInt(this.cpInfoOffsets[this.readUnsignedShort(currentOffset + 1)]);
                            currentOffset += 3;
                            ++var19;
                        }
                        annotationVisitor.visit(elementName, var18);
                        return currentOffset;
                    }
                    case 68: {
                        double[] var22 = new double[numValues];
                        int i2 = 0;
                        while (i2 < numValues) {
                            var22[i2] = Double.longBitsToDouble(this.readLong(this.cpInfoOffsets[this.readUnsignedShort(currentOffset + 1)]));
                            currentOffset += 3;
                            ++i2;
                        }
                        annotationVisitor.visit(elementName, var22);
                        return currentOffset;
                    }
                    default: {
                        currentOffset = this.readElementValues(annotationVisitor.visitArray(elementName), currentOffset - 2, false, charBuffer);
                        return currentOffset;
                    }
                    case 70: {
                        float[] var21 = new float[numValues];
                        int doubleValues = 0;
                        while (doubleValues < numValues) {
                            var21[doubleValues] = Float.intBitsToFloat(this.readInt(this.cpInfoOffsets[this.readUnsignedShort(currentOffset + 1)]));
                            currentOffset += 3;
                            ++doubleValues;
                        }
                        annotationVisitor.visit(elementName, var21);
                        return currentOffset;
                    }
                    case 73: {
                        int[] intValues = new int[numValues];
                        int var20 = 0;
                        while (var20 < numValues) {
                            intValues[var20] = this.readInt(this.cpInfoOffsets[this.readUnsignedShort(currentOffset + 1)]);
                            currentOffset += 3;
                            ++var20;
                        }
                        annotationVisitor.visit(elementName, intValues);
                        return currentOffset;
                    }
                    case 74: {
                        long[] longValues = new long[numValues];
                        int floatValues = 0;
                        while (floatValues < numValues) {
                            longValues[floatValues] = this.readLong(this.cpInfoOffsets[this.readUnsignedShort(currentOffset + 1)]);
                            currentOffset += 3;
                            ++floatValues;
                        }
                        annotationVisitor.visit(elementName, longValues);
                        return currentOffset;
                    }
                    case 83: {
                        short[] var17 = new short[numValues];
                        int charValues = 0;
                        while (charValues < numValues) {
                            var17[charValues] = (short)this.readInt(this.cpInfoOffsets[this.readUnsignedShort(currentOffset + 1)]);
                            currentOffset += 3;
                            ++charValues;
                        }
                        annotationVisitor.visit(elementName, var17);
                        return currentOffset;
                    }
                    case 90: 
                }
                boolean[] booleanValues = new boolean[numValues];
                int shortValues = 0;
                while (shortValues < numValues) {
                    booleanValues[shortValues] = this.readInt(this.cpInfoOffsets[this.readUnsignedShort(currentOffset + 1)]) != 0;
                    currentOffset += 3;
                    ++shortValues;
                }
                annotationVisitor.visit(elementName, booleanValues);
                return currentOffset;
            }
            case 99: {
                annotationVisitor.visit(elementName, Type.getType(this.readUTF8(currentOffset, charBuffer)));
                currentOffset += 2;
                break;
            }
            case 101: {
                annotationVisitor.visitEnum(elementName, this.readUTF8(currentOffset, charBuffer), this.readUTF8(currentOffset + 2, charBuffer));
                currentOffset += 4;
                break;
            }
            case 115: {
                annotationVisitor.visit(elementName, this.readUTF8(currentOffset, charBuffer));
                currentOffset += 2;
            }
        }
        return currentOffset;
    }

    private void computeImplicitFrame(Context context) {
        String methodDescriptor = context.currentMethodDescriptor;
        Object[] locals = context.currentFrameLocalTypes;
        int numLocal = 0;
        if ((context.currentMethodAccessFlags & 8) == 0) {
            locals[numLocal++] = "<init>".equals(context.currentMethodName) ? Opcodes.UNINITIALIZED_THIS : this.readClass(this.header + 2, context.charBuffer);
        }
        int currentMethodDescritorOffset = 1;
        block8: while (true) {
            int currentArgumentDescriptorStartOffset = currentMethodDescritorOffset;
            switch (methodDescriptor.charAt(currentMethodDescritorOffset++)) {
                case 'B': 
                case 'C': 
                case 'I': 
                case 'S': 
                case 'Z': {
                    locals[numLocal++] = Opcodes.INTEGER;
                    continue block8;
                }
                case 'D': {
                    locals[numLocal++] = Opcodes.DOUBLE;
                    continue block8;
                }
                default: {
                    context.currentFrameLocalCount = numLocal;
                    return;
                }
                case 'F': {
                    locals[numLocal++] = Opcodes.FLOAT;
                    continue block8;
                }
                case 'J': {
                    locals[numLocal++] = Opcodes.LONG;
                    continue block8;
                }
                case 'L': {
                    while (methodDescriptor.charAt(currentMethodDescritorOffset) != ';') {
                        ++currentMethodDescritorOffset;
                    }
                    locals[numLocal++] = methodDescriptor.substring(currentArgumentDescriptorStartOffset + 1, currentMethodDescritorOffset++);
                    continue block8;
                }
                case '[': 
            }
            while (methodDescriptor.charAt(currentMethodDescritorOffset) == '[') {
                ++currentMethodDescritorOffset;
            }
            if (methodDescriptor.charAt(currentMethodDescritorOffset) == 'L') {
                ++currentMethodDescritorOffset;
                while (methodDescriptor.charAt(currentMethodDescritorOffset) != ';') {
                    ++currentMethodDescritorOffset;
                }
            }
            int var10001 = numLocal++;
            locals[var10001] = methodDescriptor.substring(currentArgumentDescriptorStartOffset, ++currentMethodDescritorOffset);
        }
    }

    private int readStackMapFrame(int stackMapFrameOffset, boolean compressed, boolean expand, Context context) {
        int offsetDelta;
        int frameType;
        int currentOffset = stackMapFrameOffset;
        char[] charBuffer = context.charBuffer;
        Label[] labels = context.currentMethodLabels;
        if (compressed) {
            currentOffset = stackMapFrameOffset + 1;
            frameType = this.b[stackMapFrameOffset] & 0xFF;
        } else {
            frameType = 255;
            context.currentFrameOffset = -1;
        }
        context.currentFrameLocalCountDelta = 0;
        if (frameType < 64) {
            offsetDelta = frameType;
            context.currentFrameType = 3;
            context.currentFrameStackCount = 0;
        } else if (frameType < 128) {
            offsetDelta = frameType - 64;
            currentOffset = this.readVerificationTypeInfo(currentOffset, context.currentFrameStackTypes, 0, charBuffer, labels);
            context.currentFrameType = 4;
            context.currentFrameStackCount = 1;
        } else {
            if (frameType < 247) {
                throw new IllegalArgumentException();
            }
            offsetDelta = this.readUnsignedShort(currentOffset);
            currentOffset += 2;
            if (frameType == 247) {
                currentOffset = this.readVerificationTypeInfo(currentOffset, context.currentFrameStackTypes, 0, charBuffer, labels);
                context.currentFrameType = 4;
                context.currentFrameStackCount = 1;
            } else if (frameType >= 248 && frameType < 251) {
                context.currentFrameType = 2;
                context.currentFrameLocalCountDelta = 251 - frameType;
                context.currentFrameLocalCount -= context.currentFrameLocalCountDelta;
                context.currentFrameStackCount = 0;
            } else if (frameType == 251) {
                context.currentFrameType = 3;
                context.currentFrameStackCount = 0;
            } else if (frameType < 255) {
                int numberOfLocals = expand ? context.currentFrameLocalCount : 0;
                int numberOfStackItems = frameType - 251;
                while (numberOfStackItems > 0) {
                    currentOffset = this.readVerificationTypeInfo(currentOffset, context.currentFrameLocalTypes, numberOfLocals++, charBuffer, labels);
                    --numberOfStackItems;
                }
                context.currentFrameType = 1;
                context.currentFrameLocalCountDelta = frameType - 251;
                context.currentFrameLocalCount += context.currentFrameLocalCountDelta;
                context.currentFrameStackCount = 0;
            } else {
                int numberOfLocals = this.readUnsignedShort(currentOffset);
                currentOffset += 2;
                context.currentFrameType = 0;
                context.currentFrameLocalCountDelta = numberOfLocals;
                context.currentFrameLocalCount = numberOfLocals;
                int numberOfStackItems = 0;
                while (numberOfStackItems < numberOfLocals) {
                    currentOffset = this.readVerificationTypeInfo(currentOffset, context.currentFrameLocalTypes, numberOfStackItems, charBuffer, labels);
                    ++numberOfStackItems;
                }
                numberOfStackItems = this.readUnsignedShort(currentOffset);
                currentOffset += 2;
                context.currentFrameStackCount = numberOfStackItems;
                int stack = 0;
                while (stack < numberOfStackItems) {
                    currentOffset = this.readVerificationTypeInfo(currentOffset, context.currentFrameStackTypes, stack, charBuffer, labels);
                    ++stack;
                }
            }
        }
        context.currentFrameOffset += offsetDelta + 1;
        this.createLabel(context.currentFrameOffset, labels);
        return currentOffset;
    }

    private int readVerificationTypeInfo(int verificationTypeInfoOffset, Object[] frame, int index, char[] charBuffer, Label[] labels) {
        int currentOffset = verificationTypeInfoOffset + 1;
        int tag = this.b[verificationTypeInfoOffset] & 0xFF;
        switch (tag) {
            case 0: {
                frame[index] = Opcodes.TOP;
                break;
            }
            case 1: {
                frame[index] = Opcodes.INTEGER;
                break;
            }
            case 2: {
                frame[index] = Opcodes.FLOAT;
                break;
            }
            case 3: {
                frame[index] = Opcodes.DOUBLE;
                break;
            }
            case 4: {
                frame[index] = Opcodes.LONG;
                break;
            }
            case 5: {
                frame[index] = Opcodes.NULL;
                break;
            }
            case 6: {
                frame[index] = Opcodes.UNINITIALIZED_THIS;
                break;
            }
            case 7: {
                frame[index] = this.readClass(currentOffset, charBuffer);
                currentOffset += 2;
                break;
            }
            case 8: {
                frame[index] = this.createLabel(this.readUnsignedShort(currentOffset), labels);
                currentOffset += 2;
                break;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
        return currentOffset;
    }

    final int getFirstAttributeOffset() {
        int methodsCount;
        int currentOffset = this.header + 8 + this.readUnsignedShort(this.header + 6) * 2;
        int fieldsCount = this.readUnsignedShort(currentOffset);
        currentOffset += 2;
        while (fieldsCount-- > 0) {
            methodsCount = this.readUnsignedShort(currentOffset + 6);
            currentOffset += 8;
            while (methodsCount-- > 0) {
                currentOffset += 6 + this.readInt(currentOffset + 2);
            }
        }
        methodsCount = this.readUnsignedShort(currentOffset);
        currentOffset += 2;
        while (methodsCount-- > 0) {
            int attributesCount = this.readUnsignedShort(currentOffset + 6);
            currentOffset += 8;
            while (attributesCount-- > 0) {
                currentOffset += 6 + this.readInt(currentOffset + 2);
            }
        }
        return currentOffset + 2;
    }

    private int[] readBootstrapMethodsAttribute(int maxStringLength) {
        char[] charBuffer = new char[maxStringLength];
        int currentAttributeOffset = this.getFirstAttributeOffset();
        Object currentBootstrapMethodOffsets = null;
        int i2 = this.readUnsignedShort(currentAttributeOffset - 2);
        while (i2 > 0) {
            String attributeName = this.readUTF8(currentAttributeOffset, charBuffer);
            int attributeLength = this.readInt(currentAttributeOffset + 2);
            currentAttributeOffset += 6;
            if ("BootstrapMethods".equals(attributeName)) {
                int[] var10 = new int[this.readUnsignedShort(currentAttributeOffset)];
                int currentBootstrapMethodOffset = currentAttributeOffset + 2;
                int j2 = 0;
                while (j2 < var10.length) {
                    var10[j2] = currentBootstrapMethodOffset;
                    currentBootstrapMethodOffset += 4 + this.readUnsignedShort(currentBootstrapMethodOffset + 2) * 2;
                    ++j2;
                }
                return var10;
            }
            currentAttributeOffset += attributeLength;
            --i2;
        }
        return null;
    }

    private Attribute readAttribute(Attribute[] attributePrototypes, String type, int offset, int length, char[] charBuffer, int codeAttributeOffset, Label[] labels) {
        Attribute[] var8 = attributePrototypes;
        int var9 = attributePrototypes.length;
        int var10 = 0;
        while (var10 < var9) {
            Attribute attributePrototype = var8[var10];
            if (attributePrototype.type.equals(type)) {
                return attributePrototype.read(this, offset, length, charBuffer, codeAttributeOffset, labels);
            }
            ++var10;
        }
        return new Attribute(type).read(this, offset, length, null, -1, null);
    }

    public int getItemCount() {
        return this.cpInfoOffsets.length;
    }

    public int getItem(int constantPoolEntryIndex) {
        return this.cpInfoOffsets[constantPoolEntryIndex];
    }

    public int getMaxStringLength() {
        return this.maxStringLength;
    }

    public int readByte(int offset) {
        return this.b[offset] & 0xFF;
    }

    public int readUnsignedShort(int offset) {
        byte[] classFileBuffer = this.b;
        return (classFileBuffer[offset] & 0xFF) << 8 | classFileBuffer[offset + 1] & 0xFF;
    }

    public short readShort(int offset) {
        byte[] classFileBuffer = this.b;
        return (short)((classFileBuffer[offset] & 0xFF) << 8 | classFileBuffer[offset + 1] & 0xFF);
    }

    public int readInt(int offset) {
        byte[] classFileBuffer = this.b;
        return (classFileBuffer[offset] & 0xFF) << 24 | (classFileBuffer[offset + 1] & 0xFF) << 16 | (classFileBuffer[offset + 2] & 0xFF) << 8 | classFileBuffer[offset + 3] & 0xFF;
    }

    public long readLong(int offset) {
        long l1 = this.readInt(offset);
        long l0 = (long)this.readInt(offset + 4) & 0xFFFFFFFFL;
        return l1 << 32 | l0;
    }

    public String readUTF8(int offset, char[] charBuffer) {
        int constantPoolEntryIndex = this.readUnsignedShort(offset);
        return offset != 0 && constantPoolEntryIndex != 0 ? this.readUtf(constantPoolEntryIndex, charBuffer) : null;
    }

    final String readUtf(int constantPoolEntryIndex, char[] charBuffer) {
        String value = this.constantUtf8Values[constantPoolEntryIndex];
        if (value != null) {
            return value;
        }
        int cpInfoOffset = this.cpInfoOffsets[constantPoolEntryIndex];
        this.constantUtf8Values[constantPoolEntryIndex] = this.readUtf(cpInfoOffset + 2, this.readUnsignedShort(cpInfoOffset), charBuffer);
        return this.constantUtf8Values[constantPoolEntryIndex];
    }

    private String readUtf(int utfOffset, int utfLength, char[] charBuffer) {
        int currentOffset = utfOffset;
        int endOffset = utfOffset + utfLength;
        int strLength = 0;
        byte[] classFileBuffer = this.b;
        while (currentOffset < endOffset) {
            byte currentByte;
            if (((currentByte = classFileBuffer[currentOffset++]) & 0x80) == 0) {
                charBuffer[strLength++] = (char)(currentByte & 0x7F);
                continue;
            }
            if ((currentByte & 0xE0) == 192) {
                charBuffer[strLength++] = (char)(((currentByte & 0x1F) << 6) + (classFileBuffer[currentOffset++] & 0x3F));
                continue;
            }
            charBuffer[strLength++] = (char)(((currentByte & 0xF) << 12) + ((classFileBuffer[currentOffset++] & 0x3F) << 6) + (classFileBuffer[currentOffset++] & 0x3F));
        }
        return new String(charBuffer, 0, strLength);
    }

    private String readStringish(int offset, char[] charBuffer) {
        return this.readUTF8(this.cpInfoOffsets[this.readUnsignedShort(offset)], charBuffer);
    }

    public String readClass(int offset, char[] charBuffer) {
        return this.readStringish(offset, charBuffer);
    }

    public String readModule(int offset, char[] charBuffer) {
        return this.readStringish(offset, charBuffer);
    }

    public String readPackage(int offset, char[] charBuffer) {
        return this.readStringish(offset, charBuffer);
    }

    private ConstantDynamic readConstantDynamic(int constantPoolEntryIndex, char[] charBuffer) {
        ConstantDynamic constantDynamic = this.constantDynamicValues[constantPoolEntryIndex];
        if (constantDynamic != null) {
            return constantDynamic;
        }
        int cpInfoOffset = this.cpInfoOffsets[constantPoolEntryIndex];
        int nameAndTypeCpInfoOffset = this.cpInfoOffsets[this.readUnsignedShort(cpInfoOffset + 2)];
        String name = this.readUTF8(nameAndTypeCpInfoOffset, charBuffer);
        String descriptor = this.readUTF8(nameAndTypeCpInfoOffset + 2, charBuffer);
        int bootstrapMethodOffset = this.bootstrapMethodOffsets[this.readUnsignedShort(cpInfoOffset)];
        Handle handle = (Handle)this.readConst(this.readUnsignedShort(bootstrapMethodOffset), charBuffer);
        Object[] bootstrapMethodArguments = new Object[this.readUnsignedShort(bootstrapMethodOffset + 2)];
        bootstrapMethodOffset += 4;
        int i2 = 0;
        while (i2 < bootstrapMethodArguments.length) {
            bootstrapMethodArguments[i2] = this.readConst(this.readUnsignedShort(bootstrapMethodOffset), charBuffer);
            bootstrapMethodOffset += 2;
            ++i2;
        }
        this.constantDynamicValues[constantPoolEntryIndex] = new ConstantDynamic(name, descriptor, handle, bootstrapMethodArguments);
        return this.constantDynamicValues[constantPoolEntryIndex];
    }

    public Object readConst(int constantPoolEntryIndex, char[] charBuffer) {
        int cpInfoOffset = this.cpInfoOffsets[constantPoolEntryIndex];
        switch (this.b[cpInfoOffset - 1]) {
            case 3: {
                return this.readInt(cpInfoOffset);
            }
            case 4: {
                return Float.valueOf(Float.intBitsToFloat(this.readInt(cpInfoOffset)));
            }
            case 5: {
                return this.readLong(cpInfoOffset);
            }
            case 6: {
                return Double.longBitsToDouble(this.readLong(cpInfoOffset));
            }
            case 7: {
                return Type.getObjectType(this.readUTF8(cpInfoOffset, charBuffer));
            }
            case 8: {
                return this.readUTF8(cpInfoOffset, charBuffer);
            }
            default: {
                throw new IllegalArgumentException();
            }
            case 15: {
                int referenceKind = this.readByte(cpInfoOffset);
                int referenceCpInfoOffset = this.cpInfoOffsets[this.readUnsignedShort(cpInfoOffset + 1)];
                int nameAndTypeCpInfoOffset = this.cpInfoOffsets[this.readUnsignedShort(referenceCpInfoOffset + 2)];
                String owner = this.readClass(referenceCpInfoOffset, charBuffer);
                String name = this.readUTF8(nameAndTypeCpInfoOffset, charBuffer);
                String descriptor = this.readUTF8(nameAndTypeCpInfoOffset + 2, charBuffer);
                boolean isInterface = this.b[referenceCpInfoOffset - 1] == 11;
                return new Handle(referenceKind, owner, name, descriptor, isInterface);
            }
            case 16: {
                return Type.getMethodType(this.readUTF8(cpInfoOffset, charBuffer));
            }
            case 17: 
        }
        return this.readConstantDynamic(constantPoolEntryIndex, charBuffer);
    }
}

