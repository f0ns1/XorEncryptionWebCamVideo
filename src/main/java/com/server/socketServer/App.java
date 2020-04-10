package com.server.socketServer;

import java.io.IOException;
import java.net.ServerSocket;

import javax.swing.JPanel;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Server launching thread ........" );
        //String serverSocket="127.0.0.1";
        ServerSocket serverSocket=null;
		try {
			serverSocket = new ServerSocket();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        int videoServerPort=8989;
        JPanel panel = new JPanel();
        VideoServerThread video = new VideoServerThread(serverSocket, videoServerPort, panel, true);
        video.run();
    
    }
}
