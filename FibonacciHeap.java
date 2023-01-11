import java.util.Iterator;

/**
 * FibonacciHeap
 *
 * An implementation of a Fibonacci Heap over integers.
 */
public class FibonacciHeap
{
    // fields of FibonacciHeap.
    public HeapNode min;
    protected HeapNode treeListStart;
    public int treeCount;
    public int size;
    public static int linkCount;

    public static int cutCount;

    public int markedCount;

    /**
     * Construct a node from data
     * @param min
     * @param treeListStart
     * @param treeCount
     * @param size
     * @param markedCount
     */
    public FibonacciHeap(HeapNode min,
                         HeapNode treeListStart,
                         int treeCount,
                         int size,
                         int markedCount) {
        this.min = min;
        this.treeListStart = treeListStart;
        this.treeCount = treeCount;
        this.size = size;
        this.markedCount = markedCount;

    }

    /**
     * Constructs a heap using another heap data
     * @post: new heap point to the old heap nodes
     * @param heap heap to construct from
     */
    public FibonacciHeap(FibonacciHeap heap){
        this(
                heap.min,
                heap.treeListStart,
                heap.treeCount,
                heap.size,
                heap.markedCount
        );
    }

    /**
     * Default constructor, creates an empty heap.
     */
    public FibonacciHeap(){
        this(null, null, 0,0,0);
    }

    public HeapNode treeListStart() {
        return this.min;
    }

    public int getTreeCount() {
        return this.treeCount;
    }

    public int getSize() {
        return this.size;
    }

    public HeapNode getFirst(){
        return this.treeListStart;
    }

    public int getLinkCount() {
        return linkCount;
    }

    public int getCutCount() {
        return cutCount;
    }

    public int getMarkedCount() {
        return this.markedCount;
    }

   /**
    * public boolean isEmpty()
    *
    * Returns true if and only if the heap is empty.
    *   
    */
    public boolean isEmpty() {
    	return this.getSize() < 1 || this.min == null;
    }
		
   /**
    * public HeapNode insert(int key)
    *
    * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap.
    * The added key is assumed not to already belong to the heap.  
    * 
    * Returns the newly created node.
    */
    public HeapNode insert(int key) {
        // insert the created node to the heap if the heap is empty.
        if (this.isEmpty()) {
            HeapNode newNode = this.createNode(key, 0, false, null, null, null, null);
            newNode.next = newNode;
            newNode.prev = newNode;

            this.min = newNode;
            this.treeListStart = newNode;
            this.size = 1;
            this.treeCount = 1;

            return newNode;
        }

        // insert the created node to the heap if the heap is not empty.
        HeapNode newNode = this.createNode(key, 0, false, null, null, null, null);
        this.treeListStart.prev.next = newNode;
        newNode.next = this.treeListStart;
        newNode.prev = this.treeListStart.getPrev();
        this.treeListStart.prev = newNode;
        this.treeListStart = newNode;

        this.updateMin(newNode);
        this.size++;
        this.treeCount++;

        return newNode;
    }

    private void insertNode(HeapNode root) {
        // insert the created node to the heap if the heap is empty.
        if (this.isEmpty()) {
            root.next = root;
            root.prev = root;

            this.min = root;
            this.treeListStart = root;
            this.size = 1;
            this.treeCount = 1;

            return;
        }

        // insert the created node to the heap if the heap is not empty.
        this.treeListStart.prev.next = root;
        this.treeListStart.prev = root;
        this.treeListStart = root;

        this.updateMin(root);
        this.size++;
        this.treeCount++;

        return;
    }

    private HeapNode createNode(int key, int rank, boolean mark, HeapNode child, HeapNode parent, HeapNode next, HeapNode prev) {
        return new HeapNode(key, rank, mark, child, parent, next, prev);
    }

    private void updateMin(HeapNode newNode) {
        if (newNode.key < this.min.key) {
            this.min = newNode;
        }
    }

   /**
    * public void deleteMin()
    *
    * @- Deletes the node containing the minimum key.
    * @post: update minimum pointer
    * @post: heap is consolidated to a binomial heap
    */
    public void deleteMin() {
        deleteMinAndDontFindNew();
        this.min = findMin();
        consolidate();
    }

    /**
     * delete minimum and don't update the minimum pointer
     * @post: updates size, marked count, and tree count
     * @post: update treeListStart pointer
     */
    private void deleteMinAndDontFindNew(){
        if(this.isEmpty()){
            return;
        }
        int unmarkCounter = 0;
        int treeCounter = 0;
        // prepare children for insertion
        HeapNode child = this.min.child;
        HeapNode inPlaceOfMin; // for use in updating start of list
        if (child != null) {
            for (HeapNode node : new IterableNode(child)) {
                node.parent = null;
                if (node.isMarked()) {
                    unmarkCounter++;
                    node.unMark();
                }
                treeCounter++;
            }
            // insert child
            addInsteadOfMin(child);
            inPlaceOfMin = child;
        } else{
            HeapNode afterMin = this.min.next;
            HeapNode beforeMin = this.min.prev;
            beforeMin.next = afterMin;
            afterMin.prev = beforeMin;
            inPlaceOfMin = afterMin;
        }
        // remove min from the heap
        treeCounter--;

        if (this.min == this.treeListStart){
            if (size == 1) { // after deletion the tree is empty
                this.treeListStart = null;
            }
            else {
                this.treeListStart = inPlaceOfMin;
            }
        }
        // update fields
        this.addToCounters(treeCounter, -1, -unmarkCounter);
    }

    private void addInsteadOfMin(HeapNode startListToInsert) {
        HeapNode endListToInsert = startListToInsert.prev;
        HeapNode afterMin = this.min.next;
        HeapNode beforeMin = this.min.prev;

        if (afterMin == this.min){
            this.treeListStart = startListToInsert;
            return;
        }

        beforeMin.next = startListToInsert;
        startListToInsert.prev = beforeMin;
        endListToInsert.next = afterMin;
        afterMin.prev = endListToInsert;
    }


    /**
     * Consolidate trees in the heap until we have a binomial heap
     * @post: updates tree count and link count
     * @post: updates treeListStart pointer
     */
    protected void consolidate(){
        if (this.isEmpty()){
            return;
        }
        HeapNode[] buckets = new HeapNode[size+1];

        int linkCounter = 0;
        HeapNode tree = treeListStart;
        for (int i = 0; i < treeCount;) {
            int rank = tree.rank;
            if (buckets[rank] == null){
                // bucket is empty
                buckets[rank] = tree;
                tree = tree.next;
                i++;
                continue; // avoid nested ifs
            }
            //else
            // bucket is filled, connect nodes;
            tree = connectTrees(buckets[rank], tree);
            linkCounter++;
            // empty the bucket
            buckets[rank] = null;
        }

        // order new list
        int treeCounter = 0;
        HeapNode newStartOfList = null;
        for (HeapNode node : buckets){
            if (node == null){
                continue;
            }
            treeCounter++; // count trees
            if (newStartOfList == null){
                // first time
                node.next = node;
                node.prev = node;
                newStartOfList = node;
            } else{
                // connect to existing list
                HeapNode last = newStartOfList.prev;
                last.next = node;
                node.prev = last;
                node.next = newStartOfList;
                newStartOfList.prev = node;
            }
        }
        // update heap fields and pointers
        this.treeListStart = newStartOfList;
        this.treeCount = treeCounter;
        FibonacciHeap.linkCount += linkCounter;
    }

    /**
     * Connect two trees such that the smaller one is at the top
     * @param tree1 was first in order in the original list
     * @param tree2 was second in order in the original list
     * @return the root of the connected two nodes (smaller key)
     * @post: $ret.next == tree2.next
     */
    private HeapNode connectTrees(HeapNode tree1, HeapNode tree2){
        // pointer assignment for readability
        HeapNode smaller = tree1.key < tree2.key? tree1 : tree2;
        HeapNode larger = tree1.key < tree2.key? tree2 : tree1;
        HeapNode childStart = smaller.child;
        HeapNode childEnd = (childStart != null)? childStart.prev : null;

        // insert larger as the first child in the list
        smaller.next = tree2.next;
        smaller.child = larger;
        larger.parent = smaller;
        if (childStart != null) {
            larger.next = childStart;
            childStart.prev = larger;
            childEnd.next = larger;
            larger.prev = childEnd;
        } else{
            larger.next = larger;
            larger.prev = larger;
        }
        // update rank
        smaller.rank++;
        return smaller;
    }


   /**
    * public HeapNode findMin()
    *
    * Returns the node of the heap whose key is minimal, or null if the heap is empty.
    *
    */
    public HeapNode findMin() {
        if (this.isEmpty()){
            return null;
        }

    	HeapNode min = this.treeListStart;
        for (HeapNode node : new IterableNode(this.treeListStart)) {
            if (node.key < min.key){
                min = node;
            }
        }

        return min;
    }

    private void updateMin(){
        this.min = findMin();
    }

    /**
     * _INCREASES_ counter fields by the given values.
     * @pre: values can be negative to decrease.
     * @post: updates all instance counters
     */
    private void addToCounters(int treeCount,
                               int size,
                               int markedCount){
        this.treeCount += treeCount;
        this.size += size;
        this.markedCount += markedCount;
    }

   /**
    * public void meld (FibonacciHeap heap2)
    *
    * Melds heap2 with the current heap.
    *
    */
    public void meld (FibonacciHeap heap2) {
        if (heap2.isEmpty()){
            return;
        }

        if (this.isEmpty() || heap2.min.key < this.min.key){
            this.min = heap2.min;
        }

        this.addToEndOfTreeList(heap2.treeListStart);
        this.addToCounters(
                heap2.getTreeCount(),
                heap2.getSize(),
                heap2.getMarkedCount());
    }

    /**
     * Adds to the end of trees list
     * @param startListToInsert node at the end of the new list (can be one long)
     *                   Should have correct prev and next pointers
     * @post: update treeListStart pointer
     */
    private void addToEndOfTreeList(HeapNode startListToInsert){
        if (this.isEmpty()) {
            this.treeListStart = startListToInsert;
            return;
        }
        HeapNode startOrigin = this.treeListStart;
        HeapNode lastOrigin = startOrigin.prev;
        HeapNode lastListToInsert = startListToInsert.prev;

        // concatenate
        lastOrigin.next = startListToInsert;
        startListToInsert.prev = lastOrigin;
        lastListToInsert.next = startOrigin;
        startOrigin.prev = lastListToInsert;
    }

    /**
     Adds to the start of trees list
     * @param startListToInsert node at the start of the new list (can be one long)
     *                   Should have correct prev and next pointers
     * @post: update treeListStart pointer
     */
    private void addToStartOfTreeList(HeapNode startListToInsert){
        if (this.isEmpty()) {
            this.treeListStart = startListToInsert;
            return;
        }
        HeapNode startOrigin = this.treeListStart;
        HeapNode lastOrigin = startOrigin.prev;
        HeapNode lastListToInsert = startListToInsert.prev;

        // concatenate
        startOrigin.prev = lastListToInsert;
        lastListToInsert.next = startOrigin;
        lastOrigin.next = startListToInsert;
        startListToInsert.prev = lastOrigin;

        // update pointer
        this.treeListStart = startListToInsert;
    }

   /**
    * public int size()
    *
    * Returns the number of elements in the heap.
    *   
    */
    public int size() {
    	return this.size; // should be replaced by student code
    }
    	
    /**
    * public int[] countersRep()
    *
    * Return an array of counters. The i-th entry contains the number of trees of order i in the heap.
    * (Note: The size of the array depends on the maximum order of a tree.)
    * 
    */
    public int[] countersRep() {
        if (this.isEmpty()){
            return new int[]{};
        }
        // all trees but one has rank 0, the last tree has them all as children
        int theoreticalMaxRank = size - (treeCount - 1);
        int actulaMaxRank = 0;
    	int[] arr = new int[theoreticalMaxRank+1];
        for (HeapNode tree : new IterableNode(treeListStart)){
            arr[tree.rank]++;
            actulaMaxRank = (actulaMaxRank > tree.rank)? actulaMaxRank : tree.rank;
        }
        int[] noNullArr = new int[actulaMaxRank + 1];
        for (int i = 0; i < noNullArr.length; i++) {
            noNullArr[i] = arr[i];
        }
        return noNullArr;
    }
	
   /**
    * public void delete(HeapNode x)
    *
    * Deletes the node x from the heap.
	* It is assumed that x indeed belongs to the heap.
    *
    */
    public void delete(HeapNode x) {
        if (x == this.min) {
            this.deleteMin();
            return;
        }

        HeapNode oldMin = this.min;
        if (oldMin.key == Integer.MIN_VALUE){
            x.key = Integer.MIN_VALUE;
            this.decreaseKey(x, 0);
        } else {
            this.decreaseKey(x, x.key - oldMin.key + 1);
        }
        this.deleteMinAndDontFindNew();
        this.min = oldMin;
        this.consolidate();
    }

   /**
    * public void decreaseKey(HeapNode x, int delta)
    *
    * Decreases the key of the node x by a non-negative value delta. The structure of the heap should be updated
    * to reflect this change (for example, the cascading cuts procedure should be applied if needed).
    */
    public void decreaseKey(HeapNode x, int delta) {
        if (this.isEmpty() || x == null || delta < 0) {
            return;
        }

        HeapNode keyToDecrease = x;
        x.key -= delta;
        int decreasedValue = x.key;

        if (x.getParent() == null) {
            if (x.getKey() <= this.min.getKey())
                this.min = x;
            return;
        }

        if (x.getParent().getKey() < x.getKey()) {
            return;
        }

        if (!x.isMarked()){
            x.mark = true;
            markedCount++;
        }

        //if we got here, we need to cut x.
        while(x.isMarked()) {
            HeapNode parent = x.parent;
            cutNode(x);

            if (x.isMarked()) {
                x.mark = false;
                this.markedCount--;
            }

            x = parent;
        }


        // update min.
        if (decreasedValue <= this.min.getKey()) {
            min = keyToDecrease;
        }

        if (x.getParent() != null) {
            x.mark = true;
            this.markedCount++;
        }
    }

    public void cutNode(HeapNode x) {
        x.getParent().rank--;

        if (x.getParent().getChild() == x) {
            if (x.getNext() == x)
                x.getParent().child = null;
            else
                x.getParent().child = x.getNext();
        }

        x.parent = null;

        HeapNode nextNode = x.getNext();
        HeapNode prevNode = x.getPrev();
        prevNode.next = nextNode;
        nextNode.prev = prevNode;

        x.next = x;
        x.prev = x;

        x.prev = this.treeListStart.getPrev();
        x.getPrev().next = x;
        x.next = this.treeListStart;
        this.treeListStart.prev = x;
        this.treeListStart = x;

        treeCount++;
        cutCount++;
    }

   /**
    * public int nonMarked() 
    *
    * This function returns the current number of non-marked items in the heap
    */
    public int nonMarked() {
        if (this.isEmpty()) {
            return 0;
        }

        return this.getSize() - this.getMarkedCount();
    }

   /**
    * public int potential() 
    *
    * This function returns the current potential of the heap, which is:
    * Potential = #trees + 2*#marked
    *
    * In words: The potential equals to the number of trees in the heap
    * plus twice the number of marked nodes in the heap. 
    */
    public int potential() {
        if (this.isEmpty()) {
            return 0;
        }

        return this.treeCount + (2 * this.markedCount);
    }

   /**
    * public static int totalLinks()
    *
    * This static function returns the total number of link operations made during the
    * run-time of the program. A link operation is the operation which gets as input two
    * trees of the same rank, and generates a tree of rank bigger by one, by hanging the
    * tree which has larger value in its root under the other tree.
    */
    public static int totalLinks() {
        return linkCount;
    }

   /**
    * public static int totalCuts() 
    *
    * This static function returns the total number of cut operations made during the
    * run-time of the program. A cut operation is the operation which disconnects a subtree
    * from its parent (during decreaseKey/delete methods). 
    */
    public static int totalCuts() {
        return cutCount;
    }

     /**
    * public static int[] kMin(FibonacciHeap H, int k) 
    *
    * This static function returns the k smallest elements in a Fibonacci heap that contains a single tree.
    * The function should run in O(k*deg(H)). (deg(H) is the degree of the only tree in H.)
    *  
    * ###CRITICAL### : you are NOT allowed to change H. 
    */
    public static int[] kMin(FibonacciHeap H, int k) {
        if (H.isEmpty() || k == 0){
            return new int[]{};
        }
        int[] minimalNodes = new int[k];
        binomialHeap helper = new binomialHeap(H.size);
        helper.insert(H.min);
        for (int i = 0; i < k; i++) {
            HeapNode node = helper.getMin();
            minimalNodes[i] = node.key;
            helper.deleteMin();
            if (node.child != null) {// insert all children on current min
                for (HeapNode child : new IterableNode(node.child)) {
                    helper.insert(child);
                }
            }
        }
        return minimalNodes; // should be replaced by student code
    }

    /**
     * helper class for kMin
     */
    private static class binomialHeap {
        HeapNode[] heap;
        int size = 0;

        /**
         * builds a new helper binomial heap for kMin
         * @param s maximum size of the heap
         */
        private binomialHeap(int s){
            heap = new HeapNode[s];
        }

        /**
         * inserts to heap and heapifies down
         * @param node the node to insert to the heap
         */
        private void insert(HeapNode node){
            if (node == null){
                return;
            }
            size++;
            set(size, node);
            heapifyUp(size);
        }

        private void heapifyUp(int i){
            HeapNode ithNode = get(i);
            HeapNode parent = get(parentIndex(i));
            while (ithNode != null && parent != null
                    && i > 1 && ithNode.key < parent.key){
                // switch parent and i
                HeapNode mem = parent;
                set(i, parent);
                set(parentIndex(i), ithNode);
                // iterate to the parent
                i = parentIndex(i);
                ithNode = get(i);
                parent = get(parentIndex(i));
            }
        }

        /**
         * deletes the minimum value in the heap and finds a new one
         */
        private void deleteMin(){
            set(1,get(size));
            size--;
            heapifyDown(1);
        }

        /**
         * @param i < size || 0 < i
         */
        private void heapifyDown(int i){
            if (i < 1 || i > size){
                return;
            }
            int l = leftIndex(i);
            int r = rightIndex(i);
            int smallest = i;
            HeapNode left = get(l);
            if (l <= size && left != null && left.key < get(smallest).key){
                smallest = l; // left node is smaller
            }
            HeapNode right = get(r);
            if (r <= size && right != null && right.key < get(smallest).key){
                smallest = r; // right node is smaller
            }
            if (smallest > i){
                // switch smaller node with i
                HeapNode mem = get(i);
                set(i, get(smallest));
                set(smallest, mem);
                // correct from smallest and down;
                heapifyDown(smallest);
            }
        }

        private void set(int i, HeapNode node){
            heap[i-1] = node;
        }
        private HeapNode get(int i){
            if(i < 1 || i > size){
                return null;
            }
            return heap[i-1];
        }

        private HeapNode getMin(){
            return this.heap[0];
        }

        private static int parentIndex(int i){
            return i/2; // int division => floor
        }
        private static int leftIndex(int i){
            return 2*i;
        }
        private static int rightIndex(int i){
            return 2*i+1;
        }
    }
    
   /**
    * public class HeapNode
    * 
    * If you wish to implement classes other than FibonacciHeap
    * (for example HeapNode), do it in this file, not in another file. 
    *  
    */
    public static class HeapNode {

        // fields of HeapNode.
    	public int key;
        public int rank;
        public boolean mark;
        public HeapNode child;
        public HeapNode parent;
        public HeapNode next;
        public HeapNode prev;

       /**
        * Construct a node from data
        * @param key
        * @param rank
        * @param mark
        * @param child
        * @param parent
        * @param next
        * @param prev
        */
    	public HeapNode(int key,
                        int rank,
                        boolean mark,
                        HeapNode child,
                        HeapNode parent,
                        HeapNode next,
                        HeapNode prev) {
            this.key = key;
            this.rank = rank;
            this.mark = mark;
            this.child = child;
            this.parent = parent;
            this.next = next;
            this.prev = prev;
    	}

       /**
        * Constructs a lonely node with key
        * @param key to be stored in the node
        */
        public HeapNode(int key){
            this(key, 0, false, null, null, null, null);
        }

       /**
        * Constructs a lonely node with key = 0
        */
       public HeapNode(){
            this(0);
        }

    	public int getKey() {
            return this.key;
    	}

        public int getRank() {
            return this.rank;
        }

        public boolean isMarked() {
            return this.mark;
        }

        public void mark(){
            this.mark = true;
        }

        public void unMark(){
            this.mark = false;
        }

        public HeapNode getChild() {
            return this.child;
        }

        public HeapNode getParent() {
            return this.parent;
        }

        public HeapNode getNext() {
            return this.next;
        }

        public  HeapNode getPrev() {
            return this.prev;
        }

        @Override
        public String toString(){
            return "(%d)".formatted(this.key);
        }
    }

    /**
     * An iterable HeapNode wrapper class for iteration purposes
     */
    protected static class IterableNode implements Iterable<HeapNode>{

        HeapNode start;

        /**
         * construct from existing node
         * @param node to start iterating from
         */
        public IterableNode(HeapNode node){
            start = node;
        }
        @Override
        public Iterator<HeapNode> iterator(){
            return new HeapNodeIteratorAsLinkedList(start);
        }
    }

    /**
     * Iterates over HeapNodes at the same rank
     * @- goes to the next until it wraps back around
     */
    private static class HeapNodeIteratorAsLinkedList implements Iterator<HeapNode>{

        HeapNode currentNode;
        HeapNode firstNode;
        boolean isFirst = true;
        public HeapNodeIteratorAsLinkedList(HeapNode start){
            this.currentNode = start;
            this.firstNode = start;
        }

        @Override
        public boolean hasNext() {
            return isFirst || (currentNode != null && currentNode.next != firstNode);
        }

        @Override
        public HeapNode next() {
            if (isFirst){
                isFirst = false;
                return currentNode;
            }
            currentNode = currentNode.next;
            return currentNode;
        }
    }
}
