package com.server.socketServer;

import java.awt.Color;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.socket.frame.Frame;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

public class VideoServerThread extends Thread {
	private ServerSocket serverSocket;
	int videoServerPort;
	private Socket socket;
	private JPanel contentPanel;
	private boolean calling;

	public VideoServerThread(ServerSocket serverSocket, int videoServerPort, JPanel panel, boolean calling) {
		this.serverSocket = serverSocket;
		this.videoServerPort = videoServerPort;
		this.contentPanel = panel;
		this.calling = calling;
	}

	@Override
	public void run() {
		System.out.println("Video Server opened!");
		try {

			contentPanel = new JPanel();
			JFrame frame = new JFrame();
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			Window window = new Window(frame);
			window.setBounds(0, 0, 1280, 720);
			contentPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
			contentPanel.setBackground(Color.darkGray);
			frame.setContentPane(contentPanel);
			frame.setVisible(true);
			frame.setAlwaysOnTop(true);
			serverSocket = new ServerSocket(videoServerPort);
			socket = serverSocket.accept();
			InputStream in = socket.getInputStream();
			ObjectInputStream ois = new ObjectInputStream(in);
			BufferedImage bufferedImage;
			InputStream inputImage;
			Frame f;
			InputStream audio ;
			AudioStream as ;
			while (calling) {
				try {
					Object obj = ois.readObject();
					f = (Frame) obj;
					inputImage = new ByteArrayInputStream(decrypt(f.getImg()));
					bufferedImage = ImageIO.read(inputImage);	
					contentPanel.getGraphics().drawImage(bufferedImage, 0, 0, window);
					byte[] var =decrypt(f.getAudio());
					audio=new ByteArrayInputStream(var);
					AudioInputStream test = new AudioInputStream(audio, getAudioFormat(), var.length);
					bufferedImage.flush();
					inputImage.close();
					AudioPlayer.player.start(test);
					f=null;
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println(e.getMessage());
				}
			}

		}

		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static AudioFormat getAudioFormat() {
        float sampleRate = 16000;
        int sampleSizeInBits = 8;
        int channels = 2;
        boolean signed = true;
        boolean bigEndian = true;
        AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits,
                                             channels, signed, bigEndian);
        return format;
    }
	private byte[] decrypt(String bytes) {
		byte[] out = null;
		try {
			byte[] key = "MyPrivateKeyFroEncryption".getBytes();
			MessageDigest sha = MessageDigest.getInstance("SHA-1");
			key = sha.digest(key);
			key = Arrays.copyOf(key, 16);
			SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			out = cipher.doFinal(Base64.getDecoder().decode((bytes)));
		} catch (Exception e) {
			System.out.println("Error while encrypting: " + e.toString());
		}		
		return out;
	}

}
