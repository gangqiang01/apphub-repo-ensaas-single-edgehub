package com.m2m.management.utils;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import sun.net.www.protocol.https.HttpsURLConnectionImpl;

import javax.net.ssl.*;
import java.io.*;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

public class HttpUtil {

	private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(HttpUtil.class);

	public final static String GET = "GET";

	public final static String POST = "POST";

	public final static String PATCH = "PATCH";

	public final static String PUT = "PUT";

	private final static int TIMEOUT = 20000;

	public static String doGet(String urlStr, Map<String, String>setHeader) {
		HttpURLConnection httpConn = null;
		HttpsURLConnection httpsConn = null;
		try{

			URL url = new URL(urlStr);

			if(url.getProtocol().toLowerCase().equals("https")){
				httpsConn = (HttpsURLConnection) url.openConnection();
				setHttpsUrlConnection(httpsConn, GET);
				for (Map.Entry<String, String> entry : setHeader.entrySet()) {
					httpsConn.setRequestProperty(entry.getKey(), entry.getValue());
				}
				httpsConn.connect();
				return readResponseContent(httpsConn.getInputStream());

			}else{
				httpConn = (HttpURLConnection) url.openConnection();
				setHttpUrlConnection(httpConn, GET);
				for (Map.Entry<String, String> entry : setHeader.entrySet()) {
					httpConn.setRequestProperty(entry.getKey(), entry.getValue());
				}
				httpConn.connect();
				return readResponseContent(httpConn.getInputStream());
			}

		}catch (Exception e){
			e.printStackTrace();
		}finally{
			if(null!=httpConn) httpConn.disconnect();
			if(null!=httpsConn) httpsConn.disconnect();
		}
		return null;
	}


	public static String doPost(String urlStr, JSONObject auth, Map<String, String>setHeader){

		HttpURLConnection httpConn = null;
		HttpsURLConnection httpsConn = null;
		PrintWriter writer = null;
		try{
			URL url = new URL(urlStr);
			if(url.getProtocol().toLowerCase().equals("https")){
				httpsConn = (HttpsURLConnection) url.openConnection();
				setHttpsUrlConnection(httpsConn, POST);
				for (Map.Entry<String, String> entry : setHeader.entrySet()) {
					httpsConn.setRequestProperty(entry.getKey(), entry.getValue());
				}
				writer = new PrintWriter(httpsConn.getOutputStream());
				writer.print(auth);
				writer.flush();
				httpsConn.connect();
				return readResponseContent(httpsConn.getInputStream());

			}else{
				httpConn = (HttpURLConnection) url.openConnection();
				setHttpUrlConnection(httpConn, POST);
				for (Map.Entry<String, String> entry : setHeader.entrySet()) {
					httpConn.setRequestProperty(entry.getKey(), entry.getValue());
				}
				writer = new PrintWriter(httpConn.getOutputStream());
				writer.print(auth);
				writer.flush();
				httpConn.connect();
				return readResponseContent(httpConn.getInputStream());
			}
		}catch (Exception e){
			e.printStackTrace();
		}finally{
			if(null!=httpConn) httpConn.disconnect();
			if(null!=httpsConn) httpsConn.disconnect();
			if(null!=writer) writer.close();
		}
		return null;
	}

	public static String doPost(String urlStr, JSONObject auth){

		HttpURLConnection httpConn = null;
		HttpsURLConnection httpsConn = null;
		PrintWriter writer = null;
		try{
			URL url = new URL(urlStr);
			if(url.getProtocol().toLowerCase().equals("https")){
				httpsConn = (HttpsURLConnection) url.openConnection();
				setHttpsUrlConnection(httpsConn, POST);
				writer = new PrintWriter(httpsConn.getOutputStream());
				writer.print(auth);
				writer.flush();
				httpsConn.connect();
				return readResponseContent(httpsConn.getInputStream());

			}else{
				httpConn = (HttpURLConnection) url.openConnection();
				setHttpUrlConnection(httpConn, POST);
				writer = new PrintWriter(httpConn.getOutputStream());
				writer.print(auth);
				writer.flush();
				httpConn.connect();
				return readResponseContent(httpConn.getInputStream());
			}
		}catch (Exception e){
			e.printStackTrace();
		}finally{
			if(null!=httpConn) httpConn.disconnect();
			if(null!=httpsConn) httpsConn.disconnect();
			if(null!=writer) writer.close();
		}
		return null;
	}

	public static String doPatch(String urlStr, JSONObject auth, Map<String, String>setHeader){

		HttpURLConnection httpConn = null;
		HttpsURLConnection httpsConn = null;
		PrintWriter writer = null;
		try{
			URL url = new URL(urlStr);
			if(url.getProtocol().toLowerCase().equals("https")){
				httpsConn = (HttpsURLConnection) url.openConnection();
				setHttpsUrlConnection(httpsConn, PATCH);
				for (Map.Entry<String, String> entry : setHeader.entrySet()) {
					httpsConn.setRequestProperty(entry.getKey(), entry.getValue());
				}
				writer = new PrintWriter(httpsConn.getOutputStream());
				writer.print(auth);
				writer.flush();
				httpsConn.connect();
				return readResponseContent(httpsConn.getInputStream());

			}else{
				httpConn = (HttpURLConnection) url.openConnection();
				setHttpUrlConnection(httpConn, PATCH);
				for (Map.Entry<String, String> entry : setHeader.entrySet()) {
					httpConn.setRequestProperty(entry.getKey(), entry.getValue());
				}
				writer = new PrintWriter(httpConn.getOutputStream());
				writer.print(auth);
				writer.flush();
				httpConn.connect();
				return readResponseContent(httpConn.getInputStream());
			}
		}catch (Exception e){
			e.printStackTrace();
		}finally{
			if(null!=httpConn) httpConn.disconnect();
			if(null!=httpsConn) httpsConn.disconnect();
			if(null!=writer) writer.close();
		}
		return null;
	}
// add patch request
	private static void setRequestMethod(final HttpURLConnection c, final String value) {
		try {
			final Object target;
			if (c instanceof HttpsURLConnectionImpl) {
				final Field delegate = HttpsURLConnectionImpl.class.getDeclaredField("delegate");
				delegate.setAccessible(true);
				target = delegate.get(c);
			} else {
				target = c;
			}
			final Field f = HttpURLConnection.class.getDeclaredField("method");
			f.setAccessible(true);
			f.set(target, value);
		} catch (IllegalAccessException | NoSuchFieldException ex) {
			throw new AssertionError(ex);
		}
	}

	private static String readResponseContent(InputStream in) throws IOException{
		Reader reader = null;
		StringBuilder content = new StringBuilder();
		try{
			reader = new InputStreamReader(in);
			char[] buffer = new char[1024];
			int head = 0;
			while( (head=reader.read(buffer))>0 ){
				content.append(new String(buffer, 0, head));
			}
			return content.toString();
		}finally{
			if(null!=in) in.close();
			if(null!=reader) reader.close();
		}
	}




	private static void setHttpUrlConnection(HttpURLConnection conn, String requestMethod) throws ProtocolException {
		setRequestMethod(conn, requestMethod);
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setRequestProperty("Connection", "Keep-Alive");
		conn.setReadTimeout(TIMEOUT);
		conn.setConnectTimeout(TIMEOUT);
		conn.setUseCaches(false);
		conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");

	}
	private static void setHttpsUrlConnection(HttpsURLConnection conn, String requestMethod) throws ProtocolException, NoSuchAlgorithmException, KeyManagementException {
		setRequestMethod(conn, requestMethod);
		SSLContext sc = createSslContext();
		conn.setSSLSocketFactory(sc.getSocketFactory());
		conn.setHostnameVerifier(new HostnameVerifier() {
			@Override
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		});
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setRequestProperty("Connection", "Keep-Alive");
		conn.setReadTimeout(TIMEOUT);
		conn.setConnectTimeout(TIMEOUT);
		conn.setUseCaches(false);
		conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");

	}

// https
	private static SSLContext createSslContext() throws NoSuchAlgorithmException, KeyManagementException {
		SSLContext sc = SSLContext.getInstance("SSL");

		sc.init(null, new TrustManager[]{new X509TrustManager() {
			@Override
			public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

			}

			@Override
			public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

			}

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[0];
			}
		}}, new java.security.SecureRandom());

		return sc;
	}
}
