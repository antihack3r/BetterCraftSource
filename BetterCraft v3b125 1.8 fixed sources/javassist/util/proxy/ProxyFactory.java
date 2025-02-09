/*
 * Decompiled with CFR 0.152.
 */
package javassist.util.proxy;

import java.lang.invoke.MethodHandles;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import javassist.CannotCompileException;
import javassist.bytecode.Bytecode;
import javassist.bytecode.ClassFile;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.Descriptor;
import javassist.bytecode.DuplicateMemberException;
import javassist.bytecode.ExceptionsAttribute;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.SignatureAttribute;
import javassist.bytecode.StackMapTable;
import javassist.util.proxy.FactoryHelper;
import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyObject;
import javassist.util.proxy.RuntimeSupport;
import javassist.util.proxy.SecurityActions;

public class ProxyFactory {
    private Class<?> superClass = null;
    private Class<?>[] interfaces = null;
    private MethodFilter methodFilter = null;
    private MethodHandler handler = null;
    private List<Map.Entry<String, Method>> signatureMethods = null;
    private boolean hasGetHandler = false;
    private byte[] signature = null;
    private String classname;
    private String basename;
    private String superName;
    private Class<?> thisClass = null;
    private String genericSignature = null;
    private boolean factoryUseCache = useCache;
    private boolean factoryWriteReplace = useWriteReplace;
    public static boolean onlyPublicMethods = false;
    public String writeDirectory = null;
    private static final Class<?> OBJECT_TYPE = Object.class;
    private static final String HOLDER = "_methods_";
    private static final String HOLDER_TYPE = "[Ljava/lang/reflect/Method;";
    private static final String FILTER_SIGNATURE_FIELD = "_filter_signature";
    private static final String FILTER_SIGNATURE_TYPE = "[B";
    private static final String HANDLER = "handler";
    private static final String NULL_INTERCEPTOR_HOLDER = "javassist.util.proxy.RuntimeSupport";
    private static final String DEFAULT_INTERCEPTOR = "default_interceptor";
    private static final String HANDLER_TYPE = 'L' + MethodHandler.class.getName().replace('.', '/') + ';';
    private static final String HANDLER_SETTER = "setHandler";
    private static final String HANDLER_SETTER_TYPE = "(" + HANDLER_TYPE + ")V";
    private static final String HANDLER_GETTER = "getHandler";
    private static final String HANDLER_GETTER_TYPE = "()" + HANDLER_TYPE;
    private static final String SERIAL_VERSION_UID_FIELD = "serialVersionUID";
    private static final String SERIAL_VERSION_UID_TYPE = "J";
    private static final long SERIAL_VERSION_UID_VALUE = -1L;
    public static volatile boolean useCache = true;
    public static volatile boolean useWriteReplace = true;
    private static Map<ClassLoader, Map<String, ProxyDetails>> proxyCache = new WeakHashMap<ClassLoader, Map<String, ProxyDetails>>();
    private static char[] hexDigits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    public static ClassLoaderProvider classLoaderProvider = new ClassLoaderProvider(){

        @Override
        public ClassLoader get(ProxyFactory pf2) {
            return pf2.getClassLoader0();
        }
    };
    public static UniqueName nameGenerator = new UniqueName(){
        private final String sep = "_$$_jvst" + Integer.toHexString(this.hashCode() & 0xFFF) + "_";
        private int counter = 0;

        @Override
        public String get(String classname) {
            return classname + this.sep + Integer.toHexString(this.counter++);
        }
    };
    private static final String packageForJavaBase = "javassist.util.proxy.";
    private static Comparator<Map.Entry<String, Method>> sorter = new Comparator<Map.Entry<String, Method>>(){

        @Override
        public int compare(Map.Entry<String, Method> e1, Map.Entry<String, Method> e2) {
            return e1.getKey().compareTo(e2.getKey());
        }
    };
    private static final String HANDLER_GETTER_KEY = "getHandler:()";

    public boolean isUseCache() {
        return this.factoryUseCache;
    }

    public void setUseCache(boolean useCache) {
        if (this.handler != null && useCache) {
            throw new RuntimeException("caching cannot be enabled if the factory default interceptor has been set");
        }
        this.factoryUseCache = useCache;
    }

    public boolean isUseWriteReplace() {
        return this.factoryWriteReplace;
    }

    public void setUseWriteReplace(boolean useWriteReplace) {
        this.factoryWriteReplace = useWriteReplace;
    }

    public static boolean isProxyClass(Class<?> cl2) {
        return Proxy.class.isAssignableFrom(cl2);
    }

    public void setSuperclass(Class<?> clazz) {
        this.superClass = clazz;
        this.signature = null;
    }

    public Class<?> getSuperclass() {
        return this.superClass;
    }

    public void setInterfaces(Class<?>[] ifs) {
        this.interfaces = ifs;
        this.signature = null;
    }

    public Class<?>[] getInterfaces() {
        return this.interfaces;
    }

    public void setFilter(MethodFilter mf2) {
        this.methodFilter = mf2;
        this.signature = null;
    }

    public void setGenericSignature(String sig) {
        this.genericSignature = sig;
    }

    public Class<?> createClass() {
        if (this.signature == null) {
            this.computeSignature(this.methodFilter);
        }
        return this.createClass1(null);
    }

    public Class<?> createClass(MethodFilter filter) {
        this.computeSignature(filter);
        return this.createClass1(null);
    }

    Class<?> createClass(byte[] signature) {
        this.installSignature(signature);
        return this.createClass1(null);
    }

    public Class<?> createClass(MethodHandles.Lookup lookup) {
        if (this.signature == null) {
            this.computeSignature(this.methodFilter);
        }
        return this.createClass1(lookup);
    }

    public Class<?> createClass(MethodHandles.Lookup lookup, MethodFilter filter) {
        this.computeSignature(filter);
        return this.createClass1(lookup);
    }

    Class<?> createClass(MethodHandles.Lookup lookup, byte[] signature) {
        this.installSignature(signature);
        return this.createClass1(lookup);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Class<?> createClass1(MethodHandles.Lookup lookup) {
        Class<?> result = this.thisClass;
        if (result == null) {
            ClassLoader cl2 = this.getClassLoader();
            Map<ClassLoader, Map<String, ProxyDetails>> map = proxyCache;
            synchronized (map) {
                if (this.factoryUseCache) {
                    this.createClass2(cl2, lookup);
                } else {
                    this.createClass3(cl2, lookup);
                }
                result = this.thisClass;
                this.thisClass = null;
            }
        }
        return result;
    }

    public String getKey(Class<?> superClass, Class<?>[] interfaces, byte[] signature, boolean useWriteReplace) {
        int i2;
        StringBuffer sbuf = new StringBuffer();
        if (superClass != null) {
            sbuf.append(superClass.getName());
        }
        sbuf.append(":");
        for (i2 = 0; i2 < interfaces.length; ++i2) {
            sbuf.append(interfaces[i2].getName());
            sbuf.append(":");
        }
        for (i2 = 0; i2 < signature.length; ++i2) {
            byte b2 = signature[i2];
            int lo2 = b2 & 0xF;
            int hi2 = b2 >> 4 & 0xF;
            sbuf.append(hexDigits[lo2]);
            sbuf.append(hexDigits[hi2]);
        }
        if (useWriteReplace) {
            sbuf.append(":w");
        }
        return sbuf.toString();
    }

    private void createClass2(ClassLoader cl2, MethodHandles.Lookup lookup) {
        ProxyDetails details;
        String key = this.getKey(this.superClass, this.interfaces, this.signature, this.factoryWriteReplace);
        Map<String, ProxyDetails> cacheForTheLoader = proxyCache.get(cl2);
        if (cacheForTheLoader == null) {
            cacheForTheLoader = new HashMap<String, ProxyDetails>();
            proxyCache.put(cl2, cacheForTheLoader);
        }
        if ((details = cacheForTheLoader.get(key)) != null) {
            Reference<Class<?>> reference = details.proxyClass;
            this.thisClass = reference.get();
            if (this.thisClass != null) {
                return;
            }
        }
        this.createClass3(cl2, lookup);
        details = new ProxyDetails(this.signature, this.thisClass, this.factoryWriteReplace);
        cacheForTheLoader.put(key, details);
    }

    private void createClass3(ClassLoader cl2, MethodHandles.Lookup lookup) {
        this.allocateClassName();
        try {
            ClassFile cf2 = this.make();
            if (this.writeDirectory != null) {
                FactoryHelper.writeFile(cf2, this.writeDirectory);
            }
            this.thisClass = lookup == null ? FactoryHelper.toClass(cf2, this.getClassInTheSamePackage(), cl2, this.getDomain()) : FactoryHelper.toClass(cf2, lookup);
            this.setField(FILTER_SIGNATURE_FIELD, this.signature);
            if (!this.factoryUseCache) {
                this.setField(DEFAULT_INTERCEPTOR, this.handler);
            }
        }
        catch (CannotCompileException e2) {
            throw new RuntimeException(e2.getMessage(), e2);
        }
    }

    private Class<?> getClassInTheSamePackage() {
        if (this.basename.startsWith(packageForJavaBase)) {
            return this.getClass();
        }
        if (this.superClass != null && this.superClass != OBJECT_TYPE) {
            return this.superClass;
        }
        if (this.interfaces != null && this.interfaces.length > 0) {
            return this.interfaces[0];
        }
        return this.getClass();
    }

    private void setField(String fieldName, Object value) {
        if (this.thisClass != null && value != null) {
            try {
                Field f2 = this.thisClass.getField(fieldName);
                SecurityActions.setAccessible(f2, true);
                f2.set(null, value);
                SecurityActions.setAccessible(f2, false);
            }
            catch (Exception e2) {
                throw new RuntimeException(e2);
            }
        }
    }

    static byte[] getFilterSignature(Class<?> clazz) {
        return (byte[])ProxyFactory.getField(clazz, FILTER_SIGNATURE_FIELD);
    }

    private static Object getField(Class<?> clazz, String fieldName) {
        try {
            Field f2 = clazz.getField(fieldName);
            f2.setAccessible(true);
            Object value = f2.get(null);
            f2.setAccessible(false);
            return value;
        }
        catch (Exception e2) {
            throw new RuntimeException(e2);
        }
    }

    public static MethodHandler getHandler(Proxy p2) {
        try {
            Field f2 = p2.getClass().getDeclaredField(HANDLER);
            f2.setAccessible(true);
            Object value = f2.get(p2);
            f2.setAccessible(false);
            return (MethodHandler)value;
        }
        catch (Exception e2) {
            throw new RuntimeException(e2);
        }
    }

    protected ClassLoader getClassLoader() {
        return classLoaderProvider.get(this);
    }

    protected ClassLoader getClassLoader0() {
        ClassLoader loader = null;
        if (this.superClass != null && !this.superClass.getName().equals("java.lang.Object")) {
            loader = this.superClass.getClassLoader();
        } else if (this.interfaces != null && this.interfaces.length > 0) {
            loader = this.interfaces[0].getClassLoader();
        }
        if (loader == null && (loader = this.getClass().getClassLoader()) == null && (loader = Thread.currentThread().getContextClassLoader()) == null) {
            loader = ClassLoader.getSystemClassLoader();
        }
        return loader;
    }

    protected ProtectionDomain getDomain() {
        Class<?> clazz = this.superClass != null && !this.superClass.getName().equals("java.lang.Object") ? this.superClass : (this.interfaces != null && this.interfaces.length > 0 ? this.interfaces[0] : this.getClass());
        return clazz.getProtectionDomain();
    }

    public Object create(Class<?>[] paramTypes, Object[] args, MethodHandler mh) throws NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Object obj = this.create(paramTypes, args);
        ((Proxy)obj).setHandler(mh);
        return obj;
    }

    public Object create(Class<?>[] paramTypes, Object[] args) throws NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Class<?> c2 = this.createClass();
        Constructor<?> cons = c2.getConstructor(paramTypes);
        return cons.newInstance(args);
    }

    @Deprecated
    public void setHandler(MethodHandler mi) {
        if (this.factoryUseCache && mi != null) {
            this.factoryUseCache = false;
            this.thisClass = null;
        }
        this.handler = mi;
        this.setField(DEFAULT_INTERCEPTOR, this.handler);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static String makeProxyName(String classname) {
        UniqueName uniqueName = nameGenerator;
        synchronized (uniqueName) {
            return nameGenerator.get(classname);
        }
    }

    private ClassFile make() throws CannotCompileException {
        ClassFile cf2 = new ClassFile(false, this.classname, this.superName);
        cf2.setAccessFlags(1);
        ProxyFactory.setInterfaces(cf2, this.interfaces, this.hasGetHandler ? Proxy.class : ProxyObject.class);
        ConstPool pool = cf2.getConstPool();
        if (!this.factoryUseCache) {
            FieldInfo finfo = new FieldInfo(pool, DEFAULT_INTERCEPTOR, HANDLER_TYPE);
            finfo.setAccessFlags(9);
            cf2.addField(finfo);
        }
        FieldInfo finfo2 = new FieldInfo(pool, HANDLER, HANDLER_TYPE);
        finfo2.setAccessFlags(2);
        cf2.addField(finfo2);
        FieldInfo finfo3 = new FieldInfo(pool, FILTER_SIGNATURE_FIELD, FILTER_SIGNATURE_TYPE);
        finfo3.setAccessFlags(9);
        cf2.addField(finfo3);
        FieldInfo finfo4 = new FieldInfo(pool, SERIAL_VERSION_UID_FIELD, SERIAL_VERSION_UID_TYPE);
        finfo4.setAccessFlags(25);
        cf2.addField(finfo4);
        if (this.genericSignature != null) {
            SignatureAttribute sa2 = new SignatureAttribute(pool, this.genericSignature);
            cf2.addAttribute(sa2);
        }
        this.makeConstructors(this.classname, cf2, pool, this.classname);
        ArrayList<Find2MethodsArgs> forwarders = new ArrayList<Find2MethodsArgs>();
        int s2 = this.overrideMethods(cf2, pool, this.classname, forwarders);
        ProxyFactory.addClassInitializer(cf2, pool, this.classname, s2, forwarders);
        ProxyFactory.addSetter(this.classname, cf2, pool);
        if (!this.hasGetHandler) {
            ProxyFactory.addGetter(this.classname, cf2, pool);
        }
        if (this.factoryWriteReplace) {
            try {
                cf2.addMethod(ProxyFactory.makeWriteReplace(pool));
            }
            catch (DuplicateMemberException duplicateMemberException) {
                // empty catch block
            }
        }
        this.thisClass = null;
        return cf2;
    }

    private void checkClassAndSuperName() {
        if (this.interfaces == null) {
            this.interfaces = new Class[0];
        }
        if (this.superClass == null) {
            this.superClass = OBJECT_TYPE;
            this.superName = this.superClass.getName();
            this.basename = this.interfaces.length == 0 ? this.superName : this.interfaces[0].getName();
        } else {
            this.basename = this.superName = this.superClass.getName();
        }
        if (Modifier.isFinal(this.superClass.getModifiers())) {
            throw new RuntimeException(this.superName + " is final");
        }
        if (this.basename.startsWith("java.") || this.basename.startsWith("jdk.") || onlyPublicMethods) {
            this.basename = packageForJavaBase + this.basename.replace('.', '_');
        }
    }

    private void allocateClassName() {
        this.classname = ProxyFactory.makeProxyName(this.basename);
    }

    private void makeSortedMethodList() {
        this.checkClassAndSuperName();
        this.hasGetHandler = false;
        Map<String, Method> allMethods = this.getMethods(this.superClass, this.interfaces);
        this.signatureMethods = new ArrayList<Map.Entry<String, Method>>(allMethods.entrySet());
        Collections.sort(this.signatureMethods, sorter);
    }

    private void computeSignature(MethodFilter filter) {
        this.makeSortedMethodList();
        int l2 = this.signatureMethods.size();
        int maxBytes = l2 + 7 >> 3;
        this.signature = new byte[maxBytes];
        for (int idx = 0; idx < l2; ++idx) {
            Method m2 = this.signatureMethods.get(idx).getValue();
            int mod = m2.getModifiers();
            if (Modifier.isFinal(mod) || Modifier.isStatic(mod) || !ProxyFactory.isVisible(mod, this.basename, m2) || filter != null && !filter.isHandled(m2)) continue;
            this.setBit(this.signature, idx);
        }
    }

    private void installSignature(byte[] signature) {
        this.makeSortedMethodList();
        int l2 = this.signatureMethods.size();
        int maxBytes = l2 + 7 >> 3;
        if (signature.length != maxBytes) {
            throw new RuntimeException("invalid filter signature length for deserialized proxy class");
        }
        this.signature = signature;
    }

    private boolean testBit(byte[] signature, int idx) {
        int byteIdx = idx >> 3;
        if (byteIdx > signature.length) {
            return false;
        }
        byte sigByte = signature[byteIdx];
        int bitIdx = idx & 7;
        int mask = 1 << bitIdx;
        return (sigByte & mask) != 0;
    }

    private void setBit(byte[] signature, int idx) {
        int byteIdx = idx >> 3;
        if (byteIdx < signature.length) {
            int bitIdx = idx & 7;
            int mask = 1 << bitIdx;
            byte sigByte = signature[byteIdx];
            signature[byteIdx] = (byte)(sigByte | mask);
        }
    }

    private static void setInterfaces(ClassFile cf2, Class<?>[] interfaces, Class<?> proxyClass) {
        String[] list;
        String setterIntf = proxyClass.getName();
        if (interfaces == null || interfaces.length == 0) {
            list = new String[]{setterIntf};
        } else {
            list = new String[interfaces.length + 1];
            for (int i2 = 0; i2 < interfaces.length; ++i2) {
                list[i2] = interfaces[i2].getName();
            }
            list[interfaces.length] = setterIntf;
        }
        cf2.setInterfaces(list);
    }

    private static void addClassInitializer(ClassFile cf2, ConstPool cp2, String classname, int size, List<Find2MethodsArgs> forwarders) throws CannotCompileException {
        FieldInfo finfo = new FieldInfo(cp2, HOLDER, HOLDER_TYPE);
        finfo.setAccessFlags(10);
        cf2.addField(finfo);
        MethodInfo minfo = new MethodInfo(cp2, "<clinit>", "()V");
        minfo.setAccessFlags(8);
        ProxyFactory.setThrows(minfo, cp2, new Class[]{ClassNotFoundException.class});
        Bytecode code = new Bytecode(cp2, 0, 2);
        code.addIconst(size * 2);
        code.addAnewarray("java.lang.reflect.Method");
        boolean varArray = false;
        code.addAstore(0);
        code.addLdc(classname);
        code.addInvokestatic("java.lang.Class", "forName", "(Ljava/lang/String;)Ljava/lang/Class;");
        boolean varClass = true;
        code.addAstore(1);
        for (Find2MethodsArgs args : forwarders) {
            ProxyFactory.callFind2Methods(code, args.methodName, args.delegatorName, args.origIndex, args.descriptor, 1, 0);
        }
        code.addAload(0);
        code.addPutstatic(classname, HOLDER, HOLDER_TYPE);
        code.addLconst(-1L);
        code.addPutstatic(classname, SERIAL_VERSION_UID_FIELD, SERIAL_VERSION_UID_TYPE);
        code.addOpcode(177);
        minfo.setCodeAttribute(code.toCodeAttribute());
        cf2.addMethod(minfo);
    }

    private static void callFind2Methods(Bytecode code, String superMethod, String thisMethod, int index, String desc, int classVar, int arrayVar) {
        String findClass = RuntimeSupport.class.getName();
        String findDesc = "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/String;ILjava/lang/String;[Ljava/lang/reflect/Method;)V";
        code.addAload(classVar);
        code.addLdc(superMethod);
        if (thisMethod == null) {
            code.addOpcode(1);
        } else {
            code.addLdc(thisMethod);
        }
        code.addIconst(index);
        code.addLdc(desc);
        code.addAload(arrayVar);
        code.addInvokestatic(findClass, "find2Methods", findDesc);
    }

    private static void addSetter(String classname, ClassFile cf2, ConstPool cp2) throws CannotCompileException {
        MethodInfo minfo = new MethodInfo(cp2, HANDLER_SETTER, HANDLER_SETTER_TYPE);
        minfo.setAccessFlags(1);
        Bytecode code = new Bytecode(cp2, 2, 2);
        code.addAload(0);
        code.addAload(1);
        code.addPutfield(classname, HANDLER, HANDLER_TYPE);
        code.addOpcode(177);
        minfo.setCodeAttribute(code.toCodeAttribute());
        cf2.addMethod(minfo);
    }

    private static void addGetter(String classname, ClassFile cf2, ConstPool cp2) throws CannotCompileException {
        MethodInfo minfo = new MethodInfo(cp2, HANDLER_GETTER, HANDLER_GETTER_TYPE);
        minfo.setAccessFlags(1);
        Bytecode code = new Bytecode(cp2, 1, 1);
        code.addAload(0);
        code.addGetfield(classname, HANDLER, HANDLER_TYPE);
        code.addOpcode(176);
        minfo.setCodeAttribute(code.toCodeAttribute());
        cf2.addMethod(minfo);
    }

    private int overrideMethods(ClassFile cf2, ConstPool cp2, String className, List<Find2MethodsArgs> forwarders) throws CannotCompileException {
        String prefix = ProxyFactory.makeUniqueName("_d", this.signatureMethods);
        Iterator<Map.Entry<String, Method>> it2 = this.signatureMethods.iterator();
        int index = 0;
        while (it2.hasNext()) {
            Map.Entry<String, Method> e2 = it2.next();
            if ((ClassFile.MAJOR_VERSION < 49 || !ProxyFactory.isBridge(e2.getValue())) && this.testBit(this.signature, index)) {
                this.override(className, e2.getValue(), prefix, index, ProxyFactory.keyToDesc(e2.getKey(), e2.getValue()), cf2, cp2, forwarders);
            }
            ++index;
        }
        return index;
    }

    private static boolean isBridge(Method m2) {
        return m2.isBridge();
    }

    private void override(String thisClassname, Method meth, String prefix, int index, String desc, ClassFile cf2, ConstPool cp2, List<Find2MethodsArgs> forwarders) throws CannotCompileException {
        Class<?> declClass = meth.getDeclaringClass();
        String delegatorName = prefix + index + meth.getName();
        if (Modifier.isAbstract(meth.getModifiers())) {
            delegatorName = null;
        } else {
            MethodInfo delegator = this.makeDelegator(meth, desc, cp2, declClass, delegatorName);
            delegator.setAccessFlags(delegator.getAccessFlags() & 0xFFFFFFBF);
            cf2.addMethod(delegator);
        }
        MethodInfo forwarder = ProxyFactory.makeForwarder(thisClassname, meth, desc, cp2, declClass, delegatorName, index, forwarders);
        cf2.addMethod(forwarder);
    }

    private void makeConstructors(String thisClassName, ClassFile cf2, ConstPool cp2, String classname) throws CannotCompileException {
        Constructor<?>[] cons = SecurityActions.getDeclaredConstructors(this.superClass);
        boolean doHandlerInit = !this.factoryUseCache;
        for (int i2 = 0; i2 < cons.length; ++i2) {
            Constructor<?> c2 = cons[i2];
            int mod = c2.getModifiers();
            if (Modifier.isFinal(mod) || Modifier.isPrivate(mod) || !ProxyFactory.isVisible(mod, this.basename, c2)) continue;
            MethodInfo m2 = ProxyFactory.makeConstructor(thisClassName, c2, cp2, this.superClass, doHandlerInit);
            cf2.addMethod(m2);
        }
    }

    private static String makeUniqueName(String name, List<Map.Entry<String, Method>> sortedMethods) {
        if (ProxyFactory.makeUniqueName0(name, sortedMethods.iterator())) {
            return name;
        }
        for (int i2 = 100; i2 < 999; ++i2) {
            String s2 = name + i2;
            if (!ProxyFactory.makeUniqueName0(s2, sortedMethods.iterator())) continue;
            return s2;
        }
        throw new RuntimeException("cannot make a unique method name");
    }

    private static boolean makeUniqueName0(String name, Iterator<Map.Entry<String, Method>> it2) {
        while (it2.hasNext()) {
            if (!it2.next().getKey().startsWith(name)) continue;
            return false;
        }
        return true;
    }

    private static boolean isVisible(int mod, String from, Member meth) {
        if ((mod & 2) != 0) {
            return false;
        }
        if ((mod & 5) != 0) {
            return true;
        }
        String p2 = ProxyFactory.getPackageName(from);
        String q2 = ProxyFactory.getPackageName(meth.getDeclaringClass().getName());
        if (p2 == null) {
            return q2 == null;
        }
        return p2.equals(q2);
    }

    private static String getPackageName(String name) {
        int i2 = name.lastIndexOf(46);
        if (i2 < 0) {
            return null;
        }
        return name.substring(0, i2);
    }

    private Map<String, Method> getMethods(Class<?> superClass, Class<?>[] interfaceTypes) {
        HashMap<String, Method> hash = new HashMap<String, Method>();
        HashSet set = new HashSet();
        for (int i2 = 0; i2 < interfaceTypes.length; ++i2) {
            this.getMethods(hash, interfaceTypes[i2], set);
        }
        this.getMethods(hash, superClass, set);
        return hash;
    }

    private void getMethods(Map<String, Method> hash, Class<?> clazz, Set<Class<?>> visitedClasses) {
        if (!visitedClasses.add(clazz)) {
            return;
        }
        Class<?>[] ifs = clazz.getInterfaces();
        for (int i2 = 0; i2 < ifs.length; ++i2) {
            this.getMethods(hash, ifs[i2], visitedClasses);
        }
        Class<?> parent = clazz.getSuperclass();
        if (parent != null) {
            this.getMethods(hash, parent, visitedClasses);
        }
        Method[] methods = SecurityActions.getDeclaredMethods(clazz);
        for (int i3 = 0; i3 < methods.length; ++i3) {
            Method oldMethod;
            if (Modifier.isPrivate(methods[i3].getModifiers())) continue;
            Method m2 = methods[i3];
            String key = m2.getName() + ':' + RuntimeSupport.makeDescriptor(m2);
            if (key.startsWith(HANDLER_GETTER_KEY)) {
                this.hasGetHandler = true;
            }
            if (null != (oldMethod = hash.put(key, m2)) && ProxyFactory.isBridge(m2) && !Modifier.isPublic(oldMethod.getDeclaringClass().getModifiers()) && !Modifier.isAbstract(oldMethod.getModifiers()) && !ProxyFactory.isDuplicated(i3, methods)) {
                hash.put(key, oldMethod);
            }
            if (null == oldMethod || !Modifier.isPublic(oldMethod.getModifiers()) || Modifier.isPublic(m2.getModifiers())) continue;
            hash.put(key, oldMethod);
        }
    }

    private static boolean isDuplicated(int index, Method[] methods) {
        String name = methods[index].getName();
        for (int i2 = 0; i2 < methods.length; ++i2) {
            if (i2 == index || !name.equals(methods[i2].getName()) || !ProxyFactory.areParametersSame(methods[index], methods[i2])) continue;
            return true;
        }
        return false;
    }

    private static boolean areParametersSame(Method method, Method targetMethod) {
        Class<?>[] targetMethodTypes;
        Class<?>[] methodTypes = method.getParameterTypes();
        if (methodTypes.length == (targetMethodTypes = targetMethod.getParameterTypes()).length) {
            for (int i2 = 0; i2 < methodTypes.length; ++i2) {
                if (methodTypes[i2].getName().equals(targetMethodTypes[i2].getName())) continue;
                return false;
            }
            return true;
        }
        return false;
    }

    private static String keyToDesc(String key, Method m2) {
        return key.substring(key.indexOf(58) + 1);
    }

    private static MethodInfo makeConstructor(String thisClassName, Constructor<?> cons, ConstPool cp2, Class<?> superClass, boolean doHandlerInit) {
        String desc = RuntimeSupport.makeDescriptor(cons.getParameterTypes(), Void.TYPE);
        MethodInfo minfo = new MethodInfo(cp2, "<init>", desc);
        minfo.setAccessFlags(1);
        ProxyFactory.setThrows(minfo, cp2, cons.getExceptionTypes());
        Bytecode code = new Bytecode(cp2, 0, 0);
        if (doHandlerInit) {
            code.addAload(0);
            code.addGetstatic(thisClassName, DEFAULT_INTERCEPTOR, HANDLER_TYPE);
            code.addPutfield(thisClassName, HANDLER, HANDLER_TYPE);
            code.addGetstatic(thisClassName, DEFAULT_INTERCEPTOR, HANDLER_TYPE);
            code.addOpcode(199);
            code.addIndex(10);
        }
        code.addAload(0);
        code.addGetstatic(NULL_INTERCEPTOR_HOLDER, DEFAULT_INTERCEPTOR, HANDLER_TYPE);
        code.addPutfield(thisClassName, HANDLER, HANDLER_TYPE);
        int pc2 = code.currentPc();
        code.addAload(0);
        int s2 = ProxyFactory.addLoadParameters(code, cons.getParameterTypes(), 1);
        code.addInvokespecial(superClass.getName(), "<init>", desc);
        code.addOpcode(177);
        code.setMaxLocals(s2 + 1);
        CodeAttribute ca2 = code.toCodeAttribute();
        minfo.setCodeAttribute(ca2);
        StackMapTable.Writer writer = new StackMapTable.Writer(32);
        writer.sameFrame(pc2);
        ca2.setAttribute(writer.toStackMapTable(cp2));
        return minfo;
    }

    private MethodInfo makeDelegator(Method meth, String desc, ConstPool cp2, Class<?> declClass, String delegatorName) {
        MethodInfo delegator = new MethodInfo(cp2, delegatorName, desc);
        delegator.setAccessFlags(0x11 | meth.getModifiers() & 0xFFFFFAD9);
        ProxyFactory.setThrows(delegator, cp2, meth);
        Bytecode code = new Bytecode(cp2, 0, 0);
        code.addAload(0);
        int s2 = ProxyFactory.addLoadParameters(code, meth.getParameterTypes(), 1);
        Class<?> targetClass = this.invokespecialTarget(declClass);
        code.addInvokespecial(targetClass.isInterface(), cp2.addClassInfo(targetClass.getName()), meth.getName(), desc);
        ProxyFactory.addReturn(code, meth.getReturnType());
        code.setMaxLocals(++s2);
        delegator.setCodeAttribute(code.toCodeAttribute());
        return delegator;
    }

    private Class<?> invokespecialTarget(Class<?> declClass) {
        if (declClass.isInterface()) {
            for (Class<?> i2 : this.interfaces) {
                if (!declClass.isAssignableFrom(i2)) continue;
                return i2;
            }
        }
        return this.superClass;
    }

    private static MethodInfo makeForwarder(String thisClassName, Method meth, String desc, ConstPool cp2, Class<?> declClass, String delegatorName, int index, List<Find2MethodsArgs> forwarders) {
        MethodInfo forwarder = new MethodInfo(cp2, meth.getName(), desc);
        forwarder.setAccessFlags(0x10 | meth.getModifiers() & 0xFFFFFADF);
        ProxyFactory.setThrows(forwarder, cp2, meth);
        int args = Descriptor.paramSize(desc);
        Bytecode code = new Bytecode(cp2, 0, args + 2);
        int origIndex = index * 2;
        int delIndex = index * 2 + 1;
        int arrayVar = args + 1;
        code.addGetstatic(thisClassName, HOLDER, HOLDER_TYPE);
        code.addAstore(arrayVar);
        forwarders.add(new Find2MethodsArgs(meth.getName(), delegatorName, desc, origIndex));
        code.addAload(0);
        code.addGetfield(thisClassName, HANDLER, HANDLER_TYPE);
        code.addAload(0);
        code.addAload(arrayVar);
        code.addIconst(origIndex);
        code.addOpcode(50);
        code.addAload(arrayVar);
        code.addIconst(delIndex);
        code.addOpcode(50);
        ProxyFactory.makeParameterList(code, meth.getParameterTypes());
        code.addInvokeinterface(MethodHandler.class.getName(), "invoke", "(Ljava/lang/Object;Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;[Ljava/lang/Object;)Ljava/lang/Object;", 5);
        Class<?> retType = meth.getReturnType();
        ProxyFactory.addUnwrapper(code, retType);
        ProxyFactory.addReturn(code, retType);
        CodeAttribute ca2 = code.toCodeAttribute();
        forwarder.setCodeAttribute(ca2);
        return forwarder;
    }

    private static void setThrows(MethodInfo minfo, ConstPool cp2, Method orig) {
        Class<?>[] exceptions = orig.getExceptionTypes();
        ProxyFactory.setThrows(minfo, cp2, exceptions);
    }

    private static void setThrows(MethodInfo minfo, ConstPool cp2, Class<?>[] exceptions) {
        if (exceptions.length == 0) {
            return;
        }
        String[] list = new String[exceptions.length];
        for (int i2 = 0; i2 < exceptions.length; ++i2) {
            list[i2] = exceptions[i2].getName();
        }
        ExceptionsAttribute ea2 = new ExceptionsAttribute(cp2);
        ea2.setExceptions(list);
        minfo.setExceptionsAttribute(ea2);
    }

    private static int addLoadParameters(Bytecode code, Class<?>[] params, int offset) {
        int stacksize = 0;
        int n2 = params.length;
        for (int i2 = 0; i2 < n2; ++i2) {
            stacksize += ProxyFactory.addLoad(code, stacksize + offset, params[i2]);
        }
        return stacksize;
    }

    private static int addLoad(Bytecode code, int n2, Class<?> type) {
        if (type.isPrimitive()) {
            if (type == Long.TYPE) {
                code.addLload(n2);
                return 2;
            }
            if (type == Float.TYPE) {
                code.addFload(n2);
            } else {
                if (type == Double.TYPE) {
                    code.addDload(n2);
                    return 2;
                }
                code.addIload(n2);
            }
        } else {
            code.addAload(n2);
        }
        return 1;
    }

    private static int addReturn(Bytecode code, Class<?> type) {
        if (type.isPrimitive()) {
            if (type == Long.TYPE) {
                code.addOpcode(173);
                return 2;
            }
            if (type == Float.TYPE) {
                code.addOpcode(174);
            } else {
                if (type == Double.TYPE) {
                    code.addOpcode(175);
                    return 2;
                }
                if (type == Void.TYPE) {
                    code.addOpcode(177);
                    return 0;
                }
                code.addOpcode(172);
            }
        } else {
            code.addOpcode(176);
        }
        return 1;
    }

    private static void makeParameterList(Bytecode code, Class<?>[] params) {
        int regno = 1;
        int n2 = params.length;
        code.addIconst(n2);
        code.addAnewarray("java/lang/Object");
        for (int i2 = 0; i2 < n2; ++i2) {
            code.addOpcode(89);
            code.addIconst(i2);
            Class<?> type = params[i2];
            if (type.isPrimitive()) {
                regno = ProxyFactory.makeWrapper(code, type, regno);
            } else {
                code.addAload(regno);
                ++regno;
            }
            code.addOpcode(83);
        }
    }

    private static int makeWrapper(Bytecode code, Class<?> type, int regno) {
        int index = FactoryHelper.typeIndex(type);
        String wrapper = FactoryHelper.wrapperTypes[index];
        code.addNew(wrapper);
        code.addOpcode(89);
        ProxyFactory.addLoad(code, regno, type);
        code.addInvokespecial(wrapper, "<init>", FactoryHelper.wrapperDesc[index]);
        return regno + FactoryHelper.dataSize[index];
    }

    private static void addUnwrapper(Bytecode code, Class<?> type) {
        if (type.isPrimitive()) {
            if (type == Void.TYPE) {
                code.addOpcode(87);
            } else {
                int index = FactoryHelper.typeIndex(type);
                String wrapper = FactoryHelper.wrapperTypes[index];
                code.addCheckcast(wrapper);
                code.addInvokevirtual(wrapper, FactoryHelper.unwarpMethods[index], FactoryHelper.unwrapDesc[index]);
            }
        } else {
            code.addCheckcast(type.getName());
        }
    }

    private static MethodInfo makeWriteReplace(ConstPool cp2) {
        MethodInfo minfo = new MethodInfo(cp2, "writeReplace", "()Ljava/lang/Object;");
        String[] list = new String[]{"java.io.ObjectStreamException"};
        ExceptionsAttribute ea2 = new ExceptionsAttribute(cp2);
        ea2.setExceptions(list);
        minfo.setExceptionsAttribute(ea2);
        Bytecode code = new Bytecode(cp2, 0, 1);
        code.addAload(0);
        code.addInvokestatic(NULL_INTERCEPTOR_HOLDER, "makeSerializedProxy", "(Ljava/lang/Object;)Ljavassist/util/proxy/SerializedProxy;");
        code.addOpcode(176);
        minfo.setCodeAttribute(code.toCodeAttribute());
        return minfo;
    }

    static class Find2MethodsArgs {
        String methodName;
        String delegatorName;
        String descriptor;
        int origIndex;

        Find2MethodsArgs(String mname, String dname, String desc, int index) {
            this.methodName = mname;
            this.delegatorName = dname;
            this.descriptor = desc;
            this.origIndex = index;
        }
    }

    public static interface UniqueName {
        public String get(String var1);
    }

    public static interface ClassLoaderProvider {
        public ClassLoader get(ProxyFactory var1);
    }

    static class ProxyDetails {
        byte[] signature;
        Reference<Class<?>> proxyClass;
        boolean isUseWriteReplace;

        ProxyDetails(byte[] signature, Class<?> proxyClass, boolean isUseWriteReplace) {
            this.signature = signature;
            this.proxyClass = new WeakReference(proxyClass);
            this.isUseWriteReplace = isUseWriteReplace;
        }
    }
}

