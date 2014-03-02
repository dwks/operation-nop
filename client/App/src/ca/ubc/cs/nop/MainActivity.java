package ca.ubc.cs.nop;

// java stuff
import java.util.ArrayList;
import java.text.DecimalFormat;

// google play services stuff
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;

// google maps stuff
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.CameraUpdateFactory;

// json stuff
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

// android stuff
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.ComponentName;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Handler;
import android.app.Dialog;
import android.view.View;
import android.util.Log;
import android.location.Location;

// widgets
import android.widget.Toast;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ArrayAdapter;

// animation stuff
import android.graphics.drawable.*;
import android.widget.*;
import android.view.*;
import android.graphics.*;
import android.content.*;

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

    // content views
    private LinearLayout container;
    private MapFragment mapFragment;
    private MainGamePanel gameView;
    private ListView listView;

    // map state
    private GoogleMap map;

    // notification state
    private ArrayList<String> notifications = new ArrayList<String>();
    private ArrayAdapter<String> notificationAdapter;

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

        // empty notifications
        notifications.add("Current city: " + Globals.city);
        notifications.add("Flu trend (per person): " + Globals.fluPeople[2]);
        notifications.add("Flu trend (per hospital): " + Globals.fluHospitals[2]);
        notifications.add("Flu trend (per workplace): " + Globals.fluWorkPlaces[2]);
        notifications.add("Air quality: " + Globals.airQuality + "/10");
        notifications.add("Risk assessment: " + Globals.status + "/10");

        // create auxiliary views
        mapFragment = MapFragment.newInstance();
        gameView = new MainGamePanel(this);
        listView = new ListView(this);
        notificationAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, notifications);
        listView.setAdapter(notificationAdapter);

        // set view weights
        listView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1));
        gameView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 3));

        // find container view
        container = (LinearLayout) findViewById(R.id.container);

        // add all views to container
        getFragmentManager().beginTransaction()
            .add(R.id.container, mapFragment)
            .commit();

        container.addView(gameView);
        container.addView(listView);
    }

    // set up continuous polling
    private Handler timer = new Handler();

    Runnable pollTask = new Runnable() {
        public void run() {
            if(locationService == null || !locationService.isAvailable())
                return;

            Location location = locationService.getLocation();

            Globals.longitude = location.getLongitude();
            Globals.latitude = location.getLatitude();

            new RequestTask(Globals.SERVER + "/status", new RequestHandler() {
                public void onSuccess(String response) {
                    try {
                        JSONObject json = new JSONObject(response);

                        Globals.status = json.optDouble("status", Globals.status);
                        Globals.city = json.optString("city", Globals.city);
                        Globals.country = json.optString("country", Globals.country);
                        Globals.street = json.optString("street", Globals.street);
                        Globals.number = json.optString("number", Globals.number);
                        Globals.airQuality = json.optDouble("air_quality", Globals.airQuality);

                        JSONArray fluPeople = json.optJSONArray("flu_people");
                        if(fluPeople != null) {
                            Globals.fluPeople[0] = fluPeople.optDouble(0, Globals.fluPeople[0]);
                            Globals.fluPeople[1] = fluPeople.optDouble(1, Globals.fluPeople[1]);
                            Globals.fluPeople[2] = fluPeople.optDouble(2, Globals.fluPeople[2]);
                        }

                        JSONArray fluHospitals = json.optJSONArray("flu_hospitals");
                        if(fluHospitals != null) {
                            Globals.fluHospitals[0] = fluHospitals.optDouble(0, Globals.fluHospitals[0]);
                            Globals.fluHospitals[1] = fluHospitals.optDouble(1, Globals.fluHospitals[1]);
                            Globals.fluHospitals[2] = fluHospitals.optDouble(2, Globals.fluHospitals[2]);
                        }

                        JSONArray fluWorkPlaces = json.optJSONArray("flu_work_places");
                        if(fluWorkPlaces != null) {
                            Globals.fluWorkPlaces[0] = fluWorkPlaces.optDouble(0, Globals.fluWorkPlaces[0]);
                            Globals.fluWorkPlaces[1] = fluWorkPlaces.optDouble(1, Globals.fluWorkPlaces[1]);
                            Globals.fluWorkPlaces[2] = fluWorkPlaces.optDouble(2, Globals.fluWorkPlaces[2]);
                        }

                        notifications.set(0, "Current city: " + Globals.city);
                        notifications.set(1, "Flu trend (per person): " + (Globals.fluPeople[1] > 0 ? "up " : "down ") + new DecimalFormat("#.##").format(Math.abs(Globals.fluPeople[2])) + "%");
                        notifications.set(2, "Flu trend (per hospital): " + (Globals.fluHospitals[1] > 0 ? "up " : "down ") + new DecimalFormat("#.##").format(Math.abs(Globals.fluHospitals[2])) + "%");
                        notifications.set(3, "Flu trend (per workplace): " + (Globals.fluWorkPlaces[1] > 0 ? "up " : "down ") + new DecimalFormat("#.##").format(Math.abs(Globals.fluWorkPlaces[2])) + "%");
                        notifications.set(4, "Air quality: " + Globals.airQuality + "/10");
                        notifications.set(5, "Risk assessment: " + new DecimalFormat("#.##").format(Globals.status) + "/10");

                        notificationAdapter.notifyDataSetChanged();

                        Log.v("MainActivity", "Got status update: " + Globals.status);
                        Log.v("MainActivity", "Got city update: " + Globals.city);
                        Log.v("MainActivity", "Got country update: " + Globals.country);
                        Log.v("MainActivity", "Got street update: " + Globals.street);
                        Log.v("MainActivity", "Got number update: " + Globals.number);
                        Log.v("MainActivity", "Got air quality update: " + Globals.airQuality);
                        Log.v("MainActivity", "Got flu people update: " + Globals.fluPeople[0] + ", " + Globals.fluPeople[1] + ", " + Globals.fluPeople[2]);
                        Log.v("MainActivity", "Got flu hospitals update: " + Globals.fluHospitals[0] + ", " + Globals.fluHospitals[1] + ", " + Globals.fluHospitals[2]);
                        Log.v("MainActivity", "Got flu work places update: " + Globals.fluWorkPlaces[0] + ", " + Globals.fluWorkPlaces[1] + ", " + Globals.fluWorkPlaces[2]);
                    }

                    catch(JSONException e) {
                        Log.v("MainActivity", "Invalid JSON returned");
                    }
                }

                public void onFailure() {
                    Log.v("MainActivity", "Status request failed");
                }
            })
                .bind("session_id", Globals.SESSION_ID)
                .bind("pos_x", String.valueOf(location.getLongitude()))
                .bind("pos_y", String.valueOf(location.getLatitude()))
                .execute();

            timer.postDelayed(this, 5000);
        }
    };

    @Override
    public void onStart() {
        Log.v("MainActivity", "onStart");

        super.onStart();

        // bind to location service
        Intent intent = new Intent(this, LocationService.class);
        bindService(intent, locationServiceConnection, Context.BIND_AUTO_CREATE);
        timer.removeCallbacks(pollTask);
        timer.postDelayed(pollTask, 200);

        showMain(null);
    }

    @Override
    public void onStop() {
        Log.v("MainActivity", "onStop");

        super.onStop();
        unbindService(locationServiceConnection);
        timer.removeCallbacks(pollTask);
    }

    @Override
    public void onDestroy() {
      super.onDestroy();
      gameView.stopThread();
    }
  
    // button callbacks
    public void showMap(View view) {
        map = mapFragment.getMap();

        /* this might make the map not appear sometimes but it's better than crashing */
        if(map == null)
            return;

        getFragmentManager().beginTransaction()
            .show(mapFragment)
            .commit();

        gameView.setVisibility(View.GONE);

        // find clinic and place marker
        if(locationService == null || !locationService.isAvailable())
            return;

        Location location = locationService.getLocation();

        // place marker at us
        map.addMarker(new MarkerOptions()
            .position(new LatLng(location.getLatitude(), location.getLongitude()))
            .title("You")
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

        // find a hospital and place a marker there
        new RequestTask(Globals.SERVER + "/find_clinic", new RequestHandler() {
            public void onSuccess(String response) {
                Log.v("MainActivity", "Hospital request succeeded");

                try {
                    JSONArray list = new JSONArray(response);
                    JSONObject data = list.getJSONObject(0);
                    double lng = data.getDouble("POS_X");
                    double lat = data.getDouble("POS_Y");
                    String name = data.getString("DESC");

                    map.addMarker(new MarkerOptions()
                        .position(new LatLng(lat, lng))
                        .title(name));
                }

                catch(JSONException e) {
                    Log.v("MainActivity", "Hospital request returned invalid JSON");
                }
            }

            public void onFailure() {
                Log.v("MainActivity", "Hospital request failed");
            }
        })
            .bind("pos_x", String.valueOf(location.getLongitude()))
            .bind("pos_y", String.valueOf(location.getLatitude()))
            .bind("min_results", "1")
            .bind("max_results", "1")
            .execute();

        // go to us
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 10));
        listView.setVisibility(View.GONE);
    }

    public void showMain(View view) {
        gameView.setVisibility(View.VISIBLE);

        getFragmentManager().beginTransaction()
            .hide(mapFragment)
            .commit();
        listView.setVisibility(View.GONE);
    }

    public void showNotifications(View view) {
        gameView.setVisibility(View.VISIBLE);
        listView.setVisibility(View.VISIBLE);

        getFragmentManager().beginTransaction()
            .hide(mapFragment)
            .commit();
    }
}
