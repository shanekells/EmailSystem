import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Properties;
import javax.mail.*;
import javax.mail.event.*;
import javax.mail.internet.*;
import java.io.*;

public class ShowFolder3
{

   class authenticationPanel2 extends JPanel  implements ActionListener
   {
      private JButton enterButton;
      JTextField usernameTxtField = new JTextField();
      JPasswordField passwordTxtField = new JPasswordField();

      String username, password;


      public authenticationPanel2()
      {
         JLabel usernameLabel = new JLabel("Username:");
         JLabel passwordLabel = new JLabel("Password:");

         usernameTxtField.setColumns(20);
         passwordTxtField.setColumns(20);

         add(usernameLabel);
         add(usernameTxtField);
         add(passwordLabel);
         add(passwordTxtField);

         enterButton = new JButton("Enter");
         enterButton.addActionListener(this);

         add(enterButton);
      }

      public void actionPerformed(ActionEvent e)
      {
         String selected, Enter;

         username = new String(usernameTxtField.getText());
         password = new String(passwordTxtField.getPassword());

         //System.out.println(username + " " + password);

         Enter = new String("Enter");

         selected = e.getActionCommand();

         if(selected.equals(Enter))
         {
            try
            {
               enterEmailAcc(username,password);
            }catch (MessagingException E){}
         }
      }

      public void enterEmailAcc(String username, String password)
      throws MessagingException
      {
         String host,msginfo,heading;
         int port,noofmsgs;
         Integer MsgNo;
         //Object[][] data = {{"  ","   ","   "},{"  ","   ","   "},{"   ","   ","   "},{"  ","   ","   "},{"   ","   ","   "},{"  ","   ","   "},{"   ","   ","   "},{"   ","   ","   "},{"   ","   ","   "},{"   ","   ","   "},{"   ","   ","   "},{"  ","   ","   "},{"  ","   ","   "} };

         //String[] columnNames = { "Message No.", "From", "To" };

         JLabel msgNoLabel = new JLabel("Message No.");
         JLabel fromNoLabel = new JLabel("From");
         JLabel subjectNoLabel = new JLabel("Subject");

         host = new String("mail2.lyitnet.lyit.ie");
         port=110;

         heading = new String("Folder For " + username);

         Properties props = new Properties();
         props.put("mail.smtp.host", "mail2.lyitnet.lyit.ie");

         Session session = Session.getDefaultInstance(props,null);

         Store store = session.getStore("pop3");

         store.connect(host, port, username, password);

         Folder folder = store.getFolder("INBOX");

         folder.open(Folder.READ_ONLY);

         Message message[] = folder.getMessages();

         noofmsgs=message.length;

         JFrame showFolder = new JFrame(heading);
         showFolder.setBounds(300,300,600,300);

         JTable FolderTable = new JTable(noofmsgs,3);

         for(int j=0; j<noofmsgs; j++)
         {
            MsgNo = new Integer(j+1);
            FolderTable.setValueAt(MsgNo,j,0);
            FolderTable.setValueAt(message[j].getFrom()[0],j,1);
            FolderTable.setValueAt(message[j].getSubject(),j,2);
         }

         for(int i=0; i<noofmsgs; i++)
         {
            System.out.println(i + ": " + message[i].getFrom()[0]  + "\t" + message[i].getSubject());
            try
            {
               System.out.println("  " + message[i].getContent());
            }catch (IOException E){}

         }
         showFolder.getContentPane().add(FolderTable);

         showFolder.show();

         folder.close(false);
         store.close();

      }
   }

   class MyFrame extends JFrame implements WindowListener
   {
      public MyFrame()
      {
         setTitle("Authentication");
         setBounds(300, 300, 300, 250);

         Container contentPane = getContentPane();
         contentPane.add(new authenticationPanel2());

         addWindowListener(this);
      }

      public void windowActivated(WindowEvent e) {}
      public void windowClosed(WindowEvent e) { }
      public void windowClosing(WindowEvent e) { System.exit(0);}
      public void windowDeactivated(WindowEvent e) {}
      public void windowDeiconified(WindowEvent e) {}
      public void windowIconified(WindowEvent e) {}
      public void windowOpened(WindowEvent e) {}
   }

   public ShowFolder3()
   {
      MyFrame aFrame = new MyFrame();
      aFrame.show();
   }

   public static void main(String[] args)
   {
      ShowFolder3 showfolder = new ShowFolder3();
   }
}
