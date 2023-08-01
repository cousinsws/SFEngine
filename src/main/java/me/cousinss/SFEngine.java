package main.java.me.cousinss;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class SFEngine extends Application  {

    private static final double FULL_TURN = 2 * Math.PI;

    public static void main(String[] args) {
        System.out.println("Hello world");
        launch(args);
    }

    private Pane root;

    @Override
    public void start(Stage stage) {
        stage.setTitle("Hello World GUI Test");
        root = new Pane();

        stage.setScene(new Scene(root, 600, 600));
        stage.show();

        MatrixGraph m = new MatrixGraph(10);
        List<Vertex> vertices = new ArrayList<>();
        List<Line> edges = new ArrayList<>();
        for(int i = 0; i < 10; i++) {
            vertices.add(new Vertex(0, 0));
        }
        arrangeRadially(vertices, new Point2D(300, 300), 100);
        for(Vertex v : vertices) {
            root.getChildren().add(v);
        }
    }

    private class Vertex extends Circle {
        public Vertex(double x, double y) {
            super(x, y, 10, Color.RED);
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
