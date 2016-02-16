/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package echoclient;

import echoserver.Server;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author Steffen
 */
public class ClientHandler implements Runnable
{

    private Socket socket;
    private String message;
    private Scanner input;
    private PrintWriter writer;
    private ArrayList<String> specificReceivers;
    private String[] selectedUsers;
    private boolean pendingUserName;
    private Server ser;
    private String userName;

    public ClientHandler(Socket socket, Server ser) throws IOException
    {
        this.socket = socket;
        pendingUserName = true;
        this.ser = ser;
        input = new Scanner(socket.getInputStream());
        writer = new PrintWriter(socket.getOutputStream(), true);
    }

    public void sendMessage(String message)
    {
        writer.println(message.toUpperCase());

    }

    public void selectedusers(String[] stringarray)
    {
        this.selectedUsers = stringarray;
    }

    public void sendUserList(String list)
    {
        writer.println(list);
    }

    @Override
    public void run()
    {
//        try
//        {
//            input = new Scanner(socket.getInputStream());
//            writer = new PrintWriter(socket.getOutputStream(), true);
//
//            message = input.nextLine(); //IMPORTANT blocking call
//            System.out.println("ClientHandler: " + message);
//            System.out.println(String.format("Received the message: %1$S ", message));
//            while (!message.equals(ProtocolStrings.STOP))
//            {
//                checkMsgProtocol(message);             //Her bliver beskeden sendt ud til ALLE klienter.
//
//                System.out.println(String.format("Received the message: %1$S ", message.toUpperCase()));
//                message = input.nextLine(); //IMPORTANT blocking call
//            }
//            writer.println(ProtocolStrings.STOP);//Echo the stop message back to the client for a nice closedown
//            Server.clientHandlers.remove(this);
//            socket.close();
//            System.out.println("Closed a Connection");
//        } catch (IOException ex)
//        {
//            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    //    public void checkMsgProtocol(String message)
//    {
//        if (selectedUsers.length != 0)
//        {
//            sendMsgToSpecific(message);
//        } else
//        {
//            sendMessageToAll(message);
//
//        }
//        //feks. switch til at finde ud af hvilken protocolString det er.
//        //denne metode kan passende kalde sendMsgToSpecific(): 
//        //kan også passende kalde sendMessageToAll();
//    }
//
//    public void sendMsgToSpecific(String message)
//    {
//
//        specificReceivers = new ArrayList();
//        for (int i = 0; i < selectedUsers.length; i++)
//        {
//            for (int j = 0; j < ser.users.size(); j++)
//            {
//                if (selectedUsers[i].equals(ser.getUsers().).
//                {
//                    specificReceivers.add(selectedUsers[i]);
//
//                }
//            }
//        }
//        sendMessage("SEND#" + specificReceivers + "#" + message);
//    }
//
//    static public void sendMessageToAll(String messageToAll)
//    {
//        String bob;
//
//        for (ClientHandler ch : clientHandlers)
//        {
//            bob = "SEND#*#" + messageToAll;
//            ch.sendMessage(bob);
//        }
//    }
}
