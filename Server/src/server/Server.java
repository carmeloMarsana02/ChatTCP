package server;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import javax.swing.*;

public class Server {
    public static String getIncrementStr(String str){
        StringBuilder subS = new StringBuilder();
        for(char c:str.toCharArray()){
            subS.append(++c);
        }
        return subS.toString();
    }
    
    public static void main(String[] args) throws IOException {
        //create a new window
        JFrame frame = new JFrame("ServerSide");
        //panels
        JPanel topPanel = new JPanel();
        JPanel statusPanel = new JPanel();
        
        JRadioButton select = new JRadioButton("Crypt Mode", true);
        topPanel.add(BorderLayout.PAGE_START, select);
        
        JButton sendBut = new JButton("Send");
        JLabel txtLabel = new JLabel("Send a message: ");
        JTextField txtBox = new JTextField(15);
        txtBox.setBorder(BorderFactory.createEtchedBorder());
        txtBox.setEditable(false);
        topPanel.add(txtLabel);
        topPanel.add(txtBox);
        topPanel.add(sendBut);
        
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
        frame.getContentPane().add(BorderLayout.PAGE_START, topPanel);
        frame.getContentPane().add(BorderLayout.CENTER, scrollPanel);
        frame.getContentPane().add(BorderLayout.SOUTH, statusPanel);
      
        
        //setting up the window
        frame.setSize(500,500);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        //backend
        final int SERVER_PORT = 2556;
        //listening on server port
        ServerSocket server = new ServerSocket(SERVER_PORT);
        statusLabel.setText("Listening on port " + SERVER_PORT + " ...");
        
        //making the server accept requests from clients
        Socket socket = server.accept();
        statusLabel.setText("Client has now connected successfully!");
        
        //reader and writer using sockets
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
                txtArea.append("[" + dtf.format(now) + "] Server > " + inputLine + "\n");
            }
        });
        
        while(socket.isConnected()){
            String clientInput = in.nextLine();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            txtArea.append("[" + dtf.format(now) + "] Client > " + clientInput + "\n");
            if(select.isSelected()){
                txtBox.setEditable(false);
                txtBox.setText("");
                String crypted = getIncrementStr(clientInput);
                out.println(crypted);
                out.flush();
                txtArea.append("[" + dtf.format(now) + "] Server > " + crypted + "\n");
            }else{
                txtBox.setEditable(true);
            }
        }
    }
}
