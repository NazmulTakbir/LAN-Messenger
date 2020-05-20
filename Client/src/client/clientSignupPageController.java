package client;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class clientSignupPageController implements Initializable
{
    private clientMain main;
    @FXML TextField userName;
    @FXML PasswordField password;
    @FXML PasswordField confirmPassword;
    @FXML Button signup,reset,back;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        SignupThread.setController(this);
    }  
    
    @FXML
    void signupAction(ActionEvent ae)
    {
        String name=userName.getText();
        String pass=password.getText();
        
        if( name.equals("") || pass.equals(""))
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("Username And Password Fields Cannot Be Empty");
            alert.showAndWait();
            return;
        }
        
        String confirmPass=confirmPassword.getText();
        if(pass.equals(confirmPass))
        {
            new SignupThread(name,pass);
        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("Password and Password Confirmation do not match");
            alert.showAndWait();
        }
    }
    
    @FXML
    void resetAction(ActionEvent ae)
    {
        userName.setText("");
        password.setText("");
        confirmPassword.setText("");
    }
    
    @FXML
    void backAction(ActionEvent ae) throws IOException
    {
        main.showLoginPage();
    }
    
    void signupFail()
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Incorrect Credentials");
        alert.setHeaderText("User Name already exists!");
        alert.setContentText("Please, try again with different User Name");
        alert.showAndWait();
    }
    
    void signupSuccess() throws IOException
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("SignUp Successful");
        alert.setHeaderText(null);
        alert.setContentText("SignUp is Successful! Now Login to your Account");
        
        ButtonType login = new ButtonType("Log In");
        alert.getButtonTypes().setAll(login);
        
        Optional<ButtonType> result=alert.showAndWait();
        if(result.get() == login)
        {
            main.showLoginPage();
        }
    }
    void signupFail2()
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Error");
        alert.setHeaderText("Server Is Offline");
    }
    
    void setMain(clientMain main)
    {
        this.main = main;
    }
}