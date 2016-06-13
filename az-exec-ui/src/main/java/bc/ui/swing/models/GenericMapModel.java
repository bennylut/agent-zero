/* 
 * The MIT License
 *
 * Copyright 2016 Benny Lutati.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package bc.ui.swing.models;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author BLutati
 */
public class GenericMapModel<K, V> extends LinkedHashMap<K, V> {

    LinkedList<MapChangeListener<K,V>> listeners = new LinkedList<MapChangeListener<K, V>>();

    public GenericMapModel(Map<? extends K, ? extends V> m) {
        super(m);
    }

    /**
     * @param key
     * @param defaultValue
     * @return map{key} or defaultValue if map{key} == null (defaultValue will be added if key not exists)
     */
    public V get(K key, V defaultValue) {
        V temp = super.get(key);
        if (temp == null){
            put(key, defaultValue);
            temp = defaultValue;
        }

        return temp;
    }

    public GenericMapModel() {
    }

    public GenericMapModel(int initialCapacity) {
        super(initialCapacity);
    }

    public GenericMapModel(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public void addListener(MapChangeListener<K,V> l){
        listeners.add(l);
    }

    public void removeListener(MapChangeListener<K,V> l){
        listeners.remove(l);
    }

    @Override
    public void clear() {
        super.clear();
        fireItemsCleared();
    }

    @Override
    public V put(K key, V value) {
        V v = super.put(key, value);
        if (v != null) fireItemChanged(key);
        else fireItemAdded(key, value);
        return v;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        super.putAll(m);
        fireItemsAdded(m);
    }

    @Override
    public V remove(Object key) {
        V v = super.remove((K)key);
        fireItemRemoved(key,v);
        return v;
    }


    /**
     * working in linear speed.
     * @param value
     * @return
     */
    public K getKey(V value){
        for (Entry<K, V> e : entrySet()) if (e.getValue().equals(value)) return e.getKey();
        return null;
    }

    private void fireItemsCleared() {
        for (MapChangeListener<K, V> l : listeners) l.onCleared(this);
    }

    private void fireItemAdded(K key, V value) {
        for (MapChangeListener<K, V> l : listeners) l.onItemAdded(this, key, value);
    }

    private void fireItemsAdded(Map<? extends K, ? extends V> m){
        for (MapChangeListener<K, V> l : listeners) l.onItemsAdded(this, m);
    }

    private void fireItemRemoved(Object key, V v) {
        for (MapChangeListener<K, V> l : listeners) l.onItemRemoved(this, (K)key, v);
    }

    public void fireItemChanged(K newt) {
        for (MapChangeListener<K, V> l : listeners) l.onItemChanged(this, newt);
    }

    public static interface MapChangeListener<K,V>{
        public void onItemAdded(GenericMapModel<K,V> source, K key, V value);
        public void onItemRemoved(GenericMapModel<K,V> source, K key, V value);
        public void onCleared(GenericMapModel<K,V> source);
        public void onItemsAdded(GenericMapModel<K,V> source, Map<? extends K, ? extends V> items);
        public void onItemChanged(GenericMapModel<K,V> source, K key);
    }

    public static class MapChangeHandler<K,V> implements MapChangeListener<K, V>{

        @Override
        public void onItemAdded(GenericMapModel<K, V> source, K key, V value) {
        }

        @Override
        public void onItemRemoved(GenericMapModel<K, V> source, K key, V value) {
        }

        @Override
        public void onCleared(GenericMapModel<K, V> source) {
        }

        @Override
        public void onItemsAdded(GenericMapModel<K, V> source, Map<? extends K, ? extends V> items) {
        }

        @Override
        public void onItemChanged(GenericMapModel<K, V> source, K key) {
        }

    }
}
