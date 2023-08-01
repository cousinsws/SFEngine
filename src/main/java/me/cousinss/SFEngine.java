package main.java.me.cousinss;

import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
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

    public static void main(String[] args) {
        System.out.println("Hello world");
        launch(args);
    }

    private BorderPane root;
    private List<Vertex> vertices;
    private Set<Edge> edges;
    private Text hud;

    @Override
    public void start(Stage stage) {
        stage.setTitle("Hello World GUI Test");
        root = new BorderPane();
        hud = new Text("Components: 1\nTough: False");
        root.setTop(hud);
        stage.setScene(new Scene(root, 600, 600));
        stage.show();

        MatrixGraph m = new MatrixGraph(10);
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
            m.putEdge(e.v1, e.v2);
        }
//        System.out.println(m.toString(true));
        for(Vertex v : vertices) {
            root.getChildren().add(v);
        }
        int numComponents = m.numComponents();
        int[] tough = m.isTough();
        hud.setText("Components: " + numComponents +"\nTough: " +
                (numComponents == 1 ? (tough == null ? "True" :
                        ("False, Breaking Set: " + Arrays.toString(tough))) : "False (Disconnected)"));
        colorGroupVertices(m, numComponents);
        if(tough != null && numComponents == 1) {
            for(int i = 0; i < tough.length; i++) {
                vertices.get(tough[i]).setStroke(Color.BLACK);
            }
        }
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
        private Circle c;
        private Text t;
        private int id;
        public Vertex(double x, double y, String text, int id) {
            super(new Circle(10, Color.RED), new Text(text));
            this.id = id;
            this.c = (Circle) this.getChildren().get(0);
            c.setStrokeWidth(3);
            c.setStroke(Color.TRANSPARENT);
            this.t = (Text) this.getChildren().get(1);
            this.setCenterX(x);
            this.setCenterY(y);
            this.setOnMouseDragged(e -> {
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
