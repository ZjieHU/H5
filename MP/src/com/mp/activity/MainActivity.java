package com.mp.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.mp.app.R;
import com.mp.common.CommonUri;
import com.mp.util.PostUtil;

@SuppressWarnings("deprecation")
public class MainActivity extends Activity {

	private ListView lv_mp;
	private ProgressDialog dialog;
	private MyAdapter adapter;
	String params, response, url;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		SharedPreferences sharedPreferences = getSharedPreferences("mingpian",
				Activity.MODE_PRIVATE);
		String Token = sharedPreferences.getString("PhoneA", "");

		if (Token.isEmpty()) {

			// 跳入登录
			toLoginActivity();

		} else {

			// 新增名片
			toNewActivity();

			lv_mp = (ListView) findViewById(R.id.lv_mp);

			dialog = new ProgressDialog(this);
			dialog.setTitle("提示");
			dialog.setMessage("正在更新，请稍后...");
			
			adapter = new MyAdapter(this);
			// 我的名片
			url = CommonUri.MP_URL + "?PhoneA=" + Token;

			// new MyTask().execute(url);
			Task();

		}

	}

	// 异步任务
	public void Task() {

		new MyTask().execute(url);

	}

	// 登录
	public void toLoginActivity() {

		Intent intent = new Intent();
		intent.setClass(MainActivity.this, LoginActivity.class);
		startActivity(intent);
		finish();

	}

	// 新建名片
	public void toNewActivity() {
		Button btn_order_xin = (Button) findViewById(R.id.btn_new);
		btn_order_xin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, NewActivity.class);
				startActivity(intent);
				finish();
			}
		});
	}

	public class MyTask extends
			AsyncTask<String, Void, List<Map<String, Object>>> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// 显示对话框
			dialog.show();
		}

		@Override
		protected List<Map<String, Object>> doInBackground(String... params) {
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			try {
				// 获取网络JSON格式数据
				@SuppressWarnings("resource")
				HttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(params[0]);
				HttpResponse httpResponse = httpClient.execute(httpPost);
				if (httpResponse.getStatusLine().getStatusCode() == 200) {
					String jsonString = EntityUtils.toString(
							httpResponse.getEntity(), "utf-8");
					// 解析Json格式数据，并使用一个List<Map>存放
					JSONArray jsonArray = new JSONArray(jsonString);
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("ID", jsonObject.get("ID"));
						map.put("URL", jsonObject.get("URL"));
						map.put("Name", jsonObject.get("Name"));
						map.put("Position", jsonObject.get("Position"));
						map.put("Company", jsonObject.get("Company"));

						list.add(map);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return list;
		}

		@Override
		protected void onPostExecute(List<Map<String, Object>> result) {
			super.onPostExecute(result);
			// 把查询到的数据传递给适配器
			adapter.setData(result);
			// 为ListView设定适配器
			lv_mp.setAdapter(adapter);
			adapter.notifyDataSetChanged();
			// 隐藏对话框
			dialog.dismiss();
			// item跳转
			lv_mp.setOnItemClickListener(new OnItemClickListenerImpl());
			// 长按
			lv_mp.setOnItemLongClickListener(new OnItemLongClickListener() {
				@Override
				public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					String ID = (String) ((TextView) arg1
							.findViewById(R.id.ID)).getText();
					
					String URL = (String) ((TextView) arg1
							.findViewById(R.id.URL)).getText();

					dialog(ID,URL);

					return true;
				}
			});

		}
	}

	public class MyAdapter extends BaseAdapter {
		private LayoutInflater layoutInflater;
		private List<Map<String, Object>> list = null;

		public MyAdapter(Context context) {
			layoutInflater = LayoutInflater.from(context);
		}

		public void setData(List<Map<String, Object>> list) {
			this.list = list;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressLint("InflateParams")
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			if (convertView == null) {
				view = layoutInflater.inflate(R.layout.mp_item, null);
			} else {
				view = convertView;
			}

			// ID
			TextView ID = (TextView) view.findViewById(R.id.ID);
			ID.setText(list.get(position).get("ID").toString());
			
			// URL
			TextView URL = (TextView) view.findViewById(R.id.URL);
			URL.setText(list.get(position).get("URL").toString());

			// Name
			TextView Name = (TextView) view.findViewById(R.id.Name);
			Name.setText("姓名：" + list.get(position).get("Name").toString());

			// Position
			TextView Position = (TextView) view.findViewById(R.id.Position);
			Position.setText("职位："
					+ list.get(position).get("Position").toString());

			// Company
			TextView Company = (TextView) view.findViewById(R.id.Company);
			Company.setText("公司："
					+ list.get(position).get("Company").toString());

			return view;
		}

	}

	// 点击跳转详情
	private class OnItemClickListenerImpl implements OnItemClickListener {
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			@SuppressWarnings("unchecked")
			Map<String, String> map = (Map<String, String>) MainActivity.this.adapter
					.getItem(position);

			String URL = map.get("URL");

			Intent it = new Intent();
			it = new Intent(MainActivity.this, MPDActivity.class);
			it.putExtra("URL", URL);
			startActivity(it);
			finish();

		}

	}

	//
	private void dialog(final String ID,final String URL) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this); // 先得到构造器
		builder.setTitle("您想"); // 设置标题
		builder.setPositiveButton("分享", new DialogInterface.OnClickListener() { // 设置确定按钮
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss(); // 关闭dialog
						// 分享
						showShare(URL);
					}
				});
		builder.setNegativeButton("删除", new DialogInterface.OnClickListener() { // 设置取消按钮
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// 执行获取验证码
						params = "ID=" + ID;

						// 子线程请求
						new Thread() {
							public void run() {

								// 返回String
								response = PostUtil.sendPost(CommonUri.DEL_URL,
										params);
								handler.sendEmptyMessage(0x123);

							}

						}.start();

					}
				});

		builder.setNeutralButton("编辑", new DialogInterface.OnClickListener() {// 设置忽略按钮
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();

						Intent it = new Intent();
						it = new Intent(MainActivity.this, NewActivity.class);
						it.putExtra("ID", ID);
						startActivity(it);
						finish();

					}
				});
		// 参数都设置完成了，创建并显示出来
		builder.create().show();
	}

	private void showShare(String URL) {
		ShareSDK.initSDK(this);
		OnekeyShare oks = new OnekeyShare();
		// 关闭sso授权
		oks.disableSSOWhenAuthorize();

		// 分享时Notification的图标和文字 2.5.9以后的版本不调用此方法
		// oks.setNotification(R.drawable.ic_launcher,
		// getString(R.string.app_name));
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle("分享");
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl(URL);
		// text是分享文本，所有平台都需要这个字段
		oks.setText("亲，这是我的名片" + URL);
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		// oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl(URL);
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		oks.setComment("亲，这是我的名片" + URL);
		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite(getString(R.string.app_name));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		oks.setSiteUrl(URL);

		// 启动分享GUI
		oks.show(this);
	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {

		@SuppressLint({ "HandlerLeak", "NewApi" })
		public void handleMessage(Message msg) {

			if (msg.what == 0x123) {

				response = response.replace(" ", "").trim();

				if (response.equals("1")) {

					Toast.makeText(getApplicationContext(), "删除成功！",
							Toast.LENGTH_SHORT).show();

					Task();

				} else {

					Toast.makeText(getApplicationContext(), "删除失败！",
							Toast.LENGTH_SHORT).show();

				}

			}

		}

	};

}
