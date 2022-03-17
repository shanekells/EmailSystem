import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Properties;
import javax.mail.*;
import javax.mail.event.*;
import javax.mail.internet.*;
import javax.activation.*;


public class SendInterface
{
    JTextField toTxtField = new JTextField();
    JTextField fromTxtField = new JTextField();
    JTextField subjectTxtField = new JTextField();
    JTextField attachTxtField = new JTextField();

    JTextArea bodyTxtArea = new JTextArea("  ");

    class buttonPanel extends JPanel  implements ActionListener , TransportListener
    {
     private JButton mailButton,addressButton,sendButton,cancelButton,insertaddButton;

     public buttonPanel()
     {

        mailButton = new JButton("Mail");
        addressButton = new JButton("Address");
        sendButton = new JButton("Send");
        cancelButton = new JButton("Cancel");
        insertaddButton = new JButton("Insert Address");

        setLayout(new GridLayout(2,3));

        sendButton.addActionListener(this);

        add(mailButton);
        add(addressButton);
        add(sendButton);
        add(cancelButton);
        add(insertaddButton);

     }

     public void actionPerformed(ActionEvent e)
     {
        String selected, Send;

        Send  = new String("Send");

        selected = e.getActionCommand();

        if(selected.equals(Send))
        {
            try
            {
               sendmail();
            }catch (MessagingException E){}
        }
     }

     public void sendmail()
     throws MessagingException
     {
        String host , username , password , from , fileAttachment;

        host = new String("pop.mail.yahoo.com");
        username = new String("99398168");
        password = new String("JUSF");

        from = new String(fromTxtField.getText());
        fileAttachment = new String(attachTxtField.getText());

        Properties props = new Properties();
        props.put("mail.smtp.host", "mail2.lyitnet.lyit.ie");

        Session session = Session.getDefaultInstance(props,null);
        Message msg = new MimeMessage(session);

        InternetAddress addressFrom = new InternetAddress(from);

        msg.setFrom(addressFrom);

        InternetAddress addressTo = new InternetAddress(toTxtField.getText());

        msg.addRecipient(Message.RecipientType.TO, addressTo);

        msg.setSubject(subjectTxtField.getText());

           MimeBodyPart messageBodyPart = new MimeBodyPart();

           messageBodyPart.setText(bodyTxtArea.getText());

           MimeMultipart multipart = new MimeMultipart();
           multipart.addBodyPart(messageBodyPart);

           messageBodyPart = new MimeBodyPart();
           DataSource source = new FileDataSource(fileAttachment);
           messageBodyPart.setDataHandler(new DataHandler(source));
           messageBodyPart.setFileName(fileAttachment);
           multipart.addBodyPart(messageBodyPart);

           msg.setContent(multipart);


        Transport transport = session.getTransport("smtp");
        transport.addTransportListener(this);
        transport.connect(host, username, password);
        transport.send(msg);
        transport.close();


     }

      public void messageDelivered(TransportEvent e)
      {
         System.out.println("Message Delivered!!!!!!!!!!");
         JOptionPane.showMessageDialog(null,"The email has been sent","Email",JOptionPane.INFORMATION_MESSAGE);
      }
      public void messageNotDelivered(TransportEvent e)
      {
         JOptionPane.showMessageDialog(null,"The email has not been sent","Email",JOptionPane.INFORMATION_MESSAGE);
      }
      public void messagePartiallyDelivered(TransportEvent e)
      {
         JOptionPane.showMessageDialog(null,"The email has been partially sent","Email",JOptionPane.INFORMATION_MESSAGE);
      }
    }

    class txtfieldPanel extends JPanel
    {
     public txtfieldPanel()
     {
       Dimension txtField = new Dimension(2,2);

       JLabel toLabel = new JLabel("To:");
       JLabel fromLabel = new JLabel("From:");
       JLabel subjectLabel = new JLabel("Subject:");
       JLabel attachLabel = new JLabel("Attachments:");

       toTxtField.setColumns(5);
       fromTxtField.setColumns(5);
       subjectTxtField.setColumns(5);
       attachTxtField.setColumns(5);

       toTxtField.setMaximumSize(txtField);
       fromTxtField.setMaximumSize(txtField);
       subjectTxtField.setMaximumSize(txtField);
       attachTxtField.setMaximumSize(txtField);

       setLayout(new GridLayout(4,2));

       add(toLabel);
       add(toTxtField);
       add(fromLabel);
       add(fromTxtField);
       add(subjectLabel);
       add(subjectTxtField);
       add(attachLabel);
       add(attachTxtField);
     }
    }

    class bodyPanel extends JPanel
    {
     public bodyPanel()
     {
        bodyTxtArea.setLineWrap(true);
        bodyTxtArea.setRows(10);
        bodyTxtArea.setColumns(30);

        add(bodyTxtArea,BorderLayout.NORTH);
     }
    }

    class MyFrame extends JFrame implements WindowListener
    {
     public MyFrame()
     {
        setTitle("Send");
        setBounds(300, 300 , 400, 400);

        Container contentPane = getContentPane();
        contentPane.add(new buttonPanel(),BorderLayout.NORTH);
        contentPane.add(new txtfieldPanel(),BorderLayout.CENTER);
        contentPane.add(new bodyPanel(),BorderLayout.SOUTH);

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

    public SendInterface()
    {
        MyFrame aFrame = new MyFrame();
        aFrame.show();
    }

    public static void main(String[] args)
    {
        SendInterface sendinterface = new SendInterface();
    }
}
