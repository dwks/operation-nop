package ca.ubc.cs.nop;

// google play services stuff
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;

// android stuff
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.ComponentName;
import android.os.Bundle;
import android.os.IBinder;
import android.app.Dialog;
import android.view.View;
import android.util.Log;
import android.location.Location;
import android.widget.Toast;

public class MainActivity extends Activity {
    // location service connection
    private LocationService locationService;

    private ServiceConnection locationServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder binder) {
            Log.v("MainActivity", "Connected to location service");
            locationService = ((LocationService.Binder) binder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            Log.v("MainActivity", "Disconnected from location service");
            locationService = null;
        }
    };

    // activity lifecycle
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v("MainActivity", "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // check for google play services (required by location services and maps api)
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
    }

    @Override
    public void onStart() {
        Log.v("MainActivity", "onStart");

        super.onStart();
        Intent intent = new Intent(this, LocationService.class);
        bindService(intent, locationServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        Log.v("MainActivity", "onStop");

        super.onStop();
        unbindService(locationServiceConnection);
    }

    // button callbacks
    public void test(View view) {
        if(!locationService.isAvailable()) {
            Toast.makeText(getApplicationContext(), "Location services unavailable", Toast.LENGTH_SHORT).show();
            return;
        }

        Location location = locationService.getLocation();
        Toast.makeText(getApplicationContext(), "Current location: " + Location.convert(location.getLongitude(), Location.FORMAT_DEGREES) + "/" + Location.convert(location.getLatitude(), Location.FORMAT_DEGREES), Toast.LENGTH_SHORT).show();
    }
}
