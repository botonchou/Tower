package org.droidplanner.android.communication.connection;

import java.net.URISyntaxException;
import java.util.Date;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

public class SocketIOConnection {

	// TODO
	public interface Callback {
        void execute(String[] data);
    }
	
	private String url;
	private Socket socket;
	private Callback callback;
	final public static String server_ip = "http://192.168.1.199:8080";
	static private SocketIOConnection socket_conn = new SocketIOConnection(server_ip);
	
	static public SocketIOConnection getInstance() {
		return socket_conn;
	}
	
	private SocketIOConnection (String url) {
		this.url = url;
		this.callback = new Callback() {
			@Override
			public void execute(String[] data) {
				// do nothing.
			}
		};
			
		new RequestTask().execute(this.url);
	}
	
	public void setCallback(Callback callback) {
		this.callback = callback;
	}
	
	@SuppressLint("DefaultLocale")
	public void send_msg(final Object... msgs) {
		String timestamp = String.format("%d", new Date().getTime());
		
		// Note: args = [timestamp, msgs]
		Object[] args = new Object[msgs.length + 1];
		args[0] = (Object) timestamp;
		System.arraycopy(msgs, 0, args, 1, msgs.length);
		
		socket.emit("message", args);
	}
	
	public void send(final Object... msg) {
		send_msg(msg);
	}
	
	public void send(Callback callback, final Object... msg) {
		// TODO push callback into callback_queue
		send_msg(msg);
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
	    		send_msg("Hi~~ I'm an Android phone !!");
	    	  }
	    	  
	    	}).on("message", new Emitter.Listener() {

	    	  @Override
	    	  public void call(Object... args) {
	    		  // return if it's hello message
	    		  if (args.length < 2)
	    			  return;
	    		  
	    		  String timestamp = args[0].toString();
	    		  
	    		  String[] msg = new String[args.length - 1];
	    		  for (int i = 0; i < msg.length; ++i)
	    			  msg[i] = args[i + 1].toString();
	    		  
	    		  Log.d("debug", String.format("[Message] {%s} {timestamp: %s}", join(msg, ", "), timestamp));

	    		  // TODO
	    		  // Find a way to hold the reference of droid, either using callback or through constructor
	    		  callback.execute(msg);
	    		  
	    		  send_msg("ack from Android");
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
	
	public String join(String[] strs, String token) {
		String str = strs[0];
		for (int i=1; i<strs.length; ++i)
			str += token + strs[i];
		return str;
	}
	
	public String join(Object[] obj) {
		String str = "";
		for (Object o : obj)
			str += o.toString();
		return str;
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
}
