import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.util.Vector;

public class xml
{
     public static void main(String[] args)
     {
        String xmltxt = new String("<?xml version=\"1.0\"?><!DOCTYPE booklist SYSTEM \"contacts.dtd\"><contacts>");
        String xmlend = new String("</contacts>");
        String xmlcontact1 = new String("<contact><email>");
        String xmlcontact2 = new String("</email><fname>");
        String xmlcontact3 = new String("</fname><sname>");
        String xmlcontact4 = new String("</sname></contact>");

        Vector cont = new Vector();
        cont.addElement("shane.kelly@lyit.ie");
        cont.addElement("Shane");
        cont.addElement("Kelly");

        //cont.addElement("Shane");
        //cont.addElement("Shane");
        //cont.addElement("Shane");

        try
        {
           BufferedWriter out = new BufferedWriter(new FileWriter("contactsfile.xml"));

           out.write(xmltxt);

           out.write(xmlcontact1);
           out.write(cont.elementAt(0).toString());
           out.write(xmlcontact2);
           out.write(cont.elementAt(1).toString());
           out.write(xmlcontact3);
           out.write(cont.elementAt(2).toString());
           out.write(xmlcontact4);

           out.write(xmlend);

           out.close();
        }
        catch(IOException e){}
        catch(NullPointerException e2) {}

     }
}





