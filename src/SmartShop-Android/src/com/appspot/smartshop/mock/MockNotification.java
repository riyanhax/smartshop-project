package com.appspot.smartshop.mock;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import android.util.Log;

import com.appspot.smartshop.dom.Notification;

public class MockNotification {
	public static final String TAG = "[MockNotification]";
	public static final int NUM_OF_NOTIFICATION = 10;

	public static List<Notification> getInstance() {
		Log.d(TAG, "MockNotification has created");
		List<Notification> list = new LinkedList<Notification>();
		for (int i = 0; i < NUM_OF_NOTIFICATION; i++) {
			Notification notification = new Notification("Co " + i
					+ "  san pham may tinh hop voi nhu cau cua ban", new Date(
					89, 11, 11), "loi");
			list.add(notification);
			Log.d(TAG, list.get(i).content);
		}

		return list;
	}
}
