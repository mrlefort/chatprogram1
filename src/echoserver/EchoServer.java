package echoserver;

import LogFiles.Log;
import echoclient.EchoClientHandler;
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
import shared.ProtocolStrings;


public class EchoServer {

    private static boolean keepRunning = true;
    private static ServerSocket serverSocket;
    private String ip;
    private int port;
    ArrayList<String> specificReceivers;
    public static HashMap<String, EchoClientHandler> users = new HashMap<>();
    
    public static ArrayList<EchoClientHandler> clientHandlers = new ArrayList();
    
    
    //hasmap som der har socket og brugernavn som key & value, til at finde ud af hvem man skal skrive til.
    //dvs dette her skal laves om til hashmap som logger deres socket og navne.
    

    public static void stopServer() {
        keepRunning = false;
    }

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
                EchoClientHandler cl = new EchoClientHandler(socket);
                clientHandlers.add(cl);
                Thread cl1 = new Thread(cl);
                cl1.start();
                
            } while (keepRunning);
        } catch (IOException ex) {
            Logger.getLogger(EchoServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        String ip = "localhost";
        int port = 9999;
        try {
            Log.setLogFile("logFile.txt", "ServerLog");   //Start the server here 
            new EchoServer().runServer(ip, port);
            } finally{  
            Log.closeLogger(); } 
        }
    
}
