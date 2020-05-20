package client;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SettingsWindow 
{
    public void display(clientMain main)
    {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("SettingsPage.fxml"));
        Parent root;
        
        try 
        {
            root = loader.load();
            
            SettingsPageController controller = loader.getController();
            
            Scene scene = new Scene(root, 350, 400);
            
            scene.getStylesheets().clear();
            scene.getStylesheets().add("client/ChatPageStyle.css");
            
            Stage stage = new Stage();
            stage.setTitle("Settings");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.show();
           
            controller.setStage(stage);
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(chatPageController.class.getName()).log(Level.SEVERE, null, ex);
        }  
    }
}
