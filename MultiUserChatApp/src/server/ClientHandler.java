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
import java.util.ArrayList;

import objects.Message;
import objects.MessageType;
import objects.Streams;

import java.io.*;

public class ClientHandler implements Runnable {

    /*
     * Constructor : store the input/output streams of the corresponding client's socket 
     */
    ClientHandler(Server server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;
            this.out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            this.in = new ObjectInputStream(socket.getInputStream());
        } catch (SocketException se) {
            System.out.println("Error establishing connection: " + se.getMessage());
        } catch (IOException ioe) {
            System.out.println("Error establishing connection: " + ioe.getMessage());
        }
    }

    /*
     * the following method runs after the constructor's initialisation owing to the call to Thread's start method
     */
    @Override
    public void run() {
        System.out.println("in thread");
        try {

            /*
	     * get client's name
             */
            Message message = (Message) in.readObject();
            String str = message.getMessage();
            name = str;

            /*
	     * store client's name
             */
            server.getClients().put(name, new Streams(in, out));
            System.out.println(name + " at " + socket.getInetAddress().getHostAddress() + " joined the Chat!");
            server.getsLabel().setText(server.getsLabel().getText() + "\n\n" + name + " at " + socket.getRemoteSocketAddress() + " joined the Chat!");

            /*
	     * notify all the clients of new client coming online
             */
            server.getClients().forEach((k, v) -> {
                if (!k.equals(name)) {
                    try {
                        v.getOS().writeObject(new Message(new ArrayList<>(server.getClients().keySet())));
                        v.getOS().flush();
                    } catch (IOException ioe) {
                        System.out.println("Error establishing connection: " + ioe.getMessage());
                    }
                }
            });

            /*
	     * start reading for any type of message client sends to server and execute corresponding execution
             */
            while (!socket.isClosed()) {

                /*
	         * read the message
                 */
                message = (Message) in.readObject();
                final Message msg = message;

                if (message.getmType() == MessageType.REQUEST_CLIENT_LIST) {
                    /*
		     * when client requests for list of online clients
                     */
                    out.writeObject(new Message(new ArrayList<>(server.getClients().keySet())));
                    out.flush();
                } else if (msg.getmType() == MessageType.CLIENT_GLOBAL_MESSAGE) {
                    /*
		     * when client sends a global message
                     */
                    server.getClients().forEach((k, v) -> {
                        try {
                            v.getOS().writeObject(new Message(msg.getMessage(), this.name, MessageType.SERVER_GLOBAL_MESSAGE));
                            v.getOS().flush();
                        } catch (IOException ioe) {
                            System.out.println("Error establishing connection: " + ioe.getMessage());
                        }
                    });
                } else if (message.getmType() == MessageType.CLIENT_PRIVATE_MESSAGE) {
                    /*
		     * when client sends a private message
                     */
                    ObjectOutputStream out_ = server.getClients().get(message.getPerson()).getOS();
                    out_.writeObject(new Message(message.getMessage(), this.name, MessageType.SERVER_PRIVATE_MESSAGE));
                    out_.flush();
                }

            }
        } catch (ClassNotFoundException | IOException cnfe) {
            System.out.println("Error establishing connection: " + cnfe.getMessage());
        }
    }

    /*
     * global variables 
     */
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private String name;
    private Socket socket;
    private Server server;

}
