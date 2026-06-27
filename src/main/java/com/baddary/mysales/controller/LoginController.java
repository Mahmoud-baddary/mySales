package com.baddary.mysales.controller;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

import com.baddary.mysales.MainApplication;
import com.baddary.mysales.dto.CustomerDTO;
import com.baddary.mysales.dto.LoginResponseDTO;
import com.baddary.mysales.dto.PhoneDTO;
import com.baddary.mysales.helper.Helper;
import com.baddary.mysales.service.AuthApiService;
import com.baddary.mysales.service.CustomerApiService;
import com.baddary.mysales.util.TokenStore;

public class LoginController {

    @FXML
    public TextField tfUserName;
    @FXML
    public PasswordField tfPassword;
    @FXML
    public Button btnLogin;
    @FXML
    public Label lblErrorMsg;
    private final AuthApiService authApiService = new AuthApiService();
    private final CustomerApiService customerApiService = new CustomerApiService();
    private Stage stage;

    
   

   

    @FXML
    public void onBtnLoginClick(ActionEvent event) throws IOException {
        this.stage = (Stage) tfUserName.getScene().getWindow();
        // verify that username and pwd is not empty
        if (tfUserName.getText().isBlank()) {
            lblErrorMsg.setText("user name can't be empty");
            return;
        }
        if (tfPassword.getText().isBlank()) {
            lblErrorMsg.setText("password can't be empty");
            return;
        }
        btnLogin.setDisable(true);
        lblErrorMsg.setText("Logging in...");

        String username = tfUserName.getText();
        String password = tfPassword.getText();

        Task<LoginResponseDTO> loginTask = authApiService.loginAsync(username, password);
        Helper.startTask(loginTask, ev -> {
            LoginResponseDTO response = loginTask.getValue();
            // Store token and user info
            TokenStore.setToken(response.getToken());
            TokenStore.setCurrentUser(response.getUserId(), response.getUserName());
            // add guest
            Task<Long> customerCountTask = customerApiService.countAsync();
            Helper.startTask(customerCountTask, e -> {
                Long customerCount = customerCountTask.getValue();
                if (customerCount == 0) { // add guest
                    CustomerDTO dto = new CustomerDTO();
                    dto.setName("guest");
                    PhoneDTO phoneDTO = new PhoneDTO();
                    phoneDTO.setPhoneNum("0000");
                    dto.getPhoneDTOSet().add(phoneDTO);
                    Task<CustomerDTO> addCustomerTask = customerApiService.addCustomerAsync(dto);
                    Helper.startTask(addCustomerTask, null, null, stage);
                }
            }, null, this.stage);

            // Load the next screen
            try {
                FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("sales_order_view.fxml"));
                Scene scene = new Scene(loader.load());
                this.stage.setScene(scene);
                this.stage.setTitle("Sales Management");
                this.stage.sizeToScene();
                Helper.loadStyle(scene);
                SalesOrderController controller = loader.getController();
                controller.initialize(stage);
            } catch (IOException e) {
                lblErrorMsg.setText("Error loading main screen");
                e.printStackTrace();
            } finally {
                btnLogin.setDisable(false);
            }

        }, ev -> {
            lblErrorMsg.setText("Login failed: " + loginTask.getException().getMessage());
            btnLogin.setDisable(false);
        }, this.stage);

    }
}