package ca.ubc.cs.nop;

// json decoder
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

// google play services stuff
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;

// location services stuff
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import android.location.Location;

// google maps stuff
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.LatLng;

// android stuff
import android.app.Activity;
import android.os.Bundle;
import android.app.Dialog;
import android.view.View;
import android.util.Log;

public class MainActivity
    extends Activity
    implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        LocationListener
{
    private GoogleMap gmap;
    private LocationClient locationClient;
    private boolean locationServicesConnected = false;

    // location service callbacks
    @Override
    public void onConnected(Bundle dataBundle) {
        Log.v("MainActivity", "Connected to location service");
        locationServicesConnected = true;

        // set up location updates
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(5000);
        locationClient.requestLocationUpdates(locationRequest, this);
    }

    @Override
    public void onDisconnected() {
        Log.v("MainActivity", "Disconnected from location service");
        locationServicesConnected = false;
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.v("MainActivity", "Failed to connect to location service");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.v("MainActivity", "Last location: " + Location.convert(location.getLongitude(), Location.FORMAT_DEGREES) + "/" + Location.convert(location.getLatitude(), Location.FORMAT_DEGREES) + " (accurate to within " + location.getAccuracy() + "m)");

        new RequestTask("http://sirius.nss.cs.ubc.ca:8080/find_clinic", new RequestHandler() {
            public void onSuccess(String response) {
                Log.v("MainActivity", "Successfully retrieved clinic JSON");

                try {
                    JSONArray array = new JSONArray(response);

                    // no data received
                    if(array.length() == 0) {
                        Log.v("MainActivity", "No clinics nearby");
                        return;
                    }

                    JSONObject pos = array.getJSONObject(0);
                    double x = pos.getDouble("POS_X");
                    double y = pos.getDouble("POS_Y");

                    gmap.addMarker(new MarkerOptions()
                        .position(new LatLng(x, y))
                        .title("Clinic Location"));
                }

                catch(JSONException e) {
                    Log.e("MainActivity", "Invalid JSON returned: " + response);
                }
            }

            public void onFailure() {
                Log.v("MainActivity", "Failed to retrieve clinic JSON");
            }
        })
            .bind("pos_x", Location.convert(location.getLongitude(), Location.FORMAT_DEGREES))
            .bind("pos_y", Location.convert(location.getLatitude(), Location.FORMAT_DEGREES))
            .bind("min_results", "1")
            .bind("max_results", "1")
            .execute();
    }

    // lifecycle callbacks
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    // button callbacks
    public void showMapView(View button) {
        setContentView(R.layout.map);

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

        // retrieve map state and enable location layer
        Log.v("MainActivity", "Retrieving map state");
        gmap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

        Log.v("MainActivity", "Enabling My Location layer for map view");
        gmap.setMyLocationEnabled(true);

        // create location client
        Log.v("MainActivity", "Creating location client");
        locationClient = new LocationClient(this, this, this);
        locationClient.connect();
    }
}
