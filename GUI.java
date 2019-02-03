import java.awt.*;
import java.awt.geom.Point2D;
import java.net.InetAddress;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import static javax.swing.JOptionPane.OK_CANCEL_OPTION;

public class GUI {
    //Swing:
    private JFrame frame = new JFrame();
    private JPanel basePanel = new JPanel();
    private JPanel connectPanel = new JPanel();
    private JPanel messagePanel = new JPanel();
    private JPanel buttonPanel = new JPanel();

    private JPanel pinOptionsPanel = new JPanel();


    private JTextField IPAddress = new JTextField("IP Address");
    private JTextField portNum = new JTextField("Port Number");
    private JButton connectDisconect = new JButton("Connect");

    private JTextArea textArea = new JTextArea("Your Message Here");
    private JButton post = new JButton("Post");
    private JPanel postOptionsPanel = new JPanel();


    private JButton get = new JButton("Get");
    private JPanel getOptionsPanel = new JPanel();
    private JButton pinUnpin = new JButton("Pin");

    private JTextArea resultArea = new JTextArea("Results Go Here");

    private InetAddress IP;
    private int port;
    private String color, refersTo;
    private Point2D contains;

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
        postOptionsPanel.setLayout(new GridLayout(3,0));
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
                    int result = JOptionPane.showConfirmDialog(frame, getOptionsPanel, "Get Properties", OK_CANCEL_OPTION);
                    if (result == OK_CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION) {
                        break;
                    }
                    try {
                        color = colorText.getText();
                        refersTo = refersToText.getText();
                        int y = Integer.parseInt(yCoord.getText()), x = Integer.parseInt(xCoord.getText());
                        contains = new Point(x, y);

                        notFound = false;
                        connect();
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
                    int result = JOptionPane.showConfirmDialog(frame, postOptionsPanel, "Post Properties", OK_CANCEL_OPTION);
                    if (result == OK_CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION) {
                        break;
                    }
                    try {
                        color = colorTextPo.getText();
                        int y = Integer.parseInt(yCoordPo.getText()), x = Integer.parseInt(xCoordPo.getText());
                        contains = new Point(x, y);

                        notFound = false;
                        connect();
                    } catch (NumberFormatException nfe) {
                        yCoordLabelPo.setText("Y Coordinate (Integer)");
                        xCoordLabelPo.setText("X Coordinate (Integer)");
                    } catch (Exception ee) {
                    }
                }
            }
        });


        pinUnpin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean notFound = true;
                while (notFound) {
                    int result = JOptionPane.showConfirmDialog(frame, pinOptionsPanel, "Pin Location", OK_CANCEL_OPTION);
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

    //TODO STUB
    boolean verifyXCoord(int x) {
        return true;
    }

    //TODO STUB
    boolean verifyYCoord(int y) {
        return true;
    }

    //Todo STUB
    boolean verifyColor(String color) {
        return true;
    }


    //TODO STUB
    void connect() {

    }

    void diconnect() {

    }

    void pin(Point2D point) {

    }

}
