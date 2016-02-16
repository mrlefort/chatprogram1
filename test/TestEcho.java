/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import echoclient.Client;
import echoserver.Server;
import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Steffen
 */
public class TestEcho {

    public TestEcho() {
    }

    @BeforeClass
    public static void setUpClass() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Server.main(null);
            }
        }).start();
    }

    @AfterClass
    public static void tearDownClass() {
        Server.stopServer();
    }

    @Test
    public void send() throws IOException {
        Client client = new Client("localhost", 9999);
        client.connect("localhost", 9999);
        client.send("Hello");
        assertEquals("HELLO", client.receive());
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
}
