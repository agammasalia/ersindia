package ersindia.akjm;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;
import ersindia.library.*;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends Activity implements AdapterView.OnItemSelectedListener
{
	Button btnRegister, btnLinkToLogin;
	static EditText inputFullName, inputEmail, inputPassword, inputEmailAddress, inputNumber;
	TextView btnDOB;
	Spinner spinGender;
	String[] itemsGender = { "Male", "Female" };
	ArrayAdapter<String> aaGender;
	DateFormat fmtDateAndTime = DateFormat.getDateTimeInstance();
	Calendar myCalendar = Calendar.getInstance();

	// JSON Response node names
	private static String KEY_SUCCESS = "success";
	private static String KEY_UID = "uid";
	private static String KEY_NAME = "name";
	private static String KEY_EMAIL = "email";
	private static String KEY_CREATED_AT = "created_at";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		
		StrictMode.ThreadPolicy policy = new StrictMode.
		ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		// Importing all assets like buttons, text fields
		inputFullName = (EditText) findViewById(R.id.registerName);
		inputEmail = (EditText) findViewById(R.id.registerEmail);
		inputPassword = (EditText) findViewById(R.id.registerPassword);
		inputEmailAddress = (EditText) findViewById(R.id.registerEmailAddress);
		inputNumber = (EditText) findViewById(R.id.registerNumber);
		btnDOB = (TextView) findViewById(R.id.btnDOB);
		btnRegister = (Button) findViewById(R.id.btnRegister);
		btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);
		spinGender =(Spinner) findViewById(R.id.spinnerGender);
		spinGender.setOnItemSelectedListener(this);
		aaGender = new ArrayAdapter(this, R.layout.spinner, itemsGender);
		aaGender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinGender.setAdapter(aaGender);
		
		btnDOB.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v) 
			{
				new DatePickerDialog(RegisterActivity.this, d, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
			}
		});
		
		btnDOB.setText((myCalendar.get(Calendar.YEAR))+"-"+((myCalendar.get(Calendar.MONTH))+1)+"-"+myCalendar.get(Calendar.DAY_OF_MONTH));
		
		// Register Button Click event
		btnRegister.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View view) {
				String name = inputFullName.getText().toString();
				String email = inputEmail.getText().toString();
				String password = inputPassword.getText().toString();
				String emailaddress = inputEmailAddress.getText().toString();
				String number = inputNumber.getText().toString();
				String dob = btnDOB.getText().toString();
				String gender = spinGender.getSelectedItem().toString();
				String n = "";
				Pattern pattern = Patterns.EMAIL_ADDRESS;
				Matcher matcher = pattern.matcher(emailaddress);
							
				if(name.length()==0){
					Toast.makeText(getApplicationContext(), "Enter Name", Toast.LENGTH_SHORT).show();
				} else if(email.length()<4) {
					Toast.makeText(getApplicationContext(), "Enter User ID", Toast.LENGTH_SHORT).show();
				} else if(password.length()==0) {
					Toast.makeText(getApplicationContext(), "Enter Password", Toast.LENGTH_SHORT).show();
				} else if(emailaddress.length()==0) {
					Toast.makeText(getApplicationContext(), "Enter Email", Toast.LENGTH_SHORT).show();
				} else if(!matcher.matches()){
					Toast.makeText(getApplicationContext(), "Invalid Email Address", Toast.LENGTH_SHORT).show();
				} else if(number.length()!=10) {
					Toast.makeText(getApplicationContext(), "Enter 10 Digit Number", Toast.LENGTH_SHORT).show();
				}else {
					n = number.substring(0, 1);
				} if(!n.equals("9") && !n.equals("8") && !n.equals("7")){
					Toast.makeText(getApplicationContext(), "In Valid Mobile Number", Toast.LENGTH_SHORT).show();
				}else{			
								UserFunctions userFunction = new UserFunctions();
				JSONObject json = userFunction.registerUser(name, email, password, emailaddress, number, gender, dob);
				
				// check for login response
				try {
					if (json.getString(KEY_SUCCESS) != null) {
						
						String res = json.getString(KEY_SUCCESS); 
						if(Integer.parseInt(res) == 1){
							// user successfully registred
							// Store user details in SQLite Database
							DatabaseHandler db = new DatabaseHandler(getApplicationContext());
							JSONObject json_user = json.getJSONObject("user");
							
							// Clear all previous data in database
							userFunction.logoutUser(getApplicationContext());
							db.addUser(json_user.getString(KEY_NAME), json_user.getString(KEY_EMAIL), json.getString(KEY_UID), json_user.getString(KEY_CREATED_AT), null);						
							Utils.uid = json.getString(KEY_UID);
							Utils.email = json_user.getString(KEY_EMAIL);
							// Launch Dashboard Screen
							Intent dashboard = new Intent(getApplicationContext(), DashboardActivity.class);
							
							// Close all views before launching Dashboard
							dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							dashboard.putExtra(KEY_EMAIL, email);
							startActivity(dashboard);
							
							// Close Registration Screen
							finish();
						}else{
							// Error in registration
								Toast.makeText(getApplicationContext(), "UserID already used", Toast.LENGTH_SHORT).show();
//							registerErrorMsg.setText("Error occured in registration");
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
}
			});

		// Link to Login Screen
		btnLinkToLogin.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				Intent i = new Intent(getApplicationContext(), LoginActivity.class);
				startActivity(i);
				// Close Registration View
				finish();
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
			btnDOB.setText((myCalendar.get(Calendar.YEAR))+"-"+((myCalendar.get(Calendar.MONTH))+1)+"-"+myCalendar.get(Calendar.DAY_OF_MONTH));
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
}