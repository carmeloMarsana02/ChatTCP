package client;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import javax.swing.*;

public class Client {
    public static void main(String[] args) throws IOException {
        //create a new window
        JFrame frame = new JFrame("ClientSide");
        //panels
        JPanel txtPanel = new JPanel();
        JPanel statusPanel = new JPanel();
        
        JButton sendBut = new JButton("Send");
        JLabel txtLabel = new JLabel("Send a message: ");
        JTextField txtBox = new JTextField(25);
        txtBox.setBorder(null);
        txtPanel.setBorder(BorderFactory.createEtchedBorder());
        
        txtPanel.add(txtLabel);
        txtPanel.add(txtBox);
        txtPanel.add(sendBut);
        
        //text area with messages
        JTextArea txtArea = new JTextArea(24, 40);  
        txtArea.setBorder(null);
        txtArea.setEditable(false);

        //scrollbar
        JScrollPane scrollPanel = new JScrollPane(txtArea);
        
        //status panel
        JLabel statusLabel = new JLabel();
        statusPanel.add(statusLabel);
        
        //Adding all the items to the main frame
        frame.getContentPane().add(BorderLayout.PAGE_START, txtPanel);
        frame.getContentPane().add(BorderLayout.CENTER, scrollPanel);
        frame.getContentPane().add(BorderLayout.SOUTH, statusPanel);
        
        frame.setSize(500,500);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        //Backend
        final int SERVER_PORT = 2556;
        final String SERVER_IP = "127.0.0.1";
        
        Socket socket = new Socket(SERVER_IP,SERVER_PORT);
        statusLabel.setText("Now connect with IP: " + SERVER_IP + " and with port: " + SERVER_PORT);
                 
        PrintWriter out = new PrintWriter(socket.getOutputStream());
        Scanner in = new Scanner(socket.getInputStream());

        sendBut.addActionListener(al ->{
        //send message to the server on button click
            if(!txtBox.getText().isEmpty() || txtBox.getText().startsWith(" ")){
                String inputLine = txtBox.getText();
                //get the time
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");  
                LocalDateTime now = LocalDateTime.now();
                //send the message to the server
                out.println(inputLine);
                out.flush();
                //ad the message in the application itself
                txtArea.append("[" + dtf.format(now) + "] Client > " + inputLine + "\n");
            }
        });
        
        while(socket.isConnected()){
            String serverLine = in.nextLine();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");  
            LocalDateTime now = LocalDateTime.now();
            txtArea.append("[" + dtf.format(now) + "] Server > " + serverLine + "\n");
        }
    }
}
