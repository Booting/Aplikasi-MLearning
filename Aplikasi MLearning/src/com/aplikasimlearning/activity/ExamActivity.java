package com.aplikasimlearning.activity;

import android.view.Window;
import android.widget.TextView;
import android.os.Bundle;

public class ExamActivity extends DashBoardActivity {
	private TextView txtGreetings;
	private String FullName = MainActivity.FullName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_exam);
		setFooter(true, true);
		
		txtGreetings = (TextView) findViewById(R.id.txtGreetings);
		txtGreetings.setText("Welcome, " + FullName);
	}

}