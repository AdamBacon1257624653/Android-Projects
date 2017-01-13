package edu.ecjtu.activities;

import java.util.List;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import edu.ecjtu.domain.Util.MediaUtils;
import edu.ecjtu.domain.database.MusicProvider;
import edu.ecjtu.domain.vo.objects.User;
import edu.ecjtu.musicplayer.R;

public class RegisterAcitivity extends Activity implements OnClickListener,
		OnFocusChangeListener {

	private LinearLayout rl_register_back;
	private EditText et_register_username, et_register_password,
			et_register_confirm_password;
	private TextView tv_register_username_error, tv_register_password_error;
	private Button btn_register;
	private String username = "";
	private String password = "";
	private String confirmpassword = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_layout);

		initViews();
		registerListeners();
	}

	private void initViews() {
		rl_register_back = (LinearLayout) findViewById(R.id.rl_register_back);
		et_register_username = (EditText) findViewById(R.id.et_register_username);
		et_register_password = (EditText) findViewById(R.id.et_register_password);
		et_register_confirm_password = (EditText) findViewById(R.id.et_register_confirm_password);
		tv_register_password_error = (TextView) findViewById(R.id.tv_register_password_error);
		tv_register_username_error = (TextView) findViewById(R.id.tv_register_username_error);
		btn_register = (Button) findViewById(R.id.btn_register);
	}

	private void registerListeners() {
		rl_register_back.setOnClickListener(this);
		et_register_username.setOnFocusChangeListener(this);
		et_register_password.setOnFocusChangeListener(this);
		et_register_confirm_password.setOnFocusChangeListener(this);
		btn_register.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_register_back:
			finish();
			break;
		case R.id.btn_register:
			if (tv_register_username_error.getText().toString().trim()
					.isEmpty()
					&& tv_register_password_error.getText().toString()
							.isEmpty()) {
				ContentValues values = new ContentValues();
				values.put(MusicProvider.TABLE_USER_USERNAME, username);
				values.put(MusicProvider.TABLE_USER_PASSWORD, password);
				values.put(MusicProvider.TABLE_USER_ISLOGIN, "0");
				getContentResolver().insert(MusicProvider.USER_URI, values);
				Toast.makeText(this, "注册成功", 0).show();
				finish();
			} else {
				matchPassword();
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		switch (v.getId()) {
		case R.id.et_register_username:
			if (!hasFocus) {
				username = et_register_username.getText().toString();
				if (username.trim().isEmpty()) {
					tv_register_username_error.setText("用户名不能为空");
					return;
				} else {
					tv_register_username_error.setText("");
				}
				List<User> users = MediaUtils.getList(User.class, this,
						MusicProvider.USER_URI, new String[] { "Username",
								"Password" }, MusicProvider.TABLE_USER_USERNAME
								+ "=?", new String[] { username }, null);
				if (users.size() > 0) {
					tv_register_username_error.setText("用户名已存在");
				}
			}
			break;
		case R.id.et_register_password:
			if (!hasFocus) {
				password = et_register_password.getText().toString();
				if (password.trim().isEmpty()) {
					tv_register_password_error.setText("密码不能为空");
				} else {
					if (password.length() < 3 || password.length() > 10) {
						tv_register_password_error.setText("密码长度3~10");
						return;
					}
					tv_register_password_error.setText("");
				}
			}
			break;
		case R.id.et_register_confirm_password:
			if (!hasFocus) {
				matchPassword();
			}
			break;
		}
	}

	/**
	 * 匹配密码
	 */
	private void matchPassword() {
		confirmpassword = et_register_confirm_password.getText().toString();
		if (!confirmpassword.trim().equals(password.trim())) {
			tv_register_password_error.setText("密码不匹配");
		} else {
			tv_register_password_error.setText("");
		}
	}
}
