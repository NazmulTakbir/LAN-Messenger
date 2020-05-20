package client;

import java.io.Serializable;
import java.util.ArrayList;

public class group implements Serializable 
{
    private String name;
    private ArrayList<String> members = new ArrayList<String>();

    public String getName() 
    {
        return name;
    }

    public void setName(String name) 
    {
        this.name = name;
    }

    public ArrayList<String> getMembers() 
    {
        return members;
    }
}
