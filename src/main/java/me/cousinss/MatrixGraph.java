package main.java.me.cousinss;

import java.util.ArrayList;
import java.util.Arrays;

public class MatrixGraph {
    private int[][] mat;
    private int[] degree;
    private int order;
    private int size;

    private static final double LOG_TWO = 0.693147181;
    private static final double ZERO_POSITIVE_ERROR = 0.00001;
    /**
     * A positive value denoting the sensitivity of the automatic initial capacity
     * to bump up one level (power of two) based on how close the order is to a power of two.
     */
    private static final double ROUNDUP_BUFFER = 0.334;
    private static final double DEFAULT_CAPACITY = 16;

    public MatrixGraph(int order) {
        this(order, (int)(Math.max(DEFAULT_CAPACITY, Math.pow(2, Math.ceil(ROUNDUP_BUFFER + Math.log(order)/LOG_TWO)))));
    }

    public MatrixGraph(int order, int initialCapacity) {
        this.order = order;
        this.size = 0;
        if(initialCapacity < order) { //strange decision
            initialCapacity = order;
        }
        this.mat = new int[initialCapacity][initialCapacity];
        this.degree = new int[initialCapacity];
    }

    public MatrixGraph(int[][] matrix) {
        this.order = matrix.length;
        this.mat = matrix;
        this.degree = new int[order];
        int ds = 0;
        for(int i = 0; i < order; i++) {
            int sum = 0;
            for(int j = 0; j < order; j++) {
                sum+=mat[i][j];
            }
            degree[i]=sum;
            ds+=sum;
        }
        this.size = ds/2; //degree-sum theorem - size is half the degree sum (and degree sum will never be odd)
    }

    /**
     * Add a single vertex to the graph.
     * @return {@code true} if the vertex was added successfully, {@code false} otherwise.
     */
    public boolean addVertex() {
        return this.addVertex(1);
    }

    /**
     * Add a vertex or vertices to the graph.
     * @param numVertices the number of vertices to add.
     * @return {@code true} if the vertices were added successfully, {@code false} otherwise.
     */
    public boolean addVertex(int numVertices) {
        if(order + numVertices >= mat.length) {
            resizeMatrix(mat, order + numVertices);
            int[] degTemp = new int[order + numVertices];
            System.arraycopy(degree, 0, degTemp, 0, order + numVertices);
            degree = degTemp;
        }
        order += numVertices;
        return true;
    }

    public boolean putEdge(int v1, int v2) {
        if(v1 < 0 || v1 >= order || v2 < 0 || v2 >= order) {
            return false;
        }
        if(mat[v1][v2]>0) {
            return false; //edge already present
        }
        mat[v1][v2] = 1;
        mat[v2][v1] = 1;
        degree[v1]++;
        degree[v2]++;
        size++;
        return true;
    }

    public boolean removeEdge(int v1, int v2) {
        if(v1 < 0 || v1 >= order || v2 < 0 || v2 >= order) {
            return false;
        }
        if(mat[v1][v2]>0) {
            mat[v1][v2] = 0;
            mat[v2][v1] = 0;
            degree[v1]--;
            degree[v2]--;
            size--;
            return true;
        }
        return false;
    }

    public int[][] getLaplacian() {
        int[][] out = new int[order][order];
        for(int i = 0; i < order; i++) {
            for(int j = 0; j < order; j++) {
                if(i==j) {
                    out[i][j] = degree[i];
                } else {
                    out[i][j] = mat[i][j];
                }
            }
        }
        return out;
    }

    public int getDegree(int v) {
        if(v < 0 || v >= this.order) {
            return -1;
        }
        return this.degree[v];
    }

    /**
     * Whether the graph is Eulerian.
     * @return {@code true} if the graph is Eulerian, {@code false} otherwise.
     */
    public int[] isEulerian() {
        ArrayList<Integer> out = new ArrayList<>();
        for(int i = 0; i < order; i++) {
            if(degree[i] % 2 != 0) {
//                System.out.println(i + " has noneven degree " + degree[i]);
                out.add(i);
            }
        }
        if(!isConnected(true) || out.size() == 0) {
            return null;
        }
        int[] outA = new int[out.size()];
        for(int i = 0; i < out.size(); i++) {
            outA[i] = out.get(i);
        }
        return outA;
    }

    public boolean hasEulerianPath() {
        int numOdd = 0;
        for(int i = 0; i < order; i++) {
            if(degree[i] % 2 != 0) {
                if (numOdd < 2) {
                    numOdd++;
                } else {
                    return false;
                }
            }
        }
        return numOdd == 0 || numOdd == 2;
    }

    public boolean isConnected() {
        return this.isConnected(false);
    }

    public boolean isConnected(boolean allowDots) {
        if(order == 0) {
            return true;
        }
        int seed = -1;
        if(allowDots) {
            for (int i = 0; i < order; i++) {
                if (degree[i] > 0) {
                    seed = i;
                    break;
                }
            }
        } else {
            seed = 0;
        }
        boolean[] visited = new boolean[order];
        dfsUtil(seed, visited);
        for(int i = 0; i < order; i++) {
            if(visited[i] == false) {
                if(!(degree[i] == 0 && allowDots)) {
//                    System.out.println("Vertex " + i + " was disconnected from " + seed + " and was irredeemable as a dot.");
                    return false;
                }
            }
        }
        return true;
    }


    //should make async for application
    //generalize?
    //this shouldn't be that hard to generalize tbh - make saveRemoved a 2d array and run through every combination of x vertices (for some x) and see if components>=x+1
    //would have to return an int[] with length x
    /**
     * If the graph is 1-tough (it fulfills the G \ S condition).
     * @return {@code 1} if the graph is tough, or if not the negative index of some cut vertex.
     */
    public int[] isTough() {
        for(int i = 1; i < order/2; i++) {
            int[] out = isTough(i);
            if(out != null) {
                return out;
            }
        }
        return null;
    }

    public int[] isTough(int num) {
        if(num < 1) {
            return null;
        }

        //remove all num-tuples of vertices
        int[] removing = new int[num];
        return recurseTough(removing, num, 0);
    }

    private int[] recurseTough(int[] removing, int num, int l) {
//        System.out.println("Recursing tough for " + Arrays.toString(removing) + " (ignore at and after loc #"+l+")");
        if(l == num) {
//            System.out.println("Testing tough for " + Arrays.toString(removing));
            int[][] saveRemoved = new int[num][];
            for(int i = 0; i < num; i++) {
                saveRemoved[i] = new int[degree[removing[i]]];
            }
//            int[][] save = matrixCopy(mat); //TODO remove
//            int[] saveD = Arrays.copyOf(degree, degree.length);
            for (int id = 0; id < num; id++) {
                int n = 0;
                for (int x = 0; x < order; x++) {
                    if (mat[removing[id]][x] > 0) {
                        saveRemoved[id][n++] = x;
                        mat[removing[id]][x] = 0;
                        mat[x][removing[id]] = 0; //break
                        degree[removing[id]]--;
                        degree[x]--;
                    }
                }
                for(; n < saveRemoved[id].length; n++) {
                    saveRemoved[id][n] = -1;
                }
            }
            boolean notTough = numComponents()-num > num;
//            System.out.println("Was tough when removing: " + !notTough + " (" + (numComponents()-num) + "?>" + num+")");
//            System.out.println(Arrays.toString(new EigenvalueDecomposition(this.mat, this.degree, this.order).getRealEigenvalues()));
//            System.out.println(this.toString());
            //repair them
            for (int id = 0; id < num; id++) {
                for (int x = 0; x < saveRemoved[id].length; x++) {
                    if(saveRemoved[id][x] == -1) {
                        break;
                    }
                    putEdge(removing[id], saveRemoved[id][x]);
                }
            }
//            if(!equal(save, mat)) {
//                System.out.println(arrayDeepString(save));
//                System.out.println(arrayDeepString(mat));
//                System.out.println("Was removing: " + Arrays.toString(removing));
//                System.out.println(Arrays.deepToString(saveRemoved));
//            }
//            if(!equal(saveD, degree)) {
//                System.out.println(Arrays.toString(saveD));
//                System.out.println(Arrays.toString(degree));
//            }
            return notTough ? removing : null;
        } else {
            for(int i = (l == 0 ? 0 : removing[l-1]+1); i < order; i++) {
                removing[l] = i;
                int[] next = recurseTough(removing, num,l+1);
                if(next != null) {
                    return next;
                }
            }
            return null;
        }
    }

    private boolean equal(int[][] m1, int[][] m2) {
        if(m1.length != m2.length) {
            return false;
        }
        for(int i = 0; i < m1.length; i++) {
            if(m1[i].length != m2[i].length) {
                return false;
            }
            for(int j = 0; j < m1[i].length; j++) {
                if(m1[i][j] != m2[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean equal(int[] m1, int[] m2) {
        if(m1.length != m2.length) {
            return false;
        }
        for(int i = 0; i < m1.length; i++) {
            if(m1[i] != m2[i]) {
                return false;
            }
        }
        return true;
    }

    private int[][] matrixCopy(int[][] m) {
        int[][] out = new int[m.length][m[0].length];
        for(int i =0; i < m.length; i++) {
            for(int j = 0; j < m[0].length; j++) {
                out[i][j] = m[i][j];
            }
        }
        return out;
    }

    public int numComponents() {
        //the number of components of a graph given an n-by-n adjacency matrix is equal to the multiplicity of the eigenvalue 0
        /*
        https://math.stackexchange.com/questions/324427/how-to-find-the-multiplicity-of-eigenvalues
        https://textbooks.math.gatech.edu/ila/eigenvectors.html
        fuck
         */
        double[] eigen = new EigenvalueDecomposition(this.mat, this.degree, this.order).getRealEigenvalues();
//        System.out.println(Arrays.toString(eigen));
        if(eigen.length == 0) {
            return 0;
        }
        //count zeroes (multiplicity)
        for(int i = 0; i < eigen.length; i++) {
            if(Math.abs(eigen[i]) > ZERO_POSITIVE_ERROR) {
                return i;
            }
        }
        return eigen.length; //error? //probably not because i think this would happen with the empty graph
    }

    void dfsUtil(int start, boolean[] visited) {
        visited[start] = true;
        int found = 0;
        for (int i = 0; i < order; i++) {
            if(found == degree[start]) {
                return;
            }
            if (mat[start][i] == 1 && (!visited[i])) {
                dfsUtil(i, visited);
                found++;
            }
        }
    }

    /*
     * https://stackoverflow.com/a/27728645/19152535 by Paul Boddington (Stack Overflow)
     */
    private static int[][] resizeMatrix(int[][] matrix, int s) {
        int[][] temp = new int[s][s];
        s = Math.min(s, matrix.length);
        for (int i = 0; i < s; i++)
            System.arraycopy(matrix[i], 0, temp[i], 0, s);
        return temp;
    }

    protected int[][] getMatrix() {
        return mat;
    }

    protected int[] getDegree() {
        return degree;
    }

    public int getOrder() {
        return order;
    }

    public int getSize() {
        return size;
    }

    @Override
    public String toString() {
        return toString(false);
    }

    public String toString(boolean list) {
        if(!list) {
            return arrayDeepString(mat);
        }
        String out = "";
        ArrayList<Integer>[] lists = new ArrayList[this.order];
        for(int i = 0; i < this.order; i++) {
            lists[i] = new ArrayList<Integer>();
            for(int j = 0; j < this.order; j++) {
                if(mat[i][j]!=0) {
                    lists[i].add(j);
                }
            }
            out += "(" + i + ") " + lists[i].toString() + (i == this.order - 1 ? "" : ",\n");
        }
        return out;
    }

    private String arrayDeepString(int[][] mat) {
        String out = "";
        for(int i = 0; i < this.order; i++) {
            out+=Arrays.toString(mat[i]) + (i == this.order - 1 ? "" : ",\n");
        }
        return out;
    }
}
