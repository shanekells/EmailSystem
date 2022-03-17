/**
 * @author  Thomas Devine
 * @program for Shane Kelly (project 2003/4)
**/

import javax.swing.*;
import java.awt.*;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import org.xml.sax.*;
import java.util.Vector;

class Parse extends DefaultHandler
{
   private boolean inemail=false;
   private int tagCount=0;
   private Vector contactsVector = new Vector();
   final static int fields=3;

   public Vector getContacts()
   {
      return(contactsVector);
   }

   public void startDocument() throws SAXException  {}
   public void endDocument() throws SAXException {}

   public void startElement(String namespaceURI,String sName,
                            String qName,Attributes attrs) throws SAXException
   {
      if(qName.equals("email"))
      {
         inemail=true;
	 tagCount=0;
      }
   }

   public void characters(char [] buf, int offset, int len)
   {
      String s = new String(buf,offset,len);
      if(inemail)
      {
         //System.out.println("data="+s);
	 contactsVector.addElement(s);
         tagCount++;
	 if(tagCount==fields)
	 {
	    inemail=false;
	    tagCount=0;
	    //System.out.println("End of Contact");
	 }
      }
   }

   public void endElement(String namespaceURI,String sName,String qName) throws SAXException {}
}

public class ParseContacts extends JFrame
{
   public static void main(String[] args)
   {
      final int fields=3;

      JFrame frame = new JFrame();
      frame.setBounds(0, 0, 400, 200);

      // parse the XML file
      Parse handler = new Parse();
      SAXParserFactory factory = SAXParserFactory.newInstance();
      try{
         SAXParser saxParser = factory.newSAXParser();
	 saxParser.parse("contacts.xml", handler);
      }
      catch (Throwable t){
         t.printStackTrace();
      }

      // retrieve data from parser
      Vector contactsVector = handler.getContacts();
      Vector tableVector = new Vector();

      // this code splits contactsVector into a separate vector for each contact
      int count = contactsVector.size()/fields;
      for(int i=0;i<count;i++)
      {
         Vector contact = new Vector(fields);  // tmp vector
	 for(int j=0;j<fields;j++)
	    contact.addElement(contactsVector.remove(0));
	 // add the contact vector to the tableVector
	 tableVector.addElement(contact);
	 System.out.println(contact);
      }

      // table column headings
      Vector columnNames = new Vector();
      columnNames.addElement("email");
      columnNames.addElement("First Name");
      columnNames.addElement("Last Name");

      // create table using 2 vectors
      JTable table = new JTable(tableVector ,columnNames);

      // put the table in a scroll pane and display it
      Container container=frame.getContentPane();
      JScrollPane scrollPane = new JScrollPane(table);

      //table.setPreferredScrollableViewportSize(new Dimension(500, 70));
      container.add(scrollPane);
      frame.setVisible(true);
   }
}


