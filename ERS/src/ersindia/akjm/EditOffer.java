package ersindia.akjm;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ersindia.library.JSONParser;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class EditOffer extends Activity implements AdapterView.OnItemSelectedListener {

	Button buttonEditOffer, buttonDeleteOffer, buttonDate, buttonTime, mapButton;
	EditText inputEmail, inputCity, inputSource, inputDestination, inputVehicleNumber, inputVacancy, inputFare;
	TextView inputDate, inputTime;
	DateFormat fmtDateAndTime = DateFormat.getDateTimeInstance();
	Calendar myCalendar = Calendar.getInstance();
	Spinner spinGender, spinType, spinVehicle;
	String[] itemsGender;
	String[] itemsType = { "Mon-Fri", "One Day" };
	String[] itemsVehicle = {"Bike", "Auto", "Car", "Taxi" };
	ArrayAdapter<String> aaGender, aaType, aaVehicle;
	String pid,email;
	int vac;
	
	// Progress Dialog
	private ProgressDialog pDialog;

	// JSON parser class
	JSONParser jsonParser = new JSONParser();

	// single product url
	private static final String url_get_offer = Utils.serverpath+"getoffer.php";

	// url to update product
	private static final String url_edit_offer = Utils.serverpath+"editoffer.php";
	
	// url to delete product
	private static final String url_delete_offer = Utils.serverpath+"deleteoffer.php";

	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_OFFER = "offer";
	private static final String TAG_PID = "pid";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.editoffer);
		
		StrictMode.ThreadPolicy policy = new StrictMode.
		ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		if(Utils.gender.equals("Male")){
			itemsGender = new String[]{ "Both","Male Only"};
		}
		else{
			itemsGender = new String[]{ "Both","Female Only" };
		}
		mapButton = (Button) findViewById(R.id.buttonMap);
		buttonEditOffer = (Button) findViewById(R.id.buttonEditOffer);
		buttonDeleteOffer = (Button) findViewById(R.id.buttonDeleteOffer);
		buttonTime = (Button) findViewById(R.id.buttonTime);
		inputCity = (EditText) findViewById(R.id.editCity);
		inputSource = (EditText) findViewById(R.id.editSource);
		inputDestination = (EditText) findViewById(R.id.editDestination);
		inputDate= (TextView) findViewById(R.id.editDate);
		inputTime= (TextView) findViewById(R.id.editTime);
		inputVehicleNumber = (EditText) findViewById(R.id.editVehicleNumber);
		inputVacancy = (EditText) findViewById(R.id.editVacancy);
		inputFare = (EditText) findViewById(R.id.editFare);
		spinGender =(Spinner) findViewById(R.id.buttonGender);
		spinType =(Spinner) findViewById(R.id.buttonType);
		spinVehicle =(Spinner) findViewById(R.id.buttonVehicle);
		spinGender.setOnItemSelectedListener(this);
		spinType.setOnItemSelectedListener(this);
		spinVehicle.setOnItemSelectedListener(this);
		aaGender = new ArrayAdapter(this, R.layout.spinner, itemsGender);
		aaType = new ArrayAdapter(this, R.layout.spinner, itemsType);
		aaVehicle = new ArrayAdapter(this,R.layout.spinner, itemsVehicle);
		aaGender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		aaType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		aaVehicle.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinGender.setAdapter(aaGender);
		spinType.setAdapter(aaType);
		spinVehicle.setAdapter(aaVehicle);

		inputDate.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v) 
			{
				new DatePickerDialog(EditOffer.this, d, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
			}
		});
		inputTime.setOnClickListener(new View.OnClickListener() 
		{
			public  void onClick(View v)
			{
				new TimePickerDialog(EditOffer.this, t, myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), true).show();
			}
		});
		updateLabel();
		// getting product details from intent
		Intent i = getIntent();
		
		// getting product id (pid) from intent
		pid = i.getStringExtra(TAG_PID);
		email = i.getStringExtra("email");

		// Getting complete product details in background thread
		new GetOfferDetails().execute();

		// save button click event
		buttonEditOffer.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// starting background task to update product
				new SaveOfferDetails().execute();
			}
		});

		// Delete button click event
		buttonDeleteOffer.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// deleting product in background thread
				new DeleteOffer().execute();
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

	}

	@Override
	public void onBackPressed () {
		Intent in = new Intent(getApplicationContext(), PostedCombined.class);
		in.putExtra("pid", pid);
		in.putExtra("email", email);
		startActivity(in);
		finish();
	}

	DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() 
	{
		public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) 
		{
			myCalendar.set(Calendar.YEAR, year);
			myCalendar.set(Calendar.MONTH, monthOfYear);
			myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			updateLabel();
		}
	};

	TimePickerDialog.OnTimeSetListener t = new TimePickerDialog.OnTimeSetListener() 
	{
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) 
		{
			myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
			myCalendar.set(Calendar.MINUTE, minute);
			updateLabel();
		}
	};
	
	public void updateLabel() 
	{
		inputDate.setText(myCalendar.get(Calendar.YEAR)+"-"+((myCalendar.get(Calendar.MONTH))+1)+"-"+myCalendar.get(Calendar.DAY_OF_MONTH));
		inputTime.setText(myCalendar.get(Calendar.HOUR_OF_DAY)+":"+myCalendar.get(Calendar.MINUTE));
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
			pDialog = new ProgressDialog(EditOffer.this);
			pDialog.setMessage("Loading Offer Details. Please Wait...");
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
						JSONObject json = jsonParser.makeHttpRequest(url_get_offer, "GET", params);

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
							inputCity.setText(offer.getString("city"));
							inputSource.setText(offer.getString("source"));
							inputDestination.setText(offer.getString("destination"));
							inputDate.setText(offer.getString("date"));
							inputTime.setText(offer.getString("time"));
							spinGender.setSelection(aaGender.getPosition(offer.getString("gender")));
							spinType.setSelection(aaType.getPosition(offer.getString("type")));
							spinVehicle.setSelection(aaVehicle.getPosition(offer.getString("vehicle")));
							inputVehicleNumber.setText(offer.getString("vehiclenumber"));
							inputVacancy.setText(offer.getString("vacancy"));
							inputFare.setText(offer.getString("fare"));

						}else{
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

	/**
	 * Background Async Task to  Save product Details
	 * */
	class SaveOfferDetails extends AsyncTask<String, String, String> {

	
		String defaultDate, defaultTime,City,Email,Source,Destination,Date,Time,Gender,Type,Vehicle,VehicleNumber,Vacancy,Fare;

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			Email=email;
			City = inputCity.getText().toString();
			Source = inputSource.getText().toString();
			Destination = inputDestination.getText().toString();
			Date = inputDate.getText().toString();
			Time = inputTime.getText().toString();
			Gender = spinGender.getSelectedItem().toString();
			Type = spinType.getSelectedItem().toString();
			Vehicle = spinVehicle.getSelectedItem().toString();
			VehicleNumber = inputVehicleNumber.getText().toString();
			Vacancy = inputVacancy.getText().toString();
			Fare = inputFare.getText().toString();
			vac = 0;
			if(Vacancy.isEmpty()) {
				
			}else {
				vac = Integer.parseInt(Vacancy);
			}
//			int ddate = Date.compareTo(defaultDate); 
//			int dtime = Time.compareTo(defaultTime); 
			if(City.length()<5){
				Toast.makeText(getApplicationContext(), "Enter City", Toast.LENGTH_SHORT).show();
			} else if(Source.length()<3) {
				Toast.makeText(getApplicationContext(), "Enter Source", Toast.LENGTH_SHORT).show();
			} else if(Destination.length()<3) {
				Toast.makeText(getApplicationContext(), "Enter Destination", Toast.LENGTH_SHORT).show();
			} /*else if(ddate<0) {
				Toast.makeText(getApplicationContext(), "Enter Date", Toast.LENGTH_SHORT).show();
			} else if(dtime<0) {
				Toast.makeText(getApplicationContext(), "Enter Time", Toast.LENGTH_SHORT).show();
			}*/ else if(VehicleNumber.length()<6) {
				Toast.makeText(getApplicationContext(), "Enter Vehicle Number", Toast.LENGTH_SHORT).show();
			} else if(Vacancy.equals(("0")) || Vacancy.isEmpty()) {
				Toast.makeText(getApplicationContext(), "Vacancy Can't be 0 or Empty", Toast.LENGTH_SHORT).show();
			} else if (Vehicle.equals("Bike") && vac>1 || Vehicle.equals("Car") && vac>7 || Vehicle.equals("Auto") && vac>2 || Vehicle.equals("Taxi") && vac>3) {
				Toast.makeText(getApplicationContext(), "Enter Appropriate Vacancy", Toast.LENGTH_SHORT).show();
			} else if(Fare.length()==0) {
				Toast.makeText(getApplicationContext(), "Enter Fare", Toast.LENGTH_SHORT).show();
			} else {				
				pDialog = new ProgressDialog(EditOffer.this);
				pDialog.setMessage("Saving Offer ...");
				pDialog.setIndeterminate(false);
				pDialog.setCancelable(true);
				pDialog.show();
			}
		}
		
		protected String doInBackground(String... args) {

			// getting updated data from EditTexts

			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(TAG_PID, pid));
			params.add(new BasicNameValuePair("email", Email));
			params.add(new BasicNameValuePair("city", City));
			params.add(new BasicNameValuePair("source", Source));
			params.add(new BasicNameValuePair("destination", Destination));
			params.add(new BasicNameValuePair("date", Date));
			params.add(new BasicNameValuePair("time", Time));
			params.add(new BasicNameValuePair("gender", Gender));
			params.add(new BasicNameValuePair("type", Type));
			params.add(new BasicNameValuePair("vehicle", Vehicle));
			params.add(new BasicNameValuePair("vehiclenumber", VehicleNumber));
			params.add(new BasicNameValuePair("vacancy", Vacancy));
			params.add(new BasicNameValuePair("fare", Fare));

			// sending modified data through http request
			// Notice that update product url accepts GET method
			JSONObject json = jsonParser.makeHttpRequest(url_edit_offer, "GET", params);

			// check json success tag
			try {
				int success = json.getInt(TAG_SUCCESS);
				
				if (success == 1) {
					// successfully updated
					Intent in = new Intent(getApplicationContext(), PostedCombined.class);
					in.putExtra("email", email);
					in.putExtra(TAG_PID, pid);
					startActivity(in);
					finish();
				} else {
					// failed to update product
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}


		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once product updated
			pDialog.dismiss();
		}
	}

	/**
	 * Background Async Task to Delete Product
	 * */
	class DeleteOffer extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(EditOffer.this);
			pDialog.setMessage("Deleting Product...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Deleting product
		 * */
		protected String doInBackground(String... args) {

			// Check for success tag
			int success;
			try {
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("pid", pid));

				// getting product details by making HTTP request
				JSONObject json = jsonParser.makeHttpRequest(url_delete_offer, "GET", params);

				// check your log for json response
				Log.d("Delete Product", json.toString());
				
				// json success tag
				success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					// product successfully deleted
					// notify previous activity by sending code 100
					Intent in = new Intent(getApplicationContext(), PostedOffer.class);
					in.putExtra("email", email);
					startActivity(in);
					finish();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once product deleted
			pDialog.dismiss();
		}
	}

	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {}
	public void onNothingSelected(AdapterView<?> arg0) {}
}