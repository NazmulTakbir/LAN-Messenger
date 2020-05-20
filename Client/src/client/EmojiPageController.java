package client;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class EmojiPageController implements Initializable 
{
    private static EmojiWindow window;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
    
    }    
    
    @FXML
    private void imageAction(MouseEvent mouseEvent)
    {
        if(mouseEvent.getButton().equals(MouseButton.PRIMARY))
        {
            if(mouseEvent.getClickCount() == 2)
            {
                window.selectedEmoji = ( (ImageView) mouseEvent.getSource() ).getId();
                window.stage.close();
            }
        }
    }
    
    @FXML
    private void hoverEnterAction(MouseEvent mouseEvent)
    {
        ImageView image= (ImageView) mouseEvent.getSource();
        image.setFitWidth(100);
        image.setFitHeight(100);
    }
    
    @FXML
    private void hoverExitAction(MouseEvent mouseEvent)
    {
        ImageView image= (ImageView) mouseEvent.getSource();
        image.setFitWidth(90);
        image.setFitHeight(90);
    }
    
    static void setWindow(EmojiWindow w)
    {
        window = w;
    }
}
