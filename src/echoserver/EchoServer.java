package echoserver;

import LogFiles.Log;
import echoclient.ClientHandler;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import shared.ProtocolStrings;


public class EchoServer {

    private static boolean keepRunning = true;
    private static ServerSocket serverSocket;
    private String ip;
    private int port;
    
    
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList();
    

    public static void stopServer() {
        keepRunning = false;
    }

   
    //hasmap som der har socket og brugernavn som key & value, til at finde ud af hvem man skal skrive til.
    
    

    private void runServer(String ip, int port) {
        this.port = port;
        this.ip = ip;

        System.out.println("Sever started. Listening on: " + port + ", bound to: " + ip);
        try {
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(ip, port));
            do {
                Socket socket = serverSocket.accept(); //Important Blocking call
                System.out.println("Connected to a client");
                ClientHandler cl = new ClientHandler(socket);
                clientHandlers.add(cl);
                Thread cl1 = new Thread(cl);
                cl1.start();
                
            } while (keepRunning);
        } catch (IOException ex) {
            Logger.getLogger(EchoServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        String ip = "100.119.138.147";
        int port = 9999;
        try {
            Log.setLogFile("logFile.txt", "ServerLog");   //Start the server here 
            new EchoServer().runServer(ip, port);
            } finally{  
            Log.closeLogger(); } 
        }
    
}
