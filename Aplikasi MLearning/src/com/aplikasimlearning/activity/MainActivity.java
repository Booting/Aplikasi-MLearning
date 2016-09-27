package com.aplikasimlearning.activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.aplikasimlearning.referensi.JSONParser;
import com.aplikasimlearning.referensi.Referensi;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;

public class MainActivity extends DashBoardActivity {
	private EditText txtUsername, txtPassword;
	private String var_username, var_password;
	private JSONArray str_login = null;
	public static String UserCategoriId, FullName;
	
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		setFooter(false, false);
		
		if (getIntent().getBooleanExtra("EXIT", false)) {
            txtFeedBack = "Sign In";
            finish();
        }
		
		if (android.os.Build.VERSION.SDK_INT > 9) {
		    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		    StrictMode.setThreadPolicy(policy);
		}
		
		txtUsername = (EditText) findViewById(R.id.txtUsername);
		txtPassword = (EditText) findViewById(R.id.txtPassword);
	}

	public void btnLoginClick(View v) {
		var_username = txtUsername.getText().toString();
		var_password = txtPassword.getText().toString();
		String stts = "";
		
		if ((var_password.length() == 0) && (var_password.length() == 0)) {
			txtPassword.setError("Username is rquired!");
			txtPassword.setError("Password is required");
		} else if (var_password.length() == 0) {
			txtPassword.setError("Username is rquired!");
		} else if (var_password.length() == 0) {
			txtPassword.setError("Password is required");
		} else {
			String link_url    = Referensi.url+"/login.php?Username="+var_username+"&Password="+var_password;
			JSONParser jParser = new JSONParser();
			JSONObject json    = jParser.AmbilJson(link_url); 
			
			try {
				str_login = json.getJSONArray("statuslogin");
				
				for(int i = 0; i < str_login.length(); i++){
					JSONObject ar = str_login.getJSONObject(i);
					
					String alrt    = ar.getString("hasil");
					stts 		   = ar.getString("st");
					UserCategoriId = ar.getString("UserCategoriId");
					FullName	   = ar.getString("FullName");
			        Toast.makeText(getBaseContext(), alrt, Toast.LENGTH_SHORT).show();
					
	        		if(stts.trim().equals("ok")) {
						txtFeedBack = "Logout";
						btnFeedback.setText(txtFeedBack);
						finish();
						startActivity( new Intent( getApplicationContext(), HomeActivity.class ));
						overridePendingTransition( R.anim.grow_from_bottom, R.anim.pump_top );
					} else {
						Toast.makeText(getBaseContext(), alrt, Toast.LENGTH_SHORT).show();
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}