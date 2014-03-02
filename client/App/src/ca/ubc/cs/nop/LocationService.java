package ca.ubc.cs.nop;

// java stuff
import java.util.ArrayList;

// google play services stuff
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;

// location services stuff
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

// android stuff
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Binder;
import android.os.IBinder;
import android.location.Location;
import android.util.Log;

public class LocationService extends Service
    implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener
{
    // binding
    public class Binder extends android.os.Binder {
        public LocationService getService() {
            return LocationService.this;
        }
    }

    private Binder binder = new Binder();

    @Override
    public IBinder onBind(Intent intent) {
        Log.v("LocationService", "onBind");
        return binder;
    }

    // location services
    private LocationClient client;
    private boolean connected = false;

    @Override
    public void onConnected(Bundle dataBundle) {
        Log.v("LocationService", "Connected to location service");
        connected = true;
    }

    @Override
    public void onDisconnected() {
        Log.v("LocationService", "Disconnected from location service");
        connected = false;
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.v("LocationService", "Failed to connect to location service");
    }

    public boolean isAvailable() {
        Log.v("LocationService", connected ? "connected" : " disconnected");
        return connected;
    }

    public Location getLocation() {
        return client.getLastLocation();
    }

    // service lifecycle
    @Override
    public void onCreate() {
        Log.v("LocationService", "onCreate");

        Log.v("LocationService", "Creating location client");
        client = new LocationClient(this, this, this);
        client.connect();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        client.disconnect();
        return false;
    }
}
