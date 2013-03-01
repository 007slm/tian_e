package com.orange.browser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;


import android.util.Log;

public class NetworkConnector{
	
	private static HttpParams my_httpParams = null;
	private static String NET_ERROR="neterror";
	
	static{
		//set http connnect timeout
		int connection_Timeout = 60000;
		my_httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(my_httpParams, connection_Timeout);
		HttpConnectionParams.setSoTimeout(my_httpParams, connection_Timeout);
	}
	public static String doPost(String url,Map<String,String> variables){
		DefaultHttpClient httpclient = new DefaultHttpClient(my_httpParams);  

		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
		HttpPost httppost = new HttpPost(url);

		InputStream content = null;
		String returnConnection = null;
		
		if (variables != null) {
			Set<String> keys = variables.keySet();
			for (Iterator<String> i = keys.iterator(); i.hasNext();) {
				String key = (String) i.next();
				String value = (String) variables.get(key);
				if(key == null || key.trim().length()==0 || value == null || value.trim().length()==0){
					continue;
				}
				pairs.add(new BasicNameValuePair(key, value));
			}
		}
		int responseCode=0;
		try {
			UrlEncodedFormEntity p_entity = new UrlEncodedFormEntity(pairs,	"utf-8");
			
			httppost.addHeader("Accept-Encoding", "gzip");
			
			httppost.setEntity(p_entity);
			
			HttpResponse response = httpclient.execute(httppost);
			responseCode=response.getStatusLine().getStatusCode();
			HttpEntity entity = response.getEntity();
			content = entity.getContent();
			
			Header gzipHeader = response.getFirstHeader("Content-Encoding");
			if(gzipHeader!=null && gzipHeader.getValue().equalsIgnoreCase("gzip"))
			{
				Log.v("network-gziip", "gzipgzipgzip");
				content = new GZIPInputStream(content);
				returnConnection = convertStreamToString(content);
			}
			else
			{
				returnConnection = convertStreamToString(content);
			}
			
		} catch (Exception uee) {
//			Log.i("test","net---> responseCode:"+responseCode);
//			Log.i("test","Exception : "+uee.toString());
//			Log.v("network-error", "connect timeout for url:"+url);
			uee.printStackTrace();
			if(responseCode!=200){
				try {
					if(content!=null)
					{
						content.close();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				httpclient.getConnectionManager().shutdown();
				return NET_ERROR;
			}
//			DataUtil.localTimeout.set(true);
		} 
		try {
			if(content!=null)
			{
				content.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		httpclient.getConnectionManager().shutdown();
		return returnConnection;
	}

	public static String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
}
