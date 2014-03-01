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

public class MainActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        // generated, do not modify
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

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
    }
}
