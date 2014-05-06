package com.lockscreensecurity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class MoreInfo extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

		setContentView(R.layout.activity_more_info);

		Button b = (Button) findViewById(R.id.back_button);
		b.setOnClickListener(this);
		b = (Button) findViewById(R.id.email_button);
		b.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.back_button) {
			this.finish();
		} else if (id ==  R.id.email_button) {
			Intent i = new Intent(Intent.ACTION_SEND);
			i.setType("message/rfc822");
			i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"pm1122@my.bristol.ac.uk"});
			i.putExtra(Intent.EXTRA_SUBJECT, "Lockscreen Security Survey");
			i.putExtra(Intent.EXTRA_TEXT   , "body of email");
			try {
			    startActivity(Intent.createChooser(i, "Send mail..."));
			} catch (android.content.ActivityNotFoundException ex) {
			    Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
