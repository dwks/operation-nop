package ca.ubc.cs.nop;

// generated, do not modify
import android.app.Activity;
import android.os.Bundle;

// google play stuff
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.ConnectionResult;

// android stuff
import android.app.Dialog;
import android.widget.TextView;
import android.util.Log;
import android.os.Handler;

// animation stuff
import android.graphics.drawable.*;
import android.widget.*;

public class MainActivity extends Activity
{
    public static final String SERVER = "http://sirius.nss.cs.ubc.ca:8080";
    public static final String SESSION_ID
        = "f55c5204-2980-4f3c-ba2e-8a0bbc340d3c";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        // generated, do not modify
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        ImageView firstBird = (ImageView)findViewById(R.id.birdOne);
        ImageView secondBird = (ImageView)findViewById(R.id.birdTwo);
        setupAnimations(firstBird);
        setupAnimations(secondBird);

        // check for google play services (required by maps api)
        Log.v("MainActivity", "Checking for Google Play Services");
        int gplayStatus = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if(gplayStatus != ConnectionResult.SUCCESS) {
            Log.v("MainActivity", "Google Play Services unavailable");

            if(GooglePlayServicesUtil.isUserRecoverableError(gplayStatus)) {
                Log.v("MainActivity", "Prompting user to update Google Play Services");
                GooglePlayServicesUtil.getErrorDialog(gplayStatus, this, 1).show();
            }
        }

        else
            Log.v("MainActivity", "Google Play Services up-to-date");

        /*
        // example that retrieves json and puts it in the default textview
        final TextView tv = (TextView) findViewById(R.id.text);

        new RequestTask("http://sirius.nss.cs.ubc.ca:8080/find_clinic", new RequestHandler() {
            public void onSuccess(String response) {
                tv.setText(response);
            }

            public void onFailure() {
                tv.setText("Error");
            }
        })
            .bind("pos_x", "0")
            .bind("pos_y", "0")
            .bind("min_results", "2")
            .bind("max_results", "8")
            .execute();
        */

        setupNotificationQuery();
    }

    private Handler handler = new Handler();
    private static final int NOTIFICATION_PERIOD = 5*1000;

    private void setupNotificationQuery() {
        handler.removeCallbacks(requestNotifications);
        handler.postDelayed(requestNotifications, NOTIFICATION_PERIOD);
        Log.v("MainActivity", "set up notification timer");
    }

    private Runnable requestNotifications = new Runnable() {
        public void run() {
            RequestTask r = new RequestTask(
                SERVER + "/status", new RequestHandler() {

                public void onSuccess(String response) {
                    Log.v("MainActivity", "Got status: " + response);
                }

                public void onFailure() {
                    Log.v("MainActivity", "Failed to retrieve status");
                }
            });
            r.bind("session_id", SESSION_ID);
            r.bind("pos_x", 0);
            r.bind("pos_y", 0);

            r.execute();

            // schedule the next status request
            handler.postDelayed(this, NOTIFICATION_PERIOD);
        }
    };


//    AnimationDrawable birdAnimation;
    private void setupAnimations(ImageView firstBird) {
        firstBird.setBackgroundResource(R.drawable.bird);
        AnimationDrawable birdAnimation = (AnimationDrawable) firstBird.getBackground();
        birdAnimation.start();
    }
}
