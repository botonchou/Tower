package org.droidplanner.android.communication.connection;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class AndroidHttpConnection {

	static public Socket socket;
	static class RequestTask extends AsyncTask<String, String, String>{

	    @Override
	    protected String doInBackground(String... uri) {
	    	try {
				socket = IO.socket("http://140.112.21.18:8080");
			} catch (URISyntaxException e) {			
				e.printStackTrace();
			}
	    	
	    	socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
	    		
	    	  @Override
	    	  public void call(Object... args) {
	    		Log.d("debug", "[INFO] Connected to server");
	    	    socket.emit("message", "Hi~~ i'm an Android phone !!");
	    	  }
	    	  
	    	}).on("message", new Emitter.Listener() {

	    	  @Override
	    	  public void call(Object... args) {
 		  
	    		  if (args.length == 0)
	    			  return;

	    		  String msg = (String) args[0];
	    		  Log.d("debug", "Received: " + msg);
	    	  }

	    	}).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

	    	  @Override
	    	  public void call(Object... args) {
	    		  Log.d("debug", "[INFO] Disconnected from server");
	    	  }

	    	});
	    	
	    	socket.connect();
	    	
//	        HttpClient httpclient = new DefaultHttpClient();
//	        HttpResponse response;
//	        String responseString = null;
//	        try {
//	            response = httpclient.execute(new HttpGet(uri[0]));
//	            StatusLine statusLine = response.getStatusLine();
//	            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
//	                ByteArrayOutputStream out = new ByteArrayOutputStream();
//	                response.getEntity().writeTo(out);
//	                out.close();
//	                responseString = out.toString();
//	            } else{
//	                //Closes the connection.
//	                response.getEntity().getContent().close();
//	                throw new IOException(statusLine.getReasonPhrase());
//	            }
//	        } catch (ClientProtocolException e) {
//	            //TODO Handle problems..
//	        } catch (IOException e) {
//	            //TODO Handle problems..
//	        }
//	        return responseString;
	        
	        return "";
	    }

	    @Override
	    protected void onPostExecute(String result) {
	        super.onPostExecute(result);
	        //Do anything with response..
	        Log.d("debug", result);
	    }
	}
	
	public static void get(String url) {
		new RequestTask().execute(url);
	}
}
