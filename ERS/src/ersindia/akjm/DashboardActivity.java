package ersindia.akjm;

import ersindia.akjm.ServerUtilities;
import com.google.android.gcm.GCMRegistrar;
import ersindia.akjm.LoginActivity;
import ersindia.library.UserFunctions;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.StrictMode;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
 
public class DashboardActivity extends Activity 
{
    UserFunctions userFunctions;
    Button btnLogout,btnPostOffer,btnPostedOffer,btnSearchOffer,btnProfile,btnParticipatedOffer;
    String email;
 	AsyncTask<Void, Void, Void> mRegisterTask;
    
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.
		ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
        //Dashboard Screen for the application
        //Check login status  in database
        Intent i = getIntent();
		email = i.getStringExtra("email");
        userFunctions = new UserFunctions();
        if(userFunctions.isUserLoggedIn(getApplicationContext()))
        {
        	// user already logged in show databoard
            setContentView(R.layout.dashboard);

            // Make sure the device has the proper dependencies.
			GCMRegistrar.checkDevice(this);

			// Make sure the manifest was properly set - comment out this line
			// while developing the app, then uncomment it when it's ready.
			GCMRegistrar.checkManifest(this);

			// Get GCM registration id
			final String regId = GCMRegistrar.getRegistrationId(this);
			
			Log.i("registration id : ", regId);
			// Check if regid already presents
			if (regId.equals("")) {
				// Registration is not present, register now with GCM			
				GCMRegistrar.register(this, Utils.GCMSenderId);
			} else {
				// Device is already registered on GCM
				if (GCMRegistrar.isRegisteredOnServer(this)) {
					// Skips registration.				
					 Log.v("", "Already registered: "+regId);
				}else {
					// Try to register again, but not in the UI thread.
					// It's also necessary to cancel the thread onDestroy(),
					// hence the use of AsyncTask instead of a raw thread.
					final Context context = this;
					mRegisterTask = new AsyncTask<Void, Void, Void>() {

						@Override
						protected Void doInBackground(Void... params) {
							// Register on our server
							// On server creates a new user
							ServerUtilities.register(context, Utils.uid, regId);
							return null;
						}

						@Override
						protected void onPostExecute(Void result) {
							mRegisterTask = null;
						}

					};
					mRegisterTask.execute(null, null, null);
				}
			
			}
            
			btnParticipatedOffer = (Button) findViewById(R.id.btnJoinedOffer);
			btnParticipatedOffer.setOnClickListener(new View.OnClickListener() {

				 public void onClick(View arg0) 
				 {
					 // TODO Auto-generated method stub
					 Intent ParticipatedOffer = new Intent(getApplicationContext(), ParticipatedOffer.class);
					 ParticipatedOffer.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					 ParticipatedOffer.putExtra("email", email);
					 startActivity(ParticipatedOffer);
					 // Closing dashboard screen
					 
				 }
			 });
            
            //this is used to find out posted offers by particular emailid
            btnPostedOffer = (Button) findViewById(R.id.btnPostedOffer);
            btnPostedOffer.setOnClickListener(new View.OnClickListener() {

            	 public void onClick(View arg0) 
                 {
                     // TODO Auto-generated method stub
                     userFunctions.logoutUser(getApplicationContext());
                     Intent PostedOffer = new Intent(getApplicationContext(), PostedOffer.class);
                     PostedOffer.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					 PostedOffer.putExtra("email", email);
                     startActivity(PostedOffer);
                     // Closing dashboard screen
                     
                 }
             });
            

            //this is used to post offer
            btnPostOffer = (Button) findViewById(R.id.btnPostOffer);
            btnPostOffer.setOnClickListener(new View.OnClickListener() 
            {
                public void onClick(View arg0) 
                {
                    // TODO Auto-generated method stub
                    userFunctions.logoutUser(getApplicationContext());
                    Intent PostOffer = new Intent(getApplicationContext(), PostOffer.class);
                    PostOffer.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					PostOffer.putExtra("email", email);
                    startActivity(PostOffer);
                    // Closing dashboard screen
                    
                }
            });

            //this is used to enter details about search
            btnSearchOffer = (Button) findViewById(R.id.btnSearchOffer);
            btnSearchOffer.setOnClickListener(new View.OnClickListener() 
            {
                public void onClick(View arg0) 
                {
                    // TODO Auto-generated method stub
                    userFunctions.logoutUser(getApplicationContext());
                    Intent SearchOffer = new Intent(getApplicationContext(), SearchOffer.class);
                    SearchOffer.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					SearchOffer.putExtra("email", email);
                    startActivity(SearchOffer);
                    // Closing dashboard screen
                    
                }
            });
            
            
          //this is used to Profile
            btnProfile = (Button) findViewById(R.id.btnProfile);
            btnProfile.setOnClickListener(new View.OnClickListener() 
            {
                public void onClick(View arg0) 
                {
                    // TODO Auto-generated method stub
                    
                    Intent profile = new Intent(getApplicationContext(), Profile.class);
                    profile.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					profile.putExtra("email", email);
                    startActivity(profile);
                    // Closing dashboard screen
                    
                }
            });
            
          
            //this is used to logout user
            btnLogout = (Button) findViewById(R.id.btnLogout);
            btnLogout.setOnClickListener(new View.OnClickListener() 
            {
                public void onClick(View arg0) 
                {
                    // TODO Auto-generated method stub
                    userFunctions.logoutUser(getApplicationContext());
                    Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                    login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(login);
                    // Closing dashboard screen
                    finish();
                }
            });
        }
        else
        {
            // user is not logged in show login screen
            Intent login = new Intent(getApplicationContext(), LoginActivity.class);
            login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(login);
            // Closing dashboard screen
            finish();
        }
        
        // We set this variable true when we receive notification
        if (Utils.notificationReceived) {
        	onNotification();
        }
    }
    
    protected void onDestroy() {
 		// GCMRegistrar.onDestroy(this);
 		 super.onDestroy();
 		 }
 		 
 		 public void onNotification(){
 			 
 		 Utils.notificationReceived=false;
 		 PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
 		 WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "WakeLock");
 		 wl.acquire();
 		 
 		 AlertDialog.Builder mAlert=new AlertDialog.Builder(this);
 		 mAlert.setCancelable(true);
 		 
 		 mAlert.setTitle(Utils.notiTitle);
 		 mAlert.setMessage(Utils.notiMsg);
 		 mAlert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
 		 
 			 public void onClick(DialogInterface dialog, int which) {
 				 // TODO Auto-generated method stub
 				 dialog.dismiss();
 			 }
 		 });
 		 
 		 mAlert.setNegativeButton("No", new DialogInterface.OnClickListener() {
 			 public void onClick(DialogInterface dialog, int which) {
 				 // TODO Auto-generated method stub
 				 dialog.dismiss();
 			 }
 		 });
 		 
 		 mAlert.show();
 		 }
}