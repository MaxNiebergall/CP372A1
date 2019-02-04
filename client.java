import static javax.swing.JOptionPane.OK_CANCEL_OPTION;

import java.awt.Color;
import java.awt.GridLayout;
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
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class client {
	public static boolean show = true;

	public static void main(String[] args) {
		GUI gui = new GUI();
	}

	public static class GUI {
		// Swing:
		public JFrame frame = new JFrame();
		public JPanel basePanel = new JPanel();
		public JPanel connectPanel = new JPanel();
		public JPanel messagePanel = new JPanel();
		public JPanel buttonPanel = new JPanel();
		public JPanel unpinOptionsPanel = new JPanel();
		public JPanel pinOptionsPanel = new JPanel();

		// public JTextField IPAddress = new JTextField("IP Address");
		public JTextField IPAddress = new JTextField("localhost");
		// public JTextField portNum = new JTextField("Port Number");
		public JTextField portNum = new JTextField("9898");
		public JButton connectDisconect = new JButton("Connect");

		public JTextArea messageArea = new JTextArea("message here");
		public JButton post = new JButton("Post");
		public JPanel postOptionsPanel = new JPanel();

		public JButton get = new JButton("Get");
		public JButton getPins = new JButton("Get pins");
		public JPanel getOptionsPanel = new JPanel();
		public JButton pin = new JButton("Pin");
		public JButton unpin = new JButton("Unpin");

		public JTextArea resultArea = new JTextArea();
		public InetAddress IP;
		public int port;
		public Socket socket;

		BufferedReader in; // server response
		PrintWriter out; // to send data use print writer
		public ArrayList<String> colors = new ArrayList<String>();
		public String color, refersTo;
		public Point2D contains;

		GUI() {
			// TODO HANDLE RESPONSES
			// TODO update documentation to describe the way we are returning notes after
			// GET request.
			JLabel colorLabel = new JLabel("Color: ");
			JLabel refersToLabel = new JLabel("Contains Message: ");
			JLabel yCoordLabel = new JLabel("Y Coordinate: ");
			JLabel xCoordLabel = new JLabel("X Coordinate: ");
			// JTextField colorText = new JTextField();
			JTextField refersToText = new JTextField();
			JTextField yCoord = new JTextField();
			JTextField xCoord = new JTextField();
			JComboBox<String> colorComboBoxG = new JComboBox<String>();

			JLabel yCoordLabelP = new JLabel("Y Coordinate: ");
			JLabel xCoordLabelP = new JLabel("X Coordinate: ");
			JTextField yCoordP = new JTextField();
			JTextField xCoordP = new JTextField();

			JLabel yCoordLabeluP = new JLabel("Y Coordinate: ");
			JLabel xCoordLabeluP = new JLabel("X Coordinate: ");
			JTextField yCoorduP = new JTextField();
			JTextField xCoorduP = new JTextField();

			JLabel yCoordLabelPo = new JLabel("Y Coordinate: ");
			JLabel xCoordLabelPo = new JLabel("X Coordinate: ");
			JTextField yCoordPo = new JTextField();
			JTextField xCoordPo = new JTextField();
			JLabel widthLabelPo = new JLabel("Width: ");
			JTextField width = new JTextField();
			JLabel heightLabelPo = new JLabel("Height: ");
			JTextField height = new JTextField();
			JLabel colrLabelPo = new JLabel("Color:");
			JComboBox<String> colorComboBoxPo = new JComboBox<String>();

			postOptionsPanel.add(xCoordLabelPo);
			postOptionsPanel.add(xCoordPo);
			postOptionsPanel.add(yCoordLabelPo);
			postOptionsPanel.add(yCoordPo);
			postOptionsPanel.add(widthLabelPo);
			postOptionsPanel.add(width);
			postOptionsPanel.add(heightLabelPo);
			postOptionsPanel.add(height);
			postOptionsPanel.add(colrLabelPo);
			postOptionsPanel.add(colorComboBoxPo);
			postOptionsPanel.setLayout(new GridLayout(3, 0));
			postOptionsPanel.setSize(100, 50);

			pinOptionsPanel.add(xCoordLabelP);
			pinOptionsPanel.add(xCoordP);
			pinOptionsPanel.add(yCoordLabelP);
			pinOptionsPanel.add(yCoordP);
			pinOptionsPanel.setLayout(new GridLayout());
			pinOptionsPanel.setSize(100, 50);

			unpinOptionsPanel.add(xCoordLabeluP);
			unpinOptionsPanel.add(xCoorduP);
			unpinOptionsPanel.add(yCoordLabeluP);
			unpinOptionsPanel.add(yCoorduP);
			unpinOptionsPanel.setLayout(new GridLayout());
			unpinOptionsPanel.setSize(100, 50);

			getOptionsPanel.setLayout(new GridLayout());
			getOptionsPanel.setSize(100, 50);

			getOptionsPanel.add(colorLabel);
			getOptionsPanel.add(colorComboBoxG);
			getOptionsPanel.add(refersToLabel);
			getOptionsPanel.add(refersToText);
			getOptionsPanel.add(yCoordLabel);
			getOptionsPanel.add(yCoord);
			getOptionsPanel.add(xCoordLabel);
			getOptionsPanel.add(xCoord);

			getPins.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (connectDisconect.getText().equals("Connect")) {
						JOptionPane.showMessageDialog(frame, "Must connect first");
					} else {
						try {
							get();

							// Retrieve results
							String line = in.readLine();
							resultArea.setText(line);

							frame.validate();
							frame.repaint();

						} catch (NumberFormatException nfe) {
							yCoordLabel.setText("Y Coordinate (Integer)");
							xCoordLabel.setText("X Coordinate (Integer)");
						} catch (Exception ee) {
							ee.printStackTrace();
						}
					}
				}

			});

			get.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (connectDisconect.getText().equals("Connect")) {
						JOptionPane.showMessageDialog(frame, "Must connect first");
					} else {
						boolean notFound = true;
						while (notFound) {
							int result = JOptionPane.showConfirmDialog(frame, getOptionsPanel, "Get Properties",
									OK_CANCEL_OPTION);
							if (result == JOptionPane.CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION) {
								break;
							}
							try {
								color = (String) colorComboBoxG.getSelectedItem();
								refersTo = refersToText.getText();
								int y = -1, x = -1;
								if (!yCoord.getText().equals("")) {
									y = Integer.parseInt(yCoord.getText());
								}
								if (!xCoord.getText().equals("")) {
									x = Integer.parseInt(xCoord.getText());
								}

								notFound = false;
								get(x, y, refersTo, color);

								// Retrieve results
								String line = in.readLine();
								resultArea.setText(line);

								frame.validate();
								frame.repaint();

							} catch (NumberFormatException nfe) {
								yCoordLabel.setText("Y Coordinate (Integer)");
								xCoordLabel.setText("X Coordinate (Integer)");
							} catch (Exception ee) {
								ee.printStackTrace();
							}
						}
					}
				}
			});

			connectDisconect.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {

					if (connectDisconect.getText() == "Disconnect") {
						diconnect();
						colorComboBoxG.removeAllItems();
						colorComboBoxPo.removeAllItems();
						connectDisconect.setText("Connect");
					} else {
						try {
							IP = InetAddress.getByName(IPAddress.getText());
							port = Integer.parseInt(portNum.getText());
							colorComboBoxG.removeAllItems();
							colorComboBoxPo.removeAllItems();
							connect(IP, port);
							connectDisconect.setText("Disconnect");
							colorComboBoxG.addItem("None");
							colorComboBoxG.addItem("Default");
							colorComboBoxPo.addItem("Default");
							for (int i = 0; i < colors.size(); i++) {
								colorComboBoxPo.addItem(colors.get(i));
								colorComboBoxG.addItem(colors.get(i));
							}

						} catch (java.net.UnknownHostException uhe) {
							JOptionPane.showMessageDialog(frame, "IP Address is invalid");
						} catch (NumberFormatException nfe) {
							JOptionPane.showMessageDialog(frame, "Port number is not a number");
						} catch (java.net.ConnectException ce) {
							JOptionPane.showMessageDialog(frame, "Connection Error \nConnection refused");
						} catch (Exception ex) {
							JOptionPane.showMessageDialog(frame, "an exception occurred");
							ex.printStackTrace();
						}
					}
				}
			});

			post.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (connectDisconect.getText().equals("Connect")) {
						JOptionPane.showMessageDialog(frame, "Must connect first");
					} else {
						boolean notFound = true;
						while (notFound) {
							int result = JOptionPane.showConfirmDialog(frame, postOptionsPanel, "Post Properties",
									OK_CANCEL_OPTION);
							if (result == JOptionPane.CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION) {
								break;
							}
							try {
								color = (String) colorComboBoxPo.getSelectedItem();
								int y = Integer.parseInt(yCoordPo.getText()), x = Integer.parseInt(xCoordPo.getText());
								// false = x and y are not valid, true = x and y are valid raise an error here
								boolean valid = (verifyXCoord(x) == false && verifyYCoord(y) == false) ? false : true;
								int w = Integer.parseInt(width.getText()), h = Integer.parseInt(height.getText());

								notFound = false;
								if (color.equals("Default")) {
									post(x, y, w, h, messageArea.getText());
								} else {
									post(x, y, w, h, messageArea.getText(), color);
								}

								String line = in.readLine();
								JOptionPane.showMessageDialog(frame, line);

							} catch (NumberFormatException nfe) {
								yCoordLabelPo.setText("Y Coordinate (Integer)");
								xCoordLabelPo.setText("X Coordinate (Integer)");
							} catch (Exception ee) {
								// invalid number
								ee.printStackTrace();
							}
						}
					}
				}
			});

			pin.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if (connectDisconect.getText().equals("Connect")) {
						JOptionPane.showMessageDialog(frame, "Must connect first");
					} else {
						boolean notFound = true;
						while (notFound) {
							int result = JOptionPane.showConfirmDialog(frame, pinOptionsPanel, "Pin Location",
									OK_CANCEL_OPTION);
							if (result == JOptionPane.CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION) {
								break;
							}
							try {

								int y = Integer.parseInt(yCoordP.getText()), x = Integer.parseInt(xCoordP.getText());
								pin(x, y);
								notFound = false;

							} catch (NumberFormatException nfe) {
								yCoordLabelP.setText("Y Coordinate (Integer)");
								xCoordLabelP.setText("X Coordinate (Integer)");
							} catch (Exception ee) {
								ee.printStackTrace();
							}
						}
					}
				}
			});
			unpin.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if (connectDisconect.getText().equals("Connect")) {
						JOptionPane.showMessageDialog(frame, "Must connect first");
					} else {
						boolean notFound = true;
						while (notFound) {
							int result = JOptionPane.showConfirmDialog(frame, unpinOptionsPanel, "Unpin Location",
									OK_CANCEL_OPTION);
							if (result == JOptionPane.CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION) {
								break;
							}
							try {
								int y = Integer.parseInt(yCoordP.getText()), x = Integer.parseInt(xCoordP.getText());

								unpin(x, y);
								notFound = false;

							} catch (NumberFormatException nfe) {
								yCoordLabelP.setText("Y Coordinate (Integer)");
								xCoordLabelP.setText("X Coordinate (Integer)");
							} catch (Exception ee) {
								ee.printStackTrace();
							}
						}
					}
				}
			});

			resultArea.setBackground(new Color(0xFFFFFF));
			resultArea.setSize(200, 300);
			resultArea.setEditable(false);
			resultArea.setLineWrap(true);

			connectPanel.add(IPAddress);
			connectPanel.add(portNum);
			connectPanel.add(connectDisconect);
			messagePanel.add(messageArea);
			messagePanel.add(post);

			basePanel.setLayout(new GridLayout(4, 1));

			basePanel.add(connectPanel);
			basePanel.add(messagePanel);
			buttonPanel.add(get);
			buttonPanel.add(pin);
			buttonPanel.add(unpin);
			buttonPanel.add(getPins);
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
		boolean verifyColor(ArrayList<String> colors, String color) {
			boolean result = false;
			for (String color1 : colors) {
				if (color1.equalsIgnoreCase(color)) {
					result = true;
					break;
				}
			}
			return result;
		}

		// TODO Handle the methods when the socket is null, throw exceptions and catch
		// them
		void connect(InetAddress IP, int portnumber) throws Exception {

			this.socket = new Socket(IP, portnumber);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
			if (show) {
				String colorsStr = in.readLine();
				String[] colorsArr = colorsStr.split(" ");
				for (int i = 0; i < colorsArr.length; i++) {
					colors.add(colorsArr[i]);
				}
				show = false;
			}

		}

		void diconnect() {
			if (this.socket != null) {
				out.println("DISCONNECT ");
			}
		}

		void clear() {
			if (this.socket != null) {
				out.println("CLEAR ");
			}
		}

		void pin(int x, int y) {
			if (this.socket != null) {
				out.println("PIN xCoord=" + x + ", yCoord=" + y + ",");
			}
		}

		void unpin(int x, int y) {
			if (this.socket != null) {
				out.println("UNPIN xCoord=" + x + ", yCoord=" + y + ",");
			}
		}

		// TODO condition when it is null
		// TODO add the notes to whats visible
		void post(int x, int y, int w, int h, String message) {

			if (this.socket != null) {
				out.println("POST xCoord=" + x + ", yCoord=" + y + ", width=" + w + ", height=" + h + ", refersTo="
						+ message);
			}

		}

		void post(int x, int y, int w, int h, String message, String color) {
			if (this.socket != null) {
				out.println("POST xCoord=" + x + ", yCoord=" + y + ", width=" + w + ", height=" + h + ", color=" + color
						+ ", refersTo=" + message);
			}
		}

		void get(int x, int y, String message, String color) {
			String toSend = "GET ";
			if (!color.equals("None")) {
				toSend += "color=" + color;
			}

			if (x >= 0) {
				toSend += "xCoord=" + x;
			}
			if (y >= 0) {
				toSend += ", yCoord=" + y;
			}

			if (message.length() > 0) {
				toSend += ", refersTo=" + message;
			}

			out.println(toSend);
//			System.out.println(toSend);
		}

		void get() {
			out.println("GET PINS");
		}

	}

}
