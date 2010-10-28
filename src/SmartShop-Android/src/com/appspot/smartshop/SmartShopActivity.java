package com.appspot.smartshop;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.appspot.smartshop.adapter.MainAdapter;
import com.appspot.smartshop.dom.SmartshopNotification;
import com.appspot.smartshop.dom.UserInfo;
import com.appspot.smartshop.utils.DataLoader;
import com.appspot.smartshop.utils.Global;
import com.appspot.smartshop.utils.JSONParser;
import com.appspot.smartshop.utils.RestClient;
import com.appspot.smartshop.utils.SimpleAsyncTask;
import com.appspot.smartshop.utils.StringUtils;
import com.appspot.smartshop.utils.URLConstant;
import com.appspot.smartshop.utils.Utils;
import com.xtify.android.sdk.PersistentLocationManager;

public class SmartShopActivity extends ListActivity {
	public static final String TAG = "[SmartShopActivity]";

	public static final String PRODUCT = "product";
	public static final String PAGE = "page";
	// public static final String PARAM_NOFITICATION =
	// "{username:\"%s\",type_id:%d}";
	private List<SmartshopNotification> notifications;
	private int numOfNotifications = 0;
	private SimpleAsyncTask task;
	NotificationManager notificationManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Global.application = this;

		ListView listView = getListView();

		// Check whether user login or not
		Global.persistentLocationManager = new PersistentLocationManager(
				SmartShopActivity.this);
		Global.persistentLocationManager
				.setNotificationIcon(R.drawable.notification);
		Global.persistentLocationManager
				.setNotificationDetailsIcon(R.drawable.icon);
		if (Global.userInfo == null) {
			String session = Utils.loadSession();
			Log.d(TAG, "Session: " + session);
			if (StringUtils.isEmptyOrNull(session))
				Global.isLogin = false;
			else {
				String url = String.format(URLConstant.GET_USER_INFO_SESSION,
						session);
				RestClient.getData(url, new JSONParser() {

					@Override
					public void onSuccess(JSONObject json) throws JSONException {
						Global.isLogin = true;
						Global.userInfo = Global.gsonDateWithoutHour
								.fromJson(json.get("userinfo").toString(),
										UserInfo.class);

						// Start Xtify Thread
						Thread xtifyThread = new Thread(new Runnable() {
							@Override
							public void run() {
								Log.e(TAG, "Thread Xtify run");

								boolean trackLocation = Global.persistentLocationManager
										.isTrackingLocation();
								boolean deliverNotifications = Global.persistentLocationManager
										.isDeliveringNotifications();
								if (trackLocation || deliverNotifications) {
									Global.persistentLocationManager
											.startService();
								}
							}
						});
						xtifyThread.start(); // to avoid Android's
						// application-not-responding
					}

					@Override
					public void onFailure(String message) {
						Log.d(TAG, message);
					}
				});
			}

		}
		listView.setAdapter(new MainAdapter(this));

		if (Global.isLogin) {
			notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			Log.d(TAG, "[LOAD NOTIFICATIONS]");
			loadNotifications();
		}

		/**************************** Init data *********************************/
		Log.d(TAG, "[START NOTIFICATION SERVICE]");
		// TODO start service notification
		// startService(new Intent(this, NotifyingService.class));

		if (Global.mapParentCategories.size() != 0) {
			Log.d(TAG, "[DONT NEED TO GET CATEGORIES LIST]");
		} else {
			Log.d(TAG, "[GET CATEGORIES LIST]");
			task = new SimpleAsyncTask(getString(R.string.init_data), this,
					new DataLoader() {

						@Override
						public void updateUI() {
						}

						@Override
						public void loadData() {
							getCategoriesList();
						}
					});
			task.execute();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, 0, 0, getString(R.string.settings));
		menu.findItem(0).setIcon(R.drawable.menu_preferences);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0: {
			Global.persistentLocationManager.showSettingsActivity(this, true);
			return true;
		}
		}
		return false;
	}

	private void getCategoriesList() {
		// get parent categories
		RestClient.getData(URLConstant.GET_PARENT_CATEGORIES, new JSONParser() {

			@Override
			public void onSuccess(JSONObject json) throws JSONException {
				JSONArray arr = json.getJSONArray("categories");

				int len = arr.length();
				String name, key_cat;
				for (int i = 0; i < len; ++i) {
					name = arr.getJSONObject(i).getString("name");
					key_cat = arr.getJSONObject(i).getString("key_cat");

					Global.mapParentCategories.put(key_cat, name);
				}
			}

			@Override
			public void onFailure(String message) {
				task.hasData = false;
				task.message = message;
				task.cancel(true);

				Toast.makeText(SmartShopActivity.this,
						getString(R.string.cannot_connect_network),
						Toast.LENGTH_SHORT).show();
			}
		});

		// get child categories
		for (final String parentId : Global.mapParentCategories.keySet()) {
			String url = String.format(URLConstant.GET_CHILD_CATEGORIES,
					parentId);

			RestClient.getData(url, new JSONParser() {

				@Override
				public void onSuccess(JSONObject json) throws JSONException {
					JSONArray arr = json.getJSONArray("categories");

					int len = arr.length();
					String name, key_cat;
					String[] temp = new String[len];
					for (int k = 0; k < len; ++k) {
						name = arr.getJSONObject(k).getString("name");
						key_cat = arr.getJSONObject(k).getString("key_cat");

						temp[k] = name;

						Global.mapChildrenCategories.put(key_cat, name);
						Global.mapChildrenCategoriesName.put(name, key_cat);
					}

					Global.listCategories.add(temp);
				}

				@Override
				public void onFailure(String message) {
					task.hasData = false;
					task.message = message;
					task.cancel(true);
				}
			});
		}
	}

	private void loadNotifications() {
		// String param = String.format(PARAM_NOFITICATION,
		// Global.userInfo.username, 1);
		// Log.d(TAG, param);

		String url = String.format(URLConstant.GET_NOTIFICATIONS,
				Global.userInfo.username, 1, Global.lastupdateNoti);
		RestClient.getData(url, new JSONParser() {
			@Override
			public void onSuccess(JSONObject json) throws JSONException {
				// load
				JSONArray arr = json.getJSONArray("notifications");
				notifications = Global.gsonWithHour.fromJson(arr.toString(),
						SmartshopNotification.getType());
				Log
						.d(TAG, "found " + notifications.size()
								+ " notification(s)");

				// display
				if (notifications.size() == 0) {
					Toast.makeText(SmartShopActivity.this, getString(R.string.warn_no_notification), 
							Toast.LENGTH_SHORT).show();
				} else {
					String content;

					int count = 0;
					for (SmartshopNotification notification : notifications) {
						content = notification.content;
						showNotification(count + numOfNotifications, notification.getTitle(),
								content);
						count++;
					}

					numOfNotifications += notifications.size();
				}
			}

			@Override
			public void onFailure(String message) {
				Toast.makeText(SmartShopActivity.this, message, Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void showNotification(int id, String title, String content) {
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, SmartShopActivity.class), 0);

		Notification notification = new Notification(
				android.R.drawable.btn_star_big_on, null, System
						.currentTimeMillis());
		notification.setLatestEventInfo(this, title, content, contentIntent);
		// TODO custom view for notification

		notificationManager.notify(id, notification);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		Log.d(TAG, "[STOP NOTIFICATION SERVICE]");
		// Global.isStop = true;
		// TODO stop service notification
		// stopService(new Intent(this, NotifyingService.class));

		// store session
		if (Utils.isLogined())
			Utils.storeSessionState(Global.userInfo.sessionId);
	}
}
