package ersindia.akjm;

import ersindia.akjm.ConnectionDetector;
import ersindia.akjm.AlertDialogManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.view.Menu;
import android.content.Intent;

public class MainActivity extends Activity {

	ConnectionDetector cd;
	AlertDialogManager alert = new AlertDialogManager();
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
    	StrictMode.ThreadPolicy policy = new StrictMode.
		ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
    	
    	cd = new ConnectionDetector(getApplicationContext());
    	// Check if Internet present
    	if (!cd.isConnectingToInternet()) {
    		// Internet Connection is not present
    		alert.showAlertDialog(MainActivity.this, "Internet Connection Error", "Please connect to working Internet connection", false);
    		// stop executing code by return
    		return;
    	}
    		
    	Thread splashThread = new Thread() {
    		@Override
    		public void run() {
    			try {
    				sleep(1000);
    			}
    			catch (InterruptedException e) {
                }
    			finally {
    				finish();
    				Intent i = new Intent();
    				i.setClassName("ersindia.akjm","ersindia.akjm.LoginActivity");
    				startActivity(i);
    			}
    		}
    	};
    	splashThread.start();
	
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}