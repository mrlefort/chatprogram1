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
    Scanner input;
    PrintWriter writer;
    

    public void checkMsgProtocol(){
        //feks. switch til at finde ud af hvilken protocolString det er.
        //denne metode kan passende kalde sendMsgToSpecific(): 
        //kan også passende kalde sendMessageToAll();
    }
    
    public void sendMsgToSpecific(){
        //kode til at sende til specific person
    }
    
    static public void sendMessageToAll(String messageToAll){
        //der skal nok en form for switch ind her, så man kan bestemme hvem man vil skrive til.
        for (ClientHandler ch : clientHandlers) {
            ch.sendMessage(messageToAll);
        }
    }
    
    
    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    public void sendMessage(String message) {
        writer.println(message.toUpperCase());
        
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
                sendMessageToAll(message);             //Her bliver beskeden sendt ud til ALLE klienter.
                
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
