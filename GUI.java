import java.awt.Color;
import java.net.InetAddress;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class GUI {
	//Swing:
	
	private JFrame frame = new JFrame();
	private JPanel basePanel = new JPanel();
	private JPanel connectPanel = new JPanel();
	private JPanel messagePanel = new JPanel();
	private JPanel getPanel = new JPanel();
	private JPanel pinPanel = new JPanel();
	
	private JTextField IPAddress = new JTextField("IP Address");
	private JTextField portNum	= new JTextField("Port Number");
	private JButton connectDisconect = new JButton("Connect");
	
	private JTextArea textArea = new JTextArea("Your Message Here");
	private JButton post = new JButton("Post");
	
	private JButton get = new JButton("Get");
	private JOptionPane getProperties = new JOptionPane();
	
	private JButton pinUnpin = new JButton("Pin");
	private JOptionPane pinCoordiantes = new JOptionPane();
	
	private JTextArea resultArea = new JTextArea("Results Go Here");
	
	private InetAddress IP;
	GUI()
	{
			
		
		resultArea.setBackground(new Color(0xFFFFFF));
		resultArea.setSize(200, 300);
		
		connectPanel.add(IPAddress);
		connectPanel.add(portNum);
		connectPanel.add(connectDisconect);
		messagePanel.add(textArea);
		messagePanel.add(post);
		
		
		basePanel.add(connectPanel);
		basePanel.add(messagePanel);
		basePanel.add(get);
		basePanel.add(pinUnpin);
		basePanel.add(resultArea);
		
		
		frame.add(basePanel);
		frame.validate();
		frame.setVisible(true);
		frame.setSize(400, 400);
		
	}
	
	

}
