package com.baddary.mysales;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import com.baddary.mysales.controller.RegisterController;
import com.baddary.mysales.helper.Helper;
import com.baddary.mysales.service.UserApiService;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApplication extends Application {

    private final UserApiService userApiService = new UserApiService();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        proceedToUserUI(primaryStage);
    }

    @Override
    public void stop() {
        executor.shutdownNow();
    }

    private void proceedToUserUI(Stage primaryStage) {
        Task<Long> userCountTask = userApiService.countAsync();
        userCountTask.setOnSucceeded(ev -> {
            Long userCount = userCountTask.getValue();
            FXMLLoader fxmlLoader;
            Scene scene;

            try {
                if (userCount == 0) {

                    // if no users show register view firstly

                    fxmlLoader = new FXMLLoader(MainApplication.class.getResource("register_view.fxml"));
                    scene = new Scene(fxmlLoader.load());
                    RegisterController controller = fxmlLoader.getController();
                    controller.initialize(primaryStage);

                } else {
                    fxmlLoader = new FXMLLoader(MainApplication.class.getResource("login_view.fxml"));
                    scene = new Scene(fxmlLoader.load());
                   
                }

                primaryStage.setTitle("Hello!");
                primaryStage.setScene(scene);
                primaryStage.setMinWidth(350);
                primaryStage.setMinHeight(300);
                primaryStage.sizeToScene();
                primaryStage.show();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

        });
        userCountTask.setOnFailed(ev -> Helper.createAlertError("Error", "couldn't get users count",
                userCountTask.getException().getMessage()).show());
        executor.submit(userCountTask);
    }
}