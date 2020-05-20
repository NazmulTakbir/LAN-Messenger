package client;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;

public class SignupThread implements Runnable
{
    private Thread thr;
    private String name,password;
    
    private static clientMain main;
    private static clientSignupPageController signupController;
    private NetworkUtil server_nu;
    
    public SignupThread(String name,String password)
    {
        this.name=name;
        this.password=password;
        thr=new Thread(this);
        thr.start();
    }
    
    @Override
    public void run() 
    {
        try 
        {
            Socket server = new Socket( main.serverIP, 9999);
            server_nu= new NetworkUtil(server);
            server_nu.write("signupRequest");
            server_nu.write(name);
            server_nu.write(password);
            
            String existState = (String)server_nu.read();
            
            Platform.runLater(new Runnable() 
            {
                @Override
                public void run() 
                {
                    if(existState.equals("exists"))
                    {
                        signupController.signupFail();
                    }
                    else if(existState.equals("notExists"))
                    {
                        try 
                        {
                            signupController.signupSuccess();
                        } 
                        catch (IOException ex) 
                        {
                            Logger.getLogger(SignupThread.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            });
            
            server_nu.closeConnection();
        } 
        catch (UnknownHostException | ConnectException ex) 
        {
            Platform.runLater(new Runnable()
            {
                @Override
                public void run() 
                {
                    signupController.signupFail2();
                }
            });
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(SignupThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    public static void setController(clientSignupPageController c)
    {
        signupController = c;
    }
    public static void setMain(clientMain m) 
    {
        main = m;
    }   
}