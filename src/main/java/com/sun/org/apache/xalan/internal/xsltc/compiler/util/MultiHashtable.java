



package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public final class MultiHashtable<K,V> {
    static final long serialVersionUID = -6151608290510033572L;

    private final Map<K, Set<V>> map = new HashMap<>();
    private boolean modifiable = true;


    public Set<V> put(K key, V value) {
        if (modifiable) {
            Set<V> set = map.get(key);
            if (set == null) {
                set = new HashSet<>();
                map.put(key, set);
            }
            set.add(value);
            return set;
        }
        throw new UnsupportedOperationException("The MultiHashtable instance is not modifiable.");
    }


    public V maps(K key, V value) {
        if (key == null) return null;
        final Set<V> set = map.get(key);
        if (set != null) {
            for (V v : set) {
                if (v.equals(value)) {
                    return v;
                }
            }
        }
        return null;
    }


    public void makeUnmodifiable() {
        modifiable = false;
    }
}
