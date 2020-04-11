package com.server.socketServer;

import java.awt.Color;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
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
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.socket.frame.Frame;

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
			while (calling) {
				try {
					Object obj = ois.readObject();
					f = (Frame) obj;
					inputImage = new ByteArrayInputStream(decrypt(f.getBytes()));
					bufferedImage = ImageIO.read(inputImage);
					System.out.println("read image ..... " + bufferedImage);
					System.out.println(contentPanel.getWidth());
					System.out.println(contentPanel.getHeight());
					System.out.println(window);
					System.out.println(contentPanel.getGraphics());
					//contentPanel.getGraphics().drawImage(bufferedImage, 0, 0, contentPanel.getWidth(), contentPanel.getHeight(), window);
					contentPanel.getGraphics().drawImage(bufferedImage, 0, 0, window);

					bufferedImage.flush();
					inputImage.close();
					f = null;
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
