/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package echoclient;

import echoserver.EchoServer;
import static echoserver.EchoServer.clientHandlers;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import shared.ProtocolStrings;

/**
 *
 * @author Steffen
 */
public class ClientHandler implements Runnable {

    Socket socket;
    String message;
    EchoServer ser = new EchoServer();
    Scanner input;
    PrintWriter writer;
    
    String[] selectedUsers;
    
    String start = "";
    String middle = "";
    String end = "";
    
    

    public void checkMsgProtocol(String message) {
        splitMessageFirst(message);
        
        if (start.equals("SEND")){
            if (middle.equals("*")) {
                sendMessageToAll(message);
            }
            sendMsgToSpecific(message);
        
        }


        
    }
    
    //splits the message received
    public void splitMessageFirst(String message){
        String[] splitTheMessage = message.split("#");
        start = splitTheMessage[0];
        middle = splitTheMessage[1];
        end = splitTheMessage[2];
    }
    
    
    
        public void sendMsgToSpecific(String message){
        String[] splitReceivers = middle.split(",");
        ArrayList<ClientHandler> specificReceivers = new ArrayList();
        

        for (int i = 0; i < splitReceivers.length; i++) {
            for (int j = 0; j < EchoServer.clientHandlers.size(); j++) {           //skal skiftes fra min arrayliste til vores hashmap.
                if (splitReceivers[i].equals(EchoServer.clientHandlers.get(j))){   //skal skiftes fra min arrayliste til vores hashmap.
                    specificReceivers.add(EchoServer.clientHandlers.get(j));
                    
                }
            }
        }
        
            for (ClientHandler specificReceiver : specificReceivers) {
                specificReceiver.sendMessage(end);
            }
        
    }

    public void sendMessageToAll(String message) {
        
        for (ClientHandler ch : clientHandlers) {               //skal skiftes fra min arrayliste til vores hashmap.

            ch.sendMessage(end);
        
        }
        
    }

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    public void sendMessage(String message) {
        writer.println(message.toUpperCase());

    }

    public void selectedusers(String[] stringarray) {
        this.selectedUsers = stringarray;
    }

    @Override
    public void run() {
        try {
            input = new Scanner(socket.getInputStream());
            writer = new PrintWriter(socket.getOutputStream(), true);

            message = input.nextLine(); //IMPORTANT blocking call
            System.out.println("ClientHandler: " + message);
            System.out.println(String.format("Received the message: %1$S ", message));
            while (!message.equals(ProtocolStrings.STOP)) {
                checkMsgProtocol(message);            //Her tjekker vi hvem der skal have beskeden og sender den også

                System.out.println(String.format("Received the message: %1$S ", message.toUpperCase()));
                message = input.nextLine(); //IMPORTANT blocking call
            }
            writer.println(ProtocolStrings.STOP);//Echo the stop message back to the client for a nice closedown
            EchoServer.clientHandlers.remove(this);
            socket.close();
            System.out.println("Closed a Connection");
        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
