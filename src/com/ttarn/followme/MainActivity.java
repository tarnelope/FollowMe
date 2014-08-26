package com.ttarn.followme;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {
	
	private Button openMapBtn;
	private Button inviteBtn;
	
	private ContactsFragment contactsFragment;
	
	
	@Override
	public void onCreate(Bundle b) {
		super.onCreate(b);
		setContentView(R.layout.activity_main);
		
		FragmentManager fm = getFragmentManager();
		contactsFragment = (ContactsFragment) fm.findFragmentById(R.id.main_frag_contact);
		fm.beginTransaction().hide(contactsFragment).commit();
		
		openMapBtn = (Button) findViewById(R.id.open_map_btn);
		openMapBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent mapIntent = new Intent(getBaseContext(), MapActivity.class);
				startActivity(mapIntent);
			}
		});
		
		inviteBtn = (Button) findViewById(R.id.invite_btn);
		inviteBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d("Main", "INVITE BUTTON PRESSED!");
				getFragmentManager().beginTransaction().show(contactsFragment).addToBackStack(null).commit();
				
			}
		});
		
	}

}
