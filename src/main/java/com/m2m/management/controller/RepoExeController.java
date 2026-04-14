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

import lombok.extern.slf4j.Slf4j;
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

import java.io.*;
import java.util.*;
import java.util.List;

@RestController
@Slf4j
public class RepoExeController {
    private String pathSeparate = File.separator;

    @Autowired
    private IRepoExeService repoExeService;

    @Autowired
    private IRepoService repoService;

    @Autowired
    private HttpServletRequest request;

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

    private String repoName ="exePkg";

    @Value("${repo.data}")
    private String repoString;

//download  specified  version app
    @RequestMapping(value = "/repoexe/downloadApp/{reid}", method = RequestMethod.GET)
    public void  downloadAppByWeb(@PathVariable("reid") long reid, HttpServletResponse response){
        RepoExe repoExe = repoExeService.get(reid);
        if(repoExe == null){
            log.error("repoExe is null");
        }
//            set blob key in web
        Storage storage = repoExe.getStorage();
        S3Client s3Client = S3Client.getInstance(storage);

        if(!s3Client.isBucketExit()){
            s3Client.createBucket();
        }

        String filename = repoExe.getFilename();
        String exeSavePath = repoExe.getAddress().substring(5);

        try {
            String enfilename = new String(filename.getBytes(), "ISO-8859-1");
            response.setHeader("content-type", "application/octet-stream");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename="+enfilename);
            OutputStream os = response.getOutputStream();
            s3Client.downloadStreamFromBucket(exeSavePath, os);
            System.out.println("Download the exe successfully!");
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/repoexe", method = RequestMethod.GET)
    public ResponseEntity<List<RepoExe>> getRepoExe(
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name="storageId", required = true) int storageId
    ) {
        Storage storage = storageService.get(storageId);
        List<RepoExe> repoExe = new ArrayList<>();
        long count = 0;
        if(StringUtils.isEmpty(type)){
            repoExe = repoExeService.getAll(storage);
            count  = repoExeService.count(storage);
        }else{
            repoExe = repoExeService.getByType(type, storage);
            count  = repoExeService.countByType(type, storage);
        }
        if(repoExe == null){
            return new ResponseEntity(Response.error("Server error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }else {
            return new ResponseEntity(Response.success(repoExe, count), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/repoexe/byPage", method = RequestMethod.GET)
    public ResponseEntity<List<RepoExe>> getRepoExe(
            @RequestParam(name="storageId", required = true) int storageId,
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name="keywords", required = false, defaultValue ="") String keywords,
            @RequestParam(name="currentpage", required = false, defaultValue ="1") int currentpage,
            @RequestParam(name="limit", required = false, defaultValue ="10") int limit) {
        Storage storage = storageService.get(storageId);
        List<RepoExe> repoExe = new ArrayList<>();
        long count = 0;
        if(StringUtils.isEmpty(type)){
            repoExe = repoExeService.getAllByPage(keywords, currentpage-1, limit, storage);
            count = repoExeService.count(keywords, storage);
        }else{
            repoExe = repoExeService.getAllByTypeAndPage(type, keywords,currentpage-1, limit, storage);
            count = repoExeService.count(type, keywords, storage);
        }
        if(repoExe == null){
            return new ResponseEntity(Response.error("Server error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }else {
            return new ResponseEntity(Response.success(repoExe, count), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/repoexe/{reid}", method = RequestMethod.GET)
    public ResponseEntity<RepoExe> getRepoExeById(@PathVariable("reid") long reid){
        RepoExe repoExe = repoExeService.get(reid);
        if(repoExe == null){
            return new ResponseEntity(Response.error("Exe is not found in db"), HttpStatus.INTERNAL_SERVER_ERROR);
        }else{
            return new ResponseEntity(Response.success(repoExe), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/repoexe/batchDelete", method = RequestMethod.POST)
    public ResponseEntity<String> batchDeleteExe( @RequestBody RepoExe prepoExe){
        long[] reidArray = prepoExe.getReidArray();
        Boolean isDelete = false;
        for(int i = 0; i< reidArray.length; i++){
            RepoExe repoExe = repoExeService.get(reidArray[i]);
            if(repoExe == null)continue;
            String exeSavePath = repoExe.getAddress().substring(5);
            Storage storage = repoExe.getStorage();
            S3Client s3Client = S3Client.getInstance(storage);
            if(s3Client.isBucketExit()&&s3Client.isObjectExit(exeSavePath)){
                s3Client.deleteObject(exeSavePath);
            }
            isDelete = repoExeService.delete(reidArray[i]);

        }
        if(isDelete){
            return new ResponseEntity(Response.success(), HttpStatus.OK);
        }else{
            return new ResponseEntity(Response.error("Server error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/repoexe/{reid}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteRepoExeById(@PathVariable("reid") long reid){
        RepoExe repoExe = repoExeService.get(reid);
        if(repoExe == null){
            return new ResponseEntity(Response.error("Exe is not found in db"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        String exeSavePath =  repoExe.getAddress().substring(5);
        Storage storage = repoExe.getStorage();
        S3Client s3Client = S3Client.getInstance(storage);
        if(s3Client.isBucketExit()&&s3Client.isObjectExit(exeSavePath)){
            s3Client.deleteObject(exeSavePath);
        }
        if(repoExeService.delete(reid)){
            return new ResponseEntity(Response.success(), HttpStatus.OK);
        }else{
            return new ResponseEntity(Response.error("Exe is not found in db"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/repoexe/bigFile", method = RequestMethod.POST)
    public ResponseEntity<Void> createRepoExe(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value="storageId", required = true) int storageId,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "tool", required = false) String tool,
            @RequestParam(value = "filename") String filename,
            @RequestParam(value = "md5") String md5,
            @RequestParam(value = "size") Long size,
            @RequestParam(value = "chunks") Integer chunks,
            @RequestParam(value = "chunk") Integer chunk,
            @RequestParam(value = "type") String type,
            @RequestParam(value = "productName") String productname,
            @RequestParam(value = "version") String version

    ) {
        try {
            Boolean isSave = false;
            Storage storage = storageService.get(storageId);
            S3Client s3Client = S3Client.getInstance(storage);
            if(!s3Client.isBucketExit()){
                s3Client.createBucket();
            }

            if (repoExeService.get(productname, version, storage) != null) {
                return new ResponseEntity(Response.error("Exe already exists in db"), HttpStatus.INTERNAL_SERVER_ERROR);
            }

            String savePath = repoName + pathSeparate + productname.replaceAll(" ", "") + pathSeparate + version + pathSeparate + filename;
            if (UploadUtils.uploadWithBlock(s3Client, file, savePath, chunk, chunks,md5)) {
                if (chunk == chunks - 1) {
                    isSave = s3Client.uploadFileMulPartCommit(savePath, md5);
                }else{
                    return new ResponseEntity(Response.success(), HttpStatus.OK);
                }
            } else {
                return new ResponseEntity(Response.error("Save zip package by chunk error"), HttpStatus.INTERNAL_SERVER_ERROR);
            }

            if(isSave){
                String downloadAddress = AmazonConstant.protocol + savePath;
                RepoExe repoExe = new RepoExe(filename, productname);
                repoExe.setVersion(version);
                repoExe.setTool(tool);
                repoExe.setDescription(description);
                repoExe.setType(type);
                repoExe.setSize(size);
                repoExe.setAddress(downloadAddress);
                repoExe.setTs(new Date().getTime());
                repoExe.setStorage(storage);
                if (repoExeService.add(repoExe)) {
                    RepoExe repoLinuxPkgs = repoExeService.get(productname, version, storage);
                    JSONObject json = new JSONObject();
                    json.put("address", downloadAddress);
                    json.put("id", repoLinuxPkgs.getreid());
                    json.put("type", type);
                    return new ResponseEntity(Response.success(json), HttpStatus.OK);
                } else {
                    return new ResponseEntity(Response.error("Add package to db error"), HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }else {
                return new ResponseEntity(Response.error("Upload package to repo error"), HttpStatus.INTERNAL_SERVER_ERROR);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(Response.error("File upload error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
