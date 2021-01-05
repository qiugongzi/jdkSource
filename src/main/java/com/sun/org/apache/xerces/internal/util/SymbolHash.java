


package com.sun.org.apache.xerces.internal.util;



public class SymbolHash {

    protected static final int TABLE_SIZE = 101;


    protected static final int MAX_HASH_COLLISIONS = 40;

    protected static final int MULTIPLIERS_SIZE = 1 << 5;
    protected static final int MULTIPLIERS_MASK = MULTIPLIERS_SIZE - 1;

    protected int fTableSize;


    protected Entry[] fBuckets;


    protected int fNum = 0;


    protected int[] fHashMultipliers;

    public SymbolHash() {
        this(TABLE_SIZE);
    }


    public SymbolHash(int size) {
        fTableSize = size;
        fBuckets = new Entry[fTableSize];
    }

    public void put(Object key, Object value) {

        int collisionCount = 0;
        final int hash = hash(key);
        int bucket = hash % fTableSize;
        for (Entry entry = fBuckets[bucket]; entry != null; entry = entry.next) {
            if (key.equals(entry.key)) {
                entry.value = value;
                return;
            }
            ++collisionCount;
        }

        if (fNum >= fTableSize) {
            rehash();
            bucket = hash % fTableSize;
        }
        else if (collisionCount >= MAX_HASH_COLLISIONS && key instanceof String) {
            rebalance();
            bucket = hash(key) % fTableSize;
        }

        Entry entry = new Entry(key, value, fBuckets[bucket]);
        fBuckets[bucket] = entry;
        ++fNum;
    }


    public Object get(Object key) {
        int bucket = hash(key) % fTableSize;
        Entry entry = search(key, bucket);
        if (entry != null) {
            return entry.value;
        }
        return null;
    }


    public int getLength() {
        return fNum;
    }


    public int getValues(Object[] elements, int from) {
        for (int i=0, j=0; i<fTableSize && j<fNum; i++) {
            for (Entry entry = fBuckets[i]; entry != null; entry = entry.next) {
                elements[from+j] = entry.value;
                j++;
            }
        }
        return fNum;
    }


    public Object[] getEntries() {
        Object[] entries = new Object[fNum << 1];
        for (int i=0, j=0; i<fTableSize && j<fNum << 1; i++) {
            for (Entry entry = fBuckets[i]; entry != null; entry = entry.next) {
                entries[j] = entry.key;
                entries[++j] = entry.value;
                j++;
            }
        }
        return entries;
    }


    public SymbolHash makeClone() {
        SymbolHash newTable = new SymbolHash(fTableSize);
        newTable.fNum = fNum;
        newTable.fHashMultipliers = fHashMultipliers != null ? (int[]) fHashMultipliers.clone() : null;
        for (int i = 0; i < fTableSize; i++) {
            if (fBuckets[i] != null) {
                newTable.fBuckets[i] = fBuckets[i].makeClone();
            }
        }
        return newTable;
    }


    public void clear() {
        for (int i=0; i<fTableSize; i++) {
            fBuckets[i] = null;
        }
        fNum = 0;
        fHashMultipliers = null;
    } protected Entry search(Object key, int bucket) {
        for (Entry entry = fBuckets[bucket]; entry != null; entry = entry.next) {
            if (key.equals(entry.key))
                return entry;
        }
        return null;
    }


    protected int hash(Object key) {
        if (fHashMultipliers == null || !(key instanceof String)) {
            return key.hashCode() & 0x7FFFFFFF;
        }
        return hash0((String) key);
    } private int hash0(String symbol) {
        int code = 0;
        final int length = symbol.length();
        final int[] multipliers = fHashMultipliers;
        for (int i = 0; i < length; ++i) {
            code = code * multipliers[i & MULTIPLIERS_MASK] + symbol.charAt(i);
        }
        return code & 0x7FFFFFFF;
    } protected void rehash() {
        rehashCommon((fBuckets.length << 1) + 1);
    }


    protected void rebalance() {
        if (fHashMultipliers == null) {
            fHashMultipliers = new int[MULTIPLIERS_SIZE];
        }
        PrimeNumberSequenceGenerator.generateSequence(fHashMultipliers);
        rehashCommon(fBuckets.length);
    }

    private void rehashCommon(final int newCapacity) {

        final int oldCapacity = fBuckets.length;
        final Entry[] oldTable = fBuckets;

        final Entry[] newTable = new Entry[newCapacity];

        fBuckets = newTable;
        fTableSize = fBuckets.length;

        for (int i = oldCapacity; i-- > 0;) {
            for (Entry old = oldTable[i]; old != null; ) {
                Entry e = old;
                old = old.next;

                int index = hash(e.key) % newCapacity;
                e.next = newTable[index];
                newTable[index] = e;
            }
        }
    }

    protected static final class Entry {
        public Object key;
        public Object value;

        public Entry next;

        public Entry() {
            key = null;
            value = null;
            next = null;
        }

        public Entry(Object key, Object value, Entry next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }

        public Entry makeClone() {
            Entry entry = new Entry();
            entry.key = key;
            entry.value = value;
            if (next != null)
                entry.next = next.makeClone();
            return entry;
        }
    } }