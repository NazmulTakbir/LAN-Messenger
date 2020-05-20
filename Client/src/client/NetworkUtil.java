package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class NetworkUtil 
{
    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    public NetworkUtil(String s, int port) 
    {
        try 
        {
            this.socket = new Socket(s, port);
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
        } 
        catch (Exception e) 
        {
            System.out.println("In NetworkUtil : " + e.toString());
            System.exit(0);
        }
    }

    public NetworkUtil(Socket s) 
    {
        try 
        {
            this.socket = s;
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
        } 
        catch (Exception e) 
        {
            System.out.println("In NetworkUtil : " + e.toString());
            System.exit(0);
        }
    }

    public Object read() 
    {
        Object o = null;
        try 
        {
            o = ois.readUnshared(); // preferrable over readObject
        } 
        catch (Exception e) 
        {
            //System.out.println("Reading Error in network : " + e.toString());
        }
        return o;
    }

    public void write(Object o) 
    {
        try 
        {
            oos.writeUnshared(o); // preferrable over writeObject
        } 
        catch (IOException e) 
        {
            System.out.println("Writing  Error in network : " + e.toString());
        }
    }

    public void closeConnection() 
    {
        try 
        {
            ois.close();
            oos.close();
        } catch (Exception e) 
        {
            System.out.println("Closing Error in network : " + e.toString());
        }
    }
}

