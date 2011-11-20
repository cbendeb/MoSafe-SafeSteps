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
	public static String DEVICE_ID = "device_id";
	public static String CMD = "cmd";

	private static String URL = "https://api.cloudmine.me/v1/app/f1d9ec4fd59347b6ba349cb434e99e95/text";

	/*
	 * PUT https://api.cloudmine.me/v1/app/{f1d9ec4fd59347b6ba349cb434e99e95
	 * }/text Content-Type: application/json X-CloudMine-ApiKey:
	 * da3e81090f19479ca887f0e0f84d9c52
	 * 
	 * { "key1": "value1", "key2": { "inner1": "innerVal1", "inner2":
	 * "innerVal2" }, "key3": [42, 43] }
	 */

	public static void sendContact(String name, String phone, Activity act) {
		try {
			List<NameValuePair> urlParams = new ArrayList<NameValuePair>();
		    HttpPost httpost = new HttpPost(URL);
		    DefaultHttpClient httpclient = new DefaultHttpClient();

			TelephonyManager tManager = (TelephonyManager) act
					.getSystemService(Context.TELEPHONY_SERVICE);

			JSONObject jsonObj = new JSONObject();
			jsonObj.put(CONTACT_NAME, name);
			jsonObj.put(CONTACT_PHONE, phone);
			jsonObj.put(DEVICE_ID, tManager.getDeviceId());
		    StringEntity se = new StringEntity(jsonObj.toString());

		    httpost.setEntity(se);
		    httpost.setHeader("Content-type", "application/json");
		    HttpResponse response = httpclient.execute(httpost);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
