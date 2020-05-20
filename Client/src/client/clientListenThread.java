package client;

/* The clientListenThread handles any request which wants to connect to 
   the server of the client. Each connection request is handled by another 
   new thread named : clientServeThread. 
*/

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class clientListenThread implements Runnable
{
    private Thread thr;
    private ServerSocket serverSocket;
    
    static boolean runThread;
    
    static int voiceMessageNumber=0;
    
    clientListenThread(ServerSocket serverSocket)
    {
        runThread = true;
        this.serverSocket = serverSocket;
        thr = new Thread(this);
        thr.start();
    }

    @Override
    public void run() 
    {
        while( runThread )
        {
            try 
            {
                Socket socket = serverSocket.accept();
                new clientServeThread(socket, this);
            } 
            catch (IOException ex) 
            {
                Logger.getLogger(clientListenThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try 
        {
            serverSocket.close();
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(clientListenThread.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }    
}
