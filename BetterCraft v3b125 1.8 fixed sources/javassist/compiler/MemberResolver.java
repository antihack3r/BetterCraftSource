/*
 * Decompiled with CFR 0.152.
 */
package javassist.compiler;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.ClassFile;
import javassist.bytecode.Descriptor;
import javassist.bytecode.MethodInfo;
import javassist.compiler.CompileError;
import javassist.compiler.NoFieldException;
import javassist.compiler.TokenId;
import javassist.compiler.ast.ASTList;
import javassist.compiler.ast.ASTree;
import javassist.compiler.ast.Declarator;
import javassist.compiler.ast.Keyword;
import javassist.compiler.ast.Symbol;

public class MemberResolver
implements TokenId {
    private ClassPool classPool;
    private static final int YES = 0;
    private static final int NO = -1;
    private static final String INVALID = "<invalid>";
    private static Map<ClassPool, Reference<Map<String, String>>> invalidNamesMap = new WeakHashMap<ClassPool, Reference<Map<String, String>>>();
    private Map<String, String> invalidNames = null;

    public MemberResolver(ClassPool cp2) {
        this.classPool = cp2;
    }

    public ClassPool getClassPool() {
        return this.classPool;
    }

    private static void fatal() throws CompileError {
        throw new CompileError("fatal");
    }

    public Method lookupMethod(CtClass clazz, CtClass currentClass, MethodInfo current, String methodName, int[] argTypes, int[] argDims, String[] argClassNames) throws CompileError {
        Method m2;
        int res;
        Method maybe = null;
        if (current != null && clazz == currentClass && current.getName().equals(methodName) && (res = this.compareSignature(current.getDescriptor(), argTypes, argDims, argClassNames)) != -1) {
            Method r2 = new Method(clazz, current, res);
            if (res == 0) {
                return r2;
            }
            maybe = r2;
        }
        if ((m2 = this.lookupMethod(clazz, methodName, argTypes, argDims, argClassNames, maybe != null)) != null) {
            return m2;
        }
        return maybe;
    }

    private Method lookupMethod(CtClass clazz, String methodName, int[] argTypes, int[] argDims, String[] argClassNames, boolean onlyExact) throws CompileError {
        Method maybe = null;
        ClassFile cf2 = clazz.getClassFile2();
        if (cf2 != null) {
            List<MethodInfo> list = cf2.getMethods();
            for (MethodInfo minfo : list) {
                int res;
                if (!minfo.getName().equals(methodName) || (minfo.getAccessFlags() & 0x40) != 0 || (res = this.compareSignature(minfo.getDescriptor(), argTypes, argDims, argClassNames)) == -1) continue;
                Method r2 = new Method(clazz, minfo, res);
                if (res == 0) {
                    return r2;
                }
                if (maybe != null && maybe.notmatch <= res) continue;
                maybe = r2;
            }
        }
        if (onlyExact) {
            maybe = null;
        } else if (maybe != null) {
            return maybe;
        }
        int mod = clazz.getModifiers();
        boolean isIntf = Modifier.isInterface(mod);
        try {
            Method r3;
            CtClass pclazz;
            if (!isIntf && (pclazz = clazz.getSuperclass()) != null && (r3 = this.lookupMethod(pclazz, methodName, argTypes, argDims, argClassNames, onlyExact)) != null) {
                return r3;
            }
        }
        catch (NotFoundException pclazz) {
            // empty catch block
        }
        try {
            Method r4;
            CtClass pclazz;
            CtClass[] ifs;
            for (CtClass intf : ifs = clazz.getInterfaces()) {
                Method r5 = this.lookupMethod(intf, methodName, argTypes, argDims, argClassNames, onlyExact);
                if (r5 == null) continue;
                return r5;
            }
            if (isIntf && (pclazz = clazz.getSuperclass()) != null && (r4 = this.lookupMethod(pclazz, methodName, argTypes, argDims, argClassNames, onlyExact)) != null) {
                return r4;
            }
        }
        catch (NotFoundException notFoundException) {
            // empty catch block
        }
        return maybe;
    }

    private int compareSignature(String desc, int[] argTypes, int[] argDims, String[] argClassNames) throws CompileError {
        int result = 0;
        int i2 = 1;
        int nArgs = argTypes.length;
        if (nArgs != Descriptor.numOfParameters(desc)) {
            return -1;
        }
        int len = desc.length();
        int n2 = 0;
        while (i2 < len) {
            char c2;
            if ((c2 = desc.charAt(i2++)) == ')') {
                return n2 == nArgs ? result : -1;
            }
            if (n2 >= nArgs) {
                return -1;
            }
            int dim = 0;
            while (c2 == '[') {
                ++dim;
                c2 = desc.charAt(i2++);
            }
            if (argTypes[n2] == 412) {
                if (dim == 0 && c2 != 'L') {
                    return -1;
                }
                if (c2 == 'L') {
                    i2 = desc.indexOf(59, i2) + 1;
                }
            } else if (argDims[n2] != dim) {
                if (dim != 0 || c2 != 'L' || !desc.startsWith("java/lang/Object;", i2)) {
                    return -1;
                }
                i2 = desc.indexOf(59, i2) + 1;
                ++result;
                if (i2 <= 0) {
                    return -1;
                }
            } else if (c2 == 'L') {
                int j2;
                block23: {
                    j2 = desc.indexOf(59, i2);
                    if (j2 < 0 || argTypes[n2] != 307) {
                        return -1;
                    }
                    String cname = desc.substring(i2, j2);
                    if (!cname.equals(argClassNames[n2])) {
                        CtClass clazz = this.lookupClassByJvmName(argClassNames[n2]);
                        try {
                            if (clazz.subtypeOf(this.lookupClassByJvmName(cname))) {
                                ++result;
                                break block23;
                            }
                            return -1;
                        }
                        catch (NotFoundException e2) {
                            ++result;
                        }
                    }
                }
                i2 = j2 + 1;
            } else {
                int at2;
                int t2 = MemberResolver.descToType(c2);
                if (t2 != (at2 = argTypes[n2])) {
                    if (t2 == 324 && (at2 == 334 || at2 == 303 || at2 == 306)) {
                        ++result;
                    } else {
                        return -1;
                    }
                }
            }
            ++n2;
        }
        return -1;
    }

    public CtField lookupFieldByJvmName2(String jvmClassName, Symbol fieldSym, ASTree expr) throws NoFieldException {
        String field = fieldSym.get();
        CtClass cc2 = null;
        try {
            cc2 = this.lookupClass(MemberResolver.jvmToJavaName(jvmClassName), true);
        }
        catch (CompileError e2) {
            throw new NoFieldException(jvmClassName + "/" + field, expr);
        }
        try {
            return cc2.getField(field);
        }
        catch (NotFoundException e3) {
            jvmClassName = MemberResolver.javaToJvmName(cc2.getName());
            throw new NoFieldException(jvmClassName + "$" + field, expr);
        }
    }

    public CtField lookupFieldByJvmName(String jvmClassName, Symbol fieldName) throws CompileError {
        return this.lookupField(MemberResolver.jvmToJavaName(jvmClassName), fieldName);
    }

    public CtField lookupField(String className, Symbol fieldName) throws CompileError {
        CtClass cc2 = this.lookupClass(className, false);
        try {
            return cc2.getField(fieldName.get());
        }
        catch (NotFoundException notFoundException) {
            throw new CompileError("no such field: " + fieldName.get());
        }
    }

    public CtClass lookupClassByName(ASTList name) throws CompileError {
        return this.lookupClass(Declarator.astToClassName(name, '.'), false);
    }

    public CtClass lookupClassByJvmName(String jvmName) throws CompileError {
        return this.lookupClass(MemberResolver.jvmToJavaName(jvmName), false);
    }

    public CtClass lookupClass(Declarator decl) throws CompileError {
        return this.lookupClass(decl.getType(), decl.getArrayDim(), decl.getClassName());
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public CtClass lookupClass(int type, int dim, String classname) throws CompileError {
        String cname = "";
        if (type == 307) {
            CtClass clazz = this.lookupClassByJvmName(classname);
            if (dim <= 0) return clazz;
            cname = clazz.getName();
        } else {
            cname = MemberResolver.getTypeName(type);
        }
        while (dim-- > 0) {
            cname = cname + "[]";
        }
        return this.lookupClass(cname, false);
    }

    static String getTypeName(int type) throws CompileError {
        String cname = "";
        switch (type) {
            case 301: {
                cname = "boolean";
                break;
            }
            case 306: {
                cname = "char";
                break;
            }
            case 303: {
                cname = "byte";
                break;
            }
            case 334: {
                cname = "short";
                break;
            }
            case 324: {
                cname = "int";
                break;
            }
            case 326: {
                cname = "long";
                break;
            }
            case 317: {
                cname = "float";
                break;
            }
            case 312: {
                cname = "double";
                break;
            }
            case 344: {
                cname = "void";
                break;
            }
            default: {
                MemberResolver.fatal();
            }
        }
        return cname;
    }

    public CtClass lookupClass(String name, boolean notCheckInner) throws CompileError {
        Map<String, String> cache = this.getInvalidNames();
        String found = cache.get(name);
        if (found == INVALID) {
            throw new CompileError("no such class: " + name);
        }
        if (found != null) {
            try {
                return this.classPool.get(found);
            }
            catch (NotFoundException notFoundException) {
                // empty catch block
            }
        }
        CtClass cc2 = null;
        try {
            cc2 = this.lookupClass0(name, notCheckInner);
        }
        catch (NotFoundException e2) {
            cc2 = this.searchImports(name);
        }
        cache.put(name, cc2.getName());
        return cc2;
    }

    public static int getInvalidMapSize() {
        return invalidNamesMap.size();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private Map<String, String> getInvalidNames() {
        Map<String, String> ht2 = this.invalidNames;
        if (ht2 != null) return ht2;
        Class<MemberResolver> clazz = MemberResolver.class;
        synchronized (MemberResolver.class) {
            Reference<Map<String, String>> ref = invalidNamesMap.get(this.classPool);
            if (ref != null) {
                ht2 = ref.get();
            }
            if (ht2 == null) {
                ht2 = new Hashtable<String, String>();
                invalidNamesMap.put(this.classPool, new WeakReference<Map<String, String>>(ht2));
            }
            // ** MonitorExit[var2_2] (shouldn't be in output)
            this.invalidNames = ht2;
            return ht2;
        }
    }

    private CtClass searchImports(String orgName) throws CompileError {
        if (orgName.indexOf(46) < 0) {
            Iterator<String> it2 = this.classPool.getImportedPackages();
            while (it2.hasNext()) {
                String pac = it2.next();
                String fqName = pac.replaceAll("\\.$", "") + "." + orgName;
                try {
                    return this.classPool.get(fqName);
                }
                catch (NotFoundException e2) {
                    try {
                        if (!pac.endsWith("." + orgName)) continue;
                        return this.classPool.get(pac);
                    }
                    catch (NotFoundException notFoundException) {
                    }
                }
            }
        }
        this.getInvalidNames().put(orgName, INVALID);
        throw new CompileError("no such class: " + orgName);
    }

    private CtClass lookupClass0(String classname, boolean notCheckInner) throws NotFoundException {
        CtClass cc2 = null;
        do {
            try {
                cc2 = this.classPool.get(classname);
            }
            catch (NotFoundException e2) {
                int i2 = classname.lastIndexOf(46);
                if (notCheckInner || i2 < 0) {
                    throw e2;
                }
                StringBuffer sbuf = new StringBuffer(classname);
                sbuf.setCharAt(i2, '$');
                classname = sbuf.toString();
            }
        } while (cc2 == null);
        return cc2;
    }

    public String resolveClassName(ASTList name) throws CompileError {
        if (name == null) {
            return null;
        }
        return MemberResolver.javaToJvmName(this.lookupClassByName(name).getName());
    }

    public String resolveJvmClassName(String jvmName) throws CompileError {
        if (jvmName == null) {
            return null;
        }
        return MemberResolver.javaToJvmName(this.lookupClassByJvmName(jvmName).getName());
    }

    public static CtClass getSuperclass(CtClass c2) throws CompileError {
        try {
            CtClass sc2 = c2.getSuperclass();
            if (sc2 != null) {
                return sc2;
            }
        }
        catch (NotFoundException notFoundException) {
            // empty catch block
        }
        throw new CompileError("cannot find the super class of " + c2.getName());
    }

    public static CtClass getSuperInterface(CtClass c2, String interfaceName) throws CompileError {
        try {
            CtClass[] intfs = c2.getInterfaces();
            for (int i2 = 0; i2 < intfs.length; ++i2) {
                if (!intfs[i2].getName().equals(interfaceName)) continue;
                return intfs[i2];
            }
        }
        catch (NotFoundException notFoundException) {
            // empty catch block
        }
        throw new CompileError("cannot find the super interface " + interfaceName + " of " + c2.getName());
    }

    public static String javaToJvmName(String classname) {
        return classname.replace('.', '/');
    }

    public static String jvmToJavaName(String classname) {
        return classname.replace('/', '.');
    }

    public static int descToType(char c2) throws CompileError {
        switch (c2) {
            case 'Z': {
                return 301;
            }
            case 'C': {
                return 306;
            }
            case 'B': {
                return 303;
            }
            case 'S': {
                return 334;
            }
            case 'I': {
                return 324;
            }
            case 'J': {
                return 326;
            }
            case 'F': {
                return 317;
            }
            case 'D': {
                return 312;
            }
            case 'V': {
                return 344;
            }
            case 'L': 
            case '[': {
                return 307;
            }
        }
        MemberResolver.fatal();
        return 344;
    }

    public static int getModifiers(ASTList mods) {
        int m2 = 0;
        while (mods != null) {
            Keyword k2 = (Keyword)mods.head();
            mods = mods.tail();
            switch (k2.get()) {
                case 335: {
                    m2 |= 8;
                    break;
                }
                case 315: {
                    m2 |= 0x10;
                    break;
                }
                case 338: {
                    m2 |= 0x20;
                    break;
                }
                case 300: {
                    m2 |= 0x400;
                    break;
                }
                case 332: {
                    m2 |= 1;
                    break;
                }
                case 331: {
                    m2 |= 4;
                    break;
                }
                case 330: {
                    m2 |= 2;
                    break;
                }
                case 345: {
                    m2 |= 0x40;
                    break;
                }
                case 342: {
                    m2 |= 0x80;
                    break;
                }
                case 347: {
                    m2 |= 0x800;
                }
            }
        }
        return m2;
    }

    public static class Method {
        public CtClass declaring;
        public MethodInfo info;
        public int notmatch;

        public Method(CtClass c2, MethodInfo i2, int n2) {
            this.declaring = c2;
            this.info = i2;
            this.notmatch = n2;
        }

        public boolean isStatic() {
            int acc2 = this.info.getAccessFlags();
            return (acc2 & 8) != 0;
        }
    }
}

