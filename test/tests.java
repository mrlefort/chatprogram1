/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import echoclient.ClientGui;
import echoclient.EchoClient;
import echoserver.EchoServer;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.omg.CORBA.TIMEOUT;

/**
 *
 * @author Steffen
 */
public class tests implements Observer{

    /**
     *
     */
    
    static EchoClient ec;
    
    
    
    ClientGui cg;
    
    @BeforeClass
    public static void setUpClass(){
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                EchoServer.main(null);
//            }
//        }).start();
    }

    @AfterClass
    public static void tearDownClass() {
       
        ec.send("LOGOUT#");
    }
    
    private CountDownLatch lock = new CountDownLatch(1);

    
    private String receivedData = "";
    
    @Test
    public void send() throws IOException, InterruptedException {
        cg = new ClientGui();
        ec = new EchoClient("13.70.194.148", 9999, cg);
        Thread t1 = new Thread(ec);
        t1.start();
        ec.addObserver(this);
        
        Thread.sleep(500);
        ec.send("USER#Steffen");
        

        lock.await(2000, TimeUnit.MILLISECONDS);
        
        
            

        assertEquals("People connected: Steffen", receivedData);
    }
    
    
    @Override
    public void update(Observable o, Object arg) {
        receivedData = (String) arg;
        lock.countDown();
    }
}
