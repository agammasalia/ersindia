package ersindia.akjm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.app.*;
import android.widget.*;
import ersindia.akjm.R;
import java.text.DateFormat;
import java.util.Calendar;

public class SearchOffer extends Activity implements AdapterView.OnItemSelectedListener
{
	Button buttonSearchOffer, buttonSearchDate;
	static EditText searchCity, searchSource, searchDestination;
	static TextView searchDate;
	static Spinner spinSearchGender, spinSearchType;
	ArrayAdapter<String> aaGender, aaType;
	String[] itemsGender;
	String[] itemsType = { "Mon-Fri", "One Day" };
	DateFormat fmtDateAndTime = DateFormat.getDateTimeInstance();
	Calendar myCalendar = Calendar.getInstance();
	String email;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.searchoffer);
		
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
		buttonSearchOffer = (Button) findViewById(R.id.buttonSearchOffer);
		searchCity = (EditText) findViewById(R.id.searchCity);
		searchSource = (EditText) findViewById(R.id.searchSource);
		searchDestination = (EditText) findViewById(R.id.searchDestination);
		searchDate = (TextView) findViewById(R.id.searchDate);
		spinSearchGender =(Spinner) findViewById(R.id.buttonSearchGender);
		spinSearchType =(Spinner) findViewById(R.id.buttonSearchType);
		spinSearchGender.setOnItemSelectedListener(this);
		spinSearchType.setOnItemSelectedListener(this);
		aaGender = new ArrayAdapter(this, R.layout.spinner, itemsGender);
		aaType = new ArrayAdapter(this, R.layout.spinner, itemsType);
		aaGender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		aaType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinSearchGender.setAdapter(aaGender);
		spinSearchType.setAdapter(aaType);

		searchDate.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v) 
			{
				new DatePickerDialog(SearchOffer.this, d, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
			}
		});
		searchDate.setText(myCalendar.get(Calendar.YEAR)+"-"+String.format("%02d",((myCalendar.get(Calendar.MONTH))+1))+"-"+String.format("%02d",(myCalendar.get(Calendar.DAY_OF_MONTH))));
		
		buttonSearchOffer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
String city = searchCity.getText().toString();
				
if(city.length()==0){
Toast.makeText(getApplicationContext(), "Enter city", Toast.LENGTH_SHORT).show();
}				
				
else  {				
				Intent SearchedOffer = new Intent(getApplicationContext(), SearchedOffer.class);
                SearchedOffer.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                SearchedOffer.putExtra("email", email);
                startActivity(SearchedOffer);
                //	finish();
			}
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
			searchDate.setText(myCalendar.get(Calendar.YEAR)+"-"+String.format("%02d",((myCalendar.get(Calendar.MONTH))+1))+"-"+String.format("%02d",(myCalendar.get(Calendar.DAY_OF_MONTH))));
		}
	};

	@Override
	public void onItemSelected(AdapterView<?> parent, View v, int position,long id) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
	}

	public static String getCity()
	{
		String city = searchCity.getText().toString();
		return city;
    }

	public static String getSource()
	{
		String source = searchSource.getText().toString();
		return source;
    }
	
	public static String getDestination()
	{
		String destination = searchDestination.getText().toString();
		return destination;
    }

	public static String getDate()
	{
		String date = searchDate.getText().toString();
		return date;
    }

	public static String getGender()
	{
		String gender = spinSearchGender.getSelectedItem().toString();
		return gender;
    }

	public static String getType()
	{
		String type = spinSearchType.getSelectedItem().toString();
		return type;
    }
}