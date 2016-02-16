package echoserver;

import LogFiles.Log;
import echoclient.ClientHandler;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server
{

    private static boolean keepRunning = true;
    private static ServerSocket serverSocket;
    private String ip;
    private int port;
    private HashMap<String, ClientHandler> users;
    private static ArrayList<ClientHandler> clientHandlers = new ArrayList();
    //hasmap som der har socket og brugernavn som key & value, til at finde ud af hvem man skal skrive til.
    //dvs dette her skal laves om til hashmap som logger deres socket og navne.

    public Server()
    {
        users = new HashMap<>();
    }

    public HashMap getUsers()
    {
        return users;
    }

    public static void stopServer()
    {
        keepRunning = false;
    }

    public void addUser(String userName, ClientHandler ch)
    {
        users.put(userName, ch);
        System.out.println("Jeg har addet: " + userName + " og handler: " + ch);

    }

    public synchronized void userList()
    {
        String onlineMsg = "USERS#";

        for (String user : users.keySet())
        {

            onlineMsg += user + ",";

        }
        System.out.println("Her er den fulde liste: " + onlineMsg);
        for (ClientHandler ch : users.values())
        {
            ch.sendUserList(onlineMsg);
        }

    }

    public void stopUser(String userName, ClientHandler ch)
    {
        users.remove(userName, ch);
    }

    private void runServer(String ip, int port)
    {
        this.port = port;
        this.ip = ip;

        System.out.println("Sever started. Listening on: " + port + ", bound to: " + ip);
        try
        {
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(ip, port));
            do
            {
                Socket socket = serverSocket.accept(); //Important Blocking call
                System.out.println("Connected to a client");
                ClientHandler cl = new ClientHandler(socket, this);
                clientHandlers.add(cl);
                Thread cl1 = new Thread(cl);
                cl1.start();
                System.out.println("Der er følgende forbundet: " + clientHandlers.toString());

            } while (keepRunning);
        } catch (IOException ex)
        {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args)
    {
        String ip = "100.119.138.147";
        int port = 9999;
        try
        {
            Log.setLogFile("logFile.txt", "ServerLog");   //Start the server here 
            new Server().runServer(ip, port);
        } finally
        {
            Log.closeLogger();
        }
    }

}
