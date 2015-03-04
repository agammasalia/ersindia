package ersindia.akjm;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import java.util.HashMap;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import ersindia.library.JSONParser;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class SearchedOffer extends ListActivity {

	// Progress Dialog
	private ProgressDialog pDialog;

	// Creating JSON Parser object
	JSONParser jParser = new JSONParser();
	String email;
	List<HashMap<String, String>> offersList = new ArrayList<HashMap<String, String>>();;

	// url to get all offers list
	private static String url_all_offers = Utils.serverpath+"searchedoffer.php";

	// JSON Node names
	private static final String TAG_OFFERS = "offers";
	private static final String TAG_PID = "pid";
	private static final String TAG_SOURCE = "source";
	private static final String TAG_DESTINATION = "destination";
	private static final String TAG_TIME = "time";
	private static final String TAG_CITY = "city";
	private static final String TAG_DATE = "date";
	// offers JSONArray
	JSONArray offers = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.searchedoffer);
		
		StrictMode.ThreadPolicy policy = new StrictMode.
		ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		Intent i = getIntent();
		email = i.getStringExtra("email");
		
		// Loading offers in Background Thread
		new LoadAllSearchOffers().execute();

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
				Intent in = new Intent(getApplicationContext(), RequestJoin.class);
				// sending pid to next activity
				in.putExtra(TAG_PID, pid);
				in.putExtra("email", email);
				
				// starting new activity and expecting some response back
				startActivityForResult(in, 100);
			}
		});
	}

	// Response from Edit Offer Activity
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// if result code 100
		if (resultCode == 100) {
			// if result code 100 is received 
			// means user edited/deleted offer
			// reload this screen again
			Intent intent = getIntent();
			finish();
			startActivity(intent);
		}
	}

	/**
	 * Background Async Task to Load all offer by making HTTP Request
	 * */
	class LoadAllSearchOffers extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(SearchedOffer.this);
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
			String City=SearchOffer.getCity();
			String Source=SearchOffer.getSource();
			String Destination=SearchOffer.getDestination();
//			String Pickup=SearchOffer.getPickup();
//			String Drop=SearchOffer.getDrop();
			String Date=SearchOffer.getDate();
			String Gender=SearchOffer.getGender();
			String Type=SearchOffer.getType();

			params.add(new BasicNameValuePair("email", email));
			params.add(new BasicNameValuePair("city", City));
			params.add(new BasicNameValuePair("source", Source));
			params.add(new BasicNameValuePair("destination", Destination));
//			params.add(new BasicNameValuePair("pickup", Pickup));
//			params.add(new BasicNameValuePair("drop", Drop));
			params.add(new BasicNameValuePair("date", Date));
			params.add(new BasicNameValuePair("gender", Gender));
			params.add(new BasicNameValuePair("type", Type));
			
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
						String city = c.getString(TAG_CITY);
						String date = c.getString(TAG_DATE);
						String source = c.getString(TAG_SOURCE);
						String destination = c.getString(TAG_DESTINATION);
						String time = c.getString(TAG_TIME);

						// creating new HashMap
						HashMap<String, String> map = new HashMap<String, String>();

						// adding each child node to HashMap key => value
						map.put(TAG_PID, pid);
						map.put(TAG_SOURCE, source);
						map.put(TAG_DESTINATION, destination);
						map.put(TAG_TIME, time);
						map.put(TAG_CITY, city);
						map.put(TAG_DATE, date);
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
					ListAdapter adapter = new SimpleAdapter( SearchedOffer.this, offersList, R.layout.listsearchoffers, new String[] { TAG_PID, TAG_SOURCE, TAG_DESTINATION, TAG_TIME,TAG_CITY,TAG_DATE}, new int[] { R.id.pid, R.id.source, R.id.destination, R.id.time,R.id.city,R.id.date });
					// updating listview
					setListAdapter(adapter);
				}
			});
		}
	}
}