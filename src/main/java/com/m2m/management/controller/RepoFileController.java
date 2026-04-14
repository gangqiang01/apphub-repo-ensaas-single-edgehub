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
import com.m2m.management.former.Response;
import com.m2m.management.restful.CloudRepoStatusManager;
import com.m2m.management.service.*;
import com.m2m.management.service.IRepoFileService;
import com.m2m.management.service.impl.UserService;
import com.m2m.management.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @date ：Created in 8/1/19 11:50 AM
 * @description：upload file controller
 */

@Slf4j
@RestController
public class RepoFileController {
    private String baseRepoPath = DeployResource.BASEDEPLOYPATH+ RepoResource.TYPE;
    private String pathSeparate = File.separator;

    @Autowired
    private IRepoFileService repoFilesService;

    @Autowired
    private IRepoService repoService;

    @Autowired
    private UserService userService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private ICloudRepoStatusService cloudRepoStatusService;

    @Autowired
    private IAttentionService attentionService;

    @Autowired
    IServerEmailService serverEmailService;

//    @Autowired
//    private JavaMailSender mailSender;

    @Autowired
    IProductItemService productItemService;

    @Autowired
    IProductConcernService productConcernService;

    @Autowired
    IStorageService storageService;

    @Value("${repo.data}")
    private String repoString;

    private String repoName ="file";

    //download  specified  version app
    @RequestMapping(value = "/repofiles/downloadApp/{rfid}", method = RequestMethod.GET)
    public void  downloadAppByWeb(@PathVariable("rfid") long rfid, HttpServletResponse response){
        RepoFile repoFile = repoFilesService.get(rfid);
        if(repoFile == null){
            log.error("repoFile is null");
        }
        Storage storage = repoFile.getStorage();
        String filename = repoFile.getFilename();
//            set blob key in web
        S3Client s3Client = S3Client.getInstance(storage);

        if(!s3Client.isBucketExit()){
            s3Client.createBucket();
        }
        String fileSavePath = repoFile.getAddress().substring(5);
        try {
            String enfilename = new String(filename.getBytes(), "ISO-8859-1");
            response.setHeader("content-type", "application/octet-stream");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename="+enfilename);
            OutputStream os = response.getOutputStream();
            s3Client.downloadStreamFromBucket(fileSavePath, os);

            System.out.println("Download the file successfully!");
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        } 
    }
    @RequestMapping(value = "/repofiles/byPage", method = RequestMethod.GET)
    public ResponseEntity<List<RepoFile>> getRepoFiles(
            @RequestParam(name="storageId", required = true) int storageId,
            @RequestParam(name = "type", required = false, defaultValue = "") String type,
            @RequestParam(name="keywords", required = false, defaultValue ="") String keywords,
            @RequestParam(name="currentpage", required = false, defaultValue ="1") int currentpage,
            @RequestParam(name="limit", required = false, defaultValue ="10") int limit) {
//        log.info("repofile:"+keywords);
        Storage storage = storageService.get(storageId);
        List<RepoFile> repoFiles = repoFilesService.getAllByType(type, keywords, currentpage-1, limit, storage);
        long count  = repoFilesService.count(type, keywords, storage);
        if(repoFiles == null){
            return new ResponseEntity(Response.error("Server error"), HttpStatus.NOT_FOUND);
        }else {
            return new ResponseEntity(Response.success(repoFiles, count), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/repofiles", method = RequestMethod.GET)
    public ResponseEntity<List<RepoFile>> getRepoFiles(
            @RequestParam(name="storageId", required = true) int storageId,
            @RequestParam(name="type", required = true) String type) {
        Storage storage = storageService.get(storageId);
        List<RepoFile> repoFiles = repoFilesService.get(type, storage);
        long count  = repoFiles.size();
        if(repoFiles == null){
            return new ResponseEntity(Response.error("Server error"), HttpStatus.NOT_FOUND);
        }else {
            return new ResponseEntity(Response.success(repoFiles, count), HttpStatus.OK);
        }
    }


    @RequestMapping(value = "/repofiles/{rfid}", method = RequestMethod.GET)
    public ResponseEntity<RepoFile> getRepoFilesById(@PathVariable("rfid") long rfid){
        RepoFile repoFile = repoFilesService.get(rfid);
        if(repoFile == null){
            return new ResponseEntity(Response.error("App is not found in db"), HttpStatus.NOT_FOUND);
        }else{
            return new ResponseEntity(Response.success(repoFile), HttpStatus.OK);
        }
    }

 @RequestMapping(value = "/repofiles/bigFile", method = RequestMethod.POST)
    public ResponseEntity<Void> createRepoFiles(
            @RequestParam("file") MultipartFile file,
            @RequestParam(name="storageId", required = true) int storageId,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "type") String type,
            @RequestParam(value = "format") String format,
            @RequestParam(value = "filename") String filename,
            @RequestParam(value = "md5") String md5,
            @RequestParam(value = "size") Long size,
            @RequestParam(value = "chunks") Integer chunks,
            @RequestParam(value = "chunk") Integer chunk
    ) {
        try {
            Storage storage = storageService.get(storageId);
            Boolean isSave = false;
            RepoFile rFile = repoFilesService.getByFilenameAndType(filename, type, storage);
            if (rFile != null) {
                return new ResponseEntity(Response.error("File can not duplication"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
            String fileSavePath = repoName + pathSeparate + type  + pathSeparate + filename;

            S3Client s3Client = S3Client.getInstance(storage);
            if(!s3Client.isBucketExit()){
                s3Client.createBucket();
            }
            if (UploadUtils.uploadWithBlock(s3Client, file, fileSavePath, chunk, chunks,md5)) {
                if (chunk == chunks - 1) {
                    isSave = s3Client.uploadFileMulPartCommit(fileSavePath, md5);
                    if (isSave) {
                        String downloadAddress = AmazonConstant.protocol + fileSavePath;
                        RepoFile repoFile = new RepoFile(filename, type);
                        repoFile.setDescription(description);
                        repoFile.setFormat(format);
                        repoFile.setAddress(downloadAddress);
                        repoFile.setTs(new Date().getTime());
                        repoFile.setSize(size);
                        repoFile.setStorage(storage);
                        if (repoFilesService.add(repoFile)) {
                            rFile = repoFilesService.getByFilenameAndType(filename, type, storage);
                            JSONObject json = new JSONObject();
                            json.put("address", downloadAddress);
                            json.put("id", rFile.getrfid());
                            json.put("type", type);
                            return new ResponseEntity(Response.success(json), HttpStatus.OK);
                        } else {
                            return new ResponseEntity(Response.error("Add file to db error"), HttpStatus.INTERNAL_SERVER_ERROR);
                        }
                    } else {
                        return new ResponseEntity(Response.error("Upload file to blob error"), HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                }
                return new ResponseEntity(Response.success(), HttpStatus.OK);
            } else {
                return new ResponseEntity(Response.error("Save file by chunk error"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(Response.error("File upload error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/repofiles/batchDelete", method = RequestMethod.POST)
    public ResponseEntity<String> batchDeleteApp( @RequestBody RepoFile prepoFile){
        long[] rfidArray = prepoFile.getRfidArray();
//        log.info(String.valueOf(rfidArray[0]));
        Boolean isDelete = false;
        for(int i = 0; i< rfidArray.length; i++){
            RepoFile repoFile = repoFilesService.get(rfidArray[i]);
            if(repoFile == null)continue;
            Storage storage = repoFile.getStorage();
            String filePath = repoFile.getAddress().substring(5);
//            set blob key in web
            S3Client s3Client = S3Client.getInstance(storage);
            if(s3Client.isBucketExit()&&s3Client.isObjectExit(filePath)){
                s3Client.deleteObject(filePath);
            }
            isDelete = repoFilesService.delete(rfidArray[i]);
        }
        if(isDelete){
            return new ResponseEntity(Response.success(), HttpStatus.OK);
        }else{
            return new ResponseEntity(Response.error("Server error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/repofiles/{rfid}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteRepoFilesById(@PathVariable("rfid") long rfid){
        RepoFile repoFile = repoFilesService.get(rfid);
        if(repoFile == null){
            return new ResponseEntity(Response.error("App is not found in db"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        String filePath = repoFile.getAddress().substring(5);
        Storage storage = repoFile.getStorage();
//            set blob key in web
        S3Client s3Client = S3Client.getInstance(storage);
        if(s3Client.isBucketExit()&&s3Client.isObjectExit(filePath)){
            s3Client.deleteObject(filePath);
        }
        if(repoFilesService.delete(rfid)){
            return new ResponseEntity(Response.success(), HttpStatus.OK);
        }else{
            return new ResponseEntity(Response.error("file is not found in db"), HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }
}
