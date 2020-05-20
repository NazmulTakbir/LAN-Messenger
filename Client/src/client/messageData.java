package client;

/* The messageData is a pair of Message Sending Client's Name 
   and the Message content
*/
public class messageData 
{
    private final String name;
    private String message;

    public messageData(String name, String message) 
    {
        this.name = name;
        this.message = message;
        if( !message.startsWith("Emoji: ") ) formatMessage();
    }

    public String getName() 
    {
        return name;
    }

    public String getMessage() 
    {
        return message;
    }
    
    // one line of message cannot have more than 59 characters
    private void formatMessage()
    {
        int length = message.length();
        int noNewLines = length/60;
        StringBuilder str = new StringBuilder(message);
        
        for( int i=1; i<=noNewLines; i++ )
        {
            // ensures no word is split in different lines
            int backCount = 0;
            while( str.charAt(i*60-backCount) != ' ' )
            {
                backCount++;
            }
            str.replace(i*60-backCount, i*60-backCount+1, "\n");
        }
        message = str.toString();
    }
}
