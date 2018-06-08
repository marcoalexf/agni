package com.example.marisco.myapplication;

public class ListOccurrenceData {

	public ProfileResponse token;
	public boolean showPrivate = false;
	public String username;
	public String cursor;
	
	public ListOccurrenceData() {
		
	}
	
	public ListOccurrenceData(ProfileResponse token, boolean showPrivate, String username, String cursor) {
		this.token = token;
		this.showPrivate = showPrivate;
		this.username = username;
		this.cursor = cursor;
	}
	
}
