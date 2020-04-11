package com.socket.frame;

import java.io.Serializable;

public class Frame implements Serializable{

	private String bytes;
	
	public Frame(String bytes) {
		this.bytes = bytes;
	}

	public int size() {
		return bytes.length();
	}
	
	public String getBytes() {
		return bytes;
	}

	public void setBytes(String bytes) {
		this.bytes = bytes;
	}

	
}
