import java.util.HashSet;
import java.util.Set;

/**
 * Created by RobertAtkins on 4/18/16.
 */
abstract class Map<K,V>
{

    // Default hash table size (prime)
    private static final int DEFAULT_INITIAL_CAPACITY = 7;

    // Maximum hash table size
    private static final int MAX_CAPACITY = 1 << 30;

    // Default load factor threshold
    private static final float DEFAULT_MAX_LOAD_FACTOR = 0.75f;

    // Instance hash table capacity
    private int capacity;

    // Instance load factor threshold
    private float loadFactorThreshold;

    // Number of elements in the set
    private int size;

    // Hash array with each element as a linked list
    private Entry<K,V>[] table;

    /** Construct map with the default capacity and load factor */
    Map()
    {
        this(DEFAULT_INITIAL_CAPACITY, DEFAULT_MAX_LOAD_FACTOR);
    }

    /** Construct a  map with a specified capacity and load factor
     *
     * @param c Integer value for the capacity of the map
     * @param t Float value for the threshold */
    private Map(int c, float t)
    {
        if(c > MAX_CAPACITY)
            this.capacity = MAX_CAPACITY;
        else
            this.capacity = c;
        this.loadFactorThreshold = t;
        table = new Entry[capacity];
    }

    /** Hash function
     *
     * @param hc Int value for the hashcode
     * @return Value between 0 and size-1
     */
    private int hash(int hc)
    {
        return hc % capacity;
    }

    /** Resize the hash table to provided capacity by rehashing the keys
     *
     * @param newCapacity The new capacity for the table
     */
    private void resize(int newCapacity)
    {
        Set<Entry<K,V>> temp = entrySet(); // Get entries
        capacity = newCapacity;
        table = new Entry[capacity]; // Create new resized table

        size = 0; // Reset size
        for(Entry<K,V> e : temp)
            put(e.getKey(), e.getValue()); // Add old elements to new table
    }

    /** Remove all entries from the table */
    private void removeEntries()
    {
        for(int i = 0; i < capacity; i++)
        {
            table[i] = null;
        }
    }

    public void printTable()
    {
        for(int i = 0; i < table.length; i++)
        {
            if(table[i] != null && table[i].getKey() != null)
                System.out.println("[" + table[i].getKey() + "," + table[i].getValue() + "]");
            else if(table[i] == null || table[i].getKey() == null)
                System.out.println("[NULL],[NULL]");
        }
    }

    /** Clear the map */
    public void clear()
    {
        size = 0;
        removeEntries();
    }

    /** Returns true if the map contains the key
     *
     * @param key key */
    public boolean containsKey(K key)
    {
        return get(key) != null;
    }

    /** Returns true if the map contains the value
     *
     * @param value value */
    public boolean containsValue(V value)
    {
        for(int i = 0; i < table.length; i++)
            if (table[i] != null && table[i].value.equals(equals(value)))
                return true;
        return false;
    }

    /** Get all entries from the map
     *
     * @return set of entries in the map */
    public Set<Entry<K, V>> entrySet()
    {
        Set<Entry<K,V>> set = new HashSet<>();
        for(int i = 0; i < capacity; i++)
            if(table[i] != null)
                set.add(table[i]);

        return set;
    }

    /** Get a specific object in the map
     *
     * @param key key */
    public V get(K key)
    {
        // Linear probing
        int i = hash(key.hashCode());
        while (table[i] != null) {
            if (table[i].key != null && table[i].key.equals(key))
                return table[i].value;
            i = (i + 1) % table.length;
        }

        return null;
    }

    /** Determine if the map is empty or not
     *
     * @return
     */
    public boolean isEmpty()
    {
        return size() == 0;
    }

    /** Get a set of the keys in the table
     *
     * @return Set consisting of keys in the table
     */
    public Set<K> keySet()
    {
        Set<K> set = new HashSet<>();

        for(int i = 0; i < capacity; i++)
            if(table[i] != null)
                set.add(table[i].key);

        return set;
    }

    /** Add a new entry to the table
     *
     * @param key Key associated with the entry
     * @param value Value of the entry
     * @return
     */
    public V put(K key, V value)
    {
        if(containsKey(key))
        {
            remove(key);
        }
        if((double)size >= capacity * loadFactorThreshold)
            resize(capacity * 2);

        int i = hash(key.hashCode());
        while(table[i] != null && table[i].key != null)
            i = (i + 1) % table.length;

        table[i] = new Entry<>(key, value);
        size++;
        return value;
    }

    /** Remove an entry from the map
     *
     * @param key
     */
    public void remove(K key)
    {
        int i = hash(key.hashCode());

        if (table[i] != null && table[i].key.equals(key))
        {
            table[i] = null;
            size--;
        }
    }

    /** Get the size of the hash table
     *
     * @return The size of the hash table
     */
    public int size()
    {
        return size;
    }

    /** Get a set of the values in the map
     *
     * @return A set of the values
     */
    public Set<V> values()
    {
        Set<V> set = new HashSet<>();
        for(Entry<K,V> e : table)
            if(e != null)
                set.add(e.value);
        return set;
    }

    class Entry<K, V> {
        K key;
        V value;

        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "[" + key + ", " + value + "]";
        }
    }
}
