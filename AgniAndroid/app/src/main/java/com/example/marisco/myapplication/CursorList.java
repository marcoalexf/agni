package com.example.marisco.myapplication;

import java.util.List;
import java.util.Map;

public class CursorList {

	@SuppressWarnings("unused")
	private String cursor;
	@SuppressWarnings("unused")
	private List<Map<String, Object>> mapList;
	
	public CursorList(String cursor, List<Map<String, Object>> mapList) {
		this.cursor = cursor;
		this.mapList = mapList;
	}
	public String getCursor() {
		return cursor;
	}

	public List<Map<String, Object>> getMapList() {
		return mapList;
	}

}
