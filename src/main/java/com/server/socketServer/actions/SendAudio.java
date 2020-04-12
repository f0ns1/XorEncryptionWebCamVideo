package com.server.socketServer.actions;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

public class SendAudio implements Runnable {
	private byte[] tempBuffer;
	private DatagramSocket socket;
	private InetAddress ip;
	private int port;

	public SendAudio(DatagramSocket socket, InetAddress ip, int port)	{
		this.socket = socket;
		this.ip = ip;
		this.port = port;
		this.tempBuffer = new byte[4000];
	}


	public void run()	{  
		try {

			boolean stopCapture = false;

			//Get everything set up for capture
			AudioFormat audioFormat = getAudioFormat();
			DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
			TargetDataLine targetDataLine = (TargetDataLine)
			AudioSystem.getLine(dataLineInfo);
			targetDataLine.open(audioFormat);
			targetDataLine.start();

			try{
				//Loop until stopCapture is set by another thread.
				while(!stopCapture){
					//Read data from the internal buffer of the data line.
					int cnt = targetDataLine.read(tempBuffer, 0, tempBuffer.length);
					if(cnt > 0){
						DatagramPacket outPacket = new DatagramPacket(tempBuffer, tempBuffer.length, this.ip, this.port);
						this.socket.send(outPacket);
					}
				}
			}catch (Exception e) {}

		} catch (Exception e) {}
	}
	
	private AudioFormat getAudioFormat(){
		float sampleRate = 8000.0F;
		//8000,11025,16000,22050,44100
		int sampleSizeInBits = 16;
		//8,16
		int channels = 1;
		//1,2
		boolean signed = true;
		//true,false
		boolean bigEndian = false;
		//true,false
		return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
	}
}
