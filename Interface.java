import javax.swing.*;
import java.awt.*;

class buttonPanel extends JPanel
{

  private JButton mailButton,addressButton,sendButton,cancelButton,insertaddButton;

   public buttonPanel()
   {

      mailButton = new JButton("Mail");
      addressButton = new JButton("Address");
      sendButton = new JButton("Send");
      cancelButton = new JButton("Cancel");
      insertaddButton = new JButton("Insert Address");

      add(mailButton);
      add(addressButton);
      add(sendButton);
      add(cancelButton);
      add(insertaddButton);

   }
}

class MyFrame extends JFrame
{
  public MyFrame()
  {
    setTitle("Swing 2");
    setBounds(0, 0, 400, 200);

    Container contentPane = getContentPane();
    contentPane.add(new buttonPanel());
  }
}

public class Interface
{
  public static void main(String[] args)
  {
    JFrame MyFrame = new MyFrame();
    MyFrame.show();
  }
}



