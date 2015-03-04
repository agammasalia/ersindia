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
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class PostedCombined extends Activity {

	Button buttonEditDeleteOffer, buttonRemoveAccepted, buttonAcceptRequest;//,buttonprofile;
	TextView inputPid, inputEmail, inputCity, inputSource, inputDestination, inputDate, inputTime;
	String pid, email,uid;
	ListView list1, list2;
	ListAdapter adapter1, adapter2;
	private ProgressDialog pDialog;
	
	// Creating JSON Parser object
	JSONParser jParser = new JSONParser();
	JSONArray offers = null;
	List<HashMap<String, String>> offersList = new ArrayList<HashMap<String, String>>();
	List<HashMap<String, String>> offersList1 = new ArrayList<HashMap<String, String>>();
//	List<HashMap<String, String>> acceptedList = new ArrayList<HashMap<String, String>>();
//	List<HashMap<String, String>> pendingList = new ArrayList<HashMap<String, String>>();

	private static final String url_get_offer = Utils.serverpath+"getoffer.php";
	private static final String url_selected_participants = Utils.serverpath+"selectedusers.php";
	private static final String url_requesting_users = Utils.serverpath+"requestingusers.php";
	private static final String url_removing_participants = Utils.serverpath+"removingparticipants.php";
	private static final String url_accepting_users = Utils.serverpath+"acceptingusers.php";

	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_OFFER = "offer";
	private static final String TAG_PID = "pid";
	private static final String TAG_UID = "uid";
	private static final String TAG_NAME = "name";
	private static final String TAG_EMAIL = "email";
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
		setContentView(R.layout.postedcombined);
		
		StrictMode.ThreadPolicy policy = new StrictMode.
		ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		buttonEditDeleteOffer = (Button) findViewById(R.id.buttonEditDeleteOffer);
	//	buttonprofile = (Button) findViewById(R.id.btnPR);
		inputPid = (TextView) findViewById(R.id.pid);
		inputEmail = (TextView) findViewById(R.id.email);
		inputCity = (TextView) findViewById(R.id.city);
		inputSource = (TextView) findViewById(R.id.source);
		inputDestination = (TextView) findViewById(R.id.destination);
		inputDate = (TextView) findViewById(R.id.date);
		inputTime = (TextView) findViewById(R.id.time);
		list1 = (ListView) findViewById(R.id.list1);
		list2 = (ListView) findViewById(R.id.list2);
		
		// getting product details from intent
		Intent i = getIntent();
		
//		Utils.pid =  i.getStringExtra(TAG_PID);
		// getting product id (pid) from intent
		pid = i.getStringExtra(TAG_PID);//Utils.pid;
		
		email = i.getStringExtra("email");
		
		
		
		// Loading offers in Background Thread
		new GetOfferDetails().execute();

		buttonEditDeleteOffer.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				try {
					Intent in = new Intent(getApplicationContext(), EditOffer.class);
					in.putExtra(TAG_PID, pid);
					in.putExtra(TAG_EMAIL, email);
					startActivity(in);
					finish();
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		
/*		buttonprofile.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				try {
					Intent in = new Intent(getApplicationContext(), Profile.class);
					in.putExtra(TAG_UID, uid);
					in.putExtra(TAG_EMAIL, email);
					startActivity(in);
					finish();
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}); */
		
		
		
		list1.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				String requestemail = ((TextView) view.findViewById(R.id.crequestemail)).getText().toString();
				params.add(new BasicNameValuePair("pid", pid));
				params.add(new BasicNameValuePair("requestemail", requestemail));
				// getting JSON string from URL
				JSONObject json = jParser.makeHttpRequest(url_removing_participants, "GET", params);
				Intent in = new Intent(getApplicationContext(), PostedCombined.class);
				in.putExtra(TAG_PID, pid);
				in.putExtra(TAG_EMAIL, email);
				finish();
				startActivity(in);
			}
		});
		
		
		
		list2.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				String requestemail = ((TextView) view.findViewById(R.id.rrequestemail)).getText().toString();
				params.add(new BasicNameValuePair("pid", pid));
				params.add(new BasicNameValuePair("requestemail", requestemail));
				// getting JSON string from URL
				JSONObject json = jParser.makeHttpRequest(url_accepting_users, "GET", params);
				try {
					int success = json.getInt(TAG_SUCCESS);
					if (success == 0){
						Toast.makeText(getApplicationContext(), "Vacancy Exceeded", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Intent in = new Intent(getApplicationContext(), PostedCombined.class);
				in.putExtra(TAG_PID, pid);
				in.putExtra(TAG_EMAIL, email);
				finish();
				startActivity(in);
			}
		});



	}
	
	@Override
	public void onBackPressed () {
		Intent in = new Intent(getApplicationContext(), PostedOffer.class);
		in.putExtra("email", email);
		startActivity(in);
		finish();
	}


	/**
	 * Background Async Task to Get complete product details
	 * */
	class GetOfferDetails extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(PostedCombined.this);
			pDialog.setMessage("Loading offer details. Please wait...");
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
						params.add(new BasicNameValuePair("pid", pid));

						// getting product details by making HTTP request
						// Note that product details url will use GET request
						JSONObject json = jParser.makeHttpRequest(url_get_offer, "GET", params);

						// check your log for json response
						Log.d("Single Offer Details", json.toString());
						
						// json success tag
						success = json.getInt(TAG_SUCCESS);
						if (success == 1) {
							// successfully received product details
							JSONArray offerObj = json.getJSONArray(TAG_OFFER); // JSON Array
							
							// get first product object from JSON Array
							JSONObject offer = offerObj.getJSONObject(0);

							// display product data in EditText
							inputPid.setText(offer.getString(TAG_PID));
							inputEmail.setText(offer.getString(TAG_EMAIL));
							inputCity.setText(offer.getString(TAG_CITY));
							inputSource.setText(offer.getString(TAG_SOURCE));
							inputDestination.setText(offer.getString(TAG_DESTINATION));
							inputDate.setText(offer.getString(TAG_DATE));
							inputTime.setText(offer.getString(TAG_TIME));
						} else {
							// product with pid not found
						}
						
						json = jParser.makeHttpRequest(url_selected_participants, "GET", params);

						// check your log for json response
						Log.d("Confirmed Participants", json.toString());
						
						success = json.getInt(TAG_SUCCESS);
						if (success == 1) {
							JSONArray offerObj = json.getJSONArray("acceptedusers"); // JSON Array
							
							// looping through All offers
							for (int i = 0; i < offerObj.length(); i++) {
								JSONObject c = offerObj.getJSONObject(i);

								// Storing each json item in variable
								String pid = c.getString(TAG_PID);
						//		uid = c.getString(TAG_UID);
								String name = c.getString(TAG_NAME);
								String postemail = c.getString(TAG_POSTEMAIL);
								String requestemail = c.getString(TAG_REQUESTEMAIL);
								String pickup = c.getString(TAG_PICKUP);
								String drop = c.getString(TAG_DROP);
								String accepted = c.getString(TAG_ACCEPTED);
							
								// creating new HashMap
								HashMap<String, String> map = new HashMap<String, String>();

								// adding each child node to HashMap key => value
								map.put(TAG_PID, pid);
						//		map.put(TAG_UID, uid);
								map.put(TAG_NAME,name);
								map.put(TAG_POSTEMAIL, postemail);
								map.put(TAG_REQUESTEMAIL, requestemail);
								map.put(TAG_PICKUP, pickup);
								map.put(TAG_DROP, drop);
								map.put(TAG_ACCEPTED, accepted);
							
								// adding HashList to ArrayList
								offersList.add(map);
							}
						
							adapter1 = new SimpleAdapter( PostedCombined.this, offersList, R.layout.postedconfirmedonerow, new String[] {  TAG_PID, TAG_NAME, TAG_POSTEMAIL, TAG_REQUESTEMAIL, TAG_PICKUP, TAG_DROP }, new int[] { R.id.pid, R.id.cname, R.id.cpostemail, R.id.crequestemail, R.id.cpickup, R.id.cdrop});
							list1.setAdapter(adapter1);
						
						} else {
							// product with pid not found
						}
						
						
						
						
						json = jParser.makeHttpRequest(url_requesting_users, "GET", params);

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
							
								// creating new HashMap
								HashMap<String, String> map = new HashMap<String, String>();

								// adding each child node to HashMap key => value
								map.put(TAG_PID, pid);
							//	map.put(TAG_UID, uid);
								map.put(TAG_NAME,name);
								map.put(TAG_POSTEMAIL, postemail);
								map.put(TAG_REQUESTEMAIL, requestemail);
								map.put(TAG_PICKUP, pickup);
								map.put(TAG_DROP, drop);
								map.put(TAG_ACCEPTED, accepted);
							
								// adding HashList to ArrayList
								offersList1.add(map);
							}
						
							adapter2 = new SimpleAdapter( PostedCombined.this, offersList1, R.layout.postedrequestonerow, new String[] {  TAG_PID, TAG_NAME, TAG_POSTEMAIL, TAG_REQUESTEMAIL, TAG_PICKUP, TAG_DROP }, new int[] { R.id.pid, R.id.rname, R.id.rpostemail, R.id.rrequestemail, R.id.rpickup, R.id.rdrop });
							list2.setAdapter(adapter2);
							
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