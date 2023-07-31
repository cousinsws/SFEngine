package main.java.me.cousinss;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class SFEngine extends Application  {
    public static void main(String[] args) {
        System.out.println("Hello world");
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Hello World GUI Test");
        stage.setScene(new Scene(new StackPane(), 200, 200));
        stage.show();
    }
}
