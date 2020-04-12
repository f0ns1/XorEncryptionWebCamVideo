package com.server.socketServer.actions;

import java.net.DatagramSocket;
import java.net.InetAddress;

public class Connection {

	private int receive_port_audio=9992;
	private int receive_port_video=9991;
	private int send_port=9991;
	private String send_socket;
	private InetAddress client_ip;

	public void VOIPConnection() {
		// Assign the target IP address
		try {
			client_ip = InetAddress.getByName("127.0.0.1");
		} catch (Exception e) {
			System.out.println("Error: Client received invalid IP address.");
		}
		// Initiate sockets to use for audio streaming
		DatagramSocket receive_socket_audio = null;
		DatagramSocket receive_socket_video = null;
		//DatagramSocket send_socket = null;
		try {
			receive_socket_audio = new DatagramSocket(receive_port_audio);
			receive_socket_video = new DatagramSocket(receive_port_video);
			//send_socket = new DatagramSocket(send_port);
		} catch (Exception e) {
			e.printStackTrace();
		}

		//Thread CaptureAudio = new Thread(new SendAudio(send_socket, client_ip, receive_port));
		//CaptureAudio.start();

		Thread PlayAudio = new Thread(new PlayAudio(receive_socket_audio));
		PlayAudio.start();
		Thread PlayVideo = new Thread(new PlayVideo(receive_socket_video));
		PlayVideo.start();
	}

}
