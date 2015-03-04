package ersindia.akjm;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.view.Display;
import android.widget.ImageView;

public class FullScreenImage extends Activity {
	
	ImageView im;
	int width , height;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fullscreenimage);
		
		StrictMode.ThreadPolicy policy = new StrictMode.
		ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		Display display = getWindowManager().getDefaultDisplay();
		if (android.os.Build.VERSION.SDK_INT >= 13){
			
			DisplayMetrics metrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metrics);
			
			height = metrics.heightPixels;
			width = metrics.widthPixels;
			
		}else{
			
			width = display.getWidth();  
			height = display.getHeight();
		}		
		
		im = (ImageView) findViewById(R.id.imagefull);
		
		Intent i = getIntent();
		Bitmap bm = (Bitmap) i.getParcelableExtra("bitmap");
		 bm = Bitmap.createScaledBitmap(bm, width, 500, true);
		im.setImageBitmap(bm);
		
	
		
	}
}
