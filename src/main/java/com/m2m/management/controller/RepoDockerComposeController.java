package com.m2m.management.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.m2m.management.Resource.DeployResource;
import com.m2m.management.Resource.RepoResource;

import java.io.*;

import com.m2m.management.configuration.UploadConfig;
import com.m2m.management.constant.AmazonConstant;
import com.m2m.management.constant.PNLevel;
import com.m2m.management.constant.UserRole;
import com.m2m.management.entity.*;
import com.m2m.management.former.Response;
import com.m2m.management.restful.CloudRepoStatusManager;
import com.m2m.management.service.*;
import com.m2m.management.service.impl.RepoDockerComposeService;
import com.m2m.management.service.impl.RepoDockerService;
import com.m2m.management.service.impl.UserService;
import com.m2m.management.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @date ：Created in 5/13/20 3:56 PM
 * @description：repo docker compose controller
 */

@RestController
@Slf4j
public class RepoDockerComposeController {
    private String pathSeparate = File.separator;
    private long maxFileSize = 1024*1024*1L;

    private String dockerYamlImageTag = "image", getDockerYamlContainerTag = "container_name";

    @Autowired
    private IRepoDockerComposeService repoDockerComposeService;

    @Autowired
    private IRepoDockerService repoDockerService;

    @Autowired
    private IRepoService repoService;

    @Autowired
    private UserService userService;

    @Autowired
    private ICloudRepoStatusService cloudRepoStatusService;

    @Autowired
    private PlatformTransactionManager txManager;

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
    private String repoName ="dockerCompose";
    @Value("${repo.data}")
    private String repoString;
 //download  specified  version app
    @RequestMapping(value = "/repoDockerCompose/downloadApp/{rdid}", method = RequestMethod.GET)
    public void  downloadAppByWeb(@PathVariable("rdid") long rdid, HttpServletResponse response){
        RepoDockerCompose repoDockerCompose = repoDockerComposeService.get(rdid);
        if(repoDockerCompose == null){
            log.error("repoDockerCompose is null");
        }
        Storage storage = repoDockerCompose.getStorage();
//            set blob key in web
        S3Client s3Client = S3Client.getInstance(storage);
        if(!s3Client.isBucketExit()){
            s3Client.createBucket();
        }

        String fileSavePath = repoDockerCompose.getAddress().substring(5);
        try {
            String enfilename = new String(repoDockerCompose.getFilename().getBytes(), "ISO-8859-1");
            response.setHeader("content-type", "application/octet-stream");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename="+enfilename);
            OutputStream os = response.getOutputStream();
            s3Client.downloadStreamFromBucket(fileSavePath, os);
            System.out.println("Download the docker compose file successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/repoDockerCompose", method = RequestMethod.GET)
    public ResponseEntity<List<RepoDockerCompose>> getRepoDockerComposes(
            @RequestParam(name="storageId", required = true) int storageId
    ) {
        Storage storage = storageService.get(storageId);
        List<RepoDockerCompose> repoFiles = repoDockerComposeService.getAll(storage);
        long count  = repoDockerComposeService.count(storage);
        if(repoFiles == null){
            return new ResponseEntity(Response.error("Server error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }else {
            return new ResponseEntity(Response.success(repoFiles, count), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/repoDockerCompose/byPage", method = RequestMethod.GET)
    public ResponseEntity<List<RepoDockerCompose>> getRepoDockerComposes(
            @RequestParam(name="storageId", required = true) int storageId,
            @RequestParam(name="keywords", required = false, defaultValue ="") String keywords,
            @RequestParam(name="currentpage", required = false, defaultValue ="1") int currentpage,
            @RequestParam(name="limit", required = false, defaultValue ="10") int limit) {
        Storage storage = storageService.get(storageId);
        List<RepoDockerCompose> repoFiles = repoDockerComposeService.getAllByPage(keywords, currentpage-1, limit, storage);
        long count  = repoDockerComposeService.count(keywords, storage);
        if(repoFiles == null){
            return new ResponseEntity(Response.error("Server error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }else {
            return new ResponseEntity(Response.success(repoFiles, count), HttpStatus.OK);
        }
    }


    @RequestMapping(value = "/dockerCompose/content", method = RequestMethod.GET)
    public ResponseEntity<List<RepoDockerCompose>> getRepoDockerComposes(
            @RequestParam(name="storageId", required = true) int storageId,
            @RequestParam(name="address", required = true, defaultValue ="") String address) {
        String key = address.substring(5);
        String filename = address.substring(address.lastIndexOf("/")+1);
        Storage storage = storageService.get(storageId);
        S3Client s3Client = S3Client.getInstance(storage);
        if(s3Client.isBucketExit()&& s3Client.isObjectExit(key)) {
            String savePath = UploadConfig.path+"/"+ filename;
            if (s3Client.downloadFileFromBucket(key, savePath)){
                File dockerComposeFile = new File(savePath);
                if(dockerComposeFile.exists()){
                    String lineTxt = "";
                    String AlartTxt="";
                    try {
                        InputStreamReader read = new InputStreamReader(
                                new FileInputStream(dockerComposeFile),"utf-8");
                        BufferedReader bufferedReader = new BufferedReader(read);
                        while((lineTxt = bufferedReader.readLine()) != null){
                            lineTxt+="\r\n";
                            AlartTxt+=lineTxt;
                        }
                        read.close();
                        dockerComposeFile.delete();
                    } catch (IOException e) {
                        e.printStackTrace();
                        return new ResponseEntity(Response.error("Get file content error"), HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                    return new ResponseEntity(Response.success(AlartTxt), HttpStatus.OK);
                }
            }else{
                return new ResponseEntity(Response.error("Get blob file error"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity(Response.error("Blob key not found"), HttpStatus.INTERNAL_SERVER_ERROR);

    }


    @RequestMapping(value = "/repoDockerCompose", method = RequestMethod.POST)
    public ResponseEntity<Void> createRepoFiles(
            @RequestParam("file") MultipartFile file,
            @RequestParam(name="storageId", required = true) int storageId,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "productName") String tag,
            @RequestParam(value = "version") String version,
            @RequestParam(value = "type") String type
    ){
        try{
            Storage storage = storageService.get(storageId);
            S3Client s3Client = S3Client.getInstance(storage);
            String filename = file.getOriginalFilename();
            String sfilename = String.format("%s-%s.yaml",tag, version);
            Boolean isSave = false;
            File tempDir = new File(UploadConfig.path);
            String convPath = UploadConfig.path+pathSeparate+file.getOriginalFilename();

            if(!tempDir.exists()){
                tempDir.mkdirs();
            }
            //insert only one version file
            RepoDockerCompose rdc = repoDockerComposeService.get(tag, version, storage);
            if(rdc != null){
                return new ResponseEntity(Response.error("File can not duplication"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
            File convFile = new File( convPath);
            file.transferTo(convFile);
            String fileSavePath =
                    repoName +
                    pathSeparate +tag +
                    pathSeparate +version+
                    pathSeparate + sfilename;
            String saveAddress = "";
//            set blob key in web
            if(!s3Client.isBucketExit()){
                s3Client.createBucket();
            }

            if(s3Client.uploadFilePut(convFile, fileSavePath)){
                isSave = true;
            }else{
                s3Client.deleteObject(fileSavePath);
            }

            if(isSave){
                DefaultTransactionDefinition def = new DefaultTransactionDefinition();
                def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
                TransactionStatus status = txManager.getTransaction(def);
                try{
                    saveAddress = AmazonConstant.protocol+ fileSavePath;
                    RepoDockerCompose repoDockerCompose = new RepoDockerCompose(tag, description);
                    repoDockerCompose.setDescription(description);
                    repoDockerCompose.setVersion(version);
                    repoDockerCompose.setFilename(filename);
                    repoDockerCompose.setAddress(saveAddress);
                    repoDockerCompose.setType(type);
                    repoDockerCompose.setTag(tag);
                    repoDockerCompose.setTs(new Date().getTime());
                    repoDockerCompose.setStorage(storage);
                    if(repoDockerComposeService.add(repoDockerCompose)){
                        YamlUtils yamlUtils = new YamlUtils();
                        Map<String, String> imageMap =  yamlUtils.parseYamlForDockerDetails(convPath, dockerYamlImageTag);
                        Map<String, String> containerMap = yamlUtils.parseYamlForDockerDetails(convPath, getDockerYamlContainerTag);
                        int i = 0;
                        for (String imageTag : imageMap.keySet()) {
                            ++i;
                            String dockerVersion, dockerImage;
                            if (imageMap.get(imageTag).contains(":")) {
                                dockerImage = imageMap.get(imageTag).substring(0, imageMap.get(imageTag).indexOf(":"));
                                dockerVersion = imageMap.get(imageTag).substring(dockerImage.length() + 1);
                            }else {
                                dockerImage = imageMap.get(imageTag);
                                dockerVersion = "latest";
                            }
                            int j=0;
                            for(String containerTag: containerMap.keySet()){
                                ++j;
                                if(i == j){
                                    String dockerContainer = containerMap.get(containerTag);
                                    RepoDocker repoDocker = new RepoDocker(dockerContainer, dockerImage, dockerVersion);
                                    repoDocker.setRepoDockerCompose(repoDockerCompose);
                                    repoDocker.setTs(new Date().getTime());
                                    boolean res = repoDockerService.add(repoDocker);
                                    if(res == false){
                                        log.error("insert repoDocker error; compose name="+filename);
                                    }
                                }
                            }
                        }

                    }else{
                        convFile.delete();
                        return new ResponseEntity(Response.error("Add dockercompose to db error"), HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                }catch(Exception e){
                    convFile.delete();
                    e.printStackTrace();
                    txManager.rollback(status);
                    return new ResponseEntity(Response.error("Error in Docker Compose file content"), HttpStatus.INTERNAL_SERVER_ERROR);
                }
                if(convFile.exists()){
                    convFile.delete();
                }
                txManager.commit(status);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String[] emails = UserEmailUtil.getUserEmail(tag, type, productItemService, productConcernService, attentionService);
                        if(emails != null && emails.length > 0){
                            if(EmailUtil.batchSendMail(serverEmailService, emails, tag, version, "compose file", storage.getBloburl())){
                                log.info("Send emails success");
                            }else {
                                log.info("Send emails failed");
                            }
                        }
                    }
                }).start();
                RepoDockerCompose dc  = repoDockerComposeService.get(tag, version, storage);
                JSONObject json = new JSONObject();
                json.put("address", saveAddress);
                json.put("id", dc.getRdcid());
                return new ResponseEntity(Response.success(), HttpStatus.OK);
            }else{
                if(convFile.exists()){
                    convFile.delete();
                }
                return new ResponseEntity(Response.error("Upload file to repo error"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }catch(Exception e){
            e.printStackTrace();
            return new ResponseEntity(Response.error("File upload error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/repoDockerCompose/batchDelete", method = RequestMethod.POST)
    public ResponseEntity<String> batchDeleteApp( @RequestBody RepoDockerCompose prepoDockerCompose){
        long[] rdcidArray = prepoDockerCompose.getRdcidArray();
        Boolean isDelete = false;
        for(int i = 0; i< rdcidArray.length; i++){
            RepoDockerCompose repoDockerCompose = repoDockerComposeService.get(rdcidArray[i]);
            if(repoDockerCompose == null)continue;
            Storage storage = repoDockerCompose.getStorage();
            S3Client s3Client = S3Client.getInstance(storage);
            String address = repoDockerCompose.getAddress();
            String fileSavePath = address.substring(5);

            if(s3Client.isBucketExit()&&s3Client.isObjectExit(fileSavePath)){
                s3Client.deleteObject(fileSavePath);
            }
             isDelete = repoDockerComposeService.delete(rdcidArray[i]);
        }
        if(isDelete){
            return new ResponseEntity(Response.success(), HttpStatus.OK);
        }else{
            return new ResponseEntity(Response.error("Server error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/repoDockerCompose/{rdcid}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteRepoFilesById(@PathVariable("rdcid") long rdcid){
        RepoDockerCompose repoDockerCompose = repoDockerComposeService.get(rdcid);
        if(repoDockerCompose == null){
            return new ResponseEntity(Response.error("Docker compose is not found in db"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        Storage storage = repoDockerCompose.getStorage();
        S3Client s3Client = S3Client.getInstance(storage);
        String address = repoDockerCompose.getAddress();
        String fileSavePath = address.substring(5);

        if(s3Client.isBucketExit()&&s3Client.isObjectExit(fileSavePath)){
            s3Client.deleteObject(fileSavePath);
        }
        if(repoDockerComposeService.delete(rdcid)){
            return new ResponseEntity(Response.success(), HttpStatus.OK);
        }else{
            return new ResponseEntity(Response.error("File is not found in db"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
