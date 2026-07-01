package com.m2m.management.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.internal.org.apache.commons.codec.digest.DigestUtils;
import com.m2m.management.configuration.UploadConfig;
import com.m2m.management.constant.AmazonConstant;
import com.m2m.management.constant.PNLevel;
import com.m2m.management.constant.UserRole;
import com.m2m.management.entity.*;

import com.m2m.management.restful.CloudRepoStatusManager;
import com.m2m.management.service.*;
import com.m2m.management.service.impl.UserService;
import com.m2m.management.utils.*;
import com.m2m.management.former.Response;

import com.m2m.management.utils.S3Client;
import com.m2m.management.utils.VerifyWpLicense;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.*;

@RestController
@Slf4j
public class RepoAppsController {
    private String pathSeparate = File.separator;
    private String appType = "app";

    @Autowired
    private IRepoAppService repoAppsService;

    @Autowired
    private IRepoService repoService;

    @Autowired
    private ICloudRepoStatusService cloudRepoStatusService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private UserService userService;

    @Autowired
    private IAttentionService attentionService;

    @Autowired
    IProductItemService productItemService;

    @Autowired
    IProductConcernService productConcernService;

    @Autowired
    IServerEmailService serverEmailService;

    @Autowired
    IStorageService storageService;

    @Value("${repo.data}")
    private String repoString;
    private String repoName ="androidApp";
    //download  specified  version app
    @RequestMapping(value = "/repoapps/downloadApp/{raid}", method = RequestMethod.GET)
    public void  downloadAppByWeb(@PathVariable("raid") long raid, HttpServletResponse response){
        RepoApp repoApp = repoAppsService.get(raid);
        if(repoApp == null){
            log.error("repoAppList is null");
        }
        String filename = repoApp.getFilename();
        Storage storage = repoApp.getStorage();
//            set blob key in web
        S3Client s3Client = S3Client.getInstance(storage);

        if(!s3Client.isBucketExit()){
            s3Client.createBucket();
        }

        String s3Key = repoApp.getAddress().substring(5);
        try {
            String enfilename = new String(filename.getBytes(), "ISO-8859-1");
            response.setHeader("content-type", "application/octet-stream");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename="+enfilename);
            OutputStream os = response.getOutputStream();
            s3Client.downloadStreamFromBucket(s3Key, os);

            System.out.println("Download the app successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @RequestMapping(value = "/repoapps", method = RequestMethod.GET)
    public ResponseEntity<List<RepoApp>> getRepoApps(
            @RequestParam(name="storageId", required = true) int storageId
    ) {
        Storage storage = storageService.get(storageId);
        List<RepoApp> repoApps = repoAppsService.getAll(storage);
        long count  = repoAppsService.count(storage);
        if(repoApps == null){
            return new ResponseEntity(Response.error("Server error"), HttpStatus.NOT_FOUND);
        }else {
            return new ResponseEntity(Response.success(repoApps, count), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/repoapps/byPage", method = RequestMethod.GET)
    public ResponseEntity<List<RepoApp>> getRepoApps(
            @RequestParam(name="storageId", required = true) int storageId,
            @RequestParam(name="tenantId", required = true) String tenantId,
            @RequestParam(name="keywords", required = false, defaultValue ="") String keywords,
            @RequestParam(name="currentpage", required = false, defaultValue ="1") int currentpage,
            @RequestParam(name="limit", required = false, defaultValue ="10") int limit) {
        Storage storage = storageService.get(storageId);
        List<RepoApp> repoApps = repoAppsService.getAllByPage(keywords, currentpage-1, limit, storage, tenantId);
        long count  = -1;
        count = repoAppsService.count(keywords, storage);
        if(repoApps == null){
            return new ResponseEntity(Response.error("Server error"), HttpStatus.NOT_FOUND);
        }else {
            return new ResponseEntity(Response.success(repoApps, count), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/repoapps/{raid}", method = RequestMethod.GET)
    public ResponseEntity<RepoApp> getRepoAppsById(@PathVariable("raid") long raid){
        RepoApp repoApp = repoAppsService.get(raid);
        if(repoApp == null){
            return new ResponseEntity(Response.error("App is not found in db"), HttpStatus.NOT_FOUND);
        }else{
            return new ResponseEntity(Response.success(repoApp), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/repoapps/batchDelete", method = RequestMethod.POST)
    public ResponseEntity<String> batchDeleteApp( @RequestBody RepoApp prepoApp){
        long[] raidArray = prepoApp.getRaidArray();
        Boolean isDelete = false;
        for(int i = 0; i< raidArray.length; i++){
            RepoApp repoApp = repoAppsService.get(raidArray[i]);
            if(repoApp == null)continue;
            Storage storage = repoApp.getStorage();
            S3Client s3Client = S3Client.getInstance(storage);
            String apkSavePath = repoApp.getAddress().substring(5);

            if(s3Client.isBucketExit()&&s3Client.isObjectExit(apkSavePath)){
                s3Client.deleteObject(apkSavePath);
            }
            if(repoAppsService.delete(raidArray[i])){
                isDelete = true;
            }
        }
        if(isDelete){
            return new ResponseEntity(Response.success(), HttpStatus.OK);
        }else{
            return new ResponseEntity(Response.error("Server error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/repoapps/{raid}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteRepoAppsById(@PathVariable("raid") long raid){
        RepoApp repoApp = repoAppsService.get(raid);
        if(repoApp == null){
            return new ResponseEntity(Response.error("App is not found in db"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        Storage storage = repoApp.getStorage();
        S3Client s3Client = S3Client.getInstance(storage);
        String apkSavePath = repoApp.getAddress().substring(5);

        if(s3Client.isBucketExit()&&s3Client.isObjectExit(apkSavePath)){
            s3Client.deleteObject(apkSavePath);
        }
        
        if(repoAppsService.delete(raid)){
            return new ResponseEntity(Response.success(), HttpStatus.OK);
        }else{
            return new ResponseEntity(Response.error("App is not found in db"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @RequestMapping(value = "/repoapps/tenantId/{tenantId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteRepoAppsById(@PathVariable("tenantId") String tenantId){
        if(repoAppsService.deleteRepoAppByOrg(tenantId)){
            List<RepoApp> repoApps = repoAppsService.getAllByOrg(tenantId);
            if(repoApps != null&& repoApps.size() > 0){
                for(RepoApp app: repoApps){
                    Storage storage = app.getStorage();
                    S3Client s3Client = S3Client.getInstance(storage);
                    String apkSavePath = app.getAddress().substring(5);
                    if(s3Client.isBucketExit()&&s3Client.isObjectExit(apkSavePath)){
                        s3Client.deleteObject(apkSavePath);
                    }
                }
            }
            return new ResponseEntity(Response.success(), HttpStatus.OK);
        }else{
            return new ResponseEntity(Response.error("App is not found in db"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/repoapps/bigFile", method = RequestMethod.POST)
    public ResponseEntity<Void> createRepoApps(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "tenantId", required = true) String tenantId,
            @RequestParam(name="storageId", required = true) int storageId,
            @RequestParam(value = "filename") String filename,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "md5") String md5,
            @RequestParam(value = "size") Long size,
            @RequestParam(value = "chunks") Integer chunks,
            @RequestParam(value = "chunk") Integer chunk,
            @RequestParam(value = "pkgname") String pkgname,
            @RequestParam(value = "versionname") String versionname,
            @RequestParam(value = "versioncode") String versioncode
    ) {
        if(StringUtils.isEmpty(pkgname)|| StringUtils.isEmpty(versionname)|| StringUtils.isEmpty(versioncode)){
            return new ResponseEntity(Response.error("Parameter error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        Storage storage = storageService.get(storageId);
        if (repoAppsService.getByTenantId(pkgname, versionname, filename, tenantId, storage) != null
                && repoAppsService.getByTenantId(pkgname, versionname, filename, tenantId, storage).size() != 0) {
            return new ResponseEntity(Response.error("App already exists in db"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        String apkSavePath = "androidApp" + pathSeparate+ tenantId+ pathSeparate + pkgname + pathSeparate + versionname + pathSeparate + filename;
        try {
            S3Client s3Client = S3Client.getInstance(storage);
            if(!s3Client.isBucketExit()){
                s3Client.createBucket();
            }
            File md5File = new File(UploadConfig.path + "/" + md5);
            if(!md5File.exists()){
                md5File.mkdirs();
            }
            if (UploadUtils.uploadWithBlock(filename, md5, size, chunks, chunk, file)
                    &&UploadUtils.uploadWithBlock(s3Client, file, apkSavePath, chunk, chunks,md5)) {
                if (chunk == chunks - 1) {
                    //upload app icon
                    FileUtil.delDir(UploadConfig.path + "/" + md5);
                    Boolean isSave = s3Client.uploadFileMulPartCommit(apkSavePath, md5);
                    if(isSave){
                        String downloadAddress = AmazonConstant.protocol+ apkSavePath;
                        RepoApp repoApp = new RepoApp(filename, pkgname);
                        repoApp.setVersionname(versionname);
                        repoApp.setVersioncode(versioncode);
                        repoApp.setDescription(description);
                        repoApp.setSize(size);
                        repoApp.setAddress(downloadAddress);
                        repoApp.setTs(new Date().getTime());
                        repoApp.setOrg(tenantId);
                        repoApp.setStorage(storage);
                        if(repoAppsService.add(repoApp)){
                            List<RepoApp> rps = repoAppsService.getByTenantId(pkgname, versionname, filename, tenantId, storage);
                            JSONObject json = new JSONObject();
                            json.put("address", downloadAddress);
                            json.put("id", rps.get(0).getraid());
                            return new ResponseEntity(Response.success(), HttpStatus.OK);
                        }else{
                            return new ResponseEntity(Response.error("Add app to db error"), HttpStatus.INTERNAL_SERVER_ERROR);
                        }
                    }else{
                        return new ResponseEntity(Response.error("Upload app to repo error"), HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                }
                return new ResponseEntity(Response.success(), HttpStatus.OK);
            } else {
                return new ResponseEntity(Response.error("Save app by chunk error"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(Response.error("File upload error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
