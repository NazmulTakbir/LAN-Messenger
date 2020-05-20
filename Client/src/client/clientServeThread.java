package client;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;

public class clientServeThread implements Runnable
{
    private Thread thr;
    private Socket socket;
    private static chatPageController chatController;
    private clientListenThread listenThread;
    
    public clientServeThread(Socket socket, clientListenThread listenThread) 
    {
        this.socket = socket;
        this.listenThread = listenThread;
        thr = new Thread(this);
        thr.start();
    }

    @Override
    public void run() 
    {
        NetworkUtil nu = new NetworkUtil(socket);
        String identity = (String) nu.read();
        
        // The Central Server is sending the List of Clients Online
        if( identity.equals("Central Server") ) 
        {
            centralServer(nu);
        }
        else if ( identity.equals("Client") ) 
        {
            listenToClient(nu);
        }
        else if( identity.equals("GroupsInfo") )
        {
            updateGroups(nu);
        }
        else if(identity.equals("File Coming"))
        {
            receiveFile(nu);
        }
        else if(identity.equals("Image Coming"))
        {
            receiveImage(nu);
        }
        else if(identity.equals("Voice Message Coming"))
        {
            listenThread.voiceMessageNumber++;
            receiveVoice(nu);
        }
        
        nu.closeConnection();
    }
    
    private void centralServer(NetworkUtil serverNu)
    {
        // update clientOnline list
        Integer totalClients = Integer.parseInt( (String) serverNu.read() );
        clientMain.clientsOnline.clear();
        for( int i=1; i<=totalClients; i++ )
        {
            String name = (String) serverNu.read();
            Integer port = Integer.parseInt( (String) serverNu.read() ); 
            try 
            {
                InetAddress address = InetAddress.getByName( (String) serverNu.read() );
                byte[] imgContent = ( byte[] ) serverNu.read();
                clientMain.clientsOnline.add(new onlineInfo( address, name, port, imgContent) );
            } 
            catch (UnknownHostException ex) 
            {
                Logger.getLogger(clientListenThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        // update message history threads : For the updated onlinelist,
        // we check if there is a chat thread for each user. If any user
        // doesn't have an associated chat thread, then we add a chat thread
        // for that user. 
        int totalMessageThreads = clientMain.messagesHistory.size();
        for( int i=0; i<=totalClients-1; i++ )
        {
            String name = clientMain.clientsOnline.get(i).getName();
            boolean found = false;
            for( int j=0; j<=totalMessageThreads-1; j++ )
            {
                if( clientMain.messagesHistory.get(j).getName().equals(name) )
                {
                    found = true;
                    break;
                }
            }
            if( !found )
            {
                clientMain.messagesHistory.add( new messageHistory(name) );
                totalMessageThreads++;
            }
        }
        
        // update GUI
        Platform.runLater(new Runnable() 
        {
            @Override
            public void run() 
            {
                chatController.loadProPic();
                chatController.loadOnlineListView();
            }
        }); 
    }
    
    private void listenToClient(NetworkUtil clientNu)
    {
        String threadName = (String) clientNu.read();
        String clientName = threadName;
        if( threadName.startsWith("GroupChat : ") )
        {
            clientName = (String) clientNu.read();
        }
        String message = (String) clientNu.read();
        
        int totalMessageThreads = clientMain.messagesHistory.size();
        for( int j=0; j<=totalMessageThreads-1; j++ )
        {
            if( clientMain.messagesHistory.get(j).getName().equals(threadName) )
            {
                clientMain.messagesHistory.get(j).getChatHistory().add(new messageData(clientName, message) );
                clientMain.messagesHistory.get(j).incrementUnreadCount();
                Platform.runLater(new Runnable() 
                {
                    @Override
                    public void run() 
                    {
                        chatController.loadChatList(threadName);
                        chatController.loadOnlineListView();
                    }
                }); 
            }
        } 
    }
    
    // When a client creates a group, he sends the updated list of groups
    // to all the other clients - which is received here.
    private void updateGroups(NetworkUtil nu)
    {
        int size = Integer.parseInt( (String)nu.read() );
        clientMain.groups.clear();
        for( int i=1; i<=size; i++ )
        {
            clientMain.groups.add( (group) nu.read() );
        }
        
        int totalMessageThreads = clientMain.messagesHistory.size();
        int totalGroups = clientMain.groups.size();
        for( int i=0; i<=totalGroups-1; i++ )
        {
            String name = clientMain.groups.get(i).getName();
            boolean found = false;
            for( int j=0; j<=totalMessageThreads-1; j++ )
            {
                if( clientMain.messagesHistory.get(j).getName().equals(name) )
                {
                    found = true;
                    break;
                }
            }
            if( !found )
            {
                clientMain.messagesHistory.add( new messageHistory(name) );
                totalMessageThreads++;
            }
            // We make sure there is a chat thread for all the groups ( if a client
            // is not part of a group then the chat thread of that group is not
            // in the GUI of that client ) 
        }
        Platform.runLater(new Runnable() 
        {
            @Override
            public void run() 
            {
                chatController.loadOnlineListView();
            }
        }); 
    }
    
    private void receiveFile(NetworkUtil nu)
    {
        try 
        {
            String clientName = (String) nu.read();        
            String fileName=(String) nu.read();
            byte[] content = (byte[]) nu.read();
            
            File file = new File( "FilesReceived\\"+clientMain.name+fileName );        
            Files.write(file.toPath(), content);
            
            Platform.runLater(new Runnable()          
            {
                @Override
                public void run() 
                {
                    chatController.fileReceivedAlert(fileName, clientName);
                }
            }); 
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(clientListenThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void receiveImage(NetworkUtil nu)
    {
        try 
        {
            String threadName = (String) nu.read();    
            String clientName = threadName;
            if( threadName.startsWith("GroupChat : ") )
            {
                clientName = (String) nu.read();
            }            
            String fileName=(String) nu.read();
            byte[] content = (byte[]) nu.read();
            
            File file = new File( "PicsReceived\\" + fileName);        
            Files.write(file.toPath(), content);
            
            int totalMessageThreads = clientMain.messagesHistory.size();
            for( int j=0; j<=totalMessageThreads-1; j++ )
            {
                if( clientMain.messagesHistory.get(j).getName().equals(threadName) )
                {
                    clientMain.messagesHistory.get(j).getChatHistory().add(new messageData(clientName, "Picture: "+fileName) );
                    clientMain.messagesHistory.get(j).incrementUnreadCount();
                    Platform.runLater(new Runnable() 
                    {
                        @Override
                        public void run() 
                        {
                            chatController.loadChatList(threadName);
                            chatController.loadOnlineListView();
                        }
                    }); 
                }
            }
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(clientListenThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void receiveVoice(NetworkUtil nu)
    {
        try 
        {
            String clientName = (String) nu.read();
            String fileName=(String) nu.read();
            byte[] content = (byte[]) nu.read();
            String status=(String) nu.read();
            
            File file = new File( "VoiceMessage\\VoiceMessage_"+clientListenThread.voiceMessageNumber+".wav" );
            Files.write(file.toPath(), content);
            
            Platform.runLater(new Runnable() 
            {
                @Override
                public void run() 
                {
                    chatController.voiceReceivedAlert(clientName);
                }
            });  
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(clientListenThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void setController(chatPageController c)
    {
        chatController = c;
    }
}
