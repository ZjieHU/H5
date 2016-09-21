package com.mp.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class NetWorkUtil {

	public static String httpPost(String url, List<NameValuePair> params) {
		String result = "";

		CloseableHttpClient httpClient = getHttpClient();
		try {
			HttpPost post = new HttpPost(url); // 这里用上本机的某个工程做测试
			UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(params,
					"UTF-8");
			post.setEntity(uefEntity);
			// System.out.println("POST 请求...." + post.getURI());
			// 执行请求
			CloseableHttpResponse httpResponse = httpClient.execute(post);
			try {
				HttpEntity entity = httpResponse.getEntity();
				if (null != entity) {
					// System.out
					// .println("-------------------------------------------------------");
					// System.out.println(EntityUtils.toString(uefEntity));
					// System.out
					// .println("-------------------------------------------------------");
				}
			} finally {
				httpResponse.close();
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				closeHttpClient(httpClient);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// ___________________________________________________________________________

		// HttpPost httpRequest = new HttpPost(url);
		// try {
		// httpRequest.setEntity(new UrlEncodedFormEntity(params));
		//
		// HttpResponse httpResponse = new DefaultHttpClient()
		// .execute(httpRequest);
		// result = EntityUtils.toString(httpResponse.getEntity());
		// } catch (UnsupportedEncodingException e) {
		// result = "URL有误";
		// } catch (ClientProtocolException e) {
		// result = "连接失败";
		// } catch (IOException e) {
		// result = "服务器有误";
		// }

		// _____________________________________________________________________
		// HttpPost httpPost = new HttpPost(url);
		// try {
		// httpPost.setEntity(new UrlEncodedFormEntity(params));
		// } catch (UnsupportedEncodingException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// CloseableHttpResponse response2 = null;
		// try {
		// response2 = httpclient.execute(httpPost);
		// } catch (ClientProtocolException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// try {
		// System.out.println(response2.getStatusLine());
		// HttpEntity entity2 = response2.getEntity();
		// try {
		// EntityUtils.consume(entity2);
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// } finally {
		// try {
		// response2.close();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// }

		return result;
	}

	private static CloseableHttpClient getHttpClient() {
		return HttpClients.createDefault();
	}

	private static void closeHttpClient(CloseableHttpClient client)
			throws IOException {
		if (client != null) {
			client.close();
		}
	}

}
