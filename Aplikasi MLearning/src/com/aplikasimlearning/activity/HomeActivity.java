package com.aplikasimlearning.activity;

import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.content.Intent;
import android.os.Bundle;

public class HomeActivity extends DashBoardActivity {
	private TextView txtGreetings;
	private String FullName = MainActivity.FullName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_home);
		setFooter(true, true);
		
		txtGreetings = (TextView) findViewById(R.id.txtGreetings);
		txtGreetings.setText("Welcome, " + FullName);
	}

	public void btnModulClick(View v) {
		startActivity( new Intent( getApplicationContext(), ModulActivity.class ) );
		overridePendingTransition( R.anim.slide_in_left, R.anim.slide_out_left );
	}
	
	public void btnTugasClick(View v) {
		startActivity( new Intent( getApplicationContext(), TugasActivity.class ) );
		overridePendingTransition( R.anim.slide_in_left, R.anim.slide_out_left );
	}
	
	public void btnExamClick(View v) {
		startActivity( new Intent( getApplicationContext(), ExamActivity.class ) );
		overridePendingTransition( R.anim.slide_in_left, R.anim.slide_out_left );
	}
	
	public void btnAboutClick(View v) { }
	
}