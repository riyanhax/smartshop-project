package com.appspot.smartshop.ui.product;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.appspot.smartshop.R;
import com.appspot.smartshop.utils.Global;

/**
 * Demonstrates expandable lists using a custom {@link ExpandableListAdapter}
 * from {@link BaseExpandableListAdapter}.
 */
public class CategoryDialogActivity extends Activity {
	public static final String TAG = "[CategoryDialogActivity]";
	public static final int PICK_CATEGORIES = 0;
	ExpandableListAdapter mAdapter;
	private String type;
	private ArrayList<String> selectedCategories = new ArrayList<String>();
	ExpandableListView expandableListView;
	Button btnFinish;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.category_dialog_activity);

		// type (page or product)
		type = getIntent().getExtras().getString(Global.TYPE);

		// search button
		btnFinish = (Button) findViewById(R.id.btnFinish);
		btnFinish.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getSelectedCategories();
			}
		});

		// get categories infori
		// Bundle bundle = getIntent().getExtras();
		// CategoryInfo categoryInfo = (CategoryInfo)
		// bundle.get(Global.CATEGORY_INFO);
		// String[] groups = categoryInfo.parentCategory;
		// String[][] children = categoryInfo.childrenCategory;
		String[] groups= { "Áo quần", "Giày dép", "Mỹ phẩm",
				"Váy dự tiệc" };
		String[][] children = { { "Nam", "Nữ" },
				{ "Mỹ", "Trung Quốc", "Việt Nam" }, { "Son môi", "Tào Lao" },
				{ "Đầm bầu", "Váy hiệu", "Hàng Fake" } };
		// list view
		expandableListView = (ExpandableListView) findViewById(R.id.listCategoryDialog);
		mAdapter = new MyExpandableListAdapter(groups, children);
		expandableListView.setAdapter(mAdapter);
	}

	protected void getSelectedCategories() {
		Intent intent = new Intent(CategoryDialogActivity.this,
				ProductBasicAttributeActivity.class);
		intent.putExtra(Global.SELECTED_CATEGORIES, selectedCategories);
		setResult(RESULT_OK, intent);
		finish();
	}

	class MyExpandableListAdapter extends BaseExpandableListAdapter {
		public String[] groups;
		public String[][] children;

		public MyExpandableListAdapter(String[] groups, String[][] children) {
			this.groups = groups;
			this.children = children;
		}

		public Object getChild(int groupPosition, int childPosition) {
			return children[groupPosition][childPosition];
		}

		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		public int getChildrenCount(int groupPosition) {
			return children[groupPosition].length;
		}

		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) getBaseContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = inflater.inflate(R.layout.parent_category, null);
			TextView textView = (TextView) view
					.findViewById(R.id.txtParentCategory);
			textView.setText(groups[groupPosition]);
			return view;
		}

		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) getBaseContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.child_category, null);
			final TextView txtView = (TextView) convertView
					.findViewById(R.id.txtchildCategory);
			txtView.setText(children[groupPosition][childPosition]);
			final CheckBox chSubCategory = (CheckBox) convertView
					.findViewById(R.id.checkBox);
			chSubCategory
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							if (isChecked) {
								String subCategory = txtView.getText()
										.toString();
								if (!selectedCategories.contains(subCategory)) {
									selectedCategories.add(subCategory);
								}
							}
						}
					});
			return convertView;
		}

		public Object getGroup(int groupPosition) {
			return groups[groupPosition];
		}

		public int getGroupCount() {
			return groups.length;
		}

		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

		public boolean hasStableIds() {
			return true;
		}
	}
}
