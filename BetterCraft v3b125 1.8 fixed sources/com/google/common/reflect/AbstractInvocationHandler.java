/*
 * Decompiled with CFR 0.152.
 */
package com.google.common.reflect;

import com.google.common.annotations.Beta;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import javax.annotation.Nullable;

@Beta
public abstract class AbstractInvocationHandler
implements InvocationHandler {
    private static final Object[] NO_ARGS = new Object[0];

    @Override
    public final Object invoke(Object proxy, Method method, @Nullable Object[] args) throws Throwable {
        if (args == null) {
            args = NO_ARGS;
        }
        if (args.length == 0 && method.getName().equals("hashCode")) {
            return this.hashCode();
        }
        if (args.length == 1 && method.getName().equals("equals") && method.getParameterTypes()[0] == Object.class) {
            Object arg2 = args[0];
            if (arg2 == null) {
                return false;
            }
            if (proxy == arg2) {
                return true;
            }
            return AbstractInvocationHandler.isProxyOfSameInterfaces(arg2, proxy.getClass()) && this.equals(Proxy.getInvocationHandler(arg2));
        }
        if (args.length == 0 && method.getName().equals("toString")) {
            return this.toString();
        }
        return this.handleInvocation(proxy, method, args);
    }

    protected abstract Object handleInvocation(Object var1, Method var2, Object[] var3) throws Throwable;

    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    public int hashCode() {
        return super.hashCode();
    }

    public String toString() {
        return super.toString();
    }

    private static boolean isProxyOfSameInterfaces(Object arg2, Class<?> proxyClass) {
        return proxyClass.isInstance(arg2) || Proxy.isProxyClass(arg2.getClass()) && Arrays.equals(arg2.getClass().getInterfaces(), proxyClass.getInterfaces());
    }
}

