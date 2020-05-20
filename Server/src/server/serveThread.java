package server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

public class serveThread implements Runnable
{
    private Thread thr;
    private NetworkUtil client_nu;
    private centralServer server;
    
    serveThread(Socket clientSocket, centralServer server)
    {
        // The client_nu is a NetworkUtil object which streamlines the
        // communication with the client
        client_nu = new NetworkUtil(clientSocket);
        this.server = server;
        thr = new Thread(this);
        thr.start();
    }
    
    @Override
    public void run() 
    {
        String request = (String) client_nu.read();
        
        if(request.equals("loginRequest"))
        {
            boolean verdict = checkLogin();
            if( verdict )
            {
                receiveAddress();
                notifyAllActive();
            }
        }
        else if(request.equals("signupRequest"))
        {
            createNewAccount();
        }
        else if( request.equals("logoutRequest"))
        {
            String name = (String) client_nu.read();
            for( int i=0; i<=server.clientsOnline.size()-1; i++ )
            {
                if( server.clientsOnline.get(i).getName().equals(name) )
                {
                    server.clientsOnline.remove( server.clientsOnline.get(i) );
                }
            }
            // After a client has gone offline, the ArrayList of clients
            // online is updated and then the other clients are notified
            notifyAllActive();
        }
        else if( request.equals("PasswordChange") )
        {
            String name = (String) client_nu.read();
            String currentPassword = (String) client_nu.read();
            String newPassword = (String) client_nu.read();
            
            int size = server.usersInfo.size();
            for( int i=0; i<=size-1; i++ )
            {
                if( server.usersInfo.get(i).getUsername().equals(name) )
                {
                    if( server.usersInfo.get(i).getPassword().equals(currentPassword) )
                    {
                        server.usersInfo.get(i).setPassword(newPassword);
                        client_nu.write("Password Changed Successfully");
                        rewriteUsersInfoFile();
                    }
                    else
                    {
                        client_nu.write("Wrong Current Password Entered");
                    }
                    break;
                }
            }      
        }
        else if( request.equals("ProfilePicChange") )
        {
            try 
            {
                String name = (String) client_nu.read();
                String fileName = (String) client_nu.read();
                
                deleteProPic(name);
                
                byte[] imageContent = (byte[]) client_nu.read();
                
                int i, length = fileName.length();
                for( i=0; i<=length-1; i++ )
                {
                    if( fileName.charAt(i)=='.' )
                    {
                        break;
                    }
                }
                String extension = fileName.substring(i);
                
                File file = new File("src\\server\\ProfilePics\\"+name+extension);
                Files.write(file.toPath(), imageContent);
                
                int size = server.clientsOnline.size();
                for( i=0; i<=size-1; i++ )
                {
                    if( server.clientsOnline.get(i).getName().equals(name) )
                    {
                        server.clientsOnline.get(i).setImageBytes(imageContent);
                        break;
                    }
                }
                notifyAllActive();
            } 
            catch (IOException ex) 
            {
                Logger.getLogger(centralServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else if( request.equals("ProfilePicRemove") )
        {
            String name = (String) client_nu.read();
            int i, size = server.clientsOnline.size();
            for( i=0; i<=size-1; i++ )
            {
                if( server.clientsOnline.get(i).getName().equals(name) )
                {
                    try 
                    {
                        File file = new File("src\\server\\ProfilePics\\"+"Default.png");
                        byte[] imageContent = Files.readAllBytes( file.toPath() );
                        server.clientsOnline.get(i).setImageBytes(imageContent);
                        
                        deleteProPic(name);
                        notifyAllActive();
                        break;
                    } 
                    catch (IOException ex) 
                    {
                        Logger.getLogger(centralServer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        client_nu.closeConnection();
    }
    
    private void deleteProPic(String name)
    {
        String extension = ".jpg";
        File delFile = new File("src\\server\\ProfilePics\\"+name+extension);
        if( delFile.delete() )
        {
        }
        else
        {
            extension = ".png";
            delFile = new File("src\\server\\ProfilePics\\"+name+extension);
            if( delFile.delete() )
            {
            }
            else
            {
                extension = ".jpeg";
                delFile = new File("src\\server\\ProfilePics\\"+name+extension);
                delFile.delete();
                // .delete() returns false if the file doesn't exist. So no 
                // error is caused even if the file doesn't exist. 
            }
        }
    }
    
    private boolean checkLogin()
    {
        String name = (String) client_nu.read();
        String password = (String) client_nu.read();
        
        // prevent double login of same user
        for( onlineInfo temp : server.clientsOnline )
        {
            if( temp.getName().equals(name) )
            {
                client_nu.write("No|Already Logged In");
                return false;
            }
        }
        
        int i;
        for( i=0; i<=server.usersInfo.size()-1; i++ )
        {
            if( name.equals(server.usersInfo.get(i).getUsername()) && password.equals(server.usersInfo.get(i).getPassword()) )
            {
                client_nu.write("Yes");
                return true;
            }
        }
        if( i==server.usersInfo.size() ) 
        {
            client_nu.write("No|No Match");
            return false;
        }
        return false;
    }
    
    // Each client has his own server. When client 1 sends data to client
    // 2, client 1 sends the data directly to the server of client 2. ( NOT
    // through the central server ). The central server only maintains a 
    // ArrayList of information about the users who are online. The 
    // information includes - IP Address, Port Number, Name, ProfilePic of 
    // each user. 
    private void receiveAddress()
    {   
        String name = (String) client_nu.read();
        Integer port = Integer.parseInt((String) client_nu.read());
        try 
        {
            InetAddress address = InetAddress.getByName( (String) client_nu.read() );
            byte[] imgBytes = getProPic(name);
            server.clientsOnline.add( new onlineInfo( address, name, port, imgBytes) );
        } 
        catch (UnknownHostException ex) 
        {
            Logger.getLogger(centralServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private byte[] getProPic(String name)
    {
        byte[] content = null;
        String extension = ".jpg";
        while( true )
        {
            try 
            {
                File file = new File("src\\server\\ProfilePics\\"+name+extension);
                content = Files.readAllBytes(file.toPath());
                break;
            } 
            catch (IOException ex) 
            {
                if( extension.equals(".jpg") )
                {
                    extension = ".png";
                }
                else if( extension.equals(".png") )
                {
                    extension = ".jpeg";
                }
                else if( extension.equals(".jpeg") )
                {
                    // If image still not found then use the default profile picture
                    try 
                    {
                        File defaultFile = new File("src\\server\\ProfilePics\\"+"Default.png");
                        content = Files.readAllBytes(defaultFile.toPath());
                    } 
                    catch (IOException ex1) 
                    {
                        Logger.getLogger(centralServer.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                    break;
                }
            }
        }
        return content;
    }
    
    // The Central Server sends the ArrayList of information about the 
    // clients online to all the clients who are online - so that the clients
    // can communicate with each other without needing the central server
    private void notifyAllActive()
    {
        for( onlineInfo temp1 : server.clientsOnline )
        {
            try 
            {
                Socket socket = new Socket( temp1.getAddress(), temp1.getPort() );
                NetworkUtil nu = new NetworkUtil(socket);
                nu.write("Central Server");
                nu.write( (new Integer(server.clientsOnline.size())).toString() );
                for( onlineInfo temp2 : server.clientsOnline )
                {
                    nu.write(temp2.getName());
                    nu.write( (temp2.getPort()).toString() );
                    nu.write( temp2.getAddress().getHostAddress() );
                    nu.write( temp2.getImageBytes() );
                }
                nu.closeConnection();
            } 
            catch (IOException ex) 
            {
                Logger.getLogger(centralServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void createNewAccount()
    {
        String name=(String) client_nu.read();
        String password=(String) client_nu.read();
        
        int i;
        for(i=0; i<=server.usersInfo.size()-1; i++)
        {
            if(name.equals(server.usersInfo.get(i).getUsername())){
                client_nu.write("exists");
                return ;
            }
        }
        client_nu.write("notExists");
        server.usersInfo.add(new userInfo(name, password));
        File file=new File("usersInfo.txt");
        if(!file.exists())
        {
            System.out.println("File does not exist");
        }
        PrintWriter pw = null;
        try 
        {
            pw = new PrintWriter(new FileWriter(file,true));
        } 
        catch (FileNotFoundException ex) 
        {
            Logger.getLogger(centralServer.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(centralServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        pw.println(name);
        pw.println(password);
        pw.close(); 
    }
    
    private void rewriteUsersInfoFile()
    {
        File file=new File("usersInfo.txt");
        if(!file.exists())
        {
            System.out.println("File does not exist");
        }
        PrintWriter pw = null;
        try 
        {
            
            pw=new PrintWriter(new FileWriter(file, false));
            
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(centralServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        for(userInfo a:server.usersInfo)
        {
            pw.println(a.getUsername());
            pw.println(a.getPassword());
        }
        pw.close();
    }
}