package echoclient;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EchoClient extends Observable implements Runnable {

    private Socket socket;
    private int port;
    private InetAddress serverAddress;
    private Scanner input;
    private PrintWriter output;

    ArrayList<String> allMsg = new ArrayList();

    private String ip;
    private Scanner scan;
    private String[] splitTheMessage;
    private String[] userArray;
    ClientGui gui;

    private String start;
    private String middle;
    private String end;
    private String userName = "";
    String toShow;

    //figures out what to send back to the clientGui
    public void checkMsgProtocol(String message) {
        start = "";
        middle = "";
        end = "";
        splitMessageFirst(message);

        switch (start) {
            case "USERS":
                toShow = "People connected: " + middle;
                break;
            case "MESSAGE":
                toShow = middle + ": " + end;
                break;

        }

    }

    public void connect(String address, int port) throws UnknownHostException, IOException {
        this.port = port;
        serverAddress = InetAddress.getByName(address);
        socket = new Socket(serverAddress, port);
        input = new Scanner(socket.getInputStream());
        output = new PrintWriter(socket.getOutputStream(), true);  //Set to true, to get auto flush behaviour
    }

    public void send(String msg) {

        output.println(msg);
    }


    public String receive() {
        
        
        String msg = input.nextLine();
        String msg2 = msg;

        if (msg.equals("LOGOUT#")) {
            try {
                socket.close();

                
            } catch (IOException ex) {
                Logger.getLogger(EchoClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        splitTheMessage = msg.split("#");
        if (splitTheMessage[0].equals("USERS")) {
            userArray = splitTheMessage[1].split(",");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(EchoClient.class.getName()).log(Level.SEVERE, null, ex);
            }
            gui.popList(userArray);
        }

        checkMsgProtocol(msg2);

        System.out.println("Her er det vi har modtaget: " + msg);
        System.out.println("Her er det vi skriver: " + toShow);
        allMsg.add(toShow + "\n");

        setChanged();
        System.out.println("Her er det vi har modtaget: " + toShow);
        notifyObservers(toShow);
        return msg;

    }

    public EchoClient(String ip, int port, ClientGui gui) throws IOException {
        this.port = port;
        this.ip = ip;
        this.gui = gui;
    }
    
    
    
    

    //splits the message received
    public void splitMessageFirst(String message) {
        String[] splitTheMessage = message.split("#");

        switch (splitTheMessage.length) {
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
    


    @Override
    public void run() {
        try {
            connect(ip, port);
        } catch (IOException ex) {
            Logger.getLogger(EchoClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        while (true) {
            System.out.println("RDY TO RECIEVE");
            receive();

        }
    }

    public String[] getUserArray() {
        return userArray;
    }
}
