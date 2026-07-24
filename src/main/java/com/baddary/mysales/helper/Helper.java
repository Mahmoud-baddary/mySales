package com.baddary.mysales.helper;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import com.baddary.mysales.MainApplication;
import com.baddary.mysales.exception.UnauthorizedException;
import com.baddary.mysales.util.TokenStore;

public class Helper {

    private Helper() {

    }

    public static Alert createAlertInfo(String title, String... messages) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        StringBuilder message = new StringBuilder();
        for (String s : messages) {
            message.append(s);
            message.append("\n");
        }

        TextArea textArea = new TextArea(message.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefRowCount(10);
        textArea.setPrefColumnCount(40);

        // Set the TextArea as the content (instead of standard label)
        alert.getDialogPane().setContent(textArea);
        alert.setResizable(true);

        // Adjust dialog size
        alert.getDialogPane().setMinWidth(450);
        alert.getDialogPane().setMinHeight(300);
        return alert;
    }

    public static Alert createAlertError(String title, String... messages) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        StringBuilder message = new StringBuilder();
        for (String s : messages) {
            message.append(s).append("\n");
        }

        // Create a TextArea to display the full message
        TextArea textArea = new TextArea(message.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefRowCount(10);
        textArea.setPrefColumnCount(40);

        // Set the TextArea as the content (instead of standard label)
        alert.getDialogPane().setContent(textArea);
        alert.setResizable(true);

        // Adjust dialog size
        alert.getDialogPane().setMinWidth(450);
        alert.getDialogPane().setMinHeight(300);

        return alert;
    }

    public static boolean isValidDouble(String text) {
        return text.matches("[-+]?(?:\\d*\\.\\d+|\\d+\\.\\d*|\\d+)");
    }

    public static void loadStyle(Scene scene) {
        scene.getStylesheets().add(Objects.requireNonNull(Helper.class.getResource("/styles.css")).toExternalForm());
    }

    public static void startTask(Task<?> task, EventHandler<WorkerStateEvent> onSuccess,
            EventHandler<WorkerStateEvent> onFailure, Stage stage) {
        task.setOnSucceeded(onSuccess);
        if (onFailure == null) {
            task.setOnFailed(e -> {
                createAlertError("Error", task.getException().getMessage()).show();
                Throwable exception = task.getException();
                System.out.println(exception.getClass());
                System.out.println(exception instanceof UnauthorizedException);
                if (exception instanceof UnauthorizedException) {
                    TokenStore.clear();
                    redirectToLogin(stage);
                }
            });

        } else {
            task.setOnFailed(onFailure);
        }
        new Thread(task).start();
    }

    private static void redirectToLogin(Stage stage) {

        try {
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("login_view.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage2 = new Stage();
            stage2.setScene(scene);
            stage2.setTitle("Login");
            stage2.setMinHeight(300);
            stage2.setMinWidth(350);
            stage2.sizeToScene();
            Stage owner = (Stage) stage.getOwner();
            if (owner == null) {
                stage.close();
            } else {
                owner.close();
            }
            stage2.show();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void createAlertConfirm(String title, String headerTxt, String content,
            OnBtnOkClicked onBtnOkClicked) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(headerTxt);
        alert.setContentText(content);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            onBtnOkClicked.onBtnOkClicked();
        } else {

        }
    }

    @FunctionalInterface
    public interface OnBtnOkClicked {
        void onBtnOkClicked();
    }

}
