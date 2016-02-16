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

public class EchoClient extends Observable implements Runnable
{

    Socket socket;
    private int port;
    private InetAddress serverAddress;
    private Scanner input;
    private PrintWriter output;
    ArrayList<String> allMsg = new ArrayList();

    String ip;

    public void connect(String address, int port) throws UnknownHostException, IOException
    {
        this.port = port;
        serverAddress = InetAddress.getByName(address);
        socket = new Socket(serverAddress, port);
        input = new Scanner(socket.getInputStream());
        output = new PrintWriter(socket.getOutputStream(), true);  //Set to true, to get auto flush behaviour
    }

    public void send(String msg)
    {
        System.out.println("Send method: " + msg);
        output.println(msg);

    }

//    public void stop() throws IOException {
//        output.println("LOGOUT#");
//    }
    public String receive()
    {
     
            String msg = input.nextLine();
            System.out.println("Her er det vi har modtaget: " + msg);
            allMsg.add(msg + "\n");

            if (msg.equals("LOGOUT#"))
            {
                try
                {
                    socket.close();
                }
                catch (IOException ex)
                {
                    Logger.getLogger(EchoClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            setChanged();
            System.out.println("Her er det vi har modtaget: " + msg);
            notifyObservers(msg);
            return msg;
        
    }

    public EchoClient(String ip, int port) throws IOException
    {
        this.port = port;
        this.ip = ip;
        
    }

    @Override
    public void run()
    {
        try
        {
            connect(ip, port);
        }
        catch (IOException ex)
        {
            Logger.getLogger(EchoClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        while (true)
        {
            System.out.println("RDY TO RECIEVE");
            receive();

        }
    }
}
