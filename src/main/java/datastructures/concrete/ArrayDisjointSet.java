package datastructures.concrete;

import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IDisjointSet;
import misc.exceptions.NotYetImplementedException;

/**
 * See IDisjointSet for more details.
 */
public class ArrayDisjointSet<T> implements IDisjointSet<T> {
    // Note: do NOT rename or delete this field. We will be inspecting it
    // directly within our private tests.
    private int[] pointers;
    private static final int INIT_LENGTH = 10;
    private IDictionary<T, Integer> data;
    private int size;
    // However, feel free to add more methods and private helper methods.
    // You will probably need to add one or two more fields in order to
    // successfully implement this class.

    public ArrayDisjointSet() {
        pointers = new int[INIT_LENGTH];
        data = new ChainedHashDictionary<T, Integer>();
        size = 0;
    }

    @Override
    public void makeSet(T item) {
        if (this.contains(item)) {
            throw new IllegalArgumentException("Already in the Set");
        }
        if (this.pointers.length == this.size) {
            int[] temp = new int[this.pointers.length * 2];
            for (int i = 0; i < this.pointers.length; i++) {
                temp[i] = this.pointers[i];
            }
            this.pointers = temp;
        }
        this.data.put(item, this.size);
        this.pointers[this.size] = -1;
        this.size++;
    }

    @Override
    public int findSet(T item) {
        if (!this.contains(item)) {
            throw new IllegalArgumentException("Not in the Set");
        }
        int index = this.data.get(item);        
        return this.findRank(index);
    }
    
    private int findRank(int index) {
        if (this.pointers[index] < 0) {
            return index;
        }
        int result = this.findRank(this.pointers[index]);
        this.pointers[index] = result;
        return result;
    }

    private boolean contains(T item) {
        return this.data.containsKey(item);
    }
    
    @Override
    public void union(T item1, T item2) {
        if (!this.contains(item1) || !this.contains(item2)) {
            throw new IllegalArgumentException("Not in the set");
        }
        int index1 = this.findSet(item1);
        int index2 = this.findSet(item2); 
        if (index1 == index2) {
            throw new IllegalArgumentException("in the same set");
        }
        // get the index from data of item1 and item2; then check in the pointers;
        // returns neg number; (rank * -1) -1
        
        int rank1 = this.pointers[index1];
        int rank2 = this.pointers[index2];
        if (rank2 < rank1) { // rank2 is actually larger than rank1!!!!!!!
            this.pointers[index1] = index2;
            
        } else if (rank2 > rank1){
            this.pointers[index2] = index1;
        } else {
            this.pointers[index2] = index1;
            this.pointers[index1]--;
        }
                
    }
}
