package client;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class AddMembersListController implements Initializable 
{
    private static chatPageController chatController;
    
    private group tempGroup = new group();
    private Stage stage;
    
    @FXML
    private Button createGroup, cancel, selectAll;
    @FXML
    private ListView activeList;
    @FXML
    private TextField groupNameField;
    
    private ObservableList<CheckBox> list;
    private ArrayList<CheckBox> boxes;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        tempGroup.getMembers().add(clientMain.name);
        
        boxes = new ArrayList<CheckBox>();
        
        int size = clientMain.clientsOnline.size();
        for( int i=0; i<=size-1; i++ )
        {
            String name = clientMain.clientsOnline.get(i).getName();
            if( ! name.equals( clientMain.name ) )
            {
                CheckBox box = new CheckBox(name);
                box.setFont( new Font("Arial Black", 12) );
                box.setOnAction( ae -> {
                    if( ! isMemberPresent( box.getText() ) )
                    {
                        tempGroup.getMembers().add( box.getText() );
                    }
                    else
                    {
                        tempGroup.getMembers().remove( box.getText() );
                    }
                });
                boxes.add( box );
            }
        }
        
        list = FXCollections.observableArrayList(boxes);
        activeList.setItems(list);
    }    
    
    @FXML
    void createGroupAction()
    {
        String name = groupNameField.getText(); 
        if( name.equals("") )
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("Name Field Cannot Be Empty");
            alert.showAndWait();
        }
        else if( isGroupNamePresent(name) )
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("Group With Same Name Exists");
            alert.showAndWait();
        }
        else if( tempGroup.getMembers().size() < 3 )
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("You Need To Add At Least Two Members");
            alert.showAndWait();
        }
        else
        {
            tempGroup.setName( "GroupChat : " + name);
            clientMain.groups.add(tempGroup);
            stage.close();
            notifyOtherClients();
            clientMain.messagesHistory.add( new messageHistory( "GroupChat : " + name) );
            chatController.loadOnlineListView();
            
            /* The newly created group is added to the list of groups for the current 
               client. Then the updated group list is sent to all the other clients ( regardless
               of whether they are in the group or not. But the chat thread of the group 
               appears in a clients GUI only if he is in the group ). Then a thread for this 
               new group is added to the messagesHistory of the current Client
            */
        }
    }
    
    @FXML 
    private void cancelAction()
    {
        stage.close();
    }
    
    @FXML 
    private void selectAllAction()
    {
        if( selectAll.getText().equals("Select All") )
        {
            for( CheckBox temp : boxes )
            {
                if( !temp.isSelected() )
                {
                    temp.fire();
                }
            }
            selectAll.setText("Unselect All");
        }
        else
        {
            for( CheckBox temp : boxes )
            {
                if( temp.isSelected() )
                {
                    temp.fire();
                }
            }
            selectAll.setText("Select All");
        }
    }
    
    void setStage(Stage stage)
    {
        this.stage = stage;
    }
    
    private boolean isMemberPresent(String name)
    {
        for( String tempName : tempGroup.getMembers() )
        {
            if( name.equals(tempName) )
            {
                return true;
            }
        }
        return false;
    }
    
    private boolean isGroupNamePresent(String name)
    {
        for( group temp : clientMain.groups )
        {
            if( temp.getName().equals(name) )
            {
                return true;
            }
        }
        return false;
    }
    
    private void notifyOtherClients()
    {
        for( onlineInfo temp : clientMain.clientsOnline )
        {
            if( temp.getName().equals(clientMain.name) )
            {
                continue;
            }
            try 
            {
                Socket socket = new Socket(temp.getAddress(), temp.getPort());
                NetworkUtil nu = new NetworkUtil(socket);
                
                nu.write("GroupsInfo");
                
                int size = clientMain.groups.size();
                nu.write( String.valueOf( size ) );
                
                for( int i=0; i<=size-1; i++ )
                {
                    nu.write( clientMain.groups.get(i) );
                }
                nu.closeConnection();   
            } 
            catch (IOException ex) 
            {
                Logger.getLogger(AddMembersListController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    static void setController(chatPageController controller)
    {
        chatController = controller;
    }
}
