package com.planet1107.welike.nodechat;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import com.planet1107.welike.activities.ChatActivity;

public class NetClient {
	public static final int BUFFER_SIZE = 2048;
	private Socket socket = null;
	private PrintWriter out = null;
	private BufferedReader in = null;

	private String host = null;
	private String macAddress = null;
	private int port = 7999;
	ChatActivity activity;

	/**
	 * Constructor with Host, Port and MAC Address
	 * @param host
	 * @param port
	 * @param macAddress
	 */
	public NetClient(ChatActivity activity ,String host, int port, String macAddress) {
		this.host = host;
		this.port = port;
		this.macAddress = macAddress;
		this.activity=activity;
	}

	public void connectWithServer() {
		try {
			if (socket == null) {
				socket = new Socket(this.host, this.port);
				out = new PrintWriter(socket.getOutputStream());
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void disConnectWithServer() {
		if (socket != null) {
			if (socket.isConnected()) {
				try {
					//in.close();
					//out.close();
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void sendDataWithString(String message) {
		if (message != null) {
			try{
				
				out.write(message);
				out.flush();
			}catch(NullPointerException e){

			}
		}
	}

	public String receiveDataFromServer() {
		try {
			String message = "";
			int charsRead = 0;
			char[] buffer = new char[BUFFER_SIZE];
			System.out.println("before while " );		
			while ((charsRead = in.read(buffer)) != -1) {		

				message += new String(buffer).substring(0, charsRead);	
				if(message.contains("__JSON__START__")){
					JSONObject job;
					try {
						job = new JSONObject(message.replace("__JSON__START__", "").replace("__JSON__END__", ""));
						activity.onJsonRecieved(job);

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						ChatActivity.onStringRecieved(message);
					}

					message="";
				} else {
					// TODO: handle exception
					ChatActivity.onStringRecieved(message);
					message="";
				}
				//System.out.println("MEssage " + message  );

			}
			System.out.println("end while "  );
			disConnectWithServer(); // disconnect server
			return message;
		} catch (IOException e) {
			return "Error receiving response:  " + e.getMessage();
		}
	}


}

