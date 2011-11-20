package com.mosafesteps.view;

import java.util.Timer;

import com.mosafesteps.R;
import com.mosafesteps.controller.Controller;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;


public class SetTimerActivity extends Activity {
	
	TimePicker mTimePicker;
	Button mGetContactsButton;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settimeractivity);
        
        Controller.sendContact("foo", "bar", this);
        
       /* mTimePicker = (TimePicker) findViewById(R.id.time_picker);
        mTimePicker.setIs24HourView(true);
        
        mGetContactsButton = (Button) findViewById(R.id.getContactsButton);
        mGetContactsButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
				startActivityForResult(intent, 10);
			}
		});*/
    }
    
    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
      super.onActivityResult(reqCode, resultCode, data);

      switch (reqCode) {
        case (10) :
          if (resultCode == Activity.RESULT_OK) {
            Uri contactData = data.getData();
            Cursor c =  managedQuery(contactData, null, null, null, null);
            if (c.moveToFirst()) {
              String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
              // TODO Whatever you want to do with the selected contact name.
            }
          }
          break;
      }
    }


}
