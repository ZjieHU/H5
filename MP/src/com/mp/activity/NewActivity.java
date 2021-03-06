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

		// 子线程请求
		new Thread() {
			public void run() {

				// 返回String
				response2 = PostUtil.sendPost(CommonUri.MPURL_URL, "ID=" + ID);
				handler.sendEmptyMessage(0x1231);

			}

		}.start();

		// 返回主页
		toMainActivity();

		// 获取所有信息
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

		// 点击提交
		Button btn_ok = (Button) super.findViewById(R.id.btn_new);
		btn_ok.setOnClickListener(new codeOnClickListener());

	}

	// 返回主页
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

	// 点击进入的类
	public class codeOnClickListener implements OnClickListener {

		@SuppressLint("NewApi")
		public void onClick(View v) {
			// TODO 自动生成的方法存根

			// 获取输入值
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

				Toast.makeText(getApplicationContext(), "姓名未填写！",
						Toast.LENGTH_SHORT).show();

			} else {

				params = "ID=" + ID + "&PhoneA=" + Token + "&Name=" + Name
						+ "&Position=" + Position + "&Company=" + Company
						+ "&TEL=" + TEL + "&Email=" + Email + "&Website="
						+ Website + "&Weibo=" + Weibo + "&QQ=" + QQ
						+ "&Weixin=" + Weixin + "&Address=" + Address
						+ "&Abstract=" + Abstract;

				// 子线程请求
				new Thread() {
					public void run() {

						// 返回String
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
	 * 选择提示对话框
	 */
	private void ShowPickDialog() {
		new AlertDialog.Builder(this)
				.setTitle("宣传形象照")
				.setNegativeButton("相册", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						/**
						 * 刚开始，我自己也不知道ACTION_PICK是干嘛的，后来直接看Intent源码，
						 * 可以发现里面很多东西，Intent是个很强大的东西，大家一定仔细阅读下
						 */
						Intent intent = new Intent(Intent.ACTION_PICK, null);

						/**
						 * 下面这句话，与其它方式写是一样的效果，如果：
						 * intent.setData(MediaStore.Images
						 * .Media.EXTERNAL_CONTENT_URI);
						 * intent.setType(""image/*");设置数据类型
						 * 如果朋友们要限制上传到服务器的图片类型时可以直接写如
						 * ："image/jpeg 、 image/png等的类型"
						 * 这个地方小马有个疑问，希望高手解答下：就是这个数据URI与类型为什么要分两种形式来写呀？有什么区别？
						 */
						intent.setDataAndType(
								MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
								"image/*");
						startActivityForResult(intent, 1);

					}
				})
				.setPositiveButton("拍照", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.dismiss();
						/**
						 * 下面这句还是老样子，调用快速拍照功能，至于为什么叫快速拍照，大家可以参考如下官方
						 * 文档，you_sdk_path/docs/guide/topics/media/camera.html
						 * 我刚看的时候因为太长就认真看，其实是错的，这个里面有用的太多了，所以大家不要认为
						 * 官方文档太长了就不看了，其实是错的，这个地方小马也错了，必须改正
						 */
						Intent intent = new Intent(
								MediaStore.ACTION_IMAGE_CAPTURE);
						// 下面这句指定调用相机拍照后的照片存储的路径
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
		// 如果是直接从相册获取
		case 1:
			startPhotoZoom(data.getData());
			break;
		// 如果是调用相机拍照时
		case 2:
			File temp = new File(Environment.getExternalStorageDirectory()
					+ "/xiaoma.jpg");
			startPhotoZoom(Uri.fromFile(temp));
			break;
		// 取得裁剪后的图片
		case 3:
			/**
			 * 非空判断大家一定要验证，如果不验证的话， 在剪裁之后如果发现不满意，要重新裁剪，丢弃
			 * 当前功能时，会报NullException，小马只 在这个地方加下，大家可以根据不同情况在合适的 地方做判断处理类似情况
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
	 * 裁剪图片方法实现
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {
		/*
		 * 至于下面这个Intent的ACTION是怎么知道的，大家可以看下自己路径下的如下网页
		 * yourself_sdk_path/docs/reference/android/content/Intent.html
		 * 直接在里面Ctrl+F搜：CROP ，之前小马没仔细看过，其实安卓系统早已经有自带图片裁剪功能, 是直接调本地库的，小马不懂C C++
		 * 这个不做详细了解去了，有轮子就用轮子，不再研究轮子是怎么 制做的了...吼吼
		 */
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 150);
		intent.putExtra("outputY", 150);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, 3);
	}

	/**
	 * 保存裁剪之后的图片数据
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
				 * 下面注释的方法是将裁剪之后的图片以Base64Coder的字符方式上 传到服务器，QQ头像上传采用的方法跟这个类似
				 */

				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				photo.compress(Bitmap.CompressFormat.JPEG, 60, stream);
				byte[] bytes = stream.toByteArray();
				// 将图片流以字符串形式存储下来

				final String picStr = new String(Base64Coder.encodeLines(bytes));

				new Thread(new Runnable() {
					@Override
					public void run() {
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						// params.add(new BasicNameValuePair("InfoID", InfoID));
						params.add(new BasicNameValuePair("picStr", picStr));
						params.add(new BasicNameValuePair("picName", "dengzi"));

						// 下面不知道用途

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

				// 如果下载到的服务器的数据还是以Base64Coder的形式的话，可以用以下方式转换
				// 为我们可以用的图片类型就OK啦...吼吼
				// Bitmap dBitmap = BitmapFactory.decodeFile(tp);
				// Drawable drawable = new BitmapDrawable(dBitmap);

			}
		} else {
			Toast.makeText(NewActivity.this, "图片不存在", Toast.LENGTH_SHORT)
					.show();
		}
	}

	// 返回结果并跳转
	public void upRespose(String result) {

		if (result.replace(" ", "").trim().equals("success")) {

			Toast.makeText(getApplicationContext(), "名片生成", Toast.LENGTH_SHORT)
					.show();

			// 返回主页
			Intent intent = new Intent();
			intent.setClass(NewActivity.this, MainActivity.class);
			startActivity(intent);
			finish();

		} else {

			Toast.makeText(getApplicationContext(), "生成失败！", Toast.LENGTH_SHORT)
					.show();

		}

	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {

		@SuppressLint("HandlerLeak")
		public void handleMessage(Message msg) {

			if (msg.what == 0x123) {

				InfoID = InfoID.replace(" ", "").trim();

				// 根据InfoID上传头像
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
