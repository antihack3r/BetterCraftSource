/*
 * Decompiled with CFR 0.152.
 */
package javassist.compiler;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtPrimitiveType;
import javassist.NotFoundException;
import javassist.bytecode.Bytecode;
import javassist.bytecode.Descriptor;
import javassist.compiler.CompileError;
import javassist.compiler.JvstTypeChecker;
import javassist.compiler.MemberCodeGen;
import javassist.compiler.MemberResolver;
import javassist.compiler.ProceedHandler;
import javassist.compiler.SymbolTable;
import javassist.compiler.ast.ASTList;
import javassist.compiler.ast.ASTree;
import javassist.compiler.ast.CallExpr;
import javassist.compiler.ast.CastExpr;
import javassist.compiler.ast.Declarator;
import javassist.compiler.ast.Expr;
import javassist.compiler.ast.Member;
import javassist.compiler.ast.Stmnt;
import javassist.compiler.ast.Symbol;

public class JvstCodeGen
extends MemberCodeGen {
    String paramArrayName = null;
    String paramListName = null;
    CtClass[] paramTypeList = null;
    private int paramVarBase = 0;
    private boolean useParam0 = false;
    private String param0Type = null;
    public static final String sigName = "$sig";
    public static final String dollarTypeName = "$type";
    public static final String clazzName = "$class";
    private CtClass dollarType = null;
    CtClass returnType = null;
    String returnCastName = null;
    private String returnVarName = null;
    public static final String wrapperCastName = "$w";
    String proceedName = null;
    public static final String cflowName = "$cflow";
    ProceedHandler procHandler = null;

    public JvstCodeGen(Bytecode b2, CtClass cc2, ClassPool cp2) {
        super(b2, cc2, cp2);
        this.setTypeChecker(new JvstTypeChecker(cc2, cp2, this));
    }

    private int indexOfParam1() {
        return this.paramVarBase + (this.useParam0 ? 1 : 0);
    }

    public void setProceedHandler(ProceedHandler h2, String name) {
        this.proceedName = name;
        this.procHandler = h2;
    }

    public void addNullIfVoid() {
        if (this.exprType == 344) {
            this.bytecode.addOpcode(1);
            this.exprType = 307;
            this.arrayDim = 0;
            this.className = "java/lang/Object";
        }
    }

    @Override
    public void atMember(Member mem) throws CompileError {
        String name = mem.get();
        if (name.equals(this.paramArrayName)) {
            JvstCodeGen.compileParameterList(this.bytecode, this.paramTypeList, this.indexOfParam1());
            this.exprType = 307;
            this.arrayDim = 1;
            this.className = "java/lang/Object";
        } else if (name.equals(sigName)) {
            this.bytecode.addLdc(Descriptor.ofMethod(this.returnType, this.paramTypeList));
            this.bytecode.addInvokestatic("javassist/runtime/Desc", "getParams", "(Ljava/lang/String;)[Ljava/lang/Class;");
            this.exprType = 307;
            this.arrayDim = 1;
            this.className = "java/lang/Class";
        } else if (name.equals(dollarTypeName)) {
            if (this.dollarType == null) {
                throw new CompileError("$type is not available");
            }
            this.bytecode.addLdc(Descriptor.of(this.dollarType));
            this.callGetType("getType");
        } else if (name.equals(clazzName)) {
            if (this.param0Type == null) {
                throw new CompileError("$class is not available");
            }
            this.bytecode.addLdc(this.param0Type);
            this.callGetType("getClazz");
        } else {
            super.atMember(mem);
        }
    }

    private void callGetType(String method) {
        this.bytecode.addInvokestatic("javassist/runtime/Desc", method, "(Ljava/lang/String;)Ljava/lang/Class;");
        this.exprType = 307;
        this.arrayDim = 0;
        this.className = "java/lang/Class";
    }

    @Override
    protected void atFieldAssign(Expr expr, int op2, ASTree left, ASTree right, boolean doDup) throws CompileError {
        if (left instanceof Member && ((Member)left).get().equals(this.paramArrayName)) {
            if (op2 != 61) {
                throw new CompileError("bad operator for " + this.paramArrayName);
            }
            right.accept(this);
            if (this.arrayDim != 1 || this.exprType != 307) {
                throw new CompileError("invalid type for " + this.paramArrayName);
            }
            this.atAssignParamList(this.paramTypeList, this.bytecode);
            if (!doDup) {
                this.bytecode.addOpcode(87);
            }
        } else {
            super.atFieldAssign(expr, op2, left, right, doDup);
        }
    }

    protected void atAssignParamList(CtClass[] params, Bytecode code) throws CompileError {
        if (params == null) {
            return;
        }
        int varNo = this.indexOfParam1();
        int n2 = params.length;
        for (int i2 = 0; i2 < n2; ++i2) {
            code.addOpcode(89);
            code.addIconst(i2);
            code.addOpcode(50);
            this.compileUnwrapValue(params[i2], code);
            code.addStore(varNo, params[i2]);
            varNo += JvstCodeGen.is2word(this.exprType, this.arrayDim) ? 2 : 1;
        }
    }

    @Override
    public void atCastExpr(CastExpr expr) throws CompileError {
        ASTree p2;
        ASTList classname = expr.getClassName();
        if (classname != null && expr.getArrayDim() == 0 && (p2 = classname.head()) instanceof Symbol && classname.tail() == null) {
            String typename = ((Symbol)p2).get();
            if (typename.equals(this.returnCastName)) {
                this.atCastToRtype(expr);
                return;
            }
            if (typename.equals(wrapperCastName)) {
                this.atCastToWrapper(expr);
                return;
            }
        }
        super.atCastExpr(expr);
    }

    protected void atCastToRtype(CastExpr expr) throws CompileError {
        expr.getOprand().accept(this);
        if (this.exprType == 344 || JvstCodeGen.isRefType(this.exprType) || this.arrayDim > 0) {
            this.compileUnwrapValue(this.returnType, this.bytecode);
        } else if (this.returnType instanceof CtPrimitiveType) {
            CtPrimitiveType pt2 = (CtPrimitiveType)this.returnType;
            int destType = MemberResolver.descToType(pt2.getDescriptor());
            this.atNumCastExpr(this.exprType, destType);
            this.exprType = destType;
            this.arrayDim = 0;
            this.className = null;
        } else {
            throw new CompileError("invalid cast");
        }
    }

    protected void atCastToWrapper(CastExpr expr) throws CompileError {
        expr.getOprand().accept(this);
        if (JvstCodeGen.isRefType(this.exprType) || this.arrayDim > 0) {
            return;
        }
        CtClass clazz = this.resolver.lookupClass(this.exprType, this.arrayDim, this.className);
        if (clazz instanceof CtPrimitiveType) {
            CtPrimitiveType pt2 = (CtPrimitiveType)clazz;
            String wrapper = pt2.getWrapperName();
            this.bytecode.addNew(wrapper);
            this.bytecode.addOpcode(89);
            if (pt2.getDataSize() > 1) {
                this.bytecode.addOpcode(94);
            } else {
                this.bytecode.addOpcode(93);
            }
            this.bytecode.addOpcode(88);
            this.bytecode.addInvokespecial(wrapper, "<init>", "(" + pt2.getDescriptor() + ")V");
            this.exprType = 307;
            this.arrayDim = 0;
            this.className = "java/lang/Object";
        }
    }

    @Override
    public void atCallExpr(CallExpr expr) throws CompileError {
        ASTree method = expr.oprand1();
        if (method instanceof Member) {
            String name = ((Member)method).get();
            if (this.procHandler != null && name.equals(this.proceedName)) {
                this.procHandler.doit(this, this.bytecode, (ASTList)expr.oprand2());
                return;
            }
            if (name.equals(cflowName)) {
                this.atCflow((ASTList)expr.oprand2());
                return;
            }
        }
        super.atCallExpr(expr);
    }

    protected void atCflow(ASTList cname) throws CompileError {
        StringBuffer sbuf = new StringBuffer();
        if (cname == null || cname.tail() != null) {
            throw new CompileError("bad $cflow");
        }
        JvstCodeGen.makeCflowName(sbuf, cname.head());
        String name = sbuf.toString();
        Object[] names = this.resolver.getClassPool().lookupCflow(name);
        if (names == null) {
            throw new CompileError("no such $cflow: " + name);
        }
        this.bytecode.addGetstatic((String)names[0], (String)names[1], "Ljavassist/runtime/Cflow;");
        this.bytecode.addInvokevirtual("javassist.runtime.Cflow", "value", "()I");
        this.exprType = 324;
        this.arrayDim = 0;
        this.className = null;
    }

    private static void makeCflowName(StringBuffer sbuf, ASTree name) throws CompileError {
        Expr expr;
        if (name instanceof Symbol) {
            sbuf.append(((Symbol)name).get());
            return;
        }
        if (name instanceof Expr && (expr = (Expr)name).getOperator() == 46) {
            JvstCodeGen.makeCflowName(sbuf, expr.oprand1());
            sbuf.append('.');
            JvstCodeGen.makeCflowName(sbuf, expr.oprand2());
            return;
        }
        throw new CompileError("bad $cflow");
    }

    public boolean isParamListName(ASTList args) {
        if (this.paramTypeList != null && args != null && args.tail() == null) {
            ASTree left = args.head();
            return left instanceof Member && ((Member)left).get().equals(this.paramListName);
        }
        return false;
    }

    @Override
    public int getMethodArgsLength(ASTList args) {
        String pname = this.paramListName;
        int n2 = 0;
        while (args != null) {
            ASTree a2 = args.head();
            if (a2 instanceof Member && ((Member)a2).get().equals(pname)) {
                if (this.paramTypeList != null) {
                    n2 += this.paramTypeList.length;
                }
            } else {
                ++n2;
            }
            args = args.tail();
        }
        return n2;
    }

    @Override
    public void atMethodArgs(ASTList args, int[] types, int[] dims, String[] cnames) throws CompileError {
        CtClass[] params = this.paramTypeList;
        String pname = this.paramListName;
        int i2 = 0;
        while (args != null) {
            ASTree a2 = args.head();
            if (a2 instanceof Member && ((Member)a2).get().equals(pname)) {
                if (params != null) {
                    int n2 = params.length;
                    int regno = this.indexOfParam1();
                    for (int k2 = 0; k2 < n2; ++k2) {
                        CtClass p2 = params[k2];
                        regno += this.bytecode.addLoad(regno, p2);
                        this.setType(p2);
                        types[i2] = this.exprType;
                        dims[i2] = this.arrayDim;
                        cnames[i2] = this.className;
                        ++i2;
                    }
                }
            } else {
                a2.accept(this);
                types[i2] = this.exprType;
                dims[i2] = this.arrayDim;
                cnames[i2] = this.className;
                ++i2;
            }
            args = args.tail();
        }
    }

    void compileInvokeSpecial(ASTree target, int methodIndex, String descriptor, ASTList args) throws CompileError {
        target.accept(this);
        int nargs = this.getMethodArgsLength(args);
        this.atMethodArgs(args, new int[nargs], new int[nargs], new String[nargs]);
        this.bytecode.addInvokespecial(methodIndex, descriptor);
        this.setReturnType(descriptor, false, false);
        this.addNullIfVoid();
    }

    @Override
    protected void atReturnStmnt(Stmnt st2) throws CompileError {
        ASTree result = st2.getLeft();
        if (result != null && this.returnType == CtClass.voidType) {
            this.compileExpr(result);
            if (JvstCodeGen.is2word(this.exprType, this.arrayDim)) {
                this.bytecode.addOpcode(88);
            } else if (this.exprType != 344) {
                this.bytecode.addOpcode(87);
            }
            result = null;
        }
        this.atReturnStmnt2(result);
    }

    public int recordReturnType(CtClass type, String castName, String resultName, SymbolTable tbl) throws CompileError {
        this.returnType = type;
        this.returnCastName = castName;
        this.returnVarName = resultName;
        if (resultName == null) {
            return -1;
        }
        int varNo = this.getMaxLocals();
        int locals = varNo + this.recordVar(type, resultName, varNo, tbl);
        this.setMaxLocals(locals);
        return varNo;
    }

    public void recordType(CtClass t2) {
        this.dollarType = t2;
    }

    public int recordParams(CtClass[] params, boolean isStatic, String prefix, String paramVarName, String paramsName, SymbolTable tbl) throws CompileError {
        return this.recordParams(params, isStatic, prefix, paramVarName, paramsName, !isStatic, 0, this.getThisName(), tbl);
    }

    public int recordParams(CtClass[] params, boolean isStatic, String prefix, String paramVarName, String paramsName, boolean use0, int paramBase, String target, SymbolTable tbl) throws CompileError {
        this.paramTypeList = params;
        this.paramArrayName = paramVarName;
        this.paramListName = paramsName;
        this.paramVarBase = paramBase;
        this.useParam0 = use0;
        if (target != null) {
            this.param0Type = MemberResolver.jvmToJavaName(target);
        }
        this.inStaticMethod = isStatic;
        int varNo = paramBase;
        if (use0) {
            String varName = prefix + "0";
            Declarator decl = new Declarator(307, MemberResolver.javaToJvmName(target), 0, varNo++, new Symbol(varName));
            tbl.append(varName, decl);
        }
        for (int i2 = 0; i2 < params.length; ++i2) {
            varNo += this.recordVar(params[i2], prefix + (i2 + 1), varNo, tbl);
        }
        if (this.getMaxLocals() < varNo) {
            this.setMaxLocals(varNo);
        }
        return varNo;
    }

    public int recordVariable(CtClass type, String varName, SymbolTable tbl) throws CompileError {
        if (varName == null) {
            return -1;
        }
        int varNo = this.getMaxLocals();
        int locals = varNo + this.recordVar(type, varName, varNo, tbl);
        this.setMaxLocals(locals);
        return varNo;
    }

    private int recordVar(CtClass cc2, String varName, int varNo, SymbolTable tbl) throws CompileError {
        if (cc2 == CtClass.voidType) {
            this.exprType = 307;
            this.arrayDim = 0;
            this.className = "java/lang/Object";
        } else {
            this.setType(cc2);
        }
        Declarator decl = new Declarator(this.exprType, this.className, this.arrayDim, varNo, new Symbol(varName));
        tbl.append(varName, decl);
        return JvstCodeGen.is2word(this.exprType, this.arrayDim) ? 2 : 1;
    }

    public void recordVariable(String typeDesc, String varName, int varNo, SymbolTable tbl) throws CompileError {
        char c2;
        int dim = 0;
        while ((c2 = typeDesc.charAt(dim)) == '[') {
            ++dim;
        }
        int type = MemberResolver.descToType(c2);
        String cname = null;
        if (type == 307) {
            cname = dim == 0 ? typeDesc.substring(1, typeDesc.length() - 1) : typeDesc.substring(dim + 1, typeDesc.length() - 1);
        }
        Declarator decl = new Declarator(type, cname, dim, varNo, new Symbol(varName));
        tbl.append(varName, decl);
    }

    public static int compileParameterList(Bytecode code, CtClass[] params, int regno) {
        if (params == null) {
            code.addIconst(0);
            code.addAnewarray("java.lang.Object");
            return 1;
        }
        CtClass[] args = new CtClass[1];
        int n2 = params.length;
        code.addIconst(n2);
        code.addAnewarray("java.lang.Object");
        for (int i2 = 0; i2 < n2; ++i2) {
            code.addOpcode(89);
            code.addIconst(i2);
            if (params[i2].isPrimitive()) {
                CtPrimitiveType pt2 = (CtPrimitiveType)params[i2];
                String wrapper = pt2.getWrapperName();
                code.addNew(wrapper);
                code.addOpcode(89);
                int s2 = code.addLoad(regno, pt2);
                regno += s2;
                args[0] = pt2;
                code.addInvokespecial(wrapper, "<init>", Descriptor.ofMethod(CtClass.voidType, args));
            } else {
                code.addAload(regno);
                ++regno;
            }
            code.addOpcode(83);
        }
        return 8;
    }

    protected void compileUnwrapValue(CtClass type, Bytecode code) throws CompileError {
        if (type == CtClass.voidType) {
            this.addNullIfVoid();
            return;
        }
        if (this.exprType == 344) {
            throw new CompileError("invalid type for " + this.returnCastName);
        }
        if (type instanceof CtPrimitiveType) {
            CtPrimitiveType pt2 = (CtPrimitiveType)type;
            String wrapper = pt2.getWrapperName();
            code.addCheckcast(wrapper);
            code.addInvokevirtual(wrapper, pt2.getGetMethodName(), pt2.getGetMethodDescriptor());
            this.setType(type);
        } else {
            code.addCheckcast(type);
            this.setType(type);
        }
    }

    public void setType(CtClass type) throws CompileError {
        this.setType(type, 0);
    }

    private void setType(CtClass type, int dim) throws CompileError {
        if (type.isPrimitive()) {
            CtPrimitiveType pt2 = (CtPrimitiveType)type;
            this.exprType = MemberResolver.descToType(pt2.getDescriptor());
            this.arrayDim = dim;
            this.className = null;
        } else if (type.isArray()) {
            try {
                this.setType(type.getComponentType(), dim + 1);
            }
            catch (NotFoundException e2) {
                throw new CompileError("undefined type: " + type.getName());
            }
        } else {
            this.exprType = 307;
            this.arrayDim = dim;
            this.className = MemberResolver.javaToJvmName(type.getName());
        }
    }

    public void doNumCast(CtClass type) throws CompileError {
        if (this.arrayDim == 0 && !JvstCodeGen.isRefType(this.exprType)) {
            if (type instanceof CtPrimitiveType) {
                CtPrimitiveType pt2 = (CtPrimitiveType)type;
                this.atNumCastExpr(this.exprType, MemberResolver.descToType(pt2.getDescriptor()));
            } else {
                throw new CompileError("type mismatch");
            }
        }
    }
}

