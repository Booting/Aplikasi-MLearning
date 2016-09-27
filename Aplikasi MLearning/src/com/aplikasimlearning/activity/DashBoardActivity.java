package com.aplikasimlearning.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.Toast;
import android.app.AlertDialog.Builder;

/**
 *  @author : Daniel FJP Napitupulu
 *  @Email  : if09033@gmail.com
 *  @Website: http://booting09.com
 */
public abstract class DashBoardActivity extends ListActivity {
	// Constant for identifying the dialog
	private static final int DIALOG_ALERT = 10; 
	public Button btnFeedback;
	public static String txtFeedBack = "Sign In";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
    }
    
    public void setFooter(boolean btnHomeVisible, boolean btnFeedbackVisible) {
    	ViewStub stub = (ViewStub) findViewById(R.id.vsHeader);
    	View inflated = stub.inflate();
          
    	Button btnHome = (Button) inflated.findViewById(R.id.btnHome);
    	if(!btnHomeVisible)
    		btnHome.setVisibility(View.INVISIBLE);
    		
    	btnFeedback = (Button) inflated.findViewById(R.id.btnSignIn);
    	btnFeedback.setText(txtFeedBack);
    	if(!btnFeedbackVisible)
    		btnFeedback.setVisibility(View.INVISIBLE);
    }
    
    /**
     * Home button click handler
     * @param v
     */
    public void btnHomeClick(View v) {
    	startActivity( new Intent( getApplicationContext(), HomeActivity.class ));
		overridePendingTransition( R.anim.grow_from_bottom, R.anim.pump_top );
    }
    
    /**
     * Logout button click handler
     * @param v
     */
	@SuppressWarnings("deprecation")
	public void btnFeedbackClick(View v) {
		if (btnFeedback.getText().toString().equals("Sign In")) {
			startActivity( new Intent( getApplicationContext(), HomeActivity.class ) );
			overridePendingTransition( R.anim.grow_from_bottom, R.anim.pump_top );
		} else {
			showDialog(DIALOG_ALERT);
		}
	}
    
	@SuppressWarnings("deprecation")
	@Override
    protected Dialog onCreateDialog(int id) {
      switch (id) {
        case DIALOG_ALERT:
        // Create out AlterDialog
        Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Apakah Anda Benar-Benar ingin keluar?");
        builder.setCancelable(true);
        builder.setPositiveButton("Ya", new OkOnClickListener());
        builder.setNegativeButton("Tidak", new CancelOnClickListener());
        AlertDialog dialog = builder.create();
        dialog.show();
       }
      return super.onCreateDialog(id);
    }

    private final class CancelOnClickListener implements DialogInterface.OnClickListener {
    	@Override
    	public void onClick(DialogInterface dialog, int which) {
    		dialog.cancel();
    	}
    }

    private final class OkOnClickListener implements DialogInterface.OnClickListener {
    	@Override
    	public void onClick(DialogInterface dialog, int which) {
    		Intent intent = new Intent(getApplicationContext(), MainActivity.class);
    		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    		intent.putExtra("EXIT", true);
    		overridePendingTransition( R.anim.slide_in_left, R.anim.slide_out_left );
    		startActivity(intent);
    		finish();
      }
    } 
    
    // TODO Wrapper method to encapsulate getApplicationContext() that is needed for displaying Toast message
	protected void showToast(String text) {
		Toast.makeText(getApplication(), text, Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public boolean onKeyUp( int keyCode, KeyEvent event ){
		if( keyCode == KeyEvent.KEYCODE_BACK ) {
			finish();
			overridePendingTransition( R.anim.slide_in_right, R.anim.slide_out_right );
			return true;
		}
		return super.onKeyUp( keyCode, event );
	}
}