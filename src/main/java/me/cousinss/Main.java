package me.cousinss;

public class Main {
    public static void main(String[] args) {
        MatrixGraph m = new MatrixGraph(4);
        m.putEdge(0, 1);
        m.putEdge(1, 2);
        m.putEdge(2, 3);
//        m.putEdge(3, 0);
//        m.putEdge(3, 1);
        System.out.println(m.toString());
        System.out.println("The graph is Eulerian: " + m.isEulerian(false));
        System.out.println("The graph has an Eulerian Path: " + m.isEulerian(true));
        System.out.println("The graph is connected: " + m.isConnected());
        System.out.println("The graph has a cut vertex: " + (m.isOneTough() < 1));
    }
}