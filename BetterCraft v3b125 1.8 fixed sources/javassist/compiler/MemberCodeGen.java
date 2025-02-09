/*
 * Decompiled with CFR 0.152.
 */
package javassist.compiler;

import java.util.ArrayList;
import java.util.List;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.AccessFlag;
import javassist.bytecode.Bytecode;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.Descriptor;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.MethodInfo;
import javassist.compiler.AccessorMaker;
import javassist.compiler.CodeGen;
import javassist.compiler.CompileError;
import javassist.compiler.MemberResolver;
import javassist.compiler.NoFieldException;
import javassist.compiler.TypeChecker;
import javassist.compiler.ast.ASTList;
import javassist.compiler.ast.ASTree;
import javassist.compiler.ast.ArrayInit;
import javassist.compiler.ast.CallExpr;
import javassist.compiler.ast.Declarator;
import javassist.compiler.ast.Expr;
import javassist.compiler.ast.Keyword;
import javassist.compiler.ast.Member;
import javassist.compiler.ast.MethodDecl;
import javassist.compiler.ast.NewExpr;
import javassist.compiler.ast.Pair;
import javassist.compiler.ast.Stmnt;
import javassist.compiler.ast.Symbol;

public class MemberCodeGen
extends CodeGen {
    protected MemberResolver resolver;
    protected CtClass thisClass;
    protected MethodInfo thisMethod;
    protected boolean resultStatic;

    public MemberCodeGen(Bytecode b2, CtClass cc2, ClassPool cp2) {
        super(b2);
        this.resolver = new MemberResolver(cp2);
        this.thisClass = cc2;
        this.thisMethod = null;
    }

    public int getMajorVersion() {
        ClassFile cf2 = this.thisClass.getClassFile2();
        if (cf2 == null) {
            return ClassFile.MAJOR_VERSION;
        }
        return cf2.getMajorVersion();
    }

    public void setThisMethod(CtMethod m2) {
        this.thisMethod = m2.getMethodInfo2();
        if (this.typeChecker != null) {
            this.typeChecker.setThisMethod(this.thisMethod);
        }
    }

    public CtClass getThisClass() {
        return this.thisClass;
    }

    @Override
    protected String getThisName() {
        return MemberResolver.javaToJvmName(this.thisClass.getName());
    }

    @Override
    protected String getSuperName() throws CompileError {
        return MemberResolver.javaToJvmName(MemberResolver.getSuperclass(this.thisClass).getName());
    }

    @Override
    protected void insertDefaultSuperCall() throws CompileError {
        this.bytecode.addAload(0);
        this.bytecode.addInvokespecial(MemberResolver.getSuperclass(this.thisClass), "<init>", "()V");
    }

    @Override
    protected void atTryStmnt(Stmnt st2) throws CompileError {
        boolean tryNotReturn;
        Bytecode bc2 = this.bytecode;
        Stmnt body = (Stmnt)st2.getLeft();
        if (body == null) {
            return;
        }
        ASTList catchList = (ASTList)st2.getRight().getLeft();
        Stmnt finallyBlock = (Stmnt)st2.getRight().getRight().getLeft();
        ArrayList<Integer> gotoList = new ArrayList<Integer>();
        JsrHook jsrHook = null;
        if (finallyBlock != null) {
            jsrHook = new JsrHook(this);
        }
        int start = bc2.currentPc();
        body.accept(this);
        int end = bc2.currentPc();
        if (start == end) {
            throw new CompileError("empty try block");
        }
        boolean bl2 = tryNotReturn = !this.hasReturned;
        if (tryNotReturn) {
            bc2.addOpcode(167);
            gotoList.add(bc2.currentPc());
            bc2.addIndex(0);
        }
        int var = this.getMaxLocals();
        this.incMaxLocals(1);
        while (catchList != null) {
            Pair p2 = (Pair)catchList.head();
            catchList = catchList.tail();
            Declarator decl = (Declarator)p2.getLeft();
            Stmnt block = (Stmnt)p2.getRight();
            decl.setLocalVar(var);
            CtClass type = this.resolver.lookupClassByJvmName(decl.getClassName());
            decl.setClassName(MemberResolver.javaToJvmName(type.getName()));
            bc2.addExceptionHandler(start, end, bc2.currentPc(), type);
            bc2.growStack(1);
            bc2.addAstore(var);
            this.hasReturned = false;
            if (block != null) {
                block.accept(this);
            }
            if (this.hasReturned) continue;
            bc2.addOpcode(167);
            gotoList.add(bc2.currentPc());
            bc2.addIndex(0);
            tryNotReturn = true;
        }
        if (finallyBlock != null) {
            jsrHook.remove(this);
            int pcAnyCatch = bc2.currentPc();
            bc2.addExceptionHandler(start, pcAnyCatch, pcAnyCatch, 0);
            bc2.growStack(1);
            bc2.addAstore(var);
            this.hasReturned = false;
            finallyBlock.accept(this);
            if (!this.hasReturned) {
                bc2.addAload(var);
                bc2.addOpcode(191);
            }
            this.addFinally(jsrHook.jsrList, finallyBlock);
        }
        int pcEnd = bc2.currentPc();
        this.patchGoto(gotoList, pcEnd);
        boolean bl3 = this.hasReturned = !tryNotReturn;
        if (finallyBlock != null && tryNotReturn) {
            finallyBlock.accept(this);
        }
    }

    private void addFinally(List<int[]> returnList, Stmnt finallyBlock) throws CompileError {
        Bytecode bc2 = this.bytecode;
        for (int[] ret : returnList) {
            int pc2 = ret[0];
            bc2.write16bit(pc2, bc2.currentPc() - pc2 + 1);
            JsrHook2 hook = new JsrHook2(this, ret);
            finallyBlock.accept(this);
            hook.remove(this);
            if (this.hasReturned) continue;
            bc2.addOpcode(167);
            bc2.addIndex(pc2 + 3 - bc2.currentPc());
        }
    }

    @Override
    public void atNewExpr(NewExpr expr) throws CompileError {
        if (expr.isArray()) {
            this.atNewArrayExpr(expr);
        } else {
            CtClass clazz = this.resolver.lookupClassByName(expr.getClassName());
            String cname = clazz.getName();
            ASTList args = expr.getArguments();
            this.bytecode.addNew(cname);
            this.bytecode.addOpcode(89);
            this.atMethodCallCore(clazz, "<init>", args, false, true, -1, null);
            this.exprType = 307;
            this.arrayDim = 0;
            this.className = MemberResolver.javaToJvmName(cname);
        }
    }

    public void atNewArrayExpr(NewExpr expr) throws CompileError {
        int type = expr.getArrayType();
        ASTList size = expr.getArraySize();
        ASTList classname = expr.getClassName();
        ArrayInit init = expr.getInitializer();
        if (size.length() > 1) {
            if (init != null) {
                throw new CompileError("sorry, multi-dimensional array initializer for new is not supported");
            }
            this.atMultiNewArray(type, classname, size);
            return;
        }
        ASTree sizeExpr = size.head();
        this.atNewArrayExpr2(type, sizeExpr, Declarator.astToClassName(classname, '/'), init);
    }

    private void atNewArrayExpr2(int type, ASTree sizeExpr, String jvmClassname, ArrayInit init) throws CompileError {
        String elementClass;
        if (init == null) {
            if (sizeExpr == null) {
                throw new CompileError("no array size");
            }
            sizeExpr.accept(this);
        } else if (sizeExpr == null) {
            int s2 = init.size();
            this.bytecode.addIconst(s2);
        } else {
            throw new CompileError("unnecessary array size specified for new");
        }
        if (type == 307) {
            elementClass = this.resolveClassName(jvmClassname);
            this.bytecode.addAnewarray(MemberResolver.jvmToJavaName(elementClass));
        } else {
            elementClass = null;
            int atype = 0;
            switch (type) {
                case 301: {
                    atype = 4;
                    break;
                }
                case 306: {
                    atype = 5;
                    break;
                }
                case 317: {
                    atype = 6;
                    break;
                }
                case 312: {
                    atype = 7;
                    break;
                }
                case 303: {
                    atype = 8;
                    break;
                }
                case 334: {
                    atype = 9;
                    break;
                }
                case 324: {
                    atype = 10;
                    break;
                }
                case 326: {
                    atype = 11;
                    break;
                }
                default: {
                    MemberCodeGen.badNewExpr();
                }
            }
            this.bytecode.addOpcode(188);
            this.bytecode.add(atype);
        }
        if (init != null) {
            int s3 = init.size();
            ASTList list = init;
            for (int i2 = 0; i2 < s3; ++i2) {
                this.bytecode.addOpcode(89);
                this.bytecode.addIconst(i2);
                list.head().accept(this);
                if (!MemberCodeGen.isRefType(type)) {
                    this.atNumCastExpr(this.exprType, type);
                }
                this.bytecode.addOpcode(MemberCodeGen.getArrayWriteOp(type, 0));
                list = list.tail();
            }
        }
        this.exprType = type;
        this.arrayDim = 1;
        this.className = elementClass;
    }

    private static void badNewExpr() throws CompileError {
        throw new CompileError("bad new expression");
    }

    @Override
    protected void atArrayVariableAssign(ArrayInit init, int varType, int varArray, String varClass) throws CompileError {
        this.atNewArrayExpr2(varType, null, varClass, init);
    }

    @Override
    public void atArrayInit(ArrayInit init) throws CompileError {
        throw new CompileError("array initializer is not supported");
    }

    protected void atMultiNewArray(int type, ASTList classname, ASTList size) throws CompileError {
        String desc;
        ASTree s2;
        int dim = size.length();
        int count = 0;
        while (size != null && (s2 = size.head()) != null) {
            ++count;
            s2.accept(this);
            if (this.exprType != 324) {
                throw new CompileError("bad type for array size");
            }
            size = size.tail();
        }
        this.exprType = type;
        this.arrayDim = dim;
        if (type == 307) {
            this.className = this.resolveClassName(classname);
            desc = MemberCodeGen.toJvmArrayName(this.className, dim);
        } else {
            desc = MemberCodeGen.toJvmTypeName(type, dim);
        }
        this.bytecode.addMultiNewarray(desc, count);
    }

    @Override
    public void atCallExpr(CallExpr expr) throws CompileError {
        String mname = null;
        CtClass targetClass = null;
        ASTree method = expr.oprand1();
        ASTList args = (ASTList)expr.oprand2();
        boolean isStatic = false;
        boolean isSpecial = false;
        int aload0pos = -1;
        MemberResolver.Method cached = expr.getMethod();
        if (method instanceof Member) {
            mname = ((Member)method).get();
            targetClass = this.thisClass;
            if (this.inStaticMethod || cached != null && cached.isStatic()) {
                isStatic = true;
            } else {
                aload0pos = this.bytecode.currentPc();
                this.bytecode.addAload(0);
            }
        } else if (method instanceof Keyword) {
            isSpecial = true;
            mname = "<init>";
            targetClass = this.thisClass;
            if (this.inStaticMethod) {
                throw new CompileError("a constructor cannot be static");
            }
            this.bytecode.addAload(0);
            if (((Keyword)method).get() == 336) {
                targetClass = MemberResolver.getSuperclass(targetClass);
            }
        } else if (method instanceof Expr) {
            Expr e2 = (Expr)method;
            mname = ((Symbol)e2.oprand2()).get();
            int op2 = e2.getOperator();
            if (op2 == 35) {
                targetClass = this.resolver.lookupClass(((Symbol)e2.oprand1()).get(), false);
                isStatic = true;
            } else if (op2 == 46) {
                ASTree target = e2.oprand1();
                String classFollowedByDotSuper = TypeChecker.isDotSuper(target);
                if (classFollowedByDotSuper != null) {
                    isSpecial = true;
                    targetClass = MemberResolver.getSuperInterface(this.thisClass, classFollowedByDotSuper);
                    if (this.inStaticMethod || cached != null && cached.isStatic()) {
                        isStatic = true;
                    } else {
                        aload0pos = this.bytecode.currentPc();
                        this.bytecode.addAload(0);
                    }
                } else {
                    if (target instanceof Keyword && ((Keyword)target).get() == 336) {
                        isSpecial = true;
                    }
                    try {
                        target.accept(this);
                    }
                    catch (NoFieldException nfe) {
                        if (nfe.getExpr() != target) {
                            throw nfe;
                        }
                        this.exprType = 307;
                        this.arrayDim = 0;
                        this.className = nfe.getField();
                        isStatic = true;
                    }
                    if (this.arrayDim > 0) {
                        targetClass = this.resolver.lookupClass("java.lang.Object", true);
                    } else if (this.exprType == 307) {
                        targetClass = this.resolver.lookupClassByJvmName(this.className);
                    } else {
                        MemberCodeGen.badMethod();
                    }
                }
            } else {
                MemberCodeGen.badMethod();
            }
        } else {
            MemberCodeGen.fatal();
        }
        this.atMethodCallCore(targetClass, mname, args, isStatic, isSpecial, aload0pos, cached);
    }

    private static void badMethod() throws CompileError {
        throw new CompileError("bad method");
    }

    public void atMethodCallCore(CtClass targetClass, String mname, ASTList args, boolean isStatic, boolean isSpecial, int aload0pos, MemberResolver.Method found) throws CompileError {
        int nargs = this.getMethodArgsLength(args);
        int[] types = new int[nargs];
        int[] dims = new int[nargs];
        String[] cnames = new String[nargs];
        if (!isStatic && found != null && found.isStatic()) {
            this.bytecode.addOpcode(87);
            isStatic = true;
        }
        int stack = this.bytecode.getStackDepth();
        this.atMethodArgs(args, types, dims, cnames);
        if (found == null) {
            found = this.resolver.lookupMethod(targetClass, this.thisClass, this.thisMethod, mname, types, dims, cnames);
        }
        if (found == null) {
            String msg = mname.equals("<init>") ? "constructor not found" : "Method " + mname + " not found in " + targetClass.getName();
            throw new CompileError(msg);
        }
        this.atMethodCallCore2(targetClass, mname, isStatic, isSpecial, aload0pos, found);
    }

    private boolean isFromSameDeclaringClass(CtClass outer, CtClass inner) {
        try {
            while (outer != null) {
                if (this.isEnclosing(outer, inner)) {
                    return true;
                }
                outer = outer.getDeclaringClass();
            }
        }
        catch (NotFoundException notFoundException) {
            // empty catch block
        }
        return false;
    }

    private void atMethodCallCore2(CtClass targetClass, String mname, boolean isStatic, boolean isSpecial, int aload0pos, MemberResolver.Method found) throws CompileError {
        CtClass declClass = found.declaring;
        MethodInfo minfo = found.info;
        String desc = minfo.getDescriptor();
        int acc2 = minfo.getAccessFlags();
        if (mname.equals("<init>")) {
            isSpecial = true;
            if (declClass != targetClass) {
                throw new CompileError("no such constructor: " + targetClass.getName());
            }
            if (declClass != this.thisClass && AccessFlag.isPrivate(acc2) && (declClass.getClassFile().getMajorVersion() < 55 || !this.isFromSameDeclaringClass(declClass, this.thisClass))) {
                desc = this.getAccessibleConstructor(desc, declClass, minfo);
                this.bytecode.addOpcode(1);
            }
        } else if (AccessFlag.isPrivate(acc2)) {
            if (declClass == this.thisClass) {
                isSpecial = true;
            } else {
                isSpecial = false;
                isStatic = true;
                String origDesc = desc;
                if ((acc2 & 8) == 0) {
                    desc = Descriptor.insertParameter(declClass.getName(), origDesc);
                }
                acc2 = AccessFlag.setPackage(acc2) | 8;
                mname = this.getAccessiblePrivate(mname, origDesc, desc, minfo, declClass);
            }
        }
        boolean popTarget = false;
        if ((acc2 & 8) != 0) {
            if (!isStatic) {
                isStatic = true;
                if (aload0pos >= 0) {
                    this.bytecode.write(aload0pos, 0);
                } else {
                    popTarget = true;
                }
            }
            this.bytecode.addInvokestatic(declClass, mname, desc);
        } else if (isSpecial) {
            this.bytecode.addInvokespecial(targetClass, mname, desc);
        } else {
            if (!Modifier.isPublic(declClass.getModifiers()) || declClass.isInterface() != targetClass.isInterface()) {
                declClass = targetClass;
            }
            if (declClass.isInterface()) {
                int nargs = Descriptor.paramSize(desc) + 1;
                this.bytecode.addInvokeinterface(declClass, mname, desc, nargs);
            } else {
                if (isStatic) {
                    throw new CompileError(mname + " is not static");
                }
                this.bytecode.addInvokevirtual(declClass, mname, desc);
            }
        }
        this.setReturnType(desc, isStatic, popTarget);
    }

    protected String getAccessiblePrivate(String methodName, String desc, String newDesc, MethodInfo minfo, CtClass declClass) throws CompileError {
        AccessorMaker maker;
        if (this.isEnclosing(declClass, this.thisClass) && (maker = declClass.getAccessorMaker()) != null) {
            return maker.getMethodAccessor(methodName, desc, newDesc, minfo);
        }
        throw new CompileError("Method " + methodName + " is private");
    }

    protected String getAccessibleConstructor(String desc, CtClass declClass, MethodInfo minfo) throws CompileError {
        AccessorMaker maker;
        if (this.isEnclosing(declClass, this.thisClass) && (maker = declClass.getAccessorMaker()) != null) {
            return maker.getConstructor(declClass, desc, minfo);
        }
        throw new CompileError("the called constructor is private in " + declClass.getName());
    }

    private boolean isEnclosing(CtClass outer, CtClass inner) {
        try {
            while (inner != null) {
                if ((inner = inner.getDeclaringClass()) != outer) continue;
                return true;
            }
        }
        catch (NotFoundException notFoundException) {
            // empty catch block
        }
        return false;
    }

    public int getMethodArgsLength(ASTList args) {
        return ASTList.length(args);
    }

    public void atMethodArgs(ASTList args, int[] types, int[] dims, String[] cnames) throws CompileError {
        int i2 = 0;
        while (args != null) {
            ASTree a2 = args.head();
            a2.accept(this);
            types[i2] = this.exprType;
            dims[i2] = this.arrayDim;
            cnames[i2] = this.className;
            ++i2;
            args = args.tail();
        }
    }

    void setReturnType(String desc, boolean isStatic, boolean popTarget) throws CompileError {
        int i2 = desc.indexOf(41);
        if (i2 < 0) {
            MemberCodeGen.badMethod();
        }
        char c2 = desc.charAt(++i2);
        int dim = 0;
        while (c2 == '[') {
            ++dim;
            c2 = desc.charAt(++i2);
        }
        this.arrayDim = dim;
        if (c2 == 'L') {
            int j2 = desc.indexOf(59, i2 + 1);
            if (j2 < 0) {
                MemberCodeGen.badMethod();
            }
            this.exprType = 307;
            this.className = desc.substring(i2 + 1, j2);
        } else {
            this.exprType = MemberResolver.descToType(c2);
            this.className = null;
        }
        int etype = this.exprType;
        if (isStatic && popTarget) {
            if (MemberCodeGen.is2word(etype, dim)) {
                this.bytecode.addOpcode(93);
                this.bytecode.addOpcode(88);
                this.bytecode.addOpcode(87);
            } else if (etype == 344) {
                this.bytecode.addOpcode(87);
            } else {
                this.bytecode.addOpcode(95);
                this.bytecode.addOpcode(87);
            }
        }
    }

    @Override
    protected void atFieldAssign(Expr expr, int op2, ASTree left, ASTree right, boolean doDup) throws CompileError {
        int fi2;
        CtField f2 = this.fieldAccess(left, false);
        boolean is_static = this.resultStatic;
        if (op2 != 61 && !is_static) {
            this.bytecode.addOpcode(89);
        }
        if (op2 == 61) {
            FieldInfo finfo = f2.getFieldInfo2();
            this.setFieldType(finfo);
            AccessorMaker maker = this.isAccessibleField(f2, finfo);
            fi2 = maker == null ? this.addFieldrefInfo(f2, finfo) : 0;
        } else {
            fi2 = this.atFieldRead(f2, is_static);
        }
        int fType = this.exprType;
        int fDim = this.arrayDim;
        String cname = this.className;
        this.atAssignCore(expr, op2, right, fType, fDim, cname);
        boolean is2w = MemberCodeGen.is2word(fType, fDim);
        if (doDup) {
            int dup_code = is_static ? (is2w ? 92 : 89) : (is2w ? 93 : 90);
            this.bytecode.addOpcode(dup_code);
        }
        this.atFieldAssignCore(f2, is_static, fi2, is2w);
        this.exprType = fType;
        this.arrayDim = fDim;
        this.className = cname;
    }

    private void atFieldAssignCore(CtField f2, boolean is_static, int fi2, boolean is2byte) throws CompileError {
        if (fi2 != 0) {
            if (is_static) {
                this.bytecode.add(179);
                this.bytecode.growStack(is2byte ? -2 : -1);
            } else {
                this.bytecode.add(181);
                this.bytecode.growStack(is2byte ? -3 : -2);
            }
            this.bytecode.addIndex(fi2);
        } else {
            CtClass declClass = f2.getDeclaringClass();
            AccessorMaker maker = declClass.getAccessorMaker();
            FieldInfo finfo = f2.getFieldInfo2();
            MethodInfo minfo = maker.getFieldSetter(finfo, is_static);
            this.bytecode.addInvokestatic(declClass, minfo.getName(), minfo.getDescriptor());
        }
    }

    @Override
    public void atMember(Member mem) throws CompileError {
        this.atFieldRead(mem);
    }

    @Override
    protected void atFieldRead(ASTree expr) throws CompileError {
        CtField f2 = this.fieldAccess(expr, true);
        if (f2 == null) {
            this.atArrayLength(expr);
            return;
        }
        boolean is_static = this.resultStatic;
        ASTree cexpr = TypeChecker.getConstantFieldValue(f2);
        if (cexpr == null) {
            this.atFieldRead(f2, is_static);
        } else {
            cexpr.accept(this);
            this.setFieldType(f2.getFieldInfo2());
        }
    }

    private void atArrayLength(ASTree expr) throws CompileError {
        if (this.arrayDim == 0) {
            throw new CompileError(".length applied to a non array");
        }
        this.bytecode.addOpcode(190);
        this.exprType = 324;
        this.arrayDim = 0;
    }

    private int atFieldRead(CtField f2, boolean isStatic) throws CompileError {
        FieldInfo finfo = f2.getFieldInfo2();
        boolean is2byte = this.setFieldType(finfo);
        AccessorMaker maker = this.isAccessibleField(f2, finfo);
        if (maker != null) {
            MethodInfo minfo = maker.getFieldGetter(finfo, isStatic);
            this.bytecode.addInvokestatic(f2.getDeclaringClass(), minfo.getName(), minfo.getDescriptor());
            return 0;
        }
        int fi2 = this.addFieldrefInfo(f2, finfo);
        if (isStatic) {
            this.bytecode.add(178);
            this.bytecode.growStack(is2byte ? 2 : 1);
        } else {
            this.bytecode.add(180);
            this.bytecode.growStack(is2byte ? 1 : 0);
        }
        this.bytecode.addIndex(fi2);
        return fi2;
    }

    private AccessorMaker isAccessibleField(CtField f2, FieldInfo finfo) throws CompileError {
        if (AccessFlag.isPrivate(finfo.getAccessFlags()) && f2.getDeclaringClass() != this.thisClass) {
            AccessorMaker maker;
            CtClass declClass = f2.getDeclaringClass();
            if (this.isEnclosing(declClass, this.thisClass) && (maker = declClass.getAccessorMaker()) != null) {
                return maker;
            }
            throw new CompileError("Field " + f2.getName() + " in " + declClass.getName() + " is private.");
        }
        return null;
    }

    private boolean setFieldType(FieldInfo finfo) throws CompileError {
        String type = finfo.getDescriptor();
        int i2 = 0;
        int dim = 0;
        char c2 = type.charAt(i2);
        while (c2 == '[') {
            ++dim;
            c2 = type.charAt(++i2);
        }
        this.arrayDim = dim;
        this.exprType = MemberResolver.descToType(c2);
        this.className = c2 == 'L' ? type.substring(i2 + 1, type.indexOf(59, i2 + 1)) : null;
        boolean is2byte = dim == 0 && (c2 == 'J' || c2 == 'D');
        return is2byte;
    }

    private int addFieldrefInfo(CtField f2, FieldInfo finfo) {
        ConstPool cp2 = this.bytecode.getConstPool();
        String cname = f2.getDeclaringClass().getName();
        int ci = cp2.addClassInfo(cname);
        String name = finfo.getName();
        String type = finfo.getDescriptor();
        return cp2.addFieldrefInfo(ci, name, type);
    }

    @Override
    protected void atClassObject2(String cname) throws CompileError {
        if (this.getMajorVersion() < 49) {
            super.atClassObject2(cname);
        } else {
            this.bytecode.addLdc(this.bytecode.getConstPool().addClassInfo(cname));
        }
    }

    @Override
    protected void atFieldPlusPlus(int token, boolean isPost, ASTree oprand, Expr expr, boolean doDup) throws CompileError {
        CtField f2 = this.fieldAccess(oprand, false);
        boolean is_static = this.resultStatic;
        if (!is_static) {
            this.bytecode.addOpcode(89);
        }
        int fi2 = this.atFieldRead(f2, is_static);
        int t2 = this.exprType;
        boolean is2w = MemberCodeGen.is2word(t2, this.arrayDim);
        int dup_code = is_static ? (is2w ? 92 : 89) : (is2w ? 93 : 90);
        this.atPlusPlusCore(dup_code, doDup, token, isPost, expr);
        this.atFieldAssignCore(f2, is_static, fi2, is2w);
    }

    protected CtField fieldAccess(ASTree expr, boolean acceptLength) throws CompileError {
        if (expr instanceof Member) {
            String name = ((Member)expr).get();
            CtField f2 = null;
            try {
                f2 = this.thisClass.getField(name);
            }
            catch (NotFoundException e2) {
                throw new NoFieldException(name, expr);
            }
            boolean is_static = Modifier.isStatic(f2.getModifiers());
            if (!is_static) {
                if (this.inStaticMethod) {
                    throw new CompileError("not available in a static method: " + name);
                }
                this.bytecode.addAload(0);
            }
            this.resultStatic = is_static;
            return f2;
        }
        if (expr instanceof Expr) {
            Expr e3 = (Expr)expr;
            int op2 = e3.getOperator();
            if (op2 == 35) {
                CtField f3 = this.resolver.lookupField(((Symbol)e3.oprand1()).get(), (Symbol)e3.oprand2());
                this.resultStatic = true;
                return f3;
            }
            if (op2 == 46) {
                CtField f4 = null;
                try {
                    e3.oprand1().accept(this);
                    if (this.exprType == 307 && this.arrayDim == 0) {
                        f4 = this.resolver.lookupFieldByJvmName(this.className, (Symbol)e3.oprand2());
                    } else {
                        if (acceptLength && this.arrayDim > 0 && ((Symbol)e3.oprand2()).get().equals("length")) {
                            return null;
                        }
                        MemberCodeGen.badLvalue();
                    }
                    boolean is_static = Modifier.isStatic(f4.getModifiers());
                    if (is_static) {
                        this.bytecode.addOpcode(87);
                    }
                    this.resultStatic = is_static;
                    return f4;
                }
                catch (NoFieldException nfe) {
                    if (nfe.getExpr() != e3.oprand1()) {
                        throw nfe;
                    }
                    Symbol fname = (Symbol)e3.oprand2();
                    String cname = nfe.getField();
                    f4 = this.resolver.lookupFieldByJvmName2(cname, fname, expr);
                    this.resultStatic = true;
                    return f4;
                }
            }
            MemberCodeGen.badLvalue();
        } else {
            MemberCodeGen.badLvalue();
        }
        this.resultStatic = false;
        return null;
    }

    private static void badLvalue() throws CompileError {
        throw new CompileError("bad l-value");
    }

    public CtClass[] makeParamList(MethodDecl md2) throws CompileError {
        CtClass[] params;
        ASTList plist = md2.getParams();
        if (plist == null) {
            params = new CtClass[]{};
        } else {
            int i2 = 0;
            params = new CtClass[plist.length()];
            while (plist != null) {
                params[i2++] = this.resolver.lookupClass((Declarator)plist.head());
                plist = plist.tail();
            }
        }
        return params;
    }

    public CtClass[] makeThrowsList(MethodDecl md2) throws CompileError {
        ASTList list = md2.getThrows();
        if (list == null) {
            return null;
        }
        int i2 = 0;
        CtClass[] clist = new CtClass[list.length()];
        while (list != null) {
            clist[i2++] = this.resolver.lookupClassByName((ASTList)list.head());
            list = list.tail();
        }
        return clist;
    }

    @Override
    protected String resolveClassName(ASTList name) throws CompileError {
        return this.resolver.resolveClassName(name);
    }

    @Override
    protected String resolveClassName(String jvmName) throws CompileError {
        return this.resolver.resolveJvmClassName(jvmName);
    }

    static class JsrHook2
    extends CodeGen.ReturnHook {
        int var;
        int target;

        JsrHook2(CodeGen gen, int[] retTarget) {
            super(gen);
            this.target = retTarget[0];
            this.var = retTarget[1];
        }

        @Override
        protected boolean doit(Bytecode b2, int opcode) {
            switch (opcode) {
                case 177: {
                    break;
                }
                case 176: {
                    b2.addAstore(this.var);
                    break;
                }
                case 172: {
                    b2.addIstore(this.var);
                    break;
                }
                case 173: {
                    b2.addLstore(this.var);
                    break;
                }
                case 175: {
                    b2.addDstore(this.var);
                    break;
                }
                case 174: {
                    b2.addFstore(this.var);
                    break;
                }
                default: {
                    throw new RuntimeException("fatal");
                }
            }
            b2.addOpcode(167);
            b2.addIndex(this.target - b2.currentPc() + 3);
            return true;
        }
    }

    static class JsrHook
    extends CodeGen.ReturnHook {
        List<int[]> jsrList = new ArrayList<int[]>();
        CodeGen cgen;
        int var;

        JsrHook(CodeGen gen) {
            super(gen);
            this.cgen = gen;
            this.var = -1;
        }

        private int getVar(int size) {
            if (this.var < 0) {
                this.var = this.cgen.getMaxLocals();
                this.cgen.incMaxLocals(size);
            }
            return this.var;
        }

        private void jsrJmp(Bytecode b2) {
            b2.addOpcode(167);
            this.jsrList.add(new int[]{b2.currentPc(), this.var});
            b2.addIndex(0);
        }

        @Override
        protected boolean doit(Bytecode b2, int opcode) {
            switch (opcode) {
                case 177: {
                    this.jsrJmp(b2);
                    break;
                }
                case 176: {
                    b2.addAstore(this.getVar(1));
                    this.jsrJmp(b2);
                    b2.addAload(this.var);
                    break;
                }
                case 172: {
                    b2.addIstore(this.getVar(1));
                    this.jsrJmp(b2);
                    b2.addIload(this.var);
                    break;
                }
                case 173: {
                    b2.addLstore(this.getVar(2));
                    this.jsrJmp(b2);
                    b2.addLload(this.var);
                    break;
                }
                case 175: {
                    b2.addDstore(this.getVar(2));
                    this.jsrJmp(b2);
                    b2.addDload(this.var);
                    break;
                }
                case 174: {
                    b2.addFstore(this.getVar(1));
                    this.jsrJmp(b2);
                    b2.addFload(this.var);
                    break;
                }
                default: {
                    throw new RuntimeException("fatal");
                }
            }
            return false;
        }
    }
}

