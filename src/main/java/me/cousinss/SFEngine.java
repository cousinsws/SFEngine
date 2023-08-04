package main.java.me.cousinss;

import Jama.Matrix;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.*;

public class SFEngine extends Application  {

    private static final double FULL_TURN = 2 * Math.PI;
    private static final Color[] COLORS = new Color[] {
        Color.RED,
        Color.BLUE,
        Color.GREEN,
        Color.YELLOW,
        Color.AQUA,
        Color.PURPLE,
        Color.PINK,
        Color.ORANGE,
        Color.CRIMSON
    };

    private static final int EDGE_Z = 10;
    private static final int V_Z = 1;

    public static void main(String[] args) {
        System.out.println("Hello world");
        launch(args);
    }

    private BorderPane root;
    private List<Vertex> vertices;
    private Set<Edge> edges;
    private Text hud;
    private Line newEdge;
    private int newEdgeV;

    private MatrixGraph m;

    @Override
    public void start(Stage stage) {
        stage.setTitle("Hello World GUI Test");
        root = new BorderPane();
        hud = new Text("Components: 1\nTough: False");
        root.setTop(hud);
        stage.setScene(new Scene(root, 600, 600));
        stage.show();
        newEdge = null;
        root.setOnMouseClicked(e -> {
            if(isEdgeCreation(e) || newEdge != null) {
                checkFinishEdge(e);
            }
        });
//        root.setOnMouseReleased(e -> {
//            if(newEdge != null) {
//                checkFinishEdge(e);
//            }
//        });
        root.setOnMouseMoved(e -> {
           if(newEdge != null) {
               newEdge.setEndX(e.getX());
               newEdge.setEndY(e.getY());
           }
        });
        m = new MatrixGraph(10);
        vertices = new ArrayList<>();
        edges = new LinkedHashSet<>();
        for(int i = 0; i < m.getOrder(); i++) {
            vertices.add(new Vertex(0, 0, ""+i, i));
        }
        arrangeRadially(vertices, new Point2D(300, 300), 100);
        for(int i = 0; i < m.getOrder(); i++) {
            int rand = i;
            while((rand = ((int) (Math.random() * m.getOrder()))) == i) {}
            edges.add(new Edge(i, rand));
//            edges.add(new Edge(i, (int)(Math.random()*10)));
        }
        for(Edge e : edges) {
            root.getChildren().add(e);
            e.setViewOrder(EDGE_Z);
            m.putEdge(e.v1, e.v2);
        }
//        System.out.println(m.toString(true));
        for(Vertex v : vertices) {
            root.getChildren().add(v);
            v.setViewOrder(V_Z);
        }
        analyzeGraph();
    }

    private void analyzeGraph() {
        int numComponents = m.numComponents();
        int[] save = m.getDegree();
        int[] tough = m.isTough();
//        System.out.println(Arrays.toString(save) + " -> " + Arrays.toString(m.getDegree()));
//        int[] tough = null;
        int[] eulerian = m.isEulerian();
        hud.setText("Components: " + numComponents
                +"\nTough: " +
                (numComponents == 1 ? (tough == null ? "True" :
                        ("False, Breaking Set: " + Arrays.toString(tough))) : "False (Disconnected)")
                +"\nEulerian: " + (eulerian == null ? "True" : ((numComponents == 1 ? Arrays.toString(eulerian) : "False (Disconnected)")))
        );
        colorGroupVertices(m, numComponents);
        for(Vertex v : vertices) {
            v.setStroke(Color.TRANSPARENT);
        }
        if(tough != null && numComponents == 1) {
            for(int i = 0; i < tough.length; i++) {
                vertices.get(tough[i]).setStroke(Color.BLACK);
            }
        }
    }

    //this sucks. idrc
    private int[] nonEulerianSet() {
        int[] out = new int[m.getOrder()];
        int num = 0;
        for(int i = 0; i < m.getOrder(); i++) {
            if(m.getDegree(i)%2 != 0) {
                out[num++] = i;
            }
        }
        int[] fin = new int[num];
        System.arraycopy(out, 0, fin, 0, num);
        return fin;
    }

    private void checkFinishEdge(MouseEvent e) {
        if(newEdge == null) {
            for (int i = 0; i < vertices.size(); i++) {
                Vertex v = vertices.get(i);
                if (v.contains(e.getX(), e.getY())) {
                    newEdge = new Line(v.getCenterX(), v.getCenterY(), e.getX(), e.getY());
                    newEdge.setViewOrder(EDGE_Z);
                    root.getChildren().add(newEdge);
                    newEdgeV = i;
                    break;
                }
            }
        } else {
            for(int i = 0; i < vertices.size(); i++) {
                Vertex v = vertices.get(i);
                if(v.contains(newEdge.getEndX(), newEdge.getEndY())) {
                    if(newEdgeV != i) {
                        Edge ed = new Edge(newEdgeV, i);
                        if(edges.add(ed)) {
                            root.getChildren().add(ed);
                            ed.setViewOrder(EDGE_Z);
                            m.putEdge(newEdgeV, i);
//                            System.out.println("Created edge between " + i +" and " + newEdgeV);
//                            System.out.println(m.toString(true));
                            analyzeGraph();
                        }
                    }
                    root.getChildren().remove(newEdge);
                    newEdge = null;
                    newEdgeV = -1;

                    return;
                }
            }
        }
    }

    private boolean isEdgeCreation(MouseEvent e) {
        return e.isShiftDown() == true || e.getButton().equals(MouseButton.SECONDARY);
    }

    private void colorGroupVertices(MatrixGraph m, int numComponents) {
        boolean[] colored = new boolean[m.getOrder()];
        for(int i = 0; i < m.getOrder(); i++) {
            colored[i] = false;
        }
        int color = 0;
        for(int i = 0; i < m.getOrder(); i++) {
            if(colored[i]) {
                continue;
            }
            boolean[] visited = new boolean[m.getOrder()];
            m.dfsUtil(i, visited);
            for(int j = 0; j < visited.length; j++) {
                if(visited[j]) {
                    colored[j]=true;
                    vertices.get(j).setColor(Color.hsb((360/((double)numComponents))*(color), 1, 1));
                }
            }
            color++;
        }
    }

    private class Vertex extends Group {
        public static final double RADIUS = 10;
        private Circle c;
        private Text t;
        private int id;
        public Vertex(double x, double y, String text, int id) {
            super(new Circle(RADIUS, Color.RED), new Text(text));
            this.id = id;
            this.c = (Circle) this.getChildren().get(0);
            c.setStrokeWidth(3);
            c.setStroke(Color.TRANSPARENT);
            this.t = (Text) this.getChildren().get(1);
            this.setCenterX(x);
            this.setCenterY(y);
            this.setOnMouseDragged(e -> {
                if(newEdge != null) {
                    return;
                }
                this.setCenterX(e.getX());
                this.setCenterY(e.getY());
                for(Edge d : edges) {
                    if(d.v1 == id) {
                        d.setStartX(e.getX());
                        d.setStartY(e.getY());
                    } else if(d.v2 == id) {
                        d.setEndX(e.getX());
                        d.setEndY(e.getY());
                    }
                }
            });
            this.setOnMouseClicked(e -> {
                if(e.isControlDown()) {
                    ((Pane) this.getParent()).getChildren().remove(this);
                    for(Edge d : edges) {
                        if(d.v1 == this.id || d.v2 == this.id) {
                            d.remove();
                            System.out.println("Nonfunctional, undefined behavior ahead");
                        }
                    }
                    //todo: matrix.remove(this), need to add a Map<int vertexName - > vertexId (in array) so removing vertices doesnt fuck up shit on gui
                }
            });
        }

        public double getCenterX() {
            return this.c.getCenterX();
        }
        public double getCenterY() {
            return this.c.getCenterY();
        }
        public void setCenterX(double x) {
            c.setCenterX(x);
            t.setX(x);
        }
        public void setCenterY(double y) {
            c.setCenterY(y);
            t.setY(y);
        }
        public void setColor(Color color) {
            this.c.setFill(color);
        }
        public void setStroke(Color color) {
            this.c.setStroke(color);
        }
    }

    private class Edge extends Line {
        private int v1;
        private int v2;

        public Edge(int v1, int v2) {
            super(vertices.get(v1).getCenterX(), vertices.get(v1).getCenterY(), vertices.get(v2).getCenterX(), vertices.get(v2).getCenterY());
            this.setFill(Color.BLACK);
            this.setStrokeWidth(3);
            this.setStroke(Color.BLUE);
            this.v1 = v1;
            this.v2 = v2;
            this.setOnMouseClicked(e -> {
                if(e.isControlDown()) {
                    this.remove();
                }
            });
        }

        public void remove() {
            ((Pane) this.getParent()).getChildren().remove(this);
            m.removeEdge(this.v1, this.v2);
            analyzeGraph();
        }

        @Override
        public boolean equals(Object o) {
            if(! (o instanceof Edge)) {
                return false;
            }
            Edge e = (Edge) o;
            return ((e.v1 == this.v1) && (e.v2 == this.v2)) || ((e.v1 == this.v2) && (e.v2 == this.v1));
        }
    }

    private void arrangeRadially(List<Vertex> vertices, Point2D centre, double radius) {
        double turn = FULL_TURN / (double)vertices.size();
        double rad = 0;
        for(Vertex v : vertices) {
            v.setCenterX(centre.getX() + Math.cos(rad) * radius);
            v.setCenterY(centre.getY() + Math.sin(rad) * radius);
            rad+=turn;
        }
    }
}
