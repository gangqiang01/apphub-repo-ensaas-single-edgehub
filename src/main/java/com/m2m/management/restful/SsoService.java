package com.m2m.management.restful;

/**
*
* @author
* updated by avbee 270319
*/

import com.alibaba.fastjson.JSONObject;
import com.m2m.management.utils.HttpUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SsoService {

//	private String SSO_API_ENDPOINT = "https://portal-sso.arfa.wise-paas.com/v2.0";
	private SsoSubMethod ssoSubMethod = new SsoSubMethod();
	private String SSO_API_ENDPOINT ;
	//register apphub
	private String appName;
	private String serviceName;
	private String workspace;
	private String namespace;
	private String appID;
	private String  datacenter;
	private String cluster;
	private String platform;
	//license apphub name
	private final String APPNAME = "AppHub";

	public static  String getInstanceId(){
		return String.format("%s%s%s",System.getenv("cluster"), System.getenv("workspace"), System.getenv("namespace"));
	}
	public SsoService(){
		try{
			this.SSO_API_ENDPOINT = ssoSubMethod.recvSSOUrl();
			this.appID = System.getenv("appID");
			this.appName = System.getenv("appName");
			this.serviceName = System.getenv("serviceName");
			this.workspace = System.getenv("workspace");
			this.namespace = System.getenv("namespace");
			this.datacenter = System.getenv("datacenter");
			this.cluster = System.getenv("cluster");
            this.platform = System.getenv("platform");
			System.out.println("ssoURL:"+this.SSO_API_ENDPOINT);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public boolean isEnsaasPlatform(){
	    if(this.platform != null&& !this.platform.equals("ensaas"))
            return false;
	    else
            return true;
    }
//	public static String srpName = "androidlink";

	private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(SsoService.class);


	public String validateToken(JSONObject json) throws Exception {

		String apiUrl = String.format("%s%s", this.SSO_API_ENDPOINT, "/tokenvalidation");
		return HttpUtil.doPost(apiUrl, json);
	}

	public String getToken(JSONObject auth) {
		String apiUrl = String.format("%s%s", this.SSO_API_ENDPOINT, "/auth/native");
//		LOG.info("apiUrl:" + apiUrl);
//		LOG.info("auth:" + auth.toString());
		return HttpUtil.doPost(apiUrl, auth);
	}

	public String refreshToken(JSONObject tokenPayload) throws Exception {
//		LOG.info("initiate refreshToken");
		String apiUrl = String.format("%s%s", this.SSO_API_ENDPOINT, "/token");
		return HttpUtil.doPost(apiUrl, tokenPayload);
	}

	public String getTokenUser(String accessToken) {
		String apiUrl = String.format("%s%s", this.SSO_API_ENDPOINT, "/users/me");
		String Token = String.format("Bearer %s", accessToken);
		Map<String, String> setHeader = new HashMap<String, String>();
		setHeader.put("Authorization", Token);
		return HttpUtil.doGet(apiUrl, setHeader);
	}

	public String getSRPToken(String srpToken, String endPointUrl) throws Exception {
		String apiUrl = String.format("%s%s/%s", this.SSO_API_ENDPOINT, "/srps", endPointUrl);
		Map<String, String> setHeader = new HashMap<String, String>();
		setHeader.put("X-Auth-SRPToken", srpToken);
		return HttpUtil.doGet(apiUrl, setHeader);
	}

	public String createUser(String workspaceOwner, JSONObject conJson) {

		try {
			final String srpToken = ssoSubMethod.recvSrpToken();
			String srpInfo = getClientInfo();
			JSONObject srpInfoJson = JSONObject.parseObject(srpInfo);
			String clientID = srpInfoJson.getString("clientId");
			String clientSecret = srpInfoJson.getString("clientSecret");
			String urlPatch = this.SSO_API_ENDPOINT + "/clients/" + clientID + "/users";
//			System.out.println("usr:"+urlPatch);
			JSONObject body = new JSONObject();
			body.put("workspaceOwner", workspaceOwner);
			body.put("clientSecret", clientSecret);
			List<String> list = new ArrayList<String>();
			list.add("repo");
			body.put("scopes", list);
			body.put("user", conJson);
			System.out.println("usr:"+urlPatch+"body:"+body.toString()+"srpToken:"+srpToken);
			Map<String, String> setHeader = new HashMap<String, String>();
			setHeader.put("X-Auth-SRPToken", srpToken);
			return HttpUtil.doPost(urlPatch, body, setHeader);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
	}

	public String updateUserScope(String username, String accessToken){

		try {
			final String srpToken = ssoSubMethod.recvSrpToken();
			String srpInfo = getClientInfo();
			JSONObject srpInfoJson = JSONObject.parseObject(srpInfo);
			String clientID = srpInfoJson.getString("clientId");
			String clientSecret = srpInfoJson.getString("clientSecret");
			String urlPatch = this.SSO_API_ENDPOINT + "/users/" + username + "/scopes";
//			System.out.println("usr:"+urlPatch);
			JSONObject body = new JSONObject();
			body.put("clientId", clientID);
			body.put("clientSecret", clientSecret);
			body.put("action", "append");
			List<String> list = new ArrayList<String>();
			list.add("repo");
			body.put("scopes", list);

			System.out.println("usr:"+urlPatch+"body:"+body.toString()+"srpToken:"+srpToken);
			String Token = String.format("Bearer %s", accessToken);
			Map<String, String> setHeader = new HashMap<String, String>();
			setHeader.put("Authorization", Token);
			return HttpUtil.doPatch(urlPatch, body, setHeader);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
	}

	public String reSendEmail(String accessToken, String email){
		String urlPatch = this.SSO_API_ENDPOINT+"/users/"+ email+"/regemail";
		String Token = String.format("Bearer %s", accessToken);
		Map<String, String> setHeader = new HashMap<String, String>();
//		setHeader.put("Authorization", Token);
		return HttpUtil.doGet(urlPatch, setHeader);

	}

	public String forgetPassword(String accessToken, String email){
		String urlPatch = this.SSO_API_ENDPOINT+"/users/"+ email+"/pwdresetemail";
		String Token = String.format("Bearer %s", accessToken);
		Map<String, String> setHeader = new HashMap<String, String>();
//		setHeader.put("Authorization", Token);
		return HttpUtil.doGet(urlPatch, setHeader);
	}

	public String resetPassword(String accessToken, String email, String activationCode, String new_password, String mode){
		String urlPatch = this.SSO_API_ENDPOINT+"/users/"+ email+"/password?mode="+mode+"&activationCode="+activationCode;
		JSONObject body = new JSONObject();
		body.put("new_password", new_password);
		String Token = String.format("Bearer %s", accessToken);
		Map<String, String> setHeader = new HashMap<String, String>();
//		setHeader.put("Authorization", Token);
		return HttpUtil.doPatch(urlPatch, body, setHeader);
	}

	public String changePassword(String accessToken, JSONObject conJson){
		String urlPatch = this.SSO_API_ENDPOINT + "/users/" + conJson.getString("username") + "/password";
		String Token = String.format("Bearer %s", accessToken);

		Map<String, String> setHeader = new HashMap<String, String>();
		setHeader.put("Authorization", Token);
		return HttpUtil.doPatch(urlPatch, conJson, setHeader);
	}

    public String createClient() throws  Exception{
		final String srpToken = ssoSubMethod.recvSrpToken();
		String urlPatch = ssoSubMethod.recvSSOUrl() + "/clients";
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("appName",appName);
		jsonObject.put("appId", appID);
		jsonObject.put("workspace", workspace);
		jsonObject.put("namespace", namespace);
		jsonObject.put("datacenter", datacenter);
		jsonObject.put("serviceName", serviceName);
		jsonObject.put("cluster", cluster);
		List<String> scope = new ArrayList<>();
		scope.add("repo");
		jsonObject.put("scopes", scope);
		System.out.println("body:"+jsonObject.toString()+"srpToken:"+srpToken);
		Map<String, String> setHeader = new HashMap<String, String>();
		setHeader.put("X-Auth-SRPToken", srpToken);
		return HttpUtil.doPost(urlPatch, jsonObject, setHeader);
	}
//check client is already exist
	public String getClientInfo() throws  Exception{
		final String srpToken = ssoSubMethod.recvSrpToken();
		String urlPatch = ssoSubMethod.recvSSOUrl() + "/clients/"+appName+"?namespace="+namespace+
				"&workspace="+workspace+"&cluster="+cluster+"&datacenter="+datacenter+"&serviceName="+serviceName+"&appId="+appID;
		System.out.println("urlPatch:"+urlPatch+"srpToken:"+srpToken);
		Map<String, String> setHeader = new HashMap<String, String>();
		setHeader.put("X-Auth-SRPToken", srpToken);
		return HttpUtil.doGet(urlPatch, setHeader);
	}

    public boolean checkClientInfo(){
        try{
            final String srpToken = ssoSubMethod.recvSrpToken();
            String urlPatch = ssoSubMethod.recvSSOUrl() + "/clients/"+appName+"?namespace="+namespace+
                    "&workspace="+workspace+"&cluster="+cluster+"&datacenter="+datacenter+"&serviceName="+serviceName+"&appId="+appID;
            System.out.println("urlPatch:"+urlPatch+"srpToken:"+srpToken);
            Map<String, String> setHeader = new HashMap<String, String>();
            setHeader.put("X-Auth-SRPToken", srpToken);

            String result =  HttpUtil.doGet(urlPatch, setHeader);
            JSONObject resJson = JSONObject.parseObject(result);
            if(resJson == null){
                return false;
            }
            if(resJson.containsKey("error")&& resJson.getString("message").equals("Not Found")){
                return false;
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
	public String validateUser(String clientId, String accessToken){
		try{
			String urlPatch = String.format("%s/users/srpRole?clientId=%s", this.SSO_API_ENDPOINT, clientId);
			String Token = String.format("Bearer %s", accessToken);
			Map<String, String> setHeader = new HashMap<String, String>();
			setHeader.put("Authorization", Token);
			return HttpUtil.doGet(urlPatch, setHeader);
		}catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}

	//license serviceInstanceId
	public String getServiceInstanceId(){
		return String.format("%s%s%s",cluster, workspace, namespace);
	}

	public String getLicenseInfo(String pn){
		String serviceInstanceId = String.format("%s%s%s",cluster, workspace, namespace);
		try {
			String baseurl = System.getenv("apiLicense");
			if(baseurl == null){
				baseurl = "http://api.license.ensaas.en.internal/v1";
			}
			String urlPatch = baseurl + "/api/partNum/licenseQty?pn="+ pn+"&id="+ serviceInstanceId;
			System.out.println("licenseInfourl="+urlPatch);
			Map<String, String> setHeader = new HashMap<String, String>();
			return HttpUtil.doGet(urlPatch, setHeader);
		}catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}

	//verify license
	public String getLicenseInfoByAppName(){
		String serviceInstanceId = String.format("%s%s%s",cluster, workspace, namespace);
		try {
			String baseurl = System.getenv("apiLicense");
			if(baseurl == null){
				baseurl = "http://api.license.ensaas.en.internal/v1";
			}
			String urlPatch = baseurl + "/api/serviceName/"+ APPNAME+"/serviceInstanceId/"+ serviceInstanceId;
			System.out.println("licenseInfourl="+urlPatch);
			Map<String, String> setHeader = new HashMap<String, String>();
			return HttpUtil.doGet(urlPatch, setHeader);
		}catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}

	public String validateUser(String clientId, String accessToken, String subscriptionId){
		try{
			String urlPatch = "";
			if(!StringUtils.isBlank(subscriptionId)){
				urlPatch = String.format("%s/users/srpRole?clientId=%s&subscriptionId=%s", this.SSO_API_ENDPOINT, clientId, subscriptionId);
			}else{
				urlPatch = String.format("%s/users/srpRole?clientId=%s", this.SSO_API_ENDPOINT, clientId);
			}

			String Token = String.format("Bearer %s", accessToken);
			Map<String, String> setHeader = new HashMap<String, String>();
			setHeader.put("Authorization", Token);
			System.out.println("validateUserUrl:"+ urlPatch);
			return HttpUtil.doGet(urlPatch, setHeader);
		}catch (Exception e){
			e.printStackTrace();
			return null;
		}

	}

	public String removeUserScope(String username, String accessToken){

		try {
			final String srpToken = ssoSubMethod.recvSrpToken();
			String srpInfo = getClientInfo();
			JSONObject srpInfoJson = JSONObject.parseObject(srpInfo);
			String clientID = srpInfoJson.getString("clientId");
			String clientSecret = srpInfoJson.getString("clientSecret");
			String urlPatch = this.SSO_API_ENDPOINT + "/users/" + username + "/scopes";
//			System.out.println("usr:"+urlPatch);
			JSONObject body = new JSONObject();
			body.put("clientId", clientID);
			body.put("clientSecret", clientSecret);
			body.put("action", "remove");
			List<String> list = new ArrayList<String>();
			list.add("repo");
			body.put("scopes", list);

			System.out.println("usr:"+urlPatch+"body:"+body.toString()+"srpToken:"+srpToken);
			String Token = String.format("Bearer %s", accessToken);
			Map<String, String> setHeader = new HashMap<String, String>();
			setHeader.put("Authorization", Token);
			return HttpUtil.doPatch(urlPatch, body, setHeader);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
	}


}