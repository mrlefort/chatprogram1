package echoserver;

import static echoserver.EchoServer.clientHandlers;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;
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
    private String userName = "";

    public void checkMsgProtocol(String message)
    {
        start = "";
        middle = "";
        end = "";
        splitMessageFirst(message);

        if (start.equals("USER"))
        {

            addUser(middle);
        } else if (start.equals("SEND"))
        {
            if (middle.equals("*"))
            {

                String a = "MESSAGE#" + this.userName + "#" + end;
                sendMessageToAll(a);
            } else
            {
                String[] bob = middle.split(",");
                String b = "MESSAGE#" + this.userName + "#" + end;
                sendMsgToSpecific(bob, b);
            }
        } else if (start.equals("LOGOUT"))
        {
            System.out.println("Der kommer logout");
            killThisClient(userName);
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

        try
        {
            EchoServer.users.remove(userName);
            EchoServer.clientHandlers.remove(this);
            userList();
//            writer.println("LOGOUT#");
            socket.close();

        } catch (IOException ex)
        {
            Logger.getLogger(EchoClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addUser(String input)
    {
        EchoServer.users.put(input, this);
        userName = input;
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

        for (EchoClientHandler ch : EchoServer.clientHandlers)
        {

            ch.sendMessage(message);
        }

    }

    public synchronized void userList()
    {
        String onlineMsg = "USERS#";

        for (String user : EchoServer.users.keySet())
        {
            onlineMsg += user + ",";

        }
        onlineMsg = onlineMsg.substring(0, onlineMsg.length() - 1);

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

        writer.println(message);

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

            if (message.equals("LOGOUT#"))
            {
                System.out.println("Jeg har fået logout");
                killThisClient(userName);
            }

        } catch (IOException | NoSuchElementException ex)
        {
            Logger.getLogger(EchoClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
