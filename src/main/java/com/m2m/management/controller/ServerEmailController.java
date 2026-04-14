package com.m2m.management.controller;

import com.alibaba.fastjson.JSONObject;
import com.m2m.management.entity.ServerEmail;
import com.m2m.management.former.Response;
import com.m2m.management.service.IServerEmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.mail.Session;
import javax.mail.Transport;
import java.util.List;
import java.util.Properties;


@Controller
@Slf4j
public class ServerEmailController {

    @Autowired
    IServerEmailService serverEmailService;

    @RequestMapping(value = "/server/email", method = RequestMethod.POST)
    public ResponseEntity<Void> addProductImage(@RequestBody JSONObject jsonObject){
        String cemailserver = jsonObject.getString("cemailserver");
        String username = jsonObject.getString("username");
        String password = jsonObject.getString("password");
        String type = jsonObject.getString("type");
        List<ServerEmail> serverEmailList= serverEmailService.getAll();
        if(serverEmailList != null && serverEmailList.size() > 0){
            ServerEmail serverEmail = serverEmailList.get(0);
            serverEmail.setCemailserver(cemailserver);
            serverEmail.setUsername(username);
            serverEmail.setPassword(password);
            serverEmail.setType(type);
            Properties properties = new Properties();
            properties.setProperty("mail.host", cemailserver);
            properties.setProperty("mail.transport.protocol", type);
            properties.setProperty("mail.smtp.auth", "true");
            properties.put("mail.smtp.ssl.enable", "true");
            try {
                Session session = Session.getDefaultInstance(properties);
                Transport transport = session.getTransport();
                transport.connect(cemailserver, username, password);
                transport.close();
            }catch (Exception e){
                e.printStackTrace();
                return new ResponseEntity(Response.error("Email server login error"), HttpStatus.SERVICE_UNAVAILABLE);
            }
            if(serverEmailService.update(serverEmail)){
                return new ResponseEntity(Response.success(), HttpStatus.OK);
            }else{
                return new ResponseEntity(Response.error("Insert server email image to db error"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }else{
            return new ResponseEntity(Response.error("Server error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/server/email", method = RequestMethod.GET)
    public ResponseEntity<ServerEmail> getServerEmail() {
        List<ServerEmail> serverEmailList = serverEmailService.getAll();
        if(serverEmailList != null && serverEmailList.size() > 0){
            JSONObject object = new JSONObject();
            object.put("cemailserver", serverEmailList.get(0).getCemailserver());
            object.put("username", serverEmailList.get(0).getUsername());
            object.put("password", serverEmailList.get(0).getPassword());
            object.put("type", serverEmailList.get(0).getType());
            return new ResponseEntity(Response.success(object), HttpStatus.OK);
        }else{
            return new ResponseEntity(Response.error("Server error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
