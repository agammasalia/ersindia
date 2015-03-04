package ersindia.akjm;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.http.HttpResponse;
import ersindia.library.JSONParser;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class EditProfileActivity extends Activity implements View.OnClickListener {

	Button buttonSave, buttonCancel, buttonUpload,btnChangePassword;
	EditText inputName, inputPhoneno, inputEmail,  inputAddress; 
	ImageButton imagebtn;
	DateFormat fmtDateAndTime = DateFormat.getDateInstance();
	Calendar myCalendar = Calendar.getInstance();
	Spinner spinGender;
	TextView buttonDate;
	String[] itemsGender = { "Male", "Female" };
	ArrayAdapter<String> aaGender;
	String uid = null;
	Bitmap bm = null;
	
	// Progress Dialog
	private ProgressDialog pDialog;

	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_INFO = "info";
	private static final String TAG_NAME = "name";
    private static final String TAG_EMAIL = "email";
	private static final String TAG_DOB = "dob";
	private static final String TAG_GENDER = "gender";
	private static final String TAG_PHONENO = "phoneno";
	private static final String TAG_ADDRESS = "address";
	String filePath;
	String Name,Phoneno,Email ,DOB , Gender ,Address;
	// JSON parser class
	JSONParser jsonParser = new JSONParser();
	JSONObject json;
	JSONArray info = null;
	
	// single product url
	private static final String url_profile_info = Utils.serverpath+"profileinfo.php";
	private static final String url_image = Utils.serverpath+"getImage.php";

	// url to update product
	private static final String url_update_profile = Utils.serverpath+"updateprofileinfo.php";
	private static final String url_image_upload = Utils.serverpath+"base.php";
	
	InputStream is;
			
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StrictMode.ThreadPolicy policy = new StrictMode.
		ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		setContentView(R.layout.activity_edit_profile);
		initializeVar();
		new GetProfileInfo().execute();
	}
	
	public void initializeVar() {
		
		buttonSave = (Button) findViewById(R.id.ebtnsave);
		buttonCancel = (Button) findViewById(R.id.ebtncancel);
		buttonDate = (TextView) findViewById(R.id.ebtnDOB);
		buttonUpload = (Button) findViewById(R.id.btnupload);
		btnChangePassword = (Button) findViewById(R.id.btnChangePass);
		imagebtn = (ImageButton) findViewById(R.id.imageView1);
		inputName = (EditText) findViewById(R.id.ename1);
		inputPhoneno = (EditText) findViewById(R.id.ephone1);
		inputEmail = (EditText) findViewById(R.id.eemail1);
		inputAddress = (EditText) findViewById(R.id.eaddress1);
		spinGender = (Spinner) findViewById(R.id.espingender1);
		aaGender = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, itemsGender);
		aaGender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinGender.setAdapter(aaGender);
		
		// save button click event
		buttonSave.setOnClickListener(this);
		buttonCancel.setOnClickListener(this);
		buttonDate.setOnClickListener(this);
		buttonUpload.setOnClickListener(this);
		imagebtn.setOnClickListener(this);
		btnChangePassword.setOnClickListener(this);
        

		Intent i = getIntent();
		uid = i.getStringExtra("uid");
	}
	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		switch(v.getId()) {
		case R.id.ebtnsave:
			Name = inputName.getText().toString();
			Phoneno = inputPhoneno.getText().toString();
			Email = inputEmail.getText().toString();
			DOB = buttonDate.getText().toString();
			Gender = spinGender.getSelectedItem().toString();
			Address = inputAddress.getText().toString();
			
			String n = "";
			Pattern pattern = Patterns.EMAIL_ADDRESS;
			Matcher matcher = pattern.matcher(Email);
			
			
			if(Name.length()<5){
				Toast.makeText(getApplicationContext(), "Enter Name", Toast.LENGTH_SHORT).show();
			}else if(Phoneno.length()<10){
				Toast.makeText(getApplicationContext(), "Enter Mobile no.", Toast.LENGTH_SHORT).show();
			}else{
				n = Phoneno.substring(0, 1);
			} if(!n.equals("9") && !n.equals("8") && !n.equals("7")){
				Toast.makeText(getApplicationContext(), "In Valid Mobile Number", Toast.LENGTH_SHORT).show();
			}else if(Email.length()==0){
				Toast.makeText(getApplicationContext(), "Enter Email", Toast.LENGTH_SHORT).show();
			}else if(!matcher.matches()){
				Toast.makeText(getApplicationContext(), "Invalid Email Address", Toast.LENGTH_SHORT).show();
			}else{
				new SaveProfileInfo().execute();
			}
			break;
			
		case R.id.ebtncancel:
			Intent ia = new Intent(this,Profile.class);
			startActivity(ia);
			finish();
			break;
			
		case R.id.ebtnDOB:
			new DatePickerDialog(EditProfileActivity.this, d, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
			updateLabel();
			break;
			
		case R.id.imageView1:
			  
			if(bm!=null){
				
				Intent ifull = new Intent(getApplicationContext(),FullScreenImage.class);
				ifull.putExtra("bitmap",bm);
				startActivity(ifull);
				
			}else{
			  
				Toast.makeText(getApplicationContext(), "No Image To Display", Toast.LENGTH_SHORT).show();
			}
			break;
				
		case R.id.btnupload:
			Intent i = new Intent();
			i.setType("image/*");
			i.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(Intent.createChooser(i, "SELECT IMAGE"),1);
			break;
			
		case R.id.btnChangePass:
			
			Intent changepass = new Intent(getApplicationContext(), ChangePassword.class);
            changepass.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(changepass);
			break;
		}
	}
	


	public void onActivityResult(int requestCode, int resultCode , Intent data) {
		if(resultCode == RESULT_OK){
			if(requestCode == 1){
				Bitmap bitmap = getPath(data.getData());
		        imagebtn.setImageBitmap(bitmap);
				new HttpUploader().execute(filePath);
			}
		}
	}

	private Bitmap getPath(Uri uri) {
		// TODO Auto-generated method stub
		String [] proj ={MediaStore.Images.Media.DATA};
		Cursor cursor = managedQuery(uri, proj , null, null, null);
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		filePath = cursor.getString(column_index);
		cursor.close();
		// Convert file path into bitmap image using below line.
		 // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, o);

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        if(width_tmp < 1000  || height_tmp < 1000){
        	scale = 2;
        	
        }else if(width_tmp > 1000 && width_tmp < 2000 || height_tmp > 1000 && height_tmp < 2000){
        	
	    		scale = 4;
	    	
	    }else if(width_tmp > 2000 && width_tmp < 4000|| height_tmp > 2000 && height_tmp < 4000){
	    	
	    	scale = 8;
	    }else if(width_tmp > 4000 || height_tmp > 4000){
	    	
	    	scale = 10;
	    }
        
        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        Bitmap bitmapOrg = BitmapFactory.decodeFile(filePath, o2);
        bitmapOrg = Bitmap.createScaledBitmap(bitmapOrg, 450, 450, true);
        bm = bitmapOrg;
		return bitmapOrg;
	}
	

	DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
		
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			// TODO Auto-generated method stub
			myCalendar.set(Calendar.YEAR, year);
			myCalendar.set(Calendar.MONTH, monthOfYear);
			myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			updateLabel();
		}
	};
	
	public void updateLabel(){
		buttonDate.setText(myCalendar.get(Calendar.YEAR)+"-"+((myCalendar.get(Calendar.MONTH))+1)+"-"+myCalendar.get(Calendar.DAY_OF_MONTH));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.activity_edit_profile, menu);
		return true;
	}
	
	class GetProfileInfo extends AsyncTask<String, String, String> {

		protected void onPreExecute(){
			super.onPreExecute();
			pDialog = new ProgressDialog(EditProfileActivity.this);
			pDialog.setMessage("Loading Profile. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}
		
		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			runOnUiThread(new Runnable() {

		    @Override
		    public void run() {
		    	try {
		    		List<NameValuePair> params = new ArrayList<NameValuePair>();
		    		params.add(new BasicNameValuePair("uid", uid));
			   		// getting JSON string from URL
		    		json = jsonParser.makeHttpRequest(url_profile_info, "GET", params);
		    		// Check your log cat for JSON response
		    		Log.d("All Info: ", json.toString());
		    		if (json.getString(TAG_SUCCESS) != null) {
		    			String res = json.getString(TAG_SUCCESS); 
		    			if(Integer.parseInt(res) == 1){
		    				info = json.getJSONArray(TAG_INFO);
		    				JSONObject json_user = info.getJSONObject(0);
		    				inputName.setText(json_user.getString(TAG_NAME));
		    				inputEmail.setText(json_user.getString(TAG_EMAIL));
		    				buttonDate.setText(json_user.getString(TAG_DOB));	
		    				inputPhoneno.setText(json_user.getString(TAG_PHONENO));
							inputAddress.setText(json_user.getString(TAG_ADDRESS));
							spinGender.setSelection(aaGender.getPosition(json_user.getString(TAG_GENDER)));
							Bitmap bmp=getBitmapFromURL(url_image);
							imagebtn.setImageBitmap(bmp);
		    			} else {
		    				// Error if success 0
		    				Toast.makeText(getApplicationContext(), "Error Updating Info.", Toast.LENGTH_SHORT).show();
		    			}
		    		}
				} catch(JSONException e) {
					e.printStackTrace();
				}	
			}

			private Bitmap getBitmapFromURL(String urlImage) {
				// TODO Auto-generated method stub
				try {
					URL url = new URL(urlImage+"?"+"uid="+uid);
		            Log.i("URL",url.toString());
		            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		            connection.setDoInput(true);
		            connection.connect();
		            InputStream input = connection.getInputStream();
		            Log.i("GET RESPONSE—-", input.toString());
		            Bitmap mybitmap = BitmapFactory.decodeStream(input);
		            bm = mybitmap;
		            return mybitmap;
		        } catch (Exception ex) {
		            return null;
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
	
	class SaveProfileInfo extends AsyncTask<String, String, String> {
	
		JSONObject json;
		protected void onPreExecute(){
			super.onPreExecute();
			
		
			pDialog = new ProgressDialog(EditProfileActivity.this);
			pDialog.setMessage("Saving Profile. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
			
		
		}

		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			// getting updated data from EditTexts
			
			
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("uid", uid));
			params.add(new BasicNameValuePair("email", Email));
			params.add(new BasicNameValuePair("name", Name));
			params.add(new BasicNameValuePair("phoneno", Phoneno));
			params.add(new BasicNameValuePair("dob", DOB));
			params.add(new BasicNameValuePair("gender", Gender));
			params.add(new BasicNameValuePair("address", Address));
			
			
			// sending modified data through http request
			// Notice that update product url accepts GET method
			json = jsonParser.makeHttpRequest(url_update_profile, "GET", params);
			
			return null;
			
		}
			
		
		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once product updated
			// check json success tag
			try {
				int success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					// successfully updated
					pDialog.dismiss();
					try {
						Toast.makeText(EditProfileActivity.this," Profile Updated", Toast.LENGTH_SHORT).show();
						Thread.sleep(2000);
						Intent i = new Intent(EditProfileActivity.this,Profile.class);
					//	i.putExtra("uid", uid);
						startActivity(i);
						finish();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					// failed to update info
					pDialog.dismiss();
					Toast.makeText(EditProfileActivity.this," Profile Updation Failed", Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	private class HttpUploader extends AsyncTask<String, Void, String>{

	

		protected void onPreExecute(){
			super.onPreExecute();
			pDialog = new ProgressDialog(EditProfileActivity.this);
			pDialog.setMessage("Loading Image. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}
		
		@Override
		protected String doInBackground(String... filePath) {
			// TODO Auto-generated method stub
			String outPut = null;
            for (String sdPath : filePath) {
           
            	
            	// Decode image size
                BitmapFactory.Options o = new BitmapFactory.Options();
                o.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(sdPath, o);

             
                // Find the correct scale value. It should be the power of 2.
                int width_tmp = o.outWidth, height_tmp = o.outHeight;
                int scale = 1;
                if(width_tmp < 1000  || height_tmp < 1000){
                	scale = 2;
                	
                }else if(width_tmp > 1000 && width_tmp < 2000 || height_tmp > 1000 && height_tmp < 2000){
                	
        	    		scale = 4;
        	    	
        	    }else if(width_tmp > 2000 && width_tmp < 4000|| height_tmp > 2000 && height_tmp < 4000){
        	    	
        	    	scale = 9;
        	    }else if(width_tmp > 4000 || height_tmp > 4000){
        	    	
        	    	scale = 10;
        	    }
                
                // Decode with inSampleSize
                BitmapFactory.Options o2 = new BitmapFactory.Options();
                o2.inSampleSize = scale;
                Bitmap bitmapOrg = BitmapFactory.decodeFile(sdPath, o2);
                ByteArrayOutputStream bao = new ByteArrayOutputStream();
                bitmapOrg = Bitmap.createScaledBitmap(bitmapOrg, 450, 450, true); 
                bitmapOrg.compress(Bitmap.CompressFormat.JPEG, 95, bao);
                byte[] ba = bao.toByteArray();
                String ba1 = Base64.encodeToString(ba, 0);
            	
            	
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("image",ba1));
                nameValuePairs.add(new BasicNameValuePair("uid",Utils.uid));
                try {
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(url_image_upload);
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = (HttpResponse) httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();                

                    // print response
                    outPut = EntityUtils.toString(entity);
                    Log.i("GET RESPONSE—-", outPut);
                    
                    //is = entity.getContent();
                    Log.e("log_tag ******", "good connection");
                    bitmapOrg.recycle();

                } catch (Exception e) {
                    Log.e("log_tag ******", "Error in http connection " + e.toString());
                }
            }
            return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
			pDialog.dismiss();
		}
		
	}
}