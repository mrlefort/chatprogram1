/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package echoclient;

import echoserver.Server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
    private BufferedReader in;
    private Scanner scan;

    public ClientHandler(Socket socket, Server ser) throws IOException
    {
        this.socket = socket;
        pendingUserName = true;
        this.ser = ser;
        writer = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

    }

    public void stopClient()
    {
        try
        {
            ser.stopUser(userName, this);
            ser.userList();
            System.out.println("jeg er ikke lukket endnu");
            socket.close();

            System.out.println("Jeg er lukket" + socket.isClosed());
        }
        catch (IOException ex)
        {
            System.err.println("JEG HAR FANGET EX I STOP CLIENT");
        }
    }

    public synchronized void chat()
    {
        try
        {
            System.out.println("Jeg er i chat()");
            String inMsg = in.readLine();
            System.out.println("inMSG er: " + inMsg);
            String first = "";
            String middle = "";
            String last = "";
            scan = new Scanner(inMsg);
            scan.useDelimiter("#");
            while (scan.hasNext())
            {
                first = scan.next();
                if (!first.equals("LOGOFF"))
                {
                    middle = scan.next();
                    last = scan.next();
                }
                switch (first)
                {
                    case "LOGOFF":
                        System.out.println("jeg er i logoff switch");
                        stopClient();
                        break;
                    case "SEND":
                        System.out.println("JEG ER I MSG SWITCH");
                        ser.sendMessage(last, msgRecipients(middle));
                        break;
                }
            }
        }
        catch (IOException e)
        {

        }
    }

    public void message(String message)
    {
        writer.println(message);
    }

    public ArrayList<String> msgRecipients(String middle)
    {
        System.out.println("LIGE INDE I MSGRECI");
        ArrayList recipients = new ArrayList();
        scan = new Scanner(middle);
        scan.useDelimiter(",");
        String names;
        while (scan.hasNext())
        {
            names = scan.next();
            recipients.add(names);
        }
        System.out.println("præ returns");
        return recipients;
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

    public String getUserName()
    {
        return userName;
    }

    @Override
    public void run()
    {
        try
        {

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (pendingUserName)
            {
//                out.println("Please enter a username like this: USER#'your_name'");
                String nameInput = in.readLine();
                input = new Scanner(nameInput);
                input.useDelimiter("#");

                while (input.hasNext())
                {
                    String a = input.next();
                    if (a.equals("USER"))
                    {
                        userName = input.next();
                        ser.addUser(userName, this);
                        ser.userList();
                        pendingUserName = false;
                    }
                }
            }
        }
        catch (IOException ex)
        {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

        while (!socket.isClosed())
        {
            chat();
        }
    }
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
