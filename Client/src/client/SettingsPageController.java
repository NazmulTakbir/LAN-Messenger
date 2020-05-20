package client;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class SettingsPageController implements Initializable 
{
    private Stage stage;
    
    @FXML 
    private Button closeButton, setPassword, choosePic, removePic;
    @FXML 
    private PasswordField currentPassword, newPassword, confirmPassword;
            
    @FXML
    private void closeButtonAction()
    {
        stage.close();
    }
    
    @FXML
    private void removePicAction()
    {
        try 
        {
            Socket socket = new Socket( clientMain.serverIP, 9999 );
            NetworkUtil nu = new NetworkUtil(socket);
            
            nu.write("ProfilePicRemove");
            nu.write(clientMain.name);
            nu.closeConnection();
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(SettingsPageController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    private void setPasswordAction()
    {
        String currentP = currentPassword.getText();
        String newP = newPassword.getText();
        String confirmP = confirmPassword.getText();

        System.out.println( currentP + " " + newP + " " + confirmP );

        if( newP.equals("") || currentP.equals("") || confirmP.equals("") )
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("Field(s) Cannot Be Empty");
            alert.showAndWait();
        }
        else if( ! newP.equals( confirmP ) )
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("New Password And Confirm Password Don't Match");
            alert.showAndWait();
        }
        else
        {
            try 
            {
                Socket socket = new Socket( clientMain.serverIP, 9999 );
                NetworkUtil nu = new NetworkUtil(socket);
                
                nu.write("PasswordChange");
                
                nu.write(clientMain.name);
                nu.write(currentP);
                nu.write(newP);
                
                String verdict = (String) nu.read();
                
                if( verdict.equals("Password Changed Successfully") )
                {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Successful");
                    alert.setHeaderText(verdict);
                    alert.showAndWait();
                }
                else
                {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Failure");
                    alert.setHeaderText(verdict);
                    alert.showAndWait();
                }
                
                nu.closeConnection();
            } 
            catch (IOException ex) 
            {
                Logger.getLogger(SettingsPageController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        // TODO
    }    
    
    @FXML 
    private void choosePicAction()
    {
        try 
        {
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().addAll( new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.jpeg") );
            File selected = fc.showOpenDialog(null);
            
            if( selected!=null )
            {
                byte[] imageBytes = Files.readAllBytes( selected.toPath() );
            
                Socket socket = new Socket( clientMain.serverIP, 9999 );
                NetworkUtil nu = new NetworkUtil( socket );

                nu.write("ProfilePicChange");
                nu.write( clientMain.name );
                nu.write(selected.getName());
                nu.write( imageBytes );

                nu.closeConnection();
            }
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(SettingsPageController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    void setStage(Stage stage)
    {
        this.stage = stage;
    }
}
