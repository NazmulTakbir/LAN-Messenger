package client;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class clientLoginPageController implements Initializable
{
    private clientMain main;
    
    @FXML 
    TextField usernameField;
    @FXML
    PasswordField passwordField;
    @FXML
    Label errorMessage;
    @FXML
    Button signupButton, loginButton, resetButton;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        LoginThread.setController(this);
    }  
    
    @FXML
    void resetAction(ActionEvent ae)
    {
        usernameField.setText(null);
        passwordField.setText(null);
    }
    
    @FXML
    void loginAction(ActionEvent ae)
    {
        String name = usernameField.getText();
        String password = passwordField.getText();
        new LoginThread(name, password); 
    }
    void loginfail(String message)
    {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("LogIn Failed");
        alert.setHeaderText("Login Failed");
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    @FXML
    void signupAction(ActionEvent ae) throws IOException
    {
        main.showSignupPage();
    }
    
    void setMain(clientMain main)
    {
        this.main = main;
    }
}
