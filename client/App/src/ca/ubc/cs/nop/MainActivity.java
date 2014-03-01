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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

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
            .bind("min_results", "1")
            .bind("max_results", "1")
            .execute();
    }
}
