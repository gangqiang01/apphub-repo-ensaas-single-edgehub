package com.m2m.management.controller;


import com.alibaba.fastjson.JSONObject;

import com.auth0.jwt.internal.org.apache.commons.lang3.StringUtils;
import com.m2m.management.Resource.DeployResource;
import com.m2m.management.Resource.RepoResource;
import com.m2m.management.constant.UserRole;
import com.m2m.management.entity.Repo;
import com.m2m.management.entity.RepoType;
import com.m2m.management.entity.User;
import com.m2m.management.restful.SsoService;

import com.m2m.management.service.IRepoService;
import com.m2m.management.service.IRepoTypeService;
import com.m2m.management.service.IStorageService;
import com.m2m.management.service.IUserService;
import com.m2m.management.former.Response;

import com.m2m.management.utils.VerifyWpLicense;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.Date;
import java.util.List;


@RestController
@Slf4j
public class LoginController {

    private String separator = "#";
    private final int failValidate = -1;

    @Autowired
    IUserService userService;

    private String baseRepoPath = DeployResource.BASEDEPLOYPATH+ RepoResource.TYPE;
    private String pathSeparate = File.separator;
    @Autowired
    private IRepoTypeService repoTypeService;

    @Autowired
    private IRepoService repoService;

    @Autowired
    private IStorageService storageService;

    @Value("${repoType.data}")
    private String repoTypeString;

    @Value("${repo.data}")
    private String repoString;


    @Value("${astore.username}")
    private String username;

    @Value("${astore.pwd}")
    private String pwd;


    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<Void> login(@RequestBody User user, HttpServletRequest req){
        int userSsoRole = failValidate;
        JSONObject result = new JSONObject();
        JSONObject auth = new JSONObject();

        String username = user.getName();
        String pwd = user.getPasswd();
        auth.put("username", username);
        auth.put("password", pwd);

        SsoService ssoService = new SsoService();
        try {
            String tokenString = ssoService.getToken(auth);
            JSONObject tokenJSONObject = JSONObject.parseObject(tokenString);
            if (tokenString == null ||tokenJSONObject.containsKey("error")) {
                return new ResponseEntity(Response.error("Sso validate error"), HttpStatus.BAD_REQUEST);
            }

            VerifyWpLicense verifyWpLicense = new VerifyWpLicense();
            verifyWpLicense.verify();
            if(userService.get(username) == null){
                if(ssoService.isEnsaasPlatform()){
                    String accessToken = tokenJSONObject.get("accessToken").toString();
                    try {
                        String clientInfo = ssoService.getClientInfo();
                        if(clientInfo == null || JSONObject.parseObject(clientInfo).containsKey("error")){
                            log.error("clientInfo:"+clientInfo);
                            return new ResponseEntity(Response.error("SSO getClientInfo api error"), HttpStatus.BAD_REQUEST);

                        }
                        JSONObject clientInfoJson = JSONObject.parseObject(clientInfo);

                        String srpId = clientInfoJson.getString("clientId");

                        String subscriptionId = verifyWpLicense.getSubscriptionId();
                        String validateUser = ssoService.validateUser(srpId, accessToken, subscriptionId);

                        if(validateUser == null || JSONObject.parseObject(validateUser).containsKey("error")){
                            log.error("validateUser:"+validateUser);
                            return new ResponseEntity(Response.error("SSO validateUser api error"), HttpStatus.BAD_REQUEST);

                        }

//                        userSsoRole = getValidateUserResult(validateUser);
//                        System.out.println("User role:"+ validateUser);
//
//                        if(userSsoRole == failValidate){
//                            return new ResponseEntity(Response.error("Insufficient user permissions"), HttpStatus.BAD_REQUEST);
//                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        return new ResponseEntity(Response.error("SSO validateUser error"), HttpStatus.BAD_REQUEST);

                    }

//              LOG.info(userBean.getUserAll().toString());
                    int userRole = 2;
                    if(userService.get(UserRole.SYSTEMUSER.ordinal()) == null
                            ||userService.get(UserRole.SYSTEMUSER.ordinal()).size() == 0){
                        userRole = UserRole.SYSTEMUSER.ordinal();
                    }else{
                        userRole = userSsoRole;
                    }
                    User nuser = new User();
                    nuser.setName(username);
                    nuser.setTs(new Date().getTime());
                    nuser.setRole(userRole);
                    if(!userService.add(nuser)){
                        return new ResponseEntity(Response.error("Add user to db error"), HttpStatus.BAD_REQUEST);
                    }
                }else{
                    int userRole;
                    if(userService.get(UserRole.SYSTEMUSER.ordinal()) == null
                            ||userService.get(UserRole.SYSTEMUSER.ordinal()).size() == 0){
                        userRole = UserRole.SYSTEMUSER.ordinal();
                    }else{
                        userRole = UserRole.ADMINISTRATOR.ordinal();
                    }
                    User nuser = new User();
                    nuser.setName(username);
                    nuser.setTs(new Date().getTime());
                    nuser.setRole(userRole);
                    if(!userService.add(nuser)){
                        return new ResponseEntity(Response.error("Add user to db error"), HttpStatus.BAD_REQUEST);
                    }
                }
            }

            HttpSession session = req.getSession();
            session.setAttribute("username", username);
            String[] repoTypeArray = repoTypeString.split(separator);

            //   insert repotype
            for(int i=0; i<repoTypeArray.length; i++){
                try{
                    RepoType repoType = repoTypeService.get(repoTypeArray[i]);
                    if(repoType == null){
                        RepoType srepoType = new RepoType(repoTypeArray[i]);
                        srepoType.setTs(new Date().getTime());
                        repoTypeService.add(srepoType);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            //  insert repo
            String[] repoArray = repoString.split(separator);
            for(int i=0; i<repoArray.length; i++){
                try{
                    Repo repo = repoService.get(repoArray[i]);
                    if(repo == null){
                        User muser = null;
                        String description = String.format("%s %s","store", repoArray[i]);
                        String darkname = org.springframework.util.DigestUtils.md5DigestAsHex(repoArray[i].getBytes());
                        List<User> userList = userService.get(UserRole.SYSTEMUSER.ordinal());

                        if(userList != null){
                            muser = userList.get(0);
                            RepoType repoType = repoTypeService.get(repoTypeArray[i]);
                            Repo srepo = new Repo(repoArray[i], description, muser);
                            srepo.setDarkname(darkname);
                            srepo.setRepoType(repoType);
                            srepo.setTs(new Date().getTime());
                            repoService.add(srepo);
                        }else{
                            log.error("[initBD][insert repo]get user null");
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            result.put("token", tokenJSONObject.getString("accessToken"));
            result.put("refreshToken", tokenJSONObject.getString("refreshToken"));
            result.put("tokenType", tokenJSONObject.getString("tokenType"));
            result.put("expiresTime", tokenJSONObject.getString("expiresIn"));
            return new ResponseEntity(Response.success(result), HttpStatus.OK);
        }catch (HttpClientErrorException e){
            e.printStackTrace();
            return new ResponseEntity(Response.error("Server error"), HttpStatus.BAD_REQUEST);
        }catch(Exception e){
            e.printStackTrace();
            return new ResponseEntity(Response.error("Server error"), HttpStatus.BAD_REQUEST);
        }

    }

    public int getValidateUserResult(String validateUserInfo){
        JSONObject validateUserJson = JSONObject.parseObject(validateUserInfo);
        String clientRole = validateUserJson.getString("clientRole");
        String clusterRole = validateUserJson.getString("clusterRole");
        String namespaceRole = validateUserJson.getString("namespaceRole");
        String subscriptionRole = validateUserJson.getString("subscriptionRole");
        String workspaceRole = validateUserJson.getString("workspaceRole");
        if(!StringUtils.isEmpty(clusterRole)){
            return UserRole.ADMINISTRATOR.ordinal();
        }else if(!StringUtils.isEmpty(workspaceRole)){
            return UserRole.ADMINISTRATOR.ordinal();
        }else if(!StringUtils.isEmpty(namespaceRole)){
            return UserRole.ADMINISTRATOR.ordinal();
        }else if(!StringUtils.isEmpty(subscriptionRole)){
            return UserRole.ADMINISTRATOR.ordinal();
        }else{
            return failValidate;
        }
    }
}
