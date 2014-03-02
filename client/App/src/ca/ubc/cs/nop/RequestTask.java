package ca.ubc.cs.nop;

// java stuff
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

// apache commons http stuff
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.entity.UrlEncodedFormEntity;

import org.apache.http.client.ClientProtocolException;

// android stuff
import android.os.AsyncTask;
import android.util.Log;

public class RequestTask extends AsyncTask<Void, Void, Boolean> {
    // parameters
    private String url;
    private RequestHandler handler;

    // internal state
    private ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
    private String response;

    public RequestTask(String url, RequestHandler handler) {
        this.url = url;
        this.handler = handler;
    }

    public RequestTask bind(String name, String value) {
        parameters.add(new BasicNameValuePair(name, value));
        return this;
    }

    // converts @a value from int to String
    public RequestTask bind(String name, int value) {
        parameters.add(new BasicNameValuePair(name, Integer.toString(value)));
        return this;
    }

    @Override
    protected Boolean doInBackground(Void... args) {
        Log.v("RequestTask", "Begin background execution");

        HttpClient client = new DefaultHttpClient();
        HttpPost request = new HttpPost(url);

        try {
            Log.v("RequestTask", "Begin request");

            request.setEntity(new UrlEncodedFormEntity(parameters));
            Log.v("RequestTask", "Request successfully encoded");

            HttpResponse response = client.execute(request);
            Log.v("RequestTask", "Request completed");

            StatusLine statusLine = response.getStatusLine();

            if(statusLine.getStatusCode() != HttpStatus.SC_OK) {
                Log.e("RequestTask", "Request completed with errors: " + statusLine.getReasonPhrase());
                return false;
            }

            Log.v("RequestTask", "Request completed successfully");

            ByteArrayOutputStream responseBody = new ByteArrayOutputStream();
            response.getEntity().writeTo(responseBody);
            this.response = responseBody.toString();

            return true;
        }

        catch(UnsupportedEncodingException e) {
            Log.e("RequestTask", "UnsupportedEncodingException: " + e.getMessage());
            return false;
        }

        catch(ClientProtocolException e) {
            Log.e("RequestTask", "ClientProtocolException: " + e.getMessage());
            return false;
        }

        catch(IOException e) {
            Log.e("RequestTask", "IOException: " + e.getMessage());
            return false;
        }

        finally {
            Log.v("RequestTask", "End background execution");
        }
    }

    @Override
    protected void onPostExecute(Boolean ok) {
        Log.v("RequestTask", "Post-execute: " + (ok? "success: <" + response + ">" : "failure"));

        if(ok) {
            Log.v("RequestTask", "Calling success handler");
            handler.onSuccess(response);
        }

        else {
            Log.v("RequestTask", "Calling failure handler");
            handler.onFailure();
        }
    }
}
