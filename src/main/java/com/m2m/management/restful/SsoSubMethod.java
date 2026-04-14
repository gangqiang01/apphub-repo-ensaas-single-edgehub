package com.m2m.management.restful;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.m2m.management.ssoException.CannotAcquireDataException;
import com.m2m.management.utils.AES;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

/**
 * @author li.jie
 * updated by avbee 270319
 */


public class SsoSubMethod {

	public String srpId = null;
	public String srpSecret = null;
	public static ArrayNode scopes = null;
	public static String srpName = System.getenv("appName");
//	public static String srpName = "androidlink";

	private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(SsoSubMethod.class);


	public String recvSSOUrl() throws IOException, CannotAcquireDataException {
		String ssoUrl = null;

		try {
			ssoUrl = System.getenv("ssoUrl");

			System.out.println("SSOUrl:"+ssoUrl);
			return ssoUrl;
		} catch (Exception e) {
			throw new CannotAcquireDataException(e.getMessage());
		}
	}


	public String recvSrpToken() throws CannotAcquireDataException {

		try {
			long currentTime = System.currentTimeMillis() / 1000;
			String src = String.valueOf(currentTime) + "-" + srpName;
			System.out.printf("%-32s %s %s\n", "SRP TOKEN SOURCE", ":", src);
			String key = "ssoisno12345678987654321";
			Optional<byte[]> oEncrypt = AES.Encrypt(src, key);
			String base64UrlStr = Base64.getUrlEncoder().encodeToString(oEncrypt.get());
			return base64UrlStr;
		}catch (Exception e) {
			throw new CannotAcquireDataException(e.getMessage());
		}
	}


}
