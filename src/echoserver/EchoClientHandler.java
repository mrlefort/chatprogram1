/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package echoserver;

import echoserver.EchoServer;
import static echoserver.EchoServer.clientHandlers;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author Steffen
 */
public class EchoClientHandler implements Runnable
{

    private Socket socket;
    private String message;
    private EchoServer ser = new EchoServer();
    private Scanner input;
    private PrintWriter writer;
    private String[] selectedUsers;

    private String start;
    private String middle;
    private String end;
    private String userName;

    public void checkMsgProtocol(String message)
    {
        start = "";
        middle = "";
        end = "";
        splitMessageFirst(message);

        if (start.equals("USER"))
        {
            addUser(middle);
        }
        else if (start.equals("SEND"))
        {
            if (middle.equals("*"))
            {
                sendMessageToAll(end);
            }
            else
            {
                String[] bob = middle.split(",");
                sendMsgToSpecific(bob, end);
            }
        }
        else if (start.equals("LOGOUT"))
        {
            killThisClient(userName);
            writer.println("Goodbye");
        }
    }

    //splits the message received
    public void splitMessageFirst(String message)
    {
        String[] splitTheMessage = message.split("#");

        switch (splitTheMessage.length)
        {
            case 1:
                start = splitTheMessage[0];
                break;
            case 2:
                start = splitTheMessage[0];
                middle = splitTheMessage[1];
                break;
            default:
                start = splitTheMessage[0];
                middle = splitTheMessage[1];
                end = splitTheMessage[2];
                break;
        }

    }
    
    public void killThisClient(String userName)
    {
        System.out.println("lukker klienten!");
        try
        {
            EchoServer.users.remove(userName);
            userList();
            socket.close();
            System.out.println("Jeg er lukket = " + socket.isClosed());
        }
        catch (IOException ex)
        {
            Logger.getLogger(EchoClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addUser(String userName)
    {
        EchoServer.users.put(userName, this);
        this.userName = userName;
        userList();
    }

    public void sendMsgToSpecific(String[] recipients, String message)
    {
        for (String recipient : recipients)
        {
            EchoClientHandler ch = EchoServer.users.get(recipient);
            ch.sendMessage(message);
        }
    }

    public void sendMessageToAll(String message)
    {

        for (EchoClientHandler ch : clientHandlers)
        {               //skal skiftes fra min arrayliste til vores hashmap.

            ch.sendMessage(end);

        }

    }

    public synchronized void userList()
    {
        String onlineMsg = "USERS#";

        for (String user : EchoServer.users.keySet())
        {

            onlineMsg += user + ",";

        }
        System.out.println("Her er den fulde liste: " + onlineMsg);
        for (EchoClientHandler ch : EchoServer.users.values())
        {
            ch.sendUserList(onlineMsg);
        }

    }

    public void sendUserList(String list)
    {
        writer.println(list);
    }

    public EchoClientHandler(Socket socket)
    {
        this.socket = socket;
    }

    public void sendMessage(String message)
    {
        writer.println(message.toUpperCase());

    }

    public void selectedusers(String[] stringarray)
    {
        this.selectedUsers = stringarray;
    }

    @Override
    public void run()
    {
        try
        {
            input = new Scanner(socket.getInputStream());
            writer = new PrintWriter(socket.getOutputStream(), true);

            message = input.nextLine(); //IMPORTANT blocking call

            System.out.println("ClientHandler: " + message);
            System.out.println(String.format("Received the message: %1$S ", message));
            while (!message.equals("LOGOUT#"))
            {
                checkMsgProtocol(message);            //Her tjekker vi hvem der skal have beskeden og sender den også

                System.out.println(String.format("Received the message: %1$S ", message.toUpperCase()));
                message = input.nextLine(); //IMPORTANT blocking call
            }
            writer.println("LOGOUT#");//Echo the stop message back to the client for a nice closedown
            EchoServer.clientHandlers.remove(this);
            socket.close();
            System.out.println("Closed a Connection");
        }
        catch (IOException ex)
        {
            Logger.getLogger(EchoClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
