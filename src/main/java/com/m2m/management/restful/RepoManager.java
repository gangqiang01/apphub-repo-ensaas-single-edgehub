package com.m2m.management.restful;

import com.alibaba.fastjson.JSONObject;
import com.m2m.management.utils.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;

@Slf4j
public class RepoManager {

    @Value("${apprepo.server}")
    private String baseUrl;
    private RestTemplate template;
    public RepoManager(){
        PropertiesUtil propertiesUtil = new PropertiesUtil("application.properties");
        this.baseUrl = propertiesUtil.getProperty("apprepo.server");
        this.template = new RestTemplate();
    }


    public ResponseEntity<String> addUser(String darkname){
        String url = baseUrl + "/adduser?username="+darkname;
        System.out.print(url);
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<JSONObject> request = new HttpEntity<>(headers);
        ResponseEntity<String> responce = template.exchange(url, HttpMethod.GET, request, String.class);
        return responce;
    }

    public ResponseEntity<String> deleteUser(String darkname){
        String url = baseUrl + "/deluser?username="+darkname;
        System.out.print(url);
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<JSONObject> request = new HttpEntity<>(headers);
        ResponseEntity<String> responce = template.exchange(url, HttpMethod.GET, request, String.class);
        return responce;
    }

    public ResponseEntity<String> addFileByReponame(
            File file,
            String darkname,
            String description,
            String license,
            String summary,
            String website,
            String filename){
        System.out.print(baseUrl);
        String url = baseUrl + "/addfilebyname";
        FileSystemResource resource = new FileSystemResource(file);
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();

        param.add("image", resource);
        param.add("filename", filename);
        param.add("username", darkname);
        param.add("description", description);
        param.add("license", license);
        param.add("summary", summary);
        param.add("website", website);
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<MultiValueMap<String,Object>>(param);
        ResponseEntity<String> responce = template.exchange(url, HttpMethod.POST, httpEntity, String.class);
        return responce;
    }

    public ResponseEntity<String> deleteFileByReponame(String darkname, String filename){
        String url = baseUrl + "/delfilebyname?username="+darkname+"&filename="+ filename;
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<JSONObject> request = new HttpEntity<>(headers);
        ResponseEntity<String> responce = template.exchange(url, HttpMethod.GET, request, String.class);
        return responce;
    }
}
