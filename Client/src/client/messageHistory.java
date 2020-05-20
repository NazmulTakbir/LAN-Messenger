package client;

/* The Message History stores the messages the client has sent or received
   from another particular client, or from a particular group
*/

import java.util.ArrayList;

public class messageHistory 
{
    private String name;
    private ArrayList<messageData> chatHistory = new ArrayList<messageData>();
    private int unReadCount = 0;
    
    public messageHistory(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }

    public ArrayList<messageData> getChatHistory() 
    {
        return chatHistory;
    }
    
    public int getUnReadCount()
    {
        return unReadCount;
    }
    
    public void deleteUnreadCount()
    {
        this.unReadCount = 0;
    }
    
    public void incrementUnreadCount()
    {
        this.unReadCount++;
    }
}
