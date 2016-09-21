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

		// WebView显示新闻页面
		webview = (WebView) findViewById(R.id.webview);
		// 设置WebView属性，能够执行Javascript脚本
		webview.getSettings().setJavaScriptEnabled(true);
		// 加载需要显示的网页
		webview.loadUrl(URL);
		// 设置Web视图
		webview.setWebViewClient(new HelloWebViewClient());

		// 返回主页
		toMainActivity();

		// 分享
		toShare(URL);

	}

	// 返回主页
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

	// Web视图
	private class HelloWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	}

	// 分享
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

}
