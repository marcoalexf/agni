package com.example.marisco.myapplication;

import com.google.gson.annotations.SerializedName;

public class OccurrenceData {

	@SerializedName("token") LoginResponse token;

	@SerializedName("title") String title;

	@SerializedName("description") String description;

	@SerializedName("type")	 String type;

	@SerializedName("level") int level;

	@SerializedName("visibility") boolean visibility;

	@SerializedName("lat") double lat;

	@SerializedName("lon") double lon;

	@SerializedName("notificationOnResolve") boolean notificationOnResolve;
	
	public OccurrenceData(LoginResponse token, String title, String description, String type, int level,
			boolean visibility, double lat, double lon, boolean notificationOnResolve) {
		this.token = token;
		this.title = title;
		this.description = description;
		this.type = type;
		this.level = level;
		this.visibility = visibility;
		this.lat = lat;
		this.lon = lon;
		this.notificationOnResolve = notificationOnResolve;
	}

	public LoginResponse getToken() {
		return token;
	}

	public void setToken(LoginResponse token) {
		this.token = token;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public boolean isVisibility() {
		return visibility;
	}

	public void setVisibility(boolean visibility) {
		this.visibility = visibility;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public boolean isNotificationOnResolve() {
		return notificationOnResolve;
	}

	public void setNotificationOnResolve(boolean notificationOnResolve) {
		this.notificationOnResolve = notificationOnResolve;
	}
}
