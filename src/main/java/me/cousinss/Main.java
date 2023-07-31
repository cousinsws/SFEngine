package me.cousinss;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        MatrixGraph m = new MatrixGraph(InputUtil.fromTriangular(
                "111100",
                "11010",
                "1001",
                "111",
                "00",
                "0"
        ));
//        m.putEdge(0, 1);
//        m.putEdge(1, 2);
//        m.putEdge(2, 3);
//        m.putEdge(3, 0);
//        m.putEdge(3, 1);
//        m.putEdge(0, 2);
        System.out.println(m.toString());
        System.out.println("The graph is Eulerian: " + m.isEulerian(false));
        System.out.println("The graph has an Eulerian Path: " + m.isEulerian(true));
        System.out.println("The graph is connected: " + m.isConnected());
        int components = m.numComponents();
        System.out.println("The graph has: " + components + " components");
        long tS = System.nanoTime();
        System.out.println(Arrays.toString(m.isTough()));
        System.out.println(System.nanoTime()-tS);
    }
}