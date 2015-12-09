package network.framework;

import java.io.IOException;

import network.core.TempHub;
import network.core.connections.threads.Hub;

public class GameHub {
	private final static int PORT = 6969;
    
    public static void main(String[] args) {
        try {
            new TempHub(PORT); 
        }
        catch (IOException e) {
            System.out.println("Can't create listening socket.  Shutting down.");
        }
    }
}
