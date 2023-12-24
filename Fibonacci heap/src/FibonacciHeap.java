import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * FibonacciHeap
 * Tomer Yihye
 * username: tomeryihya
 * ID: 203596192
 * <p>
 * Shahar Zachariah 
 * username: shaharz
 * ID: 322710112
 * <p>
 * An implementation of fibonacci heap over integers.
 */
public class FibonacciHeap {
	
	private HeapNode min;
    private int size = 0;
    private int marksCounter = 0; 
    static int totalCuts = 0;  
    static int totalLinks = 0;  

	
   /**
    * public boolean isEmpty()
    * <P>
    * precondition: none
    * <P>
    * The method returns true if and only if the heap
    * is empty.
    *   
    */
    public boolean isEmpty() {
    	return min == null; 
    }
		
   /**
    * public HeapNode insert(int key)
    * <P>
    * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap.
    * Returns the new node created. 
    */
    public HeapNode insert(int key) {
    	HeapNode node = new HeapNode(key);
        // the heap was empty, node become root and the min node in the heap
        if(this.isEmpty()) {
            min = node;
            min.next = min;
            min.prev = min;
        } 
        else {
            min.addToSiblings(node);
            // Check if node key is the new minimum
            if (node.key < min.key)
                min = node;
        }
        this.size++;
        return node;
    }
  
   /**
    * public void deleteMin()
    * <p>
    * Delete the node containing the minimum key.
    */
    public void deleteMin() {
    	// the heap is empty
    	if(min == null)
    		return;
    	
    	size--;
    	// min is the only node in the heap;
        if(min.next == min && min.child == null) {
            min = null;
            return;
        }
        // Add min's children to the roots list
        if(min.child != null) {
        	HeapNode minChild = min.child;
        	for(HeapNode node : SiblingsNodesList(minChild)) {
        		node.parent = null;
        		min.addToSiblings(node);
        		totalLinks++;	
        	}
        }
        // Remove min from the roots list
        min.removeFromSiblings();
        
        // Perform consolidating/successive-linking
        consolidate();
    }
    
   /**
    * public HeapNode findMin()
    * <p>
    * Return the node of the heap whose key is minimal. 
    */
    public HeapNode findMin() {
    	return min;
    } 
    
   /**
    * public void meld (FibonacciHeap heap2)
    * <P>
    * Meld the heap with heap2
    */
    public void meld (FibonacciHeap heap2) {
    	HeapNode min1 = min;
    	HeapNode min2 = heap2.min;
    	int size1 = this.size;
    	int size2 = heap2.size;
    	
    	// Connecting the two roots lists
    	min1.next.prev = min2.prev;
    	min2.prev.next = min1.next;
    	min1.next = min2;
        min2.prev = min1;
        
        // update minimum from both heaps
        if(min2.key<min1.key) {
            min = min2;
        }
        // update heap size 
        this.size = size1 +size2;
        // we conect two lists into one (four nodes - two links)
        
        // reset heap2
        heap2.min = null;
        heap2.size = 0;
        heap2.marksCounter = 0;
    }
    
   /**
    * public int size()
    * <p>
    * Return the number of elements in the heap 
    */
    public int size() {
    	return size;
    }
    
    /**
    * public int[] countersRep()
    * <p>
    * Return a counters array, where the value of the i-th entry is the number of trees of order i in the heap. 
    */
    public int[] countersRep() {
    	int arraySize = findMaxRank() + 1; 
    	int[] ranksArray = new int[arraySize]; 
        
    	for(HeapNode node : rootsList()) {
    		ranksArray[node.rank]++;
        }
        return ranksArray;
    }
	
   /**
    * public void delete(HeapNode x)
    * <P>
    * Deletes the node x from the heap. 
    */
    public void delete(HeapNode x) {    
    	int delta = Math.abs(x.key-min.key)+1;
    	decreaseKey(x,delta);
        deleteMin();
    }
        
   /**
    * public void decreaseKey(HeapNode x, int delta)
    * <p>
    * The function decreases the key of the node x by delta. The structure of the heap should be updated
    * to reflect this chage (for example, the cascading cuts procedure should be applied if needed).
    */ 
    public void decreaseKey(HeapNode x, int delta) {    
    	x.key = x.key - delta;
        
    	// if x is a root only update min pointer (if needed)
        if(x.parent==null) {
            if(min.key > x.key) 
            	min = x;
            return;
        }
      	// x isn't a root
        if(x.key < x.parent.key)
            cascadingCut(x);    
    }
    
   /**
    * public int potential() 
    * <p>
    * This function returns the current potential of the heap, which is:
    * Potential = #trees + 2*#marked
    * The potential equals to the number of trees in the heap plus twice the number of marked nodes in the heap. 
    */
    public int potential() {    
    	int potential = numOfTrees() + 2*marksCounter;
    	return potential;
    }

   /**
    * public static int totalLinks() 
    * <P>
    * This static function returns the total number of link operations made during the run-time of the program.
    * A link operation is the operation which gets as input two trees of the same rank, and generates a tree of 
    * rank bigger by one, by hanging the tree which has larger value in its root on the tree which has smaller value 
    * in its root.
    */
    public static int totalLinks() {    
    	return totalLinks;
    }

   /**
    * public static int totalCuts() 
    * <p>
    * This static function returns the total number of cut operations made during the run-time of the program.
    * A cut operation is the operation which diconnects a subtree from its parent (during decreaseKey/delete methods). 
    */
    public static int totalCuts() {    
    	return totalCuts;
    }
 
   /**
    * public static int[] kMin(FibonacciHeap H, int k) 
    * <p>
    * This static function returns the k minimal elements in a binomial tree H.
    * The function should run in O(k*deg(H)). 
    * You are not allowed to change H.
    */
    public static int[] kMin(FibonacciHeap H, int k) {        	
    	//check if the heap is empty
    	if(H.isEmpty()) {
    		int[] arr = new int[0]; 
    		return arr;
    	}
    	//if the heap is not empty create the wonted array
    	int[] arr = new int[k];
    	//we create a map with the wanted minimum nodes and keys so we can still
    	//call the nodes of the original heap and arrange min keys
    	Map<Integer, HeapNode>  myMap = new HashMap<Integer, HeapNode>();
    	//we will create a new heap, in this heap we can do actions like insert and delete min
    	//and still keep the original heap unchanged
    	FibonacciHeap myHeap = new FibonacciHeap();
    	HeapNode myNode = H.min;
    	myMap.put(myNode.key , myNode);
    	myHeap.insert(myNode.key);
    	
    	for (int i = 0 ; i<k ; i++) {
    		arr[i] = myHeap.min.key;
    		myNode = myMap.get(myHeap.min.key);
    		myHeap.deleteMin();
    		HeapNode nodeChild = myNode.child;
    		// the next min node is one of the min node childs
    		//so we will find the next one:
    		if (myNode.child != null) {
    			//if it is null continue to next iteration    			
    			//first go to the next child and then check the while condition
    			//that way we can go over all children until we return to first child   			
    			do {
    				myMap.put(nodeChild.getKey(), nodeChild);
    				myHeap.insert(nodeChild.getKey());
    				nodeChild = nodeChild.next;
    			}    		
    			// if there are children, go over all children and add them
    			while (nodeChild != myNode.child || k==0);  
    		}
		}
        return arr;
    }
    
  	//helping methods (privates methods):
    /**
     * Perform a cut between node and his parent
     * if the node is a root it will do nothing 
     * <P>
     * @param node Node become a root after the cutting
     */
    private void cut(HeapNode node) {
    	// node is a root no cut is necessary 
    	if(node.parent==null)
    		return;
    	// node isn't a root
    	else {	
    		FibonacciHeap.totalCuts++;
    		if(node.marked) {
    			node.marked = false;
    			marksCounter--;
    		}
    		node.removeFromSiblings();
    		min.addToSiblings(node);
    		node.parent = null;
    		if(node.key < min.key) 
    			min = node;
    		}
    }  
    /**
     * return list with all the Siblings at the same level
     */
    private List<HeapNode> SiblingsNodesList(HeapNode firstNode) {
        if(firstNode == null) 
        	return new LinkedList<>();

        List<HeapNode> list = new LinkedList<>();
        HeapNode node = firstNode;
        list.add(node);
        for(node = firstNode.next; node != firstNode ; node = node.next) {
            list.add(node);
        }
        return list;
    }
    /**
     * return list with all the heap roots
     */
    private List<HeapNode> rootsList() {
        return SiblingsNodesList(min.next);
    }
    /**
     * @return The number of roots in the heap
     */
    private int numOfTrees() { 
    	if(min==null)
    		return 0;
    	return rootsList().size();    
    } 
    /**
     * Link 2 roots with the same rank into one with higher rank.
     * @param node1 - the first node (node1 is a root)
     * @param node2 - the second node (node2 is a root)
     * @return one heap while the minimum key is the root
     */
    private HeapNode link(HeapNode node1, HeapNode node2) {
    	HeapNode parent = node1; 
        HeapNode child = node2;
        // replace the nodes setting if necessary 
        if(node1.getKey() > node2.getKey()) {
            parent = node2;
            child = node1;
        } 
        parent.setChild(child);
        totalLinks++;
        return parent;
    }
    /**
     * while the node parent is marked the method will cut the node and add it to the roots
     * if the parent isn't marked it will mark it and return   
     * @param node Node to start the cuts
     */
    private void cascadingCut(HeapNode node) {
        HeapNode parent = node.parent;
        cut(node);
        if(parent!=null) {
        	if(parent.parent != null) {
        		if(!parent.marked) {
        			parent.marked = true;
        			marksCounter++;
        		} 
        		else 
        			cascadingCut(parent);
        	}
       }
    }
    /**
     * @return The max rank of the tree with highest rank (most sons)
     */
    private int findMaxRank() {
        int maxRank = 0 ;
        for(HeapNode node : rootsList()) {
            if(node.rank > maxRank)
            	maxRank = node.rank;
        }
        return maxRank;
    }
   /**
     * create array with different tree ranks by used consolidation/successive-linking (used after delete min).
     */
    private void consolidate() {
        int rank = 0;
    	HeapNode[] ranksArray = new HeapNode[1+size];
        for(HeapNode node : rootsList()) {
            rank = node.rank;	
            // we do not have the same rank in the array
            if(ranksArray[rank] == null) 
            	ranksArray[rank] = node;
            
            // we already have this rank in the array
            else {
                while(ranksArray[rank] != null) {
                    // linking the two nodes with the same rank
                    node = link(node,ranksArray[rank]);
                    rank = node.rank;
                    // clear the cell of rank k-1 tree after creating a k rank tree 
                   	ranksArray[rank-1] = null;
                }
                // insert the final tree to his place in the array
                ranksArray[node.rank] = node;
            }
        }
        rootsArrayToRootsList(ranksArray);
    }
    /**
     * turn ranks Array of nodes from consolidate to roots list 
     * @param ranks Array of nodes with different ranks, from consolidate
     */
    private void rootsArrayToRootsList(HeapNode[] array) {
    	HeapNode first = null;
    	HeapNode last = null;
    	
        for(HeapNode node : array) {
    		if(node == null) 
            	continue;
            // The condition will only be met once when for the first time node != null 
    		// update the pointer first - who stay still
    		// update the pointer last - who will keep update from now
    		if(first == null && last == null) {
            	first = node; 
            	last = node;
            	min = first;
            	continue;
            }
    		// update min pointer if necessary
    		if(node.key < min.key)
            	min = node;
    		
            // update pointers
            node.prev = last;
            last.next = node;
            last = node;
        }
        // update first and last pointers
    	last.next = first;
        first.prev = last;
    }
  
  
  /**
   * public class HeapNode
   * <p>
   * If you wish to implement classes other than FibonacciHeap
   * (for example HeapNode), do it in this file, not in 
   * another file   
   */
    public class HeapNode {

	public int key;
	private int rank;
    private boolean marked;
    private HeapNode child; 
    private HeapNode next;
    private HeapNode prev;
    private HeapNode parent;
	
  	public HeapNode(int key) {
	    this.key = key;
	    this.rank = 0;
        this.marked = false;
      }

  	public int getKey() {
	    return this.key;
    }

  	//helping methods (privates methods):
  	/**
     * set node as child of this.node
     * <P>
     * @param node Node to be added as a child
     */
    public void setChild(HeapNode node) { 
    	node.parent = this;
         // parent (this) don't have any other children but node
         if(this.child == null) {
             child = node;
             node.next = node;
             node.prev = node;
             rank++;
         }
         // node parent has a child before 
         else  
         	child.addToSiblings(node);
     }
  	/**
    * Add a sibling to the node.
    * Put it next to this and previous to this.next
    * <p>
    * @param node Node to add as a sibling
    */
    public void addToSiblings(HeapNode node) {
    	node.next = next;
        next.prev = node;
        if(this.parent != null) {
        	this.parent.rank++;
        }
        node.prev = this;
        next = node;
    }
   /**
    * Remove this.node from siblings.
    * Update Siblings and parent pointers.
    * if node is a root it will delete it
    */  
    public void removeFromSiblings() {
    	// this node is the only son
    	if(next == this) {
    		this.parent.child = null;
    		this.parent.rank--;
    	}
    	
    	// this node isn't the only son
    	else {  
    	// if node was the left child, update parent child to node.next 
    		if(this.parent != null) {
    			if(this.parent.child == this) 
    				this.parent.child = this.next;
    			this.parent.rank--;
    		}
    		this.prev.next = this.next;
       		this.next.prev = this.prev;
    	}
    }   
    
  	// methods for the tester in HeapNode
    public HeapNode getNext() { return this.next; }
    public void setNext(HeapNode node) { this.next = node; }
    public HeapNode getPrev() { return this.prev; }
    public void setPrev(HeapNode node) { this.prev = node; }
    public HeapNode getParent() { return this.parent; }
    public void setParent(HeapNode node) { this.parent = node; }
    public HeapNode getChild() { return this.child; }
    public int getRank() { return this.rank; }
    public void setRank(int rank) { this.rank = rank; }
    }

    // methods for the tester in FibonacciHeap
    public int getNumOfTrees() { return this.numOfTrees(); }
    public int getNumOfMarks() { return marksCounter; }
    public HeapNode getFirst() { return min; }
}   


