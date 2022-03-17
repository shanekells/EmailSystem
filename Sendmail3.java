import java.awt.*;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.*;

public class Sendmail3
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
      String host , username , password , from;

      host = new String("mail2.lyitnet.lyit.ie");
      username = new String("9939816o");
      password = new String("JUS4");
      from = new String("MclaughlinMartinyit.ie");

      Properties props = new Properties();
      props.put("mail.smtp.host", "mail2.lyitnet.lyit.ie");

      Session session = Session.getDefaultInstance(props,null);

      Message msg = new MimeMessage(session);

      InternetAddress addressFrom = new InternetAddress(from);

      msg.setFrom(addressFrom);

      InternetAddress addressTo = new InternetAddress("L000017=3@lyit.ie");

      msg.addRecipient(Message.RecipientType.TO, addressTo);

      msg.setSubject("First");
      msg.setContent("xxxx", "text/plain");

      Transport transport = session.getTransport("smtp");
      transport.connect(host, username, password);
      transport.send(msg);
      transport.close();
   }
}




