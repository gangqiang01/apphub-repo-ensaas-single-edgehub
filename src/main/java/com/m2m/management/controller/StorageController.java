package com.m2m.management.controller;

import com.alibaba.fastjson.JSONObject;
import com.amazonaws.util.StringUtils;
import com.m2m.management.constant.AmazonConstant;
import com.m2m.management.constant.UserRole;
import com.m2m.management.entity.*;
import com.m2m.management.former.Response;
import com.m2m.management.repository.IStorageRepository;
import com.m2m.management.service.*;
import com.m2m.management.service.impl.StorageService;
import com.m2m.management.utils.S3Client;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@Slf4j
public class StorageController {

    private String pathSeparate = "/";

    private String buildFileName = "build.prop";

    @Autowired
    IUserService userService;

    @Autowired
    IStorageService storageService;

    @Autowired
    IRepoAppService repoAppService;

    @Autowired
    IRepoBspService repoBspService;

    @Autowired
    IRepoDockerComposeService repoDockerComposeService;

    @Autowired
    IRepoExeService repoExeService;

    @Autowired
    IRepoFileService repoFileService;

    @Autowired
    IRepoLinuxPkgService repoLinuxPkgService;

    @Autowired
    IStorageRepository storageRepository;

    @RequestMapping(value = "/storage/byPage", method = RequestMethod.GET)
    public ResponseEntity<List<Storage>> getStorageByge(
            @RequestParam(name="keywords", required = false, defaultValue ="") String keywords,
            @RequestParam(name="currentpage", required = false, defaultValue ="1") int currentpage,
            @RequestParam(name="limit", required = false, defaultValue ="10") int limit) {

        List<Storage> storages = storageService.getAll(keywords, currentpage-1, limit);
        long count  = storageService.count(keywords);
        if(storages != null){
            return new ResponseEntity(Response.success(storages, count), HttpStatus.OK);
        }else{
            return new ResponseEntity(Response.error("Server error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @RequestMapping(value = "/storage", method = RequestMethod.GET)
    public ResponseEntity<List<Storage>> getAllStorages() {

        List<Storage> storages = storageService.getAll();
        long count  = storageService.count();
        if(storages != null){
            return new ResponseEntity(Response.success(storages, count), HttpStatus.OK);
        }else{
            return new ResponseEntity(Response.error("Server error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @RequestMapping(value = "/storage", method = RequestMethod.POST)
    public ResponseEntity<Void> createStorage(
            @RequestBody JSONObject jsonObject
    ){
        String bloburl = jsonObject.getString("bloburl");
        String bucket = jsonObject.getString("bucket");
        String accesskey = jsonObject.getString("accesskey");
        String secretkey = jsonObject.getString("secretkey");
        String platform = jsonObject.getString("platform");
        String connectionString = jsonObject.getString("connectionString");
        String container = jsonObject.getString("container");

        Storage storage = new Storage();
        if(platform.equals(S3Client.AZURE)){
            if(StringUtils.isNullOrEmpty(container)|| StringUtils.isNullOrEmpty(connectionString)){
                return new ResponseEntity(Response.error("Parameter error"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
            long count = storageService.count(container, connectionString, S3Client.AZURE);
            if(count > 0){
                return new ResponseEntity(Response.error("Storage is already create in list"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
            S3Client.clearInstance();
            S3Client s3Client = S3Client.getInstance(connectionString, container);
            boolean res = s3Client.isBucketExit();
            if(res == false){
                S3Client.clearInstance();
                return new ResponseEntity(Response.error("Storage item error"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
            storage.setBloburl(S3Client.AZURE);
            storage.setBlobbucket(container);
            storage.setBlobaccesskey(connectionString);
            storage.setType(S3Client.AZURE);
            storage.setTs(new Date().getTime());
        }else{
            if(StringUtils.isNullOrEmpty(accesskey)
                    || StringUtils.isNullOrEmpty(bucket)
                    || StringUtils.isNullOrEmpty(secretkey)){
                return new ResponseEntity(Response.error("Parameter error"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
            long count = storageService.count(bloburl, bucket, accesskey, platform);
            if(count > 0){
                return new ResponseEntity(Response.error("Storage is already create in list"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
            try{
                S3Client.clearInstance();
                S3Client s3Client = S3Client.getInstance(accesskey, secretkey, bloburl, bucket);
                s3Client.listBuckets();
            }catch (Exception e){
                S3Client.clearInstance();
                e.printStackTrace();
                return new ResponseEntity(Response.error("Storage item error"), HttpStatus.INTERNAL_SERVER_ERROR);

            }
            storage.setBloburl(bloburl);
            storage.setBlobbucket(bucket);
            storage.setBlobaccesskey(accesskey);
            storage.setBlobsecretkey(secretkey);
            storage.setType(platform);
            storage.setTs(new Date().getTime());
        }
        if(storageService.add(storage)){
            Storage st = storageService.get(bloburl, bucket, accesskey, platform);
            if(platform.equals(S3Client.AZURE)){
                st = storageService.get(container, connectionString, S3Client.AZURE);
            }
            JSONObject json = new JSONObject();
            json.put("id", st.getSid());
            return new ResponseEntity(Response.success(json), HttpStatus.OK);
        }else{
            return new ResponseEntity(Response.error("Server error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/storage/{sid}", method = RequestMethod.PUT)
    public ResponseEntity<String> saveStorage(@PathVariable("sid") long sid,
                                              @RequestBody(required = false) JSONObject body){
        Storage storage = storageRepository.findById(sid).orElse(null);
        if(storage == null){
            return new ResponseEntity(Response.error("The storage is not exist"), HttpStatus.NOT_FOUND);
        }
        // 部分更新:仅覆盖请求体中提供的字段;不传 body 时 hasFieldUpdate=false,不做 DB 写入
        boolean hasFieldUpdate = mergeStorageFields(storage, body);
        if(!hasFieldUpdate){
            return new ResponseEntity(Response.error("No fields to update"), HttpStatus.BAD_REQUEST);
        }
        String type = storage.getType();
        try {
            // 用更新后的凭据校验连通性
            if(S3Client.AZURE.equals(type)){
                S3Client s3Client = S3Client.getInstance(storage.getBlobaccesskey(), storage.getBlobbucket());
                if(!s3Client.isBucketExit()){
                    return new ResponseEntity(Response.error("The storage you chose is not exist"), HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }else{
                try{
                    S3Client s3Client = S3Client.getInstance(storage.getBlobaccesskey(), storage.getBlobsecretkey(), storage.getBloburl(), storage.getBlobbucket());
                    s3Client.listBuckets();
                }catch (Exception e){
                    log.error("validate storage failed, sid={}", sid, e);
                    return new ResponseEntity(Response.error("The storage you chose is not exist"), HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
            // 校验通过后直接持久化字段变更
            if(storageService.update(storage)){
                return new ResponseEntity(Response.success(), HttpStatus.OK);
            }else{
                return new ResponseEntity(Response.error("Update storage in db error"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }finally {
            S3Client.clearInstance();
        }
    }

    // 将 body 中提供的字段合并到 storage;字段名与 createStorage 保持一致,便于前端复用
    // S3:accesskey/secretkey/bucket/bloburl  Azure:connectionString/container  通用:platform
    private boolean mergeStorageFields(Storage storage, JSONObject body){
        if(body == null){
            return false;
        }
        boolean changed = false;
        String accesskey = body.getString("accesskey");
        if(!StringUtils.isNullOrEmpty(accesskey)){
            storage.setBlobaccesskey(accesskey);
            changed = true;
        }
        String secretkey = body.getString("secretkey");
        if(!StringUtils.isNullOrEmpty(secretkey)){
            storage.setBlobsecretkey(secretkey);
            changed = true;
        }
        String bucket = body.getString("bucket");
        if(!StringUtils.isNullOrEmpty(bucket)){
            storage.setBlobbucket(bucket);
            changed = true;
        }
        String bloburl = body.getString("bloburl");
        if(!StringUtils.isNullOrEmpty(bloburl)){
            storage.setBloburl(bloburl);
            changed = true;
        }
        String connectionString = body.getString("connectionString");
        if(!StringUtils.isNullOrEmpty(connectionString)){
            storage.setBlobaccesskey(connectionString);
            changed = true;
        }
        String container = body.getString("container");
        if(!StringUtils.isNullOrEmpty(container)){
            storage.setBlobbucket(container);
            changed = true;
        }
        String platform = body.getString("platform");
        if(!StringUtils.isNullOrEmpty(platform)){
            storage.setType(platform);
            changed = true;
        }
        if(changed){
            storage.setTs(new Date().getTime());
        }
        return changed;
    }

    @RequestMapping(value = "/storage/{sid}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteStorage(@PathVariable("sid") long sid) {
        log.info("Fetching & Deleting Storage with sid " + sid);
        Storage storage = storageService.get(sid);
        String accesskey = storage.getBlobaccesskey();
        String secretkey = storage.getBlobsecretkey();
        String bucketname = storage.getBlobbucket();
        String url = storage.getBloburl();
        String type = storage.getType();
        S3Client s3Client = null;
        if(type.equals(S3Client.AZURE)){
             s3Client = S3Client.getInstance(accesskey, bucketname);
        }else{
             s3Client = S3Client.getInstance(accesskey, secretkey, url, bucketname);
        }
        try {
            List<RepoApp> repoApps = repoAppService.getAll(storage);
            for (int i = 0; i < repoApps.size(); i++) {
                RepoApp repoApp = repoApps.get(i);
                if (repoApp == null) continue;
                String apkSavePath = repoApp.getAddress().substring(5);
                if (s3Client.isBucketExit()&&s3Client.isObjectExit(apkSavePath)) {
                    s3Client.deleteObject(apkSavePath);
                }
            }
            List<RepoBsp> repoBsps = repoBspService.getAll(storage);
            for (int i = 0; i < repoBsps.size(); i++) {
                RepoBsp repoBsp = repoBsps.get(i);
                String osType = repoBsp.getOs();
                if (repoBsp == null) continue;
                if(osType.equals("android")) {
                    String boardname = repoBsp.getBoardname();
                    String versionname = repoBsp.getVersionname();
                    String updatePkgName = boardname + ".ota.zip";
                    String pkgSaveKey = repoBsp.getAddress().substring(5) + pathSeparate + boardname + pathSeparate + versionname + pathSeparate + updatePkgName;
                    String buildSaveKey = repoBsp.getAddress().substring(5) + pathSeparate + boardname + pathSeparate + versionname + pathSeparate + buildFileName;
                    String md5SaveKey = pkgSaveKey + ".md5";
                    if (s3Client.isBucketExit()&&s3Client.isObjectExit(pkgSaveKey)) {
                        s3Client.deleteObject(pkgSaveKey);
                        s3Client.deleteObject(buildSaveKey);
                        s3Client.deleteObject(md5SaveKey);
                    }
                }else{
                    String pkgSaveKey = repoBsp.getAddress().substring(5);
                    String md5SaveKey = pkgSaveKey+".md5";
                    if(s3Client.isBucketExit()&&s3Client.isObjectExit(pkgSaveKey)){
                       s3Client.deleteObject(pkgSaveKey);
                       s3Client.deleteObject(md5SaveKey);
                    }
                }
            }
            List<RepoDockerCompose> repoDockerComposes = repoDockerComposeService.getAll(storage);
            for (int i = 0; i < repoDockerComposes.size(); i++) {
                RepoDockerCompose repoDockerCompose = repoDockerComposes.get(i);
                if (repoDockerCompose == null) continue;
                String address = repoDockerCompose.getAddress();
                String fileSavePath = address.substring(5);
                if (s3Client.isBucketExit()&&s3Client.isObjectExit(fileSavePath)) {
                    s3Client.deleteObject(fileSavePath);
                }
            }
            List<RepoExe> repoExes = repoExeService.getAll(storage);
            for (int i = 0; i < repoExes.size(); i++) {
                RepoExe repoExe = repoExes.get(i);
                if (repoExe == null) continue;
                String exeSavePath = repoExe.getAddress().substring(5);
                if (s3Client.isBucketExit()&&s3Client.isObjectExit(exeSavePath)) {
                    s3Client.deleteObject(exeSavePath);
                }

            }
            List<RepoFile> repoFiles = repoFileService.getAll(storage);
            for (int i = 0; i < repoFiles.size(); i++) {
                RepoFile repoFile = repoFiles.get(i);
                if (repoFile == null) continue;
                String filePath = repoFile.getAddress().substring(5);
                if (s3Client.isBucketExit()&&s3Client.isObjectExit(filePath)) {
                    s3Client.deleteObject(filePath);
                }
            }
            List<RepoLinuxPkg> repoLinuxPkgs = repoLinuxPkgService.getAll(storage);
            for (int i = 0; i < repoLinuxPkgs.size(); i++) {
                RepoLinuxPkg repoLinuxPkg = repoLinuxPkgs.get(i);
                if (repoLinuxPkg == null) continue;
                String fileSavePath = repoLinuxPkg.getAddress().substring(5);
                if (s3Client.isBucketExit()&&s3Client.isObjectExit(fileSavePath)) {
                    s3Client.deleteObject(fileSavePath);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        if(storageService.delete(sid)){
            return new ResponseEntity(Response.success(), HttpStatus.OK);
        }else{
            return new ResponseEntity(Response.error("Server error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
