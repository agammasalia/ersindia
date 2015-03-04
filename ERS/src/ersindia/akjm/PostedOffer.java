package ersindia.akjm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ersindia.library.JSONParser;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class PostedOffer extends ListActivity {

	// Progress Dialog
	private ProgressDialog pDialog;
	String email;
	// Creating JSON Parser object
	JSONParser jParser = new JSONParser();

	List<HashMap<String, String>> offersList = new ArrayList<HashMap<String, String>>();;

	// url to get all offers list
	private static String url_all_offers = Utils.serverpath+"postedoffer.php";

	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_OFFERS = "offers";
	private static final String TAG_PID = "pid";
	private static final String TAG_EMAIL = "email";
	private static final String TAG_CITY = "city";
	private static final String TAG_SOURCE = "source";
	private static final String TAG_DESTINATION = "destination";
	private static final String TAG_DATE = "date";
	private static final String TAG_TIME = "time";

	// offers JSONArray
	JSONArray offers = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.postedoffer);
		
		StrictMode.ThreadPolicy policy = new StrictMode.
		ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		Intent i = getIntent();
if(i.hasExtra("email")){
	email = i.getStringExtra("email");
}else{
	email = Utils.email;
}
		
		// Loading offers in Background Thread
		new LoadAllPostedOffers().execute();

		// Get listview
		ListView lv = getListView();

		// on seleting single offer
		// launching Edit Offer Screen
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// getting values from selected ListItem
				String pid = ((TextView) view.findViewById(R.id.pid)).getText().toString();

				// Starting new intent
				Intent in = new Intent(getApplicationContext(), PostedCombined.class);
				// sending pid to next activity
				in.putExtra(TAG_PID, pid);
				in.putExtra(TAG_EMAIL, email);
				
				// starting new activity and expecting some response back
				startActivity(in);
				finish();
			}
		});
	}

	/**
	 * Background Async Task to Load all offer by making HTTP Request
	 * */
	class LoadAllPostedOffers extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(PostedOffer.this);
			pDialog.setMessage("Loading offers. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * getting All offers from url
		 * */
		protected String doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			String gemail=email;
			params.add(new BasicNameValuePair("gemail", gemail));
			// getting JSON string from URL
			JSONObject json = jParser.makeHttpRequest(url_all_offers, "GET", params);
			
			// Check your log cat for JSON reponse
			Log.d("All Offers: ", json.toString());

			try {
				// Checking for SUCCESS TAG
//				int success = json.getInt(TAG_SUCCESS);

//				if (success == 1) {
					// offers found
					// Getting Array of offers
					offers = json.getJSONArray(TAG_OFFERS);

					// looping through All offers
					for (int i = 0; i < offers.length(); i++) {
						JSONObject c = offers.getJSONObject(i);

						// Storing each json item in variable
						String pid = c.getString(TAG_PID);
						String email = c.getString(TAG_EMAIL);
						String city = c.getString(TAG_CITY);
						String source = c.getString(TAG_SOURCE);
						String destination = c.getString(TAG_DESTINATION);
						String date = c.getString(TAG_DATE);
						String time = c.getString(TAG_TIME);

						// creating new HashMap
						HashMap<String, String> map = new HashMap<String, String>();

						// adding each child node to HashMap key => value
						map.put(TAG_PID, pid);
						map.put(TAG_EMAIL, email);
						map.put(TAG_CITY, city);
						map.put(TAG_SOURCE, source);
						map.put(TAG_DESTINATION, destination);
						map.put(TAG_DATE, date);
						map.put(TAG_TIME, time);

						// adding HashList to ArrayList
						offersList.add(map);
					}
//				} else {
					// no offers found
					// Launch Add New offer Activity
//					Intent i = new Intent(getApplicationContext(), PostOffer.class);
					// Closing all previous activities
//					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//					startActivity(i);
//				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all offers
			pDialog.dismiss();
			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					/**
					 * Updating parsed JSON data into ListView
					 * */
					ListAdapter adapter = new SimpleAdapter( PostedOffer.this, offersList, R.layout.listpostoffers, new String[] { TAG_PID, TAG_EMAIL, TAG_CITY, TAG_SOURCE, TAG_DESTINATION, TAG_DATE, TAG_TIME}, new int[] { R.id.pid, R.id.email, R.id.city, R.id.source, R.id.destination, R.id.date, R.id.time });
					// updating listview
					setListAdapter(adapter);
				}
			});
		}
	}
}