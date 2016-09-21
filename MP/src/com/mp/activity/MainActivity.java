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

			// �����¼
			toLoginActivity();

		} else {

			// ������Ƭ
			toNewActivity();

			lv_mp = (ListView) findViewById(R.id.lv_mp);

			dialog = new ProgressDialog(this);
			dialog.setTitle("��ʾ");
			dialog.setMessage("���ڸ��£����Ժ�...");
			
			adapter = new MyAdapter(this);
			// �ҵ���Ƭ
			url = CommonUri.MP_URL + "?PhoneA=" + Token;

			// new MyTask().execute(url);
			Task();

		}

	}

	// �첽����
	public void Task() {

		new MyTask().execute(url);

	}

	// ��¼
	public void toLoginActivity() {

		Intent intent = new Intent();
		intent.setClass(MainActivity.this, LoginActivity.class);
		startActivity(intent);
		finish();

	}

	// �½���Ƭ
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
			// ��ʾ�Ի���
			dialog.show();
		}

		@Override
		protected List<Map<String, Object>> doInBackground(String... params) {
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			try {
				// ��ȡ����JSON��ʽ����
				@SuppressWarnings("resource")
				HttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(params[0]);
				HttpResponse httpResponse = httpClient.execute(httpPost);
				if (httpResponse.getStatusLine().getStatusCode() == 200) {
					String jsonString = EntityUtils.toString(
							httpResponse.getEntity(), "utf-8");
					// ����Json��ʽ���ݣ���ʹ��һ��List<Map>���
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
			// �Ѳ�ѯ�������ݴ��ݸ�������
			adapter.setData(result);
			// ΪListView�趨������
			lv_mp.setAdapter(adapter);
			adapter.notifyDataSetChanged();
			// ���ضԻ���
			dialog.dismiss();
			// item��ת
			lv_mp.setOnItemClickListener(new OnItemClickListenerImpl());
			// ����
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
			Name.setText("������" + list.get(position).get("Name").toString());

			// Position
			TextView Position = (TextView) view.findViewById(R.id.Position);
			Position.setText("ְλ��"
					+ list.get(position).get("Position").toString());

			// Company
			TextView Company = (TextView) view.findViewById(R.id.Company);
			Company.setText("��˾��"
					+ list.get(position).get("Company").toString());

			return view;
		}

	}

	// �����ת����
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
		AlertDialog.Builder builder = new AlertDialog.Builder(this); // �ȵõ�������
		builder.setTitle("����"); // ���ñ���
		builder.setPositiveButton("����", new DialogInterface.OnClickListener() { // ����ȷ����ť
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss(); // �ر�dialog
						// ����
						showShare(URL);
					}
				});
		builder.setNegativeButton("ɾ��", new DialogInterface.OnClickListener() { // ����ȡ����ť
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// ִ�л�ȡ��֤��
						params = "ID=" + ID;

						// ���߳�����
						new Thread() {
							public void run() {

								// ����String
								response = PostUtil.sendPost(CommonUri.DEL_URL,
										params);
								handler.sendEmptyMessage(0x123);

							}

						}.start();

					}
				});

		builder.setNeutralButton("�༭", new DialogInterface.OnClickListener() {// ���ú��԰�ť
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
		// ��������������ˣ���������ʾ����
		builder.create().show();
	}

	private void showShare(String URL) {
		ShareSDK.initSDK(this);
		OnekeyShare oks = new OnekeyShare();
		// �ر�sso��Ȩ
		oks.disableSSOWhenAuthorize();

		// ����ʱNotification��ͼ������� 2.5.9�Ժ�İ汾�����ô˷���
		// oks.setNotification(R.drawable.ic_launcher,
		// getString(R.string.app_name));
		// title���⣬ӡ��ʼǡ����䡢��Ϣ��΢�š���������QQ�ռ�ʹ��
		oks.setTitle("����");
		// titleUrl�Ǳ�����������ӣ�������������QQ�ռ�ʹ��
		oks.setTitleUrl(URL);
		// text�Ƿ����ı�������ƽ̨����Ҫ����ֶ�
		oks.setText("�ף������ҵ���Ƭ" + URL);
		// imagePath��ͼƬ�ı���·����Linked-In�����ƽ̨��֧�ִ˲���
		// oks.setImagePath("/sdcard/test.jpg");//ȷ��SDcard������ڴ���ͼƬ
		// url����΢�ţ��������Ѻ�����Ȧ����ʹ��
		oks.setUrl(URL);
		// comment���Ҷ�������������ۣ�������������QQ�ռ�ʹ��
		oks.setComment("�ף������ҵ���Ƭ" + URL);
		// site�Ƿ�������ݵ���վ���ƣ�����QQ�ռ�ʹ��
		oks.setSite(getString(R.string.app_name));
		// siteUrl�Ƿ�������ݵ���վ��ַ������QQ�ռ�ʹ��
		oks.setSiteUrl(URL);

		// ��������GUI
		oks.show(this);
	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {

		@SuppressLint({ "HandlerLeak", "NewApi" })
		public void handleMessage(Message msg) {

			if (msg.what == 0x123) {

				response = response.replace(" ", "").trim();

				if (response.equals("1")) {

					Toast.makeText(getApplicationContext(), "ɾ���ɹ���",
							Toast.LENGTH_SHORT).show();

					Task();

				} else {

					Toast.makeText(getApplicationContext(), "ɾ��ʧ�ܣ�",
							Toast.LENGTH_SHORT).show();

				}

			}

		}

	};

}
