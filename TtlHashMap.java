import static java.util.Collections.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class TtlHashMap<K, V> implements Map<K, V> {

    private final HashMap<K, V> store = new HashMap<>();
    private final HashMap<K, Long> timestamps = new HashMap<>();
    private final HashMap<K, Long> ttl = new HashMap<>();

    public TtlHashMap(){
        CleanerTrhead ct = new CleanerTrhead();
        ct.start();
    }
    public void putTtl(K key, TimeUnit ttlUnit, long ttlValue) {
        ttl.put(key, ttlUnit.toNanos(ttlValue));
        clearExpired();
    }

    @Override
    public V get(Object key) {
        V value = this.store.get(key);

        if (value != null && expired(key, value)) {
            ttl.remove(key);
            store.remove(key);
            timestamps.remove(key);
            return null;
        } else {
            return value;
        }
    }

    private boolean expired(Object key, V value) {
        if(ttl.get(key)==0){
            return false;
        }
        return (System.nanoTime() - timestamps.get(key)) > ttl.get(key);
    }

    @Override
    public V put(K key, V value) {
        clearExpired();
        timestamps.put(key, System.nanoTime());
        return store.put(key, value);
    }

    @Override
    public int size() {
        return store.size();
    }

    @Override
    public boolean isEmpty() {
        return store.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        clearExpired();
        return store.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        clearExpired();
        return store.containsValue(value);
    }

    @Override
    public V remove(Object key) {
        timestamps.remove(key);
        ttl.remove(key);
        return store.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Map.Entry<? extends K, ? extends V> e : m.entrySet()) {
            this.put(e.getKey(), e.getValue());
        }
    }

    @Override
    public void clear() {
        ttl.clear();
        timestamps.clear();
        store.clear();
    }

    @Override
    public Set<K> keySet() {
        clearExpired();
        return unmodifiableSet(store.keySet());
    }

    @Override
    public Collection<V> values() {
        clearExpired();
        return unmodifiableCollection(store.values());
    }

    @Override
    public Set<java.util.Map.Entry<K, V>> entrySet() {
        clearExpired();
        return unmodifiableSet(store.entrySet());
    }

    private void clearExpired() {
        for (K k : store.keySet()) {
            this.get(k);
        }
    }

    class CleanerTrhead extends Thread{

     @Override
    public void run() {
        while (true) {
            cleanMap();
            try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void cleanMap() {
        clearExpired();
    }
}
