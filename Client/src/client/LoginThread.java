package client;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;

public class LoginThread implements Runnable 
{
    private Thread thr;
    private String name, password;
    
    private static clientMain main;
    private static clientLoginPageController LoginController;
    private NetworkUtil server_nu;
    
    LoginThread(String name, String password)
    {
        this.name = name;
        this.password = password;
        thr = new Thread(this);
        thr.start();
    }
    
    @Override
    public void run() 
    {
        try 
        {
            Socket server = new Socket( main.serverIP, 9999);
            
            server_nu = new NetworkUtil(server);
            server_nu.write("loginRequest");
            server_nu.write(name);
            server_nu.write(password);
            
            String verdict = (String) server_nu.read();
            
            Platform.runLater(new Runnable() 
            {
                @Override
                public void run() 
                {
                    if( verdict.equals("Yes") )
                    {
                        try 
                        {
                            main.showChatPage();
                        } 
                        catch (IOException ex) 
                        {
                            Logger.getLogger(LoginThread.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    else if( verdict.equals("No|No Match") )
                    {
                        LoginController.loginfail("Please Enter Valid Username/Password Combination");
                    }
                    else if( verdict.equals("No|Already Logged In") )
                    {
                        LoginController.loginfail("You Are Already Logged In");
                    }
                }
            });  
            
            if( verdict.equals("Yes") )
            {
                main.name = name;
                startListenThread();
            }
            
            server_nu.closeConnection();
        } 
        catch (UnknownHostException | ConnectException ex) 
        {
            Platform.runLater(new Runnable()
            {
                @Override
                public void run() 
                {
                    LoginController.loginfail("Server Is Offline");
                }
            });
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(LoginThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    // This starts the server of the client. Other clients can send
    // data directly to this client's server
    private void startListenThread()
    {
        int i = 10000, port;
        while(true)
        {
            try 
            {
                InetAddress add = InetAddress.getByName(main.ownIP);
                
                ServerSocket serverSocket = new ServerSocket(i, 128, add);
                
                // Print the IP Address and Port Number of client server
                System.out.println(serverSocket.getLocalSocketAddress());
                
                new clientListenThread(serverSocket);
                port = i;
                break;
            } 
            catch (IOException ex) 
            {
                i++;
            }
        }
        notifyCentralServer(port);
    }
    
    private void notifyCentralServer(int port)
    {
        server_nu.write(name);
        server_nu.write( ( new Integer(port) ).toString() );
        server_nu.write( main.ownIP );
    }
    
    public static void setController(clientLoginPageController c)
    {
        LoginController = c;
    }
    public static void setMain(clientMain m) 
    {
        main = m;
    }
}
