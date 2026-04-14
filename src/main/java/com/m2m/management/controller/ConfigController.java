package com.m2m.management.controller;


import com.alibaba.fastjson.JSONObject;
import com.amazonaws.util.StringUtils;
import com.auth0.jwt.internal.org.bouncycastle.util.encoders.Base64;
import com.m2m.management.constant.UserRole;
import com.m2m.management.entity.ServerEmail;
import com.m2m.management.entity.Storage;
import com.m2m.management.entity.User;
import com.m2m.management.former.Response;
import com.m2m.management.restful.CloudRepoStatusManager;
import com.m2m.management.restful.SsoService;
import com.m2m.management.service.IServerEmailService;
import com.m2m.management.service.IStorageService;
import com.m2m.management.service.impl.UserService;
import com.m2m.management.truelicense.VerifyLicense;
import com.m2m.management.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.List;
import java.util.Random;


@RestController
@Slf4j
public class ConfigController {

    @Autowired
    private UserService userService;

    @Autowired
    private IStorageService storageService;

    @Autowired
    IServerEmailService serverEmailService;

    //init storage
    private void initServerId(HttpServletRequest req){
        List<User> users = userService.get(UserRole.SYSTEMUSER.ordinal());
        User user = users.get(0);
        if(StringUtils.isNullOrEmpty(user.getServerId())){
            Random random = new Random();
            int serverId = (int) (random.nextDouble() * (99999 - 10000 + 1)) + 10000;
            user.setServerId(String.valueOf(serverId));
            userService.update(user);
        }
    }

    @RequestMapping(value = "/initConfig", method = RequestMethod.GET)
    public ResponseEntity<JSONObject> initConfig( HttpServletRequest req){
        initServerId(req);
        return new ResponseEntity(Response.success(), HttpStatus.OK);
    }

    @RequestMapping(value = "/configMap/customTheme", method = RequestMethod.GET)
    public ResponseEntity<JSONObject> getcustomTheme( HttpServletRequest req){
        List<ServerEmail> serverEmailList = serverEmailService.getAll();
        if(serverEmailList != null && serverEmailList.size() > 0){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("isshow", serverEmailList.get(0).getIsshow());
            jsonObject.put("projectname", serverEmailList.get(0).getProjectname());
            jsonObject.put("leftIcon", serverEmailList.get(0).getLefticon());
            jsonObject.put("rightIcon", serverEmailList.get(0).getRighticon());
            jsonObject.put("loginIcon", serverEmailList.get(0).getLoginicon());
            return new ResponseEntity(Response.success(jsonObject), HttpStatus.OK);
        }else{
            return new ResponseEntity(Response.error("Server error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/configMap", method = RequestMethod.GET)
    public ResponseEntity<JSONObject> getConfig( HttpServletRequest req){
        List<User> users = userService.get(UserRole.SYSTEMUSER.ordinal());
        if(users ==null){
            return new ResponseEntity(Response.error("Admin user is empty"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        User user = users.get(0);
        String cloudUrl = user.getCloudurl();
        String serverId = user.getServerId();
        String cloudServerId = user.getCloudServerId();
        Storage storage = user.getStorage();
        JSONObject jsonObject = new JSONObject();
        if(storage == null){
            jsonObject.put("cloudUrl", cloudUrl);
            jsonObject.put("cloudServerId", cloudServerId);
            jsonObject.put("serverId", serverId);
            return new ResponseEntity(Response.success(jsonObject), HttpStatus.OK);
        }
        String burl = storage.getBloburl();
        String accesskey = storage.getBlobaccesskey();
        String secretkey = storage.getBlobsecretkey();
        String bucket = storage.getBlobbucket();
        String type = storage.getType();
        jsonObject.put("type", type);
        jsonObject.put("endpoint", burl);
        jsonObject.put("bucket",  bucket);
        jsonObject.put("accesskey",accesskey);
        jsonObject.put("secretkey", secretkey);
        jsonObject.put("cloudUrl", cloudUrl);
        jsonObject.put("cloudServerId", cloudServerId);
        jsonObject.put("serverId", serverId);
        return new ResponseEntity(Response.success(jsonObject), HttpStatus.OK);
    }

    @RequestMapping(value = "/configMap/AIFS", method = RequestMethod.GET)
    public ResponseEntity<JSONObject> getStandardConfig( HttpServletRequest req){
        initServerId(req);
        List<User> users = userService.get(UserRole.SYSTEMUSER.ordinal());
        if(users ==null){
            return new ResponseEntity(Response.error("Admin user is empty"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        User user = users.get(0);
        String cloudUrl = user.getCloudurl();
        String serverId = user.getServerId();
        String cloudServerId = user.getCloudServerId();
        Storage storage = user.getStorage();
        JSONObject jsonObject = new JSONObject();
        if(storage == null){
            jsonObject.put("cloudUrl", cloudUrl);
            jsonObject.put("cloudServerId", cloudServerId);
            jsonObject.put("serverId", serverId);
            return new ResponseEntity(Response.success(jsonObject), HttpStatus.OK);
        }
        String burl = storage.getBloburl();
        String accesskey = storage.getBlobaccesskey();
        String secretkey = storage.getBlobsecretkey();
        String bucket = storage.getBlobbucket();
        String type = storage.getType();
        if(type.equals("azure")){
            String[] connectArray = accesskey.split(";");
            for(int i=0; i<connectArray.length; i++){
                String[] connectItem = connectArray[i].split("=", 2);
                if(connectItem[0].equals("AccountKey")){
                    secretkey = connectItem[1];
                }
                if(connectItem[0].equals("AccountName")){
                    accesskey = connectItem[1];
                }
            }
            jsonObject.put("type", type);
            jsonObject.put("bucket", bucket);

        }
        jsonObject.put("type", type);
        jsonObject.put("endpoint", burl);
        jsonObject.put("bucket",  bucket);
        jsonObject.put("accesskey",accesskey);
        jsonObject.put("secretkey", secretkey);
        return new ResponseEntity(Response.success(jsonObject), HttpStatus.OK);
    }

    @RequestMapping(value = "/partnumberLevel", method = RequestMethod.GET)
    public ResponseEntity<Void> partnumberLevel() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("partnumberLevel", VerifyWpLicense.level);
        return new ResponseEntity(Response.success(jsonObject), HttpStatus.OK);
    }

    @RequestMapping(value = "/configMap/time", method = RequestMethod.GET)
    public ResponseEntity<Void> getSystemTime() {
        long time = System.currentTimeMillis();
        return new ResponseEntity(Response.success(String.valueOf(time)), HttpStatus.OK);
    }

    @RequestMapping(value = "/hardwareInfo", method = RequestMethod.GET)
    public ResponseEntity<Void> getHardwareInfo() {
        JSONObject repResult = new JSONObject();
        String info = null;
        String encryInfo = null;
        info = SsoService.getInstanceId();
        if(info == null){
            return new ResponseEntity(Response.error("Get license info fail"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        encryInfo = DESUtil.encrypt(DESUtil.licenseKey, info);


        JSONObject userPermissionJson = new JSONObject();
        userPermissionJson.put("deviceInfo", encryInfo);
        repResult.put("info", userPermissionJson);
        return new ResponseEntity(Response.success(repResult), HttpStatus.OK);
    }

    @RequestMapping(value = "/configMap", method = RequestMethod.POST)
    public ResponseEntity<Void> setConfig(@RequestBody JSONObject json, HttpServletRequest req){
        String cloudUrl = json.getString("cloudurl");
        String serverId = json.getString("serverId");
        if(!StringUtils.isNullOrEmpty(cloudUrl)){
            if(cloudUrl.lastIndexOf("/") == cloudUrl.length()-1){
                cloudUrl = cloudUrl.substring(0, cloudUrl.length()-1);
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("serverId", serverId);
            CloudRepoStatusManager cloudRepoStatusManager = new CloudRepoStatusManager(userService);
            ResponseEntity<String> responseEntity = cloudRepoStatusManager.doPost(cloudUrl, "/configMap/verifyServerId", jsonObject);
            if(responseEntity == null){
                return new ResponseEntity(Response.error("Cloud server error"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
            String cresp = responseEntity.getBody();
            log.info("responce:"+cresp);
            JSONObject cappJson = JSONObject.parseObject(cresp);
            if(!cappJson.getString("status").equals("success")){
                return new ResponseEntity(Response.error("Verify server id error"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        List<User> users = userService.get(UserRole.SYSTEMUSER.ordinal());
        if(users ==null){
            return new ResponseEntity(Response.error("Admin user is empty"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        User user = users.get(0);
        user.setCloudurl(cloudUrl);
        user.setCloudServerId(serverId);
        if(userService.update(user)){
            CloudFileDownload.clearInstance();
            return new ResponseEntity(Response.success(), HttpStatus.OK);
        }else{
            return new ResponseEntity(Response.error("Config error"), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/configMap/setApplicationIcon", method = RequestMethod.POST)
    public ResponseEntity<Void> setApplicationIcon(@RequestParam(value = "leftIcon", required = false) MultipartFile leftIcon,
                                                   @RequestParam(value = "rightIcon", required = false) MultipartFile rightIcon,
                                                   @RequestParam(value = "loginIcon", required = false) MultipartFile loginIcon,
                                                   @RequestParam(value = "isShow") String isShow,
                                                   @RequestParam(value = "projectname") String projectname) throws IOException {
        byte[] leftImage = null;
        byte[] rightImage = null;
        byte[] loginImage = null;
        if(leftIcon != null){
            if(leftIcon.getOriginalFilename().lastIndexOf("png")>-1
                ||leftIcon.getOriginalFilename().lastIndexOf("jpg")>-1
                ||leftIcon.getOriginalFilename().lastIndexOf("jpeg")>-1
                ||leftIcon.getOriginalFilename().lastIndexOf("svg")>-1){
                leftImage = leftIcon.getBytes();
            }else{
                return new ResponseEntity(Response.error("Supported file suffixes are jpg|jpeg|png|svg for image format"),  HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        if(loginIcon != null){
            if(loginIcon.getOriginalFilename().lastIndexOf("png")>-1
                    ||loginIcon.getOriginalFilename().lastIndexOf("jpg")>-1
                    ||loginIcon.getOriginalFilename().lastIndexOf("jpeg")>-1
                    ||loginIcon.getOriginalFilename().lastIndexOf("svg")>-1){
                loginImage = loginIcon.getBytes();
            }else{
                return new ResponseEntity(Response.error("Supported file suffixes are jpg|jpeg|png|svg for image format"),  HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        if(rightIcon != null){
            if(rightIcon.getOriginalFilename().lastIndexOf("png")>-1
                    ||rightIcon.getOriginalFilename().lastIndexOf("jpg")>-1
                    ||rightIcon.getOriginalFilename().lastIndexOf("jpeg")>-1
                    ||rightIcon.getOriginalFilename().lastIndexOf("svg")>-1){
                rightImage = rightIcon.getBytes();
            }else{
                return new ResponseEntity(Response.error("Supported file suffixes are jpg|jpeg|png|svg for image format"),  HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        List<ServerEmail> serverEmailList= serverEmailService.getAll();
        if(serverEmailList != null && serverEmailList.size() > 0){
            ServerEmail serverEmail = serverEmailList.get(0);
            serverEmail.setIsshow(Integer.valueOf(isShow));
            serverEmail.setProjectname(projectname);
            if(leftImage != null){
                serverEmail.setLefticon(leftImage);
            }
            if(rightImage != null){
                serverEmail.setRighticon(rightImage);
            }
            if(loginImage != null){
                serverEmail.setLoginicon(loginImage);
            }
            boolean isupdate = serverEmailService.update(serverEmail);
            if(isupdate){
                return new ResponseEntity(Response.success(), HttpStatus.OK);
            }else{
                return new ResponseEntity(Response.error("Update icons failed"),  HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }else{
            return new ResponseEntity(Response.error("Server error"),  HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/configMap/verifyServerId", method = RequestMethod.POST)
    public ResponseEntity<Void> verifyServerId(@RequestBody JSONObject json){
        String serverId = json.getString("serverId");
        List<User> users = userService.get(UserRole.SYSTEMUSER.ordinal());
        if(users ==null){
            return new ResponseEntity(Response.error("Admin user is empty"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        User user = users.get(0);
        String originServerId = user.getServerId();
        if(serverId.equals(originServerId)){
            return new ResponseEntity(Response.success(), HttpStatus.OK);
        }else{
            return new ResponseEntity(Response.error("Server id error"), HttpStatus.OK);
        }
    }
    @RequestMapping(value = "/verifyauthcode", method = RequestMethod.POST)
    public ResponseEntity<Void> verifyAuthcode() {
        VerifyWpLicense verifyWpLicense = new VerifyWpLicense();
        if (verifyWpLicense.verify()) {
            return new ResponseEntity(Response.success(), HttpStatus.OK);
        } else {
            return new ResponseEntity(Response.error("Verify license failed"), HttpStatus.BAD_REQUEST);
        }
    }
    @RequestMapping(value = "/addLicenseFile", method = RequestMethod.POST)
    public ResponseEntity<Void> addLicenseFile(@RequestParam("file") MultipartFile file){

        InputStream filecontent = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            filecontent = file.getInputStream();
            int read = 0;
            final byte[] bytes = new byte[1024];

            while ((read = filecontent.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            List<ServerEmail> systemConfigs = serverEmailService.getAll();
            String base64 = Base64.toBase64String(out.toByteArray());
            if(systemConfigs != null&& systemConfigs.size() > 0){
                ServerEmail systemConfig = systemConfigs.get(0);
                systemConfig.setLicensefile(base64);
                serverEmailService.update(systemConfig);
            }else{
                ServerEmail systemConfig = new ServerEmail();
                systemConfig.setLicensefile(base64);
                serverEmailService.add(systemConfig);
            }
            boolean ret = verifyLicense();

            if(ret == true) {
                return new ResponseEntity(Response.success(), HttpStatus.OK);
            }else{
                return new ResponseEntity(Response.error("Verify license fail"),  HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (FileNotFoundException fne) {
            fne.printStackTrace();
            return new ResponseEntity(Response.error("Upload file error"),  HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IOException ex){
            ex.printStackTrace();
            return new ResponseEntity(Response.error("Upload file error"),  HttpStatus.INTERNAL_SERVER_ERROR);
        }finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (filecontent != null) {
                    filecontent.close();
                }
            }catch (IOException ex){
                ex.printStackTrace();
            }
        }
    }

    private boolean verifyLicense(){

        boolean ret = VerifyLicense.verifyLicense(serverEmailService);
        return ret;
    }
}
