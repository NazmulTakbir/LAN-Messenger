package client;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AddMembersWindow 
{
    public void display(clientMain main)
    {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("addMembersList.fxml"));
        Parent root;
        
        try 
        {
            root = loader.load();
            
            AddMembersListController controller = loader.getController();
            root.addEventHandler(KeyEvent.KEY_PRESSED, ev -> {
            if (ev.getCode() == KeyCode.ENTER) 
            {
               controller.createGroupAction();
               ev.consume(); 
            }
            });
            
            Scene scene = new Scene(root, 300, 500);
            scene.getStylesheets().clear();
            scene.getStylesheets().add("client/GroupWindowStyle.css");
            Stage stage = new Stage();
            stage.setTitle("Create Group");
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
