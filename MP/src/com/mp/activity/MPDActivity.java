package com.mp.activity;

import com.mp.app.R;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class MPDActivity<UMSocialService> extends Activity {

	String URL;
	private WebView webview;
	String params, response;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mpd);

		Intent it = super.getIntent();
		URL = it.getStringExtra("URL");

		// WebView��ʾ����ҳ��
		webview = (WebView) findViewById(R.id.webview);
		// ����WebView���ԣ��ܹ�ִ��Javascript�ű�
		webview.getSettings().setJavaScriptEnabled(true);
		// ������Ҫ��ʾ����ҳ
		webview.loadUrl(URL);
		// ����Web��ͼ
		webview.setWebViewClient(new HelloWebViewClient());

		// ������ҳ
		toMainActivity();

		// ����
		toShare(URL);

	}

	// ������ҳ
	public void toMainActivity() {
		Button btn_back = (Button) findViewById(R.id.btn_back);
		btn_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MPDActivity.this, MainActivity.class);
				startActivity(intent);
				finish();
			}
		});
	}

	// Web��ͼ
	private class HelloWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	}

	// ����
	public void toShare(final String URL) {
		Button btn_share = (Button) findViewById(R.id.btn_share);
		btn_share.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				showShare(URL);

			}
		});
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

}
