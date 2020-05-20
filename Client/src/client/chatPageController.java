package client;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class chatPageController implements Initializable
{          
    private clientMain main;
    String selectedClient;
    
    @FXML
    Button sendButton, emojiButton, settingsButton;
    @FXML
    private ListView<VBox> chatList;
    @FXML
    private ListView<HBox> onlineListView;
    @FXML
    private TextField messageField;
    @FXML
    private Button createGroup, logOut, sendImage, sendFile, voiceMessage;
    @FXML
    Label clientName, userSelected;
    @FXML
    private ObservableList<HBox> onlineData;
    @FXML
    private Label selectUserLabel;
    @FXML
    private ImageView imgView;
    @FXML
    private StackPane picBackground;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        clientServeThread.setController(this);
        clientName.setText( main.name );
        AddMembersListController.setController(this);
    }  
    
    void loadProPic()
    {
        int i, size = main.clientsOnline.size();
        for( i=0; i<=size-1; i++ )
        {
            if( main.clientsOnline.get(i).getName().equals(main.name) )
            {
                break;
            }
        }
        byte[] imgBytes = main.clientsOnline.get(i).getImageBytes();
        
        Image imgRaw = new Image( new ByteArrayInputStream(imgBytes) );
        imgView.setImage(imgRaw);
        
        imgView.setPreserveRatio(false);
        imgView.setSmooth(true);
    }
    
    @FXML 
    private void settingsButtonAction()
    {
        SettingsWindow window = new SettingsWindow();
        window.display(main);
    }
    
    @FXML
    private void sendImageAction()
    {
        if( selectedClient==null )
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("Please Select A User First");
            alert.showAndWait();
            return;
        }
        
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File imgSelected = fc.showOpenDialog(null);
        
        if(imgSelected!=null)
        {
            if( selectedClient.startsWith("GroupChat : ") )    // added NTS17
            {
                try 
                {
                    byte[] content = Files.readAllBytes(imgSelected.toPath());
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
                        if( name.equals(main.name) )
                        {
                            int totalMessageThreads = clientMain.messagesHistory.size();
                            for( int j=0; j<=totalMessageThreads-1; j++ )
                            {
                                if( clientMain.messagesHistory.get(j).getName().equals(selectedClient) )
                                {
                                    clientMain.messagesHistory.get(j).getChatHistory().add(new messageData(main.name, "PictureSent: "+imgSelected.getName()) );
                                }
                            }
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
                            nu.write("Image Coming");
                            nu.write( selectedClient );
                            nu.write( main.name );
                            nu.write( imgSelected.getName() );
                            nu.write( content );
                            nu.closeConnection();
                        }
                        catch (IOException ex)
                        {
                            Logger.getLogger(chatPageController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        
                        File file = new File("PicsSent\\"+imgSelected.getName());
                        Files.write(file.toPath(), content);
                        loadChatList(selectedClient);
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
                    byte[] content = Files.readAllBytes(imgSelected.toPath());
                    for(onlineInfo a: clientMain.clientsOnline)
                    {
                        if(a.getName().equals(selectedClient))
                        {
                            Socket receiverSocket=new Socket(a.getAddress(), a.getPort());
                            NetworkUtil recvr_nu=new NetworkUtil(receiverSocket);

                            recvr_nu.write("Image Coming");
                            recvr_nu.write(main.name);        // added NTS17
                            recvr_nu.write(imgSelected.getName());
                            recvr_nu.write(content);
                            // recvr_nu.write("Downloaded");        // removed NTS17
                            
                            int totalMessageThreads = clientMain.messagesHistory.size();
                            for( int j=0; j<=totalMessageThreads-1; j++ )
                            {
                                if( clientMain.messagesHistory.get(j).getName().equals(selectedClient) )
                                {
                                    clientMain.messagesHistory.get(j).getChatHistory().add(new messageData(main.name, "PictureSent: "+imgSelected.getName()) );
                                }
                            }
                            File file = new File("PicsSent\\"+imgSelected.getName());
                            Files.write(file.toPath(), content);
                            loadChatList(selectedClient);
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
    }
    
    @FXML
    private void sendFileAction()
    {
        if( selectedClient==null )
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("Please Select A User First");
            alert.showAndWait();
            return;
        }
        
        FileChooser fc = new FileChooser();
        File fileSelected = fc.showOpenDialog(null);
        
        if(fileSelected!=null)
        {
            if( selectedClient.startsWith("GroupChat : ") )    // added NTS17
            {
                Thread thr = new Thread()
                {
                    @Override
                    public void run()
                    {
                        try 
                        {
                            byte[] content = Files.readAllBytes(fileSelected.toPath());
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
                                if( name.equals(main.name) )
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
                                    nu.write("File Coming");
                                    nu.write( selectedClient+" "+clientMain.name );
                                    nu.write( fileSelected.getName() );
                                    nu.write( content );
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
                };
                thr.start(); 
            }
            else        // added NTS17
            {
                Thread thr = new Thread()
                {
                    @Override
                    public void run()
                    {
                        try 
                        {
                            byte[] content = Files.readAllBytes(fileSelected.toPath());
                            for(onlineInfo a: clientMain.clientsOnline)
                            {
                                if(a.getName().equals(selectedClient))
                                {
                                    Socket receiverSocket=new Socket(a.getAddress(), a.getPort());
                                    NetworkUtil recvr_nu=new NetworkUtil(receiverSocket);

                                    recvr_nu.write("File Coming");
                                    recvr_nu.write(main.name);        // added NTS17
                                    recvr_nu.write(fileSelected.getName());
                                    recvr_nu.write(content);
                                    // recvr_nu.write("Downloaded");        // removed NTS17

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
                };
                thr.start();
            }
        }
    }
    
    @FXML
    private void voiceMessageAction()        
    {
        if( selectedClient==null )
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("Please Select A User First");
            alert.showAndWait();
            return;
        }
        VoiceMessageWindow window = new VoiceMessageWindow();
        window.display(main, selectedClient);
    }
    
    void loadOnlineListView()
    {
        ArrayList<HBox> temp = new ArrayList<HBox>();
        for( int i=0; i<=clientMain.clientsOnline.size()-1; i++ )
        {
            String name = clientMain.clientsOnline.get(i).getName();
            if( !name.equals(main.name) )
            {
                try 
                {
                    Label picLabel = new Label();
                    Image imgRaw = new Image( new ByteArrayInputStream( clientMain.clientsOnline.get(i).getImageBytes() ) );
                    ImageView imgV = new ImageView(imgRaw);
                    imgV.setFitHeight(25);
                    imgV.setFitWidth(25);
                    picLabel.setGraphic(imgV);
                
                    Label nameLabel = new Label(name);
                    nameLabel.setFont(new Font("Arial Black", 12));
                    nameLabel.setMinWidth(175);
                    nameLabel.setPadding( new Insets(0, 0, 0, 5) );
                            
                    Label ntfPicLabel = new Label();
                    ImageView ntfImgV = new ImageView(new Image(new FileInputStream("src\\client\\Pictures\\NotificationBell.png")));
                    ntfImgV.setFitHeight(25);
                    ntfImgV.setFitWidth(25);
                    ntfPicLabel.setGraphic(ntfImgV);
                    
                    Label ntfCountLabel = new Label("");
                    ntfCountLabel.setFont(new Font("Arial Black", 12));
                    
                    int j;
                    for( j=0; j<=clientMain.messagesHistory.size()-1; j++ )
                    {
                        if( clientMain.messagesHistory.get(j).getName().equals(name) )
                        {
                            break;
                        }
                    }
                    if( clientMain.messagesHistory.get(j).getUnReadCount() == 0 )
                    {
                        ntfPicLabel.setVisible(false);
                        ntfCountLabel.setVisible(false);
                    }
                    else
                    {
                        ntfPicLabel.setVisible(true);
                        ntfCountLabel.setVisible(true);
                        ntfCountLabel.setText( String.valueOf( clientMain.messagesHistory.get(j).getUnReadCount() ) );
                    }
                    
                    HBox tempHBox = new HBox();
                    tempHBox.getChildren().addAll(picLabel, nameLabel, ntfPicLabel, ntfCountLabel);
                    
                    temp.add(tempHBox);
                } 
                catch (FileNotFoundException ex) 
                {
                    Logger.getLogger(chatPageController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        for( group tempGroup : clientMain.groups )
        {
            int isPresent = tempGroup.getMembers().indexOf( clientMain.name );
            
            if( isPresent != -1 )
            {
                try 
                {
                    Label picLabel = new Label();
                    ImageView imgV = new ImageView(new Image(new FileInputStream("src\\client\\Pictures\\GroupChat.png")));
                    imgV.setFitHeight(25);
                    imgV.setFitWidth(25);
                    picLabel.setGraphic(imgV);

                    Label nameLabel = new Label(tempGroup.getName());
                    nameLabel.setFont(new Font("Arial Black", 12));
                    nameLabel.setMinWidth(175);
                    nameLabel.setPadding( new Insets(0, 0, 0, 5) );

                    Label ntfPicLabel = new Label();
                    ImageView ntfImgV = new ImageView(new Image(new FileInputStream("src\\client\\Pictures\\NotificationBell.png")));
                    ntfImgV.setFitHeight(25);
                    ntfImgV.setFitWidth(25);
                    ntfPicLabel.setGraphic(ntfImgV);

                    Label ntfCountLabel = new Label("2");
                    ntfCountLabel.setFont(new Font("Arial Black", 12));
                    
                    int j;
                    for( j=0; j<=clientMain.messagesHistory.size()-1; j++ )
                    {
                        if( clientMain.messagesHistory.get(j).getName().equals(tempGroup.getName()) )
                        {
                            break;
                        }
                    }
                    if( clientMain.messagesHistory.get(j).getUnReadCount() == 0 )
                    {
                        ntfPicLabel.setVisible(false);
                        ntfCountLabel.setVisible(false);
                    }
                    else
                    {
                        ntfPicLabel.setVisible(true);
                        ntfCountLabel.setVisible(true);
                        ntfCountLabel.setText( String.valueOf( clientMain.messagesHistory.get(j).getUnReadCount() ) );
                    }
                    
                    HBox tempHBox = new HBox();
                    tempHBox.getChildren().addAll(picLabel, nameLabel, ntfPicLabel, ntfCountLabel);

                    temp.add(tempHBox);
                } 
                catch (FileNotFoundException ex) 
                {
                    Logger.getLogger(chatPageController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        onlineData = FXCollections.observableArrayList(temp);
        
        onlineListView.setEditable(true);
        onlineListView.setItems(onlineData);
    }
    
    void loadChatList(String clientName)
    {   
        if( userSelected.getText().equals("NO USER SELECTED") )
        {
            return;
        }
        if( clientName.equals(selectedClient) )
        {
            int i;
            for( i=0; i<=clientMain.messagesHistory.size()-1; i++ )
            {
                if( clientMain.messagesHistory.get(i).getName().equals(selectedClient) )
                {
                    break;
                }
            }
            clientMain.messagesHistory.get(i).deleteUnreadCount();
            ArrayList<messageData> chatThread = clientMain.messagesHistory.get(i).getChatHistory();

            ArrayList<VBox> tempList = new ArrayList<VBox>();
            for( i=0; i<=chatThread.size()-1; i++ )
            {
                messageData data = chatThread.get(i);

                Label name = new Label( data.getName()+":" );
                name.setFont(new Font("Arial Black", 13));
                
                Label message = new Label();
                if( data.getMessage().startsWith("Emoji: ") )
                {
                    try 
                    {
                        ImageView graphic = new ImageView(new Image(new FileInputStream("src\\client\\Pictures\\Emojis\\"+data.getMessage().substring(7)+".jpg")));
                        graphic.setFitHeight(35);
                        graphic.setFitWidth(35);
                        message.setGraphic(graphic);
                    } 
                    catch (FileNotFoundException ex) 
                    {
                        Logger.getLogger(chatPageController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                else if( data.getMessage().startsWith("Picture: ") )
                {
                    try 
                    {
                        ImageView graphic = new ImageView(new Image(new FileInputStream("PicsReceived\\"+data.getMessage().substring(9))) );
                        graphic.setFitHeight(300);
                        graphic.setFitWidth(300);
                        message.setGraphic(graphic);
                    } 
                    catch (FileNotFoundException ex) 
                    {
                        Logger.getLogger(chatPageController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                else if( data.getMessage().startsWith("PictureSent: "))
                {
                    try 
                    {
                        ImageView graphic = new ImageView(new Image(new FileInputStream("PicsSent\\"+data.getMessage().substring(13))) );
                        graphic.setFitHeight(300);
                        graphic.setFitWidth(300);
                        message.setGraphic(graphic);
                    } 
                    catch (FileNotFoundException ex) 
                    {
                        Logger.getLogger(chatPageController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                else
                {
                    message.setText( data.getMessage() );
                    message.setFont(new Font("System", 13));
                }
                

                VBox temp = new VBox( name, message );

                if( data.getName().equals(main.name) ) 
                {
                    temp.setAlignment(Pos.CENTER_RIGHT);
                }
                tempList.add(temp);
            }

            ObservableList messages = FXCollections.observableArrayList(tempList);
            chatList.setItems(messages);
            
            loadOnlineListView();
        }
        else
        {
            ArrayList<VBox> tempList = new ArrayList<VBox>();
            ObservableList messages = FXCollections.observableArrayList(tempList);
            chatList.setItems(messages);
        }
    }
    
    @FXML
    private void sendAction()
    {
        if( selectedClient==null )
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("Please Select A User First");
            alert.showAndWait();
            return;
        }
        
        if( messageField.getText() == null || messageField.getText().equals("") )
        {
            return;
        }
  
        if( selectedClient.startsWith("GroupChat : ") )
        {
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
                    nu.write("Client");
                    nu.write( selectedClient );
                    nu.write( clientMain.name );
                    nu.write( messageField.getText() );
                    nu.closeConnection();
                }
                catch (IOException ex) 
                {
                    Logger.getLogger(chatPageController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            messageField.setText("");
        }
        else
        {
            int i;
            for( i=0; i<=clientMain.clientsOnline.size()-1; i++ )
            {
                if( clientMain.clientsOnline.get(i).getName().equals(selectedClient) )
                {
                    break;
                }
            }
            if( i > clientMain.clientsOnline.size()-1 )
            {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ERROR");
                alert.setHeaderText(selectedClient+" Has Gone Offline");
                alert.showAndWait();
                loadChatList( "NOT"+selectedClient );
                userSelected.setText("NO USER SELECTED");
                selectUserLabel.setVisible(true);
                messageField.setText("");
            }
            else
            {
                try 
                {
                    // sending message
                    Socket socket = new Socket( clientMain.clientsOnline.get(i).getAddress(), clientMain.clientsOnline.get(i).getPort() );
                    NetworkUtil nu = new NetworkUtil(socket);
                    nu.write("Client");
                    nu.write( main.name );
                    nu.write( messageField.getText() );

                    // updating GUI message thread of my window
                    int totalMessageThreads = clientMain.messagesHistory.size();
                    for( int j=0; j<=totalMessageThreads-1; j++ )
                    {
                        if( clientMain.messagesHistory.get(j).getName().equals(selectedClient) )
                        {
                            clientMain.messagesHistory.get(j).getChatHistory().add(new messageData(main.name, messageField.getText()) );
                        }
                    }
                    messageField.setText("");

                    loadChatList(selectedClient);
                    nu.closeConnection();
                } 
                catch (IOException ex) 
                {
                    Logger.getLogger(chatPageController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    @FXML
    private void createGroupAction()
    {
        AddMembersWindow window = new AddMembersWindow();
        window.display(main);
    }
    
    @FXML
    void logOutAction()
    {
        try 
        {
            Socket server = new Socket( main.serverIP, 9999);
            NetworkUtil server_nu = new NetworkUtil(server);
            server_nu.write("logoutRequest");
            server_nu.write(main.name);
            clientListenThread.runThread = false;
            server_nu.closeConnection();
            System.exit(0);
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(chatPageController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML 
    private void loadUserThread(MouseEvent mouseEvent)
    {
        if( clientMain.clientsOnline.size() <= 1 )
        {
            return;
        }
        
        if(mouseEvent.getButton().equals(MouseButton.PRIMARY))
        {
            if(mouseEvent.getClickCount() == 2)
            {
                ObservableList<HBox> names;
                names = onlineListView.getSelectionModel().getSelectedItems();
                if( names.size()!=0 )
                {
                    HBox temp = names.get(0);
                    Label nameLabel = (Label) temp.getChildren().get(1);
                    selectedClient = nameLabel.getText();
                    selectUserLabel.setVisible(false);
                }
                else
                {
                    return;
                }

                if( selectedClient.startsWith("GroupChat : ") )
                {
                    userSelected.setText( selectedClient.substring(12) );
                }
                else
                {
                    userSelected.setText(selectedClient);
                }
                loadChatList(selectedClient);
            }
        }
    }
    
    @FXML
    private void emojiButtonAction()
    {
        if( selectedClient==null )
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("Please Select A User First");
            alert.showAndWait();
            return;
        }
        
        EmojiWindow window = new EmojiWindow();
        String selectedEmoji = window.display(main);
        
        if( selectedEmoji != null )
        {
            if( selectedClient.startsWith("GroupChat : ") )
            {
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
                        nu.write("Client");
                        nu.write( selectedClient );
                        nu.write( clientMain.name );
                        nu.write( "Emoji: "+selectedEmoji );
                        nu.closeConnection();
                    }
                    catch (IOException ex) 
                    {
                        Logger.getLogger(chatPageController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                messageField.setText("");
            }
            else
            {
                int i;
                for( i=0; i<=clientMain.clientsOnline.size()-1; i++ )
                {
                    if( clientMain.clientsOnline.get(i).getName().equals(selectedClient) )
                    {
                        break;
                    }
                }
                if( i > clientMain.clientsOnline.size()-1 )
                {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("ERROR");
                    alert.setHeaderText(selectedClient+" Has Gone Offline");
                    alert.showAndWait();
                    loadChatList( "NOT"+selectedClient );
                    userSelected.setText("NO USER SELECTED");
                    selectUserLabel.setVisible(true);
                    messageField.setText("");
                }
                else
                {
                    try 
                    {
                        // sending message
                        Socket socket = new Socket( clientMain.clientsOnline.get(i).getAddress(), clientMain.clientsOnline.get(i).getPort() );
                        NetworkUtil nu = new NetworkUtil(socket);
                        nu.write("Client");
                        nu.write( main.name );
                        nu.write( "Emoji: "+selectedEmoji );

                        // updating GUI message thread of my window
                        int totalMessageThreads = clientMain.messagesHistory.size();
                        for( int j=0; j<=totalMessageThreads-1; j++ )
                        {
                            if( clientMain.messagesHistory.get(j).getName().equals(selectedClient) )
                            {
                                clientMain.messagesHistory.get(j).getChatHistory().add(new messageData(main.name, "Emoji: "+selectedEmoji) );
                            }
                        }
                        messageField.setText("");

                        loadChatList(selectedClient);
                        nu.closeConnection();
                    } 
                    catch (IOException ex) 
                    {
                        Logger.getLogger(chatPageController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }
    
    void fileReceivedAlert(String fileName, String senderName)        // added NTS17
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("File Received");
        alert.setHeaderText("File Received From "+ senderName);
        alert.setContentText("Name Of Received File: "+ fileName);
        alert.showAndWait();
    }
    
    void voiceReceivedAlert(String name)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Voice Message");
        alert.setHeaderText("Received A Voice Message From: "+name);
        //alert.showAndWait();
        ButtonType play = new ButtonType("Play");
        alert.getButtonTypes().setAll(play);
        
        Optional<ButtonType> result=alert.showAndWait();
        
        Thread thr = new Thread()
        {
            @Override
            public void run()
            {
                if( result.isPresent() )
                {
                    if(result.get() == play)
                    {
                        AudioInputStream audioInputStream=null;
                        try 
                        {
                            File file=new File("VoiceMessage\\VoiceMessage_"+clientListenThread.voiceMessageNumber+".wav");
                            audioInputStream = AudioSystem.getAudioInputStream(new File("VoiceMessage\\VoiceMessage_"+clientListenThread.voiceMessageNumber+".wav").getAbsoluteFile());

                            //finding the duration
                            AudioFormat format = audioInputStream.getFormat();
                            long audioFileLength = file.length();
                            int frameSize = format.getFrameSize();
                            float frameRate = format.getFrameRate();
                            float durationInSeconds = (audioFileLength / (frameSize * frameRate));

                            //playing audio
                            Clip clip = AudioSystem.getClip();
                            clip.open(audioInputStream);
                            clip.start();
                            int durationInt=(int) durationInSeconds;
                            Thread.sleep(durationInt*1000+1000);
                        } 
                        catch (UnsupportedAudioFileException ex) 
                        {
                            Logger.getLogger(chatPageController.class.getName()).log(Level.SEVERE, null, ex);
                        } 
                        catch (IOException ex) 
                        {
                            Logger.getLogger(chatPageController.class.getName()).log(Level.SEVERE, null, ex);
                        } 
                        catch (LineUnavailableException ex) 
                        {
                            Logger.getLogger(chatPageController.class.getName()).log(Level.SEVERE, null, ex);
                        } 
                        catch (InterruptedException ex) 
                        {
                            Logger.getLogger(chatPageController.class.getName()).log(Level.SEVERE, null, ex);
                        } 
                    }
                }
            }
        };
        thr.start();
    }
    
    void setMain(clientMain main)
    {
        this.main = main;
    }
}
