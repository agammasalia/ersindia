package ersindia.akjm;

import org.json.JSONException;
import org.json.JSONObject;
import ersindia.library.*;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.view.View;
import android.app.*;
import android.widget.*;
import ersindia.akjm.R;
import java.text.DateFormat;
import java.util.Calendar;

public class PostOffer extends Activity implements AdapterView.OnItemSelectedListener
{
	Button buttonPostOffer, buttonDate, buttonTime, mapButton;
	EditText inputEmail, inputCity, inputSource, inputDestination, inputVehicleNumber, inputVacancy, inputFare;
	TextView inputDate, inputTime;
	String defaultDate, defaultTime;
	DateFormat fmtDateAndTime = DateFormat.getDateTimeInstance();
	Calendar myCalendar = Calendar.getInstance();
	Spinner spinGender, spinType, spinVehicle;
	ArrayAdapter<String> aaGender, aaType, aaVehicle;
	String[] itemsGender;
	String[] itemsType = { "Mon-Fri", "One Day" };
	String[] itemsVehicle = {"Bike", "Auto", "Car", "Taxi" };
	String email;
	// JSON Response node names
		private static String KEY_SUCCESS = "success";
	//	private static String KEY_ERROR = "error";
	//	private static String KEY_ERROR_MSG = "error_msg";
	//	private static String KEY_UID = "uid";
	//	private static String KEY_CITY = "city";
	//	private static String KEY_SOURCE = "source";
	//	private static String KEY_DESTINATION = "destination";

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.postoffer);
		
		StrictMode.ThreadPolicy policy = new StrictMode.
		ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		Intent i = getIntent();
		email = i.getStringExtra("email");
		if(Utils.gender.equals("Male")){
			itemsGender = new String[]{ "Both","Male Only"};
		}
		else{
			itemsGender = new String[]{ "Both","Female Only" };
		}
		// Importing all assets like buttons, text fields
		defaultDate =(myCalendar.get(Calendar.YEAR)+"-"+((myCalendar.get(Calendar.MONTH))+1)+"-"+myCalendar.get(Calendar.DAY_OF_MONTH));
		defaultTime =(myCalendar.get(Calendar.HOUR_OF_DAY)+":"+myCalendar.get(Calendar.MINUTE));
		buttonPostOffer = (Button) findViewById(R.id.buttonPostOffer);
		mapButton = (Button) findViewById(R.id.buttonMap);
		buttonTime = (Button) findViewById(R.id.buttonTime);
		inputEmail = (EditText) findViewById(R.id.loginEmail);
		inputCity = (EditText) findViewById(R.id.postCity);
		inputSource = (EditText) findViewById(R.id.postSource);
		inputDestination = (EditText) findViewById(R.id.postDestination);
		inputDate= (TextView) findViewById(R.id.postDate);
		inputTime= (TextView) findViewById(R.id.postTime);
		inputVehicleNumber = (EditText) findViewById(R.id.postVehicleNumber);
		inputVacancy = (EditText) findViewById(R.id.postVacancy);
		inputFare = (EditText) findViewById(R.id.postFare);
		spinGender =(Spinner) findViewById(R.id.buttonGender);
		spinType =(Spinner) findViewById(R.id.buttonType);
		spinVehicle =(Spinner) findViewById(R.id.buttonVehicle);
		spinGender.setOnItemSelectedListener(this);
		spinType.setOnItemSelectedListener(this);
		spinVehicle.setOnItemSelectedListener(this);
		aaGender = new ArrayAdapter(this, R.layout.spinner, itemsGender);
		aaType = new ArrayAdapter(this, R.layout.spinner, itemsType);
		aaVehicle = new ArrayAdapter(this, R.layout.spinner, itemsVehicle);
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
				new DatePickerDialog(PostOffer.this, d, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
			}
		});
		inputTime.setOnClickListener(new View.OnClickListener() 
		{
			public  void onClick(View v)
			{
				new TimePickerDialog(PostOffer.this, t, myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), true).show();
			}
		});
		updateLabel();
		
		buttonPostOffer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String Email=email;
				String City = inputCity.getText().toString();
				String Source = inputSource.getText().toString();
				String Destination = inputDestination.getText().toString();
				String Date = inputDate.getText().toString();
				String Time = inputTime.getText().toString();
				String Gender = spinGender.getSelectedItem().toString();
				String Type = spinType.getSelectedItem().toString();
				String Vehicle = spinVehicle.getSelectedItem().toString();
				String VehicleNumber = inputVehicleNumber.getText().toString();
				String Vacancy = inputVacancy.getText().toString();
				String Fare = inputFare.getText().toString();
				int vac = 0;
				if(Vacancy.isEmpty()) {
					
				}else {
					vac = Integer.parseInt(Vacancy);
				}
				int ddate = Date.compareTo(defaultDate); 
				int dtime = Time.compareTo(defaultTime); 
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
					JSONObject json = UserFunctions.postOffer(Email, City, Source, Destination, Date, Time, Gender, Type, Vehicle, VehicleNumber, Vacancy, Fare);
					// check for login response
					try {
						if (json.getString(KEY_SUCCESS) != null) {
							String res = json.getString(KEY_SUCCESS); 
						}
					}
					catch (JSONException e) {
						e.printStackTrace();
					}
					Intent i = new Intent(getApplicationContext(), PostedOffer.class);
					i.putExtra("email", email);
	                startActivity(i);
					finish();
				}
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
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View v, int position,long id) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
	}
}