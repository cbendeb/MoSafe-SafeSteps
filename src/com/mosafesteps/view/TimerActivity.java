package com.mosafesteps.view;

import com.mosafesteps.R;
import com.mosafesteps.controller.Controller;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class TimerActivity extends Activity implements OnClickListener {

	TextView tv; // textview to display the countdown
	TextView mHours;
	TextView mHoursMinutesSemicolon;
	TextView mMinutes;
	TextView mMinutesSecondsSemicolon;
	TextView mSeconds;
	TextView mDestination;
	TextView mStatus;

	MyCount mCounter;
	MyDelayCount mCounterDelay;

	Button mCancel;
	Button madd10;
	Button mArrived;

	long mDelay = 0;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timer);

		mDelay = getIntent().getExtras().getLong("delay");
		long duration = getIntent().getExtras().getLong("duration");
		String destination = getIntent().getExtras().getString("destination");

		int h = (int) duration / 1000 / 60 / 60;
		String hours = Integer.toString(h);
		int m = (int) duration / 1000 / 60 % 60;
		String minutes = Integer.toString(m);
		String seconds = Integer.toString((int) duration / 1000 % 60);
		hours = hours.length() > 1 ? hours : "0" + hours;
		minutes = minutes.length() > 1 ? minutes : "0" + minutes;
		seconds = seconds.length() > 1 ? seconds : "0" + seconds;

		mHours = (TextView) findViewById(R.id.hours);
		mHours.setText(hours);
		mMinutes = (TextView) findViewById(R.id.minutes);
		mMinutes.setText(minutes);
		mSeconds = (TextView) findViewById(R.id.seconds);
		mSeconds.setText(seconds);
		mDestination = (TextView) findViewById(R.id.destination_text);
		mCancel = (Button) findViewById(R.id.cancel_button);
		mCancel.setOnClickListener(this);
		madd10 = (Button) findViewById(R.id.add10_button);
		madd10.setOnClickListener(this);
		mArrived = (Button) findViewById(R.id.arrived_button);
		mArrived.setOnClickListener(this);
		mStatus = (TextView) findViewById(R.id.status_message);

		if (destination != null && !destination.equals("")) {
			String text = mDestination.getText().toString();
			mDestination.setText(text.replace("{LOCATION}", destination));
		} else {
			mDestination.setVisibility(View.GONE);
		}

		mCounter = new MyCount(duration, 100);
		mCounter.start();

		/*
		 * tv = new TextView(this); this.setContentView(tv);
		 * 
		 * // 10000 is the starting number (in milliseconds) // 1000 is the
		 * number to count down each time (in milliseconds)
		 * 
		 * counter.start();
		 */
	}
	

	// countdowntimer is an abstract class, so extend it and fill in methods
	public class MyDelayCount extends CountDownTimer {

		public MyDelayCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(1000);
			mStatus.setText("We've notified your contacts! Please let them know that you're OK.");

		}

		@Override
		public void onTick(long millisUntilFinished) {
			long seconds = (long) (millisUntilFinished / 1000.0 % 60);
			long minutes = Math.round(millisUntilFinished / 1000.0 / 60.0);
			if (minutes >= 1)
				if (minutes == 1)
					mStatus.setText("Hey, where are you? We're going to notify your contacts in "
							+ minutes + " minute");
				else
					mStatus.setText("Hey, where are you? We're going to notify your contacts in "
							+ minutes + " minutes");
			else if (seconds == 1)
				mStatus.setText("Hey, where are you? We're going to notify your contacts in "
						+ seconds + " second");
			else
				mStatus.setText("Hey, where are you? We're going to notify your contacts in "
						+ seconds + " seconds");
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		if (mCounter != null)
			mCounter.cancel();
		if (mCounterDelay != null)
			mCounterDelay.cancel();
	}

	// countdowntimer is an abstract class, so extend it and fill in methods
	public class MyCount extends CountDownTimer {

		public MyCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			mHours.setText("00");
			mMinutes.setText("00");
			mSeconds.setText("00");
			((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(1000);
			int minutesDelay = (int) (mDelay / 1000.0 / 60.0 % 60);
			if (minutesDelay > 1)
				mStatus.setText("Hey, where are you? We're going to notify your contacts in "
						+ minutesDelay + " minutes");
			else
				mStatus.setText("Hey, where are you? We're going to notify your contacts in "
						+ minutesDelay + " minute");
			mCounterDelay = new MyDelayCount(mDelay, 100);
			mCounterDelay.start();

		}

		@Override
		public void onTick(long millisUntilFinished) {

			int h = (int) (millisUntilFinished / 1000.0 / 60.0 / 60.0);
			String hours = Integer.toString(h);
			int m = (int) (millisUntilFinished / 1000.0 / 60.0 % 60);
			String minutes = Integer.toString(m);
			String seconds = Integer
					.toString((int) (millisUntilFinished / 1000.0 % 60));
			hours = hours.length() > 1 ? hours : "0" + hours;
			minutes = minutes.length() > 1 ? minutes : "0" + minutes;
			seconds = seconds.length() > 1 ? seconds : "0" + seconds;

			mHours.setText(hours);
			mMinutes.setText(minutes);
			mSeconds.setText(seconds);
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cancel_button:
			scheduleDone();
			finish();
			break;
		case R.id.add10_button:
			add10Minutes();
			break;
		case R.id.arrived_button:
			scheduleDone();
			finish();
			break;
		}
	}

	private void scheduleDone() {
		Thread t = new Thread() {
			@Override
			public void run() {
				Controller.sendDone(TimerActivity.this);
			}

		};
		t.start();
	}

	public void add10Minutes() {
		int hours = Integer.parseInt(mHours.getText().toString());
		int minutes = Integer.parseInt(mMinutes.getText().toString());
		int seconds = Integer.parseInt(mSeconds.getText().toString());

		long milliseconds = hours * 60 * 60 * 1000 + minutes * 60 * 1000
				+ seconds * 1000;
		milliseconds += 10 * 60 * 1000;

		mCounter.cancel();
		mCounter = new MyCount(milliseconds, 100);
		mCounter.start();
		scheduleAdd10();
	}

	private void scheduleAdd10() {
		Thread t = new Thread() {
			@Override
			public void run() {
				Controller.sendAdd10(TimerActivity.this);
			}

		};
		t.start();
	}

}
