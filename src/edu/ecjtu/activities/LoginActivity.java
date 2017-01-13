package edu.ecjtu.activities;

import java.util.List;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import edu.ecjtu.domain.Util.MediaUtils;
import edu.ecjtu.domain.database.MusicProvider;
import edu.ecjtu.domain.vo.objects.User;
import edu.ecjtu.musicplayer.R;

public class LoginActivity extends Activity implements OnClickListener {

	private LinearLayout rl_login_back;
	private Button btn_login;
	private EditText et_login_username, et_login_password;
	private TextView tv_login_username_error;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_layout);

		initViews();
		registerListeners();
	}

	private void initViews() {
		rl_login_back = (LinearLayout) findViewById(R.id.rl_login_back);
		btn_login = (Button) findViewById(R.id.btn_login);
		et_login_username = (EditText) findViewById(R.id.et_login_username);
		tv_login_username_error = (TextView) findViewById(R.id.tv_login_username_error);
		et_login_password = (EditText) findViewById(R.id.et_login_password);
	}

	private void registerListeners() {
		rl_login_back.setOnClickListener(this);
		btn_login.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_login_back:
			finish();
			break;
		case R.id.btn_login:
			logIn();
			break;
		default:
			break;
		}
	}

	private void logIn() {
		String username = et_login_username.getText().toString();
		String password = et_login_password.getText().toString();

		List<User> users = MediaUtils.getList(User.class, this,
				MusicProvider.USER_URI,
				new String[] { "Username", "Password" },
				MusicProvider.TABLE_USER_USERNAME + "=?",
				new String[] { username }, null);
		if (users.size() < 1) {
			tv_login_username_error.setText("用户名不存在!");
			return;
		} else if (!users.get(0).getPassword().equals(password)) {
			tv_login_username_error.setText("用户名和密码不匹配!");
			return;
		}
		ContentValues values = new ContentValues();
		values.put(MusicProvider.TABLE_USER_ISLOGIN, "0");
		getContentResolver().update(MusicProvider.USER_URI, values, null, null);
		values.clear();
		values.put(MusicProvider.TABLE_USER_USERNAME, username);
		values.put(MusicProvider.TABLE_USER_ISLOGIN, 1);
		int count = getContentResolver().update(MusicProvider.USER_URI, values,
				MusicProvider.TABLE_USER_USERNAME + "=?",
				new String[] { username });
		values.clear();
		if (count > 0) {
			String message;
			if (getIntent().getStringExtra(MusicProvider.TABLE_USER_USERNAME) == null) {
				message = "登录成功";
			} else {
				message = "账号切换成功";
			}
			Toast.makeText(this, message, 0).show();
			LoginOrRegisterActivity.isLogIn = true;
			finish();
		}
	}
}
