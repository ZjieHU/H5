package com.mp.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.mp.app.R;
import com.mp.common.CommonUri;
import com.mp.util.NetWorkUtil;
import com.mp.util.PostUtil;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class NewActivity extends Activity {

	private Button chosePic;
	private ImageView imageView;
	private Intent dataIntent = null;

	EditText et_Name, et_Position, et_Company, et_TEL, et_Email, et_Website,
			et_Weibo, et_QQ, et_Weixin, et_Address, et_Abstract;
	String params, InfoID, response2, Token, ID, result;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mp_new);

		init();

		SharedPreferences sharedPreferences = getSharedPreferences("mingpian",
				Activity.MODE_PRIVATE);
		Token = sharedPreferences.getString("PhoneA", "");

		Intent it = super.getIntent();
		ID = it.getStringExtra("ID");

		// ���߳�����
		new Thread() {
			public void run() {

				// ����String
				response2 = PostUtil.sendPost(CommonUri.MPURL_URL, "ID=" + ID);
				handler.sendEmptyMessage(0x1231);

			}

		}.start();

		// ������ҳ
		toMainActivity();

		// ��ȡ������Ϣ
		et_Name = (EditText) super.findViewById(R.id.et_Name);
		et_Position = (EditText) super.findViewById(R.id.et_Position);
		et_Company = (EditText) super.findViewById(R.id.et_Company);
		et_TEL = (EditText) super.findViewById(R.id.et_TEL);
		et_Email = (EditText) super.findViewById(R.id.et_Email);
		et_Website = (EditText) super.findViewById(R.id.et_Website);
		et_Weibo = (EditText) super.findViewById(R.id.et_Weibo);
		et_QQ = (EditText) super.findViewById(R.id.et_QQ);
		et_Weixin = (EditText) super.findViewById(R.id.et_Weixin);
		et_Address = (EditText) super.findViewById(R.id.et_Address);
		et_Abstract = (EditText) super.findViewById(R.id.et_Abstract);

		// ����ύ
		Button btn_ok = (Button) super.findViewById(R.id.btn_new);
		btn_ok.setOnClickListener(new codeOnClickListener());

	}

	// ������ҳ
	public void toMainActivity() {
		Button btn_back = (Button) findViewById(R.id.btn_back);
		btn_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(NewActivity.this, MainActivity.class);
				startActivity(intent);
				finish();
			}
		});
	}

	// ����������
	public class codeOnClickListener implements OnClickListener {

		@SuppressLint("NewApi")
		public void onClick(View v) {
			// TODO �Զ����ɵķ������

			// ��ȡ����ֵ
			String Name = et_Name.getText().toString();
			String Position = et_Position.getText().toString();
			String Company = et_Company.getText().toString();
			String TEL = et_TEL.getText().toString();
			String Email = et_Email.getText().toString();
			String Website = et_Website.getText().toString();
			String Weibo = et_Weibo.getText().toString();
			String QQ = et_QQ.getText().toString();
			String Weixin = et_Weixin.getText().toString();
			String Address = et_Address.getText().toString();
			String Abstract = et_Abstract.getText().toString();

			if (Name.isEmpty()) {

				Toast.makeText(getApplicationContext(), "����δ��д��",
						Toast.LENGTH_SHORT).show();

			} else {

				params = "ID=" + ID + "&PhoneA=" + Token + "&Name=" + Name
						+ "&Position=" + Position + "&Company=" + Company
						+ "&TEL=" + TEL + "&Email=" + Email + "&Website="
						+ Website + "&Weibo=" + Weibo + "&QQ=" + QQ
						+ "&Weixin=" + Weixin + "&Address=" + Address
						+ "&Abstract=" + Abstract;

				// ���߳�����
				new Thread() {
					public void run() {

						// ����String
						InfoID = PostUtil.sendPost(CommonUri.NEW_URL, params);
						handler.sendEmptyMessage(0x123);

					}

				}.start();

			}

		}

	}

	public void setEditText(String response2) throws JSONException {

		String jsonString = response2.replace("[", "").replace(",]", "");

		JSONObject jsonObj = new JSONObject(jsonString);

		et_Name.setText(jsonObj.getString("Name"));
		et_Position.setText(jsonObj.getString("Position"));
		et_Company.setText(jsonObj.getString("Company"));
		et_TEL.setText(jsonObj.getString("TEL"));
		et_Email.setText(jsonObj.getString("Email"));
		et_Website.setText(jsonObj.getString("Website"));
		et_Weibo.setText(jsonObj.getString("Weibo"));
		et_QQ.setText(jsonObj.getString("QQ"));
		et_Weixin.setText(jsonObj.getString("Weixin"));
		et_Address.setText(jsonObj.getString("Address"));
		et_Abstract.setText(jsonObj.getString("Abstract"));

	}

	private void init() {
		chosePic = (Button) findViewById(R.id.chosePic);
		imageView = (ImageView) findViewById(R.id.imageView);
		chosePic.setOnClickListener(new OnPicClickListener());
	}

	public class OnPicClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

			ShowPickDialog();

		}

	}

	/**
	 * ѡ����ʾ�Ի���
	 */
	private void ShowPickDialog() {
		new AlertDialog.Builder(this)
				.setTitle("����������")
				.setNegativeButton("���", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						/**
						 * �տ�ʼ�����Լ�Ҳ��֪��ACTION_PICK�Ǹ���ģ�����ֱ�ӿ�IntentԴ�룬
						 * ���Է�������ܶණ����Intent�Ǹ���ǿ��Ķ��������һ����ϸ�Ķ���
						 */
						Intent intent = new Intent(Intent.ACTION_PICK, null);

						/**
						 * ������仰����������ʽд��һ����Ч���������
						 * intent.setData(MediaStore.Images
						 * .Media.EXTERNAL_CONTENT_URI);
						 * intent.setType(""image/*");������������
						 * ���������Ҫ�����ϴ�����������ͼƬ����ʱ����ֱ��д��
						 * ��"image/jpeg �� image/png�ȵ�����"
						 * ����ط�С���и����ʣ�ϣ�����ֽ���£������������URI������ΪʲôҪ��������ʽ��дѽ����ʲô����
						 */
						intent.setDataAndType(
								MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
								"image/*");
						startActivityForResult(intent, 1);

					}
				})
				.setPositiveButton("����", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.dismiss();
						/**
						 * ������仹�������ӣ����ÿ������չ��ܣ�����Ϊʲô�п������գ���ҿ��Բο����¹ٷ�
						 * �ĵ���you_sdk_path/docs/guide/topics/media/camera.html
						 * �Ҹտ���ʱ����Ϊ̫�������濴����ʵ�Ǵ�ģ�����������õ�̫���ˣ����Դ�Ҳ�Ҫ��Ϊ
						 * �ٷ��ĵ�̫���˾Ͳ����ˣ���ʵ�Ǵ�ģ�����ط�С��Ҳ���ˣ��������
						 */
						Intent intent = new Intent(
								MediaStore.ACTION_IMAGE_CAPTURE);
						// �������ָ������������պ����Ƭ�洢��·��
						intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri
								.fromFile(new File(Environment
										.getExternalStorageDirectory(),
										"xiaoma.jpg")));
						startActivityForResult(intent, 2);
					}
				}).show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		// �����ֱ�Ӵ�����ȡ
		case 1:
			startPhotoZoom(data.getData());
			break;
		// ����ǵ����������ʱ
		case 2:
			File temp = new File(Environment.getExternalStorageDirectory()
					+ "/xiaoma.jpg");
			startPhotoZoom(Uri.fromFile(temp));
			break;
		// ȡ�òü����ͼƬ
		case 3:
			/**
			 * �ǿ��жϴ��һ��Ҫ��֤���������֤�Ļ��� �ڼ���֮��������ֲ����⣬Ҫ���²ü�������
			 * ��ǰ����ʱ���ᱨNullException��С��ֻ ������ط����£���ҿ��Ը��ݲ�ͬ����ں��ʵ� �ط����жϴ����������
			 * 
			 */
			if (data != null) {
				dataIntent = data;
				setPicToView(data);
			}
			break;
		default:
			break;

		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * �ü�ͼƬ����ʵ��
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {
		/*
		 * �����������Intent��ACTION����ô֪���ģ���ҿ��Կ����Լ�·���µ�������ҳ
		 * yourself_sdk_path/docs/reference/android/content/Intent.html
		 * ֱ��������Ctrl+F�ѣ�CROP ��֮ǰС��û��ϸ��������ʵ��׿ϵͳ���Ѿ����Դ�ͼƬ�ü�����, ��ֱ�ӵ����ؿ�ģ�С����C C++
		 * ���������ϸ�˽�ȥ�ˣ������Ӿ������ӣ������о���������ô ��������...���
		 */
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// �������crop=true�������ڿ�����Intent��������ʾ��VIEW�ɲü�
		intent.putExtra("crop", "true");
		// aspectX aspectY �ǿ�ߵı���
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY �ǲü�ͼƬ���
		intent.putExtra("outputX", 150);
		intent.putExtra("outputY", 150);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, 3);
	}

	/**
	 * ����ü�֮���ͼƬ����
	 * 
	 * @param picdata
	 */
	@SuppressWarnings("deprecation")
	private void setPicToView(Intent picdata) {
		Bundle extras = picdata.getExtras();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");
			Drawable drawable = new BitmapDrawable(photo);
			imageView.setBackgroundDrawable(drawable);
		}
	}

	private void uploadPic(final String InfoID) {
		if (dataIntent != null) {
			Bundle extras = dataIntent.getExtras();
			if (extras != null) {
				Bitmap photo = extras.getParcelable("data");
				@SuppressWarnings({ "deprecation", "unused" })
				Drawable drawable = new BitmapDrawable(photo);

				/**
				 * ����ע�͵ķ����ǽ��ü�֮���ͼƬ��Base64Coder���ַ���ʽ�� ������������QQͷ���ϴ����õķ������������
				 */

				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				photo.compress(Bitmap.CompressFormat.JPEG, 60, stream);
				byte[] bytes = stream.toByteArray();
				// ��ͼƬ�����ַ�����ʽ�洢����

				final String picStr = new String(Base64Coder.encodeLines(bytes));

				new Thread(new Runnable() {
					@Override
					public void run() {
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						// params.add(new BasicNameValuePair("InfoID", InfoID));
						params.add(new BasicNameValuePair("picStr", picStr));
						params.add(new BasicNameValuePair("picName", "dengzi"));

						// ���治֪����;

						final String result = NetWorkUtil.httpPost(
								"http://mp.renmaidao.com.cn/ResponseServlet",
								params);
						handler.sendEmptyMessage(0x1232);
						runOnUiThread(new Runnable() {
							public void run() {
								Toast.makeText(NewActivity.this, result,
										Toast.LENGTH_SHORT).show();
							}
						});
						;
					}
				}).start();

				// ������ص��ķ����������ݻ�����Base64Coder����ʽ�Ļ������������·�ʽת��
				// Ϊ���ǿ����õ�ͼƬ���;�OK��...���
				// Bitmap dBitmap = BitmapFactory.decodeFile(tp);
				// Drawable drawable = new BitmapDrawable(dBitmap);

			}
		} else {
			Toast.makeText(NewActivity.this, "ͼƬ������", Toast.LENGTH_SHORT)
					.show();
		}
	}

	// ���ؽ������ת
	public void upRespose(String result) {

		if (result.replace(" ", "").trim().equals("success")) {

			Toast.makeText(getApplicationContext(), "��Ƭ����", Toast.LENGTH_SHORT)
					.show();

			// ������ҳ
			Intent intent = new Intent();
			intent.setClass(NewActivity.this, MainActivity.class);
			startActivity(intent);
			finish();

		} else {

			Toast.makeText(getApplicationContext(), "����ʧ�ܣ�", Toast.LENGTH_SHORT)
					.show();

		}

	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {

		@SuppressLint("HandlerLeak")
		public void handleMessage(Message msg) {

			if (msg.what == 0x123) {

				InfoID = InfoID.replace(" ", "").trim();

				// ����InfoID�ϴ�ͷ��
				uploadPic(InfoID);

			} else if (msg.what == 0x1231) {

				response2 = response2.replace(" ", "").trim();

				try {
					setEditText(response2);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else if (msg.what == 0x1232) {

				upRespose(result);

			}

		}

	};

}
