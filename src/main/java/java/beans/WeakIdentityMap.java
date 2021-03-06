

package java.beans;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;


abstract class WeakIdentityMap<T> {

    private static final int MAXIMUM_CAPACITY = 1 << 30; private static final Object NULL = new Object(); private final ReferenceQueue<Object> queue = new ReferenceQueue<Object>();

    private volatile Entry<T>[] table = newTable(1<<3); private int threshold = 6; private int size = 0; public T get(Object key) {
        removeStaleEntries();
        if (key == null) {
            key = NULL;
        }
        int hash = key.hashCode();
        Entry<T>[] table = this.table;
        int index = getIndex(table, hash);
        for (Entry<T> entry = table[index]; entry != null; entry = entry.next) {
            if (entry.isMatched(key, hash)) {
                return entry.value;
            }
        }
        synchronized (NULL) {
            index = getIndex(this.table, hash);
            for (Entry<T> entry = this.table[index]; entry != null; entry = entry.next) {
                if (entry.isMatched(key, hash)) {
                    return entry.value;
                }
            }
            T value = create(key);
            this.table[index] = new Entry<T>(key, hash, value, this.queue, this.table[index]);
            if (++this.size >= this.threshold) {
                if (this.table.length == MAXIMUM_CAPACITY) {
                    this.threshold = Integer.MAX_VALUE;
                }
                else {
                    removeStaleEntries();
                    table = newTable(this.table.length * 2);
                    transfer(this.table, table);
                    if (this.size >= this.threshold / 2) {
                        this.table = table;
                        this.threshold *= 2;
                    }
                    else {
                        transfer(table, this.table);
                    }
                }
            }
            return value;
        }
    }

    protected abstract T create(Object key);

    private void removeStaleEntries() {
        Object ref = this.queue.poll();
        if (ref != null) {
            synchronized (NULL) {
                do {
                    @SuppressWarnings("unchecked")
                    Entry<T> entry = (Entry<T>) ref;
                    int index = getIndex(this.table, entry.hash);

                    Entry<T> prev = this.table[index];
                    Entry<T> current = prev;
                    while (current != null) {
                        Entry<T> next = current.next;
                        if (current == entry) {
                            if (prev == entry) {
                                this.table[index] = next;
                            }
                            else {
                                prev.next = next;
                            }
                            entry.value = null; entry.next = null; this.size--;
                            break;
                        }
                        prev = current;
                        current = next;
                    }
                    ref = this.queue.poll();
                }
                while (ref != null);
            }
        }
    }

    private void transfer(Entry<T>[] oldTable, Entry<T>[] newTable) {
        for (int i = 0; i < oldTable.length; i++) {
            Entry<T> entry = oldTable[i];
            oldTable[i] = null;
            while (entry != null) {
                Entry<T> next = entry.next;
                Object key = entry.get();
                if (key == null) {
                    entry.value = null; entry.next = null; this.size--;
                }
                else {
                    int index = getIndex(newTable, entry.hash);
                    entry.next = newTable[index];
                    newTable[index] = entry;
                }
                entry = next;
            }
        }
    }


    @SuppressWarnings("unchecked")
    private Entry<T>[] newTable(int length) {
        return (Entry<T>[]) new Entry<?>[length];
    }

    private static int getIndex(Entry<?>[] table, int hash) {
        return hash & (table.length - 1);
    }

    private static class Entry<T> extends WeakReference<Object> {
        private final int hash;
        private volatile T value;
        private volatile Entry<T> next;

        Entry(Object key, int hash, T value, ReferenceQueue<Object> queue, Entry<T> next) {
            super(key, queue);
            this.hash = hash;
            this.value = value;
            this.next  = next;
        }

        boolean isMatched(Object key, int hash) {
            return (this.hash == hash) && (key == get());
        }
    }
}
