package ersindia.akjm;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ersindia.library.JSONParser;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Profile extends Activity  {
		String passemail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	StrictMode.ThreadPolicy policy = new StrictMode.
    	ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
    	new LoadProfileInfo().execute();
    }
    
   class LoadProfileInfo extends AsyncTask<String,String,String> implements View.OnClickListener {

	   String name,age,dob,gender,email,phoneno,address,uid = null,pid=null,id=null;
	   float rating;
	   TextView name1,age1,dob1,gender1,phoneno1,email1,address1;
	 
	   Button btnedit,btncall;
	   ImageButton imageView;
		
		// url to get info
		private static final String url_profile = Utils.serverpath+"profileinfo.php";
		private static final String url_image = Utils.serverpath+"getImage.php";
		
		// Progress Dialog
		private ProgressDialog pDialog;
		
		private static final String TAG_SUCCESS = "success";
		private static final String TAG_INFO = "info";
		private static final String TAG_NAME = "name";
		private static final String TAG_AGE = "age";
		private static final String TAG_DOB = "dob";
		private static final String TAG_GENDER = "gender";
		private static final String TAG_PHONENO = "phoneno";
		private static final String TAG_EMAIL = "email";
		private static final String TAG_ADDRESS = "address";
		//private static final String TAG_RATING = "rating";
		// Creating JSON Parser object
		JSONParser jParser = new JSONParser();
		JSONObject json;
		JSONArray info=null;
		Bitmap bm = null;
	  
		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Profile.this);
			pDialog.setMessage("Loading Profile. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
			
			setContentView(R.layout.profile);
			name1 = (TextView) findViewById(R.id.name1);
	    	age1 = (TextView) findViewById(R.id.age1);
	    	dob1 = (TextView) findViewById(R.id.dob1);
	    	gender1 = (TextView) findViewById(R.id.gender1);
	    	phoneno1 = (TextView) findViewById(R.id.phone1);
	    	email1 = (TextView) findViewById(R.id.email1);
	    	address1 = (TextView) findViewById(R.id.address1);
	  //  	rb = (RatingBar) findViewById(R.id.ratingBar1);
	    	imageView = (ImageButton) findViewById(R.id.imageView); 
	    	btnedit = (Button) findViewById(R.id.btneditprof);
	    	btncall = (Button) findViewById(R.id.btncall);
	    	
	    	btnedit.setOnClickListener(this);
	    	btncall.setOnClickListener(this);
	    	imageView.setOnClickListener(this);
	    	
	
	    	Intent i = getIntent();
	    	passemail = i.getStringExtra(TAG_EMAIL);
	    	if(i.hasExtra("pid")) {
	       		pid = i.getStringExtra("pid");
	    	} else if(i.hasExtra("uid")){
	    		uid = i.getStringExtra("uid");
	    	} else {
	    		uid = Utils.uid;
	    	}
		}	

		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
		
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			if(pid!= null){
			params.add(new BasicNameValuePair("pid", pid));
			btnedit.setVisibility(View.GONE);
			} else {
				if(uid.equals(Utils.uid)) {
					params.add(new BasicNameValuePair("uid", uid));
					id = uid;
					btncall.setVisibility(View.GONE);
				} else {
					params.add(new BasicNameValuePair("uid", uid));
					btnedit.setVisibility(View.GONE);
					id = uid;
				}
			}

			// getting JSON string from URL
			json = jParser.makeHttpRequest(url_profile, "GET", params);
		
			// Check your log cat for JSON response
			Log.d("All Info: ", json.toString());
			try {
				if (json.getString(TAG_SUCCESS) != null) {
					String res = json.getString(TAG_SUCCESS); 
					if(Integer.parseInt(res) == 1){
						info = json.getJSONArray(TAG_INFO);
						JSONObject json_user = info.getJSONObject(0);
						name = json_user.getString(TAG_NAME);
						age = json_user.getString(TAG_AGE);
						email = json_user.getString(TAG_EMAIL);
						dob = json_user.getString(TAG_DOB);
						gender = json_user.getString(TAG_GENDER);
						phoneno = json_user.getString(TAG_PHONENO);
						address = json_user.getString(TAG_ADDRESS);
				
			
				
						id = json_user.getString("uid");
						final Bitmap bmp=getBitmapFromURL(url_image);
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
			            	
								name1.setText(name);  
								age1.setText(age);
								dob1.setText(dob);
								gender1.setText(gender);
								phoneno1.setText(phoneno);
								email1.setText(email);
								address1.setText(address);
							
								imageView.setImageBitmap(bmp);
							}
						});
					}	
				}
			} catch(JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		private Bitmap getBitmapFromURL(String urlImage) {
			// TODO Auto-generated method stub
			try {

				URL url = new URL(urlImage+"?"+"uid="+id);
				Log.i("URL",url.toString());
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setDoInput(true);
				connection.connect();
				InputStream input = connection.getInputStream();
				Log.i("GET RESPONSE—-", input.toString());
				bm = BitmapFactory.decodeStream(input);
				
				return bm;

			} catch (Exception ex) {
				return null;
			}
		}

	
		/**
		  * After completing background task Dismiss the progress dialog
	 	**/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all offers
			pDialog.dismiss();
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
		
			switch(v.getId()) {
				case R.id.btneditprof:
					Intent i =new Intent(getApplicationContext(),EditProfileActivity.class);
					i.putExtra("uid", uid);
					startActivity(i);
					finish();
					break;
				
				case R.id.imageView:
					if(bm!=null){
						
						Intent ifull = new Intent(getApplicationContext(),FullScreenImage.class);
						ifull.putExtra("bitmap",bm);
						startActivity(ifull);
						
					}else{
					  
						Toast.makeText(getApplicationContext(), "No Image To Display", Toast.LENGTH_SHORT).show();
					}
					break;
				
				case R.id.btncall:
					String num = "tel:"+phoneno;
					Intent call = new Intent(Intent.ACTION_CALL,Uri.parse(num));
					startActivity(call);
					break;
			}
		}
	}
   
  /* 	public void onBackPressed(){
   		
   		Intent ifull = new Intent(getApplicationContext(),DashboardActivity.class);
		ifull.putExtra("email",passemail);
		startActivity(ifull);
   		
   	}
   */
   	
   	
}