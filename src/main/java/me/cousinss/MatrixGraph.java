package me.cousinss;

import java.util.Arrays;

public class MatrixGraph {
    private int[][] mat;
    private int[] degree;
    private int order;
    private int size;

    private static final double LOG_TWO = 0.693147181;
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

    /**
     * Whether the graph is Eulerian.
     * @param path if an Eulerian path, rather than strictly an Eulerian cycle, is permitted.
     * @return {@code true} if the graph is Eulerian to the given specification, {@code false} otherwise.
     */
    public boolean isEulerian(boolean path) {
        int numOdd = 0;
        for(int i = 0; i < order; i++) {
            if(degree[i] % 2 != 0) {
                if (path && numOdd < 2) {
                    numOdd++;
                } else {
                    return false;
                }
            }
        }
        return (path ? numOdd == 0 || numOdd == 2 : true) && isConnected(true);
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
                    System.out.println("Vertex " + i + " was disconnected from " + seed + " and was irredeemable as a dot.");
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
     * If the graph is tough.
     * @return {@code 1} if the graph is tough, or if not the negative index of some cut vertex.
     */
    public int isOneTough() {
        int[] saveRemoved;
        int deg;
        for(int i = 0; i < order; i++) {
            deg = degree[i];
            saveRemoved = new int[deg]; //literally just adjacency list of vertex i (lol)
            int n = 0;
            for(int j = 0; j < order; j++) {
                if(mat[i][j] > 0) {
                    saveRemoved[n++] = j;
                    mat[i][j] = 0;
                    mat[j][i] = 0; //break
                    degree[i]--;
                    degree[j]--;
                    if(n == deg) {
                        break;
                    }
                }
            }
            int components = numComponents();
            for(int j = 0; j < deg; j++) {
                putEdge(i, saveRemoved[j]); //repair
            }
            if(components >= 2) {
                System.out.println("Removing vertex " + i + " disconnected the graph.");
                return -i;
            }
        }
        return 1;
    }

    public int numComponents() {
        return 1; //TODO;
        //the number of components of a graph given an n-by-n adjacency matrix is equal to the multiplicity of the eigenvalue 0
        /*
        https://math.stackexchange.com/questions/324427/how-to-find-the-multiplicity-of-eigenvalues
        https://textbooks.math.gatech.edu/ila/eigenvectors.html
        fuck
         */
    }

    private void dfsUtil(int start, boolean[] visited)
    {
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
        return Arrays.deepToString(mat);
    }
}
