package com.baddary.mysales.controller;

import com.baddary.mysales.MainApplication;
import com.baddary.mysales.dto.UserDTO;
import com.baddary.mysales.enums.UserRole;
import com.baddary.mysales.helper.Helper;
import com.baddary.mysales.service.UserApiService;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class RegisterController {
    @FXML
    public TextField usernameField;
    @FXML
    public PasswordField passwordField;
    @FXML
    public PasswordField confirmPasswordField;
    @FXML
    public ComboBox<String> roleComboBox;
    @FXML
    public Label errorLabel;

    private final UserApiService userApiService = new UserApiService();
    private Stage stage;

    @FXML
    public void initialize(Stage stage) {
        this.stage = stage;
        // if no user so the first user must be admin
        Task<Long> countAsync = userApiService.countAsync();
        Helper.startTask(countAsync, e -> {
            Long count = countAsync.getValue();
            if (count == 0) {
                roleComboBox.getSelectionModel().select(0);
                roleComboBox.setDisable(true);
            }
        }, null, stage);

    }

    @FXML
    public void handleRegister(ActionEvent event) {
        String userName = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        if (userName.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            errorLabel.setText("empty fields are not allowed");
            return;
        }
        if (!password.equals(confirmPassword)) {
            errorLabel.setText("password doesn't match confirm password");
            return;
        }

        String roleString = roleComboBox.getSelectionModel().getSelectedItem().toString();
        UserRole role = roleString.equalsIgnoreCase("admin") ? UserRole.ADMIN : UserRole.USER;
        UserDTO userDTO = new UserDTO();
        userDTO.setName(userName);
        userDTO.setRole(role);
        userDTO.setPassword(password);
        Task<Long> userCountTask = userApiService.countAsync();
        Helper.startTask(userCountTask, e -> {
            Long count = userCountTask.getValue();
            if (count == 0) {
                // register first time
                Task<UserDTO> registerFirstAsync = userApiService.registerFirstAsync(userDTO);
                Helper.startTask(registerFirstAsync, ev -> {
                    Helper.createAlertInfo("Success", "user registeration is successful").show();
                    // go to login page
                    FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("login_view.fxml"));
                    try {
                        Parent root = loader.load();
                        stage.setScene(new Scene(root));
                    } catch (IOException e1) {
                        Helper.createAlertError("Error", e1.getMessage()).show();
                    }

                }, null, this.stage);
            } else {
                Task<UserDTO> registerAsync = userApiService.registerAsync(userDTO);
                Helper.startTask(registerAsync, ev -> {
                    Helper.createAlertInfo("Success", "user registeration is successful").show();
                    clearFields();
                }, null, this.stage);
            }
        }, null, this.stage);

    }

    @FXML
    public void handleLogin(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("login_view.fxml"));
        Parent root = loader.load();
        this.stage.setScene(new Scene(root));
    }

    private void clearFields() {
        // clear fields
        usernameField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
    }
}
