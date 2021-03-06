package com.appspot.smartshop.ui.product.vatgia;

import java.net.URLEncoder;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.appspot.smartshop.R;
import com.appspot.smartshop.adapter.NProductVatgiaAdapter;
import com.appspot.smartshop.dom.NProductVatGia;
import com.appspot.smartshop.facebook.utils.FacebookUtils;
import com.appspot.smartshop.utils.DataLoader;
import com.appspot.smartshop.utils.Global;
import com.appspot.smartshop.utils.JSONParser;
import com.appspot.smartshop.utils.RestClient;
import com.appspot.smartshop.utils.SimpleAsyncTask;
import com.appspot.smartshop.utils.URLConstant;

public class SearchVatgiaActivity extends Activity {
	public static final String TAG = "[SearchVatgiaActivity]";
	//set up variable for facebook connection
	//end set up variable for facebook connection
	
	private static final int NO_PAGE = 0;
	
	private EditText txtSearch;
	private ListView listProducts;
	private NProductVatgiaAdapter adapter;
	private TextView txtPageInfo;
	private LinkedList<NProductVatGia> products;
	
	private int currentPage = 1;
	private int numOfPages = NO_PAGE;
	private boolean first = true;
	
	private String query = null;
	private AlertDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_vatgia);
		// search field
		txtSearch = (EditText) findViewById(R.id.txtSearch);
		
		// buttons
		Button btnSearch = (Button) findViewById(R.id.btnSearch);
		btnSearch.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				query = txtSearch.getText().toString(); 
				query = URLEncoder.encode(query);
				if (query != null && !query.trim().equals("")) {
					searchProductsByQuery(URLEncoder.encode(query));	
				}
			}
		});
		
		btnNext = (Button) findViewById(R.id.btnNext);
		btnNext.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (numOfPages != NO_PAGE) {
					if (currentPage < numOfPages && (query != null || !query.trim().equals(""))) {
						currentPage++;
						searchProductsByQuery(query);
					}
				}
			}
		});
		btnNext.setVisibility(View.GONE);
		
		btnPrev = (Button) findViewById(R.id.btnPrev);
		btnPrev.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (numOfPages != NO_PAGE) {
					if (currentPage > 1 && (query != null || !query.trim().equals(""))) {
						currentPage--;
						searchProductsByQuery(query);
					}
				}
			}
		});
		btnPrev.setVisibility(View.GONE);
		
		// list view
		listProducts = (ListView) findViewById(R.id.listVatgiapProducts);
		
		// text fields
		txtPageInfo = (TextView) findViewById(R.id.txtPageInfo);
		txtPageInfo.setVisibility(View.GONE);
		
		// TODO: search products based on price range 
		Button btnPriceRange = (Button) findViewById(R.id.btnPriceRange);
		btnPriceRange.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				createProductPriceDialog();
			}
		});
	}
	
	private void createProductPriceDialog() {
		LayoutInflater inflater = LayoutInflater.from(this);
		final View view = inflater.inflate(R.layout.product_price_range_dialog, null);
		
		Button btnSearch = (Button) view.findViewById(R.id.btnSearch);
		btnSearch.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				EditText txtFromPrice = (EditText) view.findViewById(R.id.txtFromPrice);
				EditText txtToPrice = (EditText) view.findViewById(R.id.txtToPrice);
				
				query = txtSearch.getText().toString();
				url = String.format(URLConstant.GET_VATGIA_PRODUCTS, query, currentPage);
				
				String fromPrice = txtFromPrice.getText().toString();
				String toPrice = txtToPrice.getText().toString();
				
				url += "&price_from=" + fromPrice + "&price_to=" + toPrice;
				
				loadProducts();
				dialog.dismiss();
			}
		});
		
		Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setView(view);
		dialog = dialogBuilder.create();
		
		dialog.show();
	}

	protected void loadProducts() {
		task = new SimpleAsyncTask(this, new DataLoader() {
			
			@Override
			public void updateUI() {
				if (numOfPages != NO_PAGE) {
					txtPageInfo.setVisibility(View.VISIBLE);
					txtPageInfo.setText(currentPage + " / " + numOfPages);
					adapter = new NProductVatgiaAdapter(SearchVatgiaActivity.this, 0, products, new FacebookUtils(SearchVatgiaActivity.this));
					listProducts.setAdapter(adapter);
					
					btnNext.setVisibility(View.VISIBLE);
					btnPrev.setVisibility(View.VISIBLE);
				} else {
					txtPageInfo.setVisibility(View.GONE);
					btnNext.setVisibility(View.GONE);
					btnPrev.setVisibility(View.GONE);
				}
			}
			
			@Override
			public void loadData() {
				
				RestClient.getData(url, new JSONParser() {
					
					@Override
					public void onSuccess(JSONObject json) throws JSONException {
						if (first) {
							numOfPages = json.getInt("numOfPages");
							first = false;
						}
						
						JSONArray arr = json.getJSONArray("results");
						products = Global.gsonDateWithoutHour.fromJson(arr.toString(), 
								NProductVatGia.getType());
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

	private SimpleAsyncTask task;
	private String url;

	private Button btnNext;

	private Button btnPrev;
	protected void searchProductsByQuery(final String query) {
		
		task = new SimpleAsyncTask(this, new DataLoader() {
			
			@Override
			public void updateUI() {
				if (numOfPages != NO_PAGE) {
					txtPageInfo.setVisibility(View.VISIBLE);
					txtPageInfo.setText(currentPage + " / " + numOfPages);
					adapter = new NProductVatgiaAdapter(SearchVatgiaActivity.this, 0, products, new FacebookUtils(SearchVatgiaActivity.this));
					listProducts.setAdapter(adapter);
					
					btnNext.setVisibility(View.VISIBLE);
					btnPrev.setVisibility(View.VISIBLE);
				} else {
					txtPageInfo.setVisibility(View.GONE);
					btnNext.setVisibility(View.GONE);
					btnPrev.setVisibility(View.GONE);
				}
			}
			
			@Override
			public void loadData() {
				url = String.format(URLConstant.GET_VATGIA_PRODUCTS, query, currentPage);
				RestClient.getData(url, new JSONParser() {
					
					@Override
					public void onSuccess(JSONObject json) throws JSONException {
						if (first) {
							numOfPages = json.getInt("numOfPages");
							first = false;
						}
						
						JSONArray arr = json.getJSONArray("results");
						products = Global.gsonDateWithoutHour.fromJson(arr.toString(), 
								NProductVatGia.getType());
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
}
