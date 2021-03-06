package com.appspot.smartshop.ui.user;

import java.util.Arrays;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.appspot.smartshop.R;
import com.appspot.smartshop.adapter.AddFriendAdapter;
import com.appspot.smartshop.dom.UserInfo;
import com.appspot.smartshop.ui.BaseUIActivity;
import com.appspot.smartshop.utils.DataLoader;
import com.appspot.smartshop.utils.Global;
import com.appspot.smartshop.utils.JSONParser;
import com.appspot.smartshop.utils.RestClient;
import com.appspot.smartshop.utils.SimpleAsyncTask;
import com.appspot.smartshop.utils.URLConstant;
import com.appspot.smartshop.utils.Utils;

public class AddFriendActivity extends BaseUIActivity {
	public static final String TAG = "AddFriendActivity";
	public static final String PARAM_SEARCH_FRIENDS = "{username:\"%s\"}";
	public static final String PARAM_ADD_FRIENDS = "{friends:%s}";
	public static String friendsToAdd = "";
	public AddFriendAdapter adapter;
	public ListView friendList;
	public LinkedList<UserInfo> friends;
	public Button btnFriendSearch;
	public TextView txtFriendSearch;
	public Button btnAddFriend;

	@Override
	protected void onCreatePre() {
		setContentView(R.layout.add_friend);
	}

	@Override
	protected void onCreatePost(Bundle savedInstanceState) {
		// TODO: just for test, please remove after test
		Global.application = this;

		friends = new LinkedList<UserInfo>();
		friendList = (ListView) findViewById(R.id.listFriend);
		txtFriendSearch = (TextView) findViewById(R.id.txtFriendSearch);
		btnFriendSearch = (Button) findViewById(R.id.btnFriendSearch);
		btnFriendSearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				loadFriendList();

			}
		});
		btnAddFriend = (Button) findViewById(R.id.btnAddFriends);
		btnAddFriend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (friendsToAdd.length() == 0) {
					Toast.makeText(AddFriendActivity.this,
							getString(R.string.warn_not_choose_user),
							Toast.LENGTH_SHORT).show();
				} else {
					addFriends();
				}
			}
		});
		adapter = new AddFriendAdapter(this, 0, new LinkedList<UserInfo>());
		friendList.setAdapter(adapter);
		// Log.d(TAG, "is executed");
		// loadFriendList();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, getString(R.string.return_to_home)).setIcon(R.drawable.home);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == 0) {
			Utils.returnHomeActivity(this);
		}
		return super.onOptionsItemSelected(item);
	}
	
	public static SimpleAsyncTask taskAddFriend;
	boolean isSuccess = false;

	protected void addFriends() {
		isSuccess = false;
		final String param = String.format(PARAM_ADD_FRIENDS, friendsToAdd);

		taskAddFriend = new SimpleAsyncTask(this, new DataLoader() {

			@Override
			public void updateUI() {

			}

			@Override
			public void loadData() {
				RestClient.postData(String.format(URLConstant.ADD_FRIENDS_TO_LIST, Global.getSession()), 
						param, new JSONParser() {

							@Override
							public void onSuccess(JSONObject json)
									throws JSONException {
								updateUserFriendList();
								friendsToAdd = "";
								isSuccess = true;
								Log.d(TAG + "onSuccess", "" + isSuccess);
							}

							@Override
							public void onFailure(String message) {
								isSuccess = false;
								Log.d(TAG + "onFailure", "" + isSuccess);
							}
						});
			}
		});
		taskAddFriend.execute();
		Toast.makeText(AddFriendActivity.this,
				getString(R.string.addFriendSuccess), Toast.LENGTH_SHORT)
				.show();
	}
	
	private void updateUserFriendList() {
		String[] friends = friendsToAdd.split(",");
		Log.d(TAG, "new friends  = " + Arrays.toString(friends));
		for (String friend : friends) {
			friend = friend.trim();
			Global.userInfo.setFriendsUsername.add(friend);
		}
	}

	public static SimpleAsyncTask task;

	protected void loadFriendList() {
		String param = String.format(PARAM_SEARCH_FRIENDS, txtFriendSearch
				.getText().toString());
		Log.d(TAG, param);
		task = new SimpleAsyncTask(this, new DataLoader() {
			@Override
			public void updateUI() {
				adapter = new AddFriendAdapter(AddFriendActivity.this,
						R.layout.add_friend_item, friends);
				friendList.setAdapter(adapter);

			}

			String url = URLConstant.SEARCH_FRIEND_BY_QUERY
					+ txtFriendSearch.getText().toString();

			@Override
			public void loadData() {
				RestClient.getData(url, new JSONParser() {
					@Override
					public void onSuccess(JSONObject json) throws JSONException {
						JSONArray arr = json.getJSONArray("userinfos");
						friends = Global.gsonWithHour.fromJson(arr.toString(),
								UserInfo.getType());
					}

					@Override
					public void onFailure(String message) {
						// TODO Auto-generated method stub

					}
				});
			}
		});
		task.execute();
	}

}
