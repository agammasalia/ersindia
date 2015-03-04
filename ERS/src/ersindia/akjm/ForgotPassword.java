package ersindia.akjm;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ersindia.library.JSONParser;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ForgotPassword extends Activity {

	Button btnpasswordsend;
	EditText loginEmail,emailAdd;

	// JSON parser class
	JSONParser jsonParser = new JSONParser();

	// url
	private static final String url_forgot_password = Utils.serverpath+"forgotpassword.php";

	// JSON Node names
	private static final String TAG_SUCCESS = "success";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forgotpassword);
		
		StrictMode.ThreadPolicy policy = new StrictMode.
		ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		btnpasswordsend = (Button) findViewById(R.id.btnpasswordsend);
		loginEmail = (EditText) findViewById(R.id.loginEmail);
		emailAdd = (EditText) findViewById(R.id.emailAdd);
		
		btnpasswordsend.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// starting background task to update product
				
				String email = loginEmail.getText().toString();
				String eadd = emailAdd.getText().toString();
				Pattern pattern = Patterns.EMAIL_ADDRESS;
				Matcher matcher = pattern.matcher(eadd);
				
				if(loginEmail.length()<4){
					Toast.makeText(getApplicationContext(), "Enter User ID", Toast.LENGTH_SHORT).show();
				} else if(emailAdd.length()==0) {
					Toast.makeText(getApplicationContext(), "Enter Email Address", Toast.LENGTH_SHORT).show();
				}else if(!matcher.matches()){
					Toast.makeText(getApplicationContext(), "Invalid Email Address", Toast.LENGTH_SHORT).show();
				}else {
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("email", email));
				params.add(new BasicNameValuePair("emailAddress", eadd));

				
				JSONObject json = jsonParser.makeHttpRequest(url_forgot_password, "GET", params);
				

				// check your log for json response
				Log.d("Forgot Password", json.toString());
				
				// json success tag
				int success;
				try {
					success = json.getInt(TAG_SUCCESS);
					if (success == 1) {
						// successfully received product details
						JSONArray msgObj = json.getJSONArray("message"); // JSON Array
					
						for (int i = 0; i < msgObj.length(); i++) {
							JSONObject msg = msgObj.getJSONObject(i);
							String strmsg = msg.getString("message");
							Log.d("MESSAGE"+(i+1)+":", strmsg);
						}
					} else {
						
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			}
		});
	}

	@Override
	public void onBackPressed () {
		Intent in = new Intent(getApplicationContext(), LoginActivity.class);
		startActivity(in);
		finish();
	}
}