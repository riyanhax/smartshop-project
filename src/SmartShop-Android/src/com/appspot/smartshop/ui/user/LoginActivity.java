package com.appspot.smartshop.ui.user;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.appspot.smartshop.R;
import com.appspot.smartshop.SmartShopActivity;
import com.appspot.smartshop.dom.SmartshopNotification;
import com.appspot.smartshop.dom.UserInfo;
import com.appspot.smartshop.ui.BaseUIActivity;
import com.appspot.smartshop.utils.DataLoader;
import com.appspot.smartshop.utils.Global;
import com.appspot.smartshop.utils.JSONParser;
import com.appspot.smartshop.utils.RestClient;
import com.appspot.smartshop.utils.SimpleAsyncTask;
import com.appspot.smartshop.utils.StringUtils;
import com.appspot.smartshop.utils.URLConstant;
import com.appspot.smartshop.utils.Utils;

public class LoginActivity extends BaseUIActivity {
	public static final String TAG = "[LoginActivity]";
	public static final String PARAM_NOFITICATION = "{username:\"%s\",type_id:%d}";
	private TextView lblUsername;
	private EditText txtUsername;
	private TextView lblPassword;
	private EditText txtPassword;
	private String lastActivity;
	public static String charTicker = "Bạn có %d thông báo mới";
	public List<SmartshopNotification> notifications;

	@Override
	protected void onCreatePre() {
		setContentView(R.layout.login);
	}

	@Override
	protected void onCreatePost(Bundle savedInstanceState) {
		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay();
		int width = display.getWidth();
		int labelWidth = (int) (width * 0.25);
		int textWidth = width - labelWidth;

		// set up textviews
		lblUsername = (TextView) findViewById(R.id.lblUsername);
		lblUsername.setWidth(labelWidth);
		txtUsername = (EditText) findViewById(R.id.txtUsername);
		txtUsername.setWidth(textWidth);

		lblPassword = (TextView) findViewById(R.id.lblPassword);
		lblPassword.setWidth(labelWidth);
		txtPassword = (EditText) findViewById(R.id.txtPassword);
		txtPassword.setWidth(textWidth);

		// butons
		Button btnLogin = (Button) findViewById(R.id.btnLogin);
		btnLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				login();
			}
		});

		Button btnCancel = (Button) findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		lastActivity = getIntent().getStringExtra(Global.LOGIN_LAST_ACTIVITY);
	}

	private SimpleAsyncTask task;

	protected void login() {
		String username = txtUsername.getText().toString();
		String pass = txtPassword.getText().toString();
		if (username == null || username.trim().equals("")
				|| pass == null || pass.trim().equals("")) {
			Toast.makeText(this, "Điền tên đăng nhập và mật khẩu", Toast.LENGTH_SHORT).show();
			return;
		}
		
		final String userkey = Utils.getAlphaNumeric(20);
		final String url = String.format(URLConstant.LOGIN, username, Utils
				.getMD5(pass), userkey);

		task = new SimpleAsyncTask(getString(R.string.loading_when_login), this, new DataLoader() {
						
			public void updateUI() {
			}

			@Override
			public void loadData() {
				RestClient.getData(url, new JSONParser() {

					@Override
					public void onSuccess(JSONObject json) throws JSONException {
						Global.isLogin = true;
						Global.userInfo = Global.gsonDateWithoutHour.fromJson(json.get("userinfo").toString(), UserInfo.class);
						Log.e(TAG, "[CURRENT SESSION] " + Global.userInfo.sessionId);
						
						if (StringUtils.isEmptyOrNull(lastActivity)){
							Intent intent = new Intent(LoginActivity.this, SmartShopActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(intent);
						}else{
							if (lastActivity.equals(Global.VIEW_PROFILE_ACTIVITY)){
								Intent intent = new Intent(LoginActivity.this, ViewUserProfileActivity.class);
								intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(intent);
							}
						}
					}

					@Override
					public void onFailure(String message) {
						task.hasData = false;
						task.message = message;
						task.cancel(true);
					}
				});
			}
		});

		task.execute();
	}

	public void generateNotification(int numOfNewNotification,
			int notificatioinID, CharSequence charTitle,
			CharSequence charContent) {
		NotificationManager myNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		long when = System.currentTimeMillis();
		charTicker = String.format(charTicker, numOfNewNotification);
		Notification notification = new Notification(
				android.R.drawable.btn_star_big_on, charTicker, when);
		Context context = getApplicationContext();
		Intent notificationIntent = new Intent(this, SmartShopActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, 0);
		notification.setLatestEventInfo(context, charTitle, charContent,
				contentIntent);
		myNotificationManager.notify(notificatioinID, notification);
	}

}
