

package java.lang;

import java.lang.ClassValue.ClassValueMap;
import java.util.WeakHashMap;
import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.ClassValue.ClassValueMap.probeHomeLocation;
import static java.lang.ClassValue.ClassValueMap.probeBackupLocations;


public abstract class ClassValue<T> {

    protected ClassValue() {
    }


    protected abstract T computeValue(Class<?> type);


    public T get(Class<?> type) {
        Entry<?>[] cache;
        Entry<T> e = probeHomeLocation(cache = getCacheCarefully(type), this);
        if (match(e))
            return e.value();
        return getFromBackup(cache, type);
    }


    public void remove(Class<?> type) {
        ClassValueMap map = getMap(type);
        map.removeEntry(this);
    }

    void put(Class<?> type, T value) {
        ClassValueMap map = getMap(type);
        map.changeEntry(this, value);
    }

    private static Entry<?>[] getCacheCarefully(Class<?> type) {
        ClassValueMap map = type.classValueMap;
        if (map == null)  return EMPTY_CACHE;
        Entry<?>[] cache = map.getCache();
        return cache;
        }


    private static final Entry<?>[] EMPTY_CACHE = { null };


    private T getFromBackup(Entry<?>[] cache, Class<?> type) {
        Entry<T> e = probeBackupLocations(cache, this);
        if (e != null)
            return e.value();
        return getFromHashMap(type);
    }

    @SuppressWarnings("unchecked")
    Entry<T> castEntry(Entry<?> e) { return (Entry<T>) e; }


    private T getFromHashMap(Class<?> type) {
        ClassValueMap map = getMap(type);
        for (;;) {
            Entry<T> e = map.startEntry(this);
            if (!e.isPromise())
                return e.value();
            try {
                e = makeEntry(e.version(), computeValue(type));
            } finally {
                e = map.finishEntry(this, e);
            }
            if (e != null)
                return e.value();
            }
    }


    boolean match(Entry<?> e) {
        return (e != null && e.get() == this.version);
        }


    final int hashCodeForCache = nextHashCode.getAndAdd(HASH_INCREMENT) & HASH_MASK;


    private static final AtomicInteger nextHashCode = new AtomicInteger();


    private static final int HASH_INCREMENT = 0x61c88647;


    static final int HASH_MASK = (-1 >>> 2);


    static class Identity {
    }

    final Identity identity = new Identity();


    private volatile Version<T> version = new Version<>(this);
    Version<T> version() { return version; }
    void bumpVersion() { version = new Version<>(this); }
    static class Version<T> {
        private final ClassValue<T> classValue;
        private final Entry<T> promise = new Entry<>(this);
        Version(ClassValue<T> classValue) { this.classValue = classValue; }
        ClassValue<T> classValue() { return classValue; }
        Entry<T> promise() { return promise; }
        boolean isLive() { return classValue.version() == this; }
    }


    static class Entry<T> extends WeakReference<Version<T>> {
        final Object value;  Entry(Version<T> version, T value) {
            super(version);
            this.value = value;  }
        private void assertNotPromise() { assert(!isPromise()); }

        Entry(Version<T> version) {
            super(version);
            this.value = this;  }

        @SuppressWarnings("unchecked")  T value() { assertNotPromise(); return (T) value; }
        boolean isPromise() { return value == this; }
        Version<T> version() { return get(); }
        ClassValue<T> classValueOrNull() {
            Version<T> v = version();
            return (v == null) ? null : v.classValue();
        }
        boolean isLive() {
            Version<T> v = version();
            if (v == null)  return false;
            if (v.isLive())  return true;
            clear();
            return false;
        }
        Entry<T> refreshVersion(Version<T> v2) {
            assertNotPromise();
            @SuppressWarnings("unchecked")  Entry<T> e2 = new Entry<>(v2, (T) value);
            clear();
            return e2;
        }
        static final Entry<?> DEAD_ENTRY = new Entry<>(null, null);
    }


    private static ClassValueMap getMap(Class<?> type) {
        ClassValueMap map = type.classValueMap;
        if (map != null)  return map;
        return initializeMap(type);
    }

    private static final Object CRITICAL_SECTION = new Object();
    private static ClassValueMap initializeMap(Class<?> type) {
        ClassValueMap map;
        synchronized (CRITICAL_SECTION) {  if ((map = type.classValueMap) == null)
                type.classValueMap = map = new ClassValueMap(type);
        }
            return map;
        }

    static <T> Entry<T> makeEntry(Version<T> explicitVersion, T value) {
        return new Entry<>(explicitVersion, value);

        }

    static class ClassValueMap extends WeakHashMap<ClassValue.Identity, Entry<?>> {
        private final Class<?> type;
        private Entry<?>[] cacheArray;
        private int cacheLoad, cacheLoadLimit;


        private static final int INITIAL_ENTRIES = 32;


        ClassValueMap(Class<?> type) {
            this.type = type;
            sizeCache(INITIAL_ENTRIES);
        }

        Entry<?>[] getCache() { return cacheArray; }


        synchronized
        <T> Entry<T> startEntry(ClassValue<T> classValue) {
            @SuppressWarnings("unchecked")  Entry<T> e = (Entry<T>) get(classValue.identity);
            Version<T> v = classValue.version();
            if (e == null) {
                e = v.promise();
                put(classValue.identity, e);
                return e;
            } else if (e.isPromise()) {
                if (e.version() != v) {
                    e = v.promise();
                    put(classValue.identity, e);
                }
                return e;
            } else {
                if (e.version() != v) {
                    e = e.refreshVersion(v);
                    put(classValue.identity, e);
                }
                checkCacheLoad();
                addToCache(classValue, e);
                return e;
            }
        }


        synchronized
        <T> Entry<T> finishEntry(ClassValue<T> classValue, Entry<T> e) {
            @SuppressWarnings("unchecked")  Entry<T> e0 = (Entry<T>) get(classValue.identity);
            if (e == e0) {
                assert(e.isPromise());
                remove(classValue.identity);
                return null;
            } else if (e0 != null && e0.isPromise() && e0.version() == e.version()) {
                Version<T> v = classValue.version();
                if (e.version() != v)
                    e = e.refreshVersion(v);
                put(classValue.identity, e);
                checkCacheLoad();
                addToCache(classValue, e);
                return e;
            } else {
                return null;
            }
        }


        synchronized
        void removeEntry(ClassValue<?> classValue) {
            Entry<?> e = remove(classValue.identity);
            if (e == null) {
                } else if (e.isPromise()) {
                put(classValue.identity, e);
            } else {
                classValue.bumpVersion();
                removeStaleEntries(classValue);
            }
        }


        synchronized
        <T> void changeEntry(ClassValue<T> classValue, T value) {
            @SuppressWarnings("unchecked")  Entry<T> e0 = (Entry<T>) get(classValue.identity);
            Version<T> version = classValue.version();
            if (e0 != null) {
                if (e0.version() == version && e0.value() == value)
                    return;
                classValue.bumpVersion();
                removeStaleEntries(classValue);
            }
            Entry<T> e = makeEntry(version, value);
            put(classValue.identity, e);
            checkCacheLoad();
            addToCache(classValue, e);
        }

        static Entry<?> loadFromCache(Entry<?>[] cache, int i) {
            return cache[i & (cache.length-1)];
            }


        static <T> Entry<T> probeHomeLocation(Entry<?>[] cache, ClassValue<T> classValue) {
            return classValue.castEntry(loadFromCache(cache, classValue.hashCodeForCache));
        }


        static <T> Entry<T> probeBackupLocations(Entry<?>[] cache, ClassValue<T> classValue) {
            if (PROBE_LIMIT <= 0)  return null;
            int mask = (cache.length-1);
            int home = (classValue.hashCodeForCache & mask);
            Entry<?> e2 = cache[home];  if (e2 == null) {
                return null;   }
            int pos2 = -1;
            for (int i = home + 1; i < home + PROBE_LIMIT; i++) {
                Entry<?> e = cache[i & mask];
                if (e == null) {
                    break;   }
                if (classValue.match(e)) {
                    cache[home] = e;
                    if (pos2 >= 0) {
                        cache[i & mask] = Entry.DEAD_ENTRY;
                    } else {
                        pos2 = i;
                    }
                    cache[pos2 & mask] = ((entryDislocation(cache, pos2, e2) < PROBE_LIMIT)
                                          ? e2                  : Entry.DEAD_ENTRY);
                    return classValue.castEntry(e);
                }
                if (!e.isLive() && pos2 < 0)  pos2 = i;
            }
            return null;
        }


        private static int entryDislocation(Entry<?>[] cache, int pos, Entry<?> e) {
            ClassValue<?> cv = e.classValueOrNull();
            if (cv == null)  return 0;  int mask = (cache.length-1);
            return (pos - cv.hashCodeForCache) & mask;
        }

        private void sizeCache(int length) {
            assert((length & (length-1)) == 0);  cacheLoad = 0;
            cacheLoadLimit = (int) ((double) length * CACHE_LOAD_LIMIT / 100);
            cacheArray = new Entry<?>[length];
        }


        private void checkCacheLoad() {
            if (cacheLoad >= cacheLoadLimit) {
                reduceCacheLoad();
            }
        }
        private void reduceCacheLoad() {
            removeStaleEntries();
            if (cacheLoad < cacheLoadLimit)
                return;  Entry<?>[] oldCache = getCache();
            if (oldCache.length > HASH_MASK)
                return;  sizeCache(oldCache.length * 2);
            for (Entry<?> e : oldCache) {
                if (e != null && e.isLive()) {
                    addToCache(e);
                }
            }
        }


        private void removeStaleEntries(Entry<?>[] cache, int begin, int count) {
            if (PROBE_LIMIT <= 0)  return;
            int mask = (cache.length-1);
            int removed = 0;
            for (int i = begin; i < begin + count; i++) {
                Entry<?> e = cache[i & mask];
                if (e == null || e.isLive())
                    continue;  Entry<?> replacement = null;
                if (PROBE_LIMIT > 1) {
                    replacement = findReplacement(cache, i);
                }
                cache[i & mask] = replacement;
                if (replacement == null)  removed += 1;
            }
            cacheLoad = Math.max(0, cacheLoad - removed);
        }


        private Entry<?> findReplacement(Entry<?>[] cache, int home1) {
            Entry<?> replacement = null;
            int haveReplacement = -1, replacementPos = 0;
            int mask = (cache.length-1);
            for (int i2 = home1 + 1; i2 < home1 + PROBE_LIMIT; i2++) {
                Entry<?> e2 = cache[i2 & mask];
                if (e2 == null)  break;  if (!e2.isLive())  continue;  int dis2 = entryDislocation(cache, i2, e2);
                if (dis2 == 0)  continue;  int home2 = i2 - dis2;
                if (home2 <= home1) {
                    if (home2 == home1) {
                        haveReplacement = 1;
                        replacementPos = i2;
                        replacement = e2;
                    } else if (haveReplacement <= 0) {
                        haveReplacement = 0;
                        replacementPos = i2;
                        replacement = e2;
                    }
                    }
            }
            if (haveReplacement >= 0) {
                if (cache[(replacementPos+1) & mask] != null) {
                    cache[replacementPos & mask] = (Entry<?>) Entry.DEAD_ENTRY;
                } else {
                    cache[replacementPos & mask] = null;
                    cacheLoad -= 1;
                }
            }
            return replacement;
        }


        private void removeStaleEntries(ClassValue<?> classValue) {
            removeStaleEntries(getCache(), classValue.hashCodeForCache, PROBE_LIMIT);
        }


        private void removeStaleEntries() {
            Entry<?>[] cache = getCache();
            removeStaleEntries(cache, 0, cache.length + PROBE_LIMIT - 1);
        }


        private <T> void addToCache(Entry<T> e) {
            ClassValue<T> classValue = e.classValueOrNull();
            if (classValue != null)
                addToCache(classValue, e);
        }


        private <T> void addToCache(ClassValue<T> classValue, Entry<T> e) {
            if (PROBE_LIMIT <= 0)  return;  Entry<?>[] cache = getCache();
            int mask = (cache.length-1);
            int home = classValue.hashCodeForCache & mask;
            Entry<?> e2 = placeInCache(cache, home, e, false);
            if (e2 == null)  return;  if (PROBE_LIMIT > 1) {
                int dis2 = entryDislocation(cache, home, e2);
                int home2 = home - dis2;
                for (int i2 = home2; i2 < home2 + PROBE_LIMIT; i2++) {
                    if (placeInCache(cache, i2 & mask, e2, true) == null) {
                        return;
                    }
                }
            }
            }


        private Entry<?> placeInCache(Entry<?>[] cache, int pos, Entry<?> e, boolean gently) {
            Entry<?> e2 = overwrittenEntry(cache[pos]);
            if (gently && e2 != null) {
                return e;
            } else {
                cache[pos] = e;
                return e2;
            }
        }


        private <T> Entry<T> overwrittenEntry(Entry<T> e2) {
            if (e2 == null)  cacheLoad += 1;
            else if (e2.isLive())  return e2;
            return null;
        }


        private static final int CACHE_LOAD_LIMIT = 67;  private static final int PROBE_LIMIT      =  6;       }
}
