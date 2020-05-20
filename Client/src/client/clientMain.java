package client;

import java.io.IOException;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class clientMain extends Application
{
    Stage stage;
    static String name;
    static String serverIP, ownIP;
    
    static ArrayList<onlineInfo> clientsOnline = new ArrayList<onlineInfo>();
    static ArrayList<messageHistory> messagesHistory = new ArrayList<messageHistory>(); 
    static ArrayList<group> groups = new ArrayList<group>();
    
    @Override
    public void start(Stage primaryStage) throws IOException 
    {
        stage = primaryStage;
        LoginThread.setMain(this);
        showIPPage();
    }
    
    public void showIPPage() throws IOException
    {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("IPAddressWindow.fxml"));
        Parent root = loader.load();
        
        IPAddressWindowController controller = loader.getController();
        controller.setMain(this);

        root.addEventHandler(KeyEvent.KEY_PRESSED, ev -> {
            if (ev.getCode() == KeyCode.ENTER) 
            {
               controller.submitButton.fire();
               ev.consume(); 
            }
        });
        
        stage.setTitle("Enter Server IP");
        
        Scene scene = new Scene(root, 400, 300);
        stage.setScene(scene);
        
        stage.show();
    }
    
    public void showLoginPage() throws IOException
    {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("clientLoginPage.fxml"));
        Parent root = loader.load();
        
        clientLoginPageController controller = loader.getController();
        controller.setMain(this);

        root.addEventHandler(KeyEvent.KEY_PRESSED, ev -> {
            if (ev.getCode() == KeyCode.ENTER) 
            {
               controller.loginButton.fire();
               ev.consume(); 
            }
        });
        
        stage.setTitle("Login");
        
        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().clear();
        scene.getStylesheets().add("client/clientLoginPageStyle.css");
        stage.setScene(scene);
        
        stage.show();
    }
    
    public void showChatPage() throws IOException
    {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("chatPage.fxml"));
        Parent root = loader.load();

        chatPageController controller = loader.getController();
        controller.setMain(this);
        
        this.stage.setOnCloseRequest( e -> controller.logOutAction() );
        
        root.addEventHandler(KeyEvent.KEY_PRESSED, ev -> {
            if (ev.getCode() == KeyCode.ENTER) 
            {
               controller.sendButton.fire();
               ev.consume(); 
            }
        });
        
        stage.setTitle("ChatPage");
        
        Scene scene = new Scene(root, 1000, 600);
        scene.getStylesheets().clear();
        scene.getStylesheets().add("client/ChatPageStyle.css");
        stage.setScene(scene);
        
        stage.show();
    }
    
    public void showSignupPage() throws IOException
    {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("clientSignupPage.fxml"));
        Parent root = loader.load();

        clientSignupPageController controller = loader.getController();
        controller.setMain(this);

        root.addEventHandler(KeyEvent.KEY_PRESSED, ev -> {
            if (ev.getCode() == KeyCode.ENTER) 
            {
               controller.signup.fire();
               ev.consume(); 
            }
        });
        
        stage.setTitle("Signup");
        
        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().clear();
        scene.getStylesheets().add("client/clientLoginPageStyle.css");
        stage.setScene(scene);
        
        stage.show();
    }
    
    public static void main(String[] args) 
    {
        launch(args);
    }
}
