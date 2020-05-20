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
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class VoiceMessagePage2Controller implements Initializable 
{
    private Stage stage;
    private String selectedClient;
    
    @FXML
    private Button sendButton, cancelButton;
    
    @FXML
    private void sendButtonAction()
    {
        File voice = new File("VoiceMessage\\RecordAudio.wav");
        if(voice!=null)
        {
            if( selectedClient.startsWith("GroupChat : ") ) 
            {
                try 
                {
                    byte[] content = Files.readAllBytes(voice.toPath());
                    int i;
                    for( i=0; i<=clientMain.groups.size()-1; i++ )
                    {
                        if( clientMain.groups.get(i).getName().equals(selectedClient) )
                        {
                            break;
                        }
                    }
                    for( String name : clientMain.groups.get(i).getMembers() )
                    {
                        if( name.equals(clientMain.name) )
                        {
                            continue;
                        }
                        int j;
                        for( j=0; j<=clientMain.clientsOnline.size()-1; j++ )
                        {
                            if( clientMain.clientsOnline.get(j).getName().equals( name ) )
                            {
                                break;
                            }
                        }
                        if( j > clientMain.clientsOnline.size()-1 )
                        {
                            continue;
                        }
                        try
                        {
                            Socket socket = new Socket( clientMain.clientsOnline.get(j).getAddress(), clientMain.clientsOnline.get(j).getPort() );
                            NetworkUtil nu = new NetworkUtil(socket);
                            
                            nu.write("Voice Message Coming");
                            nu.write(clientMain.name);
                            nu.write(voice.getName());
                            nu.write(content);
                            nu.write("Voice Sent");
                            
                            nu.closeConnection();
                        }
                        catch (IOException ex)
                        {
                            Logger.getLogger(chatPageController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } 
                catch (IOException ex) 
                {
                    Logger.getLogger(chatPageController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else
            {
                try 
                {
                    byte[] content = Files.readAllBytes(voice.toPath());
                    for(onlineInfo a: clientMain.clientsOnline)
                    {
                        if(a.getName().equals(selectedClient))
                        {
                            Socket receiverSocket=new Socket(a.getAddress(), a.getPort());
                            NetworkUtil recvr_nu=new NetworkUtil(receiverSocket);

                            recvr_nu.write("Voice Message Coming");
                            recvr_nu.write(clientMain.name);
                            recvr_nu.write(voice.getName());
                            recvr_nu.write(content);
                            recvr_nu.write("Voice Sent");

                            recvr_nu.closeConnection();
                            break;
                        }
                    }
                } 
                catch (IOException ex) 
                {
                    Logger.getLogger(chatPageController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        stage.close();
    }
    
    @FXML
    private void cancelButtonAction()
    {
        stage.close();
    }
    
    void setStage(Stage stage)
    {
        this.stage = stage;
    }
    void setSelectedClient(String selectedClient)
    {
        this.selectedClient = selectedClient;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {

    }    
}
