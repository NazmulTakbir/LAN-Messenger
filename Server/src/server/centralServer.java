package server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class centralServer
{
    ArrayList<userInfo> usersInfo = new ArrayList<userInfo>();
    ArrayList<onlineInfo> clientsOnline = new ArrayList<onlineInfo>();
    private ServerSocket serverSocket;
    private String IP;
    
    centralServer()
    {
        // Starts the server in the LAN
        // Command Prompt -> ipconfig -> IPv4 Address
        System.out.print("Enter IP Address: ");
        Scanner snr = new Scanner(System.in);
        IP = snr.nextLine();
        
        loadUserInfo();
        
        try 
        { 
            InetAddress add = InetAddress.getByName(IP);
            serverSocket = new ServerSocket(9999, 128, add);
            
            // Print out IP address and Port Number of the Server
            System.out.println(serverSocket.getLocalSocketAddress());
            
            // This loop keeps looking for any new client trying to connect
            // A client connect requiest is handled using another thread
            while(true)
            {
                Socket clientSocket = serverSocket.accept();
                new serveThread(clientSocket, this);
            }
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(centralServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void main(String[] args) 
    {
        new centralServer();
    }
    
    // Loads the ID and Password of all registered users into an ArrayList
    private void loadUserInfo()
    {
        File file = new File("usersInfo.txt");    
        try 
        {
            Scanner snr = new Scanner(file);
            while( snr.hasNext() )
            {
                String name = snr.nextLine();
                String password = snr.nextLine();
                usersInfo.add( new userInfo(name, password) );
            }
        } 
        catch (FileNotFoundException ex) 
        {
            System.out.println("usersInfo.txt File Not Found");
        }
    }
}