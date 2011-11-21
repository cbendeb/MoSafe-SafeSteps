package com.mosafesteps.view;

import com.mosafesteps.R;
import com.mosafesteps.R.layout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Mobsafety extends Activity {
    /** Called when the activity is first created. */
	Button mSetTimer;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mSetTimer = (Button) findViewById(R.id.buttonSetTimer);
        
        mSetTimer.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Intent intent = new Intent(Mobsafety.this, SetTimerActivity.class);
				startActivity(intent);
				
			}
		});
    }
}