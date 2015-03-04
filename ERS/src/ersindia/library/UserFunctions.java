package ersindia.library;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import ersindia.akjm.Utils;
import android.content.Context;

public class UserFunctions 
{
	private static JSONParser jsonParser;
	private static String loginURL = Utils.serverpath;
	private static String registerURL = Utils.serverpath;
	private static String postofferURL =Utils.serverpath+"post.php";
	private static String login_tag = "login";
	private static String register_tag = "register";
	private static String postoffer_tag = "postoffer";
	
	// constructor
	public UserFunctions()
	{
		jsonParser = new JSONParser();
	}
	
	//function make Login Request
	public JSONObject loginUser(String email, String password)
	{
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", login_tag));
		params.add(new BasicNameValuePair("email", email));
		params.add(new BasicNameValuePair("password", password));
		JSONObject json = jsonParser.getJSONFromUrl(loginURL, params);
		// return json
		// Log.e("JSON", json.toString());
		return json;
	}
	
	//function make Login Request
	public JSONObject registerUser(String name, String email, String password, String emailaddress, String number, String gender, String dob)
	{
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", register_tag));
		params.add(new BasicNameValuePair("name", name));
		params.add(new BasicNameValuePair("email", email));
		params.add(new BasicNameValuePair("password", password));
		params.add(new BasicNameValuePair("emailaddress", emailaddress));
		params.add(new BasicNameValuePair("number", number));
		params.add(new BasicNameValuePair("gender", gender));
		params.add(new BasicNameValuePair("dob", dob));
		
		// getting JSON Object
		JSONObject json = jsonParser.getJSONFromUrl(registerURL, params);
		return json;
	}
	
	//function for Post Offer
	public static JSONObject postOffer(String Email, String City, String Source, String Destination, String  Date, String  Time, String  Gender, String  Type, String  Vehicle, String  VehicleNumber, String  Vacancy, String  Fare)
	{
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", postoffer_tag));
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
		
		// getting JSON Object
		JSONObject json = jsonParser.getJSONFromUrl(postofferURL, params);
		return json;
	}
	
	//function get Login status
	public boolean isUserLoggedIn(Context context)
	{
		DatabaseHandler db = new DatabaseHandler(context);
		int count = db.getRowCount();
		if(count > 0)
		{
			// user logged in
			return true;
		}
		return false;
	}
	
	//Function to logout user. Reset Database
	public boolean logoutUser(Context context)
	{
		DatabaseHandler db = new DatabaseHandler(context);
		db.resetTables();
		return true;
	}
}