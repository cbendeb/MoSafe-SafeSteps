package com.mosafesteps.view;

import java.util.Timer;

import com.mosafesteps.R;
import com.mosafesteps.controller.Controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.Toast;

public class SetTimerActivity extends Activity implements
		OnFocusChangeListener, OnTimeChangedListener {

	TimePicker mTimePicker;
	Button mStart;
	EditText mName;
	EditText mFrom;
	EditText mTo;
	EditText mDontCheck;
	EditText mMessage;

	// //////////////////////////
	// Persistent storage / Preferences
	private static SharedPreferences sSharedPref;
	private static SharedPreferences.Editor sEditor;

	private String statusMessageALL = "{NAME} was going from {FROM} to {TO} and didn't check in with SafeSteps. Please give them a call to make sure they're doing OK!";
	private String statusMessageNAME = "{NAME} didn't check in with SafeSteps. Please give them a call to make sure they're doing OK!";
	private String statusMessageNAMEFROM = "{NAME} was going from {FROM} and didn't check in with SafeSteps. Please give them a call to make sure they're doing OK!";
	private String statusMessageNAMETO = "{NAME} was going to {TO} and didn't check in with SafeSteps. Please give them a call to make sure they're doing OK!";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settimeractivity);

		sSharedPref = this.getSharedPreferences("com.mosafesteps.SHARED_PREFS",
				Context.MODE_PRIVATE);
		sEditor = sSharedPref.edit();

		Controller.sendContact("foo", "123456789", this);

		mStart = (Button) findViewById(R.id.start_button);
		mName = (EditText) findViewById(R.id.name_input);
		mFrom = (EditText) findViewById(R.id.from_input);
		mTo = (EditText) findViewById(R.id.to_input);
		mMessage = (EditText) findViewById(R.id.message_input);
		mDontCheck = (EditText) findViewById(R.id.dont_check_input);
		mName.setOnFocusChangeListener(this);
		mFrom.setOnFocusChangeListener(this);
		mTo.setOnFocusChangeListener(this);
		mDontCheck.setOnFocusChangeListener(this);
		mTimePicker = (TimePicker) findViewById(R.id.timePicker);
		mTimePicker.setIs24HourView(true);
		mTimePicker.setCurrentHour(0);
		mTimePicker.setCurrentHour(0);
		mTimePicker.setOnTimeChangedListener(this);

		retrieveData();

		mStart.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if (!mName.getText().toString().equals("")
						&& !(mTimePicker.getCurrentHour() == 0 && mTimePicker
								.getCurrentMinute() == 0)) {
					Intent intent = new Intent(SetTimerActivity.this,
							TimerActivity.class);
					startActivity(intent);
				} else if (mName.getText().toString().equals("")) {

					int duration = Toast.LENGTH_LONG;

					Toast toast = Toast.makeText(SetTimerActivity.this,
							"Please tell us your name!", duration);
					toast.show();
				} else {

					int duration = Toast.LENGTH_LONG;

					Toast toast = Toast
							.makeText(
									SetTimerActivity.this,
									"Please tell us how long it'll take you to get where you're going!",
									duration);
					toast.show();
				}

			}
		});

		/*
		 * mTimePicker = (TimePicker) findViewById(R.id.time_picker);
		 * mTimePicker.setIs24HourView(true);
		 * 
		 * mGetContactsButton = (Button) findViewById(R.id.getContactsButton);
		 * mGetContactsButton.setOnClickListener(new View.OnClickListener() {
		 * 
		 * public void onClick(View v) { Intent intent = new
		 * Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
		 * startActivityForResult(intent, 10); } });
		 */
	}

	private void retrieveData() {

		String name = sSharedPref.getString("NAME", "");
		String from = sSharedPref.getString("FROM", "");
		String to = sSharedPref.getString("TO", "");
		String dontCheck = sSharedPref.getString("DONT_CHECK", "");
		int minutes = sSharedPref.getInt("MINUTES", 0);
		int hours = sSharedPref.getInt("HOURS", 0);

		mName.setText(name);
		mFrom.setText(from);
		mTo.setText(to);
		mDontCheck.setText(dontCheck);
		mTimePicker.setCurrentHour(hours);
		mTimePicker.setCurrentMinute(minutes);
	}

	@Override
	public void onActivityResult(int reqCode, int resultCode, Intent data) {
		super.onActivityResult(reqCode, resultCode, data);

		switch (reqCode) {
		case (10):
			if (resultCode == Activity.RESULT_OK) {
				Uri contactData = data.getData();
				Cursor c = managedQuery(contactData, null, null, null, null);
				if (c.moveToFirst()) {
					String name = c
							.getString(c
									.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
					// TODO Whatever you want to do with the selected contact
					// name.
				}
			}
			break;
		}
	}

	public void onFocusChange(View v, boolean hasFocus) {
		switch (v.getId()) {
		case R.id.name_input:
			if (!hasFocus) {
				sEditor.putString("NAME", mName.getText().toString());
				sEditor.commit();
				populateText();
			}
			break;
		case R.id.from_input:
			if (!hasFocus) {
				sEditor.putString("FROM", mFrom.getText().toString());
				sEditor.commit();
				populateText();
			}
			break;
		case R.id.to_input:
			if (!hasFocus) {
				sEditor.putString("TO", mTo.getText().toString());
				sEditor.commit();
				populateText();
			}
			break;
		case R.id.dont_check_input:
			if (!hasFocus) {
				sEditor.putString("DONT_CHECK", mDontCheck.getText().toString());
				sEditor.commit();
				populateText();
			}
			break;
		default:
		}
	}

	private void populateText() {
		String name = mName.getText().toString();
		String to = mTo.getText().toString();
		String from = mFrom.getText().toString();
		String newMessage;

		if (name.equals("")) {
			mMessage.setText("");
			return;
		}

		if (from.equals("") && to.equals("")) {
			StringBuilder sb = new StringBuilder(statusMessageNAME);
			int index = sb.indexOf("{NAME}");
			sb.replace(index, index + 6, name);

			mMessage.setText(sb.toString());
		} else if (!from.equals("") && to.equals("")) {
			StringBuilder sb = new StringBuilder(statusMessageNAMEFROM);
			int index = sb.indexOf("{NAME}");
			sb.replace(index, index + 6, name);
			index = sb.indexOf("{FROM}");
			sb.replace(index, index + 6, from);

			mMessage.setText(sb.toString());
		} else if (from.equals("") && !to.equals("")) {
			StringBuilder sb = new StringBuilder(statusMessageNAMETO);
			int index = sb.indexOf("{NAME}");
			sb.replace(index, index + 6, name);
			index = sb.indexOf("{TO}");
			sb.replace(index, index + 4, to);

			mMessage.setText(sb.toString());
		} else {
			StringBuilder sb = new StringBuilder(statusMessageALL);
			int index = sb.indexOf("{NAME}");
			sb.replace(index, index + 6, name);
			index = sb.indexOf("{TO}");
			sb.replace(index, index + 4, to);
			index = sb.indexOf("{FROM}");
			sb.replace(index, index + 6, from);

			mMessage.setText(sb.toString());

		}
	}

	public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
		sEditor.putInt("HOURS", hourOfDay);
		sEditor.putInt("MINUTES", minute);
		sEditor.commit();

	}

}
