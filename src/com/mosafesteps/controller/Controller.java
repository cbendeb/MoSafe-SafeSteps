package com.mosafesteps.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.telephony.TelephonyManager;

public class Controller {

	public static String CONTACT_NAME = "contact_name";
	public static String CONTACT_PHONE = "contact_phone";
	public static String DEVICE_ID = "device";
	public static String CMD = "cmd";
	public static String MESSAGE = "message";
	public static String DURATION = "duration";
	public static String DELAY = "delay";
	public static String PHONE = "phone";
	private static String MESSAGEURL = "http://gentle-autumn-1780.herokuapp.com/trips/create_android?";
	private static String CONTACTURL = "http://gentle-autumn-1780.herokuapp.com/notifications/create_android?";
	private static String DELAYURL = "http://gentle-autumn-1780.herokuapp.com/notifications/delay_notifications?";
    private static String CANCELURL = "http://gentle-autumn-1780.herokuapp.com/notifications/cancel_notifications?";

	public static void sendAdd10(Activity act){
		List<NameValuePair> urlParams = new ArrayList<NameValuePair>();
	    DefaultHttpClient httpclient = new DefaultHttpClient();

		TelephonyManager tManager = (TelephonyManager) act
				.getSystemService(Context.TELEPHONY_SERVICE);
		String device = tManager.getDeviceId();
		urlParams.add(new BasicNameValuePair(DEVICE_ID, device));
		String paramString = URLEncodedUtils.format(urlParams, "utf-8");
		String url = DELAYURL + paramString;
	    HttpPost httpost = new HttpPost(url);
	    try {
			HttpResponse response = httpclient.execute(httpost);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void sendDone(Activity act){
		List<NameValuePair> urlParams = new ArrayList<NameValuePair>();
	    DefaultHttpClient httpclient = new DefaultHttpClient();

		TelephonyManager tManager = (TelephonyManager) act
				.getSystemService(Context.TELEPHONY_SERVICE);
		String device = tManager.getDeviceId();
		urlParams.add(new BasicNameValuePair(DEVICE_ID, device));
		String paramString = URLEncodedUtils.format(urlParams, "utf-8");
		String url = CANCELURL + paramString;
	    HttpPost httpost = new HttpPost(url);
	    try {
			HttpResponse response = httpclient.execute(httpost);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void sendContact(String message, long duration, long delay, ArrayList<String> contactNums, Activity act) {
		try {
			List<NameValuePair> urlParams = new ArrayList<NameValuePair>();
		    DefaultHttpClient httpclient = new DefaultHttpClient();

			TelephonyManager tManager = (TelephonyManager) act
					.getSystemService(Context.TELEPHONY_SERVICE);
			String device = tManager.getDeviceId();
			
			//send message information
			urlParams.add(new BasicNameValuePair(DEVICE_ID, device));
			urlParams.add(new BasicNameValuePair(MESSAGE, message));
			urlParams.add(new BasicNameValuePair(DURATION, Long.toString(duration)));
			
			String paramString = URLEncodedUtils.format(urlParams, "utf-8");
			String url = MESSAGEURL + paramString;
			
		    HttpPost httpost = new HttpPost(url);
		    HttpResponse response = httpclient.execute(httpost);
		    
		    for(int i = 0; i < contactNums.size(); i++){
		    	urlParams.clear();
				urlParams.add(new BasicNameValuePair(DEVICE_ID, device));
				urlParams.add(new BasicNameValuePair(DELAY, Long.toString(delay)));
				urlParams.add(new BasicNameValuePair(PHONE, contactNums.get(i)));
				paramString = URLEncodedUtils.format(urlParams, "utf-8");
				url = CONTACTURL + paramString;
				httpost = new HttpPost(url);
				response = httpclient.execute(httpost);
		    }
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
