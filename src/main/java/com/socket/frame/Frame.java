package com.socket.frame;

import java.io.Serializable;

public class Frame implements Serializable{

	private String img;
	private String audio;
	
	public Frame(String img, String audio) {
		this.img = img;
		this.audio= audio;
	}

	public int sizeImg() {
		return img.length();
	}
	public int sizeAudio() {
		return audio.length();
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public String getAudio() {
		return audio;
	}

	public void setAudio(String audio) {
		this.audio = audio;
	}	
	
}
