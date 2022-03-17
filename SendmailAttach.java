import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

public class SendmailAttach
{
   public static void main(String[] args)
   {
      try
      {
         sendemail();
      }catch (MessagingException E){}
   }

   public static void sendemail()
   throws MessagingException
   {
      String host , username , password , from , fileAttachment;

      host = new String("mail2.lyitnet.lyit.ie");
      username = new String("99398168");
      password = new String("JUSF");
      from = new String("99398168@lyit.ie");
      fileAttachment = new String("Presentation.ppt");

      Properties props = new Properties();
      props.put("mail.smtp.host", "mail2.lyitnet.lyit.ie");

      Session session = Session.getDefaultInstance(props,null);
      Message msg = new MimeMessage(session);

      InternetAddress addressFrom = new InternetAddress(from);

      msg.setFrom(addressFrom);

      InternetAddress addressTo = new InternetAddress("L00001703@lyit.ie");

      msg.addRecipient(Message.RecipientType.TO, addressTo);

      msg.setSubject("First");
      //msg.setContent("Hello", "text/plain");

      MimeBodyPart messageBodyPart = new MimeBodyPart();

      messageBodyPart.setText("See attachment");

      MimeMultipart multipart = new MimeMultipart();
      multipart.addBodyPart(messageBodyPart);

      messageBodyPart = new MimeBodyPart();
      DataSource source = new FileDataSource(fileAttachment);
      messageBodyPart.setDataHandler(new DataHandler(source));
      messageBodyPart.setFileName(fileAttachment);
      multipart.addBodyPart(messageBodyPart);

      msg.setContent(multipart);

      Transport transport = session.getTransport("smtp");
      //transport.addTransportListener(this);
      transport.connect(host, username, password);
      transport.send(msg);
      transport.close();
   }
}




