package FiSy;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

public class Main extends Application {


    public static Stage pStage;

    public Stage getPrimaryStage() {
        return pStage;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        pStage = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("mainfx.fxml"));
        primaryStage.setTitle("");
        primaryStage.initStyle(StageStyle.UNIFIED);
        primaryStage.setResizable(false);
        primaryStage.setMaximized(false);
        Scene scene = new Scene(root,  Color.rgb(31,33,32));
        primaryStage.setScene(scene);
        //primaryStage.sizeToScene();

        primaryStage.setOnHiding(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("Application Closed by click to Close Button(X)");
                        System.exit(0);
                    }
                });
            }
        });

        primaryStage.show();
    }
}
