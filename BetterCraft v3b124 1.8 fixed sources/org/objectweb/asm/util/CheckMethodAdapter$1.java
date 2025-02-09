/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.BasicVerifier;
import org.objectweb.asm.util.CheckClassAdapter;

class CheckMethodAdapter$1
extends MethodNode {
    final /* synthetic */ MethodVisitor val$cmv;

    CheckMethodAdapter$1(int n2, int n3, String string, String string2, String string3, String[] stringArray, MethodVisitor methodVisitor) {
        this.val$cmv = methodVisitor;
        super(n2, n3, string, string2, string3, stringArray);
    }

    public void visitEnd() {
        Analyzer analyzer = new Analyzer(new BasicVerifier());
        try {
            analyzer.analyze("dummy", this);
        }
        catch (Exception exception) {
            if (exception instanceof IndexOutOfBoundsException && this.maxLocals == 0 && this.maxStack == 0) {
                throw new RuntimeException("Data flow checking option requires valid, non zero maxLocals and maxStack values.");
            }
            exception.printStackTrace();
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter((Writer)stringWriter, true);
            CheckClassAdapter.printAnalyzerResult(this, analyzer, printWriter);
            printWriter.close();
            throw new RuntimeException(exception.getMessage() + ' ' + stringWriter.toString());
        }
        this.accept(this.val$cmv);
    }
}

