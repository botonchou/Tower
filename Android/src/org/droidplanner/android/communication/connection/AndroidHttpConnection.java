package org.droidplanner.android.communication.connection;

import java.net.URISyntaxException;
import java.util.Date;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import android.os.AsyncTask;
import android.util.Log;

public class AndroidHttpConnection {

	// TODO
	public interface Callback {
        void execute(String data);
    }
	
	private String url;
	private Socket socket;
	private Callback callback;
	
	public AndroidHttpConnection (String url, Callback callback) {
		this.url = url;
		this.callback = callback;
		new RequestTask().execute(this.url);
	}	
	
	class RequestTask extends AsyncTask<String, String, String>{

	    @Override
	    protected String doInBackground(String... uri) {
	    	
	    	if (uri.length == 0)
	    		return "";
	    	
	    	String server_ip = uri[0];
	    	try {
				socket = IO.socket(server_ip);
			} catch (URISyntaxException e) {			
				e.printStackTrace();
			}
	    	
	    	socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
	    		
	    	  @Override
	    	  public void call(Object... args) {
	    		Log.d("debug", "[INFO] Connected to server");
	    		send("Hi~~ I'm an Android phone !!");
	    	  }
	    	  
	    	}).on("message", new Emitter.Listener() {

	    	  @Override
	    	  public void call(Object... args) {
 		  
	    		  if (args.length != 3)
	    			  return;	    		  
	    		  
	    		  String timestamp = args[0].toString();
	    		  String msg = args[1].toString();
	    		  String md5 = args[2].toString();
	    		  	    		  
	    		  if (!md5.equals(MD5(timestamp + msg)))
	    			  return;
	    		  
	    		  Log.d("debug", String.format("[Message] %s {timestamp: %s, MD5: %s}", msg, timestamp, md5));

	    		  // TODO
	    		  // Find a way to hold the reference of droid, either using callback or through constructor
	    		  callback.execute(msg);
	    		  
	    		  send("ack from Android");
	    	  }

	    	}).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

	    	  @Override
	    	  public void call(Object... args) {
	    		  Log.d("debug", "[INFO] Disconnected from server");
	    	  }

	    	});
	    	
	    	socket.connect();
	        
	        return "";
	    }

	    @Override
	    protected void onPostExecute(String result) {
	        super.onPostExecute(result);
	        //Do anything with response..
	        Log.d("debug", result);
	    }
	}
	
	public String MD5(String md5) {
		try {
			java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
			byte[] array = md.digest(md5.getBytes());
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < array.length; ++i) {
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
			}
			return sb.toString();
		} catch (java.security.NoSuchAlgorithmException e) {
		}
		return null;
	}
	
	public void send(String msg) {
		String timestamp = String.format("%d", new Date().getTime());
		socket.emit("message", timestamp, msg, MD5(timestamp + msg));
	}
}
