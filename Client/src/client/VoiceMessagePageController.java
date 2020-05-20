package client;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class VoiceMessagePageController implements Initializable 
{
    private Thread thread;
    private Stage stage;
    private boolean recorded = false;
    private boolean isRecording = false;
    private String selectedClient;
    
    void setStage(Stage stage)
    {
        this.stage = stage;
    }
    void setSelectedClient(String selectedClient)
    {
        this.selectedClient = selectedClient;
    }
    
    @FXML
    private ImageView imgVW;
    @FXML
    private Button startStopButton;
    @FXML
    private Label recordLabel;
    
    @FXML
    private void startStopButtonAction()
    {
        if( startStopButton.getText().equals("Start") )
        {
            try 
            {
                startStopButton.setText("Stop");
                Image imgRaw = new Image(new FileInputStream("src\\client\\Pictures\\Stop.png"));
                imgVW.setImage(imgRaw);
                recordLabel.setVisible(true);
                
                thread.start();
                isRecording = true;
                
                stage.setOnCloseRequest( e -> {
                    e.consume();
                    closeWindowAction();
                });
            } 
            catch (FileNotFoundException ex) 
            {
                Logger.getLogger(VoiceMessagePageController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else if( startStopButton.getText().equals("Stop") )
        {
            try 
            {
                startStopButton.setText("Start");
                Image imgRaw = new Image(new FileInputStream("src\\client\\Pictures\\Start.png"));
                imgVW.setImage(imgRaw);
                recordLabel.setVisible(false);
                
                VoiceMessage.finish();
                recorded = true;
                isRecording = false;
                
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("VoiceMessagePage2.fxml"));
                Parent root;
                
                root = loader.load();
            
                Scene scene = new Scene(root, 250, 100);
                scene.getStylesheets().clear();
                scene.getStylesheets().add("client/ChatPageStyle.css");
                VoiceMessagePage2Controller controller = loader.getController();
                controller.setStage(stage);
                controller.setSelectedClient(selectedClient);
                
                stage.setScene(scene);
            } 
            catch (FileNotFoundException ex) 
            {
                Logger.getLogger(VoiceMessagePageController.class.getName()).log(Level.SEVERE, null, ex);
            } 
            catch (IOException ex) 
            {
                Logger.getLogger(VoiceMessagePageController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void closeWindowAction()
    {
        if( isRecording )
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Cannot Send While Recording");
            alert.setContentText("Press Stop To Stop Recording");
            alert.showAndWait();
        }
        else
        {
            stage.close();
        }
    }
        
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        try 
        {
            Image imgRaw = new Image(new FileInputStream("src\\client\\Pictures\\Start.png"));
            imgVW.setImage(imgRaw);
            recordLabel.setVisible(false);
            
            VoiceMessage javaSoundRecorder=new VoiceMessage();
            thread = new Thread(javaSoundRecorder);
        } 
        catch (FileNotFoundException ex) 
        {
            Logger.getLogger(VoiceMessagePageController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
}
