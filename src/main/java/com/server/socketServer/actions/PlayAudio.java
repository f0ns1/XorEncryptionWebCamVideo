package com.server.socketServer.actions;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

import sun.audio.AudioPlayer;

/* This method receives audio input
 * from a UDP packet and plays it.
 */
class PlayAudio implements Runnable {

	private DatagramSocket socket;
	private byte[] tempBuffer;

	public PlayAudio(DatagramSocket socket)	{
		this.socket = socket;
		this.tempBuffer = new byte[4016];
	}

	public void run()	{
		try{

			DatagramPacket inPacket;
			boolean stopPlay = false;

			//Loop until stopPlay is set by another thread.
			while (!stopPlay)	{
				//Put received data into a byte array object
				inPacket = new DatagramPacket(tempBuffer, 4016);
				this.socket.receive(inPacket);
				byte[] audioData = decrypt(inPacket.getData());
				//Get an input stream on the byte array containing the data
				InputStream byteArrayInputStream = new ByteArrayInputStream(audioData);
				AudioFormat audioFormat = getAudioFormat();
				AudioInputStream audioInputStream = new AudioInputStream(byteArrayInputStream, audioFormat, audioData.length/audioFormat.getFrameSize());
				DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
				SourceDataLine sourceDataLine = (SourceDataLine)
				AudioSystem.getLine(dataLineInfo);
				sourceDataLine.open(audioFormat);
				sourceDataLine.start();

				try { 
					int cnt;
					//Keep looping until the input read method returns -1 for empty stream.
					while((cnt = audioInputStream.read(tempBuffer, 0, tempBuffer.length)) != -1){
						if(cnt > 0){
							//Write data to the internal buffer of the data line where it will be delivered to the speaker.
							sourceDataLine.write(tempBuffer, 0, cnt);
						}
					}
					sourceDataLine.drain();
					sourceDataLine.close();					
				}catch (Exception e) {
					
					e.printStackTrace();
				}
				
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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
			System.out.println("Error while encrypting: " + e.toString());
		}		
		return out;
	}

}
