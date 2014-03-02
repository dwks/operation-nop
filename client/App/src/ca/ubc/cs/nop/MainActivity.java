package ca.ubc.cs.nop;

// google play services stuff
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;

// google maps stuff
import com.google.android.gms.maps.MapFragment;

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
import android.widget.Button;
import android.widget.RelativeLayout;

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

    RelativeLayout container;
    MapFragment mapFragment;
    Button placeholder1;
    Button placeholder2;
    MainGamePanel gameView;

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

        // create auxiliary views
        mapFragment = MapFragment.newInstance();

        placeholder1 = new Button(this);
        placeholder1.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT));
        placeholder1.setText("~");

        placeholder2 = new Button(this);
        placeholder2.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT));
        placeholder2.setText("~");

        gameView = new MainGamePanel(this);

        // find container view
        container = (RelativeLayout) findViewById(R.id.container);

        // add all views to container
        getFragmentManager().beginTransaction()
            .add(R.id.container, mapFragment)
            .commit();

        container.addView(placeholder1);
        container.addView(placeholder2);
        container.addView(gameView);

        // show main view
        getFragmentManager().beginTransaction()
            .hide(mapFragment)
            .commit();

        placeholder2.setVisibility(View.GONE);
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

    @Override
    public void onDestroy() {
      super.onDestroy();
      gameView.stopThread();
    }
  
    // button callbacks
    public void showMap(View view) {
        getFragmentManager().beginTransaction()
            .show(mapFragment)
            .commit();

        placeholder1.setVisibility(View.GONE);
        placeholder2.setVisibility(View.GONE);
        gameView.setVisibility(View.GONE);
    }

    public void showMain(View view) {
        placeholder1.setVisibility(View.VISIBLE);
        gameView.setVisibility(View.VISIBLE);

        getFragmentManager().beginTransaction()
            .hide(mapFragment)
            .commit();

        placeholder2.setVisibility(View.GONE);
    }

    public void showNotifications(View view) {
        placeholder2.setVisibility(View.VISIBLE);
        gameView.setVisibility(View.VISIBLE);

        getFragmentManager().beginTransaction()
            .hide(mapFragment)
            .commit();

        placeholder1.setVisibility(View.GONE);
    }
}
