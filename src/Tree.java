import java.util.*;

/**
 * Created by RobertAtkins on 5/31/16.
 */ 
class Tree<K extends Comparable<K>,V>
{
    private static final boolean RED = true;
    private static final boolean BLACK = false;

    private Node root; // Root of BST

    private class Node
    {
        private K key;  // Key
        private V value;    // Data
        private ArrayList<V> values;  // Value list
        private Node left, right;   // Links to left and right subtrees
        private boolean color; // Color of parent link
        private int N;  // Subtree count

        Node(K key, V value, boolean color, int N)
        {
            values = new ArrayList<>();
            this.key = key;
            this.values.add(value);
            this.color = color;
            this.N = N;
        }
    }

    // Initializes empty BST
    public Tree()
    {

    }
    ////////////////////////////////////////
    // Node tree helper functions         //
    ////////////////////////////////////////
    // Is the node red? False if null
    private boolean isRed(Node n)
    {
        if(n == null)
            return false;
        return n.color == RED;
    }

    // Number of nodes in a subtree rooted at n; 0 if n is null
    private int size(Node n)
    {
        if(n == null)
            return 0;
        return n.N;
    }

    // Number of key-value pairs in the tree
    public int size()
    {
        return size(root);
    }

    // Is the table empty?
    public boolean isEmpty()
    {
        return root == null;
    }

    ////////////////////////////////////////
    // Red-black tree searchDialog functions    //
    ////////////////////////////////////////

    // Public value getter
    public ArrayList<V> get(K key)
    {
        if(key == null)
            throw new NullPointerException("Key argument is null");
        return get(root, key);
    }

    // value associated with the given key in subtree rooted at x; null if no such key
    private ArrayList<V> get(Node node, K key)
    {
        while(node != null)
        {
            int comparator = key.compareTo(node.key);
            if(comparator < 0)
                if(node.left == null)
                    return null;
                else
                    node = node.left;
            else if (comparator > 0)
                if(node.right == null)
                    return null;
                else
                    node = node.right;
            else
                return node.values;
        }
        return null;
    }

    // Does the tree contain the given key?
    public boolean contains(K key)
    {
        return get(key) != null;
    }
    ////////////////////////////////////////
    // Red-black tree insertion functions //
    ////////////////////////////////////////

    // Insertion
    public void put(K key, V value)
    {
        if(key == null)
            throw new NullPointerException("Key argument is null");
        if(value == null)
        {
            delete(key);
            return;
        }

        root = put(root, key, value);
        root.color = BLACK;
    }

    // Insert key-value pair in subtree at h
    private Node put(Node n, K key, V value)
    {
        if(n == null)
            return new Node(key, value, RED, 1);

        int comparator = key.compareTo(n.key);
        if(comparator < 0)
            n.left = put(n.left, key, value);
        else if(comparator > 0)
            n.right = put(n.right, key, value);
        else
            n.values.add(value);

        if(isRed(n.right) && !isRed(n.left))
            n = rotateLeft(n);
        if(isRed(n.left) && isRed(n.left.left))
            n = rotateRight(n);
        if(isRed(n.left) && isRed(n.right))
            flipColors(n);
        n.N = size(n.left) + size(n.right) + 1;

        return n;
    }

    ////////////////////////////////////////
    // Red-black tree deletion functions  //
    ////////////////////////////////////////

    // Removes smallest key and associated value
    public void deleteMin()
    {
        if(isEmpty())
            throw new NoSuchElementException("BST Underflow");

        if(!isRed(root.left) && !isRed(root.right))
            root.color = RED;

        root = deleteMin(root);
        if(!isEmpty())
            root.color = BLACK;
    }

    // Delete key-value pair with minimum key rooted at n
    private Node deleteMin(Node n)
    {
        if(n.left == null)
            return null;
        if(!isRed(n.left) && !isRed(n.left.left))
            n = moveRedLeft(n);

        n.left = deleteMin(n.left);
        return balance(n);
    }

    // Remove largest key and associated value
    public void deleteMax()
    {
        if(isEmpty())
            throw new NoSuchElementException("BST Underflow");
        if(!isRed(root.left) && !isRed(root.right))
            root.color = RED;

        root = deleteMax(root);
        if(!isEmpty())
            root.color = BLACK;
    }

    // Delete key-value pair with maximum key root at n
    private Node deleteMax(Node n)
    {
        if(isRed(n.left))
            n = rotateRight(n);
        if(n.right == null)
            return null;
        if(!isRed(n.right) && !isRed(n.right.left))
            n = moveRedRight(n);

        n.right = deleteMax(n.right);

        return balance(n);
    }

    // Remove specified key from the table
    public void delete(K key)
    {
        if(key == null)
            throw new NullPointerException("Key argument is null");
        if(!contains(key))
            return;

        if(!isRed(root.left) && !isRed(root.right))
            root.color = RED;

        root = delete(root, key);
        if(!isEmpty())
            root.color = BLACK;
    }

    // Deletes the key-value pair rooted at n
    private Node delete(Node n, K key)
    {
        if(key.compareTo(n.key) < 0)
        {
            if(!isRed(n.left) && !isRed(n.left.left))
                n = moveRedLeft(n);
            n.left = delete(n.left, key);
        }
        else
        {
            if(isRed(n.left))
                n = rotateRight(n);
            if(key.compareTo(n.key) == 0 && (n.right == null))
                return null;
            if(!isRed(n.right) && !isRed(n.right.left))
                n = moveRedRight(n);
            if(key.compareTo(n.key) == 0)
            {
                Node x = min(n.right);
                n.key = x.key;
                n.values = x.values;
                n.right = deleteMin(n.right);
            }
            else n.right = delete(n.right, key);
        }

        return balance(n);
    }

    ////////////////////////////////////////
    // Red-black tree helper functions    //
    ////////////////////////////////////////

    // Make left leaning link to the right node
    private Node rotateRight(Node n)
    {
        Node x = n.left;
        n.left = x.right;
        x.right = n;
        x.color = x.right.color;
        x.right.color = RED;
        x.N = n.N;
        n.N = size(n.left) + size(n.right) + 1;
        return x;
    }

    private Node rotateLeft(Node n)
    {
        Node x = n.right;
        n.right = x.left;
        x.left = n;
        x.color = x.left.color;
        x.left.color = RED;
        x.N = n.N;
        n.N = size(n.left) + size(n.right) + 1;
        return x;
    }

    private void flipColors(Node n)
    {
        n.color = !n.color;
        n.left.color = !n.left.color;
        n.right.color = !n.right.color;
    }

    private Node moveRedLeft(Node n)
    {
        flipColors(n);
        if(isRed(n.right.left))
        {
            n.right = rotateRight(n.right);
            n = rotateLeft(n);
            flipColors(n);
        }
        return n;
    }

    private Node moveRedRight(Node n)
    {
        flipColors(n);
        if(isRed(n.left.left))
        {
            n = rotateRight(n);
            flipColors(n);
        }
        return n;
    }

    private Node balance(Node n)
    {
        if(isRed(n.right))
            n = rotateLeft(n);
        if(isRed(n.left) && isRed(n.left.left))
            n = rotateRight(n);
        if(isRed(n.left) && isRed(n.right))
            flipColors(n);
        n.N = size(n.left) + size(n.right) + 1;

        return n;
    }

    ////////////////////////////////////////
    // Utility functions                  //
    ////////////////////////////////////////

    public int height()
    {
        return height(root);
    }

    private int height(Node n)
    {
        if(n == null)
            return -1;
        return 1 + Math.max(height(n.left), height(n.right));
    }

    ////////////////////////////////////////
    // Ordered symbol table methods       //
    ////////////////////////////////////////

    // Return smallest key in table
    public K min()
    {
        if(isEmpty())
            throw new NoSuchElementException("Tree is empty.");
        return min(root).key;
    }

    // Smallest key in subtree rooted at n
    private Node min(Node n)
    {
        if(n.left == null)
            return n;
        else
            return min(n.left);
    }

    // Return largest key in table
    public K max()
    {
        if(isEmpty())
            throw new NoSuchElementException("Tree is empty.");
        return max(root).key;
    }

    // Largest key in subtree rooted at n
    private Node max(Node n)
    {
        if(n.right == null)
            return n;
        else
            return max(n.right);
    }

    // Return largest key in table less than or equal to key
    public K floor(K key)
    {
        if(key == null)
            throw new NullPointerException("Key argument is null");
        if(isEmpty())
            throw new NoSuchElementException("Tree is empty");
        Node n = floor(root, key);
        if(n == null)
            return null;
        else
            return n.key;
    }

    // Largest key in subtree rooted at n less than or equal to key
    private Node floor(Node n, K key)
    {
        if(n == null)
            return null;
        int comparator = key.compareTo(n.key);
        if(comparator == 0)
            return n;
        if(comparator > 0)
            return floor(n.left, key);
        Node x = floor(n.right, key);
        if(x != null)
            return x;
        else
            return n;
    }

    // Returns the smallest key in the table greater than or equal to key
    public K ceiling(K key)
    {
        if(key == null)
            throw new NullPointerException("Key argument is null");
        if(isEmpty())
            throw new NoSuchElementException("Tree is empty");
        Node n = ceiling(root, key);
        if(n == null)
            return null;
        else
            return n.key;
    }

    // Smallest key in subtree rooted at n greater than or equal to key
    private Node ceiling(Node n, K key)
    {
        if(n == null)
            return null;
        int comparator = key.compareTo(n.key);
        if(comparator == 0)
            return n;
        if(comparator > 0)
            return ceiling(n.right, key);
        Node x = ceiling(n.left, key);
        if(x != null)
            return x;
        else
            return n;
    }

    // Returns the Kth smallest key in the table
    public K select(int k)
    {
        if(k < 0 || k > size())
            throw new IllegalArgumentException();
        Node n = select(root, k);
        return n.key;
    }

    // Key of rank k in the subtree rooted at n
    private Node select(Node n, int k)
    {
        int t = size(n.left);
        if(t > k)
            return select(n.left, k);
        else if(t < k)
            return select(n.right, k-t-1);
        else return n;
    }

    // Returns number of keys in the table strictly less than key
    public int rank(K key)
    {
        if(key == null)
            throw new NullPointerException("Key argument is null");
        return rank(root, key);
    }

    // Number of keys less than key subrooted at n
    private int rank(Node n, K key)
    {
        if(n == null)
            return 0;
        int comparator = key.compareTo(n.key);
        if(comparator < 0)
            return rank(n.left, key);
        else if(comparator > 0)
            return 1 + size(n.left) + rank(n.right, key);
        else
            return size(n.left);
    }

    ////////////////////////////////////////
    // Range count and range searchDialog       //
    ////////////////////////////////////////

    // Returns all keys in table as iterable
    public Iterable<K> keys()
    {
        if(isEmpty())
            return new ArrayList<K>();
        return keys(min(), max());
    }

    // Returns all keys in a given range
    public Iterable<K> keys(K low, K high)
    {
        if(low == null)
            throw new NullPointerException("Low argument is null");
        if(high == null)
            throw new NullPointerException("High argument is null");
        ArrayList<K> list = new ArrayList<K>();
        keys(root, list, low, high);
        return list;
    }

    // Add the keys between low and high subrooted at n to the list
    private void keys(Node n, ArrayList<K> list, K low, K high)
    {
        if(n == null)
            return;
        int comparatorLow = low.compareTo(n.key);
        int comparatorHigh = high.compareTo(n.key);
        if(comparatorLow < 0)
            keys(n.left, list, low, high);
        if(comparatorLow <= 0 && comparatorHigh >= 0)
            list.add(n.key);
        if(comparatorHigh > 0)
            keys(n.right, list, low, high);
    }

    // Return the number of keys in the tree
    public int size(K low, K high)
    {
        if(low == null)
            throw new NullPointerException("Low argument is null");
        if(high == null)
            throw new NullPointerException("High argument is null");

        if(low.compareTo(high) > 0)
            return 0;
        if(contains(high))
            return rank(high) - rank(low) - 1;
        else
            return rank(high) - rank(low);
    }

    ////////////////////////////////////////
    // Tree integrity checkers            //
    ////////////////////////////////////////
    private boolean check()
    {
        if(!isBST())
            System.out.println("");
        if(!isSizeConsistent())
            System.out.println("");
        if(!isRankConsistent())
            System.out.println("");
        if(!is23())
            System.out.println("");
        if(!isBalanced())
            System.out.println("");

        return isBST() && isSizeConsistent() && isRankConsistent() && is23() && isBalanced();
    }

    // Does the tree satisfy symmetric order
    private boolean isBST()
    {
        return isBST(root, null, null);
    }

    // Is the tree at n a bst?
    private boolean isBST(Node n, K min, K max)
    {
        if(n == null)
            return true;
        if(min != null && n.key.compareTo(min) <= 0)
            return false;
        if(max != null && n.key.compareTo(max) <= 0)
            return false;
        return isBST(n.left, min, n.key) && isBST(n.right, n.key, max);
    }

    // Are the size fields correct?
    private boolean isSizeConsistent()
    {
        return isSizeConsistent(root);
    }
    private boolean isSizeConsistent(Node n)
    {
        if(n == null)
            return true;
        if(n.N != size(n.left) + size(n.right) + 1)
            return false;
        return isSizeConsistent(n.left) && isSizeConsistent(n.right);
    }

    // Are ranks consistent?
    private boolean isRankConsistent()
    {
        for(int i = 0; i < size(); i++)
            if(i != rank(select(i)))
                return false;
        for(K key : keys())
            if(key.compareTo(select(rank(key))) != 0)
                return false;
        return true;
    }

    // Does the tree have no red right links, and at most one (left) red link in a row on any path?
    private boolean is23()
    {
        return is23(root);
    }
    private boolean is23(Node n)
    {
        if(n == null)
            return true;
        if(isRed(n.right))
            return false;
        if(n != root && isRed(n) && isRed(n.left))
            return false;
        return is23(n.left) && is23(n.right);
    }

    // Do all root to leaf paths have same number of black edges?
    private boolean isBalanced()
    {
        int black = 0;
        Node n = root;
        while(n != root)
        {
            if(n != null)
                black++;
            n = n.left;
        }
        return isBalanced(root, black);
    }

    // Do all root to leaf paths have same number of black links?
    private boolean isBalanced(Node n, int black)
    {
        if(n == null)
            return black == 0;
        if(!isRed(n))
            black --;
        return isBalanced(n.left, black) && isBalanced(n.right, black);
    }

    public static Tree<String, Asset>
            customIDBST,
            assetTagBST,
            ownerFullNameBST,
            ownerFirstNameBST,
            ownerLastNameBST,
            ownerIDBST,
            emailBST,
            locationBST,
            typeBST,
            manufacturerBST,
            modelBST,
            serialBST,
            propertyManagerFullNameBST,
            propertyManagerFirstNameBST,
            propertyManagerLastNameBST,
            propertyManagerIDBST,
            purchaserFullNameBST,
            purchaserFirstNameBST,
            purchaserLastNameBST,
            purchaserIDBST;

    public static void setTrees()
    {
        customIDBST = new Tree<>();
        assetTagBST = new Tree<>();
        ownerFullNameBST = new Tree<>();
        ownerFirstNameBST = new Tree<>();
        ownerLastNameBST = new Tree<>();
        ownerIDBST = new Tree<>();
        emailBST = new Tree<>();
        locationBST = new Tree<>();
        typeBST = new Tree<>();
        manufacturerBST = new Tree<>();
        modelBST = new Tree<>();
        serialBST = new Tree<>();
        propertyManagerFullNameBST = new Tree<>();
        propertyManagerFirstNameBST = new Tree<>();
        propertyManagerLastNameBST = new Tree<>();
        propertyManagerIDBST = new Tree<>();
        purchaserFullNameBST = new Tree<>();
        purchaserFirstNameBST = new Tree<>();
        purchaserLastNameBST = new Tree<>();
        purchaserIDBST = new Tree<>();

        if(!Asset.getMap().isEmpty())
        {
            for(Asset a : Asset.getMap().keySet())
            {
                customIDBST.put(a.getName().toLowerCase(), a);
                assetTagBST.put(a.getAssetTag(), a);
                ownerFullNameBST.put(a.getOwnerFullName().toLowerCase().toLowerCase(), a);
                ownerFirstNameBST.put(a.getOwner().getFirstName().toLowerCase() ,a);
                ownerLastNameBST.put(a.getOwner().getLastName().toLowerCase(),a);
                ownerIDBST.put(a.getOwner().getCustomId(), a);
                emailBST.put(a.getOwner().getEmail(), a);
                locationBST.put(a.getLocation(), a);
                typeBST.put(a.getType(), a);
                manufacturerBST.put(a.getManufacturer(), a);
                modelBST.put(a.getModel(), a);
                serialBST.put(a.getSerial(), a);
                propertyManagerFullNameBST.put(a.getPropertyManagerFullName().toLowerCase(), a);
                propertyManagerFirstNameBST.put(a.getPropertyManager().getFirstName().toLowerCase(),a);
                propertyManagerLastNameBST.put(a.getPropertyManager().getLastName().toLowerCase(),a);
                propertyManagerIDBST.put(a.getOwner().getCustomId(), a);
                purchaserFullNameBST.put(a.getPurchaserFullName().toLowerCase(), a);
                purchaserFirstNameBST.put(a.getPurchaser().getFirstName().toLowerCase(),a);
                purchaserLastNameBST.put(a.getPurchaser().getLastName().toLowerCase(),a);
                purchaserIDBST.put(a.getPurchaser().getCustomId(), a);
            }
        }
    }
}
