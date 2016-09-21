package com.mp.activity;

import com.mp.app.R;
import com.mp.common.Check;
import com.mp.common.CommonUri;
import com.mp.common.Get;
import com.mp.util.PostUtil;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {

	EditText et_PhoneA, et_VerCode;
	String params, response;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		// 获取所有信息
		et_PhoneA = (EditText) super.findViewById(R.id.et_PhoneA);
		et_VerCode = (EditText) super.findViewById(R.id.et_VerCode);

		// 点击获取验证码
		Button btn_vercode = (Button) super.findViewById(R.id.btn_vercode);
		btn_vercode.setOnClickListener(new codeOnClickListener());

		// 点击登录
		Button btn_login = (Button) super.findViewById(R.id.btn_login);
		btn_login.setOnClickListener(new loginOnClickListener());

	}

	// 验证码的类
	public class codeOnClickListener implements OnClickListener {

		@SuppressLint("NewApi")
		public void onClick(View v) {
			// TODO 自动生成的方法存根

			// 获取手机号码
			String PhoneA = et_PhoneA.getText().toString();

			// 验证手机号码
			if (PhoneA.isEmpty() || Check.isMobileNumber(PhoneA) == false) {

				Toast.makeText(getApplicationContext(), "手机号码为空或格式不正确",
						Toast.LENGTH_SHORT).show();

			} else {

				// 执行获取验证码
				params = "PhoneA=" + PhoneA;

				// 子线程请求
				new Thread() {
					public void run() {

						// 返回String
						response = PostUtil.sendPost(CommonUri.VER_URL, params);
						handler.sendEmptyMessage(0x123);

					}

				}.start();

			}

		}

	}

	// 登录的类
	public class loginOnClickListener implements OnClickListener {

		@SuppressLint("NewApi")
		public void onClick(View v) {
			// TODO 自动生成的方法存根

			// 获取手机号码
			String PhoneA = et_PhoneA.getText().toString();
			String VerCode = et_VerCode.getText().toString();

			// 验证手机号码
			if (PhoneA.isEmpty() || Check.isMobileNumber(PhoneA) == false) {

				Toast.makeText(getApplicationContext(), "手机号码为空或格式不正确",
						Toast.LENGTH_SHORT).show();

			} else {

				if (VerCode.isEmpty() || Get.getStringLength(VerCode) != 4) {

					Toast.makeText(getApplicationContext(), "验证码为空或不是4位数",
							Toast.LENGTH_SHORT).show();

				} else {

					// 执行获取验证码
					params = "PhoneA=" + PhoneA + "&VerCode=" + VerCode;

					// 子线程请求
					new Thread() {
						public void run() {

							// 返回String
							response = PostUtil.sendPost(CommonUri.LOGIN_URL,
									params);
							handler.sendEmptyMessage(0x1231);

						}

					}.start();

				}

			}

		}
	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {

		@SuppressLint({ "HandlerLeak", "NewApi" })
		public void handleMessage(Message msg) {

			if (msg.what == 0x123) {

				response = response.replace(" ", "").trim();

				if (response.equals("1")) {

					Toast.makeText(getApplicationContext(), "验证码已经发送",
							Toast.LENGTH_SHORT).show();

				} else {

					Toast.makeText(getApplicationContext(), "发送失败，请重新请求",
							Toast.LENGTH_SHORT).show();

				}

			} else if (msg.what == 0x1231) {

				response = response.replace(" ", "").trim();

				if (response.replace(" ", "").trim().isEmpty()) {

					Toast.makeText(getApplicationContext(), "登录失败",
							Toast.LENGTH_SHORT).show();

				} else {

					// 提示
					Toast.makeText(getApplicationContext(), "登录成功",
							Toast.LENGTH_SHORT).show();

					// 写入Share
					SharedPreferences mySharedPreferences = getSharedPreferences(
							"mingpian", Activity.MODE_PRIVATE);
					SharedPreferences.Editor editor = mySharedPreferences
							.edit();
					editor.putString("PhoneA", response);
					editor.commit();

					// 跳转主页
					Intent intent = new Intent();
					intent.setClass(LoginActivity.this, MainActivity.class);
					startActivity(intent);
					finish();

				}

			}

		}

	};

}
