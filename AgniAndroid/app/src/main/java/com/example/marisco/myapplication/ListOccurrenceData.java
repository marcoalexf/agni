package com.example.marisco.myapplication;

public class ListOccurrenceData {

	public LoginResponse token;
	public boolean showPrivate = false;
	public String username;
	public String cursor;
	public Float lat;
	public Float lon;
	public int radius = 10000;
	
	public ListOccurrenceData() {
		
	}
	
	public ListOccurrenceData(LoginResponse token, boolean showPrivate, String username, String cursor,
				Float lat, Float lon, Integer radius) {
		this.token = token;
		this.showPrivate = showPrivate;
		this.username = username;
		this.cursor = cursor;
		this.lat = lat;
		this.lon = lon;
		if(radius != null) {
			this.radius = radius;
		}
	}
	
}
