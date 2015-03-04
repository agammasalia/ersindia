package ersindia.akjm;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import ersindia.library.JSONParser;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View;

public class ChangePassword extends Activity implements View.OnClickListener{
	
	
	EditText newPass, conPass;
	Button save , cancel;
	String np , cp;
	// JSON parser class
	JSONParser jsonParser = new JSONParser();

	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.changepass);
		
		StrictMode.ThreadPolicy policy = new StrictMode.
				ThreadPolicy.Builder().permitAll().build();
				StrictMode.setThreadPolicy(policy);
								
				newPass = (EditText) findViewById(R.id.newPass);
				conPass = (EditText) findViewById(R.id.confirmPass);
				save = (Button) findViewById(R.id.btnSave);
				cancel = (Button) findViewById(R.id.btnCancel);
				
				save.setOnClickListener(this);
				cancel.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		switch(v.getId()){
		
			case R.id.btnSave:
				
				np = newPass.getText().toString();
				cp = conPass.getText().toString();
				
				if(np.length()<6){
					Toast.makeText(getApplicationContext(), "Enter Password of Minimum 6 Character", Toast.LENGTH_SHORT).show();
				}else if(cp.length()<6){
					Toast.makeText(getApplicationContext(), "Enter Confirm Password of Minimum 6 Character", Toast.LENGTH_SHORT).show();
				}else{
				
					if(np.equals(cp)){
						
						new SaveNewPassword().execute();
						
					}else{
						Toast.makeText(getApplicationContext(), "Password Does Not Match", Toast.LENGTH_SHORT).show();
					}
				}
				
				break;
				
			case R.id.btnCancel:
				Intent i = new Intent(getApplicationContext(),DashboardActivity.class);
				startActivity(i);
				break;
		}
		
	}
	
	class SaveNewPassword extends AsyncTask<String, String, String>{

		private ProgressDialog pDialog;
		private static final String url_changePass = Utils.serverpath+"changepassword.php";
		private static final String TAG_SUCCESS = "success";
		
		protected void onPreExecute(){
			super.onPreExecute();
			pDialog = new ProgressDialog(ChangePassword.this);
			pDialog.setMessage("Saving Changes. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}
		
		@Override
		protected String doInBackground(String... param) {
			// TODO Auto-generated method stub
			
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("uid", Utils.uid));
			params.add(new BasicNameValuePair("password", np));
			

			// sending modified data through http request
			// Notice that update product url accepts GET method
			JSONObject json = jsonParser.getJSONFromUrl(url_changePass, params);

			// check json success tag
			try {
				if (json.getString(TAG_SUCCESS) != null){
				String res = json.getString(TAG_SUCCESS);
				
				if(Integer.parseInt(res) == 1) {
					// successfully updated
						Intent in = new Intent(getApplicationContext(), Profile.class);
						startActivity(in);
						finish();
					} else {
						// failed to change Password
						Toast.makeText(getApplicationContext(), "Error Changing Password", Toast.LENGTH_SHORT).show();
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			
			return null;
		}
		
		protected void onPostExecute(String file_url){
			pDialog.dismiss();
			
		}
		
	}

}
