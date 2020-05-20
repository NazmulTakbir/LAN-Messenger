package client;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class IPAddressWindowController implements Initializable 
{
    private clientMain main;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
    
    }    
    
    @FXML
    Button submitButton; 
    @FXML
    TextField ipField, ipField2;
    
    void setMain(clientMain main)
    {
        this.main = main;
    }
    
    @FXML 
    private void submitAction() throws IOException
    {
        if( ipField.getText().equals("") || ipField.getText()==null || ipField2.getText().equals("") || ipField2.getText()==null)
        {
            return;
        }
        else
        {
            main.serverIP = ipField.getText();
            main.ownIP = ipField2.getText();
            main.showLoginPage();
        }
    }
}
