/*
 * Decompiled with CFR 0.152.
 */
package javassist.util.proxy;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;
import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;

public class ProxyObjectOutputStream
extends ObjectOutputStream {
    public ProxyObjectOutputStream(OutputStream out) throws IOException {
        super(out);
    }

    @Override
    protected void writeClassDescriptor(ObjectStreamClass desc) throws IOException {
        Class<?> cl2 = desc.forClass();
        if (ProxyFactory.isProxyClass(cl2)) {
            this.writeBoolean(true);
            Class<?> superClass = cl2.getSuperclass();
            Class<?>[] interfaces = cl2.getInterfaces();
            byte[] signature = ProxyFactory.getFilterSignature(cl2);
            String name = superClass.getName();
            this.writeObject(name);
            this.writeInt(interfaces.length - 1);
            for (int i2 = 0; i2 < interfaces.length; ++i2) {
                Class<?> interfaze = interfaces[i2];
                if (interfaze == ProxyObject.class || interfaze == Proxy.class) continue;
                name = interfaces[i2].getName();
                this.writeObject(name);
            }
            this.writeInt(signature.length);
            this.write(signature);
        } else {
            this.writeBoolean(false);
            super.writeClassDescriptor(desc);
        }
    }
}

