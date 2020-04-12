package com.server.socketServer.actions;

import java.awt.Color;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import sun.audio.AudioPlayer;

/* This method receives audio input
 * from a UDP packet and plays it.
 */
class PlayVideo implements Runnable {

	private DatagramSocket socket;
	private byte[] tempBuffer;

	public PlayVideo(DatagramSocket socket)	{
		this.socket = socket;
		this.tempBuffer = new byte[40000];
	}

	public void run()	{
		try{

			DatagramPacket inPacket;
			boolean stopPlay = false;
			JPanel contentPanel = new JPanel();
			JFrame frame = new JFrame();
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			Window window = new Window(frame);
			window.setBounds(0, 0, 1280, 720);
			contentPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
			contentPanel.setBackground(Color.darkGray);
			frame.setContentPane(contentPanel);
			frame.setVisible(true);
			frame.setAlwaysOnTop(true);
			
			//Loop until stopPlay is set by another thread.
			while (!stopPlay)	{
				//Put received data into a byte array object
				try {
					inPacket = new DatagramPacket(tempBuffer, 40000);
					this.socket.receive(inPacket);
					//byte[] process = processPacket(inPacket.getData(), inPacket.getLength());
					byte[] input = Arrays.copyOf(inPacket.getData(), inPacket.getLength());
					byte[] videoData = decrypt(input);
					InputStream inputImage = new ByteArrayInputStream(videoData);
					BufferedImage bufferedImage = ImageIO.read(inputImage);	
					contentPanel.getGraphics().drawImage(bufferedImage, 0, 0, window);
					bufferedImage.flush();
					inputImage.close();
				}catch(Exception e) {
					e.printStackTrace();
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private byte[] processPacket(byte[] data, int offset) {
		byte[] dataIn = new byte[data.length -(data.length - offset)];
		int cont =0;
		for(int i =0; i < data.length ; i++) {
			if(i > offset ) {
				dataIn[cont] =data[i];
				cont++;
			}
		}
		System.out.println("data= "+data.length);
		System.out.println("dataIn= "+dataIn.length);
		System.out.println("offSet= "+offset);
		return dataIn;
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
	
	
	private byte[] decrypt(byte[] bytes) {
		byte[] out = null;
		try {
			byte[] key = "MyPrivateKeyFroEncryption".getBytes();
			MessageDigest sha = MessageDigest.getInstance("SHA-1");
			key = sha.digest(key);
			key = Arrays.copyOf(key, 16);
			SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			out = cipher.doFinal(bytes);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error while encrypting: " + e.toString());
		}		
		return out;
	}

}
