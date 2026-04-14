package com.m2m.management.controller;

import com.alibaba.druid.util.StringUtils;
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
import com.m2m.management.service.impl.UserService;
import com.m2m.management.utils.*;
import com.m2m.management.former.Response;

import com.m2m.management.utils.HttpConnectUtil;
import com.m2m.management.utils.S3Client;
import com.m2m.management.utils.VerifyWpLicense;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

@RestController
@Slf4j
public class RepoBspsController {
    private String baseRepoPath = DeployResource.BASEDEPLOYPATH + RepoResource.TYPE;
    private String pathSeparate = File.separator;
    private String buildFileName = "build.prop";
    private String androidOsType = "android";

    @Autowired
    private IRepoBspService repoBspsService;

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

//    @Autowired
//    private JavaMailSender mailSender;

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

    @RequestMapping(value = "/repobsps/downloadApp/{rbid}", method = RequestMethod.GET)
    public void downloadBspByWeb(@PathVariable("rbid") long rbid, HttpServletResponse response) {
        RepoBsp repoBsp = repoBspsService.get(rbid);
        if (repoBsp == null) {
            log.error("repoBsp is null");
        }
        Storage storage = repoBsp.getStorage();
        String boardname = repoBsp.getBoardname();
        String versionname = repoBsp.getVersionname();
//            set blob key in web
        S3Client s3Client = S3Client.getInstance(storage);

        if (!s3Client.isBucketExit()) {
            s3Client.createBucket();
        }
        String savePath = "", filename = "";
        String address = repoBsp.getAddress();
        if (repoBsp.getOs().equals("android")) {
            filename = boardname + ".ota.zip";
            savePath = repoBsp.getAddress().substring(AmazonConstant.protocol.length()) + "/" + boardname + "/" + versionname + pathSeparate + filename;
        } else {
            filename = address.substring(address.lastIndexOf(pathSeparate) + 1);
            savePath = address.substring(AmazonConstant.protocol.length());
        }

        try {
            String enfilename = new String(filename.getBytes(), "ISO-8859-1");
            response.setHeader("content-type", "application/octet-stream");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + enfilename);
            OutputStream os = response.getOutputStream();
            s3Client.downloadStreamFromBucket(savePath, os);
            System.out.println("Download the app successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/repobsps/downloadApp/buildprop/{rbid}", method = RequestMethod.GET)
    public void downloadBuildPropByWeb(@PathVariable("rbid") long rbid, HttpServletResponse response) {
        RepoBsp repoBsp = repoBspsService.get(rbid);
        if (repoBsp == null) {
            log.error("repoBsp is null");
        }
        String boardname = repoBsp.getBoardname();
        String versionname = repoBsp.getVersionname();
        Storage storage = repoBsp.getStorage();
//            set blob key in web
        S3Client s3Client = S3Client.getInstance(storage);

        if (!s3Client.isBucketExit()) {
            s3Client.createBucket();
        }
        String boardPath = System.getProperty("java.io.tmpdir") + pathSeparate + boardname;
        File boardFile = new File(boardPath);
        if (!boardFile.exists())
            boardFile.mkdirs();


        String buildpropName = boardname + "_" + buildFileName;
        String buildSaveKey = repoBsp.getAddress().substring(AmazonConstant.protocol.length()) + pathSeparate + boardname + pathSeparate + versionname + pathSeparate + buildFileName;
        try {
            String enfilename = new String(buildpropName.getBytes(), "ISO-8859-1");
            response.setHeader("content-type", "application/octet-stream");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + enfilename);
            OutputStream os = response.getOutputStream();
            s3Client.downloadStreamFromBucket(buildSaveKey, os);
            System.out.println("Download the app successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/repobsps/downloadApp/md5/{rbid}", method = RequestMethod.GET)
    public void downloadBspMd5ByWeb(@PathVariable("rbid") long rbid, HttpServletResponse response) {
        RepoBsp repoBsp = repoBspsService.get(rbid);
        if (repoBsp == null) {
            log.error("repoBsp is null");
        }
        String boardname = repoBsp.getBoardname();
        String versionname = repoBsp.getVersionname();
        Storage storage = repoBsp.getStorage();
//            set blob key in web
        S3Client s3Client = S3Client.getInstance(storage);

        if (!s3Client.isBucketExit()) {
            s3Client.createBucket();
        }
        String md5SavePath = "", filename = "";
        String address = repoBsp.getAddress();
        if (repoBsp.getOs().equals("android")) {
            filename = boardname + ".ota.zip.md5";
            md5SavePath = address.substring(AmazonConstant.protocol.length()) + pathSeparate + boardname + pathSeparate + versionname + pathSeparate + filename;
        } else {

            filename = address.substring(address.lastIndexOf(pathSeparate) + 1) + ".md5";
            md5SavePath = address.substring(AmazonConstant.protocol.length()) + ".md5";
        }

        try {
            String enfilename = new String(filename.getBytes(), "ISO-8859-1");
            response.setHeader("content-type", "application/octet-stream");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + enfilename);
            OutputStream os = response.getOutputStream();
            s3Client.downloadStreamFromBucket(md5SavePath, os);
            System.out.println("Download the app successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/repobsps", method = RequestMethod.GET)
    public ResponseEntity<List<RepoBsp>> getRepoBsps(
            @RequestParam(name = "storageId", required = true) int storageId,
            @RequestParam(name = "os", required = false) String os
    ) {
        Storage storage = storageService.get(storageId);
        List<RepoBsp> repoBsps = new ArrayList<>();
        long count = 0;
        if (StringUtils.isEmpty(os)) {
            repoBsps = repoBspsService.getAll(storage);
            count = repoBspsService.count(storage);
        } else {
            repoBsps = repoBspsService.getByOs(os, storage);
            count = repoBspsService.count(os, storage);

        }
        if (repoBsps == null) {
            return new ResponseEntity(Response.error("Server error"), HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            return new ResponseEntity(Response.success(repoBsps, count), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/repobsps/byOsAndPage", method = RequestMethod.GET)
    public ResponseEntity<List<RepoBsp>> getRepoBspsByOsAndPage(
            @RequestParam(name = "storageId", required = true) int storageId,
            @RequestParam(name = "os") String os,
            @RequestParam(name = "keywords", required = false, defaultValue = "") String keywords,
            @RequestParam(name = "currentpage", required = false, defaultValue = "1") int currentpage,
            @RequestParam(name = "limit", required = false, defaultValue = "10") int limit) {
        Storage storage = storageService.get(storageId);
        List<RepoBsp> repoBsps = repoBspsService.getAllByOsAndPage(os, keywords, currentpage - 1, limit, storage);
        long count = repoBspsService.count(os, keywords, storage);
        if (repoBsps == null) {
            return new ResponseEntity(Response.error("Server error"), HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            return new ResponseEntity(Response.success(repoBsps, count), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/repobsps/{rbid}", method = RequestMethod.GET)
    public ResponseEntity<RepoBsp> getRepoBspsById(@PathVariable("rbid") long rbid) {
        RepoBsp repoBsp = repoBspsService.get(rbid);
        if (repoBsp == null) {
            return new ResponseEntity(Response.error("Repobsp is not found in db"), HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            return new ResponseEntity(Response.success(repoBsp), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/repobsps/{rbid}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteRepoBspsById(@PathVariable("rbid") long rbid) {
        RepoBsp repoBsp = repoBspsService.get(rbid);
        if (repoBsp == null) {
            return new ResponseEntity(Response.error("Bsp is not found in db"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        Storage storage = repoBsp.getStorage();
        S3Client s3Client = S3Client.getInstance(storage);
        if (repoBsp.getOs().equals("android")) {
            String boardname = repoBsp.getBoardname();
            String versionname = repoBsp.getVersionname();
            String updatePkgName = boardname + ".ota.zip";
            String pkgSaveKey = repoBsp.getAddress().substring(AmazonConstant.protocol.length()) + pathSeparate + boardname + pathSeparate + versionname + pathSeparate + updatePkgName;
            String buildSaveKey = repoBsp.getAddress().substring(AmazonConstant.protocol.length()) + pathSeparate + boardname + pathSeparate + versionname + pathSeparate + buildFileName;
            String md5SaveKey = pkgSaveKey + ".md5";

            if (s3Client.isBucketExit() && s3Client.isObjectExit(pkgSaveKey)) {
                if (s3Client.deleteObject(pkgSaveKey)
                        && s3Client.deleteObject(buildSaveKey)
                        && s3Client.deleteObject(md5SaveKey)) {
                    log.info("delete blob bsp success");
                }
            }
        } else {
            String pkgSaveKey = repoBsp.getAddress().substring(AmazonConstant.protocol.length());
            String md5SaveKey = pkgSaveKey + ".md5";
            if (s3Client.isBucketExit() && s3Client.isObjectExit(pkgSaveKey)) {
                if (s3Client.deleteObject(pkgSaveKey)
                        && s3Client.deleteObject(md5SaveKey)) {
                    log.info("delete blob os success");
                }
            }
        }

        //delete db
        if (repoBspsService.delete(rbid)) {
            return new ResponseEntity(Response.success(), HttpStatus.OK);
        } else {
            return new ResponseEntity(Response.error("Repobsp is not found in db"), HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }

    @RequestMapping(value = "/repobsps/batchDelete", method = RequestMethod.POST)
    public ResponseEntity<String> batchDeleteApp(@RequestBody RepoBsp prepoBsp) {
        long[] rbidArray = prepoBsp.getRbidArray();
        Boolean isDelete = false;
        //            set blob key in web
        for (int i = 0; i < rbidArray.length; i++) {
            RepoBsp repoBsp = repoBspsService.get(rbidArray[i]);
            if (repoBsp == null) continue;
            Storage storage = repoBsp.getStorage();
            S3Client s3Client = S3Client.getInstance(storage);
            if (repoBsp.getOs().equals("android")) {
                String boardname = repoBsp.getBoardname();
                String versionname = repoBsp.getVersionname();
                String updatePkgName = boardname + ".ota.zip";
                String pkgSaveKey = repoBsp.getAddress().substring(AmazonConstant.protocol.length()) + pathSeparate + boardname + pathSeparate + versionname + pathSeparate + updatePkgName;
                String buildSaveKey = repoBsp.getAddress().substring(AmazonConstant.protocol.length()) + pathSeparate + boardname + pathSeparate + versionname + pathSeparate + buildFileName;
                String md5SaveKey = pkgSaveKey + ".md5";

                //delete repo bsp and db
                if (s3Client.isBucketExit() && s3Client.isObjectExit(pkgSaveKey)) {
                    s3Client.deleteObject(pkgSaveKey);
                    s3Client.deleteObject(buildSaveKey);
                    s3Client.deleteObject(md5SaveKey);
                }
            } else {
                String pkgSaveKey = repoBsp.getAddress().substring(AmazonConstant.protocol.length());
                String md5SaveKey = pkgSaveKey + ".md5";

                //delete repo bsp and db
                if (s3Client.isBucketExit() && s3Client.isObjectExit(pkgSaveKey)) {
                    s3Client.deleteObject(pkgSaveKey);
                    s3Client.deleteObject(md5SaveKey);
                }
            }

            //delete db
            if (repoBspsService.delete(rbidArray[i])) {
                isDelete = true;
            }

        }
        if (isDelete) {
            return new ResponseEntity(Response.success(), HttpStatus.OK);
        } else {
            return new ResponseEntity(Response.error("Server error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/addBspFile/bigFile", method = RequestMethod.POST)
    public ResponseEntity<Void> createRepoBsps(
            @RequestParam("file") MultipartFile file,
            @RequestParam(name = "storageId", required = true) int storageId,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "filename") String filename,
            @RequestParam(value = "md5") String md5,
            @RequestParam(value = "size") Long size,
            @RequestParam(value = "chunks") Integer chunks,
            @RequestParam(value = "chunk") Integer chunk,
            @RequestParam(value = "boardname") String boardname,
            @RequestParam(value = "version") String version

    ) {
        String repoName = "bsp";
        Storage storage = storageService.get(storageId);
        if (repoBspsService.get(boardname, version, storage, androidOsType) != null
                && repoBspsService.get(boardname, version, storage).size() != 0) {
            return new ResponseEntity(Response.error("Bsp already exists in db"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        try {
            S3Client s3Client = S3Client.getInstance(storage);
            if (!s3Client.isBucketExit()) {
                s3Client.createBucket();
            }

            if (StringUtils.isEmpty(boardname) || StringUtils.isEmpty(version)) {
                return new ResponseEntity(Response.error("Parameter error"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
            String updatePkgName = boardname + ".ota.zip";
            String saveKey = repoName + pathSeparate + boardname + pathSeparate + version + pathSeparate + updatePkgName;
            if (filename.equals(buildFileName))
                saveKey = repoName + pathSeparate + boardname + pathSeparate + version + pathSeparate + buildFileName;
            String tempMd5Path = UploadConfig.path + pathSeparate + updatePkgName + ".md5";
            if (UploadUtils.uploadWithBlock(s3Client, file, saveKey, chunk, chunks, md5)) {
                if (chunk == chunks - 1) {
                    if (filename.equals(buildFileName)) {
                        if (s3Client.uploadFileMulPartCommit(saveKey, md5)) {
                            return new ResponseEntity(Response.success(), HttpStatus.OK);
                        } else {
                            return new ResponseEntity(Response.error("Add bsp to repo error"), HttpStatus.INTERNAL_SERVER_ERROR);
                        }
                    } else {
                        boolean isSave = false;
                        String md5SaveKey = saveKey + ".md5";
                        if (FileUtil.copyFile(md5, tempMd5Path)) {
                            if (!s3Client.uploadFilePut(tempMd5Path, md5SaveKey)) {
                                s3Client.deleteObject(md5SaveKey);
                            }
                        }
                        if (s3Client.uploadFileMulPartCommit(saveKey, md5)) {
                            isSave = true;
                        } else {
                            s3Client.deleteObject(saveKey);
                        }
                        if (isSave) {
                            String downloadAddress = AmazonConstant.protocol + repoName;
                            RepoBsp repoBsp = new RepoBsp(version, boardname);
                            repoBsp.setDescription(description);
                            repoBsp.setAddress(downloadAddress);
                            repoBsp.setSize(size);
                            repoBsp.setOs(androidOsType);
                            repoBsp.setTs(new Date().getTime());
                            repoBsp.setStorage(storage);
                            if (repoBspsService.add(repoBsp)) {
                                List<RepoBsp> rps = repoBspsService.get(boardname, version, storage);
                                JSONObject json = new JSONObject();
                                json.put("address", downloadAddress);
                                json.put("id", rps.get(0).getrbid());
                                return new ResponseEntity(Response.success(), HttpStatus.OK);
                            } else {
                                return new ResponseEntity(Response.error("Add bsp to db error"), HttpStatus.INTERNAL_SERVER_ERROR);
                            }
                        } else {
                            return new ResponseEntity(Response.error("Add bsp to repo error"), HttpStatus.INTERNAL_SERVER_ERROR);
                        }
                    }
                }
                return new ResponseEntity(Response.success(), HttpStatus.OK);
            } else {
                return new ResponseEntity(Response.error("Save file by chunk error"), HttpStatus.INTERNAL_SERVER_ERROR);
            }

        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity(Response.error("IO error"), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(Response.error("File upload error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/addOsFile/bigFile", method = RequestMethod.POST)
    public ResponseEntity<Void> createRepoOs(
            @RequestParam(name = "storageId", required = true) int storageId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "filename") String filename,
            @RequestParam(value = "md5") String md5,
            @RequestParam(value = "size") Long size,
            @RequestParam(value = "os") String os,
            @RequestParam(value = "chunks") Integer chunks,
            @RequestParam(value = "chunk") Integer chunk

    ) {
        String repoName = "os";
        Storage storage = storageService.get(storageId);
        String fileInfo = "", versionname = "";
        if (filename.indexOf("tar.gz") != -1 &&
                filename.substring(filename.lastIndexOf("tar.gz")).equals("tar.gz")) {
            fileInfo = filename.substring(0, filename.lastIndexOf(".tar.gz"));
        } else {
            fileInfo = filename.substring(0, filename.lastIndexOf("."));
        }
        if (fileInfo == null || fileInfo.indexOf("_") == -1) {
            return new ResponseEntity(Response.error("filename is illegal"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        int in = fileInfo.indexOf("_");
        String boardname = fileInfo.substring(0, in);
        if (fileInfo.indexOf("_", in + 1) == -1) {
            versionname = fileInfo.substring(in + 1);
        } else {
            versionname = fileInfo.substring(in + 1, fileInfo.indexOf("_", in + 1));
        }

        if (repoBspsService.get(boardname, versionname, storage, os) != null
                && repoBspsService.get(boardname, versionname, storage, os).size() != 0) {
            return new ResponseEntity(Response.error("Os already exists in db"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        String bspSavePath = repoName + pathSeparate + boardname + pathSeparate + versionname + pathSeparate + filename;

        String bspMd5SavePath = bspSavePath + ".md5";
        try {
            S3Client s3Client = S3Client.getInstance(storage);
            if (!s3Client.isBucketExit()) {
                s3Client.createBucket();
            }

            if (UploadUtils.uploadWithBlock(s3Client, file, bspSavePath, chunk, chunks, md5)) {
                if (chunk == chunks - 1) {
                    Boolean isSave = false;
                    String tempMd5Path = UploadConfig.path + pathSeparate + filename + ".md5";
                    if (FileUtil.copyFile(md5, tempMd5Path)) {
                        if (s3Client.uploadFileMulPartCommit(bspSavePath, md5)
                                && s3Client.uploadFilePut(tempMd5Path, bspMd5SavePath)) {
                            isSave = true;
                        } else {
                            s3Client.deleteObject(bspSavePath);
                            s3Client.deleteObject(bspMd5SavePath);
                        }
                    }
                    new File(tempMd5Path).delete();
                    if (isSave) {
                        String downloadAddress = AmazonConstant.protocol + bspSavePath;
                        RepoBsp repoBsp = new RepoBsp(versionname, boardname);
                        repoBsp.setDescription(description);
                        repoBsp.setAddress(downloadAddress);
                        repoBsp.setSize(size);
                        repoBsp.setOs(os);
                        repoBsp.setTs(new Date().getTime());
                        repoBsp.setStorage(storage);
                        if (repoBspsService.add(repoBsp)) {
                            List<RepoBsp> rps = repoBspsService.get(boardname, versionname, storage, os);
                            JSONObject json = new JSONObject();
                            json.put("address", downloadAddress);
                            json.put("id", rps.get(0).getrbid());
                            return new ResponseEntity(Response.success(), HttpStatus.OK);
                        } else {
                            return new ResponseEntity(Response.error("Add bsp to db error"), HttpStatus.INTERNAL_SERVER_ERROR);
                        }
                    } else {
                        return new ResponseEntity(Response.error("Add bsp to repo error"), HttpStatus.INTERNAL_SERVER_ERROR);
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
}
