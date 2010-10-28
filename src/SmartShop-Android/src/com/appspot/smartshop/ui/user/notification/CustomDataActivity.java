package com.appspot.smartshop.ui.user.notification;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import com.appspot.smartshop.R;
import com.xtify.android.sdk.NotificationActivity;

/**
 * This is a sample Activity that responds to a custom notification action. The
 * data is retrieved from the Intent and in this case, it is displayed in a text
 * box. To launch this Activity, your notification would set the action to
 * "com.acmelabs.DISPLAY_CUSTOM_DATA" in order to match the intent filter
 * defined in AndroidManifest.xml.
 */
public class CustomDataActivity extends NotificationActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("CustomDataActivity", "Running");
		setContentView(R.layout.custom_data);
		Intent intent = getIntent();
		String data = decodeUriData(intent.getData());
		if (data != null) {
			EditText dataTextBox = (EditText) findViewById(R.id.dataTextBox);
			dataTextBox.setText(data);
		}
	}
};