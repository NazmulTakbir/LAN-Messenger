package client;

import java.net.InetAddress;

public class onlineInfo 
{
    private InetAddress address;
    private String name;
    private Integer port;
    private byte[] imageBytes;
    
    public onlineInfo(InetAddress address, String name, Integer port, byte[] imageBytes) 
    {
        this.address = address;
        this.name = name;
        this.port = port;
        this.imageBytes = imageBytes;
    }

    public InetAddress getAddress() 
    {
        return address;
    }

    public String getName() 
    {
        return name;
    }

    public Integer getPort() 
    {
        return port;
    }
    
    public byte[] getImageBytes()
    {
        return imageBytes;
    }
}