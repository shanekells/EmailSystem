import java.awt.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import javax.mail.*;
import javax.mail.event.*;
import javax.mail.internet.*;
import javax.activation.*;           //import the necessary classes
import java.io.*;
import javax.swing.text.html.HTMLEditorKit.ParserCallback;
import javax.swing.text.html.HTMLEditorKit.*;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;
import javax.swing.table.*;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import org.xml.sax.*;

//EmailSys class
public class EmailSys extends JFrame implements WindowListener
{

   JTable FolderTable, AddressTable;
   JTabbedPane jtp;
   JComboBox  addresses = new JComboBox();
   JTextArea bodyTxt = new JTextArea("   ");
   String username, password, newContent;
   JTextArea bodyTxtArea = new JTextArea("  ");
   JTextField toTxtField = new JTextField();
   JTextField subjectTxtField = new JTextField();
   File file ;                          //declare the necessary global objects
   Vector fileNames = new Vector();
   Vector filePaths = new Vector();
   Vector contactNames = new Vector();
   int noofmsgs , contactCount, avoid , exist, noofattach, mail2;
   String host = new String("");

   public EmailSys()
   {
      setTitle("Email");  //set the attributes of the frame
      setBounds(150, 150 , 390, 530);

      Container contents = getContentPane();
      jtp = new JTabbedPane();   //initialise the tabbed pane

      jtp.add("Authentication", new authenticationPanel()); //add panel to tab

      jtp.setEnabledAt(0,true); //set tab enabled

      addWindowListener(this); //add listener

      contents.add(jtp);  //add tab to content pane
      setVisible(true);
   }

   public void windowActivated(WindowEvent e) {}
   public void windowClosed(WindowEvent e) { }      //window listener methods
   public void windowClosing(WindowEvent e) { System.exit(0);}
   public void windowDeactivated(WindowEvent e) {}
   public void windowDeiconified(WindowEvent e) {}
   public void windowIconified(WindowEvent e) {}
   public void windowOpened(WindowEvent e) {}


            //authentication panel
   class authenticationPanel extends JPanel  implements ActionListener
   {
      private JButton enterButton , quitButton; //declare necessary object
      JTextField usernameTxtField = new JTextField();
      JPasswordField passwordTxtField = new JPasswordField();

      public authenticationPanel()
      {
         JLabel usernameLabel = new JLabel("      Username:"); //initialise labels
         JLabel passwordLabel = new JLabel("      Password:");

         usernameTxtField.setColumns(20);
         passwordTxtField.setColumns(20); //set the columns of the text fields

         add(usernameLabel);
         add(usernameTxtField); //add to panel
         add(passwordLabel);
         add(passwordTxtField);
                                //declare buttons and add listener to buttons
         enterButton = new JButton("Enter",new ImageIcon("enter.jpg"));
         enterButton.addActionListener(this);

         quitButton = new JButton("Exit",new ImageIcon("exit.jpg"));
         quitButton.addActionListener(this);

         add(enterButton); //add buttons
         add(quitButton);
      }


      public void actionPerformed(ActionEvent e)
      {
         String selected, Exit , Enter , empty;

         Exit  = new String("Exit");  //declare strings
         Enter  = new String("Enter");
         empty = new String("");

         selected = e.getActionCommand();
                    //get username and password from textfields
         username = new String(usernameTxtField.getText());
         password = new String(passwordTxtField.getPassword());

         if(selected.equals(Enter)) //test to see if enter was clicked
         {
            mail2=1;
            contactCount=0;  //initialise variables
            avoid=0;
            exist=0;
            noofattach=0;
                                     //if textfields are empty
            if(passwordTxtField.getPassword().equals(empty)||usernameTxtField.getText().equals(empty))
            {       //show message
               JOptionPane.showMessageDialog(null,"Both username and password must be entered","Email",JOptionPane.INFORMATION_MESSAGE);
            }
            else
            {
               jtp.removeTabAt(0);  //remove tab
               jtp.add("Inbox", new InboxPanel()); //add panel

               if(exist==1)//if connected
               {             //add panel
                  jtp.add("Contacts", new ContactPanel());
                  readcontactsfromfile(); //get contacts from file
                  jtp.add(new SendPanel(),1); ////add panel
                  jtp.setTitleAt(1,"Send");//set title

                  toTxtField.setText(" "); //clear text field
               }
               else
               {//if not connected then try mail server 2
                  mail2=0;
                  jtp.removeTabAt(0); //remove tab
                  jtp.add("Inbox", new InboxPanel()); //add panel

                  if(exist==1) //if connected
                  {            //add panel
                     jtp.add("Contacts", new ContactPanel());
                     readcontactsfromfile(); //get contacts
                     jtp.add(new SendPanel(),1); //add panel
                     jtp.setTitleAt(1,"Send");

                     toTxtField.setText(" ");
                  }
               }

               if(exist==0)
               {
                  jtp.removeTabAt(0); //remove tab and add panel
                  jtp.add("Authentication", new authenticationPanel());
                            //show message
                  JOptionPane.showMessageDialog(null,"Invalid username and password. Please re-enter details.","Email",JOptionPane.INFORMATION_MESSAGE);
               }
               exist=0;
            }
         }
         else if(selected.equals(Exit))
         {
            System.exit(0);
         }
      }


      public void readcontactsfromfile()
      {

         final int fields=3;

         // parse the XML file
         Parse handler = new Parse();
         SAXParserFactory factory = SAXParserFactory.newInstance();

         try
         {          //declare objects
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse("contacts.xml", handler);
         }
         catch (Throwable t){t.printStackTrace();}

         // retrieve data from parser
         Vector contactsVector = handler.getContacts();

         // this code splits contactsVector into a separate vector for each contact
         int count = contactsVector.size()/fields;
         contactCount=count;  //set contact count

         for(int i=0;i<count;i++)  //loop through contacts
         {
            Vector contact = new Vector(fields);

            for(int j=0;j<fields;j++)
            {
               if(j==1)
               {
                  String FullName = new String(contactsVector.elementAt(0)+" "+contactsVector.elementAt(1));
                  addresses.addItem(FullName); //add to combo box
                  contactNames.addElement(FullName); //add to vector
               }    //add to address table
               AddressTable.setValueAt(contactsVector.elementAt(0),i,j);
               contact.addElement(contactsVector.remove(0));
            }
         }

      }
   }
                     //inbox panel
   class InboxPanel extends JPanel  implements ActionListener , ConnectionListener
   {
      private JButton deleteButton,signoutButton,readButton,refreshButton;
                                //declare buttons and variables
      String msginfo,heading,msgNo,from,subject;
      int port;
      Integer MsgNo;
      Vector columnNames = new Vector();

      public InboxPanel()
      {

         msgNo = new String("Message No");
         from = new String("From");              //initialise variables
         subject = new String("Subject");
                             //initialise buttons and add listeners
         deleteButton = new JButton("Delete", new ImageIcon("deleteimage.gif"));
         deleteButton.addActionListener(this);
         refreshButton = new JButton("Refresh", new ImageIcon("refresh.gif"));
         refreshButton.addActionListener(this);
         signoutButton = new JButton("Signout",new ImageIcon("signoutimage.gif"));
         signoutButton.addActionListener(this);
         readButton = new JButton("Read Email",new ImageIcon("read.jpg"));
         readButton.addActionListener(this);

         add(deleteButton);
         add(signoutButton);
         add(readButton);        //add buttons
         add(refreshButton);

         columnNames.add(msgNo);
         columnNames.add(from);   //add to column names
         columnNames.add(subject);


         if(mail2 == 1)   //if mail2 = 1
         {              //try mail2 server
            host = new String("mail2.lyitnet.lyit.ie");
         }
         else
         {       //try mail1 server
            host = new String("mail1.lyitnet.lyit.ie");
         }

         port=110; //set port
                           //declare property object and initialise
         Properties props = new Properties();
         props.put("mail.smtp.host", host);  //declare session and apply properties
         Session session = Session.getDefaultInstance(props,null);

         try
         {
            Store store = session.getStore("pop3"); //create store with POP3

            store.addConnectionListener(this); //add connection listener

            store.connect(host, port, username, password); //connect to store

            Folder folder = store.getFolder("INBOX"); //get folder

            folder.open(Folder.READ_ONLY); //open as read only

            Message message[] = folder.getMessages(); //get messages

            noofmsgs=message.length; //get no of messages
                         //declare model and use to declare table
            DefaultTableModel model = new DefaultTableModel(columnNames,noofmsgs);
            FolderTable = new JTable(model);
            FolderTable.setShowVerticalLines(false); //dont show vertical lines

            int i=noofmsgs-1;

            for(int j=0; j<noofmsgs; j++)
            {             //loop through and add message attrinbutes to table
               MsgNo = new Integer(j+1);
               FolderTable.setValueAt(MsgNo,j,0);
               FolderTable.setValueAt(message[i].getFrom()[0],j,1);
               FolderTable.setValueAt(message[i].getSubject(),j,2);
               i=i-1;
            }
                          //set viewing size
            FolderTable.setPreferredScrollableViewportSize(new Dimension(360,320));
            JScrollPane scrollPane = new JScrollPane(FolderTable); //add to scrollpane
            add(scrollPane); //add scroll pane

            folder.close(false); //close folder
            store.close();  //close store
           }catch (MessagingException E){}
      }

      public void closed(ConnectionEvent e) {}   //store listener methods
      public void opened(ConnectionEvent e) {exist=1;}
      public void disconnected(ConnectionEvent e) {}

      public void actionPerformed(ActionEvent e)
      {

         String selected, Signout , Read , Delete , Refresh;
         int port=0;

         Signout  = new String("Signout");
         Read  = new String("Read Email");
         Delete  = new String("Delete");
         Refresh = new String("Refresh");

         selected = e.getActionCommand();

         if(selected.equals(Signout))
         {
            writecontactstofile(); //write contacts to xml file
            jtp.removeTabAt(2);
            jtp.removeTabAt(1);
            jtp.removeTabAt(0);
            jtp.add("Authentication", new authenticationPanel());
         }
         else if(selected.equals(Refresh))
         {
            jtp.removeTabAt(0);
            jtp.add(new InboxPanel(),0);
            jtp.setTitleAt(0,"Inbox");
         }
         else if(selected.equals(Read))
         {
            if(FolderTable.getSelectedRow() == -1) //if they didn't select an email
            {                //then show message
               JOptionPane.showMessageDialog(null,"No email selected","Email",JOptionPane.INFORMATION_MESSAGE);
            }
            else
            {
               jtp.add("Email", new ReadMsgPanel());
               jtp.setSelectedIndex(3);
            }
         }
         else if(selected.equals(Delete))
         {                //get selected message
            int EmailSelectedToDelete = FolderTable.getSelectedRow();

            EmailSelectedToDelete=EmailSelectedToDelete+1;
            EmailSelectedToDelete=noofmsgs-EmailSelectedToDelete;

            if(EmailSelectedToDelete == -1)
            {
               JOptionPane.showMessageDialog(null,"No email selected","Email",JOptionPane.INFORMATION_MESSAGE);
            }
            else
            {              //get confirmation
               int ans = JOptionPane.showConfirmDialog(null,"Are you sure you want to delete the Email?", "Email", JOptionPane.YES_NO_OPTION);

               if(ans==0)
               {
				   int RowSelected=FolderTable.getSelectedRow();
				      //delete message from table
                  ((DefaultTableModel)FolderTable.getModel()).removeRow(RowSelected);
                  FolderTable.revalidate(); //refresh table
               }

               port=110;

               Properties props = new Properties();
               props.put("mail.smtp.host", "mail2.lyitnet.lyit.ie");

               Session session = Session.getDefaultInstance(props,null);

               try
               {
                  Store store = session.getStore("pop3");

                  store.connect(host, port, username, password);

                  Folder folder = store.getFolder("INBOX");

                  folder.open(Folder.READ_WRITE);

                  Message message[] = folder.getMessages();
                            //set message flag as deleted
                  message[EmailSelectedToDelete].setFlag(Flags.Flag.DELETED,true);

                  folder.close(true); //close folder and set expunge to true
                  store.close();   //which deletes all messages with deleted flag

                  jtp.removeTabAt(0);
			      jtp.add(new InboxPanel(),0);
                  jtp.setTitleAt(0,"Inbox");

               }catch(MessagingException E){}
            }
         }
      }
   }
                       //send panel
   class SendPanel extends JPanel  implements ActionListener , TransportListener
   {
      private JButton addattachmentButton,sendButton, signoutButton;

      public SendPanel()
      {
         JLabel toLabel, fromLabel, subjectLabel, attachLabel, insertLabel;

         toLabel = new JLabel("            To:           ");
         subjectLabel = new JLabel("      Subject:          ");
         attachLabel = new JLabel("       Attachments: ");
         insertLabel = new JLabel("          Select Email Address:");

         addresses.addActionListener(this);

         addattachmentButton = new JButton("Add Attachment",new ImageIcon("add1.jpg"));
         addattachmentButton.addActionListener(this);
         sendButton = new JButton("Send",new ImageIcon("send.jpg"));
         sendButton.addActionListener(this);
         signoutButton = new JButton("Signout",new ImageIcon("signoutimage.gif"));
         signoutButton.addActionListener(this);

         toTxtField.setColumns(20);
         subjectTxtField.setColumns(20);

         add(addattachmentButton);
         add(sendButton);
         add(signoutButton);

         if(contactCount != 0)   //add combo box if there is contacts
         {
            add(insertLabel);
            add(addresses);
         }

         add(toLabel);
         add(toTxtField);
         add(subjectLabel);
         add(subjectTxtField);
         add(attachLabel);

         for(int i=0; i<noofattach; i++)
         {                   //add attachment to screen
            JLabel attachmentsLabel = new JLabel(fileNames.elementAt(i).toString(),new ImageIcon("attachment.gif"),0);
            add(attachmentsLabel);
         }

         bodyTxtArea.setLineWrap(true);
         bodyTxtArea.setRows(18);          //add text area
         bodyTxtArea.setColumns(27);
         add(bodyTxtArea,BorderLayout.NORTH);

      }

      public void messageDelivered(TransportEvent e2) {System.out.println("message delivered");}
      public void messageNotDelivered(TransportEvent e2) {System.out.println("message not delivered");}
      public void messagePartiallyDelivered(TransportEvent e2) {}

      public void actionPerformed(ActionEvent e)
      {

         String selected, Send , AddAttachment , InsertAddress , domain , fromField ,signout;
         int skip=0;

         Send  = new String("Send");
         AddAttachment  = new String("Add Attachment");
         InsertAddress  = new String("Insert Address");
         domain = new String("@lyit.ie");
         signout = new String("Signout");

         selected = e.getActionCommand();

         if(selected.equals(Send))
         {
            String fileAttachment , empty;

            empty = new String(" ");
                        //check if to address is entered
            if(toTxtField.getText().equals(empty))
            {
               JOptionPane.showMessageDialog(null,"Address of the recepient must be entered!","Send",JOptionPane.INFORMATION_MESSAGE);
            }
            else
            {

               if(noofattach == 0) //if there is no attachments to add to message
               {
                  skip=1; //the skip
               }
                             //set from field
               fromField = new String(username + domain);

               Properties props = new Properties();
               props.put("mail.smtp.host", host);

               Session session = Session.getDefaultInstance(props,null);
               Message msg = new MimeMessage(session); //create message object

               try
               {             //create address object
                  InternetAddress addressFrom = new InternetAddress(fromField);
                     //set from
                  msg.setFrom(addressFrom);
                              //set address object
                  InternetAddress addressTo = new InternetAddress(toTxtField.getText());
                           //set recipient
                  msg.addRecipient(Message.RecipientType.TO, addressTo);
                               //set subject
                  msg.setSubject(subjectTxtField.getText());

                  if(skip==0) //if there is attachments
                  {          //create message body part object
                     MimeBodyPart messageBodyPart = new MimeBodyPart();
                              //set the text of the object
                     messageBodyPart.setText(bodyTxtArea.getText());
                             //create multipart and add body
                     MimeMultipart multipart = new MimeMultipart();
                     multipart.addBodyPart(messageBodyPart);

                     for(int j=0; j<noofattach; j++)
                     {       //get file path
                        fileAttachment = new String(filePaths.elementAt(j).toString());
                                 //initialise body part
                        messageBodyPart = new MimeBodyPart();
                        //get data source
                        DataSource source = new FileDataSource(fileAttachment);
                                   //set handler
                        messageBodyPart.setDataHandler(new DataHandler(source));
                                 //set file name
                        messageBodyPart.setFileName(fileNames.elementAt(j).toString());
                                  //add to multipart
                        multipart.addBodyPart(messageBodyPart);
                     }
                     msg.setContent(multipart); //add multipart to message
                  }
                  else
                  {            //else set content to body
                     msg.setContent(bodyTxtArea.getText(),"text/plain");
                  }
                          //create transport object using smtp protocol
                  Transport transport = session.getTransport("smtp");
                  transport.connect(host, username, password); //connect
                  transport.send(msg); //send message
                  transport.close();//close transport

               }catch (MessagingException E){}

               noofattach=0;
               jtp.removeTabAt(1);
               jtp.add(new SendPanel(),1);
               jtp.setTitleAt(1,"Send");
               toTxtField.setText("   ");
               subjectTxtField.setText("   ");
               bodyTxtArea.setText("  ");
            }
         }
         else if(selected.equals(AddAttachment))
         {
            JFrame frame = new JFrame(); //create frame
            JFileChooser fc = new JFileChooser(); //get file chooser

            int returnValue = fc.showOpenDialog(frame); //get file using file chooser

            if(returnValue == JFileChooser.APPROVE_OPTION) //if file chosen
            {
               File file = fc.getSelectedFile();  //get file
               fileNames.add(file.getName()); //add to vector
               filePaths.add(file.getPath());
               noofattach++; //increment attachment count
               jtp.removeTabAt(1);  //refresh send panel to add attachment
               jtp.add(new SendPanel(),1);
               jtp.setTitleAt(1,"Send");
            }
         }
         else if(selected.equals(signout))
         {
            writecontactstofile();
            jtp.removeTabAt(2);
            jtp.removeTabAt(1);
            jtp.removeTabAt(0);
            jtp.add("Authentication", new authenticationPanel());
         }
         else
         {
            String name = (String)addresses.getSelectedItem();
                    //get selected combo box item
            StringTokenizer stringnames = new StringTokenizer(name);
            String firstname = stringnames.nextToken();//break down to first name
               //and last name using string tokenizer
            String surname = stringnames.nextToken();

            for(int i=0; i<contactCount; i++)
            {                 //check if name ie equal to any in address table
               if(firstname.equals(AddressTable.getValueAt(i,1).toString()))
               {
                  if(surname.equals(AddressTable.getValueAt(i,2).toString()))
                  {       //if so then add email address to to field
                     toTxtField.setText(AddressTable.getValueAt(i,0).toString());
                  }
               }
            }

         }
      }
   }
                //contact panel
   class ContactPanel extends JPanel  implements ActionListener
   {
      private JButton deleteButton, addButton , signoutButton , editButton;

      public ContactPanel()
      {

         Vector columnnames = new Vector();
         String firstname = new String("First Name");
         String surname = new String("Surname");
         String emailaddress = new String("Email Address");

         columnnames.add(emailaddress);
         columnnames.add(firstname);
         columnnames.add(surname);

         deleteButton = new JButton("Delete",new ImageIcon("deleteimage.gif"));
         deleteButton.addActionListener(this);
         signoutButton = new JButton("Signout",new ImageIcon("signoutimage.gif"));
         signoutButton.addActionListener(this);
         addButton = new JButton("Add Contact", new ImageIcon("add1.jpg"));
         addButton.addActionListener(this);
         editButton = new JButton("Edit",new ImageIcon("edit.jpg"));
         editButton.addActionListener(this);


         add(deleteButton);
         add(signoutButton);
         add(addButton);
         add(editButton);

         DefaultTableModel model2 = new DefaultTableModel(columnnames,20);
         AddressTable = new JTable(model2);
                   //create table for contacts
         AddressTable.setPreferredScrollableViewportSize(new Dimension(320,240));
         AddressTable.setShowVerticalLines(false);

         JScrollPane scrollPane2 = new JScrollPane(AddressTable);

         scrollPane2.createVerticalScrollBar();
         add(scrollPane2);

      }

      public void actionPerformed(ActionEvent e)
      {
         String selected, Signout, Add, Delete, Edit;

         Signout  = new String("Signout");
         Delete  = new String("Delete");
         Add = new String("Add Contact");
         Edit = new String("Edit");

         selected = e.getActionCommand();

         if(selected.equals(Signout))
         {
            writecontactstofile();
            jtp.removeTabAt(2);
            jtp.removeTabAt(1);
            jtp.removeTabAt(0);
            jtp.add("Authentication", new authenticationPanel());
         }
         else if(selected.equals(Add))
         {
            jtp.add("Add Contact", new AddContactPanel());
            jtp.setSelectedIndex(3);
            jtp.setEnabledAt(0,false);
            jtp.setEnabledAt(1,false);
            jtp.setEnabledAt(2,false);
         }
         else if(selected.equals(Edit))
         {
            if(AddressTable.getSelectedRow() == -1)
            {
               JOptionPane.showMessageDialog(null,"No email selected","Email",JOptionPane.INFORMATION_MESSAGE);
            }
            else
            {
               jtp.add("Edit Contact", new EditContactPanel());
               jtp.setSelectedIndex(3);
               jtp.setEnabledAt(0,false);
               jtp.setEnabledAt(1,false);
               jtp.setEnabledAt(2,false);
            }
         }
         else if(selected.equals(Delete))
         {
            int AddressSelectedToDelete=AddressTable.getSelectedRow();

            if(AddressSelectedToDelete == -1)
            {
               JOptionPane.showMessageDialog(null,"No contact selected","Email",JOptionPane.INFORMATION_MESSAGE);
            }
            else
            {
               int ans = JOptionPane.showConfirmDialog(null,"Are you sure you want to delete the contact?", "Email", JOptionPane.YES_NO_OPTION);

               if(ans==0)
               {
                  for(int i=0; i<contactCount; i++)
                  {
                               //delete contact from combo box
                     StringTokenizer stringnames = new StringTokenizer(addresses.getItemAt(i).toString());//from file.
                     String firstname = stringnames.nextToken(); //get first name and last
                     String surname = stringnames.nextToken();    //name from combo box
                                     //delete address from combo box
                     if(firstname.equals(AddressTable.getValueAt(AddressSelectedToDelete,1)))
                     {
                        if(surname.equals(AddressTable.getValueAt(AddressSelectedToDelete,2).toString()))
                        {
                           addresses.removeItemAt(i);
                           i=contactCount; //exit loop
                        }
                     }
                  }
                               //refresh contact table
                  ((DefaultTableModel)AddressTable.getModel()).removeRow(AddressSelectedToDelete);
                  AddressTable.revalidate();

                  jtp.removeTabAt(1);
                  jtp.add(new SendPanel(),1);
                  jtp.setTitleAt(1,"Send");
                  jtp.setSelectedIndex(2);
                  contactCount=contactCount-1; //decrement contact count
               }
            }
         }
      }
   }
                     //add contact panel
   class AddContactPanel extends JPanel  implements ActionListener
   {

      private JButton addButton, cancelButton , signoutButton;
      private JLabel FirstName, Surname , EmailAddress;
      private JTextField fname , sname , emailadd;

      public AddContactPanel()
      {

         addButton = new JButton("Add Contact", new ImageIcon("add1.jpg"));
         addButton.addActionListener(this);
         signoutButton = new JButton("Signout",new ImageIcon("signoutimage.gif"));
         signoutButton.addActionListener(this);
         cancelButton = new JButton("Cancel",new ImageIcon("cancel.jpg"));
         cancelButton.addActionListener(this);

         add(addButton);
         add(signoutButton);
         add(cancelButton);

         FirstName = new JLabel("      First Name: ");
         Surname = new JLabel("      Surname: ");
         EmailAddress = new JLabel("       Email: ");

         fname = new JTextField();
         sname = new JTextField();
         emailadd = new JTextField();

         fname.setColumns(20);
         sname.setColumns(22);
         emailadd.setColumns(20);

         add(FirstName);
         add(fname);
         add(Surname);
         add(sname);
         add(EmailAddress);
         add(emailadd);
      }

      public void actionPerformed(ActionEvent e)
      {
         String selected, Signout, Add, Cancel , empty;

         Signout  = new String("Signout");
         Cancel  = new String("Cancel");
         Add = new String("Add Contact");
         empty = new String("");

         selected = e.getActionCommand();

         if(selected.equals(Signout))
         {
            writecontactstofile();
            jtp.removeTabAt(3);
            jtp.removeTabAt(2);
            jtp.removeTabAt(1);
            jtp.removeTabAt(0);
            jtp.add("Authentication", new authenticationPanel());
         }
         else if(selected.equals(Add))
         {
            if(fname.getText().equals(empty)||sname.getText().equals(empty)||emailadd.getText().equals(empty))
            {
               JOptionPane.showMessageDialog(null,"All information must be entered","Contacts",JOptionPane.INFORMATION_MESSAGE);
            }
            else
            {
               String fullname = new String(fname.getText()+" "+sname.getText());
               addresses.addItem(fullname);      //add contact to combo box
               AddressTable.setValueAt(fname.getText(),contactCount,1);//add to table
               AddressTable.setValueAt(sname.getText(),contactCount,2);
               AddressTable.setValueAt(emailadd.getText(),contactCount,0);
               AddressTable.revalidate();  //refresh table
               contactCount++;  //increment contact count
               jtp.removeTabAt(3);
               jtp.setEnabledAt(0,true);
               jtp.setEnabledAt(1,true);
               jtp.setEnabledAt(2,true);
               jtp.removeTabAt(1);
               jtp.add(new SendPanel(),1);
               jtp.setTitleAt(1,"Send");
               jtp.setSelectedIndex(2);
            }
         }
         else if(selected.equals(Cancel))
         {
            jtp.removeTabAt(3);
            jtp.setEnabledAt(0,true);
            jtp.setEnabledAt(1,true);
            jtp.setEnabledAt(2,true);
         }
      }
   }
                  //edit contact panel
   class EditContactPanel extends JPanel  implements ActionListener
   {

      private JButton editButton, cancelButton , signoutButton;
      private JLabel FirstName, Surname , EmailAddress;
      private JTextField fname , sname , emailadd;

      public EditContactPanel()
      {

         editButton = new JButton("Edit Contact",new ImageIcon("edit.jpg"));
         editButton.addActionListener(this);
         signoutButton = new JButton("Signout",new ImageIcon("signoutimage.gif"));
         signoutButton.addActionListener(this);
         cancelButton = new JButton("Cancel",new ImageIcon("cancel.jpg"));
         cancelButton.addActionListener(this);

         add(editButton);
         add(signoutButton);
         add(cancelButton);

         FirstName = new JLabel("        First Name: ");
         Surname = new JLabel("       Surname: ");
         EmailAddress = new JLabel("       Email: ");

         int ContactSelected = AddressTable.getSelectedRow();
                             //put contact details to edit in text fields
         fname = new JTextField(AddressTable.getValueAt(ContactSelected,1).toString());
         sname = new JTextField(AddressTable.getValueAt(ContactSelected,2).toString());
         emailadd = new JTextField(AddressTable.getValueAt(ContactSelected,0).toString());

         fname.setColumns(20);
         sname.setColumns(22);
         emailadd.setColumns(20);

         add(FirstName);
         add(fname);
         add(Surname);
         add(sname);
         add(EmailAddress);
         add(emailadd);
      }

      public void actionPerformed(ActionEvent e)
      {
         String selected, Signout, Edit, Cancel , empty;

         Signout  = new String("Signout");
         Cancel  = new String("Cancel");
         Edit = new String("Edit Contact");
         empty = new String("");

         selected = e.getActionCommand();

         if(selected.equals(Signout))
         {
            writecontactstofile();
            jtp.removeTabAt(3);
            jtp.removeTabAt(2);
            jtp.removeTabAt(1);
            jtp.removeTabAt(0);
            jtp.add("Authentication", new authenticationPanel());
         }
         else if(selected.equals(Edit))
         {
            if(fname.getText().equals(empty)||sname.getText().equals(empty)||emailadd.getText().equals(empty))
            {
               JOptionPane.showMessageDialog(null,"All information must be entered","Contacts",JOptionPane.INFORMATION_MESSAGE);
            }
            else
            {
               int AddressSelectedToEdit= AddressTable.getSelectedRow();

               for(int i=0; i<contactCount; i++)
               {
                  StringTokenizer stringnames = new StringTokenizer(addresses.getItemAt(i).toString());//from file.
                  String firstname = stringnames.nextToken();
                  String surname = stringnames.nextToken();
                               //get contact in combo box and delete
                  if(firstname.equals(AddressTable.getValueAt(AddressSelectedToEdit,1)))
                  {
                     if(surname.equals(AddressTable.getValueAt(AddressSelectedToEdit,2).toString()))
                     {
                        addresses.removeItemAt(i);
                        i=contactCount;
                     }
                  }
               }

               String fullname = new String(fname.getText()+" "+sname.getText());

               addresses.addItem(fullname);  //add new edit version of contact
                         //edit table
               AddressTable.setValueAt(fname.getText(),AddressSelectedToEdit,1);
               AddressTable.setValueAt(sname.getText(),AddressSelectedToEdit,2);
               AddressTable.setValueAt(emailadd.getText(),AddressSelectedToEdit,0);
               AddressTable.revalidate();
               jtp.removeTabAt(3);
               jtp.setEnabledAt(0,true);
               jtp.setEnabledAt(1,true);
               jtp.setEnabledAt(2,true);
               jtp.removeTabAt(1);
               jtp.add(new SendPanel(),1);
               jtp.setTitleAt(1,"Send");
               jtp.setSelectedIndex(2);
            }
         }
         else if(selected.equals(Cancel))
         {
            jtp.removeTabAt(3);
            jtp.setEnabledAt(0,true);
            jtp.setEnabledAt(1,true);
            jtp.setEnabledAt(2,true);
         }
      }
   }

                  //read message panel
   class ReadMsgPanel extends JPanel  implements ActionListener
   {
      private JButton returnButton , signoutButton;
      Vector attachNames = new Vector();
      int port,attach=0,nooffiles=0;        //declare objects and variables
      BufferedInputStream readfrominput;
      FileOutputStream filetowrite;
      BufferedOutputStream writetofile;

      public ReadMsgPanel()
      {

         String from , subject , body;
         JLabel fromLabel , subjectLabel , attachLabel;
         int EmailSelectedToRead=FolderTable.getSelectedRow();

         jtp.setEnabledAt(0,false);
         jtp.setEnabledAt(1,false);
         jtp.setEnabledAt(2,false);

         EmailSelectedToRead=EmailSelectedToRead+1;
         EmailSelectedToRead=noofmsgs-EmailSelectedToRead;

         returnButton = new JButton("Return",new ImageIcon("return.jpg"));
         returnButton.addActionListener(this);

         signoutButton = new JButton("Signout",new ImageIcon("signoutimage.gif"));
         signoutButton.addActionListener(this);

         add(returnButton);
         add(signoutButton);

         port=110;

         Properties props = new Properties();
         props.put("mail.smtp.host", host);

         Session session = Session.getDefaultInstance(props,null);

         body = new String(" ");

         try
         {
            Store store = session.getStore("pop3");

            store.connect(host, port, username, password);

            Folder folder = store.getFolder("INBOX");

            folder.open(Folder.READ_ONLY);

            Message message[] = folder.getMessages();

            from = new String(" ");
            subject = new String(" ");
                                            //check if message has attachments
            if(message[EmailSelectedToRead].isMimeType("multipart/*"))
            {
                           //if so get multipart object
               Multipart mpreceived = (Multipart)message[EmailSelectedToRead].getContent();
                             //get attributes
               from = new String(message[EmailSelectedToRead].getFrom()[0].toString());
               subject = new String(message[EmailSelectedToRead].getSubject());
                              //get no of parts in multipart
               int noofparts = mpreceived.getCount();

               for(int j=0; j<noofparts; j++)   //loop through parts
               {                //get body apart
                  Part p = mpreceived.getBodyPart(j);
                              //get type and disposition
                  String ptype = p.getContentType();
                  String disp = p.getDisposition();

                  String attachment = new String("attachment");
                                  //check if attachment
                  if (disp != null && disp.equalsIgnoreCase(attachment))
                  {         //if so get file name
                      attachNames.add(p.getFileName());
                                   //use data handler to handle the file
                      DataHandler dh = p.getDataHandler();
                                   //create file for data
                      File file = new File(p.getFileName().toString());
                                          //declare output stream
                      filetowrite = new FileOutputStream(file);
                            //declare buffered output stream
                      BufferedOutputStream writetofile = new BufferedOutputStream(filetowrite);
                               //get input from file
                      InputStream attachInput = dh.getInputStream();
                                        //get buffered input stream
                      readfrominput = new BufferedInputStream(attachInput);

                      int writeinput;
                               //get input
                      while ((writeinput = readfrominput.read()) != -1)
                      {       //and write to file
                         writetofile.write(writeinput);
                      }

                      attach=1;

                      writetofile.flush();//flush and streams
                      writetofile.close();
                      readfrominput.close();
                      nooffiles++;
                  }

                  if(p.isMimeType("text/*") && attach!= 1 ) //if it is normal text then
                  {          //add to body
                     body = new String(p.getContent().toString());
                  }
               }
            }
            else  //else just normal email
            {
                     //get attributes
               from = new String(message[EmailSelectedToRead].getFrom()[0].toString());
               subject = new String(message[EmailSelectedToRead].getSubject());

               try
               {             //get body

                  body = new String(message[EmailSelectedToRead].getContent().toString());
               }catch (IOException E){}
            }

            fromLabel = new JLabel("From:            " +from);
            subjectLabel = new JLabel("Subject:       "+subject);
            attachLabel = new JLabel("Attachments:");

            bodyTxt.setEditable(false);
            bodyTxt.setLineWrap(true);
            bodyTxt.setRows(20);
            bodyTxt.setColumns(33);

            String parsedTxt = new String(" ");

            parsedTxt = parseString(body);
            //parse string

            bodyTxt.setText(parsedTxt);

            JScrollPane scrolltxtArea = new JScrollPane(bodyTxt);

            add(fromLabel);
            add(subjectLabel);
            add(scrolltxtArea);
            add(attachLabel);

            for(int k=0; k<nooffiles; k++)
            {   //add labels for files
               JLabel filenameLabel = new JLabel(attachNames.elementAt(k).toString(),new ImageIcon("attachment.gif"),0);
               add(filenameLabel);
            }

            folder.close(false);
            store.close();

         }catch(MessagingException E){}
         catch(IOException E2){}
      }
                   //function to parse string
      public String parseString(String content)
      throws MessagingException
      {

         newContent = new String("   ");
                       //create parser
         HTMLEditorKit.ParserCallback callback = new HTMLEditorKit.ParserCallback()
         {          //get text
            public void handleText(char[] data, int pos)
            {          //add to string
                String strdata = new String(data);
                newContent = newContent + strdata;
                newContent = newContent + "  "; //space between each word
            }
         };

         try
         {         //read content
            Reader reader = new StringReader(content);  //parse
            new ParserDelegator().parse(reader, callback, true);
         }catch(FileNotFoundException e)
         {
            System.out.println("file not found");
         }
         catch(IOException e2)
         {
            System.out.println("io error");
         }

         return newContent;
      }


      public void actionPerformed(ActionEvent e)
      {

         String selected, Signout , Return;

         Signout  = new String("Signout");
         Return  = new String("Return");

         selected = e.getActionCommand();

         if(selected.equals(Signout))
         {
            writecontactstofile();
            jtp.removeTabAt(3);
            jtp.removeTabAt(2);
            jtp.removeTabAt(1);
            jtp.removeTabAt(0);
            jtp.add("Authentication", new authenticationPanel());
         }
         else if(selected.equals(Return))
         {
            bodyTxt.setText("   ");
            jtp.setEnabledAt(0,true);
            jtp.setEnabledAt(1,true);
            jtp.setEnabledAt(2,true);
            jtp.removeTabAt(3);
            jtp.setSelectedIndex(0);
         }
      }
   }
               //write contacts to file function
   public void writecontactstofile()
   {
      String xmltxt = new String("<?xml version=\"1.0\"?><!DOCTYPE booklist SYSTEM \"contacts.dtd\"><contacts>");
      String xmlend = new String("</contacts>");      //declare string to right to file
      String xmlcode1 = new String("<contact><email>");
      String xmlcode2 = new String("</email><fname>");    //xml content
      String xmlcode3 = new String("</fname><sname>");
      String xmlcode4 = new String("</sname></contact>");

      try
      {           //get file writer
         BufferedWriter out = new BufferedWriter(new FileWriter("contacts.xml"));

         if(contactCount==0)
         {

         }
         else
         {
            out.write(xmltxt);  //write xml text
            for(int i=0; i<contactCount; i++)//for each contact
            {

               out.write(xmlcode1);   //write xml code and add contacts details
               out.write(AddressTable.getValueAt(i,0).toString());
               out.write(xmlcode2);
               out.write(AddressTable.getValueAt(i,1).toString());
               out.write(xmlcode3);
               out.write(AddressTable.getValueAt(i,2).toString());
               out.write(xmlcode4);
            }
            out.write(xmlend);

         }
         out.close();
      }
      catch(IOException e){}
      catch(NullPointerException e2) {System.out.println("");}
   }

   public static void main(String args[])
   {
      EmailSys st = new EmailSys();
   }
}
          //parse xml file function
class Parse extends DefaultHandler
{
   private boolean inemail=false;
   private int tagCount=0;        //declare variables
   private Vector contactsVector = new Vector();
   final static int fields=3;

   public Vector getContacts() //get contact function
   {
      return(contactsVector); //return vector
   }
                       //parse functions
   public void startDocument() throws SAXException  {}
   public void endDocument() throws SAXException {}
           //if start of element
   public void startElement(String namespaceURI,String sName,
                            String qName,Attributes attrs) throws SAXException
   {
      if(qName.equals("email"))
      {   //if email
         inemail=true; //set tags
	 tagCount=0;
      }
   }
                //if characters
   public void characters(char [] buf, int offset, int len)
   {                    //declare string
      String s = new String(buf,offset,len);
      if(inemail) //if email
      {             //add data to vector
	 contactsVector.addElement(s);
         tagCount++; //increment count
	 if(tagCount==fields)  //stop loop
	 {
	    inemail=false;//set tags
	    tagCount=0;
	 }
      }
   }
                   //end element function.
   public void endElement(String namespaceURI,String sName,String qName) throws SAXException {}
}