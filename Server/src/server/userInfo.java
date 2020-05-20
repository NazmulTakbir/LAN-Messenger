package server;

public class userInfo 
{
    private String username;
    private String password;

    public userInfo(String username, String password) 
    {
        this.username = username;
        this.password = password;
    }

    public String getUsername() 
    {
        return username;
    }

    public String getPassword() 
    {
        return password;
    }
    
    public void setPassword(String password)
    {
        this.password = password;
    }
}