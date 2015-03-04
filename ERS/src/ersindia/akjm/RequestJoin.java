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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class RequestJoin extends Activity{

	Button buttonDate, buttonTime, buttonJoinOffer, buttonShowProfile, mapButton;
	TextView  inputCity, inputSource, inputDestination, inputDate, inputTime, spinGender, spinType, spinVehicle, inputVehicleNumber, inputVacancy, inputFare;
	String pid, inputEmail, pick,drop;
	String email;
	EditText  pickupPoint, dropPoint;
	List<HashMap<String, String>> offersList = new ArrayList<HashMap<String, String>>();
	List<HashMap<String, String>> offersList1 = new ArrayList<HashMap<String, String>>();
	ListView listAccepted, listRejected;
	ListAdapter adapter1, adapter2;

	// Progress Dialog
	private ProgressDialog pDialog;

	// JSON parser class
	JSONParser jsonParser = new JSONParser();

	// single offer details url
	private static final String url_offer_detials = Utils.serverpath+"getoffer.php";

	// url to request a join
	private static final String url_request_join = Utils.serverpath+"requestjoin.php";

	private static final String url_selected_participants = Utils.serverpath+"selectedusers.php";
	private static final String url_requesting_users = Utils.serverpath+"requestingusers.php";
	
	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_OFFER = "offer";
	private static final String TAG_PID = "pid";
	private static final String TAG_NAME = "name";
	private static final String TAG_EMAIL = "email";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.requestjoin);
		
		StrictMode.ThreadPolicy policy = new StrictMode.
		ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		mapButton = (Button) findViewById(R.id.buttonMap);
		buttonJoinOffer = (Button) findViewById(R.id.buttonEditOffer);
		buttonShowProfile = (Button) findViewById(R.id.buttonDeleteOffer);
		inputCity = (TextView) findViewById(R.id.editCity);
		inputSource = (TextView) findViewById(R.id.editSource);
		inputDestination = (TextView) findViewById(R.id.editDestination);
		inputDate = (TextView) findViewById(R.id.editDate);
		inputTime = (TextView) findViewById(R.id.editTime);
		spinGender = (TextView) findViewById(R.id.editGender);
		spinType = (TextView) findViewById(R.id.editType);
		spinVehicle = (TextView) findViewById(R.id.editVehicle);
		inputVehicleNumber = (TextView) findViewById(R.id.editVehicleNumber);
		inputVacancy = (TextView) findViewById(R.id.editVacancy);
		inputFare = (TextView) findViewById(R.id.editFare);
		listAccepted = (ListView) findViewById(R.id.listAccepted);
		listRejected = (ListView) findViewById(R.id.listRejected);
		pickupPoint = (EditText) findViewById(R.id.pickupPoint);
		dropPoint = (EditText) findViewById(R.id.dropPoint);
		
		

		// getting product details from intent
		Intent i = getIntent();
		
		// getting product id (pid) from intent
		pid = i.getStringExtra(TAG_PID);
		email = i.getStringExtra("email");

		// Getting complete offer details
		new GetOfferDetails().execute();

		buttonJoinOffer.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				pick = pickupPoint.getText().toString();
				drop = dropPoint.getText().toString();
				if(pick.length()==0){
					Toast.makeText(getApplicationContext(), "Enter Pickup Point", Toast.LENGTH_SHORT).show();
				} else if(drop.length()==0) {
					Toast.makeText(getApplicationContext(), "Enter Drop Point", Toast.LENGTH_SHORT).show();
				} else{
				// request to join in ride
				new RequestForJoin().execute();
				Intent in = new Intent(getApplicationContext(), ParticipatedOffer.class);
				// sending email to next activity
				in.putExtra("email", email);
				startActivity(in);
			}}
		});

		buttonShowProfile.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// shows profile of ride creator
				Intent i = new Intent(getApplicationContext(),Profile.class);
				i.putExtra("pid", pid);
				startActivity(i);
			}
		});
		
		//map button click event
		mapButton.setOnClickListener(new View.OnClickListener() 
		{

			@Override
			public void onClick(View arg0) {
				String source= inputSource.getText().toString();
				String destination= inputDestination.getText().toString();
				Intent intent = new Intent(android.content.Intent.ACTION_VIEW,Uri.parse(String.format("http://maps.google.com/maps?saddr=" + source +"&daddr="+ destination)));
				startActivity(intent);
			}
		});
		
		listAccepted.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent i = new Intent(getApplicationContext(),Profile.class);
				String uid = ((TextView) view.findViewById(R.id.email)).getText().toString();
				i.putExtra("uid", uid);
				startActivity(i);
			}
		});

		listRejected.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent i = new Intent(getApplicationContext(),Profile.class);
				String uid = ((TextView) view.findViewById(R.id.email)).getText().toString();
				i.putExtra("uid", uid);
				startActivity(i);
			}
		});

	}

	// Get complete offer details
	class GetOfferDetails extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(RequestJoin.this);
			pDialog.setMessage("Loading offer details. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

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
						params.add(new BasicNameValuePair("requestemail", email));

						// getting product details by making HTTP request
						// Note that product details url will use GET request
						JSONObject json = jsonParser.makeHttpRequest(url_offer_detials, "GET", params);

						// check your log for json response
						Log.d("Single Offer Detail", json.toString());
						
						// json success tag
						success = json.getInt(TAG_SUCCESS);
						if(success!=0){
							
							JSONArray offerObj = json.getJSONArray(TAG_OFFER); // JSON Array
							
							// get first product object from JSON Array
							JSONObject offer = offerObj.getJSONObject(0);

							// display product data in EditText
							inputEmail= offer.getString("email");
							inputCity.setText(offer.getString("city"));
							inputSource.setText(offer.getString("source"));
							inputDestination.setText(offer.getString("destination"));
							inputDate.setText(offer.getString("date"));
							inputTime.setText(offer.getString("time"));
							spinGender.setText(offer.getString("gender"));
							spinType.setText(offer.getString("type"));
							spinVehicle.setText(offer.getString("vehicle"));
							inputVehicleNumber.setText(offer.getString("vehiclenumber"));
							inputVacancy.setText(offer.getString("vacancy"));
							inputFare.setText(offer.getString("fare"));
						}
						
						
						json = jsonParser.makeHttpRequest(url_selected_participants, "GET", params);

						// check your log for json response
						Log.d("Confirmed Participants of One Ride", json.toString());
						
						success = json.getInt(TAG_SUCCESS);
						if (success == 1) {
							JSONArray offerObj = json.getJSONArray("acceptedusers"); // JSON Array
							
							// looping through All offers
							for (int i = 0; i < offerObj.length(); i++) {
								JSONObject c = offerObj.getJSONObject(i);

								// Storing each json item in variable
								String email = c.getString("uid");
								String name = c.getString(TAG_NAME);
								
								// creating new HashMap
								HashMap<String, String> map = new HashMap<String, String>();

								// adding each child node to HashMap key => value
								map.put(TAG_EMAIL, email);
								map.put(TAG_NAME,name);
							
								// adding HashList to ArrayList
								offersList.add(map);
							}

							adapter1 = new SimpleAdapter( RequestJoin.this, offersList, R.layout.requestjoinedonerow, new String[] { TAG_EMAIL, TAG_NAME }, new int[] { R.id.email, R.id.name });
							listAccepted.setAdapter(adapter1);
						
						} else {
							// product with pid not found
						}
						
						
						json = jsonParser.makeHttpRequest(url_requesting_users, "GET", params);

						// check your log for json response
						Log.d("Requesting Users  of One Ride", json.toString());
						
						success = json.getInt(TAG_SUCCESS);
						if (success == 1) {
							JSONArray offerObj1 = json.getJSONArray("acceptedusers"); // JSON Array
							
							// looping through All offers
							for (int i = 0; i < offerObj1.length(); i++) {
								JSONObject c = offerObj1.getJSONObject(i);

								// Storing each json item in variable
								String email = c.getString("uid");
								String name = c.getString(TAG_NAME);
							
								// creating new HashMap
								HashMap<String, String> map = new HashMap<String, String>();

								// adding each child node to HashMap key => value
								map.put(TAG_EMAIL, email);
								map.put(TAG_NAME,name);
							
								// adding HashList to ArrayList
								offersList1.add(map);
							}
						
							adapter2 = new SimpleAdapter( RequestJoin.this, offersList1, R.layout.requestjoinedonerow, new String[] { TAG_EMAIL, TAG_NAME }, new int[] { R.id.email, R.id.name });
							listRejected.setAdapter(adapter2);
							
						} else {
							// product with pid not found
						}
						
						
						if (success == 1) {
							// successfully received product details
							buttonJoinOffer.setEnabled(true);

						}else  if(success == 2){
							buttonJoinOffer.setEnabled(false);
							Toast.makeText(getApplicationContext(), "All Ready Requested", Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});
			return null;
		}

		protected void onPostExecute(String file_url) {
			// dismiss the dialog once got all details
			pDialog.dismiss();
		}
	}

	class RequestForJoin extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(RequestJoin.this);
			pDialog.setMessage("Sending Request ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

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
						params.add(new BasicNameValuePair("postemail", inputEmail));
						params.add(new BasicNameValuePair("requestemail", email));
						params.add(new BasicNameValuePair("pickup", pick));
						params.add(new BasicNameValuePair("drop", drop));

						// getting product details by making HTTP request
						// Note that product details url will use GET request
						JSONObject json = jsonParser.makeHttpRequest(url_request_join, "GET", params);

						// check your log for json response
						Log.d("Request Join", json.toString());
						
						// json success tag
						success = json.getInt(TAG_SUCCESS);
						if (success == 1) {
							
							buttonJoinOffer.setVisibility(View.GONE);
							
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});
			return null;
		}

		protected void onPostExecute(String file_url) {
			// dismiss the dialog once product updated
			pDialog.dismiss();
		}
	}
}