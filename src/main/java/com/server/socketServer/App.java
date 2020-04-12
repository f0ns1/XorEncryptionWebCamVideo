package com.server.socketServer;

import java.io.IOException;
import java.net.ServerSocket;

import javax.swing.JPanel;

import com.server.socketServer.actions.Connection;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Server launching thread ........" );
        Connection conn = new Connection();
        conn.VOIPConnection();
    }
}
