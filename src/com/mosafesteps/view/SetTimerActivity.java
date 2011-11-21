package com.mosafesteps.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;

import com.mosafesteps.R;
import com.mosafesteps.controller.Controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.Toast;

public class SetTimerActivity extends Activity implements
		OnFocusChangeListener, OnTimeChangedListener, OnClickListener,
		OnItemClickListener {

	private TimePicker mTimePicker;
	private Button mStart;
	private EditText mName;
	private EditText mFrom;
	private EditText mTo;
	private EditText mDontCheck;
	private EditText mMessage;
	private ListView mContactList;

	HashMap<String, ListContact> contacts = new HashMap<String, ListContact>();
	ArrayList<ListContact> shownContacts = new ArrayList<SetTimerActivity.ListContact>();

	private AlertDialog.Builder mContactSelector;
	String[] names;
	boolean[] selections;

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

		mStart = (Button) findViewById(R.id.start_button);
		mName = (EditText) findViewById(R.id.name_input);
		mFrom = (EditText) findViewById(R.id.from_input);
		mTo = (EditText) findViewById(R.id.to_input);
		mMessage = (EditText) findViewById(R.id.message_input);
		mDontCheck = (EditText) findViewById(R.id.dont_check_input);
		mContactList = (ListView) findViewById(R.id.contactList);

		mContactSelector = new AlertDialog.Builder(this);

		mName.setOnFocusChangeListener(this);
		mFrom.setOnFocusChangeListener(this);
		mTo.setOnFocusChangeListener(this);
		mDontCheck.setOnFocusChangeListener(this);
		mTimePicker = (TimePicker) findViewById(R.id.timePicker);
		mTimePicker.setIs24HourView(true);
		mTimePicker.setCurrentHour(0);
		mTimePicker.setCurrentHour(0);
		mTimePicker.setOnTimeChangedListener(this);
		mContactList.setOnItemClickListener(this);

		retrieveData();

		mStart.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if (mName.getText().toString().equals("")) {

					int duration = Toast.LENGTH_LONG;

					Toast toast = Toast.makeText(SetTimerActivity.this,
							"Please tell us your name!", duration);
					toast.show();
				} else if (shownContacts.size() == 0
						|| shownContacts.get(0).mName.equals("")) {
					int duration = Toast.LENGTH_LONG;
					Toast toast = Toast.makeText(SetTimerActivity.this,
							"Please tell us who you'd like to notify!",
							duration);
					toast.show();
				} else if (mTimePicker.getCurrentHour() == 0
						&& mTimePicker.getCurrentMinute() == 0) {

					int duration = Toast.LENGTH_LONG;

					Toast toast = Toast
							.makeText(
									SetTimerActivity.this,
									"Please tell us how long it'll take you to get where you're going!",
									duration);
					toast.show();
				} else {
					scheduleSendNotifications();
					Intent intent = new Intent(SetTimerActivity.this,
							TimerActivity.class);
					Bundle bundle = new Bundle();
					bundle.putLong("duration", mTimePicker.getCurrentHour()
							* 60 * 60 * 1000 + mTimePicker.getCurrentMinute()
							* 60 * 1000);
					if (mDontCheck.getText().toString().equals("")) {
						bundle.putLong("delay", 0);

					} else {
						bundle.putLong("delay", Integer.parseInt(mDontCheck
								.getText().toString()) * 60 * 1000);
					}
					bundle.putString("destination", mTo.getText().toString());
					intent.putExtras(bundle);
					startActivity(intent);
				}

			}

		});
		ArrayList<String> sc = new ArrayList<String>();
		sc.add("");
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, sc);
		mContactList.setAdapter(arrayAdapter);

		getContactList();
	}

	private void scheduleSendNotifications() {

		Thread t = new Thread() {
			@Override
			public void run() {
				ArrayList<String> contactNums = new ArrayList<String>();
				for (int i = 0; i < shownContacts.size(); i++) {
					contactNums.add(shownContacts.get(i).mPhone);
				}
				long millisecondsDuration = mTimePicker.getCurrentHour() * 60
						* 60 * 1000 + mTimePicker.getCurrentMinute() * 60
						* 1000;

				long millisecondsDelay = 0;
				if (!mDontCheck.getText().toString().equals("")) {
					millisecondsDelay = Integer.parseInt(mDontCheck.getText()
							.toString()) * 60 * 1000;
				}
				Controller.sendContact(mMessage.getText().toString(),
						millisecondsDuration, millisecondsDelay, contactNums,
						SetTimerActivity.this);
			}
		};
		t.start();

	}

	private void getContactList() {
		// Run query to get all contacts with a phone number
		Uri uri = ContactsContract.Contacts.CONTENT_URI;
		String[] projection = new String[] { ContactsContract.Contacts._ID,
				ContactsContract.Contacts.DISPLAY_NAME, };
		String selection = ContactsContract.Contacts.HAS_PHONE_NUMBER + " = '"
				+ "1" + "'";
		String[] selectionArgs = null;
		String sortOrder = ContactsContract.Contacts.DISPLAY_NAME
				+ " COLLATE LOCALIZED ASC";

		Cursor c1 = managedQuery(ContactsContract.Contacts.CONTENT_URI,
				new String[] { ContactsContract.Contacts._ID,
						ContactsContract.Contacts.DISPLAY_NAME }, null, null,
				ContactsContract.Contacts.DISPLAY_NAME
						+ " COLLATE LOCALIZED ASC");

		Cursor cursor = managedQuery(uri, projection, selection, selectionArgs,
				sortOrder);
		String query = "";
		ArrayList<String> contactsForAdapter = new ArrayList<String>();

		// process the contacts to get their IDs
		while (cursor.moveToNext()) {
			String contactId = cursor.getString(cursor
					.getColumnIndex(ContactsContract.Contacts._ID));
			String contactName = cursor.getString(cursor
					.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			ListContact contact;
			if (contacts.containsKey(contactId)) {
				contact = contacts.get(contactId);

				contact.mName = contactName;
			} else {
				contact = new ListContact();

				contact.mId = contactId;
				contact.mName = contactName;
				contacts.put(contactId, contact);
				contactsForAdapter.add(contact.mName);
			}
			query += Phone.CONTACT_ID + " = " + contactId + " OR ";
		}

		// Run the query to get the contacts' phone numbers
		if (query.length() > 0) {
			query = query.substring(0, query.length() - 3);
			Cursor phones = managedQuery(Phone.CONTENT_URI, null, query, null,
					null);

			while (phones.moveToNext()) {
				String contactId = phones.getString(phones
						.getColumnIndex(Phone.CONTACT_ID));
				String phoneNumber = phones.getString(phones
						.getColumnIndex(Phone.NUMBER));
				ListContact contact = contacts.get(contactId);
				if (contact != null) {
					contact.mPhone = phoneNumber;
				}
			}
			// phones.close();
		}
		// cursor.close();

		/*
		 * String[] fields = new String[] { ContactsContract.Data.DISPLAY_NAME
		 * }; SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
		 * R.layout.contact_entry, cursor, fields, new int[] {
		 * R.id.contactEntryText }); mContactList.setAdapter(adapter);
		 */

		HashMap<String, ListContact> newmap = new HashMap<String, SetTimerActivity.ListContact>();

		Iterator<String> keys = contacts.keySet().iterator();
		String key;
		ListContact value;
		while (keys.hasNext()) {
			key = keys.next();
			value = contacts.get(key);
			newmap.put(value.mName, value);
		}
		contacts = newmap;
		names = new String[contactsForAdapter.size()];
		selections = new boolean[contactsForAdapter.size()];
		contactsForAdapter.toArray(names);
		loadSelections();

		mContactSelector
				.setTitle("Choose your contacts")
				.setMultiChoiceItems(names, selections,
						multiChoiceClickListener)
				.setPositiveButton("OK", returnFromContactSelector).create();

	}

	OnMultiChoiceClickListener multiChoiceClickListener = new OnMultiChoiceClickListener() {

		public void onClick(DialogInterface dialog, int which, boolean isChecked) {
			selections[which] = isChecked;

		}

	};

	DialogInterface.OnClickListener returnFromContactSelector = new DialogInterface.OnClickListener() {

		public void onClick(DialogInterface dialog, int which) {
			saveSelections();
			shownContacts.clear();
			for (int i = 0; i < selections.length; i++) {
				if (selections[i]) {
					shownContacts.add(contacts.get(names[i]));
				}
			}
			populateContactList();
		}
	};

	private void saveSelections() {
		for (int i = 0; i < selections.length; i++) {
			sEditor.putBoolean(names[i], selections[i]);
			sEditor.commit();
		}
	}

	private void loadSelections() {
		boolean shown;
		shownContacts.clear();
		for (int i = 0; i < names.length; i++) {
			shown = sSharedPref.getBoolean(names[i], false);
			if (shown) {
				selections[i] = true;
				if (contacts.containsKey(names[i])) {
					shownContacts.add(contacts.get(names[i]));
				}
			}
		}
		populateContactList();
	}

	private void populateContactList() {
		if (shownContacts.size() == 0) {
			ListContact lc = new ListContact();
			lc.mName = "";
			shownContacts.add(lc);
		}
		ArrayAdapter<ListContact> arrayAdapter = new ArrayAdapter<ListContact>(
				SetTimerActivity.this, android.R.layout.simple_list_item_1,
				shownContacts);
		mContactList.setAdapter(arrayAdapter);
	}

	/*
	 * private void getContactsAndNumbers(){ // // Find contact based on name.
	 * // ContentResolver cr = getContentResolver(); Cursor cursor =
	 * cr.query(ContactsContract.Contacts.CONTENT_URI, null, "DISPLAY_NAME = '"
	 * + NAME + "'", null, null); if (cursor.moveToFirst()) { String contactId =
	 * cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
	 * // // Get all phone numbers. // Cursor phones =
	 * cr.query(Phone.CONTENT_URI, null, Phone.CONTACT_ID + " = " + contactId,
	 * null, null); while (phones.moveToNext()) { String number =
	 * phones.getString(phones.getColumnIndex(Phone.NUMBER)); int type =
	 * phones.getInt(phones.getColumnIndex(Phone.TYPE)); switch (type) { case
	 * Phone.TYPE_HOME: // do something with the Home number here... break; case
	 * Phone.TYPE_MOBILE: // do something with the Mobile number here... break;
	 * case Phone.TYPE_WORK: // do something with the Work number here... break;
	 * } } phones.close(); // // Get all email addresses. // Cursor emails =
	 * cr.query(Email.CONTENT_URI, null, Email.CONTACT_ID + " = " + contactId,
	 * null, null); while (emails.moveToNext()) { String email =
	 * emails.getString(emails.getColumnIndex(Email.DATA)); int type =
	 * emails.getInt(emails.getColumnIndex(Phone.TYPE)); switch (type) { case
	 * Email.TYPE_HOME: // do something with the Home email here... break; case
	 * Email.TYPE_WORK: // do something with the Work email here... break; } }
	 * emails.close(); } cursor.close(); }
	 */

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

	private class ListContact {
		public String mName;
		public String mId;
		public String mPhone;

		@Override
		public String toString() {
			return mName;
		}
	}

	public void onClick(View v) {

	}

	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

		mContactSelector.show();
	}

}
