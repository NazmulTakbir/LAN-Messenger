package client;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class EmojiWindow 
{
    boolean isEmojiSelected = false;
    String selectedEmoji;
    Stage stage;
    
    String display(clientMain main)
    {
        EmojiPageController.setWindow(this);
        
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("EmojiPage.fxml"));
        Parent root;
        
        try 
        {
            root = loader.load();
            Scene scene = new Scene(root, 600, 500);
            scene.getStylesheets().clear();
            scene.getStylesheets().add("client/EmojiPageStyle.css");
            stage = new Stage();
            stage.setTitle("Select Emoji");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(chatPageController.class.getName()).log(Level.SEVERE, null, ex);
        }  
        return selectedEmoji;
    }
}
