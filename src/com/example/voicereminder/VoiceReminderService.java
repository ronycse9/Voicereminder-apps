package com.example.voicereminder;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class VoiceReminderService extends Service {

	public static String TAG = VoiceReminderService.class.getSimpleName();
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		Intent alarmIntent = new Intent(getBaseContext(), VoiceReminderScreen.class);
		alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		alarmIntent.putExtras(intent);
		getApplication().startActivity(alarmIntent);
		
		VoiceReminderManagerHelper.setAlarms(this);
		
		return super.onStartCommand(intent, flags, startId);
	}
	
}