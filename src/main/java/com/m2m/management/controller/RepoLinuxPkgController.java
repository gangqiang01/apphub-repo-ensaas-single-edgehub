package com.m2m.management.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.internal.org.apache.commons.codec.digest.DigestUtils;
import com.m2m.management.Resource.DeployResource;
import com.m2m.management.Resource.RepoResource;
import com.m2m.management.configuration.UploadConfig;
import com.m2m.management.constant.AmazonConstant;
import com.m2m.management.constant.PNLevel;
import com.m2m.management.constant.UserRole;
import com.m2m.management.entity.*;

import com.m2m.management.restful.CloudRepoStatusManager;
import com.m2m.management.service.*;
import com.m2m.management.former.Response;

import com.m2m.management.service.impl.UserService;
import com.m2m.management.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Date;

@RestController
@Slf4j
public class RepoLinuxPkgController {
    private String pathSeparate = File.separator;

    @Autowired
    private IRepoLinuxPkgService repoLinuxPkgService;

    @Autowired
    private IRepoService repoService;

    @Autowired
    private ICloudRepoStatusService cloudRepoStatusService;

    @Autowired
    private UserService userService;

    @Autowired
    private IAttentionService attentionService;

    @Autowired
    IServerEmailService serverEmailService;

    @Autowired
    IProductItemService productItemService;

    @Autowired
    IProductConcernService productConcernService;
    @Autowired
    IStorageService storageService;
    private String repoName ="linuxPkg";
    @Value("${repo.data}")
    private String repoString;
//download  specified  version app
    @RequestMapping(value = "/repoLinuxPkg/downloadApp/{rlid}", method = RequestMethod.GET)
    public void  downloadAppByWeb(@PathVariable("rlid") long rlid, HttpServletResponse response){
        RepoLinuxPkg repoLinuxPkg = repoLinuxPkgService.get(rlid);
        if(repoLinuxPkg == null){
            log.error("repoExe is null");
        }
        Storage storage = repoLinuxPkg.getStorage();
//            set blob key in web
        S3Client s3Client = S3Client.getInstance(storage);

        if(!s3Client.isBucketExit()){
            s3Client.createBucket();
        }
        String filename = repoLinuxPkg.getFilename();
        String debSavePath =  repoLinuxPkg.getAddress().substring(5);
        try {
            String enfilename = new String(filename.getBytes(), "ISO-8859-1");
            response.setHeader("content-type", "application/octet-stream");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename="+enfilename);
            OutputStream os = response.getOutputStream();
            s3Client.downloadStreamFromBucket(debSavePath, os);

            System.out.println("Download the linuxpkg successfully!");
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @RequestMapping(value = "/repoLinuxPkg", method = RequestMethod.GET)
    public ResponseEntity<List<RepoLinuxPkg>> getRepoLinuxPkgByType(
            @RequestParam(name="storageId", required = true) int storageId,
            @RequestParam(name = "type", required = false) String type
    ){
        List<RepoLinuxPkg> repoLinuxPkg = new ArrayList<>();
        Storage storage = storageService.get(storageId);
        if(StringUtils.isEmpty("type")){
            repoLinuxPkg = repoLinuxPkgService.getAll(storage);
        }else{
            repoLinuxPkg = repoLinuxPkgService.getAll(type, storage);
        }
        if(repoLinuxPkg == null){
            return new ResponseEntity(Response.error("Server error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }else {
            return new ResponseEntity(Response.success(repoLinuxPkg), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/repoLinuxPkg/byPage", method = RequestMethod.GET)
    public ResponseEntity<List<RepoLinuxPkg>> getRepoLinuxPkgByType(
            @RequestParam(name="storageId", required = true) int storageId,
            @RequestParam(name="keywords", required = false, defaultValue ="") String keywords,
            @RequestParam(name="currentpage", required = false, defaultValue ="1") int currentpage,
            @RequestParam(name="limit", required = false, defaultValue ="10") int limit,
            @RequestParam(name="tenantId", required = true) String tenantId,
            @RequestParam(name = "type", required = true) String type){
        Storage storage = storageService.get(storageId);
        List<RepoLinuxPkg> repoLinuxPkg = repoLinuxPkgService.getAllByPage(keywords, type, currentpage-1, limit, storage, tenantId);
        long  count = repoLinuxPkgService.countByTenantId(keywords, type, storage, tenantId);

        if(repoLinuxPkg == null){
            return new ResponseEntity(Response.error("Server error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }else {
            return new ResponseEntity(Response.success(repoLinuxPkg, count), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/repoLinuxPkg/{rlid}", method = RequestMethod.GET)
    public ResponseEntity<RepoLinuxPkg> getRepoLinuxPkgById(@PathVariable("rlid") long rlid){
        RepoLinuxPkg repoLinuxPkg = repoLinuxPkgService.get(rlid);
        if(repoLinuxPkg == null){
            return new ResponseEntity(Response.error("LinuxPkg is not found in db"), HttpStatus.INTERNAL_SERVER_ERROR);
        }else{
            return new ResponseEntity(Response.success(repoLinuxPkg), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/repoLinuxPkg/batchDelete", method = RequestMethod.POST)
    public ResponseEntity<String> batchDeleteLinuxPkg( @RequestBody RepoLinuxPkg prepoLinuxPkg){
        long[] rlidArray = prepoLinuxPkg.getRlidArray();
        Boolean isDelete = false;
        for(int i = 0; i< rlidArray.length; i++){
            RepoLinuxPkg repoLinuxPkg = repoLinuxPkgService.get(rlidArray[i]);
            if(repoLinuxPkg == null)continue;
            Storage storage = repoLinuxPkg.getStorage();
            String fileSavePath = repoLinuxPkg.getAddress().substring(5);
            S3Client s3Client = S3Client.getInstance(storage);
            if(s3Client.isBucketExit()&&s3Client.isObjectExit(fileSavePath)){
                s3Client.deleteObject(fileSavePath);
            }
            isDelete = repoLinuxPkgService.delete(rlidArray[i]);
        }
        if(isDelete){
            return new ResponseEntity(Response.success(), HttpStatus.OK);
        }else{
            return new ResponseEntity(Response.error("Server error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @RequestMapping(value = "/repoLinuxPkg/tenantId/{tenantId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteRepoExeByTenantId(@PathVariable("tenantId") String tenantId){
        if(repoLinuxPkgService.deleteRepoPkgByOrg(tenantId)){
            List<RepoLinuxPkg> repoPkgs = repoLinuxPkgService.getAllByOrg(tenantId);
            if(repoPkgs != null&& repoPkgs.size() > 0){
                for(RepoLinuxPkg repoPkg: repoPkgs){
                    String fileSavePath =  repoPkg.getAddress().substring(5);
                    Storage storage = repoPkg.getStorage();
                    S3Client s3Client = S3Client.getInstance(storage);
                    if(s3Client.isBucketExit()&&s3Client.isObjectExit(fileSavePath)){
                        s3Client.deleteObject(fileSavePath);
                    }
                }
            }
            return new ResponseEntity(Response.success(), HttpStatus.OK);
        }else{
            return new ResponseEntity(Response.error("LinuxPkg is not found in db"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/repoLinuxPkg/{rlid}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteRepoLinuxPkgById(@PathVariable("rlid") long rlid){
        RepoLinuxPkg repoLinuxPkg = repoLinuxPkgService.get(rlid);
        if(repoLinuxPkg == null){
            return new ResponseEntity(Response.error("LinuxPkg is not found in db"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        String fileSavePath = repoLinuxPkg.getAddress().substring(5);
        Storage storage = repoLinuxPkg.getStorage();
        S3Client s3Client = S3Client.getInstance(storage);
        if(s3Client.isBucketExit()&&s3Client.isObjectExit(fileSavePath)){
            s3Client.deleteObject(fileSavePath);
        }
        if(repoLinuxPkgService.delete(rlid)){
            return new ResponseEntity(Response.success(), HttpStatus.OK);
        }else{
            return new ResponseEntity(Response.error("LinuxPkg is not found in db"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    //deb pkg
    @RequestMapping(value = "/repoLinuxPkg/bigFile", method = RequestMethod.POST)
    public ResponseEntity<Void> createRepoLinuxPkg(
            @RequestParam("file") MultipartFile file,
            @RequestParam(name="storageId", required = true) int storageId,
            @RequestParam(value = "tenantId", required = true) String tenantId,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "filename") String filename,
            @RequestParam(value = "md5") String md5,
            @RequestParam(value = "size") Long size,
            @RequestParam(value = "chunks") Integer chunks,
            @RequestParam(value = "chunk") Integer chunk,
            @RequestParam(value = "type") String type,
            @RequestParam(value = "productName") String productname,
            @RequestParam(value = "version") String version
    ){
        try {
            Storage storage = storageService.get(storageId);
            Boolean isSave = false;
            S3Client s3Client = S3Client.getInstance(storage);
            if(!s3Client.isBucketExit()){
                s3Client.createBucket();
            }
            if(StringUtils.isEmpty(productname)|| StringUtils.isEmpty(version)){
                return new ResponseEntity(Response.error("This filename format error"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
            if (repoLinuxPkgService.getByTenantId(productname, version, type, tenantId, storage) != null) {
                return new ResponseEntity(Response.error("LinuxPkg already exists in db"), HttpStatus.INTERNAL_SERVER_ERROR);
            }

            String debSavePath = repoName+ pathSeparate+ tenantId + pathSeparate + productname.replaceAll(" ", "") + pathSeparate + version + pathSeparate + filename;
            if(UploadUtils.uploadWithBlock(s3Client, file, debSavePath, chunk, chunks,md5)) {
                if (chunk == chunks - 1) {
                    isSave = s3Client.uploadFileMulPartCommit(debSavePath, md5);
                }else{
                    return new ResponseEntity(Response.success(), HttpStatus.OK);
                }
            } else {
                return new ResponseEntity(Response.error("Save package by chunk error"), HttpStatus.INTERNAL_SERVER_ERROR);
            }

            if(isSave){
                String downloadAddress = AmazonConstant.protocol + debSavePath;
                RepoLinuxPkg repoLinuxPkg = new RepoLinuxPkg(productname, version, description);
                repoLinuxPkg.setFilename(filename);
                repoLinuxPkg.setAddress(downloadAddress);
                repoLinuxPkg.setTs(new Date().getTime());
                repoLinuxPkg.setType(type);
                repoLinuxPkg.setSize(size);
                repoLinuxPkg.setOrg(tenantId);
                repoLinuxPkg.setStorage(storage);
                if (repoLinuxPkgService.add(repoLinuxPkg)) {
                    repoLinuxPkg = repoLinuxPkgService.getByTenantId(productname, version, type, tenantId, storage);
                    JSONObject json = new JSONObject();
                    json.put("address", downloadAddress);
                    json.put("id", repoLinuxPkg.getRlid());
                    json.put("type", type);
                    return new ResponseEntity(Response.success(json), HttpStatus.OK);
                } else {
                    return new ResponseEntity(Response.error("Add package to db error"), HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }else{
                return new ResponseEntity(Response.error("Copy package to repo error"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(Response.error("File upload error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
