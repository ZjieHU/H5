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

		// ��ȡ������Ϣ
		et_PhoneA = (EditText) super.findViewById(R.id.et_PhoneA);
		et_VerCode = (EditText) super.findViewById(R.id.et_VerCode);

		// �����ȡ��֤��
		Button btn_vercode = (Button) super.findViewById(R.id.btn_vercode);
		btn_vercode.setOnClickListener(new codeOnClickListener());

		// �����¼
		Button btn_login = (Button) super.findViewById(R.id.btn_login);
		btn_login.setOnClickListener(new loginOnClickListener());

	}

	// ��֤�����
	public class codeOnClickListener implements OnClickListener {

		@SuppressLint("NewApi")
		public void onClick(View v) {
			// TODO �Զ����ɵķ������

			// ��ȡ�ֻ�����
			String PhoneA = et_PhoneA.getText().toString();

			// ��֤�ֻ�����
			if (PhoneA.isEmpty() || Check.isMobileNumber(PhoneA) == false) {

				Toast.makeText(getApplicationContext(), "�ֻ�����Ϊ�ջ��ʽ����ȷ",
						Toast.LENGTH_SHORT).show();

			} else {

				// ִ�л�ȡ��֤��
				params = "PhoneA=" + PhoneA;

				// ���߳�����
				new Thread() {
					public void run() {

						// ����String
						response = PostUtil.sendPost(CommonUri.VER_URL, params);
						handler.sendEmptyMessage(0x123);

					}

				}.start();

			}

		}

	}

	// ��¼����
	public class loginOnClickListener implements OnClickListener {

		@SuppressLint("NewApi")
		public void onClick(View v) {
			// TODO �Զ����ɵķ������

			// ��ȡ�ֻ�����
			String PhoneA = et_PhoneA.getText().toString();
			String VerCode = et_VerCode.getText().toString();

			// ��֤�ֻ�����
			if (PhoneA.isEmpty() || Check.isMobileNumber(PhoneA) == false) {

				Toast.makeText(getApplicationContext(), "�ֻ�����Ϊ�ջ��ʽ����ȷ",
						Toast.LENGTH_SHORT).show();

			} else {

				if (VerCode.isEmpty() || Get.getStringLength(VerCode) != 4) {

					Toast.makeText(getApplicationContext(), "��֤��Ϊ�ջ���4λ��",
							Toast.LENGTH_SHORT).show();

				} else {

					// ִ�л�ȡ��֤��
					params = "PhoneA=" + PhoneA + "&VerCode=" + VerCode;

					// ���߳�����
					new Thread() {
						public void run() {

							// ����String
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

					Toast.makeText(getApplicationContext(), "��֤���Ѿ�����",
							Toast.LENGTH_SHORT).show();

				} else {

					Toast.makeText(getApplicationContext(), "����ʧ�ܣ�����������",
							Toast.LENGTH_SHORT).show();

				}

			} else if (msg.what == 0x1231) {

				response = response.replace(" ", "").trim();

				if (response.replace(" ", "").trim().isEmpty()) {

					Toast.makeText(getApplicationContext(), "��¼ʧ��",
							Toast.LENGTH_SHORT).show();

				} else {

					// ��ʾ
					Toast.makeText(getApplicationContext(), "��¼�ɹ�",
							Toast.LENGTH_SHORT).show();

					// д��Share
					SharedPreferences mySharedPreferences = getSharedPreferences(
							"mingpian", Activity.MODE_PRIVATE);
					SharedPreferences.Editor editor = mySharedPreferences
							.edit();
					editor.putString("PhoneA", response);
					editor.commit();

					// ��ת��ҳ
					Intent intent = new Intent();
					intent.setClass(LoginActivity.this, MainActivity.class);
					startActivity(intent);
					finish();

				}

			}

		}

	};

}
