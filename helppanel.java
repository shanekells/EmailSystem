// 10. helppanel.java
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;


public class helppanel extends JPanel implements ActionListener
{
	public helppanel()
	{
		setLayout(new BorderLayout());

		JButton j = new JButton("Return");
		add(j,BorderLayout.NORTH);

		j.addActionListener(this);
	}

	public void actionPerformed(ActionEvent e)
	{
		JFrame f = new JFrame();
		f.setBounds(0, 0, 200, 200);
		f.show();
	}
}