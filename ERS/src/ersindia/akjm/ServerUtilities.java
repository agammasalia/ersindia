package ersindia.akjm;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import android.content.Context;
import android.util.Log;
import com.google.android.gcm.GCMRegistrar;

public final class ServerUtilities {
	
	private static final int MAX_ATTEMPTS = 5;
    private static final int BACKOFF_MILLI_SECONDS = 2000;
    private static final Random random = new Random();

    private static String registerURL = Utils.serverpath+"registerID.php";
    
    // Register this account/device pair within the server.
    static void register(final Context context, String uid, final String regId) {
    
    	Log.i("GCM", "registering device (regId = " + regId + ")");

        Map<String, String> params = new HashMap<String, String>();
        params.put("regId", regId);
        params.put("uid", uid);
        
        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
        // Once GCM returns a registration id, we need to register on our server
        // As the server might be down, we will retry it a couple times.
        for (int i = 1; i <= MAX_ATTEMPTS; i++) {
            Log.d("GCM", "Attempt #" + i + " to register");
            try {
                post(registerURL, params);
                GCMRegistrar.setRegisteredOnServer(context, true);
                return;
            } catch (IOException e) {
                // Here we are simplifying and retrying on any error; in a real application, it should retry only on unrecoverable errors
                Log.e("GCM", "Failed to register on attempt " + i + ":" + e);
                if (i == MAX_ATTEMPTS) {
                    break;
                }
            }
        }
    }

    
    // Unregister this account/device pair within the server.
    static void unregister(final Context context, final String regId) {
    	
        Log.i("GCM", "unregistering device (regId = " + regId + ")");
        String serverUrl = registerURL + "/unregister";
        Map<String, String> params = new HashMap<String, String>();
        params.put("regId", regId);
        try {
            post(serverUrl, params);
            GCMRegistrar.setRegisteredOnServer(context, false);
        } catch (IOException e) {
        }
    }  

    
    /* Issue a POST request to the server.
     * @param endpoint POST address.
     * @param params request parameters.
     * @throws IOException propagated from POST.
     */
    
    private static void post(String endpoint, Map<String, String> params) throws IOException {   	
        
        URL url;
        try {
            url = new URL(endpoint);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url: " + endpoint);
        }
        StringBuilder bodyBuilder = new StringBuilder();
        Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
        // constructs the POST body using the parameters
        while (iterator.hasNext()) {
            Entry<String, String> param = iterator.next();
            bodyBuilder.append(param.getKey()).append('=').append(param.getValue());
            if (iterator.hasNext()) {
                bodyBuilder.append('&');
            }
        }
        String body = bodyBuilder.toString();
        Log.v("GCM", "Posting '" + body + "' to " + url);
        byte[] bytes = body.getBytes();
        HttpURLConnection conn = null;
        try {
        	Log.e("URL", "> " + url);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setFixedLengthStreamingMode(bytes.length);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            // post the request
            OutputStream out = conn.getOutputStream();
            out.write(bytes);
            out.close();
            // handle the response
            int status = conn.getResponseCode();
            if (status != 200) {
              throw new IOException("Post failed with error code " + status);
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
	}
}