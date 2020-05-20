package client;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class VoiceMessageWindow 
{
    Stage stage;
    
    void display(clientMain main, String selectedClient)
    {       
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("VoiceMessagePage.fxml"));
        Parent root;
        
        try 
        {
            root = loader.load();
            
            Scene scene = new Scene(root, 250, 150);
            scene.getStylesheets().clear();
            scene.getStylesheets().add("client/ChatPageStyle.css");
            stage = new Stage();
            
            VoiceMessagePageController controller = loader.getController();
            controller.setStage(stage);
            controller.setSelectedClient(selectedClient);
            
            stage.setTitle("Voice Message");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(chatPageController.class.getName()).log(Level.SEVERE, null, ex);
        }  
    }
}
