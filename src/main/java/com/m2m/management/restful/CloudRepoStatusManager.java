package com.m2m.management.restful;

import com.alibaba.fastjson.JSONObject;
import com.m2m.management.constant.UserRole;
import com.m2m.management.entity.User;
import com.m2m.management.service.impl.UserService;
import com.m2m.management.utils.AES;
import com.m2m.management.utils.PropertiesUtil;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.net.URLEncoder;

/**
 * @date ：Created in 9/2/20 11:33 AM
 * @description：restful api  from cloud
 */

public class CloudRepoStatusManager {

    private String baseUrl;
    private RestTemplate  template;
    private final String SRPNAME = "astore";

    private UserService userService;

    public CloudRepoStatusManager(UserService userService){
        this.userService = userService;
    }
    public ResponseEntity<String> doGet (String path){
        this.template = new RestTemplate();
        ResponseEntity<String> responce = null;
        List<User> users = userService.get(UserRole.SYSTEMUSER.ordinal());
        User user = users.get(0);
        this.baseUrl = user.getCloudurl();
        String url = baseUrl + path;
        HttpHeaders headers = new HttpHeaders();
        String srpToken = createSrptoken();
        System.out.println("url="+ url + "##srptoken="+srpToken);
        headers.add("Srptoken", srpToken);
        HttpEntity<JSONObject> request = new HttpEntity<>(headers);
        try{
            responce = template.exchange(url, HttpMethod.GET, request, String.class);
        }catch (HttpClientErrorException e){
            e.printStackTrace();
        }

        return responce;
    }

    public ResponseEntity<String> doPost (String host, String path, JSONObject body){
        this.template = new RestTemplate();
        ResponseEntity<String> responce = null;
        String url = host + path;
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<JSONObject> request = new HttpEntity<>(body, headers);
        try{
            responce = template.exchange(url, HttpMethod.POST, request, String.class);
        }catch (HttpClientErrorException e){
            e.printStackTrace();
        }
        return responce;
    }

    public String createSrptoken(){
        List<User> users = userService.get(UserRole.SYSTEMUSER.ordinal());
        User user = users.get(0);
        this.baseUrl = user.getCloudurl();
        String url = baseUrl + "/configMap/time";
        ResponseEntity<String> responce = null;
        long nowTime = System.currentTimeMillis()/1000;
        try{
            responce = template.exchange(url, HttpMethod.GET, null, String.class);
        }catch (HttpClientErrorException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        System.out.println("responce:"+ responce);
        if(responce != null){
            String body = responce.getBody();
            if(body !=null){
                JSONObject res = JSONObject.parseObject(body);
                if(res.getString("status").equals("success")){
                    nowTime = Long.valueOf(res.getString("data"));
                }
            }

        }

        String enString = String.format("%s#%s", nowTime, SRPNAME);
        String srpToken = null;
        try{
            srpToken = URLEncoder.encode(AES.Encrypt(enString));
            return srpToken;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }



}
