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
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ParticipatedOffer extends Activity {

	String pid,email,uid;
	ListView list3, list4;
	ListAdapter adapter3, adapter4;
	Button buttonprofile;
	private ProgressDialog pDialog;
	
	// Creating JSON Parser object
	JSONParser jParser = new JSONParser();
	JSONArray offers = null;
	List<HashMap<String, String>> acceptedList = new ArrayList<HashMap<String, String>>();
	List<HashMap<String, String>> pendingList = new ArrayList<HashMap<String, String>>();

	private static final String url_accepted_rides = Utils.serverpath+"acceptedrides.php";
	private static final String url_requesting_rides = Utils.serverpath+"requestingrides.php";
	private static final String url_removing_rides = Utils.serverpath+"removingrides.php";

	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_NAME= "name";
	private static final String TAG_PID = "pid";
	private static final String TAG_EMAIL = "email";
	private static final String TAG_UID = "uid";
	private static final String TAG_CITY = "city";
	private static final String TAG_SOURCE = "source";
	private static final String TAG_DESTINATION = "destination";
	private static final String TAG_DATE = "date";
	private static final String TAG_TIME = "time";
	private static final String TAG_POSTEMAIL = "postemail";
	private static final String TAG_REQUESTEMAIL = "requestemail";
	private static final String TAG_PICKUP = "pickup";
	private static final String TAG_DROP = "drop";
	private static final String TAG_ACCEPTED = "accepted";
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.participatedcombined);
		
		StrictMode.ThreadPolicy policy = new StrictMode.
		ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
				
		list3 = (ListView) findViewById(R.id.list3);
		list4 = (ListView) findViewById(R.id.list4);
//		buttonprofile = (Button) findViewById(R.id.btnPR);
		
		// getting product details from intent
		Intent i = getIntent();
		email = i.getStringExtra("email");

		// Loading offers in Background Thread
		new GetParticipatedOffers().execute();
		
	/*	buttonprofile.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				try {
					Intent in = new Intent(getApplicationContext(), Profile.class);
					in.putExtra(TAG_UID, uid);
					startActivity(in);
					finish();
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}); */
		

		list3.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				String pid=((TextView) view.findViewById(R.id.pid)).getText().toString();
				String requestemail = email;
				params.add(new BasicNameValuePair("pid", pid));
				params.add(new BasicNameValuePair("requestemail", requestemail));
				// getting JSON string from URL
				JSONObject json = jParser.makeHttpRequest(url_removing_rides, "GET", params);
				Intent in = new Intent(getApplicationContext(), ParticipatedOffer.class);
				in.putExtra(TAG_EMAIL, email);
				finish();
				startActivity(in);
			}
		});
		
		list4.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				String pid=((TextView) view.findViewById(R.id.pid)).getText().toString();
				String requestemail = email;
				params.add(new BasicNameValuePair("pid", pid));
				params.add(new BasicNameValuePair("requestemail", requestemail));
				// getting JSON string from URL
				JSONObject json = jParser.makeHttpRequest(url_removing_rides, "GET", params);
				Intent in = new Intent(getApplicationContext(), ParticipatedOffer.class);
				in.putExtra(TAG_EMAIL, email);
				finish();
				startActivity(in);
			}
		});
	}
	
	/**
	 * Background Async Task to Get complete product details
	 * */
	class GetParticipatedOffers extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(ParticipatedOffer.this);
			pDialog.setMessage("Loading participated offers. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Getting product details in background thread
		 * */
		protected String doInBackground(String... params) {

			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					// Check for success tag
					int success;
					try {
						// Building Parameters
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("email", email));
						JSONObject json = jParser.makeHttpRequest(url_accepted_rides, "GET", params);

						// check your log for json response
						Log.d("Confirmed Rides", json.toString());
						success = json.getInt(TAG_SUCCESS);
						if (success == 1) {
							JSONArray offerObj = json.getJSONArray("acceptedusers"); // JSON Array
							
							// looping through All offers
							for (int i = 0; i < offerObj.length(); i++) {
								JSONObject c = offerObj.getJSONObject(i);

								// Storing each json item in variable
								String pid = c.getString(TAG_PID);
							//	uid = c.getString(TAG_UID);
								String name = c.getString(TAG_NAME);
								String postemail = c.getString(TAG_POSTEMAIL);
								String requestemail = c.getString(TAG_REQUESTEMAIL);
								String pickup = c.getString(TAG_PICKUP);
								String drop = c.getString(TAG_DROP);
								String accepted = c.getString(TAG_ACCEPTED);
								String date=c.getString(TAG_DATE);
								String time=c.getString(TAG_TIME);
								String source=c.getString(TAG_SOURCE);
								String destination=c.getString(TAG_DESTINATION);
								String city=c.getString(TAG_CITY);
							
								// creating new HashMap
								HashMap<String, String> map = new HashMap<String, String>();

								// adding each child node to HashMap key => value
								map.put(TAG_PID, pid);
								//	map.put(TAG_UID, uid);
								map.put(TAG_NAME, name);
								map.put(TAG_POSTEMAIL, postemail);
								map.put(TAG_REQUESTEMAIL, requestemail);
								map.put(TAG_PICKUP, pickup);
								map.put(TAG_DROP, drop);
								map.put(TAG_ACCEPTED, accepted);
								map.put(TAG_DATE, date);
								map.put(TAG_TIME, time);
								map.put(TAG_CITY, city);
								map.put(TAG_SOURCE, source);
								map.put(TAG_DESTINATION, destination);

							
								// adding HashList to ArrayList
								acceptedList.add(map);
							}
						
							adapter3 = new SimpleAdapter( ParticipatedOffer.this, acceptedList, R.layout.participatedrequestonerow, new String[] {TAG_DATE, TAG_TIME, TAG_CITY, TAG_SOURCE, TAG_DESTINATION, TAG_PID, TAG_NAME, TAG_POSTEMAIL, TAG_REQUESTEMAIL, TAG_PICKUP, TAG_DROP }, new int[] {R.id.date, R.id.rtime, R.id.rcity, R.id.rsource, R.id.rdestination, R.id.pid, R.id.rname, R.id.rpostemail, R.id.rrequestemail, R.id.rpickup, R.id.rdrop });
							list3.setAdapter(adapter3);
						
						} else {
							// product with pid not found
						}
						
						
						json = jParser.makeHttpRequest(url_requesting_rides, "GET", params);

						// check your log for json response
						Log.d("Requesting Users", json.toString());
						
						success = json.getInt(TAG_SUCCESS);
						if (success == 1) {
							JSONArray offerObj1 = json.getJSONArray("acceptedusers"); // JSON Array
							
							// looping through All offers
							for (int i = 0; i < offerObj1.length(); i++) {
								JSONObject c = offerObj1.getJSONObject(i);

								// Storing each json item in variable
								String pid = c.getString(TAG_PID);
							//	uid = c.getString(TAG_UID);
								String name = c.getString(TAG_NAME);
								String postemail = c.getString(TAG_POSTEMAIL);
								String requestemail = c.getString(TAG_REQUESTEMAIL);
								String pickup = c.getString(TAG_PICKUP);
								String drop = c.getString(TAG_DROP);
								String accepted = c.getString(TAG_ACCEPTED);
								String date=c.getString(TAG_DATE);
								String time=c.getString(TAG_TIME);
								String source=c.getString(TAG_SOURCE);
								String destination=c.getString(TAG_DESTINATION);
								String city=c.getString(TAG_CITY);
							
								// creating new HashMap
								HashMap<String, String> map = new HashMap<String, String>();

								// adding each child node to HashMap key => value
								map.put(TAG_PID, pid);
								//	map.put(TAG_UID, uid);
								map.put(TAG_NAME, name);
								map.put(TAG_POSTEMAIL, postemail);
								map.put(TAG_REQUESTEMAIL, requestemail);
								map.put(TAG_PICKUP, pickup);
								map.put(TAG_DROP, drop);
								map.put(TAG_ACCEPTED, accepted);
								map.put(TAG_DATE, date);
								map.put(TAG_TIME, time);
								map.put(TAG_CITY, city);
								map.put(TAG_SOURCE, source);
								map.put(TAG_DESTINATION, destination);

								// adding HashList to ArrayList
								pendingList.add(map);
							}
						
							adapter4 = new SimpleAdapter( ParticipatedOffer.this, pendingList, R.layout.participatedrequestonerow, new String[] {TAG_DATE, TAG_TIME, TAG_CITY, TAG_SOURCE, TAG_DESTINATION, TAG_PID, TAG_NAME, TAG_POSTEMAIL, TAG_REQUESTEMAIL, TAG_PICKUP, TAG_DROP }, new int[] {R.id.date, R.id.rtime, R.id.rcity, R.id.rsource, R.id.rdestination, R.id.pid, R.id.rname, R.id.rpostemail, R.id.rrequestemail, R.id.rpickup, R.id.rdrop });
							list4.setAdapter(adapter4);
							
						} else {
							// product with pid not found
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});
			return null;
		}


		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once got all details
			pDialog.dismiss();
		}
	}
}