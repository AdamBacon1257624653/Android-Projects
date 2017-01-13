package edu.ecjtu.activities;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import edu.ecjtu.domain.database.MusicProvider;
import edu.ecjtu.musicplayer.R;

public class LoginOrRegisterActivity extends Activity implements
		OnClickListener {

	private LinearLayout rl_loginregister_back;
	private Button btn_login, btn_register, btn_exit;
	public static boolean isLogIn = false;
	private String username;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loginregister_layout);

		initViews();
		inflateViews();
		registerListener();
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (isLogIn) {
			isLogIn = false;
			finish();
		}
	}

	private void initViews() {
		rl_loginregister_back = (LinearLayout) findViewById(R.id.rl_loginregister_back);
		btn_login = (Button) findViewById(R.id.btn_login);
		btn_register = (Button) findViewById(R.id.btn_register);
		btn_exit = (Button) findViewById(R.id.btn_exit);
	}

	private void inflateViews() {
		username = getIntent()
				.getStringExtra(MusicProvider.TABLE_USER_USERNAME);
		if (username != null) {
			btn_login.setText("ÇÐ»»ÕËºÅ");
			btn_exit.setVisibility(View.VISIBLE);
		}
	}

	private void registerListener() {
		this.rl_loginregister_back.setOnClickListener(this);
		this.btn_login.setOnClickListener(this);
		this.btn_register.setOnClickListener(this);
		this.btn_exit.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.rl_loginregister_back:
			setResult(RESULT_OK);
			this.finish();
			break;
		case R.id.btn_login:
			Intent loginIntent = new Intent(this, LoginActivity.class);
			if (username != null) {
				loginIntent.putExtra(MusicProvider.TABLE_USER_USERNAME,
						username);
			}
			startActivity(loginIntent);
			break;
		case R.id.btn_register:
			Intent registerIntent = new Intent(this, RegisterAcitivity.class);
			startActivity(registerIntent);
			break;
		case R.id.btn_exit:
			ContentValues values = new ContentValues();
			values.put(MusicProvider.TABLE_USER_ISLOGIN, "0");
			getContentResolver().update(MusicProvider.USER_URI, values, null,
					null);
			values.clear();
			Toast.makeText(this, "ÍË³ö³É¹¦", 0).show();
			finish();
			break;
		}
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_OK);
		super.onBackPressed();
	}
}
