package com.appspot.smartshop.dom;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import com.google.gson.reflect.TypeToken;

public class MyNotification {
	public Long id;
	public String content;
	public Date date;
	public boolean isNew;
	public String username;

	public MyNotification() {
	}

	public MyNotification(String content, Date date, String username) {
		this.content = content;
		this.date = date;
		this.username = username;
		this.isNew = false;
	}

	public static Type getType() {
		return new TypeToken<List<MyNotification>>() {
		}.getType();
	}
}