package com.socket.frame;

import java.io.Serializable;

public class Frame implements Serializable{

	private byte[] bytes;
	
	public Frame(byte[] bytes) {
		this.bytes = bytes;
	}

	public int size() {
		return bytes.length;
	}
	
	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

	
}
