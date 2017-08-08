package com.example.voicereminder;

import com.example.voicereminder.R;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.MediaColumns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

public class VoiceReminderDetailsActivity extends Activity {
	
	protected static final int SELECT_PICTURE = 0;

	private VoiceReminderDBHelper dbHelper = new VoiceReminderDBHelper(this);
	
	private VoiceReminderModel alarmDetails;
	
	private TimePicker timePicker;
	private EditText edtName;
	private CustomSwitch chkWeekly;
	private CustomSwitch chkSunday;
	private CustomSwitch chkMonday;
	private CustomSwitch chkTuesday;
	private CustomSwitch chkWednesday;
	private CustomSwitch chkThursday;
	private CustomSwitch chkFriday;
	private CustomSwitch chkSaturday;
	private TextView txtToneSelection;
	 String selectedImagePath;
		//ADDED
		 String filemanagerstring,imagePath;
		 int column_index;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_ACTION_BAR);

		setContentView(R.layout.activity_details);

		getActionBar().setTitle("Create Voice Reminder");
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		timePicker = (TimePicker) findViewById(R.id.alarm_details_time_picker);
		edtName = (EditText) findViewById(R.id.alarm_details_name);
		chkWeekly = (CustomSwitch) findViewById(R.id.alarm_details_repeat_weekly);
		chkSunday = (CustomSwitch) findViewById(R.id.alarm_details_repeat_sunday);
		chkMonday = (CustomSwitch) findViewById(R.id.alarm_details_repeat_monday);
		chkTuesday = (CustomSwitch) findViewById(R.id.alarm_details_repeat_tuesday);
		chkWednesday = (CustomSwitch) findViewById(R.id.alarm_details_repeat_wednesday);
		chkThursday = (CustomSwitch) findViewById(R.id.alarm_details_repeat_thursday);
		chkFriday = (CustomSwitch) findViewById(R.id.alarm_details_repeat_friday);
		chkSaturday = (CustomSwitch) findViewById(R.id.alarm_details_repeat_saturday);
		txtToneSelection = (TextView) findViewById(R.id.alarm_label_tone_selection);
		Button bt=(Button) findViewById(R.id.voice_record);
		bt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(VoiceReminderDetailsActivity.this,Recorder_voice.class);
				startActivity(intent);
				
			}
		});
		
		
		
		long id = getIntent().getExtras().getLong("id");
		
		if (id == -1) {
			alarmDetails = new VoiceReminderModel();
		} else {
			alarmDetails = dbHelper.getAlarm(id);
			
			timePicker.setCurrentMinute(alarmDetails.timeMinute);
			timePicker.setCurrentHour(alarmDetails.timeHour);
			
			edtName.setText(alarmDetails.name);
			
			chkWeekly.setChecked(alarmDetails.repeatWeekly);
			chkSunday.setChecked(alarmDetails.getRepeatingDay(VoiceReminderModel.SUNDAY));
			chkMonday.setChecked(alarmDetails.getRepeatingDay(VoiceReminderModel.MONDAY));
			chkTuesday.setChecked(alarmDetails.getRepeatingDay(VoiceReminderModel.TUESDAY));
			chkWednesday.setChecked(alarmDetails.getRepeatingDay(VoiceReminderModel.WEDNESDAY));
			chkThursday.setChecked(alarmDetails.getRepeatingDay(VoiceReminderModel.THURSDAY));
			chkFriday.setChecked(alarmDetails.getRepeatingDay(VoiceReminderModel.FRDIAY));
			chkSaturday.setChecked(alarmDetails.getRepeatingDay(VoiceReminderModel.SATURDAY));

			txtToneSelection.setText(RingtoneManager.getRingtone(this, alarmDetails.alarmTone).getTitle(this));
		}
		
		
	final LinearLayout ringToneContainer = (LinearLayout) findViewById(R.id.alarm_ringtone_container);
		ringToneContainer.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				 Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
		            intent.setType("audio/*");
		            intent.setAction(Intent.ACTION_GET_CONTENT);
		            startActivityForResult(Intent.createChooser(intent,"Select Ringtone"),1);
		            
				//Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
				//startActivityForResult(intent , 1);
			}
		});
		
	}	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (resultCode == RESULT_OK) {
	        if(requestCode==1)
	        {
	        	Uri selectedImageUri = data.getData();

	            //OI FILE Manager
	            filemanagerstring = selectedImageUri.getPath();

	            //MEDIA GALLERY
	            selectedImagePath = getPath(selectedImageUri);
	            imagePath.getBytes();
	            alarmDetails.alarmTone=selectedImageUri;
	            
	        }
	    }
	}
	public String getPath(Uri uri) {
		String[] projection = { MediaColumns.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		column_index = cursor
		        .getColumnIndexOrThrow(MediaColumns.DATA);
		cursor.moveToFirst();
		 imagePath = cursor.getString(column_index);
		 txtToneSelection.setText(imagePath.toString());
		 
		return cursor.getString(column_index);
		}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.alarm_details, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
			case android.R.id.home: {
				finish();
				break;
			}
			case R.id.action_save_alarm_details: {
				updateModelFromLayout();
				
				VoiceReminderManagerHelper.cancelAlarms(this);
				
				if (alarmDetails.id < 0) {
					dbHelper.createAlarm(alarmDetails);
				} else {
					dbHelper.updateAlarm(alarmDetails);
				}
				
				VoiceReminderManagerHelper.setAlarms(this);
				
				setResult(RESULT_OK);
				finish();
			}
		}

		return super.onOptionsItemSelected(item);
	}

	
	private void updateModelFromLayout() {		
		alarmDetails.timeMinute = timePicker.getCurrentMinute().intValue();
		alarmDetails.timeHour = timePicker.getCurrentHour().intValue();
		alarmDetails.name = edtName.getText().toString();
		alarmDetails.repeatWeekly = chkWeekly.isChecked();	
		alarmDetails.setRepeatingDay(VoiceReminderModel.SUNDAY, chkSunday.isChecked());	
		alarmDetails.setRepeatingDay(VoiceReminderModel.MONDAY, chkMonday.isChecked());	
		alarmDetails.setRepeatingDay(VoiceReminderModel.TUESDAY, chkTuesday.isChecked());
		alarmDetails.setRepeatingDay(VoiceReminderModel.WEDNESDAY, chkWednesday.isChecked());	
		alarmDetails.setRepeatingDay(VoiceReminderModel.THURSDAY, chkThursday.isChecked());
		alarmDetails.setRepeatingDay(VoiceReminderModel.FRDIAY, chkFriday.isChecked());
		alarmDetails.setRepeatingDay(VoiceReminderModel.SATURDAY, chkSaturday.isChecked());
		alarmDetails.isEnabled = true;
	}
	
}
