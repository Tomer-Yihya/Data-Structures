public class FiboMeasurements {

    public static double log2(int n) {
        return Math.log(n) / Math.log(2);
    }

    public static double geomSum(double q, int maxI) {
        double sum = 0;
        for(int k = 1; k <= maxI; k++) {
            sum += Math.pow(q, k);
        }
        return sum;
    }

    public static void firstMeasurement() throws Exception {
        System.out.println("######################## Measurement 1 ########################");
        FibonacciHeap.HeapNode node;
        int nodeIndex;
        for(int pow = 10; pow <= 12; pow++) {
        	int m = (int) Math.pow(2,pow);
            System.out.println("-----------------");
            System.out.println("run for m = " + m);
            FibonacciHeap.totalLinks = 0;
            FibonacciHeap.totalCuts = 0;
            FibonacciHeap.HeapNode[] nodes = new FibonacciHeap.HeapNode[m + 1];
            FibonacciHeap heap = new FibonacciHeap();
            long start = System.nanoTime();
            
            // insert(m), insert(m-1),..., insert(0)) 
            for(int j = m ; j >= 0; j--) {
                node = heap.insert(j);   
                nodes[j] = node;
            }
            // delete min()
            heap.deleteMin();
            // decrease-key (m*0.5^k)   [0=< k=< log(m-1)] 
            int delta = m+1;
            for (int k = 1; k <= log2(m)-1; k++) {
                nodeIndex = (int) Math.floor(m * geomSum(0.5,k) + 2);
                heap.decreaseKey(nodes[nodeIndex],delta);
            } 
            // decreaseKey(m-1,delta) <= delta = m+1 
            heap.decreaseKey(nodes[m-1],delta);
            long end = System.nanoTime();
            System.out.println("time ms: " + ((end - start)) / 1000000.0);
            System.out.println("totalLinks = " + FibonacciHeap.totalLinks());
            System.out.println("totalCuts = " + FibonacciHeap.totalCuts());
            System.out.println("potential = " + heap.potential());
            //System.out.println("potential=> " + heap.getNumOfTrees()+"+"+heap.getNumOfMarks()+"*2="+heap.potential());
            Thread.sleep(1000);
        }
    }

	public static void secondMeasurement() {
        System.out.println("######################## Measurement 2 ########################");
        FibonacciHeap.HeapNode node;
        for (int m = 1000; m <= 3000; m += 1000) {
            System.out.println("-----------------");
            System.out.println("run for m = " + m);
            FibonacciHeap.totalLinks = 0;
            FibonacciHeap.totalCuts = 0;
            FibonacciHeap.HeapNode[] nodes = new FibonacciHeap.HeapNode[m];
            FibonacciHeap heap = new FibonacciHeap();
            long start = System.nanoTime();
            
            // insert(m), insert(m-1),..., insert(1))
            int k = 0;
            for(int j = m ; j > 0; j--) {
                node = heap.insert(j);
                nodes[k] = node;
                k++;
            }
            // deleteMin(),deleteMin(),....,deleteMin() 
            // m/2 times in total
            for(int j = 0; j < m/2; j++) {
                heap.deleteMin();
            }
            long end = System.nanoTime();
            System.out.println("time ms: " + ((end - start) / 1000000.0));
            System.out.println("totalLinks = " + FibonacciHeap.totalLinks());
            System.out.println("totalCuts = " + FibonacciHeap.totalCuts());
            System.out.println("potential = " + heap.potential());
        }
    }

    public static void main(String[] args) throws Exception {
        firstMeasurement();
        secondMeasurement();
    }
}
