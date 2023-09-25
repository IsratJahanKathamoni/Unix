/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server;

/*
 * CNC 509 Group Assignment
 * Team Members: Mohd. Ariful Islam - 2120640, Puja Das - 2111363, Umme Tasnim - 1921848
 * @author dark_
 */
import java.net.*;
import java.io.*;
import java.util.*;

import javax.swing.*;

import objects.Streams;

import java.awt.Dimension;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {

    /**
     * main method of the Main-Class of the jar file
     */
    public static void main(String[] args) {
        new Server();
    }

    /*
	 * start Listening through any available port, generate GUI to inform the user of server running
	 * and start listening through that port
     */
    Server() {

        clients = new HashMap<>();
        String message = "Using port number 50000\nTo listen to clients through a different port, type the port number:\n";
        SwingUtilities.invokeLater(() -> {
            String str = (String) JOptionPane.showInputDialog(frame, message, "50000");
            if (str == null) {
                System.exit(0);
            }
            str = str.trim();
            pNumber = Integer.parseInt(str);
            try {
                Server.this.server = new ServerSocket(pNumber);
            } catch (IOException ioe) {
                System.out.println("Could't connect to server: " + ioe.getMessage());
            }
            if (server == null) {
                JOptionPane.showMessageDialog(frame, "Port in Use!", "Error!", JOptionPane.ERROR_MESSAGE);

                System.exit(0);
            }

            try {
                setGUI();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

    }


    /*
	 * the following method code runs in Event Dispatch Thread as it conains Swing components 
     */
    private void setGUI() throws FileNotFoundException {

        JFrame.setDefaultLookAndFeelDecorated(true);

        frame = new JFrame();
        try {
            sLabel = new JTextArea();
            sLabelPane = new JScrollPane();
            sLabelPane.setViewportView(sLabel);
            sLabel.setEditable(false);
            sLabel.setText("Server Listening at: \n" + InetAddress.getLocalHost() + "\nPort: " + pNumber + "\n\nIf client is on the same machine,\nuse localhost as the IP address\nand port as mentioned above,\nelse if on a different machine,\nuse the above IP address and port number.");
        } catch (UnknownHostException uhe) {
            uhe.printStackTrace();
        }
        frame.add(sLabelPane);
        frame.setPreferredSize(new Dimension(450, 200));
        frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        startListening();
    }

    /*
	 * start listening to specified port forever
     */
    private void startListening() {

        new SwingWorker<Object, Object>() {

            @Override
            protected Object doInBackground() throws Exception {
                while (!server.isClosed()) {
                    try {
                        Socket socket = server.accept();
                        new Thread(new ClientHandler(Server.this, socket)).start();
                        System.out.println("Client connected at: " + socket.getRemoteSocketAddress());
                    } catch (IOException ioe) {
                        System.out.println("Error establishing connection: " + ioe.getMessage());
                    }

                }
                return null;
            }
        }.execute();

    }

    public HashMap<String, Streams> getClients() {
        return clients;
    }

    public JTextArea getsLabel() {
        return sLabel;
    }

    /*
	 * global variables 
     */
    private ServerSocket server;
    private JFrame frame;
    private JTextArea sLabel;
    private JScrollPane sLabelPane;
    private int pNumber;

    /* 
     * hashtable to store names and Input/Output streams of all the clients
     */
    private final HashMap<String, Streams> clients;

}
