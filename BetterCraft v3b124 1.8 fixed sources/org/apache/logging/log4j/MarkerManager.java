/*
 * Decompiled with CFR 0.152.
 */
package org.apache.logging.log4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.logging.log4j.Marker;

public final class MarkerManager {
    private static ConcurrentMap<String, Marker> markerMap = new ConcurrentHashMap<String, Marker>();

    private MarkerManager() {
    }

    public static Marker getMarker(String name) {
        markerMap.putIfAbsent(name, new Log4jMarker(name));
        return (Marker)markerMap.get(name);
    }

    public static Marker getMarker(String name, String parent) {
        Marker parentMarker = (Marker)markerMap.get(parent);
        if (parentMarker == null) {
            throw new IllegalArgumentException("Parent Marker " + parent + " has not been defined");
        }
        return MarkerManager.getMarker(name, parentMarker);
    }

    public static Marker getMarker(String name, Marker parent) {
        markerMap.putIfAbsent(name, new Log4jMarker(name, parent));
        return (Marker)markerMap.get(name);
    }

    private static class Log4jMarker
    implements Marker {
        private static final long serialVersionUID = 100L;
        private final String name;
        private final Marker parent;

        public Log4jMarker(String name) {
            this.name = name;
            this.parent = null;
        }

        public Log4jMarker(String name, Marker parent) {
            this.name = name;
            this.parent = parent;
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public Marker getParent() {
            return this.parent;
        }

        @Override
        public boolean isInstanceOf(Marker m2) {
            if (m2 == null) {
                throw new IllegalArgumentException("A marker parameter is required");
            }
            Marker test = this;
            do {
                if (test != m2) continue;
                return true;
            } while ((test = test.getParent()) != null);
            return false;
        }

        @Override
        public boolean isInstanceOf(String name) {
            if (name == null) {
                throw new IllegalArgumentException("A marker name is required");
            }
            Marker toTest = this;
            do {
                if (!name.equals(toTest.getName())) continue;
                return true;
            } while ((toTest = toTest.getParent()) != null);
            return false;
        }

        public boolean equals(Object o2) {
            if (this == o2) {
                return true;
            }
            if (o2 == null || !(o2 instanceof Marker)) {
                return false;
            }
            Marker marker = (Marker)o2;
            return !(this.name != null ? !this.name.equals(marker.getName()) : marker.getName() != null);
        }

        public int hashCode() {
            return this.name != null ? this.name.hashCode() : 0;
        }

        public String toString() {
            StringBuilder sb2 = new StringBuilder(this.name);
            if (this.parent != null) {
                sb2.append("[ ");
                boolean first = true;
                for (Marker m2 = this.parent; m2 != null; m2 = m2.getParent()) {
                    if (!first) {
                        sb2.append(", ");
                    }
                    sb2.append(m2.getName());
                    first = false;
                }
                sb2.append(" ]");
            }
            return sb2.toString();
        }
    }
}

