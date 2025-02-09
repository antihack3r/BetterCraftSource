/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  [Lcom.sun.jna.WString;
 *  [Ljava.lang.String;
 *  [Ljava.lang.Object;
 */
package com.sun.jna;

import [Lcom.sun.jna.CfrRenamed5706;
import [Ljava.lang.CfrRenamed5689;
import [Ljava.lang.CfrRenamed5699;
import com.sun.jna.AltCallingConvention;
import com.sun.jna.Callback;
import com.sun.jna.CallbackParameterContext;
import com.sun.jna.CallbackProxy;
import com.sun.jna.CallbackResultContext;
import com.sun.jna.CallbackThreadInitializer;
import com.sun.jna.FromNativeConverter;
import com.sun.jna.Function;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeMapped;
import com.sun.jna.NativeMappedConverter;
import com.sun.jna.NativeString;
import com.sun.jna.Pointer;
import com.sun.jna.StringArray;
import com.sun.jna.Structure;
import com.sun.jna.ToNativeConverter;
import com.sun.jna.TypeMapper;
import com.sun.jna.WString;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

class CallbackReference
extends WeakReference {
    static final Map callbackMap = new WeakHashMap();
    static final Map allocations = new WeakHashMap();
    private static final Method PROXY_CALLBACK_METHOD;
    private static final Map initializers;
    Pointer cbstruct;
    CallbackProxy proxy;
    Method method;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static void setCallbackThreadInitializer(Callback cb2, CallbackThreadInitializer initializer) {
        Map map = callbackMap;
        synchronized (map) {
            if (initializer != null) {
                initializers.put(cb2, initializer);
            } else {
                initializers.remove(cb2);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static ThreadGroup initializeThread(Callback cb2, AttachOptions args) {
        CallbackThreadInitializer init = null;
        if (cb2 instanceof DefaultCallbackProxy) {
            cb2 = ((DefaultCallbackProxy)cb2).getCallback();
        }
        Map map = initializers;
        synchronized (map) {
            init = (CallbackThreadInitializer)initializers.get(cb2);
        }
        ThreadGroup group = null;
        if (init != null) {
            group = init.getThreadGroup(cb2);
            args.name = init.getName(cb2);
            args.daemon = init.isDaemon(cb2);
            args.detach = init.detach(cb2);
            args.write();
        }
        return group;
    }

    public static Callback getCallback(Class type, Pointer p2) {
        return CallbackReference.getCallback(type, p2, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static Callback getCallback(Class type, Pointer p2, boolean direct) {
        Map map;
        if (p2 == null) {
            return null;
        }
        if (!type.isInterface()) {
            throw new IllegalArgumentException("Callback type must be an interface");
        }
        Map map2 = map = callbackMap;
        synchronized (map2) {
            Iterator i2 = map.keySet().iterator();
            while (i2.hasNext()) {
                CallbackReference cbref;
                Pointer cbp;
                Callback cb2 = (Callback)i2.next();
                if (!type.isAssignableFrom(cb2.getClass()) || !p2.equals(cbp = (cbref = (CallbackReference)map.get(cb2)) != null ? cbref.getTrampoline() : CallbackReference.getNativeFunctionPointer(cb2))) continue;
                return cb2;
            }
            int ctype = AltCallingConvention.class.isAssignableFrom(type) ? 1 : 0;
            HashMap<String, Method> foptions = new HashMap<String, Method>();
            Map options = Native.getLibraryOptions(type);
            if (options != null) {
                foptions.putAll(options);
            }
            foptions.put("invoking-method", CallbackReference.getCallbackMethod(type));
            NativeFunctionHandler h2 = new NativeFunctionHandler(p2, ctype, foptions);
            Callback cb3 = (Callback)Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type}, (InvocationHandler)h2);
            map.put(cb3, null);
            return cb3;
        }
    }

    private CallbackReference(Callback callback, int callingConvention, boolean direct) {
        super(callback);
        boolean ppc;
        TypeMapper mapper = Native.getTypeMapper(callback.getClass());
        String arch = System.getProperty("os.arch").toLowerCase();
        boolean bl2 = ppc = "ppc".equals(arch) || "powerpc".equals(arch);
        if (direct) {
            Method m2 = CallbackReference.getCallbackMethod(callback);
            Class<?>[] ptypes = m2.getParameterTypes();
            for (int i2 = 0; i2 < ptypes.length; ++i2) {
                if (ppc && (ptypes[i2] == Float.TYPE || ptypes[i2] == Double.TYPE)) {
                    direct = false;
                    break;
                }
                if (mapper == null || mapper.getFromNativeConverter(ptypes[i2]) == null) continue;
                direct = false;
                break;
            }
            if (mapper != null && mapper.getToNativeConverter(m2.getReturnType()) != null) {
                direct = false;
            }
        }
        if (direct) {
            Class<?> returnType;
            this.method = CallbackReference.getCallbackMethod(callback);
            Class[] nativeParamTypes = this.method.getParameterTypes();
            long peer = Native.createNativeCallback(callback, this.method, nativeParamTypes, returnType = this.method.getReturnType(), callingConvention, true);
            this.cbstruct = peer != 0L ? new Pointer(peer) : null;
        } else {
            this.proxy = callback instanceof CallbackProxy ? (CallbackProxy)callback : new DefaultCallbackProxy(CallbackReference.getCallbackMethod(callback), mapper);
            Class[] nativeParamTypes = this.proxy.getParameterTypes();
            Class returnType = this.proxy.getReturnType();
            if (mapper != null) {
                for (int i3 = 0; i3 < nativeParamTypes.length; ++i3) {
                    FromNativeConverter rc2 = mapper.getFromNativeConverter(nativeParamTypes[i3]);
                    if (rc2 == null) continue;
                    nativeParamTypes[i3] = rc2.nativeType();
                }
                ToNativeConverter tn2 = mapper.getToNativeConverter(returnType);
                if (tn2 != null) {
                    returnType = tn2.nativeType();
                }
            }
            for (int i4 = 0; i4 < nativeParamTypes.length; ++i4) {
                nativeParamTypes[i4] = this.getNativeType(nativeParamTypes[i4]);
                if (CallbackReference.isAllowableNativeType(nativeParamTypes[i4])) continue;
                String msg = "Callback argument " + nativeParamTypes[i4] + " requires custom type conversion";
                throw new IllegalArgumentException(msg);
            }
            if (!CallbackReference.isAllowableNativeType(returnType = this.getNativeType(returnType))) {
                String msg = "Callback return type " + returnType + " requires custom type conversion";
                throw new IllegalArgumentException(msg);
            }
            long peer = Native.createNativeCallback(this.proxy, PROXY_CALLBACK_METHOD, nativeParamTypes, returnType, callingConvention, false);
            this.cbstruct = peer != 0L ? new Pointer(peer) : null;
        }
    }

    private Class getNativeType(Class cls) {
        if (Structure.class.isAssignableFrom(cls)) {
            Structure.newInstance(cls);
            if (!Structure.ByValue.class.isAssignableFrom(cls)) {
                return Pointer.class;
            }
        } else {
            if (NativeMapped.class.isAssignableFrom(cls)) {
                return NativeMappedConverter.getInstance(cls).nativeType();
            }
            if (cls == String.class || cls == WString.class || cls == CfrRenamed5689.class || cls == CfrRenamed5706.class || Callback.class.isAssignableFrom(cls)) {
                return Pointer.class;
            }
        }
        return cls;
    }

    private static Method checkMethod(Method m2) {
        if (m2.getParameterTypes().length > 256) {
            String msg = "Method signature exceeds the maximum parameter count: " + m2;
            throw new UnsupportedOperationException(msg);
        }
        return m2;
    }

    static Class findCallbackClass(Class type) {
        if (!Callback.class.isAssignableFrom(type)) {
            throw new IllegalArgumentException(type.getName() + " is not derived from com.sun.jna.Callback");
        }
        if (type.isInterface()) {
            return type;
        }
        Class<?>[] ifaces = type.getInterfaces();
        for (int i2 = 0; i2 < ifaces.length; ++i2) {
            if (!(class$com$sun$jna$Callback == null ? CallbackReference.class$("com.sun.jna.Callback") : class$com$sun$jna$Callback).isAssignableFrom(ifaces[i2])) continue;
            try {
                CallbackReference.getCallbackMethod(ifaces[i2]);
                return ifaces[i2];
            }
            catch (IllegalArgumentException e2) {
                break;
            }
        }
        if (Callback.class.isAssignableFrom(type.getSuperclass())) {
            return CallbackReference.findCallbackClass(type.getSuperclass());
        }
        return type;
    }

    private static Method getCallbackMethod(Callback callback) {
        return CallbackReference.getCallbackMethod(CallbackReference.findCallbackClass(callback.getClass()));
    }

    private static Method getCallbackMethod(Class cls) {
        Method[] pubMethods = cls.getDeclaredMethods();
        Method[] classMethods = cls.getMethods();
        HashSet<Method> pmethods = new HashSet<Method>(Arrays.asList(pubMethods));
        pmethods.retainAll(Arrays.asList(classMethods));
        Iterator i2 = pmethods.iterator();
        while (i2.hasNext()) {
            Method m2 = (Method)i2.next();
            if (!Callback.FORBIDDEN_NAMES.contains(m2.getName())) continue;
            i2.remove();
        }
        Method[] methods = pmethods.toArray(new Method[pmethods.size()]);
        if (methods.length == 1) {
            return CallbackReference.checkMethod(methods[0]);
        }
        for (int i3 = 0; i3 < methods.length; ++i3) {
            Method m3 = methods[i3];
            if (!"callback".equals(m3.getName())) continue;
            return CallbackReference.checkMethod(m3);
        }
        String msg = "Callback must implement a single public method, or one public method named 'callback'";
        throw new IllegalArgumentException(msg);
    }

    private void setCallbackOptions(int options) {
        this.cbstruct.setInt(Pointer.SIZE, options);
    }

    public Pointer getTrampoline() {
        return this.cbstruct.getPointer(0L);
    }

    protected void finalize() {
        this.dispose();
    }

    protected synchronized void dispose() {
        if (this.cbstruct != null) {
            Native.freeNativeCallback(this.cbstruct.peer);
            this.cbstruct.peer = 0L;
            this.cbstruct = null;
        }
    }

    private Callback getCallback() {
        return (Callback)this.get();
    }

    private static Pointer getNativeFunctionPointer(Callback cb2) {
        InvocationHandler handler;
        if (Proxy.isProxyClass(cb2.getClass()) && (handler = Proxy.getInvocationHandler(cb2)) instanceof NativeFunctionHandler) {
            return ((NativeFunctionHandler)handler).getPointer();
        }
        return null;
    }

    public static Pointer getFunctionPointer(Callback cb2) {
        return CallbackReference.getFunctionPointer(cb2, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static Pointer getFunctionPointer(Callback cb2, boolean direct) {
        Map map;
        Pointer fp2 = null;
        if (cb2 == null) {
            return null;
        }
        fp2 = CallbackReference.getNativeFunctionPointer(cb2);
        if (fp2 != null) {
            return fp2;
        }
        int callingConvention = cb2 instanceof AltCallingConvention ? 1 : 0;
        Map map2 = map = callbackMap;
        synchronized (map2) {
            CallbackReference cbref = (CallbackReference)map.get(cb2);
            if (cbref == null) {
                cbref = new CallbackReference(cb2, callingConvention, direct);
                map.put(cb2, cbref);
                if (initializers.containsKey(cb2)) {
                    cbref.setCallbackOptions(1);
                }
            }
            return cbref.getTrampoline();
        }
    }

    private static boolean isAllowableNativeType(Class cls) {
        return cls == Void.TYPE || cls == Void.class || cls == Boolean.TYPE || cls == Boolean.class || cls == Byte.TYPE || cls == Byte.class || cls == Short.TYPE || cls == Short.class || cls == Character.TYPE || cls == Character.class || cls == Integer.TYPE || cls == Integer.class || cls == Long.TYPE || cls == Long.class || cls == Float.TYPE || cls == Float.class || cls == Double.TYPE || cls == Double.class || Structure.ByValue.class.isAssignableFrom(cls) && Structure.class.isAssignableFrom(cls) || Pointer.class.isAssignableFrom(cls);
    }

    private static Pointer getNativeString(Object value, boolean wide) {
        if (value != null) {
            NativeString ns2 = new NativeString(value.toString(), wide);
            allocations.put(value, ns2);
            return ns2.getPointer();
        }
        return null;
    }

    static {
        try {
            PROXY_CALLBACK_METHOD = CallbackProxy.class.getMethod("callback", CfrRenamed5699.class);
        }
        catch (Exception e2) {
            throw new Error("Error looking up CallbackProxy.callback() method");
        }
        initializers = new WeakHashMap();
    }

    private static class NativeFunctionHandler
    implements InvocationHandler {
        private Function function;
        private Map options;

        public NativeFunctionHandler(Pointer address, int callingConvention, Map options) {
            this.function = new Function(address, callingConvention);
            this.options = options;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (Library.Handler.OBJECT_TOSTRING.equals(method)) {
                String str = "Proxy interface to " + this.function;
                Method m2 = (Method)this.options.get("invoking-method");
                Class cls = CallbackReference.findCallbackClass(m2.getDeclaringClass());
                str = str + " (" + cls.getName() + ")";
                return str;
            }
            if (Library.Handler.OBJECT_HASHCODE.equals(method)) {
                return new Integer(this.hashCode());
            }
            if (Library.Handler.OBJECT_EQUALS.equals(method)) {
                Object o2 = args[0];
                if (o2 != null && Proxy.isProxyClass(o2.getClass())) {
                    return Function.valueOf(Proxy.getInvocationHandler(o2) == this);
                }
                return Boolean.FALSE;
            }
            if (Function.isVarArgs(method)) {
                args = Function.concatenateVarArgs(args);
            }
            return this.function.invoke(method.getReturnType(), args, this.options);
        }

        public Pointer getPointer() {
            return this.function;
        }
    }

    private class DefaultCallbackProxy
    implements CallbackProxy {
        private Method callbackMethod;
        private ToNativeConverter toNative;
        private FromNativeConverter[] fromNative;

        public DefaultCallbackProxy(Method callbackMethod, TypeMapper mapper) {
            this.callbackMethod = callbackMethod;
            Class<?>[] argTypes = callbackMethod.getParameterTypes();
            Class<?> returnType = callbackMethod.getReturnType();
            this.fromNative = new FromNativeConverter[argTypes.length];
            if ((class$com$sun$jna$NativeMapped == null ? (class$com$sun$jna$NativeMapped = CallbackReference.class$("com.sun.jna.NativeMapped")) : class$com$sun$jna$NativeMapped).isAssignableFrom(returnType)) {
                this.toNative = NativeMappedConverter.getInstance(returnType);
            } else if (mapper != null) {
                this.toNative = mapper.getToNativeConverter(returnType);
            }
            for (int i2 = 0; i2 < this.fromNative.length; ++i2) {
                if ((class$com$sun$jna$NativeMapped == null ? CallbackReference.class$("com.sun.jna.NativeMapped") : class$com$sun$jna$NativeMapped).isAssignableFrom(argTypes[i2])) {
                    this.fromNative[i2] = new NativeMappedConverter(argTypes[i2]);
                    continue;
                }
                if (mapper == null) continue;
                this.fromNative[i2] = mapper.getFromNativeConverter(argTypes[i2]);
            }
            if (!callbackMethod.isAccessible()) {
                try {
                    callbackMethod.setAccessible(true);
                }
                catch (SecurityException e2) {
                    throw new IllegalArgumentException("Callback method is inaccessible, make sure the interface is public: " + callbackMethod);
                }
            }
        }

        public Callback getCallback() {
            return CallbackReference.this.getCallback();
        }

        private Object invokeCallback(Object[] args) {
            Class<?>[] paramTypes = this.callbackMethod.getParameterTypes();
            Object[] callbackArgs = new Object[args.length];
            for (int i2 = 0; i2 < args.length; ++i2) {
                Class<?> type = paramTypes[i2];
                Object arg2 = args[i2];
                if (this.fromNative[i2] != null) {
                    CallbackParameterContext context = new CallbackParameterContext(type, this.callbackMethod, args, i2);
                    callbackArgs[i2] = this.fromNative[i2].fromNative(arg2, context);
                    continue;
                }
                callbackArgs[i2] = this.convertArgument(arg2, type);
            }
            Object result = null;
            Callback cb2 = this.getCallback();
            if (cb2 != null) {
                try {
                    result = this.convertResult(this.callbackMethod.invoke((Object)cb2, callbackArgs));
                }
                catch (IllegalArgumentException e2) {
                    Native.getCallbackExceptionHandler().uncaughtException(cb2, e2);
                }
                catch (IllegalAccessException e3) {
                    Native.getCallbackExceptionHandler().uncaughtException(cb2, e3);
                }
                catch (InvocationTargetException e4) {
                    Native.getCallbackExceptionHandler().uncaughtException(cb2, e4.getTargetException());
                }
            }
            for (int i3 = 0; i3 < callbackArgs.length; ++i3) {
                if (!(callbackArgs[i3] instanceof Structure) || callbackArgs[i3] instanceof Structure.ByValue) continue;
                ((Structure)callbackArgs[i3]).autoWrite();
            }
            return result;
        }

        public Object callback(Object[] args) {
            try {
                return this.invokeCallback(args);
            }
            catch (Throwable t2) {
                Native.getCallbackExceptionHandler().uncaughtException(this.getCallback(), t2);
                return null;
            }
        }

        private Object convertArgument(Object value, Class dstType) {
            if (value instanceof Pointer) {
                if (dstType == (class$java$lang$String == null ? (class$java$lang$String = CallbackReference.class$("java.lang.String")) : class$java$lang$String)) {
                    value = ((Pointer)value).getString(0L);
                } else if (dstType == (class$com$sun$jna$WString == null ? (class$com$sun$jna$WString = CallbackReference.class$("com.sun.jna.WString")) : class$com$sun$jna$WString)) {
                    value = new WString(((Pointer)value).getString(0L, true));
                } else if (dstType == (array$Ljava$lang$String == null ? (array$Ljava$lang$String = CallbackReference.class$("[Ljava.lang.String;")) : array$Ljava$lang$String) || dstType == (array$Lcom$sun$jna$WString == null ? (array$Lcom$sun$jna$WString = CallbackReference.class$("[Lcom.sun.jna.WString;")) : array$Lcom$sun$jna$WString)) {
                    value = ((Pointer)value).getStringArray(0L, dstType == (array$Lcom$sun$jna$WString == null ? (array$Lcom$sun$jna$WString = CallbackReference.class$("[Lcom.sun.jna.WString;")) : array$Lcom$sun$jna$WString));
                } else if ((class$com$sun$jna$Callback == null ? (class$com$sun$jna$Callback = CallbackReference.class$("com.sun.jna.Callback")) : class$com$sun$jna$Callback).isAssignableFrom(dstType)) {
                    value = CallbackReference.getCallback(dstType, (Pointer)value);
                } else if ((class$com$sun$jna$Structure == null ? (class$com$sun$jna$Structure = CallbackReference.class$("com.sun.jna.Structure")) : class$com$sun$jna$Structure).isAssignableFrom(dstType)) {
                    Structure s2 = Structure.newInstance(dstType);
                    if ((class$com$sun$jna$Structure$ByValue == null ? (class$com$sun$jna$Structure$ByValue = CallbackReference.class$("com.sun.jna.Structure$ByValue")) : class$com$sun$jna$Structure$ByValue).isAssignableFrom(dstType)) {
                        byte[] buf = new byte[s2.size()];
                        ((Pointer)value).read(0L, buf, 0, buf.length);
                        s2.getPointer().write(0L, buf, 0, buf.length);
                    } else {
                        s2.useMemory((Pointer)value);
                    }
                    s2.read();
                    value = s2;
                }
            } else if ((Boolean.TYPE == dstType || (class$java$lang$Boolean == null ? (class$java$lang$Boolean = CallbackReference.class$("java.lang.Boolean")) : class$java$lang$Boolean) == dstType) && value instanceof Number) {
                value = Function.valueOf(((Number)value).intValue() != 0);
            }
            return value;
        }

        private Object convertResult(Object value) {
            Class<?> cls;
            if (this.toNative != null) {
                value = this.toNative.toNative(value, new CallbackResultContext(this.callbackMethod));
            }
            if (value == null) {
                return null;
            }
            if ((class$com$sun$jna$Structure == null ? (class$com$sun$jna$Structure = CallbackReference.class$("com.sun.jna.Structure")) : class$com$sun$jna$Structure).isAssignableFrom(cls = value.getClass())) {
                if ((class$com$sun$jna$Structure$ByValue == null ? (class$com$sun$jna$Structure$ByValue = CallbackReference.class$("com.sun.jna.Structure$ByValue")) : class$com$sun$jna$Structure$ByValue).isAssignableFrom(cls)) {
                    return value;
                }
                return ((Structure)value).getPointer();
            }
            if (cls == Boolean.TYPE || cls == (class$java$lang$Boolean == null ? (class$java$lang$Boolean = CallbackReference.class$("java.lang.Boolean")) : class$java$lang$Boolean)) {
                return Boolean.TRUE.equals(value) ? Function.INTEGER_TRUE : Function.INTEGER_FALSE;
            }
            if (cls == (class$java$lang$String == null ? (class$java$lang$String = CallbackReference.class$("java.lang.String")) : class$java$lang$String) || cls == (class$com$sun$jna$WString == null ? (class$com$sun$jna$WString = CallbackReference.class$("com.sun.jna.WString")) : class$com$sun$jna$WString)) {
                return CallbackReference.getNativeString(value, cls == (class$com$sun$jna$WString == null ? (class$com$sun$jna$WString = CallbackReference.class$("com.sun.jna.WString")) : class$com$sun$jna$WString));
            }
            if (cls == (array$Ljava$lang$String == null ? (array$Ljava$lang$String = CallbackReference.class$("[Ljava.lang.String;")) : array$Ljava$lang$String) || cls == (class$com$sun$jna$WString == null ? (class$com$sun$jna$WString = CallbackReference.class$("com.sun.jna.WString")) : class$com$sun$jna$WString)) {
                StringArray sa2 = cls == (array$Ljava$lang$String == null ? (array$Ljava$lang$String = CallbackReference.class$("[Ljava.lang.String;")) : array$Ljava$lang$String) ? new StringArray((String[])value) : new StringArray((WString[])value);
                allocations.put(value, sa2);
                return sa2;
            }
            if ((class$com$sun$jna$Callback == null ? (class$com$sun$jna$Callback = CallbackReference.class$("com.sun.jna.Callback")) : class$com$sun$jna$Callback).isAssignableFrom(cls)) {
                return CallbackReference.getFunctionPointer((Callback)value);
            }
            return value;
        }

        public Class[] getParameterTypes() {
            return this.callbackMethod.getParameterTypes();
        }

        public Class getReturnType() {
            return this.callbackMethod.getReturnType();
        }
    }

    static class AttachOptions
    extends Structure {
        public boolean daemon;
        public boolean detach;
        public String name;

        AttachOptions() {
        }
    }
}

