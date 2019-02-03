import static javax.swing.JOptionPane.OK_CANCEL_OPTION;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class client {

	public static class GUI {
		// Swing:
		public JFrame frame = new JFrame();
		public JPanel basePanel = new JPanel();
		public JPanel connectPanel = new JPanel();
		public JPanel messagePanel = new JPanel();
		public JPanel buttonPanel = new JPanel();

		public JPanel pinOptionsPanel = new JPanel();

		// public JTextField IPAddress = new JTextField("IP Address");
		public JTextField IPAddress = new JTextField("localhost");
		// public JTextField portNum = new JTextField("Port Number");
		public JTextField portNum = new JTextField("4554");
		public JButton connectDisconect = new JButton("Connect");

		public JTextArea textArea = new JTextArea("message here");
		public JButton post = new JButton("Post");
		public JPanel postOptionsPanel = new JPanel();

		public JButton get = new JButton("Get");
		public JPanel getOptionsPanel = new JPanel();
		public JButton pinUnpin = new JButton("Pin");

		public JTextArea resultArea = new JTextArea();
		public InetAddress IP;
		public int port;
		public Socket socket;
		private String defaultColor;
		BufferedReader in; // server response
		PrintWriter out; // to send data use print writer
		public ArrayList<String> colours = new ArrayList<String>();
		public String color, refersTo;
		public Point2D contains;

		GUI() {

			JLabel colorLabel = new JLabel("Color:");
			JLabel refersToLabel = new JLabel("Contains Message:");
			JLabel yCoordLabel = new JLabel("Y Coordinate:");
			JLabel xCoordLabel = new JLabel("X Coordinate:");
			JTextField colorText = new JTextField();
			JTextField refersToText = new JTextField();
			JTextField yCoord = new JTextField();
			JTextField xCoord = new JTextField();

			JLabel yCoordLabelP = new JLabel("Y Coordinate:");
			JLabel xCoordLabelP = new JLabel("X Coordinate:");
			JTextField yCoordP = new JTextField();
			JTextField xCoordP = new JTextField();

			JLabel yCoordLabelPo = new JLabel("Y Coordinate:");
			JLabel xCoordLabelPo = new JLabel("X Coordinate:");
			JTextField yCoordPo = new JTextField();
			JTextField xCoordPo = new JTextField();
			JLabel widthLabelPo = new JLabel("Width:");
			JTextField width = new JTextField();
			JLabel heightLabelPo = new JLabel("Height:");
			JTextField height = new JTextField();
			JLabel colrLabelPo = new JLabel("Color:");
			JTextField colorTextPo = new JTextField();

			postOptionsPanel.add(xCoordLabelPo);
			postOptionsPanel.add(xCoordPo);
			postOptionsPanel.add(yCoordLabelPo);
			postOptionsPanel.add(yCoordPo);
			postOptionsPanel.add(widthLabelPo);
			postOptionsPanel.add(width);
			postOptionsPanel.add(heightLabelPo);
			postOptionsPanel.add(height);
			postOptionsPanel.add(colrLabelPo);
			postOptionsPanel.add(colorTextPo);
			postOptionsPanel.setLayout(new GridLayout(3, 0));
			postOptionsPanel.setSize(100, 50);

			pinOptionsPanel.add(xCoordLabelP);
			pinOptionsPanel.add(xCoordP);
			pinOptionsPanel.add(yCoordLabelP);
			pinOptionsPanel.add(yCoordP);
			pinOptionsPanel.setLayout(new GridLayout());
			pinOptionsPanel.setSize(100, 50);

			getOptionsPanel.setLayout(new GridLayout());
			getOptionsPanel.setSize(100, 50);

			getOptionsPanel.add(colorLabel);
			getOptionsPanel.add(colorText);
			getOptionsPanel.add(refersToLabel);
			getOptionsPanel.add(refersToText);
			getOptionsPanel.add(yCoordLabel);
			getOptionsPanel.add(yCoord);
			getOptionsPanel.add(xCoordLabel);
			getOptionsPanel.add(xCoord);

			get.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					boolean notFound = true;
					while (notFound) {
						int result = JOptionPane.showConfirmDialog(frame, getOptionsPanel, "Get Properties",
								OK_CANCEL_OPTION);
						if (result == OK_CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION) {
							break;
						}
						try {
							color = colorText.getText();
							refersTo = refersToText.getText();
							int y = Integer.parseInt(yCoord.getText()), x = Integer.parseInt(xCoord.getText());
							int w = Integer.parseInt(width.getText()), h = Integer.parseInt(height.getText());
							contains = new Point(x, y);
							notFound = false;
							post(x, y, w, h, textArea.getText());
						} catch (NumberFormatException nfe) {
							yCoordLabel.setText("Y Coordinate (Integer)");
							xCoordLabel.setText("X Coordinate (Integer)");
						} catch (Exception ee) {
						}
					}
				}
			});

			connectDisconect.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {

					if (connectDisconect.getText() == "Disconnect") {
						diconnect();
						connectDisconect.setText("Connect");
					} else {
						try {
							IP = InetAddress.getByName(IPAddress.getText());
							port = Integer.parseInt(portNum.getText());
							connect(IP, port);
							connectDisconect.setText("Disconnect");

						} catch (java.net.UnknownHostException uhe) {
							JOptionPane.showMessageDialog(frame, "IP Address is invalid");
						} catch (NumberFormatException nfe) {
							JOptionPane.showMessageDialog(frame, "Port number is not a number");
						} catch (Exception ex) {
							JOptionPane.showMessageDialog(frame, "an exception occured");
						}
					}
				}
			});

			post.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					boolean notFound = true;
					while (notFound) {
						int result = JOptionPane.showConfirmDialog(frame, postOptionsPanel, "Post Properties",
								OK_CANCEL_OPTION);
						if (result == OK_CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION) {
							break;
						}
						try {
							color = colorTextPo.getText();
							int y = Integer.parseInt(yCoordPo.getText()), x = Integer.parseInt(xCoordPo.getText());
							// false = x and y are not valid, true = x and y are valid raise an error here
							boolean valid = (verifyXCoord(x) == false && verifyYCoord(y) == false) ? false : true;
							int w = Integer.parseInt(width.getText()), h = Integer.parseInt(height.getText());
							contains = new Point(x, y);
							notFound = false;
							if (color.equals("")) {
								post(x, y, w, h, textArea.getText());

							}
						} catch (NumberFormatException nfe) {
							yCoordLabelPo.setText("Y Coordinate (Integer)");
							xCoordLabelPo.setText("X Coordinate (Integer)");
						} catch (Exception ee) {
							// invalid number
						}
					}
				}
			});

			pinUnpin.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					boolean notFound = true;
					while (notFound) {
						int result = JOptionPane.showConfirmDialog(frame, pinOptionsPanel, "Pin Location",
								OK_CANCEL_OPTION);
						if (result == OK_CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION) {
							break;
						}
						try {

							int y = Integer.parseInt(yCoordP.getText()), x = Integer.parseInt(xCoordP.getText());

							notFound = false;
							pin(new Point(x, y));
						} catch (NumberFormatException nfe) {
							yCoordLabelP.setText("Y Coordinate (Integer)");
							xCoordLabelP.setText("X Coordinate (Integer)");
						} catch (Exception ee) {
						}
					}
				}
			});

			resultArea.setBackground(new Color(0xFFFFFF));
			resultArea.setSize(200, 300);

			connectPanel.add(IPAddress);
			connectPanel.add(portNum);
			connectPanel.add(connectDisconect);
			messagePanel.add(textArea);
			messagePanel.add(post);

			basePanel.setLayout(new GridLayout(2, 1));
			basePanel.add(connectPanel);
			basePanel.add(messagePanel);
			buttonPanel.add(get);
			buttonPanel.add(pinUnpin);
			basePanel.add(buttonPanel);
			basePanel.add(resultArea);

			frame.add(basePanel);
			frame.validate();
			frame.setVisible(true);
			frame.setSize(400, 400);

		}

		// TODO STUB
		boolean verifyXCoord(int x) {
			return ((x >= 0) ? true : false);
		}

		// TODO STUB
		boolean verifyYCoord(int y) {
			return ((y >= 0) ? true : false);
		}

		// Todo STUB
		boolean verifyColor(ArrayList<String> colours, String color) {
			boolean result = false;
			for (String colour : colours) {
				if (colour.equalsIgnoreCase(color)) {
					result = true;
					break;
				}
			}
			return result;
		}

		// TODO STUB
		void connect(InetAddress IP, int portnumber) throws Exception {

			this.socket = new Socket(IP, portnumber);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
		}

		void diconnect() {

		}

		void pin(Point2D point) {

		}

		void unpin(Point2D point) {

		}

		void showResults() {

		}

		// TODO condition when it is null
		// TODO overload colour
		void post(int x, int y, int w, int h, String message) {

			if (this.socket != null) {
				out.println("xCoord=" + x + "yCoord=" + y + "width=" + w + "height=" + h + "refersTo=" + message);

			}
		}

		void setDefaultColor(String defaultColor) {
			this.defaultColor = defaultColor;
		}

		void addColor(String color) {
			this.colours.add(color);
		}

	}

}
