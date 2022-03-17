// 11. Help.java


import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class Help extends JFrame
{
	public Help()
	{
		setTitle("PCS System Implementation");
		setSize(100,200);

		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
		}});

		Container content = getContentPane();
		content.add(new helppanel());
	}

   public static void main(String[] args)
	{
		Help h = new Help();
		h.show();
    }
}